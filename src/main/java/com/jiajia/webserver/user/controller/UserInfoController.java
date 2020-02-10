package com.jiajia.webserver.user.controller;

import com.jiajia.Constants;
import com.jiajia.webserver.base.controller.BaseController;
import com.jiajia.webserver.user.model.UserInfoEntity;
import com.jiajia.webserver.user.service.UserInfoService;
import com.jiajia.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户信息表
 */
@Controller
@RequestMapping("userinfo")
public class UserInfoController extends BaseController {

    @Autowired
    private UserInfoService userInfoServiceImpl;

    /**
     * 页面
     */
    @RequestMapping("/page")
    public String page(@RequestParam Map<String, Object> params) {
        return "userinfo";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/list",  method = RequestMethod.POST)
    @ResponseBody
    public Object list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<UserInfoEntity> userInfoList = userInfoServiceImpl.queryList(query);
        int total = userInfoServiceImpl.queryTotal(query);
        return putMsgToJsonString(Constants.WebSite.SUCCESS, "", total, userInfoList);
    }


    /**
     * 信息
     */
    @RequestMapping(value = "/info/{id}",  method = RequestMethod.POST)
    @ResponseBody
    public Object info(@PathVariable("id") Long id) {
        UserInfoEntity userInfo = userInfoServiceImpl.queryObject(id);
        return putMsgToJsonString(Constants.WebSite.SUCCESS, "", 0, userInfo);
    }

    /**
     * 保存
     */
    @RequestMapping(value = "/save",  method = RequestMethod.POST)
    @ResponseBody
    public Object save(@ModelAttribute UserInfoEntity userInfo) {
        userInfoServiceImpl.save(userInfo);
        return putMsgToJsonString(Constants.WebSite.SUCCESS, "", 0, userInfo);
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update",  method = RequestMethod.POST)
    @ResponseBody
    public Object update(@ModelAttribute UserInfoEntity userInfo) {
        int result = userInfoServiceImpl.update(userInfo);
        return putMsgToJsonString(result, "", 0, "");
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete",  method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@RequestParam Long[] ids) {
        int result = userInfoServiceImpl.deleteBatch(ids);
        return putMsgToJsonString(result, "", 0, "");
    }

}
