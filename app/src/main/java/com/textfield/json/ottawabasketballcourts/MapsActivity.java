package com.textfield.json.ottawabasketballcourts;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.textfield.json.ottawabasketballcourts.util.DB;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Court> courts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
    public void loadMarkers(final GoogleMap googleMap, String type) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 50; // offset from edges of the map in pixels

        for (Marker m : markers) {
            m.setVisible(true);
            //builder.include(m.getPosition());
        }
        if (type.equals("all")) {
            for (Marker m : markers) {
                m.setVisible(true);
                builder.include(m.getPosition());
            }
        } else {
            for (int i = 0; i < courts.size(); i++) {
                if (courts.get(i).getType().equals(type)) {
                    markers.get(i).setVisible(true);
                    builder.include(markers.get(i).getPosition());
                } else {
                    markers.get(i).setVisible(false);
                }
            }
        }
        LatLngBounds bounds = builder.build();
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                googleMap.animateCamera(cu);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        googleMap.clear();
        googleMap.setMyLocationEnabled(true);

        DB db = new DB(MapsActivity.this);

        db.createDatabase();
        db.open();
        Cursor cursor = db.runQuery("select * from courts;");
        do {
            Court c = new Court(cursor.getString(cursor.getColumnIndex("courttype")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")),
                    cursor.getInt(cursor.getColumnIndex("id")));

            courts.add(c);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(c.getLocation())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                    .title(String.format("%s (%s)", c.getName(), c.getType())));
            marker.setTag(c);
            markers.add(marker);
        }
        while (cursor.moveToNext());
        db.close();

        SegmentedGroup segmented = (SegmentedGroup) findViewById(R.id.segmented);
        segmented.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.full) {
                    loadMarkers(googleMap, "full");
                }
                if (checkedId == R.id.half) {
                    loadMarkers(googleMap, "half");
                }
                if (checkedId == R.id.all) {
                    loadMarkers(googleMap, "all");
                }
            }
        });
        loadMarkers(googleMap, "all");
    }

}