package com.k00140908.darren.the88days.ManageFriends.ManageGCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.k00140908.darren.the88days.MainActivity;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.UserLocalStore;
import com.k00140908.darren.the88days.db.MessengerDB;

/**
 * Created by darre on 06/04/2016.
 */
public class GCMListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String senderUserId = "";
        String message = "";
        String sentAt = "";
        String MessageType = "";
        String senderUserName = "";

        if (data != null) {
            senderUserId = data.getString("senderUserId");
            message = data.getString("msg");
            sentAt = data.getString("sentAt");
            senderUserName = data.getString("senderUserName");
            MessageType = data.getString("MessageType");
        }

        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "From: " + senderUserId);
        Log.d(TAG, "sentAt: " + sentAt);


        UserLocalStore userLocalStore;
        User user = null;
        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();


        if (MessageType != "") {
            if (MessageType.equals("1")) {

                MessengerDB messengerDB = new MessengerDB(getApplicationContext());
                if(messengerDB.checkIfExists(senderUserId))
                {
                    messengerDB.updateBackpacker(senderUserId,senderUserName);
                }
                else
                {
                    messengerDB.insertBackpacker(senderUserId,senderUserName);
                }

                long success = messengerDB.insertMessage(senderUserId,message,user.getUserId());




                SharedPreferences prefs = getApplicationContext().getSharedPreferences("userDetails", 0);


                String ChatWindowOpenUserId = prefs.getString("NewChatId", "");
                boolean MessageListOpen = prefs.getBoolean("MessageListOpen", false);

                if(ChatWindowOpenUserId.equals(senderUserId)) {
                    SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
                    userLocalDatabaseEditor.putBoolean("NewChatWindowsMessage", true);
                    userLocalDatabaseEditor.apply();
                }
                else if(MessageListOpen)
                {
                    SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
                    userLocalDatabaseEditor.putBoolean("NewListMessage", true);
                    userLocalDatabaseEditor.apply();
                }
                else
                {
                    sendMessageNotification(message, senderUserName);
                }


//                MainActivity main = new MainActivity();
//                //MainActivity.class.fragmentManager.findFragmentByTag("fragmentTag");
//
//                ChatFragment myFragment = (ChatFragment) main.getSupportFragmentManager().findFragmentByTag("ChatWindow");
//                if (myFragment != null && myFragment.isVisible()) {
//                    Log.d(TAG, "Visible");
//                }
//                else
//                {
//                    Log.d(TAG, "Not Visible");
//                }



//                Fragment currentFragment = .findFragmentById(R.id.content_frame);
//                if (currentFragment instanceof ChatFragment) {
//
//
//
//                sendMessageNotification(message, senderUserName);
            }
            else if (MessageType.equals("2")) {
                sendFriendRequestNotification(senderUserName);
            }else {
                acceptFriendNotification(senderUserName);
            }
         }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendMessageNotification(String message,String userName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Notifications","NewMessage");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.backpacker_icon)
                .setContentTitle("New Message From "+userName)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void sendFriendRequestNotification(String senderUserName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Notifications","NewFriendRequest");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.backpacker_icon)
                .setContentTitle("New Friend Request")
                .setContentText(senderUserName)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void acceptFriendNotification(String senderUserName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Notifications","AcceptedFriendRequest");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.backpacker_icon)
                .setContentTitle("Accepted friend request")
                .setContentText(senderUserName)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}