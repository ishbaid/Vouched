package co.pipevine.core;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.VouchFragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import co.pipevine.android.R;

import com.google.code.linkedinapi.schema.Person;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ViewProfileFragment extends Fragment implements View.OnClickListener{


	ImageView viewPic;
	TextView viewName, viewInfo, vScore, given, received;

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

	Button vouch;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.view_profile, container, false);
		
		vouch = (Button) rootView.findViewById(R.id.vouch);
		vouch.setOnClickListener(this);
		vouch.setEnabled(!ViewConnectionProfileActivity.isVouched);
		
		viewPic = (ImageView) rootView.findViewById(R.id.viewPic);
		viewName = (TextView) rootView.findViewById(R.id.viewName);
		viewInfo = (TextView) rootView.findViewById(R.id.viewInfo);
		vScore = (TextView) rootView.findViewById(R.id.vouch_score);
		given = (TextView) rootView.findViewById(R.id.num_given);
		received = (TextView) rootView.findViewById(R.id.num_received);
		

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

		setGraph(0, 0, 0, 0, 0, 0, 0, 0);
		setScores(0, 0, 0, 0, 0, 0, 0, 0);

		Person connection = ViewConnectionProfileActivity.connection; 
		if(connection != null){

			viewName.setText(connection.getFirstName() + " "  + connection.getLastName());
			if(connection.getHeadline() != null)
				viewInfo.setText(connection.getHeadline());
			else
				viewInfo.setText("");
			viewPic.setTag(connection.getPictureUrl());
			new DownloadImagesTask().execute(viewPic);
			LayoutParams params = (LayoutParams) viewPic.getLayoutParams();
			params.width = 150;
			params.height = 150;
			viewPic.setLayoutParams(params);
		}

		return rootView;
		//return super.onCreateView(inflater, container, savedInstanceState);
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
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		ParseQuery<ParseObject> innerSearch = ParseQuery.getQuery("User");
		innerSearch.whereEqualTo("linkedinID", ViewConnectionProfileActivity.getConnectionID());

		ParseQuery<ParseObject> search = ParseQuery.getQuery("ScoreData");
		search.whereMatchesQuery("scoreForUser", innerSearch);
		search.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					if(objects.size() == 1){

						ParseObject score = objects.get(0);
						int tvs = score.getInt("totalVouchScore");
						int vg = score.getInt("totalVouchesGiven");
						int vr = score.getInt("totalVouchesReceived");



						vScore.setText(tvs + "");
						given.setText(vg + "X");
						received.setText(vr + "X");

						int []percents = new int[8];

						for(int i = 0; i < traitNumbers.length; i ++){

							int number = score.getInt(traitNumbers[i]);
							number *= 100;
							if(vr != 0)
								percents[i] = number/vr;
							else
								percents[i] = 0;

						}
						setGraph(percents[0], percents[2], percents[4], percents[6], percents[1], percents[3], percents[5], percents[7]);

						int [] scores = new int[8];
						for(int i = 0; i < traitScores.length; i ++){

							//this use to score, but now it represents vouches given for specific trait
							//scores[i] = score.getInt(traitScores[i]);
							scores[i] = score.getInt(traitNumbers[i]);
						}
						setScores(scores[0], scores[2], scores[4], scores[6], scores[1], scores[3], scores[5], scores[7]);



					}
					else{

						Log.d("Baid", "Didn't find scoreData: " + objects.size());
					}
				}
			}


		});

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == vouch.getId()){
			
			//getActivity().onBackPressed();
			
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.putExtra("Fragment", 0);
			intent.putExtra("ID", ViewConnectionProfileActivity.connection.getId());
			startActivity(intent);

		}


	}




}
