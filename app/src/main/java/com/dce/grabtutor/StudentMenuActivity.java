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
import android.widget.SeekBar;
import android.widget.Spinner;
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

public class StudentMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView tvNavUserFullName;
    TextView tvNavUserEmail;
    TextView tvSearchDistanceValue;

    //    EditText etSearchRangeDistance;
    SeekBar sbSearchDistance;
    Spinner spSearchDay;
    Spinner spSearchHourFrom;
    Spinner spSearchHourTo;
    Spinner spSearchMeridiemFrom;
    Spinner spSearchMeridiemTo;
    Spinner spSearchSubject;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_menu);
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
        tvSearchDistanceValue = (TextView) findViewById(R.id.tvSearchDistanceValue);

//        etSearchRangeDistance = (EditText) findViewById(R.id.etSearchRangeDistance);
        sbSearchDistance = (SeekBar) findViewById(R.id.sbSearchDistance);
        sbSearchDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSearchDistanceValue.setText("Search Distance - " + (progress + 1) + "Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            loadSchedules();
        } else if (id == R.id.nav_messages) {
            loadConversations();
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

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

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
                android.Manifest.permission.ACCESS_FINE_LOCATION)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
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
//                                Toast.makeText(StudentMenuActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(StudentMenuActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
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
            final int searchDistance = (sbSearchDistance.getProgress() + 1);
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

                                            Location locationTutor = new Location("");
                                            locationTutor.setLatitude(account.getAcc_latitude());
                                            locationTutor.setLongitude(account.getAcc_longitude());

                                            Location locationCurrent = new Location("");
                                            locationCurrent.setLatitude(Account.loggedAccount.getAcc_latitude());
                                            locationCurrent.setLongitude(Account.loggedAccount.getAcc_longitude());

                                            double distanceInKilometers = (double) locationCurrent.distanceTo(locationTutor) / 1000;
                                            if (distanceInKilometers <= searchDistance) {
                                                searchedTutor.setDistance(Double.valueOf(distanceInKilometers).intValue());
                                                SearchedTutor.searchedTutors.add(searchedTutor);
                                                count++;
                                            }

                                        }

                                        StudentTutorSearchInformationActivity.subj_name = searchSubject;

                                        if (count > 0) {
                                            Intent intent = new Intent(StudentMenuActivity.this, StudentTutorSearchListActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(StudentMenuActivity.this, "No Tutors Found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(StudentMenuActivity.this, "No Tutors Found", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(StudentMenuActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
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

    // Schedules
    public void loadSchedules() {

        Schedule.schedules_monday = new ArrayList<>();
        Schedule.schedules_tuesday = new ArrayList<>();
        Schedule.schedules_wednesday = new ArrayList<>();
        Schedule.schedules_thursday = new ArrayList<>();
        Schedule.schedules_friday = new ArrayList<>();
        Schedule.schedules_saturday = new ArrayList<>();
        Schedule.schedules_sunday = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.SCHEDULE_LOAD_STUDENT,
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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
                                        student.setAcc_user(sched.getString(Account.ACCOUNT_USERNAME));
                                        student.setAcc_pass(sched.getString(Account.ACCOUNT_PASSWORD));
                                        student.setAcc_fname(sched.getString(Account.ACCOUNT_FIRST_NAME));
                                        student.setAcc_mname(sched.getString(Account.ACCOUNT_MIDDLE_NAME));
                                        student.setAcc_lname(sched.getString(Account.ACCOUNT_LAST_NAME));
                                        student.setAcc_email(sched.getString(Account.ACCOUNT_EMAIL));
                                        student.setAcc_gender(sched.getString(Account.ACCOUNT_GENDER));
                                        student.setAcc_type(sched.getString(Account.ACCOUNT_TYPE));
                                        student.setAcc_token(sched.getString(Account.ACCOUNT_TOKEN));
                                        student.setAcc_latitude(sched.getDouble(Account.ACCOUNT_LATITUDE));
                                        student.setAcc_longitude(sched.getDouble(Account.ACCOUNT_LONGITUDE));

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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
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
                                        student.setAcc_id(sched.getInt(Schedule.SCHEDULE_TUTOR_ID));
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

                                Intent intent = new Intent(StudentMenuActivity.this, StudentScheduleManagementActivity.class);
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

    // Messages or Conversations
    public void loadConversations() {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

}
