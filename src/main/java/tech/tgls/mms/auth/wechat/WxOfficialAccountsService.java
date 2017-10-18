package tech.tgls.mms.auth.wechat;

import tech.tgls.mms.auth.wechat.domain.WxPublicAccount;

/**
 * Created by Liya on 2017/3/24.
 */
public interface WxOfficialAccountsService {
	WxPublicAccount findByAppId(String appId);

	void update(WxPublicAccount wxPublicAccount);

	WxPublicAccount findById(long id);
}
