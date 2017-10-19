package tech.tgls.mms.auth.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ViewController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String indexPage() {
		return "index";
	}

	@RequestMapping(value = "login", method = RequestMethod.GET)
	public String loginPage() {
		return "login";
	}
	
	@RequestMapping(value = "authorize", method = RequestMethod.GET)
	public String authorizePage() {
		return "authorize";
	}
	
	@RequestMapping(value = "/admin/login.html", method = RequestMethod.GET)
	public String adminLoginPage() {
		return "admin-login";
	}
}
