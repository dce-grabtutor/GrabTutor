package com.dce.grabtutor.Model;

import java.util.ArrayList;

/**
 * Created by Skye on 5/7/2017.
 */

public class PendingBooking {

    public static final String PENDING_BOOKING_ID = "pb_id";
    public static ArrayList<PendingBooking> pendingBookings;
    private int pb_id;
    private Schedule schedule;
    private Account account;

    public int getPb_id() {
        return pb_id;
    }

    public void setPb_id(int pb_id) {
        this.pb_id = pb_id;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
