package tech.tgls.mms.auth.error;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class ErrorHandlerUtils {

	public void redirectToErrorPage(HttpServletResponse response, String errorCode) throws IOException {
		response.sendRedirect("/error.html?code=" + errorCode);
	}
}
