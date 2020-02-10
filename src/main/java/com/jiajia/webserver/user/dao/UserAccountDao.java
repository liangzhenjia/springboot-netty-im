package com.jiajia.webserver.user.dao;

import com.jiajia.webserver.base.dao.BaseDao;
import com.jiajia.webserver.user.model.UserAccountEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserAccountDao extends BaseDao<UserAccountEntity> {
    UserAccountEntity queryObjectByAccount(Map<String, Object> map);
}
