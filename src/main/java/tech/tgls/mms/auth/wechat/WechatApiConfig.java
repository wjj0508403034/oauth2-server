package tech.tgls.mms.auth.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by Liya on 2017/1/22.
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatApiConfig {

    private String commonAccessTokenUrl;//获取公众号普通类型AccessToken的接口地址

    private String jsapiTicketUrl;//获取公众号jsapiTicket的接口地址

    private String oauth2CodeUrl;//微信oauth2.0授权入口的接口地址

    private String oauth2AccessTokenUrl;//微信oauth2.0授权获取access_token的接口地址

    private String oauth2UserUrl;//微信oauth2.0授权获取微信用户信息的接口地址

    private  String getTicketUrl;//调用微信卡券JS API的临时票据，有效期为7200 秒，通过access_token 来获取


    public String getCommonAccessTokenUrl() {
        return commonAccessTokenUrl;
    }

    public void setCommonAccessTokenUrl(String commonAccessTokenUrl) {
        this.commonAccessTokenUrl = commonAccessTokenUrl;
    }

    public String getJsapiTicketUrl() {
        return jsapiTicketUrl;
    }

    public void setJsapiTicketUrl(String jsapiTicketUrl) {
        this.jsapiTicketUrl = jsapiTicketUrl;
    }

    public String getOauth2CodeUrl() {
        return oauth2CodeUrl;
    }

    public void setOauth2CodeUrl(String oauth2CodeUrl) {
        this.oauth2CodeUrl = oauth2CodeUrl;
    }

    public String getOauth2AccessTokenUrl() {
        return oauth2AccessTokenUrl;
    }

    public void setOauth2AccessTokenUrl(String oauth2AccessTokenUrl) {
        this.oauth2AccessTokenUrl = oauth2AccessTokenUrl;
    }

    public String getOauth2UserUrl() {
        return oauth2UserUrl;
    }

    public void setOauth2UserUrl(String oauth2UserUrl) {
        this.oauth2UserUrl = oauth2UserUrl;
    }

    public String getGetTicketUrl() {
        return getTicketUrl;
    }

    public void setGetTicketUrl(String getTicketUrl) {
        this.getTicketUrl = getTicketUrl;
    }
}