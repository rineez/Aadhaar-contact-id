package com.livares.aadhaar.webapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebServiceResult {

	private String responseText;
	private JSONObject responseJson;
	private boolean success;
	private Exception exception;
	
	public WebServiceResult(String responseText) {
		this.responseText=responseText;
		if(this.responseText==null){
			Exception exception = new Exception("Server failure: Null response returned from web service");
			this.exception = exception;
			this.success=false;
			return;
		}
		try {
			this.responseText=this.responseText.trim();
			Object response = null;
        	if(this.responseText.startsWith("[")){
        		response = new JSONArray(this.responseText);
        	}
        	else if(this.responseText.startsWith("{")){
        		response = new JSONObject(this.responseText);
        	}else{
        		//Response is simple string or number
        		try{
        			response = Double.parseDouble(this.responseText);
        		}catch(NumberFormatException nfe){
        			response = this.responseText;
        		}
        	}
    		this.responseJson = new JSONObject();
    		this.responseJson.put("response", response);
			this.success=true;
		} catch (JSONException je) {
			exception=je;
			this.success=false;
		}
		
	}
	
	public WebServiceResult(String responseText,Exception exception) {
		this.responseText=responseText;
		this.exception=exception;
		success=false;
	}
	
	public WebServiceResult(JSONObject responseJson) {
		this.responseText=responseJson.toString();
		this.responseJson=responseJson;
		success=true;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public JSONObject getResponseJson() {
		return responseJson;
	}

	public void setResponseJson(JSONObject responseJson) {
		this.responseJson = responseJson;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
}
