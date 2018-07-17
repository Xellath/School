package se.mah.af2015.p4;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MovementActivity handles GUI events and binds to our MovementService
 *
 * @author Alexander Johansson (AF2015)
 */
public class MovementActivity extends AppCompatActivity implements SensorEventListener, OnMovementChangeListener {
    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;
    private Sensor mMagnetometerSensor;

    private TextView mSteps;
    private TextView mStepsPerSecond;

    private Button mResetData;

    private ImageView mCompass;

    private int mUserId;

    private PathfinderDBHelper mPathfinderDb;

    private MovementServiceConnection mConnection;

    public MovementService movementService;
    public boolean serviceBound;

    private float[] mLastAccelerometer = new float[3];
    private boolean mLastAccelerometerSet;

    private float[] mLastMagnetometer = new float[3];
    private boolean mLastMagnetometerSet;

    private float[] mRotationMatrix = new float[16];
    private float[] mOrientation = new float[3];

    private float mCurrentDegree;
    private long mLastUpdateTime;

    private int mUserSteps;
    private long mLastStepUpdateTime;

    private float mLastX;
    private float mLastY;
    private float mLastZ;

    private static final float sShakeThreshold = 28;
    private boolean mFirstValue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement);

        // Init
        initGUI();
        initSensors();
        initService();
    }

    /**
     * Method initialises GUI elements
     */
    private void initGUI() {
        // Get TextView elements
        mSteps = (TextView) findViewById(R.id.sensor_steps_taken);
        mStepsPerSecond = (TextView) findViewById(R.id.sensor_steps_ps);

        // Reset steps per second every second
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set steps to 0;
                mUserSteps = 0;
                mStepsPerSecond.setText(getResources().getText(R.string.steps_per_second) + " " + mUserSteps);

                handler.postDelayed(this, 1000);
            }
        }, 1000);

        // Reset button
        mResetData = (Button) findViewById(R.id.reset_button);
        mResetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset steps and update UI
                mPathfinderDb.resetUserSteps(mUserId);
                mUserSteps = 0;
                update();

                // Notify user
                Toast.makeText(getApplication(), getResources().getText(R.string.data_reset), Toast.LENGTH_SHORT).show();
            }
        });

        // Get compass ImageView element
        mCompass = (ImageView) findViewById(R.id.compass);
    }

    /**
     * Method initialises sensor related functionality
     */
    private void initSensors() {
        // Get Sensor Manager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // If Sensor.TYPE_ACCELEROMETER exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // Get Sensor
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.accelerometer)), Toast.LENGTH_LONG).show();
        }

        // If Sensor.TYPE_MAGNETIC_FIELD exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            // Get Sensor
            mMagnetometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.magnetometer)), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method initialises service components
     */
    private void initService() {
        // Get bundle from previous activity
        Bundle extraBundle = getIntent().getExtras();
        if(extraBundle != null) {
            // Get user id
            mUserId = extraBundle.getInt("userid");

            // Make new ServiceConnection, new Intent and bind the service
            mConnection = new MovementServiceConnection(this, mUserId);
            Intent movementIntent = new Intent(this, MovementService.class);
            bindService(movementIntent, mConnection, Context.BIND_AUTO_CREATE);

            // Init database helper, update GUI
            mPathfinderDb = new PathfinderDBHelper(this);
            update();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int registreredSensors = 0;
        // If Sensor exists
        if(mAccelerometerSensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            registreredSensors++;
        }

        if(mMagnetometerSensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mMagnetometerSensor, SensorManager.SENSOR_DELAY_UI);
            registreredSensors++;
        }

        // If registered sensors are more than 0
        if(registreredSensors > 0) {
            // Notify user
            Toast.makeText(this, getResources().getText(R.string.listeners_registered), Toast.LENGTH_SHORT).show();
        }

        update();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister listeners and notify user
        mSensorManager.unregisterListener(this, mAccelerometerSensor);
        mSensorManager.unregisterListener(this, mMagnetometerSensor);
        Toast.makeText(this, getResources().getText(R.string.listeners_unregistered), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // If service bound, unbind service
        if(serviceBound) {
            unbindService(mConnection);
            serviceBound = false;
        }

        // For GC
        mSensorManager = null;
        mAccelerometerSensor = null;
        mMagnetometerSensor = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Rotate
        rotateCompass(event);

        // Detect shake
        detectShake(event);
    }

    /**
     * Method rotates compass given current readings, N pointing towards north
     * @param event SensorEvent
     */
    private void rotateCompass(SensorEvent event) {
        // If Sensor.TYPE_ACCELEROMETER
        if(event.sensor == mAccelerometerSensor) {
            // Copy values to member variable
            System.arraycopy(event.values, 0, mLastAccelerometer, 0,
                    event.values.length);
            mLastAccelerometerSet = true;
        } else if(event.sensor == mMagnetometerSensor) { // If Sensor.TYPE_MAGNETOMETER
            // Copy values to member variable
            System.arraycopy(event.values, 0, mLastMagnetometer, 0,
                    event.values.length);
            mLastMagnetometerSet = true;
        }

        // Only 4 times in 1 second
        if(mLastAccelerometerSet && mLastMagnetometerSet &&
                System.currentTimeMillis() - mLastUpdateTime > 250) {
            // Get rotation and orientation
            SensorManager.getRotationMatrix(mRotationMatrix, null,
                    mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);
            // Calculate
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegrees = (float)
                    (Math.toDegrees(azimuthInRadians) + 360) % 360;
            //Log.d("rotateCompass", "curDegree: " + mCurrentDegree + ", azimuth: " + -azimuthInDegrees);
            // If compass is transitioning from -360 to <= -0, or vice versa
            if(isTransitioning(mCurrentDegree, -azimuthInDegrees)) {
                // Set rotation
                mCompass.setRotation(-azimuthInDegrees);
                //Log.e("rotateCompass", "rotatedWith setRotation");
            } else {
                // Apply rotation to compass
                RotateAnimation mRotateAnimation = new RotateAnimation(
                        mCurrentDegree, -azimuthInDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                mRotateAnimation.setDuration(250);
                mRotateAnimation.setFillAfter(true);
                mCompass.startAnimation(mRotateAnimation);
            }

            mCurrentDegree = -azimuthInDegrees;
            mLastUpdateTime = System.currentTimeMillis();
        }
    }

    private boolean isTransitioning(float curDegrees, float toDegrees) {
        if(((curDegrees <= -350.0f && curDegrees >= -360.0f) && (toDegrees <= -0.0f && toDegrees >= -10.0f))
                || ((curDegrees <= -0.0f && curDegrees >= -10.0f) && (toDegrees <= -350.0f && toDegrees >= -360.0f))) {
            return true;
        }

        return false;
    }

    /**
     * Method detects a shake event from SensorEvent.values given
     * @param event SensorEvent
     */
    private void detectShake(SensorEvent event) {
        // If Sensor.TYPE_ACCELEROMETER
        if(event.sensor == mAccelerometerSensor) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Not first value (no last values)
            if(mFirstValue) {
                float deltaX = Math.abs(mLastX - x);
                float deltaY = Math.abs(mLastY - y);
                float deltaZ = Math.abs(mLastZ - z);

                // If thresholds are met
                if((deltaX > sShakeThreshold && deltaY > sShakeThreshold)
                        || (deltaX > sShakeThreshold && deltaZ > sShakeThreshold)
                        || (deltaY > sShakeThreshold && deltaZ > sShakeThreshold)) {
                    // Shake was performed
                    // Give compass element a rotation
                    RotateAnimation mRotateAnimation = new RotateAnimation(
                            mCurrentDegree, 360,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    mRotateAnimation.setDuration(250);
                    mRotateAnimation.setFillAfter(true);
                    mCompass.startAnimation(mRotateAnimation);

                    // Notify user
                    Toast.makeText(this, getResources().getText(R.string.shake), Toast.LENGTH_SHORT).show();
                }
            }

            // Copy for comparison
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mFirstValue = true;
        }
    }

    @Override
    public void update() {
        // Get steps and update GUI element
        int steps = mPathfinderDb.getUserSteps(mUserId);
        mSteps.setText(getResources().getText(R.string.steps_taken) + " " + steps);

        // Update steps per second
        mUserSteps++;
        mStepsPerSecond.setText(getResources().getText(R.string.steps_per_second) + " " + mUserSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
