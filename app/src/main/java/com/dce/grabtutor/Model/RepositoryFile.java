package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 5/13/2017.
 */

public class RepositoryFile {
    public static final String REPOSITORY_FILE_FILE_NAME = "file_name";
    public static final String REPOSITORY_FILE_ACC_ID = "acc_id";
    public static ArrayList<RepositoryFile> repositoryFiles;
    private int acc_id;
    private String file_name;
    private String file_download_url;

    public int getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(int acc_id) {
        this.acc_id = acc_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_download_url() {
        return file_download_url;
    }

    public void setFile_download_url(String file_download_url) {
        this.file_download_url = file_download_url;
    }

}
