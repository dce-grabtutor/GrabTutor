package com.dce.grabtutor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dce.grabtutor.Model.Account;
import com.dce.grabtutor.Model.Conversation;
import com.dce.grabtutor.Model.Message;
import com.dce.grabtutor.Model.URI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    public static boolean isActive = false;

    RecyclerView rv;
    MessageAdapter adapter;

    EditText etMessageBody;
    String lastMessage;

    int position;
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                reloadMessages();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        setTitle("Messages (" + Conversation.conversations.get(position).getConv_account().getAcc_user() + ")");

        etMessageBody = (EditText) findViewById(R.id.etMessageBody);

        rv = (RecyclerView) findViewById(R.id.rvMessage);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver((messageReceiver), new IntentFilter("messages"));

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.MESSAGE_LOAD,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean("success")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("messages");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonMessage = jsonArray.getJSONObject(i);

                                        Message message = new Message();
                                        message.setMsg_id(jsonMessage.getInt(Message.MESSAGE_ID));
                                        message.setMsg_body(jsonMessage.getString(Message.MESSAGE_BODY));
                                        message.setMsg_datetime(jsonMessage.getString(Message.MESSAGE_DATETIME));
                                        message.setAcc_id(jsonMessage.getInt(Message.MESSAGE_ACC_ID));
                                        message.setConv_id(jsonMessage.getInt(Message.MESSAGE_CONV_ID));

                                        Message.messages.add(message);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            reloadMessages();
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
                    params.put(Conversation.CONVERSATION_ID, String.valueOf(Conversation.conversations.get(position).getConv_id()));
                    return params;
                }
            };

            Message.messages = new ArrayList<Message>();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void reloadMessages() {
        try {
            adapter = new MessageAdapter(MessageActivity.this);
            rv.setAdapter(adapter);
            rv.scrollToPosition(Message.messages.size() - 1);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void btnSendClick(View view) {
        if (etMessageBody.getText().toString().trim().length() < 1) {
            // Do nothing if empty
        } else {
            try {
                lastMessage = etMessageBody.getText().toString();
                etMessageBody.setText("");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URI.MESSAGE_SEND,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println(response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success")) {
                                        Message message = new Message();
                                        message.setMsg_body(lastMessage);
                                        message.setMsg_datetime(jsonObject.getString(Message.MESSAGE_DATETIME));
                                        message.setConv_id(Conversation.conversations.get(position).getConv_id());
                                        message.setAcc_id(Account.loggedAccount.getAcc_id());

                                        Message.messages.add(message);
                                        reloadMessages();

                                        etMessageBody.setText("");
                                    } else {
                                        System.out.println("Message not Accepted");
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Toast.makeText(MessageActivity.this, "Failed to Send Message", Toast.LENGTH_SHORT).show();
                                    etMessageBody.setText(lastMessage);
                                }
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Toast.makeText(MessageActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                                etMessageBody.setText(lastMessage);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(Message.MESSAGE_BODY, lastMessage);
                        params.put(Message.MESSAGE_ACC_ID, String.valueOf(Account.loggedAccount.getAcc_id()));
                        params.put(Message.MESSAGE_CONV_ID, String.valueOf(Conversation.conversations.get(position).getConv_id()));
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        Context context;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_message, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message message = Message.messages.get(position);

            holder.tvRvMessageBody.setText(message.getMsg_body());
            holder.tvRvMessageDateTime.setText(message.getMsg_datetime());

            if (message.getAcc_id() == Account.loggedAccount.getAcc_id()) {
                holder.ll.setGravity(Gravity.RIGHT);
//                holder.tvRvMessageBody.setGravity(Gravity.RIGHT);
                holder.tvRvMessageDateTime.setGravity(Gravity.RIGHT);
            }
        }

        @Override
        public int getItemCount() {
            return Message.messages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;

            TextView tvRvMessageBody;
            TextView tvRvMessageDateTime;

            LinearLayout ll;

            public ViewHolder(View itemView) {
                super(itemView);

                tvRvMessageBody = (TextView) itemView.findViewById(R.id.tvRvMessageBody);
                tvRvMessageDateTime = (TextView) itemView.findViewById(R.id.tvRvMessageDateTime);
                cv = (CardView) itemView.findViewById(R.id.cvRvMessage);
                ll = (LinearLayout) itemView.findViewById(R.id.llRvMessage);
            }
        }
    }

}
