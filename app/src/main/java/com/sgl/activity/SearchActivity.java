package com.sgl.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sgl.R;
import com.sgl.adapters.JobCardListAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JobCard;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;

import java.util.ArrayList;

public class SearchActivity extends ParentActivity implements View.OnClickListener, JobCardListAdapter.OnJobCardClickListener
{

    private Context mContext;
    private ImageView imgBack, imgSearch, imgKeyboard;
    private EditText edtSearch;
    private RecyclerView relativeSearchedJobCards;
    private ArrayList<JobCard> mJobCards = new ArrayList<JobCard>();
    private JobCardListAdapter jobCardListAdapter;
    private TextView txtResultFound, txtTitle;
    private String meter_reader_id;
    public static SearchActivity searchActivity;
    private Typeface regular;
    private String searchQuery = "", position = "", binder = "";
    private SearchView searchView;
    private Toolbar mToolbar;
    private Spinner spinnerBinder;
    private ArrayAdapter<String> dataAdapter;
    private ArrayList<String> routes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        regular = App.getSansationRegularFont();
        searchActivity = this;

        Intent i = getIntent();
        if (i != null)
        {
            meter_reader_id = i.getStringExtra(AppConstants.CURRENT_METER_READER_ID);
        }

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        txtTitle = findViewById(R.id.title_bar);
        txtTitle.setTypeface(regular);
        imgSearch = findViewById(R.id.img_search);
        imgSearch.setOnClickListener(this);
        edtSearch = findViewById(R.id.edt_search);
        edtSearch.setTypeface(regular);
        txtResultFound = findViewById(R.id.txt_result_found);
        txtResultFound.setText(R.string.search_text_no);
        imgKeyboard = findViewById(R.id.img_keyboard);
        imgKeyboard.setOnClickListener(this);
        imgKeyboard.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
        txtResultFound.setTypeface(regular);
        relativeSearchedJobCards = findViewById(R.id.rv_searched_job_cards);
        jobCardListAdapter = new JobCardListAdapter(mContext, mJobCards, this, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        relativeSearchedJobCards.setLayoutManager(layoutManager);
        relativeSearchedJobCards.setAdapter(jobCardListAdapter);

        spinnerBinder = findViewById(R.id.spinner_binder);

        position = AppPreferences.getInstance(mContext).getString(AppConstants.TOTAL, AppConstants.BLANK_STRING);
        binder = AppPreferences.getInstance(mContext).getString(AppConstants.FILTER_BINDER, AppConstants.BLANK_STRING);
        final Typeface bold = App.getSansationBoldFont();
        routes = new ArrayList<>();
        routes.add("All");
        routes.addAll(DatabaseManager.getRoutes(mContext, meter_reader_id));
        setAdapter(routes);

        dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, routes) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBinder.setAdapter(dataAdapter);
        if(!position.equalsIgnoreCase(AppConstants.BLANK_STRING))
        spinnerBinder.setSelection(Integer.parseInt(position));
        spinnerBinder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position1, long id)
            {
                binder = String.valueOf(parentView.getItemAtPosition(position1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {}

        });
    }

    @Override
    protected void onResume()
    {
        invalidateOptionsMenu();
        if(mJobCards!=null)
            mJobCards.clear();
        txtResultFound.setText(R.string.search_text_no);
        imgKeyboard.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
        txtResultFound.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onClick(View v)
    {
        if (v == imgBack)
        {
            finish();
        }
        if (v == imgKeyboard)
        {
            edtSearch.setText("");
        }
        if(searchView.getInputType()==InputType.TYPE_CLASS_PHONE)
        {
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);
            txtResultFound.setText(R.string.search_text_name);
            imgKeyboard.setImageDrawable(getResources().getDrawable(R.drawable.keypad));
        }
        else
        {
            searchView.setInputType(InputType.TYPE_CLASS_PHONE);
            txtResultFound.setText(R.string.search_text_no);
            imgKeyboard.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onJobCardClick(JobCard jobCard)
    {
        App.ReadingTakenBy = getString(R.string.meter_reading_manual);
        edtSearch.setText(" ");
        Intent i = new Intent(mContext, AddMeterReadingActivity.class);
        i.putExtra(AppConstants.CURRENT_JOB_CARD, jobCard);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.LEFT));
        searchView.onActionViewExpanded();
        searchView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchView.setQuery(searchQuery, false);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(final String newText)
            {   jobCardListAdapter=null;
                if(newText.length() >= 1)
                {
                    if(mJobCards != null)
                    {
                        mJobCards.clear();
                    }

//                    if(binder.equals(getString(R.string.all)))
//                        mJobCards = DatabaseManager.getJobCardsBySearch(mContext, newText, meter_reader_id, AppConstants.JOB_CARD_STATUS_ALLOCATED);
//                    else
//                        mJobCards = DatabaseManager.getJobCardsBySearch(mContext, newText, meter_reader_id, AppConstants.JOB_CARD_STATUS_ALLOCATED, binder);

                    if(jobCardListAdapter != null)
                    {
//                        jobCardListAdapter.setJobCard(mJobCards);
                    }
                    else
                    {
                        jobCardListAdapter = new JobCardListAdapter(mContext, mJobCards, SearchActivity.this, false);
                        relativeSearchedJobCards.setAdapter(jobCardListAdapter);
                    }
                    txtResultFound.setTypeface(regular);
                    if(mJobCards == null)
                    {
                        jobCardListAdapter.notifyDataSetChanged();
                        txtResultFound.setText(R.string.No_Consumer_found );
                    }
                    else
                    {
                        if(mJobCards.size() == 1)
                        {
                            txtResultFound.setText(mJobCards.size() + " " + getString(R.string.record_found));
                            txtResultFound.setGravity(Gravity.LEFT);
                        }
                        else
                        {
                            txtResultFound.setText(mJobCards.size() + " " + getString(R.string.records_found));
                            txtResultFound.setGravity(Gravity.LEFT);
                        }
                    }
                }
                else
                {
                    if(mJobCards != null)
                    {
                        mJobCards.clear();
                    }
                    if(jobCardListAdapter != null)
                    {
//                        jobCardListAdapter.setJobCard(mJobCards);
                    }
                    else
                    {
                        jobCardListAdapter = new JobCardListAdapter(mContext, mJobCards, SearchActivity.this, false);
                        relativeSearchedJobCards.setAdapter(jobCardListAdapter);
                    }
                    txtResultFound.setText(R.string.search_text_no);
                    imgKeyboard.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
                    txtResultFound.setGravity(Gravity.CENTER);
                }
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return true;
            }
        });
        return true;
    }
    //For search menu on toolbar ends, Piyush : 06-03-17

    private void setAdapter(ArrayList<String> binders){
        final Typeface bold = App.getSansationBoldFont();

    }
}
