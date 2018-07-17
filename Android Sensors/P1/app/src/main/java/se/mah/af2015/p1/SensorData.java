package se.mah.af2015.p1;

/**
 * SensorData holds static data about sensor types, eg. TYPE_ACCELEROMETER
 *
 * @author Alexander Johansson (AF2015).
 */
public class SensorData {
    public static final String mAccuracyNoContact = "No Contact"; // SENSOR_STATUS_NO_CONTACT = -1
    public static final String[] mAccuracyValues = {
            "Unreliable",   // SENSOR_STATUS_UNRELIABLE
            "Low",          // SENSOR_STATUS_ACCURACY_LOW
            "Medium",       // SENSOR_STATUS_ACCURACY_MEDIUM
            "High",         // SENSOR_STATUS_ACCURACY_HIGH
    };

    // null fields are for TYPES that don't have documented value declarations
    public static final String[][] mSensorValuesFormat = new String[][] {
            null, // padding because sensor types start at 1
            {"Acceleration force (X):", "Acceleration force (Y):", "Acceleration force (Z):"}, // SENSOR_TYPE_ACCELEROMETER
            {"Geomagnetic field strength (X):", "Geomagnetic field strength (Y):", "Geomagnetic field strength (Z):"}, // SENSOR_TYPE_GEOMAGNETIC_FIELD
            {"Azimuth (Z):", "Pitch (X):", "Roll (Y):"}, // SENSOR_TYPE_ORIENTATION
            {"Rate of rotation (X):", "Rate of rotation (Y):", "Rate of rotation (Z):"}, // SENSOR_TYPE_GYROSCOPE
            {"Illuminance:"}, // SENSOR_TYPE_LIGHT
            {"Ambient air pressure:"}, // SENSOR_TYPE_PRESSURE
            {"Device temperature:"}, // SENSOR_TYPE_TEMPERATURE
            {"Distance:"}, // SENSOR_TYPE_PROXIMITY
            {"Force of gravity (X):", "Force of gravity (Y):", "Force of gravity (Z):"}, // SENSOR_TYPE_GRAVITY
            {"Acceleration force (X):", "Acceleration force (Y):", "Acceleration force (Z):"}, // SENSOR_TYPE_LINEAR_ACCELERATION
            {"Rotation vector (X):", "Rotation vector (Y):", "Rotation vector (Z):", "Scalar component:", "Estimated accuracy:"}, // SENSOR_TYPE_ROTATION_VECTOR
            {"Ambient relative humidity:"}, // SENSOR_TYPE_RELATIVE_HUMIDITY
            {"Ambient air temperature:"}, // SENSOR_TYPE_AMBIENT_TEMPERATURE
            {"Geomagnetic field strength (X):", "Geomagnetic field strength (Y):", "Geomagnetic field strength (Z):", "Iron bias estimation (X):", "Iron bias estimation (Y):", "Iron bias estimation (Z):"}, // SENSOR_TYPE_MAGNETIC_FIELD_UNCALIBRATED
            {"Rotation vector (X):", "Rotation vector (Y):", "Rotation vector (Z):"}, // SENSOR_TYPE_GAME_ROTATION_VECTOR
            {"Rate of rotation (X):", "Rate of rotation (Y):", "Rate of rotation (Z):", "Estimated drift (X):", "Estimated drift (Y):", "Estimated drift (Z):"}, // SENSOR_TYPE_GYROSCOPE_UNCALIBRATED
            null, // SENSOR_TYPE_SIGNIFICANT_MOTION
            null, // SENSOR_TYPE_STEP_DETECTOR
            {"Steps:"}, // SENSOR_TYPE_STEP_COUNTER
            {"Rotation vector (X):", "Rotation vector (Y):", "Rotation vector (Z):", "Scalar component:", "Estimated accuracy:"}, // SENSOR_TYPE_GEOMAGNETIC_ROTATION_VECTOR
            {"Heart rate:"}, // SENSOR_TYPE_HEART_RATE
            null, // SENSOR_TYPE_WAKE_UP_TILT_DETECTOR
            null, // SENSOR_TYPE_WAKE_GESTURE
            null, // SENSOR_TYPE_GLANCE_GESTURE
            null, // SENSOR_TYPE_PICK_UP_GESTURE
            null, // SENSOR_TYPE_WRIST_TILT_GESTURE
            {"Azimuth (Z):", "Pitch (X):", "Roll (Y):"}, // SENSOR_TYPE_DEVICE_ORIENTATION
            {"Rotation vector (X):", "Rotation vector (Y):", "Rotation vector (Z):", "Scalar component:", "Translation (X):", "Translation (Y):", "Translation (Z):", "Delta rotation vector (X):", "Delta rotation vector (Y):", "Delta rotation vector (Z):", "Delta rotation scalar:", "Delta translation (X):", "Delta translation (Y):", "Delta translation (Z):", "Sequence number:"}, // SENSOR_TYPE_POSE_6DOF
            {"Stationary:"}, // SENSOR_TYPE_STATIONARY_DETECT
            {"In motion:"}, // SENSOR_TYPE_MOTION_DETECT
            {"Confidence:"}, // SENSOR_TYPE_HEART_BEAT
            null, // SENSOR_TYPE_DYNAMIC_SENSOR_META
    };
}
