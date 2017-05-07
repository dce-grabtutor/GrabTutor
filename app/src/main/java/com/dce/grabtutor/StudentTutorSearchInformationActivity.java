package com.dce.grabtutor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Schedule;
import com.dce.grabtutor.Model.SearchedTutor;
import com.dce.grabtutor.Model.URI;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentTutorSearchInformationActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener {

    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleMap mMap;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tutor_search_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tutor Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        intent.getIntExtra("position", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                showTutorLocation();
            }
        } else {
            mMap.setMyLocationEnabled(true);
            showTutorLocation();
        }
    }

    public void showTutorLocation() {
        Account account = SearchedTutor.searchedTutors.get(position).getAccount();
        Schedule schedule = SearchedTutor.searchedTutors.get(position).getSchedule();

        LatLng latLng = new LatLng(account.getAcc_latitude(), account.getAcc_longitude());
        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Tutor Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        MarkerOptions markerCurrent = new MarkerOptions();
        markerCurrent.position(new LatLng(Account.loggedAccount.getAcc_latitude(), Account.loggedAccount.getAcc_longitude()));

        markerCurrent.title("Your Location");
        markerCurrent.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(markerCurrent);

        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        boundBuilder.include(markerOptions.getPosition());
        boundBuilder.include(markerCurrent.getPosition());
        LatLngBounds bounds = boundBuilder.build();

        int height = getResources().getDisplayMetrics().heightPixels;
        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width * 0.40);

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng)      // Sets the center of the map to location user
//                .zoom(17)                   // Sets the zoom
//                .bearing(90)                // Sets the orientation of the camera to east
//                .tilt(40)                  // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void btnBookNowClick(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_BOOKING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                SearchedTutor.searchedTutors.remove(position);
                                Toast.makeText(StudentTutorSearchInformationActivity.this, "Tutorial Request has been sent", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(StudentTutorSearchInformationActivity.this, "Failed to Send Tutorial Request", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(StudentTutorSearchInformationActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Schedule.SCHEDULE_ID, String.valueOf(SearchedTutor.searchedTutors.get(position).getSchedule().getSched_id()));
                params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
