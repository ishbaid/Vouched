package info.androidhive.slidingmenu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import co.pipevine.android.R;

public class SettingsFragment extends Fragment implements OnCheckedChangeListener {
	
	CheckBox never;
	TextView privacy, terms;
	public static boolean neverState;
	public SettingsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);
        never = (CheckBox) rootView.findViewById(R.id.checkBox2);
        privacy = (TextView) rootView.findViewById(R.id.textView1);
        terms = (TextView) rootView.findViewById(R.id.textView2);
        
        privacy.setText(Html.fromHtml(
                "<a href=\"http://vouched.cloudvent.net/privacy.pdf\">Vouched Privacy Policy</a> "));
        terms.setText(Html.fromHtml(
                "<a href=\"http://vouched.cloudvent.net/terms.pdf\">Vouched Terms of Service</a> "));
        privacy.setMovementMethod(LinkMovementMethod.getInstance());
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        return rootView;
    }
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		never.setChecked(neverState);
	}


	public static void setNever(boolean checked){
		
		neverState = checked;
		
	}
	
	//updates whether or preference is checked
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		int id = buttonView.getId();
		if(id == never.getId())
			neverState = isChecked;
		
	}
	
	
}
