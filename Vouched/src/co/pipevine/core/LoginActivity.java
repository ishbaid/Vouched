package co.pipevine.core;




import info.androidhive.slidingmenu.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientException;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Connections;
import com.google.code.linkedinapi.schema.Person;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class LoginActivity extends Activity {

	// /change keys!!!!!!!!!!

	static final String CONSUMER_KEY = "77ueptpbgjy2oe";
	static final String CONSUMER_SECRET = "nq72mQZmcTVjSly8";

	static final String APP_NAME = "LITest";
	static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	static final String OAUTH_CALLBACK_HOST = "litestcalback";
	static final String OAUTH_CALLBACK_URL = String.format("%s://%s",
			OAUTH_CALLBACK_SCHEME, OAUTH_CALLBACK_HOST);
	static final String OAUTH_QUERY_TOKEN = "oauth_token";
	static final String OAUTH_QUERY_VERIFIER = "oauth_verifier";
	static final String OAUTH_QUERY_PROBLEM = "oauth_problem";

	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
			.getInstance().createLinkedInOAuthService(CONSUMER_KEY,
					CONSUMER_SECRET);
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory
			.newInstance(CONSUMER_KEY, CONSUMER_SECRET);

	static final String OAUTH_PREF = "LIKEDIN_OAUTH";
	static final String PREF_TOKEN = "token";
	static final String PREF_TOKENSECRET = "tokenSecret";
	static final String PREF_REQTOKENSECRET = "requestTokenSecret";

	TextView tv = null;
	public static Person user;
	public static String fn, ln, email, location, url, headline, id;

	//variables for connections
	public static Connections contactList;
	public static HashMap<String, Person> allConnections;
	public static ArrayList<String> orderedNames;
	public static HashMap<String, Person> alphaMap;

	//accessor methods for user
	public static String getFirst(){return fn;}
	public static String getLast(){return ln;}
	public static String getEmail(){return email;}
	public static String getLocation(){return location;}
	public static String getPicture(){return url;}
	public static String getHeadline(){return headline;}
	public static String getUserID(){return id;}

	//accessors for connections
	public static HashMap<String, Person>getConnectionHashMap(){ return allConnections;}
	public static ArrayList<String> getOrderedNames(){return orderedNames;}
	public static HashMap<String, Person>getAlphaMap(){ return alphaMap;}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		//initialize parse
		Parse.initialize(this, "TQLt2PWNmJp6JBLYF95jnIDnxcoXdA2322CGoWdj", "aNTQtT0FzERvfFsQs3BknbxqQm49IB7dqE313WrF");

		super.onCreate(savedInstanceState);
		tv = new TextView(this);
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);

			}
		});

		setContentView(tv);
		final SharedPreferences pref = getSharedPreferences(OAUTH_PREF,
				MODE_PRIVATE);
		final String token = pref.getString(PREF_TOKEN, null);
		final String tokenSecret = pref.getString(PREF_TOKENSECRET, null);
		if (token == null || tokenSecret == null) {
			startAutheniticate();
		} else {
			showCurrentUser(new LinkedInAccessToken(token, tokenSecret));
		}

	}

	void startAutheniticate() {
		new AsyncTask<Void, Void, LinkedInRequestToken>() {

			@Override
			protected LinkedInRequestToken doInBackground(Void... params) {
				return oAuthService.getOAuthRequestToken(OAUTH_CALLBACK_URL);
			}

			@Override
			protected void onPostExecute(LinkedInRequestToken liToken) {
				final String uri = liToken.getAuthorizationUrl();
				getSharedPreferences(OAUTH_PREF, MODE_PRIVATE)
				.edit()
				.putString(PREF_REQTOKENSECRET,
						liToken.getTokenSecret()).commit();
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			}
		}.execute();
	}

	void finishAuthenticate(final Uri uri) {
		if (uri != null && uri.getScheme().equals(OAUTH_CALLBACK_SCHEME)) {
			final String problem = uri.getQueryParameter(OAUTH_QUERY_PROBLEM);
			if (problem == null) {

				new AsyncTask<Void, Void, LinkedInAccessToken>() {

					@Override
					protected LinkedInAccessToken doInBackground(Void... params) {
						final SharedPreferences pref = getSharedPreferences(
								OAUTH_PREF, MODE_PRIVATE);
						final LinkedInAccessToken accessToken = oAuthService
								.getOAuthAccessToken(
										new LinkedInRequestToken(
												uri.getQueryParameter(OAUTH_QUERY_TOKEN),
												pref.getString(
														PREF_REQTOKENSECRET,
														null)),
														uri.getQueryParameter(OAUTH_QUERY_VERIFIER));
						pref.edit()
						.putString(PREF_TOKEN, accessToken.getToken())
						.putString(PREF_TOKENSECRET,
								accessToken.getTokenSecret())
								.remove(PREF_REQTOKENSECRET).commit();
						return accessToken;
					}

					@Override
					protected void onPostExecute(LinkedInAccessToken accessToken) {
						showCurrentUser(accessToken);
					}
				}.execute();

			} else {
				Toast.makeText(this,
						"Appliaction down due OAuth problem: " + problem,
						Toast.LENGTH_LONG).show();
				finish();
			}

		}
	}

	void clearTokens() {
		getSharedPreferences(OAUTH_PREF, MODE_PRIVATE).edit()
		.remove(PREF_TOKEN).remove(PREF_TOKENSECRET)
		.remove(PREF_REQTOKENSECRET).commit();
	}

	void showCurrentUser(final LinkedInAccessToken accessToken) {
		final LinkedInApiClient client = factory
				.createLinkedInApiClient(accessToken);

		new AsyncTask<Void, Void, Object>() {

			ProgressDialog progDailog;
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
		          progDailog = new ProgressDialog(LoginActivity.this);
		            progDailog.setMessage("Loading...");
		            progDailog.setIndeterminate(false);
		            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		            progDailog.setCancelable(true);
		            progDailog.show();
			}

			@Override
			protected Object doInBackground(Void... params) {
				try {

					final Person p = client.getProfileForCurrentUser(EnumSet.of(
							ProfileField.ID, ProfileField.FIRST_NAME,
							ProfileField.LAST_NAME, ProfileField.HEADLINE, ProfileField.EMAIL_ADDRESS,
							ProfileField.LOCATION_NAME, ProfileField.PICTURE_URL));
					user = p;

					fn = p.getFirstName();
					ln = p.getLastName();
					email = p.getEmailAddress();
					headline = p.getHeadline();
					id = p.getId();
					location = p.getLocation().getName();
					url = p.getPictureUrl();

					contactList = client.getConnectionsForCurrentUser();
					orderedNames = new ArrayList<String>();
					alphaMap = new HashMap<String, Person>();

					allConnections = new HashMap<String, Person>();
					for(Person person: contactList.getPersonList()){

						allConnections.put(person.getId(), person);
						String name = person.getLastName() + ", " + person.getFirstName();
						if(!name.equals("private, private")){

							orderedNames.add(name);
							alphaMap.put(name, person);
						}
					}
					//sorts names
					Collections.sort(orderedNames);

					Log.d("Baid", allConnections.size() + " connections loaded");
					Log.d("Baid", "orderedList size: " + orderedNames.size() + " AlphaHash size: " + alphaMap.size());


					//query database for current user
					ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
					query.whereEqualTo("linkedinID", id);
					query.findInBackground(new FindCallback<ParseObject>(){

						@Override
						public void done(List<ParseObject> objects,
								ParseException e) {
							// TODO Auto-generated method stub
							if(e == null){

								Log.d("Baid", "Search yielded " + objects.size() + " result(s)");
								if(objects.size() == 0){
									//account does not exist

									ParseObject person = new ParseObject("Person");
									person.put("firstName", fn);
									person.put("lastName", ln);
									person.put("linkedinID", id);
									person.put("email", email);
									person.put("headline", headline);
									person.put("location", location);
									person.put("profilePhotoURL", url);



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

									//create toVouch and vouchedFor list
									JSONArray tvUpload = new JSONArray();
									//vouchedFor should then empty
									JSONArray vfUpload = new JSONArray();

									//fill toVouch with all connections, because this account is new
									for(Person contact: contactList.getPersonList()){

										tvUpload.put(contact.getId());
									}

									//only upload if we list was empty to begin with
									person.put("toVouch", tvUpload);
									person.put("vouchedFor", vfUpload);


									person.saveInBackground(new SaveCallback(){

										//when information has been saved
										@Override
										public void done(ParseException e) {
											// TODO Auto-generated method stub
											//indicates when user has successfully created an account
											if(e == null)
												Toast.makeText(LoginActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
										}



									});
								}// if objects.size == 0
								//account exits, we can load information
								else if(objects.size() == 1){

									Log.d("Baid", "Loading data");
									int vouchScore = 0;
									int vouchesGiven = 0;						
									int vouchesReceived = 0;

									//CHECK FOR UPDATES/NEW CONNECTIONS HERE!!!

									//there is only one person with the linkedin ID that was specified
									ParseObject person = objects.get(0);


									//get vouches received
									List<Integer>nvr = new ArrayList<Integer>();
									nvr = person.getList("numberVouchesReceived");

									//get vouches received
									List<Integer>nvg = new ArrayList<Integer>();
									nvg = person.getList("numberVouchesGiven");

									//nvr is not null, but nvg is null, then this must be a partial account
									if(nvg == null && nvr != null){

										person.put("email", email);
										person.put("headline", "HEADLINE");
										person.put("location", location);
										JSONArray social = new JSONArray();
										JSONArray nvgToUpload =  new JSONArray();
										for(int i = 0; i < 5; i ++){
											social.put(false);
											nvgToUpload.put(0);
										}
										person.put("socialShares", social);
										person.put("numberVouchesGiven", nvgToUpload);



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


									if(nvg != null){

										//nvg should have a size of 5
										assert(nvg.size() == 5);
										for(int i = 1; i < nvg.size(); i ++){

											//be ware of a classcast exception
											vouchesGiven += nvg.get(i);

										}
									}



									if(nvr != null){

										//should be of size 9
										assert(nvr.size() == 9);
										//the very last index keeps track of total
										vouchesReceived = nvr.get(nvr.size() - 1);
									}
									Log.d("Baid", "VS: " + vouchScore + " Given: " + vouchesGiven + " Receieved: " + vouchesReceived);

								}//else if objects.size == 0
								//there should never be multiple objects with same linkedin ID
								else{

									AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();  
									alertDialog.setTitle("Uh oh!");  
									alertDialog.setMessage("Multiple ID's may exits\n" + id);
									alertDialog.setCanceledOnTouchOutside(true);
									alertDialog.show(); 
									return;
								}//else
							}//if e == null
							//error
							else {
								Log.d("score", "Error: " + e.getMessage());
								AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();  
								alertDialog.setTitle("Uh oh!");  
								alertDialog.setMessage("Something went wrong trying to retrive information.");
								alertDialog.setCanceledOnTouchOutside(true);
								alertDialog.show(); 
								return;
							}

						}//done


					});//findCallback

					// /////////////////////////////////////////////////////////
					// here you can do client API calls ...
					// client.postComment(arg0, arg1);
					// client.updateCurrentStatus(arg0);
					// or any other API call
					// (this sample only check for current user
					// and pass it to onPostExecute)
					// /////////////////////////////////////////////////////////
					return p;
				} catch (LinkedInApiClientException ex) {
					return ex;
				}
			}

			@Override
			protected void onPostExecute(Object result) {
				
				progDailog.dismiss();
				if (result instanceof Exception) {
					//result is an Exception :) 
					final Exception ex = (Exception) result;
					clearTokens();
					Toast.makeText(
							LoginActivity.this,
							"Appliaction down due LinkedInApiClientException: "
									+ ex.getMessage()
									+ " Authokens cleared - try run application again.",
									Toast.LENGTH_LONG).show();
					finish();
				} else if (result instanceof Person) {
					final Person p = (Person) result;
					tv.setText(getLast() + ", " + getFirst() + "\n" + getEmail() + "\n" + getHeadline() + "\n" + getLocation() + "\nID: " + getUserID() + "\n" + getPicture() + "\n");
					
					for(String name: orderedNames){
						
						tv.setText(tv.getText() + name + "\n");
					}
				}
			}
		}.execute();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		finishAuthenticate(intent.getData());
	}
}