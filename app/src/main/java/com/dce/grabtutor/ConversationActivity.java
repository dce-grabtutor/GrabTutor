package com.dce.grabtutor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Service.Model.Account;
import com.dce.grabtutor.Service.Model.Conversation;
import com.dce.grabtutor.Service.Model.URI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {

    RecyclerView rv;
    ConversationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Conversations");

        rv = (RecyclerView) findViewById(R.id.rvConversation);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    @Override
    public void onResume() {
        super.onResume();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.CONVERSATION_LOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {

                                Conversation.conversations = new ArrayList<>();

                                JSONArray jsonArray = jsonObject.getJSONArray("accounts");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonAccount = jsonArray.getJSONObject(i);

                                    Conversation conversation = new Conversation();
                                    Account account = new Account();

                                    conversation.setConv_id(jsonAccount.getInt(Conversation.CONVERSATION_ID));

                                    account.setAcc_id(jsonAccount.getInt(Account.ACCOUNT_ID));
                                    account.setAcc_user(jsonAccount.getString(Account.ACCOUNT_USERNAME));
                                    account.setAcc_fname(jsonAccount.getString(Account.ACCOUNT_FIRST_NAME));
                                    account.setAcc_mname(jsonAccount.getString(Account.ACCOUNT_MIDDLE_NAME));
                                    account.setAcc_lname(jsonAccount.getString(Account.ACCOUNT_LAST_NAME));

                                    conversation.setConv_account(account);
                                    Conversation.conversations.add(conversation);
                                }

                                adapter = new ConversationAdapter(ConversationActivity.this);
                                rv.setAdapter(adapter);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Account.ACCOUNT_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                params.put(Account.ACCOUNT_TYPE, Account.loggedAccount.getAcc_type());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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

    public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
        Context context;

        public ConversationAdapter(Context context) {
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;

            TextView tvRvConversationInitial;
            TextView tvRvConversationFullName;
            TextView tvRvConversationUsername;

            public ViewHolder(View itemView) {
                super(itemView);

                tvRvConversationInitial = (TextView) itemView.findViewById(R.id.tvRvConversationInitial);
                tvRvConversationFullName = (TextView) itemView.findViewById(R.id.tvRvConversationFullName);
                tvRvConversationUsername = (TextView) itemView.findViewById(R.id.tvRvConversationUsername);
                cv = (CardView) itemView.findViewById(R.id.cvRvConversation);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_conversation, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
                }
            });

            Conversation conversation = Conversation.conversations.get(position);

            String firstName = conversation.getConv_account().getAcc_fname();
            String middleName = conversation.getConv_account().getAcc_mname();
            String lastName = conversation.getConv_account().getAcc_lname();
            String fullName = lastName + ", " + firstName + " " + middleName;

            holder.tvRvConversationInitial.setText(conversation.getConv_account().getAcc_fname().substring(0, 1));
            holder.tvRvConversationFullName.setText(fullName);
            holder.tvRvConversationUsername.setText(conversation.getConv_account().getAcc_user());
        }

        @Override
        public int getItemCount() {
            return Conversation.conversations.size();
        }
    }

}
