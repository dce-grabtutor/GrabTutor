package com.dce.grabtutor.Handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dce.grabtutor.Model.DatabaseHandler;
import com.dce.grabtutor.Model.Message;

import java.util.ArrayList;

/**
 * Created by Skye on 4/27/2017.
 */

public class MessageHandler {
    Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    public void addMessage(Message message) {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler(context);
            SQLiteDatabase db = dbHandler.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(Message.MESSAGE_ID, message.getMsg_id());
            cv.put(Message.MESSAGE_BODY, message.getMsg_body());
            cv.put(Message.MESSAGE_DATETIME, message.getMsg_datetime());
            cv.put(Message.MESSAGE_ACC_ID, message.getAcc_id());
            cv.put(Message.MESSAGE_CONV_ID, message.getConv_id());

            db.insert(DatabaseHandler.TABLE_MESSAGES, null, cv);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("FUCKING ERROR HERE: ");
        }
    }

    public ArrayList<Message> getNotifiedMessages(int conv_id) {
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        SQLiteDatabase db = dbHandler.getWritableDatabase();

        ArrayList<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE conv_id = " + conv_id;

        ArrayList<String> removeIDs = new ArrayList<>();
        try {
            Cursor rs = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_MESSAGES, null);
            while (rs.moveToNext()) {
                Message message = new Message();
                message.setAcc_id(rs.getInt(rs.getColumnIndex(Message.MESSAGE_ID)));
                message.setMsg_body(rs.getString(rs.getColumnIndex(Message.MESSAGE_BODY)));
                message.setMsg_datetime(rs.getString(rs.getColumnIndex(Message.MESSAGE_DATETIME)));
                message.setAcc_id(rs.getInt(rs.getColumnIndex(Message.MESSAGE_ACC_ID)));
                message.setConv_id(rs.getInt(rs.getColumnIndex(Message.MESSAGE_CONV_ID)));

                messages.add(message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        db.delete(DatabaseHandler.TABLE_MESSAGES, Message.MESSAGE_ID, removeIDs.toArray(new String[removeIDs.size()]));
        return messages;
    }
}
