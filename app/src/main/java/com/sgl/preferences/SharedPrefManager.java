
/*
' History Header:      Version         - Date        - Developer Name   - Work Description
' History       :        1.0           - Aug-2016    - Bynry01  - class describes all necessary info about the UserLogin Table Table
 */

/*
 ##############################################################################################
 #####                                                                                    #####
 #####     FILE              : UserLoginTable.Java 	       								  #####
 #####     CREATED BY        : Bynry01                                                    #####
 #####     CREATION DATE     : Aug-2016                                                   #####
 #####                                                                                    #####
 #####     MODIFIED  BY      : Bynry01                                                    #####
 #####     MODIFIED ON       :                                                   	      #####
 #####                                                                                    #####
 #####     CODE BRIEFING     : SharedPrefManager Class.         		 			   	  #####
 #####                         class describes all necessary info about UserLogin Table   #####
 #####                                                                                    #####
 #####     COMPANY           : Bynry.                                                    #####
 #####                                                                                    #####
 ##############################################################################################
 */
package com.sgl.preferences;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bynry01 on 6/21/2015.
 */
public class SharedPrefManager
{

    public static final String MY_PREFERENCES = "MyPrefs";
    public static  String AUTH_TOKEN = "auth_token";
    public static String DEVICE_FCM_TOKEN  = "DEVICE_FCM_TOKEN";

    public static String USER_NAME = "user_name";
    public static String USER_ID = "user_id";

    public static String FCM_PREF = "fcm_pref";
    public static String FCM_TOKEN = "fcm_token";

    public static String PASSWORD = "pasword";
    public static String USER_KEY = "user_key";
    public static String USER_LOGGED_IN_DATE = "user_logged_in_date";



    public static void saveUserCredentials(Context context, String userKey, String userName, String password,String userid)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_KEY, userKey).putString(USER_NAME, userName).putString(PASSWORD, password).putString(USER_ID,userid);
        editor.commit();
    }

    public static void saveValue(Context context, String key, Object value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof Integer)
        {
            editor.putInt(key, (int) value);
        } else if (value instanceof String)
        {
            editor.putString(key, (String) value);
        } else if (value instanceof Float)
        {
            editor.putFloat(key, (float) value);
        } else if (value instanceof Long)
        {
            editor.putLong(key, (long) value);
        } else if (value instanceof Boolean)
        {
            editor.putBoolean(key, (boolean) value);
        }
        editor.commit();
    }

    public static String getStringValue(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static int getIntValue(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, 0);
    }

    public static float getFloatValue(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getFloat(key, 0);
    }


    public static boolean getBooleanValue(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    /**
     * method to determine whether we have asked
     * for this permission before.. if we have, we do not want to ask again.
     * They either rejected us or later removed the permission.
     * @param permission
     * @return
     */
    public static boolean shouldWeAskPermission(Context context,String permission)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(permission, true);
    }

    public static long getLongValue(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPref.getLong(key, 0);
    }
}
