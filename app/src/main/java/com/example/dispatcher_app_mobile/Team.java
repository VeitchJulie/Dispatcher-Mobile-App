package com.example.dispatcher_app_mobile;

import java.util.Arrays;

public class Team {
    String id;
    Case[] cases;

    @Override
    public String toString() {
        return Arrays.toString(cases);
    }

    public Case[] getCases() {
        return cases;
    }
}
