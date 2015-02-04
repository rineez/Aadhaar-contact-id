package com.livares.aadhaar.pac;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class App {

	private App() {}
	
	public static final boolean DEVELOPMENT = true;
	public static final boolean DEBUG = false;
	public static final String APP_ID = "com.livares.aadhaar.pac";
	
	//BASE URLS are expected to end with a slash('/')
	private static final String API_BASE_URL="http://192.168.1.98/aadhaarpacserver/";
	private static final String API_DEVELOPMENT_BASE_URL="http://192.168.1.98/aadhaarpacserver/";
	
	public static String SessionToken=null;
	public static String ContactCode=null;
	public static String IP="192.168.1.98";
	
	public static String baseUrl() {
		if(DEVELOPMENT){
			return "http://"+IP+"/aadhaarpacserver/";
		}else {
			return "http://"+IP+"/aadhaarpacserver/";
		}
	}
	
	public static void debugToast(Context ctx,String message){
		if(App.DEBUG){
			Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
		}
	}
	public static void infoToast(Context ctx,String message){
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void setCustomTypeFace(Context ctx,String fontfile,View v){	
		
		Typeface myTypeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/"+fontfile);
		setCustomTypeFace(ctx, myTypeface, v);
	}
	public static void setCustomTypeFace(Context ctx,Typeface typeface,View v){
		
	    try {
	        if (v instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) v;
	            for (int i = 0; i < vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                setCustomTypeFace(ctx, typeface, child);
	            }
	        } else if (v instanceof TextView) {
	            ((TextView)v).setTypeface(typeface);
	        }/* else if (v instanceof EditText) {
	            ((EditText)v).setTypeface(typeface);
	        }*/
	    } catch (Exception e) {
	        // ignore
	    }
	    
	}
	
	public static void setCustomTypeFace(Context ctx,String fontfile,EditText v){
		Typeface myTypeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/"+fontfile);
	    v.setTypeface(myTypeface);
	}
}
