package com.grupp32.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import collection.Summoner;
import decoder.ImageStreamDecoder;

/**
 * TabFragment represents the information in MainActivity.
 * The UI is filled with data from current summoner, based on view pager (tab) position.
 * Code samples for a scrollable fragment taken from ObservableScrollView (credits to ksoichiro; https://github.com/ksoichiro/Android-ObservableScrollView).
 *
 * @author Alexander Johansson, Christoffer Nilsson
 */
public class TabFragment extends FlexibleSpaceFragment<ObservableScrollView> implements View.OnClickListener, View.OnLongClickListener {
    private ImageButton spell1;
    private ImageButton spell2;
    private ImageButton ultimate;

    private ProgressBar progressBarSpell1;
    private ProgressBar progressBarSpell2;
    private ProgressBar progressBarUltimate;
    private TextView textSpell1;
    private TextView textSpell2;
    private TextView textUltimate;

    private int textColor;
    private int textCriticalColor;
    private TextToSpeech textToSpeech;

    private CountDownTimer[] timers = new CountDownTimer[3];
    private boolean playSound = true;

    private Summoner tabSummoner;

    private static final String DDRAGON_SPELL_URL = "http://ddragon.leagueoflegends.com/cdn/%s/img/spell/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set root view
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        // Fix for a viewgroup touch bug, credits to ksoichiro
        final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        scrollView.setTouchInterceptionViewGroup((ViewGroup) view.findViewById(R.id.fragment_root));

        // Retrieve arguments passed from activity
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SCROLL_Y)) {
            // If y-axis was changed (to match scrolled view when switching fragment)
            final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
            ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
                @Override
                public void run() {
                    // Scroll to y-axis
                    scrollView.scrollTo(0, scrollY);
                }
            });
            updateFlexibleSpace(scrollY, view); // Update space with new axis
        } else {
            updateFlexibleSpace(0, view);
        }

        // Init listeners, both click and longclick
        spell1 = (ImageButton) view.findViewById(R.id.summoner_spell1);
        spell2 = (ImageButton) view.findViewById(R.id.summoner_spell2);
        ultimate = (ImageButton) view.findViewById(R.id.summoner_ultimate);
        spell1.setOnClickListener(this);
        spell2.setOnClickListener(this);
        ultimate.setOnClickListener(this);
        spell1.setOnLongClickListener(this);
        spell2.setOnLongClickListener(this);
        ultimate.setOnLongClickListener(this);

        progressBarSpell1 = (ProgressBar) view.findViewById(R.id.progress_bar_spell1);
        progressBarSpell2 = (ProgressBar) view.findViewById(R.id.progress_bar_spell2);
        progressBarUltimate = (ProgressBar) view.findViewById(R.id.progress_bar_ultimate);

        textSpell1 = (TextView) view.findViewById(R.id.text_spell1);
        textSpell2 = (TextView) view.findViewById(R.id.text_spell2);
        textUltimate = (TextView) view.findViewById(R.id.text_ultimate);

        // Parse colors for cooldown usage
        textColor = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorProgressNumber)));
        textCriticalColor = Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorProgressNumberCritical)));

        // Initialize text-to-speech
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK); // Set locale to UK (for british english)
                }
            }
        });

        final TextView runes = (TextView) view.findViewById(R.id.runes);
        final TextView masteries = (TextView) view.findViewById(R.id.masteries);
        final TextView tier = (TextView) view.findViewById(R.id.tier_text);

        final ImageView imageTier = (ImageView) view.findViewById(R.id.imgTier);

        final TextView lpWinLoss = (TextView) view.findViewById(R.id.lp_win_loss);
        final TextView winRatio = (TextView) view.findViewById(R.id.win_ratio);
        final TextView champWinRatio = (TextView) view.findViewById(R.id.champ_win_ratio);

        // If args were passed (summoner information)
        if (args != null && args.containsKey(ARG_SUMMONER)) {
            Summoner summoner = (Summoner) args.getSerializable(ARG_SUMMONER);
            tabSummoner = summoner;
            if (summoner != null) { // Summoner not null
                // Set its content to UI elements
                String patchVersion = args.getString(ARG_VERSION);
                runes.setText(summoner.getRunes().toString());
                masteries.setText(summoner.getMasteries());

                int wins = summoner.getWins();
                int losses = summoner.getLosses();
                int champWins = summoner.getChampion().getWins();
                int champLosses = summoner.getChampion().getLosses();
                if ((wins + losses) != 0) { // Makes sure percentage doesn't divide by zero
                    // Set LP
                    lpWinLoss.setText(String.format(lpWinLoss.getText().toString(), summoner.getLeaguePoints(), wins, losses));
                    double percentage = (double) wins / ((double) wins + (double) losses); // No zeroes
                    // Set winratio
                    winRatio.setText(String.format(winRatio.getText().toString(), percentage * 100, "%"));
                } else { // Just set LP and 0% winrate
                    lpWinLoss.setText(String.format(lpWinLoss.getText().toString(), summoner.getLeaguePoints(), wins, losses));
                    winRatio.setText(String.format(winRatio.getText().toString(), 0.0, "%"));
                }

                if ((champWins + champLosses) != 0) { // Makes sure percentage doesn't divide by zero
                    double percentage = (double) champWins / ((double) champWins + (double) champLosses);
                    champWinRatio.setText(String.format(champWinRatio.getText().toString(), percentage * 100, "%"));
                } else {
                    champWinRatio.setText("");
                }

                Context context = getActivity();
                int imageResource;
                // Gets lowercase tier and division
                String tierText = summoner.getTier().toLowerCase();
                String divisionText = summoner.getDivision().toLowerCase();
                // If tier equals to the lone tiers, don't set division
                if (tierText.equals("challenger") || tierText.equals("master") || tierText.equals("provisional")) {
                    imageResource = context.getResources().getIdentifier("@drawable/" + tierText, null, context.getPackageName());
                    tier.setText(summoner.getTier());
                } else { // Set division if summoner is diamond or lower (to match drawable image names ie. "diamond_iv"
                    imageResource = context.getResources().getIdentifier("@drawable/" + tierText + "_" + divisionText, null, context.getPackageName());
                    String tempText = summoner.getTier() + " " + summoner.getDivision();
                    tier.setText(tempText);
                }

                // Start decoder and decode image from resources (drawable)
                ImageStreamDecoder decoder = new ImageStreamDecoder();
                imageTier.setImageBitmap(decoder.decodeSampledBitmapFromResource(context.getResources(), imageResource, 200, 200));

                try {
                    // Calls for image switches to match current summoner spells and champion ultimate images
                    new ImageSwitcher().execute(
                            new URL(String.format(DDRAGON_SPELL_URL, patchVersion) + summoner.getSpell1().getImageName()),
                            new URL(String.format(DDRAGON_SPELL_URL, patchVersion) + summoner.getSpell2().getImageName()),
                            new URL(String.format(DDRAGON_SPELL_URL, patchVersion) + summoner.getChampion().getUltimateImageName())
                    );
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        scrollView.setScrollViewCallbacks(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Manually shutdown the text-to-speech session to avoid any memory leaks
        textToSpeech.shutdown();
    }

    @Override
    protected void updateFlexibleSpace(int scrollY) {
        // Sometimes scrollable.getCurrentScrollY() and the real scrollY has different values.
        // As a workaround, we should call scrollVerticallyTo() to make sure that they match.
        Scrollable s = getScrollable();
        s.scrollVerticallyTo(scrollY);

        // If scrollable.getCurrentScrollY() and the real scrollY has the same values,
        // calling scrollVerticallyTo() won't invoke scroll (or onScrollChanged()), so we call it here.
        // Calling this twice is not a problem as long as updateFlexibleSpace(int, View) has idempotence.
        updateFlexibleSpace(scrollY, getView());
    }

    @Override
    protected void updateFlexibleSpace(int scrollY, View view) {
        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);

        // Get parent activity
        MainActivity parentActivity = (MainActivity) getActivity();
        if (parentActivity != null) {
            // Call scrollChanged to match y-axis
            parentActivity.onScrollChanged(scrollY, scrollView);
        }
    }

    /**
     * Sets the resource to greyscale (imagebutton).
     *
     * @param view view to set to greyscale
     */
    private void resourceToGreyscale(ImageView view) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); // 0 sat means no color = greyscale

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        view.setColorFilter(filter); // Set the color filter to the matrix initialized above
    }

    /**
     * Resets the current color filter to null. Removes greyscale.
     *
     * @param view view to reset color filter
     */
    private void resetColorFilter(ImageView view) {
        view.setColorFilter(null);
    }

    @Override
    public void onClick(View v) {
        // Speech string
        String spellReadySpeech = "";
        // Based on which view, seperate actions
        switch (v.getId()) {
            case R.id.summoner_spell1:
                if (spell1.getColorFilter() == null) { // If color filter is null, start timer
                    spellReadySpeech = tabSummoner.getChampion().getName() + " " + tabSummoner.getSpell1().getName() + " ready";
                    timers[0] = startTimer(tabSummoner.getSpell1().getCooldown(), spell1, progressBarSpell1, textSpell1, spellReadySpeech);
                }
                break;
            case R.id.summoner_spell2:
                if (spell2.getColorFilter() == null) { // If color filter is null, start timer
                    spellReadySpeech = tabSummoner.getChampion().getName() + " " + tabSummoner.getSpell2().getName() + " ready";
                    timers[1] = startTimer(tabSummoner.getSpell2().getCooldown(), spell2, progressBarSpell2, textSpell2, spellReadySpeech);
                }
                break;
            case R.id.summoner_ultimate:
                if (ultimate.getColorFilter() == null) { // If color filter is null, start timer
                    spellReadySpeech = tabSummoner.getChampion().getName() + " ultimate ready";
                    double[] cooldowns = tabSummoner.getChampion().getUltimateCooldowns();
                    timers[2] = startTimer((int) cooldowns[cooldowns.length - 1], ultimate, progressBarUltimate, textUltimate, spellReadySpeech);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // Based on which view, seperate the buttons, but reset them
        switch (v.getId()) {
            case R.id.summoner_spell1:
                resetTimer(0, spell1, progressBarSpell1, textSpell1);
                break;
            case R.id.summoner_spell2:
                resetTimer(1, spell2, progressBarSpell2, textSpell2);
                break;
            case R.id.summoner_ultimate:
                resetTimer(2, ultimate, progressBarUltimate, textUltimate);
                break;
        }

        return true;
    }

    /**
     * Resets the timer for set index.
     *
     * @param timerIndex index of timer
     * @param ivResource ImageView resource to reset
     * @param pResource  ProgressBar resource to reset
     * @param tvResource TextView resource to reset
     */
    private void resetTimer(final int timerIndex, final ImageView ivResource, final ProgressBar pResource, final TextView tvResource) {
        if (timers[timerIndex] != null) { // If timer was started, cancel it
            timers[timerIndex].cancel();
        }
        // Reset progress bar visiblity and color filter
        resetColorFilter(ivResource);
        pResource.setVisibility(View.INVISIBLE);
        tvResource.setText("");

        // Show message
        Toast.makeText(getActivity(), getString(R.string.reset_spell), Toast.LENGTH_LONG).show();
    }

    /**
     * Toggles a boolean for whether or not to play sound (text-to-speech).
     */
    public void toggleSound() {
        playSound = !playSound;
    }

    /**
     * Resets all timers.
     */
    public void resetTimers() {
        for (int timerIndex = 0; timerIndex < timers.length; timerIndex++) {
            if (timers[timerIndex] != null) {
                timers[timerIndex].cancel();
            }
        }
    }

    /**
     * Starts a timer with given parameters, returns a CountDownTimer for storing.
     *
     * @param seconds          amount of seconds
     * @param ivResource       ImageView resource
     * @param pResource        ProgressBar resource
     * @param tvResource       TextView resource
     * @param spellReadySpeech Text-to-speech string upon completion
     * @return <code>CountDownTimer</code> - timer object
     */
    private CountDownTimer startTimer(final int seconds, final ImageView ivResource, final ProgressBar pResource, final TextView tvResource, final String spellReadySpeech) {
        // Set the button to greyscale and set progressbar visiblity
        resourceToGreyscale(ivResource);
        pResource.setVisibility(View.VISIBLE);
        pResource.setMax(seconds);
        // Init a timer
        CountDownTimer timer = new CountDownTimer(seconds * 1000, 500) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                // Every tick set the progress bar and text
                long curSecond = leftTimeInMilliseconds / 1000;
                pResource.setProgress(seconds - (int) curSecond);
                if ((int) curSecond <= 10) {
                    tvResource.setTextColor(textCriticalColor);
                } else {
                    tvResource.setTextColor(textColor);
                }
                tvResource.setText(Long.toString(curSecond));
            }

            @Override
            public void onFinish() {
                // Event fired when timer ticks runs out
                // If sound is toggled on, launch text-to-speech in queue-mode
                if (playSound) {
                    textToSpeech.speak(spellReadySpeech, TextToSpeech.QUEUE_ADD, null, null);
                }

                // Reset button to original state
                resetColorFilter(ivResource);
                pResource.setVisibility(View.INVISIBLE);
                tvResource.setText("");
            }
        }.start();

        return timer;
    }

    /**
     * This nested class handles image switching for summoner spells and champion ultimate.
     * This is handled asynchronously.
     */
    private class ImageSwitcher extends AsyncTask<URL, Bitmap[], Bitmap[]> {
        protected Bitmap[] doInBackground(URL... urls) {
            // Init bitmaps and the decoder
            Bitmap[] bitmaps = new Bitmap[4];
            ImageStreamDecoder decoder = new ImageStreamDecoder();
            try {
                // Try to decode from given URL (input stream), use decoder to load the bitmap into memory efficiently
                bitmaps[0] = decoder.decodeSampledBitmapFromStream((InputStream) urls[0].getContent(), 64, 64);
                bitmaps[1] = decoder.decodeSampledBitmapFromStream((InputStream) urls[1].getContent(), 64, 64);
                bitmaps[2] = decoder.decodeSampledBitmapFromStream((InputStream) urls[2].getContent(), 64, 64);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmaps;
        }

        protected void onPostExecute(Bitmap[] bitmaps) {
            if (bitmaps != null) { // If bitmaps not null
                // Set scaled bitmaps to ImageButton
                spell1.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[0], spell1.getWidth(), spell1.getHeight(), false));
                spell2.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[1], spell2.getWidth(), spell2.getHeight(), false));
                ultimate.setImageBitmap(Bitmap.createScaledBitmap(bitmaps[2], ultimate.getWidth(), ultimate.getHeight(), false));

                spell1.setVisibility(View.VISIBLE);
                spell2.setVisibility(View.VISIBLE);
                ultimate.setVisibility(View.VISIBLE);
            }
        }
    }
}