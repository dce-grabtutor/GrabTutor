package com.dce.grabtutor.Service.Model;

/**
 * Created by Skye on 5/2/2017.
 */

public class SearchSetting {

    public static final String SEARCH_SETTING_ID = "ss_id";
    public static final String SEARCH_SETTING_DISTANCE_RANGE = "ss_drange";
    public static final String SEARCH_SETTING_ACC_ID = "acc_id";
    public static SearchSetting currentSearchSetting;
    private int ss_id;
    private int ss_drange;
    private int acc_id;

    public int getSs_id() {
        return ss_id;
    }

    public void setSs_id(int ss_id) {
        this.ss_id = ss_id;
    }

    public int getSs_drange() {
        return ss_drange;
    }

    public void setSs_drange(int ss_drange) {
        this.ss_drange = ss_drange;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

}
