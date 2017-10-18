package tech.tgls.mms.auth.wechat.security;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class WechatStateToken {

	private List<String> tokens = new ArrayList<>();

	public WechatStateToken(String state) {
		byte[] decodeBytes = Base64.getDecoder().decode(state);
		String temp = new String(decodeBytes);
		for (String part : temp.split(":")) {
			this.tokens.add(part);
		}
	}

	public String getAppId() {
		if (this.tokens.size() >= 2) {
			return this.tokens.get(1);
		}

		return null;
	}

	public String getTokenType() {
		if (this.tokens.size() >= 1) {
			return this.tokens.get(0);
		}

		return null;
	}
}
