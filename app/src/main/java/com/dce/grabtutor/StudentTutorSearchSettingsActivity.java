package com.dce.grabtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.SearchSetting;
import com.dce.grabtutor.Model.URI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentTutorSearchSettingsActivity extends AppCompatActivity {

    EditText etSearchRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tutor_search_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Search Settings");

        etSearchRange = (EditText) findViewById(R.id.etSearchRange);
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

    public void btnSearchScheduleManagementClick(View view) {
        Intent intent = new Intent(this, StudentTutorSearchScheduleManagementActivity.class);
        startActivity(intent);
    }

    public void btnSaveSearchRange(View view) {
        try {
            final int ss_drange = Integer.parseInt(etSearchRange.getText().toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.USER_SEARCH_DISTANCE_UPDATE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    SearchSetting searchSetting = new SearchSetting();
                                    searchSetting.setSs_id(jsonObject.getInt(SearchSetting.SEARCH_SETTING_ID));
                                    searchSetting.setSs_drange(ss_drange);
                                    searchSetting.setAcc_id(Account.loggedAccount.getAcc_id());

                                    SearchSetting.currentSearchSetting = new SearchSetting();
                                    SearchSetting.currentSearchSetting = searchSetting;

                                    Toast.makeText(StudentTutorSearchSettingsActivity.this, "Search Range Updated", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Toast.makeText(StudentTutorSearchSettingsActivity.this, "Failed to Update Search Range", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(StudentTutorSearchSettingsActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                    params.put(SearchSetting.SEARCH_SETTING_DISTANCE_RANGE, String.valueOf(ss_drange));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Please Provide a Valid Input", Toast.LENGTH_SHORT).show();
        }
    }

}
