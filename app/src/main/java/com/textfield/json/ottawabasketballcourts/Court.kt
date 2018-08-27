package com.textfield.json.ottawabasketballcourts

import android.content.Context
import android.database.Cursor
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class Court(context: Context, cursor: Cursor, googleMap: GoogleMap) {

    private val name = cursor.getString(cursor.getColumnIndex("name"))!!
    private val nameFr = cursor.getString(cursor.getColumnIndex("nameFr"))!!
    val type = cursor.getInt(cursor.getColumnIndex("type"))
    private val typeString: String = if (type == 1) {
        context.resources.getString(R.string.full)
    } else {
        context.resources.getString(R.string.half)
    }
    val id = cursor.getInt(cursor.getColumnIndex("id"))
    private val location: LatLng = LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude")))

    val marker = googleMap.addMarker(MarkerOptions().position(location)
            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
            .title("$this ($typeString)"))!!


    override fun toString(): String {
        return if (Locale.getDefault().displayLanguage.contains("fr")) nameFr else name
    }
}
