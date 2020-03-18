package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.models.HistoryCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class HistoryCardAdapter  extends RecyclerView.Adapter<HistoryCardAdapter.HistoryCardListHolder>
{

    public Context mContext;
    private ArrayList<HistoryCard> mHistoryCards;
    
    public HistoryCardAdapter(Context context, ArrayList<HistoryCard> HistoryCards)
    {
        this.mContext = context;
        this.mHistoryCards = HistoryCards;
    }

    @Override
    public HistoryCardListHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_card, null);
        HistoryCardListHolder viewHolder = new HistoryCardListHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryCardListHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final HistoryCard item = mHistoryCards.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold=App.getSansationBoldFont();
        holder.txtOpen.setTypeface(bold);
        holder.txtOpen.setText(String.valueOf(item.open));
        holder.txtRevisit.setTypeface(bold);
        holder.txtRevisit.setText(String.valueOf(item.revisit));
        holder.date.setTypeface(regular);
        holder.date.setText(String.valueOf(item.date));
        holder.txtBillCycle.setTypeface(regular);
        holder.txtBillCycle.setText(String.valueOf(item.billcycle));
        holder.txtRoute.setTypeface(regular);
        holder.txtRoute.setText(String.valueOf(item.route));
        holder.txtUnBill.setTypeface(bold);
        holder.txtUnBill.setText(String.valueOf(item.unbill));
    }

    @Override
    public int getItemCount()
    {
        if(mHistoryCards != null && mHistoryCards.size() > 0)
            return mHistoryCards.size();
        else
            return 0;
    }
    
    public  class HistoryCardListHolder extends RecyclerView.ViewHolder
    {
        public TextView txtRevisit, lblRevisit, txtRoute, txtOpen, lblOpen, txtUnBill, lblUnBill, date, txtBillCycle;

        public HistoryCardListHolder(View itemView)
        {
            super(itemView);
            
            Typeface regular= App.getSansationRegularFont();
            Typeface bold=App.getSansationBoldFont();
            
            txtRevisit = itemView.findViewById(R.id.revisit);
            txtRevisit.setTypeface(bold);
            lblRevisit = itemView.findViewById(R.id.lbl_revisit);
            lblRevisit.setTypeface(regular);
            txtRoute = itemView.findViewById(R.id.route);
            txtRoute.setTypeface(regular);
            date = itemView.findViewById(R.id.date);
            date.setTypeface(regular);
            txtOpen = itemView.findViewById(R.id.open);
            txtOpen.setTypeface(bold);
            lblOpen = itemView.findViewById(R.id.lbl_open);
            lblOpen.setTypeface(regular);
            txtUnBill = itemView.findViewById(R.id.unbill);
            txtUnBill.setTypeface(bold);
            lblUnBill = itemView.findViewById(R.id.lbl_unbill);
            lblUnBill.setTypeface(regular);
            txtBillCycle = itemView.findViewById(R.id.bill_cycle_code);
            txtBillCycle.setTypeface(regular);

        }
    }
}
