package se.mah.af2015.p1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * SensorListFragment represents a list of sensors
 * ListView's SensorAdapter connects an ArrayList of Sensor objects to the given ListView position,
 * correct Sensor from the list is then forwarded in OnItemClickListener to a new fragment
 *
 * @author Alexander Johansson (AF2015)
 */
public class SensorListFragment extends Fragment {
    // Variables holding sensor data
    private SensorManager mSensorManager;
    private List<Sensor> mSensorsList;

    /**
     * Empty constructor for fragment
     */
    public SensorListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View rootView = inflater.inflate(R.layout.fragment_sensor_list, container, false);

        // Initialise SensorManager
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        // Get List of Sensor objects
        mSensorsList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Make an ArrayList from given List, for ArrayAdapter
        ArrayList<Sensor> sensorList = new ArrayList<>(mSensorsList);
        // Create adapter
        SensorAdapter adapter = new SensorAdapter(getContext(), sensorList);
        // Find ListView and set its' adapter, register OnItemClickListener
        ListView listView = (ListView) rootView.findViewById(R.id.sensor_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected position and cast item to Sensor, call method
                Sensor sensor = (Sensor) parent.getItemAtPosition(position);
                showSensingFragment(sensor);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // For GC
        mSensorManager = null;
        mSensorsList = null;
    }

    /**
     * Method creates a new instance of SensingFragment and makes a fragment transaction
     * @param sensor Sensor
     */
    private void showSensingFragment(Sensor sensor) {
        // Create instance with parameters
        SensingFragment sensingFragment = SensingFragment.newInstance(sensor);

        // Get FragmentManager
        FragmentManager fm = getActivity().getSupportFragmentManager();
        // Make transaction
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.content_sensing, sensingFragment).commit();
    }
}
