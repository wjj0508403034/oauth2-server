package tech.tgls.mms.auth.account.security.weixin;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;


public class WeiXinPreAuthorizeFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WeiXinPreAuthorizeFilter.class);
	
//	@Autowired
//	private AuthenticationManager authenticationManager;
	
	private ApplicationContext applicationContext;

	public WeiXinPreAuthorizeFilter(ApplicationContext applicationContext) {
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
			String wxOpenId = this.getWxOpenID(state);
			if (!StringUtils.isEmpty(wxOpenId)) {
				// 微信的OpenId不等于空，自动登陆
				WeiXinToken authRequest = new WeiXinToken(wxOpenId);
				this.applicationContext.getBean(AuthenticationManager.class)
						.authenticate(authRequest);
			}
		}

		filterChain.doFilter(request, response);

	}

	/*
	 * 从state里面获取微信的openID state是下面字符串的base64码 weixin:openId
	 */
	private String getWxOpenID(String state) {
		if (!StringUtils.isEmpty(state)) {
			String stateStr = new String(Base64.getDecoder().decode(state));
			String[] parts = stateStr.split(":");
			if (parts.length == 2) {
				if (StringUtils.equalsIgnoreCase(parts[0], "weixin")) {
					if (!StringUtils.isEmpty(parts[1])) {
						return parts[1];
					} else {
						LOGGER.error("WeiXin openId is null");
					}
				}
			}
		}

		return null;
	}

}
