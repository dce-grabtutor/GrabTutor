package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 5/7/2017.
 */

public class BookedSchedule {

    public static final String BOOKED_SCHEDULE_ID = "bsched_id";
    public static final String BOOKED_SCHEDULE_SCHEDULE_ID = "sched_id";
    public static final String BOOKED_SCHEDULE_SUBJECT_ID = "subj_id";
    public static final String BOOKED_SCHEDULE_STUDENT_ID = "student_id";
    public static final String BOOKED_SCHEDULE_TUTOR_ID = "tutor_id";
    public static ArrayList<BookedSchedule> bookedSchedules;
    private int bsched_id;
    private int sched_id;
    private int subj_id;
    private int student_id;
    private int tutor_id;

    public int getBsched_id() {
        return bsched_id;
    }

    public void setBsched_id(int bsched_id) {
        this.bsched_id = bsched_id;
    }

    public int getSched_id() {
        return sched_id;
    }

    public void setSched_id(int sched_id) {
        this.sched_id = sched_id;
    }

    public int getSubj_id() {
        return subj_id;
    }

    public void setSubj_id(int subj_id) {
        this.subj_id = subj_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public int getTutor_id() {
        return tutor_id;
    }

    public void setTutor_id(int tutor_id) {
        this.tutor_id = tutor_id;
    }

}
