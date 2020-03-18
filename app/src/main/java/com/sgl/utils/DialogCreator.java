package com.sgl.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.activity.AddMeterReadingActivity;
import com.sgl.activity.BillDistributionLandingScreen;
import com.sgl.activity.DisconnectionNoticeLandingActivity;
import com.sgl.activity.LandingActivity;
import com.sgl.activity.LoginActivity;
import com.sgl.activity.MyPaymentActivity;
import com.sgl.configuration.AppConstants;

/**
 * Created by Bynry01 on 23-08-2016.
 */
public class DialogCreator {

    //Dialog without Sub-title start Piyush : 02-03-17
    public static void showMessageDialog(final Context mContext, String message, final String mImageDisplay) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_without_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        ImageView imageView = promptView.findViewById(R.id.image_view);
        if (mImageDisplay.equals(CommonUtils.getString(mContext, R.string.error)) || mImageDisplay.equals(CommonUtils.getString(mContext, R.string.spot_bill_dialog))) {
            imageView.setImageResource(R.drawable.high_importance);
        } else {
            imageView.setImageResource(R.drawable.checked_green);
        }
        TextView msg = promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button ok = promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
                if (mImageDisplay.equals(CommonUtils.getString(mContext, R.string.spot_bill_dialog))) {
                    ((AddMeterReadingActivity) mContext).finish();}
            }
        });
        alert.setView(promptView);
        alert.show();
    }
    //Dialog without Sub-title ends Piyush : 02-03-17


    //Dialog with Sub-title start Piyush : 02-03-17
    public static void showExitDialog(final Activity activity, String title, String message, final String screenName) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(activity).create();
        TextView t = promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
        t.setText(title);
        TextView msg = promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommonUtils.deleteCache(activity);
                activity.finish();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                activity.startActivity(a);
                AppPreferences.getInstance(activity).putString(AppConstants.SCREEN_FROM_EXIT, screenName);
                alert.dismiss();
            }
        });

        Button no = promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }
    //Dialog with Sub-title ends Piyush : 02-03-17

    //Dialog for Consumer Dialog start Piyush : 04-03-17
    public static void showConsumerDetailsDialog(final Context context, String consumerName, String consumerAddress, String consumerNumber,
                                                 String consumerMeterNo, String cycle, String subDiv, String consumerMobileNo,
                                                 String consumerBinderNo, String sdAmount, String exSdAmount, String totalAmount) {
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_consumer_details, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        TextView lblConsumerMeterNo, lblConsumerNumber, lblCycle, lblSubDiv, lblMobile, lblBinder, lblSdAmount, lblExSdAmount, lblTotalAmount;
        TextView txtConsumerName, txtConsumerAddress, txtConsumerMeterNo, txtConsumerNumber, txtSubDiv, txtCycle, txtBinderNo, txtMobileNo,
                txtSdAmount, txtExSdAmount, txtTotalAmount;

        LinearLayout linearLayout4, linearLayout5;

        //Initialising all labels starts
        lblConsumerNumber = promptView.findViewById(R.id.lbl_consumer_no);
        lblConsumerNumber.setTypeface(bold);
        lblConsumerMeterNo = promptView.findViewById(R.id.lbl_meter_no);
        lblConsumerMeterNo.setTypeface(bold);
        lblCycle = promptView.findViewById(R.id.lbl_cycle);
        lblCycle.setTypeface(bold);
        lblSubDiv = promptView.findViewById(R.id.lbl_subdiv);
        lblSubDiv.setTypeface(bold);
        lblMobile = promptView.findViewById(R.id.lbl_mobile);
        lblMobile.setTypeface(bold);
        lblBinder = promptView.findViewById(R.id.lbl_binder_no);
        lblBinder.setTypeface(bold);
        lblSdAmount = promptView.findViewById(R.id.lbl_sd_amount);
        lblSdAmount.setTypeface(bold);
        lblExSdAmount = promptView.findViewById(R.id.lbl_ex_sd_amount);
        lblExSdAmount.setTypeface(bold);
        lblTotalAmount = promptView.findViewById(R.id.lbl_total_amount);
        lblTotalAmount.setTypeface(bold);
        //Initialising all labels ends

        //Initialising all text starts
        txtConsumerName = promptView.findViewById(R.id.txt_consumer_name);
        txtConsumerName.setTypeface(bold);
        txtConsumerAddress = promptView.findViewById(R.id.txt_consumer_address);
        txtConsumerAddress.setTypeface(regular);
        txtConsumerNumber = promptView.findViewById(R.id.txt_consumer_no);
        txtConsumerNumber.setTypeface(regular);
        txtConsumerMeterNo = promptView.findViewById(R.id.txt_meter_no);
        txtConsumerMeterNo.setTypeface(regular);
        txtCycle = promptView.findViewById(R.id.txt_cycle);
        txtCycle.setTypeface(regular);
        txtSubDiv = promptView.findViewById(R.id.txt_subdiv);
        txtSubDiv.setTypeface(regular);
        txtBinderNo = promptView.findViewById(R.id.txt_binder_no);
        txtBinderNo.setTypeface(regular);
        txtMobileNo = promptView.findViewById(R.id.txt_mobile_no);
        txtMobileNo.setTypeface(regular);

        txtSdAmount = promptView.findViewById(R.id.txt_sd_amount);
        txtSdAmount.setTypeface(regular);
        txtExSdAmount = promptView.findViewById(R.id.txt_ex_sd_amount);
        txtExSdAmount.setTypeface(regular);
        txtTotalAmount = promptView.findViewById(R.id.txt_total_amount);
        txtTotalAmount.setTypeface(regular);

        linearLayout4 = promptView.findViewById(R.id.linear_layout_4);
        linearLayout5 = promptView.findViewById(R.id.linear_layout_5);
        //Initialising all text ends

        //Setting values to the text starts
        txtConsumerName.setText(consumerName);
        txtConsumerAddress.setText(consumerAddress);
        txtConsumerNumber.setText(consumerNumber);
        txtConsumerMeterNo.setText(consumerMeterNo);
        txtCycle.setText(cycle);
        txtSubDiv.setText(subDiv);
        txtBinderNo.setText(consumerBinderNo);
        txtMobileNo.setText(consumerMobileNo);

        if (sdAmount == null) {
            linearLayout4.setVisibility(View.GONE);
            linearLayout5.setVisibility(View.GONE);
        } else {
            txtSdAmount.setText(sdAmount);
            txtExSdAmount.setText(exSdAmount);
            txtTotalAmount.setText(totalAmount);
        }
        //Setting values to the text ends

        //OK button code starts
        Button yes = (Button) promptView.findViewById(R.id.btn_ok);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.show();
        //OK button code ends
    }
    //Dialog for Consumer Dialog ends Piyush : 04-03-17

    public static void showUpdateDialog(final Context mContext, String message, final String link) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_without_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        ImageView imageView = promptView.findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.checked_green);
        TextView msg = promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button ok = promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setText("Update");
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    mContext.startActivity(myIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

    public static void showLogoutDialog(final Activity activity, String title, String message) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(activity).create();
        TextView t = promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
        t.setText(title);
        TextView msg = promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommonUtils.logout(activity);
                Intent in = new Intent(activity, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(in);
                activity.finish();
                alert.dismiss();
            }
        });

        Button no = promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }
}