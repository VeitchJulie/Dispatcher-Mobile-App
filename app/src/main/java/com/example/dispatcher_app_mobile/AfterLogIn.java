package com.example.dispatcher_app_mobile;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
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
    TextView welcomeText, caseList, newTextView, newCaseTextView;
    ImageButton logOutButton, historyButton;
    Button acceptButton, endButton;
    LinearLayout linearLayoutPast, linearLayoutNew;
    String myTeamId, request;
    protected static Boolean isCase = false;
    Team team;
    Case currCase;
    String[] teamCases;
    MainActivity mainActivity = MainActivity.get();
    Double[] caseLocation;

    private static final String TAG = "AfterLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_log_in);

        INSTANCE = this;
        Intent intent = getIntent();
        myTeamId = intent.getStringExtra("myTeamId");
        welcomeText = findViewById(R.id.welcomeText);
        newTextView = findViewById(R.id.newTextView);
        newCaseTextView = findViewById(R.id.newCaseTextView);
//        caseList = findViewById(R.id.caseList);
        welcomeText.setText("Welcome " + myTeamId);
        caseLocation = new Double[2];
        logOutButton = findViewById(R.id.logOutButton);
        acceptButton = findViewById(R.id.acceptButton);
        historyButton = findViewById(R.id.historyButton);
        endButton = findViewById(R.id.endButton);
        linearLayoutPast = findViewById(R.id.linearPast);
        linearLayoutNew = findViewById(R.id.linearNew);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject object = new JSONObject();
                try {
                    object.put("state", "Busy");
                    object.put("endLat", caseLocation[0]);
                    object.put("endLong", caseLocation[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
                String url = "http://10.0.2.2:8000/teams/"+myTeamId + "/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        newTextView.setText("Ongoing");
                        acceptButton.setVisibility(View.INVISIBLE);
                        endButton.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Error");
                    }
                });

                queue.add(request);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject object = new JSONObject();
                try {
                    object.put("state", "Free");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
                String url = "http://10.0.2.2:8000/teams/"+myTeamId + "/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        newTextView.setText("New Cases");
                        newCaseTextView.setText("No new cases right now");
                        acceptButton.setVisibility(View.INVISIBLE);
                        endButton.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Error");
                    }
                });

                queue.add(request);
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(AfterLogIn.this, History.class);
                mainIntent.putExtra("myTeamId", myTeamId);
                AfterLogIn.this.startActivity(mainIntent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                JSONObject dummyObject = new JSONObject();
//                try {
//                    dummyObject.put("id", myTeamId);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
                String url = "http://10.0.2.2:8000/teams/"+ myTeamId + "/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
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

                team = gson.fromJson(response.toString(), Team.class);
                acceptButton.setVisibility(View.VISIBLE);
                currCase = team.getCase((team.getCases().length - 1));
                newCaseTextView.setText("Name: " + currCase.getName() +
                        "\nPhone: " + currCase.getPhone() +
                        "\nExtra Information: " + currCase.getExtra_information());
                caseLocation[0] = currCase.getLat();
                caseLocation[1] = currCase.getLng();
                setCaseList(team.getCases());
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
        linearLayoutPast.removeAllViews();
        for(int i = 0; i<cases.length; i++){
//            if(cases[i].getState().equals("PAST")){
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.drawable.text_border);
                textView.setTextSize(24);
                textView.setGravity(Gravity.CENTER);
                textView.setText(String.valueOf(cases[i].getId()));
                linearLayoutPast.addView(textView);
//            }
        }
    }
}