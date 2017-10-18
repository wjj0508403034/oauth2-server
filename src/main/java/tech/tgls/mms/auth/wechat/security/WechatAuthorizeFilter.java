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
import org.springframework.web.filter.OncePerRequestFilter;

import tech.tgls.mms.auth.wechat.WeChatDelegateService;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;

public class WechatAuthorizeFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WechatAuthorizeFilter.class);

	private ApplicationContext applicationContext;

	public WechatAuthorizeFilter(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (StringUtils.startsWithIgnoreCase(uri, "/oauth/authorize")) {
			LOGGER.info("This is authorize request, url: {}",
					request.getRequestURL() + "?" + request.getQueryString());
			String state = request.getParameter("state");
			
			if (!StringUtils.isEmpty(state)) {
				WechatStateToken stateToken = new WechatStateToken(state);
				String officialAccountAppId = stateToken.getAppId();
				LOGGER.info("授权参数里面公众号的appId: {}", officialAccountAppId);
				
				if (officialAccountAppId == null) {
					// 跳转到授权错误页面,授权参数里面没有公众号的appId
					LOGGER.error("跳转到授权错误页面,授权参数里面没有公众号的appId");
					response.sendRedirect("/errors/oauth");
					return;
				}

				WxPublicAccount officialAccount = this.weChatDelegateService()
						.getPublicAccount(officialAccountAppId);
				if (officialAccount == null) {
					// 跳转到授权错误页面,授权参数里面的公众号的appId不正确或者没有在auth server里面注册
					LOGGER.error("跳转到授权错误页面,授权参数里面的公众号的appId不正确或者没有在auth server里面注册");
					response.sendRedirect("/errors/oauth");
					return;
				}

				// 跳转到微信去授权
				String weChatAuthorizeUrl = this.weChatDelegateService()
						.buildAuthorizeRequestUrl(officialAccount, state);
				response.sendRedirect(weChatAuthorizeUrl);
				return;
			}
		}

		filterChain.doFilter(request, response);

	}

	private WeChatDelegateService weChatDelegateService() {
		return this.applicationContext.getBean(WeChatDelegateService.class);
	}
}
