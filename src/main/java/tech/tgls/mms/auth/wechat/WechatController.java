package tech.tgls.mms.auth.wechat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tech.tgls.mms.auth.common.AdvancedUtil;
import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WxInfo;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;
import tech.tgls.mms.auth.wechat.security.WechatStateToken;

@Controller
public class WechatController {

	private static final Logger logger = LoggerFactory
			.getLogger(WechatController.class);

	@Autowired
	private WxOfficialAccountsService accountsService;

	@Autowired
	private WeChatDelegateService weChatDelegateService;

	@Autowired
	private AdvancedUtil advancedUtil;

	@RequestMapping("/wechat/oauthCallback")
	public String oauthCallback(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 用户同意授权后，能获取到微信code
		String code = request.getParameter("code");
		if (code != null) {
			return getRedirectUrlByWxOauth(request, response, code);
		} else {
			return "redirect:/errors/wx404";
		}
	}

	private String getRedirectUrlByWxOauth(HttpServletRequest request,
			HttpServletResponse response, String code) throws IOException {
		String state = request.getParameter("state");
		String originAuthorizeRequestUrl = "";
		WechatStateToken stateToken = new WechatStateToken(state);
		String appId = stateToken.getAppId();

		// 从数据库读取公众号信息
		WxPublicAccount wxPublicAccount = weChatDelegateService
				.getPublicAccount(appId);
		if (wxPublicAccount != null) {
			logger.info("已经授权成功，获得的code为：" + code);

			// 获取网页授权access_token
			Oauth2AccessToken weixinOauthToken = weChatDelegateService
					.getTokenByCode(wxPublicAccount, code);

			// 用户标识
			String openId = weixinOauthToken.getOpenid();

			WxInfo w = weChatDelegateService.findWxInfoFromDB(openId);

			JsonResultBean snsUserInfoResult = null;
			// 用户信息未获取
			if (w == null) {
				// 获取用户信息
				logger.info("wxInfo is null-----------");
				snsUserInfoResult = weChatDelegateService.getWeChatUserInfo(
						wxPublicAccount, weixinOauthToken);
				if (snsUserInfoResult.getStat() == Constants.RETURN_CODE_SUCCESS) {
					return this.loginAndContinueAuthorize(openId,originAuthorizeRequestUrl);
				} else {
					return "redirect:/errors/wx404";
				}

			} else if (w != null && StringUtils.isBlank(w.getWxUnionId())) {
				snsUserInfoResult = weChatDelegateService.getWeChatUserInfo(
						wxPublicAccount, weixinOauthToken);
				if (snsUserInfoResult.getStat() == Constants.RETURN_CODE_SUCCESS) {
					return this.loginAndContinueAuthorize(openId,originAuthorizeRequestUrl);
				} else {
					return "redirect:/errors/wx404";
				}
			} else {
				logger.info("用户已经存在-------");
				return this.loginAndContinueAuthorize(openId,originAuthorizeRequestUrl);
			}
		} else {
			logger.info("根据appId 没有找到对应公众号信息" + appId);
			return "redirect:/errors/wx404";

		}

	}

	private String loginAndContinueAuthorize(String openId,String authorizeUrl) {
		logger.info("用户{}登录系统，并且继续做授权请求。", openId);
		weChatDelegateService.loginByWechatOpenId(openId);
		return "";
	}
}
