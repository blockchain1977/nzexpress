package utils.wechat.message.resp;

import java.io.UnsupportedEncodingException;

/**
 * Created by yplyf on 2015/4/8.
 */
public class TextMessage extends BaseMessage {
    // 回复的消息内容
    private String Content;
    public int MAX_LENGTH = 2030;

    public String getContent() {
        return Content;
    }

    public static int getByteSize(String content) {
        int size = 0;
        if (null != content) {
            try {
                // 汉字采用utf-8编码时占3个字节
                size = content.getBytes("utf-8").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    public void setContent(String content) {
        int size = getByteSize(content);
        if (size > MAX_LENGTH) {
            String prefix = "此消息过长，只显示后面的最新信息：\n";
            int sizePrefix = getByteSize(prefix);
            int index = size - MAX_LENGTH + sizePrefix;
            content = prefix + content.substring(index);
        }
        Content = content;
    }

    public static void main(String args[]) {
        String aa = "此消息过长，只显示后面的最新信息：\n";
        System.out.println(getByteSize(aa));
    }
}