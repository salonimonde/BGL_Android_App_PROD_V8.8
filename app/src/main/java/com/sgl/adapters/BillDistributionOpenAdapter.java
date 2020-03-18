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
import com.sgl.models.BillCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class BillDistributionOpenAdapter extends RecyclerView.Adapter<BillDistributionOpenAdapter.BillDistributionHolder>
{

    private Context mContext;
    private ArrayList<BillCard> mBillCards;
    private OnBillCardClickListener mListener;

    public BillDistributionOpenAdapter(Context context, ArrayList<BillCard> billCards, OnBillCardClickListener listener)
    {
        this.mContext = context;
        this.mBillCards = billCards;
        this.mListener = listener;
    }

    public interface OnBillCardClickListener
    {
        void onBillCardClick(BillCard billCard);
    }

    @Override
    public BillDistributionHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_open_card, null);
        BillDistributionOpenAdapter.BillDistributionHolder viewHolder = new BillDistributionOpenAdapter.BillDistributionHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BillDistributionHolder holder, int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final BillCard item = mBillCards.get(position);
        holder.txtSubDivisionName.setText(item.subdivision_name);
        holder.txtBinderNo.setText(item.cycle_code+"/"+item.binder_code);
        holder.txtConsumers.setText(item.consumer_assigned);
        holder.txtEndDate.setText(item.end_date);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mListener.onBillCardClick(item);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if(mBillCards != null && mBillCards.size() > 0)
            return mBillCards.size();
        else
            return 0;
    }

    public class BillDistributionHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout relativeLayout;
        public TextView lblSubDivisionName, txtSubDivisionName, lblBinderNo, txtBinderNo, lblConsumers, txtConsumers, lblEndDate,
                txtEndDate;
        public Typeface bold, regular;

        public BillDistributionHolder(View itemView)
        {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();

            relativeLayout = itemView.findViewById(R.id.relative_card);

            lblSubDivisionName = itemView.findViewById(R.id.lbl_sub_division_name);
            lblSubDivisionName.setTypeface(regular);
            txtSubDivisionName = itemView.findViewById(R.id.txt_sub_division_name);
            txtSubDivisionName.setTypeface(regular);
            lblBinderNo = itemView.findViewById(R.id.lbl_binder_no);
            lblBinderNo.setTypeface(regular);
            txtBinderNo = itemView.findViewById(R.id.txt_binder_no);
            txtBinderNo.setTypeface(regular);
            lblConsumers = itemView.findViewById(R.id.lbl_consumers);
            lblConsumers.setTypeface(regular);
            txtConsumers = itemView.findViewById(R.id.txt_consumers);
            txtConsumers.setTypeface(regular);
            lblEndDate = itemView.findViewById(R.id.lbl_end_date);
            lblEndDate.setTypeface(regular);
            txtEndDate = itemView.findViewById(R.id.txt_end_date);
            txtEndDate.setTypeface(regular);
        }
    }
}
