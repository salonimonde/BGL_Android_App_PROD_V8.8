package com.sgl.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sgl.R;
import com.sgl.activity.AddMeterReadingActivity;
import com.sgl.adapters.ConsumerItemAdapter;
import com.sgl.adapters.JobCardListAdapter;
import com.sgl.adapters.ReadingCardListAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.Consumer;
import com.sgl.models.JobCard;
import com.sgl.models.MeterReading;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;

import java.util.ArrayList;
import java.util.Collections;

public class LandingReadingsFragment extends Fragment implements ReadingCardListAdapter.OnJobCardClickListener {
    private static final String ARG_NORMAL_READINGS = "";
    private static final String ARG_REVISIT_READINGS = "";
    private static final String ARG_NEW_READINGS = "";

    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private static int normalReadings = 1, revisitReadings = 0, newReadings = 0;

    private UserProfile userProfile;
    private String meter_reader_id;

    private TextView lblBlankScreenMsg;

    public LandingReadingsFragment() {
        // Required empty public constructor
    }

    public static LandingReadingsFragment newInstance(int normal, int revisit, int newReading) {
        LandingReadingsFragment fragment = new LandingReadingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NORMAL_READINGS, normal);
        args.putInt(ARG_REVISIT_READINGS, revisit);
        args.putInt(ARG_NEW_READINGS, newReading);
        normalReadings = normal;
        revisitReadings = revisit;
        newReadings = newReading;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            normalReadings = getArguments().getInt(ARG_NORMAL_READINGS);
            revisitReadings = getArguments().getInt(ARG_REVISIT_READINGS);
            newReadings = getArguments().getInt(ARG_NEW_READINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_landing_readings, container, false);

        mContext = getActivity();
        getUserProfileDetails();
        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        setAdapters();
        return rootView;
    }

    private void setAdapters() {
        if (normalReadings == 1) {
            ArrayList<JobCard> mJobCards = DatabaseManager.getJobCards(mContext, meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, "False");
            ArrayList<MeterReading> mReadingCards = DatabaseManager.getMeterReading(mContext, meter_reader_id);

            if (mReadingCards == null && mJobCards == null) {
                mReadingCards = new ArrayList<>();
                mJobCards = new ArrayList<>();
            }
            Collections.reverse(mReadingCards);
//            Collections.reverse(mJobCards);

            ReadingCardListAdapter adapter = new ReadingCardListAdapter(mContext, mReadingCards, mJobCards, this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            if (mReadingCards != null && mReadingCards.size() > 0)
                lblBlankScreenMsg.setVisibility(View.GONE);
            else
                lblBlankScreenMsg.setVisibility(View.VISIBLE);
        } else if (revisitReadings == 1) {
            ArrayList<JobCard> mJobCards = DatabaseManager.getJobCards(mContext, meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, "True");
            ArrayList<MeterReading> mReadingCards = DatabaseManager.getMeterReading(mContext, meter_reader_id);
            if (mJobCards == null) {
                mJobCards = new ArrayList<>();
            }

            ReadingCardListAdapter adapter = new ReadingCardListAdapter(mContext, mReadingCards, mJobCards, this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            if (mJobCards != null && mJobCards.size() > 0)
                lblBlankScreenMsg.setVisibility(View.GONE);
            else
                lblBlankScreenMsg.setVisibility(View.VISIBLE);
        } else if (newReadings == 1) {
            ArrayList<Consumer> mConsumers = DatabaseManager.getUnBilledConsumerRecords(meter_reader_id);

            if (mConsumers == null) {
                mConsumers = new ArrayList<>();
            }

            ConsumerItemAdapter adapter = new ConsumerItemAdapter(mContext, mConsumers);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            if (mConsumers != null && mConsumers.size() > 0)
                lblBlankScreenMsg.setVisibility(View.GONE);
            else
                lblBlankScreenMsg.setVisibility(View.VISIBLE);
        }
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }
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
    public void onJobCardClick(MeterReading reading, JobCard jobCard) {
        App.ReadingTakenBy = getString(R.string.meter_reading_manual);
        if (reading.reader_status.equalsIgnoreCase(getString(R.string.door_lock))) {
            Intent i = new Intent(mContext, AddMeterReadingActivity.class);
            i.putExtra(AppConstants.CURRENT_JOB_CARD, jobCard);
            startActivity(i);
        }
    }

}
