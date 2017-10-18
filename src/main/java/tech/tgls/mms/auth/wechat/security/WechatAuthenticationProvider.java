package tech.tgls.mms.auth.wechat.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class WechatAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory
			.getLogger(WechatAuthenticationProvider.class);
	protected MessageSourceAccessor messages = SpringSecurityMessageSource
			.getAccessor();

	private UserDetailsService userDetailsService;

	public WechatAuthenticationProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		WechatToken token = (WechatToken) authentication;

		UserDetails loadedUser = null;
		try {
			loadedUser = this.userDetailsService.loadUserByUsername(token
					.getOpenId());
		} catch (UsernameNotFoundException notFound) {
			throw notFound;
		} catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			logger.error("UserDetailsService returned null, which is an interface contract violation.");
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}

		token.setPrincipal(loadedUser);
		token.setDetails(loadedUser);
		authentication.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(WechatToken.class);
	}

}
