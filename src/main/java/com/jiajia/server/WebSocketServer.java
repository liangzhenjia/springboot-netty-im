package com.jiajia.server;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.jiajia.Constants;
import com.jiajia.server.connector.impl.ImConnectorImpl;
import com.jiajia.server.model.proto.MessageProto;
import com.jiajia.server.proxy.MessageProxy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

@Component
public class WebSocketServer implements ApplicationRunner {

    private final static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private ProtobufDecoder decoder = new ProtobufDecoder(MessageProto.Model.getDefaultInstance());
    @Autowired
    private MessageProxy proxy;
    @Autowired
    private ImConnectorImpl connector;
    @Value("${websocket.port}")
    private int port;
    //主线程,负责创建新连接
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    //工作线程,主要用于读取数据以及业务逻辑处理
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    /**
     * 要启动一个Netty服务端，必须要指定三类属性，分别是线程模型、IO模型、连接读写处理逻辑
     *
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("start jiajiaim websocketserver ...");

        // Server 服务启动,这个类将引导我们进行服务端的启动工作
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 线程模型, 给引导类配置两大线程组
        bootstrap.group(bossGroup, workerGroup);
        // 指定IO模型,这里也有其他的 选择，如果你想指定 IO 模型为 BIO，那么这里配置上 OioServerSocketChannel.class 类型即可
        bootstrap.channel(NioServerSocketChannel.class);
        // 连接读写处理逻辑, 这里指定
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            public void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                // HTTP请求的解码和编码
                pipeline.addLast(new HttpServerCodec());
                // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
                // 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
                pipeline.addLast(new HttpObjectAggregator(Constants.ImserverConfig.MAX_AGGREGATED_CONTENT_LENGTH));
                // 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
                pipeline.addLast(new ChunkedWriteHandler());
                // WebSocket数据压缩
                pipeline.addLast(new WebSocketServerCompressionHandler());
                // 协议包长度限制
                pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, Constants.ImserverConfig.MAX_FRAME_LENGTH));
                // 协议包解码
                pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                        ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                        objs.add(buf);
                        buf.retain();
                    }
                });
                // 协议包编码
                pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                        ByteBuf result = null;
                        if (msg instanceof MessageLite) {
                            result = wrappedBuffer(((MessageLite) msg).toByteArray());
                        }
                        if (msg instanceof MessageLite.Builder) {
                            result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                        }
                        // 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的
                        WebSocketFrame frame = new BinaryWebSocketFrame(result);
                        out.add(frame);
                    }
                });
                // 协议包解码时指定Protobuf字节数实例化为CommonProtocol类型
                pipeline.addLast(decoder);
                pipeline.addLast(new IdleStateHandler(Constants.ImserverConfig.READ_IDLE_TIME, Constants.ImserverConfig.WRITE_IDLE_TIME, 0));
                // 业务处理器
                pipeline.addLast(new WebSocketServerHandler(proxy, connector));

            }
        });

        // 表示是否开启Nagle算法，true表示关闭，false表示开启，通俗地说，如果要求高实时性，
        // 有数据发送时就马上发送，就关闭，如果需要减少发送次数减少网络交互，就开启。
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        // 绑定接口，同步等待成功
        log.info("start jiajiaim websocketserver at port[" + port + "].");
        ChannelFuture future = bootstrap.bind(port).sync();
        channel = future.channel();
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("websocketserver have success bind to " + port);
                } else {
                    log.error("websocketserver fail bind to " + port);
                }
            }
        });
        // future.channel().closeFuture().syncUninterruptibly();
    }

    /**
     * 被@PreDestroy修饰的方法会在服务器卸载Servlet的时候运行，并且只会被服务器调用一次
     */
    @PreDestroy
    public void destroy() {
        log.info("destroy jiajiaim websocketserver ...");
        // 释放线程池资源
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("destroy jiajiaim webscoketserver complate.");
    }


}
