package com.sgl.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.sgl.R;
import com.sgl.adapters.PaymentCardAdapter;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JsonResponse;
import com.sgl.models.PaymentCalculation;
import com.sgl.models.Response;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.webservice.WebRequests;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sgl.configuration.AppConstants.REQUEST_GET_PAYMENT_DETAILS;

/**
 * Created by Bynry01 on 10/10/2016.
 */
public class MyPaymentActivity extends ParentActivity implements View.OnClickListener, PaymentCardAdapter.OnPaymentClickListener, ServiceCaller,
        NavigationView.OnNavigationItemSelectedListener {

    public static Activity context;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private Typeface regular, bold;
    private ProgressDialog pDialog;
    private String bankName, consumerName, accountNo, ifsc;
    private DrawerLayout drawer;
    private Intent intent;
    private Toolbar toolbar;
    private CircleImageView imgMrProfile, imgDrawerProfile;
    private TextView txtName, txtDrawerMRName, txtDrawerMobileNo, lblBlankScreenMsg;
    private LinearLayout toolbarLinearProfile;
    private UserProfile userProfile;
    public static String meter_reader_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypayment);
        mContext = this;
        context = this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        AppPreferences.getInstance(mContext).putString(AppConstants.SCREEN_FROM_EXIT, getString(R.string.payment));

        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }

        imgMrProfile = findViewById(R.id.image_profile);
        txtName = findViewById(R.id.txt_name);
        txtName.setTypeface(regular);
        txtName.setSelected(true);
        txtName.setText(userProfile.meter_reader_name);
        lblBlankScreenMsg = findViewById(R.id.lbl_blank_msg);
        lblBlankScreenMsg.setTypeface(regular);

        toolbarLinearProfile = findViewById(R.id.linear_profile);
        toolbarLinearProfile.setOnClickListener(this);

//        initProgressDialog();
//        getPaymentDetails();
//        demoData();

        drawer = findViewById(R.id.drawer_layout);
        final RelativeLayout mainView = findViewById(R.id.mainView);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
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
        toolbar.setNavigationIcon(null);

        NavigationView navigationView = findViewById(R.id.nav_view_right);
        View header = navigationView.getHeaderView(AppConstants.ZERO);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);

        txtDrawerMRName = header.findViewById(R.id.txt_drawer_mr_name);
        txtDrawerMRName.setTypeface(bold);
        txtDrawerMRName.setText(userProfile.email_id + " | " + userProfile.meter_reader_name);
        txtDrawerMobileNo = header.findViewById(R.id.txt_drawer_mobile_no);
        txtDrawerMobileNo.setTypeface(regular);
        txtDrawerMobileNo.setText(userProfile.contact_no);
        imgDrawerProfile = header.findViewById(R.id.img_drawer_profile);

        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
        }

        RelativeLayout relativeNotSubscribed = findViewById(R.id.relative_not_subscribed);
        relativeNotSubscribed.setOnClickListener(null);
        if (AppConstants.IS_PAY_SUBSCRIBED) {
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
    }

    public void demoData() {
        String response = "{\"responsedata\":{\"bank_name\":\"SBI \",\"third_month\":{},\"ifsc\":\"SBIN0031164\",\"ac_name\":\"Ravi Vyas\",\"second_month\":{\"domestic_payment_calculation\":{\"category\":\"Domestic\",\"snf\":\"2\",\"distributedamount\":\"0.0\",\"totalreading\":\"1767\",\"readingamount\":\"3971.25\",\"readingconsumers\":\"3660\",\"distributed\":\"0\",\"distributedbinderassigned\":\"0\",\"billablereading\":\"1765\",\"other_deduction\":\"0\",\"distributedconsumer\":\"10\",\"mr_rate\":\"2.25\",\"readingbinderassigned\":\"24\",\"billmonth\":\"November 17\",\"created_date\":\"30-11-2017\",\"totalamount\":\"3971.25\",\"allocated\":\"1769\",\"bd_rate\":\"1.25\",\"id\":\"698\",\"rnt\":\"0\"},\"zero_payment_calculation\":{\"category\":\"00\",\"snf\":\"0\",\"distributedamount\":\"0.0\",\"totalreading\":\"32\",\"readingamount\":\"800.0\",\"readingconsumers\":\"64\",\"distributed\":\"0\",\"distributedbinderassigned\":\"0\",\"billablereading\":\"32\",\"other_deduction\":\"0\",\"distributedconsumer\":\"20\",\"mr_rate\":\"25\",\"readingbinderassigned\":\"4\",\"billmonth\":\"November 17\",\"created_date\":\"30-11-2017\",\"totalamount\":\"800.0\",\"allocated\":\"34\",\"bd_rate\":\"5\",\"id\":\"664\",\"rnt\":\"0\"},\"dc_payment_calculation\":{\"dc_distributed\":\"30\",\"dcconsumers\":\"32\",\"dc_rate\":\"25\",\"dcbinderassigned\":\"4\",\"billmonth\":\"November 17\",\"created_date\":\"30-11-2017\",\"dcamount\":\"800.0\"}},\"ac_no\":\"51106911696\",\"first_month\":{\"domestic_payment_calculation\":{\"category\":\"Domestic\",\"snf\":\"16\",\"distributedamount\":\"0.0\",\"totalreading\":\"1126\",\"readingamount\":\"2497.5\",\"readingconsumers\":\"3883\",\"distributed\":\"0\",\"distributedbinderassigned\":\"0\",\"billablereading\":\"1110\",\"other_deduction\":\"0\",\"distributedconsumer\":\"0\",\"mr_rate\":\"2.25\",\"readingbinderassigned\":\"28\",\"billmonth\":\"October 17\",\"created_date\":\"02-11-2017\",\"totalamount\":\"2497.5\",\"allocated\":\"1126\",\"bd_rate\":\"1.25\",\"id\":\"644\",\"rnt\":\"0\"},\"zero_payment_calculation\":{\"category\":\"00\",\"snf\":\"0\",\"distributedamount\":\"0.0\",\"totalreading\":\"33\",\"readingamount\":\"825.0\",\"readingconsumers\":\"101\",\"distributed\":\"0\",\"distributedbinderassigned\":\"0\",\"billablereading\":\"33\",\"other_deduction\":\"0\",\"distributedconsumer\":\"0\",\"mr_rate\":\"25\",\"readingbinderassigned\":\"3\",\"billmonth\":\"October 17\",\"created_date\":\"02-11-2017\",\"totalamount\":\"825.0\",\"allocated\":\"35\",\"bd_rate\":\"5\",\"id\":\"608\",\"rnt\":\"0\"},\"dc_payment_calculation\":{\"distributed\":\"0\",\"distributedbinderassigned\":\"0\",\"billablereading\":\"32\",\"dc_rate\":\"25\",\"dcbinderassigned\":\"4\",\"billmonth\":\"November 17\",\"created_date\":\"30-11-2017\",\"totalamount\":\"800.0\"}}},\"message\":\"Payment Info Successfully Calculation.\",\"result\":\"success\",\"authorization\":\"Token 85304d1ed9b278e1cdc10b0074efe68327bc7ffc\"}";
        Gson gson = new Gson();
        JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
        setData(jsonResponse.responsedata);
    }


    private void loadRecyclerView(ArrayList<PaymentCalculation> mpay) {
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        PaymentCardAdapter adapter = new PaymentCardAdapter(mContext, mpay, this);
        recyclerView.setAdapter(adapter);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == toolbarLinearProfile) {
            intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPayCardClick(PaymentCalculation pay) {
        Intent i = new Intent(mContext, PaymentDetailsActivity.class);
        i.putExtra("CURRENT_PAYMENT_CARD", pay);
        startActivity(i);
    }

    private void getPaymentDetails() {
        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            // Your dialog code.
            if (pDialog != null) {
                pDialog.setMessage(getString(R.string.downloading_payment_details_please_wait));
                pDialog.show();
            }

            JsonObjectRequest request = WebRequests.getPaymentData(Request.Method.GET, AppConstants.URL_GET_PAYMENT_DETAILS, REQUEST_GET_PAYMENT_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, REQUEST_GET_PAYMENT_DETAILS);
        } else
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.error));

    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {
            case REQUEST_GET_PAYMENT_DETAILS:
                if (jsonResponse != null) {
                    if (jsonResponse.responsedata != null) {
                        consumerName = jsonResponse.responsedata.ac_name;
                        bankName = jsonResponse.responsedata.bank_name;
                        accountNo = jsonResponse.responsedata.ac_no;
                        ifsc = jsonResponse.responsedata.ifsc;
                    }
                    if (jsonResponse.result != null) {
                        if (jsonResponse.responsedata != null)
                            setData(jsonResponse.responsedata);
                        dismissDialog();
                    } else {
                        lblBlankScreenMsg.setVisibility(View.VISIBLE);
                        DialogCreator.showMessageDialog(mContext, jsonResponse.message, getString(R.string.error));
                        dismissDialog();
                    }
                }
                break;
        }
    }

    private void setData(Response responseData) {
        lblBlankScreenMsg.setVisibility(View.GONE);
        ArrayList<PaymentCalculation> paymentCalculation = new ArrayList<>();

        /*if (responseData.months != null)
            for (int i = 0; responseData.months.size() > i; i++) {
                if (responseData.months.get(i) != null)
                    if (responseData.months.get(i).domestic_payment_calculation != null || responseData.months.get(i).zero_payment_calculation != null || responseData.months.get(i).dc_payment_calculation != null)
                 *//*d*//*
                        if (responseData.months.get(i).domestic_payment_calculation != null && responseData.months.get(i).zero_payment_calculation == null && responseData.months.get(i).dc_payment_calculation == null) {
                            float total = Float.parseFloat(responseData.months.get(i).domestic_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).domestic_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));
                 *//*z*//*
                        } else if (responseData.months.get(i).zero_payment_calculation != null && responseData.months.get(i).domestic_payment_calculation == null && responseData.months.get(i).dc_payment_calculation == null) {
                            float total = Float.parseFloat(responseData.months.get(i).zero_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).zero_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));
                   *//*dc*//*
                        } else if (responseData.months.get(i).dc_payment_calculation != null && responseData.months.get(i).zero_payment_calculation == null && responseData.months.get(i).domestic_payment_calculation == null) {
                            float total = Float.parseFloat(responseData.months.get(i).dc_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).dc_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));

               *//*dz*//*
                        } else if (responseData.months.get(i).domestic_payment_calculation != null && responseData.months.get(i).zero_payment_calculation != null && responseData.months.get(i).dc_payment_calculation == null) {
                            float total = Float.parseFloat(responseData.months.get(i).domestic_payment_calculation.totalamount) + Float.parseFloat(responseData.months.get(i).zero_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).domestic_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));

                  *//*ddc*//*
                        } else if (responseData.months.get(i).domestic_payment_calculation != null && responseData.months.get(i).zero_payment_calculation == null && responseData.months.get(i).dc_payment_calculation != null) {
                            float total = Float.parseFloat(responseData.months.get(i).domestic_payment_calculation.totalamount) + Float.parseFloat(responseData.months.get(i).dc_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).domestic_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));

                *//*zdc*//*
                        } else if (responseData.months.get(i).domestic_payment_calculation == null && responseData.months.get(i).zero_payment_calculation != null && responseData.months.get(i).dc_payment_calculation == null) {
                            float total = Float.parseFloat(responseData.months.get(i).zero_payment_calculation.totalamount) + Float.parseFloat(responseData.months.get(i).dc_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).domestic_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));

                    *//*dzdc*//*
                        } else if (responseData.months.get(i).domestic_payment_calculation != null && responseData.months.get(i).zero_payment_calculation != null && responseData.months.get(i).dc_payment_calculation != null) {
                            float total = Float.parseFloat(responseData.months.get(i).domestic_payment_calculation.totalamount) + Float.parseFloat(responseData.months.get(i).zero_payment_calculation.totalamount) + Float.parseFloat(responseData.months.get(i).dc_payment_calculation.totalamount);
                            responseData.months.get(i).pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                            responseData.months.get(i).esi = Double.valueOf(Double.valueOf(total) * .0175);
                            responseData.months.get(i).other = responseData.months.get(i).domestic_payment_calculation.other_deduction;
                            if (responseData.months.get(i).other != null)
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi - Double.valueOf(responseData.months.get(i).other);
                            else
                                responseData.months.get(i).grandtotal = total - responseData.months.get(i).pf - responseData.months.get(i).esi;
                            paymentCalculation.add(responseData.months.get(i));

                        }

            }*/

        //first month
        if (responseData.first_month != null)
            if (responseData.first_month.domestic_payment_calculation != null || responseData.first_month.zero_payment_calculation != null)
                if (responseData.first_month.domestic_payment_calculation != null && responseData.first_month.zero_payment_calculation == null) {
                    float total = Float.parseFloat(responseData.first_month.domestic_payment_calculation.totalamount);
                    responseData.first_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.first_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.first_month.other = responseData.first_month.domestic_payment_calculation.other_deduction;
                    if (responseData.first_month.other != null)
                        responseData.first_month.grandtotal = total - responseData.first_month.pf - responseData.first_month.esi - Double.valueOf(responseData.first_month.other);
                    else
                        responseData.first_month.grandtotal = total - responseData.first_month.pf - responseData.first_month.esi;
                    paymentCalculation.add(responseData.first_month);
                } else if (responseData.first_month.zero_payment_calculation != null && responseData.first_month.domestic_payment_calculation == null) {
                    float total = Float.parseFloat(responseData.first_month.zero_payment_calculation.totalamount);
                    responseData.first_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.first_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.first_month.other = responseData.first_month.zero_payment_calculation.other_deduction;
                    if (responseData.first_month.other != null)
                        responseData.first_month.grandtotal = total - responseData.first_month.pf - responseData.first_month.esi - Double.valueOf(responseData.first_month.other);
                    else
                        responseData.first_month.grandtotal = total - responseData.first_month.pf - responseData.first_month.esi;
                    paymentCalculation.add(responseData.first_month);
                } else if (responseData.first_month.domestic_payment_calculation != null && responseData.first_month.zero_payment_calculation != null) {
                    float total = Float.parseFloat(responseData.first_month.domestic_payment_calculation.totalamount) + Float.parseFloat(responseData.first_month.zero_payment_calculation.totalamount);
                    responseData.first_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.first_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.first_month.other = responseData.first_month.domestic_payment_calculation.other_deduction;
                    if (responseData.first_month.other != null)
                        responseData.first_month.grandtotal = total - responseData.first_month.pf - responseData.first_month.esi - Double.valueOf(responseData.first_month.other);
                    else
                        responseData.first_month.grandtotal = total - responseData.first_month.pf - responseData.first_month.esi;
                    paymentCalculation.add(responseData.first_month);

                }

        // Second month
        if (responseData.second_month != null)
            if (responseData.second_month.domestic_payment_calculation != null || responseData.second_month.zero_payment_calculation != null)
                if (responseData.second_month.domestic_payment_calculation != null && responseData.second_month.zero_payment_calculation == null) {
                    float total = Float.parseFloat(responseData.second_month.domestic_payment_calculation.totalamount);
                    responseData.second_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.second_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.second_month.other = responseData.second_month.domestic_payment_calculation.other_deduction;
                    if (responseData.second_month.other != null)
                        responseData.second_month.grandtotal = total - responseData.second_month.pf - responseData.second_month.esi - Double.valueOf(responseData.second_month.other);
                    else
                        responseData.second_month.grandtotal = total - responseData.second_month.pf - responseData.second_month.esi;
                    paymentCalculation.add(responseData.second_month);
                } else if (responseData.second_month.zero_payment_calculation != null && responseData.second_month.domestic_payment_calculation == null) {
                    float total = Float.parseFloat(responseData.second_month.zero_payment_calculation.totalamount);
                    responseData.second_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.second_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.second_month.other = responseData.second_month.zero_payment_calculation.other_deduction;
                    if (responseData.second_month.other != null)
                        responseData.second_month.grandtotal = total - responseData.second_month.pf - responseData.second_month.esi - Double.valueOf(responseData.second_month.other);
                    else
                        responseData.second_month.grandtotal = total - responseData.second_month.pf - responseData.second_month.esi;
                    paymentCalculation.add(responseData.second_month);
                } else if (responseData.second_month.domestic_payment_calculation != null && responseData.second_month.zero_payment_calculation != null) {
                    float total = Float.parseFloat(responseData.second_month.domestic_payment_calculation.totalamount) + Float.parseFloat(responseData.second_month.zero_payment_calculation.totalamount);
                    responseData.second_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.second_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.second_month.other = responseData.second_month.domestic_payment_calculation.other_deduction;
                    if (responseData.second_month.other != null)
                        responseData.second_month.grandtotal = total - responseData.second_month.pf - responseData.second_month.esi - Double.valueOf(responseData.second_month.other);
                    else
                        responseData.second_month.grandtotal = total - responseData.second_month.pf - responseData.second_month.esi;
                    paymentCalculation.add(responseData.second_month);
                }

        //third month
        if (responseData.third_month != null)
            if (responseData.third_month.domestic_payment_calculation != null || responseData.third_month.zero_payment_calculation != null)
                if (responseData.third_month.domestic_payment_calculation != null && responseData.third_month.zero_payment_calculation == null) {
                    float total = Float.parseFloat(responseData.third_month.domestic_payment_calculation.totalamount);
                    responseData.third_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.third_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.third_month.other = responseData.third_month.domestic_payment_calculation.other_deduction;
                    if (responseData.third_month.other != null)
                        responseData.third_month.grandtotal = total - responseData.third_month.pf - responseData.third_month.esi - Double.valueOf(responseData.third_month.other);
                    else
                        responseData.third_month.grandtotal = total - responseData.third_month.pf - responseData.third_month.esi;
                    paymentCalculation.add(responseData.third_month);
                } else if (responseData.second_month.zero_payment_calculation != null && responseData.third_month.domestic_payment_calculation == null) {
                    float total = Float.parseFloat(responseData.third_month.zero_payment_calculation.totalamount);
                    responseData.third_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.third_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.third_month.other = responseData.third_month.zero_payment_calculation.other_deduction;
                    if (responseData.third_month.other != null)
                        responseData.third_month.grandtotal = total - responseData.third_month.pf - responseData.third_month.esi - Double.valueOf(responseData.third_month.other);
                    else
                        responseData.third_month.grandtotal = total - responseData.third_month.pf - responseData.third_month.esi;
                    paymentCalculation.add(responseData.third_month);
                } else if (responseData.third_month.domestic_payment_calculation != null && responseData.third_month.zero_payment_calculation != null) {
                    float total = Float.parseFloat(responseData.third_month.domestic_payment_calculation.totalamount) + Float.parseFloat(responseData.third_month.zero_payment_calculation.totalamount);
                    responseData.third_month.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                    responseData.third_month.esi = Double.valueOf(Double.valueOf(total) * .0175);
                    responseData.third_month.other = responseData.third_month.domestic_payment_calculation.other_deduction;
                    if (responseData.third_month.other != null)
                        responseData.third_month.grandtotal = total - responseData.third_month.pf - responseData.third_month.esi - Double.valueOf(responseData.third_month.other);
                    else
                        responseData.third_month.grandtotal = total - responseData.third_month.pf - responseData.third_month.esi;
                    paymentCalculation.add(responseData.third_month);
                }

        loadRecyclerView(paymentCalculation);
        if (paymentCalculation.size() == 0) {
            lblBlankScreenMsg.setVisibility(View.VISIBLE);
            DialogCreator.showMessageDialog(mContext, getString(R.string.your_payment_detail_is_not_ready_please_contact_to_supervisor), getString(R.string.error));
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case REQUEST_GET_PAYMENT_DETAILS:
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                dismissDialog();
                break;
        }
    }

    public void showInfo() {
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_consumer_details, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

        TextView lblConsumerMeterNo, lblConsumerNumber, lblCycle, lblSubDiv, lblMobile, lblBinder;
        TextView txtConsumerName, txtConsumerAddress, txtConsumerMeterNo, txtConsumerNumber, txtsubdiv, txtcycle, txtbinderNo, txtmobileNo;
        lblConsumerNumber = promptView.findViewById(R.id.lbl_consumer_no);
        lblConsumerNumber.setTypeface(bold);
        lblConsumerNumber.setText("A/c Holder Name");
        lblConsumerMeterNo = promptView.findViewById(R.id.lbl_meter_no);
        lblConsumerMeterNo.setTypeface(bold);
        lblConsumerMeterNo.setText("Account No.");
        lblCycle = promptView.findViewById(R.id.lbl_cycle);
        lblCycle.setTypeface(bold);
        lblCycle.setText("IFSC Code");
        lblSubDiv = promptView.findViewById(R.id.lbl_subdiv);
        lblSubDiv.setTypeface(bold);
        lblSubDiv.setText("Bank Name");
        lblMobile = promptView.findViewById(R.id.lbl_mobile);
        lblMobile.setTypeface(bold);
        lblMobile.setVisibility(View.GONE);
        lblBinder = promptView.findViewById(R.id.lbl_binder_no);
        lblBinder.setTypeface(bold);
        lblBinder.setVisibility(View.GONE);

        txtConsumerName = promptView.findViewById(R.id.txt_consumer_name);
        txtConsumerName.setTypeface(bold);
        txtConsumerName.setText("Bank Details");
        txtConsumerAddress = promptView.findViewById(R.id.txt_consumer_address);
        txtConsumerAddress.setTypeface(regular);
        txtConsumerAddress.setVisibility(View.INVISIBLE);
        txtConsumerNumber = promptView.findViewById(R.id.txt_consumer_no);
        txtConsumerNumber.setTypeface(regular);
        if (consumerName != null)
            txtConsumerNumber.setText(consumerName);
        else
            txtConsumerNumber.setText("");

        txtConsumerMeterNo = promptView.findViewById(R.id.txt_meter_no);
        txtConsumerMeterNo.setTypeface(regular);
        if (accountNo != null)
            txtConsumerMeterNo.setText(accountNo);
        else
            txtConsumerMeterNo.setText("");
        txtcycle = promptView.findViewById(R.id.txt_cycle);
        txtcycle.setTypeface(regular);
        if (ifsc != null)
            txtcycle.setText(ifsc);
        else
            txtcycle.setText("");
        txtsubdiv = promptView.findViewById(R.id.txt_subdiv);
        txtsubdiv.setTypeface(regular);
        if (bankName != null)
            txtsubdiv.setText(bankName);
        else
            txtsubdiv.setText("");
        txtbinderNo = promptView.findViewById(R.id.txt_binder_no);
        txtbinderNo.setTypeface(regular);
        txtbinderNo.setVisibility(View.GONE);
        txtmobileNo = promptView.findViewById(R.id.txt_mobile_no);
        txtmobileNo.setTypeface(regular);
        txtmobileNo.setVisibility(View.GONE);
        //Initialising all text ends

        //Setting values to the text starts
//        txtConsumerName.setText(consumerName);
//        txtConsumerAddress.setText(consumerAddress);
//        txtConsumerNumber.setText(ac_name);
//        txtConsumerMeterNo.setText(ac_no);
//        txtcycle.setText(ifsc);
//        txtsubdiv.setText(bank_name);
//        txtbinderNo.setText(consumerbinderNo);
//        txtmobileNo.setText(consumermobileNo);
        //Setting values to the text ends

        //OK button code starts
        Button yes = (Button) promptView.findViewById(R.id.btn_ok);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.show();
        //OK button code ends
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_my_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_drawer:
                drawer = findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
                return true;
            case R.id.action_bank_info:
                showInfo();
                return true;
            default:
                break;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meter_reading) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, LandingActivity.class);
            startActivity(intent);
            finish();
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
        }else if(id == R.id.nav_logout) {
            drawer.closeDrawer(GravityCompat.END);
            performLogout();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public static void landingFinish(Activity context) {
        context.finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            DialogCreator.showExitDialog(this, getString(R.string.exit_app), getString(R.string.do_you_want_to_exit), getString(R.string.payment));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userProfile.profile_image != null && !userProfile.profile_image.equalsIgnoreCase("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        } else {
            imgMrProfile.setImageResource(R.drawable.defaultprofile);
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtils.deleteCache(mContext);
        super.onDestroy();
    }

    private void performLogout() {
        DialogCreator.showLogoutDialog(this, getString(R.string.logout), getString(R.string.logout_string));
    }
}
