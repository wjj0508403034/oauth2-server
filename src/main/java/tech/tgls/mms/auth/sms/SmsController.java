package tech.tgls.mms.auth.sms;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tech.tgls.mms.auth.api.exception.OperateFailureException;
import tech.tgls.mms.auth.common.DateUtil;
import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.common.utils.YunPianSms;

import com.aliyuncs.exceptions.ClientException;

@Controller
public class SmsController {

	private static final Logger logger = LoggerFactory
			.getLogger(SmsController.class);

	@Autowired
	private SmsService oauthSmsService;
	
    @Autowired
    private YunPianSms yunPianSms;

	// 短信发送时间限制（分钟）
	private static final int SMS_SEND_TIME_LIMIT_IN_MIN = 1;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@RequestMapping("/ignore/sms/getVerificationCode/{mobile}/{kaptchaCode}")
	@ResponseBody
	public JsonResultBean getVerificationCode(@PathVariable String mobile,
			@PathVariable String kaptchaCode, HttpServletRequest request,
			HttpServletResponse response) throws ClientException {
		/*
		 * 验证图形验证码是否正确，“undefined”是不需要传图形验证码
		 */
		if (!"undefined".equals(kaptchaCode)) {
			Cookie myCookie[] = request.getCookies();
			for (Cookie cookie : myCookie) {
				if (cookie.getName().equals(Constants.KAPTCHA_CODE)) {
					String kaptchaCodeRight = redisTemplate.opsForValue().get(
							cookie.getValue());
					if (null == kaptchaCodeRight) {
						JsonResultBean result = new JsonResultBean();
						result.setStat(Constants.RETURN_CODE_FAIL);
						result.setData("请输入正确的图形验证码");
						return result;
					}
					if (!kaptchaCodeRight.equals(kaptchaCode)) {
						JsonResultBean result = new JsonResultBean();
						result.setStat(Constants.RETURN_CODE_FAIL);
						result.setData("请输入正确的图形验证码");
						return result;
					}
					redisTemplate.delete(cookie.getValue());
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
					break;
				}
			}
		}
		/*
		 * 在redis中添加openId对应的获取短信验证码次数
		 */
		//long wxInfoId = Auth.getWxUserId(request);
		//wxInfoService.addAccountByRedisWxInfoId(wxInfoId);
		if (StringUtils.isEmpty(mobile)) {
			JsonResultBean result = new JsonResultBean();
			result.setStat(Constants.RETURN_CODE_FAIL);
			result.setData("请输入手机号");
			return result;
		}

		if (hasSentWithinMin(mobile, SMS_SEND_TIME_LIMIT_IN_MIN)) {
			// 如果发现在限制的时间内请求发送短信，直接返回错误提示
			JsonResultBean result = new JsonResultBean();
			result.setStat(Constants.RETURN_CODE_FAIL);
			result.setData(String.format("%s分钟内请勿重复发送短信验证码. ",
					Integer.toString(SMS_SEND_TIME_LIMIT_IN_MIN)));
			return result;
		} else {
			return sendSmsCode(mobile);

		}
	}
	
    @RequestMapping("/ignore/sms/validateVcode")
    @ResponseBody
    public Boolean validateVcode(HttpServletRequest request) {
        String tel = request.getParameter("tel");
        String code = request.getParameter("vcode");
        logger.info("tel：" + tel);
        logger.info("vcode：" + code);
//        RegistSmsCode oauthSmsCode = oauthSmsService.findByTelAndCode(tel, code);
        RegistSmsCode oauthSmsCode = oauthSmsService.findByTel(tel);
        if (oauthSmsCode == null) return false;
        if (!oauthSmsCode.getCode().equals(code)) return false;
        Date nowTime = new Date();
        Date createTime = oauthSmsCode.getCreated();
        if (createTime == null) return false;
        int difMin = DateUtil.getDiscrepantMin(createTime, nowTime);
        logger.info("创建时间与现在时间分钟差：" + difMin);
        if (difMin > 10) return false;
        return true;
    }
	
    /**
     * 生成，发送验证码，并记录在数据库中
     *
     * @param mobile 接收验证码的手机
     */
    private JsonResultBean sendSmsCode(@PathVariable String mobile) {
        JsonResultBean result = new JsonResultBean();
        // 生成验证码
        String verificationCode = YunPianSms.generateVerificationCode();
        try {
            // 发送验证码
            Map<String, String> templateParams = new HashMap<>();
            templateParams.put("code", verificationCode);
            result = yunPianSms.sendSms(YunPianSms.SmsTemplate.T_VERIFICATION_CODE, templateParams, mobile);
            return result;
        } catch (OperateFailureException e) {
            result.setStat(Constants.RETURN_CODE_FAIL);
            result.setData(e.getMessage());
        } catch (Exception e) {
            result.setStat(Constants.RETURN_CODE_FAIL);
            result.setData("发送短信失败，请稍后再试或者联系客服，谢谢");
        }
        return result;
    }

	/**
	 * 指定手机号在指定时间（分钟）内是否已经发送过验证码
	 *
	 * @param mobile
	 *            手机号码
	 * @param min
	 *            时间（分钟）
	 */
	public Boolean hasSentWithinMin(String mobile, int min) {
		RegistSmsCode oauthSmsCode = oauthSmsService.findByTel(mobile);
		if (oauthSmsCode == null || oauthSmsCode.getCreated() == null) {
			return false;
		}

		Date now = new Date();
		Long timeDiffInSeconds = Duration.between(
				oauthSmsCode.getCreated().toInstant(), now.toInstant())
				.getSeconds();
		logger.info("手机号为" + mobile + ",上一次发送验证时间为" + oauthSmsCode.getCreated()
				+ ",当次发送时间" + now + ",两次时间间隔" + timeDiffInSeconds + "秒");
		return timeDiffInSeconds <= min * 60;
	}
}
