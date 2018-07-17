package se.mah.af2015.worldandfriends;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private NetworkService mNetworkService;
    private ServiceConnection mServiceConnection;
    private ReceiveListener mReceiveListener;
    private boolean mBound = false;

    public static ArrayList<String> mGroups = new ArrayList<>();
    public static String mCurrentGroup = "";
    public static String mCurrentName = "";
    public static String mCurrentID = "";

    private LocationManager mLocationManager;
    private JsonHandler mJsonHandler;
    private GPSListener mGpsListener;
    private String mProvider;

    public static UserFragment mUserFragment;
    public static MapFragment mMapFragment;
    public static GroupFragment mGroupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserFragment = new UserFragment();
        mMapFragment = new MapFragment();
        mGroupFragment = new GroupFragment();

        if(findViewById(R.id.container) != null) {
            showFragment(mUserFragment, "UserFragment");
        }

        mJsonHandler = new JsonHandler(this);

        Intent intent = new Intent(this, NetworkService.class);
        if(savedInstanceState == null) {
            startService(intent);
        }

        mServiceConnection = new ServiceConn();
        boolean result = bindService(intent, mServiceConnection, 0);
        if (!result) {
            Log.d("MainActivity", "No binding");
        } else {
            Log.d("MainActivity", "Bound");
        }

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mGpsListener = new GPSListener();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            // Get provider from location manager
            Criteria criteria = new Criteria();
            mProvider = mLocationManager.getBestProvider(criteria, false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mServiceConnection);
            mReceiveListener.stopListener();
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(mProvider, 2500, 0, mGpsListener);
        }
    }

    public void showFragment(Fragment fragment, String backStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(backStack).commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
            fragmentManager.beginTransaction().commit();
        }
    }

    public void updateGroups() {
        Log.d("UpdateGroups", "connecting");
        mNetworkService.connect();
        mNetworkService.send(mJsonHandler.groups());
    }

    public void updateMap() {

    }

    public void addToGroupArray(String group) {
        mGroups.add(group);
    }

    public void clearGroupArray() {
        mGroups.clear();
    }

    public void connectToGroup(String group, String name) {
        if (!group.equals("") && !name.equals("")) {
            mNetworkService.connect();
            if (mCurrentID.equals("")) {
                mCurrentGroup = group;
                mCurrentName = name;
                mNetworkService.send(mJsonHandler.register(group, name));
                Log.d("ConnectToGroup", "Registering...");
            }

            if (!mCurrentGroup.equals(group)) {
                mCurrentGroup = group;
                mCurrentName = name;
                mNetworkService.send(mJsonHandler.unregister(mCurrentID));
                mNetworkService.send(mJsonHandler.register(group, name));
                Log.d("ConnectToGroup", "Switcihng");
            }
        } else {
            Log.d("ConnectToGroup", "One or more fields empty...");
        }
    }

    public void disconnect() {
        mNetworkService.send(mJsonHandler.unregister(mCurrentID));
        mCurrentGroup = "";
        mCurrentName = "";
        mCurrentID = "";
        mMapFragment.clearMapMarkers();
        mNetworkService.disconnect();
    }

    private class ServiceConn implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            NetworkService.LocalService localService = (NetworkService.LocalService) binder;
            mNetworkService = localService.getService();
            mBound = true;
            mReceiveListener = new ReceiveListener();
            mReceiveListener.start();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    }

    private class ReceiveListener extends Thread {
        public void stopListener() {
            interrupt();
            mReceiveListener = null;
        }

        public void run() {
            String message;
            while(mReceiveListener != null) {
                try {
                    message = mNetworkService.receive();
                    try {
                        JSONObject jsonObject = new JSONObject(message);
                        Log.d("ReceiveListener", jsonObject.toString());
                        String type = jsonObject.getString("type");

                        if(type.equals("locations")) {
                            runOnUiThread(new UpdateMapUI(jsonObject));
                        }

                        if(type.equals("register")) {
                            mCurrentID = jsonObject.getString("id");
                            if(!mCurrentID.equals("")) {
                                Location temp = mGpsListener.getLastLocation();
                                if(temp != null) mNetworkService.send(mJsonHandler.location(mCurrentID, "" + temp.getLatitude(), "" + temp.getLongitude()));
                            }
                        }

                        if(type.equals("groups")) {
                            runOnUiThread(new UpdateGroupUI(jsonObject));
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    mReceiveListener = null;
                }
            }
        }
    }

    private class UpdateMapUI implements Runnable {
        private JSONObject jsonObject;

        public UpdateMapUI(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                mJsonHandler.getMemberLocations(jsonObject, mCurrentGroup, mMapFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateGroupUI implements Runnable {
        private JSONObject jsonObject;

        public UpdateGroupUI(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            mJsonHandler.getGroups(jsonObject);
        }
    }

    private class GPSListener implements LocationListener {
        private Location lastLocation;

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
            if(!mCurrentID.equals("")) {
                mNetworkService.send(mJsonHandler.location(mCurrentID, "" + location.getLatitude(), "" + location.getLongitude()));
            }
        }

        @Override
        public void onStatusChanged(String s, int status, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        public Location getLastLocation() {
            return lastLocation;
        }
    }
}
