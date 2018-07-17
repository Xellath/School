package se.mah.af2015.p1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * SensingFragment represents a basic UI for showing Sensor data - it handles sensor events through a listener
 *
 * @author Alexander Johansson (AF2015)
 */
public class SensingFragment extends Fragment implements SensorEventListener {
    // Variables holding sensor data
    private SensorManager mSensorManager;
    private Sensor mSensor;

    // TextViews for displaying event values
    private TextView mSensorValues;
    private TextView mSensorAccuracy;
    private TextView mSensorTimestamp;

    /**
     * Empty constructor for Fragment
     */
    public SensingFragment() {
    }

    /**
     * Statically creates a fragment to use in the transaction, sets selected sensor as data with the fragment
     * Returns an instance of SensingFragment
     * @param sensor Sensor
     * @return SensingFragment
     */
    public static SensingFragment newInstance(Sensor sensor) {
        // Create fragment and set it's data, return it to caller
        SensingFragment sensingFragment = new SensingFragment();
        sensingFragment.setSensor(sensor);
        return sensingFragment;
    }

    /**
     * Method sets assigns current fragment that given Sensor is the one to show data about
     * @param sensor Sensor
     */
    private void setSensor(Sensor sensor) {
        this.mSensor = sensor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

        // Find all TextViews for displaying Sensor data
        final TextView mSensorName = (TextView) rootView.findViewById(R.id.name);
        final TextView mSensorMaxRange = (TextView) rootView.findViewById(R.id.max_range);
        final TextView mSensorMinDelay = (TextView) rootView.findViewById(R.id.min_delay);
        final TextView mSensorPower = (TextView) rootView.findViewById(R.id.power);
        final TextView mSensorResolution = (TextView) rootView.findViewById(R.id.resolution);
        final TextView mSensorVendor = (TextView) rootView.findViewById(R.id.vendor);
        final TextView mSensorVersion = (TextView) rootView.findViewById(R.id.version);

        mSensorValues = (TextView) rootView.findViewById(R.id.values);
        mSensorAccuracy = (TextView) rootView.findViewById(R.id.accuracy);
        mSensorTimestamp = (TextView) rootView.findViewById(R.id.timestamp);

        // Initialise SensorManager
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        // If given Sensor exists
        if(mSensor != null) {
            // Set some of it's data to given TextViews, get Strings dynamically
            mSensorName.setText(getResources().getString(R.string.name) + " " + mSensor.getName());
            mSensorMaxRange.setText(getResources().getString(R.string.max_range) + " " + mSensor.getMaximumRange());
            mSensorMinDelay.setText(getResources().getString(R.string.min_delay) + " " + mSensor.getMinDelay());
            mSensorPower.setText(getResources().getString(R.string.power) + " " + mSensor.getPower());
            mSensorResolution.setText(getResources().getString(R.string.resolution) + " " + mSensor.getResolution());
            mSensorVendor.setText(getResources().getString(R.string.vendor) + " " + mSensor.getVendor());
            mSensorVersion.setText(getResources().getString(R.string.version) + " " + mSensor.getVersion());
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(getActivity(), getResources().getText(R.string.does_not_exist), Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // If Sensor exists
        if(mSensor != null) {
            // Register listener and notify user
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Toast.makeText(getActivity(), getResources().getText(R.string.listener_registered), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // If Sensor exists
        if(mSensor != null) {
            // Unregister listener and notify user
            mSensorManager.unregisterListener(this);
            Toast.makeText(getActivity(), getResources().getText(R.string.listener_unregistered), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // For GC
        mSensorManager = null;
        mSensor = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Get sensor type
        int sensorType = mSensor.getType();
        // Get length of event data
        int valuesCount = event.values.length;
        String valuesString = "";

        // If sensor is generic base or has non-declared value in documentation
        if(sensorType == Sensor.TYPE_DEVICE_PRIVATE_BASE || SensorData.mSensorValuesFormat[sensorType] == null) {
            // Iterate for each values[] entry
            for(int i = 0; i < valuesCount; i++) {
                // Concenate to string, ex:
                // Value (0): 36346
                // Value (1): 43646
                // Value (2): 57457
                valuesString += String.format(getResources().getString(R.string.value), i) + " " + String.valueOf(event.values[i]) + (i == (valuesCount - 1) ? "" : "\n");
            }
        } else if(SensorData.mSensorValuesFormat[sensorType].length == valuesCount) {
            // Amount of declared values match
            // This means we can use declared data declarations in our formatting

            // Iterate for each values[] entry
            for (int i = 0; i < valuesCount; i++) {
                // Concenate to string, ex:
                // Rotation vector (X): 36346
                // Rotation vector (Y): 43646
                // Rotation vector (Z): 57457
                valuesString += SensorData.mSensorValuesFormat[sensorType][i] + " " + String.valueOf(event.values[i]) + (i == (valuesCount - 1) ? "" : "\n");
            }
        } else {
            // Declared sensor values do not match
            // This means we use the first declared definitions, but for the remaining values we format them like the first example Value (0): xxx,
            // Example list would be:
            // Distance: 4
            // Value (1): 0
            // Value (2): 0

            // Init variable to keep track on which index we're on
            int i = 0;
            // Iterate for each declared definition, format according to definition
            for(i = 0; i < SensorData.mSensorValuesFormat[sensorType].length; i++) {
                // Concenate DECLARED VALUES/DEFINITION to string, ex:
                // Rotation vector (X): 36346
                // Rotation vector (Y): 43646
                // Rotation vector (Z): 57457
                valuesString += SensorData.mSensorValuesFormat[sensorType][i] + " " + String.valueOf(event.values[i]) + (i == (valuesCount - 1) ? "" : "\n");
            }

            // Iterate for each remaining values, format normally
            for(; i < valuesCount; i++) {
                // Concenate REMAINING VALUES to string, ex:
                // Value (0): 36346
                // Value (1): 43646
                // Value (2): 57457
                valuesString += String.format(getResources().getString(R.string.value), i) + " " + String.valueOf(event.values[i]) + (i == (valuesCount - 1) ? "" : "\n");
            }
        }

        // Set event data to TextViews, get Strings dynamically
        mSensorValues.setText(valuesString);
        // If accuracy == SENSOR_STATUS_NO_CONTACT
        if(event.accuracy == SensorManager.SENSOR_STATUS_NO_CONTACT) {
            // Print No Contact
            mSensorAccuracy.setText(getResources().getString(R.string.accuracy) + " " + SensorData.mAccuracyNoContact + " (" + String.valueOf(event.accuracy) + ")");
        } else {
            // Get corresponding accuracy value
            mSensorAccuracy.setText(getResources().getString(R.string.accuracy) + " " + SensorData.mAccuracyValues[event.accuracy] + " (" + String.valueOf(event.accuracy) + ")");
        }

        // Set timestamp
        mSensorTimestamp.setText(getResources().getString(R.string.timestamp) + " " + String.valueOf(event.timestamp));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
