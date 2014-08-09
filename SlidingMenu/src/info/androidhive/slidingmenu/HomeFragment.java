package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
import co.pipevine.core.ViewConnectionProfileActivity;


public class HomeFragment extends Fragment  {

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
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", MainActivity.getUserID());
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub

				if(e == null && objects.size() == 1){

					ParseObject person = objects.get(0);

					int vs = person.getInt("totalVouchScore");
					int givenValue = 0;
					int receiveValue = 0;
					
					List<Integer> nvg = new ArrayList<Integer>();
					nvg = person.getList("numberVouchesGiven");

					List<Integer> nvr = new ArrayList<Integer>();
					nvr = person.getList("numberVouchesReceieved");

					if(nvg != null){
						
						//size should be 5
						if(nvg.size() != 5){
							
							Log.d("Baid", "Error 6");
						}

						
						for(int i = 1; i < nvg.size(); i ++){

							givenValue += nvg.get(i);
						}
						

					}
					
					if(nvr != null){
						
						//size should be 9
						if(nvr.size() == 9){
							
							receiveValue = nvr.get(nvr.size() - 1);
							
							
						}else{
							
							Log.d("Baid", "Error 6");
						}
						
					}
					
					
					setConnectionNumber(vs, givenValue, receiveValue);

				}
			}


		});
		
	}
	//updates stats
	public static void setConnectionNumber(int vScore, int given, int received){
		
		if(score == null || numGiven == null || numReceived == null)
			return;
		score.setText(vScore  + "");
		numGiven.setText(given  + "");
		numReceived.setText(received + "");
		Log.d("Baid", "score: " + vScore + " Given: " + given + " Received: " + received);
		
	}

	
	
}
