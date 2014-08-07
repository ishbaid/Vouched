package info.androidhive.slidingmenu;

import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import co.pipevine.android.R;

public class PagesFragment extends Fragment {
	
	Button reset;
	public PagesFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);
        
        reset = (Button) rootView.findViewById(R.id.button1);
        reset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//query should pull all objects, since all vouchscore are positive
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
				query.whereGreaterThan("totalVouchScore", -1);
				query.findInBackground(new FindCallback<ParseObject>(){

					@Override
					public void done(List<ParseObject> objects, ParseException e) {
						// TODO Auto-generated method stub
						final int size = objects.size();
						Log.d("Baid", "Number of results: " + objects.size());
						//deletes all elements
						for(int i = 0; i < objects.size(); i ++){
							
							ParseObject person = objects.get(i);
							person.deleteInBackground(new DeleteCallback(){

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									if(e == null){
										
										Log.d("Baid", "Database has been erase. Objects: " + size);
									}
								}
								
								
							});
						}
					}
					
					
				});
				
			}
		});
        
        return rootView;
    }
}
