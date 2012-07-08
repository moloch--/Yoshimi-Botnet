package com.redactedlabs.yoshimi;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactsList {
	
	private String phoneNumber;
	private String contactName;
	private String contactEmail;

	
	public ContactsList(){}
	
	public String getJsonObject() throws JSONException{
		//Returns a JSON Object String for Instance of Contact
		JSONObject contactJSONObject = new JSONObject();
		contactJSONObject.accumulate("contactName", contactName);
		contactJSONObject.accumulate("phoneNumber", phoneNumber);
		contactJSONObject.accumulate("contactEmail", contactEmail);
		System.out.println(contactJSONObject.toString());
		return contactJSONObject.toString();
	}
	
	
	//Getters
	public String getPhoneNumber(){
		return phoneNumber;
	}
	
	public String getContactName(){
		return contactName;
	}
	
	public String getEmail(){
		return contactEmail;
	}
	
	//Setters
	public void setPhoneNumber(String number){
		phoneNumber = number;
	}

	public void setContactName(String name){
		contactName = name;
	}
	
	public void setEmail(String email){
		contactEmail = email;
	}
	
}
