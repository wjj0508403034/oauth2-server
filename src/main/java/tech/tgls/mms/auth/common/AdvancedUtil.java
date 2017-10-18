package tech.tgls.mms.auth.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.wechat.WeChatCommonUtils;
import tech.tgls.mms.auth.wechat.WxInfoService;
import tech.tgls.mms.auth.wechat.WxOfficialAccountsService;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WechatUser;
import tech.tgls.mms.auth.wechat.domain.WxInfo;
import tech.tgls.mms.auth.wechat.util.Auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Liya on 2016/12/21.
 */
@Component
public class AdvancedUtil {
	private static final Logger log = LoggerFactory
			.getLogger(AdvancedUtil.class);

	@Autowired
	private WxInfoService wxInfoService;

	@Autowired
	private WeChatCommonUtils weChatCommonUtils;

	@Autowired
	private WxOfficialAccountsService accountsService;

	/**
	 * @param redirectUrl
	 *            回调地址 return 返回获取code的地址
	 */
	public String getRequestCodeUrl(String redirectUrl, String scope,
			String state, String appId) throws UnsupportedEncodingException {
		log.info("获取的回调地址为：" + redirectUrl);
		return weChatCommonUtils.getWechatOauth2Url(appId, redirectUrl, scope,
				state);
	}

	// return
	// "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx580a186bb09b91f8&redirect_uri=http://mqj.wojilu.com/goback/doGet&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";

	/**
	 * 获取网页授权凭证
	 *
	 * @param appId
	 *            公众账号的唯一标识
	 * @param appSecret
	 *            公众账号的密钥
	 * @param code
	 * @return WeixinAouth2Token
	 */
	public Oauth2AccessToken getOauthAccessToken(String appId,
			String appSecret, String code) throws IOException {
		Oauth2AccessToken wat = null;
		wat = weChatCommonUtils.getOauth2AccessToken(appId, appSecret, code);

		if (wat != null) {
			return wat;
		} else {
			wat = null;
			log.error("获取网页授权凭证失败 ");
		}

		return wat;
	}

	/**
	 * 通过网页授权获取用户信息
	 * 
	 * @return SNSUserInfo
	 */

	public JsonResultBean getSNSUserInfo(Oauth2AccessToken oauth2AccessToken,
			HttpServletRequest request, HttpServletResponse response,
			String appId) throws IOException {
		JsonResultBean resultBean = new JsonResultBean();

		WechatUser wechatUser = getWechatUserInfo(oauth2AccessToken, request,
				response, appId);
		if (wechatUser != null) {
			resultBean.setData(wechatUser);
			resultBean.setStat(Constants.RETURN_CODE_SUCCESS);
		} else {
			resultBean.setStat(Constants.RETURN_CODE_FAIL);
			log.info("用户获取失败");
			return resultBean;
		}
		return resultBean;
	}
	
	/**
	 * 通过网页授权获取用户信息
	 * 
	 * @return SNSUserInfo
	 */

	public JsonResultBean getSNSUserInfoDelegate(Oauth2AccessToken oauth2AccessToken,
			String appId) throws IOException {
		JsonResultBean resultBean = new JsonResultBean();

		WechatUser wechatUser = getWechatUserInfoDelegate(oauth2AccessToken, appId);
		if (wechatUser != null) {
			resultBean.setData(wechatUser);
			resultBean.setStat(Constants.RETURN_CODE_SUCCESS);
		} else {
			resultBean.setStat(Constants.RETURN_CODE_FAIL);
			log.info("用户获取失败");
			return resultBean;
		}
		return resultBean;
	}

	/**
	 * 方法说明 获取微信用户基本信息
	 * 
	 * @param oauth2AccessToken
	 * @return
	 * @throws IOException
	 */
	public WechatUser getWechatUserInfo(Oauth2AccessToken oauth2AccessToken,
			HttpServletRequest request, HttpServletResponse response,
			String appId) throws IOException {

		String openId = oauth2AccessToken.getOpenid();
		String accessToken = oauth2AccessToken.getAccess_token();
		WechatUser wechatUser = null;
		wechatUser = refetchedWechatUser(request, response, accessToken,
				openId, appId);
		return wechatUser;

	}
	
	/**
	 * 方法说明 获取微信用户基本信息
	 * 
	 * @param oauth2AccessToken
	 * @return
	 * @throws IOException
	 */
	public WechatUser getWechatUserInfoDelegate(Oauth2AccessToken oauth2AccessToken,
			String appId) throws IOException {

		String openId = oauth2AccessToken.getOpenid();
		String accessToken = oauth2AccessToken.getAccess_token();
		WechatUser wechatUser = null;
		wechatUser = refetchedWechatUserDelegate(accessToken,
				openId, appId);
		return wechatUser;

	}

	/**
	 *
	 * @param accessToken
	 * @param openId
	 * @param appId
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public WechatUser refetchedWechatUser(HttpServletRequest request,
			HttpServletResponse response, String accessToken, String openId,
			String appId) throws IOException {
		WechatUser wechatUser = null;
		WxInfo snsUserInfo = null;
		wechatUser = weChatCommonUtils.refetchedWechatUser(accessToken, openId,
				appId);
		if (wechatUser != null) {
			// 保存到数据库
			snsUserInfo = new WxInfo();
			// 用户的标识
			snsUserInfo.setWxOpenId(wechatUser.getOpenid());

			// 昵称
			snsUserInfo.setNickName(wechatUser.getNickname());

			snsUserInfo.setAvatarUrl(wechatUser.getHeadimgurl());

			snsUserInfo.setWxAppId(appId);

			snsUserInfo.setWxUnionId(wechatUser.getUnionid());

			addOrUpdate(request, response, snsUserInfo, appId);
			// // 更新数据库微信用户信息
			// wechatUser = wechatUserWebService.addOrUpdate(wechatUser);
			// // 写入缓存
			// wechatUserJson = JsonUtil.objectToJson(wechatUser, config);
			// iCache.add(wechatUserKey, 43200, wechatUserJson,
			// CacheTimeUnit.SECOND);
		}

		return wechatUser;
	}
	
	/**
	 *
	 * @param accessToken
	 * @param openId
	 * @param appId
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public WechatUser refetchedWechatUserDelegate( String accessToken, String openId,
			String appId) throws IOException {
		WechatUser wechatUser = null;
		WxInfo snsUserInfo = null;
		wechatUser = weChatCommonUtils.refetchedWechatUser(accessToken, openId,
				appId);
		if (wechatUser != null) {
			// 保存到数据库
			snsUserInfo = new WxInfo();
			// 用户的标识
			snsUserInfo.setWxOpenId(wechatUser.getOpenid());

			// 昵称
			snsUserInfo.setNickName(wechatUser.getNickname());

			snsUserInfo.setAvatarUrl(wechatUser.getHeadimgurl());

			snsUserInfo.setWxAppId(appId);

			snsUserInfo.setWxUnionId(wechatUser.getUnionid());

			addOrUpdateDelegate( snsUserInfo, appId);
			// // 更新数据库微信用户信息
			// wechatUser = wechatUserWebService.addOrUpdate(wechatUser);
			// // 写入缓存
			// wechatUserJson = JsonUtil.objectToJson(wechatUser, config);
			// iCache.add(wechatUserKey, 43200, wechatUserJson,
			// CacheTimeUnit.SECOND);
		}

		return wechatUser;
	}

	private void addOrUpdate(HttpServletRequest request,
			HttpServletResponse response, WxInfo wxInfo, String appId) {
		boolean exist;
		WxInfo existWxInfo = getByOpenid(wxInfo.getWxOpenId());
		if (existWxInfo == null)
			exist = false;
		else
			exist = true;
		// 判断是否存在 //true 表存在
		if (exist) {
			// 存在修改信息
			existWxInfo.setCreateTime(new Date());

			existWxInfo.setWxUnionId(wxInfo.getWxUnionId());
			Auth.addWxInfoIdAuthCookie(request, response, existWxInfo.getId());
			wxInfoService.update(existWxInfo);
		} else {
			// 不存在添加
			wxInfo.setCreateTime(new Date());
			wxInfoService.insert(wxInfo);
			Auth.addWxInfoIdAuthCookie(request, response, wxInfo.getId());
		}

	}
	
	private void addOrUpdateDelegate(WxInfo wxInfo, String appId) {
		boolean exist;
		WxInfo existWxInfo = getByOpenid(wxInfo.getWxOpenId());
		if (existWxInfo == null)
			exist = false;
		else
			exist = true;
		// 判断是否存在 //true 表存在
		if (exist) {
			// 存在修改信息
			existWxInfo.setCreateTime(new Date());

			existWxInfo.setWxUnionId(wxInfo.getWxUnionId());
			wxInfoService.update(existWxInfo);
		} else {
			// 不存在添加
			wxInfo.setCreateTime(new Date());
			wxInfoService.insert(wxInfo);
		}

	}

	public WxInfo getByOpenid(String openid) {
		WxInfo w = wxInfoService.existFindByOpenId(openid);
		return w;
	}

	/*
	 * test
	 */
	public JsonResultBean getSNSUserInfo(HttpServletRequest request,
			HttpServletResponse response, String openId, String appId)
			throws IOException {
		JsonResultBean resultBean = new JsonResultBean();
		WxInfo snsUserInfo = null;
		snsUserInfo = new WxInfo();
		// 用户的标识
		snsUserInfo.setWxOpenId(openId);
		snsUserInfo.setWxAppId(appId);
		snsUserInfo.setWxUnionId("unionId");
		addOrUpdate(request, response, snsUserInfo, appId);

		resultBean.setStat(Constants.RETURN_CODE_SUCCESS);
		return resultBean;
	}

}