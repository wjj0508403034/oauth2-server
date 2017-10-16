package tech.tgls.mms.auth.sms.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.tgls.mms.auth.common.BaseDao;
import tech.tgls.mms.auth.common.DateUtil;
import tech.tgls.mms.auth.sms.RegistSmsCode;
import tech.tgls.mms.auth.sms.SmsService;

@Service
public class SmsServiceImpl implements SmsService {

	@Autowired
	BaseDao<RegistSmsCode> oauthSmsCodeBaseDao;
	private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

	@Override
	public void insert(RegistSmsCode oauthSmsCode) {
		oauthSmsCodeBaseDao.insert(oauthSmsCode);
	}

	@Override
	public RegistSmsCode findByTelAndCode(String tel, String code) {
		List<RegistSmsCode> list = oauthSmsCodeBaseDao
				.query("from RegistSmsCode where tel=:tel and code=:code order by id desc").setParameter("tel", tel)
				.setParameter("code", code).getResultList();

		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public RegistSmsCode findByTel(String tel) {
		List<RegistSmsCode> list = oauthSmsCodeBaseDao.query("from RegistSmsCode where tel=:tel  order by created desc")
				.setParameter("tel", tel).getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public boolean isSmsCodeValid(String tel, String code) {
		RegistSmsCode oauthSmsCode = this.findByTel(tel);
		if (oauthSmsCode == null)
			return false;
		if (!oauthSmsCode.getCode().equals(code))
			return false;
		Date nowTime = new Date();
		Date createTime = oauthSmsCode.getCreated();
		if (createTime == null)
			return false;
		int difMin = DateUtil.getDiscrepantMin(createTime, nowTime);
		logger.info("创建时间与现在时间分钟差：" + difMin);
		if (difMin > 10)
			return false;
		return true;
	}
}
