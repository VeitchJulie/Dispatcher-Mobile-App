package com.example.dispatcher_app_mobile;

import com.android.volley.toolbox.StringRequest;

public class Case {
    String id, state, lat, lng;

    @Override
    public String toString() {
        return "Case{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
