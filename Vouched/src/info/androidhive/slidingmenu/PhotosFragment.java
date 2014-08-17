package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brickred.socialauth.Contact;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	ArrayList<String>skipped;

	//keeps track of vouches traits and order
	HashMap<String, Integer> vouchData;

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

	//keeps track of scoreData keys
	String [] traitNumbers = {
			"professionalismNumber",
			"productivityNumber",
			"integrityNumber",
			"adaptabilityNumber",
			"communicationNumber",
			"leadershipNumber",
			"innovationNumber",
			"teamworkNumber",	

	};
	String [] traitScores = {

			"professionalismScore",
			"productivityScore",
			"integrityScore",
			"adaptabilityScore",
			"communicationScore",
			"leadershipScore",
			"innovationScore",
			"teamworkScore"

	};

	//keeps track of traits to be used for vouchData
	String [] traits = {"professionalism", "productivity", "integrity", "adaptability", "communication"
			, "leadership", "innovation", "teamwork"};

	//keeps track of which connections have been vouched for and which haven't
	static HashMap<String, Person> toVouch;

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

		//sets up double tap


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

		vouchData = new HashMap<String, Integer>();


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

			//invite comes up
			@Override
			public void onSwipeUp() {
				// TODO Auto-generated method stub
				super.onSwipeUp();
				Log.d("Baid", "swipe up");
				inviteDialog(currentConnection.getId());
			}

			@Override
			public void onSwipeDown() {
				// TODO Auto-generated method stub
				super.onSwipeDown();
				Log.d("Baid", "swipe down");
			}
			


		});

		return rootView;
	}

	private void uploadToDatabase(){


		//check if account exists
		//if null: create, else:update


		//users vouch score increases depending on the number of traits they vouched for
		//connection's vouch score will increase depending on number of vouched traits
		final Person connection = currentConnection;
		if(connection == null){

			return;
		}


		String id = connection.getId();

		//NEW DATABASE CODE
		ParseQuery<ParseObject> search = ParseQuery.getQuery("User");
		search.whereEqualTo("linkedinID", id);
		search.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					//account already exits
					if(objects.size() == 1){

						ParseObject person = objects.get(0);
						
						//if user does not have an account, bring up option to send an invite
						List<String>tv = new ArrayList<String>();
						tv = person.getList("toVouch");
						if(tv == null)
							inviteDialog(currentConnection.getId());
						updateScoreData(person);

					}//objects.size == 1
					//account does not exits
					else if(objects.size() == 0){
						
						//send invite
						if(!PagesFragment.neverState)
							inviteDialog(currentConnection.getId());
						
						final ParseObject person = new ParseObject("User");

						person.put("linkedinID", connection.getId());
						person.put("firstName", connection.getFirstName());
						person.put("lastName", connection.getLastName());

						//may need to check if null
						if(connection.getHeadline() != null)
							person.put("headline", connection.getHeadline());
						if(connection.getPictureUrl() != null)
							person.put("location", connection.getLocation().getName());
						if(connection.getPictureUrl() != null)
							person.put("profilePhotoURL", connection.getPictureUrl());

						person.saveInBackground(new SaveCallback(){

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub

								if(e == null){

									//create scoreData object for new user
									createScoreData(person);
								}
							}


						});




					}//obects.size == 0
				}//e== null

			}


		});



	}

	private void inviteDialog(final String curID){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Invite");
		alert.setMessage("Enter Message:");

		// Set an EditText view to get user input 
		LinearLayout dialog = new LinearLayout(getActivity());
		dialog.setOrientation(LinearLayout.VERTICAL);
		
		final EditText input = new EditText(getActivity());
		input.setText("Hey " + currentConnection.getFirstName() + ",\n\n");
		input.setText(input.getText() + getString(R.string.invite_message));
		input.setText(input.getText() + "\n" + LoginActivity.getFirst());
		dialog.addView(input);
		
		final CheckBox ask = new CheckBox(getActivity());
		ask.setText("Apply to All");
		dialog.addView(ask);
		
		alert.setView(dialog);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				if(ask.isChecked()){
					
					PagesFragment.setAuto(true);
				}
				final String message = input.getText().toString();
				new AsyncTask<Void, Void, Object>(){

					ProgressDialog progDailog;
					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						super.onPreExecute();
						progDailog = new ProgressDialog(getActivity());
						progDailog.setMessage("Sending...");
						progDailog.setIndeterminate(false);
						progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progDailog.setCancelable(true);
						progDailog.show();
					}

					@Override
					protected Object doInBackground(Void... params) {
						// TODO Auto-generated method stub
						LoginActivity.client.sendMessage(Arrays.asList(curID, LoginActivity.getUserID()), "I just vouched for you", message);
						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						Log.d("Baid", "Message sent");
						progDailog.dismiss();
						
					}
					
					
				}.execute();
			}
		});
		
		
		alert.setNegativeButton("Not Right Now", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				if(ask.isChecked()){
					
					PagesFragment.setNever(true);
				}
			}
		});

		alert.show();
	}


	//if score data exits, we will update it for connection
	private void updateScoreData(ParseObject person){



		ParseQuery<ParseObject> query = ParseQuery.getQuery("ScoreData");
		query.whereEqualTo("scoreForUser", person);
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					if(objects.size() == 1){

						ParseObject score = objects.get(0);
						ParseObject vouchObject = new ParseObject("Vouch");
						vouchObject.saveInBackground();
						int rank = 1;

						int scoreToAdd = 0;
						for(int i = 0; i < allButtons.length; i ++){

							//we only care about buttons that are pressed
							if(allButtons[i].isChecked()){

								//retrieve scores associated with button
								int traitNum = score.getInt(traitNumbers[i]);
								int traitScore =score.getInt( traitScores[i]);

								score.put(traitNumbers[i], traitNum + 1);
								score.put(traitScores[i], svr[i] + traitScore);
								scoreToAdd += svr[i];

								vouchData.put(traits[i], rank);
								rank ++;
							}

						}

						//creates vouch object
						vouchObject.put("isFor", currentConnection.getId());
						vouchObject.put("isFrom", LoginActivity.getUserID());
						//initalizes jsonobject with hashmap
						JSONObject data = new JSONObject(vouchData);
						vouchObject.put("data", data.toString());



						//increment connection score
						int tvs = score.getInt("totalVouchScore");
						score.put("totalVouchScore", tvs + scoreToAdd);

						//increment connection vouches received by one
						int tvr = score.getInt("totalVouchesReceived");
						score.put("totalVouchesReceived", tvr + 1);

						score.saveInBackground(new SaveCallback(){

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub

								if(e == null){

									Log.d("Baid", "Connection's score has been saved!");

									//add callback if you want to know when it finishes
									
									updateUserData();
									vouchContact();
									setConnection();
									resetButtons();
								}

							}


						});




					}//size == 1
					else{

						Log.d("Baid", "Error retieving connection scoreData");
					}
				}//e == null

			}


		});
	}

	private void createScoreData(ParseObject person){


		final ParseObject score = new ParseObject("ScoreData");
		ParseObject vouchObject = new ParseObject("Vouch");
		vouchObject.saveInBackground();

		int rank = 1;
		int scoreToAdd = 0;
		for(int i = 0; i < allButtons.length; i ++){


			//we only care about buttons that are pressed

			if(allButtons[i].isChecked()){

				score.put(traitNumbers[i], 1);
				score.put(traitScores[i], svr[i]);
				scoreToAdd += svr[i];

				vouchData.put(traits[i], rank);
				rank ++;

			}
			else{

				score.put(traitNumbers[i], 0);
				score.put(traitScores[i], 0);
			}
		}

		/*for (Map.Entry<String, Integer> entry : vouchData.entrySet()) {

			String key = entry.getKey();
		    Integer value = entry.getValue();
		    Log.d("Baid", "Key: " + key + "value:  " + value);
		    // ...
		}*/

		//creates vouch object
		vouchObject.put("isFor", currentConnection.getId());
		vouchObject.put("isFrom", LoginActivity.getUserID());
		//initalizes jsonobject with hashmap
		JSONObject data = new JSONObject(vouchData);
		vouchObject.put("data", data.toString());
		//add callback if you want to find out when have has been completed
		vouchObject.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e == null)
					Log.d("Baid", "Saved Vouch object!");
			}
			
			
		});




		score.put("totalVouchScore", scoreToAdd);
		score.put("totalVouchesReceived", 1);


		score.put("totalVouchesGiven", 0);
		score.put("vouchOne", 0);
		score.put("vouchTwo", 0);
		score.put("vouchThree", 0);
		score.put("vouchFour", 0);
		score.put("skips", 0);
		score.put("scoreForUser", person);

		score.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					Log.d("Baid", "ScoreData saved for new connection");

					updateUserData();
					vouchContact();
					setConnection();
					resetButtons();
				}

			}


		});
	}

	private void skip(){

		if(currentConnection != null && curIndex < IDs.size()){

			skipped.add(currentConnection.getId());
			IDs.set(curIndex, null);

			ParseObject score = LoginActivity.getParseScore();
			if(score != null){

				int v0 = score.getInt("skips");
				score.put("skips", v0 + 1);
			}
			else{

				Log.d("Baid", "Error: User scoredata is null!");
			}

		}

		setConnection();
		resetButtons();
		//Toast.makeText(getActivity(), "Dismissed", Toast.LENGTH_SHORT).show();


	}

	private void vouch(){

		int traitsVouched = curRank - 1;
		if(traitsVouched == 0){
			
			//AlertView: Must vouch at least one trait
			AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();  
			alertDialog.setTitle("Alert ");  
			alertDialog.setMessage("You must vouch at least one trait");
			alertDialog.setCanceledOnTouchOutside(true);
			alertDialog.show(); 
		}
		else{
			
			uploadToDatabase();
		}
		
		
		//Toast.makeText(getActivity(), "Vouched!", Toast.LENGTH_SHORT).show();
		
	}



	//updates user 
	private void updateUserData(){

		int traitsVouched = curRank - 1;
		ParseObject score = LoginActivity.getParseScore();
		if(score == null){

			Log.d("Baid", "Error: scoredata for user is null!");
			return;
		}

		//increment tvg by 1
		int tvg = score.getInt("totalVouchesGiven");

		int tvs = score.getInt("totalVouchScore");


		int v1 = score.getInt("vouchOne");
		int v2 = score.getInt("vouchTwo");
		int v3 = score.getInt("vouchThree");
		int v4 = score.getInt("vouchFour");
		if(traitsVouched >= 0 && traitsVouched < 5){

			score.put("totalVouchesGiven", tvg + 1);
		}
		else{

			Log.d("Baid", "Error 5");
		}

		int scoreToAdd = 0;
		switch(traitsVouched){

		case(1):

			scoreToAdd += 4;
		score.put("vouchOne", v1 + 1);
		break;
		case(2):

			scoreToAdd += 7;
		score.put("vouchTwo", v2 + 1);
		break;

		case(3):

			scoreToAdd += 9;
		score.put("vouchThree", v3 + 1);
		break;
		case(4):

			scoreToAdd += 10;
		score.put("vouchFour", v4 + 1);
		break;
		default:

			Log.d("Baid", "unrecognized traits vouched--Error 5");
			break;

		}

		//update totalScore
		score.put("totalVouchScore", tvs + scoreToAdd);



		Log.d("Baid", "User now has earned " + (tvs + scoreToAdd) + " points");

	}

	//transfer contact from toVouch has table to vouchedFor hashtable
	private void vouchContact(){

		if(currentConnection == null)
			return;
		String id = currentConnection.getId();
		Person contact = toVouch.get(id);
		if(contact != null){


			toVouch.remove(id);
			//this index has been vouchedFor
			IDs.set(curIndex, null);
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


		//update toVouch array
		ParseObject person = LoginActivity.getParseUser();
		if(person != null){

			JSONArray tv = new JSONArray();
			for(int i = 0; i < IDs.size(); i ++){

				String vID = IDs.get(i);
				if(vID != null)
					tv.put(vID);
			}
			//puts skipped contacts at the end
			for(int i = 0; i < skipped.size(); i ++){

				String cID = skipped.get(i);
				if(cID != null)
					tv.put(cID);
			}
			person.put("toVouch", tv);

			person.saveInBackground(new SaveCallback(){

				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if(e == null){

						Log.d("Baid", "Updated User Vouch Data");
					}
				}


			});

		}

		//save user score
		ParseObject score = LoginActivity.getParseScore();
		score.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					Log.d("Baid", "User scoreData saved");
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

		vouchData = new HashMap<String, Integer>();

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
		if(name.equals("private private")){

			//we need to skip current connection
			Log.d("Baid", "Private connection");
			setConnection();
			return;

		}



		cName.setText(name);

		String header = connection.getHeadline();
		if(header.length() <= 55)
			cInfo.setText(connection.getHeadline());
		else
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



		//retrieve toVouch and vouchedFor
		IDs = new ArrayList<String>();
		skipped = new ArrayList<String>();
		curIndex = -1;



		toVouch = new HashMap<String, Person>();
		ParseObject user = LoginActivity.getParseUser();

		List<String> tv = new ArrayList<String>();
		tv = user.getList("toVouch");
		if(tv == null){

			Log.d("Baid", "Error! toVouch should not be null");
		}

		HashMap<String, Person> all = LoginActivity.getConnectionHashMap();

		//creates list of connections to vouch for
		for(int i = 0; i < tv.size(); i ++){

			String id = tv.get(i);

			//add to IDs
			IDs.add(id);


			Person contact = all.get(id);
			toVouch.put(id, contact);

		}
		setConnection();


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

			if (mAccel > 7) {
				Toast toast = Toast.makeText(getActivity(), "Reset.", Toast.LENGTH_LONG);
				toast.show();
				resetButtons();
				// mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};







}