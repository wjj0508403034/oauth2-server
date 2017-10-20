package tech.tgls.mms.auth.account.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.account.AccountService;

@Primary
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private AccountService accountService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = this.accountService.findByUsername(username);
		if (account == null) {
			throw new UsernameNotFoundException(username);
		}

		return new UserDetailsImpl(account);
	}

	public AccountService getAccountService() {
		return this.accountService;
	}

}
