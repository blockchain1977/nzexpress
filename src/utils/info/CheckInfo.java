package utils.info;

/**
 * Created by kyle.yu on 2/03/2016.
 */
public class CheckInfo {
    public static String checkInfo() {
        String info = "奥克兰天气：\n" + new WeatherInfo().checkAucklandWeather().trim();
        info += "\n================\n";

        info += "黄金价格：\n" + new GoldInfo().checkGoldPrice().trim();

        return info;
    }

    public static String checkMoreInfo() {
        String info = "奥克兰天气：\n" + new WeatherInfo().checkAucklandWeather().trim();
        info += "\n================\n";

        info += "汇率：\n" + new CurrencyInfo().checkNzdRmbRate().trim();
        info += "\n================\n";

        info += "黄金价格：\n" + new GoldInfo().checkGoldPrice().trim();

        return info;
    }

    public static void main(String[] args) {
        String info = new CheckInfo().checkInfo();
        System.out.println("Final result : \n" + info);
    }
}
