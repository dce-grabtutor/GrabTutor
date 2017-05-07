package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 4/30/2017.
 */

public class Subject {
    public static final String SUBJECT_ID = "subj_id";
    public static final String SUBJECT_NAME = "subj_name";
    public static ArrayList<Subject> currentSubjects;
    private int subj_id;
    private String subj_name;

    public int getSubj_id() {
        return subj_id;
    }

    public void setSubj_id(int subj_id) {
        this.subj_id = subj_id;
    }

    public String getSubj_name() {
        return subj_name;
    }

    public void setSubj_name(String subj_name) {
        this.subj_name = subj_name;
    }
}
