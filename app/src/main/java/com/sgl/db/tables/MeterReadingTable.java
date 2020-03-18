package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the MeterReadingTable of device database
 *
 * @author Bynry01
 */
public class MeterReadingTable
{

    public static final String TABLE_NAME = "MeterReadingTable";
    public static final String PATH = "METER_READING_TABLE";
    public static final int PATH_TOKEN = 40;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String METER_NO = "meter_no";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String JOB_CARD_ID = "job_card_id";
        public static final String CURRENT_METER_READING = "current_meter_reading";
        public static final String METER_STATUS = "meter_status";
        public static final String READER_STATUS = "reader_status";
        public static final String READING_IMAGE = "reading_image";
        public static final String READING_MONTH = "reading_month";
        public static final String READER_REMARK_COMMENT = "reader_remark_comment";
        public static final String IS_SUSPICIOUS = "isSuspicious";
        public static final String SUSPICIOUS_REMARKS = "suspicious_remark";
        public static final String SUSPICIOUS_READING_IMAGE = "suspicious_reading_image";
        public static final String CUR_LAT= "cur_lat";
        public static final String CUR_LNG = "cur_lng";
        public static final String IS_UPLOADED = "isUploaded";
        public static final String IS_REVISIT = "is_revisit";
        public static final String READING_DATE = "reading_date";
        public static final String PRV_SEQUENCE = "prv_sequence";
        public static final String NEW_SEQUENCE = "new_sequence";
        public static final String READING_TAKEN_BY = "reading_taken_by";
        public static final String LOCATION_GUIDANCE = "location_guidance";
        public static final String MOBILE_NO = "mobile_no";
        public static final String METER_TYPE = "meter_type";
        public static final String ZONE_CODE = "zone_code";

        public static final String DOOR_LOCK_READING_ATTEMPT = "door_lock_reading_attempt";
        public static final String PERMANENTLY_LOCK_READING_ATTEMPT = "permanently_locked_reading_attempt";
        public static final String CHEQUE_IMAGE = "cheque_image";
        public static final String CHEQUE_NUMBER = "cheque_number";
        public static final String CHEQUE_AMOUNT = "cheque_amount";


        public static final String TIME_TAKEN = "time_taken";
        public static final String IS_SPOT_BILL = "spot_billing";

        public static final String CURRENT_CHARGES  = "current_charges";
        public static final String CONSUMPTION_CHARGES  = "consumption_charges";
        public static final String AMOUNT_BEFORE_DUE = "before_due";
        public static final String AMOUNT_AFTER_DUE = "after_due";
        public static final String BILL_TYPE = "bill_type";
        public static final String DUE_DATE = "due_date";

        public static final String PREVIOUS_DUE = "previous_due";
        public static final String PAYMENTS = "payments";
        public static final String EMI_SECURITY_DEPOSIT = "emi_security_deposit";
        public static final String TOTAL_PRICE = "total_price";
        public static final String CONSUMP_DAYS = "consump_days";



        public static final String NAME_CHANGE = "name_change";
        public static final String ADD_CHANGE = "address_change";
        public static final String METER_CHANGE = "meter_change";
        public static final String MOB_CHANGE = "mobile_change";


        public static final String CHANGED_NAME = "changed_name";
        public static final String CHANGED_ADD = "changed_address";
        public static final String CHANGED_METER = "changed_meter";
        public static final String CHANGED_MOB = "changed_mobile";
    }
}