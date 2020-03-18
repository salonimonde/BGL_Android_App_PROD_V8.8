package com.sgl.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JobCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class BuildingNumberAdapter extends RecyclerView.Adapter<BuildingNumberAdapter.BuildingNumberHolder>
{

    private JobCardListAdapter.OnJobCardClickListener mListener;
    private Context context;
    private ArrayList<String> mJobCards;
    private JobCardListAdapter jobCardListAdapter;
    private ArrayList<JobCard> jobCards;
    private String meterReaderId = "", searchText = "";

    public BuildingNumberAdapter(Context context, ArrayList<String> jobCards, JobCardListAdapter.OnJobCardClickListener listener, String searchText, String meterReaderId)
    {
        this.context = context;
        this.mJobCards = jobCards;
        this.mListener = listener;
        this.searchText = searchText;
        this.meterReaderId = meterReaderId;
    }

    @Override
    public BuildingNumberAdapter.BuildingNumberHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_building_number, null);
        BuildingNumberAdapter.BuildingNumberHolder viewHolder = new BuildingNumberAdapter.BuildingNumberHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BuildingNumberAdapter.BuildingNumberHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, context);
        Typeface bold = App.getSansationBoldFont();

        holder.txtBuildingNumber.setTypeface(bold);
        holder.txtBuildingNumber.setText(mJobCards.get(position));

        String building = mJobCards.get(position), street, buildingNo;
        int posOfOpenBracket = building.indexOf("(");
        int posOfCloseBracket = building.lastIndexOf(")");
        street = building.substring(0, posOfOpenBracket);
        buildingNo = building.substring(posOfOpenBracket + 1, posOfCloseBracket);

        jobCards = DatabaseManager.getJobCardsSearchByAddress(context, searchText, meterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED, buildingNo, street);

        jobCardListAdapter = new JobCardListAdapter(context, jobCards, mListener, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(jobCardListAdapter);
    }

    @Override
    public int getItemCount()
    {
        if (mJobCards != null && mJobCards.size() > 0)
        {
            return mJobCards.size();
        }
        else
            return 0;
    }

    public void setJobCard(ArrayList<String> jobCards)
    {
        mJobCards = jobCards;
        notifyDataSetChanged();
    }

    public class BuildingNumberHolder extends RecyclerView.ViewHolder
    {

        public TextView txtBuildingNumber;
        public RecyclerView recyclerView;

        public BuildingNumberHolder(View itemView)
        {
            super(itemView);

            txtBuildingNumber = itemView.findViewById(R.id.txt_building_no);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }
    }
}