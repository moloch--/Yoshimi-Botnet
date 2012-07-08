package com.redactedlabs.yoshimi;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "The MainActivity was created");
        if (!isServiceRunning()) {
        	Log.d(TAG, "Starting Yoshimi Service...");
	        Intent myIntent = new Intent(getApplicationContext(), YoshimiService.class);
	        startService(myIntent);
        } else {
        	Log.d(TAG, "Yoshimi Service is already running.");
        }
        final Button button = (Button) findViewById(R.id.status);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isServiceRunning()) {
                	Log.d(TAG, "Yoshimi Service is alive!");
                } else {
                	Log.d(TAG, "Yoshimi Service is NOT running!");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.redactedlabs.yoshimi.YoshimiService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
}
