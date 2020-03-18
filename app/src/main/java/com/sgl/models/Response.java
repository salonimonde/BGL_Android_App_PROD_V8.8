package com.sgl.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bynry01 on 12-09-2016.
 */
public class Response implements Serializable
{
  public String is_next;
  public ArrayList<UserProfile> user_info;
  public ArrayList<JobCard> jobcards;
  public ArrayList<BillCard> billcards;
  public ArrayList<String> re_de_assigned_jobcards;
  public ArrayList<String> re_de_bd_jobcards;
  public PaymentCalculation first_month;
  public PaymentCalculation second_month;
  public PaymentCalculation third_month;
  public String error_code;
  public ArrayList<String> error;
  public ArrayList<String> new_meter_readings;
  public ArrayList<String> new_unbilled_consumers;
  public String bank_name;
  public String ac_name;
  public String ifsc;
  public String ac_no;

  public ArrayList<Disconnection> disconnectionCards;
  public ArrayList <PaymentCalculation> months;



  public String due_date;
  public String amt_after_due_date;
  public String consumption;
  public String total_charges;
  public String previous_reading;
  public String contact_no;
  public String current_reading;
  public String emi_security_deposit;
  public String consumption_charges;
  public String arrears;
  public String amt_payable_after_due_date;
  public String tin_no;
  public String total_price;
  public String invoice_no;
  public String current_reading_date;
  public String previous_reading_date;
  public String payment;

}
