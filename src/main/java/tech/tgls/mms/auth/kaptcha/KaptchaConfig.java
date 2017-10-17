package tech.tgls.mms.auth.kaptcha;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * tech.tgls.mms.auth.kaptcha
 *
 * @author: wuzhencheng <zhencheng.wu@pactera.com>
 * @since 2017/5/10 13:57
 */
@Configuration
public class KaptchaConfig {
	@Bean(name = "kaptchaProducer")
	public DefaultKaptcha getKaptchaBean() {
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		Properties properties = new Properties();
		properties.setProperty(Constants.KAPTCHA_BORDER, "no");
		properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_FROM, "white");
		properties.setProperty(Constants.KAPTCHA_BACKGROUND_CLR_TO, "white");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,
				"black");
		properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, "200");
		properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, "60");
		properties.setProperty(Constants.KAPTCHA_SESSION_CONFIG_KEY, "code");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "5");
		properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "40");
		properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL,
				"com.google.code.kaptcha.impl.ShadowGimpy");
		Config config = new Config(properties);
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}