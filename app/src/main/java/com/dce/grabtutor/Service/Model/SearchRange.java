package com.dce.grabtutor.Service.Model;

/**
 * Created by Skye on 5/2/2017.
 */

public class SearchRange {

    public static final String SEARCH_RANGE_ID = "sr_id";
    public static final String SEARCH_RANGE_DAY = "sr_day";
    public static final String SEARCH_RANGE_START_HOUR = "sr_start_hour";
    public static final String SEARCH_RANGE_END_HOUR = "sr_end_hour";
    public static final String SEARCH_RANGE_SS_ID = "ss_id";
    public static SearchRange currentSearchRange;
    private int sr_id;
    private String sr_day;
    private int sr_start_hour;
    private int sr_end_hour;
    private int ss_id;

    public int getSr_id() {
        return sr_id;
    }

    public void setSr_id(int sr_id) {
        this.sr_id = sr_id;
    }

    public String getSr_day() {
        return sr_day;
    }

    public void setSr_day(String sr_day) {
        this.sr_day = sr_day;
    }

    public int getSr_start_hour() {
        return sr_start_hour;
    }

    public void setSr_start_hour(int sr_start_hour) {
        this.sr_start_hour = sr_start_hour;
    }

    public int getSr_end_hour() {
        return sr_end_hour;
    }

    public void setSr_end_hour(int sr_end_hour) {
        this.sr_end_hour = sr_end_hour;
    }

    public int getSs_id() {
        return ss_id;
    }

    public void setSs_id(int ss_id) {
        this.ss_id = ss_id;
    }

}
