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
@ConfigurationProperties(prefix = "robot.moli")
@Component
public class MLRobotConf {
    private String apiUrl;

    private String key;

    private String secret;

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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
