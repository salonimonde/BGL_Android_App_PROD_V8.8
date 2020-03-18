package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.UploadBillHistory;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class BillDistributionHistoryAdapter extends RecyclerView.Adapter<BillDistributionHistoryAdapter.BillDistributionHistoryHolder>
{

    private Context mContext;
    private ArrayList<UploadBillHistory> mBillCards;

    public BillDistributionHistoryAdapter(Context context, ArrayList<UploadBillHistory> mHistoryCards) {
        mContext = context;
        mBillCards = mHistoryCards;
    }

    @Override
    public BillDistributionHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_history_card, null);
        BillDistributionHistoryHolder viewHolder = new BillDistributionHistoryHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(BillDistributionHistoryHolder holder, int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final UploadBillHistory item = mBillCards.get(position);
        holder.txtBillCycleCode.setText(item.cycle_code);
        holder.txtBinders.setText(item.binder_code);
        holder.txtDate.setText(item.reading_date);
        holder.txtTotalConsumers.setText(item.consumer_assigned);
        holder.txtCompleted.setText(item.distributed);
    }

    @Override
    public int getItemCount() {
        if(mBillCards != null && mBillCards.size() > 0)
            return mBillCards.size();
        else
            return 0;
    }

    public class BillDistributionHistoryHolder extends RecyclerView.ViewHolder 
    {
        
        public TextView txtBillCycleCode, txtBinders, txtDate, txtTotalConsumers, lblConsumers, txtCompleted, lblCompleted;
        public Typeface bold, regular;

        public BillDistributionHistoryHolder(View itemView) {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();
            
            txtBillCycleCode = itemView.findViewById(R.id.bill_cycle_code);
            txtBillCycleCode.setTypeface(regular);
            txtBinders = itemView.findViewById(R.id.binder);
            txtBinders.setTypeface(regular);
            txtDate = itemView.findViewById(R.id.date);
            txtDate.setTypeface(regular);
            txtTotalConsumers = itemView.findViewById(R.id.txt_consumers);
            txtTotalConsumers.setTypeface(regular);
            lblConsumers = itemView.findViewById(R.id.lbl_consumer);
            lblConsumers.setTypeface(regular);
            txtCompleted = itemView.findViewById(R.id.txt_completed);
            txtCompleted.setTypeface(regular);
            lblCompleted = itemView.findViewById(R.id.lbl_completed);
            lblCompleted.setTypeface(regular);
        }
    }
}
