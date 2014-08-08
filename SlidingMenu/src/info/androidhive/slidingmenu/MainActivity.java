package info.androidhive.slidingmenu;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.Contact;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import co.pipevine.android.R;
import co.pipevine.core.ContactDataListener;
import co.pipevine.core.Login;
import co.pipevine.core.Login.ResponseListener;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;




	static String fn;
	static String ln;
	static String email;
	static String location;
	static String URL;
	static String ID;

	//keeps track of contacts


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//initialize parse
		Parse.initialize(this, "TQLt2PWNmJp6JBLYF95jnIDnxcoXdA2322CGoWdj", "aNTQtT0FzERvfFsQs3BknbxqQm49IB7dqE313WrF");


		SharedPreferences prefs = getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		boolean firstLaunch = prefs.getBoolean("launch", true);

		//handles login
		if(savedInstanceState == null){



			boolean loggedIn = prefs.getBoolean("LoggedIn", false);

			if(!loggedIn || firstLaunch){

				Log.d("Baid", "Requesting login");
				Intent launch = new Intent(MainActivity.this, Login.class);
				startActivityForResult(launch, 1);
			}




		}


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sp = this.getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		//next time will not be first launch
		edit.putBoolean("launch", false);

		//connections is null to begin with


		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));


		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		//loads connections

	}

	public static String getUserID(){

		return ID;
	}



	//handles post login
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		SharedPreferences settings = MainActivity.this.getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = settings.edit();

		if (requestCode == 1) {

			//successfully retrieved profile data
			if(resultCode == RESULT_OK){



				fn = data.getStringExtra("First Name");
				ln = data.getStringExtra("Last Name");
				email = data.getStringExtra("Email");
				location = data.getStringExtra("Location");
				URL = data.getStringExtra("Url");
				ID = data.getStringExtra("ID");
				uploadToDatabase();

				

				//If we have loggedIn, then we can load connections
				Login.adapter.getContactListAsync(new ContactDataListener());
				


				//we successfully logged in
				edit.putBoolean("LoggedIn", true);


			}
			if (resultCode == RESULT_CANCELED) {
				//Write your code if there's no result

				//we did not log in 
				edit.putBoolean("LoggedIn", false);

				//error
				AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();  
				alertDialog.setTitle("Error ");  
				alertDialog.setMessage("Problem signing in wiht LinkedIn");
				alertDialog.setCanceledOnTouchOutside(true);
				alertDialog.show(); 

				//try sign in again
				Intent launch = new Intent(MainActivity.this, Login.class);
				startActivityForResult(launch, 1);

			}
			edit.commit();
		}
	}

	//upload data to database
	private void uploadToDatabase(){

		Log.d("Baid", "uploadToDatabase in MainActivity running");



		ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
		query.whereEqualTo("linkedinID", ID);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {

					//success
					Log.d("score", "Retrieved " + list.size() + " scores");
					if(list.size() == 0){
						//account does not exist

						ParseObject person = new ParseObject("Person");
						person.put("firstName", fn);
						person.put("lastName", ln);
						person.put("linkedinID", ID);
						person.put("email", email);
						person.put("headline", "");
						person.put("location", location);
						person.put("profilePhotoURL", URL);



						int[]nvgZero = {0, 0, 0, 0, 0};
						ArrayList<Integer>nvgData = new ArrayList<Integer>();
						for(int i = 0; i < nvgZero.length; i ++){

							nvgData.add(nvgZero[i]);
						}
						JSONArray nvg = new JSONArray(nvgData);


						//size 9-- 8 traits, and total
						int[]nvrZero = {0, 0, 0, 0, 0, 0, 0, 0, 0};
						int[]svrZero = {0, 0, 0, 0, 0, 0, 0, 0, 0};
						ArrayList<Integer>nvrData = new ArrayList<Integer>();
						ArrayList<Integer>svrData = new ArrayList<Integer>();
						for(int i = 0; i < nvrZero.length; i ++){

							nvrData.add(nvrZero[i]);
							svrData.add(svrZero[i]);
						}
						JSONArray nvr = new JSONArray(nvrData);
						JSONArray svr = new JSONArray(svrData);

						person.put("numberVouchesGiven", nvg);
						person.put("numberVouchesReceived",nvr);
						person.put("scoreVouchesReceived", svr);

						//keeps track of social shares
						boolean[] socialZero = {false, false, false, false, false};
						ArrayList<Boolean> socialData = new ArrayList<Boolean>();
						for(int i = 0; i < socialZero.length; i ++){

							socialData.add(socialZero[i]);
						}
						JSONArray social = new JSONArray(socialData);

						person.put("socialShares", social);
						//inital vouch score is 0
						person.put("totalVouchScore", 0);

						person.saveInBackground(new SaveCallback(){

							//when information has been saved
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								//indicates when user has successfully created an account
								if(e == null)
									Toast.makeText(MainActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
							}



						});
					}
					//account exits, we can load information
					else if(list.size() == 1){

						Log.d("Baid", "Loading data");
						int vouchScore = 0;
						int vouchesGiven = 0;						
						int vouchesReceived = 0;

						//there is only one person with the linkedin ID that was specified
						ParseObject person = list.get(0);
						
						//get person's email
						String personEmail = person.getString("email");
						//if the account exists, but we don't have email, this must be 
						//a connection's account
						if(personEmail == null){
						
							person.put("email", email);
							person.put("headline", "HEADLINE");
							person.put("location", location);
							JSONArray social = new JSONArray();
							JSONArray nvg =  new JSONArray();
							for(int i = 0; i < 5; i ++){
								social.put(false);
								nvg.put(0);
							}
							person.put("socialShares", social);
							person.put("numberVouchesGiven", nvg);
							person.saveInBackground(new SaveCallback(){

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									if(e == null){
										
										Log.d("Baid", "Information updated! Account created!");
									}
								}
								
								
							});
							
							
						}
						
						//get vouches score
						vouchScore = person.getInt("totalVouchScore");

						//get vouches received
						List<Integer>nvg = new ArrayList<Integer>();
						nvg = person.getList("numberVouchesGiven");
						if(nvg != null){

							//nvg should have a size of 5
							assert(nvg.size() == 5);
							for(int i = 1; i < nvg.size(); i ++){

								//be ware of a classcast exception
								vouchesGiven += nvg.get(i);

							}
						}


						//get vouches received
						List<Integer>nvr = new ArrayList<Integer>();
						nvr = person.getList("numberVouchesReceived");
						if(nvr != null){

							//should be of size 9
							assert(nvr.size() == 9);
							//the very last index keeps track of total
							vouchesReceived = nvr.get(nvr.size() - 1);
						}


						//display
						HomeFragment.setConnectionNumber(vouchScore, vouchesGiven, vouchesReceived);

					}
					//there should never be multiple objects with same linkedin ID
					else{

						AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();  
						alertDialog.setTitle("Uh oh!");  
						alertDialog.setMessage("Multiple ID's may exits\n" + ID);
						alertDialog.setCanceledOnTouchOutside(true);
						alertDialog.show(); 
						return;
					}


				}
				//error
				else {
					Log.d("score", "Error: " + e.getMessage());
					AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();  
					alertDialog.setTitle("Uh oh!");  
					alertDialog.setMessage("Something went wrong trying to retrive information.");
					alertDialog.setCanceledOnTouchOutside(true);
					alertDialog.show(); 
					return;
				}
			}
		});

	}


	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager =
				(SearchManager) getSystemService(MainActivity.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
		return true;
	}




	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new FindPeopleFragment();
			break;
		case 2:
			fragment = new PhotosFragment();
			break;
		case 3:
			fragment = new CommunityFragment();
			break;
		case 4:
			fragment = new PagesFragment();
			break;
		case 5:
			fragment = new WhatsHotFragment();
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}





}
