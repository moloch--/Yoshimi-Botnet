package com.redactedlabs.yoshimi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SystemBootReceiver extends BroadcastReceiver {
	
	private static final String TAG = "SystemBootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive Called!");
		Intent startYoshimi = new Intent(context, YoshimiService.class);
		context.startService(startYoshimi);		
	}
	
}