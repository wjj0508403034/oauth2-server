package tech.tgls.mms.auth.account;

import java.security.Principal;

public interface AccountService {

	Account findByUsername(String username);

	UserInfo getUserInfo(Principal principal);

}
