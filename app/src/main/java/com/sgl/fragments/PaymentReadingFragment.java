package com.sgl.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.Payment;
import com.sgl.utils.App;

/**
 * Created by Admin on 29-08-2017.
 */
public class PaymentReadingFragment extends Fragment
{
    private View mRootView;
    private TextView lblBinder, txtBinder, txtConsumer, lblConsumer, txtAllocated, lblAllocated, lblReading, txtReading,
            txtSnf, lblSnf, txtRnt, lblRnt, lblRate, txtRate, txtBillable, lblBillable, lblAmount, txtAmoumt;

    public PaymentReadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_payment_reading, container, false);
        Payment payment = (Payment) getArguments().getSerializable("CURRENT_PAYMENT_CARD");
        Typeface bold = App.getSansationBoldFont(), regular = App.getSansationRegularFont();

        lblBillable = mRootView.findViewById(R.id.lbl_billablereading);
        lblBillable.setTypeface(regular);

        lblBinder = mRootView.findViewById(R.id.lbl_binder);
        lblBinder.setTypeface(regular);

        lblConsumer = mRootView.findViewById(R.id.lbl_consumers);
        lblConsumer.setTypeface(regular);

        lblAllocated = mRootView.findViewById(R.id.lbl_allocated);
        lblAllocated.setTypeface(regular);

        lblReading = mRootView.findViewById(R.id.lbl_readings);
        lblReading.setTypeface(regular);

        lblSnf = mRootView.findViewById(R.id.lbl_sitenotfound);
        lblSnf.setTypeface(regular);

        lblRnt = mRootView.findViewById(R.id.lbl_rnt);
        lblRnt.setTypeface(regular);

        lblAmount = mRootView.findViewById(R.id.lbl_amount);
        lblAmount.setTypeface(regular);

        lblRate = mRootView.findViewById(R.id.lbl_rate);
        lblRate.setTypeface(regular);

        txtReading = mRootView.findViewById(R.id.txt_reading);
        txtReading.setTypeface(regular);
        txtReading.setText(payment.totalreading);

        txtBillable = mRootView.findViewById(R.id.txt_billablereading);
        txtBillable.setTypeface(regular);
        txtBillable.setText(payment.billablereading);

        txtAllocated = mRootView.findViewById(R.id.txt_allocated);
        txtAllocated.setTypeface(regular);
        txtAllocated.setText(payment.allocated);

        txtSnf = mRootView.findViewById(R.id.txt_sitenotfound);
        txtSnf.setTypeface(regular);
        txtSnf.setText(payment.snf);

        txtRnt = mRootView.findViewById(R.id.txt_rnt);
        txtRnt.setTypeface(regular);
        txtRnt.setText(payment.rnt);

        txtBinder = mRootView.findViewById(R.id.txt_binder);
        txtBinder.setTypeface(regular);
        txtBinder.setText(payment.readingbinderassigned);

        txtRate = mRootView.findViewById(R.id.txt_rate);
        txtRate.setTypeface(regular);
        txtRate.setText(payment.mr_rate);

        txtConsumer = mRootView.findViewById(R.id.txt_consumers);
        txtConsumer.setTypeface(regular);
        txtConsumer.setText(payment.readingconsumers);

        txtAmoumt = mRootView.findViewById(R.id.txt_amount);
        txtAmoumt.setTypeface(bold);
        txtAmoumt.setText("Rs. " + Math.round(Math.floor(Double.parseDouble(payment.readingamount))));

        return mRootView;

    }
}
