package com.sgl.models;

import java.io.Serializable;

/**
 * Created by  on 31-05-2017.
 * Bynry
 */
public class Payment implements Serializable {
    public String grandtotal;
    public String readingamount;
    public String distributedamount;
    public String totalamount;
    public String totalreading;
    public String mr_rate;
    public String bd_rate;
    public String billablereading;
    public String distributed;
    public String readingbinderassigned;
    public String distributedbinderassigned;
    public String rnt;
    public String allocated;
    public String distributedconsumer;
    public String readingconsumers;
    public String meter_reader_id;
    public String snf;
    public String other_deduction;
    public String billmonth;

    public String dcconsumers;
    public String dcbinderassigned;
    public String dcamount;
    public String dc_distributed;
    public String dc_rate;

    public Payment() {
    }


}

