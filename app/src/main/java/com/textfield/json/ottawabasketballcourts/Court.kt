package com.textfield.json.ottawabasketballcourts

import android.database.Cursor
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class Court(cursor: Cursor) {

    val name = cursor.getString(cursor.getColumnIndex("name"))
    val nameFr = cursor.getString(cursor.getColumnIndex("nameFr"))
    val type = cursor.getString(cursor.getColumnIndex("type"))
    val id = cursor.getInt(cursor.getColumnIndex("id"))
    val location: LatLng = LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude")))

    override fun toString(): String {
        if (Locale.getDefault().displayLanguage.contains("fr")) {
            return nameFr
        }
        return name
    }
}
