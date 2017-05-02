package com.dce.grabtutor.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dce.grabtutor.Service.Model.Account;
import com.dce.grabtutor.Service.Model.DatabaseHandler;

/**
 * Created by Skye on 4/27/2017.
 */

public class AccountHandler {
    Context context;

    public AccountHandler(Context context){
        this.context = context;
    }

    public void setupLoggedAccount(Account account) {
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ACCOUNTS, Account.ACCOUNT_ID, null);

        ContentValues cv = new ContentValues();
        cv.put(Account.ACCOUNT_ID, account.getAcc_id());
        cv.put(Account.ACCOUNT_USERNAME, account.getAcc_user());
//        cv.put(Account.ACCOUNT_PASSWORD, account.getAcc_pass());
        cv.put(Account.ACCOUNT_FIRST_NAME, account.getAcc_fname());
        cv.put(Account.ACCOUNT_MIDDLE_NAME, account.getAcc_mname());
        cv.put(Account.ACCOUNT_LAST_NAME, account.getAcc_lname());
        cv.put(Account.ACCOUNT_EMAIL, account.getAcc_email());
        cv.put(Account.ACCOUNT_GENDER, account.getAcc_gender());
        cv.put(Account.ACCOUNT_TYPE, account.getAcc_type());
        cv.put(Account.ACCOUNT_TOKEN, account.getAcc_token());

        db.insert(DatabaseHandler.TABLE_ACCOUNTS, null, cv);
        db.close();
    }

    public Account getLoggedAccount() {
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        Account account = new Account();

        Cursor rs = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_ACCOUNTS, null);
        rs.moveToFirst();
        do {
            account.setAcc_id(rs.getInt(rs.getColumnIndex(Account.ACCOUNT_ID)));
            account.setAcc_user(rs.getString(rs.getColumnIndex(Account.ACCOUNT_USERNAME)));
//            account.setAcc_pass(rs.getString(rs.getColumnIndex(Account.ACCOUNT_PASS)));
            account.setAcc_fname(rs.getString(rs.getColumnIndex(Account.ACCOUNT_FIRST_NAME)));
            account.setAcc_mname(rs.getString(rs.getColumnIndex(Account.ACCOUNT_MIDDLE_NAME)));
            account.setAcc_lname(rs.getString(rs.getColumnIndex(Account.ACCOUNT_LAST_NAME)));
            account.setAcc_email(rs.getString(rs.getColumnIndex(Account.ACCOUNT_EMAIL)));
            account.setAcc_gender(rs.getString(rs.getColumnIndex(Account.ACCOUNT_GENDER)));
            account.setAcc_type(rs.getString(rs.getColumnIndex(Account.ACCOUNT_TYPE)));
            account.setAcc_token(rs.getString(rs.getColumnIndex(Account.ACCOUNT_TOKEN)));
        } while (rs.moveToNext());

        db.close();
        return account;
    }

    public void removeLoggedAccount() {
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        db.delete(DatabaseHandler.TABLE_ACCOUNTS, null, null);
        db.close();
    }

}
