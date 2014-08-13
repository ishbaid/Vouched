package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brickred.socialauth.Contact;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import co.pipevine.android.R;
import co.pipevine.core.DownloadImagesTask;
import co.pipevine.core.LoginActivity;
import co.pipevine.core.OnSwipeTouchListener;

import com.google.code.linkedinapi.schema.Person;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class PhotosFragment extends Fragment implements View.OnClickListener, OnCheckedChangeListener{

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
	Person currentConnection;


	//shows message on swipe
	Toast action;

	Button skip, vouch;

	ToggleButton prof, prod, integ, adapt, comm, lead, innovation, team;
	ToggleButton [] allButtons;
	int numChecked;

	//keeps track of button images
	TypedArray traits0, traits1, traits2, traits3, traits4;
	TypedArray []allArrays;

	//keeps track of which connections have been vouched for and which haven't
	static HashMap<String, Person> toVouch;
	static HashMap<String, Person> vouchedFor;
	//keeps track of current button rank
	int curRank = 1;
	//keeps track of previous button
	ToggleButton prev;

	//keeps track of weighted score for vouched score. Don't need to keep track of
	//unweighted score (number)

	//scoreVouchesReceived
	int [] svr;
	//keeps track of number of vouches given
	int [] nvg;
	//keeps track of score earned in current vouching session
	int earnedScore;

	public PhotosFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_photos, container, false);


		//setup accelerometer
		   /* do this in onCreate */
	    mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	    mAccel = 0.00f;
	    mAccelCurrent = SensorManager.GRAVITY_EARTH;
	    mAccelLast = SensorManager.GRAVITY_EARTH;


		prof = (ToggleButton) rootView.findViewById(R.id.prof);
		prod = (ToggleButton) rootView.findViewById(R.id.prod);
		integ = (ToggleButton) rootView.findViewById(R.id.integ);
		adapt = (ToggleButton) rootView.findViewById(R.id.adapt);
		comm = (ToggleButton) rootView.findViewById(R.id.comm);
		lead = (ToggleButton) rootView.findViewById(R.id.lead);
		innovation = (ToggleButton) rootView.findViewById(R.id.innovation);
		team = (ToggleButton) rootView.findViewById(R.id.team);

		skip = (Button) rootView.findViewById(R.id.skip);
		vouch = (Button) rootView.findViewById(R.id.next);

		skip.setOnClickListener(this);
		vouch.setOnClickListener(this);

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
				skip();
			}

			@Override
			public void onSwipeRight() {
				// TODO Auto-generated method stub
				super.onSwipeRight();
				//save changes
				vouch();
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
		Person connection = currentConnection;
		if(connection == null){

			return;
		}

		String firstName = connection.getFirstName();
		String lastName = connection.getLastName();
		String id = connection.getId();



		message += firstName +"\n";
		message += lastName +"\n";
		message += id +"\n";

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", id);
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub

				if(e == null){

					if(objects.size() == 1){

						Log.d("Baid", "Person already exists. Let's update their information.");
						ParseObject person = objects.get(0);

						//get numberVouchesReceieved
						List<Integer> nvrDatabase = new ArrayList<Integer>();
						nvrDatabase = person.getList("numberVouchesReceived");
						//should be of size 9
						assert(nvrDatabase.size() == 9);

						//get scoreVouchesReceived
						List<Integer> svrDatabase = new ArrayList<Integer>();
						svrDatabase = person.getList("scoreVouchesReceived");
						//should be of size 9
						assert(svrDatabase.size() == 9);

						//get total score
						int score = person.getInt("totalVouchScore");


						JSONArray nvrToUpload = new JSONArray();

						//increment nvrValues
						for(int i = 0; i < allButtons.length; i ++){

							if(allButtons[i].isChecked()){

								//increment this value
								int currentValue = nvrDatabase.get(i);
								nvrDatabase.set(i, currentValue + 1);
								nvrToUpload.put(currentValue + 1);

								//increment tota
								
								

							}
						}
						//put total into JSONArray
						//one additional person has vouched for connection, increment total by one
						int currentTotal = nvrDatabase.get(nvrDatabase.size() - 1);
						nvrDatabase.set(nvrDatabase.size() - 1, currentTotal + 1);
						nvrToUpload.put(nvrDatabase.get(nvrDatabase.size() - 1));
						//insert
						person.put("numberVouchesReceived", nvrToUpload);


						JSONArray svrToUpload =  new JSONArray();

						//update svr values
						int counter = 0;
						for(int i = 0; i < svr.length - 1; i ++){

							svrToUpload.put(svr[i] + svrDatabase.get(i));
							counter += svr[i];

						}
						svr[8] = counter;
						int total = svrDatabase.get(svrDatabase.size() - 1);
						int totalScore = counter + total;
						svrToUpload.put(totalScore);
						Log.d("Baid", "New vouch score for " + currentConnection.getFirstName() + " is " + (totalScore + score));


						//insert
						person.put("scoreVouchesReceived", svrToUpload);
						person.put("totalVouchScore", totalScore + score);

						person.saveInBackground(new SaveCallback(){

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e == null){

									updateUserData();
									vouchContact();
									setConnection();
									resetButtons();

								}
							}


						});


					}
					else if(objects.size() == 0){


						Log.d("Baid", "Person doesn't exist yet");

						Person contact = currentConnection;
						if(contact == null){

							Log.d("Baid", "*No connection.");
							return;
						}

						ParseObject person = new ParseObject("Person");
						person.put("linkedinID", contact.getId());
						person.put("firstName", contact.getFirstName());
						person.put("lastName", contact.getLastName());
						if(contact.getPictureUrl() != null)
							person.put("profilePhotoURL", contact.getPictureUrl());

						//insert scores given
						JSONArray nvrData = new JSONArray();


						for(int i = 0; i < allButtons.length; i ++){


							if(allButtons[i].isChecked()){
								nvrData.put(1);
								Log.d("Baid", 1 + "");
								
							}

							else{
								nvrData.put(0);
								Log.d("Baid", 0 +"");
							}
						}
						
						//one person has vouched for connection
						nvrData.put(1);
						person.put("numberVouchesReceived", nvrData);

						JSONArray svrData = new JSONArray();
						//goes through and totals up score for this particular vouch
						int counter = 0;
						for(int i = 0; i < svr.length - 1; i ++){

							int value = svr[i];
							svrData.put(value);
							counter += value;
						}
						svr[8] = counter;
						svrData.put(counter);

						person.put("scoreVouchesReceived", svrData);
						person.put("totalVouchScore", counter);

						person.saveInBackground(new SaveCallback(){

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e == null){

									Log.d("Baid", "Partial account created for " + currentConnection.getFirstName());

									//only after we've created the account, can we move on
									updateUserData();
									vouchContact();
									setConnection();
									resetButtons();
								}
							}


						});

					}

				}

			}


		});

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




		//error is counter is less than 0 or more than 10

		//update both user and connection's total vouch score
		//manage toVouch and vouchFor array
		//send push notification/messages if possible to recepient of vouch

	}

	private void skip(){

		setConnection();
		resetButtons();
		Toast.makeText(getActivity(), "Dismissed", Toast.LENGTH_SHORT).show();


	}

	private void vouch(){

		uploadToDatabase();
		Toast.makeText(getActivity(), "Vouched!", Toast.LENGTH_SHORT).show();

	}



	//updates user 
	private void updateUserData(){

		int traitsVouched = curRank - 1;
		if(traitsVouched >= 0 && traitsVouched < 5){

			//increment by one
			nvg[traitsVouched] ++;
		}
		else{

			Log.d("Baid", "Error 5");
		}

		switch(traitsVouched){

		case(1):

			earnedScore += 4;
		break;
		case(2):

			earnedScore += 7;
		break;

		case(3):

			earnedScore += 9;
		break;
		case(4):

			earnedScore += 10;
		break;
		default:

			Log.d("Baid", "unrecognized traits vouched--Error 5");
			break;

		}

		Log.d("Baid", "User now has earned " + earnedScore + " points");

	}

	//transfer contact from toVouch has table to vouchedFor hashtable
	private void vouchContact(){

		if(currentConnection == null)
			return;
		String id = currentConnection.getId();
		Person contact = toVouch.get(id);
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
		 mSensorManager.unregisterListener(mSensorListener);
		super.onPause();


		//eventually I want to be able to combine code from uploadtodatabase, onResume,
		//and this function so I only have to make one API call in order to
		//retrieve user

		//retrieves user 
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", LoginActivity.getUserID());
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
					for(Map.Entry<String,Person> map : toVouch.entrySet()){

						tv.put(map.getKey());

					}

					for(Map.Entry<String,Person> map : vouchedFor.entrySet()){

						vf.put(map.getKey());

					}


					//upload to database

					person.put("toVouch", tv);
					person.put("vouchedFor", vf);

					//gets number of vouches receieved
					List<Integer> nvrDatabase = new ArrayList<Integer>();
					nvrDatabase = person.getList("numberVouchesReceived");
					assert(nvrDatabase.size() == 9);
					int numReceived = nvrDatabase.get(8);

					//get numberVouchesGiven
					List<Integer> nvgDatabase = new ArrayList<Integer>();
					nvgDatabase = person.getList("numberVouchesGiven");
					assert(nvgDatabase.size() == 5);

					JSONArray nvgToUpload = new JSONArray();
					//place holder for skips
					nvgToUpload.put(0);

					//counts all given vouches
					int counter = 0;
					//I'm starting at one, because I don't want to count skips
					for(int i = 1; i < nvgDatabase.size(); i ++){



						int value = nvgDatabase.get(i);
						int curValue = nvg[i];
						nvgToUpload.put(curValue + value);
						counter += curValue + value;

					}

					person.put("numberVouchesGiven", nvgToUpload);

					int tvs = person.getInt("totalVouchScore");
					tvs += earnedScore;
					person.put("totalVouchScore", tvs);
					Log.d("Baid", "User's new total vouch score is " + tvs);


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

		int id = v.getId();
		if(id == skip.getId()){

			skip();
		}
		else if(id == vouch.getId()){

			vouch();
		}


	}

	//displays current connection information
	public static void setConnection(){


		//if we have a null connection, we have finished all connections
		Person connection = nextConnection();
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
		String url = connection.getPictureUrl();
		cPic.setTag(url);
		new DownloadImagesTask().execute(cPic);
		LayoutParams params = (LayoutParams) cPic.getLayoutParams();

		params.width = 300;
		params.height = 300;
		//cPic.setLayoutParams(params);
	}

	//updates Connection, and returns current contact
	private static Person nextConnection(){

		curIndex ++;

		if(curIndex < IDs.size()){

			String id = IDs.get(curIndex);
			Person contact = toVouch.get(id);
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
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		  

		//reset numberVouchesGiven to 0
		nvg = new int[5];
		for(int i = 0; i < nvg.length; i ++){

			nvg[i] = 0;
		}
		earnedScore = 0;

		//retrieve toVouch and vouchedFor
		IDs = new ArrayList<String>();
		curIndex = -1;

		toVouch = new HashMap<String, Person>();
		vouchedFor = new HashMap<String, Person>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", LoginActivity.getUserID());
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
		HashMap<String, Person> all = LoginActivity.getConnectionHashMap();
		
		//creates TV hashmap
		for(int i = 0; i < tv.size(); i ++){

			String id = tv.get(i);

			//add to IDs
			IDs.add(id);

			Person contact = all.get(id);
			toVouch.put(id, contact);
			
	

		}
		//creates VF hashmap
		for(int i = 0; i < vf.size(); i ++){

			String id = vf.get(i);
			Person contact = all.get(id);
			vouchedFor.put(id, contact);

		}



	}

	//if database doesn't have a record, we will use the information from contactdatalistener
	private void initalizeMaps(){

		toVouch = LoginActivity.getConnectionHashMap();
		

		//adds all elements from toVouch to IDs
		for(Map.Entry<String,Person> map : toVouch.entrySet()){

			IDs.add(map.getKey());

		}


	}


	//handles shake
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity


	//
	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			
			if (mAccel > 10) {
			    Toast toast = Toast.makeText(getActivity(), "Reset.", Toast.LENGTH_LONG);
			    toast.show();
			    resetButtons();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};







}