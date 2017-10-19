package tech.tgls.mms.auth.account;

import java.security.Principal;
import java.util.Map;

public interface AccountService {

	Account findByUsername(String username);
	
	Account findAdminByUsername(String username);

	UserInfo getUserInfo(Principal principal);

	UserInfo updateUserProfile(Principal principal, Map<String, Object> data);

	Account createWechatAccountIfNotExists(String openId);

}
