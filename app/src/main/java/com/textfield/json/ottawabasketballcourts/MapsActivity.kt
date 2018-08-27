package com.textfield.json.ottawabasketballcourts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.textfield.json.ottawabasketballcourts.util.DB
import info.hoang8f.android.segmented.SegmentedGroup
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val courts = ArrayList<Court>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        DB.getInstance(this).createDatabase()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun loadMarkers(googleMap: GoogleMap, type: Int) {
        val builder = LatLngBounds.Builder()

        for (court in courts) {
            court.marker.isVisible = (court.type == type || type == 2)
            if (court.marker.isVisible){
                builder.include(court.marker.position)
            }
        }
        val latLngBounds = builder.build()
        googleMap.setLatLngBoundsForCameraTarget(latLngBounds)
        googleMap.setOnMapLoadedCallback { googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50)) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val db = DB.getInstance(this)
        db.open()
        val cursor = db.runQuery("select * from courts;")!!
        do {
            val c = Court(this, cursor, googleMap)
            courts.add(c)
        } while (cursor.moveToNext())
        db.close()

        findViewById<SegmentedGroup>(R.id.segmented).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.half -> loadMarkers(googleMap, 0)
                R.id.full -> loadMarkers(googleMap, 1)
                R.id.all -> loadMarkers(googleMap, 2)
            }
        }
        loadMarkers(googleMap, 2)
    }
}