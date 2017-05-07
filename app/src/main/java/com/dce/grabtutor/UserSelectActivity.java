package com.dce.grabtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dce.grabtutor.Handler.AccountHandler;
import com.dce.grabtutor.Model.Account;

public class UserSelectActivity extends AppCompatActivity {

    int adminLoginCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);

        try {
            Account account = new AccountHandler(this).getLoggedAccount();
            if (account.getAcc_id() > 0) {
                if (account.getAcc_type().equals(LoginActivity.USER_TYPE_STUDENT)) {
                    Intent menuIntent = new Intent(this, StudentMenuActivity.class);
                    Account.loggedAccount = account;
                    startActivity(menuIntent);
                } else if (account.getAcc_type().equals(LoginActivity.USER_TYPE_TUTOR)) {
                    Intent menuIntent = new Intent(this, TutorMenuActivity.class);
                    Account.loggedAccount = account;
                    startActivity(menuIntent);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adminLoginCounter = 0;
    }

    @Override
    public void onBackPressed() {
        adminLoginCounter++;

        if (adminLoginCounter == 10) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("UserType", LoginActivity.USER_TYPE_ADMIN);
            startActivity(intent);
        }
    }


    public void btnStudentTypeClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("UserType", LoginActivity.USER_TYPE_STUDENT);
        startActivity(intent);
    }

    public void btnTutorTypeClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("UserType", LoginActivity.USER_TYPE_TUTOR);
        startActivity(intent);
    }
}
