package com.dce.grabtutor.Task;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.URI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Skye on 4/26/2017.
 */

public class TokenUpdateTask {

    public static final String MODE_UPDATE = "token_update";
    public static final String MODE_REMOVE = "token_remove";

    public TokenUpdateTask(final Context context,  final String mode, final int acc_id, final String token) {
        String uri = null;
        if (mode.equals(MODE_UPDATE)) {
            uri = URI.TOKEN_UPDATE;
        } else if (mode.equals(MODE_REMOVE)) {
            uri = URI.TOKEN_REMOVE;
        }

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("Token Updated: " + response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
//                        Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_TOKEN, token);
                params.put(Account.ACCOUNT_ID, String.valueOf(acc_id));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
