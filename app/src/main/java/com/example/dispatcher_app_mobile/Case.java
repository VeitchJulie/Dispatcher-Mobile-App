package com.example.dispatcher_app_mobile;

import com.android.volley.toolbox.StringRequest;

public class Case {
    int id;
    String state, name, phone, extra_information;
    Double lat, lng;

    @Override
    public String toString() {
        return "Case{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", extra_information='" + extra_information + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    public int getId() {
        return id;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getExtra_information() {
        return extra_information;
    }
}
