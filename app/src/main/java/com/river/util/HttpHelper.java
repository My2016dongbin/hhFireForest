package com.river.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;



import android.R.integer;
import android.R.string;
import android.os.Environment;

public class HttpHelper {
	
	public static String invokePost(String API_URL, String action, String filePath, Map<String, String> params) {
        try {
           
        	String url = API_URL + action + "/";
            //Log.d(TAG, "url is" + url);             
            HttpPost httpPost = new HttpPost(url);            
            File file = new File(filePath);                
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));
            FileBody fb = new FileBody(file);
            reqEntity.addPart("myFile", fb);  
            if(params != null){  
                Iterator<String> it = params.keySet().iterator();  
                while(it.hasNext()){  
                    String name = it.next();  
                    reqEntity.addPart(name, new StringBody(params.get(name),Charset.forName("UTF-8")));  
                }  
            }
            httpPost.setEntity(reqEntity);  
            return invoke(httpPost);
        } catch (Exception e) {
           // Log.e(TAG, e.toString());
        }
        return null;
    }
	
	
	public static String invokePost1(String API_URL, String action, String filePath, Map<String, String> params,String lsm) {
        try {
           
        	String url = API_URL + action + "/";
            //Log.d(TAG, "url is" + url);             
            HttpPost httpPost = new HttpPost(url);            
           
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));
            
            
//            File[] files = file.listFiles();
//            Integer index=1;
//            for (File file1:files) {
//            	 FileBody fb = new FileBody(file1);
//                 reqEntity.addPart("myFile", fb);  
//                 index++;
//            	//list.add("http://www.test.com/img/"+img.getName());
//            }
//            FileBody fb = new FileBody(file);
//            reqEntity.addPart("myFile", fb);  
//            
//            FileBody fb1 = new FileBody(file1);
//            reqEntity.addPart("myFile1", fb1);  
            
         String tempString=filePath;
            android.util.Log.e("uploadImage", "invokePost1: uploadImage-"+tempString );
         tempString=tempString.substring(0,tempString.length()-1);
         int index=tempString.lastIndexOf("/");
         tempString=tempString.substring(0,index);
//                    FileOutputStream fos1= new FileOutputStream(new File(Environment.getExternalStorageDirectory()  
//                            + "/szd/"+lsm+".zip"));
            android.util.Log.e("uploadImage", "invokePost1: uploadImage--"+tempString );
         
         FileOutputStream fos1= new FileOutputStream(new File(tempString+"/"+lsm+".zip"));
                 ZipUtil.toZip(filePath, fos1,true);
                 
//                 File aFile=new File(Environment.getExternalStorageDirectory()  
//                            + "/szd/"+lsm+".zip");
                 File aFile=new File(tempString+"/"+lsm+".zip");
                 
                 FileBody fb = new FileBody(aFile);
                 reqEntity.addPart("myFile", fb);  
          
            
            
            
           
            if(params != null){  
                Iterator<String> it = params.keySet().iterator();  
                while(it.hasNext()){  
                    String name = it.next();  
                    reqEntity.addPart(name, new StringBody(params.get(name),Charset.forName("UTF-8")));  
                }  
            }
            httpPost.setEntity(reqEntity);  
            return invoke(httpPost);
        } catch (Exception e) {
           // Log.e(TAG, e.toString());
        }
        return null;
    }
	
	
	
	
	public static String invoke1(String API_URL, String action,List<NameValuePair> params)
	{
		String url = API_URL + action + "/";
        //Log.d(TAG, "url is" + url);             
        HttpPost httpPost = new HttpPost(url);    
        
        
        
        httpPost.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
      //  httpPost.setHeader("Accept", "application/json");
        // 创建form表单对象
        UrlEncodedFormEntity formEntity;
		try {
			formEntity = new UrlEncodedFormEntity(params, "utf-8");
			   //formEntity.setContentType("Content-Type:application/json");  
		        
		       
		        httpPost.setEntity(formEntity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
        
   
       
        return invoke(httpPost);
	}
	
	public static String invoke(HttpPost httpPost)
	{
		String result="";
		HttpClient httpClient = new DefaultHttpClient();
		try {
			
		HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{
			result =EntityUtils.toString(httpResponse.getEntity());
			} 
			else 
			{
						result = "请求失败";
		    }
			//Log.e(TAG, "result := " + result);
			} 
		catch (Exception e) {
			e.printStackTrace();
			}
			
		return result;
	}
	


}
