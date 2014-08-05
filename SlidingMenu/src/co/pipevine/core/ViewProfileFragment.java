package co.pipevine.core;

import org.brickred.socialauth.Contact;

import co.pipevine.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewProfileFragment extends Fragment{

	ImageView viewPic;
	TextView viewName, viewInfo, vScore;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.view_profile, container, false);
		viewPic = (ImageView) rootView.findViewById(R.id.viewPic);
		viewName = (TextView) rootView.findViewById(R.id.viewName);
		viewInfo = (TextView) rootView.findViewById(R.id.viewInfo);
		
		Contact connection = ViewConnectionProfileActivity.connection; 
		if(connection != null){

			viewName.setText(connection.getFirstName() + " "  + connection.getLastName());
			viewInfo.setText("");
			viewPic.setTag(connection.getProfileImageURL());
			new DownloadImagesTask().execute(viewPic);
			LayoutParams params = (LayoutParams) viewPic.getLayoutParams();
			params.width = 150;
			params.height = 150;
			viewPic.setLayoutParams(params);
		}

		return rootView;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}



}
