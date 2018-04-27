package utils.info;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.kuaidi.KUAIDI100;

import java.io.IOException;

/**
 * Created by kyle.yu on 2/03/2016.
 */
public class GoldInfo {
    private String url = "http://www.baidu.com/s?wd=%E9%BB%84%E9%87%91%E4%BB%B7%E6%A0%BC&rsv_spt=1&rsv_iqid=0xd881724f00075a59&issp=1&f=8&rsv_bp=0&rsv_idx=2&ie=utf-8&tn=baiduhome_pg&rsv_enter=1&rsv_sug3=14&rsv_sug2=0&inputT=2549&rsv_sug4=3697";

    public String checkGoldPrice() {
        String price = "";

        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
            price = extractPriceInfo(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return price;
    }

    private String extractPriceInfo(Document doc) {
        String price = "";

        Elements wholeElement = null;

        if (doc == null) {
            return "";
        }

        System.out.println(doc);

        // status
        wholeElement = doc.getElementsByClass("op-table05-main-table");

        if (wholeElement.size() == 0) {
            return "";
        }
        String wholeText = wholeElement.toString();
        System.out.println("==================Whole Text In price==================");
        System.out.println(wholeText);
        System.out.println("====================================");

        Elements tds = wholeElement.get(0).select("td");

        // not result found
        if (tds.size() == 0) {
            return "";
        }
        for (Element element : tds) {
            price = price + element.ownText() + " ";
        }

        return price;
    }

    public static void main(String[] args) {
        String price = new GoldInfo().checkGoldPrice();
        System.out.println("Final result : \n" + price);
    }
}
