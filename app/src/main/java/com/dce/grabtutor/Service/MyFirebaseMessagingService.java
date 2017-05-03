package com.dce.grabtutor.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.dce.grabtutor.MessageActivity;
import com.dce.grabtutor.Model.Message;
import com.dce.grabtutor.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Skye on 4/26/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //If notification is message
        if (remoteMessage.getData().get("type").equals("message")) {
            try {
                if (Message.messages != null && MessageActivity.isActive == true) {
                    Message message = new Message();
                    message.setMsg_id(Integer.parseInt(remoteMessage.getData().get(Message.MESSAGE_ID)));
                    message.setMsg_body(remoteMessage.getData().get(Message.MESSAGE_BODY));
                    message.setMsg_datetime(remoteMessage.getData().get(Message.MESSAGE_DATETIME));
                    message.setAcc_id(Integer.parseInt(remoteMessage.getData().get(Message.MESSAGE_ACC_ID)));
                    message.setConv_id(Integer.parseInt(remoteMessage.getData().get(Message.MESSAGE_CONV_ID)));
                    Message.messages.add(message);
                } else {
                    sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                }

                Intent intent = new Intent("messages");
                broadcaster.sendBroadcast(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //Calling method to generate notification
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    //This method generates the notification
    private void sendNotification(String title, String messageBody) {
//        Intent intent = new Intent(this, LoginActivity.class);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}