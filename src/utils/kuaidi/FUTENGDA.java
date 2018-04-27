package utils.kuaidi;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FUTENGDA extends ExpressCompany {
	static String url = "http://www.ftdlogistics.co.nz/query";

	public FUTENGDA() {

	}

	@Override
	public String checkOrderStatus(String orderNumber) {
		String status = "";
		
		Document doc = null;

        if (orderNumberCheck(orderNumber) == false) {
            return "";
        }
		
		try {
			doc = Jsoup.connect(url).data("codes", orderNumber)
					.data("x", "10").data("y", "10").post();
			status = extractStatusInfo(doc);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}
	
	private String extractStatusInfo(Document doc) {
		String resultOverall = "";
		
		Elements wholeElement = null;

        if (doc == null) {
            return "";
        }
		
		// status
		wholeElement = doc.getElementsByClass("qrBox");

        if (wholeElement.size() == 0) {
            return "";
        }
		String wholeText = wholeElement.toString();
		System.out.println("==================Whole Text In qrBox==================");
		System.out.println(wholeText);
		System.out.println("====================================");
		
		// international
		String resultInternational = "";
		Elements interNationalList = wholeElement.get(0).select("p");
		
		// not result found
		if (interNationalList.size() == 0) {
			return "";
		}
		for (Element element : interNationalList) {
			resultInternational = resultInternational + element.ownText() + "\n";
		}
//	    System.out.println(resultInternational);
	    resultOverall = resultOverall + resultInternational;
		
		// local, kuaidi 100
		String resultLocal = "";
		
		Elements customList = wholeElement.get(0).getElementsByClass("clear");
		System.out.println("customList: " + customList);
		System.out.println("====================");
		// no local info
		if (customList.size() == 0) {
			return resultOverall;
		}
		
		//String customStatus = customList.get(0).text();
		//resultOverall = resultOverall + customStatus + "\n";
		
		//if (customStatus.equalsIgnoreCase("清关完成")) {
		String companyCode = "";
		String orderNumber = "";
		String companyName = "";

		if (!(customList.size() > 1)) {
			return resultOverall;
		}
		String companyURL = customList.get(1).select("script").get(0).attr("src");
		companyCode = companyURL.substring(companyURL.indexOf("exp=") + 4);
		companyCode = companyCode.substring(0, companyCode.indexOf("&"));
		if (companyCode.equals("yd")) {
			companyCode = "yunda";
		} else if (companyCode.equals("qf")) {
			companyCode = "quanfengkuaidi";
		} else if (companyCode.equals("st")) {
			companyCode = "shentong";
		} else if (companyCode.equals("yt")) {
			companyCode = "yuantong";
		} else if (companyCode.equals("zt")) {
			companyCode = "zhongtong";
		} else if (companyCode.equals("ems")) {
			companyCode = "ems";
		} else if (companyCode.equals("sf")) {
			companyCode = "sf";
		} else {
			System.out.println("Unknown company : " + companyCode);
		}

		companyName = customList.get(2).select("a").get(0).text();
		orderNumber = companyURL.substring(companyURL.indexOf("sn") + 2);
		orderNumber = orderNumber.substring(0, orderNumber.indexOf("&"));
//			System.out.println(orderNumber);

		resultLocal = KUAIDI100.checkOrderStatus(companyCode, orderNumber);
		resultLocal = companyName + "\n" + resultLocal;
		//}
		
		resultOverall = resultOverall + resultLocal;
		
		return resultOverall;
	}

	public static void main(String[] args) {
		String result = new FUTENGDA().checkOrderStatus("nz1416564");
		System.out.println("Final result : \n" + result);
	}

}
