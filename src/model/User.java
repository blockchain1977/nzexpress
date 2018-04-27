package model;

import org.apache.log4j.Logger;
import utils.wechat.menu.WeixinUtil;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by yplyf on 2015/5/23.
 * User model
 */
public class User {
    private static Logger logger = Logger.getLogger(User.class);

    public final static String WEICHAT_ACCOUNTS =
            "o6w5vsxddiCCxTmP6OArqJy8JJfQ," +     // lili, production weichat
            "oACuus5z3r_ejZjYJ8aUQ0WLIcWs, " +  // lili, test weichat
            "o6w5vs3gDDtsiY0-z3lDYK4iYwHo, " +  // Kyle, production weichat
            "o6w5vszqpR5Njr3Sgkk9MXpXoRxc, " + // yangguangmingmei
            "oACuus2eZlcF2GyOl05ww_yi766o"; // Kyle, test weichat

    public final static int DEFAULT_DAYSGRANTED = 60; // 60 days
    public final static int MAX_TRACKING_NUMBER = 5;
    public final static int DEFAULT_PRIVILEGE = 12;
    public final static int DEFAULT_TIMEINTERVAL = DEFAULT_PRIVILEGE;

    /*
    In database
     */
    private String useridentity;
    private String active;
    private int daysgranted;
    // max tracking number
    private int maxtrackingnumber;
    // start tracking days
    private Date starttrackingdate;
    // the bigger the privilege, the low of power, the longer the interval
    private int userprivilege;
    /*
    In database end
     */

    // if equal 5, db op and tracking op will be performed once after 5 rounds.
    // Only read from DB, neven write to DB.
    private int timeinterval;
    private boolean dirty;

    private String lastTrackingTime;

    // in memory objects
    List<TrackingStatus> packagesStatus;

    UserSession userSession;

    public int getMaxTrackingNumber() {
        return maxtrackingnumber;
    }

    public void setMaxTrackingNumber(int number) {
        maxtrackingnumber = number;
        dirty = true;
    }

    public boolean isExceedMaxNumberForTrack() {
        return (packagesStatus.size() >= getMaxTrackingNumber());
    }

    public User(String useridentity) {
        this.useridentity = useridentity;
        active = "Y";
        daysgranted = DEFAULT_DAYSGRANTED;
        timeinterval = DEFAULT_TIMEINTERVAL;
        maxtrackingnumber = MAX_TRACKING_NUMBER;
        starttrackingdate = null;
        userprivilege = DEFAULT_PRIVILEGE;


        lastTrackingTime = WeixinUtil.getToday();

        packagesStatus = new Vector<>();
        dirty = true;
        userSession = new UserSession();
    }

    // used by me to reset the info for a user
    public void refreshUser() {
        daysgranted = DEFAULT_DAYSGRANTED;
        timeinterval = DEFAULT_TIMEINTERVAL;
        maxtrackingnumber = MAX_TRACKING_NUMBER;
        userprivilege = DEFAULT_PRIVILEGE;
        starttrackingdate = null;

        dirty = true;
    }

    public void refreshUserFromDB() {
        UserDBHelper.loadUser(useridentity);
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public String getLastTrackingTime() {
        return lastTrackingTime;
    }

    public void setLastTrackingTime(String lastTrackingTime) {
        this.lastTrackingTime = lastTrackingTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "useridentity='" + useridentity +
                ", active='" + active +
                ", starttrackingdate =" + starttrackingdate +
                ", daysgranted=" + daysgranted +
                ", timeinterval=" + timeinterval +
                ", dirty=" + dirty +
                ", userprivilege =" + userprivilege +
                ", lastTrackingTime =" + lastTrackingTime +

                ", packagesStatus=" + packagesStatus +
                "}";
    }

    public String debugOutput() {
        return "User\n" +
                "useridentity=" + useridentity + "\n" +
                "active=" + active + "\n" +
                "daysgranted=" + daysgranted + "\n" +
                "timeinterval=" + timeinterval + "\n" +
                "userprivilege=" + userprivilege + "\n" +
                "dirty=" + dirty + "\n" +
                "lastTrackingTime=" + lastTrackingTime + "\n" +
                "starttrackingdate=" + starttrackingdate + "\n" +
                "\n" +
                "==================";
    }

    public String getUseridentity() {
        return useridentity;
    }

    public String getActive() {
        logger.debug("getActive");
        return active;
    }

    public void setActive(String active) {
        logger.debug("User " + useridentity + " active set as : " + active);
        this.active = active;
        setDirty(true);
    }

    public int getDaysgranted() {
        return daysgranted;
    }

    public void setDaysgranted(int daysgranted) {
        logger.debug("User " + useridentity + " daysgranted set as : " + daysgranted);
        this.daysgranted = daysgranted;
        setDirty(true);
    }

    public int getTimeinterval() {
        return timeinterval;
    }

    public void setTimeinterval(int timeinterval) {
        logger.debug("User " + useridentity + " timeinterval set as : " + timeinterval);
        this.timeinterval = timeinterval;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        logger.debug("User " + useridentity + " dirty set as : " + dirty);
        this.dirty = dirty;
    }

    public List<TrackingStatus> getPackagesStatus() {
        return packagesStatus;
    }

    public void setPackagesStatus(List<TrackingStatus> packagesStatus) {
        logger.debug("User " + useridentity + " packagesStatus set");
        this.packagesStatus = packagesStatus;
    }

    public Date getStarttrackingdate() {
        return starttrackingdate;
    }

    public void setStarttrackingdate(Date starttrackingdate) {
        this.starttrackingdate = starttrackingdate;
    }

    public int getUserprivilege() {
        return userprivilege;
    }

    public void setUserprivilege(int userprivilege) {
        this.userprivilege = userprivilege;
    }

    public boolean isMyself() {
        if (WEICHAT_ACCOUNTS.contains(useridentity)) {
            return true;
        }
        return false;
    }

    public boolean isInApprovedList() {
        return true;
    }

    public boolean isValid4Tracking() {
        if (isMyself()) {
            return true;
        }

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        if (starttrackingdate == null) {
            setStarttrackingdate(now);
            return true;
        }
        cal.setTime(starttrackingdate);
        cal.add(Calendar.DATE, daysgranted);
        if (cal.getTime().after(now)) {
            return true;
        }

        logger.debug("User : " + useridentity + " trial period finished. ");
        // so far, do not enforce the valid yet. So return true always.
        // Will clear the start tracking column and change below return as false later
        return true;
    }

    public void addMessage(String msgType, String content, String eventType, String eventKey) {
//        Message msg = new Message(getID(), msgType, content, eventType, eventKey);
//        msg.insert();
    }

    public TrackingStatus addTrackingPackage(String packageNumber, String description) {
        logger.debug("User " + useridentity + " add tracking package : " + packageNumber);
        for (TrackingStatus ts : getPackagesStatus()) {
            if (ts.getPackage_number().equalsIgnoreCase(packageNumber)) {
                return ts;
            }
        }

        TrackingStatus tsNew = new TrackingStatus(useridentity, packageNumber, description);
        packagesStatus.add(tsNew);
        return tsNew;
    }

    public void removeTrackingPackage(int index) {
        if (index < packagesStatus.size()) {
            logger.debug("User " + useridentity + " remove tracking package : " + packagesStatus.get(index)
            + " index of this package is : " + index);
            packagesStatus.remove(index);
        }
    }

    public void removeTrackingPackage(TrackingStatus ts) {
        logger.debug("User " + useridentity + " remove tracking package : " + ts);
           packagesStatus.remove(ts);
    }

    public boolean inCommandMode() {
        return (!getUserSession().getCurrentCommand().equalsIgnoreCase(UserCommands.COMMANDS_NONE));
    }

}
