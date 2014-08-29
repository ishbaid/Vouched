package info.androidhive.slidingmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SectionIndexer;
import co.pipevine.vouchedapp.R;
import co.pipevine.core.LoginActivity;
import co.pipevine.core.ViewConnectionProfileActivity;

import com.google.code.linkedinapi.schema.Person;
import com.parse.ParseObject;


public class ConnectionListFragment extends Fragment {

	ListView cList;
	EditText search;
	ArrayAdapter<String> adapter;

	ArrayList<String>data;
	ArrayList<String>sorted;
	HashMap<String, Person> tvMap; 
	int textlength = 0;
	public ConnectionListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_find_people, container, false);

		cList = (ListView) rootView.findViewById(R.id.list);

		//for search
		data = LoginActivity.getOrderedNames();
		sorted = new ArrayList<String>();

		search = (EditText) rootView.findViewById(R.id.search);
		//handles search
		search.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				textlength = search.getText().length();
				sorted.clear();
				for(int i = 0;i < data.size();i++)
				{
					if(textlength <= data.get(i).length())
					{
						if(data.get(i).toLowerCase().contains(search.getText().toString().toLowerCase())){
							sorted.add(data.get(i));
						}
					}
				}

				cList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , sorted));
			}


		});

		return rootView;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ParseObject person = LoginActivity.getParseUser();

		//get list of connnections toVouch
		List<String> tvList = new ArrayList<String>();
		//tvList = person.getList("toVouch");
		tvList = LoginActivity.getTvList();
		//get all connections
		HashMap<String, Person> allMap = LoginActivity.getConnectionHashMap();

		tvMap = new HashMap<String, Person>();

		//go through list of id's we need to vouch for and create hashmap
		for(int i = 0; i < tvList.size(); i ++){

			String id = tvList.get(i);
			Person toAdd = allMap.get(id);
			if(toAdd != null){

				tvMap.put(id, toAdd);
			}
		}

		Log.d("Baid", "To Vouch Map contatins " + tvMap.size() + " entries");
		//once we're done, we can call loadlist
		loadList();
	}

	private void loadList(){

		//creates list adapter that allows for fast scrolling
		adapter = new MyIndexerAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, LoginActivity.getOrderedNames());
		cList.setAdapter(adapter);
		cList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ViewConnectionProfileActivity.class);

				String  reverseName    = (String) cList.getItemAtPosition(position);  

				HashMap<String, Person> alphaMap = LoginActivity.getAlphaMap();
				Person toShow = alphaMap.get(reverseName);


				String intentID = toShow.getId();

				//if person is in tvMap, then we still need to vouch for them
				Person checkVouched = tvMap.get(intentID);

				//need to vouch for
				if(checkVouched != null){

					intent.putExtra("Vouched", false);
				}
				//don't need to vouch for
				else{

					intent.putExtra("Vouched", true);
				}

				intent.putExtra("ID", intentID);
				startActivity(intent);

			}


		});

	}






	//adapter that allows for fast scrolling
	class MyIndexerAdapter<T> extends ArrayAdapter<T> implements SectionIndexer {

		ArrayList<String> myElements;
		HashMap<String, Integer> alphaIndexer;

		String[] sections;

		public MyIndexerAdapter(Context context, int textViewResourceId,
				List<T> objects) {
			super(context, textViewResourceId, objects);
			myElements = (ArrayList<String>) objects;
			// here is the tricky stuff
			alphaIndexer = new HashMap<String, Integer>();
			// in this hashmap we will store here the positions for
			// the sections

			int size = myElements.size();
			for (int i = size - 1; i >= 0; i--) {
				String element = myElements.get(i);
				alphaIndexer.put(element.substring(0, 1), i);
				//We store the first letter of the word, and its index.
				//The Hashmap will replace the value for identical keys are putted in
			}

			// now we have an hashmap containing for each first-letter
			// sections(key), the index(value) in where this sections begins

			// we have now to build the sections(letters to be displayed)
			// array .it must contains the keys, and must (I do so...) be
			// ordered alphabetically

			Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
			// cannot be sorted...

			Iterator<String> it = keys.iterator();
			ArrayList<String> keyList = new ArrayList<String>(); // list can be
			// sorted

			while (it.hasNext()) {
				String key = it.next();
				keyList.add(key);
			}

			Collections.sort(keyList);

			sections = new String[keyList.size()]; // simple conversion to an
			// array of object
			keyList.toArray(sections);

			// ooOO00K !

		}

		@Override
		public int getPositionForSection(int section) {
			// Log.v("getPositionForSection", ""+section);
			String letter = sections[section];

			return alphaIndexer.get(letter);
		}

		@Override
		public int getSectionForPosition(int position) {

			// you will notice it will be never called (right?)
			Log.v("getSectionForPosition", "called");
			return 0;
		}

		@Override
		public Object[] getSections() {

			return sections; // to string will be called each object, to display
			// the letter
		}

	}


}