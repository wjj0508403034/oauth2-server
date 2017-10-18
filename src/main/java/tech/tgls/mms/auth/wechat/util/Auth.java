package tech.tgls.mms.auth.wechat.util;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import tech.tgls.mms.auth.common.consts.Constants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenyuyang
 */
public class Auth {

    private static final Logger logger = LoggerFactory.getLogger(Auth.class);

    public static Cookie getCookieByName(HttpServletRequest request, String cookieName) {
        Cookie[] arrCookie = request.getCookies();
        if (arrCookie != null) {
            for (Cookie cookie : arrCookie) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }


    //----------------------------------------------------------------------------

    public static long getUserId(HttpServletRequest request)
            throws SignatureException, NoSuchAlgorithmException, JWTVerifyException, InvalidKeyException, IOException {

        String cookieName = SiteConfig.authCookieName;
        String tokenKey = SiteConfig.authCookieTokenKey;

        return getUserId(request, cookieName, tokenKey);
    }


    public static void addAuthCookie(HttpServletResponse response, long userId, int rememberDays) {

        Cookie cookie = Auth.makeAuthCookie(userId, rememberDays); // 记住30天
        response.addCookie(cookie);
    }

    private static Cookie makeAuthCookie(long userId, int rememberDays) {

        String cookieName = SiteConfig.authCookieName;

        String tokenKey = SiteConfig.authCookieTokenKey;

        return makeAuthCookie(userId, rememberDays, cookieName, tokenKey);
    }

    private static Cookie makeAuthCookie(long userId, int rememberDays, String cookieName, String tokenKey) {
        String token = makeAuthToken(userId, tokenKey);

        int maxAge = 60 * 60 * 24 * rememberDays; // 天
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setPath("/");
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }

        return cookie;
    }


    private static String makeAuthToken(long userId, String tokenKey) {
        JWTSigner signer = new JWTSigner(tokenKey);

        HashMap<String, Object> claims = new HashMap<String, Object>();

        AuthUser user = new AuthUser(userId);
        claims.put("user", user);
        return signer.sign(claims);
    }

    //----------------------------------------------------------------------------


    public static void clearAuthCookie(HttpServletResponse response) {

        String cookieName = SiteConfig.authCookieName;

        clearAuthCookie(response, cookieName);

    }


    //----------------------------------------------------------------------------
    /**
     * mobile端cookie添加用户userId
     *
     * @param request
     * @param response
     * @param userId
     */
    public static void addMobileUserAuthCookie(HttpServletRequest request, HttpServletResponse response, long userId) {
        String appId = (String) request.getSession().getAttribute(Constants.sessionAppId);
        logger.info("addMobileUserAuthCookie-sessionAppId:" + appId);
        addAuthCookie(response, userId, Constants.mobileTokenKey, Constants.mobileCookieName + "-" + appId, 100);
    }

    /**
     * mobile端cookie添加用户wxInfoId
     *
     * @param request
     * @param response
     * @param wxInfoId
     */
    public static void addWxInfoIdAuthCookie(HttpServletRequest request, HttpServletResponse response, long wxInfoId) {
        String appId = (String) request.getSession().getAttribute(Constants.sessionAppId);
        logger.info("addWxUserIdAuthCookie-sessionAppId:" + appId);
        Auth.addAuthCookie(response, wxInfoId, Constants.wxTokenKey, Constants.wxCookieName + "-" + appId, 100);
    }


    public static void addAuthCookie(HttpServletResponse response, long userId, String tokenKey, String cookieName, int rememberDays) {

        Cookie cookie = Auth.makeAuthCookie(userId, rememberDays, cookieName, tokenKey); // 记住30天
        response.addCookie(cookie);

    }

    /**
     * 清除userId缓存
     * @param request
     * @param response
     */
    public static void clearMobileUserAuthCookie(HttpServletRequest request,HttpServletResponse response) {
        String appId = (String) request.getSession().getAttribute(Constants.sessionAppId);
        logger.info("clearMobileUserAuthCookie-sessionAppId:" + appId);
        Cookie cookie = new Cookie(Constants.mobileCookieName + "-" + appId, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    /**
     * 清除wxInfo 缓存
     * @param request
     * @param response
     */
    public static void clearWxInFoIdAuthCookie(HttpServletRequest request,HttpServletResponse response) {
        String appId = (String) request.getSession().getAttribute(Constants.sessionAppId);
        logger.info("clearWxInFoIdAuthCookie-sessionAppId:" + appId);
        Cookie cookie = new Cookie(Constants.wxCookieName + "-" + appId, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
    public static void clearAuthCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    @SuppressWarnings("unchecked")
	public static long getUserId(HttpServletRequest request, String cookieName, String tokenKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, JWTVerifyException {
        Cookie cookie = getCookieByName(request, cookieName);
        if (cookie == null) return 0;

        JWTVerifier verifier = new JWTVerifier(tokenKey);
        String token = cookie.getValue();
        if (!StringUtils.hasText(token)) return 0;

        Map<String, Object> decoded = verifier.verify(token);

        HashMap<String, Object> deUser = (HashMap<String, Object>) decoded.get("user");

        Object strUserId = deUser.get("id");
        if (strUserId == null) return 0;

        return Long.parseLong(strUserId.toString());
    }

    public static long getMobileUserId(HttpServletRequest request) {

        try {
            String appId= (String)request.getSession().getAttribute(Constants.sessionAppId);
            logger.info("getMobileUserId-sessionAppId:" + appId);
            return getUserId(request, Constants.mobileCookieName  + "-" + appId, Constants.mobileTokenKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (JWTVerifyException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getWxUserId(HttpServletRequest request) {

        try {
            String appId= (String)request.getSession().getAttribute(Constants.sessionAppId);
            logger.info("sessionAppId:" + appId);
            return getUserId(request, Constants.wxCookieName + "-" + appId, Constants.wxTokenKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (JWTVerifyException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static long getUserByOpenId(HttpServletRequest request) {

        try {
            return getUserId(request, Constants.userByOpenIdCookieName, Constants.userByOpenIdTokenKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (JWTVerifyException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //----------------------------------------------------------------------------

    public static String hashPwd(String inputPwd) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(inputPwd);
    }


    public static Boolean isPwdCorrect(String inputPwd, String hashedPwd) {

        if (hashedPwd == null) return false;
        if (hashedPwd.length() != 60) return false;

        try {
            return BCrypt.checkpw(inputPwd, hashedPwd);
        } catch (Exception ex) {
            logger.info("isPwdCorrect", ex);
            return false;
        }
    }

}