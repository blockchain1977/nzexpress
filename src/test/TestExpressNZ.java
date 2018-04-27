package test;

import controller.weichatplatform.expressnz.ExpressNZ;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kyle.yu on 5/10/2015.
 */
public class TestExpressNZ {
    String testData1 = "{MsgId=6201998149330234614, FromUserName=oACuus2eZlcF2GyOl05ww_yi766o, CreateTime=1444015221, Content=aaa, ToUserName=gh_aa962dbb9cb8, MsgType=text}";
    String exprectedResult1 = "<xml>\n" +
            "  <ToUserName><![CDATA[oACuus2eZlcF2GyOl05ww_yi766o]]></ToUserName>\n" +
            "  <FromUserName><![CDATA[gh_aa962dbb9cb8]]></FromUserName>\n" +
            "  <CreateTime><![CDATA[1444015234665]]></CreateTime>\n" +
            "  <MsgType><![CDATA[text]]></MsgType>\n" +
            "  <FuncFlag><![CDATA[0]]></FuncFlag>\n" +
            "  <Content><![CDATA[单号 aaa 的查询结果：\n" +
            "==========\n" +
            "无效的快递单号\n" +
            "]]></Content>\n" +
            "</xml>\n";

        boolean testCase1(String input) {
            Map<String, String> requestMap = new HashMap<>();
            String result = ExpressNZ.processRequest(requestMap);
            return exprectedResult1.equalsIgnoreCase(result);
        }

}
