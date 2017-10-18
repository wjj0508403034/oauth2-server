package tech.tgls.mms.auth.wechat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WxInfo;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;

public interface WeChatDelegateService {

	WxPublicAccount getPublicAccount(String appId);

	String buildAuthorizeRequestUrl(WxPublicAccount account, String state)
			throws UnsupportedEncodingException;

	Oauth2AccessToken getTokenByCode(WxPublicAccount account, String code)
			throws IOException;

	WxInfo findWxInfoFromDB(String openId);

	JsonResultBean getWeChatUserInfo(WxPublicAccount account,
			Oauth2AccessToken accessToken) throws IOException;
	
	void loginByWechatOpenId(String openId);

}
