package com.dce.grabtutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Handler.AccountHandler;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.URI;
import com.dce.grabtutor.Task.TokenUpdateTask;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_TYPE_STUDENT = "Student";
    public static final String USER_TYPE_TUTOR = "Tutor";

    public static boolean loggedOut = false;

    private String userType;

    EditText etLoginUsername;
    EditText etLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        userType = intent.getStringExtra("UserType");

        etLoginUsername = (EditText) findViewById(R.id.etLoginUsername);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loggedOut) {
            etLoginUsername.setText("");
            etLoginPassword.setText("");
            Snackbar.make(findViewById(R.id.layout_login), "Logged Out", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            loggedOut = false;
        }
    }

    public void btnLoginClick(View view) {
        try {
            if (etLoginUsername.getText().toString().length() < 1) {
                Toast.makeText(this, "Username must not be empty", Toast.LENGTH_SHORT).show();
            } else if (etLoginPassword.getText().toString().length() < 1) {
                Toast.makeText(this, "Password must not be empty", Toast.LENGTH_SHORT).show();
            } else {
                Account account = new Account();
                account.setAcc_user(etLoginUsername.getText().toString());
                account.setAcc_pass(etLoginPassword.getText().toString());

                login(account);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void login(final Account account) {
        final ProgressDialog progDialog = new ProgressDialog(this);
        progDialog.setMessage("Logging In..");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("account");
                                JSONObject jsonAccount = jsonArray.getJSONObject(0);

                                Account account = new Account();
                                account.setAcc_id(jsonAccount.getInt(Account.ACCOUNT_ID));
                                account.setAcc_user(jsonAccount.getString(Account.ACCOUNT_USERNAME));
                                account.setAcc_fname(jsonAccount.getString(Account.ACCOUNT_FIRST_NAME));
                                account.setAcc_mname(jsonAccount.getString(Account.ACCOUNT_MIDDLE_NAME));
                                account.setAcc_lname(jsonAccount.getString(Account.ACCOUNT_LAST_NAME));
                                account.setAcc_email(jsonAccount.getString(Account.ACCOUNT_EMAIL));
                                account.setAcc_gender(jsonAccount.getString(Account.ACCOUNT_GENDER));
                                account.setAcc_type(jsonAccount.getString(Account.ACCOUNT_TYPE));

                                String acc_token = FirebaseInstanceId.getInstance().getToken();
                                account.setAcc_token(acc_token);

                                Account.loggedAccount = account;

                                new TokenUpdateTask(LoginActivity.this, TokenUpdateTask.MODE_UPDATE, account.getAcc_id(), acc_token);
                                new AccountHandler(LoginActivity.this).setupLoggedAccount(account);
                                if (account.getAcc_type().equals(USER_TYPE_STUDENT)) {
                                    Intent intent = new Intent(LoginActivity.this, StudentMenuActivity.class);
                                    startActivity(intent);
                                } else if (account.getAcc_type().equals(USER_TYPE_TUTOR)) {
                                    Intent intent = new Intent(LoginActivity.this, TutorMenuActivity.class);
                                    startActivity(intent);
                                }
                            }

                            progDialog.dismiss();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            progDialog.dismiss();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                        progDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_USERNAME, account.getAcc_user());
                params.put(Account.ACCOUNT_PASSWORD, account.getAcc_pass());
                params.put(Account.ACCOUNT_TYPE, userType);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnSignupClick(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra("UserType", userType);
        startActivity(intent);
    }

}