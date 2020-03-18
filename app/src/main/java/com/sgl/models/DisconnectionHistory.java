package com.sgl.models;

import java.io.Serializable;

/**
 * Created by Piyush on 08-12-2017.
 * Bynry
 */
public class DisconnectionHistory implements Serializable
{
    public String meter_reader_id;
    public String binder_code;
    public String bill_month;
    public String job_card_id;
    public String date;
    public String delivery_status;

    public String total;
    public String totalDelivered;
    public String totalNotDDelivered;
}