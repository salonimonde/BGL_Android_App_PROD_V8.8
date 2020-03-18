package com.sgl.models;

import java.io.Serializable;

/**
 * Created by Bynry01 on 12-09-2016.
 */
public class PaymentCalculation implements Serializable {
    public Payment domestic_payment_calculation;
    public Payment zero_payment_calculation;
    public Payment dc_payment_calculation;
    public Double grandtotal;
    public Double pf;
    public Double esi;
    public String other;
}
