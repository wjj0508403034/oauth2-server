package tech.tgls.mms.auth.kaptcha;

import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.kaptcha.impl.DefaultKaptcha;

import tech.tgls.mms.auth.common.consts.Constants;

@Controller
public class KaptchaController {

	@Autowired
	@Qualifier("kaptchaProducer")
	private DefaultKaptcha kaptchaProducer;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@RequestMapping(value = "/kaptcha-image", method = RequestMethod.GET)
	public ModelAndView getKaptchaImage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");

		Cookie myCookie[] = request.getCookies();
		if (myCookie == null) {
			return null;
		}

		for (Cookie cookie : myCookie) {
			if (cookie.getName().equals(Constants.KAPTCHA_CODE)) {
				redisTemplate.delete(cookie.getValue());
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
				break;
			}
		}

		String capText = kaptchaProducer.createText();
		System.out.println("capText: " + capText);

		try {
			String uuid = UUID.randomUUID().toString();
			redisTemplate.opsForValue().set(uuid, capText, 60 * 5,
					TimeUnit.SECONDS);
			Cookie cookie = new Cookie(Constants.KAPTCHA_CODE, uuid);
			cookie.setMaxAge(60 * 5);
			cookie.setPath("/");
			response.addCookie(cookie);
		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedImage bi = kaptchaProducer.createImage(capText);
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
		return null;
	}
}
