package com.jiajia.webserver.dwrmanage.controller;

import com.jiajia.Constants;
import com.jiajia.server.connector.ImConnector;
import com.jiajia.server.model.MessageWrapper;
import com.jiajia.server.model.proto.MessageBodyProto;
import com.jiajia.server.model.proto.MessageProto;
import com.jiajia.server.proxy.MessageProxy;
import com.jiajia.webserver.dwrmanage.connector.DwrConnector;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@RemoteProxy(name = "Imwebserver")
public class DwrController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DwrConnector dwrConnectorImpl;

    @Autowired
    private ImConnector connector;

    @Autowired
    private MessageProxy proxy;

    /**
     * 创建连接
     *
     * @param id
     */
    @RemoteMethod
    public void serverconnect() {
        WebContext wct = WebContextFactory.get();
        ScriptSession session = wct.getScriptSession();
        dwrConnectorImpl.connect(session, (String) session.getAttribute(Constants.SessionConfig.SESSION_KEY));
    }

    /**
     * 关闭连接
     *
     * @param id
     */
    @RemoteMethod
    public void closeconnect() {
        WebContext wct = WebContextFactory.get();
        ScriptSession session = wct.getScriptSession();
        dwrConnectorImpl.close(session);
    }

    /**
     * 发送消息
     *
     * @param id
     */
    @RemoteMethod
    public void sendMsg(String receiver, String msg) {
        System.out.println("this is sendMsg method");
        WebContext wct = WebContextFactory.get();
        ScriptSession session = wct.getScriptSession();
        MessageProto.Model.Builder message = MessageProto.Model.newBuilder();
        message.setCmd(Constants.CmdType.MESSAGE);
        message.setReceiver(receiver);
        MessageBodyProto.MessageBody.Builder msgbody = MessageBodyProto.MessageBody.newBuilder();
        msgbody.setContent(msg);
        message.setContent(msgbody.build().toByteString());
        String sessionId = (String) session.getAttribute(Constants.SessionConfig.SESSION_KEY);
        MessageWrapper wrapper = proxy.convertToMessageWrapper(sessionId, message.build());
        connector.pushMessage(wrapper.getSessionId(), wrapper);
    }

    /**
     * 发送群消息
     *
     * @param id
     */
    @RemoteMethod
    public void sendGroupMsg(String groupId, String msg) {
        WebContext wct = WebContextFactory.get();
        ScriptSession session = wct.getScriptSession();
        MessageProto.Model.Builder message = MessageProto.Model.newBuilder();
        message.setCmd(Constants.CmdType.MESSAGE);
        message.setGroupId(groupId);
        MessageBodyProto.MessageBody.Builder msgbody = MessageBodyProto.MessageBody.newBuilder();
        msgbody.setContent(msg);
        message.setContent(msgbody.build().toByteString());
        String sessionId = (String) session.getAttribute(Constants.SessionConfig.SESSION_KEY);
        MessageWrapper wrapper = proxy.convertToMessageWrapper(sessionId, message.build());
        connector.pushGroupMessage(wrapper);
    }

}
