package com.jiajia.webserver.user.dao;

import com.jiajia.webserver.base.dao.BaseDao;
import com.jiajia.webserver.user.model.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoDao extends BaseDao<UserInfoEntity> {

}
