package com.dce.grabtutor.Model;

/**
 * Created by Skye on 4/25/2017.
 */

public class Account {

    public static Account loggedAccount;

    public static final String ACCOUNT_ID = "acc_id";
    public static final String ACCOUNT_USERNAME = "acc_user";
    public static final String ACCOUNT_PASSWORD = "acc_pass";

    public static final String ACCOUNT_FIRST_NAME = "acc_fname";
    public static final String ACCOUNT_MIDDLE_NAME = "acc_mname";
    public static final String ACCOUNT_LAST_NAME = "acc_lname";

    public static final String ACCOUNT_EMAIL = "acc_email";
    public static final String ACCOUNT_GENDER = "acc_gender";
    public static final String ACCOUNT_TYPE = "acc_type";
    public static final String ACCOUNT_TOKEN = "acc_token";

    public static final String ACCOUNT_LONGITUDE = "acc_longitude";
    public static final String ACCOUNT_LATITUDE = "acc_latitude";

    private int acc_id;
    private String acc_user;
    private String acc_pass;

    private String acc_fname;
    private String acc_mname;
    private String acc_lname;

    private String acc_email;
    private String acc_gender;
    private String acc_type;
    private String acc_token;

    private double acc_longitude;
    private double acc_latitde;

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public String getAcc_user() {
        return acc_user;
    }

    public void setAcc_user(String acc_user) {
        this.acc_user = acc_user;
    }

    public String getAcc_pass() {
        return acc_pass;
    }

    public void setAcc_pass(String acc_pass) {
        this.acc_pass = acc_pass;
    }

    public String getAcc_fname() {
        return acc_fname;
    }

    public void setAcc_fname(String acc_fname) {
        this.acc_fname = acc_fname;
    }

    public String getAcc_mname() {
        return acc_mname;
    }

    public void setAcc_mname(String acc_mname) {
        this.acc_mname = acc_mname;
    }

    public String getAcc_lname() {
        return acc_lname;
    }

    public void setAcc_lname(String acc_lname) {
        this.acc_lname = acc_lname;
    }

    public String getAcc_email() {
        return acc_email;
    }

    public void setAcc_email(String acc_email) {
        this.acc_email = acc_email;
    }

    public String getAcc_gender() {
        return acc_gender;
    }

    public void setAcc_gender(String acc_gender) {
        this.acc_gender = acc_gender;
    }

    public String getAcc_type() {
        return acc_type;
    }

    public void setAcc_type(String acc_type) {
        this.acc_type = acc_type;
    }

    public String getAcc_token() {
        return acc_token;
    }

    public void setAcc_token(String acc_token) {
        this.acc_token = acc_token;
    }

    public double getAcc_longitude() {
        return acc_longitude;
    }

    public void setAcc_longitude(double acc_longitude) {
        this.acc_longitude = acc_longitude;
    }

    public double getAcc_latitde() {
        return acc_latitde;
    }

    public void setAcc_latitde(double acc_latitde) {
        this.acc_latitde = acc_latitde;
    }

}
