package tech.tgls.mms.auth.wechat.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.account.AccountService;
import tech.tgls.mms.auth.common.AdvancedUtil;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.web.WebServerConfig;
import tech.tgls.mms.auth.wechat.WeChatDelegateService;
import tech.tgls.mms.auth.wechat.WxInfoService;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WxInfo;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;
import tech.tgls.mms.auth.wechat.repo.WxInfoRepo;
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

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private AccountService accountService;

	@Autowired
	private WxInfoService wxInfoService;

	@Autowired
	private WebServerConfig webServerConfig;
	
	@Autowired
	private WxInfoRepo wxInfoRepo;

	@Override
	public WxPublicAccount getPublicAccount(String appId) {
		return this.wxOfficialAccountsService.findByAppId(appId);
	}

	@Override
	public String buildAuthorizeRequestUrl(WxPublicAccount account, String state) throws UnsupportedEncodingException {
		return this.advancedUtil.getRequestCodeUrl(webServerConfig.getDomainAddress().trim() + "/wechat/oauthCallback",
				"snsapi_userinfo", state, account.getAppId());
	}

	@Override
	public Oauth2AccessToken getTokenByCode(WxPublicAccount account, String code) throws IOException {
		return advancedUtil.getOauthAccessToken(account.getAppId(), account.getSecret(), code);
	}

	@Override
	public WxInfo findWxInfoFromDB(String openId) {
		return advancedUtil.getByOpenid(openId);
	}

	@Transactional
	@Override
	public void bindWeChatUser(Account account,String openId) {
		WxInfo wxInfo = this.wxInfoRepo.findByOpenId(openId);
		if (wxInfo != null) {
			wxInfo.setUser(account);
			this.wxInfoRepo.save(wxInfo);
		}
	}

	@Override
	public JsonResultBean getWeChatUserInfo(WxPublicAccount account, Oauth2AccessToken accessToken) throws IOException {
		return advancedUtil.getSNSUserInfoDelegate(accessToken, account.getAppId());
	}

	@Override
	public void loginByWechatOpenId(String openId) {
		WechatToken authRequest = new WechatToken(openId);
		this.authenticationManager.authenticate(authRequest);
	}

	@Override
	public String storeAuthorizeRequest(String authorizeRequestUrl) {
		String uuid = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set(uuid, authorizeRequestUrl, 60 * 15, TimeUnit.SECONDS);
		return uuid;
	}

	@Override
	public String obtainAuthorizeRequest(String token) {
		return redisTemplate.opsForValue().get(token);
	}

	@Override
	public Account createWeChatAccountIfNotExists(String openId) {
		return this.accountService.createWechatAccountIfNotExists(openId);
	}

	@Override
	public String setWechatAuthorizeSuccessState(String openId) {
		String uuid = UUID.randomUUID().toString();
		redisTemplate.opsForValue().set(uuid, openId, 60 * 3, TimeUnit.SECONDS);
		return uuid;
	}

	@Override
	public String getWechatAuthorizeOpenId(String token) {
		return redisTemplate.opsForValue().get(token);
	}

	@Override
	public Account findBindUser(String openId) {
		WxInfo wxInfo = this.findWxInfoFromDB(openId);
		if(wxInfo != null){
			return wxInfo.getUser();
		}
		return null;
	}

	@Override
	public void autoLogin(Account account) {
		this.accountService.autoLogin(account);
	}

}
