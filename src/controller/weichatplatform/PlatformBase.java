package controller.weichatplatform;

/**
 * Created by kyle.yu on 15/05/2015.
 */
public class PlatformBase {
    /**
     *
     * @param hexEmoji
     * @return
     */
    public static String emoji(int hexEmoji) {
        return String.valueOf(Character.toChars(hexEmoji));
    }

}
