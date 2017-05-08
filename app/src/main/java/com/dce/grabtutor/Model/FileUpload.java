package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 5/8/2017.
 */

public class FileUpload {

    public static final String FILE_UPLOAD_ID = "fu_id";
    public static final String FILE_UPLOAD_ACC_ID = "acc_id";
    public static final String FILE_UPLOAD_NAME = "fu_name";
    public static ArrayList<FileUpload> fileUploads;
    private int fu_id;
    private int acc_id;
    private String fu_name;

    public int getFu_id() {
        return fu_id;
    }

    public void setFu_id(int fu_id) {
        this.fu_id = fu_id;
    }

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public String getFu_name() {
        return fu_name;
    }

    public void setFu_name(String fu_name) {
        this.fu_name = fu_name;
    }

}
