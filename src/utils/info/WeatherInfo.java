package utils.info;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by kyle.yu on 2/03/2016.
 */
public class WeatherInfo {
    private String url = "http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=2&tn=baiduhome_pg&wd=%E5%A5%A5%E5%85%8B%E5%85%B0%E5%A4%A9%E6%B0%94&rsv_spt=1&oq=auckland%20weather&rsv_pq=eba7d252000a3446&rsv_t=1bc62xJuQAPC%2BqcHHmD%2FQ2GrNhvJrshW9kt6%2F98IhgQBAM3k6XJtRa8UqykyV8u9%2BYJn&rsv_enter=0&inputT=8585&rsv_sug3=80&rsv_sug1=26&rsv_sug7=100&rsv_sug2=0&rsv_sug4=8585";

    public String checkAucklandWeather() {
        String weather = "";

        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
            weather = extractWeatherInfo(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return weather;
    }

    private String extractWeatherInfo(Document doc) {
        String weather = "";

        Elements wholeElements = null;

        if (doc == null) {
            return "";
        }

        System.out.println(doc);


        Elements todays = doc.getElementsByClass("op_weather4_twoicon_today");

        if (todays.size() == 0) {
            return "";
        }

        Element today = todays.get(0);
        weather += "日期：" + today.getElementsByClass("op_weather4_twoicon_date").get(0).ownText().trim() + "\n";
        weather += "温度：" + today.getElementsByClass("op_weather4_twoicon_temp").get(0).ownText().trim() + " - ";
        weather += "天气：" + today.getElementsByClass("op_weather4_twoicon_weath").get(0).ownText().trim() + " - ";
        weather += "风速：" + today.getElementsByClass("op_weather4_twoicon_wind").get(0).ownText().trim() + "\n";

        // status
        wholeElements = doc.getElementsByClass("op_weather4_twoicon_day");

        if (wholeElements.size() == 0) {
            return "";
        }
        String wholeText = wholeElements.toString();
        System.out.println("==================Whole Text In price==================");
        System.out.println(wholeText);
        System.out.println("====================================");

        for (Element e : wholeElements) {
            weather += "日期：" + e.getElementsByClass("op_weather4_twoicon_date").get(0).ownText().trim() + "\n";
            weather += "温度：" + e.getElementsByClass("op_weather4_twoicon_temp").get(0).ownText().trim() + " - ";
            weather += "天气：" + e.getElementsByClass("op_weather4_twoicon_weath").get(0).ownText().trim() + " - ";
            weather += "风速：" + e.getElementsByClass("op_weather4_twoicon_wind").get(0).ownText().trim() + "\n";
        }

        return weather;
    }

    public static void main(String[] args) {
        String weather = new WeatherInfo().checkAucklandWeather();
        System.out.println("Final result : \n" + weather);
    }
}
