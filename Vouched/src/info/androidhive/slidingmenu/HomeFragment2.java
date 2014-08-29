package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import co.pipevine.vouchedapp.R;
import co.pipevine.core.OnSwipeTouchListener;

public class HomeFragment2 extends Fragment {

	FrameLayout home2Background;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_dashboard2, container, false);
		
		home2Background = (FrameLayout) rootView.findViewById(R.id.dashboard2_background);
		home2Background.setOnTouchListener(new OnSwipeTouchListener(getActivity()){

			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				super.onSwipeRight();
				/*FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);

				Fragment newFragment = new HomeFragment();
				
				ft.replace(R.id.frame_container, newFragment);
				
				// Start the animated transition.
				ft.commit();*/
			}
			
			
		});
		return rootView;
		}
	
	

}
