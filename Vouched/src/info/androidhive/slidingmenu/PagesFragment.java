package info.androidhive.slidingmenu;


import android.support.v4.app.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import co.pipevine.android.R;

public class PagesFragment extends Fragment implements OnCheckedChangeListener {
	
	CheckBox automatic, never;
	public static boolean autoState, neverState;
	public PagesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);
        automatic = (CheckBox) rootView.findViewById(R.id.checkBox1);
        never = (CheckBox) rootView.findViewById(R.id.checkBox2);

        
        return rootView;
    }
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		automatic.setChecked(autoState);
		never.setChecked(neverState);
	}

	public static void setAuto(boolean checked){
		
		autoState = checked;
		
	}
	
	public static void setNever(boolean checked){
		
		neverState = checked;
		
	}
	
	//updates whether or preference is checked
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		int id = buttonView.getId();
		if(id == automatic.getId())
			autoState = isChecked;
		else if(id == never.getId())
			neverState = isChecked;
		
	}
	
	
}
