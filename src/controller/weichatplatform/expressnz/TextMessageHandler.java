package controller.weichatplatform.expressnz;

import model.User;
import model.UserCommands;
import org.apache.log4j.Logger;
import utils.kuaidi.OrderCheck;
import utils.wechat.MessageUtil;
import utils.wechat.menu.WeixinUtil;
import utils.wechat.message.resp.BaseMessage;
import utils.wechat.message.resp.TextMessage;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by kyle.yu on 7/09/2015.
 * Handle the Text Message
 */
public class TextMessageHandler {
    private static Logger logger = Logger.getLogger(TextMessageHandler.class);

    public static BaseMessage processTextMessage(Map<String, String> requestMap) {
        logger.debug("Process Text Message");
        BaseMessage respMessage;
        String fromUserName = requestMap.get("FromUserName");

        // myself
        User user = BackendActs.getUser(fromUserName);
        if (user.inCommandMode()) {
            respMessage = UserCommands.processFollowupCommands(requestMap);
            return respMessage;
        } else {
            if (user.isMyself()) {
                respMessage = processRequestFromMe(requestMap);
            } else if (user.isInApprovedList()) {
                respMessage = processRequestFromApprovedUser(requestMap);
            } else {
                respMessage = processRequestFromUser(requestMap);
            }
            return respMessage;
        }
    }

    /*
     I can make suggestion, check individual package and issue commands
     */
    private static BaseMessage processRequestFromMe(Map<String, String> requestMap) {
        logger.debug("Process Request From Me");
        BaseMessage respMessage = null;
        String msg = WeixinUtil.getUserInput(requestMap).trim();

        if ((!msg.isEmpty()) && UserCommands.isCommands(msg)) {
            try {
                respMessage = UserCommands.processMyCommands(requestMap);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            respMessage = processRequestFromUser(requestMap);
        }

        return respMessage;
    }

    /*
     Approved user can make suggestion, check individual package and issue approved commands
     */
    private static BaseMessage processRequestFromApprovedUser(Map<String, String> requestMap) {
        logger.debug("Process Request from Approved User");
        BaseMessage respMessage = null;
        String msg = WeixinUtil.getUserInput(requestMap).trim();

        if ((!msg.isEmpty()) && UserCommands.isInApprovedCommands(msg)) {
            try {
                respMessage = UserCommands.processApprovedCommands(requestMap);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            respMessage = processRequestFromUser(requestMap);
        }

        return respMessage;
    }

    /*
      user can only make suggestion, check individual package
     */
    private static BaseMessage processRequestFromUser(Map<String, String> requestMap) {
        logger.debug("Process Request from user");
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        String respContent = "";

        String orderNumbers = WeixinUtil.getUserInput(requestMap).trim();

        StringTokenizer st = new StringTokenizer(orderNumbers, ",");

        // 目前只支持查询一个快递单号。微信需要5秒钟之内返回 so break at the end
        while (st.hasMoreTokens()) {
            String orderNumber = st.nextToken();

            if (orderNumber.startsWith("建议")) {
                respContent = "感谢您的建议。我们会尽快和您联系\n";
                BackendActs.getSuggestion(requestMap);

            } else {
                String orderStatus = OrderCheck.checkOrderStatus(orderNumber);

                if (orderStatus == null || orderStatus.isEmpty()) {
                    orderStatus = "无法查询该快递单\n";
                }

                if ( ! orderStatus.contains("无效的快递单号")) {
                    // 如果是有效快递单号，将信息写入数据库
                    BackendActs.insertMessage(requestMap);
                }

                respContent = respContent + "单号 " + orderNumbers + " 的查询结果：" + "\n==========\n" + orderStatus + "\n\n";
                respContent = respContent + UserCommands.gotoNoneCmdStatus(BackendActs.getUser(fromUserName));
            }
            // 目前只支持查询一个快递单号。微信需要5秒钟之内返回
            break;
        }

        respContent = respContent + ExpressNZ.getInfoMessage();

        TextMessage txtMessage = new TextMessage();
        txtMessage.setToUserName(fromUserName);
        txtMessage.setFromUserName(toUserName);
        txtMessage.setCreateTime(new Date().getTime());
        txtMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
        txtMessage.setFuncFlag(0);
        txtMessage.setContent(respContent);

        return txtMessage;
    }
}
