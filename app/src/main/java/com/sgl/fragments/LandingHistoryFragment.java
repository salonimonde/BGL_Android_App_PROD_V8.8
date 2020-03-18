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
import com.sgl.adapters.HistoryCardAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.HistoryCard;
import com.sgl.models.UploadsHistory;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Bynry01 on 10/14/2016.
 */

public class LandingHistoryFragment extends Fragment {

    private String meter_reader_id;
    Context mContext;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    private View mRootView;
    private UserProfile userProfile;

    private ArrayList<HistoryCard> mHistoryCardsArray;

    private TextView lblBlankScreenMsg;

    public LandingHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_landing_history, container, false);
        mContext = getActivity();
        getUserProfileDetails();
        loadRecyclerView();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRootView != null)
            loadRecyclerView();
    }

    private void loadRecyclerView() {
        lblBlankScreenMsg = mRootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = mRootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getHistoryDatesArray();

        HistoryCardAdapter adapter = new HistoryCardAdapter(mContext, mHistoryCardsArray);
        recyclerView.setAdapter(adapter);

        if (mHistoryCardsArray != null && mHistoryCardsArray.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

    private void getHistoryDatesArray() {
        try {
            //delete old records which are not in range of days.
            DatabaseManager.deleteUploadsHistory(getActivity(), meter_reader_id);

            int lTotalRevisit = 0;
            int lTotalNormal = 0;
            int lTotalNewConsumer = 0;
            mHistoryCardsArray = new ArrayList<>();
            for (int i = 0; i < AppConstants.UPLOAD_HISTORY_DATE_COUNT; i++) {
                String date = CommonUtils.getPreviousDate(i);
                ArrayList<String> uploadsHistoryRoutes = DatabaseManager.getUploadsHistoryRoutes(mContext, date);
                if (uploadsHistoryRoutes != null) {
                    for (int j = 0; j < uploadsHistoryRoutes.size(); j++) {
                        String route = uploadsHistoryRoutes.get(j);
                        ArrayList<UploadsHistory> uploadsHistory = DatabaseManager.getUploadsHistory(mContext, CommonUtils.getPreviousDate(i), route, meter_reader_id);
                        String billCycle = uploadsHistory.get(0).bill_cycle_code;

                        int revisit = 0;
                        int normal = 0;
                        int newConsumer = 0;
                        for (int i2 = 0; i2 < uploadsHistory.size(); i2++) {
                            if (uploadsHistory.get(i2).upload_status.equalsIgnoreCase(getString(R.string.addnewconsumer))) {
                                newConsumer += 1;
                                lTotalNewConsumer += 1;
                            } else if (uploadsHistory.get(i2).upload_status.equalsIgnoreCase(getString(R.string.meter_status_normal))) {
                                normal += 1;
                                lTotalNormal += 1;
                            } else if (uploadsHistory.get(i2).upload_status.equalsIgnoreCase(getString(R.string.revisit))) {
                                revisit += 1;
                                lTotalRevisit += 1;
                            }
                        }
                        HistoryCard historyCard = new HistoryCard();
                        historyCard.date = date;
                        historyCard.route = route;
                        historyCard.open = normal;
                        historyCard.unbill = newConsumer;
                        historyCard.revisit = revisit;
                        historyCard.billcycle = billCycle;

                        mHistoryCardsArray.add(historyCard);

                        ((LandingActivity) getActivity()).loadHistoryData(lTotalNormal, lTotalRevisit, lTotalNewConsumer);
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}