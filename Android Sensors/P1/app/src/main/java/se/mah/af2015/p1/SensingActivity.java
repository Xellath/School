package se.mah.af2015.p1;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * SensingActivity represents the activity that holds content for The Sensing Station
 *
 * @author Alexander Johansson (AF2015)
 */
public class SensingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content view
        setContentView(R.layout.activity_sensing);

        // Display SensorListFragment
        showListFragment();
    }

    /**
     * Method creates and instance of SensorListFragment and makes the fragment transaction
     */
    private void showListFragment() {
        // Create instance
        SensorListFragment listFragment = new SensorListFragment();

        // Get FragmentManager and make transaction
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_sensing, listFragment).commit();
    }
}
