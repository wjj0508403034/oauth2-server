package tech.tgls.mms.auth.account.impl;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import tech.tgls.mms.auth.account.Account;

public class UserDetailsImpl extends User {

	private static final long serialVersionUID = -7753321142410356192L;
	private Account account;

	public UserDetailsImpl(Account account) {
		super(account.getUsername(), account.getPasswordOrDefault(),
				AuthorityUtils.createAuthorityList("ROLE_USER"));
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}

}
