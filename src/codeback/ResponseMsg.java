package codeback;

import utils.wechat.MessageUtil;
import utils.wechat.message.resp.Article;
import utils.wechat.message.resp.NewsMessage;
import utils.wechat.message.resp.TextMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kyle.yu on 15/05/2015.
 */
public class ResponseMsg {
    // picture url
    final static String WEIDIAN_URL_CLICKINDIANPIC = "https://mmbiz.qlogo.cn/mmbiz/xlg5WVvGgXfHcAOwX10o39veuYVZcIoW5E5tP7ZHpBicmwyPoYUUpibbyyYhzZaH5Zk0REWC6oIETkrFqQR9VOQQ/0?wx_fmt=jpeg";
    final static String WEIDIAN_URL_CLICKINCATEGORYPIC = "https://mmbiz.qlogo.cn/mmbiz/xlg5WVvGgXfHcAOwX10o39veuYVZcIoWoibiadWGoSrcVy4hjt8kXjQjNrpSx2TibiaFNZeJCQp0RibaN1Q6fUl7uQg/0?wx_fmt=jpeg";
    final static String WEIDIAN_URL_CLICKINDINGDANPIC = "https://mmbiz.qlogo.cn/mmbiz/xlg5WVvGgXfHcAOwX10o39veuYVZcIoWInmES46kncibiahNjD7A9dLNyW2ohWiaLPTZTGWAIaNnGTqPnj0lLPnOg/0?wx_fmt=jpeg";

    // site link url
    final static String WEIDIAN_URL_MAIN = "http://wd.koudai.com/?userid=330873760";
    final static String WEIDIAN_URL_CATEGORY = "http://wd.koudai.com/item_classes.html?userid=330873760";
    final static String WEIDIAN_URL_DINGDAN = "http://weidian.com/user/userinfo/userIndex.html?wfr=wechatpo_keywords_myorder";

    // prompt msg
    final static String NORMAL_GREETING_MSG = "欢迎您关注 开心生活Go! \n\n\n" +
            "在这里，您可以发现新西兰的风土人情逸闻趣事。\n\n\n" +
            "您也可以购买到我们精选的新西兰本土最有口碑的产品。\n\n\n" +
            "开心生活，由此出发，GO！\n\n";

    final static String CATEGORY_ARTICLE_DESC = "您也可以在输入框中输入分类关键字, 如: 奶粉, 健康产品, 蜂产品, 护肤品, 中老年, 儿童，女士，男士 浏览分类商品信息。";

    public static boolean inCategory(String userMessage) {
        if (userMessage.equals("奶粉") || userMessage.equals("健康产品")
                || userMessage.equals("蜂产品") || userMessage.equals("护肤品")
                || userMessage.equals("儿童") || userMessage.equals("中老年")
                || userMessage.equals("男士") || userMessage.equals("女士")) {
            return true;
        }

        return false;
    }

    public static String getCategoryUrl(String userMessage) {
        String subURL = "";
        if (userMessage.equals("奶粉")) {
            subURL = "&c=45268016&des=奶粉";
        } else if (userMessage.equals("健康产品")) {
            subURL = "&c=44779722&des=天然膳食补充";
        } else if (userMessage.equals("蜂产品")) {
            subURL = "&c=45268396&des=蜂产品";
        } else if (userMessage.equals("护肤品")) {
            subURL = "&c=44787760&des=有机护肤";
        } else if (userMessage.equals("中老年")) {
            subURL = "&c=44781084&des=中老年专区";
        } else if (userMessage.equals("儿童")) {
            subURL = "&c=44781591&des=婴幼儿专区";
        } else if (userMessage.equals("男士")) {
            subURL = "c=44783271&des=男士专区";
        } else if (userMessage.equals("女士")) {
            subURL = "&c=44779723&des=女士专区";
        }

        return subURL;
    }

    public static String respGreeting(Map<String, String> requestMap) {
        String respMessage;

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(fromUserName);
        textMessage.setFromUserName(toUserName);
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setContent(NORMAL_GREETING_MSG);

        respMessage = MessageUtil.textMessageToXml(textMessage);

        return respMessage;
    }

    public static String respDingDan(Map<String, String> requestMap) {
        List<Article> articleList = new ArrayList<>();

        Article article = createDingDanArticle();
        articleList.add(article);

        return createArticleResp(requestMap, articleList);
    }

    public static String respCategory(Map<String, String> requestMap, String category) {


        List<Article> articleList = new ArrayList<>();

        Article article = createCategoryArticle(category, getCategoryUrl(category));
        articleList.add(article);

        return createArticleResp(requestMap, articleList);
    }

    public static String respQuestion(Map<String, String> requestMap) {
        List<Article> articleList = new ArrayList<>();

        Article article = createQuestionMsgArticle();
        articleList.add(article);

        // question message will be marked as important
        return createArticleResp(requestMap, articleList, true);
    }

    public static String respOtherMsg(Map<String, String> requestMap) {
        List<Article> articleList = new ArrayList<Article>();

        Article article = createOtherMsgArticle();
        articleList.add(article);

        return createArticleResp(requestMap, articleList);
    }

    private static String createArticleResp(Map<String, String> requestMap, List<Article> articleList) {
        return createArticleResp(requestMap, articleList, false);
    }

    private static String createArticleResp(Map<String, String> requestMap, List<Article> articleList, boolean important) {
        String respMessage;

        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");

        NewsMessage newsMessage = new NewsMessage();
        newsMessage.setToUserName(fromUserName);
        newsMessage.setFromUserName(toUserName);
        newsMessage.setCreateTime(new Date().getTime());
        newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
        if (important) {
            newsMessage.setFuncFlag(1);
        } else {
            newsMessage.setFuncFlag(0);
        }

        newsMessage.setArticleCount(articleList.size());
        newsMessage.setArticles(articleList);
        respMessage = MessageUtil.newsMessageToXml(newsMessage);

        return respMessage;
    }

    private static Article createCategoryArticle(String category, String categoryUrl) {
        Article article = new Article();
        article.setTitle("点击本消息进入" + category + "分类!");
        article.setDescription(CATEGORY_ARTICLE_DESC);
        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
        article.setUrl(WEIDIAN_URL_CATEGORY + categoryUrl);

        return article;
    }

    private static Article createDingDanArticle() {
        Article article = new Article();
        article.setTitle("点击本消息并登录查看您的订单!");
        article.setDescription(CATEGORY_ARTICLE_DESC);
        article.setPicUrl(WEIDIAN_URL_CLICKINDINGDANPIC);
        article.setUrl(WEIDIAN_URL_DINGDAN);

        return article;
    }

//    private static Article createGreetingArticle() {
//        Article article = new Article();
//        article.setTitle("欢迎您关注 开心生活Go~!");
//        article.setDescription(NORMAL_GREETING_MSG);
//        article.setPicUrl(WEIDIAN_URL_CLICKINDIANPIC);
//        article.setUrl(WEIDIAN_URL_MAIN);
//        return article;
//    }

    private static Article createOtherMsgArticle() {
        Article article = new Article();
        article.setTitle("感谢您的关注!");
        article.setDescription(NORMAL_GREETING_MSG);
        article.setPicUrl(WEIDIAN_URL_CLICKINDIANPIC);
        article.setUrl(WEIDIAN_URL_MAIN);
        return article;
    }

    private static Article createQuestionMsgArticle() {
        Article article = new Article();
        article.setTitle("感谢您的提问! 我们会尽快联系您.");
        article.setDescription(NORMAL_GREETING_MSG);
        article.setPicUrl(WEIDIAN_URL_CLICKINDIANPIC);
        article.setUrl(WEIDIAN_URL_MAIN);
        return article;
    }
//
//    private static Article createMilkPowderArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入奶粉分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl("http://wd.koudai.com/item_classes.html?userid=260094356&c=25967314&des=奶粉");
//        return article;
//    }
//
//    private static Article createBaoJianPinArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入健康产品分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl("http://wd.koudai.com/item_classes.html?userid=260094356&c=25967314&des=健康产品");
//        return article;
//    }
//
//    private static Article createElderArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入中老年分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl(WEIDIAN_URL_ELDER);
//        return article;
//    }
//
//    private static Article createKidsArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入儿童分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl(WEIDIAN_URL_KIDS);
//        return article;
//    }
//
//    private static Article createHoneyArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入蜂产品分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl(WEIDIAN_URL_HONEY);
//        return article;
//    }
//
//    private static Article createSkinCareArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入护肤品分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl(WEIDIAN_URL_SKINCARE);
//        return article;
//    }
//
//    private static Article createManArticle() {
//        Article article = new Article();
//        article.setTitle("点击本消息进入男士分类!");
//        article.setDescription(CATEGORY_ARTICLE_DESC);
//        article.setPicUrl(WEIDIAN_URL_CLICKINCATEGORYPIC);
//        article.setUrl(WEIDIAN_URL_MAN);
//        return article;
//    }
//
//    private static Article createWomanArticle() {
//
//    }
}

    /*
            if ("1".equals(content)) {
                Article article = new Article();
                article.setTitle("新西兰奶粉直邮");
                article.setDescription(""); //爱他美金装新西兰直邮（1段/2段/3段/4段）。
                article.setPicUrl("");
                article.setUrl("http://weidian.com/item_classes.html?userid=260094356&c=25967314&des=%E5%A5%B6%E7%B2%89");
                articleList.add(article);

                Article article1 = new Article();
                article1.setTitle("爱他美金装新西兰直邮（1段/2段/3段/4段）");
                article1.setDescription(""); //爱他美金装新西兰直邮（1段/2段/3段/4段）。
                article1.setPicUrl("http://wd.geilicdn.com/vshop260094356-1421133485122-6506791.jpg?w=1080&h=0");
                article1.setUrl("http://weidian.com/item.html?itemID=889238348&f_seller_id=");
                articleList.add(article1);

                Article article2 = new Article();
                article2.setTitle("可瑞康金装新西兰直邮（1段/2段/3段/4段）");
                article2.setDescription(""); //可瑞康金装新西兰直邮（1段/2段/3段/4段）。
                article2.setPicUrl("http://wd.geilicdn.com/vshop260094356-1420323387869-3956549.jpg?w=1080&h=0");
                article2.setUrl("http://weidian.com/item.html?itemID=824936266&f_seller_id=");
                articleList.add(article2);

                Article article3 = new Article();
                article3.setTitle("可瑞康羊奶粉新西兰直邮（1段/2段/3段）");
                article3.setDescription(""); //可瑞康羊奶粉新西兰直邮（1段/2段/3段）。
                article3.setPicUrl("http://wd.geilicdn.com/vshop260094356-1421135699936-1007694.jpg?w=1080&h=0");
                article3.setUrl("http://weidian.com/item.html?itemID=889449716&f_seller_id=");
                articleList.add(article3);

                Article article4 = new Article();
                article4.setTitle("输入1，查看奶粉信息\n" +
                        "输入2，查看保健品信息\n" +
                        "输入3，查看化妆品信息");
                article4.setDescription("");
                article4.setPicUrl("");
                article4.setUrl("");
                articleList.add(article4);

                newsMessage.setArticleCount(articleList.size());
                newsMessage.setArticles(articleList);
                respMessage = MessageUtil.newsMessageToXml(newsMessage);
            } else {*/

//                }
//                // ��ͼ����Ϣ---����ͼƬ
//                else if ("2".equals(content)) {
//                    Article article = new Article();
//                    article.setTitle("΢�Ź����ʺſ����̳�Java��");
//                    // ͼ����Ϣ�п���ʹ��QQ���顢��ű���
//                    article.setDescription("��壬80��" + emoji(0x1F6B9)
//                            + "��΢�Ź����ʺſ�������4���¡�Ϊ�����ѧ�����ţ����Ƴ���ϵ�����ؽ̳̣�Ҳϣ���˻����ʶ���ͬ�У�\n\nĿǰ���Ƴ��̳̹�12ƪ�������ӿ����á���Ϣ��װ����ܴ��QQ���鷢�͡���ű��鷢�͵ȡ�\n\n���ڻ��ƻ��Ƴ�һЩʵ�ù��ܵĿ������⣬���磺����Ԥ�����ܱ����������칦�ܵȡ�");
//                    // ��ͼƬ��Ϊ��
//                    article.setPicUrl("");
//                    article.setUrl("http://blog.csdn.net/lyq8479");
//                    articleList.add(article);
//                    newsMessage.setArticleCount(articleList.size());
//                    newsMessage.setArticles(articleList);
//                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
//                }
//                // ��ͼ����Ϣ
//                else if ("3".equals(content)) {
//                    Article article1 = new Article();
//                    article1.setTitle("΢�Ź����ʺſ����̳�\n����");
//                    article1.setDescription("");
//                    article1.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
//                    article1.setUrl("http://blog.csdn.net/lyq8479/article/details/8937622");
//
//                    Article article2 = new Article();
//                    article2.setTitle("��2ƪ\n΢�Ź����ʺŵ�����");
//                    article2.setDescription("");
//                    article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
//                    article2.setUrl("http://blog.csdn.net/lyq8479/article/details/8941577");
//
//                    Article article3 = new Article();
//                    article3.setTitle("��3ƪ\n����ģʽ���ü��ӿ�����");
//                    article3.setDescription("");
//                    article3.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
//                    article3.setUrl("http://blog.csdn.net/lyq8479/article/details/8944988");
//
//                    articleList.add(article1);
//                    articleList.add(article2);
//                    articleList.add(article3);
//                    newsMessage.setArticleCount(articleList.size());
//                    newsMessage.setArticles(articleList);
//                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
//                }
//                // ��ͼ����Ϣ---������Ϣ����ͼƬ
//                else if ("4".equals(content)) {
//                    Article article1 = new Article();
//                    article1.setTitle("΢�Ź����ʺſ����̳�Java��");
//                    article1.setDescription("");
//                    // ��ͼƬ��Ϊ��
//                    article1.setPicUrl("");
//                    article1.setUrl("http://blog.csdn.net/lyq8479");
//
//                    Article article2 = new Article();
//                    article2.setTitle("��4ƪ\n��Ϣ����Ϣ���?�ߵķ�װ");
//                    article2.setDescription("");
//                    article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
//                    article2.setUrl("http://blog.csdn.net/lyq8479/article/details/8949088");
//
//                    Article article3 = new Article();
//                    article3.setTitle("��5ƪ\n������Ϣ�Ľ�������Ӧ");
//                    article3.setDescription("");
//                    article3.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
//                    article3.setUrl("http://blog.csdn.net/lyq8479/article/details/8952173");
//
//                    Article article4 = new Article();
//                    article4.setTitle("��6ƪ\n�ı���Ϣ�����ݳ������ƽ���");
//                    article4.setDescription("");
//                    article4.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
//                    article4.setUrl("http://blog.csdn.net/lyq8479/article/details/8967824");
//
//                    articleList.add(article1);
//                    articleList.add(article2);
//                    articleList.add(article3);
//                    articleList.add(article4);
//                    newsMessage.setArticleCount(articleList.size());
//                    newsMessage.setArticles(articleList);
//                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
//                }
//                // ��ͼ����Ϣ---���һ����Ϣ����ͼƬ
//                else if ("5".equals(content)) {
//                    Article article1 = new Article();
//                    article1.setTitle("��7ƪ\n�ı���Ϣ�л��з��ʹ��");
//                    article1.setDescription("");
//                    article1.setPicUrl("http://0.xiaoqrobot.duapp.com/images/avatar_liufeng.jpg");
//                    article1.setUrl("http://blog.csdn.net/lyq8479/article/details/9141467");
//
//                    Article article2 = new Article();
//                    article2.setTitle("��8ƪ\n�ı���Ϣ��ʹ����ҳ������");
//                    article2.setDescription("");
//                    article2.setPicUrl("http://avatar.csdn.net/1/4/A/1_lyq8479.jpg");
//                    article2.setUrl("http://blog.csdn.net/lyq8479/article/details/9157455");
//
//                    Article article3 = new Article();
//                    article3.setTitle("���������¶������������ͨ������Ի��ע΢�Ź����ʺ�xiaoqrobot��֧����壡");
//                    article3.setDescription("");
//                    // ��ͼƬ��Ϊ��
//                    article3.setPicUrl("");
//                    article3.setUrl("http://blog.csdn.net/lyq8479");
//
//                    articleList.add(article1);
//                    articleList.add(article2);
//                    articleList.add(article3);
//                    newsMessage.setArticleCount(articleList.size());
//                    newsMessage.setArticles(articleList);
//                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
//                }

