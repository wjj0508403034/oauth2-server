package tech.tgls.mms.auth.view.helper;

import javax.servlet.http.HttpServletRequest;

public class StaticHelper {
	
    private static String version;

    public static String getPath(HttpServletRequest request){
        return request.getContextPath();
    }

    public static String getVersion() {
        return "v=022";
    }
}
