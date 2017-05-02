package com.dce.grabtutor.Service;

import android.util.Log;

import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Task.TokenUpdateTask;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Skye on 4/26/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
//        new TokenUpdateTask(MyFirebaseInstanceIDService.this, TokenUpdateTask.MODE_UPDATE, Account.loggedAccount.getAcc_id(), token);
//        System.out.println(token);
    }

}
