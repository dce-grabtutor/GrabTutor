package com.dce.grabtutor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Handler.AccountHandler;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Aptitude;
import com.dce.grabtutor.Model.PendingBooking;
import com.dce.grabtutor.Model.Schedule;
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

public class TutorMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    TextView tvNavUserFullName;
    TextView tvNavUserEmail;
    double longitude;
    double latitude;

//    TrackGPS gps;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        tvNavUserFullName = (TextView) headerView.findViewById(R.id.tvNavUserFullName);
        tvNavUserEmail = (TextView) headerView.findViewById(R.id.tvNavUserEmail);

        Account account = Account.loggedAccount;
        String fullName = account.getAcc_lname() + ", " + account.getAcc_fname() + " " + account.getAcc_mname();
        String email = account.getAcc_email();

        tvNavUserFullName.setText(fullName);
        tvNavUserEmail.setText(email);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
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
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

//        gps = new TrackGPS(TutorMenuActivity.this);
//        if (gps.canGetLocation()) {
//            longitude = gps.getLongitude();
//            latitude = gps.getLatitude();
//            Location loc = new Location("");
//
//            loc.setLongitude(longitude);
//            loc.setLatitude(latitude);
//            onLocationChanged(loc);
////            updateLocationInServer(longitude, latitude);
////            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
//        } else {
//            gps.showSettingsAlert();
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        System.out.println("Location Changed: " + location.getLatitude() + ", " + location.getLongitude());
        Account.loggedAccount.setAcc_latitude(location.getLatitude());
        Account.loggedAccount.setAcc_longitude(location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void btnSetLocationClick(View view) {
        try {
            LatLng center = mMap.getCameraPosition().target;
            updateLocationInServer(center.longitude, center.latitude);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            loadSchedules();
        } else if (id == R.id.nav_subjects) {
            loadSubjects();
        } else if (id == R.id.nav_tutor_requests) {
            loadTutorialRequests();
        } else if (id == R.id.nav_messages) {
            loadConversations();
        } else if (id == R.id.nav_upload) {
            Intent intent = new Intent(this, TutorRepositoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            AccountHandler accountHandler = new AccountHandler(this);
            accountHandler.removeLoggedAccount();
            LoginActivity.loggedOut = true;
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Messages or Conversations
    public void loadConversations() {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

    public void loadSubjects() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_SUBJECT_LOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Subject.currentSubjects = new ArrayList<>();

                            if (jsonObject.getBoolean("success")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("subjects");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonSubject = jsonArray.getJSONObject(i);

                                    Subject subject = new Subject();
                                    subject.setSubj_id(jsonSubject.getInt(Subject.SUBJECT_ID));
                                    subject.setSubj_name(jsonSubject.getString(Subject.SUBJECT_NAME));

                                    Subject.currentSubjects.add(subject);
                                }

                                Intent intent = new Intent(TutorMenuActivity.this, TutorSubjectManagementActivity.class);
                                startActivity(intent);
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
                        Toast.makeText(TutorMenuActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Schedules
    public void loadSchedules() {

        Schedule.schedules_monday = new ArrayList<>();
        Schedule.schedules_tuesday = new ArrayList<>();
        Schedule.schedules_wednesday = new ArrayList<>();
        Schedule.schedules_thursday = new ArrayList<>();
        Schedule.schedules_friday = new ArrayList<>();
        Schedule.schedules_saturday = new ArrayList<>();
        Schedule.schedules_sunday = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.SCHEDULE_LOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("success")) {
                                JSONArray arrayMonday = jsonObject.getJSONArray("monday");
                                JSONArray arrayTuesday = jsonObject.getJSONArray("tuesday");
                                JSONArray arrayWednesday = jsonObject.getJSONArray("wednesday");
                                JSONArray arrayThursday = jsonObject.getJSONArray("thursday");
                                JSONArray arrayFriday = jsonObject.getJSONArray("friday");
                                JSONArray arraySaturday = jsonObject.getJSONArray("saturday");
                                JSONArray arraySunday = jsonObject.getJSONArray("sunday");

                                for (int i = 0; i < arrayMonday.length(); i++) {
                                    JSONObject sched = arrayMonday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_monday.add(schedule);
                                }

                                for (int i = 0; i < arrayTuesday.length(); i++) {
                                    JSONObject sched = arrayTuesday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_tuesday.add(schedule);
                                }

                                for (int i = 0; i < arrayWednesday.length(); i++) {
                                    JSONObject sched = arrayWednesday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_wednesday.add(schedule);
                                }

                                for (int i = 0; i < arrayThursday.length(); i++) {
                                    JSONObject sched = arrayThursday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_thursday.add(schedule);
                                }

                                for (int i = 0; i < arrayFriday.length(); i++) {
                                    JSONObject sched = arrayFriday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_friday.add(schedule);
                                }

                                for (int i = 0; i < arraySaturday.length(); i++) {
                                    JSONObject sched = arraySaturday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_saturday.add(schedule);
                                }

                                for (int i = 0; i < arraySunday.length(); i++) {
                                    JSONObject sched = arraySunday.getJSONObject(i);

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(sched.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(sched.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(sched.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(sched.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(sched.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setSched_status(sched.getString(Schedule.SCHEDULE_STATUS));
                                    schedule.setAcc_id(sched.getInt(Schedule.SCHEDULE_ACC_ID));

                                    if (schedule.getSched_status().equals("Reserved")) {
                                        Subject subject = new Subject();
                                        subject.setSubj_id(sched.getInt(Subject.SUBJECT_ID));
                                        subject.setSubj_name(sched.getString(Subject.SUBJECT_NAME));

                                        schedule.setSubject(subject);

                                        Account student = new Account();
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_STUD_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));

                                        schedule.setStudent(student);
                                    } else {
                                        Subject subject = new Subject();
                                        Account account = new Account();

                                        schedule.setSubject(subject);
                                        schedule.setStudent(account);
                                    }

                                    Schedule.schedules_sunday.add(schedule);
                                }

                                Intent intent = new Intent(TutorMenuActivity.this, TutorScheduleManagementActivity.class);
                                startActivity(intent);
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Schedule.SCHEDULE_ACC_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void loadTutorialRequests() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_REQUEST_LOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            PendingBooking.pendingBookings = new ArrayList<>();

                            if (jsonObject.getBoolean("success")) {

                                JSONArray arrayPendingBookings = jsonObject.getJSONArray("pending_bookings");
                                for (int i = 0; i < arrayPendingBookings.length(); i++) {
                                    JSONObject jsonPendingBooking = arrayPendingBookings.getJSONObject(i);
                                    PendingBooking pendingBooking = new PendingBooking();
                                    pendingBooking.setPb_id(jsonPendingBooking.getInt(PendingBooking.PENDING_BOOKING_ID));

                                    JSONObject jsonSchedule = jsonPendingBooking.getJSONObject("schedule");
                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(jsonSchedule.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(jsonSchedule.getString(Schedule.SCHEDULE_DAY));
                                    schedule.setSched_hour(jsonSchedule.getInt(Schedule.SCHEDULE_HOUR));
                                    schedule.setSched_minute(jsonSchedule.getInt(Schedule.SCHEDULE_MINUTE));
                                    schedule.setSched_meridiem(jsonSchedule.getString(Schedule.SCHEUDLE_MERIDIEM));
                                    schedule.setAcc_id(jsonSchedule.getInt(Account.ACCOUNT_ID));

                                    JSONObject jsonAccount = jsonPendingBooking.getJSONObject("account");
                                    Account account = new Account();
                                    account.setAcc_id(jsonAccount.getInt(Account.ACCOUNT_ID));
                                    account.setAcc_user(jsonAccount.getString(Account.ACCOUNT_USERNAME));
                                    account.setAcc_fname(jsonAccount.getString(Account.ACCOUNT_FIRST_NAME));
                                    account.setAcc_mname(jsonAccount.getString(Account.ACCOUNT_MIDDLE_NAME));
                                    account.setAcc_lname(jsonAccount.getString(Account.ACCOUNT_LAST_NAME));
                                    account.setAcc_email(jsonAccount.getString(Account.ACCOUNT_EMAIL));
                                    account.setAcc_gender(jsonAccount.getString(Account.ACCOUNT_GENDER));
                                    account.setAcc_type(jsonAccount.getString(Account.ACCOUNT_TYPE));
                                    account.setAcc_token(jsonAccount.getString(Account.ACCOUNT_TOKEN));
                                    account.setAcc_longitude(jsonAccount.getDouble(Account.ACCOUNT_LONGITUDE));
                                    account.setAcc_latitude(jsonAccount.getDouble(Account.ACCOUNT_LATITUDE));

                                    JSONObject jsonSubject = jsonPendingBooking.getJSONObject("subject");
                                    Subject subject = new Subject();
                                    subject.setSubj_id(jsonSubject.getInt(Subject.SUBJECT_ID));
                                    subject.setSubj_name(jsonSubject.getString(Subject.SUBJECT_NAME));

                                    pendingBooking.setSchedule(schedule);
                                    pendingBooking.setAccount(account);
                                    pendingBooking.setSubject(subject);

                                    PendingBooking.pendingBookings.add(pendingBooking);
                                }
                            }

                            Intent intent = new Intent(TutorMenuActivity.this, TutorTutorialRequestListActivity.class);
                            startActivity(intent);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(TutorMenuActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnAptitudeTestClick(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.APTITUDE_STATUS_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("success")) {
                                Aptitude.currentAptitude = new Aptitude();

                                Aptitude aptitude = new Aptitude();
                                aptitude.setApt_id(jsonObject.getInt(Aptitude.APTITUDE_ID));
                                aptitude.setAcc_id(jsonObject.getInt(Aptitude.APTITUDE_ACC_ID));
                                aptitude.setApt_start(jsonObject.getString(Aptitude.APTITUDE_START));
                                aptitude.setApt_end(jsonObject.getString(Aptitude.APTITUDE_END));

                                Aptitude.currentAptitude = aptitude;

                                Intent intent = new Intent(TutorMenuActivity.this, TutorAptitudeTestPreExamActivity.class);
                                intent.putExtra("ongoing", true);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(TutorMenuActivity.this, TutorAptitudeTestPreExamActivity.class);
                                intent.putExtra("ongoing", false);
                                startActivity(intent);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(TutorMenuActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
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
                params.put(Schedule.SCHEDULE_ACC_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
                                Toast.makeText(TutorMenuActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(TutorMenuActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
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

}
