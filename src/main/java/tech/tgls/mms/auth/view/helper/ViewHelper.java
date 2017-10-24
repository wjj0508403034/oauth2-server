package tech.tgls.mms.auth.view.helper;

import javax.servlet.http.HttpServletRequest;

public class ViewHelper {

	public static boolean hasAttr(HttpServletRequest request, String attrName) {
		return request.getAttribute(attrName) != null;
	}

	public static boolean isAccountLargeZero(HttpServletRequest request) {
		Integer account = (Integer) request.getAttribute("account");
		return account != null && account > 0;
	}
	
	public static int getAccountAttr(HttpServletRequest request){
		Integer account = (Integer) request.getAttribute("account");
		if(account != null){
			return account.intValue();
		}
		
		return 0;
	}
}
