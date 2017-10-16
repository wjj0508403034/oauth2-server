package tech.tgls.mms.auth.sms;

/**
 * Created by Liya on 2017/4/21.
 */
public interface SmsService {
	void insert(RegistSmsCode registSmsCode);

	RegistSmsCode findByTelAndCode(String tel, String code);

	RegistSmsCode findByTel(String tel);

	boolean isSmsCodeValid(String tel, String code);
}
