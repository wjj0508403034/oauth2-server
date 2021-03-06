package tech.tgls.mms.auth.wechat.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class WechatToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -6273156219651714388L;

	private String openId;
	private Object principal;
	private Object credentials;

	public WechatToken(String openId) {
		super(null);
		this.openId = openId;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

	public String getOpenId() {
		return openId;
	}

}
