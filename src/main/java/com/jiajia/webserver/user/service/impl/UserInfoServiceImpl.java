package com.jiajia.webserver.user.service.impl;

import com.jiajia.webserver.user.dao.UserInfoDao;
import com.jiajia.webserver.user.model.UserInfoEntity;
import com.jiajia.webserver.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("userInfoServiceImpl")
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;

    @Override
    public UserInfoEntity queryObject(Long id) {
        return userInfoDao.queryObject(id);
    }

    @Override
    public List<UserInfoEntity> queryList(Map<String, Object> map) {
        return userInfoDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return userInfoDao.queryTotal(map);
    }

    @Override
    public void save(UserInfoEntity userInfo) {
        userInfoDao.save(userInfo);
    }

    @Override
    public int update(UserInfoEntity userInfo) {
        return userInfoDao.update(userInfo);
    }

    @Override
    public int delete(Long id) {
        return userInfoDao.delete(id);
    }

    @Override
    public int deleteBatch(Long[] ids) {
        return userInfoDao.deleteBatch(ids);
    }

}
