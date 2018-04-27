package utils.kuaidi;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class YIDATONG extends ExpressCompany {

	static String url = "http://www.qexpress.co.nz/tracking.aspx?orderNumber=";
	
	@Override
	public String checkOrderStatus(String orderNumber) {

		Document doc = null;

        if (orderNumberCheck(orderNumber) == false) {
            return "";
        }

		String queryURL = url + orderNumber;
		
		String status = "";
		
		try {
			doc = Jsoup.connect(queryURL).get();
			
			status = extractStatusInfo(doc);
	
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}
	
	private String extractStatusInfo(Document doc) {
		String resultOverall = "";
		
		Elements wholeElements = null;

        if (doc == null) {
            return "";
        }
//		System.out.println(doc.toString());
		// status
		wholeElements = doc.getElementsByClass("list");

        if (wholeElements.size() == 0) {
            return "";
        }

		for (int listIndex = 0; listIndex < wholeElements.size(); listIndex++) {
			Element el = wholeElements.get(listIndex);
//			String elText = el.toString();
//			System.out.println("==================Element Text==================");
//			System.out.println(elText);
//			System.out.println("====================================");
			
			Elements allItems = el.select("td");
			
			// not result found
			if (allItems.size() == 0) {
				return "";
			}
			
			for (int i = 0; i < allItems.size() / 2; i++) {
				Element elementTime = allItems.get(i * 2);
				Element elementDetail = allItems.get(i * 2 + 1);
				String tempItem = elementTime.ownText() + " " + elementDetail.ownText();
//				System.out.println(temp);
				resultOverall = resultOverall + tempItem + "\n";
			}
		}
		
		return resultOverall;
	}
	
	public static void main(String[] args) {

		//String result = new YIDATONG().checkOrderStatus("QE20110002537");
        String result = new YIDATONG().checkOrderStatus("ZY030674275NZ");
		
		System.out.println("Final result : " + result);

	}

}
