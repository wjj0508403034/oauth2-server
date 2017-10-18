package tech.tgls.mms.auth.wechat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WxInfo;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;
import tech.tgls.mms.auth.wechat.security.WechatStateToken;

@Controller
public class WechatController {

	private static final Logger logger = LoggerFactory.getLogger(WechatController.class);

	@Autowired
	private WeChatDelegateService weChatDelegateService;

	@RequestMapping("/wechat/oauthCallback")
	public String oauthCallback(HttpServletRequest request, HttpServletResponse response)
			throws IOException, URISyntaxException {

		// 用户同意授权后，能获取到微信code
		String code = request.getParameter("code");

		if (code == null) {
			logger.error("微信oauth回调返回的code为空");
			return "redirect:/errors/wx404";
		}

		logger.info("微信已经授权成功，获得的code为：" + code);

		String state = request.getParameter("state");
		if (StringUtils.isEmpty(state)) {
			logger.error("微信oauth回调返回的state为空");
			return "redirect:/errors/wx404";
		}

		String originAuthorizeRequestUrl = weChatDelegateService.obtainAuthorizeRequest(state);
		if (StringUtils.isEmpty(originAuthorizeRequestUrl)) {
			logger.error("授权超时，原来授权信息已经丢失");
			return "redirect:/errors/wx404";
		}
		
		originAuthorizeRequestUrl = URLDecoder.decode(originAuthorizeRequestUrl, "utf-8");

		String appId = this.getOpenIdFromUrl(originAuthorizeRequestUrl);

		// 从数据库读取公众号信息
		WxPublicAccount wxPublicAccount = weChatDelegateService.getPublicAccount(appId);
		if (wxPublicAccount == null) {
			logger.error("根据appId 没有找到对应公众号信息 {}", appId);
			return "redirect:/errors/wx404";
		}

		// 获取网页授权access_token
		Oauth2AccessToken weixinOauthToken = weChatDelegateService.getTokenByCode(wxPublicAccount, code);

		if (weixinOauthToken == null) {
			logger.error("获取微信token失败");
			return "redirect:/errors/wx404";
		}

		// 用户标识
		String openId = weixinOauthToken.getOpenid();
		logger.info("微信用户标识{}", openId);
		Account account = weChatDelegateService.createWeChatAccountIfNotExists(openId);

		WxInfo w = weChatDelegateService.findWxInfoFromDB(openId);

		JsonResultBean snsUserInfoResult = null;
		// 用户信息未获取
		if (w == null) {
			// 获取用户信息
			logger.info("wxInfo is null-----------");
			snsUserInfoResult = weChatDelegateService.getWeChatUserInfo(wxPublicAccount, weixinOauthToken);
			if (snsUserInfoResult.getStat() == Constants.RETURN_CODE_SUCCESS) {
				weChatDelegateService.bindWeChatUser(account);
				return this.loginAndContinueAuthorize(openId, originAuthorizeRequestUrl);
			}

			logger.error("获取微信用户{}信息错误", appId);
			return "redirect:/errors/wx404";
		}

		if (StringUtils.isBlank(w.getWxUnionId())) {
			snsUserInfoResult = weChatDelegateService.getWeChatUserInfo(wxPublicAccount, weixinOauthToken);
			if (snsUserInfoResult.getStat() == Constants.RETURN_CODE_SUCCESS) {
				weChatDelegateService.bindWeChatUser(account);
				return this.loginAndContinueAuthorize(openId, originAuthorizeRequestUrl);
			}

			logger.error("获取微信用户{}信息错误", appId);
			return "redirect:/errors/wx404";
		}

		logger.info("用户已经存在-------");
		return this.loginAndContinueAuthorize(openId, originAuthorizeRequestUrl);
	}

	private String loginAndContinueAuthorize(String openId, String authorizeUrl) {
		logger.info("用户{}登录系统，并且继续做授权请求。", openId);
		weChatDelegateService.loginByWechatOpenId(openId);
		String stateToken = weChatDelegateService.setWechatAuthorizeSuccessState();
		return "redirect:" +  authorizeUrl + "&authorizedToken=" + stateToken;
	}

	private String getOpenIdFromUrl(String authorizeUrl) throws URISyntaxException {
		String state = this.getOriginState(authorizeUrl);
		WechatStateToken stateToken = new WechatStateToken(state);
		return stateToken.getAppId();
	}

	private String getOriginState(String authorizeUrl) throws URISyntaxException {
		List<NameValuePair> params = new URIBuilder(authorizeUrl.replace(" ", "%20")).getQueryParams();
		for (NameValuePair param : params) {
			if (StringUtils.equalsIgnoreCase("state", param.getName())) {
				return param.getValue();
			}
		}

		return null;
	}
}
