package se.mah.af2015.p4;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * MovementServiceConnection provides a connection between the Activity and Service
 *
 * @author Alexander Johansson (AF2015).
 */
public class MovementServiceConnection implements ServiceConnection {
    private final MovementActivity mActivity;
    private final int mUserId;

    /**
     * Constructor
     * @param activity MovementActivity
     * @param userId Integer
     */
    public MovementServiceConnection(MovementActivity activity, int userId) {
        mActivity = activity;
        mUserId = userId;
    }

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        // Get Service and set bind information to activity
        MovementService.LocalBinder binder = (MovementService.LocalBinder) service;
        mActivity.movementService = binder.getService();
        mActivity.serviceBound = true;
        mActivity.movementService.setListenerActivity(mActivity);
        mActivity.movementService.setUserId(mUserId);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mActivity.serviceBound = false;
    }
}