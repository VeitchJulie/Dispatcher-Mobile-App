package com.example.dispatcher_app_mobile;

import com.android.volley.toolbox.StringRequest;

public class Case {
    int id;
    String state, name, phone, extraInformation;
    double lat, lng;
    boolean isPatient;

    @Override
    public String toString() {
        return "id=" + id +
                ", " + state  +
                ", name='" + name  +
                ", phone='" + phone ;
    }

    public boolean isPatient() {
        return isPatient;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getExtra_information() {
        return extraInformation;
    }
}
