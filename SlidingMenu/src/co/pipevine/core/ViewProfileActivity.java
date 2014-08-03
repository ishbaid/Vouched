package co.pipevine.core;

import java.util.Random;

import info.androidhive.slidingmenu.PhotosFragment;

import org.brickred.socialauth.Contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import co.pipevine.android.R;

public class ViewProfileActivity extends Activity{

	Contact connection;
	ImageView viewPic;
	TextView viewName, viewInfo, vScore;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_profile);
		
		viewPic = (ImageView) findViewById(R.id.viewPic);
		viewName = (TextView) findViewById(R.id.viewName);
		viewInfo = (TextView) findViewById(R.id.viewInfo);
		vScore = (TextView) findViewById(R.id.vScore);
		
		Random rgen = new Random();
		vScore.setText((rgen.nextInt(100) + 1) + "");
		
		Intent intent = getIntent();
		String reverseName = intent.getStringExtra("reverseName");
		if(reverseName != null){
			
			connection = ContactDataListener.getOrdererdContacts().get(reverseName);
			viewName.setText(connection.getFirstName() + " "  + connection.getLastName());
			viewInfo.setText("");
			viewPic.setTag(connection.getProfileImageURL());
			new DownloadImagesTask().execute(viewPic);
			LayoutParams params = (LayoutParams) viewPic.getLayoutParams();
			params.width = 300;
			params.height = 300;
			viewPic.setLayoutParams(params);
		}
	}
	
	

}
