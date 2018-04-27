package main;

import org.apache.log4j.Logger;
import utils.ConfigUtil;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


/**
 * Created by yplyf on 2015/1/11.
 * Main
 */
public class Main extends javax.servlet.http.HttpServlet {
    private static Logger logger = Logger.getLogger(Main.class);

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response) {

        ConfigUtil.setContextPath(getServletContext());
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("UTF-8");

        // Check real
        if (!AccessManager.checkSignature(request)) {
            logger.info("check signature failed. Attaker?");
            return;
        }

        // 调用核心业务类接收消息、处理消息
        String respMessage = CoreService.processRequest(request);

        // 响应消息
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert out != null;
        out.print(respMessage);
        out.close();
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)  {
        logger.info("doGet start");
        if (AccessManager.checkSignature(request)) {
            response.setContentType("text/plain");
            String echostr = request.getParameter("echostr");
            logger.debug("echostr is : " + echostr);
            PrintWriter pw = null;
            try {
                pw = response.getWriter();
            } catch (Exception e) {
                logger.debug(e);
            }
            assert pw != null;
            pw.print(echostr);
            pw.flush();
            pw.close();
        }
    }

}