package main;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import utils.HttpUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by yplyf on 2015/4/11.
 */
public class AccessManager {
    private static Logger logger = Logger.getLogger(AccessManager.class);

    final static private String TOCKEN="a1b2cdefg";

    private static  long timeLastGetToken = 0;
    private static String accessToken = "";
    private static int expireTime = 0;

    final static private String APPID = "wx2bad8702c81fd750";
    final static private String APPSECRET = "a87dd87195c67d9b29db21136de73c95";
    final static private String accessTokenAPI = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET;

    public static String getAccessToken() {

        long now = Calendar.getInstance().getTimeInMillis();
        if ((now - timeLastGetToken) > expireTime) {
            String respJson = HttpUtil.getInstance().issueGetRequest(accessTokenAPI, null, null);

            JSONObject respJsonObj = new JSONObject(respJson);
            accessToken = respJsonObj.getString("access_token");
            expireTime = respJsonObj.getInt("expires_in");
            logger.debug("Access Token = " + accessToken + " Expire Time = " + expireTime);
        }

        return accessToken;
    }

    public static boolean checkSignature(javax.servlet.http.HttpServletRequest request)
    {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        if ((signature == null) || (timestamp == null) || (nonce == null)) {
            return false;
        }
        logger.debug("signature=" + signature);
        logger.debug("timestamp=" + timestamp);
        logger.debug("nonce=" + nonce);

        String tocken = TOCKEN;
        String[] strArray = {tocken, timestamp, nonce};
        Arrays.sort(strArray);

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < strArray.length; i++) {
            content.append(strArray[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    private static String byteToHexStr(byte b) {
        char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArray = new char[2];
        tempArray[0] = hex[(b >>> 4) & 0X0F];
        tempArray[1] = hex[b & 0X0F];

        String str = new String(tempArray);
        return str;
    }
}
