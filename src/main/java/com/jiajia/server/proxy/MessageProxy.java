package com.jiajia.server.proxy;

import com.jiajia.server.model.MessageWrapper;
import com.jiajia.server.model.proto.MessageProto;

public interface MessageProxy {

    MessageWrapper  convertToMessageWrapper(String sessionId, MessageProto.Model message);
    /**
     * 保存在线消息
     * @param message
     */
    void  saveOnlineMessageToDB(MessageWrapper message);
    /**
     * 保存离线消息
     * @param message
     */
    void  saveOfflineMessageToDB(MessageWrapper message);
    /**
     * 获取上线状态消息
     * @param sessionId
     * @return
     */
    MessageProto.Model  getOnLineStateMsg(String sessionId);
    /**
     * 重连状态消息
     * @param sessionId
     * @return
     */
    MessageWrapper  getReConnectionStateMsg(String sessionId);
    
    /**
     * 获取下线状态消息
     * @param sessionId
     * @return
     */
    MessageProto.Model  getOffLineStateMsg(String sessionId);
}

