package tech.tgls.mms.auth.wechat;

import java.util.List;

import tech.tgls.mms.auth.wechat.domain.WxInfo;

/**
 * Created by Liya on 2016/12/8.
 */
public interface WxInfoService {

	// 根据用户登录的id获取用户微信信息
	WxInfo getWxUserIdById(Long id);

	List<WxInfo> findByUserId(Long userId);

	void insert(WxInfo wx);

	WxInfo existFindByOpenId(String openid);

	void update(WxInfo wx);

	WxInfo findById(long id);

}
