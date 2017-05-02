package com.dce.grabtutor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

public class StudentTutorSearchSettingsActivity extends AppCompatActivity {

    EditText etSearchRangeMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tutor_search_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Search Settings");

        etSearchRangeMax = (EditText) findViewById(R.id.etSearchRangeMax);
    }

}
