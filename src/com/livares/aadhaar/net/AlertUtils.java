package com.livares.aadhaar.net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;

public class AlertUtils {

	private AlertUtils() {}
	
	public static void showMessage(Context context, String title, String message) {
		showMessage(context, title, message, null);
	}
	
	public static void showMessage(Context context, String title, String message,OnDismissListener listener) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
 
			// set title
			alertDialogBuilder.setTitle(title);
			

			// set dialog message
			alertDialogBuilder
				.setMessage(message)
				.setCancelable(true);
			
			// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
			
				
				alertDialog.setCanceledOnTouchOutside(true);
				if(listener!=null)
					alertDialog.setOnDismissListener(listener);
				// show it
				alertDialog.show();
	}

}
