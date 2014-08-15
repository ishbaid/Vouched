package co.pipevine.core;

import info.androidhive.slidingmenu.PhotosFragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

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
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.view_profile, container, false);
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
		
		setGraph(10, 20, 30, 50, 70, 10, 20, 50);

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
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", ViewConnectionProfileActivity.getConnectionID());
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub

				if(e == null && objects.size() == 1){

					ParseObject person = objects.get(0);

					int vs = person.getInt("totalVouchScore");
					vScore.setText(vs + "");

					List<Integer> nvg = new ArrayList<Integer>();
					nvg = person.getList("numberVouchesGiven");

					List<Integer> nvr = new ArrayList<Integer>();
					nvr = person.getList("numberVouchesReceieved");

					if(nvg != null){

						//size should be 5
						if(nvg.size() != 5){

							Log.d("Baid", "Error 6");
						}

						int givenCount = 0;
						for(int i = 1; i < nvg.size(); i ++){

							givenCount += nvg.get(i);
						}
						given.setText(givenCount + "");

					}

					if(nvr != null){

						//size should be 9
						if(nvr.size() == 9){

							received.setText(nvr.get(nvr.size() - 1) + "");


						}else{

							Log.d("Baid", "Error 6");
						}

					}


				}
			}


		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		
	}




}
