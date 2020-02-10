package com.jiajia.robot.proxy.impl;

import com.jiajia.conf.MLRobotConf;
import com.jiajia.Constants;
import com.jiajia.robot.proxy.RobotProxy;
import com.jiajia.server.model.MessageWrapper;
import com.jiajia.server.model.proto.MessageBodyProto;
import com.jiajia.server.model.proto.MessageProto;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 茉莉
 */
@Service
public class MLRobotProxyImpl implements RobotProxy {
    private final static Logger log = LoggerFactory.getLogger(MLRobotProxyImpl.class);

    @Autowired
    private MLRobotConf conf;

    @Override
    public MessageWrapper botMessageReply(String user, String content) {
        log.info("MLRebot reply user -->" + user + "--mes:" + content);
        String message = "";
        try {
            //只实现了基础的  具体的请自行修改
            Document doc = Jsoup.connect(conf.getApiUrl()).timeout(62000).data("api_key", conf.getKey()).data("api_secret", conf.getSecret()).data("limit", "5").data("question", content).post();
            message = doc.select("body").html();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MessageProto.Model.Builder result = MessageProto.Model.newBuilder();
        result.setCmd(Constants.CmdType.MESSAGE);
        result.setMsgtype(Constants.ProtobufType.REPLY);
        result.setSender(Constants.ImserverConfig.REBOT_SESSIONID);//机器人ID
        result.setReceiver(user);//回复人
        result.setTimeStamp(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        MessageBodyProto.MessageBody.Builder msgbody = MessageBodyProto.MessageBody.newBuilder();
        msgbody.setContent(message);
        result.setContent(msgbody.build().toByteString());
        return new MessageWrapper(MessageWrapper.MessageProtocol.REPLY, Constants.ImserverConfig.REBOT_SESSIONID, user, result.build());
    }


}
