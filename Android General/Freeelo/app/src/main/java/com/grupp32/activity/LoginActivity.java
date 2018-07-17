package com.grupp32.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;

import org.json.JSONException;

import java.util.HashSet;
import java.util.Iterator;

import collection.CurrentGame;
import collection.Summoner;
import json.JSONParser;
import json.JSONRequester;
import version.ResourceUtil;
import version.VersionUtil;

/**
 * LoginActivity represents the first landing page of Freeelo.
 * The UI contains a spinner, a text field and a button that can be interacted with.
 *
 * @author Alexander Johansson, Sigvard Nilsson
 */
public class LoginActivity extends AppCompatActivity {

	private AutoCompleteTextView summonerName;
	private Spinner region;
	private CircularProgressButton search;

	private boolean searchedPressed = false;

	private String[] autoCompleteArray;
	private HashSet autoCompleteSet;
	private SharedPreferences prefs;

	private Summoner[] previouslySearchedSummonerArr;
	private String previouslySearchedPatchVersion;
	private String previouslySearchedSummoner;
	private String previouslySearchedRegion;

	private static final String ARG_SUMMONERS = "ARG_SUMMONERS";
	private static final String ARG_SEARCHED_SUMMONER = "ARG_SEARCHED_SUMMONERS";
	private static final String ARG_VERSION = "ARG_VERSION";
	private static final String ARG_REGION = "ARG_REGION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set root view
		setContentView(R.layout.activity_login);

		// Find the button view, and assign it a listener
		search = (CircularProgressButton) findViewById(R.id.search_button);
		search.setIndeterminateProgressMode(true); // Sets the CircularProgressButton to be indeterminate (no 0-100 progress bar, always spinning)
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!searchedPressed && !checkPreviouslySearched()) { // Check if previously searched, searchPressed check disables button spamming
					searchedPressed = true;
					search.setProgress(1);
					checkCurrentGame();
				}
			}
		});

		setupSharedPrefs();
		setupSearchBar();

		// Find spinner view and set its adapter from layout, also its content from strings (regions)
		region = (Spinner) findViewById(R.id.region);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.regions, R.layout.textview_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		region.setAdapter(adapter);

		// Get intent (if forwarded from any previous page, in case of back-pressing from MainActivity)
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			// Set data to match previous search from MainActivity from bundle
			previouslySearchedSummonerArr = (Summoner[]) bundle.getSerializable(ARG_SUMMONERS);
			previouslySearchedPatchVersion = bundle.getString(ARG_VERSION);
			previouslySearchedSummoner = bundle.getString(ARG_SEARCHED_SUMMONER);
			previouslySearchedRegion = bundle.getString(ARG_REGION);
			summonerName.setText(previouslySearchedSummoner);
			region.setSelection(((ArrayAdapter) region.getAdapter()).getPosition(previouslySearchedRegion));
		}
	}

	/**
	 * Setups SharedPreferences for Freeelo app, allows us to store summoner names that have been entered
	 */
	private void setupSharedPrefs() {
		// Get any names previously entered
		prefs = getSharedPreferences("autocomplete", MODE_PRIVATE);
		HashSet hashSet = (HashSet) prefs.getStringSet("searchedNames", new HashSet<String>());
		// Clone
		autoCompleteSet = (HashSet) hashSet.clone();
		autoCompleteArray = new String[hashSet.size()];
		Iterator it = hashSet.iterator();
		int index = 0;
		while (it.hasNext()) {
			// Add to string array (for later adapter usage)
			autoCompleteArray[index++] = (String) it.next();
		}
	}

	/**
	 * Setups the "Search"-bar and sets previously searched names from SharedPreferences.
	 * <code>setupSharedPrefs();</code> needs to be called beforehand.
	 */
	private void setupSearchBar() {
		summonerName = (AutoCompleteTextView) findViewById(R.id.summoner_name);
		summonerName.requestFocus();
		summonerName.setOnEditorActionListener(new AutoCompleteTextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// Checks if user pressed "Done" on the soft keyboard
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					if (!searchedPressed && !checkPreviouslySearched()) { // Check if previously searched
						searchedPressed = true;
						search.setProgress(1);
						checkCurrentGame();
					}
				}
				return false;
			}
		});
		// Set adapter to the string array from SharedPrefs
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoCompleteArray);
		summonerName.setAdapter(adapter);
	}

	/**
	 * Checks if the previously searched summoner matches the current search, if so, move directly to next intent (data is already stored)
	 *
	 * @return <code>boolean</code> - true if previously searched, false if not
	 */
	private boolean checkPreviouslySearched() {
		if (previouslySearchedPatchVersion != null && previouslySearchedSummonerArr != null && previouslySearchedSummoner != null &&
				previouslySearchedSummoner.equals(summonerName.getText().toString().trim()) && previouslySearchedRegion != null) {
			startIntentToMain(previouslySearchedSummonerArr, previouslySearchedPatchVersion, previouslySearchedSummoner, previouslySearchedRegion);
			return true;
		}

		return false;
	}

	/**
	 * Starts a current game check using the information input from the user.
	 */
	private void checkCurrentGame() {
		String summoner = summonerName.getText().toString().trim();
		if (!summoner.isEmpty()) { // If summoner name not empty
			String region = this.region.getSelectedItem().toString();

			GetCurrentGame game = new GetCurrentGame();
			game.execute(summoner, region); // Execute the check, input summoner name and region
		} else { // Show error
			Toast.makeText(getApplication(), getString(R.string.enter_name), Toast.LENGTH_LONG).show();
			search.setProgress(0);
			searchedPressed = false;
		}
	}

	/**
	 * Forwards the user to next activity/intent (screen).
	 *
	 * @param summonerArr      summoners to be passed with the intent
	 * @param patchVersion     patch version to be passed with the intent
	 * @param searchedSummoner summoner name searched
	 * @param searchedRegion   summoner region
	 */
	private void startIntentToMain(Summoner[] summonerArr, String patchVersion, String searchedSummoner, String searchedRegion) {
		// Add summoner name to previously searched list (for autocomplete)
		autoCompleteSet.add(searchedSummoner);
		prefs.edit().putStringSet("searchedNames", autoCompleteSet).apply();
		// Start intent to next activity
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		Bundle extras = new Bundle();
		// Set information to pass
		extras.putSerializable(ARG_SUMMONERS, summonerArr);
		extras.putString(ARG_VERSION, patchVersion);
		extras.putString(ARG_SEARCHED_SUMMONER, searchedSummoner);
		extras.putString(ARG_REGION, searchedRegion);
		intent.putExtras(extras);
		startActivity(intent);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		// Event called whenever a motion (click, focus, touch etc.) is issued
		if (v instanceof EditText) { // If view matches EditText (in our case the text field, AutoCompleteTextView inherits EditText)
			View w = getCurrentFocus();
			if (w != null) {
				int scrCoords[] = new int[2];
				w.getLocationOnScreen(scrCoords);
				float x = event.getRawX() + w.getLeft() - scrCoords[0];
				float y = event.getRawY() + w.getTop() - scrCoords[1];

				// Check if touch is outside of bounds
				if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
					// Hide keyboard
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(w.getWindowToken(), 0);
				}
			}
		}

		return ret;
	}

	/**
	 * This nested class is in charge of handling game requests asynchronously,
	 * allowing a worker thread to do its magic and later updating UI threads.
	 */
	private class GetCurrentGame extends AsyncTask<String, Integer, CurrentGame> {
		private String summonerName;
		private VersionUtil versionUtil;

		@Override
		protected CurrentGame doInBackground(String... params) {
			summonerName = params[0];
			String region = params[1];

			// Initialize parser and requester
			Context context = getApplicationContext();
			ResourceUtil resourceUtil = new ResourceUtil(context);
			JSONParser parser = new JSONParser(resourceUtil);
			JSONRequester requester = new JSONRequester(parser);
			versionUtil = new VersionUtil(context, parser, requester, resourceUtil, region);
			versionUtil.setupAndBuildResources();

			// Check if a search is being conducted for the first time, or if patch data is not latest version
			if (versionUtil.isFirstTime() || !versionUtil.isLatestVersion()) {
				publishProgress(2); // Forwards to onProgressUpdate()
				versionUtil.updateVersion();

				publishProgress(100);

				publishProgress(3);
			} else { // Otherwise just update resources
				try {
					parser.updateResources();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// Conduct a new search
			CurrentGame game = new CurrentGame(summonerName, region, parser, requester);
			try {
				game.searchCurrentGame();

				publishProgress(100);

				try {
					// Sleep to show "Search"-button animation
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return game;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			switch (values[0]) {
				case 2:
					Toast.makeText(getApplication(), getString(R.string.patch_data_notcurrent), Toast.LENGTH_LONG).show();
					break;
				case 3:
					Toast.makeText(getApplication(), getString(R.string.patch_data_complete_proceeding), Toast.LENGTH_LONG).show();
					break;
			}

			search.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(CurrentGame result) {
			super.onPostExecute(result);
			// Event fired when worker thread has a result
			if (result != null) { // Send to MainActivity if data has been retrieved
				Summoner[] summoners = result.getSummoners();
				search.setProgress(0);
				startIntentToMain(summoners, versionUtil.getVersion(), summonerName, (String) region.getSelectedItem());
			} else { // Display error and reset button if not
				search.setProgress(-1);
				Toast.makeText(getApplication(), String.format(getString(R.string.not_in_game), summonerName), Toast.LENGTH_LONG).show();

				// Reset button with a seperate thread (does not work on onPostExecute - it's an UI thread)
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						search.setProgress(0);
					}
				}, 1000);
			}

			searchedPressed = false;
		}
	}
}