package utils.info;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by kyle.yu on 2/03/2016.
 */
public class CurrencyInfo {
    private String url = "http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=2&tn=baiduhome_pg&wd=nzd%20rmb&rsv_spt=1&oq=nzd%20rmb%20usd&rsv_pq=e633f9fb000a0b53&rsv_t=1708b5vvtmUypjctmlVvj6gmkHlEoBapVAiq6HowKdgoR0X%2FwtLgF6PFUU4kZExP%2FfQd&rsv_enter=0&rsv_sug=1";

    public String checkNzdRmbRate() {
        String rate = "";

        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
            rate = extractRateInfo(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rate;
    }

    private String extractRateInfo(Document doc) {
        String rate = "";

        Elements wholeElement = null;

        if (doc == null) {
            return "";
        }

        System.out.println(doc);

        // status
        wholeElement = doc.getElementsByClass("op_exrate_result");

        if (wholeElement.size() == 0) {
            return "";
        }
        String wholeText = wholeElement.toString();
        System.out.println("==================Whole Text In price==================");
        System.out.println(wholeText);
        System.out.println("====================================");

        Elements tds = wholeElement.get(0).select("div");

        // not result found
        if (tds.size() == 0) {
            return "";
        }
        for (Element element : tds) {
            rate = rate + element.ownText() + " ";
        }

        return rate;
    }

    public static void main(String[] args) {
        String rate = new CurrencyInfo().checkNzdRmbRate();
        System.out.println("Final result : \n" + rate);
    }
}
