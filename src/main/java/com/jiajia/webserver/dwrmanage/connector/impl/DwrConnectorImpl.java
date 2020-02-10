package com.jiajia.webserver.dwrmanage.connector.impl;

import com.jiajia.Constants;
import com.jiajia.server.model.MessageWrapper;
import com.jiajia.server.model.Session;
import com.jiajia.server.session.SessionManager;
import com.jiajia.webserver.dwrmanage.connector.DwrConnector;
import org.directwebremoting.ScriptSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DwrConnectorImpl implements DwrConnector {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void close(ScriptSession scriptSession) {
        String sessionId = (String) scriptSession.getAttribute(Constants.SessionConfig.SESSION_KEY);
        try {
            String nid = scriptSession.getId();
            Session session = sessionManager.getSession(sessionId);
            if (session != null) {
                sessionManager.removeSession(sessionId, nid);

                log.info("dwrconnector close sessionId -> " + sessionId + " success ");
            }
        } catch (Exception e) {
            log.error("dwrconnector close sessionId -->" + sessionId + "  Exception.", e);
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public void connect(ScriptSession scriptSession, String sessionId) {
        try {
            log.info("dwrconnector connect sessionId -> " + sessionId);
            sessionManager.createSession(scriptSession, sessionId);
        } catch (Exception e) {
            log.error("dwrconnector connect  Exception.", e);
        }
    }

    @Override
    public void pushMessage(String sessionId, MessageWrapper wrapper) throws RuntimeException {
        Session session = sessionManager.getSession(sessionId);
        session.write(wrapper.getBody());
        //dwrScriptSessionManagerImpl.getScriptSessionsByHttpSessionId(scriptSession.getHttpSessionId());
        //DwrUtil.sendMessageAuto((String)request.getParameter("my"), "sssssss中文");
    }

}
