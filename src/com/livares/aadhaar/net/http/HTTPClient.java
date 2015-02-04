package com.livares.aadhaar.net.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.livares.aadhaar.pac.App;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

public class HTTPClient {

	private static final String TAG = "HTTPClient";
	private static final int CONNECTION_TIMEOUT = 8000;
	private static final int READ_TIMEOUT = 15000;
	private static final int UPLOAD_TIMEOUT = 240000;
	
	private static CookieStore cookieStore;
	
	static{
		cookieStore = new BasicCookieStore();
	}
	
	public static void enableCookiePersistence(Context context){
		PersistentCookieStore persistentStore = new PersistentCookieStore(context);
		if(cookieStore!=null){
			//Copy cookies from current cookie store
			List<Cookie> cookies = cookieStore.getCookies();
			for(Cookie cookie: cookies){
				persistentStore.addCookie(cookie);
			}
		}
		cookieStore = persistentStore;
	}
	
	public static void disableCookiePersistence(){
		BasicCookieStore basicStore = new BasicCookieStore();
		if(cookieStore!=null){
			//Copy cookies from current cookie store
			List<Cookie> cookies = cookieStore.getCookies();
			for(Cookie cookie: cookies){
				basicStore.addCookie(cookie);
			}
		}
		cookieStore = basicStore;
	}
	
	public static void clearCookies(){
		if(cookieStore!=null)
			cookieStore.clear();
	}
	
	public static void setCookieStore(CookieStore cookieStore) {
		HTTPClient.cookieStore = cookieStore;
	}
	
	// End of all static stuff
	
	public HTTPClient() {}
	
	public String get(String url){
		return get(url,null);
	}
	
	public String get(String url,Map<String, String> headers){
		Log.i("HTTP GET", url);
		Log.i("HTTP GET HEADERS", ""+headers);
			AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AadhaarPACAndroid");
		 	String result = null;
	        HttpParams myParams = httpClient.getParams();
	        HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(myParams, READ_TIMEOUT);
	        
	        HttpGet httpGet = new HttpGet(url);
	        if(headers!=null){
                java.util.Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
                while(iter.hasNext()){
                Entry<String, String> entry =  iter.next();  
                      String key = entry.getKey();
                    String value = entry.getValue();
                    httpGet.setHeader(key, value);  
              }
            }
	        try {
	    		// Create local HTTP context
	    	    HttpContext localContext = new BasicHttpContext();
	    	    // Bind custom cookie store to the local context
	    	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    	    
	            HttpResponse response = httpClient.execute(httpGet,localContext);
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != HttpStatus.SC_OK) {
	            	response.getEntity().consumeContent();
	                Log.w(TAG, "Status Not OK ("+statusCode+") on HTTP GET " + url);
	                return null;
	            }

	            result = readResponse(response);
	            Log.i("HTTP GET RESPONSE", result);
	            
	        }catch(IOException ex){
	            
	            httpGet.abort();
	            Log.w(TAG, "Error while in HTTP GET " + url, ex);

	        } finally {
	            if(httpClient!=null)
	            	httpClient.close();
	        }
	        return result;
	 }
	
	 public String post(String url,Map<String, String> data){
		 return post(url, data,null);
	 }
	 
	 public String post(String url,Map<String, String> data,Map<String, String> headers){
			Log.i("HTTP POST", url);
			if(App.DEVELOPMENT){
				url+="?XDEBUG_SESSION_START="+App.APP_ID;
			}
			Log.i("HTTP POST HEADERS", ""+headers);
			Log.i("HTTP POST BODY", ""+data);
		 	AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AadhaarPACAndroid");
	        String result = null;
	        
	        HttpParams myParams = httpClient.getParams();
	        HttpConnectionParams.setConnectionTimeout(myParams, CONNECTION_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(myParams, READ_TIMEOUT);
	        
	        HttpPost httpPost = new HttpPost(url);
	        
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        try{
	              if(headers!=null){
	                  java.util.Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
	                  while(iter.hasNext()){
	                  Entry<String, String> entry =  iter.next();  
	                        String key = entry.getKey();
	                      String value = entry.getValue();
	                      httpPost.setHeader(key, value);  
	                }
	              }
	              if(data!=null){
	                  java.util.Iterator<Entry<String, String>> iter = data.entrySet().iterator();
	                  while(iter.hasNext()){
	                  Entry<String, String> entry =  iter.next();  
	                        String key = entry.getKey();
	                      String value = entry.getValue();
	                      nameValuePairs.add(new BasicNameValuePair(key,value));  
	                }
	                if(App.DEVELOPMENT){
	                	nameValuePairs.add(new BasicNameValuePair("XDEBUG_SESSION_START", App.APP_ID));
	                }
	                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	              }
	    		// Create local HTTP context
	    	    HttpContext localContext = new BasicHttpContext();
	    	    // Bind custom cookie store to the local context
	    	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	              
	            HttpResponse response = httpClient.execute(httpPost,localContext);
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != HttpStatus.SC_OK) {
	            	//result = readResponse(response);
	            	response.getEntity().consumeContent();
	                Log.w(TAG, "Status Not OK ("+statusCode+") on HTTP POST " + url);
	                return null;
	            }

	            result = readResponse(response);
	            Log.i("HTTP POST RESPONSE", result);
	        }catch(IOException ex){
	            
	            httpPost.abort();
	            Log.w(TAG, "Error while in HTTP POST " + url, ex);
	            
	        } finally {
	            if (httpClient != null) {
	                httpClient.close();
	            }
	        }
	        
	        return result;
	 }
	
	 public JSONObject postForJson(String url,Map<String, String> data){
		return postForJson(url, data, null);
	 }
	 
	 public JSONObject postForJson(String url,Map<String, String> data,Map<String, String> headers){
	        
	        String result = null;
	        JSONObject jObj = null;
            result = post(url, data, headers);
            if(result!=null){
	            try {
	    			jObj = new JSONObject(result);
	    		} catch (JSONException e) {
	    			Log.e("JSON Parser", "Error while parsing http response as json");
	    			Log.e("JSON Parser", e.getMessage());
	    		}
            }
	        return jObj;
	        
	 }
	 public JSONArray getForJson(String url){
			return getForJson(url, null);
		 }
	 public JSONArray getForJson(String url,Map<String, String> headers){
	        
	        String result = null;
	        JSONArray jarray= null;
	        result = get(url,headers);
            if(result!=null){
	            try {
	            	result=result.trim();
	            	if(result.startsWith("[")){
	            		jarray = new JSONArray(result);				  		
	            	}
	            	else
	            	{
	            		result="[".concat(result).concat("]");
	            		jarray = new JSONArray(result);	
	            	}
	            	
	    		} catch (JSONException e) {
	    			Log.e("JSON Parser", "Error while parsing http response as json");
	    			Log.e("JSON Parser", e.getMessage());
	    		}
	        }
	        return jarray;
	        
	 }
	 
	 
	  private String readResponse(HttpResponse response) throws IOException
	  {
	        HttpEntity responseEntity = response.getEntity();
	        if (responseEntity != null) {

	            InputStream is = null;
	            try{
	                is = responseEntity.getContent();
	                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
	                StringBuilder stringBuilder = new StringBuilder();
	                String line = null;
	                while ((line = reader.readLine()) != null) {
	                    stringBuilder.append(line);
	                }
	                //If everything goes well, we return the response text as a String
	                return stringBuilder.toString();
	            } finally {
	                if (is != null) {
	                    is.close();
	                }
	                responseEntity.consumeContent();
	            }
	        
	        }
	        //If things didn't work out well we must return null
	        return null;
	  }
}
