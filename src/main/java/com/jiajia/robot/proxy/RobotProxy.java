package com.jiajia.robot.proxy;

import com.jiajia.server.model.MessageWrapper;

public interface RobotProxy {
    /**
     * 机器人回复
     *
     * @param user    用于区分回复谁，机器人接口短暂记忆
     * @param content
     * @return
     */
    MessageWrapper botMessageReply(String user, String content);
}
