package com.example.dispatcher_app_mobile;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AfterLogIn extends AppCompatActivity {

    public static AfterLogIn INSTANCE;
    TextView welcomeText, caseList;
    Button logOutButton;
    LinearLayout linearLayout;
    String myTeamId, request;
    protected static Boolean isCase = false;
    String[] teamCases;

    private static final String TAG = "AfterLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_log_in);

        INSTANCE = this;
        Intent intent = getIntent();
        myTeamId = intent.getStringExtra("teamId");
        request = intent.getStringExtra("case");
        welcomeText = findViewById(R.id.welcomeText);
//        caseList = findViewById(R.id.caseList);
        welcomeText.setText("Welcome " + myTeamId);

        logOutButton = findViewById(R.id.logOutButton);
        linearLayout = findViewById(R.id.linear);

//        caseList.setText(request);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject dummyObject = new JSONObject();
                try {
                    dummyObject.put("id", myTeamId);
                    dummyObject.put("token", "token");
                    dummyObject.put("token", "token");
                    dummyObject.put("state", "Free");
                    dummyObject.put("lat",0);
                    dummyObject.put("long", 0);
                    dummyObject.put("endLat", "0.000000000000000000000000000000");
                    dummyObject.put("endLong", "0.000000000000000000000000000000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
                String url = "http://10.0.2.2:8000/teams/"+myTeamId + "/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, dummyObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent mainIntent = new Intent(AfterLogIn.this, MainActivity.class);
                        AfterLogIn.this.startActivity(mainIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 204){
                            Intent mainIntent = new Intent(AfterLogIn.this, MainActivity.class);
                            AfterLogIn.this.startActivity(mainIntent);
                        }else {
                            welcomeText.setText("error");
                        }
                    }
                });

                queue.add(request);
            }
        });
    }

    public static AfterLogIn get(){
        return INSTANCE;
    }

    public void getCaseList(){

        RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
        String url = "http://10.0.2.2:8000/teams/" + myTeamId + "/cases/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();

                Team team = gson.fromJson(response.toString(), Team.class);
                setCaseList(team.getCases());
//                caseList.setText(String.valueOf(team.getCases().length));
//
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "error");
            }
        });

        queue.add(request);
    }

    public void setCaseList(Case[] cases){
        linearLayout.removeAllViews();
        for(int i = 0; i<cases.length; i++){
            TextView textView = new TextView(this);
            textView.setText(cases[i].toString());
            linearLayout.addView(textView);
        }
    }
}