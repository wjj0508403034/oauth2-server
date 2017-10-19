package tech.tgls.mms.auth.admin.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class AdminAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -3625665688626567368L;

	private Collection<GrantedAuthority> authorities = new ArrayList<>();
	private Object principal;
	private Object credentials;
	private String username;

	public AdminAuthenticationToken(String username, String password) {
		super(null);

		this.username = username;
		this.credentials = password;
	}

	public void setPrincipal(Object principal) {
		this.principal = principal;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	public void setAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		this.authorities.addAll(authorities);
	}

}
