package com.dce.grabtutor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Subject;
import com.dce.grabtutor.Model.URI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TutorSubjectManagementActivity extends AppCompatActivity {

    static final String SUBJECT_MATH = "Math";
    static final String SUBJECT_ENGLISH = "English";
    static final String SUBJECT_SCIENCE = "Science";

    static final String STATUS_ADD = "Add";
    static final String STATUS_REMOVE = "Remove";

    CheckBox cbMath;
    CheckBox cbScience;
    CheckBox cbEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_subject_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Subject Management");

        cbMath = (CheckBox) findViewById(R.id.cbMath);
        cbScience = (CheckBox) findViewById(R.id.cbScience);
        cbEnglish = (CheckBox) findViewById(R.id.cbEnglish);
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


    @Override
    public void onResume() {
        super.onResume();

        try {
            for (Subject subject : Subject.currentSubjects) {
                if (subject.getSubj_name().equals(SUBJECT_MATH)) {
                    cbMath.setChecked(true);
                } else if (subject.getSubj_name().equals(SUBJECT_ENGLISH)) {
                    cbEnglish.setChecked(true);
                } else if (subject.getSubj_name().equals(SUBJECT_SCIENCE)) {
                    cbScience.setChecked(true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cbSubjectClick(View view) {
        CheckBox cb = ((CheckBox) view);

        String status = "";
        if (cb.isChecked()) {
            status = STATUS_ADD;
        } else {
            status = STATUS_REMOVE;
        }

        String subject = "";
        if (cb.getText().equals(SUBJECT_MATH)) {
            subject = SUBJECT_MATH;
        } else if (cb.getText().equals(SUBJECT_ENGLISH)) {
            subject = SUBJECT_ENGLISH;
        } else if (cb.getText().equals(SUBJECT_SCIENCE)) {
            subject = SUBJECT_SCIENCE;
        }

        final String finalSubject = subject;
        final String finalStatus = status;

        final ProgressDialog progDialog = new ProgressDialog(this);
        progDialog.setMessage("Updating..");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.TUTOR_SUBJECT_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getBoolean("success")) {

                                String update = "";
                                if (finalStatus == STATUS_ADD) {
                                    update = "Added";
                                } else if (finalStatus == STATUS_REMOVE) {
                                    update = "Removed";
                                }

                                Toast.makeText(TutorSubjectManagementActivity.this, "Subject Successfully " + update, Toast.LENGTH_SHORT).show();
                            }

                            progDialog.dismiss();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            progDialog.dismiss();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(TutorSubjectManagementActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                        progDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                params.put(Subject.SUBJECT_NAME, finalSubject);
                params.put("status", finalStatus);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
