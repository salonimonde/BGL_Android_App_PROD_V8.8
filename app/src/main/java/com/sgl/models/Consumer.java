package com.sgl.models;

import java.io.Serializable;

/**
 * Created by Bynry01 on 24-08-2016.
 */
public class Consumer implements Serializable
{
    public String consumer_name;
    public String consumer_no;
    public String meter_no;
    public String dtc;
    public String bill_cycle_code;
    public String pole_no;
    public String route_code;
    public String contact_no;
    public String address;
    public String current_meter_reading;
    public String meter_status;
    public String reader_status;
    public String cur_lat;
    public String cur_lng;
    public String email_id;
    public MeterImage meter_image;
    public String reader_remark_comment;
    public String suspicious_activity;
    public String suspicious_remark;
    public MeterImage suspicious_activity_image;
    public String connection_status;
    public String reading_month;
    public String meter_reader_id;
    public String isUploaded;
    public String reading_date;
    public String reading_taken_by;
    public String new_sequence;
    public String location_guidance;
    public String current_kvah_reading;
    public String current_kva_reading;
    public String iskwhroundcompleted;
    public String iskvahroundcompleted;
    public String panel_no;
    public String mobile_no;
    public String meter_type;
    public String id;
    public String zone_code;
    public String current_kw_reading;
    public String current_pf_reading;

    public Consumer()
    { }
}
