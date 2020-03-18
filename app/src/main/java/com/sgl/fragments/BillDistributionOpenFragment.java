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

import com.sgl.R;
import com.sgl.activity.BillDistributionDetailActivity;
import com.sgl.activity.LandingActivity;
import com.sgl.adapters.BillDistributionOpenAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.BillCard;
import com.sgl.utils.App;

import java.util.ArrayList;

public class BillDistributionOpenFragment extends Fragment implements BillDistributionOpenAdapter.OnBillCardClickListener
{

    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<BillCard> mBillCards;
    private TextView lblBlankScreenMsg;

    public BillDistributionOpenFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        mBillCards = DatabaseManager.getBillCards(this.getContext(), LandingActivity.meter_reader_id, AppConstants.BILL_CARD_STATUS_ALLOCATED);

        BillDistributionOpenAdapter adapter = new BillDistributionOpenAdapter(this.getContext(), mBillCards, this);

        View rootView = inflater.inflate(R.layout.fragment_bill_distrubution_open, container, false);
        mContext = getActivity();
        lblBlankScreenMsg = rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(mBillCards != null && mBillCards.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        return rootView;
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

    @Override
    public void onBillCardClick(BillCard billCard)
    {
        Intent intent = new Intent(mContext, BillDistributionDetailActivity.class);
        intent.putExtra(AppConstants.CURRENT_Bill_CARD, billCard);
        startActivity(intent);
    }
}