package model;

import controller.weichatplatform.expressnz.ExpressNZConfHelper;
import utils.DBUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by kyle.yu on 14/09/2015.
 */
public class UserDBHelper {
    public final static String TABLE_NAME = "users";

    public static User loadUser(String useridentity) {
        User user = new User(useridentity);

        String sql = "select active, daysgranted, userprivilege, maxtrackingnumber, starttrackingdate, userprivilege from " +
                ExpressNZConfHelper.dbName + "." + TABLE_NAME + " where useridentity='" + useridentity + "'";
        try {
            ResultSet rs = DBUtil.executeQuery(sql);

            if (rs.next()) {
                // find the user from database
                user.setActive(rs.getString("active"));
                user.setDaysgranted(rs.getInt("daysgranted"));
                user.setUserprivilege(rs.getInt("userprivilege"));
                int number = rs.getInt("maxtrackingnumber");
                if (number > 0) {
                    user.setMaxTrackingNumber(number);
                } else {
                    user.setMaxTrackingNumber(User.MAX_TRACKING_NUMBER);
                }
                Date date = rs.getDate("starttrackingdate");
                if (date != null) {
                    user.setStarttrackingdate(date);
                }

            } else {
                // do nothing. use default value
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        user.setTimeinterval(user.getUserprivilege());

        user.setPackagesStatus(loadUserAllTrackingStatus(useridentity));

        return user;
    }

    public static Map<String, User> loadUsers() {

        // did not revoke loadUser function, because want to execute one sql command to get all user info
        Map<String, User> users = new HashMap<>();

        String sql = "select useridentity, active, daysgranted, maxtrackingnumber, starttrackingdate, userprivilege from " +
                ExpressNZConfHelper.dbName + "." + TABLE_NAME + " where active <> 'N'";
        try {
            ResultSet rs = DBUtil.executeQuery(sql);

            while (rs.next()) {
                String useridentity = rs.getString("useridentity");

                User user = new User(useridentity);
                // find the user from database
                user.setActive(rs.getString("active"));
                user.setDaysgranted(rs.getInt("daysgranted"));
                user.setUserprivilege(rs.getInt("userprivilege"));
                int number = rs.getInt("maxtrackingnumber");
                if (number > 0) {
                    user.setMaxTrackingNumber(number);
                } else {
                    user.setMaxTrackingNumber(User.MAX_TRACKING_NUMBER);
                }
                Date date = rs.getDate("starttrackingdate");
                if (date != null) {
                    user.setStarttrackingdate(date);
                }

                user.setTimeinterval(user.getUserprivilege());

                user.setPackagesStatus(loadUserAllTrackingStatus(useridentity));

                users.put(useridentity, user);
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    private static Vector<TrackingStatus> loadUserAllTrackingStatus(String useridentity) {
        Vector<TrackingStatus> packages_status = new Vector<>();

        String sql = "select description, package_number, tracking_enabled, comments, package_latest_status from "
                + ExpressNZConfHelper.dbName + "." + TrackingStatusDBHelper.TABLE_NAME + " where useridentity='" + useridentity + "'" +
                " and tracking_enabled='Y'";
        ResultSet rs = DBUtil.executeQuery(sql);

        try {
            while(rs.next()) {
                String description = rs.getString("description");
                String packageNumber = rs.getString("package_number");
                String laststatus = rs.getString("package_latest_status");
                String trackingenabled = rs.getString("tracking_enabled");
                String comments = rs.getString("comments");

                TrackingStatus ts = new TrackingStatus(useridentity, packageNumber, description, trackingenabled, comments, laststatus);

                packages_status.add(ts);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return packages_status;
    }

    public static void insertUpdateUser(User user) {
        Connection conn = DBUtil.getConnection(ExpressNZConfHelper.dbName, ExpressNZConfHelper.dbUser, ExpressNZConfHelper.dbPass);
        String sql = "INSERT INTO " + ExpressNZConfHelper.dbName + "." + TABLE_NAME + " (useridentity, active, daysgranted, userprivilege, maxtrackingnumber, starttrackingdate ) VALUES(?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE    \n" +
                "active=VALUES(active), daysgranted=VALUES(daysgranted), userprivilege=VALUES(userprivilege)" +
                ", maxtrackingnumber=VALUES(maxtrackingnumber), starttrackingdate=VALUES(starttrackingdate) ;";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, user.getUseridentity());
            st.setString(2, user.getActive());
            st.setInt(3, user.getDaysgranted());
            st.setInt(4, user.getUserprivilege());
            st.setInt(5, user.getMaxTrackingNumber());
            if (user.getStarttrackingdate() == null) {
                st.setDate(6, null);
            } else {
                st.setDate(6, new Date(user.getStarttrackingdate().getTime()));
            }
            st.executeUpdate();

            user.setDirty(false);
            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
