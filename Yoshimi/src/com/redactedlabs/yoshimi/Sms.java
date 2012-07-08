package com.redactedlabs.yoshimi;

import android.telephony.SmsManager;

public class Sms {

	public static void send(String phoneNumber, String message) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	}
}
