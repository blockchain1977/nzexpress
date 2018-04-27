package model;

import controller.weichatplatform.expressnz.BackendActs;
import controller.weichatplatform.expressnz.ExpressNZ;
import controller.weichatplatform.expressnz.ExpressNZConfHelper;
import org.apache.log4j.Logger;
import utils.kuaidi.OrderCheck;
import utils.wechat.MessageUtil;
import utils.wechat.menu.WeixinUtil;
import utils.wechat.message.resp.Article;
import utils.wechat.message.resp.BaseMessage;
import utils.wechat.message.resp.NewsMessage;
import utils.wechat.message.resp.TextMessage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by yplyf on 2015/9/21.
 * Processing User Commands
 */
public class UserCommands {
    private static Logger logger = Logger.getLogger(UserCommands.class);

    public final static String COMMANDS_NONE = "";
    // every commands' initial status. all other status should start from 1
    public final static int COMMANDS_INITIALSTATUS = 0;

    public final static String COMMANDS_MINE_SUMMARY = "summary";
    public final static String COMMANDS_MINE_REFRESHTRACKING = "refreshtracking";
    public final static String COMMANDS_MINE_SETINTERVAL = "set";
    public final static String COMMANDS_MINE_DEBUG = "debug";
    public final static String COMMANDS_MINE_WAKEUPBACKEND = "wake";
    public final static String COMMANDS_MINE_USER = "user";
    public final static String COMMANDS_MINE_FLUSHDB = "flushdb";

    public final static String COMMANDS_MINE_ALL = COMMANDS_MINE_SUMMARY
            + "," + COMMANDS_MINE_REFRESHTRACKING
            + "," + COMMANDS_MINE_SETINTERVAL
            + "," + COMMANDS_MINE_DEBUG
            + "," + COMMANDS_MINE_WAKEUPBACKEND
            + "," + COMMANDS_MINE_USER
            + "," + COMMANDS_MINE_FLUSHDB;

    public final static String COMMANDS_APPROVED_TRACK = "track";
    public final static String COMMANDS_APPROVED_UNTRACK = "untrack";
    public final static String COMMANDS_APPROVED_GET = "get";
    public final static String COMMANDS_APPROVED_TRACKDETAIL = "detail";
//    public final static String COMMANDS_APPROVED_RERUN = "r";

    public final static String COMMANDS_APPROVED_ALL =
            COMMANDS_APPROVED_TRACK
                    + "," + COMMANDS_APPROVED_UNTRACK
                    + "," + COMMANDS_APPROVED_GET
                    + "," + COMMANDS_APPROVED_TRACKDETAIL;
//                    + "," + COMMANDS_APPROVED_RERUN;

    public final static String COMMANDS_ALL = COMMANDS_MINE_ALL
                    + "," + COMMANDS_APPROVED_ALL;

    private static Map<String, String> commandUserMapping = new HashMap<>();

    /*
       start of processCommands. will be moved to menu action later
    */
    public static BaseMessage  processCommand_Rerun(Map<String, String> requestMap) {
        logger.debug("Rerun previous Command");

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");
        User user = BackendActs.getUser(fromUserName);

        BaseMessage baseMessage = null;

        String preCmd = user.getUserSession().getPreCommand();
        switch (preCmd) {
            case COMMANDS_APPROVED_GET:
                baseMessage = processCommand_Get(requestMap);
                break;
            case COMMANDS_APPROVED_TRACK:
                baseMessage = processCommand_Track(requestMap);
                break;
            case COMMANDS_APPROVED_TRACKDETAIL:
                baseMessage = processCommand_TrackDetail(requestMap);
                break;
            case COMMANDS_APPROVED_UNTRACK:
                baseMessage = processCommand_UnTrack(requestMap);
                break;
            default :
                TextMessage txtMessage = new TextMessage();
                txtMessage.setToUserName(fromUserName);
                txtMessage.setFromUserName(toUserName);
                txtMessage.setCreateTime(new Date().getTime());
                txtMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                txtMessage.setFuncFlag(0);
                txtMessage.setContent(gotoNoneCmdStatus(user));

                baseMessage = txtMessage;
                logger.debug("Unknown previous command: " + preCmd);
        }
        return baseMessage;
    }

    public static BaseMessage processCommand_Track(Map<String, String> requestMap) {
        logger.debug("Process Track Command");
        String respContent;

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        User user = BackendActs.getUser(fromUserName);

        if (user.isValid4Tracking()) {
            if (user.isExceedMaxNumberForTrack() && (! user.isMyself())) {
                respContent = "您已添加跟踪了" + user.getMaxTrackingNumber() + "个快递单，达到了跟踪数限制，无法添加新的快递单。\n";
                respContent = respContent + gotoNoneCmdStatus(user);
            } else {
                user.getUserSession().setCurrentCommand(COMMANDS_APPROVED_TRACK);
                respContent = "[当前模式]：\n" +
                        "添加跟踪快递单。\n" +
                        "您可以给快递单加一个标签。这个标签是为了方便记忆对快递单的简单描述，会出现在之后的跟踪信息中。\n" +
                        "[格式]：\n<标签><空格><快递单号>\n" +
                        "[举例]：\n给小明的奶粉 NZE12345\n\n";
                respContent = respContent + gotoFolloupCmdStatus();
            }
        } else {
            respContent = gotoInvalidTracking(user);
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

    private static String getTrackingPackageList(User user) {
        String respContent = "";
        List<TrackingStatus> tsList = user.getPackagesStatus();
        for (int tsIndex = 0; tsIndex < tsList.size(); tsIndex++) {
            TrackingStatus ts = tsList.get(tsIndex);
            if (ts.getTracking_enabled().equalsIgnoreCase(TrackingStatus.TRACKING_ENABLE_NO)) {
                continue;
            }
            String tsLine = (tsIndex+1) + " : " + ts.getDescription() + " : " + ts.getPackage_number();
            respContent = respContent + tsLine + "\n";
        }
        logger.debug("Tracking Package List: \n" + respContent);

        return respContent;
    }

    public static BaseMessage processCommand_UnTrack(Map<String, String> requestMap) {
        logger.debug("Process Untrack Command");
        String respContent;

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        User user = BackendActs.getUser(fromUserName);

        if (user.isValid4Tracking()) {
            user.getUserSession().setCurrentCommand(COMMANDS_APPROVED_UNTRACK);
            respContent = "[当前模式]：\n" +
                    "删除跟踪快递单。\n" +
                    "请输入您要取消跟踪的快递单序号。\n";
            respContent = respContent + getTrackingPackageList(user) + "\n";
            respContent = respContent + gotoFolloupCmdStatus();
        } else {
            respContent = gotoInvalidTracking(user);
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

    public static BaseMessage processCommand_TrackDetail(Map<String, String> requestMap) {
        logger.debug("Process Track Detail Command");
        String respContent;

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        User user = BackendActs.getUser(fromUserName);

        if (user.isValid4Tracking()) {
            user.getUserSession().setCurrentCommand(COMMANDS_APPROVED_TRACKDETAIL);
            respContent = "[当前模式]：\n" +
                    "查询快递单详情。\n" +
                    "请输入您要查看详情的快递单序号。\n";
            respContent = respContent + getTrackingPackageList(user) + "\n";
            respContent = respContent + gotoFolloupCmdStatus();
        } else {
            respContent = gotoInvalidTracking(user);
        }

        respContent = respContent + ExpressNZ.getInfoMessage() ;

        TextMessage txtMessage = new TextMessage();
        txtMessage.setToUserName(fromUserName);
        txtMessage.setFromUserName(toUserName);
        txtMessage.setCreateTime(new Date().getTime());
        txtMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
        txtMessage.setFuncFlag(0);
        txtMessage.setContent(respContent);

        return txtMessage;
    }

    public static BaseMessage processCommand_Get(Map<String, String> requestMap) {
        logger.debug("Process Get Command");
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        User user = BackendActs.getUser(fromUserName);

        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(fromUserName);
        newsMessage.setFromUserName(toUserName);
        newsMessage.setCreateTime(new Date().getTime());
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);

        List<Article> articleList = new ArrayList<>();
        int articleCount = 0;

        Article article1 = new Article();
        article1.setTitle("以下物流信息的查询时间为北京时间：" + user.getLastTrackingTime() + "。\n");
        article1.setDescription("已签收的快递单将自动从跟踪列表中删除。");
        article1.setPicUrl(""); // 二维码地址
        articleList.add(article1);
        articleCount++;

        String respContent = "";

        if (user != null) {
            List<TrackingStatus> tsList = user.getPackagesStatus();
            for (TrackingStatus ts : tsList) {

                if (ts.getTracking_enabled().equalsIgnoreCase(TrackingStatus.TRACKING_ENABLE_NO)) {
                    continue;
                }

                String statusLine = ts.getPackage_latest_status();
                if (statusLine != null) {
                    String temp = statusLine.replace("未妥投", "").replace("未签收", "");

                    if (temp.contains("签收") || temp.contains("妥投")){
                        respContent = respContent + "标签：" + ts.getDescription()
                                + "\n快递单号： " + ts.getPackage_number()
                                + "\n最新状态： \n" + ts.getPackage_latest_status()
                                + "\n该快递单已签收，将从跟踪列表中删除。";
                        ts.setTracking_enabled(TrackingStatus.TRACKING_ENABLE_NO);
                    } else{
                        respContent = respContent + "标签：" + ts.getDescription()
                                + "\n快递单号： " + ts.getPackage_number()
                                + "\n最新状态： \n" + ts.getPackage_latest_status();
                    }
                } else {
                    respContent = respContent + "标签：" + ts.getDescription()
                            + "\n快递单号： " + ts.getPackage_number()
                            + "\n最新状态： 无状态信息。\n";
                }

                if (articleCount < NewsMessage.MAX_ARTICLE_COUNT) {
                    Article article = new Article();
                    article.setTitle(respContent);
                    article.setDescription("");
                    article.setPicUrl(""); // 二维码地址
                    articleList.add(article);
                    articleCount++;
                    respContent = "";
                } else {
                    respContent = respContent + "\n ============= \n";
                }
            }
        }

        // NewsMessage: 最多8个主题
        if (articleCount >= NewsMessage.MAX_ARTICLE_COUNT) {
            Article article = new Article();
            article.setTitle(respContent);
            article.setDescription("");
            article.setPicUrl(""); // 二维码地址
            articleList.add(article);
            articleCount++;
        }

        newsMessage.setArticleCount(articleCount);
        newsMessage.setArticles(articleList);

        gotoNoneCmdStatus(user);

        return newsMessage;
    }


    private static String processCommand_WakeupBackend() {
        String respContent;
        BackendActs.wakeBackendThreadUp();
        respContent = "Wake up the backend thread";
        return respContent;
    }

    private static String processCommand_UserControl(Map<String, String> requestMap) {
        logger.debug("Process User Control Command");
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        User me = BackendActs.getUser(fromUserName);
        me.getUserSession().setCurrentCommand(COMMANDS_MINE_USER);

        String content = requestMap.get("Content");

        String respContent = "";

        StringTokenizer st = new StringTokenizer(content, " ");
        String command = st.nextToken();

        Collection<User> users = BackendActs.getUsers();
        int userIndex = 1;
        for (User user : users) {
            respContent = respContent + userIndex + " : " + user.getUseridentity() + "\n";
            commandUserMapping.put(String.valueOf(userIndex), user.getUseridentity());
            userIndex++;
        }
        respContent = respContent + gotoFolloupCmdStatus();

        return respContent;
    }

    public static BaseMessage processCommand_Debug(Map<String, String> requestMap) {
        logger.debug("Process Debug Command");
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        String respContent;
        respContent = "Debug output : " + BackendActs.debugOutput();

        // write file
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/var/lib/tomcat7/webapps/ROOT/debug/userinfo");

            out.write(respContent.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // send feedback
        List<Article> articleList = new ArrayList<>();
        int articleCount = 0;

        Article article1 = new Article();
        article1.setTitle("所有在使用的用户信息");
//        article1.setDescription(respContent);
        article1.setDescription("点击链接看详情");
        article1.setUrl("http://52.69.106.22/debug/userinfo"); // 二维码地址
        articleList.add(article1);
        articleCount++;

        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(fromUserName);
        newsMessage.setFromUserName(toUserName);
        newsMessage.setCreateTime(new Date().getTime());
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
        newsMessage.setArticleCount(articleCount);
        newsMessage.setArticles(articleList);

        return newsMessage;
    }

    private static String processCommand_SetInterval(String command) {
        String respContent;
        int index = command.indexOf(" ");
        //logger.debug("index = " + index);
        String intervalSubString = command.substring(index + 1);
        //logger.debug("subString = " + intervalSubString);
        int interval = Integer.parseInt(intervalSubString) * 1000;
        //logger.debug("interval = " + interval);
        BackendActs.setBackendInterval(interval);
        respContent = "Set backend thread interval as : " + interval;
        return respContent;
    }

    private static String processCommand_RefreshTracking() {
        String respContent;//TODO: This command used by me to start tracking thread/stop tracking thread/update tracking period etc.
        // so far, just change the idle status
        BackendActs.setIdle(!BackendActs.isIdle());
        respContent = "Set backend thread as Idle";

        return respContent;
    }

    private static void processCommand_Summary() {
        // user command : get all user list
        // prepare sql clause
        // TODO: should not touch any db op here. Use other wrapped class to get the users
//            String sql = "select count(distinct weichat) as usernumbers from kuaidi.order";
//            PreparedStatement preparedInsStmt = DBUtil.getConnection(dbName, dbUser, dbPass).prepareStatement(sql);
//            ResultSet rs = preparedInsStmt.executeQuery();
//            while (rs.next()) {
//                respContent = "user number is : " + rs.getInt("usernumbers");
//                break;
//            }
        // TODO: should not touch any db op here. Use other wrapped class to get all the orders
        // orders command : get sum of ordernumber for each company
//            String sql = "select ordernumber from kuaidi.order";
//            PreparedStatement preparedInsStmt = DBUtil.getConnection(dbName, dbUser, dbPass).prepareStatement(sql);
//            ResultSet rs = preparedInsStmt.executeQuery();
//            int ftdOrderNumber = 0;
//            int ydtOrderNumber = 0;
//            int cgOrderNumber = 0;
//            while (rs.next()) {
//                String ftdpattern = ExpressNZConfHelper.ftdpattern.toUpperCase();
//                String ydtpattern = ExpressNZConfHelper.ydtpattern.toUpperCase();
//                String cgpattern = ExpressNZConfHelper.cgpattern.toUpperCase();
//
//                String orderNumber = rs.getString("ordernumber").toUpperCase().trim();
//                if (orderNumber.startsWith(ftdpattern)) {
//                    ftdOrderNumber++;
//                } else if (orderNumber.startsWith(cgpattern)) {
//                    cgOrderNumber++;
//                } else if (orderNumber.endsWith(ydtpattern)) {
//                    ydtOrderNumber++;
//                }
//            }
//
//            respContent = "FTD order number : " + ftdOrderNumber + " YDT order number : " + ydtOrderNumber + " CG order number : " + cgOrderNumber;
    }

    private static String processCommand_flushDB() {
        String respContent;

        BackendActs.flushDB();
        respContent = "Database flushed";

        return respContent;
    }

    /**********************************************************************************************************************
        end of processCommands
     */

    private static String processFollowupCommand_Track(User user, String command) {
        logger.debug("Process Track Followup Commands");
        String respContent;

        StringTokenizer st = new StringTokenizer(command);
        String description = null;
        String packageNumber = null;
        // st.nextToken();
        if (st.hasMoreTokens()) {
            description = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            packageNumber = st.nextToken();
        }

        if (user.isExceedMaxNumberForTrack() && (! user.isMyself())) {
            respContent = "您已添加跟踪了" + user.getMaxTrackingNumber() + "个快递单，达到了跟踪数限制，无法添加新的快递单。\n";
            respContent = respContent + gotoNoneCmdStatus(user);
        } else {
            if (((description == null) || (description.length() > 50))
                    || ((packageNumber == null) || (packageNumber.length() > 50 || packageNumber.length() < 4))) {
                // incorrect number
                respContent = "请使用正确的命令格式：\n" +
                        "<快递单简单标签><空格><快递单号>\n\n";
                respContent = respContent + gotoFolloupCmdStatus();
            } else {
                TrackingStatus status = BackendActs.trackingPakcage(user, packageNumber, description);
                respContent = description + " " + packageNumber + " 已加入跟踪列表。\n" +
                        "最新状态为：";
                String latestStatus = OrderCheck.checkOrderStatus(packageNumber);
                status.setPackage_latest_status(latestStatus);
                respContent = respContent + latestStatus + "\n\n";
                respContent = respContent + gotoFolloupCmdStatus();
            }
        }
        return respContent;
    }

    private static String processFollowupCommand_UnTrack(User user, String number) {
        logger.debug("Process Untrack Followup Commands");
        String respContent;

        if ((number == null) || (Integer.parseInt(number) > user.getPackagesStatus().size()) || (Integer.parseInt(number) < 1)) {
            // incorrect number
            respContent = "输入错误。请输入要删除跟踪的快递单序号。\n\n";
            respContent = respContent + gotoFolloupCmdStatus();
        } else {
            if (user.isValid4Tracking()) {
                int index = Integer.parseInt(number);
                index--;
                user.removeTrackingPackage(index);

                respContent = "取消成功！\n\n";
                respContent = respContent + getTrackingPackageList(user) + "\n";
                respContent = respContent + gotoFolloupCmdStatus();
            } else {
                respContent = gotoInvalidTracking(user);
            }
        }

        return respContent;
    }

    private static String processFollowupCommand_TrackDetail(User user, String number) {
        logger.debug("Process Track Detail Followup Commands");
        String respContent;

        if ((number == null) || (Integer.parseInt(number) > user.getPackagesStatus().size()) || (Integer.parseInt(number) < 1)) {
            // incorrect number
            respContent = "输入错误。请输入要查看详情的快递单序号。\n\n";
            respContent = respContent + gotoFolloupCmdStatus();
        } else {
            if (user.isValid4Tracking()) {
                int index = Integer.parseInt(number);
                index--;
                String package_number = user.getPackagesStatus().get(index).getPackage_number();
                respContent = OrderCheck.checkOrderStatus(package_number);
                respContent = "单号 " + package_number + " 的查询结果：" + "\n==========\n" + respContent + "\n\n";
                respContent = respContent + getTrackingPackageList(user) + "\n";
                respContent = respContent + gotoFolloupCmdStatus();
            } else {
                respContent = gotoInvalidTracking(user);
            }
        }

        return respContent;
    }

    private static String processFollowupCommand_UserControl(String content, User user) {
        logger.debug("Process Followup User Control Command");

        String respContent = "";

        StringTokenizer st = new StringTokenizer(content, " ");
        String userIndex = st.nextToken();
        if ((userIndex == null)) {
            respContent = "用户序号输入错误。\n";
            respContent = respContent + gotoFolloupCmdStatus();
        } else if (userIndex.equalsIgnoreCase("all")) {
            for (String userIdentity : commandUserMapping.values()) {
                if (userIdentity != null) {
                    User tempuser = BackendActs.getUser(userIdentity);
                    tempuser.refreshUserFromDB();
                }
            }

            respContent = "从数据库刷新所有用户数据。 \n";
            respContent = respContent + gotoNoneCmdStatus(user);
        } else {
            if (st.hasMoreTokens()) {
                if (st.nextToken().equalsIgnoreCase("db")) {
                    // load usre from db
                    String userIdentity = commandUserMapping.get(userIndex);
                    if (userIdentity == null) {
                        respContent = respContent + gotoFolloupCmdStatus();
                    } else {
                        User tempuser = BackendActs.getUser(userIdentity);
                        tempuser.refreshUserFromDB();
                        respContent = "从数据库刷新用户数据： " + userIdentity + "\n";
                        respContent = respContent + gotoNoneCmdStatus(user);

                    }
                }
            } else {
                String userIdentity = commandUserMapping.get(userIndex);
                if (userIdentity == null) {
                    respContent = "用户序号错误。\n";
                    respContent = respContent + gotoFolloupCmdStatus();
                } else {
                    User tempuser = BackendActs.getUser(userIdentity);
                    tempuser.refreshUser();
                    respContent = "刷新用户数据为默认值： " + userIdentity + "\n";
                    respContent = respContent + gotoNoneCmdStatus(user);
                }
            }
        }

        return respContent;
    }

    private static String gotoFolloupCmdStatus() {
        return "( 输入\"x\"或点击菜单[单个查询]，回到查询单个快递单模式。)";
    }

    private static String gotoInvalidTracking(User user) {
        String respContent;
        respContent = "对不起，您的使用时间已过期。如需延长，请联系\"" + ExpressNZConfHelper.recipients + "\"。\n";
        respContent = respContent + gotoNoneCmdStatus(user);
        return respContent;
    }

    /******************************************************************************************************
        public methods below
     */

    public static String gotoNoneCmdStatus(User user) {
        user.getUserSession().setCurrentCommand(COMMANDS_NONE);
        return "[当前模式]：\n" +
                "单个快递查询模式。\n" +
                "直接输入快递单号可查询单个快递详情。\n" +
                "[格式]：直接输入<快递单号>\n" +
                "[举例]：NZE12345\n\n" +
                "要批量跟踪快递，请先点菜单【批量跟踪】->【添加跟踪快递】。之后可用菜单【批量跟踪】->【查询跟踪快递】来查询";
    }

    public static BaseMessage processFollowupCommands(Map<String, String> requestMap) {
        logger.debug("Process Followup Commands");
        String respContent = "";

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");
        String content = WeixinUtil.getUserInput(requestMap).trim();

        User user = BackendActs.getUser(fromUserName);
        String command = user.getUserSession().getCurrentCommand();

        if (content.equalsIgnoreCase("x") || content.equalsIgnoreCase("返回")) {
            respContent = respContent + gotoNoneCmdStatus(user);
        } else {
            try {
                if (command.equalsIgnoreCase(COMMANDS_APPROVED_TRACK)) {
                    respContent = processFollowupCommand_Track(user, content);
                } else if (command.equalsIgnoreCase(COMMANDS_APPROVED_UNTRACK)) {
                    respContent = processFollowupCommand_UnTrack(user, content);
                } else if (command.equalsIgnoreCase(COMMANDS_APPROVED_TRACKDETAIL)) {
                    respContent = processFollowupCommand_TrackDetail(user, content);
                } else if (command.equalsIgnoreCase(COMMANDS_MINE_USER)) {
                    respContent = processFollowupCommand_UserControl(content, user);
                }
            } catch (Exception e) {
                respContent = respContent + "输入错误。请重新输入：\n\n";
                respContent = respContent + gotoFolloupCmdStatus();
            }
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

    public static BaseMessage processMyCommands(Map<String, String> requestMap) throws SQLException {
        logger.debug("Process My Commands");
        BaseMessage respMessage = null;
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        String command = WeixinUtil.getUserInput(requestMap).trim();
        command = command.toLowerCase();

        logger.debug("Command is : " + command);
        String respContent = "";

        // My commands:
        if (command.startsWith(COMMANDS_MINE_SUMMARY)) {
            processCommand_Summary();

        } else if (command.startsWith(COMMANDS_MINE_REFRESHTRACKING)) {
            respContent = processCommand_RefreshTracking();

        } else if (command.startsWith(COMMANDS_MINE_SETINTERVAL)) {
            respContent = processCommand_SetInterval(command);
        } else if (command.startsWith(COMMANDS_MINE_DEBUG)) {
            respMessage = processCommand_Debug(requestMap);
        } else if (command.startsWith(COMMANDS_MINE_WAKEUPBACKEND)) {
            respContent = processCommand_WakeupBackend();
        } else if (command.startsWith(COMMANDS_MINE_USER)) {
            respContent = processCommand_UserControl(requestMap);
        } else if (command.equalsIgnoreCase(COMMANDS_MINE_FLUSHDB)) {
            respContent = processCommand_flushDB();
        }
        // approved user's and my commands
        else {
            respMessage = processApprovedCommands(requestMap);
        }

        if (respMessage == null) {
            respContent = respContent + ExpressNZ.getInfoMessage();

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

    public static BaseMessage processApprovedCommands(Map<String, String> requestMap) throws SQLException {
        logger.debug("Process Approved Commands");
        BaseMessage baseMessage = null;

        String command = WeixinUtil.getUserInput(requestMap).trim();
        command = command.toLowerCase().trim();

        if (command.startsWith(COMMANDS_APPROVED_TRACK)) {
            baseMessage = processCommand_Track(requestMap);
        } else if (command.startsWith(COMMANDS_APPROVED_GET)) {
            baseMessage = processCommand_Get(requestMap);
        } else if (command.startsWith(COMMANDS_APPROVED_UNTRACK)) {
            baseMessage = processCommand_UnTrack(requestMap);
        } else if (command.startsWith(COMMANDS_APPROVED_TRACKDETAIL)){
            baseMessage = processCommand_TrackDetail(requestMap);
        }
//        else if (command.equalsIgnoreCase(COMMANDS_APPROVED_RERUN)) {
//            baseMessage = processCommand_Rerun(requestMap);
//        }

        return baseMessage;
    }

    public static boolean isCommands(String msg) {
        msg = msg.toLowerCase();
        logger.debug("Is Commands? : " + msg);
        StringTokenizer st = new StringTokenizer(UserCommands.COMMANDS_ALL, ",");
        while (st.hasMoreTokens()) {
            if (msg.startsWith(st.nextToken())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isInApprovedCommands(String msg) {
        msg = msg.toLowerCase();
        logger.debug("Is Approved Commands? : " + msg);
        StringTokenizer st = new StringTokenizer(UserCommands.COMMANDS_APPROVED_ALL, ",");
        while (st.hasMoreTokens()) {
            if (msg.startsWith(st.nextToken())) {
                return true;
            }
        }

        return false;
    }
}
