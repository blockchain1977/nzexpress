package utils;

import controller.weichatplatform.expressnz.ExpressNZConfHelper;
import org.apache.log4j.Logger;

import java.sql.*;


/**
 * Created by yplyf on 2015/1/26.
 */
public class DBUtil {
    private static Logger logger = Logger.getLogger(DBUtil.class);

    static Connection conn = null;

    public static Connection getConnection(String database, String username, String password) {
        try {
            if ((conn != null) && (!conn.isClosed())) {
                logger.debug("reuse exist connection");

                return conn;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String url;
        try {
                Class.forName("com.mysql.jdbc.Driver");
                url = "jdbc:mysql://localhost:12306/" + database ;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            logger.debug("username = " + username + " password = " + password);
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static Connection getConnection() {
        return DBUtil.getConnection(ExpressNZConfHelper.dbName, ExpressNZConfHelper.dbUser, ExpressNZConfHelper.dbPass);
    }

    public static  ResultSet executeQuery(String sql) {
        logger.debug("Execute Query: " + sql);

        Connection conn = DBUtil.getConnection();
        ResultSet rs = null;

        try {
            PreparedStatement st = conn.prepareStatement(sql);
            rs = st.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public static  int executeUpdate(String sql) {
        logger.debug("Execute Update : " + sql);

        Connection conn = DBUtil.getConnection();
        int result = -1;

        try {
            PreparedStatement st = conn.prepareStatement(sql);
            result = st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String args[]) {
        String aa = "aaa";
        aa = aa.substring(0, 3);
        System.out.println(aa);
        //System.out.println(recipients.toString());
        //MailUtil.addMailToQueue("Test", "Haha");
    }
}
