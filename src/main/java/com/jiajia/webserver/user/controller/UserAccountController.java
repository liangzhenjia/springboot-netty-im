package com.jiajia.webserver.user.controller;

import com.jiajia.Constants;
import com.jiajia.util.Query;
import com.jiajia.webserver.base.controller.BaseController;
import com.jiajia.webserver.user.model.UserAccountEntity;
import com.jiajia.webserver.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户帐号
 */
@Controller
@RequestMapping("useraccount")
public class UserAccountController extends BaseController {
    @Autowired
    private UserAccountService userAccountServiceImpl;

    /**
     * 页面
     */
    @RequestMapping("/page")
    public String page(@RequestParam Map<String, Object> params) {
        return "useraccount";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Object list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<UserAccountEntity> userAccountList = userAccountServiceImpl.queryList(query);
        int total = userAccountServiceImpl.queryTotal(query);
        return putMsgToJsonString(Constants.WebSite.SUCCESS, "", total, userAccountList);
    }


    /**
     * 信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/info/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Object info(@PathVariable("id") Long id) {
        UserAccountEntity userAccount = userAccountServiceImpl.queryObject(id);
        return putMsgToJsonString(Constants.WebSite.SUCCESS, "", 0, userAccount);
    }

    /**
     * 保存
     *
     * @param userAccount
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Object save(@ModelAttribute UserAccountEntity userAccount) {
        userAccount.setDisablestate(0);
        userAccount.setIsdel(0);
        userAccountServiceImpl.save(userAccount);
        return putMsgToJsonString(Constants.WebSite.SUCCESS, "", 0, userAccount);
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Object update(@ModelAttribute UserAccountEntity userAccount) {
        int result = userAccountServiceImpl.update(userAccount);
        return putMsgToJsonString(result, "", 0, "");
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@RequestParam Long[] ids) {
        int result = userAccountServiceImpl.deleteBatch(ids);
        return putMsgToJsonString(result, "", 0, "");
    }

}
