package com.livares.aadhaar.pac;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.khoslalabs.ac.capture.model.auth.AuthCaptureData;
import com.khoslalabs.ac.capture.model.auth.AuthCaptureRequest;
import com.khoslalabs.ac.capture.model.common.Location;
import com.khoslalabs.ac.capture.model.common.LocationType;
import com.khoslalabs.ac.capture.model.common.request.CertificateType;
import com.khoslalabs.ac.capture.model.common.request.Modality;
import com.khoslalabs.ac.capture.model.common.request.ModalityType;
import com.khoslalabs.ac.gateway.model.AuthResponse;
import com.livares.aadhaar.webapi.ResponseCallBack;
import com.livares.aadhaar.webapi.WebServiceRequest;
import com.livares.aadhaar.webapi.WebServiceTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	public static final int QRCODE_REQUEST = 1000;
	public static final int AADHAAR_CONNECT_AUTH_REQUEST = 1001;
	public static final String TEAM_ID = "hackteam13";

	private EditText in_aadhaar_number;
	private ImageView qrCodeScanner;
	
	private TextView lbl_error_message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		in_aadhaar_number = (EditText)findViewById(R.id.in_aadhaar_number);
		lbl_error_message = (TextView)findViewById(R.id.lbl_error_message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void scanUsingQRCode(View v) {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		try {
			startActivityForResult(intent, QRCODE_REQUEST);
		} catch (Exception e) {
			showToast("No QR Code scanning modules found.", Toast.LENGTH_LONG);
		}
	}

	public void authenticate(View v) {
		if (TextUtils.isEmpty(in_aadhaar_number.getText())) {
			showToast(
					"Invalid Aadhaar Number. Please enter a valid Aadhaar Number",
					Toast.LENGTH_LONG);
			return;
		}

		
		AuthCaptureRequest authCaptureRequest = new AuthCaptureRequest();
		authCaptureRequest.setAadhaar(in_aadhaar_number.getText().toString());
		authCaptureRequest.setModality(Modality.biometric);
		authCaptureRequest.setModalityType(ModalityType.fp);
		authCaptureRequest.setNumOffingersToCapture(1);
		authCaptureRequest.setCertificateType(CertificateType.preprod);

		Location loc = new Location();
		loc.setType(LocationType.pincode);
		loc.setPincode("560076");
		authCaptureRequest.setLocation(loc);

		Intent i = new Intent();
		i = new Intent("com.khoslalabs.ac.action.AUTHCAPTURE");
		i.putExtra("REQUEST", new Gson().toJson(authCaptureRequest));
		try {
			startActivityForResult(i, AADHAAR_CONNECT_AUTH_REQUEST);
		} catch (Exception e) {
			Log.e("ERROR", e.getMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == QRCODE_REQUEST && resultCode == RESULT_OK
				&& data != null) {
			String contents = data.getStringExtra("SCAN_RESULT");
			if (!TextUtils.isEmpty(contents)) {
				String aadhaar = readValue(contents, "uid");
				in_aadhaar_number.setText(aadhaar);
				qrCodeScanner.setImageResource(R.drawable.qrcode_green);
			} else {
				qrCodeScanner.setImageResource(R.drawable.qrcode_gray);
			}
			return;
		}

		if (resultCode == RESULT_OK
				&& requestCode == AADHAAR_CONNECT_AUTH_REQUEST && data != null) {
			lbl_error_message.setVisibility(View.GONE);
			lbl_error_message.setText("");
			String responseStr = data.getStringExtra("RESPONSE");
			final AuthCaptureData authCaptureData = new Gson().fromJson(
					responseStr, AuthCaptureData.class);
			AadhaarAuthAsyncTask authAsyncTask = new AadhaarAuthAsyncTask(this,authCaptureData){
				@Override
				protected void onPostExecute(AuthResponse response) {
					loginHandler(response);
					
					super.onPostExecute(response);
				}
			};
			authAsyncTask.execute("https://ac.khoslalabs.com/hackgate/" + TEAM_ID + "/auth");
			
			return;
		}
	}

	// HELPER METHODS
	private String readValue(String contents, String dataName) {
		String[] keys;
		if (dataName.contains(",")) {
			keys = dataName.split(",");
		} else {
			keys = new String[] { dataName };
		}
		String value = "";
		for (String key : keys) {
			int startIndex = contents.indexOf(key + "=");
			if (startIndex >= 0) {
				int endIndex = contents.indexOf("\"", startIndex + key.length()
						+ 1 + 1);
				if (endIndex >= 0) {
					value += " ";
					value += contents.substring(startIndex + key.length() + 1,
							endIndex).replaceAll("\"", "");
				}
			}
		}
		return value.trim();
	}

	private void showToast(String text, int duration) {
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}
	
	private void loginHandler(final AuthResponse aadharResponse){
		String loginUrl = App.baseUrl()+"login";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("aadhaar", in_aadhaar_number.getText().toString());
		data.put("aadhaar_ref",  aadharResponse.getReferenceCode());
		data.put("status",  aadharResponse.isSuccess()?"1":"0");
		WebServiceTask apiCall = new WebServiceTask();
		WebServiceRequest request = WebServiceRequest.toPost(loginUrl, null, data);
		apiCall.setCallback(new ResponseCallBack() {
			
			@Override
			public void onSuccess(JSONObject response) {
				try {
					JSONObject json = response.getJSONObject("response");
					if(json!=null){
						int status = json.getInt("status");
						if(aadharResponse.isSuccess()){
							if(status==1){
								App.SessionToken = json.getString("session_id");
								App.ContactCode = json.getString("contact_code");
								Intent manageIntent = new Intent(LoginActivity.this, ManageAddressActivity.class);
								startActivity(manageIntent);
							}else{
								lbl_error_message.setVisibility(View.VISIBLE);
								lbl_error_message.setText("Sorry, Some error caused failure in login.");
							}
						}else{
							lbl_error_message.setVisibility(View.VISIBLE);
							lbl_error_message.setText("Login Failed!");
						}
					}
				} catch (JSONException e) {
					Log.e("Login Response", e.getMessage());
				}
			}
			@Override
			public void onFail(String response, Exception e) {
				lbl_error_message.setVisibility(View.VISIBLE);
				lbl_error_message.setText("Sorry, Some error caused failure in login");
				super.onFail(response, e);
			}
		});
		apiCall.execute(request);
	}
	
}
