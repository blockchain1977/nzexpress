package utils;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created on 14/11/15.
 */
public class StringUtil {
    public static String convertMapToSortedBody(Map<String, String> bodyCont) {
        //sort the keys
        SortedSet<String> keys = new TreeSet<>(bodyCont.keySet());
        StringBuffer buffer = new StringBuffer();
        for (String key : keys) {
            String value = bodyCont.get(key);
            buffer.append(key);
            buffer.append("=");
            buffer.append(value);
            buffer.append("&");
        }
        String result = buffer.toString();
        if (result.lastIndexOf("&") == result.length() - 1) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
