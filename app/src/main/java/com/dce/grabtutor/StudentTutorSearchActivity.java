package com.dce.grabtutor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.dce.grabtutor.Model.Subject;
import com.dce.grabtutor.Model.URI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentTutorSearchActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    EditText etSearchRangeDistance;
    Spinner spSearchDay;
    Spinner spSearchHourFrom;
    Spinner spSearchHourTo;
    Spinner spSearchMeridiemFrom;
    Spinner spSearchMeridiemTo;
    Spinner spSearchSubject;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tutor_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tutor Search");

        etSearchRangeDistance = (EditText) findViewById(R.id.etSearchRangeDistance);
        spSearchDay = (Spinner) findViewById(R.id.spSearchDay);
        spSearchHourFrom = (Spinner) findViewById(R.id.spSearchHourFrom);
        spSearchHourTo = (Spinner) findViewById(R.id.spSearchHourTo);
        spSearchMeridiemFrom = (Spinner) findViewById(R.id.spSearchMeridiemFrom);
        spSearchMeridiemTo = (Spinner) findViewById(R.id.spSearchMeridiemTo);
        spSearchSubject = (Spinner) findViewById(R.id.spSearchSubject);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //send updated location to server
        updateLocationInServer(location.getLongitude(), location.getLatitude());

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    public void updateLocationInServer(final double longitude, final double latitude) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.USER_LOCATION_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(StudentTutorSearchActivity.this, "Location Update Sent to Server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(StudentTutorSearchActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                params.put(Account.ACCOUNT_LONGITUDE, String.valueOf(longitude));
                params.put(Account.ACCOUNT_LATITUDE, String.valueOf(latitude));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnSearchClick(View view) {
        try {
            int searchDistance = Integer.parseInt(etSearchRangeDistance.getText().toString());
            final int searchStart = Integer.parseInt(spSearchHourFrom.getSelectedItem().toString());
            final int searchEnd = Integer.parseInt(spSearchHourTo.getSelectedItem().toString());

            final String searchMeridiemStart = spSearchMeridiemFrom.getSelectedItem().toString();
            final String searchMeridiemEnd = spSearchMeridiemTo.getSelectedItem().toString();

            final String searchDay = spSearchDay.getSelectedItem().toString();
            final String searchSubject = spSearchSubject.getSelectedItem().toString();

            if (searchDistance > 0 && searchDistance < 21) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_SEARCH,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {

                                        SearchedTutor.searchedTutors = new ArrayList<>();

                                        int count = 0;
                                        JSONArray arrayAccount = jsonObject.getJSONArray("accounts");
                                        for (int i = 0; i < arrayAccount.length(); i++) {
                                            JSONObject accountObject = arrayAccount.getJSONObject(i);

                                            Account account = new Account();
                                            account.setAcc_id(accountObject.getInt(Account.ACCOUNT_ID));
                                            account.setAcc_user(accountObject.getString(Account.ACCOUNT_USERNAME));
                                            account.setAcc_fname(accountObject.getString(Account.ACCOUNT_FIRST_NAME));
                                            account.setAcc_mname(accountObject.getString(Account.ACCOUNT_MIDDLE_NAME));
                                            account.setAcc_lname(accountObject.getString(Account.ACCOUNT_LAST_NAME));
                                            account.setAcc_email(accountObject.getString(Account.ACCOUNT_EMAIL));
                                            account.setAcc_gender(accountObject.getString(Account.ACCOUNT_GENDER));
                                            account.setAcc_type(accountObject.getString(Account.ACCOUNT_TYPE));
                                            account.setAcc_token(accountObject.getString(Account.ACCOUNT_TOKEN));
                                            account.setAcc_longitude(accountObject.getDouble(Account.ACCOUNT_LONGITUDE));
                                            account.setAcc_latitude(accountObject.getDouble(account.ACCOUNT_LATITUDE));

                                            JSONObject scheduleObject = accountObject.getJSONObject("schedule");

                                            Schedule schedule = new Schedule();
                                            schedule.setSched_id(scheduleObject.getInt(Schedule.SCHEDULE_ID));
                                            schedule.setSched_day(scheduleObject.getString(Schedule.SCHEDULE_DAY));
                                            schedule.setSched_hour(scheduleObject.getInt(Schedule.SCHEDULE_HOUR));
                                            schedule.setSched_minute(scheduleObject.getInt(Schedule.SCHEDULE_MINUTE));
                                            schedule.setSched_meridiem(scheduleObject.getString(Schedule.SCHEUDLE_MERIDIEM));
//                                            schedule.setSched_status(scheduleObject.getString(Schedule.SCHEDULE_STATUS));
                                            schedule.setAcc_id(scheduleObject.getInt(Schedule.SCHEDULE_ACC_ID));

                                            SearchedTutor searchedTutor = new SearchedTutor();
                                            searchedTutor.setAccount(account);
                                            searchedTutor.setSchedule(schedule);

                                            SearchedTutor.searchedTutors.add(searchedTutor);
                                            count++;
                                        }

                                        if (count > 0) {
                                            Intent intent = new Intent(StudentTutorSearchActivity.this, StudentTutorSearchListActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(StudentTutorSearchActivity.this, "No Tutors Found, Please Try Again", Toast.LENGTH_SHORT).show();
                                        }
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
                                Toast.makeText(StudentTutorSearchActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                        params.put("search_day", searchDay);
                        params.put("search_hour_start", String.valueOf(searchStart));
                        params.put("search_hour_end", String.valueOf(searchEnd));
                        params.put("search_meridiem_start", searchMeridiemStart);
                        params.put("search_meridiem_end", searchMeridiemEnd);
                        params.put(Subject.SUBJECT_NAME, searchSubject);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } else {
                Toast.makeText(this, "Please provide a search range between 1 to 20Km", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Please provide a valid input", Toast.LENGTH_SHORT).show();
        }
    }

}
