package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.db.DatabaseManager;
import com.sgl.hepler.ItemTouchHelperAdapter;
import com.sgl.models.NotificationCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.NotificationCardHolder>
        implements ItemTouchHelperAdapter
{

    public Context mContext;
    private ArrayList<NotificationCard> mNotificationCard;
    
    public NotificationCardAdapter(Context context, ArrayList<NotificationCard> NotificationCards)
    {
        this.mContext = context;
        this.mNotificationCard = NotificationCards;

    }
    
    @Override
    public NotificationCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_notification_card, null);
        NotificationCardHolder viewHolder = new NotificationCardHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationCardHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final NotificationCard item = mNotificationCard.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold=App.getSansationBoldFont();
        holder.date.setTypeface(bold);
        holder.date.setText(String.valueOf(item.date));
        holder.msg.setTypeface(regular);
        holder.msg.setText(String.valueOf(item.message));
        holder.title.setText(String.valueOf(item.title));

    }

    @Override
    public int getItemCount()
    {
        if(mNotificationCard != null && mNotificationCard.size() > 0)
            return mNotificationCard.size();
        else
            return 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        Collections.swap(mNotificationCard, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position)
    {
        DatabaseManager.deleteAccount(mContext, mNotificationCard.get(position).message);
        mNotificationCard.remove(position);
        notifyItemRemoved(position);
    }

    public class NotificationCardHolder extends RecyclerView.ViewHolder
    {
        
        public TextView msg, date, title;
        public LinearLayout card;

        public NotificationCardHolder(View itemView)
        {
            super(itemView);
            
            Typeface regular = App.getSansationRegularFont();
            Typeface bold = App.getSansationBoldFont();
            
            msg = itemView.findViewById(R.id.tv_notifications);
            msg.setTypeface(regular);
            date = itemView.findViewById(R.id.tv_date);
            date.setTypeface(bold);
            title = itemView.findViewById(R.id.tv_notifications_title);
            title.setTypeface(bold);
            card = itemView.findViewById(R.id.card);
        }
    }
}



