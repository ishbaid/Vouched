package info.androidhive.slidingmenu;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import co.pipevine.android.R;
import co.pipevine.core.ContactDataListener;
import co.pipevine.core.DownloadImagesTask;
import co.pipevine.core.OnSwipeTouchListener;


public class HomeFragment extends Fragment {

	FrameLayout homeBackground;
	TextView name, info, score;
	static TextView numConnections;
	TextView numVouched;
	//contains profile picture
	ImageView proPic;
	public HomeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

		homeBackground = (FrameLayout) rootView.findViewById(R.id.dashboard_background);
		homeBackground.setOnTouchListener(new OnSwipeTouchListener(getActivity()){

			//transition to new fragment
			@Override
			public void onSwipeLeft() {
				// TODO Auto-generated method stub
				super.onSwipeLeft();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

				Fragment newFragment = new HomeFragment2();
				
				ft.replace(R.id.frame_container, newFragment);
				
				// Start the animated transition.
				ft.commit();
			}

			
			
		});
		

		
		score = (TextView) rootView.findViewById(R.id.vouch_score);
		numConnections = (TextView) rootView.findViewById(R.id.num_connections);
		numVouched = (TextView) rootView.findViewById(R.id.num_vouched);
		name = (TextView) rootView.findViewById(R.id.name);
		info = (TextView) rootView.findViewById(R.id.info);
		proPic = (ImageView) rootView.findViewById(R.id.proPic);
		
		if(MainActivity.fn != null){
			name.setText(MainActivity.fn + " " + MainActivity.ln);
			info.setText(MainActivity.email + "\n" + MainActivity.location + "\n" + MainActivity.URL);
		}
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(MainActivity.fn != null){
			name.setText(MainActivity.fn + " " + MainActivity.ln);
			info.setText(MainActivity.email + "\n" + MainActivity.location);
		} 
		//checks if user is loggedin
		//it might be more efficient to combine all these if statements together
		setConnectionNumber(getActivity());
		
		if(MainActivity.URL != null){
			
			
			
			proPic.setTag(MainActivity.URL);
			new DownloadImagesTask().execute(proPic);
			LayoutParams params = (LayoutParams) proPic.getLayoutParams();
			params.width = 150;
			params.height = 150;
			proPic.setLayoutParams(params);
		}
	}
	
	public static void setConnectionNumber(Context context){
		
		SharedPreferences prefs = context.getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		boolean loggedIn = prefs.getBoolean("LoggedIn", false);
		if(loggedIn && numConnections != null){
			
			List<String> contacts = ContactDataListener.getNames();
			if(contacts != null)
				numConnections.setText(ContactDataListener.getNames().size() + "");
			
		}
		
	}

	
	
}
