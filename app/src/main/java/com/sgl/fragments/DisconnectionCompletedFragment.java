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
import com.sgl.adapters.DisconnectionCompletedAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.UploadDisconnectionNotices;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;

import java.util.ArrayList;

public class DisconnectionCompletedFragment extends Fragment 
{
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices;
    private String meterReaderId = "";

    private TextView lblBlankScreenMsg;

    public DisconnectionCompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        meterReaderId = AppPreferences.getInstance(mContext).getString(AppConstants.METER_READER_ID, AppConstants.BLANK_STRING);

        uploadDisconnectionNotices = DatabaseManager.getCompletedDCNoticesCards(getContext(), meterReaderId);

        DisconnectionCompletedAdapter adapter = new DisconnectionCompletedAdapter(this.getContext(), uploadDisconnectionNotices);
        
        View rootView = inflater.inflate(R.layout.fragment_disconnection_completed, container, false);
        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(uploadDisconnectionNotices != null && uploadDisconnectionNotices.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
