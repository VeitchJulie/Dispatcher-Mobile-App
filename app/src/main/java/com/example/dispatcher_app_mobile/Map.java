package com.example.dispatcher_app_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    //    ImageButton backButton;
    MapView map;
    Double lat, lng;
    String myTeamId;
    private GoogleMap mMap;
    private static final String TAG = "Map";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

//        map = (MapView) findViewById(R.id.mapView);
//        map.onCreate(savedInstanceState);
//        map.getMapAsync(this);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 52.1);
        lng = intent.getDoubleExtra("lng", 21.2);
//        backButton = findViewById(R.id.backButton);

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent mainIntent = new Intent(Map.this, AfterLogIn.class);
//                mainIntent.putExtra("myTeamId", myTeamId);
//                Map.this.startActivity(mainIntent);
//            }
//        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap.setMaxZoomPreference(30);
        LatLng casePosition = new LatLng(lat, lng);
        googleMap.addMarker(new MarkerOptions()
                .position(casePosition)
                .title("Case Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12));
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        map.onSaveInstanceState(outState);
//    }
}