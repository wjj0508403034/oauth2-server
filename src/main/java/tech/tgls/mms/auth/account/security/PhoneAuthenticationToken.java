package tech.tgls.mms.auth.account.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PhoneAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -2733053778964109645L;

	private Object principal;
	private Object credentials;
	private String smscode;
	private String phone;

	public PhoneAuthenticationToken(String phone, String smscode) {
		super(null);
		this.phone = phone;
		this.smscode = smscode;
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


	public String getSmscode() {
		return smscode;
	}

	public String getPhone() {
		return phone;
	}

}
