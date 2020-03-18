package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.PaymentCalculation;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;


public class PaymentCardAdapter extends RecyclerView.Adapter<PaymentCardAdapter.PaymentCardHolder> 
{
    public Context mContext;
    private OnPaymentClickListener mListener;
    private ArrayList<PaymentCalculation> mPaymentCard;
    
    public PaymentCardAdapter(Context context, ArrayList<PaymentCalculation> PaymentCards, OnPaymentClickListener listener) {
        this.mContext = context;
        this.mPaymentCard = PaymentCards;
        this.mListener = listener;
    }

    @Override
    public PaymentCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_payment_card, null);
        PaymentCardHolder viewHolder = new PaymentCardHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PaymentCardHolder holder, final int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final PaymentCalculation item = mPaymentCard.get(position);
        Typeface bold = App.getSansationBoldFont();
        holder.month.setTypeface(bold);
        if (item.domestic_payment_calculation != null)
            holder.month.setText(String.valueOf(item.domestic_payment_calculation.billmonth));
        else
            holder.month.setText(String.valueOf(item.zero_payment_calculation.billmonth));
        
        holder.amount.setText(String.valueOf("Rs. " + Math.round(item.grandtotal)));
        holder.amount.setTypeface(bold);
        holder.relativePaymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPayCardClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mPaymentCard != null && mPaymentCard.size() > 0)
            return mPaymentCard.size();
        else
            return 0;
    }

    public interface OnPaymentClickListener {
        void onPayCardClick(PaymentCalculation pay);
    }

    public class PaymentCardHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativePaymentCard;
        public TextView month, billablereading, amount, totalreading, lbltotalconsumer, billdistributed, totalconsumer, lblbilldistributed, genrateddate, lblmonth,
                lblbillablereading, lblamount, lblcatgory, lbltotalreading, lblgenrateddate;


        public PaymentCardHolder(View itemView) {
            super(itemView);
            Typeface regular = App.getSansationRegularFont();
            relativePaymentCard = itemView.findViewById(R.id.rl_job_payment_cell);
            month = itemView.findViewById(R.id.month);
            month.setTypeface(regular);
            billablereading = itemView.findViewById(R.id.billabereading);
            billablereading.setTypeface(regular);
            amount = itemView.findViewById(R.id.amount);
            amount.setTypeface(regular);
            totalreading = itemView.findViewById(R.id.totalreading);
            totalreading.setTypeface(regular);
            genrateddate = itemView.findViewById(R.id.genrated_on);
            genrateddate.setTypeface(regular);
            billdistributed = itemView.findViewById(R.id.billdistributed);
            billdistributed.setTypeface(regular);
            totalconsumer = itemView.findViewById(R.id.totalbills);
            totalconsumer.setTypeface(regular);

            lblmonth = itemView.findViewById(R.id.lbl_month);
            lblmonth.setTypeface(regular);
            lblbillablereading = itemView.findViewById(R.id.lbl_billablereading);
            lblbillablereading.setTypeface(regular);
            lbltotalreading = itemView.findViewById(R.id.lbl_totalreading);
            lbltotalreading.setTypeface(regular);
            lblamount = itemView.findViewById(R.id.lbl_amount);
            lblamount.setTypeface(regular);
            lblgenrateddate = itemView.findViewById(R.id.lbl_genrated_on);
            lblgenrateddate.setTypeface(regular);
            lbltotalconsumer = itemView.findViewById(R.id.lbl_totalcomsumer);
            lbltotalconsumer.setTypeface(regular);
            lblbilldistributed = itemView.findViewById(R.id.lbl_billdistributed);
            lblbilldistributed.setTypeface(regular);

        }
    }
}



