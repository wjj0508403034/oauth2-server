package tech.tgls.mms.auth.wechat.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.tgls.mms.auth.common.BaseDao;
import tech.tgls.mms.auth.wechat.WxOfficialAccountsService;
import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;

/**
 * Created by Liya on 2017/3/24.
 */
@Service
public class WxOfficialAccountsServiceImpl implements WxOfficialAccountsService {
	@Autowired
	private BaseDao<WxPublicAccount> baseDao;

	@SuppressWarnings("unchecked")
	@Override
	public WxPublicAccount findByAppId(String appId) {
		List<WxPublicAccount> accountList = baseDao
				.query("from tech.tgls.mms.wx.domain.WxPublicAccount where app_id=:appId")
				.setParameter("appId", appId).getResultList();
		if (accountList.size() == 0)
			return null;
		return accountList.get(0);
	}

	@Override
	public void update(WxPublicAccount wxPublicAccount) {
		baseDao.update(wxPublicAccount);
	}

	@Override
	public WxPublicAccount findById(long id) {
		WxPublicAccount wxPublicAccount = baseDao.findById(
				WxPublicAccount.class, id);
		if (wxPublicAccount == null)
			return null;
		return wxPublicAccount;
	}
}
