package tech.tgls.mms.auth.wechat.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.account.AccountService;
import tech.tgls.mms.auth.error.ErrorCodes;
import tech.tgls.mms.auth.error.ErrorHandlerUtils;
import tech.tgls.mms.auth.wechat.WeChatDelegateService;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;

public class WechatAuthorizeFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(WechatAuthorizeFilter.class);

	private ApplicationContext applicationContext;

	public WechatAuthorizeFilter(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (StringUtils.startsWithIgnoreCase(uri, "/oauth/authorize")) {
			String authorizeUrl = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request)).build()
					.toUriString();
			LOGGER.info("This is authorize request, url: {}", authorizeUrl);
			String authorizedToken = request.getParameter("authorizedToken");
			if (!StringUtils.isEmpty(authorizedToken)) {
				LOGGER.info("授权参数里面authorizedToken: {}", authorizedToken);
				String openId = this.weChatDelegateService().getWechatAuthorizeOpenId(authorizedToken);
				if(!StringUtils.isEmpty(openId)){
					LOGGER.info("微信已经授权成功。");
					Account account = this.accountService().getCurrentAccount();
					if(account != null){
						this.weChatDelegateService().bindWeChatUser(account,openId);
					}
					filterChain.doFilter(request, response);
					return;
				}
			}

			String state = request.getParameter("state");

			if (!StringUtils.isEmpty(state)) {
				WechatStateToken stateToken = new WechatStateToken(state);
				String officialAccountAppId = stateToken.getAppId();
				LOGGER.info("授权参数里面公众号的appId: {}", officialAccountAppId);

				if (officialAccountAppId == null) {
					// 跳转到授权错误页面,授权参数里面没有公众号的appId
					LOGGER.error("跳转到授权错误页面,授权参数里面没有公众号的appId");
					this.errorHandlerUtils().redirectToErrorPage(response, ErrorCodes.Authorize_Error_Without_App_Id);
					return;
				}

				WxPublicAccount officialAccount = this.weChatDelegateService().getPublicAccount(officialAccountAppId);
				if (officialAccount == null) {
					// 跳转到授权错误页面,授权参数里面的公众号的appId不正确或者没有在auth server里面注册
					LOGGER.error("跳转到授权错误页面,授权参数里面的公众号的appId不正确或者没有在auth server里面注册");
					this.errorHandlerUtils().redirectToErrorPage(response,
							ErrorCodes.Authorize_Error_With_Invalid_App_Id);
					return;
				}

				String newState = this.weChatDelegateService().storeAuthorizeRequest(authorizeUrl);
				// 跳转到微信去授权
				String weChatAuthorizeUrl = this.weChatDelegateService().buildAuthorizeRequestUrl(officialAccount,
						newState);
				response.sendRedirect(weChatAuthorizeUrl);
				return;
			}
		}

		filterChain.doFilter(request, response);

	}

	private ErrorHandlerUtils errorHandlerUtils() {
		return this.applicationContext.getBean(ErrorHandlerUtils.class);
	}

	private WeChatDelegateService weChatDelegateService() {
		return this.applicationContext.getBean(WeChatDelegateService.class);
	}
	
	private AccountService accountService(){
		return this.applicationContext.getBean(AccountService.class);
	}
}
