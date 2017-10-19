package tech.tgls.mms.auth.account.impl;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

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
	public Account findAdminByUsername(String username) {
		return this.accountRepo.findAdminByUsername(username);
	}

	@Override
	public UserInfo getUserInfo(Principal principal) {
		Account account = this.getAccount(principal);
		UserInfo userInfo = new UserInfo();
		userInfo.put("userId", account.getId());
		userInfo.put("username", account.getUsername());

		List<UserAdditionalInfo> additionalInfos = this.userAddtionalInfoRepo.findUserInfosByUserId(account.getId());
		if (additionalInfos != null) {
			for (UserAdditionalInfo info : additionalInfos) {
				userInfo.put(info.getName(), info.getValue());
			}
		}
		return userInfo;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public UserInfo updateUserProfile(Principal principal, Map<String, Object> data) {
		Account account = this.getAccount(principal);
		this.userAddtionalInfoRepo.deleteUserInfosByUserId(account.getId());

		for (Entry<String, Object> entry : data.entrySet()) {
			if (!this.ignoreKeys(entry.getKey())) {
				UserAdditionalInfo userInfo = new UserAdditionalInfo(account.getId(), entry.getKey(),
						entry.getValue().toString());
				this.userAddtionalInfoRepo.save(userInfo);
			}
		}

		return this.getUserInfo(principal);
	}

	@Override
	public Account createWechatAccountIfNotExists(String openId) {
		Account account = this.findByUsername(openId);
		if (account == null) {
			account = new Account();
			account.setUsername(openId);
			account = this.accountRepo.save(account);
		}

		return account;
	}

	private boolean ignoreKeys(String key) {
		if (StringUtils.equalsIgnoreCase("userId", key)) {
			return true;
		}

		if (StringUtils.equalsIgnoreCase("username", key)) {
			return true;
		}

		return false;
	}

	private Account getAccount(Principal principal) {
		if (principal instanceof Authentication) {
			UserDetailsImpl user = (UserDetailsImpl) ((Authentication) principal).getPrincipal();
			return user.getAccount();
		}

		throw new RuntimeException("Current user is null");
	}


}
