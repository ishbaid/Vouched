package co.pipevine.core;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.PhotosFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONArray;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

// To receive the contacts response after authentication
public final class ContactDataListener implements SocialAuthListener<List<Contact>> {

	
	//list of all connections
	static List<String>names;
	//hashtable of all contacts
	static HashMap<String, Contact> orderedConnections;

	//keeps track of which connections to vouch for and which have already been v
	//vouched for
	//Maps id to contact
	static HashMap<String, Contact> toVouch;
	static HashMap<String, Contact> vouchedFor;
	boolean isNull = false;

	public static List<String> getNames(){

		return names;
	}

	public static HashMap<String, Contact> getOrdererdContacts(){

		return orderedConnections;
	}
	public static HashMap<String, Contact> getToVouch(){
		
		return toVouch;
	}
	public static HashMap<String, Contact> getVouchedFor(){
		
		return vouchedFor;
	}

	@Override
	public void onError(SocialAuthError arg0) {
		// TODO Auto-generated method stub

	}

	public void setup(List<String>tv, List<String> vf){


		toVouch = new HashMap<String, Contact>();
		vouchedFor = new HashMap<String, Contact>();

		if(tv != null){


			for(int i = 0; i < tv.size(); i ++){

				String id = tv.get(i);

				//we create an instance of contact, because we need to indicate 
				//that a contact is associated with this particular id
				Contact contact = new Contact();				
				toVouch.put(id, contact);
			}
		}
		if(vf != null){

			for(int i = 0; i < vf.size(); i ++){

				String id =  vf.get(i);

				Contact contact = new Contact();
				vouchedFor.put(id, contact);
			}
		}
		//if either list is null, we need to remember
		if(tv == null || vf == null){

			isNull = true;

		}


	}


	@Override
	public void onExecute(String arg0, List<Contact> t) {
		// TODO Auto-generated method stub
		//get toLoad and vouchedFor from database
		Log.d("Custom-UI", "Receiving Data");


		//I created this, so we can use this list within the done function
		final List<Contact> list = t;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", MainActivity.getUserID());
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				//there should only be one object and no exception
				if(objects.size() == 1 && e == null){

					ParseObject person = objects.get(0);
					List<String>tv = new ArrayList<String>();
					tv = person.getList("toVouch");
					List<String>vf = new ArrayList<String>();
					vf = person.getList("vouchedFor");

					ContactDataListener.this.setup(tv, vf);
					//goes through contacts and figures out which users to vouch for
					// and which users have already been vouched for
					for(int i = 0; i < list.size(); i ++){
						
						Contact contact = list.get(i);
						String id = contact.getId();
						//if the downloaded toVouch array or vouchedFor arrays were null
						//that means we still need to vouch for every connection
						if(isNull){
							
							toVouch.put(id, contact);
						}
						else{							
							
							//if user isn't in either array, it must be a new connection
							//if toVouch.get(id) == null and vouchedFor.get(id) == null, we found a new connectoin
							//otherwise we still need to replace the blank instance of a contact with the real contact
							if(vouchedFor.get(id) == null){	
								
								toVouch.put(id, contact);
							}
							else if (vouchedFor.get(id) != null){
								
								vouchedFor.put(id, contact);
							}
							
						}
						
					}//for
					
					Log.d("Baid", "toVouch size: " + toVouch.size() + " vf size: " + vouchedFor.size());
					
				}


			}


		});


		orderedConnections = new HashMap<String, Contact>();

		

		
		names = new ArrayList<String>();

		if (t != null && t.size() > 0) {


			for(int i = 0; i < t.size(); i ++){

				Contact contact = t.get(i);
				
				String n = contact.getLastName() + ", " + contact.getFirstName();

				//does not include private connections
				if(!n.equals("private, private")){

					orderedConnections.put(n, contact);
					names.add(n);
				}
			}//for
			
			
			
			Collections.sort(names);

			Log.d("Baid", "Connection have been loaded");

		}



	}
}