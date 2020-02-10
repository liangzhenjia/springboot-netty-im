package com.jiajia.webserver.user.model;

import com.jiajia.webserver.base.model.BaseModel;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;


public class UserAccountEntity extends BaseModel {
    private static final long serialVersionUID = 1L;

    //帐号
    private String account;
    //密码
    private String password;
    //禁用状态（0 启用  1 禁用）
    private Integer disablestate;
    //是否删除（0未删除1已删除）
    private Integer isdel;

    private UserInfoEntity userInfo;//用户基本信息

    /**
     * 设置：帐号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取：帐号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置：密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取：密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置：禁用状态（0 启用  1 禁用）
     */
    public void setDisablestate(Integer disablestate) {
        this.disablestate = disablestate;
    }

    /**
     * 获取：禁用状态（0 启用  1 禁用）
     */
    public Integer getDisablestate() {
        return disablestate;
    }

    /**
     * 设置：是否删除（0未删除1已删除）
     */
    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }

    /**
     * 获取：是否删除（0未删除1已删除）
     */
    public Integer getIsdel() {
        return isdel;
    }


    public UserInfoEntity getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoEntity userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
