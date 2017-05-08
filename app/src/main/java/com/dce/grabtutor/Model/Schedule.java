package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 4/29/2017.
 */

public class Schedule {

    public static final String SCHEDULE_ID = "sched_id";
    public static final String SCHEDULE_DAY = "sched_day";
    public static final String SCHEDULE_HOUR = "sched_hour";
    public static final String SCHEDULE_MINUTE = "sched_minute";
    public static final String SCHEUDLE_MERIDIEM = "sched_meridiem";
    public static final String SCHEDULE_STATUS = "sched_status";
    public static final String SCHEDULE_ACC_ID = "acc_id";
    public static final String SCHEDULE_STUD_ID = "stud_id";
    public static final String SCHEDULE_TUTOR_ID = "tutor_id";

    public static ArrayList<Schedule> schedules_monday;
    public static ArrayList<Schedule> schedules_tuesday;
    public static ArrayList<Schedule> schedules_wednesday;
    public static ArrayList<Schedule> schedules_thursday;
    public static ArrayList<Schedule> schedules_friday;
    public static ArrayList<Schedule> schedules_saturday;
    public static ArrayList<Schedule> schedules_sunday;

    private int sched_id;
    private String sched_day;
    private int sched_hour;
    private int sched_minute;
    private String sched_meridiem;
    private String sched_status;
    private int acc_id;

    private Subject subject;
    private Account student;

    public static int getReservedCount(ArrayList<Schedule> schedules) {
        int count = 0;
        try {
            for (Schedule schedule : schedules) {
                if (schedule.getSched_status().equals("Reserved")) {
                    count++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return count;
    }

    public static int getAvailableCount(ArrayList<Schedule> schedules) {
        int count = 0;
        try {
            for (Schedule schedule : schedules) {
                if (schedule.getSched_status().equals("Available")) {
                    count++;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return count;
    }

    public int getSched_id() {
        return sched_id;
    }

    public void setSched_id(int sched_id) {
        this.sched_id = sched_id;
    }

    public String getSched_day() {
        return sched_day;
    }

    public void setSched_day(String sched_day) {
        this.sched_day = sched_day;
    }

    public int getSched_hour() {
        return sched_hour;
    }

    public void setSched_hour(int sched_hour) {
        this.sched_hour = sched_hour;
    }

    public int getSched_minute() {
        return sched_minute;
    }

    public void setSched_minute(int sched_minute) {
        this.sched_minute = sched_minute;
    }

    public String getSched_meridiem() {
        return sched_meridiem;
    }

    public void setSched_meridiem(String sched_meridiem) {
        this.sched_meridiem = sched_meridiem;
    }

    public String getSched_status() {
        return sched_status;
    }

    public void setSched_status(String sched_status) {
        this.sched_status = sched_status;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Account getStudent() {
        return student;
    }

    public void setStudent(Account student) {
        this.student = student;
    }

}
