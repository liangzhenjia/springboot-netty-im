package com.jiajia.webserver.user.service;

import com.jiajia.webserver.user.model.UserInfoEntity;

import java.util.List;
import java.util.Map;


public interface UserInfoService {

    UserInfoEntity queryObject(Long id);

    List<UserInfoEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(UserInfoEntity userInfo);

    int update(UserInfoEntity userInfo);

    int delete(Long id);

    int deleteBatch(Long[] ids);
}
