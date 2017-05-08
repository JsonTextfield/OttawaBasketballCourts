package com.textfield.json.ottawabasketballcourts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.textfield.json.ottawabasketballcourts.util.DB;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean[] courtTypes = {true, true};

    ArrayList<Marker> full = new ArrayList<>();
    ArrayList<Marker> half = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setTitle(R.string.basketball_courts);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.filter) {
            String[] items = getResources().getStringArray(R.array.types);
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle(getResources().getString(R.string.court_types))
                    .setMultiChoiceItems(items, courtTypes, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {

                            if (indexSelected == 0) {
                                for (Marker marker : full) {
                                    marker.setVisible(isChecked);
                                }
                            } else {
                                for (Marker marker : half) {
                                    marker.setVisible(isChecked);
                                }
                            }
                            courtTypes[indexSelected] = isChecked;
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        LatLng me;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location myself = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(myself == null){
                myself = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            try {
                me = new LatLng(myself.getLatitude(), myself.getLongitude());
            } catch (NullPointerException x) {
                me = new LatLng(45.38, -75.69);
            }
        } else {
            me = new LatLng(45.38, -75.69);
        }
        googleMap.clear();

        DB db = new DB(this);
        db.createDatabase();
        db.open();
        Cursor cursor = db.runQuery("select * from courts;");
        do {
            Court c = new Court(cursor.getString(cursor.getColumnIndex("courttype")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getDouble(cursor.getColumnIndex("latitude")),
                    cursor.getDouble(cursor.getColumnIndex("longitude")), cursor.getInt(cursor.getColumnIndex("id")));
            if (c.getType().equals("full")) {
                full.add(googleMap.addMarker(new MarkerOptions().position(c.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title(String.format("%s - %s", c.getName(), c.getType()))));
            } else {
                half.add(googleMap.addMarker(new MarkerOptions().position(c.getLocation()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)).title(String.format("%s - %s", c.getName(), c.getType()))));
            }
        }
        while (cursor.moveToNext());
        db.close();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(me)      // Sets the center of the map to location user
                .zoom(13)        // Sets the zoom
                .bearing(-30)    // Sets the orientation of the camera to east
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

}