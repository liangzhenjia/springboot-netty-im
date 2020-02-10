package com.jiajia.webserver.dwrmanage.connector;

import com.jiajia.server.model.MessageWrapper;
import org.directwebremoting.ScriptSession;

public interface DwrConnector {

    void close(ScriptSession scriptSession);

    void connect(ScriptSession scriptSession, String sessionId);

    /**
     * 发送消息
     * @param sessionId  发送人
     * @param wrapper   发送内容
     * @throws RuntimeException
     */
    void pushMessage(String sessionId, MessageWrapper wrapper) throws RuntimeException;

}
