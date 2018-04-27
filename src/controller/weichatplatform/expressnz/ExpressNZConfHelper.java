package controller.weichatplatform.expressnz;

import utils.ConfigUtil;

/**
 * Created by yplyf on 2015/5/17.
 * Deal with config parameters for ExpressNZ
 */
public class ExpressNZConfHelper {
    public static String dbName = ConfigUtil.getConfig().getProperty("expressnz.database");
    public static String dbUser = ConfigUtil.getConfig().getProperty("expressnz.dbuser");
    public static String dbPass = ConfigUtil.getConfig().getProperty("expressnz.dbpass");
    public static String recipients = ConfigUtil.getConfig().getProperty("expressnz.recipients");

    public static String ftdenable = ConfigUtil.getConfig().getProperty("expressnz.ftdenable");
    public static String ydtenable = ConfigUtil.getConfig().getProperty("expressnz.ydtenable");
    public static String cgenable = ConfigUtil.getConfig().getProperty("expressnz.cgenable");
    public static String ftdpattern = ConfigUtil.getConfig().getProperty("expressnz.ftdpattern");
    public static String ydtpattern = ConfigUtil.getConfig().getProperty("expressnz.ydtpattern");
    public static String cgpattern = ConfigUtil.getConfig().getProperty("expressnz.cgpattern");

}
