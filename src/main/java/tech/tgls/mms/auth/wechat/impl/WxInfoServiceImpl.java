package tech.tgls.mms.auth.wechat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.tgls.mms.auth.common.BaseDao;
import tech.tgls.mms.auth.wechat.WxInfoService;
import tech.tgls.mms.auth.wechat.domain.WxInfo;

import java.util.List;

/**
 * Created by Liya on 2016/12/8.
 */
@Service
public class WxInfoServiceImpl implements WxInfoService {
	private static final Logger logger = LoggerFactory
			.getLogger(WxInfoService.class);

	@Autowired
	private BaseDao<WxInfo> wxInfoBaseDao;

	@SuppressWarnings("unchecked")
	@Override
	public WxInfo getWxUserIdById(Long id) {
		List<WxInfo> list = wxInfoBaseDao
				.query("from WxInfo where user.id=:id").setParameter("id", id)
				.getResultList();
		if (list.size() == 0)
			return null;

		WxInfo firstWxInfo = list.get(0);
		if (list.size() > 1) {
			// 解决数据库openid数据重复问题
			for (WxInfo w : list) {
				if (w.getUser() != null) {
					firstWxInfo = w;
					break;
				}
			}
		}
		return firstWxInfo;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WxInfo> findByUserId(Long userId) {
		List<WxInfo> list = wxInfoBaseDao
				.query("from WxInfo where user.id=:id")
				.setParameter("id", userId).getResultList();
		if (list.size() == 0)
			return null;
		return list;
	}

	@Override
	public void insert(WxInfo wx) {

		wxInfoBaseDao.insert(wx);
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxInfo existFindByOpenId(String openid) {

		List<WxInfo> list = wxInfoBaseDao
				.query("from WxInfo where wxOpenId=:openId")
				.setParameter("openId", openid).getResultList();
		logger.info("获取到的openid为：" + openid + ",查询到的记录数为：" + list.size());
		if (list.size() == 0)
			return null;

		WxInfo firstWxInfo = list.get(0);
		if (list.size() > 1) {
			// 解决数据库openid数据重复问题
			for (WxInfo w : list) {
				if (w.getUser() != null) {
					firstWxInfo = w;
					break;
				}
			}
		}
		return firstWxInfo;

	}

	@Transactional
	@Override
	public void update(WxInfo wx) {
		wxInfoBaseDao.update(wx);
	}

	@Override
	public WxInfo findById(long id) {
		return wxInfoBaseDao.findById(WxInfo.class, id);
	}
}
