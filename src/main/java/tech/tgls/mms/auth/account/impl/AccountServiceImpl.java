package tech.tgls.mms.auth.account.impl;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.account.AccountService;
import tech.tgls.mms.auth.account.UserAdditionalInfo;
import tech.tgls.mms.auth.account.UserInfo;
import tech.tgls.mms.auth.account.repo.AccountRepo;
import tech.tgls.mms.auth.account.repo.UserAdditionalInfoRepo;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private UserAdditionalInfoRepo userAddtionalInfoRepo;

	@Override
	public Account findByUsername(String username) {
		return this.accountRepo.findByUsername(username);
	}

	@Override
	public UserInfo getUserInfo(Principal principal) {
		UserDetailsImpl user = null;
		if (principal instanceof OAuth2Authentication) {
			user = (UserDetailsImpl) ((OAuth2Authentication) principal)
					.getPrincipal();
		}

		if (user == null) {
			throw new RuntimeException("Current user is null");
		}

		Account account = user.getAccount();
		UserInfo userInfo = new UserInfo();
		userInfo.put("userId", account.getId());
		userInfo.put("username", account.getUsername());

		List<UserAdditionalInfo> additionalInfos = this.userAddtionalInfoRepo
				.findUserInfosByUserId(account.getId());
		if (additionalInfos != null) {
			for (UserAdditionalInfo info : additionalInfos) {
				userInfo.put(info.getName(), info.getValue());
			}
		}
		return userInfo;
	}

}
