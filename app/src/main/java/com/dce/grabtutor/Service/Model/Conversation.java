package com.dce.grabtutor.Service.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 4/26/2017.
 */

public class Conversation {
    public static ArrayList<Conversation> conversations;

    public static final String CONVERSATION_ID = "conv_id";
    public static final String CONVERSATION_ACC_ID_STUDENT = "student_id";
    public static final String CONVERSATION_ACC_ID_TUTOR = "tutor_id";

    private int conv_id;
    private Account conv_account;

    public int getConv_id() {
        return conv_id;
    }

    public void setConv_id(int conv_id) {
        this.conv_id = conv_id;
    }

    public Account getConv_account() {
        return conv_account;
    }

    public void setConv_account(Account conv_account) {
        this.conv_account = conv_account;
    }
}
