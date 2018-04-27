package main;

import com.google.gson.Gson;
import codeback.WeChatUserInfo;
import org.apache.log4j.Logger;
import utils.HttpUtil;

/**
 * Created by yplyf on 2015/5/6.
 * This is for more powerful weichat platform user
 */
public class ApiManager {
    private static Logger logger;

    static {
        logger = Logger.getLogger(ApiManager.class);
    }

    public static WeChatUserInfo getWeChatUserInfo(String openID) {
        String api = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + AccessManager.getAccessToken() + "&openid=" + openID + "&lang=zh_CN";

        String respJson = HttpUtil.getInstance().issueGetRequest(api, null, null);
        logger.debug("WeChatUserInfo JSON : " + respJson);
        Gson gson = new Gson();
        WeChatUserInfo userInfo = gson.fromJson(respJson, WeChatUserInfo.class);
        logger.debug(userInfo);
        return userInfo;
    }
}
