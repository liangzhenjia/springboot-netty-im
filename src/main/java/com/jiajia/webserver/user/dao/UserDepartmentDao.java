package com.jiajia.webserver.user.dao;

import com.jiajia.webserver.base.dao.BaseDao;
import com.jiajia.webserver.user.model.ImFriendUserData;
import com.jiajia.webserver.user.model.UserDepartmentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDepartmentDao extends BaseDao<UserDepartmentEntity> {
    List<ImFriendUserData> queryGroupAndUser();
}
