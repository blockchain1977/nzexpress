package main;

import controller.weichatplatform.expressnz.BackendActs;
import org.apache.log4j.Logger;
import utils.ConfigUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by yplyf on 2015/9/16.
 * Listener to start the backend thread
 */
public class ExpressNZServletContextListener implements ServletContextListener {
    private static Logger logger = Logger.getLogger(ExpressNZServletContextListener.class);

    private static BackendActs backendActs = null;
    private Thread t = null;

    public void contextInitialized(ServletContextEvent sce) {
        ConfigUtil.setContextPath(sce.getServletContext());

        t = new Thread() {
            @Override
            public void run() {
                super.run();
                if (backendActs==null) {
                    backendActs = new BackendActs();
                    backendActs.setName("BackEnd Actions");
                    backendActs.setPriority(Thread.MIN_PRIORITY);
                    backendActs.start();

                    logger.debug("BackEnd Actions Thread started");
                }

                if(!backendActs.isAlive())
                {
                    backendActs.flushDB();
                    backendActs = new BackendActs();
                    backendActs.setName("BackEnd Actions");
                    backendActs.setPriority(Thread.MIN_PRIORITY);
                    backendActs.start();

                    logger.debug("BackEnd Actions Thread started");
                }
            }
        };
        t.start();
    }



    public static String backendThreadStatus() {
        return backendActs.getState().toString();
    }

    public void contextDestroyed(ServletContextEvent sce){
        try {
            backendActs.setExit(true);
            backendActs.interrupt();

            logger.debug("BackEnd Actions Thread interruptted");
        } catch (Exception ex) {
        }
    }
}
