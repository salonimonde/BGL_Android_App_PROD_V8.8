package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the UserProfileTable of device database
 *
 * @author Bynry01
 */
public class UserProfileTable
{

    public static final String TABLE_NAME = "UserProfileTable";
    public static final String PATH = "USER_PROFILE_TABLE";
    public static final int PATH_TOKEN = 50;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    /**
     * This class contains Constants to describe name of Columns of UserLoginTable
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String METER_READER_NAME = "meter_reader_name";

//        public static final String ADDRESS = "address";
        public static final String CITY = "city";
//        public static final String STATE = "state";
        public static final String EMP_ID = "emp_id";
        public static final String EMAIL_ID = "email_id";
//        public static final String EMP_TYPE= "emp_type";
        public static final String IMAGE = "image";
        public static final String DEVICE_IMEI_ID = "device_imei_ID";
        public static final String CONTACT_NO = "contact_no";
//        public static final String FCM_TOKEN = "fcm_token";
        public static final String APP_VERSION= "app_version";
        public static final String CURRENT_DATE = "current_date";
        public static final String APP_LINK= "app_link";
    }
}