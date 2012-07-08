package com.redactedlabs.yoshimi;

import org.json.JSONException;
import org.json.JSONObject;

public class SMSList {
	
	/*
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
	
			smsList[smsIndex].setContactName(contactName);
		  smsList[smsIndex].setPhoneNumber(phoneNumber);
		  smsList[smsIndex].setMessageDate(date);
		  smsList[smsIndex].setMessageType(messageType);
		  smsList[smsIndex].setMessageBody(messageBody);
		  smsList[smsIndex].setMessageRead(messageRead);
	*/
	
	
	//TODO Redefine Class for SMS Data ^^
	
	private String phoneNumber;
	private String contactName;
	private String messageDate;
	private String messageType;
	private String messageBody;
	private String messageRead;

	
	public SMSList() {
		/* Blank Constructor */
	}
	
	public String getJsonObject() throws JSONException{
		//Returns a JSON Object String for Instance of Contact
		JSONObject contactJSONObject = new JSONObject();
		contactJSONObject.accumulate("contactName", contactName);
		contactJSONObject.accumulate("phoneNumber", phoneNumber);
		contactJSONObject.accumulate("messageDate", messageDate);
		contactJSONObject.accumulate("messageType", messageType);
		contactJSONObject.accumulate("messageBody", messageBody);
		contactJSONObject.accumulate("messageRead", messageRead);
		return contactJSONObject.toString();
	}
	
	
	//Getters
	public String getPhoneNumber(){
		return phoneNumber;
	}
	
	public String getContactName(){
		return contactName;
	}
	
	public String getMessageBody(){
		return messageBody;
	}
	
	public String getMessageType(){
		return messageType;
	}
	
	public String getMessageRead(){
		return messageRead;
	}
	
	public String getMessageDate(){
		return messageDate;
	}
	
	//Setters
	public void setPhoneNumber(String number){
		phoneNumber = number;
	}

	public void setContactName(String name){
		contactName = name;
	}
	
	public void setMessageDate(String date){
		messageDate = date;
	}
	
	public void setMessageType(String type){
		messageType = type;
	}
	
	public void setMessageRead(String read){
		messageRead = read;
	}
	
	public void setMessageBody(String body){
		messageBody = body;
	}

}
