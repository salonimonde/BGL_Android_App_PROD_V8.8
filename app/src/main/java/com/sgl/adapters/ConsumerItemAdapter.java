package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.Consumer;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ConsumerItemAdapter extends RecyclerView.Adapter<ConsumerItemAdapter.ViewHolder>
{

    private ArrayList<Consumer> consumers;
    private Context mContext;
    private Typeface bold;

    public ConsumerItemAdapter(Context context, ArrayList<Consumer> consumers)
    {
        this.consumers = consumers;
        mContext = context;
    }

    @Override
    public ConsumerItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cell_reading_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        mContext = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConsumerItemAdapter.ViewHolder viewHolder, int position)
    {
        viewHolder.bind(consumers.get(position));
        CommonUtils.setAnimation(viewHolder.itemView, position, -1, mContext);
        bold = App.getSansationRegularFont();

        viewHolder.txtConsumerName.setTypeface(bold);
        viewHolder.txtConsumerId.setTypeface(bold);
        viewHolder.txtMeterNo.setTypeface(bold);
        viewHolder.txtReading.setTypeface(bold);

        viewHolder.lblConsumerName.setTypeface(bold);
        viewHolder.lblConsumerId.setTypeface(bold);
        viewHolder.lblMeterNo.setTypeface(bold);
        viewHolder.lblReading.setTypeface(bold);
    }

    @Override
    public int getItemCount()
    {
        if (consumers != null)
            return consumers.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView txtConsumerName, txtConsumerId, txtMeterNo, txtReading;
        private TextView lblConsumerName, lblConsumerId, lblMeterNo, lblReading;

        public ViewHolder(View itemView)
        {
            super(itemView);

            txtConsumerName = itemView.findViewById(R.id.consumerName);
            txtConsumerId = itemView.findViewById(R.id.txt_consumer_no);
            txtMeterNo = itemView.findViewById(R.id.txt_reading);
            txtReading = itemView.findViewById(R.id.txt_reading_take);

            lblConsumerName = itemView.findViewById(R.id.lbl_consumer);
            lblConsumerId = itemView.findViewById(R.id.lbl_consumerno);
            lblConsumerId.setText("CRN No.");
            lblMeterNo = itemView.findViewById(R.id.lbl_meternno);
            lblReading = itemView.findViewById(R.id.lbl_duedate);

        }

        public void bind(final Consumer consumer) {
            txtConsumerName.setText(consumer.consumer_name);
            txtConsumerId.setText(consumer.consumer_no);
            txtMeterNo.setText(consumer.current_meter_reading);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = null;
            try {
                date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(consumer.reading_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtReading.setText(dateFormat.format(date1));
        }
    }
}
