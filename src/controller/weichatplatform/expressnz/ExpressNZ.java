package controller.weichatplatform.expressnz;

import org.apache.log4j.Logger;
import utils.wechat.MessageUtil;
import utils.wechat.message.resp.BaseMessage;
import controller.weichatplatform.PlatformBase;

import java.util.*;

/**
 * Created by kyle.yu on 15/05/2015.
 * Main entry for ExpressNZ
 */
public class ExpressNZ extends PlatformBase {
    private static Logger logger = Logger.getLogger(ExpressNZ.class);

    public static String processRequest(Map<String, String> requestMap) {
        String msgType = requestMap.get("MsgType");

        BaseMessage respMessage = null;

        if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
            respMessage = TextMessageHandler.processTextMessage(requestMap);
        } else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)){
            respMessage = WCEventHandler.processEvent(requestMap);
        }

        return MessageUtil.messageToXml(respMessage);
    }

    public static String getInfoMessage() {
        return "";
        //return "\n**********\n有任何建议，请联系" + ExpressNZConfHelper.recipients + "或输入：建议+您的建议";
    }

}
