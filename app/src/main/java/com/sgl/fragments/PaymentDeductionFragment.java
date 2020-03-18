package com.sgl.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.PaymentCalculation;
import com.sgl.utils.App;

/**
 * Created by Admin on 29-08-2017.
 */

public class PaymentDeductionFragment extends Fragment
{
    long a, b, c;
    private TextView lblPf, txtPf, lblTd, txtTd, txtEsi, lblEsi, txtOther, lblOther, lblPTax, txtPTax;

    public PaymentDeductionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_payment_deduction, container, false);
        PaymentCalculation mPayment = (PaymentCalculation) getArguments().getSerializable("CURRENT_PAYMENT_CARD");
        Typeface bold = App.getSansationBoldFont(), regular = App.getSansationRegularFont();

        a = Math.round(Math.floor(Double.valueOf(mPayment.pf)));
        b =  Math.round(Math.floor(Double.valueOf(mPayment.esi)));
        if (mPayment.other != null)
            c = a + b + Math.round(Math.floor(Double.valueOf(Double.valueOf(mPayment.other))));
        else
            c = a + b;

        lblPf = mRootView.findViewById(R.id.lbl_pftax);
        lblPf.setTypeface(regular);

        lblOther = mRootView.findViewById(R.id.lbl_other);
        lblOther.setTypeface(regular);

        lblEsi = mRootView.findViewById(R.id.lbl_esi);
        lblEsi.setTypeface(regular);

        lblPf = mRootView.findViewById(R.id.lbl_pf);
        lblPf.setTypeface(regular);

        lblPTax = mRootView.findViewById(R.id.lbl_pftax);
        lblPTax.setTypeface(regular);

        lblTd = mRootView.findViewById(R.id.lbl_total_deduction);
        lblTd.setTypeface(regular);

        txtPTax = mRootView.findViewById(R.id.txt_pftax);
        txtPTax.setTypeface(regular);
        txtPTax.setText("Rs. " + "0");

        txtTd = mRootView.findViewById(R.id.txt_total_deduction);
        txtTd.setTypeface(bold);

        txtTd.setText("Rs. " + Math.round(Math.floor(c)));

        txtPf = mRootView.findViewById(R.id.txt_pf);
        txtPf.setTypeface(regular);

        txtPf.setText("Rs. " + Math.round(Math.floor(Double.valueOf(mPayment.pf))));


        txtOther = mRootView.findViewById(R.id.txt_other);
        txtOther.setTypeface(regular);
        if (mPayment.other != null)
            txtOther.setText("Rs. " +Math.round(Math.floor(Double.valueOf(mPayment.other))));
        else
            txtOther.setText("Rs. " + "0");

        txtEsi = mRootView.findViewById(R.id.txt_esi);
        txtEsi.setTypeface(regular);
        txtEsi.setText("Rs. " + Math.round(Math.floor(Double.valueOf(mPayment.esi))));

        return mRootView;

    }
}
