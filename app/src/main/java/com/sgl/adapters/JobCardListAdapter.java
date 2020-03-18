package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.JobCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class JobCardListAdapter extends RecyclerView.Adapter<JobCardListAdapter.JobCardListHolder> {

    private OnJobCardClickListener mListener;
    public Context mContext;
    private ArrayList<JobCard> mJobCards;
    private boolean setDifferentValues;

    public JobCardListAdapter(Context context, ArrayList<JobCard> jobCards, OnJobCardClickListener listener, boolean setDifferentValues) {
        this.mContext = context;
        this.mJobCards = jobCards;
        this.mListener = listener;
        this.setDifferentValues = setDifferentValues;
    }

    @Override
    public JobCardListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_job_card, null);
        JobCardListHolder viewHolder = new JobCardListHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final JobCardListHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final JobCard item = mJobCards.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();


        String strDate = item.schedule_end_date.trim();
        String[] separateDate = strDate.split("-");
        String newDate = separateDate[2] + "/" + separateDate[1] + "/" + separateDate[0];

        holder.mConsumerName.setTypeface(regular);
        holder.mConsumerName.setText(item.consumer_name);
        holder.mConsumerID.setTypeface(bold);
        holder.mConsumerID.setText(item.consumer_no);
        holder.mScheduleEndDate.setTypeface(bold);
        holder.mScheduleEndDate.setText(newDate);
        holder.mMeterID.setTypeface(bold);
        holder.mMeterID.setText(item.meter_no);

        if(setDifferentValues)
        {
            holder.lblDueDate.setText(CommonUtils.getString(mContext, R.string.meter_no));
            holder.mScheduleEndDate.setText(item.meter_no);
            holder.lblBPNo.setText(CommonUtils.getString(mContext, R.string.address));
            holder.mConsumerID.setText(item.address);
            holder.linearDivider.setVisibility(View.GONE);
            holder.linearFourthBlock.setVisibility(View.GONE);
        }

        /*String doorLockReadingAttempts = mJobCards.get(position).door_lock_reading_attempt;
        int doorReadAttempts = 0;
        if(doorLockReadingAttempts.equals("") || doorLockReadingAttempts.isEmpty() || doorLockReadingAttempts == null)
            doorReadAttempts = 1;
        else
            doorReadAttempts = Integer.parseInt(doorLockReadingAttempts) - 1;

        String permLockReadingAttempts = mJobCards.get(position).permanently_locked_reading_attempt;
        int permReadAttempts = 0;
        if(permLockReadingAttempts.equals("") || permLockReadingAttempts.isEmpty() || permLockReadingAttempts == null)
            permReadAttempts = 1;
        else
            permReadAttempts = Integer.parseInt(permLockReadingAttempts) - 1;

        if(doorLockReadingAttempts.equals("2"))
        {
            holder.linearCard.setBackgroundColor(CommonUtils.getColor(mContext, R.color.attempt_2));
            holder.txtNoOfAttempts.setVisibility(View.VISIBLE);
            holder.txtNoOfAttempts.setText("DL-"+doorReadAttempts);
        }
        else if(permLockReadingAttempts.equals("2"))
        {
            holder.linearCard.setBackgroundColor(CommonUtils.getColor(mContext, R.color.attempt_3));
            holder.txtNoOfAttempts.setVisibility(View.VISIBLE);
            holder.txtNoOfAttempts.setText("PL-"+permReadAttempts);
        }
        else
        {
            holder.linearCard.setBackgroundColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            holder.txtNoOfAttempts.setVisibility(View.GONE);
        }*/

        holder.linearCard.setBackgroundColor(CommonUtils.getColor(mContext, R.color.colorWhite));
        holder.txtNoOfAttempts.setVisibility(View.GONE);

        holder.relativeJobCard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mListener.onJobCardClick(item);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if(mJobCards != null && mJobCards.size() > 0)
            return mJobCards.size();
        else
            return 0;
    }

    public interface OnJobCardClickListener
    {
        void onJobCardClick(JobCard jobCard);
    }

    public class JobCardListHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout relativeJobCard;
        public LinearLayout linearCard, linearDivider, linearFourthBlock;
        public TextView mConsumerName, mScheduleEndDate, mConsumerID, mMeterID, lblBPName, lblDueDate,
                lblBPNo, lblMeterNo, txtNoOfAttempts;

        public JobCardListHolder(View itemView)
        {
            super(itemView);
            relativeJobCard = itemView.findViewById(R.id.rl_job_card_cell);

            linearCard = itemView.findViewById(R.id.linear_card);
            linearDivider = itemView.findViewById(R.id.linear_divider);
            linearFourthBlock = itemView.findViewById(R.id.linear_fourth_block);
            mConsumerName = itemView.findViewById(R.id.consumerName);
            mConsumerID = itemView.findViewById(R.id.consumerId);
            mScheduleEndDate = itemView.findViewById(R.id.scheduleEnDate);
            mMeterID = itemView.findViewById(R.id.meterID);
            Typeface regular= App.getSansationRegularFont();
            lblBPName = itemView.findViewById(R.id.lbl_consumer);
            lblBPName.setTypeface(regular);
            lblDueDate = itemView.findViewById(R.id.lbl_duedate);
            lblDueDate.setTypeface(regular);
            lblBPNo = itemView.findViewById(R.id.lbl_consumerno);
            lblBPNo.setTypeface(regular);
            lblMeterNo = itemView.findViewById(R.id.lbl_meternno);
            lblMeterNo.setTypeface(regular);
            txtNoOfAttempts = itemView.findViewById(R.id.txt_no_of_attempts);
            txtNoOfAttempts.setTypeface(regular);
        }
    }
}