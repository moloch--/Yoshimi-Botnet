package com.redactedlabs.yoshimi;

import org.json.JSONException;
import org.json.JSONObject;

public class RecentCall {
	
	private String phoneNumber;
	private String callType;
	private String name;
	private String numberType;
	
	public RecentCall(){}
	
	public String getJsonObject() throws JSONException{
		//Returns a JSON Object String for Instance of Recent Call
		JSONObject contactJSONObject = new JSONObject();
		contactJSONObject.accumulate("contactName", name);
		contactJSONObject.accumulate("phoneNumber", phoneNumber);
		contactJSONObject.accumulate("callType", callType);
		contactJSONObject.accumulate("numberType", numberType);
		System.out.println(contactJSONObject.toString());
		return contactJSONObject.toString();
	}
	
	//Getters
	public String getPhoneNumber(){
		return phoneNumber;
	}
	
	public String getCallType(){
		return callType;
	}
	
	public String getName(){
		return name;
	}
	
	public String getNumberType(){
		return numberType;
	}
	
	//Setters
	public void setPhoneNumber(String number){
		phoneNumber = number;
	}
	
	public void setCallType(String type){
		callType = type;
	}
	
	public void setName(String associatedName){
		name = associatedName;
	}
	
	public void setNumberType(String phoneNumberType){
		numberType = phoneNumberType;
	}
}
