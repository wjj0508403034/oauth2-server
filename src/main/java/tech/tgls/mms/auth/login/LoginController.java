package tech.tgls.mms.auth.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import tech.tgls.mms.auth.account.security.PhoneAuthenticationToken;
import tech.tgls.mms.auth.admin.security.AdminAuthenticationToken;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory
			.getLogger(LoginController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@RequestMapping(value = "/loginByPhone", method = RequestMethod.POST)
	public String loginByPhone(HttpServletRequest request,
			HttpServletResponse response, Model model,
			@RequestParam(name = "username") String phone,
			@RequestParam(name = "smscode") String smscode)
			throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		logger.info(" begin login ...");

		try {
			PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(
					phone, smscode);

			Authentication authResult = this.authenticationManager
					.authenticate(authRequest);

			long endTime = System.currentTimeMillis();
			logger.info("login successfully.");
			logger.info("LoginController.loginByPhone执行耗时："
					+ (endTime - startTime) / 1000 + "秒");

			SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
			successHandler.onAuthenticationSuccess(request, response,
					authResult);
			return null;
		} catch (AuthenticationException ex) {
			logger.error("login failed.", ex);
			Integer account = (Integer) request.getAttribute("account");
			if (account == null) {
				account = 0;
			}
			request.setAttribute("account", ++account);
			return "login";
		}
	}

	@RequestMapping(value = "/admin/login", method = RequestMethod.POST)
	public String loginByAdmin(HttpServletRequest request,
			HttpServletResponse response, Model model,
			@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password)
			throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		logger.info("Admin login ...");
		try {
			AdminAuthenticationToken authRequest = new AdminAuthenticationToken(
					username, password);

			Authentication authResult = this.authenticationManager
					.authenticate(authRequest);

			long endTime = System.currentTimeMillis();
			logger.info("login successfully.");
			logger.info("LoginController.loginByAdmin执行耗时："
					+ (endTime - startTime) / 1000 + "秒");

			SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
			successHandler.onAuthenticationSuccess(request, response,
					authResult);
			return null;
		} catch (AuthenticationException ex) {
			logger.error("Admin login failed.", ex);
			return "admin-login";
		}
	}
}
