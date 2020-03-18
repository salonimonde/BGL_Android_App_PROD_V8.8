package com.sgl.models;

import java.io.Serializable;

/**
 * Created by Piyush on 31-05-2017.
 * Bynry
 */
public class BillCard implements Serializable
{
    public String jobcard_id;
    public String subdivision_name;
    public String cycle_code;
    public String binder_code;
    public String start_date;
    public String end_date;
    public String consumer_assigned;
    public String meter_reader_id;
    public String jobcard_status;
    public String distributed;
    public String remark;
    public String billmonth;
    public String reading_date;
    public String bill_received_count;

    public BillCard()
    {
    }
}

