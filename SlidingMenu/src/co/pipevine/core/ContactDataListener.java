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

import android.util.Log;

// To receive the contacts response after authentication
public final class ContactDataListener implements SocialAuthListener<List<Contact>> {

	//list of all connections
	static List<String>names;
	//hashtable of all contacts
	static HashMap<String, Contact> orderedConnections;


	public static List<String> getNames(){

		return names;
	}

	public static HashMap<String, Contact> getOrdererdContacts(){

		return orderedConnections;
	}

	@Override
	public void onError(SocialAuthError arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExecute(String arg0, List<Contact> t) {
		// TODO Auto-generated method stub

			

		orderedConnections = new HashMap<String, Contact>();
		Log.d("Custom-UI", "Receiving Data");

		PhotosFragment.contactsList = t;
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
			}

			Collections.sort(names);

			//PhotosFragment.setConnection();
			
		}



	}
}