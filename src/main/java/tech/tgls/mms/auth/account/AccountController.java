package tech.tgls.mms.auth.account;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
	
//	@RequestMapping(value = "/api/user/wxInfo", method = RequestMethod.GET)
//	@ResponseBody
//	public WxInfo getCurrentUserWxInfo(Principal principal){
//		return this.accountService.getUserWxInfo(principal);
//	}
	
	@RequestMapping(value = "/api/user/profile", method = RequestMethod.PUT)
	@ResponseBody
	public UserInfo updateUserProfile(Principal principal, @RequestBody Map<String,Object> data){
		return this.accountService.updateUserProfile(principal,data);
	}
}
