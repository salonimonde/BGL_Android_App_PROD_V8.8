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
import com.sgl.adapters.DisconnectionOpenAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.Disconnection;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;

import java.util.ArrayList;

public class DisconnectionOpenFragment extends Fragment
{
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Disconnection> disconnections;
    private String meterReaderId = "";

    private TextView lblBlankScreenMsg;

    public DisconnectionOpenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContext = getActivity();
        meterReaderId = AppPreferences.getInstance(mContext).getString(AppConstants.METER_READER_ID, "");

        disconnections = DatabaseManager.getDisconnectionOpenCards(this.getContext(), meterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED);

        DisconnectionOpenAdapter adapter = new DisconnectionOpenAdapter(this.getContext(), disconnections);

        View rootView =  inflater.inflate(R.layout.fragment_disconnection_open, container, false);

        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(disconnections != null && disconnections.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        return rootView;
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
