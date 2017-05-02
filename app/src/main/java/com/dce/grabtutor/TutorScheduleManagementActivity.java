package com.dce.grabtutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Service.Model.Account;
import com.dce.grabtutor.Service.Model.Schedule;
import com.dce.grabtutor.Service.Model.URI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorScheduleManagementActivity extends AppCompatActivity {

    Spinner spScheduleDay;
    Spinner spScheduleTimeHour;
    Spinner spScheduleTimeMinute;
    Spinner spScheduleTimeMeridiem;

    TextView tvScheduleMonAvailableCount;
    TextView tvScheduleTueAvailableCount;
    TextView tvScheduleWedAvailableCount;
    TextView tvScheduleThuAvailableCount;
    TextView tvScheduleFriAvailableCount;
    TextView tvScheduleSatAvailableCount;
    TextView tvScheduleSunAvailableCount;

    TextView tvScheduleMonReservedCount;
    TextView tvScheduleTueReservedCount;
    TextView tvScheduleWedReservedCount;
    TextView tvScheduleThuReservedCount;
    TextView tvScheduleFriReservedCount;
    TextView tvScheduleSatReservedCount;
    TextView tvScheduleSunReservedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_schedule_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Schedule Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spScheduleDay = (Spinner) findViewById(R.id.spScheduleDay);
        spScheduleTimeHour = (Spinner) findViewById(R.id.spScheduleTimeHour);
        spScheduleTimeMinute = (Spinner) findViewById(R.id.spScheduleTimeMinute);
        spScheduleTimeMeridiem = (Spinner) findViewById(R.id.spScheduleTimeMeridiem);

        tvScheduleMonAvailableCount = (TextView) findViewById(R.id.tvScheduleMonAvailableCount);
        tvScheduleTueAvailableCount = (TextView) findViewById(R.id.tvScheduleTueAvailableCount);
        tvScheduleWedAvailableCount = (TextView) findViewById(R.id.tvScheduleWedAvailableCount);
        tvScheduleThuAvailableCount = (TextView) findViewById(R.id.tvScheduleThuAvailableCount);
        tvScheduleFriAvailableCount = (TextView) findViewById(R.id.tvScheduleFriAvailableCount);
        tvScheduleSatAvailableCount = (TextView) findViewById(R.id.tvScheduleSatAvailableCount);
        tvScheduleSunAvailableCount = (TextView) findViewById(R.id.tvScheduleSunAvailableCount);

        tvScheduleMonReservedCount = (TextView) findViewById(R.id.tvScheduleMonReservedCount);
        tvScheduleTueReservedCount = (TextView) findViewById(R.id.tvScheduleTueReservedCount);
        tvScheduleWedReservedCount = (TextView) findViewById(R.id.tvScheduleWedReservedCount);
        tvScheduleThuReservedCount = (TextView) findViewById(R.id.tvScheduleThuReservedCount);
        tvScheduleFriReservedCount = (TextView) findViewById(R.id.tvScheduleFriReservedCount);
        tvScheduleSatReservedCount = (TextView) findViewById(R.id.tvScheduleSatReservedCount);
        tvScheduleSunReservedCount = (TextView) findViewById(R.id.tvScheduleSunReservedCount);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedules();
    }

    protected void loadSchedules() {
        scheduleStatusCounter(Schedule.schedules_monday, "Monday");
        scheduleStatusCounter(Schedule.schedules_tuesday, "Tuesday");
        scheduleStatusCounter(Schedule.schedules_wednesday, "Wednesday");
        scheduleStatusCounter(Schedule.schedules_thursday, "Thursday");
        scheduleStatusCounter(Schedule.schedules_friday, "Friday");
        scheduleStatusCounter(Schedule.schedules_saturday, "Saturday");
        scheduleStatusCounter(Schedule.schedules_sunday, "Sunday");
    }

    protected void scheduleStatusCounter(ArrayList<Schedule> schedules, String schedule_day) {
        int availableCount = 0;
        int reservedCount = 0;

        for (Schedule schedule : schedules) {
            String sched_status = schedule.getSched_status();
            if (sched_status.equals("Available")) {
                availableCount++;
            } else if (sched_status.equals("Reserved")) {
                reservedCount++;
            }
        }

        if (schedule_day.equals("Monday")) {
            tvScheduleMonAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleMonReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Tuesday")) {
            tvScheduleTueAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleTueReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Wednesday")) {
            tvScheduleWedAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleWedReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Thursday")) {
            tvScheduleThuAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleThuReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Friday")) {
            tvScheduleFriAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleFriReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Saturday")) {
            tvScheduleSatAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleSatReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Sunday")) {
            tvScheduleSunAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleSunReservedCount.setText(String.valueOf(reservedCount));
        }
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

    public void btnAddScheduleClick(View view) {
        try {
            final ProgressDialog progDialog = new ProgressDialog(this);
            progDialog.setMessage("Adding Schedule..");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.SCHEDULE_ADD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {

                                    Schedule schedule = new Schedule();
                                    schedule.setSched_id(jsonObject.getInt(Schedule.SCHEDULE_ID));
                                    schedule.setSched_day(spScheduleDay.getSelectedItem().toString());
                                    schedule.setSched_hour(Integer.parseInt(spScheduleTimeHour.getSelectedItem().toString()));
                                    schedule.setSched_minute(Integer.parseInt(spScheduleTimeMinute.getSelectedItem().toString()));
                                    schedule.setSched_meridiem(spScheduleTimeMeridiem.getSelectedItem().toString());
                                    schedule.setSched_status("Available");
                                    schedule.setAcc_id(Account.loggedAccount.getAcc_id());

                                    String schedDay = schedule.getSched_day();
                                    if (schedule.getSched_day().equals("Monday")) {
                                        Schedule.schedules_monday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_monday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_monday)));
                                    } else if (schedDay.equals("Tuesday")) {
                                        Schedule.schedules_tuesday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_tuesday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_tuesday)));
                                    } else if (schedDay.equals("Wednesday")) {
                                        Schedule.schedules_wednesday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_wednesday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_wednesday)));
                                    } else if (schedDay.equals("Thursday")) {
                                        Schedule.schedules_thursday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_thursday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_thursday)));
                                    } else if (schedDay.equals("Friday")) {
                                        Schedule.schedules_friday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_friday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_friday)));
                                    } else if (schedDay.equals("Saturday")) {
                                        Schedule.schedules_saturday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_saturday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_saturday)));
                                    } else if (schedDay.equals("Sunday")) {
                                        Schedule.schedules_sunday.add(schedule);
                                        tvScheduleMonAvailableCount.setText(String.valueOf(Schedule.getAvailableCount(Schedule.schedules_sunday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_sunday)));
                                        tvScheduleMonReservedCount.setText(String.valueOf(Schedule.getReservedCount(Schedule.schedules_sunday)));
                                    }

                                    Toast.makeText(TutorScheduleManagementActivity.this, "Schedule Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TutorScheduleManagementActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                }

                                progDialog.dismiss();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Toast.makeText(TutorScheduleManagementActivity.this, "Schedule not added", Toast.LENGTH_SHORT).show();
                                progDialog.dismiss();
                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(TutorScheduleManagementActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            progDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Schedule.SCHEDULE_DAY, spScheduleDay.getSelectedItem().toString());
                    params.put(Schedule.SCHEDULE_HOUR, spScheduleTimeHour.getSelectedItem().toString());
                    params.put(Schedule.SCHEDULE_MINUTE, spScheduleTimeMinute.getSelectedItem().toString());
                    params.put(Schedule.SCHEUDLE_MERIDIEM, spScheduleTimeMeridiem.getSelectedItem().toString());
                    params.put(Schedule.SCHEDULE_ACC_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cvScheduleMondayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Monday");
        startActivity(intent);
    }

    public void cvScheduleTuesdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Tuesday");
        startActivity(intent);
    }

    public void cvScheduleWednesdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Wednesday");
        startActivity(intent);
    }

    public void cvScheduleThursdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Thursday");
        startActivity(intent);
    }

    public void cvScheduleFridayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Friday");
        startActivity(intent);
    }

    public void cvScheduleSaturdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Saturday");
        startActivity(intent);
    }

    public void cvScheduleSundayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewActivity.class);
        intent.putExtra("day", "Sunday");
        startActivity(intent);
    }

}
