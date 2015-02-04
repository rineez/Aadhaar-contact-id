package com.livares.aadhaar.webapi;

import org.json.JSONObject;

import android.util.Log;

public abstract class ResponseCallBack {

	private static final String TAG = ResponseCallBack.class.getName();
	
	abstract public void onSuccess(JSONObject response);
	
	public void onFail(String response,Exception e){
		Log.d(TAG, "Exception in Webservice Request",e);
		Log.d(TAG, "Exception in Webservice Request:"+response);
	}
	
}
