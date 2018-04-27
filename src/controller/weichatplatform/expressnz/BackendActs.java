package controller.weichatplatform.expressnz;

import main.ExpressNZServletContextListener;
import model.TrackingStatus;
import model.TrackingStatusDBHelper;
import model.User;
import model.UserDBHelper;
import org.apache.log4j.Logger;
import utils.MailUtil;
import utils.info.CheckInfo;
import utils.kuaidi.OrderCheck;
import utils.wechat.menu.WeixinUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yplyf on 2015/5/6.
 * Create a separate thread to run some backend actions which will not impact the import daily tasks of query
 */
public class BackendActs extends Thread{
    private static Logger logger = Logger.getLogger(BackendActs.class);

    private static Map<String, User> userMap;

    // 5 mins
    private static long BACKEND_INTERVAL = 300000;

    private static boolean idle;

    private static boolean exit;

    private final static Object threadWaitMutex = new Object();

    private final static Object userMapMutex = new Object();

    private static String info;
    private static String moreInfo;
    private static int period = 12; // every 12 times of BACKEND_INTERVAL, the info will be retrived
    private final static int CHECKINFO_PERIOD = 12;

    static {
        logger.debug("Log user info from DB");

        synchronized (userMapMutex) {
            userMap = UserDBHelper.loadUsers();
        }
        idle = false;
        exit = false;
    }

    public static String getInfo() {
        return info;
    }

    public static String getMoreInfo() {
        return moreInfo;
    }

    public static void setBackendInterval(long backendInterval) {
        BACKEND_INTERVAL = backendInterval;
    }

    public static User getUser(String useridentity) {
        User user = BackendActs.getUserMap().get(useridentity);
        if (user == null) {
            logger.debug("Insert user in getUser method: Existing user, but not in database");
            user = new User(useridentity);
            BackendActs.getUserMap().put(useridentity, user);
        }
        return user;
    }

    public static Collection<User> getUsers() {
        return BackendActs.getUserMap().values();
    }

    public static boolean isIdle() {
        return idle;
    }

    public static void setIdle(boolean idle) {
        BackendActs.idle = idle;
    }

    public static boolean isExit() {
        return exit;
    }

    public static void setExit(boolean exit) {
        BackendActs.exit = exit;
    }

    public static Map<String, User> getUserMap() {
        synchronized (userMapMutex) {
            return userMap;
        }
    }

    public static void retrivePackageStatus(User user) {
        logger.debug("RetrivePackageStatus");

        user.setLastTrackingTime(WeixinUtil.getToday());

        // get package status
        List<TrackingStatus> packagesStatus = user.getPackagesStatus();
        if (packagesStatus != null) {
            for (TrackingStatus ts : packagesStatus) {
                if (ts.getTracking_enabled().equalsIgnoreCase(TrackingStatus.TRACKING_ENABLE_YES)) {
                    ts.setPackage_latest_status(OrderCheck.checkOrderStatus(ts.getPackage_number()));
                }
            }
        }
    }

    public static void flushDB() {
        new Thread() {
            @Override
            public void run() {
                Iterator it = BackendActs.getUserMap().entrySet().iterator();
                while (it.hasNext()) {
                    User user = (User)((Map.Entry)it.next()).getValue();
                    updateDatabase(user);
                }
            }
        }.start();
    }

    public static void updateDatabase(User user) {
        logger.debug("Update Database User Info");

        // update database
        if (user.isDirty()) {
            UserDBHelper.insertUpdateUser(user);
            user.setDirty(false);
        }

        List<TrackingStatus> packagesStatus = user.getPackagesStatus();
        if (packagesStatus != null) {
            Iterator<TrackingStatus> iterator = packagesStatus.iterator();
            while(iterator.hasNext()) {
                TrackingStatus ts = iterator.next();
                if (ts.isDirty()) {
                    TrackingStatusDBHelper.insertUpdateTrackingStatus(ts);
                    ts.setDirty(false);
                    if (ts.getTracking_enabled().equalsIgnoreCase(TrackingStatus.TRACKING_ENABLE_NO)) {
                        logger.debug("Remove the package from tracking: " + ts);
                        iterator.remove();
                    }
                }
            }
        }
    }

    public static String debugOutput() {
        logger.debug("Debug Output");

        String output = "BACKEND_INTERVAL = " + BACKEND_INTERVAL
                + " idle = " + idle
                + " exit = " + exit
                + " backend thread status is " + ExpressNZServletContextListener.backendThreadStatus()
                + "\n\n";

        output = output + "User Info: \n";
        int userCount = 0;
        Iterator it = BackendActs.getUserMap().entrySet().iterator();
        while (it.hasNext()) {
            User user = (User)((Map.Entry)it.next()).getValue();
            output = output + user.debugOutput() + "\n\n";
            userCount++;
        }

        output = "User Count = " + userCount + "\n" + output;
        logger.debug(output);

        return output;
    }

    @Override
    public void run() {
        while(true) {
            try {
                if (isExit()) {
                    logger.debug("Exit the backend thread");
                    break;
                }

                if (isIdle()) {
                    logger.debug("BackendActs idle");
                } else {
                    logger.debug("BackendActs alive");

                    Iterator it = BackendActs.getUserMap().entrySet().iterator();
                    while (it.hasNext()) {
                        User user = (User) ((Map.Entry) it.next()).getValue();

                        if ((user.getTimeinterval()) > 0) {
                            user.setTimeinterval(user.getTimeinterval() - 1);
                        } else {
                            user.setTimeinterval(user.getUserprivilege());

                            retrivePackageStatus(user);
                            updateDatabase(user);

                            if (user.getActive().equalsIgnoreCase("N")) {
                                it.remove();
                            }
                        }
                    }

                    try {
                        MailUtil.sendMails();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                debugOutput();

                if (period == CHECKINFO_PERIOD) {
                    info = CheckInfo.checkInfo();
                    moreInfo = CheckInfo.checkMoreInfo();
                    period = 0;
                } else {
                    period++;
                }

                waitForSomeTime(BACKEND_INTERVAL);
            } catch (Exception e) {
                logger.debug("!!!!!!!!!!!!!!!!!!!something happened!!!!!!!!!!!!!!!!!!!!!!!");
                e.printStackTrace();
            }
        }

    }

    public static void waitForSomeTime(long time) {
        logger.debug("Wait For Some Time: " + time);
        try {
            synchronized(threadWaitMutex) {
                threadWaitMutex.wait(time);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void wakeBackendThreadUp() {
        logger.debug("Wakt Up the Back End Thread.");
        synchronized(threadWaitMutex) {
            threadWaitMutex.notify();
        }
    }

    public BackendActs() {

    }

    public static void sendMail(final String subject, final String content) {
        MailUtil.addMailToQueue(subject, content, ExpressNZConfHelper.recipients);
    }

    public static void sendMail(final String subject, final String content, final String recipients) {
        MailUtil.addMailToQueue(subject, content, recipients);
    }

    public static void addUser(final Map<String, String> requestMap) {
        String fromUserName = requestMap.get("FromUserName");

        User user = getUserMap().get(fromUserName);
        if (user == null) {
            logger.debug("Add new user");
            user = new User(fromUserName);
            getUserMap().put(fromUserName, user);
        } else {
            logger.debug("Make existing user as active");
            user.setActive("Y");
        }

        // send mail
        MailUtil.addMailToQueue(fromUserName + " 关注了express_nz <EOM>", "Empty", ExpressNZConfHelper.recipients);
    }

    public static void getSuggestion(final Map<String, String> requestMap) {
        logger.debug("Get Suggestion.");
        insertMessage(requestMap);
        // send mail
        String fromUserName = requestMap.get("FromUserName");
        String content = WeixinUtil.getUserInput(requestMap).trim();
        MailUtil.addMailToQueue(fromUserName + " has suggestion: ", content, ExpressNZConfHelper.recipients);
    }

    public static void removeUser(final Map<String, String> requestMap) {
        logger.debug("Remove User.");

        String fromUserName = requestMap.get("FromUserName");

        User user = getUserMap().get(fromUserName);
        if (user != null) {
            user.setActive("N");
        }

        // send mail
        MailUtil.addMailToQueue(fromUserName + " 取消了关注express_nz <EOM>", "", ExpressNZConfHelper.recipients);
    }



    public static void insertMessage(final Map<String, String> requestMap) {/*
        new Thread() {
            @Override
            public void run() {
                super.run();
                String fromUserName = requestMap.get("FromUserName");
                String content = "";
                String eventType = "";
                String eventKey = "";

                String msgType = requestMap.get("MsgType");

                if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                    content = WeixinUtil.getUserInput(requestMap).trim();
                } else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                    // 事件类型
                    eventType = requestMap.get("Event");

                    if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE) || eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                        // Do nothing
                    }
                    // 自定义菜单点击事件
                    else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK) || eventType.equals(MessageUtil.EVENT_TYPE_VIEW)) {
                        eventKey = requestMap.get("EventKey");
                    }
                } else {
                    // Do nothing
                }
                // write db

                User user = new User(fromUserName);
                user.addMessage(msgType, content, eventType, eventKey);
            }
        }.start();*/
    }

    public static TrackingStatus trackingPakcage(final User user, final String packageNumber, final String description) {
        return user.addTrackingPackage(packageNumber, description);
    }

}
