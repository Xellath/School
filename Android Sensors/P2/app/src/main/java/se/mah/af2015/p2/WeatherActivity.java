package se.mah.af2015.p2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * WeatherActivity represents the Activity that holds data for P2: The Weather Channel
 * This activity implements SensorEventListener, giving us data from temperature, humidity and pressure sensors,
 * if sensors are available.
 *
 * @author Alexander Johansson (AF2015)
 */
public class WeatherActivity extends AppCompatActivity implements SensorEventListener, LocationListener, ApiRequestFinished {
    // Variables holding sensor data
    private SensorManager mSensorManager;

    // Location data
    private LocationManager mLocationManager;
    private String mProvider;

    private Sensor mTemperatureSensor = null;
    private Sensor mPressureSensor = null;
    private Sensor mHumiditySensor = null;

    // TextViews for displaying event values
    private TextView mSensorTemperatureValue;
    private TextView mSensorPressureValue;
    private TextView mSensorHumidityValue;

    private TextView mSensorsTimestamp;

    private TextView mApiTemperatureValue;
    private TextView mApiPressureValue;
    private TextView mApiHumidityValue;
    private TextView mApiCityValue;

    private TextView mEstimatedAltitude;

    // For formatting timestamp
    private SimpleDateFormat dateFormat;
    private Date date;

    // RadioButtons
    private RadioButton mUseAsync;
    private RadioButton mUseVolley;

    // Buttons
    private Button mRequestData;
    private Button mEstimateAltitude;

    // Holds device location
    private double mCurrentLatitude = Double.NaN;
    private double mCurrentLongitude = Double.NaN;;

    // Holds sensor and api pressure
    private float mSensorPressure = Float.NaN;
    private float mApiPressure = Float.NaN;

    // Constant for checking for permission
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 1030;

    // API request URL and application key
    private static final String mApiUrl = "http://api.openweathermap.org/data/2.5/weather?lat=%.2f&lon=%.2f&units=metric&APPID=%s";
    private static final String mApiKey = "7e9f5a7c075beaa522c06f5c951afab6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set content view
        setContentView(R.layout.activity_weather);

        // Initialise
        initMisc();
        initSensors();
        initLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int registreredSensors = 0;
        // If Sensor exists
        if(mTemperatureSensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            registreredSensors++;
        }

        if(mPressureSensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mPressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            registreredSensors++;
        }

        if(mHumiditySensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mHumiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
            registreredSensors++;
        }

        // If registered sensors are more than 0
        if(registreredSensors > 0) {
            // Notify user
            Toast.makeText(this, getResources().getText(R.string.listeners_registered), Toast.LENGTH_SHORT).show();
        }

        // Request location updates if permissions are granted
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(mProvider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister listeners and notify user
        mSensorManager.unregisterListener(this, mTemperatureSensor);
        mSensorManager.unregisterListener(this, mPressureSensor);
        mSensorManager.unregisterListener(this, mHumiditySensor);
        Toast.makeText(this, getResources().getText(R.string.listeners_unregistered), Toast.LENGTH_SHORT).show();

        // Remove location listener on pause
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // For GC
        mSensorManager = null;
        mTemperatureSensor = null;
        mPressureSensor = null;
        mHumiditySensor = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Depending on sensor type provided that triggered the SensorEvent, set correlating values
        switch(event.sensor.getType()) {
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                mSensorTemperatureValue.setText(getResources().getText(R.string.temperature) + " " + event.values[0] + " " + getResources().getText(R.string.temperature_unit));
                break;
            case Sensor.TYPE_PRESSURE:
                mSensorPressureValue.setText(getResources().getText(R.string.pressure) + " " + event.values[0] + " " + getResources().getText(R.string.pressure_unit));
                // If value has not been changed, retain NaN status (fix only for emulator)
                if(event.values[0] != 0.0f) {
                    mSensorPressure = event.values[0];
                }
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                mSensorHumidityValue.setText(getResources().getText(R.string.humidity) + " " + event.values[0] + " " + getResources().getText(R.string.humidity_unit));
                break;
        }

        // Set timestamp
        date = new Date(event.timestamp / 1000000);
        mSensorsTimestamp.setText(getResources().getText(R.string.timestamp) + " " + dateFormat.format(date));
    }

    @Override
    public void requestFinished(String output) {
        if(output != null) {
            try {
                // Create json object from string
                JSONObject outputObject = new JSONObject(output);
                // Read from json object
                String city = outputObject.getString("name");
                JSONObject main = outputObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double pressure = main.getDouble("pressure");
                double humidity = main.getDouble("humidity");

                mApiPressure = (float) pressure;

                // Format and set values to TextView
                mApiTemperatureValue.setText(getResources().getText(R.string.temperature) + " " + temperature + " " + getResources().getText(R.string.temperature_unit));
                mApiPressureValue.setText(getResources().getText(R.string.pressure) + " " + pressure  + " " + getResources().getText(R.string.pressure_unit));
                mApiHumidityValue.setText(getResources().getText(R.string.humidity) + " " + humidity + " " + getResources().getText(R.string.humidity_unit));
                mApiCityValue.setText(getResources().getText(R.string.current_city) + " " + city);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Notify user if no results
            Toast.makeText(this, getResources().getText(R.string.api_no_results), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Store latitude and longitude
        mCurrentLatitude = location.getLatitude();
        mCurrentLongitude = location.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Criteria criteria = new Criteria();
                    mProvider = mLocationManager.getBestProvider(criteria, false);
                }
                break;
        }
    }

    /**
     * Method initialises miscellaneous variables
     */
    private void initMisc() {
        dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        mApiTemperatureValue = (TextView) findViewById(R.id.api_temperature);
        mApiPressureValue = (TextView) findViewById(R.id.api_pressure);
        mApiHumidityValue = (TextView) findViewById(R.id.api_humidity);
        mApiCityValue = (TextView) findViewById(R.id.api_current_city);

        // Find Radiobuttons and request data button
        mUseAsync = (RadioButton) findViewById(R.id.use_async);
        mUseVolley = (RadioButton) findViewById(R.id.use_volley);

        // Find request button and register OnClickListener
        mRequestData = (Button) findViewById(R.id.request_data);
        mRequestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequestClick();
            }
        });

        // Find altitude button and register OnClickListener
        mEstimateAltitude = (Button) findViewById(R.id.estimate_altitude);
        mEstimateAltitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEstimateClick();
            }
        });

        mEstimatedAltitude = (TextView) findViewById(R.id.estimated_altitude);
    }

    /**
     * Method initialises sensors
     */
    private void initSensors() {
        // Find all TextViews for displaying Sensor data
        mSensorTemperatureValue = (TextView) findViewById(R.id.sensor_temperature);
        mSensorPressureValue = (TextView) findViewById(R.id.sensor_pressure);
        mSensorHumidityValue = (TextView) findViewById(R.id.sensor_humidity);

        mSensorsTimestamp = (TextView) findViewById(R.id.sensor_timestamp);

        // Initialise SensorManager
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        // If Sensor.TYPE_TEMPERATURE exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            // Get Sensor
            mTemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.temperature)), Toast.LENGTH_LONG).show();
        }

        // If Sensor.TYPE_PRESSURE exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            // Get Sensor
            mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.pressure)), Toast.LENGTH_LONG).show();
        }

        // If Sensor.TYPE_RELATIVE_HUMIDITY exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            // Get Sensor
            mHumiditySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.humidity)), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method initialises location services
     */
    private void initLocation() {
        // Get the location manager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // If location permission is not granted
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user.
                // After the user sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            } else {
                // No explanation needed, we can request the permission.
                // REQUEST_FINE_LOCATION_PERMISSION is a defined constant
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            }
        } else {
            // Get provider from location manager
            Criteria criteria = new Criteria();
            mProvider = mLocationManager.getBestProvider(criteria, false);
        }
    }

    /**
     * Method queues a request for Volley to handle, upon result it handles the request response in a different method
     * @param apiUrl String
     * @param apiKey String
     * @param latitude double
     * @param longitude double
     */
    private void volleyRequest(String apiUrl, String apiKey, double latitude, double longitude) {
        // Init request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        // Format url and format string request
        String formattedUrl = String.format(apiUrl, latitude, longitude, apiKey);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, formattedUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Process response
                        requestFinished(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Notify user if no results
                        Toast.makeText(getApplication(), getResources().getText(R.string.could_not_connect), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplication(), getResources().getText(R.string.api_no_results), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add to Volley request queue
        queue.add(stringRequest);
    }

    /**
     * Method is called upon clicking "Request Data" button in the UI,
     * it starts an Async or a Volley request
     */
    private void handleRequestClick() {
        // Check whether user wants to use Async or Volley
        if(mUseAsync.isChecked() && !mUseVolley.isChecked()) {
            // Use Async
            if(!Double.isNaN(mCurrentLatitude) && !Double.isNaN(mCurrentLongitude)) {
                // Start Api request using Async
                // Use current location
                new WeatherApiRequest(this, WeatherActivity.this).execute(mApiUrl, mApiKey, mCurrentLatitude, mCurrentLongitude);
            } else {
                // Start Api request using Async
                // Use Malmö Latitude/Longitude
                // 55.60587, 13.00073
                new WeatherApiRequest(this, WeatherActivity.this).execute(mApiUrl, mApiKey, 55.60587, 13.00073);
                Toast.makeText(this, getResources().getText(R.string.could_not_find_loc), Toast.LENGTH_SHORT).show();
            }
        } else if(!mUseAsync.isChecked() && mUseVolley.isChecked()) {
            // Use Volley
            if(!Double.isNaN(mCurrentLatitude) && !Double.isNaN(mCurrentLongitude)) {
                // Start Api request using Volley
                // Use current location
                volleyRequest(mApiUrl, mApiKey, mCurrentLatitude, mCurrentLongitude);
            } else {
                // Start Api request using Volley
                // Use Malmö Latitude/Longitude
                // 55.60587, 13.00073
                volleyRequest(mApiUrl, mApiKey, 55.60587, 13.00073);
                Toast.makeText(this, getResources().getText(R.string.could_not_find_loc), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method is called upon clicking "Estimate Altitude" button in the UI,
     * it evaluates if API data exists, and if so, it calculates and shows estimated altitude
     */
    private void handleEstimateClick() {
        // Check if the values are actual values and that the sensor/api pressure value has been assigned a value
        if(!Float.isNaN(mSensorPressure) && !Float.isNaN(mApiPressure)) {
            // Calculate altitude
            float altitude = SensorManager.getAltitude(mApiPressure, mSensorPressure);

            // Format text and set
            mEstimatedAltitude.setText(getResources().getText(R.string.estimated_altitude) + " " + altitude + " " + getResources().getText(R.string.altitude_unit));
        } else {
            // Notify user if we can't estimate
            Toast.makeText(this, getResources().getText(R.string.could_not_estimate), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not used
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not used
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not used
    }
}
