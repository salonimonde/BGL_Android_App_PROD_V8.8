package com.sgl.models;

import java.io.Serializable;

/**
 * Created by Piyush on 31-05-2017.
 * Bynry
 */
public class UploadBillHistory implements Serializable
{
    public String jobcard_id;
    public String subdivision_name;
    public String cycle_code;
    public String binder_code;
    public String consumer_assigned;
    public String meter_reader_id;
    public String distributed;
    public String billmonth;
    public String reading_date;

    public UploadBillHistory() {
    }
}

