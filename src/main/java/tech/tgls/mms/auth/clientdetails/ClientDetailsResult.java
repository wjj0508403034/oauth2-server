package tech.tgls.mms.auth.clientdetails;

import org.springframework.security.oauth2.provider.ClientDetails;

public class ClientDetailsResult {

	private String client_id;
	private String client_secret;
	private String redirect_uri;
	private String authorized_grant_types;
	private String scope;

	public ClientDetailsResult(ClientDetails details) {
		this.client_id = details.getClientId();
		this.client_secret = details.getClientSecret();
		this.redirect_uri = String
				.join(",", details.getRegisteredRedirectUri());
		this.authorized_grant_types = String.join(",",
				details.getAuthorizedGrantTypes());
		this.scope = String.join(",", details.getScope());
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	public String getRedirect_uri() {
		return redirect_uri;
	}

	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}

	public String getAuthorized_grant_types() {
		return authorized_grant_types;
	}

	public void setAuthorized_grant_types(String authorized_grant_types) {
		this.authorized_grant_types = authorized_grant_types;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}
