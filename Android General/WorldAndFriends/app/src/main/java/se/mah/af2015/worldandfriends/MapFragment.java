package se.mah.af2015.worldandfriends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final TextView currentGroup = (TextView) rootView.findViewById(R.id.current_group);
        currentGroup.setText(MainActivity.mCurrentGroup);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng malmo = new LatLng(55.60587, 13.00073);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(malmo, 12f));
    }

    public void addMapMarker(double lat, double lng, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lng, lat));
        markerOptions.title(title);
        mMap.addMarker(markerOptions);
    }

    public void clearMapMarkers() {
        mMap.clear();
    }
}
