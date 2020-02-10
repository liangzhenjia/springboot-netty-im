package com.jiajia.server.session;

import com.jiajia.server.model.MessageWrapper;
import com.jiajia.server.model.Session;
import io.netty.channel.ChannelHandlerContext;
import org.directwebremoting.ScriptSession;

import java.util.Set;

public interface SessionManager {

    /**
     * 添加指定session
     *
     * @param session
     */
    void addSession(Session session);

    void updateSession(Session session);

    /**
     * 删除指定session
     *
     * @param sessionId
     */
     void removeSession(String sessionId);

    /**
     * 删除指定session
     * 
     *
     * @param sessionId
     * @param nid  is socketid 
     */
     void removeSession(String sessionId, String nid);

    /**
     * 根据指定sessionId获取session
     *
     * @param sessionId
     * @return
     */
    Session getSession(String sessionId);

    /**
     * 获取所有的session
     *
     * @return
     */
    Session[] getSessions();

    /**
     * 获取所有的session的id集合
     *
     * @return
     */
    Set<String> getSessionKeys();

    /**
     * 获取所有的session数目
     *
     * @return
     */
    int getSessionCount();
 
    Session createSession(MessageWrapper wrapper, ChannelHandlerContext ctx);
    
    Session createSession(ScriptSession scriptSession, String sessionid);
    
    boolean exist(String sessionId) ;
    

   
}
