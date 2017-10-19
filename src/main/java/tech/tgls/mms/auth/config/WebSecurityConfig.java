package tech.tgls.mms.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import tech.tgls.mms.auth.account.security.PhoneAuthenticationProvider;
import tech.tgls.mms.auth.admin.AdminUserDetailsService;
import tech.tgls.mms.auth.admin.security.AdminAuthenticationProvider;
import tech.tgls.mms.auth.sms.SmsService;
import tech.tgls.mms.auth.wechat.security.WechatAuthenticationProvider;
import tech.tgls.mms.auth.wechat.security.WechatAuthorizeFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SmsService smsService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Qualifier(value = "adminUserDetailsService")
	@Autowired
	private AdminUserDetailsService adminUserDetailsService;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public PhoneAuthenticationProvider phoneAuthenticationProvider() {
		return new PhoneAuthenticationProvider(userDetailsService, smsService);
	}

	@Bean
	public WechatAuthenticationProvider weiXinAuthenticationProvider() {
		return new WechatAuthenticationProvider(userDetailsService);
	}

	@Bean
	public AdminAuthenticationProvider adminAuthenticationProvider() {
		AdminAuthenticationProvider provider =  new AdminAuthenticationProvider();
		provider.setUserDetailsService(adminUserDetailsService);
		return provider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
				.antMatchers("/ignore/**", "/loginByPhone","/admin/login.html","/admin/login", "/kaptcha-image",
						"/wechat/oauthCallback").permitAll();
		http.formLogin().loginPage("/login").permitAll().and()
				.authorizeRequests().anyRequest().authenticated();

		WechatAuthorizeFilter weiXinPreAuthorizeFilter = new WechatAuthorizeFilter(
				applicationContext);
		http.addFilterBefore(weiXinPreAuthorizeFilter,
				AbstractPreAuthenticatedProcessingFilter.class);
	}

	@Autowired
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.authenticationProvider(this.adminAuthenticationProvider());
		auth.authenticationProvider(this.weiXinAuthenticationProvider());
		auth.authenticationProvider(this.phoneAuthenticationProvider());
	}
}
