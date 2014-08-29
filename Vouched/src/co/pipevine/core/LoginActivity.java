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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import co.pipevine.vouchedapp.R;

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

	public static Person user;
	public static String fn, ln, email, location, url, headline, id;
	public static ParseObject parseUser;
	public static ParseObject parseScore;

	//variables for connections
	public static Connections contactList;
	public static HashMap<String, Person> allConnections;
	public static ArrayList<String> orderedNames;
	public static HashMap<String, Person> alphaMap;
	//keeps track of linkedinIDs of connections, we need to vouch for
	public static List<String> toVouchList;

	//accessor methods for user
	public static String getFirst(){return fn;}
	public static String getLast(){return ln;}
	public static String getEmail(){return email;}
	public static String getLocation(){return location;}
	public static String getPicture(){return url;}
	public static String getHeadline(){return headline;}
	public static String getUserID(){return id;}
	public static ParseObject getParseUser(){return parseUser;};
	public static ParseObject getParseScore(){return parseScore;};

	//mutators for user ParseObjects
	public static void setParseUser(ParseObject person){parseUser = person;}
	public static void setParseScore(ParseObject score){parseScore = score;}

	//accessors for connections
	public static HashMap<String, Person>getConnectionHashMap(){ return allConnections;}
	public static ArrayList<String> getOrderedNames(){return orderedNames;}
	public static HashMap<String, Person>getAlphaMap(){ return alphaMap;}
	public static List<String> getTvList(){return toVouchList;}
	
	
	//mutators for connections
	public static void setToVouchList(List<String> tv){toVouchList = tv;}
	

	LinearLayout background;
	Button signIn;

	public static LinkedInApiClient client = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//initialize parse
		Parse.initialize(this, "TQLt2PWNmJp6JBLYF95jnIDnxcoXdA2322CGoWdj", "aNTQtT0FzERvfFsQs3BknbxqQm49IB7dqE313WrF");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		//Not loggedin
		SharedPreferences settings = LoginActivity.this.getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = settings.edit();
		edit.putBoolean("LoggedIn", false);
		edit.commit();
		
		background = (LinearLayout) findViewById(R.id.spash_background);
		signIn = (Button) findViewById(R.id.signin);
		signIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//do nothing for now
				Log.d("Baid", "Launch login");
				startAutheniticate();
			}
		});


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
		client = factory.createLinkedInApiClient(accessToken);

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


					//NEW DATABASE CODE
					ParseQuery<ParseObject> search = ParseQuery.getQuery("User");
					search.whereEqualTo("linkedinID", id);
					search.findInBackground(new FindCallback<ParseObject>(){

						@Override
						public void done(List<ParseObject> objects,
								ParseException e) {
							// TODO Auto-generated method stub
							if(e == null){

								Log.d("Baid", "Users returned: " + objects.size());
								//if user doesn't not already exist, we will make an account for them 
								if(objects.size() == 0){



									final ParseObject person = new ParseObject("User");

									person.put("firstName", fn);
									person.put("lastName", ln);
									person.put("linkedinID", id);
									person.put("email", email);
									person.put("headline", headline);
									person.put("location", location);
									person.put("profilePhotoURL", url);

									//create toVouch list
									JSONArray tvUpload = new JSONArray();
									//keeps track of all connections
									JSONArray allContacts = new JSONArray();
									//keeps track of connections to vouch
									toVouchList = new ArrayList<String>();
									
									//fill toVouch with all connections, because this account is new
									for(Person contact: contactList.getPersonList()){

										tvUpload.put(contact.getId());
										allContacts.put(contact.getId());
										toVouchList.add(contact.getId());
									}

									//upload jsonarrays
									person.put("toVouch", tvUpload);
									person.put("connections", allContacts);


									person.saveInBackground(new SaveCallback(){

										@Override
										public void done(ParseException e) {
											// TODO Auto-generated method stub

											if(e == null){

												Log.d("Baid", "Account created");
												Toast.makeText(LoginActivity.this, "Account Created!", Toast.LENGTH_LONG).show();

												//store person as parseObject to use later
												parseUser = person;

												createScoreData();


											}// e == null
											else{

												Log.d("Baid", "Error creating account");
												Toast.makeText(LoginActivity.this, "Error creating account! Uh oh.", Toast.LENGTH_LONG).show();

											}//else
										}//done


									});//savecallback

								}//if object.size == 0
								else if(objects.size() == 1){

									final ParseObject person = objects.get(0);
									List<String>tvDatabase = new ArrayList<String>();
									tvDatabase = person.getList("toVouch");
									toVouchList = tvDatabase;
									//if toVouch is null, this is a partial account
									if(tvDatabase == null){


										//email has no been put in
										person.put("email", email);
										//headline, location, picture are updates
										person.put("headline", headline);
										person.put("location", location);
										person.put("profilePhotoURL", url);

										//create toVouch list
										JSONArray tvUpload = new JSONArray();
										//keeps track of all connections
										JSONArray allContacts = new JSONArray();

										//fill toVouch with all connections, because this account is new
										for(Person contact: contactList.getPersonList()){

											tvUpload.put(contact.getId());
											allContacts.put(contact.getId());
										}

										//upload jsonarrays
										person.put("toVouch", tvUpload);
										person.put("connections", allContacts);

										person.saveInBackground(new SaveCallback(){

											@Override
											public void done(ParseException e) {
												// TODO Auto-generated method stub
												if(e == null){

													Toast.makeText(LoginActivity.this, "Partial Account Updated!", Toast.LENGTH_SHORT).show();
													Log.d("Baid", "Updated Account");
													//store user to access later
													parseUser = person;
													createScoreData();
												}

											}


										});

									}//if toVouch is null
									//otherwise, the current user already has an existing full account
									else{

										//store user to use later
										parseUser = person;

										createScoreData();
									}




								}//else objects.size == 1
								else{

									Log.d("score", "Error: " + e.getMessage());
									AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();  
									alertDialog.setTitle("Uh oh!");  
									alertDialog.setMessage("Something went wrong trying to retrive information.");
									alertDialog.setCanceledOnTouchOutside(true);
									alertDialog.show(); 
									return;
								}
							}//e == null

						}//done


					});//search query

					
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
					//tv.setText(getLast() + ", " + getFirst() + "\n" + getEmail() + "\n" + getHeadline() + "\n" + getLocation() + "\nID: " + getUserID() + "\n" + getPicture() + "\n");
					
					//we are now loggedin
					SharedPreferences settings = LoginActivity.this.getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
					SharedPreferences.Editor edit = settings.edit();
					edit.putBoolean("LoggedIn", true);
					edit.commit();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				}
			}
		}.execute();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		finishAuthenticate(intent.getData());
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences pref = getSharedPreferences("co.pipevine.core", Context.MODE_PRIVATE);
		boolean loggedIn = pref.getBoolean("LoggedIn", false);
		if(loggedIn){

			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	//creates scoreDataObject for current user or stores it if it already exists
	private void createScoreData(){

		//if parseUser is null, then we cannot create scoreData
		if(parseUser == null){

			Log.d("score", "Error: parseUser is null ");
			AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();  
			alertDialog.setTitle("Uh oh!");  
			alertDialog.setMessage("ParseUser is not initialized");
			alertDialog.setCanceledOnTouchOutside(true);
			alertDialog.show(); 
			return;

		}

		ParseQuery<ParseObject> query = ParseQuery.getQuery("ScoreData");
		query.whereEqualTo("scoreForUser", parseUser);
		query.findInBackground(new FindCallback<ParseObject>(){

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){

					//if no scoredata exits, we will make one
					if(objects.size() == 0){

						final ParseObject score = new ParseObject("ScoreData");

						score.put("professionalismNumber", 0);
						score.put("professionalismScore", 0);
						score.put("integrityNumber", 0);
						score.put("integrityScore", 0);
						score.put("communicationNumber", 0);
						score.put("communicationScore", 0);
						score.put("innovationNumber", 0);
						score.put("innovationScore", 0);
						score.put("productivityNumber", 0);
						score.put("productivityScore", 0);
						score.put("adaptabilityNumber", 0);
						score.put("adaptabilityScore", 0);
						score.put("leadershipNumber", 0);
						score.put("leadershipScore", 0);
						score.put("teamworkNumber", 0);
						score.put("teamworkScore", 0);
						score.put("totalVouchScore", 0);
						score.put("totalVouchesReceived", 0);
						score.put("totalVouchesGiven", 0);
						score.put("vouchOne", 0);
						score.put("vouchTwo", 0);
						score.put("vouchThree", 0);
						score.put("vouchFour", 0);
						score.put("skips", 0);
						score.put("scoreForUser", parseUser);

						score.saveInBackground(new SaveCallback(){

							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e == null){

									parseScore = score;
									Log.d("Baid", "scoredata Successfully created!");
								}
							}


						});

					}//objects size == 0
					else if(objects.size() == 1){


						ParseObject score = objects.get(0);
						parseScore = score;
						Log.d("Baid", "Successfully retrieved parseScore!");
					}// objects size == 1
					else{

						Log.d("Baid", "Error. Incorrect number of scoreData");
					}
				}// e== null

			}



		});




	}

}