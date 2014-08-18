package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.support.v4.app.*;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import co.pipevine.android.R;
import co.pipevine.core.DownloadImagesTask;
import co.pipevine.core.LoginActivity;
import co.pipevine.core.OnSwipeTouchListener;
import co.pipevine.core.ViewConnectionProfileActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class HomeFragment extends Fragment implements View.OnClickListener {

	FrameLayout homeBackground;
	LinearLayout aboutVS, aboutGiven, aboutReceived;

	TextView name, info;
	static TextView score;
	static TextView numGiven;
	static TextView numReceived;

	//bar and anti bar add to constant number
	//bars for all traits
	View bProf, bInteg, bComm, bInnovation, bProd, bAdapt, bLead, bTeam;

	//keeps track of antibars for all graphs
	View  aProf, aInteg, aComm, aInnovation, aProd, aAdapt,  aLead, aTeam;

	TextView sProf, sInteg, sComm, sInnovation, sProd, sAdapt, sLead, sTeam;

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
	//contains profile picture
	ImageView proPic;

	public HomeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);


		bProf = (View) rootView.findViewById(R.id.bar_prof);
		bInteg = (View) rootView.findViewById(R.id.bar_integ);
		bComm = (View) rootView.findViewById(R.id.bar_comm);
		bInnovation = (View) rootView.findViewById(R.id.bar_innovation);
		bProd = (View) rootView.findViewById(R.id.bar_prod);
		bAdapt = (View) rootView.findViewById(R.id.bar_adapt);
		bLead = (View) rootView.findViewById(R.id.bar_lead);
		bTeam = (View) rootView.findViewById(R.id.bar_team);

		aProf = (View) rootView.findViewById(R.id.blank_prof);
		aInteg = (View) rootView.findViewById(R.id.blank_integ);
		aComm = (View) rootView.findViewById(R.id.blank_comm);
		aInnovation = (View) rootView.findViewById(R.id.blank_innovation);
		aProd = (View) rootView.findViewById(R.id.blank_prod);
		aAdapt = (View) rootView.findViewById(R.id.blank_adapt);
		aLead = (View) rootView.findViewById(R.id.blank_lead);
		aTeam = (View) rootView.findViewById(R.id.blank_team);

		sProf = (TextView) rootView.findViewById(R.id.score_prof);
		sInteg = (TextView) rootView.findViewById(R.id.score_integ);
		sComm = (TextView) rootView.findViewById(R.id.score_comm);
		sInnovation = (TextView) rootView.findViewById(R.id.score_innovation);
		sProd = (TextView) rootView.findViewById(R.id.score_prod);
		sAdapt = (TextView) rootView.findViewById(R.id.score_adapt);
		sLead = (TextView) rootView.findViewById(R.id.score_lead);
		sTeam = (TextView) rootView.findViewById(R.id.score_team);

		setGraph(10, 20, 30, 50, 70, 10, 20, 50);

		aboutVS = (LinearLayout) rootView.findViewById(R.id.about_vs);
		aboutGiven = (LinearLayout) rootView.findViewById(R.id.about_given);
		aboutReceived = (LinearLayout) rootView.findViewById(R.id.about_received);

		aboutVS.setOnClickListener(this);
		aboutGiven.setOnClickListener(this);
		aboutReceived.setOnClickListener(this);

		homeBackground = (FrameLayout) rootView.findViewById(R.id.dashboard_background);
		homeBackground.setOnTouchListener(new OnSwipeTouchListener(getActivity()){

			//transition to new fragment
			@Override
			public void onSwipeLeft() {
				// TODO Auto-generated method stub
				super.onSwipeLeft();
				/*FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

				Fragment newFragment = new HomeFragment2();

				ft.replace(R.id.frame_container, newFragment);

				// Start the animated transition.
				ft.commit();*/
			}



		});



		score = (TextView) rootView.findViewById(R.id.vouch_score);
		numGiven = (TextView) rootView.findViewById(R.id.num_given);
		numReceived = (TextView) rootView.findViewById(R.id.num_received);
		name = (TextView) rootView.findViewById(R.id.name);
		info = (TextView) rootView.findViewById(R.id.info);
		proPic = (ImageView) rootView.findViewById(R.id.proPic);


		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(LoginActivity.getFirst() != null){
			name.setText(LoginActivity.getFirst() + " " + LoginActivity.getLast());
			String headline = LoginActivity.getHeadline();
			String loc = LoginActivity.getLocation();
			info.setText("");
			if(headline != null && headline.length() <= 45)
				info.setText(headline + "\n");
			if(loc != null)
				info.setText(info.getText() + loc + "\n");
		}


		if(LoginActivity.getPicture() != null){



			proPic.setTag(LoginActivity.getPicture());
			new DownloadImagesTask().execute(proPic);
			LayoutParams params = (LayoutParams) proPic.getLayoutParams();
			params.width = 150;
			params.height = 150;
			proPic.setLayoutParams(params);
		}

		ParseQuery<ParseObject> innerSearch = ParseQuery.getQuery("User");
		innerSearch.whereEqualTo("linkedinID", LoginActivity.getUserID());

		ParseQuery<ParseObject> search = ParseQuery.getQuery("ScoreData");
		search.whereMatchesQuery("scoreForUser", innerSearch);
		search.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					if(objects.size() == 1){

						Log.d("Baid", "Found User ScoreData");
						ParseObject score = objects.get(0);
						int vs = score.getInt("totalVouchScore");
						int received = score.getInt("totalVouchesReceived");
						int given = score.getInt("totalVouchesGiven");
						setConnectionNumber(vs, given, received);

						int []percents = new int[8];

						for(int i = 0; i < traitNumbers.length; i ++){

							int number = score.getInt(traitNumbers[i]);
							number *= 100;
							if(received != 0)
								percents[i] = number/received;
							else
								percents[i] = 0;

						}
						setGraph(percents[0], percents[2], percents[4], percents[6], percents[1], percents[3], percents[5], percents[7]);

						int [] scores = new int[8];
						for(int i = 0; i < traitScores.length; i ++){

							scores[i] = score.getInt(traitScores[i]);

						}
						setScores(scores[0], scores[2], scores[4], scores[6], scores[1], scores[3], scores[5], scores[7]);


					}
					else{

						Log.d("Baid", "Couldn't find user score data");
					}
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

	//sets scores
	private void setScores(int prof, int integ, int comm, int innovation, int prod, int adapt, int lead, int team ){

		sProf.setText(prof + "");
		sInteg.setText(integ + "");
		sComm.setText(comm + "");
		sInnovation.setText(innovation + "");
		sProd.setText(prod + "");
		sAdapt.setText(adapt + "");
		sLead.setText(lead + "");
		sTeam.setText(team + "");
	}

	//sets graph
	private void setGraph(int prof, int integ, int comm, int innovation, int prod, int adapt, int lead, int team ){

		int total = 100;

		LayoutParams params = (LayoutParams) bProf.getLayoutParams();
		params.width = prof;
		bProf.setLayoutParams(params);

		params = (LayoutParams) bInteg.getLayoutParams();
		params.width = integ;
		bInteg.setLayoutParams(params);

		params = (LayoutParams) bComm.getLayoutParams();
		params.width = comm;
		bComm.setLayoutParams(params);

		params = (LayoutParams) bInnovation.getLayoutParams();
		params.width = innovation;
		bInnovation.setLayoutParams(params);

		params = (LayoutParams) bProd.getLayoutParams();
		params.width = prod;
		bProd.setLayoutParams(params);

		params = (LayoutParams) bAdapt.getLayoutParams();
		params.width = adapt;
		bAdapt.setLayoutParams(params);

		params = (LayoutParams) bLead.getLayoutParams();
		params.width = lead;
		bLead.setLayoutParams(params);

		params = (LayoutParams) bTeam.getLayoutParams();
		params.width = team;
		bTeam.setLayoutParams(params);


		//set antibars		
		params = (LayoutParams) aProf.getLayoutParams();
		params.width = 100 - prof;
		aProf.setLayoutParams(params);

		params = (LayoutParams) aInteg.getLayoutParams();
		params.width = 100 - integ;
		aInteg.setLayoutParams(params);

		params = (LayoutParams) aComm.getLayoutParams();
		params.width = 100 - comm;
		aComm.setLayoutParams(params);

		params = (LayoutParams) aInnovation.getLayoutParams();
		params.width = 100 - innovation;
		aInnovation.setLayoutParams(params);

		params = (LayoutParams) aProd.getLayoutParams();
		params.width = 100 - prod;
		aProd.setLayoutParams(params);

		params = (LayoutParams) aAdapt.getLayoutParams();
		params.width = 100 -adapt;
		aAdapt.setLayoutParams(params);

		params = (LayoutParams) aLead.getLayoutParams();
		params.width = 100 - lead;
		aLead.setLayoutParams(params);

		params = (LayoutParams) aTeam.getLayoutParams();
		params.width = 100 - team;
		aTeam.setLayoutParams(params);


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(v instanceof LinearLayout){

			AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();  
			alertDialog.setTitle("Information");  

			alertDialog.setCanceledOnTouchOutside(true);

			if(id == aboutVS.getId()){

				alertDialog.setMessage("The VouchScore is based on total points for vouches received, plus points for vouches given, plus points for sharing the Vouched app. ");
			}
			else if(id == aboutGiven.getId()){

				alertDialog.setMessage("The number of individual connections you have vouched for. ");
				getGiven();
			}
			else if(id == aboutReceived.getId()){

				alertDialog.setMessage("The number of people who have vouched for you. ");
				getReceived();
			}

			//alertDialog.show(); 

		}


	}

	private void getGiven(){

		Log.d("Baid", "getGiven");
		ParseQuery<ParseObject> inner = ParseQuery.getQuery("Vouch");
		inner.whereEqualTo("isFrom", LoginActivity.getUserID());
		inner.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					Log.d("Baid", "Results given: " + objects.size());
					ArrayList<String> resultIDs = new ArrayList<String>();
					for(int i = 0; i < objects.size(); i ++){

						ParseObject result = objects.get(i);
						String rID = result.getString("isFor");
						resultIDs.add(rID);

					}
					if(resultIDs.size() > 0)
						retrieveNames(resultIDs);
				}

			}


		});


	}

	private void getReceived(){

		Log.d("Baid", "getReceived");
		ParseQuery<ParseObject> inner = ParseQuery.getQuery("Vouch");
		inner.whereEqualTo("isFor", LoginActivity.getUserID());
		inner.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					Log.d("Baid", "Results received: " + objects.size());
					ArrayList<String> resultIDs = new ArrayList<String>();
					for(int i = 0; i < objects.size(); i ++){

						ParseObject result = objects.get(i);
						//find out who user received vouches from
						String rID = result.getString("isFrom");
						resultIDs.add(rID);

					}
					if(resultIDs.size() > 0)
						retrieveNames(resultIDs);

				}

			}


		});

	}

	private void retrieveNames(ArrayList<String> IDs){

		ParseQuery<ParseObject> inner = ParseQuery.getQuery("User");
		inner.whereContainedIn("linkedinID", IDs);
		inner.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub

				final List<ParseObject> resultUsers = objects;
				if(e == null){

					Log.d("Baid", "Retrieved " + objects.size() + " names");
					ArrayList<String> names = new ArrayList<String>();
					for(int i = 0; i < objects.size(); i ++){

						ParseObject user = objects.get(i);
						String name = user.getString("firstName") + " " + user.getString("lastName");
						names.add(name);
					}

					final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();  
					alertDialog.setTitle("Results");

					alertDialog.setCanceledOnTouchOutside(true);

					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_list_item_1, android.R.id.text1, names);

					

					alertDialog.show();
					//LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					alertDialog.setContentView(R.layout.list);
					
					ListView list = (ListView) alertDialog.findViewById(R.id.dialog_list);
					list.setAdapter(adapter);
					list.setOnItemClickListener(new OnItemClickListener(){

						@Override
						public void onItemClick(AdapterView<?> adapter, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							
							ParseObject person = resultUsers.get(position);
							String reverseName = person.getString("lastName") + ", " + person.getString("firstName");
							
							Intent intent = new Intent(getActivity(), ViewConnectionProfileActivity.class);
							intent.putExtra("reverseName", reverseName);
							startActivity(intent);
							alertDialog.dismiss();
						}
						
						
					});

				}
			}


		});

	}



}
