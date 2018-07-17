package com.grupp32.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import collection.Summoner;
import decoder.ImageStreamDecoder;
import widget.SlidingTabLayout;

/**
 * MainActivity represents a set of summoners. The UI contains of retrieved information from the Riot Games API.
 * Code samples for a scrollable fragment taken from ObservableScrollView (credits to ksoichiro; https://github.com/ksoichiro/Android-ObservableScrollView).
 *
 * @author Alexander Johansson
 */
public class MainActivity extends AppCompatActivity {

	private SlidingTabLayout tabLayout;
	private ViewPager viewPager;
	private NavigationAdapter pagerAdapter;

	private ArrayList<Summoner> summoners = new ArrayList<Summoner>();
	private Summoner[] summonerArr;
	private String patchVersion;
	private String searchedSummoner;
	private String searchedRegion;
	private ImageView flexibleImage;
	private TextView flexibleTitle;

	private int flexibleSpaceHeight;
	private int tabHeight;

	private static final String ARG_SUMMONERS = "ARG_SUMMONERS";
	private static final String ARG_SEARCHED_SUMMONER = "ARG_SEARCHED_SUMMONERS";
	private static final String ARG_VERSION = "ARG_VERSION";
	private static final String ARG_REGION = "ARG_REGION";

	private static final String DDRAGON_CHAMP_SPLASH_URL = "http://ddragon.leagueoflegends.com/cdn/img/champion/splash/";

	@Override
	public void onBackPressed() {
		// Event fired when user presses back-button in Android

		resetFragmentTimers();

		// Start intent to move back to LoginActivity, while keeping data in a bundle, for reusing if search matches
		Intent backIntent = new Intent(MainActivity.this, LoginActivity.class);
		Bundle extras = new Bundle();
		extras.putSerializable(ARG_SUMMONERS, summonerArr);
		extras.putString(ARG_VERSION, patchVersion);
		extras.putString(ARG_SEARCHED_SUMMONER, searchedSummoner);
		extras.putString(ARG_REGION, searchedRegion);
		backIntent.putExtras(extras);
		backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // For popping backstack, user cannot navigate back to this activity through back
		startActivity(backIntent);
	}

	@Override
	public void onPause() {
		// Toggles sound based on app pause (disables the sound from triggering when app is in background, resumes when in foreground)
		toggleFragmentSound();
		super.onPause();
	}

	@Override
	public void onResume() {
		// Toggles sound based on app resume (disables the sound from triggering when app is in background, resumes when in foreground)
		toggleFragmentSound();
		super.onResume();
	}

	/**
	 * Toggles sound enabled/disabled for the app. Forwards the toggle to all child fragments
	 */
	private void toggleFragmentSound() {
		// Get all child fragments
		FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
		List<Fragment> fragments = fragmentManager.getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) { // For every fragment
				if (fragment != null) { // Not null
					// Toggle sound in TabFragment
					((TabFragment) fragment).toggleSound();
				}
			}
		}
	}

	/**
	 * Resets timers in all fragments for the app. Forwards to child fragments.
	 */
	private void resetFragmentTimers() {
		// Get all child fragments
		FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
		List<Fragment> fragments = fragmentManager.getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) { // For every fragment
				if (fragment != null) { // Not null
					// Reset timers in TabFragment
					((TabFragment) fragment).resetTimers();
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set root view
		setContentView(R.layout.activity_main);

		flexibleImage = (ImageView) findViewById(R.id.image);
		flexibleTitle = (TextView) findViewById(R.id.title);

		// Catch bundle from LoginActivity
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			// Get serialized data from bundle
			summonerArr = (Summoner[]) bundle.getSerializable(ARG_SUMMONERS);
			patchVersion = bundle.getString(ARG_VERSION);
			searchedSummoner = bundle.getString(ARG_SEARCHED_SUMMONER);
			searchedRegion = bundle.getString(ARG_REGION);
			// Add summoners
			for (Summoner summoner : summonerArr) {
				summoners.add(summoner);
			}
		}

		// Create a view pager (tabs) and set its adapter to the pager (which has summoner information passed)
		pagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		pagerAdapter.setSummoners(summoners); // Pass summoner info
		pagerAdapter.setPatchVersion(patchVersion); // Pass version info
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOffscreenPageLimit(5); // Set this to cache all 5 fragments, avoid reloading them
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				// Event fires when a tab is selected
				try {
					// Switch flexible image
					new ImageSwitcher().execute(
							new URL(DDRAGON_CHAMP_SPLASH_URL + summoners.get(position).getChampion().getKey() + "_0.jpg")
					);

					// Set flexible title
					flexibleTitle.setText(summoners.get(position).getName());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		flexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
		tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);

		// Setup the tab layout
		tabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		tabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
		tabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));
		tabLayout.setDistributeEvenly(true);
		tabLayout.setViewPager(viewPager);

		ScrollUtils.addOnGlobalLayoutListener(tabLayout, new Runnable() {
			@Override
			public void run() {
				// Translates the tab
				translateTab(0, false);
			}
		});

		// Set view pager to 2 so it scrolls to the middle automatically
		viewPager.setCurrentItem(2);
	}

	/**
	 * Called by children Fragments when their scrollY are changed.
	 * They all call this method even when they are inactive
	 * but this Activity should listen only the active child,
	 * so each Fragments will pass themselves for Activity to check if they are active.
	 * <p/>
	 * Credits to ksoichiro
	 *
	 * @param scrollY scroll position of Scrollable
	 * @param s       caller Scrollable view
	 */
	public void onScrollChanged(int scrollY, Scrollable s) {
		FlexibleSpaceFragment fragment = (FlexibleSpaceFragment) pagerAdapter.getItemAt(viewPager.getCurrentItem());
		if (fragment == null) {
			return;
		}
		View view = fragment.getView();
		if (view == null) {
			return;
		}
		Scrollable scrollable = (Scrollable) view.findViewById(R.id.scroll);
		if (scrollable == null) {
			return;
		}
		if (scrollable == s) {
			// This method is called by not only the current fragment but also other fragments
			// when their scrollY is changed.
			// So we need to check the caller(S) is the current fragment.
			int adjustedScrollY = Math.min(scrollY, flexibleSpaceHeight - tabHeight);
			translateTab(adjustedScrollY, false);
			propagateScroll(adjustedScrollY);
		}
	}

	/**
	 * Sets current scrolled Y-axis
	 * <p/>
	 * Credits to ksoichiro
	 *
	 * @param scrollY y-axis
	 */
	private void propagateScroll(int scrollY) {
		// Set scrollY for the fragments that are not created yet
		pagerAdapter.setScrollY(scrollY);

		// Set scrollY for the active fragments
		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			// Skip current item
			if (i == viewPager.getCurrentItem()) {
				continue;
			}

			// Skip destroyed or not created item
			FlexibleSpaceFragment f = (FlexibleSpaceFragment) pagerAdapter.getItemAt(i);
			if (f == null) {
				continue;
			}

			View view = f.getView();
			if (view == null) {
				continue;
			}
			f.setScrollY(scrollY, flexibleSpaceHeight);
			f.updateFlexibleSpace(scrollY);
		}
	}

	/**
	 * Animates and displays views depending on the scroll axis.
	 * <p/>
	 * Credits to ksoichiro
	 *
	 * @param scrollY
	 * @param animated
	 */
	private void translateTab(int scrollY, boolean animated) {
		int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
		int flexTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
		View imageView = flexibleImage;
		View overlayView = findViewById(R.id.overlay);
		TextView titleView = flexibleTitle;

		// Translate overlay and image
		float flexibleRange = flexibleSpaceImageHeight - getActionBarSize();
		int minOverlayTransitionY = flexTabHeight - overlayView.getHeight();
		ViewHelper.setTranslationY(overlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
		ViewHelper.setTranslationY(imageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

		// Change alpha of overlay
		ViewHelper.setAlpha(overlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

		// Scale title text
		float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY - flexTabHeight) / flexibleRange, 0, 0.3f);
		ViewHelper.setPivotX(titleView, 0);
		ViewHelper.setPivotY(titleView, 0);
		ViewHelper.setScaleX(titleView, scale);
		ViewHelper.setScaleY(titleView, scale);

		// Translate title text
		int maxTitleTranslationY = flexibleSpaceImageHeight - flexTabHeight - getActionBarSize();
		int titleTranslationY = maxTitleTranslationY - scrollY;
		ViewHelper.setTranslationY(titleView, titleTranslationY);

		// If tabs are moving, cancel it to start a new animation.
		ViewPropertyAnimator.animate(tabLayout).cancel();
		// Tabs will move between the top of the screen to the bottom of the image.
		float translationY = ScrollUtils.getFloat(-scrollY + flexibleSpaceHeight - tabHeight, 0, flexibleSpaceHeight - tabHeight);
		if (animated) {
			// Animation will be invoked only when the current tab is changed.
			ViewPropertyAnimator.animate(tabLayout)
					.translationY(translationY)
					.setDuration(200)
					.start();
		} else {
			// When Fragments' scroll, translate tabs immediately (without animation).
			ViewHelper.setTranslationY(tabLayout, translationY);
		}
	}

	/**
	 * Retrieves current actionbar size.
	 *
	 * @return <code>int</code> - actionbar size
	 */
	protected int getActionBarSize() {
		TypedValue typedValue = new TypedValue();
		int[] textSizeAttr = new int[]{R.attr.actionBarSize};
		int indexOfAttrTextSize = 0;
		TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
		int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
		a.recycle();
		return actionBarSize;
	}

	/**
	 * This nested class handles information about summoners for the seperate tabs.
	 * The adapter creates the fragment upon request (when selecting the tab).
	 */
	private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {
		private ArrayList<Summoner> summoners = new ArrayList<Summoner>();
		private String patchVersion;

		private int scrollY;

		/**
		 * Constructor for this adapter, sets its superclass FragmentManager.
		 *
		 * @param fm fragment manager
		 */
		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * Sets the current summoners for the adapter to use.
		 *
		 * @param summoners array list with Summoner-objects
		 */
		public void setSummoners(ArrayList<Summoner> summoners) {
			this.summoners = summoners;
		}

		/**
		 * Sets the current patch version for the adapter to use.
		 *
		 * @param patchVersion patch version
		 */
		public void setPatchVersion(String patchVersion) {
			this.patchVersion = patchVersion;
		}

		/**
		 * Sets the Y-axis for the adapter to propagate.
		 *
		 * @param scrollY y-axis
		 */
		public void setScrollY(int scrollY) {
			this.scrollY = scrollY;
		}

		@Override
		protected Fragment createItem(int position) {
			// Creates a fragment and sets its arguments
			FlexibleSpaceFragment fragment;
			fragment = new TabFragment();
			fragment.setArguments(scrollY, summoners.get(position), patchVersion); // Pass summoner based on tab position, pass patch version
			return fragment;
		}

		@Override
		public int getCount() {
			return summoners.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return summoners.get(position).getChampion().getName();
		}
	}

	/**
	 * This nested class handles changing of the image for the flexible image view.
	 * This is handled asynchronously.
	 */
	private class ImageSwitcher extends AsyncTask<URL, Bitmap, Bitmap> {
		protected Bitmap doInBackground(URL... urls) {
			// Init a bitmap and the decoder
			Bitmap bitmap = null;
			ImageStreamDecoder decoder = new ImageStreamDecoder();
			try {
				// Try to decode from given URL (input stream), use decoder to load the bitmap into memory efficiently
				bitmap = decoder.decodeSampledBitmapFromStream((InputStream) urls[0].getContent(), 300, 240);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		protected void onPostExecute(Bitmap bitmap) {
			// If bitmap not null
			if (bitmap != null) {
				// Set image
				flexibleImage.setImageBitmap(bitmap);
			}
		}
	}
}