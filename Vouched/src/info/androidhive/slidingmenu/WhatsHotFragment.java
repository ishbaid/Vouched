package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import co.pipevine.android.R;

public class WhatsHotFragment extends Fragment {
	
	public WhatsHotFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_whats_hot, container, false);
         
        
        //log out
        SharedPreferences settings = getActivity().getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean("LoggedIn", false);
		edit.commit();
        return rootView;
    }
}
