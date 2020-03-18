package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the JobCardTable of device database
 *
 * @author Bynry01
 */
public class JobCardTable
{

    public static final String TABLE_NAME = "JobCardTable";
    public static final String PATH = "JOB_CARD_TABLE";
    public static final int PATH_TOKEN = 30;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String BILL_CYCLE_CODE = "bill_cycle_code";
        public static final String SCHEDULE_MONTH = "schedule_month";
        public static final String SCHEDULE_END_DATE = "schedule_end_date";
        public static final String ROUTE_ID = "route_code";
        public static final String CONSUMER_NO = "consumer_no";
        public static final String CONSUMER_NAME = "consumer_name";
        public static final String PHONE_NO = "phone_no";
        public static final String ADDRESS = "address";
        public static final String METER_ID = "meter_no";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String PRV_METER_READING = "prv_meter_reading";
        public static final String PRV_LAT = "prv_lat";
        public static final String PRV_LONG = "prv_long";
        public static final String ASSIGNED_DATE = "assigned_date";
        public static final String JOB_CARD_STATUS = "job_card_status";
        public static final String JOB_CARD_ID = "job_card_id";
        public static final String IS_REVISIT = "is_revisit";
        public static final String PRV_SEQUENCE = "prv_sequence";
        public static final String ZONE_CODE = "zone_code";
        public static final String CATEGORY_ID = "category_id";
        public static final String AVG_CONSUMPTION = "avg_consumption";
        public static final String ACCOUNT_NO = "account_no";
        public static final String CURRENT_SEQUENCE = "current_sequence";
        public static final String LOCATION_GUIDANCE = "location_guidance";
        public static final String STREET = "street";
        public static final String BUILDING_NO = "building_no";
        public static final String DOOR_LOCK_READING_ATTEMPT = "door_lock_reading_attempt";
        public static final String PERMANENTLY_LOCK_READING_ATTEMPT = "permanently_locked_reading_attempt";
        public static final String SD_AMOUNT = "sd_amount";
        public static final String EXCLUDING_SD_AMOUNT = "excluding_sd_amount";
        public static final String TOTAL_AMOUNT = "total_amount";


        public static final String METER_DIGIT = "meter_digit";

        public static final String PRV_READING_DATE = "prv_reading_date";
        public static final String TOTAL_PRICE = "total_price";
        public static final String PREVIOUS_DUE = "previous_due";
        public static final String PAYMENTS = "payments";
        public static final String EMI_SECURITY_DEPOSIT = "emi_security_deposit";
        public static final String AVG_UNIT = "avg_unit";

        public static final String DUE_AMOUNT = "due_amount";

    }
}