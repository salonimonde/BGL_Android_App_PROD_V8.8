package com.sgl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sgl.R;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.fragments.DisconnectionCompletedFragment;
import com.sgl.fragments.DisconnectionHistoryFragment;
import com.sgl.fragments.DisconnectionOpenFragment;
import com.sgl.models.DisconnectionHistory;
import com.sgl.models.JsonResponse;
import com.sgl.models.UploadDisconnectionNotices;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.webservice.WebRequests;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisconnectionNoticeLandingActivity extends ParentActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        ServiceCaller
{

    private Context mContext;
    public static Activity context;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private Intent intent;
    private CircleImageView imgMrProfile, imgDrawerProfile;
    private TextView txtName, txtDrawerMRName, txtDrawerMobileNo;
    private LinearLayout toolbarLinearProfile;
    private UserProfile userProfile;
    public static String meter_reader_id;
    private Typeface regular, bold;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Menu menu;
    private int onCompleted = 0;
    private static boolean show = false;
    private ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices;
    private Timer buttonTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnection_notice_landing);
        mContext = this;
        context = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppPreferences.getInstance(mContext).putString(AppConstants.SCREEN_FROM_EXIT, getString(R.string.disconnection));
        getUserProfileDetails();

        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();

        imgMrProfile = findViewById(R.id.image_profile);
        txtName = findViewById(R.id.txt_name);
        txtName.setTypeface(regular);
        txtName.setSelected(true);
        txtName.setText(userProfile.meter_reader_name);

        toolbarLinearProfile = findViewById(R.id.linear_profile);
        toolbarLinearProfile.setOnClickListener(this);

        drawer = findViewById(R.id.drawer_layout);
        final RelativeLayout mainView = findViewById(R.id.mainView);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        {
            public void onDrawerClosed(View view)
            {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(-(slideOffset * drawerView.getWidth()));
            }
        };
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        toolbar.setNavigationIcon(null);

        NavigationView navigationView = findViewById(R.id.nav_view_right);
        View header = navigationView.getHeaderView(AppConstants.ZERO);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        txtDrawerMRName = header.findViewById(R.id.txt_drawer_mr_name);
        txtDrawerMRName.setTypeface(bold);
        txtDrawerMRName.setText(userProfile.email_id + " | " + userProfile.meter_reader_name);
        txtDrawerMobileNo = header.findViewById(R.id.txt_drawer_mobile_no);
        txtDrawerMobileNo.setTypeface(regular);
        txtDrawerMobileNo.setText(userProfile.contact_no);
        imgDrawerProfile = header.findViewById(R.id.img_drawer_profile);

        if (userProfile.profile_image != null && !userProfile.profile_image.equals("") )
        {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL+userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        }

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(viewPager.getCurrentItem() == 0) {
                    onCompleted = 0;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                }
                else if(viewPager.getCurrentItem() == 1) {
                    onCompleted = 1;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.upload_to_the_cloud));
                }
                else if(viewPager.getCurrentItem() == 2) {
                    onCompleted = 0;
                    menu.getItem(AppConstants.ZERO).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        RelativeLayout relativeNotSubscribed = findViewById(R.id.relative_not_subscribed);
        relativeNotSubscribed.setOnClickListener(null);
        if (AppConstants.IS_DN_SUBSCRIBED) {
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

        reinitializeViewPager();
    }

    public static void landingFinish(Activity context) {
        context.finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new DisconnectionNoticeLandingActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DisconnectionOpenFragment(), getString(R.string.open));
        adapter.addFragment(new DisconnectionCompletedFragment(), getString(R.string.completed));
        adapter.addFragment(new DisconnectionHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
    }

    private void reinitializeViewPager() {
        adapter = null;
        viewPager.removeAllViews();
        adapter = new DisconnectionNoticeLandingActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DisconnectionOpenFragment(), getString(R.string.open));
        adapter.addFragment(new DisconnectionCompletedFragment(), getString(R.string.completed));
        adapter.addFragment(new DisconnectionHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        this.invalidateOptionsMenu();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if(userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(userProfile.profile_image!=null&&!userProfile.profile_image.equalsIgnoreCase("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL+userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL+userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        }
        else {
            imgMrProfile.setImageResource(R.drawable.defaultprofile);
        }
        reinitializeViewPager();
    }

    @Override
    public void onClick(View v) {
        if (v == toolbarLinearProfile)
        {
            intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        Drawable dre= ContextCompat.getDrawable(this, R.drawable.notification_icon);
        int count = DatabaseManager.getCount(this,"false", AppPreferences.getInstance(mContext).getString(AppConstants.METER_READER_ID, ""));
        if (count > 0)
            ActionItemBadge.update(this, menu.findItem(R.id.action_notifications),dre, ActionItemBadge.BadgeStyles.YELLOW, count);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sync:
                if(onCompleted == 1)
                {
                    uploadDisconnectionCards();
                }
                else
                {
                    show = true;
                    getDisconnectionCards();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meter_reading)
        {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, LandingActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_bill_distribution)
        {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, BillDistributionLandingScreen.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_disconnection)
        {
            drawer.closeDrawer(GravityCompat.END);
        }
        else if (id == R.id.nav_payment)
        {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, MyPaymentActivity.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.nav_logout) {
            drawer.closeDrawer(GravityCompat.END);
            performLogout();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        final MenuItem item = menu.findItem(R.id.action_sync);
        if(show)
        {
            item.setEnabled(false);
            item.getIcon().setAlpha(125);
            buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
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

    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            DialogCreator.showExitDialog(this, getString(R.string.exit_app), getString(R.string.do_you_want_to_exit), getString(R.string.disconnection));
        }
    }

    private void callNotification() {
        intent = new Intent(mContext, NotificationActivity.class);
        intent.putExtra(AppConstants.CURRENT_METER_READER_ID, meter_reader_id);
        startActivity(intent);
    }

    private void getDisconnectionCards()
    {
        if(CommonUtils.isNetworkAvailable(mContext) == true)
        {
            showLoadingDialog(getString(R.string.updating_your_assigned_consumer_information_please_wait));
            JsonObjectRequest request = WebRequests.getBillCards(Request.Method.GET, AppConstants.URL_GET_DISCONNECTION_DETAILS, AppConstants.REQUEST_GET_DISCONNECTION_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_DISCONNECTION_DETAILS);
        }
        else
        {
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }

    private void uploadDisconnectionCards()
    {
        if (CommonUtils.isNetworkAvailable(mContext) == true)
        {
            uploadDisconnectionNotices = DatabaseManager.getDisconnectionNotices(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
            if (uploadDisconnectionNotices != null && uploadDisconnectionNotices.size() > 0)
            {
                JSONObject object = getDCNoticesJson(uploadDisconnectionNotices);
                uploadDCNotices(object);
            }
            else
            {
                Toast.makeText(mContext, getString(R.string.no_readings_available_to_be_uploaded), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }

    public JSONObject getDCNoticesJson(ArrayList<UploadDisconnectionNotices> readings)
    {
        JSONObject jsonObject = null;
        try
        {
            Gson gson = new Gson();
            String jsonString = gson.toJson(readings);
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObject = new JSONObject();
            jsonObject.put("city", userProfile.city);
            jsonObject.put("dc_notices", jsonArray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void uploadDCNotices(JSONObject object)
    {
        showLoadingDialog(getString(R.string.uploading_disconnection_notices_please_wait));
        JsonObjectRequest request = WebRequests.uploadMeterReading(object, Request.Method.POST, AppConstants.URL_UPLOAD_DISCONNECTION_DETAILS, AppConstants.REQUEST_UPLOAD_DISCONNECTION_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN), null);
        App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_UPLOAD_DISCONNECTION_DETAILS);
    }

    private void getDeassignedDCNotices()
    {
        showLoadingDialog(getString(R.string.updating_your_deassign_consumer_data_please_wait));
        JsonObjectRequest request = WebRequests.getDeassignedBillCards(Request.Method.GET, AppConstants.URL_DEASSIGN_DISCONNECTION_DETAILS, AppConstants.REQUEST_DEASSIGN_DISCONNECTION_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
        App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_DEASSIGN_DISCONNECTION_DETAILS);
    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label)
    {
        switch(label) {
            case AppConstants.REQUEST_GET_DISCONNECTION_DETAILS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            dismissLoadingDialog();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });
                        }
                        else
                        {
                            if (jsonResponse.responsedata != null && jsonResponse.responsedata.disconnectionCards!= null && jsonResponse.responsedata.disconnectionCards.size() > 0) {
                                DatabaseManager.saveDisconnectionCards(mContext, jsonResponse.responsedata.disconnectionCards);
                                reinitializeViewPager();
                                dismissLoadingDialog();
                                Toast.makeText(mContext, getString(R.string.consumer_information_downloaded), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, getString(R.string.consumer_information_not_assigned_to_you_for_reading_as_of_now), Toast.LENGTH_SHORT).show();
                            }

                            if (jsonResponse.authorization != null) {
                                CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                            }
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        Toast.makeText(mContext, R.string.please_contact_server_admin, Toast.LENGTH_SHORT).show();
                    }
                }
                dismissLoadingDialog();
                getDeassignedDCNotices();
            }
            break;
            case AppConstants.REQUEST_DEASSIGN_DISCONNECTION_DETAILS: {
                if(jsonResponse != null) {
                    if(jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if(jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });
                        }
                        if(jsonResponse.responsedata != null && jsonResponse.responsedata.re_de_assigned_jobcards != null && jsonResponse.responsedata.re_de_assigned_jobcards.size() > 0) {
                            DatabaseManager.handleDeassignedDCNotices(mContext, jsonResponse.responsedata.re_de_assigned_jobcards, meter_reader_id);
                            //refresh summary UI
                            reinitializeViewPager();
                            dismissLoadingDialog();
                            Toast.makeText(mContext, getString(R.string.updated_reassign_deassign_consumer_info_successfully), Toast.LENGTH_SHORT).show();

                        }
                        if(jsonResponse.authorization != null) {
                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        dismissLoadingDialog();
                    }
                    else if(jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissLoadingDialog();
                    }
                }
            }
            break;
            case AppConstants.REQUEST_UPLOAD_DISCONNECTION_DETAILS: {
                dismissLoadingDialog();
                if(jsonResponse != null) {
                    if(jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        for(int i = 0; i < uploadDisconnectionNotices.size(); i++) {
                            if(uploadDisconnectionNotices != null) {
                                DisconnectionHistory disconnectionHistory = new DisconnectionHistory();
                                disconnectionHistory.meter_reader_id = uploadDisconnectionNotices.get(i).meter_reader_id;
                                disconnectionHistory.binder_code = uploadDisconnectionNotices.get(i).binder_code;
                                disconnectionHistory.bill_month = uploadDisconnectionNotices.get(i).bill_month;
                                disconnectionHistory.job_card_id = uploadDisconnectionNotices.get(i).job_card_id;
                                disconnectionHistory.date = CommonUtils.getCurrentDate();
                                disconnectionHistory.delivery_status = uploadDisconnectionNotices.get(i).delivery_status;

                                DatabaseManager.saveDCNoticesHistory(mContext, disconnectionHistory);
                            }
                        }
                        DatabaseManager.deleteDCNotices(mContext, uploadDisconnectionNotices, meter_reader_id);

                        reinitializeViewPager();

                        uploadDisconnectionNotices = null;
                        uploadDisconnectionNotices = DatabaseManager.getDisconnectionNotices(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
                        if (uploadDisconnectionNotices != null && uploadDisconnectionNotices.size() > 0)
                        {
                            JSONObject jsonObject = getDCNoticesJson(uploadDisconnectionNotices);
                            uploadDCNotices(jsonObject);
                        }
                        else
                        {
                            Toast.makeText(this, getString(R.string.consumer_readings_successfully_uploaded), Toast.LENGTH_LONG).show();
                            viewPager.setCurrentItem(0);
                        }
                    }
                    dismissLoadingDialog();
                    getDeassignedDCNotices();
                }
            }
            //refresh summary UI
            reinitializeViewPager();
            dismissLoadingDialog();
            break;
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response)
    {
        dismissLoadingDialog();
        switch(label) {
            case AppConstants.REQUEST_GET_DISCONNECTION_DETAILS: {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                getDeassignedDCNotices();
            }
            break;
            case AppConstants.REQUEST_DEASSIGN_DISCONNECTION_DETAILS: {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
            break;
            case AppConstants.REQUEST_UPLOAD_DISCONNECTION_DETAILS: {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                getDeassignedDCNotices();
            }
            break;
        }
    }

    @Override
    protected void onDestroy()
    {
        if(buttonTimer != null)
            buttonTimer.cancel();
        CommonUtils.deleteCache(mContext);
        super.onDestroy();
    }

    private void performLogout() {
        DialogCreator.showLogoutDialog(this, getString(R.string.logout), getString(R.string.logout_string));
    }
}