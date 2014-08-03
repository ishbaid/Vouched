package co.pipevine.core;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import co.pipevine.android.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	boolean loggedIn;
	Button linkedin;
	SocialAuthAdapter adapter;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View rootView = inflater.inflate(R.layout.login, container, false);

		loggedIn = false;

		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);

		linkedin = (Button)rootView.findViewById(R.id.linkedin);
		linkedin.setBackgroundResource(R.drawable.linkedin);

		linkedin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//adapter.authorize(getActivity(), Provider.LINKEDIN);

			}
		});
		return rootView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}





	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			// Variable to receive message status
			Log.d("Share-Menu", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(getActivity(), providerName + " connected", Toast.LENGTH_SHORT).show();

			loggedIn = true;

			//Ish: When login is completed, we can go to dashboard


			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.


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
				Toast.makeText(getActivity(), "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getActivity(), "Message not posted on" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}




}
