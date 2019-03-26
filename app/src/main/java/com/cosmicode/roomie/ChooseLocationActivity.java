package com.cosmicode.roomie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.fragment.app.FragmentActivity;

import static com.cosmicode.roomie.util.GeoLocationUtil.getLocationText;

public class ChooseLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "ChooseLocationActivity";
    private GoogleMap mMap;
    private Marker marker;
    private double[] coordinates;
    private String[] addressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        Intent intent = getIntent();
        coordinates = intent.getDoubleArrayExtra("Address");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng location = new LatLng(coordinates[0], coordinates[1]);
        addressText = getLocationText(location, getApplicationContext());
        marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                coordinates[0] = marker.getPosition().latitude;
                coordinates[1] = marker.getPosition().longitude;
                addressText = getLocationText(marker.getPosition(), getApplicationContext());
            }
        });

        mMap.setOnMapClickListener(latlng -> {

            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .draggable(true));

            coordinates[0] = marker.getPosition().latitude;
            coordinates[1] = marker.getPosition().longitude;

            addressText = getLocationText(marker.getPosition(), getApplicationContext());
        });
    }

    public void onConfirmLocation(View view) {
        Intent intent = new Intent();
        intent.putExtra("Address", coordinates);
        intent.putExtra("City", addressText[0]);
        intent.putExtra("State", addressText[1]);
        setResult(RESULT_OK, intent);
        finish();
    }
}
