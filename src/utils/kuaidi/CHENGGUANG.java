package utils.kuaidi;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CHENGGUANG extends ExpressCompany {
	static String url = "http://www.flywayex.com/cgi-bin/GInfo.dll?EmmisTrack";

	public CHENGGUANG() {
	}

	@Override
	public String checkOrderStatus(String orderNumber) {
		Document doc = null;
		String status = "";

        if (orderNumberCheck(orderNumber) == false) {
            return "";
        }
		
		try {
			doc = Jsoup.connect(url).data("w", "flyway")
					.data("cno", orderNumber).timeout(4000).post();
			status = extractStatusInfo(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}
	
	
	private String extractStatusInfo(Document doc) {
		String resultOverall = "";
		
		Element wholeElement = null;

        if (doc == null) {
            return "";
        }

		// status
		wholeElement = doc.getElementById("oDetail");
        if (wholeElement == null) {
            return "";
        }
//		String wholeText = wholeElement.toString();
//		System.out.println("==================Whole Text==================");
//		System.out.println(wholeText);
//		System.out.println("====================================");
		
		Elements allItems = wholeElement.select("td");
		
		// not result found
		if (allItems.size() == 0) {
			return "";
		}
		
		for (int i = 0; i < allItems.size() / 3; i++) {
			Element elementTime = allItems.get(i * 3);
			Element elementLocation = allItems.get(i * 3 + 1);
			Element elementDetail = allItems.get(i * 3 + 2);
			String tempItem = elementTime.ownText() + " " + elementLocation.ownText() + " " + elementDetail.ownText();
//			System.out.println(temp);
			resultOverall = resultOverall + tempItem + "\n";
		}
		
		return resultOverall;
	}
	
	public static void main(String[] args) {
		System.out.println(new CHENGGUANG().checkOrderStatus("108001027409"));
	}

}
