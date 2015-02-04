package com.livares.aadhaar.webapi.data;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.livares.aadhaar.pac.App;
import com.livares.aadhaar.pac.model.ContactAddress;
import com.livares.aadhaar.webapi.ResponseCallBack;
import com.livares.aadhaar.webapi.WebServiceRequest;
import com.livares.aadhaar.webapi.WebServiceTask;

public class ContactAddressData extends WebData<ContactAddress>{
	
	private static ContactAddress address=new ContactAddress();
	
	public ContactAddressData() {}
	
	public static ContactAddress getLoadedContactAddress() {
		return address;
	}
	
	public void loadAddress(String address_code){
		String addressUrl = App.baseUrl()+"address";
		WebServiceTask apiCall = new WebServiceTask();
		WebServiceRequest request = WebServiceRequest.toGet(addressUrl, null);
		apiCall.setCallback(new ResponseCallBack() {
			
			@Override
			public void onSuccess(JSONObject response) {
				address = null;
				try {
					JSONObject json = response.getJSONObject("response");
					ContactAddress address = new ContactAddress();
					address.setCode(json.getString("code"));
					address.setName(json.getString("name"));
					address.setCareOf(json.getString("care_of"));
					address.setBuilding(json.getString("building"));
					address.setLandmark(json.getString("landmark"));
					address.setStreet(json.getString("street"));
					address.setLocality(json.getString("locality"));
					address.setPo_name(json.getString("po_name"));
					address.setRegion(json.getString("region"));
					address.setDistrict(json.getString("district"));
					address.setState(json.getString("state"));
					address.setPincode(json.getString("pincode"));
				} catch (JSONException e) {
					Log.e("Load Contact", e.getMessage());
				}
				if(dataListener!=null){
					dataListener.onDataLoaded(address);
				}
			}
			
			@Override
			public void onFail(String response, Exception e) {
				super.onFail(response, e);
				if(dataListener!=null){
					dataListener.onDataLoaded(null);
				}
			}
		});
		apiCall.execute(request);
	}
	
	public void save(final ContactAddress contactAddress){
		String addressUrl = App.baseUrl()+"address";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("code", contactAddress.getCode());
		data.put("name", contactAddress.getName());
		data.put("care_of", contactAddress.getName());
		data.put("building", contactAddress.getName());
		data.put("landmark", contactAddress.getName());
		data.put("street", contactAddress.getName());
		data.put("locality", contactAddress.getName());
		data.put("po_name", contactAddress.getName());
		data.put("region", contactAddress.getName());
		data.put("district", contactAddress.getName());
		data.put("state", contactAddress.getName());
		data.put("pincode", contactAddress.getName());
		WebServiceTask apiCall = new WebServiceTask();
		WebServiceRequest request = WebServiceRequest.toPost(addressUrl, null, data);
		apiCall.setCallback(new ResponseCallBack() {
			
			@Override
			public void onSuccess(JSONObject response) {
				try {
					JSONObject json = response.getJSONObject("response");
					if(json!=null){
						//long sync_id = json.getLong("id");
						//contactAddress.setUpdated(0);
						
						if(dataListener!=null){
							dataListener.onDataSaved(contactAddress);
						}
						return;
					}
				} catch (JSONException e) {
					Log.e("Save Contact", "ContactAddress"+contactAddress.getName()+":"+e.getMessage());
				}
				if(dataListener!=null){
					dataListener.onDataSaved(null);
				}
			}
		});
		apiCall.execute(request);
	}

}
