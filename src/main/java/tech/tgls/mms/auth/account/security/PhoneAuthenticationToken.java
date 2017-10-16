package tech.tgls.mms.auth.account.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PhoneAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -2733053778964109645L;

	private Object principal;
	private Object credentials;

	public PhoneAuthenticationToken(String phone, String smscode) {
		super(null);
		this.principal = phone;
		this.credentials = smscode;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

}
