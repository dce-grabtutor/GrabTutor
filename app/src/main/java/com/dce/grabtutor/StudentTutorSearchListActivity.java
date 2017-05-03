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
import android.widget.TextView;

import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Schedule;
import com.dce.grabtutor.Model.SearchedTutor;

public class StudentTutorSearchListActivity extends AppCompatActivity {

    RecyclerView rv;
    TutorListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tutor_search_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select a Tutor");

        rv = (RecyclerView) findViewById(R.id.rvStudentTutorSearchList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new TutorListAdapter(this);
        rv.setAdapter(adapter);
    }

    public class TutorListAdapter extends RecyclerView.Adapter<TutorListAdapter.ViewHolder> {

        Context context;

        public TutorListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_student_tutor_search_list, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SearchedTutor searchedTutor = SearchedTutor.searchedTutors.get(position);
            Account tutorAccount = searchedTutor.getAccount();
            Schedule tutorSchedule = searchedTutor.getSchedule();

            holder.tvTutorName.setText(tutorAccount.getFullName());
            holder.tvTutorUsername.setText(tutorAccount.getAcc_user());

            int startHour = tutorSchedule.getSched_hour();
            String startMeridiem = tutorSchedule.getSched_meridiem();

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

            holder.tvTutorScheduleTime.setText(String.format("%02d", startHour) + ":" + String.format("%02d", tutorSchedule.getSched_minute()) + " " + startMeridiem + " - " + String.format("%02d", endHour) + ":" + String.format("%02d", tutorSchedule.getSched_minute()) + " " + endMeridiem);
            holder.tvTutorScheduleDay.setText(tutorSchedule.getSched_day());
        }

        @Override
        public int getItemCount() {
            return SearchedTutor.searchedTutors.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cvStudentTutorList;

            TextView tvTutorName;
            TextView tvTutorUsername;
            TextView tvTutorScheduleTime;
            TextView tvTutorScheduleDay;

            public ViewHolder(View itemView) {
                super(itemView);

                tvTutorName = (TextView) itemView.findViewById(R.id.tvTutorName);
                tvTutorUsername = (TextView) itemView.findViewById(R.id.tvTutorUsername);
                tvTutorScheduleTime = (TextView) itemView.findViewById(R.id.tvTutorScheduleTime);
                tvTutorScheduleDay = (TextView) itemView.findViewById(R.id.tvTutorScheduleDay);

                cvStudentTutorList = (CardView) itemView.findViewById(R.id.cvStudentTutorSearchList);
            }
        }

    }

}
