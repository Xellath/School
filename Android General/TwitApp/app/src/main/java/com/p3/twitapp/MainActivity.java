package com.p3.twitapp;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static final String TWITTER_KEY = "KZ4v1oVkvRa7rveaRxsz6BnC3";
    public static final String TWITTER_SECRET = "Hu33z4x0DP1mmSSkCRO4EgDUTefWOI5uefAtklV9WDaV7tic9S";

    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Snackbar.make(viewGroup, "@" + session.getUserName() + " logged in!", Snackbar.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(TwitterException exception) {
                Snackbar.make(viewGroup, "Login failed!", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
