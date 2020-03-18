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
import com.sgl.adapters.BillDistributionHistoryAdapter;
import com.sgl.db.DatabaseManager;
import com.sgl.models.UploadBillHistory;
import com.sgl.utils.App;

import java.util.ArrayList;

public class BillDistributionHistoryFragment extends Fragment
{

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private TextView lblBlankScreenMsg;

    public BillDistributionHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bill_distribution_history, container, false);
        //delete records not in scope
        DatabaseManager.deleteUploadsBillHistory(this.getContext(), LandingActivity.meter_reader_id);

        ArrayList<UploadBillHistory> uploadBillHistories = DatabaseManager.getHistoryBillCards(this.getContext(), LandingActivity.meter_reader_id);

        BillDistributionHistoryAdapter adapter = new BillDistributionHistoryAdapter(this.getContext(), uploadBillHistories);

        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(uploadBillHistories != null && uploadBillHistories.size() > 0)
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
