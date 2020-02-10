package com.jiajia.server;

import com.jiajia.Constants;
import com.jiajia.server.connector.ImConnector;
import com.jiajia.server.model.MessageWrapper;
import com.jiajia.server.model.proto.MessageProto;
import com.jiajia.server.proxy.MessageProxy;
import com.jiajia.util.ImUtils;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 标有@Sharable的Handler，代表了他是一个可以被分享的handler，这就是说服务器注册了这个handler后，可以分享给多个客户端使用，
 * 如果没有使用该注解，则每次客户端请求时，都必须重新创建一个handler。
 */
@Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<MessageProto.Model> {

    private final static Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);

    private ImConnector connector;

    private MessageProxy proxy;

    public WebSocketServerHandler(MessageProxy proxy, ImConnector connector) {
        this.connector = connector;
        this.proxy = proxy;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o) throws Exception {
        String sessionId = ctx.channel().attr(Constants.SessionConfig.SERVER_SESSION_ID).get();
        //发送心跳包
        if (o instanceof IdleStateEvent && ((IdleStateEvent) o).state().equals(IdleState.WRITER_IDLE)) {
            if (StringUtils.isNotEmpty(sessionId)) {
                MessageProto.Model.Builder builder = MessageProto.Model.newBuilder();
                builder.setCmd(Constants.CmdType.HEARTBEAT);
                builder.setMsgtype(Constants.ProtobufType.SEND);
                ctx.channel().writeAndFlush(builder);
            }
            log.debug(IdleState.WRITER_IDLE + "... from " + sessionId + "-->" + ctx.channel().remoteAddress() + " nid:" + ctx.channel().id().asShortText());
        }

        //如果心跳请求发出70秒内没收到响应，则关闭连接
        if (o instanceof IdleStateEvent && ((IdleStateEvent) o).state().equals(IdleState.READER_IDLE)) {
            log.debug(IdleState.READER_IDLE + "... from " + sessionId + " nid:" + ctx.channel().id().asShortText());
            Long lastTime = (Long) ctx.channel().attr(Constants.SessionConfig.SERVER_SESSION_HEARBEAT).get();
            if (lastTime == null || ((System.currentTimeMillis() - lastTime) / 1000 >= Constants.ImserverConfig.PING_TIME_OUT)) {
                connector.close(ctx);
            }
            //ctx.channel().attr(Constants.SessionConfig.SERVER_SESSION_HEARBEAT).set(null);
        }
    }

    /**
     * 每当从服务端读到客户端写入信息时,将信息转发给其他客户端的Channel.
     *
     * @param ctx
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Model message) throws Exception {
        try {
            System.out.println(new Date() + ": 服务端读到数据 -> " + message.getContent().toStringUtf8());
            String sessionId = connector.getChannelSessionId(ctx);
            // inbound
            if (message.getMsgtype() == Constants.ProtobufType.SEND) {
                ctx.channel().attr(Constants.SessionConfig.SERVER_SESSION_HEARBEAT).set(System.currentTimeMillis());
                MessageWrapper wrapper = proxy.convertToMessageWrapper(sessionId, message);
                // 发送消息
                if (wrapper != null)
                    this.receiveMessages(ctx, wrapper);
            }
            // outbound
            if (message.getMsgtype() == Constants.ProtobufType.REPLY) {
                MessageWrapper wrapper = proxy.convertToMessageWrapper(sessionId, message);
                // 发送消息
                if (wrapper != null)
                    this.receiveMessages(ctx, wrapper);
            }
        } catch (Exception e) {
            log.error("ImWebSocketServerHandler channerRead error.", e);
            throw e;
        }

    }


    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("ImWebSocketServerHandler  join from " + ImUtils.getRemoteAddress(ctx) + " nid:" + ctx.channel().id().asShortText());
    }

    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("ImWebSocketServerHandler Disconnected from {" + ctx.channel().remoteAddress() + "--->" + ctx.channel().localAddress() + "}");
    }

    /**
     * 服务端监听到客户端活动
     *
     * @param ctx
     * @throws Exception
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug("ImWebSocketServerHandler channelActive from (" + ImUtils.getRemoteAddress(ctx) + ")");
    }

    /**
     * 服务端监听到客户端不活动
     *
     * @param ctx
     * @throws Exception
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.debug("ImWebSocketServerHandler channelInactive from (" + ImUtils.getRemoteAddress(ctx) + ")");
        String sessionId = connector.getChannelSessionId(ctx);
        receiveMessages(ctx, new MessageWrapper(MessageWrapper.MessageProtocol.CLOSE, sessionId, null, null));
    }

    /**
     * 当服务端的IO 抛出异常时被调用
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("ImWebSocketServerHandler (" + ImUtils.getRemoteAddress(ctx) + ") -> Unexpected exception from downstream." + cause);
    }


    /**
     * to send message
     *
     * @param handler
     * @param wrapper
     */
    private void receiveMessages(ChannelHandlerContext handler, MessageWrapper wrapper) {
        //设置消息来源为Websocket
        wrapper.setSource(Constants.ImserverConfig.WEBSOCKET);
        if (wrapper.isConnect()) {
            connector.connect(handler, wrapper);
        } else if (wrapper.isClose()) {
            connector.close(handler, wrapper);
        } else if (wrapper.isHeartbeat()) {
            connector.heartbeatToClient(handler, wrapper);
        } else if (wrapper.isGroup()) {
            connector.pushGroupMessage(wrapper);
        } else if (wrapper.isSend()) {
            connector.pushMessage(wrapper);
        } else if (wrapper.isReply()) {
            connector.pushMessage(wrapper.getSessionId(), wrapper);
        }
    }
}
