package com.sgl.db.tables;

import android.net.Uri;

import com.sgl.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the Consumer Table of device database
 *
 * @author Bynry01
 */
public class ConsumerTable
{

    public static final String TABLE_NAME = "ConsumerTable";
    public static final String PATH = "CONSUMER_TABLE";
    public static final int PATH_TOKEN = 20;
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
        public static final String CONSUMER_NAME = "consumer_name";
        public static final String PHONE_NO = "contact_no";
        public static final String ADDRESS = "address";
//        public static final String POLE_NO = "pole_no";
        public static final String ROUTE_ID = "route_code";
        public static final String BILL_CYCLE_CODE = "bill_cycle_code";
        public static final String METER_NO = "meter_no";
        public static final String DT_CODE = "dt_code";
        public static final String MONTH = "month";
        public static final String CONNECTION_STATUS = "connection_status";
        public static final String EMAIL_ID = "email_id";
        public static final String CURRENT_METER_READING = "current_meter_reading";
        public static final String METER_STATUS = "meter_status";
        public static final String READER_STATUS = "reader_status";
        public static final String READING_IMAGE = "reading_image";
        public static final String COMMENTS = "comments";
        public static final String IS_SUSPICIOUS = "isSuspicious";
        public static final String SUSPICIOUS_REMARKS = "suspicious_remark";
        public static final String SUSPICIOUS_READING_IMAGE = "suspicious_reading_image";
        public static final String CUR_LAT= "cur_lat";
        public static final String CUR_LNG = "cur_lng";
        public static final String NEW_SEQUENCE = "new_sequence";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String READING_DATE = "reading_date";
        public static final String READING_TAKEN_BY = "reading_taken_by";
        public static final String LOCATION_GUIDANCE = "location_guidance";
//        public static final String CURRENT_KVAH_READING = "current_kvah_reading";
//        public static final String CURRENT_KVA_READING = "current_kva_reading";
//        public static final String ISKVAHROUNDCOMPLETED = "is_kvahroundcompleted";
//        public static final String ISKWHROUNDCOMPLETED = "is_kwhroundcompleted";
        public static final String MOBILE_NO = "mobile_no";
//        public static final String PANEL_NO = "panel_no";
        public static final String METER_TYPE = "meter_type";
        public static final String ZONE_CODE = "zone_code";
//        public static final String CURRENT_KW_READING = "current_kw_reading";
//        public static final String CURRENT_PF_READING = "current_pf_reading";

    }
}