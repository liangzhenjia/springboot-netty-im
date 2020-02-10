package com.jiajia.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MLRobotConf
 * <p>
 * Description
 *
 * @Author LiangZhenjia <79416121@qq.com>
 * @Date 2020/1/25
 */
@ConfigurationProperties(prefix = "robot.turing")
@Component
public class TuringConf {
    private String apiUrl;

    private String key;

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
