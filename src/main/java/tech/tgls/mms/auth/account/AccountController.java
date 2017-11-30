package tech.tgls.mms.auth.account;

import java.security.Principal;
import java.util.List;
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
		
	@RequestMapping(value = "/api/user/profile", method = RequestMethod.PUT)
	@ResponseBody
	public UserInfo updateUserProfile(Principal principal, @RequestBody Map<String,Object> data){
		return this.accountService.updateUserProfile(principal,data);
	}
	
	@RequestMapping(value = "/api/getUserInfosByIds", method = RequestMethod.POST)
	@ResponseBody
	public List<UserInfo> getUserInfosByIds(@RequestBody List<Long> userIds){
		return this.accountService.getUserInfosByIds(userIds);
	}
}
