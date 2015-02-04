package com.livares.aadhaar.pac;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class PermanentContactRegisterActivity extends Activity {
	public static final int QRCODE_REQUEST = 1000;
	public static final int AADHAAR_CONNECT_AUTH_REQUEST = 1001;
	public static final String TEAM_ID = "hackteam13";

	private EditText in_aadhaar_number;
	private ImageView qrCodeScanner;
	private Button btn_create_address;
	private TextView lbl_this_is_address_code;
	private TextView lbl_address_code,lbl_error_message;
	private Button auth_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_permanent_contact_register);
		getActionBar().setTitle(R.string.title_activity_permanent_contact_register);
		in_aadhaar_number = (EditText) findViewById(R.id.in_aadhaar_number);
		lbl_this_is_address_code = (TextView) findViewById(R.id.lbl_this_is_address_code);
		lbl_address_code = (TextView) findViewById(R.id.lbl_address_code);
		lbl_error_message = (TextView) findViewById(R.id.lbl_error_message);
		auth_button = (Button) findViewById(R.id.auth_button);
		btn_create_address = (Button) findViewById(R.id.btn_create_address);
		btn_create_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent manageContactIntent = new Intent(PermanentContactRegisterActivity.this, ManageAddressActivity.class);
				startActivity(manageContactIntent);
				
			}
		});
		
		in_aadhaar_number = (EditText) findViewById(R.id.in_aadhaar_number);
		qrCodeScanner = (ImageView) findViewById(R.id.barcode);
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
			String responseStr = data.getStringExtra("RESPONSE");
			final AuthCaptureData authCaptureData = new Gson().fromJson(
					responseStr, AuthCaptureData.class);
			AadhaarAuthAsyncTask authAsyncTask = new AadhaarAuthAsyncTask(this,authCaptureData){
				@Override
				protected void onPostExecute(AuthResponse response) {
					if(response.isSuccess()){
						registerPAC(response);
					}
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
	
	private void registerPAC(AuthResponse response){
		lbl_error_message.setVisibility(View.GONE);
		String registerUrl = App.baseUrl()+"register";
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("aadhaar", in_aadhaar_number.getText().toString());
		data.put("aadhaar_ref",  response.getReferenceCode());
		WebServiceTask apiCall = new WebServiceTask();
		WebServiceRequest request = WebServiceRequest.toPost(registerUrl, null, data);
		apiCall.setCallback(new ResponseCallBack() {
			
			@Override
			public void onSuccess(JSONObject response) {
				try {
					JSONObject json = response.getJSONObject("response");
					if(json!=null){
						int status = json.getInt("status");
						if(status==1){
							lbl_address_code.setText(json.getString("contact_code"));
							lbl_this_is_address_code.setVisibility(View.VISIBLE);
							lbl_address_code.setVisibility(View.VISIBLE);
							btn_create_address.setVisibility(View.VISIBLE);
							auth_button.setVisibility(View.GONE);
						}else if(status==2){
							Intent loginIntent = new Intent(PermanentContactRegisterActivity.this, LoginActivity.class);
							startActivity(loginIntent);
						}else{
							lbl_this_is_address_code.setVisibility(View.GONE);
							lbl_address_code.setVisibility(View.GONE);
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
					Log.e("Register PAC", e.getMessage());
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
