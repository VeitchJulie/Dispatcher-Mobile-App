package com.example.dispatcher_app_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

public class History extends AppCompatActivity {

    private static final String TAG = "History";
    Team team;
    Case[] cases;
    LinearLayout linearLayout;
    ImageButton backButton;
    TextView pastCasesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        String myTeamId = intent.getStringExtra("myTeamId");
        linearLayout = findViewById(R.id.linearLayoutHistory);
        backButton = findViewById(R.id.backButton);
        pastCasesText = findViewById(R.id.pastCasesText);

        pastCasesText.setText("Past Cases for " + myTeamId);

        RequestQueue queue = Volley.newRequestQueue(History.this);
        String url = "http://10.0.2.2:8000/teams/" + 1111 + "/cases/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();

                team = gson.fromJson(response.toString(), Team.class);
                linearLayout.removeAllViews();
                for(int i = 0; i<team.getCases().length; i++){
//            if(cases[i].getState().equals("PAST")){
                    TextView textView = new TextView(History.this);
                    textView.setBackgroundResource(R.drawable.text_border);
                    textView.setTextSize(24);
                    textView.setGravity(Gravity.CENTER);
                    textView.setText(String.valueOf(team.getCases()[i].getId()));
                    linearLayout.addView(textView);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "error");
            }
        });

        queue.add(request);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(History.this, AfterLogIn.class);
                mainIntent.putExtra("myTeamId", myTeamId);
                History.this.startActivity(mainIntent);
            }
        });

    }
}