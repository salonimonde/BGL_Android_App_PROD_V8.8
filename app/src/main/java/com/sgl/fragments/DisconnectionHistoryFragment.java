package com.sgl.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.adapters.DisconnectionHistoryAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.DisconnectionHistory;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class DisconnectionHistoryFragment extends Fragment
{
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private String meterReaderId;
    private ArrayList<DisconnectionHistory> dcNoticeCards;

    private TextView lblBlankScreenMsg;

    public DisconnectionHistoryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_disconnection_history, container, false);

        meterReaderId = AppPreferences.getInstance(getContext()).getString(AppConstants.METER_READER_ID, "");
        mContext = getActivity();
        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        loadRecyclerView();

        return rootView;
    }

    private void loadRecyclerView()
    {
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getHistoryDatesArray();

        DisconnectionHistoryAdapter adapter = new DisconnectionHistoryAdapter(mContext, dcNoticeCards);
        recyclerView.setAdapter(adapter);

        if(dcNoticeCards != null && dcNoticeCards.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);
    }

    private void getHistoryDatesArray()
    {
        try{
            //delete old records which are not in range of days.
            DatabaseManager.deleteDCNoticesHistory(getActivity(), meterReaderId);

            dcNoticeCards = new ArrayList<>();
            for (int i = 0; i < AppConstants.UPLOAD_HISTORY_DATE_COUNT; i++)
            {
                String date = CommonUtils.getPreviousDate(i);
                ArrayList<String> historyBinders = DatabaseManager.getDCNoticeHistoryBinders(mContext, date);
                if(historyBinders != null)
                {
                    for (int j = 0; j < historyBinders.size(); j++)
                    {
                        String binders = historyBinders.get(j);
                        ArrayList<DisconnectionHistory> dcNoticesHistory = DatabaseManager.getDCNoticesHistory(mContext,CommonUtils.getPreviousDate(i),binders,meterReaderId);
                        String billMonth = dcNoticesHistory.get(0).bill_month;

                        int totalDelivered = 0, totalNotDelivered = 0;
                        for (int k = 0; k < dcNoticesHistory.size(); k++)
                        {
                            if(dcNoticesHistory.get(k).delivery_status.equalsIgnoreCase(getString(R.string.delivered)))
                            {
                                totalDelivered += 1;
                            }
                            else if(dcNoticesHistory.get(k).delivery_status.equalsIgnoreCase(getString(R.string.not_delivered)))
                            {
                                totalNotDelivered += 1;
                            }
                        }

                        DisconnectionHistory disconnectionHistory = new DisconnectionHistory();
                        disconnectionHistory.date = date;
                        disconnectionHistory.binder_code = binders;
                        disconnectionHistory.bill_month = billMonth;
                        disconnectionHistory.total = ""+(totalDelivered + totalNotDelivered);
                        disconnectionHistory.totalDelivered = ""+totalDelivered;
                        disconnectionHistory.totalNotDDelivered = ""+totalNotDelivered;

                        dcNoticeCards.add(disconnectionHistory);
                    }
                }
            }
        }catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecyclerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
