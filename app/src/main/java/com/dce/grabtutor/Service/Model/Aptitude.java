package com.dce.grabtutor.Service.Model;

/**
 * Created by Skye on 5/1/2017.
 */

public class Aptitude {

    public static Aptitude currentAptitude;

    public static final String APTITUDE_ID = "apt_id";
    public static final String APTITUDE_ACC_ID = "acc_id";
    public static final String APTITUDE_START = "apt_start";
    public static final String APTITUDE_END = "apt_end";

    private int apt_id;
    private int acc_id;
    private String apt_start;
    private String apt_end;

    public int getApt_id() {
        return apt_id;
    }

    public void setApt_id(int apt_id) {
        this.apt_id = apt_id;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public String getApt_start() {
        return apt_start;
    }

    public void setApt_start(String apt_start) {
        this.apt_start = apt_start;
    }

    public String getApt_end() {
        return apt_end;
    }

    public void setApt_end(String apt_end) {
        this.apt_end = apt_end;
    }

}
