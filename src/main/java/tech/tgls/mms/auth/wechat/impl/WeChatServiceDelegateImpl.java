package tech.tgls.mms.auth.wechat.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import tech.tgls.mms.auth.common.AdvancedUtil;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.wechat.WeChatDelegateService;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WxInfo;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;
import tech.tgls.mms.auth.wechat.security.WechatToken;
import tech.tgls.mms.auth.wechat.WxOfficialAccountsService;

@Service
public class WeChatServiceDelegateImpl implements WeChatDelegateService {

	@Autowired
	private WxOfficialAccountsService wxOfficialAccountsService;

	@Autowired
	private AdvancedUtil advancedUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public WxPublicAccount getPublicAccount(String appId) {
		return this.wxOfficialAccountsService.findByAppId(appId);
	}

	@Override
	public String buildAuthorizeRequestUrl(WxPublicAccount account, String state)
			throws UnsupportedEncodingException {
		return this.advancedUtil.getRequestCodeUrl("/wechat/oauthCallback",
				"snsapi_userinfo", state, account.getAppId());
	}

	@Override
	public Oauth2AccessToken getTokenByCode(WxPublicAccount account, String code)
			throws IOException {
		return advancedUtil.getOauthAccessToken(account.getAppId(),
				account.getSecret(), code);
	}

	@Override
	public WxInfo findWxInfoFromDB(String openId) {
		return advancedUtil.getByOpenid(openId);
	}

	@Override
	public JsonResultBean getWeChatUserInfo(WxPublicAccount account,
			Oauth2AccessToken accessToken) throws IOException {
		return advancedUtil.getSNSUserInfoDelegate(accessToken,
				account.getAppId());
	}

	@Override
	public void loginByWechatOpenId(String openId) {
		WechatToken authRequest = new WechatToken(openId);
		this.authenticationManager.authenticate(authRequest);
	}
}
