package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 4/26/2017.
 */

public class Message {
    public static final String MESSAGE_ID = "msg_id";
    public static final String MESSAGE_BODY = "msg_body";
    public static final String MESSAGE_DATETIME = "msg_datetime";
    public static final String MESSAGE_ACC_ID = "acc_id";
    public static final String MESSAGE_CONV_ID = "conv_id";
    public static ArrayList<Message> messages;
    private int msg_id;
    private String msg_body;
    private String msg_datetime;
    private int acc_id;
    private int conv_id;

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public String getMsg_body() {
        return msg_body;
    }

    public void setMsg_body(String msg_body) {
        this.msg_body = msg_body;
    }

    public String getMsg_datetime() {
        return msg_datetime;
    }

    public void setMsg_datetime(String msg_datetime) {
        this.msg_datetime = msg_datetime;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public int getConv_id() {
        return conv_id;
    }

    public void setConv_id(int conv_id) {
        this.conv_id = conv_id;
    }
}
