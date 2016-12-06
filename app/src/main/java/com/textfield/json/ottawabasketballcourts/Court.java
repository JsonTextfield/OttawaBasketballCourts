package com.textfield.json.ottawabasketballcourts;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jason on 20/02/2016.
 */
public class Court{
    private String name, type;
    private LatLng location;
    private int id;

    public Court(String type, String text, double lat, double lng, int id) {
        name = text;
        location = new LatLng(lat, lng);
        this.type = type;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
