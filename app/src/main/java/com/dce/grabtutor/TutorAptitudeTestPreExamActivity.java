package com.dce.grabtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TutorAptitudeTestPreExamActivity extends AppCompatActivity {

    boolean ongoing;

    TextView tvAptitudePreTestDetails;
    Button btnAptitudeTestStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_aptitude_test_pre_exam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Aptitude Test");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        ongoing = intent.getBooleanExtra("ongoing", false);

        tvAptitudePreTestDetails = (TextView) findViewById(R.id.tvAptitudePreTestDetails);
        btnAptitudeTestStart = (Button) findViewById(R.id.btnAptitudeTestStart);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ongoing) {
            tvAptitudePreTestDetails.setText("Ongoing Exam");
            btnAptitudeTestStart.setText("Resume Aptitude Test");
        } else {
            tvAptitudePreTestDetails.setText("The aptitude test has 30 items consisting of different subjects which can only be taken for a duration of an hour. Failure to submit the exam before the deadline will count only the items that you have answered. The passing score will be 70% of the total number of items.");
            btnAptitudeTestStart.setText("Start Aptitude Test");
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

    public void btnAptitudeTestStartClick(View view) {
        Intent intent = new Intent(TutorAptitudeTestPreExamActivity.this, TutorAptitudeTestExamActivity.class);
        startActivity(intent);
        this.finish();
    }

}
