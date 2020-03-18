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
public class PaymentBillDistributionFragment extends Fragment {

    private TextView lblBinder, txtBinder, lblRate, txtRate, txtConsumer, lblDistributed, txtDistributed,
            lblConsumer, lblAmount, txtAmount;

    public PaymentBillDistributionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_payment_billdistribution, container, false);
        Payment mPayment = (Payment) getArguments().getSerializable("CURRENT_PAYMENT_CARD");

        Typeface bold = App.getSansationBoldFont(), regular = App.getSansationRegularFont();

        lblBinder = mRootView.findViewById(R.id.lbl_binder);
        lblBinder.setTypeface(regular);

        lblConsumer = mRootView.findViewById(R.id.lbl_consumers);
        lblConsumer.setTypeface(regular);

        lblAmount = mRootView.findViewById(R.id.lbl_amount);
        lblAmount.setTypeface(regular);

        lblRate = mRootView.findViewById(R.id.lbl_rate);
        lblRate.setTypeface(regular);

        lblDistributed = mRootView.findViewById(R.id.lbl_distributed);
        lblDistributed.setTypeface(regular);

        txtDistributed = mRootView.findViewById(R.id.txt_distributed);
        txtDistributed.setTypeface(regular);
        if (mPayment.dcconsumers != null) {
            if (!mPayment.dcconsumers.equalsIgnoreCase("0"))
                txtDistributed.setText(mPayment.dcconsumers);
        } else
            txtDistributed.setText(mPayment.distributed);

        txtRate = mRootView.findViewById(R.id.txt_rate);
        txtRate.setTypeface(regular);
        if (mPayment.dcconsumers != null) {
            if (!mPayment.dcconsumers.equalsIgnoreCase("0"))
                txtRate.setText(mPayment.dc_rate);
        } else
            txtRate.setText(mPayment.bd_rate);

        txtBinder = mRootView.findViewById(R.id.txt_binder);
        txtBinder.setTypeface(regular);
        if (mPayment.dcconsumers != null) {
            if (!mPayment.dcconsumers.equalsIgnoreCase("0"))
                txtBinder.setText(mPayment.dcbinderassigned);
        } else
            txtBinder.setText(mPayment.distributedbinderassigned);

        txtConsumer = mRootView.findViewById(R.id.txt_consumers);
        txtConsumer.setTypeface(regular);
        if (mPayment.dcconsumers != null) {
            if (!mPayment.dcconsumers.equalsIgnoreCase("0"))
                txtConsumer.setText(mPayment.dc_distributed);
        } else
            txtConsumer.setText(mPayment.distributedconsumer);

        txtAmount = mRootView.findViewById(R.id.txt_amount);
        txtAmount.setTypeface(bold);
        if (mPayment.dcconsumers != null) {
            if (!mPayment.dcconsumers.equalsIgnoreCase("0"))
                txtAmount.setText("Rs. " + Math.round(Math.floor(Double.parseDouble(mPayment.dcamount))));
        } else
            txtAmount.setText("Rs. " + Math.round(Math.floor(Double.parseDouble(mPayment.distributedamount))));

        return mRootView;

    }
}
