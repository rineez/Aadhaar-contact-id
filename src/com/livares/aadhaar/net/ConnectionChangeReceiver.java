package com.livares.aadhaar.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.livares.aadhaar.pac.App;

public class ConnectionChangeReceiver extends BroadcastReceiver
{
	private static final String TAG = ConnectionChangeReceiver.class.getName();
	
	private Context mContext;
	
    @Override
    public void onReceive(final Context context, final Intent intent) {
    	mContext=context;
        String status = NetworkUtil.getConnectivityStatusString(context);
        Log.d(TAG, "ConnectionChange:"+status);
        App.debugToast(context, "ConnectionChange:"+status);
        if(NetworkUtil.getConnectivityStatus(context)!=NetworkUtil.TYPE_NOT_CONNECTED){
        	new CheckInternetTask().execute("");
        }
    }
    
    private class CheckInternetTask extends AsyncTask<String, Integer, Boolean>{
    	
    	@Override
    	protected Boolean doInBackground(String... params) {
    		return NetworkUtil.hasInternet();
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean hasInternet) {
        	if(!hasInternet){
                Log.d(TAG, "No connection to server");
                App.debugToast(mContext, "No connection to server");
        		return;
        	}
        	try{
        		mContext.unregisterReceiver(ConnectionChangeReceiver.this);
        	}catch(Exception ex){}
        	App.debugToast(mContext, "Internet Connected");
        	InternetManager.instance().reset();
        	Log.e(TAG,"DispatchIntent On Internet Connected");
        	//Intent dispatchIntent = new Intent(mContext, MessageDispatchService.class);
        	//mContext.startService(dispatchIntent);
    	}

    }
    
}