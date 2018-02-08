package com.textfield.json.ottawabasketballcourts

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Jason on 20/02/2016.
 */
class Court(val type: String, var name: String?, lat: Double, lng: Double, val id: Int) {
    val location: LatLng = LatLng(lat, lng)

    override fun toString(): String {
        return name!!
    }
}
