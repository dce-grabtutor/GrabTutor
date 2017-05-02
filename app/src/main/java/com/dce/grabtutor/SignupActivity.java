package com.dce.grabtutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.dce.grabtutor.Service.Model.URI;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    TextView tvSignupUserType;

    EditText etSignupUsername;
    EditText etSignupPassword;
    EditText etSignupVerifyPassword;
    EditText etSignupFirstName;
    EditText etSignupMiddleName;
    EditText etSignupLastName;
    EditText etSignupEmail;
    Spinner spSignupGender;

    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();
        userType = intent.getStringExtra("UserType");

        tvSignupUserType = (TextView) findViewById(R.id.tvSignupUserType);
        tvSignupUserType.setText(userType);

        etSignupUsername = (EditText) findViewById(R.id.etSignupUsername);
        etSignupPassword = (EditText) findViewById(R.id.etSignupPassword);
        etSignupVerifyPassword = (EditText) findViewById(R.id.etSignupVerifyPassword);
        etSignupFirstName = (EditText) findViewById(R.id.etSignupFirstName);
        etSignupMiddleName = (EditText) findViewById(R.id.etSignupMiddleName);
        etSignupLastName = (EditText) findViewById(R.id.etSignupLastName);
        etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
        spSignupGender = (Spinner) findViewById(R.id.spSignupGender);
    }

    public void btnSignupClick(View view){
        try {
            String acc_user = etSignupUsername.getText().toString();
            String acc_pass = etSignupPassword.getText().toString();
            String acc_vpass = etSignupVerifyPassword.getText().toString();
            String acc_fname = etSignupFirstName.getText().toString();
            String acc_mname = etSignupMiddleName.getText().toString();
            String acc_lname = etSignupLastName.getText().toString();
            String acc_email = etSignupEmail.getText().toString();
            String acc_gender = spSignupGender.getSelectedItem().toString();

            if (acc_user.length() < 1) {
                Toast.makeText(this, "Username must not be empty", Toast.LENGTH_SHORT).show();
            } else if (acc_pass.length() < 1) {
                Toast.makeText(this, "Password must not be empty", Toast.LENGTH_SHORT).show();
            } else if (acc_vpass.length() < 1) {
                Toast.makeText(this, "Verify Password must not be empty", Toast.LENGTH_SHORT).show();
            } else if (acc_fname.length() < 1) {
                Toast.makeText(this, "First Name must not be empty", Toast.LENGTH_SHORT).show();
            } else if (acc_mname.length() < 1) {
                Toast.makeText(this, "Middle Name must not be empty", Toast.LENGTH_SHORT).show();
            } else if (acc_lname.length() < 1) {
                Toast.makeText(this, "Last Name must not be empty", Toast.LENGTH_SHORT).show();
            } else if (acc_email.length() < 1) {
                Toast.makeText(this, "Email must not be empty", Toast.LENGTH_SHORT).show();
            } else if (!acc_pass.equals(acc_vpass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                Account account = new Account();
                account.setAcc_user(acc_user);
                account.setAcc_pass(acc_pass);
                account.setAcc_fname(acc_fname);
                account.setAcc_mname(acc_mname);
                account.setAcc_lname(acc_lname);
                account.setAcc_email(acc_email);
                account.setAcc_gender(acc_gender);
                account.setAcc_type(userType);
//                account.setAcc_token(FirebaseInstanceId.getInstance().getToken());

                signup(account);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Please provide valid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    public void signup(final Account account) {
        final ProgressDialog progDialog = new ProgressDialog(this);
        progDialog.setMessage("Signing Up..");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                progDialog.dismiss();
                            } else {
                                String error = jsonObject.getString("error");
                                Toast.makeText(SignupActivity.this, error, Toast.LENGTH_SHORT).show();
                                progDialog.dismiss();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                            progDialog.dismiss();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(SignupActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_USERNAME, account.getAcc_user());
                params.put(Account.ACCOUNT_PASSWORD, account.getAcc_pass());
                params.put(Account.ACCOUNT_FIRST_NAME, account.getAcc_fname());
                params.put(Account.ACCOUNT_MIDDLE_NAME, account.getAcc_mname());
                params.put(Account.ACCOUNT_LAST_NAME, account.getAcc_lname());
                params.put(Account.ACCOUNT_EMAIL, account.getAcc_email());
                params.put(Account.ACCOUNT_GENDER, account.getAcc_gender());
                params.put(Account.ACCOUNT_TYPE, account.getAcc_type());
//                params.put(Account.ACCOUNT_TOKEN, account.getAcc_token());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
