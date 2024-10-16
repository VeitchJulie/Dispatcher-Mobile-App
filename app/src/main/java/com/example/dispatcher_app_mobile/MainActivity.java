package com.example.dispatcher_app_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener{

    public static MainActivity INSTANCE;

    Button btn;
    TextView text;
    EditText edit;

    String myTeamId;

    LocationManager lm;
    Criteria cr;
    Location location;
    String bestProvider;
    Double[] teamLocation;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        text = findViewById(R.id.textView2);
        edit = findViewById(R.id.editText);

        teamLocation = new Double[2];
        setLocation();

        final String TAG = "MainActivity";

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();
                    }
                });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myTeamId = edit.getText().toString();

                JSONObject object = new JSONObject();
                try {
                    object.put("id", myTeamId);
                    object.put("token", token);
                    object.put("state", "Free");
                    object.put("lat",teamLocation[0]);
                    object.put("long", teamLocation[1]);
                    object.put("endLat", "0.000000000000000000000000000000");
                    object.put("endLong", "0.000000000000000000000000000000");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://10.0.2.2:8000/teams/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent mainIntent = new Intent(MainActivity.this, AfterLogIn.class);
                        mainIntent.putExtra("myTeamId", myTeamId);
                        MainActivity.this.startActivity(mainIntent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        text.setText(error.getMessage());
                    }
                });

                queue.add(request);
            }
        });


    }

    private void setLocation() {
        cr = new Criteria();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        bestProvider = lm.getBestProvider(cr, true);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            return;
        }

        location = lm.getLastKnownLocation(bestProvider);
        lm.requestLocationUpdates(bestProvider, 1000, 1, (LocationListener) this);
        if(!(location == null)){
            teamLocation[0] = location.getLatitude();
            teamLocation[1] = location.getLongitude();
        }

    }

    private void updateApi(){
        JSONObject object = new JSONObject();
        try {
            object.put("lat",teamLocation[0]);
            object.put("long", teamLocation[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://10.0.2.2:8000/teams/" + myTeamId + "/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                text.setText("posted!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                text.setText(error.getMessage());
            }
        });

        queue.add(request);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(myTeamId != null){
            setLocation();
            updateApi();
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        if(myTeamId != null){
            setLocation();
            updateApi();
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
//        text2.setText("disabled ");
    }

    public static MainActivity get(){
        return INSTANCE;
    }

    public Double[] getTeamLocation() {
        return teamLocation;
    }

    public String getToken() {
        return token;
    }
}