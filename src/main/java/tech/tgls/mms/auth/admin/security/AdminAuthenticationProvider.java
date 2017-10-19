package tech.tgls.mms.auth.admin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import tech.tgls.mms.auth.admin.AdminUserDetailsService;

@SuppressWarnings("deprecation")
public class AdminAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory
			.getLogger(AdminAuthenticationProvider.class);
	protected MessageSourceAccessor messages = SpringSecurityMessageSource
			.getAccessor();

	private AdminUserDetailsService adminUserDetailsService;

	private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();

	private SaltSource saltSource;

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(AdminAuthenticationToken.class);
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		AdminAuthenticationToken token = (AdminAuthenticationToken) authentication;
		String username = token.getUsername();

		UserDetails loadedUser = null;
		try {
			loadedUser = this.adminUserDetailsService
					.loadUserByUsername(username);
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

		try {
			additionalAuthenticationChecks(loadedUser, token);
		} catch (AuthenticationException exception) {
			logger.error("Username and password are invalid.", exception);
			throw exception;
		}

		token.setPrincipal(loadedUser);
		token.setDetails(loadedUser);
		token.setAuthorities(loadedUser.getAuthorities());
		authentication.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

	public void setUserDetailsService(
			AdminUserDetailsService adminUserDetailsService) {
		this.adminUserDetailsService = adminUserDetailsService;
	}

	protected void additionalAuthenticationChecks(UserDetails userDetails,
			AdminAuthenticationToken authentication)
			throws AuthenticationException {
		Object salt = null;

		if (this.saltSource != null) {
			salt = this.saltSource.getSalt(userDetails);
		}

		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.isPasswordValid(userDetails.getPassword(),
				presentedPassword, salt)) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"));
		}
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

}
