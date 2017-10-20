package tech.tgls.mms.auth.account.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.account.impl.UserDetailsImpl;
import tech.tgls.mms.auth.account.impl.UserDetailsServiceImpl;
import tech.tgls.mms.auth.sms.SmsService;

public class PhoneAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(PhoneAuthenticationProvider.class);
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	private UserDetailsServiceImpl userDetailsService;
	private SmsService smsService;

	public PhoneAuthenticationProvider(UserDetailsService userDetailsService, SmsService smsService) {
		this.userDetailsService = (UserDetailsServiceImpl) userDetailsService;
		this.smsService = smsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		PhoneAuthenticationToken token = (PhoneAuthenticationToken) authentication;
		String phone = token.getPhone();
		String smscode = token.getSmscode();

		UserDetails loadedUser = null;
		try {
			loadedUser = this.userDetailsService.loadUserByUsername(phone);
		} catch (UsernameNotFoundException notFound) {
			this.isSmsCodeValid(phone, smscode);
			logger.info("当前的电话号码{}还不是用户，自动转成用户", phone);
			Account account = this.userDetailsService.getAccountService().createPhoneAccount(phone);
			loadedUser = new UserDetailsImpl(account);
		} catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			logger.error("UserDetailsService returned null, which is an interface contract violation.");
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}

		this.isSmsCodeValid(phone, smscode);

		token.setPrincipal(loadedUser);
		token.setDetails(loadedUser);
		authentication.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

	private void isSmsCodeValid(String phone, String smscode) {
		if (!this.smsService.isSmsCodeValid(phone, smscode)) {
			logger.error("Bad credentials, sms code invalid or expired.");
			throw new BadCredentialsException(messages.getMessage("PhoneAuthenticationProvider.badCredentials",
					"Bad credentials, sms code invalid or expired."));
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(PhoneAuthenticationToken.class);
	}

}
