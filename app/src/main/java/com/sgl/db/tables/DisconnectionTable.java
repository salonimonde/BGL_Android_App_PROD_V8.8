package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;

/**
 * Created by Piyush on 05-12-2017.
 * Bynry
 */
public class DisconnectionTable
{
    public static final String TABLE_NAME = "DisconnectionTable";
    public static final String PATH = "DISCONNECTION_TABLE";
    public static final int PATH_TOKEN = 90;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String BINDER_CODE = "binder_code";
        public static final String CONSUMER_NO = "consumer_no";
        public static final String NAME = "consumer_name";
        public static final String ADDRESS = "address";
        public static final String CONTACT_NO = "contact_none";
        public static final String LATITUDE= "latitude";
        public static final String LONGITUDE= "longitude";
        public static final String JOB_CARD_STATUS = "job_card_status";
        public static final String JOB_CARD_ID = "job_card_id";
        public static final String ZONE_CODE = "zone_code";
        public static final String BILL_MONTH = "bill_month";
        public static final String DUE_DATE = "due_date";
        public static final String NOTICE_DATE = "notice_date";
        public static final String DISCONNECTION_NOTICE_NO = "disconnection_notice_no";
        public static final String TOTOS = "totos";
    }
}
