package tech.tgls.mms.auth.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 云片网APIKEY相关配置
 *
 * @author Lei Sun <leix.sun@qq.com>
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "yunpian")
public class YunPianConfig {
    private String smsApiKey;

    public String getSmsApiKey() {
        return smsApiKey;
    }

    public void setSmsApiKey(String smsApiKey) {
        this.smsApiKey = smsApiKey;
    }
}

