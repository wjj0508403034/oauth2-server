package tech.tgls.mms.auth.wechat;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import tech.tgls.mms.auth.common.HttpClientUtils;
import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.wechat.domain.Oauth2AccessToken;
import tech.tgls.mms.auth.wechat.domain.WechatUser;
import tech.tgls.mms.auth.wechat.domain.WxAccessToken;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Liya on 2017/1/22.
 */
@Component
public class WeChatCommonUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(WeChatCommonUtils.class);
	@Autowired
	private WechatApiConfig wechatApiConfig;

	@Autowired
	private WxOfficialAccountsService officialAccountsService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * 方法说明 组装Oauth2.0授权地址
	 *
	 * @param appid
	 *            公众号appid
	 * @param state
	 *            可选参数无论授权成功还是失败都会返回
	 * @param scope
	 *            授权作用域
	 * @param redirectUrl
	 *            授权成功后回调地址
	 * @return 授权地址
	 */
	public String getWechatOauth2Url(String appid, String redirectUrl,
			String scope, String state) throws UnsupportedEncodingException {
		logger.info("组装微信Oauth2.0网页授权地址   start ...");
		redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
		String oauth2Url = wechatApiConfig.getOauth2CodeUrl();
		oauth2Url = oauth2Url + "?appid=" + appid + "&redirect_uri="
				+ redirectUrl + "&response_type=code&scope=" + scope
				+ "&state=" + state + "#wechat_redirect";
		logger.info("组装微信Oauth2.0网页授权地址   end ...oauth2Url:{}", oauth2Url);
		return oauth2Url;
	}

	/**
	 * 方法说明 获取指定公众号或开放平台应用的微信Oauth2.0授权access_token凭证
	 * 
	 * @param appId
	 *            公众号appid
	 * @param appSecret
	 *            公众号appSecret
	 * @param code
	 *            微信授权code
	 * @return Oauth2AccessToken 或者null
	 * @throws IOException
	 */
	public Oauth2AccessToken getOauth2AccessToken(String appId,
			String appSecret, String code) throws IOException {
		Oauth2AccessToken oauth2AccessToken = null;
		String oauth2AccessTokenUrl = wechatApiConfig.getOauth2AccessTokenUrl();
		oauth2AccessTokenUrl = oauth2AccessTokenUrl + "?appid=" + appId
				+ "&secret=" + appSecret + "&code=" + code
				+ "&grant_type=authorization_code";
		long startTime = System.currentTimeMillis();
		logger.info("oauth2AccessTokenUrl:" + oauth2AccessTokenUrl);
		JsonResultBean jsonResultBean = HttpClientUtils
				.get(oauth2AccessTokenUrl);
		long endTime = System.currentTimeMillis();
		logger.info("获取指定公众号或开放平台应用的微信Oauth2.0授权access_token凭证执行耗时："
				+ (endTime - startTime) / 1000 + "秒");

		if (jsonResultBean.getStat() == Constants.RETURN_CODE_SUCCESS) {
			if (jsonResultBean.getData().toString().contains("errcode")) {
				oauth2AccessToken = null;
				logger.error("获取网页授权access_token失败：" + jsonResultBean.getData());
			} else {
				Object jsonObject = jsonResultBean.getData();
				logger.info("获取的网页授权access_token信息：:" + jsonObject);
				if (null != jsonObject) {
					mapper.configure(
							JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,
							true);// 允许反斜杆等字符
					mapper.configure(
							DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
							false);
					long startTime2 = System.currentTimeMillis();
					oauth2AccessToken = mapper.readValue(jsonObject.toString(),
							Oauth2AccessToken.class);
					long endTime2 = System.currentTimeMillis();
					logger.info("Oauth2AccessToken json格式化执行耗时："
							+ (endTime2 - startTime2) / 1000 + "秒");
					oauth2AccessToken.setAppid(appId);
				}
			}
		} else {
			oauth2AccessToken = null;
			logger.error("获取网页授权凭证失败 ");
		}
		return oauth2AccessToken;
	}

	/**
	 *
	 * 方法说明 调用微信api获取微信用户基本信息
	 *
	 * @param openId
	 * @return WechatUserJson or 空
	 * @param appid
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public WechatUser refetchedWechatUser(String accessToken, String openId,
			String appid) throws IOException {
		String wechatUserJson = null;
		WechatUser wechatUser = null;
		// 1.调用接口
		String apiUrl = wechatApiConfig.getOauth2UserUrl();
		apiUrl = apiUrl + "?access_token=" + accessToken + "&openid=" + openId
				+ "&lang=zh_CN";

		JsonResultBean jsonResultBean = HttpClientUtils.httpRequest(apiUrl,
				"GET", null);
		if (jsonResultBean.getStat() == Constants.RETURN_CODE_SUCCESS) {
			wechatUserJson = jsonResultBean.getData().toString();
			logger.info("获取用户基本信息：" + wechatUserJson);
			if (wechatUserJson.contains("errcode")) {
				logger.error("获取微信用户信息出错！");
			} else {
				// 获取成功
				mapper.configure(
						JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);// 允许反斜杆等字符
				mapper.configure(
						DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
						false);
				wechatUser = mapper.readValue(wechatUserJson, WechatUser.class);
			}

		} else {
			logger.error("获取微信用户信息出错！");

		}

		return wechatUser;
	}

	/**
	 * 根据appid获取access_token
	 * 
	 * @param appId
	 * @return
	 */
	public String getToken(String appId) throws IOException {
		String accessToken = null;
		WxPublicAccount wxPublicAccount = officialAccountsService
				.findByAppId(appId);
		if (wxPublicAccount == null) {
			accessToken = null;
			logger.error("数据库中不存该公众号信息 ");
		} else {

			String wxAccessTokenUrl = wechatApiConfig.getCommonAccessTokenUrl();
			wxAccessTokenUrl = wxAccessTokenUrl + "?appid=" + appId
					+ "&secret=" + wxPublicAccount.getSecret()
					+ "&grant_type=client_credential";
			// JsonResultBean jsonResultBean =
			// HttpClientUtils.httpRequest(wxAccessTokenUrl, "GET", null);
			JsonResultBean jsonResultBean = HttpClientUtils.httpRequest(
					wxAccessTokenUrl, "GET", null);
			logger.info("获取access_token url：" + wxAccessTokenUrl);
			if (jsonResultBean.getStat() == Constants.RETURN_CODE_SUCCESS) {
				if (jsonResultBean.getData().toString().contains("errcode")) {

					logger.error("获取普通access_token失败");
				} else {
					Object jsonObject = jsonResultBean.getData();
					logger.info("获取的普通access_token信息：:" + jsonObject);
					if (null != jsonObject) {
						mapper.configure(
								JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,
								true);// 允许反斜杆等字符
						WxAccessToken wxAccessToken = mapper.readValue(
								jsonObject.toString(), WxAccessToken.class);
						accessToken = wxAccessToken.getAccess_token();
					}
				}
			} else {
				logger.error("获取access_token失败 ");
			}
		}
		return accessToken;
	}

	public String getticket(String appId) throws IOException {
		// 取消缓存
		// String jsapiTicket =
		// redisTemplate.opsForValue().get("wxJsapiTicket");
		String accessToken = getToken(appId);

		String getTicketUrl = wechatApiConfig.getGetTicketUrl();
		String requestUrl = getTicketUrl
				+ "?access_token=ACCESSTOKEN&type=jsapi";
		requestUrl = requestUrl.replace("ACCESSTOKEN", accessToken);
		// 获取授权凭证
		JsonResultBean jsonResult = HttpClientUtils.httpRequest(requestUrl,
				"GET", null);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = null;
		try {
			map = mapper.readValue(jsonResult.getData().toString(),
					new TypeReference<Map<String, Object>>() {
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map.get("ticket").toString();
		// jsapiTicket = map.get("ticket").toString();
		// redisTemplate.opsForValue().set("wxJsapiTicket", jsapiTicket);
		// redisTemplate.expire("wxJsapiTicket", 7000, TimeUnit.SECONDS);
		// return jsapiTicket;
	}
}