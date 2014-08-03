package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import co.pipevine.android.R;
import co.pipevine.core.DownloadImagesTask;
import co.pipevine.core.OnSwipeTouchListener;


public class HomeFragment extends Fragment {

	RelativeLayout homeBackground;
	TextView name, info;
	//contains profile picture
	ImageView proPic;
	public HomeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);

		homeBackground = (RelativeLayout) rootView.findViewById(R.id.home_background);
		homeBackground.setOnTouchListener(new OnSwipeTouchListener(getActivity()){

			//transition to new fragment
			@Override
			public void onSwipeLeft() {
				// TODO Auto-generated method stub
				super.onSwipeLeft();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

				Fragment newFragment = new HomeFragment2();
				
				ft.replace(R.id.home_background, newFragment);
				//ft.replace(R.id.details_fragment_container, newFragment, "detailFragment");

				// Start the animated transition.
				ft.commit();
			}

			
			
		});
		
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
	

	
	
}
