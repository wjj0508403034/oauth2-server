package tech.tgls.mms.auth.account.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import tech.tgls.mms.auth.account.Account;
import tech.tgls.mms.auth.account.AccountService;
import tech.tgls.mms.auth.account.Role;
import tech.tgls.mms.auth.account.UserAdditionalInfo;
import tech.tgls.mms.auth.account.UserInfo;
import tech.tgls.mms.auth.account.repo.AccountRepo;
import tech.tgls.mms.auth.account.repo.UserAdditionalInfoRepo;
import tech.tgls.mms.auth.account.security.PhoneAuthenticationToken;
import tech.tgls.mms.auth.wechat.WxInfoService;
import tech.tgls.mms.auth.wechat.domain.WxInfo;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private UserAdditionalInfoRepo userAddtionalInfoRepo;

	@Autowired
	private WxInfoService wxInfoService;

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
		return this.getUserInfoByAccount(account);
	}
	
	@Override
	public List<UserInfo> getUserInfosByIds(List<Long> userIds) {
		List<UserInfo> result = new ArrayList<>();
		Iterable<Account> accounts = this.accountRepo.findAll(userIds);
		for(Account account : accounts){
			if(account != null){
				result.add(this.getUserInfoByAccount(account));
			}
		}
		return result;
	}

	@Override
	public WxInfo getUserWxInfo(Principal principal) {
		Account account = this.getAccount(principal);
		return this.wxInfoService.getWxUserIdById(account.getId());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public UserInfo updateUserProfile(Principal principal, Map<String, Object> data) {
		Account account = this.getAccount(principal);
		Map<String, UserAdditionalInfo> userInfoMap = this.getUserInfoMap(account);

		for (Entry<String, Object> entry : data.entrySet()) {
			if (!this.ignoreKeys(entry.getKey())) {
				if (!userInfoMap.containsKey(entry.getKey())) {
					this.addUserAdditionalInfo(account, entry);
					continue;
				}

				UserAdditionalInfo info = userInfoMap.get(entry.getKey());
				Object value = entry.getValue();
				if (value == null) {
					this.deleteUserAdditionInfo(info);
					continue;
				}

				this.updateUserAdditionInfo(info, value);
			}
		}

		return this.getUserInfo(principal);
	}

	private void addUserAdditionalInfo(Account account, Entry<String, Object> entry) {
		UserAdditionalInfo userInfo = new UserAdditionalInfo(account.getId(), entry.getKey(),
				entry.getValue().toString());
		this.userAddtionalInfoRepo.save(userInfo);
	}

	private void deleteUserAdditionInfo(UserAdditionalInfo info) {
		this.userAddtionalInfoRepo.delete(info);
	}

	private void updateUserAdditionInfo(UserAdditionalInfo info, Object value) {
		info.setValue(value.toString());
		this.userAddtionalInfoRepo.save(info);
	}

	private Map<String, UserAdditionalInfo> getUserInfoMap(Account account) {
		List<UserAdditionalInfo> infos = this.userAddtionalInfoRepo.findUserInfosByUserId(account.getId());
		Map<String, UserAdditionalInfo> userInfoMap = new HashMap<>();
		for (UserAdditionalInfo info : infos) {
			userInfoMap.put(info.getName(), info);
		}

		return userInfoMap;
	}

	@Override
	public Account createWechatAccountIfNotExists(String openId) {
		Account account = this.findByUsername(openId);
		if (account == null) {
			account = new Account();
			account.setUsername(openId);
			account.setAccountType("weixin");
			account.setRole(Role.USER);
			account = this.accountRepo.save(account);
		}

		return account;
	}

	@Override
	public Account createPhoneAccount(String phone) {
		Account account = new Account();
		account.setUsername(phone);
		account.setAccountType("phone");
		account.setRole(Role.USER);
		return this.accountRepo.save(account);
	}

	@Override
	public void autoLogin(Account account) {
		PhoneAuthenticationToken authenticationToken = new PhoneAuthenticationToken(null, null);
		UserDetailsImpl userDetails = new UserDetailsImpl(account);
		authenticationToken.setPrincipal(userDetails);
		authenticationToken.setDetails(userDetails);
		authenticationToken.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	}

	@Override
	public boolean isLogin() {
		return SecurityContextHolder.getContext().getAuthentication() != null;
	}

	@Override
	public Account getCurrentAccount() {
		if (this.isLogin()) {
			Account account = this.getAccount(SecurityContextHolder.getContext().getAuthentication());
			if (account != null) {
				return this.accountRepo.findOne(account.getId());
			}
		}

		return null;
	}

	private boolean ignoreKeys(String key) {
		if (StringUtils.equalsIgnoreCase("userId", key)) {
			return true;
		}

		if (StringUtils.equalsIgnoreCase("username", key)) {
			return true;
		}

		if (StringUtils.equalsIgnoreCase("wenxin", key)) {
			return true;
		}

		return false;
	}

	private Account getAccount(Principal principal) {
		if (principal instanceof Authentication) {
			Object user = ((Authentication) principal).getPrincipal();
			if (user instanceof UserDetailsImpl) {
				return ((UserDetailsImpl) user).getAccount();
			}
		}

		throw new RuntimeException("Current user is null");
	}
	
	private UserInfo getUserInfoByAccount(Account account){
		UserInfo userInfo = new UserInfo();
		userInfo.put("userId", account.getId());
		userInfo.put("username", account.getUsername());

		List<UserAdditionalInfo> additionalInfos = this.userAddtionalInfoRepo.findUserInfosByUserId(account.getId());
		if (additionalInfos != null) {
			for (UserAdditionalInfo info : additionalInfos) {
				userInfo.put(info.getName(), info.getValue());
			}
		}

		List<WxInfo> wxInfo = this.wxInfoService.findByUserId(account.getId());
		userInfo.put("wenxin", wxInfo);
		return userInfo;
	}



}
