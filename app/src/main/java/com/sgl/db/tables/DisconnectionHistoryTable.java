package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;

/**
 * Created by Piyush on 08-12-2017.
 * Bynry
 */
public class DisconnectionHistoryTable
{
    public static final String TABLE_NAME = "DisconnectionHistoryTable";
    public static final String PATH = "DISCONNECTION_HISTORY_TABLE";
    public static final int PATH_TOKEN = 110;
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
        public static final String BILL_MONTH = "bill_month";
        public static final String JOB_CARD_ID = "job_card_id";
        public static final String DATE = "date";
        public static final String DELIVERY_STATUS = "delivery_status";
    }
}
