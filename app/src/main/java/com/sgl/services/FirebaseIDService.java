package com.sgl.services;

import android.content.Context;
import android.util.Log;

import com.sgl.configuration.AppConstants;
import com.sgl.utils.AppPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Piyush on 25-02-2017.
 *//*public void onNewToken() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }*/

public class FirebaseIDService extends FirebaseInstanceIdService
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

    }*/
    @Override
    public void onTokenRefresh()
    {
        mContext = getApplication();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + refreshedToken);
        AppPreferences.getInstance(mContext).putString(AppConstants.FCM_KEY, refreshedToken);

    }




}
