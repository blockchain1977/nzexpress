package model;

import utils.DBUtil;
import controller.weichatplatform.expressnz.ExpressNZConfHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by yplyf on 2015/5/23.
 */
public class Message {
    public final static String TABLE_NAME = "messages";

    private int userid;
    private String msgtype;
    private String content;
    private String eventtype;
    private String eventkey;

    public Message(int userid, String msgtype, String content, String eventtype, String eventkey) {
        this.userid = userid;
        this.msgtype = msgtype;
        this.content = content;
        this.eventtype = eventtype;
        this.eventkey = eventkey;
    }

    public void insert() {
        Connection conn = DBUtil.getConnection(ExpressNZConfHelper.dbName, ExpressNZConfHelper.dbUser, ExpressNZConfHelper.dbPass);
        String sql = "INSERT INTO " + ExpressNZConfHelper.dbName + ".messages (userid" +
                "  ,msgtype" +
                "  ,content" +
                "  ,eventtype" +
                "  ,eventkey) VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE \n" +
                "count=count+1";
        try {
            PreparedStatement st = conn.prepareStatement(sql);

            st.setInt(1, userid);
            st.setString(2, msgtype);

            st.setString(3, content);
            st.setString(4, eventtype);
            st.setString(5, eventkey);
            st.executeUpdate();

            st.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {

    }

}
