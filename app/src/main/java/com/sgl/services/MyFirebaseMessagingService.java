package com.sgl.services;

import android.content.Context;
import android.util.Log;

import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.NotificationCard;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Piyush on 25-02-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService

{

    private static final String TAG = "FirebaseIDService";
    public String refreshedToken = "";
    private Context mContext;

    /*public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        mContext = getApplication();
        AppPreferences.getInstance(mContext).putString(AppConstants.FCM_KEY, token);
        // TODO: Implement this method to send token to your app server.
    }*/


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCard notificationCard = new NotificationCard();
        notificationCard.message = remoteMessage.getNotification().getBody();
        notificationCard.title = remoteMessage.getNotification().getTitle();
        notificationCard.date = CommonUtils.getCurrentDate();
        notificationCard.is_read = "false";
        notificationCard.meter_reader_id = AppPreferences.getInstance(this).getString(AppConstants.METER_READER_ID, "");
        DatabaseManager.saveNotification(this, notificationCard);

        Log.d("FCM Keyyyyyy", "" + remoteMessage.getNotification().getBody());
    }


}
