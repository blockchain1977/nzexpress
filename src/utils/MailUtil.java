package utils;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by yplyf on 2014/11/25.
 */
public class MailUtil {
    private static Logger logger = Logger.getLogger(MailUtil.class);

    static List<MailContent> mailList = new Vector<>();

    // send through sys command
    public static void addMailToQueue(String subject, String content, String recipients) {
        MailContent mail = new MailContent();
        mail.content = content;
        mail.subject = subject;
        mail.recipients = recipients;

        mailList.add(mail);
    }

    public static void sendMails() {
        for (int mailIndex = 0; mailIndex < mailList.size(); mailIndex++) {
            MailContent mail = mailList.get(mailIndex);
            String cmd = "/home/ubuntu/sendmail.sh \'" + mail.subject + "\'" + " \'" + mail.content + "\'" + " \"" + mail.recipients + "\"";

            boolean result = SysUtil.runSystemCommand(cmd);
            logger.debug("Send Mail: " + cmd + " Status : " + result);
        }

        mailList = new Vector<>();
    }

    public static void main(String args[]) {

        //MailUtil.addMailToQueue("Test", "Haha");
    }
}

class MailContent {
    String subject;
    String content;
    String recipients;

    protected MailContent() {};
}
