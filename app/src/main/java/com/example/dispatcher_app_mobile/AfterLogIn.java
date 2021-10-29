package com.example.dispatcher_app_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AfterLogIn extends AppCompatActivity {

    TextView welcomeText, caseList;
    Button logOutButton;
    String myTeamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_log_in);
        Intent intent = getIntent();
        myTeamId = intent.getStringExtra("teamId");

        welcomeText = findViewById(R.id.welcomeText);
        caseList = findViewById(R.id.textView3);
        welcomeText.setText("Welcome " + myTeamId);

        logOutButton = findViewById(R.id.logOutButton);

        RequestQueue queue = Volley.newRequestQueue(AfterLogIn.this);
        String url = "http://10.0.2.2:8000/teams/" + myTeamId + "/cases/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                caseList.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                caseList.setText(error.getMessage());
            }
        });

        queue.add(request);

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
}