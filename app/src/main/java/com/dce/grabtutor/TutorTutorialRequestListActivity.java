package com.dce.grabtutor;

import android.content.Context;
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
import com.dce.grabtutor.Model.PendingBooking;
import com.dce.grabtutor.Model.Schedule;
import com.dce.grabtutor.Model.Subject;
import com.dce.grabtutor.Model.URI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorTutorialRequestListActivity extends AppCompatActivity {

    RecyclerView rv;
    TutorialRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_tutorial_request_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tutorial Requests");

        rv = (RecyclerView) findViewById(R.id.rvTutorialRequest);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        adapter = new TutorialRequestAdapter(this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
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

    public class TutorialRequestAdapter extends RecyclerView.Adapter<TutorialRequestAdapter.ViewHolder> {

        Context context;

        public TutorialRequestAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_tutorial_request, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final PendingBooking pendingBooking = PendingBooking.pendingBookings.get(position);
            Account account = pendingBooking.getAccount();
            final Schedule schedule = pendingBooking.getSchedule();
            Subject subject = pendingBooking.getSubject();

            holder.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_REQUEST_APPROVE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.getBoolean("success")) {
                                            ArrayList<PendingBooking> removeBookings = new ArrayList<PendingBooking>();

                                            for (PendingBooking targetBooking : PendingBooking.pendingBookings) {
                                                Schedule removeSchedule = targetBooking.getSchedule();
                                                if (schedule.getSched_id() == removeSchedule.getSched_id()) {
                                                    removeBookings.add(targetBooking);
                                                }
                                            }

                                            for (PendingBooking removeBooking : removeBookings) {
                                                PendingBooking.pendingBookings.remove(removeBooking);
                                            }

                                            Toast.makeText(context, "Tutorial Request Accepted", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
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
                                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(pendingBooking.PENDING_BOOKING_ID, String.valueOf(pendingBooking.getPb_id()));
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(TutorTutorialRequestListActivity.this);
                    requestQueue.add(stringRequest);
                }
            });

            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_REQUEST_DECLINE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println(response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.getBoolean("success")) {
                                            PendingBooking.pendingBookings.remove(pendingBooking);

                                            Toast.makeText(context, "Tutorial Request Declined", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
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
                                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(PendingBooking.PENDING_BOOKING_ID, String.valueOf(pendingBooking.getPb_id()));
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(TutorTutorialRequestListActivity.this);
                    requestQueue.add(stringRequest);
                }
            });

            holder.tvStudentFullName.setText(account.getFullName());
            holder.tvStudentUsername.setText(account.getAcc_user());
            holder.tvStudentGender.setText(account.getAcc_gender());
            holder.tvSubjectName.setText(subject.getSubj_name());

            int startHour = schedule.getSched_hour();
            String startMeridiem = schedule.getSched_meridiem();

            int endHour = 0;
            String endMeridiem = "";

            if (startHour == 11 && startMeridiem.equals("AM")) {
                endHour = 12;
                endMeridiem = "PM";
            }

            if (startHour == 12) {
                endHour = 1;
                endMeridiem = "PM";
            } else {
                endHour = startHour + 1;
                endMeridiem = startMeridiem;
            }

            holder.tvScheduleTime.setText(String.format("%02d", startHour) + ":" + String.format("%02d", schedule.getSched_minute()) + " " + startMeridiem + " - " + String.format("%02d", endHour) + ":" + String.format("%02d", schedule.getSched_minute()) + " " + endMeridiem);
            holder.tvScheduleDay.setText(schedule.getSched_day());
        }

        @Override
        public int getItemCount() {
            return PendingBooking.pendingBookings.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            CardView cv;
            TextView tvStudentFullName;
            TextView tvStudentUsername;
            TextView tvStudentGender;
            TextView tvSubjectName;
            TextView tvScheduleDay;
            TextView tvScheduleTime;

            Button btnApprove;
            Button btnDecline;

            public ViewHolder(View itemView) {
                super(itemView);

                cv = (CardView) itemView.findViewById(R.id.cv);
                tvStudentFullName = (TextView) itemView.findViewById(R.id.tvStudentFullName);
                tvStudentUsername = (TextView) itemView.findViewById(R.id.tvStudentUsername);
                tvStudentGender = (TextView) itemView.findViewById(R.id.tvStudentGender);
                tvSubjectName = (TextView) itemView.findViewById(R.id.tvSubjectName);
                tvScheduleDay = (TextView) itemView.findViewById(R.id.tvScheduleDay);
                tvScheduleTime = (TextView) itemView.findViewById(R.id.tvScheduleTime);
                btnApprove = (Button) itemView.findViewById(R.id.btnApprove);
                btnDecline = (Button) itemView.findViewById(R.id.btnDecline);
            }
        }
    }

}
