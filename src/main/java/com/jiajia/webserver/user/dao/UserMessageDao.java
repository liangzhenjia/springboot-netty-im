package com.jiajia.webserver.user.dao;

import com.jiajia.webserver.base.dao.BaseDao;
import com.jiajia.webserver.user.model.UserMessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMessageDao extends BaseDao<UserMessageEntity> {
    /**
     * 获取历史记录
     *
     * @param map
     * @return
     */
    List<UserMessageEntity> getHistoryMessageList(Map<String, Object> map);

    /**
     * 获取历史记录总条数
     *
     * @param map
     * @return
     */
    int getHistoryMessageCount(Map<String, Object> map);

    /**
     * 获取离线消息
     *
     * @param map
     * @return
     */
    List<UserMessageEntity> getOfflineMessageList(Map<String, Object> map);

    /**
     * 修改消息为已读状态
     *
     * @param map
     * @return
     */
    int updatemsgstate(Map<String, Object> map);

}
