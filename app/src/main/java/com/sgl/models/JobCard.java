package com.sgl.models;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Bynry01 on 09-09-2016.
 */
public class JobCard implements Serializable {

    public String id;
    public String consumer_name;//	"consumer_name": "shubham33",
    public String consumer_no;//"consumer_no": "None",
    public String meter_no;//"meter_no": "688686",

    public String dt_code;//"dt_code": "123",
    public String bill_cycle_code;//"bill_cycle_code": "200"
    public String schedule_month;//"schedule_month": "201609",
    public String schedule_end_date;//"schedule_end_date": "2016-09-30",
    public String route_code;//"route_code": "1000",
    public String pole_no;//"pole_no": "88888",
    public String phone_no;//	"phone_no": "8007271913",
    public String address;//"address": "ABC Road Mumbai ",

    public String meter_reader_id;
    public String prv_meter_reading;
    public String lattitude;
    public String longitude;
    public String job_card_status;
    public String assigned_date;//assigned_date
    public String job_card_id;//"job_card_id": "33",
    public String is_revisit;
    public String prv_sequence;
    public String location_guidance;
    public String prv_kvah_reading;
    public String prv_kva_reading;
    //    public String prv_kwh_reading;
    public String iskwhroundcompleted;
    public String iskvahroundcompleted;
    public String zone_code;
    public String category_id;
    public String avg_consumption;
    public String meter_digit;
    public String account_no;
//    public String route_image;
    public String current_sequence;
    public String snf;
    public String prv_status;
    public String attempt;
    public String street;
    public String building_no;

    public String door_lock_reading_attempt;
    public String permanently_locked_reading_attempt;
    public String sd_amount;
    public String excluding_sd_amount;
    public String total_amount;



    public String prev_reading_date;
    public String total_price;
    public String pre_due;
    public String payment;
    public String emi_security_dep;
    public String avg_unit;

    public String subdivision;
    public String due_amount;

    public JobCard() {
    }

    public String getAccount_no() {
        return account_no;
    }

}
