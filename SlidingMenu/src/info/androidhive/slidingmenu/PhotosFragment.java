package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.brickred.socialauth.Contact;
import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.res.TypedArray;
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
import co.pipevine.core.ContactDataListener;
import co.pipevine.core.DownloadImagesTask;
import co.pipevine.core.OnSwipeTouchListener;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class PhotosFragment extends Fragment implements View.OnClickListener, OnCheckedChangeListener {

	RelativeLayout background;
	//connection name
	static TextView cName;
	static TextView cInfo;
	//connection picture
	static ImageView cPic;

	static //keeps track of IDs of connections that need to be vouched for
	ArrayList<String> IDs;
	
	static //keeps track of current index
	int curIndex = -1;
	
	static //keeps track of current connection
	Contact currentConnection;





	//shows message on swipe
	Toast action;

	ToggleButton prof, prod, integ, adapt, comm, lead, innovation, team;
	ToggleButton [] allButtons;
	int numChecked;

	//keeps track of button images
	TypedArray traits0, traits1, traits2, traits3, traits4;
	TypedArray []allArrays;

	//keeps track of which connections have been vouched for and which haven't
	static HashMap<String, Contact> toVouch;
	static HashMap<String, Contact> vouchedFor;
	//keeps track of current button rank
	int curRank = 1;
	//keeps track of previous button
	ToggleButton prev;

	//keeps track of weighted score for vouched score. Don't need to keep track of
	//unweighted score (number)

	//scoreVouchesReceived
	int [] svr;

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

		//keeps track of all array
		allArrays = new TypedArray[5];

		//gets images for all states of all buttons
		traits0= getResources().obtainTypedArray(R.array.traits0);
		traits1= getResources().obtainTypedArray(R.array.traits1);
		traits2= getResources().obtainTypedArray(R.array.traits2);
		traits3= getResources().obtainTypedArray(R.array.traits3);
		traits4= getResources().obtainTypedArray(R.array.traits4);

		allArrays[0] = traits0;
		allArrays[1] = traits1;
		allArrays[2] = traits2;
		allArrays[3] = traits3;
		allArrays[4] = traits4;


		allButtons = new ToggleButton[8];
		allButtons[0] = prof;
		allButtons[1] = prod;
		allButtons[2] = integ;
		allButtons[3] = adapt;
		allButtons[4] = comm;
		allButtons[5] = lead;
		allButtons[6] = innovation;
		allButtons[7] = team;

		for(int i = 0; i < allButtons.length; i ++){

			ToggleButton button = allButtons[i];
			button.setOnCheckedChangeListener(this);
			button.setBackgroundResource(traits0.getResourceId(i, -1));
		}
		numChecked = 0;

		currentConnection = null;

		/*Array[0] = Professionalism
		Array[1] = Productivity
		Array[2] = Integrity
		Array[3] = Adaptability	
		Array[4] = Communication
		Array[5] = Leadership
		Array[6] = Innovation
		Array[7] = Teamwork
		Array[8] = Total*/
		//initialize score vouches array to 0's

		svr = new int[9];
		for(int i = 0; i < svr.length; i ++ ){

			svr[i] = 0;
		}


		cName =(TextView) rootView.findViewById(R.id.cName);
		cInfo = (TextView) rootView.findViewById(R.id.cInfo);
		cPic = (ImageView) rootView.findViewById(R.id.cPic);
		background = (RelativeLayout) rootView.findViewById(R.id.background);
		background.setOnTouchListener(new OnSwipeTouchListener(getActivity()){

			@Override
			public void onSwipeLeft() {
				// TODO Auto-generated method stub
				super.onSwipeLeft();
				setConnection();
				resetButtons();
				Toast.makeText(getActivity(), "Dismissed", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				super.onSwipeRight();
				//save changes
				uploadToDatabase();

				vouchContact();
				setConnection();
				resetButtons();

				Toast.makeText(getActivity(), "Vouched!", Toast.LENGTH_SHORT).show();

			}


		});

		return rootView;
	}

	private void uploadToDatabase(){

		String message = "";
		//check if account exists
		//if null: create, else:update


		//users vouch score increases depending on the number of traits they vouched for
		//connection's vouch score will increase depending on number of vouched traits
		Contact connection = currentConnection;
		if(connection == null){

			Toast.makeText(getActivity(), "No more connections at this time.", Toast.LENGTH_SHORT).show();
			return;
		}

		String firstName = connection.getFirstName();
		String lastName = connection.getLastName();
		String id = connection.getId();



		message += firstName +"\n";
		message += lastName +"\n";
		message += id +"\n";

		//verify that this is indeed number traits vouched for
		//numberVouchesGiven[curRank] ++
		//user has given a vouch with curRank traits vouched
		int traitsVouched = curRank - 1;
		message += traitsVouched +"\n";
		//updates numberVouchesReceived
		for(int i = 0; i < allButtons.length; i ++){

			if(allButtons[i].isChecked()){
				//nvr[i] ++
				//total needs to be updated for each trait
				//nvr[8] ++ 
			}
		}
		//updates scoreVouchesReceived
		int counter = 0;
		//don't add to total. Go to length - 1
		for(int i = 0; i < svr.length - 1; i ++){

			if(svr[i] > 0){
				message += i + ")" + svr[i] +"\n";
			}
			counter += svr[i];
			//scoreVouchesReceived[i] += svr[i]

		}
		svr[8] = counter;
		message += counter +"\n";
		//error is counter is less than 0 or more than 10

		//update both user and connection's total vouch score
		//manage toVouch and vouchFor array
		//send push notification/messages if possible to recepient of vouch

	}

	//transfer contact from toVouch has table to vouchedFor hashtable
	private void vouchContact(){

		if(currentConnection == null)
			return;
		String id = currentConnection.getId();
		Contact contact = toVouch.get(id);
		if(contact != null){
			
			vouchedFor.put(id, contact);
			toVouch.remove(id);
		}
		else if(contact == null){
			
			Log.d("Baid", "Error 2");
		}

	}

	//lets upload to database what connections we've vouche for and which connections, we still need
	//to vouch for
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//eventually I want to be able to combine code from uploadtodatabase, onResume,
		//and this function so I only have to make one API call in order to
		//retrieve user

		//retrieves user 
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", MainActivity.getUserID());
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub


				//there should only be one user with this user id
				if(objects.size() == 1 && e == null){

					ParseObject person = objects.get(0);

					//insert all linkedin id's to appropriate jsonarray
					JSONArray tv = new JSONArray();
					JSONArray vf = new JSONArray();

					//adds all elements from toVouch to IDs
					for(Map.Entry<String,Contact> map : toVouch.entrySet()){

					     tv.put(map.getKey());

					}
					
					for(Map.Entry<String,Contact> map : vouchedFor.entrySet()){

					     vf.put(map.getKey());

					}
					

					//upload to database

					person.put("toVouch", tv);
					person.put("vouchedFor", vf);
					person.saveInBackground(new SaveCallback(){

						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							//data was successfully saved
							if(e == null){

								Log.d("Baid", "Data Saved");
								//Toast.makeText(getActivity(), "Data Saved!", Toast.LENGTH_SHORT).show();
								//Toast.makeText(getActivity(), "Data Saved!", Toast.LENGTH_SHORT).show();
							}
						}


					});
				}

			}


		});
	}

	private void resetButtons(){

		//rank is one again

		for(int i = 0; i < allButtons.length; i ++){

			ToggleButton button = allButtons[i];
			button.setBackgroundResource(traits0.getResourceId(i, -1));
			button.setChecked(false);
			//set enabled
			button.setEnabled(true);

		}
		//resets svr
		for(int i = 0; i < svr.length; i ++){

			svr[i] = 0;
		}

		curRank = 1;
	}


	//handles pressing toggle buttons
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(buttonView instanceof ToggleButton){

			if(isChecked){


				if(numChecked < 4){


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
					//don't allow the code to reach bottom segment
					return;
				}

			}
			else{

				numChecked --;
			}

		}

		//figure out what button was pressed and changes images
		//and handles any other changes that need to be made
		int id = buttonView.getId();
		for(int i = 0; i < allButtons.length; i ++){

			ToggleButton button = allButtons[i];
			if(id == button.getId()){

				if(isChecked){

					//prevents IndexOutOfBounds
					TypedArray ar = null;
					if(curRank < allArrays.length && curRank >= 0)
						ar = allArrays[curRank];
					else
						return;

					button.setBackgroundResource(ar.getResourceId(i, -1));
					//inputs score for individual trait
					svr[i] = 5 - curRank;
					//increment numb

					//previous button becomes null
					if(prev != null)
						prev.setEnabled(false);
					curRank ++;

					prev = button;

				}
				else{

					//svr for trait is now 0
					svr[i] = 0;

					curRank --;
					//returns button to unpressed state
					TypedArray ar = allArrays[0];
					button.setBackgroundResource(ar.getResourceId(i, -1));
					prev = null;
				}


				break;
			}

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub




	}

	//displays current connection information
	public static void setConnection(){

		Log.d("Baid", "CurIndex at sc is " + curIndex);
		//if we have a null connection, we have finished all connections
		Contact connection = nextConnection();
		if(connection == null){
			
			Log.d("Baid", "No connections left");
			return;
		}
		String name = connection.getFirstName() + " " + connection.getLastName();

		//skips private accounts and skips accounts that we have already vouched for
		if(name.equals("private private") || vouchedFor.get(connection.getId()) != null){

			//we need to skip current connection
			Log.d("Baid", "Private connection");
			setConnection();
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

	//updates Connection, and returns current contact
	private static Contact nextConnection(){

		curIndex ++;
		Log.d("Baid", "CurIndex at nc is " + curIndex);
		if(curIndex < IDs.size()){

			String id = IDs.get(curIndex);
			Contact contact = toVouch.get(id);
			currentConnection = contact;
			if(contact == null){
				
				Log.d("Baid", "Error 3");
			}
			return contact;
		}
		else{

			cPic.setBackgroundResource(R.drawable.default_face);
			cName.setText("No more connections.");
			currentConnection = null;
			return null;
		}
	}

	//load connections
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//retrieve toVouch and vouchedFor
		IDs = new ArrayList<String>();
		curIndex = -1;
		
		toVouch = new HashMap<String, Contact>();
		vouchedFor = new HashMap<String, Contact>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", MainActivity.getUserID());
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(objects.size() == 1 && e == null){

					ParseObject person = objects.get(0);


					List<String> tvData = new ArrayList<String>();
					List<String> vfData = new ArrayList<String>();
					tvData = person.getList("toVouch");
					vfData = person.getList("vouchedFor");
					if(tvData != null && vfData != null){

						Log.d("Baid", "Loaded " + tvData.size() + " connections that need to be vouched for.");
						Log.d("Baid", vfData.size() + " connections have already been vouched for.");
						Log.d("Baid", "Current index is " + curIndex);
						createMaps(tvData, vfData);
						setConnection();
					}
					else{

						initalizeMaps();
						setConnection();
					}

				}

			}


		});

		//these hashtables are only accurate if this is first vouching session since contactDataListener was called





	}

	private void createMaps(List<String> tv, List<String> vf){

		//ISH: TODO what if these are null
		HashMap<String, Contact> oldToVouch= ContactDataListener.getToVouch();
		HashMap<String, Contact> oldVouchedFor = ContactDataListener.getVouchedFor();
		//creates TV hashmap
		for(int i = 0; i < tv.size(); i ++){

			String id = tv.get(i);
			
			//add to IDs
			IDs.add(id);
			
			Contact a = oldToVouch.get(id);
			Contact b = oldVouchedFor.get(id);
			if(a != null && b == null){
				toVouch.put(id, a);
			}
			else if(a == null && b != null){
				toVouch.put(id, b);
			}
			else if(a == null && b == null){

				Log.d("Baid", "Couldn't find conenction");
			}
			else if(a != null && b != null){

				Log.d("Baid", "Duplicate Connection.");
			}
			else{

				Log.d("Baid", "This statement should never execute");
			}

		}
		//creates VF hashmap
		for(int i = 0; i < vf.size(); i ++){

			String id = vf.get(i);
			
			
			Contact a = oldToVouch.get(id);
			Contact b = oldVouchedFor.get(id);
			if(a != null && b == null){
				vouchedFor.put(id, a);
			}
			else if(a == null && b != null){
				vouchedFor.put(id, b);
			}
			else{

				Log.d("Baid", "Error 1");
			}

		}

		

	}

	//if database doesn't have a record, we will use the information from contactdatalistener
	private void initalizeMaps(){

		toVouch = ContactDataListener.getToVouch();
		vouchedFor = ContactDataListener.getVouchedFor();
		
		//adds all elements from toVouch to IDs
		for(Map.Entry<String,Contact> map : toVouch.entrySet()){

		     IDs.add(map.getKey());

		}

		
	}






}