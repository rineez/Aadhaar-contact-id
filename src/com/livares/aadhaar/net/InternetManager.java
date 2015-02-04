package com.livares.aadhaar.net;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class InternetManager {

	private static final String TAG = InternetManager.class.getName();
	
	private static final int MAX_RETRY = 2;
	
	private boolean forcedWiFiConnection = false;
	private boolean forcedMobileDataConnection = false;
	private boolean waitingForMobileData = false;
	private boolean waitingForWifi = false;
	
	private Timer retryTimer;
	private TimerTask retryTask;
	private int retryCounter;
	
	private static InternetManager ref;
	
	private InternetManager() {}
	
	public static InternetManager instance(){
		if(ref==null){
			ref = new InternetManager();
			ref.retryTimer = new Timer();
		}
		return ref;
	}
	
	/**
	 * 
	 * @param context 
	 * @param enable true to enable internet and false to disable
	 * @return true if internet connectivity is already in the requested state
	 */
	public boolean setInternetEnabled(Context context, boolean enable, BroadcastReceiver receiver) {
		int status = NetworkUtil.getConnectivityStatus(context);
		if(enable == (status == NetworkUtil.TYPE_NOT_CONNECTED)){
			/*
			 * Requested to enable(true) While in Disconnected state
			 * OR Requested to disable(false) While in Connected state
			 */
			if(enable){
				try {
					setWifiEnabled(context, enable);
				} catch (Exception wifiException) {
					Log.e(TAG, "Failed to enable WiFi Connection");
					Log.e(TAG, wifiException.getMessage());
					try {
						setMobileDataEnabled(context, enable);
					} catch (Exception mobileDataException) {
						Log.e(TAG, "Failed to enable Mobile Data Connection");
						Log.e(TAG, mobileDataException.getMessage());
					}
				}
			}
			else{
				try {
					if(forcedWiFiConnection){
						setWifiEnabled(context, false);
					}
					if(forcedMobileDataConnection){
						setMobileDataEnabled(context, false);
					}
				} catch (Exception e) {}
			}
			if(receiver!=null){
				//Register a receiver to listen for state change
				IntentFilter filter = new IntentFilter();
				filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
				context.registerReceiver(receiver, filter);
			}
			//Means state is being changed
			return false;
		}
		//Means already in requested state
		return true;
	}
	
	private void setMobileDataEnabled(final Context context, boolean enabled) throws Exception {
		if(enabled && waitingForMobileData) return;
		 
		 final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class conManClass = Class.forName(conman.getClass().getName());

			final Field iConnectivityManagerField = conManClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
			forcedMobileDataConnection = enabled;
			waitingForMobileData = enabled;
			if(enabled){
				if(retryCounter<MAX_RETRY){
					startRetryTimer(new TimerTask() {
						
						@Override
						public void run() {
							retryCounter++;
							Log.d(TAG, "Retry for internet with Mobile Data: "+retryCounter);
							try {
								setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
								setWifiEnabled(context, true);
							} catch (Exception e) {}
						}
					});
				}else{
					reset();
				}
			}
	}
	
	private void setWifiEnabled(final Context context, boolean enabled) throws Exception {
		if(enabled && waitingForWifi) return;
		
		final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
		wifiManager.setWifiEnabled(enabled);
		forcedWiFiConnection = enabled;
		waitingForWifi = enabled;
		if(enabled){
			if(retryCounter<MAX_RETRY){
				startRetryTimer(new TimerTask() {
					
					@Override
					public void run() {
						retryCounter++;
						Log.d(TAG, "Retry for internet with Wifi: "+retryCounter);
						try {
							wifiManager.setWifiEnabled(false);
							setMobileDataEnabled(context, true);
						} catch (Exception e) {}
					}
				});
			}else{
				reset();
			}
		}
	}
	
	public void reset() {
		cancelRetryTimer();
		waitingForWifi = false;
		waitingForMobileData = false;
		retryCounter = 0;
	}
	
	public boolean isWaitingForMobileData() {
		return waitingForMobileData;
	}
	
	public boolean isWaitingForWifi() {
		return waitingForWifi;
	}
	
	public boolean isForcedConnection() {
		return forcedWiFiConnection||forcedMobileDataConnection;
	}
	
	public void startRetryTimer(TimerTask task){
		if(retryTask!=null){
			retryTask.cancel();
		}
		retryTask = task;
		retryTimer.schedule(retryTask, 15000L);
	}
	
	public void cancelRetryTimer(){
		if(retryTask!=null){
			retryTask.cancel();
		}
		if(retryTimer!=null)
			retryTimer.cancel();
		retryTimer = new Timer();
	}
	

}
