package com.sgl.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.adapters.NotificationCardAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.hepler.ItemTouchHelperAdapter;
import com.sgl.hepler.OnStartDragListener;
import com.sgl.hepler.SimpleItemTouchHelperCallback;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;

/**
 * Created by Bynry01 on 10/10/2016.
 */
public class NotificationActivity extends ParentActivity implements View.OnClickListener, OnStartDragListener
{

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private TextView title;
    private ImageView imgBack;
    private ItemTouchHelper mItemTouchHelper;
    private Typeface regular;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mContext = this;
        regular = App.getSansationRegularFont();
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        title = findViewById(R.id.title_bar);
        title.setText(getString(R.string.notifications));
        title.setOnClickListener(this);
        title.setTypeface(regular);

        loadRecyclerView();
    }

    private void loadRecyclerView()
    {
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        NotificationCardAdapter adapter = new NotificationCardAdapter(mContext, DatabaseManager.getAllNotification(mContext, AppPreferences.getInstance(mContext).getString(AppConstants.METER_READER_ID, AppConstants.BLANK_STRING)));
        recyclerView.setAdapter(adapter);

        //To activate swipe animation Piyush : 25-02-17 starts
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        //To activate swipe animation Piyush : 25-02-17 ends
    }

    @Override
    public void onClick(View v)
    {
        if (v == imgBack)
        {
            finish();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder)
    {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
