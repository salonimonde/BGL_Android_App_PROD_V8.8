package com.sgl.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.activity.AddMeterReadingActivity;
import com.sgl.adapters.JobCardListAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JobCard;
import com.sgl.utils.App;

import java.util.ArrayList;

public class LandingTodayFragment extends Fragment implements JobCardListAdapter.OnJobCardClickListener {
    private static final String ARG_IS_REVISIT = "is_revisit";
    private static final String ARG_READER_ID = "meter_reader_id";
    private static final String ARG_IS_PENDING = "is_pending";
    private static final String ARG_IS_FILTER = "is_filter";

    private static String mMeterReaderId;
    private static String isRevisited;
    private static String isPending = "False";
    private static String isFilterApply = "False";

    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<JobCard> mJobCards;
    private TextView lblBlankScreenMsg;

    public LandingTodayFragment() {
        // Required empty public constructor
    }

    public static LandingTodayFragment newInstance(String meter_reader_id, String is_revisit, String is_Pending, String isFilter) {
        LandingTodayFragment fragment = new LandingTodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_READER_ID, meter_reader_id);
        args.putString(ARG_IS_REVISIT, is_revisit);
        args.putString(ARG_IS_PENDING, isPending);
        args.putString(ARG_IS_FILTER, isFilter);
        mMeterReaderId = meter_reader_id;
        isRevisited = is_revisit;
        isPending = is_Pending;
        isFilterApply = isFilter;
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMeterReaderId = getArguments().getString(ARG_READER_ID);
            isRevisited = getArguments().getString(ARG_IS_REVISIT);
            isPending = getArguments().getString(ARG_IS_PENDING);
            isFilterApply = getArguments().getString(ARG_IS_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_landing_today, container, false);
        mContext = getActivity();
        if (isPending.equals(getString(R.string.False))) {
            if (isFilterApply.equals(getString(R.string.all))) {

                mJobCards = DatabaseManager.getJobCards(this.getActivity(), mMeterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED, isRevisited);

            } else {
                mJobCards = DatabaseManager.getJobCardsFilter(mMeterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED, isRevisited, isFilterApply);
            }
        } else {

            mJobCards = DatabaseManager.getJobCards(mMeterReaderId, AppConstants.JOB_CARD_STATUS_COMPLETED);
        }
        JobCardListAdapter adapter = new JobCardListAdapter(mContext, mJobCards, this, false);
        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);

        if (mJobCards != null && mJobCards.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onJobCardClick(JobCard jobCard) {
        App.ReadingTakenBy = getString(R.string.meter_reading_manual);
        Intent i = new Intent(mContext, AddMeterReadingActivity.class);
        i.putExtra(AppConstants.CURRENT_JOB_CARD, jobCard);
        startActivity(i);
    }

}