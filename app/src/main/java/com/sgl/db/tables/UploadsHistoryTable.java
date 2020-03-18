package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the Consumer Table of device database
 *
 *
 */
public class UploadsHistoryTable
{

    public static final String TABLE_NAME = "UploadsHistoryTable";
    public static final String PATH = "UPLOAD_HISTORY_TABLE";
    public static final int PATH_TOKEN = 21;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String CONSUMER_ID = "consumer_no";
        public static final String ROUTE_ID = "route_code";
        public static final String BILL_CYCLE_CODE = "bill_cycle_code";
        public static final String MONTH = "month";
        public static final String READING_DATE = "reading_date";
        public static final String UPLOAD_STATUS = "upload_status";
        public static final String METER_READER_ID = "meter_reader_id";
    }
}