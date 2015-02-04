package com.livares.aadhaar.pac;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.livares.aadhaar.pac.model.ContactAddress;
import com.livares.aadhaar.webapi.ResponseCallBack;
import com.livares.aadhaar.webapi.WebServiceRequest;
import com.livares.aadhaar.webapi.WebServiceTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ManageAddressActivity extends Activity {

	private Button btn_update;
	private TextView lbl_error_message;
	private EditText in_name;
	private EditText in_building;
	private EditText in_street;
	private EditText in_region;
	private EditText in_po_name;
	private EditText in_district;
	private EditText in_state;
	private EditText in_pincode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_address);
		getActionBar().setTitle(R.string.title_activity_permanent_contact_register);
		lbl_error_message = (TextView)findViewById(R.id.lbl_error_message);
		
		in_name = (EditText)findViewById(R.id.in_name);
		in_building = (EditText)findViewById(R.id.in_building);
		in_street = (EditText)findViewById(R.id.in_street);
		in_po_name = (EditText)findViewById(R.id.in_po_name);
		in_region = (EditText)findViewById(R.id.in_region);
		in_district = (EditText)findViewById(R.id.in_district);
		in_state = (EditText)findViewById(R.id.in_state);
		in_pincode = (EditText)findViewById(R.id.in_pincode);
		
		btn_update = (Button) findViewById(R.id.btn_update);
		btn_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateAddress();
				
			}
		});
		loadAddress();
	}

	private void loadAddress(){
		lbl_error_message.setVisibility(View.GONE);
		in_name.setText("");
		in_building.setText("");
		in_street.setText("");
		in_po_name.setText("");
		in_region.setText("");
		in_district.setText("");
		in_state.setText("");
		in_pincode.setText("");
		String addressUrl = App.baseUrl()+"getaddress";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("contact_code", App.ContactCode);
		data.put("session_id",  App.SessionToken);
		String query="";
		if(data!=null){
            java.util.Iterator<Entry<String, String>> iter = data.entrySet().iterator();
            while(iter.hasNext()){
            Entry<String, String> entry =  iter.next();  
                  String key = entry.getKey();
                String value = entry.getValue();
                if(!query.isEmpty()){
                	query+="&";
                }
                query+=key+"="+value;
          }
          addressUrl+="?"+query;
        }
		
		WebServiceTask apiCall = new WebServiceTask();
		WebServiceRequest request = WebServiceRequest.toGet(addressUrl, null);
		apiCall.setCallback(new ResponseCallBack() {
			
			@Override
			public void onSuccess(JSONObject response) {
				try {
					JSONObject json = response.getJSONObject("response");
					if(json!=null){
						int status = json.getInt("status");
						if(status==1){
							in_name.setText(json.getString("name"));
							in_building.setText(json.getString("building"));
							in_street.setText(json.getString("street"));
							in_po_name.setText(json.getString("po_name"));
							in_region.setText(json.getString("region"));
							in_district.setText(json.getString("district"));
							in_state.setText(json.getString("state"));
							in_pincode.setText(json.getString("pincode"));
						}else{
							lbl_error_message.setVisibility(View.VISIBLE);
							String message = json.getString("message");
							if(message!=null){
								lbl_error_message.setText(message);
							}else{
								lbl_error_message.setText("Sorry, some error caused failure in loading");
							}
						}
					}
				} catch (JSONException e) {
					Log.e("Loading Address", e.getMessage());
				}
			}
			@Override
			public void onFail(String response, Exception e) {
				lbl_error_message.setVisibility(View.VISIBLE);
				lbl_error_message.setText("Sorry, some error caused failure in registration");
				super.onFail(response, e);
			}
		});
		apiCall.execute(request);
	}
	
	private void updateAddress(){
		lbl_error_message.setVisibility(View.GONE);
		String addressUrl = App.baseUrl()+"putaddress";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("contact_code", App.ContactCode);
		data.put("session_id",  App.SessionToken);
		data.put("name",  in_name.getText().toString());
		data.put("building",  in_building.getText().toString());
		data.put("street",  in_street.getText().toString());
		data.put("po_name",  in_po_name.getText().toString());
		data.put("region",  in_region.getText().toString());
		data.put("district",  in_district.getText().toString());
		data.put("state",  in_state.getText().toString());
		data.put("pincode",  in_pincode.getText().toString());
		WebServiceTask apiCall = new WebServiceTask();
		WebServiceRequest request = WebServiceRequest.toPost(addressUrl, null, data);
		apiCall.setCallback(new ResponseCallBack() {
			
			@Override
			public void onSuccess(JSONObject response) {
				try {
					JSONObject json = response.getJSONObject("response");
					if(json!=null){
						int status = json.getInt("status");
						if(status==1){
							lbl_error_message.setVisibility(View.VISIBLE);
							lbl_error_message.setText("Contact address updated successfully");
						}else{
							lbl_error_message.setVisibility(View.VISIBLE);
							String message = json.getString("message");
							if(message!=null){
								lbl_error_message.setText(message);
							}else{
								lbl_error_message.setText("Sorry, some error caused failure in registration");
							}
						}
					}
				} catch (JSONException e) {
					Log.e("Update Address", e.getMessage());
				}
			}
			@Override
			public void onFail(String response, Exception e) {
				lbl_error_message.setVisibility(View.VISIBLE);
				lbl_error_message.setText("Sorry, some error caused failure in registration");
				super.onFail(response, e);
			}
		});
		apiCall.execute(request);
	}
	
}
