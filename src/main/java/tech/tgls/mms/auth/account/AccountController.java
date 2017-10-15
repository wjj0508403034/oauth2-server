package tech.tgls.mms.auth.account;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@RequestMapping(value = "/api/user/me", method = RequestMethod.GET)
	@ResponseBody
	public UserInfo getCurrentUserInfo(Principal principal){
		return this.accountService.getUserInfo(principal);
	}
}
