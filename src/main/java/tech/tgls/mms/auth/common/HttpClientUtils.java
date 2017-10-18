package tech.tgls.mms.auth.common;



import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.tgls.mms.auth.common.consts.Constants;
import tech.tgls.mms.auth.common.jsonbean.JsonResultBean;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Liya on 2016/12/21.
 */
public class HttpClientUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);


    public  static JsonResultBean httpRequest(String requestUrl, String requestMethod, String outputStr) {
        JsonResultBean resultBean=new JsonResultBean();
        try {
            URL url = new URL(requestUrl);
//           HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();//https 协议
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http 协议

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);

            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            resultBean.setStat(Constants.RETURN_CODE_SUCCESS);
            resultBean.setData(buffer);
        } catch (ConnectException ce) {
            resultBean.setStat(Constants.RETURN_CODE_FAIL);
            log.error("连接超时：{}", ce);
        } catch (Exception e) {
            resultBean.setStat(Constants.RETURN_CODE_FAIL);
            log.error("https请求异常：{}", e);
        }
        return resultBean;
    }

    public static JsonResultBean get(String path)
    {
        JsonResultBean resultBean = new JsonResultBean();
        String result = "";
        try
        {
            result = Request.Get(path)
                    .connectTimeout(3000)
                    .socketTimeout(3000)
                    .execute().returnContent().asString();
            resultBean.setData(result);
        }
        catch (IOException e)
        {
            resultBean.setStat(Constants.RETURN_CODE_FAIL);
            log.error("https请求异常：{}", e);
        }
        return resultBean;
    }
}
