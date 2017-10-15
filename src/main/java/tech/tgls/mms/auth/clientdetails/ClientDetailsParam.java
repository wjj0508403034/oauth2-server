package tech.tgls.mms.auth.clientdetails;

import java.util.HashSet;
import java.util.Set;

public class ClientDetailsParam {

	private String callbackUrl;

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public Set<String> getRedirectUrls() {
		Set<String> sets = new HashSet<>();
		sets.add(this.getCallbackUrl());
		return sets;
	}
}
