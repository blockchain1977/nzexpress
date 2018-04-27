package utils.kuaidi;
  
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class KUAIDI100 {  
    public static void main(String[] args) {  
    	
    	String company = "yunda";
    	String orderNumber = "3100362333842";
    	
    	checkOrderStatus(company, orderNumber);  
          
    }
    
    private static String formatOrderStatus(String rawStatus) {
    	String formatedStatus = "";

        try {
            JSONObject jObj = new JSONObject(rawStatus);
            JSONArray jArray = jObj.getJSONArray("data");
            for (int i = 0; i < jArray.length(); i++) {
                String time = "";
                String context = "";

                JSONObject deliverRecord = jArray.getJSONObject(i);
                time = deliverRecord.getString("time");
                context = deliverRecord.getString("context");

                formatedStatus = time + " : " + context + "\n" + formatedStatus;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return formatedStatus;
        }
    	
    	return formatedStatus;
    }

	public static String checkOrderStatus(String company, String orderNumber) {
		String resultStatus = "";

        orderNumber = orderNumber.replaceAll("[^\\w]", "");
		
		String url = "http://www.kuaidi100.com/query?type=" + company + "&postid=" + orderNumber + "&id=1&valicode=&temp=0.3015635129995644";  
          
        try {  
            HttpURLConnection.setFollowRedirects(true);  
            HttpURLConnection http = (HttpURLConnection) (new URL(url).openConnection());  
            http.setDoOutput(true);  
            http.setDoOutput(true);  
            http.setInstanceFollowRedirects(true);  
            http.setRequestMethod("GET");  
            http.setRequestProperty("Connection", "keep-alive");  
            http.setRequestProperty("X-Requested-With", "XMLHttpRequest");  
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.92 Safari/537.1 LBBROWSER");  
            http.setRequestProperty("Accept", "*/*");  
            http.setRequestProperty("Referer", "http://www.kuaidi100.com/");  
            http.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");  
            http.setRequestProperty("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");  
            http.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");  
              
            System.out.println("response  is : "+http.getResponseCode()+" "+http.getResponseMessage());  
            String contentEncoding = http.getContentEncoding();  
            System.out.println("response  encoding is : "+contentEncoding);  
            InputStream  in = null;  
            if("gzip".equalsIgnoreCase(contentEncoding)){  
                in = new GZIPInputStream(http.getInputStream());   
            }else{  
                in = http.getInputStream();  
            }  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            int data = -1;  
            while((data = in.read())!=-1){  
                baos.write(data);  
            }  
            resultStatus = baos.toString("utf8");  

        } catch (MalformedURLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        
//        System.out.println("beforeFormat: " + resultStatus);
        resultStatus = formatOrderStatus(resultStatus);
        
        return resultStatus;
	}  
}  