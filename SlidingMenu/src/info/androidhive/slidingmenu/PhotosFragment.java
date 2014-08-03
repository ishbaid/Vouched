package info.androidhive.slidingmenu;

import java.util.List;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import co.pipevine.android.R;
import co.pipevine.core.DownloadImagesTask;
import co.pipevine.core.Login;
import co.pipevine.core.*;
import co.pipevine.core.OnSwipeTouchListener;

public class PhotosFragment extends Fragment implements View.OnClickListener, OnCheckedChangeListener {

	RelativeLayout background;
	//connection name
	static TextView cName;
	static TextView cInfo;
	//connection picture
	static ImageView cPic;
	//keeps track of all connections
	public static List<Contact> contactsList;
	//keeps track of which connection we are in the list
	static int curIndex;
	//shows message on swipe
	Toast action;

	ToggleButton prof, prod, integ, adapt, comm, lead, innovation, team;
	ToggleButton [] allButtons;
	int numChecked;

	public PhotosFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_photos, container, false);

		prof = (ToggleButton) rootView.findViewById(R.id.prof);
		prod = (ToggleButton) rootView.findViewById(R.id.prod);
		integ = (ToggleButton) rootView.findViewById(R.id.integ);
		adapt = (ToggleButton) rootView.findViewById(R.id.adapt);
		comm = (ToggleButton) rootView.findViewById(R.id.comm);
		lead = (ToggleButton) rootView.findViewById(R.id.lead);
		innovation = (ToggleButton) rootView.findViewById(R.id.innovation);
		team = (ToggleButton) rootView.findViewById(R.id.team);


		allButtons = new ToggleButton[8];
		allButtons[0] = prof;
		allButtons[1] = prod;
		allButtons[2] = integ;
		allButtons[3] = adapt;
		allButtons[4] = comm;
		allButtons[5] = lead;
		allButtons[6] = innovation;
		allButtons[7] = team;

		for(ToggleButton button:allButtons){

			button.setOnCheckedChangeListener(this);
			button.setBackgroundColor(Color.GRAY);
		}
		numChecked = 0;



		curIndex = 0;
		cName =(TextView) rootView.findViewById(R.id.cName);
		cInfo = (TextView) rootView.findViewById(R.id.cInfo);
		cPic = (ImageView) rootView.findViewById(R.id.cPic);
		background = (RelativeLayout) rootView.findViewById(R.id.background);
		background.setOnTouchListener(new OnSwipeTouchListener(getActivity()){

			@Override
			public void onSwipeLeft() {
				// TODO Auto-generated method stub
				super.onSwipeLeft();
				nextConnection();
				resetButtons();
				Toast.makeText(getActivity(), "Dismissed", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				super.onSwipeRight();
				//save changes

				nextConnection();
				resetButtons();

				Toast.makeText(getActivity(), "Vouched!", Toast.LENGTH_SHORT).show();

			}


		});

		return rootView;
	}

	private void resetButtons(){
		
		for(ToggleButton button: allButtons){
			
			button.setBackgroundColor(Color.GRAY);
			button.setChecked(false);
		}
	}


	//handles pressing toggle buttons
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(buttonView instanceof ToggleButton){

			if(isChecked){

				if(numChecked < 4){
					
					buttonView.setBackgroundColor(Color.GREEN);
					numChecked ++;
				}
				else{
					
					buttonView.setChecked(!isChecked);
					//AlertView: Can only recommend up to 4 traits
					AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();  
					alertDialog.setTitle("Alert ");  
					alertDialog.setMessage("You can only recommend up to 4 Traits!");
					alertDialog.setCanceledOnTouchOutside(true);
					alertDialog.show(); 
				}

			}
			else{
				buttonView.setBackgroundColor(Color.GRAY);
				numChecked --;
			}

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub




	}

	//displays current connection information
	public static void setConnection(){

		Contact connection = contactsList.get(curIndex);
		String name = connection.getFirstName() + " " + connection.getLastName();
		
		//skips private accounts
		if(name.equals("private private")){
			
			nextConnection();
			return;
			
		}
		
		cName.setText(name);
		cInfo.setText("");
		String url = connection.getProfileImageURL();
		cPic.setTag(url);
		new DownloadImagesTask().execute(cPic);
		LayoutParams params = (LayoutParams) cPic.getLayoutParams();

		params.width = 300;
		params.height = 300;
		//cPic.setLayoutParams(params);
	}

	//updates Connection
	private static void nextConnection(){

		if(curIndex < contactsList.size() - 1){

			curIndex ++;
			setConnection();
		}
		else{

			cPic.setBackgroundResource(R.drawable.default_face);
			cName.setText("No more connections.");
		}
	}

	//load connections
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setConnection();


	}






}