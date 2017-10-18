package tech.tgls.mms.auth.wechat.domain;



import java.io.Serializable;

import tech.tgls.mms.auth.wechat.util.json.WxGsonBuilder;

/**
 * Created by Liya on 2017/4/14.
 */
public class WxAccessToken implements Serializable {
    private static final long serialVersionUID = 8709719312922168909L;

    private String access_token;

    private int expires_in = -1;

    public static WxAccessToken fromJson(String json) {
        return WxGsonBuilder.create().fromJson(json, WxAccessToken.class);
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}