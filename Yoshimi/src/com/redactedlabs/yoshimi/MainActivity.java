package com.redactedlabs.yoshimi;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
//import android.view.View;
//import android.widget.Button;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        
        //PORTING UI - CLEAN THIS UP
        setContentView(R.layout.splash);
        Thread splashTimer = new Thread(){
        	//Display's the Splash Screen for 3 Seconds.
        	public void run(){
        		try{     			
        			for(int splashTimer = 0; splashTimer < 5000; splashTimer = splashTimer + 100){
        				sleep(100);
        			}
        			//After Splash Start Console View
        			startActivity(new Intent("com.redactedlabs.yoshimi.console"));
        		} 
        		catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		finally{
        			finish();
        		}
        	}
        };
        splashTimer.start();
        
        
        Log.d(TAG, "The MainActivity was created");
        if (!isServiceRunning()) {
        	Log.d(TAG, "Starting Yoshimi Service...");
	        Intent myIntent = new Intent(getApplicationContext(), YoshimiService.class);
	        startService(myIntent);
        } else {
        	Log.d(TAG, "Yoshimi Service is already running.");
        }
        
        /*  DELETE THIS - It's the button clicklistener no longer being used...
         * 
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
        
        */
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
