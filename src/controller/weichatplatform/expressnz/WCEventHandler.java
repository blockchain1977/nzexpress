package controller.weichatplatform.expressnz;

import model.User;
import model.UserCommands;
import org.apache.log4j.Logger;
import utils.info.CheckInfo;
import utils.wechat.MessageUtil;
import utils.wechat.message.resp.Article;
import utils.wechat.message.resp.BaseMessage;
import utils.wechat.message.resp.NewsMessage;
import utils.wechat.message.resp.TextMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kyle.yu on 7/09/2015.
 */
public class WCEventHandler {
    private static Logger logger = Logger.getLogger(WCEventHandler.class);

    public static BaseMessage processEvent(Map<String, String> requestMap) {
        BaseMessage respMessage = null;

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        String respContent = "";
        // 事件类型
        String eventType = requestMap.get("Event");

        // 订阅
        switch (eventType) {
            case MessageUtil.EVENT_TYPE_SUBSCRIBE:
                String description = "谢谢您的关注！直接输入快递单号查询。目前支持快递公司：";

                if (ExpressNZConfHelper.cgenable.equalsIgnoreCase("true")) {
                    description = description + " " + "程光";
                }
                if (ExpressNZConfHelper.ftdenable.equalsIgnoreCase("true")) {
                    description = description + " " + "富腾达";
                }
                if (ExpressNZConfHelper.ydtenable.equalsIgnoreCase("true")) {
                    description = description + " " + "易达通";
                }
                description = description + "\n";

                List<Article> articleList = new ArrayList<>();
                int articleCount = 0;

                Article article1 = new Article();
                article1.setTitle("点击查看使用说明");
                article1.setDescription(description);
                article1.setPicUrl("https://mmbiz.qlogo.cn/mmbiz/qunBNyV82tYHt2bMkSGcCtQBN7pMaExarE95v5DNWHkTBm0v0Bk3eIibts1sBsKNwDNdburOjIJu09b4USYTUMA/0?wx_fmt=jpeg"); // 二维码地址
                article1.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMDM5OTk3OQ==&mid=211984643&idx=1&sn=00abc4f2938ca6adeaad7254dbccf623#rd");
                articleList.add(article1);
                articleCount++;

                logger.debug("User subscribed : " + fromUserName);
                BackendActs.addUser(requestMap);

                NewsMessage newsMessage = new NewsMessage();
                newsMessage.setToUserName(fromUserName);
                newsMessage.setFromUserName(toUserName);
                newsMessage.setCreateTime(new Date().getTime());
                newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);

                newsMessage.setArticleCount(articleCount);
                newsMessage.setArticles(articleList);
                return newsMessage;
            // 取消订阅
            case MessageUtil.EVENT_TYPE_UNSUBSCRIBE:
                // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                logger.debug("User unsubscribed : " + fromUserName);
                BackendActs.removeUser(requestMap);
                break;
            // 自定义菜单点击事件
            case MessageUtil.EVENT_TYPE_CLICK:
                String eventKey = requestMap.get("EventKey");
                logger.debug("User  : " + fromUserName + " clicked the menu, EventKey : " + eventKey);

                if (eventKey.equals("11")) {
                    //respContent = "使用说明";
                    respContent = "【单个查询】：\n" +
                            "直接输入快递单号，可以查询单个快递的详情。\n\n" +

                            "【批量跟踪】 -> 【添加跟踪快递】：\n" +
                            "点击菜单后，输入快递单信息，将快递单加入跟踪列表。\n" +
                            "如：给小明的奶粉 996688\n" +
                            "目前最多可跟踪5个快递单\n\n" +

                            "【批量跟踪】 -> 【查询跟踪快递】：\n" +
                            "点击菜单后，可一次性查询多个快递单的最新状态。查询列表需要使用【添加跟踪快递】来添加\n" +
                            "返回信息如下：" +
                            "以下物流信息的查询时间为北京时间：2015-10-02 10:00:00\n" +
                            "标签：给小明的奶粉\n" +
                            "快递单号：996688\n" +
                            "最新状态：\n" +
                            "2015-10-01 10:33 包裹进入清关流程\n\n" +

                            "【批量跟踪】 -> 【查询跟踪详情】：\n" +
                            "点击菜单后，系统返回一个当前正在跟踪的快递单的列表。在每个快递单的开头有一个序号，只需输入序号，即可查询该快递单的详情。\n" +
                            "如：" +
                            "1  ： 给小明的奶粉 ： 996688\n" +
                            "2  ： 给张三的鱼油 ： nz6688\n" +
                            "输入：1\n" +
                            "系统返回\"给小明的奶粉\"的详细信息\n\n" +

                            "【批量跟踪】 -> 【删除跟踪快递】：\n" +
                            "点击菜单后，系统返回一个当前正在跟踪的快递单的列表。在每个快递单的开头有一个序号，只需输入序号，即可将该快递单从跟踪列表中删除。\n\n" +

                            "如果误点击【批量跟踪】中的子菜单，可以输入\"x\"或点击菜单【单个查询】，返回到查询单个快递单的状态。\n";
                } else if (eventKey.equals("12")) {
                    if (BackendActs.getUser(fromUserName).isMyself()) {
                        respContent = BackendActs.getMoreInfo();
                    } else {
                        respContent = BackendActs.getInfo();
                    }
                } else if (eventKey.equals("13")) {
                    //respContent = "联系我们";
                    if (BackendActs.getUser(fromUserName).isMyself()) {
                        respMessage = UserCommands.processCommand_Debug(requestMap);
                    } else {
                        respContent = "我们的邮箱是：" + ExpressNZConfHelper.recipients;
                    }
                } else if (eventKey.equals("21")) {
                    //respContent = "快递单号查询";
                    User user = BackendActs.getUser(fromUserName);
                    respContent = respContent + UserCommands.gotoNoneCmdStatus(user);
                } else if (eventKey.equals("31")) {
                    //respContent = "添加跟踪快递单";
                    return UserCommands.processCommand_Track(requestMap);
                } else if (eventKey.equals("32")) {
                    //respContent = "删除跟踪快递单";
                    return UserCommands.processCommand_UnTrack(requestMap);
                } else if (eventKey.equals("33")) {
                    //respContent = "查询跟踪快递单";
                    return UserCommands.processCommand_Get(requestMap);
                } else if (eventKey.equals("34")) {
                    //respContent = "查询快递单详情";
                    return UserCommands.processCommand_TrackDetail(requestMap);
                }
                break;
        }

        //respContent = respContent + " 功能开发中";
        //BackendActs.getUser(fromUserName).getUserSession().setCurrentCommand(UserCommands.COMMANDS_NONE);


        respContent = respContent + ExpressNZ.getInfoMessage();

        if (respMessage == null) {
            TextMessage txtMessage = new TextMessage();
            txtMessage.setToUserName(fromUserName);
            txtMessage.setFromUserName(toUserName);
            txtMessage.setCreateTime(new Date().getTime());
            txtMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            txtMessage.setFuncFlag(0);
            txtMessage.setContent(respContent);

            respMessage = txtMessage;
        }

        return respMessage;
    }

}
