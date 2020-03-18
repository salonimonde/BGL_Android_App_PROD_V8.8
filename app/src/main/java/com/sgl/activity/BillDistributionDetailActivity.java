package com.sgl.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.BillCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

public class BillDistributionDetailActivity extends ParentActivity implements View.OnClickListener 
{

    private Context mContext;
    private TextView lblConsumersReceived, txtConsumersReceived, lblBillMonth, txtBillMonth, lblSubDivisionName, txtSubDivisionName, txtcycleName, lblBinderNo, txtBinderNo, lblConsumers, lblcycleName,
            txtConsumers, txtIsCompleted;
    private EditText edtDistributed, edtRemark;
    private RadioButton radioYes, radioNo;
    private RadioGroup radioYesno;
    private ImageView imgBack;
    private Button btnSubmit;
    private BillCard mBillCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_distribution_detail);
        mContext = this;
        Intent i = this.getIntent();
        if (i != null) {
            if (mBillCard == null) {
                mBillCard = (BillCard) i.getSerializableExtra(AppConstants.CURRENT_Bill_CARD);
            }
        }
        Typeface regular = App.getSansationRegularFont();

        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        lblBillMonth = findViewById(R.id.lbl_bill_month);
        lblBillMonth.setTypeface(regular);
        lblSubDivisionName = findViewById(R.id.lbl_sub_division_name);
        lblSubDivisionName.setTypeface(regular);
        lblcycleName = findViewById(R.id.lbl_sub_division_name);
        lblcycleName.setTypeface(regular);
        lblBinderNo = findViewById(R.id.lbl_binder_no);
        lblBinderNo.setTypeface(regular);
        lblConsumers = findViewById(R.id.lbl_consumers);
        lblConsumers.setTypeface(regular);
        lblConsumersReceived = findViewById(R.id.lbl_consumers_recevied);
        lblConsumersReceived.setTypeface(regular);

        txtConsumersReceived = findViewById(R.id.txt_consumers_recevied);
        txtConsumersReceived.setTypeface(regular);
        txtConsumersReceived.setText(mBillCard.bill_received_count);

        txtBillMonth = findViewById(R.id.txt_bill_month);
        txtBillMonth.setTypeface(regular);
        txtBillMonth.setText(mBillCard.billmonth);

        txtSubDivisionName = findViewById(R.id.txt_sub_division_name);
        txtSubDivisionName.setTypeface(regular);
        txtSubDivisionName.setText(mBillCard.subdivision_name);

        txtcycleName = findViewById(R.id.txt_cycle_name);
        txtcycleName.setTypeface(regular);
        txtcycleName.setText(mBillCard.cycle_code);

        txtBinderNo = findViewById(R.id.txt_binder_no);
        txtBinderNo.setTypeface(regular);
        txtBinderNo.setText(mBillCard.binder_code);

        txtConsumers = findViewById(R.id.txt_consumers);
        txtConsumers.setTypeface(regular);
        txtConsumers.setText(mBillCard.consumer_assigned);

        txtIsCompleted = findViewById(R.id.txt_is_completed);
        txtIsCompleted.setTypeface(regular);

        edtDistributed = findViewById(R.id.edt_distributed);
        edtDistributed.setTypeface(regular);

        edtRemark = findViewById(R.id.edt_remark);
        edtRemark.setTypeface(regular);

        radioYesno = findViewById(R.id.rg_yesno);

        radioYes = findViewById(R.id.radio_yes);
        radioYes.setTypeface(regular);
        radioYes.setOnClickListener(this);
        radioNo = findViewById(R.id.radio_no);
        radioNo.setTypeface(regular);
        radioNo.setOnClickListener(this);
        radioNo.setChecked(true);

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setTypeface(regular);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == radioYes) {
            edtDistributed.setVisibility(View.GONE);
        }

        if (v == radioNo) {
            edtDistributed.setVisibility(View.VISIBLE);
        }

        if (v == btnSubmit) {
            submitBtnClicked();
        }
        if (v == imgBack) {
            finish();
        }
    }

    private void submitBtnClicked() {
        if (radioYesno.getCheckedRadioButtonId() == R.id.radio_yes) {
            mBillCard.distributed = mBillCard.bill_received_count;
            mBillCard.remark = edtRemark.getText().toString().trim();
            mBillCard.reading_date = CommonUtils.getCurrentDateTime();
            DatabaseManager.saveBillCardStatus(mContext, mBillCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
            finish();
        } else if (radioYesno.getCheckedRadioButtonId() == R.id.radio_no) {
            if (edtDistributed.getText().toString().trim().equals("")) {
                Toast.makeText(mContext, getString(R.string.please_add_total_bill_distributed), Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(edtDistributed.getText().toString().trim()) <= Integer.parseInt(mBillCard.bill_received_count)) {
                mBillCard.distributed = edtDistributed.getText().toString().trim();
                mBillCard.remark = edtRemark.getText().toString().trim();
                mBillCard.reading_date = CommonUtils.getCurrentDateTime();
                DatabaseManager.saveBillCardStatus(mContext, mBillCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
                finish();

            } else
                Toast.makeText(mContext, getString(R.string.please_enter_valid_count), Toast.LENGTH_SHORT).show();
        }
    }
}
