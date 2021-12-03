package com.example.dispatcher_app_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class AfterLogIn extends AppCompatActivity {

    public static AfterLogIn INSTANCE;
    TextView caseList, newTextView, caseName, caseDetails, casePhone, isPatientTextView;
    ImageButton logOutButton, historyButton;
    Button acceptButton, endButton, mapButton;
    LinearLayout linearLayoutPast, linearLayoutNew;
    String myTeamId, request;
    protected static boolean isCase = false;
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
//        welcomeText = findViewById(R.id.welcomeText);
        newTextView = findViewById(R.id.newTextView);
        caseName = findViewById(R.id.caseName);
        casePhone = findViewById(R.id.casePhone);
        caseDetails = findViewById(R.id.caseDetails);
        isPatientTextView = findViewById(R.id.isPatientTextView);
//        caseList = findViewById(R.id.caseList);
//        welcomeText.setText("Welcome " + myTeamId);
        caseLocation = new Double[2];
        logOutButton = findViewById(R.id.logOutButton);
        mapButton = findViewById(R.id.mapButton);
        acceptButton = findViewById(R.id.acceptButton);
        historyButton = findViewById(R.id.historyButton);
        endButton = findViewById(R.id.endButton);
//        linearLayoutPast = findViewById(R.id.linearPast);
        linearLayoutNew = findViewById(R.id.linearNew);

        getCaseList(true);

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
                        changeCaseState("ONGOING");
                        newTextView.setText("Ongoing");
                        acceptButton.setVisibility(View.INVISIBLE);
                        endButton.setVisibility(View.VISIBLE);
                        mapButton.setVisibility(View.VISIBLE);
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
                JSONObject TeamObject = new JSONObject();
                try {
                    TeamObject.put("state", "Free");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
                String url = "http://10.0.2.2:8000/teams/"+myTeamId + "/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, TeamObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        changeCaseState("PAST");
                        newTextView.setText("No new cases right now");
//                        caseName.setText("No new cases right now");
                        linearLayoutNew.setVisibility(View.INVISIBLE);
                        caseName.setText("");
                        casePhone.setText("");
                        caseDetails.setText("");
                        isPatientTextView.setText("");
                        acceptButton.setVisibility(View.INVISIBLE);
                        mapButton.setVisibility(View.INVISIBLE);
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
                Intent intent = new Intent(AfterLogIn.this, PastCases.class);
                intent.putExtra("myTeamId", myTeamId);
                AfterLogIn.this.startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AfterLogIn.this, Map.class);
                intent.putExtra("myTeamId", myTeamId);
                intent.putExtra("lat", currCase.getLat());
                intent.putExtra("lng", currCase.getLng());
                AfterLogIn.this.startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        if(error.networkResponse == null){
                            Intent mainIntent = new Intent(AfterLogIn.this, MainActivity.class);
                            AfterLogIn.this.startActivity(mainIntent);
                        }else {
                            caseName.setText("error");
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

    public void getCaseList(boolean isOnCreate){
        RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
        String url = "http://10.0.2.2:8000/teams/" + myTeamId + "/cases/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();

                team = gson.fromJson(response.toString(), Team.class);

                if(!isOnCreate) {
                    currCase = team.getCase((team.getCases().length - 1));
                    if (currCase.getState().equals("TOACCEPT")) {
                        acceptButton.setVisibility(View.VISIBLE);
                        endButton.setVisibility(View.INVISIBLE);
                        mapButton.setVisibility(View.INVISIBLE);
                    } else if(currCase.getState().equals("ONGOING")) {
                        acceptButton.setVisibility(View.INVISIBLE);
                        endButton.setVisibility(View.VISIBLE);
                        mapButton.setVisibility(View.VISIBLE);
                    }
                    linearLayoutNew.setVisibility(View.VISIBLE);
                    caseName.setText(String.format("%s", currCase.getName()));
                    casePhone.setText(String.format("%s", currCase.getPhone()));
                    caseDetails.setText(String.format("%s", currCase.getExtra_information()));
                    if(currCase.isPatient() == true){
                        isPatientTextView.setVisibility(View.VISIBLE);
                        isPatientTextView.setText("Caller is patient");
                    } else{
                        isPatientTextView.setVisibility(View.INVISIBLE);
                    }
                    caseLocation[0] = currCase.getLat();
                    caseLocation[1] = currCase.getLng();
                }else if(isOnCreate && team.getCases().length >= 1){
                    currCase = team.getCase((team.getCases().length - 1));
                    linearLayoutNew.setVisibility(View.VISIBLE);
                    caseName.setText(String.format("%s", currCase.getName()));
                    casePhone.setText(String.format("%s", currCase.getPhone()));
                    caseDetails.setText(String.format("%s", currCase.getExtra_information()));
                    if(currCase.isPatient() == true){
                        isPatientTextView.setVisibility(View.VISIBLE);
                        isPatientTextView.setText("Caller is patient");
                    }else{
                        isPatientTextView.setVisibility(View.INVISIBLE);
                    }
                    caseLocation[0] = currCase.getLat();
                    caseLocation[1] = currCase.getLng();
                    Log.i(TAG, currCase.toString());
//                    endButton.setVisibility(View.VISIBLE);
//                    mapButton.setVisibility(View.VISIBLE);
                    if (currCase.getState().equals("TOACCEPT")) {
                        acceptButton.setVisibility(View.VISIBLE);
                        endButton.setVisibility(View.INVISIBLE);
                        mapButton.setVisibility(View.INVISIBLE);
                    } else if(currCase.getState().equals("ONGOING")) {
                        acceptButton.setVisibility(View.INVISIBLE);
                        endButton.setVisibility(View.VISIBLE);
                        mapButton.setVisibility(View.VISIBLE);
                    } else if(currCase.getState().equals("PAST")){
                        newTextView.setText("No new cases right now");
                        linearLayoutNew.setVisibility(View.INVISIBLE);
                        caseName.setText("");
                        casePhone.setText("");
                        caseDetails.setText("");
                        isPatientTextView.setText("");
                        acceptButton.setVisibility(View.INVISIBLE);
                        mapButton.setVisibility(View.INVISIBLE);
                        endButton.setVisibility(View.INVISIBLE);
                    }
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "error");
            }
        });

        queue.add(request);
    }

    public void changeCaseState(String state){
        JSONObject CaseObject = new JSONObject();
        try {
            CaseObject.put("state", state);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue caseQueue = Volley.newRequestQueue(AfterLogIn.this);
        String caseUrl = "http://10.0.2.2:8000/cases/"+ currCase.getId() + "/";
        JsonObjectRequest caseRequest = new JsonObjectRequest(Request.Method.PATCH, caseUrl, CaseObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                currCase.setState(state);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error");
            }
        });

        caseQueue.add(caseRequest);
    }
}