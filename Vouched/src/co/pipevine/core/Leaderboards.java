package co.pipevine.core;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import co.pipevine.vouchedapp.R;

public class Leaderboards extends Activity {

	final static int RESULTS = 10;
	ListView lv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		//Parse.initialize(this, "TQLt2PWNmJp6JBLYF95jnIDnxcoXdA2322CGoWdj", "aNTQtT0FzERvfFsQs3BknbxqQm49IB7dqE313WrF");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboards);

		lv = (ListView) findViewById(R.id.listView1);
		Intent intent = getIntent();
		final String key = intent.getStringExtra("Key");
		if(key == null)
			return;
		Log.d("Baid", "Key is " + key);
		ParseQuery<ParseObject> search = ParseQuery.getQuery("ScoreData");
		search.orderByDescending(key);
		search.include("scoreForUser");
		search.setLimit(RESULTS);
		search.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					Log.d("Baid", "Found results:" + objects.size());
					ArrayList<String>top = new ArrayList<String>();
					for(int i = 0; i < objects.size(); i ++){

						ParseObject score = objects.get(i);
						ParseObject person = null;
						try {
							person = score.fetchIfNeeded().getParseObject("scoreForUser");
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						String toAdd = "";
						if(person != null)
							toAdd =   (i + 1) + ") " + person.getString("firstName") + " " + person.getString("lastName");


						top.add(toAdd);

					}



					ArrayAdapter<String> adapter = new ArrayAdapter<String>(Leaderboards.this,
							android.R.layout.simple_list_item_1, android.R.id.text1, top);

					lv.setAdapter(adapter);
				}

			}


		});
	}



}
