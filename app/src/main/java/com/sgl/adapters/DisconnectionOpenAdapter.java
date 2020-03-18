package com.sgl.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.activity.DisconnectionNoticeDetailActivity;
import com.sgl.configuration.AppConstants;
import com.sgl.models.Disconnection;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class DisconnectionOpenAdapter extends RecyclerView.Adapter<DisconnectionOpenAdapter.DisconnectionOpenHolder>
{
    private Context mContext;
    private ArrayList<Disconnection> disconnections;

    public DisconnectionOpenAdapter(Context context, ArrayList<Disconnection> disconnections)
    {
        this.mContext = context;
        this.disconnections = disconnections;
    }

    @Override
    public DisconnectionOpenAdapter.DisconnectionOpenHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_open_card, null);
        DisconnectionOpenAdapter.DisconnectionOpenHolder viewHolder = new DisconnectionOpenAdapter.DisconnectionOpenHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DisconnectionOpenAdapter.DisconnectionOpenHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        holder.txtSubDivisionName.setText(disconnections.get(position).consumer_name);
        holder.txtBinderNo.setText(disconnections.get(position).consumer_no);
        holder.txtConsumers.setText(disconnections.get(position).binder_code);
        holder.txtEndDate.setText(disconnections.get(position).disconnection_notice_no);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, DisconnectionNoticeDetailActivity.class);
                intent.putExtra(AppConstants.DISCONNECTION_ADAPTER_VALUE, disconnections.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if (disconnections != null && disconnections.size() > 0)
            return disconnections.size();
        else
            return 0;
    }

    public class DisconnectionOpenHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout relativeLayout;
        public TextView lblSubDivisionName, txtSubDivisionName, lblBinderNo, txtBinderNo, lblConsumers, txtConsumers, lblEndDate,
                txtEndDate;
        public Typeface bold, regular;

        public DisconnectionOpenHolder(View itemView)
        {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();

            relativeLayout = itemView.findViewById(R.id.relative_card);

            lblSubDivisionName = itemView.findViewById(R.id.lbl_sub_division_name);
            lblSubDivisionName.setTypeface(regular);
            lblSubDivisionName.setText(CommonUtils.getString(mContext, R.string.consumer_name));
            txtSubDivisionName = itemView.findViewById(R.id.txt_sub_division_name);
            txtSubDivisionName.setTypeface(regular);
            lblBinderNo = itemView.findViewById(R.id.lbl_binder_no);
            lblBinderNo.setTypeface(regular);
            lblBinderNo.setText(CommonUtils.getString(mContext, R.string.consumer_no));
            txtBinderNo = itemView.findViewById(R.id.txt_binder_no);
            txtBinderNo.setTypeface(regular);
            lblConsumers = itemView.findViewById(R.id.lbl_consumers);
            lblConsumers.setTypeface(regular);
            lblConsumers.setText(CommonUtils.getString(mContext, R.string.binder));
            txtConsumers = itemView.findViewById(R.id.txt_consumers);
            txtConsumers.setTypeface(regular);
            lblEndDate = itemView.findViewById(R.id.lbl_end_date);
            lblEndDate.setTypeface(regular);
            lblEndDate.setText(CommonUtils.getString(mContext, R.string.dc_notice_no));
            txtEndDate = itemView.findViewById(R.id.txt_end_date);
            txtEndDate.setTypeface(regular);
        }
    }
}