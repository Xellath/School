package se.mah.af2015.p3;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.etiennelawlor.discreteslider.library.utilities.DisplayUtility;

/**
 * LightActivity represents the Activity that holds data for P3: Lightbender
 * This activity implements SensorEventListener, giving us data from light and proximity sensors if available.
 *
 * @author Alexander Johansson (AF2015)
 */
public class LightActivity extends AppCompatActivity implements SensorEventListener {
    // Variables holding sensor data
    private SensorManager mSensorManager;

    private Sensor mProximitySensor = null;
    private Sensor mLightSensor = null;

    private TextView mSensorProximityValue;
    private TextView mSensorLuxValue;

    // RadioButton and variables for settings
    private RadioButton mUseSettingsBrightness;

    private ContentResolver mContentResolver;
    private Window mWindow;

    // Slider
    private DiscreteSlider discreteSlider;
    private RelativeLayout tickMarkLabels;

    // Camera related view and variables
    private ImageView mFlashIndicator;

    private CameraManager mCameraManager;
    private String mCameraID;
    private CameraCharacteristics mCameraParameters;

    private boolean mIsFlashlightOn = false;

    // Current light level = Overcast (starting value)
    private int mCurrentLightLevel = 2;

    // Declare constants for light levels
    private static final int MIN = 0;
    private static final int MAX = 1;

    private static final int[][] lightRanges = {{0, 1}, {2, 10}, {11, 20}, {21, 600}, {601, 1500}};
    private static final String[] lightLevels = {"Sunlight", "Daylight", "Overcast", "Twilight", "Night"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        // Init stuff
        initSensors();
        initScreenBrightness();
        initSlider();
        initCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int registreredSensors = 0;
        // If Sensor exists
        if(mLightSensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            registreredSensors++;
        }

        if(mProximitySensor != null) {
            // Register listener and increment registered sensors
            mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            registreredSensors++;
        }

        // If registered sensors are more than 0
        if(registreredSensors > 0) {
            // Notify user
            Toast.makeText(this, getResources().getText(R.string.listeners_registered), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister listeners and notify user
        mSensorManager.unregisterListener(this, mLightSensor);
        mSensorManager.unregisterListener(this, mProximitySensor);
        Toast.makeText(this, getResources().getText(R.string.listeners_unregistered), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // For GC
        mSensorManager = null;
        mLightSensor = null;
        mProximitySensor = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Depending on sensor type provided that triggered the SensorEvent, set correlating values
        switch(event.sensor.getType()) {
            case Sensor.TYPE_LIGHT:
                float light = event.values[0];
                mSensorLuxValue.setText(getResources().getText(R.string.lux) + " " + light + " " + getResources().getText(R.string.lux_unit));

                // If light is within range
                if(light > 0 && light < 1500) {
                    // If light is less than current minimum in range, set to MIN
                    if(light <= lightRanges[mCurrentLightLevel][MIN]) {
                        light = lightRanges[mCurrentLightLevel][MIN];
                    } else if(light >= lightRanges[mCurrentLightLevel][MAX]) { // If life is less than current maximum in range, set to MAX
                        light = lightRanges[mCurrentLightLevel][MAX];
                    }

                    // Change brightness
                    changeScreenBrightness(1 / light);
                }
                break;
            case Sensor.TYPE_PROXIMITY:
                float distanceFromPhone = event.values[0];
                mSensorProximityValue.setText(getResources().getText(R.string.proximity) + " " + distanceFromPhone + " " + getResources().getText(R.string.proximity_unit));

                // Check if distance is less than max range (means that something is within proximity)
                if(distanceFromPhone < mProximitySensor.getMaximumRange()) {
                    // Turn on flashlight if not on
                    if(!mIsFlashlightOn) {
                        turnTorchLightOn();
                    }
                } else {
                    // Turn off flashlight if on
                    if(mIsFlashlightOn) {
                        turnTorchLightOff();
                    }
                }
                break;
        }
    }

    /**
     * Method initialises sensors
     */
    private void initSensors() {
        mSensorProximityValue = (TextView) findViewById(R.id.sensor_proximity);
        mSensorLuxValue = (TextView) findViewById(R.id.sensor_lux);

        // Initialise SensorManager
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        // If Sensor.TYPE_PROXIMITY exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            // Get Sensor
            mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.proximity)), Toast.LENGTH_LONG).show();
        }

        // If Sensor.TYPE_LIGHT exists
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            // Get Sensor
            mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        } else {
            // Sensor does not exist, notify user
            Toast.makeText(this, String.format(getResources().getString(R.string.does_not_exist), getResources().getText(R.string.lux)), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method initialises content resolver and window
     */
    private void initScreenBrightness()
    {
        mUseSettingsBrightness = (RadioButton) findViewById(R.id.use_system);
        mUseSettingsBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Show user warning that system settings are permanent
                if(isChecked) {
                    Toast.makeText(getApplication(), String.format(getResources().getString(R.string.system_brightness_notification)), Toast.LENGTH_LONG).show();
                }
            }
        });

        mContentResolver = getContentResolver();
        mWindow = getWindow();
    }

    /**
     * Method initialises the DiscreteSlider and registers listeners
     */
    private void initSlider() {
        // Find slider and RelativeLayout holding labels
        discreteSlider = (DiscreteSlider) findViewById(R.id.state_slider);
        tickMarkLabels = (RelativeLayout) findViewById(R.id.tick_mark_labels);
        // Register ChangeListener on slider
        discreteSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                mCurrentLightLevel = position;

                // Get child count
                int childCount = tickMarkLabels.getChildCount();
                // For each child
                for(int i = 0; i < childCount; i++) {
                    // Find TextView child at position
                    TextView textView = (TextView) tickMarkLabels.getChildAt(i);
                    // Set color if selected position
                    if(i == position) {
                        textView.setTextColor(ContextCompat.getColor(getApplication(), R.color.colorAccent));
                    } else {
                        textView.setTextColor(ContextCompat.getColor(getApplication(), R.color.grey_400));
                    }
                }
            }
        });

        // Register GlobalLayout on RelativeLayout
        tickMarkLabels.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tickMarkLabels.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Add labels
                addTickMarkTextLabels();
            }
        });
    }

    /**
     * Method initialises the camera parameters
     */
    private void initCamera() {
        // Get flash image view
        mFlashIndicator = (ImageView) findViewById(R.id.flashlight);

        // Get camera manager
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Retrieve first camers, get its characteristics
            mCameraID = mCameraManager.getCameraIdList()[0];
            mCameraParameters = mCameraManager.getCameraCharacteristics(mCameraID);
        } catch(CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method turns on the camera flashlight if available, always animates UI on change
     */
    private void turnTorchLightOn() {
        try {
            // If flash is available, turn it on
            if(mCameraParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) != null) {
                mCameraManager.setTorchMode(mCameraID, true);
            }
        } catch(CameraAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Set boolean to true
        mIsFlashlightOn = true;
        // Decode image resource, animate image switch
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_flash_on);
        animateImageChange(image);
    }

    /**
     * Method turns off the camera flashlight if available, always animates UI on change
     */
    private void turnTorchLightOff() {
        try {
            // If flash is available, turn it off
            if(mCameraParameters.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) != null) {
                mCameraManager.setTorchMode(mCameraID, false);
            }
        } catch(CameraAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // Set boolean to false
        mIsFlashlightOn = false;
        // Decode image resource, animate image switch
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_flash_off);
        animateImageChange(image);
    }

    /**
     * Method changes screen brightness, and if checked in UI, it also sets permanent screen brightness (system settings)
     * @param light
     */
    private void changeScreenBrightness(float light) {
        // If radio button is checked
        if(mUseSettingsBrightness.isChecked()) {
            // Check if permissions are allowed
            if (!Settings.System.canWrite(this)) {
                // Start intent for requesting permission to write to settings
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(intent);
            } else {
                // Set system settings
                Settings.System.putInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS, (int) (light * 255));
            }
        }

        // Always set (local) screen brightness
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
        mLayoutParams.screenBrightness = light;
        mWindow.setAttributes(mLayoutParams);
    }

    /**
     * Method adds TextView elements to act as labels for the slider.
     *
     * Method sample code retrieved from: https://github.com/lawloretienne/DiscreteSlider/blob/master/sample
     * Credit to Tienne Lawlore (@lawloretienne)
     *
     * Edited slightly to remove redundancy and fit my (AF2015) needs.
     */
    private void addTickMarkTextLabels() {
        // Get amount of ticks (stops in slider), its radius and width
        int tickMarkCount = discreteSlider.getTickMarkCount();
        float tickMarkRadius = discreteSlider.getTickMarkRadius();
        int width = tickMarkLabels.getMeasuredWidth();

        // Get margins to calculate intervals
        int discreteSliderBackdropLeftMargin = DisplayUtility.dp2px(this, 32);
        int discreteSliderBackdropRightMargin = DisplayUtility.dp2px(this, 32);
        // Calculate intervals
        int interval = (width - (discreteSliderBackdropLeftMargin + discreteSliderBackdropRightMargin) - ((int) (tickMarkRadius * 2))) / (tickMarkCount - 1);
        int tickMarkLabelWidth = DisplayUtility.dp2px(this, 40);

        // For each tick
        for(int i = 0; i < tickMarkCount; i++) {
            // Create TextView
            TextView textView = new TextView(this);

            // Create layout params, wrap content
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(tickMarkLabelWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

            // Set text in TextView to one of the light levels, set textsize and gravity
            textView.setText(lightLevels[i]);
            textView.setTextSize(10);
            textView.setGravity(Gravity.CENTER);
            // Depending on position on slider, set text to "activated" color
            if(i == discreteSlider.getPosition()) {
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            } else {
                textView.setTextColor(ContextCompat.getColor(this, R.color.grey_400));
            }

            // Calculate margins and set TextViews layout params
            int left = discreteSliderBackdropLeftMargin + (int) tickMarkRadius + (i * interval) - (tickMarkLabelWidth / 2);
            layoutParams.setMargins(left, 0, 0, 0);
            textView.setLayoutParams(layoutParams);

            // Add view to RelativeLayout
            tickMarkLabels.addView(textView);
        }
    }

    /**
     * Method animates the changing of the incoming image in given ImageView
     * @param newImage Bitmap
     */
    private void animateImageChange(final Bitmap newImage) {
        // Init animations
        final Animation animOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        final Animation animIn  = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                // Set image bitmap
                mFlashIndicator.setImageBitmap(newImage);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                // Start animation
                mFlashIndicator.startAnimation(animIn);
            }
        });

        // Start animation
        mFlashIndicator.startAnimation(animOut);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
