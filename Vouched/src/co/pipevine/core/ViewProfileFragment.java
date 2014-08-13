package co.pipevine.core;

import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.Contact;

import com.google.code.linkedinapi.schema.Person;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import co.pipevine.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewProfileFragment extends Fragment{

	ImageView viewPic;
	TextView viewName, viewInfo, vScore, given, received;
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




}
