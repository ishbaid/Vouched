package co.pipevine.core;


import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import co.pipevine.android.R;



public class Login extends Activity {

	LinearLayout linkedin;
	boolean loggedIn;

	public static SocialAuthAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//removes action bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		 
		loggedIn = false;

		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);

		linkedin = (LinearLayout)findViewById(R.id.spash_background);
		

		linkedin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adapter.authorize(Login.this, Provider.LINKEDIN);
				
			}
		});
		
		

	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//skips to dashboard if user has logged in before
		//if(adapter != null)
			//adapter.authorize(getApplicationContext(), Provider.LINKEDIN);
	}



	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		//Not calling super prevents the user from by passing the login screen
		//super.onBackPressed();
	}



	public final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			// Variable to receive message status
			Log.d("Share-Menu", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(Login.this, providerName + " connected", Toast.LENGTH_SHORT).show();

			loggedIn = true;

			//Ish: When login is completed, we can go to dashboard


			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.
			adapter.getUserProfileAsync(new ProfileDataListener()); 




		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Menu", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Menu", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Menu", "Dialog Closed by pressing Back Key");

		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(Login.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(Login.this, "Message not posted on" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To receive the profile response after authentication
	private final class ProfileDataListener implements SocialAuthListener<Profile> {

		@Override
		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onExecute(String arg0, Profile t) {
			// TODO Auto-generated method stub
			Log.d("Custom-UI", "Receiving Data");
			Profile profileMap = t;
			Log.d("Custom-UI",  "Validate ID         = " + profileMap.getValidatedId());
			Log.d("Custom-UI",  "First Name          = " + profileMap.getFirstName());
			Log.d("Custom-UI",  "Last Name           = " + profileMap.getLastName());
			Log.d("Custom-UI",  "Email               = " + profileMap.getEmail());
			Log.d("Custom-UI",  "Country                  = " + profileMap.getCountry());
			Log.d("Custom-UI",  "Language                 = " + profileMap.getLanguage());
			Log.d("Custom-UI",  "Location                 = " + profileMap.getLocation());
			Log.d("Custom-UI",  "Profile Image URL  = " + profileMap.getProfileImageURL());
			
			
			
			Intent returnIntent = new Intent();
			returnIntent.putExtra("First Name", profileMap.getFirstName());
			returnIntent.putExtra("Last Name", profileMap.getLastName());
			returnIntent.putExtra("Email", profileMap.getEmail());
			returnIntent.putExtra("Location", profileMap.getLocation());
			returnIntent.putExtra("Url", profileMap.getProfileImageURL());
			returnIntent.putExtra("ID", profileMap.getValidatedId());
			setResult(RESULT_OK,returnIntent);
			finish();

		}


	}


}
