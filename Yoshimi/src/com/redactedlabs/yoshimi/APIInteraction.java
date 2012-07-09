package com.redactedlabs.yoshimi;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class APIInteraction {

	public static Cursor retrieveCalls(Context act){
		
		String[] strFields = {
		        android.provider.CallLog.Calls.NUMBER, 
		        android.provider.CallLog.Calls.TYPE,
		        android.provider.CallLog.Calls.CACHED_NAME,
		        android.provider.CallLog.Calls.CACHED_NUMBER_TYPE
		};
		
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC"; 
		 
		Cursor mCallCursor = act.getContentResolver().query(
		        android.provider.CallLog.Calls.CONTENT_URI,
		        strFields,
		        null,
		        null,
		        strOrder
		);
		return mCallCursor;
	}
	
	public static Cursor retrieveContacts(Context act) {
		Cursor mContactCursor = act.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null); 
		return mContactCursor;
	}
	
	public static Cursor retrievePhoneNumbers(String contactId, Context act) {
		Cursor mPhoneCursor = act.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
		return mPhoneCursor;
	}
	
	public static Cursor retrieveEmailContacts(String contactId, Context act) {
		Cursor mEmailCursor = act.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
		return mEmailCursor == null ? null:mEmailCursor;
	}
	
	public static String retrieveContactName(String contactId, Context act){
		Cursor mContactCursor = act.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID +" = "+ contactId, null, null); 
		String contactName = "Unavailable";
		if(mContactCursor.moveToFirst()){
			contactName = mContactCursor.getString(mContactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		}
		return contactName;
	}
	
	public static Cursor retrieveSMS(Context act) {
			Uri SMSPROVIDERURI = Uri.parse("content://sms");
			/*
				String[] strFields = new String[] {
		        // N.B.: These columns must appear in the same order as the
		        // calls to add appear in convertIccToSms.
		        "service_center_address",       // getServiceCenterAddress
		        "address",                      // getDisplayOriginatingAddress
		        "message_class",                // getMessageClass
		        "body",                         // getDisplayMessageBody
		        "date",                         // getTimestampMillis
		        "status",                       // getStatusOnIcc
		        "index_on_icc",                 // getIndexOnIcc
		        "is_status_report",             // isStatusReportMessage
		        "transport_type",               // Always "sms".
		        "type",                         // Always MESSAGE_TYPE_ALL.
		        "locked",                       // Always 0 (false).
		        "error_code",                   // Always 0
		        "_id"
		    };
		    
		    Column Names as Returned by getColumnNames():
		    
			    07-08 03:12:38.813: I/System.out(11300): Column Names: _id
				07-08 03:12:38.813: I/System.out(11300): Column Names: thread_id
				07-08 03:12:38.817: I/System.out(11300): Column Names: address
				07-08 03:12:38.817: I/System.out(11300): Column Names: person
				07-08 03:12:38.817: I/System.out(11300): Column Names: date
				07-08 03:12:38.817: I/System.out(11300): Column Names: protocol
				07-08 03:12:38.817: I/System.out(11300): Column Names: read
				07-08 03:12:38.817: I/System.out(11300): Column Names: status
				07-08 03:12:38.817: I/System.out(11300): Column Names: type
				07-08 03:12:38.817: I/System.out(11300): Column Names: reply_path_present
				07-08 03:12:38.817: I/System.out(11300): Column Names: subject
				07-08 03:12:38.817: I/System.out(11300): Column Names: body
				07-08 03:12:38.817: I/System.out(11300): Column Names: service_center
				07-08 03:12:38.817: I/System.out(11300): Column Names: locked
				07-08 03:12:38.817: I/System.out(11300): Column Names: error_code
				07-08 03:12:38.817: I/System.out(11300): Column Names: seen
			*/
			String strOrder = "date" + " DESC";
			Cursor mSMSCursor = act.getContentResolver().query(
			        SMSPROVIDERURI,
			        null,
			        null,
			        null,
			        strOrder
			);
			/*
			String[] columnNames = mSMSCursor.getColumnNames();
			for(int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
				System.out.println("Column Names: " + columnNames[columnIndex]);
			}
			*/
			return mSMSCursor;
	}

}
