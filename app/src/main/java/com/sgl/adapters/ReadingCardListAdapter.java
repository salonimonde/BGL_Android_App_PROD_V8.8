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
import android.widget.Toast;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JobCard;
import com.sgl.models.MeterReading;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReadingCardListAdapter extends RecyclerView.Adapter<ReadingCardListAdapter.ReadingCardListHolder> {

    public Context mContext;
    private ArrayList<MeterReading> mReadingCards;
    private ArrayList<JobCard> mJobCard;
    private OnJobCardClickListener mListener;

    public ReadingCardListAdapter(Context context, ArrayList<MeterReading> readingCard, ArrayList<JobCard> jobCards, ReadingCardListAdapter.OnJobCardClickListener listener) {
        this.mContext = context;
        this.mReadingCards = readingCard;
        this.mJobCard = jobCards;
        this.mListener = listener;
    }

    @Override
    public ReadingCardListAdapter.ReadingCardListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_reading_card, null);
        ReadingCardListAdapter.ReadingCardListHolder viewHolder = new ReadingCardListAdapter.ReadingCardListHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReadingCardListAdapter.ReadingCardListHolder holder, int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);


        final MeterReading meterReadingItem = mReadingCards.get(position);
//            final JobCard jobCardItem = mJobCard.get(position);
        final ArrayList<JobCard> mmJobCard = DatabaseManager.getJobCardByJobcardId(mContext, meterReadingItem.meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, meterReadingItem.isRevisit, meterReadingItem.job_card_id);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();

        holder.mConsumerName.setTypeface(regular);
        holder.mConsumerName.setText(mmJobCard.get(0).consumer_name);
        holder.mConsumerID.setTypeface(bold);
        holder.mConsumerID.setText(mmJobCard.get(0).meter_no);
        holder.mScheduleEndDate.setTypeface(bold);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(meterReadingItem.reading_date);
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.mScheduleEndDate.setText(dateFormat.format(date1));
        holder.mMeterID.setTypeface(bold);
        holder.mMeterID.setText(meterReadingItem.current_meter_reading);


        // TODO Uncomment for normal flow of door lock

        /*if (meterReadingItem.reader_status.equalsIgnoreCase(mContext.getString(R.string.door_lock))) {
            holder.relativeJobCard_bg.setBackgroundColor(CommonUtils.getColor(mContext, R.color.colorText11));
        }
        holder.relativeJobCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onJobCardClick(meterReadingItem, mmJobCard.get(0));
            }
        });*/
    }

    @Override
    public int getItemCount() {
        if (mJobCard != null && mJobCard.size() > 0)
            return mJobCard.size();
        else
            return 0;
    }

    public interface OnJobCardClickListener {
        void onJobCardClick(MeterReading meterReadingItem, JobCard jobCardItem);
    }

    public class ReadingCardListHolder extends RecyclerView.ViewHolder {

        public TextView mstatus, mConsumerName, mScheduleEndDate, mConsumerID, mMeterID, ConsumerName, ReadingDate, ConsumerID, MeterID;
        public RelativeLayout relativeJobCard;
        public LinearLayout relativeJobCard_bg;

        public ReadingCardListHolder(View itemView) {
            super(itemView);
            relativeJobCard = itemView.findViewById(R.id.rl_reading_card_cell);
            mConsumerName = itemView.findViewById(R.id.consumerName);
            mConsumerID = itemView.findViewById(R.id.txt_consumer_no);
            mScheduleEndDate = itemView.findViewById(R.id.txt_reading_take);
            mMeterID = itemView.findViewById(R.id.txt_reading);
            Typeface regular = App.getSansationRegularFont();
            ConsumerName = itemView.findViewById(R.id.lbl_consumer);
            ConsumerName.setTypeface(regular);
            ConsumerID = itemView.findViewById(R.id.lbl_consumerno);
            ConsumerID.setTypeface(regular);
            ReadingDate = itemView.findViewById(R.id.lbl_duedate);
            ReadingDate.setTypeface(regular);
            MeterID = itemView.findViewById(R.id.lbl_meternno);
            MeterID.setTypeface(regular);
            relativeJobCard_bg = itemView.findViewById(R.id.rl_reading_card_cell_bg);
        }
    }
}