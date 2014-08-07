package info.androidhive.slidingmenu;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
	TextView name, info;
	static TextView score;
	static TextView numGiven;
	static TextView numReceived;
	
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
		numGiven = (TextView) rootView.findViewById(R.id.num_given);
		numReceived = (TextView) rootView.findViewById(R.id.num_received);
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

		
		if(MainActivity.URL != null){
			
			
			
			proPic.setTag(MainActivity.URL);
			new DownloadImagesTask().execute(proPic);
			LayoutParams params = (LayoutParams) proPic.getLayoutParams();
			params.width = 150;
			params.height = 150;
			proPic.setLayoutParams(params);
		}
	}
	//updates stats
	public static void setConnectionNumber(int vScore, int given, int received){
		
		score.setText(vScore  + "");
		numGiven.setText(given  + "");
		numReceived.setText(received + "");
		Log.d("Baid", "score: " + vScore + " Given: " + given + " Received: " + received);
		
	}

	
	
}
