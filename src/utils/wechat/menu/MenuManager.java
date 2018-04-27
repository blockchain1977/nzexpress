package utils.wechat.menu;

import utils.wechat.menu.pojo.*;
import org.apache.log4j.Logger;

/**
 * Created by kyle.yu on 21/05/2015.
 */
public class MenuManager {
    private static Logger logger = Logger.getLogger(MenuManager.class);

    public static void main(String[] args) {

        String appId = "wx7c87bfe13d1d0389";

        String appSecret = "a143e475e0c1103d460fcd6ac28e7039";

        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        if (null != at) {

            int result = WeixinUtil.createMenu(getMenu(), at.getToken());

            if (0 == result)
                System.out.println("创建菜单成功");
            else
                System.out.println("创建菜单失败：" + result);
        }
    }

    private static Menu getMenu() {
        CommonButton btn21 = new CommonButton();
        btn21.setName("单个查询");
        btn21.setType("click");
        btn21.setKey("21");
//
//        ViewButton btn22 = new ViewButton();
//        btn22.setName("二维码扫描查询");
//        btn22.setType("view");
//        btn22.setUrl("http://weidian.com/user/userinfo/userIndex.html?wfr=wechatpo_keywords_myorder");

//        ComplexButton mainBtn2 = new ComplexButton();
//        mainBtn2.setName("单个快递查询");
//        mainBtn2.setSub_button(new Button[]{btn21, btn22});

        CommonButton btn31 = new CommonButton();
        btn31.setName("添加跟踪快递");
        btn31.setType("click");
        btn31.setKey("31");

        CommonButton btn32 = new CommonButton();
        btn32.setName("删除跟踪快递");
        btn32.setType("click");
        btn32.setKey("32");

        CommonButton btn33 = new CommonButton();
        btn33.setName("查询跟踪快递");
        btn33.setType("click");
        btn33.setKey("33");

        CommonButton btn34 = new CommonButton();
        btn34.setName("查询跟踪详情");
        btn34.setType("click");
        btn34.setKey("34");


        ComplexButton mainBtn3 = new ComplexButton();
        mainBtn3.setName("批量跟踪");
        mainBtn3.setSub_button(new Button[]{btn31, btn33, btn34, btn32});

        ViewButton btn11 = new ViewButton();
        btn11.setName("使用说明");
        btn11.setType("view");
        btn11.setUrl("http://mp.weixin.qq.com/s?__biz=MzAxMDM5OTk3OQ==&mid=211984643&idx=1&sn=00abc4f2938ca6adeaad7254dbccf623#rd");

//        CommonButton btn12 = new CommonButton();
//        btn12.setName("上传身份证");
//        btn12.setType("click");
//        btn12.setKey("12");

        CommonButton btn12 = new CommonButton();
        btn12.setName("常用资讯");
        btn12.setType("click");
        btn12.setKey("12");

        CommonButton btn13 = new CommonButton();
        btn13.setName("联系我们");
        btn13.setType("click");
        btn13.setKey("13");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("使用说明");
        mainBtn1.setSub_button(new Button[]{btn11, btn12, btn13});

        Menu menu = new Menu();
        menu.setButton(new Button[] { btn21, mainBtn3, mainBtn1 });

        return menu;
    }
}

/* Happylifenz menu
package utils.wechat.menu;

import utils.wechat.menu.pojo.*;

public class MenuManager {
    //private static Logger logger = LoggerFactory.getLogger(MenuManager.class);

    public static void main(String[] args) {
        String appId = "wx35fd9fab43311881";

        String appSecret = "7e58952216839e738767c71cbf89a350";

        AccessToken at = WeixinUtil.getAccessToken(appId, appSecret);

        if (null != at) {

            int result = WeixinUtil.createMenu(getMenu(), at.getToken());

            if (0 == result)
                System.out.println("创建菜单成功");
            else
                System.out.println("创建菜单失败：" + result);
        }
    }

    private static Menu getMenu() {
        ViewButton btn11 = new ViewButton();
        btn11.setName("往期精彩");
        btn11.setType("view");
        btn11.setUrl("http://mp.weixin.qq.com/mp/getmasssendmsg?__biz=MzA5OTU4NjI5Ng==#wechat_webview_type=1&wechat_redirect");

        CommonButton btn12 = new CommonButton();
        btn12.setName("开发中");
        btn12.setType("click");
        btn12.setKey("12");

        CommonButton btn13 = new CommonButton();
        btn13.setName("开发中");
        btn13.setType("click");
        btn13.setKey("13");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("随便看看");
        mainBtn1.setSub_button(new Button[]{btn11, btn12, btn13});

        ViewButton btn21 = new ViewButton();
        btn21.setName("进店看看");
        btn21.setType("view");
        btn21.setUrl("http://wd.koudai.com/?userid=330873760");

        ViewButton btn22 = new ViewButton();
        btn22.setName("我的订单");
        btn22.setType("view");
        btn22.setUrl("http://weidian.com/user/userinfo/userIndex.html?wfr=wechatpo_keywords_myorder");

        ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("~微店~");
        mainBtn2.setSub_button(new Button[]{btn21, btn22});

        ViewButton btn31 = new ViewButton();
        btn31.setName("快递查询");
        btn31.setType("view");
        btn31.setUrl("http://wd.koudai.com/?userid=330873760");

        ViewButton btn32 = new ViewButton();
        btn32.setName("上传身份证");
        btn32.setType("view");
        btn32.setUrl("http://wd.koudai.com/?userid=330873760");

        ViewButton btn33 = new ViewButton();
        btn33.setName("公众号开发");
        btn33.setType("view");
        btn33.setUrl("http://wd.koudai.com/?userid=330873760");

        ComplexButton mainBtn3 = new ComplexButton();
        mainBtn3.setName("服务专区");
        mainBtn3.setSub_button(new Button[]{btn31, btn32, btn33});

        Menu menu = new Menu();
        menu.setButton(new Button[] { mainBtn1, mainBtn2, mainBtn3 });

        return menu;
    }
}
 */