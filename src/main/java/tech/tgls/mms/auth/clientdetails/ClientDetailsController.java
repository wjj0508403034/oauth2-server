package tech.tgls.mms.auth.clientdetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ClientDetailsController {

	private final static Integer Day = 24 * 60 * 60;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/admin/clientdetails", method = RequestMethod.POST)
	@ResponseBody
	public ClientDetailsResult createOAuthClientDetails(
			@RequestBody ClientDetailsParam clientDetailsParam) {
		BaseClientDetails clientDetails = new BaseClientDetails();
		clientDetails.setClientId(UUID.randomUUID().toString());
		clientDetails.setClientSecret(UUID.randomUUID().toString());
		clientDetails.setAutoApproveScopes(this.autoApproveScopes());
		clientDetails.setAccessTokenValiditySeconds(Day);
		clientDetails.setRefreshTokenValiditySeconds(30 * Day);
		clientDetails.setAuthorizedGrantTypes(this.authorizedGrantTypes());
		clientDetails.setRegisteredRedirectUri(clientDetailsParam
				.getRedirectUrls());
		clientDetails.setScope(this.scopes());

		((JdbcClientDetailsService) this.clientDetailsService)
				.addClientDetails(clientDetails);
		return new ClientDetailsResult(clientDetails);
	}

	private Collection<String> autoApproveScopes() {
		List<String> scopes = new ArrayList<>();
		scopes.add("read");
		scopes.add("write");
		return scopes;
	}

	private Collection<String> authorizedGrantTypes() {
		List<String> grantTypes = new ArrayList<>();
		grantTypes.add("authorization_code");
		grantTypes.add("refresh_token");
		grantTypes.add("client_credentials");
		return grantTypes;
	}

	private Collection<String> scopes() {
		List<String> scopes = new ArrayList<>();
		scopes.add("read");
		scopes.add("write");
		return scopes;
	}

}
