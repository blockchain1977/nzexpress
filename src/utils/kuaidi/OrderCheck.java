package utils.kuaidi;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import controller.weichatplatform.expressnz.ExpressNZConfHelper;

public class OrderCheck {
    private static Logger logger = Logger.getLogger(OrderCheck.class);

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please provide order numbers and separate them with \",\"");
			return;
		}

        checkOrderListStatus(args[0]);

	}

    public static String checkOrderListStatus(String orderNumbers) {
        StringTokenizer st = new StringTokenizer(orderNumbers, ",");
        String result = "";

        logger.debug("Check Order List Status - OrderNumber : " + orderNumbers);

        while (st.hasMoreTokens()) {
            String orderNumber = st.nextToken();
            String orderStatus = checkOrderStatus(orderNumber);
  //        String statusForLog = "<ordernumber>\n" + orderNumber + "\n</ordernumber>" + "\n" + "<orderstatus>\n" + orderStatus + "</orderstatus>\n";
            result = result + orderNumbers + "\n======\n" + orderStatus;
        }

        return result;
    }

    public static String checkOrderStatus(String orderNumber) {
        String result = "";
        String orderStatus = "";

        orderNumber = orderNumber.toUpperCase();

        String ftdpattern = ExpressNZConfHelper.ftdpattern.toUpperCase();
        String ydtpattern = ExpressNZConfHelper.ydtpattern.toUpperCase();
        String cgpattern = ExpressNZConfHelper.cgpattern.toUpperCase();

        if ((orderNumber.startsWith(ftdpattern) || orderNumber.startsWith("FTD")) && (ExpressNZConfHelper.ftdenable.equalsIgnoreCase("true"))) {
            orderStatus = new FUTENGDA().checkOrderStatus(orderNumber);
        } else if (orderNumber.startsWith(cgpattern) && (ExpressNZConfHelper.cgenable.equalsIgnoreCase("true"))) {
            orderStatus = new CHENGGUANG().checkOrderStatus(orderNumber);
        } else if (orderNumber.endsWith(ydtpattern) && (ExpressNZConfHelper.ydtenable.equalsIgnoreCase("true"))) {
            orderStatus = new YIDATONG().checkOrderStatus(orderNumber);
        } else {
            return "无效的快递单号。目前仅支持程光，富腾达，易达通等快递公司。";
        }

        result = orderStatus;

        return result;
    }

}
