package com.livares.aadhaar.pac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.livares.aadhaar.pac.R;

public class MainActivity extends Activity{
	
	private Button btn_register;
	private Button btn_manage_address;
	private EditText in_ip;
	private Button btn_setip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_register = (Button)findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showRegistrationForm();
				
			}
		});
		in_ip = (EditText)findViewById(R.id.in_ip);
		btn_setip = (Button)findViewById(R.id.btn_setip);
		btn_setip.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ip = in_ip.getText().toString();
				if(ip.length()!=0){
					App.IP = ip;
				}
				
			}
		});
		
		btn_manage_address = (Button)findViewById(R.id.btn_manage_address);
		btn_manage_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLoginForm();
				
			}
		});
		
	}


	public void showLoginForm() {
		Intent loginIntent = new Intent(this, LoginActivity.class);
		startActivity(loginIntent);
	}
	
	public void showRegistrationForm() {
		Intent registerIntent = new Intent(this, PermanentContactRegisterActivity.class);
		startActivity(registerIntent);
	}
	
	
}
