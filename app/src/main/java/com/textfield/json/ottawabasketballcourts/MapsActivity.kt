package com.textfield.json.ottawabasketballcourts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.textfield.json.ottawabasketballcourts.util.DB
import info.hoang8f.android.segmented.SegmentedGroup
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val markers = ArrayList<Marker>()
    private val courts = ArrayList<Court>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
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
    private fun loadMarkers(googleMap: GoogleMap, type: String) {
        val builder = LatLngBounds.Builder()

        for (i in 0 until courts.size){
            if (courts[i].type == type || type == "all") {
                markers[i].isVisible = true
                builder.include(markers[i].position)
            } else{
                markers[i].isVisible = false
            }
        }

        googleMap.setOnMapLoadedCallback {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.clear()

        val db = DB(this@MapsActivity)

        db.createDatabase()
        db.open()
        val cursor = db.runQuery("select * from courts;")
        do {
            val c = Court(cursor)
            courts.add(c)
            val marker = googleMap.addMarker(MarkerOptions().position(c.location)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                    .title(String.format("%s (%s)", c.name, c.type)))
            marker.tag = c
            markers.add(marker)
        } while (cursor!!.moveToNext())
        db.close()

        val segmented = findViewById(R.id.segmented) as SegmentedGroup
        segmented.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.full -> loadMarkers(googleMap, "full")
                R.id.half -> loadMarkers(googleMap, "half")
                R.id.all -> loadMarkers(googleMap, "all")
            }
        }
        loadMarkers(googleMap, "all")
    }

}