package utils;

/**
 * Created by yplyf on 2015/4/11.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import utils.StringUtil;

/**
 * Created on 14/11/15.
 */
public class HttpUtil {

    private static Logger logger = Logger.getLogger(HttpUtil.class);

    private static HttpUtil instance = new HttpUtil();

    private PoolingHttpClientConnectionManager connManager = null;

    private HttpUtil() {
        connManager = new PoolingHttpClientConnectionManager();
    }

    public static HttpUtil getInstance() {
        return instance;
    }

    public String issueGetRequest(String url, List<String> params, Header[] headers) {
        logger.debug("Issue Get Reqeust");
        //as we use multithread for request, we get client from multithread pool when needed
        CloseableHttpClient client = HttpClients.custom().setConnectionManager
                (connManager).build();

        if (params != null && params.size() != 0) {
            url = url + "?" + params.get(0);
            for (int i = 1; i < params.size(); i++) {
                url += "&" + params.get(i);
            }
        }

        HttpGet httpGet = new HttpGet(url);

        if (headers != null) {
            for (Header header : headers) {
                httpGet.addHeader(header);
            }
        }

        CloseableHttpResponse response = null;
        String retString = null;
        try {
            response = client.execute(httpGet);
            retString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpGet.releaseConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return retString;
    }

    public String issuePostRequest(String url, List<Header> headers, Map<String, String> bodyMap) {
        logger.debug("Issue Post Reqeust");
        //as we use multithread for request, we get client from multithread pool when needed
        CloseableHttpClient client = HttpClients.custom().setConnectionManager
                (connManager).build();

        HttpPost httpPost = new HttpPost(url);
//        logger.info("Post Request to: " + url);
//        logger.debug("*************Request Headers*************");
        if (headers != null) {
            for (Header header : headers) {
                httpPost.addHeader(header);
//                logger.debug(header.getName() + " : " + header.getValue());
            }
        }
//        logger.debug("*************End of Request Headers*************");

        StringEntity entity = null;
        try {
            String entityString = StringUtil.convertMapToSortedBody(bodyMap);
            entity = new StringEntity(entityString);
//            logger.info("Request Body:");
//            logger.info(EntityUtils.toString(entity));
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(entity);
        for (Header header : httpPost.getAllHeaders()) {
//            logger.info(header.getName() + " : " + header.getValue());
        }

        CloseableHttpResponse response = null;
        String retString = null;
        try {
            response = client.execute(httpPost);
//            logger.debug("*************Response Headers*************");
            for (Header header : response.getAllHeaders()) {
//                logger.debug(header.getName() + " : " + header.getValue());
            }
//            logger.debug("*************End of Response Headers*************");
            retString = EntityUtils.toString(response.getEntity());
//            logger.info(url + " Response string: " + retString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpPost.releaseConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retString;
    }

    public static void main(String[] args) {
        String abc = "abc=342&dbc=233&";
        logger.info(abc.indexOf("&"));
        logger.info(abc.lastIndexOf("&"));
        logger.info(abc.length() - 1);
        if (abc.lastIndexOf("&") == abc.length() - 1) {
            logger.info("here");
            abc = abc.substring(0, abc.length() - 1);
        }
        logger.info(abc);
    }

}
