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
import com.sgl.activity.LandingActivity;
import com.sgl.adapters.SummaryCardAdapter;
import com.sgl.db.DatabaseManager;
import com.sgl.models.SummaryCard;
import com.sgl.utils.App;

import java.util.ArrayList;

public class  LandingSummaryFragment extends Fragment
{
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<SummaryCard> mSummaryCardsArray;

    private TextView lblBlankScreenMsg;

    public LandingSummaryFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_landing_summary, container, false);
        mContext = getActivity();
        loadRecyclerView(rootView);

        return rootView;
    }

    private void loadRecyclerView(View rootView)
    {

        Typeface reg = App.getSansationRegularFont();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        lblBlankScreenMsg.setTypeface(reg);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        mSummaryCardsArray = DatabaseManager.getSummaryCard(mContext, LandingActivity.meter_reader_id);
        SummaryCardAdapter adapter = new SummaryCardAdapter(mContext, mSummaryCardsArray);
        recyclerView.setAdapter(adapter);

        if(mSummaryCardsArray != null && mSummaryCardsArray.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}
