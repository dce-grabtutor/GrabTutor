package com.dce.grabtutor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Schedule;
import com.dce.grabtutor.Model.URI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduleDayViewActivity extends AppCompatActivity {

    RecyclerView rvAvailable;
    RecyclerView rvReserved;
//    View vRvScheduleDayDivider;

    ScheduleAvailableAdapter adapterAvailable;
    ScheduleReservedAdapter adapterReserved;

    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_day_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        day = intent.getStringExtra("day");
        System.out.println(day);

        setTitle("Schedule (" + day + ")");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvAvailable = (RecyclerView) findViewById(R.id.rvScheduleDayAvailable);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvAvailable.setLayoutManager(llm);

        rvReserved = (RecyclerView) findViewById(R.id.rvScheduleDayReserved);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        rvReserved.setLayoutManager(llm2);

//        vRvScheduleDayDivider = (View) findViewById(R.id.vRvScheduleDayDivider);
        loadViews();
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

    public void loadViews() {
        ArrayList<Schedule> schedules = new ArrayList<>();
        if (day.equals("Monday")) {
            schedules = Schedule.schedules_monday;
            int reservedCount = Schedule.getReservedCount(schedules);

            if (reservedCount < 1) {
                (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                vRvScheduleDayDivider.setVisibility(View.GONE);
            }
        } else {
            if (day.equals("Tuesday")) {
                schedules = Schedule.schedules_tuesday;
                int reservedCount = Schedule.getReservedCount(schedules);

                if (reservedCount < 1) {
                    (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                    vRvScheduleDayDivider.setVisibility(View.GONE);
                }
            } else if (day.equals("Wednesday")) {
                schedules = Schedule.schedules_wednesday;
                int reservedCount = Schedule.getReservedCount(schedules);

                if (reservedCount < 1) {
                    (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                    vRvScheduleDayDivider.setVisibility(View.GONE);
                }
            } else if (day.equals("Thursday")) {
                schedules = Schedule.schedules_thursday;
                int reservedCount = Schedule.getReservedCount(schedules);

                if (reservedCount < 1) {
                    (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                    vRvScheduleDayDivider.setVisibility(View.GONE);
                }
            } else if (day.equals("Friday")) {
                schedules = Schedule.schedules_friday;
                int reservedCount = Schedule.getReservedCount(schedules);

                if (reservedCount < 1) {
                    (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                    vRvScheduleDayDivider.setVisibility(View.GONE);
                }
            } else if (day.equals("Saturday")) {
                schedules = Schedule.schedules_saturday;
                int reservedCount = Schedule.getReservedCount(schedules);

                if (reservedCount < 1) {
                    (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                    vRvScheduleDayDivider.setVisibility(View.GONE);
                }
            } else if (day.equals("Sunday")) {
                schedules = Schedule.schedules_sunday;
                int reservedCount = Schedule.getReservedCount(schedules);

                if (reservedCount < 1) {
                    (rvAvailable.getLayoutParams()).height = RecyclerView.LayoutParams.MATCH_PARENT;
//                    vRvScheduleDayDivider.setVisibility(View.GONE);
                }
            }
        }

        adapterAvailable = new ScheduleAvailableAdapter(this, schedules, day);
        adapterReserved = new ScheduleReservedAdapter(this, schedules, day);

        rvAvailable.setAdapter(adapterAvailable);
        rvReserved.setAdapter(adapterReserved);
    }

    public class ScheduleAvailableAdapter extends RecyclerView.Adapter<ScheduleAvailableAdapter.ViewHolder> {

        String day;
        ArrayList<Schedule> schedules;
        Context context;

        public ScheduleAvailableAdapter(Context context, ArrayList<Schedule> schedules, String day) {
            this.context = context;
            this.schedules = schedules;
            this.day = day;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_schedule_day_available, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Schedule schedule = this.schedules.get(position);
            String hourStart = String.format("%02d", schedule.getSched_hour());
            String hourEnd = String.format("%02d", schedule.getSched_hour() + 1);
            String minute = String.format("%02d", schedule.getSched_minute());
            String meridiem = schedule.getSched_meridiem();
            String meridiem_end = "";

            if (schedule.getSched_hour() == 11 && schedule.getSched_meridiem().equals("AM")) {
                meridiem_end = "PM";
            } else {
                meridiem_end = meridiem;
            }

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.SCHEDULE_REMOVE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.getBoolean("success")) {
                                            if (day.equals("Monday")) {
                                                Schedule.schedules_monday.remove(schedule);
                                            } else if (day.equals("Tuesday")) {
                                                Schedule.schedules_tuesday.remove(schedule);
                                            } else if (day.equals("Wednesday")) {
                                                Schedule.schedules_wednesday.remove(schedule);
                                            } else if (day.equals("Thursday")) {
                                                Schedule.schedules_thursday.remove(schedule);
                                            } else if (day.equals("Friday")) {
                                                Schedule.schedules_friday.remove(schedule);
                                            } else if (day.equals("Saturday")) {
                                                Schedule.schedules_saturday.remove(schedule);
                                            } else if (day.equals("Sunday")) {
                                                Schedule.schedules_sunday.remove(schedule);
                                            }

                                            adapterAvailable.notifyDataSetChanged();
                                            Toast.makeText(context, "Schedule Successfully Removed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to Remove Schedule", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        Toast.makeText(context, "Schedule not Removed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(Schedule.SCHEDULE_ID, String.valueOf(schedule.getSched_id()));
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(stringRequest);
                }
            });

            holder.tvScheduleStatus.setText(schedule.getSched_status().toString());
            holder.tvScheduleTime.setText(hourStart + ":" + minute + " " + meridiem + " - " + hourEnd + ":" + minute + " " + meridiem_end);

            if (schedule.getSched_status().equals("Reserved")) {
                holder.cv.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return this.schedules.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            Button btnRemove;

            TextView tvScheduleStatus;
            TextView tvScheduleTime;

            public ViewHolder(View itemView) {
                super(itemView);

                tvScheduleStatus = (TextView) itemView.findViewById(R.id.tvScheduleStatus);
                tvScheduleTime = (TextView) itemView.findViewById(R.id.tvScheduleTime);

                btnRemove = (Button) itemView.findViewById(R.id.btnRemoveSchedule);
                cv = (CardView) itemView.findViewById(R.id.cvScheduleDay);
            }
        }
    }

    public class ScheduleReservedAdapter extends RecyclerView.Adapter<ScheduleReservedAdapter.ViewHolder> {

        String day;
        ArrayList<Schedule> schedules;
        Context context;

        public ScheduleReservedAdapter(Context context, ArrayList<Schedule> schedules, String day) {
            this.context = context;
            this.schedules = schedules;
            this.day = day;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_schedule_day_reserved, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Schedule schedule = this.schedules.get(position);
            String hourStart = String.format("%02d", schedule.getSched_hour());
            String hourEnd = String.format("%02d", schedule.getSched_hour() + 1);
            String minute = String.format("%02d", schedule.getSched_minute());
            String meridiem = schedule.getSched_meridiem();
            String meridiem_end = "";

            if (schedule.getSched_hour() == 11 && schedule.getSched_meridiem().equals("AM")) {
                meridiem_end = "PM";
            } else {
                meridiem_end = meridiem;
            }

            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.SCHEDULE_REMOVE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.getBoolean("success")) {
                                            if (day.equals("Monday")) {
                                                Schedule.schedules_monday.remove(schedule);
                                            } else if (day.equals("Tuesday")) {
                                                Schedule.schedules_tuesday.remove(schedule);
                                            } else if (day.equals("Wednesday")) {
                                                Schedule.schedules_wednesday.remove(schedule);
                                            } else if (day.equals("Thursday")) {
                                                Schedule.schedules_thursday.remove(schedule);
                                            } else if (day.equals("Friday")) {
                                                Schedule.schedules_friday.remove(schedule);
                                            } else if (day.equals("Saturday")) {
                                                Schedule.schedules_saturday.remove(schedule);
                                            } else if (day.equals("Sunday")) {
                                                Schedule.schedules_sunday.remove(schedule);
                                            }

                                            if (Schedule.getReservedCount(schedules) < 1) {
                                                loadViews();
                                            } else {
                                                adapterReserved.notifyDataSetChanged();
                                            }

                                            Toast.makeText(context, "Schedule Successfully Removed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to Remove Schedule", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        Toast.makeText(context, "Schedule not Removed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(Schedule.SCHEDULE_ID, String.valueOf(schedule.getSched_id()));
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(stringRequest);
                }
            });

            holder.tvScheduleStatus.setText(schedule.getSched_status().toString());
            holder.tvScheduleTime.setText(hourStart + ":" + minute + " " + meridiem + " - " + hourEnd + ":" + minute + " " + meridiem_end);

            if (schedule.getSched_status().equals("Available")) {
                holder.cvScheduleDay.setVisibility(View.GONE);
            } else {
                Account student = schedule.getStudent();
                String fullName = student.getAcc_lname() + ", " + student.getAcc_fname() + " " + student.getAcc_mname();

                holder.tvScheduleUsername.setText(student.getAcc_user());
                holder.tvScheduleFullName.setText(fullName);

                holder.tvScheduleSubject.setText(schedule.getSubject().getSubj_name());
            }
        }

        @Override
        public int getItemCount() {
            return this.schedules.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cvScheduleDay;

            Button btnRemove;

            TextView tvScheduleStatus;
            TextView tvScheduleTime;
            TextView tvScheduleSubject;

            TextView tvScheduleFullName;
            TextView tvScheduleUsername;

            public ViewHolder(View itemView) {
                super(itemView);

                tvScheduleStatus = (TextView) itemView.findViewById(R.id.tvScheduleStatus);
                tvScheduleTime = (TextView) itemView.findViewById(R.id.tvScheduleTime);
                tvScheduleSubject = (TextView) itemView.findViewById(R.id.tvScheduleSubject);
                tvScheduleFullName = (TextView) itemView.findViewById(R.id.tvScheduleUserFullName);
                tvScheduleUsername = (TextView) itemView.findViewById(R.id.tvScheduleUsername);

                cvScheduleDay = (CardView) itemView.findViewById(R.id.cvScheduleDay);

                btnRemove = (Button) itemView.findViewById(R.id.btnRemoveSchedule);
            }
        }
    }

}
