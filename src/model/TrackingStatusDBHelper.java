package model;

import controller.weichatplatform.expressnz.ExpressNZConfHelper;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kyle.yu on 14/09/2015.
 */
public class TrackingStatusDBHelper {
    public final static String TABLE_NAME = "trackingstatus";

    // TODO: so far, do not consider that multiple user tracking same package. will do it later.

    public static TrackingStatus loadTrackingStatus(String useridentity, String packageNumber) {
        String description = "";
        String tracking_enabled = "Y";
        String comments = "";
        String package_latest_status = "";

        String sql = "select description, tracking_enabled, comments, package_latest_status from " +
                ExpressNZConfHelper.dbName + "." + TABLE_NAME + " where useridentity='" + useridentity + "' " +
                "and package_number='" + packageNumber + "' " +
                "and tracking_enabled='" + tracking_enabled + "'" ;
        try {
            ResultSet rs = DBUtil.executeQuery(sql);

            if (rs.next()) {
                // find the user from database
                description = rs.getString("description");
                comments = rs.getString("comments");
                package_latest_status = rs.getString("package_latest_status");
            } else {
                // do nothing. use default value
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TrackingStatus ts = new TrackingStatus(useridentity, packageNumber, description, tracking_enabled, comments, package_latest_status);

        return ts;
    }

    public static void insertUpdateTrackingStatus(TrackingStatus ts) {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO " + ExpressNZConfHelper.dbName + "." + TABLE_NAME +
                "(package_number, description, tracking_enabled, comments, package_latest_status,useridentity) " +
                "VALUES(?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE" +
                " package_latest_status=VALUES(package_latest_status), tracking_enabled=VALUES(tracking_enabled), " +
                "description=VALUES(description), comments=VALUES(comments)";
        try {
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, ts.getPackage_number());
            st.setString(2, ts.getDescription());
            st.setString(3, ts.getTracking_enabled());
            st.setString(4, ts.getComments());
            st.setString(5, ts.getPackage_latest_status());
            st.setString(6, ts.getUseridentity());

            st.executeUpdate();

            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
