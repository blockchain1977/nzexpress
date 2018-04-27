package codeback;

import utils.ConfigUtil;
import utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by yplyf on 2015/5/6.
 */
public class WeChatUserInfo {
    boolean subscribe;
    String openid;
    String nickname;
    boolean sex;
    String city;
    String country;
    String province;
    String language;
    String headingurl;
    String subscribe_time;
    String unionid;

    @Override
    public String toString() {
        return "WeChatUserInfo{" +
                "subscribe=" + subscribe +
                ", openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", language='" + language + '\'' +
                ", headimgurl='" + headingurl + '\'' +
                ", subscribe_time='" + subscribe_time + '\'' +
                ", unionid='" + unionid + '\'' +
                '}';
    }

    // write to database
    public void serialize() {
//        String dbName = ConfigUtil.getConfig().getProperty("expressnz.database");
//        String dbUser = ConfigUtil.getConfig().getProperty("expressnz.username");
//        String dbPass = ConfigUtil.getConfig().getProperty("expressnz.password");
//
//        Connection conn = DBUtil.getConnection(dbName, dbUser, dbPass);
//
//        String query = "insert into kuaidi.userinfo (openid, nickname, sex, city, country, province, language, headingurl, subscribe_time, unionid)"
//                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//        // create the mysql insert preparedstatement
//        PreparedStatement preparedStmt = null;
//        try {
//            preparedStmt = conn.prepareStatement(query);
//
//            preparedStmt.setString (1, openid);
//            preparedStmt.setString (2, nickname);
//            preparedStmt.setBoolean (3, sex);
//            preparedStmt.setString (4, city);
//            preparedStmt.setString (5, country);
//            preparedStmt.setString (6, province);
//            preparedStmt.setString (7, language);
//            preparedStmt.setString (8, headingurl);
//            preparedStmt.setString (9, subscribe_time);
//            preparedStmt.setString (10, unionid);
//
//            preparedStmt.execute();
//
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public WeChatUserInfo() {

    }
}
