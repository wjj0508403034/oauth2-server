package tech.tgls.mms.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import tech.tgls.mms.auth.account.security.PhoneAuthenticationProvider;
import tech.tgls.mms.auth.account.security.weixin.WeiXinAuthenticationProvider;
import tech.tgls.mms.auth.account.security.weixin.WeiXinPreAuthorizeFilter;
import tech.tgls.mms.auth.sms.SmsService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SmsService smsService;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public PhoneAuthenticationProvider phoneAuthenticationProvider() {
		return new PhoneAuthenticationProvider(userDetailsService, smsService);
	}
	
	@Bean
	public WeiXinAuthenticationProvider weiXinAuthenticationProvider(){
		return new WeiXinAuthenticationProvider(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/ignore/**", "/loginByPhone","/kaptcha-image").permitAll();
		http.formLogin().loginPage("/login").permitAll().and().authorizeRequests().anyRequest().authenticated();
		
		WeiXinPreAuthorizeFilter weiXinPreAuthorizeFilter = new WeiXinPreAuthorizeFilter(applicationContext);
		http.addFilterBefore(weiXinPreAuthorizeFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.weiXinAuthenticationProvider());
		auth.authenticationProvider(this.phoneAuthenticationProvider());
	}
}
