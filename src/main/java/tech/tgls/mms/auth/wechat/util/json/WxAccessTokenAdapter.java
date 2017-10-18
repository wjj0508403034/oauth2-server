package tech.tgls.mms.auth.wechat.util.json;

import com.google.gson.*;

import java.lang.reflect.Type;

import tech.tgls.mms.auth.wechat.domain.WxAccessToken;

/**
 * @author Daniel Qian
 */
public class WxAccessTokenAdapter implements JsonDeserializer<WxAccessToken> {

  @Override
  public WxAccessToken deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    WxAccessToken accessToken = new WxAccessToken();
    JsonObject accessTokenJsonObject = json.getAsJsonObject();

    if (accessTokenJsonObject.get("access_token") != null && !accessTokenJsonObject.get("access_token").isJsonNull()) {
      accessToken.setAccess_token(GsonHelper.getAsString(accessTokenJsonObject.get("access_token")));
    }
    if (accessTokenJsonObject.get("expires_in") != null && !accessTokenJsonObject.get("expires_in").isJsonNull()) {
      accessToken.setExpires_in(GsonHelper.getAsPrimitiveInt(accessTokenJsonObject.get("expires_in")));
    }
    return accessToken;
  }

}
