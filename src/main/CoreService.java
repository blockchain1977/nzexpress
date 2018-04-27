package main;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import utils.wechat.MessageUtil;
import controller.weichatplatform.expressnz.ExpressNZ;


public class CoreService {

    private static Logger logger = Logger.getLogger(CoreService.class);

    /**
     *
     * @param request
     * @return
     */
    public static String processRequest(HttpServletRequest request) {
        String respMessage = null;

        Map<String, String> requestMap = null;
        try {
            requestMap = MessageUtil.parseXml(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug("Request Map detail : " + requestMap);
        respMessage = ExpressNZ.processRequest(requestMap);
        logger.debug("Response Message : " + respMessage);

        return respMessage;
    }
}  