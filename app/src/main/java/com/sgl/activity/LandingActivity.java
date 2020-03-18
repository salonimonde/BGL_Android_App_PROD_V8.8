package com.sgl.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.sgl.R;
import com.sgl.adapters.ViewPagerAdapter;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.db.tables.JobCardTable;
import com.sgl.fragments.LandingHistoryFragment;
import com.sgl.fragments.LandingReadingsFragment;
import com.sgl.fragments.LandingSummaryFragment;
import com.sgl.fragments.LandingTodayFragment;
import com.sgl.models.Consumer;
import com.sgl.models.HistoryCard;
import com.sgl.models.JobCard;
import com.sgl.models.JsonResponse;
import com.sgl.models.MeterReading;
import com.sgl.models.PendingCount;
import com.sgl.models.Sequence;
import com.sgl.models.SummaryCard;
import com.sgl.models.SummaryCount;
import com.sgl.models.UploadsHistory;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.utils.LocationManagerReceiver;
import com.sgl.webservice.WebRequests;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingActivity extends ParentActivity implements View.OnClickListener, ServiceCaller, NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext = this;
    private Toolbar mToolbar;
    private Button openCountButton, revisitCountButton, pendingUploadButton, totalButton, btnRoute, btnTotal, btnOpen,
            btnCompleted, btnHistory, btnRevisitHistory, btnUnBillHistory;
    private Button normalPendingReadings, revisitPendingReadings, unBillPendingReadings, totalPendingReadings;
    private ImageButton btnSearch, btnScanQrCode, btnAddNewConsumer;
    private LinearLayout toolbarLinearProfile;
    private int mPageNumber = 1;
    private ArrayList<MeterReading> readingToUpload;
    private ProgressDialog pDialog;
    public static String meter_reader_id;
    private ArrayList<Consumer> unBillConsumerToUpload;
    private UserProfile userProfile;
    private ViewPagerAdapter adapter;
    private TextView txtName, total, open, pending, revisit, pendingOpen, pendingTotal, pendingRoutes, pendingCompleted,
            historyOpen, historyRevisit, historyUnBill, txtReadingTotal, txtReadingNormal, txtReadingRevisit, txtReadingNew,
            txtDrawerMRName, txtDrawerMobileNo;
    private CircleImageView imgMrProfile, imgDrawerProfile;
    private SummaryCount summaryCount;
    private Typeface regular, bold;
    private ArrayList<MeterReading> deleteJobs;
    private FrameLayout summary, history, today, reading;
    private int isReadings = 0;
    private Menu menu;
    private JobCard mjobcard;
    private ArrayList<String> routes;
    private ArrayList<SummaryCard> mSummaryCardsArray;
    private int position = 0, versionCode;
    public static int checkIt = 0;
    private String binder;
    public static Activity context;
    private Spinner spinnerBinder;
    private ArrayAdapter<String> dataAdapter;

    private DrawerLayout drawer;
    private Intent intent;
    private Timer buttonTimer;
    private String revisitFlag = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;
    private LocationManagerReceiver mLocationManagerReceiver;
    private static boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mContext = this;
        context = this;

        AppPreferences.getInstance(mContext).putString(AppConstants.SCREEN_FROM_EXIT, getString(R.string.landing_screen));
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        binder = getString(R.string.all);
        getUserProfileDetails();
        imgMrProfile = findViewById(R.id.image_profile);
        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
        }

        /*Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("Activity") != null) {
                revisitFlag = intent.getStringExtra("IS_REVISIT");
            }
        }*/

        if (App.welcome == true) {
            TextView title = new TextView(mContext);
            title.setText("Due Alert");
            title.setGravity(Gravity.CENTER);
            title.setTextSize(30);
            DialogCreator.showMessageDialog(mContext, getString(R.string.welcome_you_have_successfully_login), getString(R.string.success));
            App.welcome = false;
        }
        summaryCount = DatabaseManager.getSummary(meter_reader_id);
        if (summaryCount.pendingUpload == 0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), AppConstants.ZERO);
                versionCode = pInfo.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*if (Integer.parseInt(userProfile.app_version) != versionCode && Integer.parseInt(userProfile.app_version) > versionCode) {
                if (userProfile.app_link != null)
                    DialogCreator.showUpdateDialog(mContext, getString(R.string.alert_app_ready_to_update), userProfile.app_link);
            }*/
        }
        initProgressDialog();
        today = findViewById(R.id.submenu_today_container);
        reading = findViewById(R.id.submenu_reading_upload_container);
        summary = findViewById(R.id.submenu_summary_container);
        summary.setVisibility(View.GONE);
        history = findViewById(R.id.submenu_history_container);
        history.setVisibility(View.GONE);
        toolbarLinearProfile = findViewById(R.id.linear_profile);
        toolbarLinearProfile.setOnClickListener(this);
        txtName = findViewById(R.id.txt_name);
        txtName.setTypeface(regular);
        txtName.setSelected(true);
        historyOpen = findViewById(R.id.tv_open_history);
        historyOpen.setTypeface(regular);
        historyRevisit = findViewById(R.id.tv_revisit_history);
        historyRevisit.setTypeface(regular);
        historyUnBill = findViewById(R.id.tv_unbill_history);
        historyUnBill.setTypeface(regular);
        btnHistory = findViewById(R.id.open_history);
        btnHistory.setTypeface(regular);
        btnRevisitHistory = findViewById(R.id.revisit_history);
        btnRevisitHistory.setTypeface(regular);
        btnUnBillHistory = findViewById(R.id.unbill_history);
        btnUnBillHistory.setTypeface(regular);
        pendingOpen = findViewById(R.id.tv_open_summary);
        pendingOpen.setTypeface(regular);
        pendingTotal = findViewById(R.id.tv_total_summary);
        pendingTotal.setTypeface(regular);
        pendingCompleted = findViewById(R.id.tv_completed_symmary);
        pendingCompleted.setTypeface(regular);
        pendingRoutes = findViewById(R.id.tv_routes);
        pendingRoutes.setTypeface(regular);
        txtReadingTotal = findViewById(R.id.txt_reading_total);
        txtReadingTotal.setTypeface(regular);
        txtReadingNormal = findViewById(R.id.txt_reading_normal);
        txtReadingNormal.setTypeface(regular);
        txtReadingRevisit = findViewById(R.id.txt_reading_revisit);
        txtReadingRevisit.setTypeface(regular);
        txtReadingNew = findViewById(R.id.txt_reading_new);
        txtReadingNew.setTypeface(regular);
        txtName.setText(userProfile.meter_reader_name);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setFontOnTabs();
        btnRoute = findViewById(R.id.routes);
        btnRoute.setTypeface(regular);
        btnTotal = findViewById(R.id.total_summary);
        btnTotal.setTypeface(regular);
        btnOpen = findViewById(R.id.open_summary);
        btnOpen.setTypeface(regular);
        btnCompleted = findViewById(R.id.completed);
        btnCompleted.setTypeface(regular);
        total = findViewById(R.id.total);
        total.setTypeface(regular);
        open = findViewById(R.id.open);
        open.setTypeface(regular);
        revisit = findViewById(R.id.revisit);
        revisit.setTypeface(regular);
        pending = findViewById(R.id.pending);
        pending.setTypeface(regular);
        totalButton = findViewById(R.id.tv_total);
        totalButton.setTypeface(regular);
        openCountButton = findViewById(R.id.tv_open);
        openCountButton.setTypeface(regular);
        revisitCountButton = findViewById(R.id.tv_revisit);
        revisitCountButton.setTypeface(regular);
        pendingUploadButton = findViewById(R.id.tv_pending_upload);
        pendingUploadButton.setTypeface(regular);
        btnSearch = findViewById(R.id.landing_search_action);
        btnSearch.setOnClickListener(this);
        btnAddNewConsumer = findViewById(R.id.landing_add_consumer_action);
        btnAddNewConsumer.setOnClickListener(this);
        btnScanQrCode = findViewById(R.id.landing_scan_qr_action);
        btnScanQrCode.setOnClickListener(this);
        openCountButton.setOnClickListener(this);
        revisitCountButton.setOnClickListener(this);
        pendingUploadButton.setOnClickListener(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        totalPendingReadings = findViewById(R.id.tv_total_pending_upload);
        totalPendingReadings.setTypeface(regular);
        normalPendingReadings = findViewById(R.id.tv_normal_Pending);
        normalPendingReadings.setTypeface(regular);
        revisitPendingReadings = findViewById(R.id.tv_revisit_pending);
        revisitPendingReadings.setTypeface(regular);
        unBillPendingReadings = findViewById(R.id.tv_Unbill_pending);
        unBillPendingReadings.setTypeface(regular);
        totalPendingReadings.setOnClickListener(this);
        normalPendingReadings.setOnClickListener(this);
        revisitPendingReadings.setOnClickListener(this);
        unBillPendingReadings.setOnClickListener(this);
        drawer = findViewById(R.id.drawer_layout);
        final RelativeLayout mainView = findViewById(R.id.mainView);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(-(slideOffset * drawerView.getWidth()));
            }
        };
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mToolbar.setNavigationIcon(null);

        NavigationView navigationView = findViewById(R.id.nav_view_right);
        View header = navigationView.getHeaderView(AppConstants.ZERO);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(AppConstants.ZERO).setChecked(true);

        txtDrawerMRName = header.findViewById(R.id.txt_drawer_mr_name);
        txtDrawerMRName.setTypeface(bold);
        txtDrawerMobileNo = header.findViewById(R.id.txt_drawer_mobile_no);
        txtDrawerMobileNo.setTypeface(regular);
        imgDrawerProfile = header.findViewById(R.id.img_drawer_profile);

        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        }
        txtDrawerMRName.setText(userProfile.email_id + " | " + userProfile.meter_reader_name);
        txtDrawerMobileNo.setText(userProfile.contact_no);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                /*invalidateOptionsMenu();*/
                if (viewPager.getCurrentItem() == 0) {
                    isReadings = 0;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                    today.setVisibility(View.VISIBLE);
                    reading.setVisibility(View.GONE);
                    summary.setVisibility(View.GONE);
                    history.setVisibility(View.GONE);
                    btnScanQrCode.setEnabled(true);
                } else if (viewPager.getCurrentItem() == 1) {
                    isReadings = 1;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.upload_to_the_cloud));
                    today.setVisibility(View.GONE);
                    reading.setVisibility(View.VISIBLE);
                    summary.setVisibility(View.GONE);
                    history.setVisibility(View.GONE);
                    btnScanQrCode.setEnabled(false);
                } else if (viewPager.getCurrentItem() == 2) {
                    isReadings = 0;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                    today.setVisibility(View.GONE);
                    reading.setVisibility(View.GONE);
                    summary.setVisibility(View.VISIBLE);
                    history.setVisibility(View.GONE);
                    btnScanQrCode.setEnabled(false);
                } else if (viewPager.getCurrentItem() == 3) {
                    isReadings = 0;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                    today.setVisibility(View.GONE);
                    reading.setVisibility(View.GONE);
                    summary.setVisibility(View.GONE);
                    history.setVisibility(View.VISIBLE);
                    btnScanQrCode.setEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
//                    today.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 2) {
//                    today.setVisibility(View.VISIBLE);
                } else {
                    today.setVisibility(View.VISIBLE);
                    summary.setVisibility(View.GONE);
                    history.setVisibility(View.GONE);
                    reading.setVisibility(View.GONE);
                }
                super.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
            }
        });

        RelativeLayout relativeNotSubscribed = findViewById(R.id.relative_not_subscribed);
        relativeNotSubscribed.setOnClickListener(null);
        if (AppConstants.IS_MR_SUBSCRIBED) {
            relativeNotSubscribed.setVisibility(View.GONE);
        } else {
            relativeNotSubscribed.setVisibility(View.VISIBLE);
        }
        ImageView imgHamburger = findViewById(R.id.img_hamburger);
        imgHamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(mContext).addApi(AppIndex.API).build();


        if (checkIt == 1) {
            reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
            openCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
            openCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
            revisitCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.black));
        } else {
            reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
        }

    }

    private void setFontOnTabs() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(AppConstants.ZERO);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(regular);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetSequences();
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        this.invalidateOptionsMenu();
        binder = AppPreferences.getInstance(mContext).getString(AppConstants.FILTER_BINDER, AppConstants.BLANK_STRING);
        if (checkIt == 1) {
            reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
        } else {
            reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
        }
        loadData();
        userProfile = DatabaseManager.getUserProfile(mContext, meter_reader_id);
        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        } else
            imgMrProfile.setImageResource(R.drawable.defaultprofile);
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
            AppPreferences.getInstance(mContext).putString(AppConstants.METER_READER_ID, userProfile.meter_reader_id);
        }
    }

    private void initProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(mContext);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void loadData() {
        setFontOnTabs();
        totalButton.setTypeface(regular);
        revisitCountButton.setTypeface(regular);
        openCountButton.setTypeface(regular);
        pendingUploadButton.setTypeface(regular);
        pendingUploadButton.setTypeface(regular);

        if (binder.equals(getString(R.string.all))) {
            summaryCount = DatabaseManager.getSummary(meter_reader_id);
            totalButton.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.total_jobs));
            openCountButton.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.open));
            revisitCountButton.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.revisit));
            pendingUploadButton.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.pendingUpload));

            if (summaryCount.open != 0) {
                routes = new ArrayList<>();
                routes.add(getString(R.string.all));
                routes.addAll(DatabaseManager.getRoutes(mContext, meter_reader_id));
                setAdapter(routes);
            } else if (summaryCount.revisit != 0) {
                routes = new ArrayList<>();
                routes.add(getString(R.string.all));
                routes.addAll(DatabaseManager.getRoutes(mContext, meter_reader_id));
                setAdapter(routes);
            }

        } else {
            try {
                mSummaryCardsArray = DatabaseManager.getSummaryCard(mContext, meter_reader_id, binder);
                routes = new ArrayList<>();
                routes.add(getString(R.string.all));
                routes.addAll(DatabaseManager.getRoutes(mContext, meter_reader_id));
                setAdapter(routes);

                if (position == 0) {
                    int pos = Integer.parseInt(AppPreferences.getInstance(mContext).getString(AppConstants.TOTAL, ""));
                    position = pos;
                }
                final SummaryCard item = mSummaryCardsArray.get(position - 1);
                totalButton.setText(MessageFormat.format(getString(R.string.pattern), item.total));
                openCountButton.setText(MessageFormat.format(getString(R.string.pattern), item.open));
                revisitCountButton.setText(MessageFormat.format(getString(R.string.pattern), item.revisit));
                pendingUploadButton.setText(MessageFormat.format(getString(R.string.pattern), item.completed));

            } catch (Exception e) {
                binder = getString(R.string.all);
                AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                position = 0;
                reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
            }
        }
        loadReadingsData();
        loadSummaryData();
        loadHistoryData();
    }

    private void loadHistoryData() {
        HistoryCard lHistoryCard = DatabaseManager.getUploadsHistoryCounts(mContext, meter_reader_id);
        btnHistory.setText(String.valueOf(lHistoryCard.open));
        btnRevisitHistory.setText(String.valueOf(lHistoryCard.revisit));
        btnUnBillHistory.setText(String.valueOf(lHistoryCard.unbill));
    }

    public void loadHistoryData(int pOpen, int pRevisit, int pUnbill) {
        btnHistory.setText(String.valueOf(pOpen));
        btnRevisitHistory.setText(String.valueOf(pRevisit));
        btnUnBillHistory.setText(String.valueOf(pUnbill));
    }

    private void loadSummaryData() {
        summaryCount = DatabaseManager.getSummary(meter_reader_id);
        PendingCount pendingCount = DatabaseManager.getPendingCount(meter_reader_id);
        ArrayList<String> lRoutesList = DatabaseManager.getTotalRoutes(mContext, meter_reader_id);
        int lRoutes;

        if (lRoutesList != null)
            lRoutes = lRoutesList.size();
        else
            lRoutes = 0;

        btnRoute.setText(MessageFormat.format(getString(R.string.pattern), lRoutes));
        btnTotal.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.total_jobs));
        btnOpen.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.open + summaryCount.revisit));
        btnCompleted.setText(MessageFormat.format(getString(R.string.pattern), pendingCount.normalPending + pendingCount.revisitPending));
    }

    private void loadReadingsData() {
        summaryCount = DatabaseManager.getSummary(meter_reader_id);
        PendingCount pendingCount = DatabaseManager.getPendingCount(meter_reader_id);
        totalPendingReadings.setTypeface(regular);
        totalPendingReadings.setText(MessageFormat.format(getString(R.string.pattern), summaryCount.pendingUpload));
        normalPendingReadings.setTypeface(regular);
        normalPendingReadings.setText(MessageFormat.format(getString(R.string.pattern), pendingCount.normalPending));
        revisitPendingReadings.setTypeface(regular);
        revisitPendingReadings.setText(MessageFormat.format(getString(R.string.pattern), pendingCount.revisitPending));
        unBillPendingReadings.setTypeface(regular);
        unBillPendingReadings.setText(MessageFormat.format(getString(R.string.pattern), pendingCount.unBillPending));
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        adapter.addFragment(LandingTodayFragment.newInstance(meter_reader_id, getString(R.string.False), getString(R.string.False), getString(R.string.False)), getString(R.string.today));
        adapter.addFragment(new LandingReadingsFragment(), getString(R.string.readings));
        adapter.addFragment(new LandingSummaryFragment(), getString(R.string.summary));
        adapter.addFragment(new LandingHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
    }

    private void reinitializeViewPager(String isRevisited, String showIsPending, String isFilterApply) {

        adapter = null;
        viewPager.removeAllViews();
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        adapter.addFragment(LandingTodayFragment.newInstance(meter_reader_id, isRevisited, showIsPending, isFilterApply), getString(R.string.today));
        adapter.addFragment(new LandingReadingsFragment(), getString(R.string.readings));
        adapter.addFragment(new LandingSummaryFragment(), getString(R.string.summary));
        adapter.addFragment(new LandingHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        loadData();
    }

    private void reinitializeViewPager1(int isNormal, int isRevisit, int isNewReading) {
        adapter = null;
        viewPager.removeAllViews();
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        adapter.addFragment(new LandingTodayFragment(), getString(R.string.today));
        adapter.addFragment(LandingReadingsFragment.newInstance(isNormal, isRevisit, isNewReading), getString(R.string.readings));
        adapter.addFragment(new LandingSummaryFragment(), getString(R.string.summary));
        adapter.addFragment(new LandingHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        loadData();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            DialogCreator.showExitDialog(this, getString(R.string.exit_app), getString(R.string.do_you_want_to_exit), getString(R.string.landing_screen));
        }
    }

    private void setOpenButtonColor() {
        // reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
        openCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
        openCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.black));
        revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
        revisitCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
        pendingUploadButton.setBackgroundResource(R.drawable.ripple_oval_black);
    }

    private void setRevisitButtonColor() {
        checkIt = 1;
//        reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
        openCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
        openCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
        revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
        revisitCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.black));
        pendingUploadButton.setBackgroundResource(R.drawable.ripple_oval_black);
    }

    @Override
    public void onClick(View v) {

        if (openCountButton == v) {
            checkIt = 0;
            reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
            openCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
            openCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.black));
            revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
            revisitCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            pendingUploadButton.setBackgroundResource(R.drawable.ripple_oval_black);

        }
        if (revisitCountButton == v) {
            checkIt = 1;
            reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
            openCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
            openCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
            revisitCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.black));
            pendingUploadButton.setBackgroundResource(R.drawable.ripple_oval_black);
        }
        if (v == btnSearch) {
            if (summaryCount != null && summaryCount.open > 0 || summaryCount.revisit > 0) {
                intent = new Intent(mContext, SearchStreetWiseActivity.class);
                intent.putExtra(AppConstants.CURRENT_METER_READER_ID, meter_reader_id);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, getString(R.string.please_update_consumer_data_before_starting_search), Toast.LENGTH_LONG).show();
            }

        }
        if (v == toolbarLinearProfile) {
            intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }
        if (v == btnScanQrCode) {
            if (summaryCount != null && summaryCount.open > 0 || summaryCount.revisit > 0) {
                if (routes.size() > 2) {
                    showFilterDialog(mContext);
                } else {
                    Toast.makeText(mContext, getString(R.string.only_one_binder_is_assign_to_you), Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(mContext, getString(R.string.please_update_consumer_data_before_start_filter), Toast.LENGTH_LONG).show();

        }
        if (v == btnAddNewConsumer) {
            if (summaryCount != null && summaryCount.open > 0 || summaryCount.revisit > 0) {
                App.ConsumerAddedBy = getString(R.string.meter_reading_manual);
                intent = new Intent(mContext, AddNewConsumerActivity.class);
                startActivity(intent);
            } else
                Toast.makeText(mContext, getString(R.string.please_update_consumer_data_before_adding_new_consumer), Toast.LENGTH_LONG).show();

        }
        //Code For Reading Tab starts, Piyush : 03-03-17
        if (v == normalPendingReadings) {
            reinitializeViewPager1(AppConstants.ONE, AppConstants.ZERO, AppConstants.ZERO);
            normalPendingReadings.setBackgroundResource(R.drawable.ripple_oval_red);
            normalPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.black));
            revisitPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            revisitPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            unBillPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            unBillPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
        }
        if (revisitPendingReadings == v) {
            reinitializeViewPager1(AppConstants.ZERO, AppConstants.ONE, AppConstants.ZERO);
            normalPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            normalPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            revisitPendingReadings.setBackgroundResource(R.drawable.ripple_oval_red);
            revisitPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.black));
            unBillPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            unBillPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
        }
        if (unBillPendingReadings == v) {
            reinitializeViewPager1(AppConstants.ZERO, AppConstants.ZERO, AppConstants.ONE);
            normalPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            normalPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            revisitPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            revisitPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
            unBillPendingReadings.setBackgroundResource(R.drawable.ripple_oval_red);
            unBillPendingReadings.setTextColor(CommonUtils.getColor(mContext, R.color.black));
        }
        //Code For Reading Tab starts, Piyush : 03-03-17
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_hamburger));
        //To show count of notification on icon starts, Piyush : 28-02-17
        Drawable dre = ContextCompat.getDrawable(mContext, R.drawable.notification_icon);
        int count = DatabaseManager.getCount(mContext, "false", meter_reader_id);
        if (count > 0)
            ActionItemBadge.update(this, menu.findItem(R.id.action_notifications), dre, ActionItemBadge.BadgeStyles.YELLOW, count);
        //To show count of notification on icon starts, Piyush : 29-02-17
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (isReadings == 0) {
                    show = true;
                    mPageNumber = 1;
                    getAllJobCards();
                } else {
                    doMeterUpload();
                }
                return true;
            case R.id.action_logout:
                drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
                return true;
            case R.id.action_notifications:
                callNotification();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meter_reading) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (id == R.id.nav_bill_distribution) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, BillDistributionLandingScreen.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_disconnection) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, DisconnectionNoticeLandingActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_payment) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, MyPaymentActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            drawer.closeDrawer(GravityCompat.END);
            performLogout();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void doMeterUpload() {
        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            readingToUpload = DatabaseManager.getMeterReadings(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
            if (readingToUpload != null && readingToUpload.size() > 0) {
                JSONObject object = getMeterReadingJson(readingToUpload);
                uploadMeterReading(object);
            } else {
                Toast.makeText(mContext, getString(R.string.no_readings_available_to_be_uploaded), Toast.LENGTH_LONG).show();
                uploadConsumerReading();
            }
        } else {
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }

    public JSONObject getMeterReadingJson(ArrayList<MeterReading> readings) {
        JSONObject jsonObject = null;
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(readings);
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObject = new JSONObject();
            jsonObject.put("city", userProfile.city);
            jsonObject.put("readings", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void uploadMeterReading(JSONObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your dialog code.
                if (pDialog != null) {
                    pDialog.setMessage(getString(R.string.uploading_meter_readings_please_wait));
                    pDialog.show();
                }
            }
        });
        JsonObjectRequest request = WebRequests.uploadMeterReading(object, Request.Method.POST, AppConstants.URL_UPLOAD_METER_READING, AppConstants.REQUEST_UPLOAD_METER_READING, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN), userProfile.city);
        App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_UPLOAD_METER_READING);
    }

    private void uploadConsumerReading() {
        unBillConsumerToUpload = DatabaseManager.getUnBillConsumers(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
        if (unBillConsumerToUpload != null && unBillConsumerToUpload.size() > 0) {
            JSONObject jObject = getUnbillMeterReadingJson(unBillConsumerToUpload);
            uploadConsumerReading(jObject);
        } else {
            Toast.makeText(mContext, getString(R.string.consumer_readings_are_not_available_for_upload), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadConsumerReading(JSONObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your dialog code.
                if (pDialog != null) {
                    pDialog.setMessage(getString(R.string.uploading_new_consumer_meter_readings_please_wait));
                    pDialog.show();
                }
            }
        });
        JsonObjectRequest request = WebRequests.uploadConsumerMeterReading(object, Request.Method.POST, AppConstants.URL_UPLOAD_UN_BILL_METER_READING, AppConstants.REQUEST_UPLOAD_UN_BILL_METER_READING, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
        App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_UPLOAD_UN_BILL_METER_READING);
    }

    private JSONObject getUnbillMeterReadingJson(ArrayList<Consumer> unBillConsumerToUpload) {
        JSONObject jsonObject = null;
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(unBillConsumerToUpload);
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObject = new JSONObject();
            jsonObject.put("city", userProfile.city);
            jsonObject.put("unbilled_consumers", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_sync);
        if (show) {
            item.setEnabled(false);
            item.getIcon().setAlpha(125);
            buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.setEnabled(true);
                            item.getIcon().setAlpha(255);
                            show = false;
                        }
                    });
                }
            }, 10000);
        }
        return true;
    }

    private void callNotification() {
//        mediaPlayer.start();
        intent = new Intent(mContext, NotificationActivity.class);
        intent.putExtra(AppConstants.CURRENT_METER_READER_ID, meter_reader_id);
        startActivity(intent);
    }

    private void getAllJobCards() {
        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            invalidateOptionsMenu();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Your dialog code.
                    if (pDialog != null) {
                        pDialog.setMessage(getString(R.string.updating_your_assigned_consumer_information_please_wait));
                        pDialog.show();
                    }
                }
            });

            JsonObjectRequest request = WebRequests.getJobCards(Request.Method.GET, AppConstants.URL_GET_JOB_CARDS, AppConstants.REQUEST_GET_JOB_CARDS, this, mPageNumber, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_JOB_CARDS);
        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }

    private void getDeassignedReassignedJobCards() {
        if (summaryCount.pendingUpload == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Your dialog code.
                    if (pDialog != null) {
                        pDialog.setMessage(getString(R.string.updating_your_deassign_consumer_data_please_wait));
                        pDialog.show();
                    }
                }
            });

            JsonObjectRequest request = WebRequests.getdeassignedreassignedjobcards(Request.Method.GET, AppConstants.URL_GET_DEASSIGNED_REASSIGNED_JOB_CARDS, AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS, this, mPageNumber, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS);
        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(mContext, getString(R.string.upload_before_download_error), getString(R.string.error));
        }
    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {

        switch (label) {
            case AppConstants.REQUEST_GET_JOB_CARDS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            dismissDialog();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });
                        } else {
                            if (jsonResponse.responsedata != null && jsonResponse.responsedata.jobcards != null && jsonResponse.responsedata.jobcards.size() > 0) {
                                DatabaseManager.saveJobCards(mContext, jsonResponse.responsedata.jobcards);
//                                reinitializeViewPager(revisitFlag, "False", binder);

                                if (jsonResponse.responsedata.is_next.equals("true")) {
                                    mPageNumber += 1;
                                    reinitializeViewPager("False", "False", binder);

//                                    reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
                                    position = 0;
                                    dismissDialog();
                                    getAllJobCards();

                                } else {
                                    mPageNumber = 1;
                                    //refresh summary on UI
                                    binder = getString(R.string.all);
                                    AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
//                                    reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
                                    revisitFlag = jsonResponse.responsedata.jobcards.get(0).is_revisit;
                                    if (checkIt == 1) {
                                        reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
                                    } else {
                                        reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
                                    }
                                    position = 0;
                                    dismissDialog();

                                    Toast.makeText(mContext, getString(R.string.consumer_information_downloaded), Toast.LENGTH_LONG).show();
                                    getDeassignedReassignedJobCards();
                                }
                            } else {
                                mPageNumber = 1;
                                Toast.makeText(mContext, getString(R.string.consumer_information_not_assigned_to_you_for_reading_as_of_now), Toast.LENGTH_LONG).show();

                                getDeassignedReassignedJobCards();
                            }
                            if (jsonResponse.authorization != null) {
                                CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                            }
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        Toast.makeText(mContext, R.string.please_contact_server_admin, Toast.LENGTH_LONG).show();
                        getDeassignedReassignedJobCards();
                    } else {
                        getDeassignedReassignedJobCards();
                    }
                } else
                    getDeassignedReassignedJobCards();
            }
            break;

            case AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });
                        }
                        if (jsonResponse.responsedata != null && jsonResponse.responsedata.re_de_assigned_jobcards != null && jsonResponse.responsedata.re_de_assigned_jobcards.size() > 0) {
                            DatabaseManager.handleAssignedDeassignedJobs(mContext, jsonResponse.responsedata.re_de_assigned_jobcards, meter_reader_id);

                            //refresh summary UI
                            binder = getString(R.string.all);

                            AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                            AppPreferences.getInstance(mContext).getString(AppConstants.TOTAL, "0");
                            reinitializeViewPager(revisitFlag, "False", binder);

//                            reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
                            if (jsonResponse.responsedata.is_next.equals("true")) {
                                mPageNumber += 1;
                                dismissDialog();
                                getDeassignedReassignedJobCards();
                            } else {
                                position = 0;
                                dismissDialog();
                                Toast.makeText(mContext, getString(R.string.updated_reassign_deassign_consumer_info_successfully), Toast.LENGTH_LONG).show();
                            }
                        }
                        if (jsonResponse.authorization != null) {
                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        resetSequences();
                        dismissDialog();
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                }
            }
            break;

            case AppConstants.REQUEST_UPLOAD_METER_READING: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.authorization != null) {
//                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        //changes for Delete Job cards Starts Avinesh
                        if (jsonResponse.responsedata.new_meter_readings != null && jsonResponse.responsedata.new_meter_readings.size() > 0) {
                            for (int i = 0; i < jsonResponse.responsedata.new_meter_readings.size(); i++) {
                                deleteJobs = DatabaseManager.getMeterReading(mContext, readingToUpload.get(i).meter_reader_id, jsonResponse.responsedata.new_meter_readings.get(i));
                                ArrayList<JobCard> lJobCardArray = DatabaseManager.getJobCard(readingToUpload.get(i).meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, jsonResponse.responsedata.new_meter_readings.get(i));
                                if (lJobCardArray != null) {
                                    if (!lJobCardArray.get(0).phone_no.equalsIgnoreCase("0") && lJobCardArray.get(0).phone_no.length() == 10)
                                        if ((readingToUpload.get(i).reader_status.equalsIgnoreCase(getString(R.string.door_lock)))) {
//                                            sentSMS(lJobCardArray.get(0), readingToUpload.get(i).reading_date);
                                            try {
                                                sendSMSForDoorLock(deleteJobs.get(0), lJobCardArray.get(0));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    UploadsHistory lUploadsHistory = new UploadsHistory();
                                    lUploadsHistory.consumer_no = lJobCardArray.get(0).consumer_no;
                                    lUploadsHistory.route_code = lJobCardArray.get(0).route_code;
                                    lUploadsHistory.bill_cycle_code = lJobCardArray.get(0).bill_cycle_code;
                                    lUploadsHistory.month = lJobCardArray.get(0).schedule_month;
                                    lUploadsHistory.meter_reader_id = lJobCardArray.get(0).meter_reader_id;
                                    if (readingToUpload.get(0).isRevisit.equalsIgnoreCase(getString(R.string.True))) {
                                        lUploadsHistory.upload_status = getString(R.string.revisit);
                                    } else {
                                        lUploadsHistory.upload_status = getString(R.string.meter_status_normal);
                                    }
                                    lUploadsHistory.reading_date = CommonUtils.getCurrentDate();

                                    DatabaseManager.saveUploadsHistory(mContext, lUploadsHistory);
                                }
                                DatabaseManager.deleteMeterReadings(mContext, deleteJobs);
                            }

                            //refresh summary UI
                            dismissDialog();
                            binder = getString(R.string.all);
                            AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                            loadData();
                            reinitializeViewPager1(AppConstants.ONE, AppConstants.ONE, AppConstants.ONE);

                            readingToUpload = null;
                            readingToUpload = DatabaseManager.getMeterReadings(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
                            if (readingToUpload != null && readingToUpload.size() > 0) {
                                JSONObject jObject = getMeterReadingJson(readingToUpload);
                                uploadMeterReading(jObject);
                            } else {
                                Toast.makeText(mContext, getString(R.string.consumer_readings_successfully_uploaded), Toast.LENGTH_LONG).show();
                                viewPager.setCurrentItem(0);
                                uploadConsumerReading();
                            }
                        }
                        //changes for Delete Job cards Ends Avinesh

                        else {
                            Toast.makeText(mContext, R.string.error_in_upload, Toast.LENGTH_LONG).show();
                            dismissDialog();
                            loadData();
                            reinitializeViewPager1(AppConstants.ONE, AppConstants.ONE, AppConstants.ONE);
                            uploadConsumerReading();
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        uploadConsumerReading();
                    } else {
                        uploadConsumerReading();
                    }
                } else {
                    uploadConsumerReading();
                }
            }
            break;

            case AppConstants.REQUEST_UPLOAD_UN_BILL_METER_READING: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.authorization != null) {
//                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        if (jsonResponse.responsedata.new_unbilled_consumers != null && jsonResponse.responsedata.new_unbilled_consumers.size() > 0) {
                            //changes for count in historyTab Starts Avinesh:01-03-17
                            for (int i = 0; i < jsonResponse.responsedata.new_unbilled_consumers.size(); i++) {
                                if (unBillConsumerToUpload.size() >= 0) {
                                    UploadsHistory lUploadsHistory = new UploadsHistory();
                                    lUploadsHistory.consumer_no = unBillConsumerToUpload.get(i).consumer_no;
                                    lUploadsHistory.route_code = unBillConsumerToUpload.get(i).route_code;
                                    lUploadsHistory.bill_cycle_code = unBillConsumerToUpload.get(i).bill_cycle_code;
                                    lUploadsHistory.month = unBillConsumerToUpload.get(i).reading_month;
                                    lUploadsHistory.upload_status = getString(R.string.addnewconsumer);
                                    lUploadsHistory.reading_date = CommonUtils.getCurrentDate();
                                    lUploadsHistory.meter_reader_id = unBillConsumerToUpload.get(i).meter_reader_id;

                                    DatabaseManager.saveUploadsHistory(mContext, lUploadsHistory);
                                }
                                DatabaseManager.deleteUnBillConsumer(mContext, jsonResponse.responsedata.new_unbilled_consumers.get(i), meter_reader_id);
                            }
                            //changes for count in historyTab Ends Avinesh:01-03-17

                            //refresh summary UI
                            dismissDialog();
                            loadData();
                            reinitializeViewPager1(AppConstants.ONE, AppConstants.ONE, AppConstants.ONE);

                            unBillConsumerToUpload = null;
                            unBillConsumerToUpload = DatabaseManager.getUnBillConsumers(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
                            if (unBillConsumerToUpload != null && unBillConsumerToUpload.size() > 0) {
                                JSONObject jObject = getUnbillMeterReadingJson(unBillConsumerToUpload);
                                uploadConsumerReading(jObject);
                            } else {
                                mPageNumber = 1;
                            }
                        }
                        dismissDialog();
                        loadData();
                        reinitializeViewPager1(AppConstants.ONE, AppConstants.ONE, AppConstants.ONE);
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                }
            }
            break;
        }
    }

    private void resetSequences() {
        ArrayList<Sequence> se = DatabaseManager.getAllSequence(mContext, meter_reader_id);
        if (se != null)
            for (Sequence s : se) {
                if ((DatabaseManager.getJobCardsCount(mContext, meter_reader_id, s.route_code)) != null) {
                    if ((DatabaseManager.getJobCardsCount(mContext, meter_reader_id, s.route_code)).size() == 0) {
                        DatabaseManager.deleteSequence(mContext, s);
                    }
                } else
                    DatabaseManager.deleteSequence(mContext, s);
            }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_GET_JOB_CARDS: {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                getDeassignedReassignedJobCards();
            }
            break;
            case AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS: {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
            default:
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                break;
        }
        dismissDialog();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        if (buttonTimer != null)
            buttonTimer.cancel();
        CommonUtils.deleteCache(mContext);
        super.onDestroy();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Landing Page") // TODO: Define a title for the content shown.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public static void landingFinish(Activity context) {
        context.finish();
    }

    //Dialog for validation Dialog start Piyush : 01-04-17
    public void showFilterDialog(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        final Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final AlertDialog alert2;
        View promptView = layoutInflater.inflate(R.layout.filter_dialog, null);

        final TextView txtError;
        final Button positive, negative;
        //Initialising all fields starts
        alert2 = new AlertDialog.Builder(mContext).create();
        spinnerBinder = promptView.findViewById(R.id.spinner_binder);
        positive = promptView.findViewById(R.id.btn_yes);
        negative = promptView.findViewById(R.id.btn_no);
        txtError = promptView.findViewById(R.id.txt_error);
        //Initialising all fields ends

        //Setting font style to all fields starts
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        txtError.setTypeface(regular);
        //Setting font style to all fields ends

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
        spinnerBinder.setSelection(position);
        spinnerBinder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position1, long id) {
                position = position1;
                AppPreferences.getInstance(mContext).putString(AppConstants.TOTAL, String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (binder.equals("Select Binder*")) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                    binder = spinnerBinder.getItemAtPosition(position).toString();
                    if (checkIt == 1) {
                        reinitializeViewPager(getString(R.string.True), getString(R.string.False), binder);
                       /* openCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
                        openCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
                        revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
                        revisitCountButton.setTextColor(CommonUtils.getColor(mContext, R.color.black));
*/
                    } else {
                        reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
                    }
                    AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                    //reinitializeViewPager(getString(R.string.False), getString(R.string.False), binder);
                    alert2.dismiss();
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtError.setVisibility(View.GONE);
                alert2.dismiss();
            }
        });

        alert2.setView(promptView);
        alert2.setCancelable(false);
        alert2.show();
        //OK button code ends>
    }
    //Dialog for validation Dialog ends Piyush : 20-05-17

    private void setAdapter(ArrayList<String> binders) {
        final Typeface bold = App.getSansationBoldFont();
    }

    private void sentSMS(final JobCard jobCard, String date) {
        String tag_json_obj = "json_obj_req";
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String url = null;
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        url = "http://www.cescrajasthan.co.in/kotawebservice/send_sms_thru_api.jsp?kno=" + jobCard.consumer_no +
                "&mobno=" + /*"9595092582"*/jobCard.phone_no + "&message=X&lang_type=H&msg_type=H1&crt_by=BYNRY&rdng_dt=X&rdng=X&adv=X&agency_contact_no=" + userProfile.contact_no;

        final ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
//                        if (jsonResponse.SMSSENT != null)
//                            Log.i("SMSSSSSSSSSSSSSSS  ", jobCard.consumer_no + "   ||||   " + jsonResponse.SMSSENT.toString());
//                        else if (jsonResponse.SMSERROR != null)
//                            Log.i("SMSSSSSSSSSSSSSSS  ", jobCard.consumer_no + "   ||||   " + jsonResponse.SMSERROR.toString());

                        pDialog.hide();
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
                pDialog.dismiss();
            }
        });

// Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void performLogout() {
        DialogCreator.showLogoutDialog(this, getString(R.string.logout), getString(R.string.logout_string));
    }

    private void sendSMSForDoorLock(MeterReading meterReading, JobCard jobCard) throws JSONException {

        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            JSONObject object = getMeterReadingJsonSendSMS(meterReading, jobCard);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Your dialog code.
                    if (pDialog != null) {
                        pDialog.setMessage(getString(R.string.getting_spot_bill_data_please_wait));
                        pDialog.show();
                    }
                }
            });

            JsonObjectRequest request = WebRequests.callPostMethod(object, Request.Method.POST, AppConstants.URL_SEND_SMS_DOOR_LOCK, AppConstants.REQUEST_SEND_SMS_DOOR_LOCK, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_SEND_SMS_DOOR_LOCK);

        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.spot_bill_dialog));
        }
    }

    public JSONObject getMeterReadingJsonSendSMS(MeterReading meterReading, JobCard jobCard) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(getString(R.string.meter_reader_name), userProfile.meter_reader_name);
            jsonObject.put(getString(R.string.reading_date_api), meterReading.reading_date);
            jsonObject.put(getString(R.string.reader_status_api), meterReading.reader_status);
            jsonObject.put(getString(R.string.meter_status_api), meterReading.meter_status);
            jsonObject.put(getString(R.string.meter_no_api), meterReading.meter_no);
            jsonObject.put(getString(R.string.consumer_no_api), jobCard.consumer_no);
            jsonObject.put(getString(R.string.mobile_no), jobCard.phone_no);
            jsonObject.put(getString(R.string.meter_reading_api), meterReading.current_meter_reading);
//            jsonObject.put(getString(R.string.mobile_no), "8766491254");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}