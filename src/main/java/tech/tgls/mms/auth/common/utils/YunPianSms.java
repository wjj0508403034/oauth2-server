package tech.tgls.mms.auth.common.utils;

import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;





import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.tgls.mms.auth.api.exception.OperateFailureException;
import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;
import tech.tgls.mms.auth.core.config.YunPianConfig;
import tech.tgls.mms.auth.sms.RegistSmsCode;
import tech.tgls.mms.auth.sms.SmsService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 云片网发送短信接口服务
 *
 * @author Lei Sun <leix.sun@qq.com>
 */
@Component
public class YunPianSms {
    @Autowired
    private YunPianConfig yunPianConfig;

    @Autowired
    private SmsService oauthSmsService;

    /**
     * 向指定的手机号码发送根据指定的模版和模版参数生成的内容。
     *
     * @param smsTemplate    短信模版
     * @param templateParams 短信模版的参数
     * @param mobile         接收短信的手机号码
     */
    public JsonResultBean sendSms(SmsTemplate smsTemplate, Map<String, String> templateParams, String mobile) {
        // TODO: 验证手机号是否合法有效
        // TODO: 优化YunpianClient的初始化和关闭过程

        YunpianClient client = new YunpianClient(yunPianConfig.getSmsApiKey()).init();

        Map<String, String> smsParams = new HashMap<>();
        smsParams.put("text", smsTemplate.render(templateParams));
        smsParams.put("mobile", mobile);
        Result<SmsSingleSend> result = client.sms().single_send(smsParams);
        client.close();
        JsonResultBean json = new JsonResultBean();
        if (!result.isOK() && 0 == result.getCode()) {
            // 记录在数据库中
            RegistSmsCode code = new RegistSmsCode();
            code.setCode(templateParams.get("code"));
            code.setTel(mobile);
            code.setCreated(new Date());
            oauthSmsService.insert(code);
            json.setStat(Constants.RETURN_CODE_SUCCESS);
            json.setData(result.getMsg());
            return json;
        }
        if (Constants.COED_RESEND_IN_30S == result.getCode()) {
            json.setStat(Constants.RETURN_CODE_FAIL);
            json.setData(Constants.TIP_IN_30S);
            return json;
        }
        // FIXME: 原始的异常被丢掉了。需要改造GenericException增加包含Throwable的构造器
        throw new OperateFailureException(result.getMsg());
    }

    public enum SmsTemplate {
        T_VERIFICATION_CODE("【港华燃气】欢迎登录港华燃气。您本次登录验证码为：#code# （10分钟内有效）。如非本人操作，请忽略。");

        private final String template;

        SmsTemplate(String template) {
            this.template = template;
        }

        public String getTemplate() {
            return template;
        }

        public String render(Map<String, String> params) {
            return StrSubstitutor.replace(this.template, params, "#", "#");
        }
    }

    /**
     * 生成5位随机验证码
     *
     * @return 生成的5位随机验证码
     */
    public static String generateVerificationCode() {
        return RandomStringUtils.randomNumeric(5);
    }
}