package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 5/3/2017.
 */

public class SearchedTutor {

    public static ArrayList<SearchedTutor> searchedTutors;

    private Account account;
    private Schedule schedule;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

}
