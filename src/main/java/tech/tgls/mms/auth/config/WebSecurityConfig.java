package tech.tgls.mms.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import tech.tgls.mms.auth.account.security.PhoneAuthenticationProvider;
import tech.tgls.mms.auth.sms.SmsService;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private SmsService smsService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public PhoneAuthenticationProvider phoneAuthenticationProvider() {
		return new PhoneAuthenticationProvider(userDetailsService, smsService);
	}

//	@Autowired
//	private AuthenticationManager authenticationManager;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/ignore/**", "/loginByPhone").permitAll();
		http.formLogin().loginPage("/login").permitAll().and().authorizeRequests().anyRequest().authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.phoneAuthenticationProvider());
		//auth.parentAuthenticationManager(authenticationManager);
	}
}
