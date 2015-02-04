package com.livares.aadhaar.webapi;

import org.json.JSONObject;
import android.os.AsyncTask;
import com.livares.aadhaar.net.http.HTTPClient;

public class WebServiceTask extends AsyncTask<WebServiceRequest, Integer, WebServiceResult>{

	private HTTPClient http;
	
	private ResponseCallBack callback=new ResponseCallBack() {
		//Empty implementation
		@Override
		public void onSuccess(JSONObject response) {}
	};
	
	public ResponseCallBack getCallback() {
		return callback;
	}
	
	public void setCallback(ResponseCallBack callback) {
		this.callback = callback;
	}
	
	public WebServiceTask() {
		http=new HTTPClient();
	}

	@Override
	protected WebServiceResult doInBackground(WebServiceRequest... params) {
		WebServiceRequest request = params[0];
		String result = null;
		
		if(request.getMethod()==WebServiceRequest.POST){
			result = http.post(request.getUrl(), request.getData(), request.getHeaders());
		}else{
			result = http.get(request.getUrl(), request.getHeaders());
		}
		return new WebServiceResult(result);
	}
	
	@Override
	protected void onPostExecute(WebServiceResult result) {
		if(result.isSuccess()){
			fireSuccessCallBack(result.getResponseJson());
		}else{
			fireFailCallBack(result.getResponseText(), result.getException());
		}
	}
	
	private void fireSuccessCallBack(JSONObject result){
		if(callback!=null){
			callback.onSuccess(result);
		}
	}

	private void fireFailCallBack(String response,Exception e){
		if(callback!=null){
			callback.onFail(response, e);
		}
	}
	
}