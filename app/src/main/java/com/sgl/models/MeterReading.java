package com.sgl.models;

import java.io.Serializable;

/**
 * Created by Bynry01 on 8/30/2015.
 */
public class MeterReading implements Serializable
{
    public String meter_no;
    public String meter_reader_id;
    public String job_card_id;// "job_card_id":"151",
    public String current_meter_reading;//"current_meter_reading":"123456",
    public String meter_status;//"meter_status":"test",
    public String reader_status;//"reader_status":"test",
    public String reading_month;//"reading_month":"201609",

    public MeterImage meter_image;
    public String suspicious_activity;//"suspicious_activity":"true",
    public MeterImage suspicious_activity_image;

    public String reader_remark_comment;
    public String suspicious_remark;
    public String cur_lat;
    public String cur_lng;
    public String isUploaded;
    public String isRevisit;
    public String prv_sequence;
    public String new_sequence;
    public String reading_date;
    public String reading_taken_by;
    public String location_guidance;
    public String mobile_no;
    public String meter_type;
    public String zone_code;

    public String door_lock_reading_attempt;
    public String permanently_locked_reading_attempt;
    public MeterImage cheque_image;
    public String cheque_number;
    public String cheque_amount;

    public String time_taken;

    public String spot_billing;


    public String current_charges;
    public String consumptionCharges;
    public String amt_before_due;
    public String amt_after_due;
    public String bill_type;
    public String due_date;
    public String pre_due;
    public String payment;
    public String emi_security_dep;
    public String total_price;
    public String consump_days;



    public String name_change;
    public String address_change;
    public String meter_change;
    public String mobile_change;

    public String changed_name;
    public String changed_address;
    public String changed_meter;
    public String changed_mobile;


}


