package com.dce.grabtutor.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Skye on 4/27/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "grabtutor.db";

    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_MESSAGES = "messages";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ACCOUNTS = "CREATE TABLE " +
                TABLE_ACCOUNTS + " (" +
                Account.ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Account.ACCOUNT_USERNAME + " TEXT, " +
                Account.ACCOUNT_PASSWORD + " TEXT, " +
                Account.ACCOUNT_FIRST_NAME + " TEXT, " +
                Account.ACCOUNT_MIDDLE_NAME + " TEXT, " +
                Account.ACCOUNT_LAST_NAME + " TEXT, " +
                Account.ACCOUNT_EMAIL + " TEXT, " +
                Account.ACCOUNT_GENDER + " TEXT, " +
                Account.ACCOUNT_TYPE + " TEXT, " +
                Account.ACCOUNT_TOKEN + " TEXT, " +
                Account.ACCOUNT_LATITUDE + " REAL, " +
                Account.ACCOUNT_LONGITUDE + " REAL " +
                ")";

        String CREATE_TABLE_MESSAGES =  "CREATE TABLE " +
                TABLE_MESSAGES + " (" +
                Message.MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Message.MESSAGE_BODY + " TEXT, " +
                Message.MESSAGE_DATETIME + " TEXT, " +
                Message.MESSAGE_ACC_ID + " INTEGER, " +
                Message.MESSAGE_CONV_ID + " INTEGER " +
                ")";

        db.execSQL(CREATE_TABLE_ACCOUNTS);
        db.execSQL(CREATE_TABLE_MESSAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }
}
