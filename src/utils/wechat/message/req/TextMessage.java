package utils.wechat.message.req;

/**
 * Created by yplyf on 2015/4/8.
 */
public class TextMessage extends BaseMessage {
    // 消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}