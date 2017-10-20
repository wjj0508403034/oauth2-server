package tech.tgls.mms.auth.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server")
public class WebServerConfig {

	private String domainAddress;

	public String getDomainAddress() {
		return domainAddress;
	}

	public void setDomainAddress(String domainAddress) {
		this.domainAddress = domainAddress;
	}
}
