package com.dce.grabtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Aptitude;
import com.dce.grabtutor.Model.Schedule;
import com.dce.grabtutor.Model.Subject;
import com.dce.grabtutor.Model.TrackGPS;
import com.dce.grabtutor.Model.URI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvNavUserFullName;
    TextView tvNavUserEmail;
    double longitude;
    double latitude;
    private TrackGPS gps;

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

        gps = new TrackGPS(TutorMenuActivity.this);
        if (gps.canGetLocation()) {
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            updateLocationInServer(longitude, latitude);
//            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            gps.showSettingsAlert();
        }

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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule) {
            loadSchedules();
        } else if (id == R.id.nav_messages) {
            loadConversations();
        } else if (id == R.id.nav_logout) {
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
                                Toast.makeText(TutorMenuActivity.this, "Location Update Sent to Server", Toast.LENGTH_SHORT).show();
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
