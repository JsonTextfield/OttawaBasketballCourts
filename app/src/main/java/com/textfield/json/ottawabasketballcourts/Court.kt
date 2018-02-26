package com.textfield.json.ottawabasketballcourts

import android.database.Cursor
import com.google.android.gms.maps.model.LatLng

/**
 * Created by Jason on 20/02/2016.
 */
class Court(cursor: Cursor) {
    val type = cursor.getString(cursor.getColumnIndex("courttype"))
    val name = cursor.getString(cursor.getColumnIndex("name"))
    val lat = cursor.getDouble(cursor.getColumnIndex("latitude"))
    val lng = cursor.getDouble(cursor.getColumnIndex("longitude"))
    val id = cursor.getInt(cursor.getColumnIndex("id"))
    val location: LatLng = LatLng(lat, lng)


    override fun toString(): String {
        return name
    }
}
