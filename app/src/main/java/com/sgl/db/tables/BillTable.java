package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the Consumer Table of device database
 *
 * @author Bynry01
 */
public class BillTable
{

    public static final String TABLE_NAME = "BillTable";
    public static final String PATH = "Bill_TABLE";
    public static final int PATH_TOKEN = 22;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String JOB_CARD_ID = "jobcard_id";
        public static final String SUBDIVISION = "subdivision_name";
        public static final String CYCLE_CODE = "cycle_code";
        public static final String BINDER_CODE = "binder_code";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String CONSUMER_ASSIGNED = "consumer_assigned";
        public static final String JOBCARD_STATUS = "jobcard_status";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String REMARK = "remark";
        public static final String BILLMONTH = "billmonth";
        public static final String READING_DATE = "reading_date";
        public static final String DISTRIBUTED = "distributed";
        public static final String BILL_RECEIVED_COUNT = "bill_received_count";
    }
}