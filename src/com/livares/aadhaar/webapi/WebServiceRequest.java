package com.livares.aadhaar.webapi;

import java.util.Map;

public class WebServiceRequest {

	public static final int GET=0;
	public static final int POST=1;
	
	private int method=GET;
	private String url;
	private Map<String,String> data;
	private Map<String,String> headers;
	
	public static WebServiceRequest toGet(String url,Map<String,String> headers){
		return new WebServiceRequest(url, headers, null); 
	}
	public static WebServiceRequest toPost(String url,Map<String,String> headers, Map<String,String> data){
		return new WebServiceRequest(url, headers, data).setMethod(POST); 
	}
	
	public WebServiceRequest(String url) {
		this(url,null, null);
	}
	
	public WebServiceRequest(String url,Map<String,String> headers) {
		this(url,headers,null);
	}
	
	public WebServiceRequest(String url,Map<String,String> headers, Map<String,String> data) {
		this.url=url;
		this.headers=headers;
		this.data=data;
	}

	public int getMethod() {
		return method;
	}

	public WebServiceRequest setMethod(int method) {
		this.method = method;
		return this;
	}

	public Map<String, String> getData() {
		return data;
	}

	public WebServiceRequest setData(Map<String, String> data) {
		this.data = data;
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public WebServiceRequest setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public WebServiceRequest setUrl(String url) {
		this.url = url;
		return this;
	}
	
	
	
}
