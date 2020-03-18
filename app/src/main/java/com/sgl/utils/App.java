package com.sgl.utils;

import android.Manifest;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.sgl.bluetooth.EvoluteBTConnection;

import java.util.ArrayList;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public class App extends MultiDexApplication
{

    public static final String TAG = App.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static App mInstance;
    public ArrayList<String> permissions;
    private static Typeface mRegularType, mBoldType;
    public static boolean welcome = false;
    public static boolean sms = false;
    public static String ReadingTakenBy;
    public static String ConsumerAddedBy;

    /**Bluetooth communication connection object*/
    public EvoluteBTConnection mBTcomm = null;
    public boolean connection = false;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        MultiDex.install(this);
        mInstance = this;
        permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        mRegularType = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Sansation_Regular_0.ttf");
        mBoldType = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Sansation_Bold_0.ttf");
    }

    public static synchronized App getInstance()
    {
        return mInstance;
    }

    public static Typeface getSansationRegularFont()
    {
        return mRegularType;
    }

    public static Typeface getSansationBoldFont()
    {
        return mBoldType;
    }

    public static boolean getStatus()
    {
        return sms;
    }
    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag)
    {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * Set up a Bluetooth connection
     * @param sMac Bluetooth hardware address
     * @return Boolean
     * */


    // TODO uncomment below lines for bluetooth
    /*public boolean createConn(String sMac){
        if (null == this.mBTcomm)
        {
            this.mBTcomm = new EvoluteBTConnection(sMac);
            if (this.mBTcomm.createConn()){
                connection = true;
                return true;
            }
            else{
                this.mBTcomm = null;
                connection = false;
                return false;
            }
        }
        else
            return true;
    }

    *//**
     * Close and release the connection
     * @return void
     * *//*
    public void closeConn(){
        if (null != this.mBTcomm){
            this.mBTcomm.closeConn();
            this.mBTcomm = null;
        }
    }*/
}
