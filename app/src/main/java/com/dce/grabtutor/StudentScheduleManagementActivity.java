package com.dce.grabtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dce.grabtutor.Model.Schedule;

import java.util.ArrayList;

public class StudentScheduleManagementActivity extends AppCompatActivity {

//    TextView tvScheduleMonAvailableCount;
//    TextView tvScheduleTueAvailableCount;
//    TextView tvScheduleWedAvailableCount;
//    TextView tvScheduleThuAvailableCount;
//    TextView tvScheduleFriAvailableCount;
//    TextView tvScheduleSatAvailableCount;
//    TextView tvScheduleSunAvailableCount;

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
        setContentView(R.layout.activity_student_schedule_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("My Schedule");

//        tvScheduleMonAvailableCount = (TextView) findViewById(R.id.tvScheduleMonAvailableCount);
//        tvScheduleTueAvailableCount = (TextView) findViewById(R.id.tvScheduleTueAvailableCount);
//        tvScheduleWedAvailableCount = (TextView) findViewById(R.id.tvScheduleWedAvailableCount);
//        tvScheduleThuAvailableCount = (TextView) findViewById(R.id.tvScheduleThuAvailableCount);
//        tvScheduleFriAvailableCount = (TextView) findViewById(R.id.tvScheduleFriAvailableCount);
//        tvScheduleSatAvailableCount = (TextView) findViewById(R.id.tvScheduleSatAvailableCount);
//        tvScheduleSunAvailableCount = (TextView) findViewById(R.id.tvScheduleSunAvailableCount);

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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
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
//        int availableCount = 0;
        int reservedCount = 0;

        for (Schedule schedule : schedules) {
            String sched_status = schedule.getSched_status();
//            if (sched_status.equals("Available")) {
//                availableCount++;
//            } else
            if (sched_status.equals("Reserved")) {
                reservedCount++;
            }
        }

        if (schedule_day.equals("Monday")) {
//            tvScheduleMonAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleMonReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Tuesday")) {
//            tvScheduleTueAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleTueReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Wednesday")) {
//            tvScheduleWedAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleWedReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Thursday")) {
//            tvScheduleThuAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleThuReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Friday")) {
//            tvScheduleFriAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleFriReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Saturday")) {
//            tvScheduleSatAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleSatReservedCount.setText(String.valueOf(reservedCount));
        } else if (schedule_day.equals("Sunday")) {
//            tvScheduleSunAvailableCount.setText(String.valueOf(availableCount));
            tvScheduleSunReservedCount.setText(String.valueOf(reservedCount));
        }
    }

    public void cvScheduleMondayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Monday");
        startActivity(intent);
    }

    public void cvScheduleTuesdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Tuesday");
        startActivity(intent);
    }

    public void cvScheduleWednesdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Wednesday");
        startActivity(intent);
    }

    public void cvScheduleThursdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Thursday");
        startActivity(intent);
    }

    public void cvScheduleFridayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Friday");
        startActivity(intent);
    }

    public void cvScheduleSaturdayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Saturday");
        startActivity(intent);
    }

    public void cvScheduleSundayClick(View view) {
        Intent intent = new Intent(this, ScheduleDayViewStudentActivity.class);
        intent.putExtra("day", "Sunday");
        startActivity(intent);
    }

}
