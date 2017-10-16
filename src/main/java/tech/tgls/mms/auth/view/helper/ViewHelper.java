package tech.tgls.mms.auth.view.helper;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

public class ViewHelper {

	public static boolean hasAttr(HttpServletRequest request, String attrName) {
		return request.getAttribute(attrName) != null;
	}

	public static boolean isAccountLargeZero(HttpServletRequest request) {
		String account = (String) request.getAttribute("account");
		if (!StringUtils.isEmpty(account)) {
			return Integer.valueOf(account) > 0;
		}

		return false;
	}
}
