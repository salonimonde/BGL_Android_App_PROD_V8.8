package com.sgl.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.sgl.configuration.AppConstants;

/**
 * Created by Piyush on 06-03-2017.
 * Bynry
 */
public class AppPreferences
{
    private static SharedPreferences mAppSharedPrefs;
    private static AppPreferences mInstance;
    public static String value="1";

    public static AppPreferences getInstance(Context pContext)
    {
        if (mInstance == null)
            mInstance = new AppPreferences();


        mAppSharedPrefs = pContext.getSharedPreferences(
                AppConstants.SHARED_PREF, Activity.MODE_PRIVATE);

        return mInstance;
    }

    public void putString(String pKey, String pDefaultVal)
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.putString(pKey, pDefaultVal);
        prefsEditor.apply();
    }

    public void putInt(String pKey, int pDefaultVal)
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.putInt(pKey, pDefaultVal);
        prefsEditor.apply();

    }

    public void putFloat(String pKey, float pDefaultVal)
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.putFloat(pKey, pDefaultVal);
        prefsEditor.apply();

    }

    public void putBoolean(String pKey, boolean pDefaultVal)
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.putBoolean(pKey, pDefaultVal);
        prefsEditor.apply();
    }

    public void putLong(String pKey, long pDefaultVal)
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.putLong(pKey, pDefaultVal);
        prefsEditor.apply();
    }

    public void putDouble(String pKey, double pDefaultVal)
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.putLong(pKey, Double.doubleToRawLongBits(pDefaultVal));
        prefsEditor.apply();
    }

    public String getString(String key, String pDefaultVal)
    {
        return mAppSharedPrefs.getString(key, pDefaultVal);
    }

    public float getFloat(String pKey, float pDefaultVal)
    {
        return mAppSharedPrefs.getFloat(pKey, pDefaultVal);
    }

    public int getInt(String pKey, int pDefaultVal)
    {
        return mAppSharedPrefs.getInt(pKey, pDefaultVal);
    }

    public long getLong(String pKey, long pDefaultVal)
    {
        return mAppSharedPrefs.getLong(pKey, pDefaultVal);
    }

    public boolean getBoolean(String pKey, boolean pDefaultVal)
    {
        return mAppSharedPrefs.getBoolean(pKey, pDefaultVal);
    }

    public double getDouble(String pKey, double pDefaultVal)
    {
        return Double.longBitsToDouble(mAppSharedPrefs.getLong(pKey, Double.doubleToLongBits(pDefaultVal)));
    }

    public void deleteAllPreferences()
    {
        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }

    public void deletePreferences(String name)
    {

        SharedPreferences.Editor prefsEditor = mAppSharedPrefs.edit();
        prefsEditor.remove(name);
        prefsEditor.apply();
    }

    public static boolean shouldWeAskPermission(Context context,String permission)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE);
        return sharedPref.getBoolean(permission, true);
    }

    public static void saveValue(Context context, String key, Object value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof Integer)
        {
            editor.putInt(key, (int) value);
        }
        else if (value instanceof String)
        {
            editor.putString(key, (String) value);
        }
        else if (value instanceof Float)
        {
            editor.putFloat(key, (float) value);
        }
        else if (value instanceof Long)
        {
            editor.putLong(key, (long) value);
        }
        else if (value instanceof Boolean)
        {
            editor.putBoolean(key, (boolean) value);
        }
        editor.commit();
    }
}
