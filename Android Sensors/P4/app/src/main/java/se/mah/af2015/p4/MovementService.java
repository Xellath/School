package se.mah.af2015.p4;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * MovementService creates a service for the Pathfinder app, it handles step detection sensor readings and
 * forward them to the app's activity once bound to an activity.
 *
 * @author Alexander Johansson (AF2015)
 */
public class MovementService extends Service implements SensorEventListener {
    private LocalBinder mBinder;
    private OnMovementChangeListener mListener;
    private SensorManager mSensorManager;

    private Sensor mStepDetectorSensor;

    private int mUserId;
    private PathfinderDBHelper mPathfinderDb;

    /**
     * Empty Constructor
     */
    public MovementService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Init Binder
        mBinder = new LocalBinder();

        // Get Sensor Manager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // If Sensor.TYPE_STEP_DETECTOR exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            // Get Sensor
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

            // Init database
            mPathfinderDb = new PathfinderDBHelper(this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Register listener and return binder
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return mBinder;
    }

    @Override
    public void onDestroy() {
        // Unregister listener
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Increment user steps
        mPathfinderDb.incrementUserSteps(mUserId);
        // Update GUI if listener is not null
        if(mListener != null) mListener.update();
    }

    /**
     * Method sets listener activity
     * @param listener OnMovementChangeListener
     */
    public void setListenerActivity(OnMovementChangeListener listener) {
        mListener = listener;
    }

    /**
     * Method sets user id
     * @param id Integer
     */
    public void setUserId(int id) {
        mUserId = id;
    }

    /**
     * LocalBinder provides our ServiceConnection with a Service instance
     *
     * @author Alexander Johansson (AF2015)
     */
    public class LocalBinder extends Binder {

        /**
         * Method returns outclass service context
         * @return
         */
        MovementService getService() {
            return MovementService.this;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}

