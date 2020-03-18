package com.sgl.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.sgl.R;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JsonResponse;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.utils.LocationManagerReceiver;
import com.sgl.webservice.WebRequests;

import java.util.ArrayList;

public class LoginActivity extends ParentActivity implements ServiceCaller, OnClickListener {

    // UI references.
    private Context mContext;
    private EditText mEmailView;
    private EditText mPasswordView;
    private String user_email;
    private RelativeLayout rl_login_view;
    private Typeface regular;
    private TextView Forget;
    private ProgressDialog pDialog;
    private String deviceToken = "";
    private ImageView login_image;
    private static int z = 0;
    private String imeiNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        regular = App.getSansationRegularFont();

        setupUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CommonUtils.askForPermissions(mContext, rl_login_view, App.getInstance().permissions);
        }
        login_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCounts();

            }
        });
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

    // Set up the login form.
    private void setupUI() {
        initProgressDialog();
        mEmailView = findViewById(R.id.act_email);
        mEmailView.setTypeface(regular);
        mEmailView.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        rl_login_view = findViewById(R.id.rl_login_view);
        mPasswordView = findViewById(R.id.ed_password);
        login_image = findViewById(R.id.login_image);
        mPasswordView.setTypeface(regular);
        Forget = findViewById(R.id.forget);
        Forget.setTypeface(regular);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setTypeface(regular);
        mEmailSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                CommonUtils.hideKeyBoard(mContext);
                performLogin();
                break;
        }
    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {
            case AppConstants.REQUEST_LOGIN: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.responsedata != null && jsonResponse.responsedata.user_info != null && jsonResponse.responsedata.user_info.size() > 0) {
                            dismissDialog();

                            deviceToken = AppPreferences.getInstance(mContext).getString(AppConstants.FCM_KEY, "");

                            CommonUtils.saveCredentials(mContext, user_email, jsonResponse.authorization);
                            DatabaseManager.saveLoginDetails(this, user_email, jsonResponse.responsedata.user_info);

                            String mrId = mEmailView.getText().toString().trim();
                            JsonObjectRequest request = WebRequests.updateDeviceToken(Request.Method.POST, AppConstants.URL_UPDATE_DEVICE_TOKEN,
                                    AppConstants.UPDATE_DEVICE_TOKEN, this, mrId, deviceToken);
                            App.getInstance().addToRequestQueue(request, AppConstants.UPDATE_DEVICE_TOKEN);

                            App.welcome = true;
                            showMainActivity();
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                        DialogCreator.showMessageDialog(mContext, jsonResponse.message != null ? jsonResponse.message : getString(R.string.er_please_contact_server_admin), getString(R.string.error));
                    }
                } else
                    Toast.makeText(this, R.string.er_data_not_avaliable, Toast.LENGTH_LONG).show();
            }
            break;
            case AppConstants.UPDATE_DEVICE_TOKEN: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        System.out.println("Update Device Token Api Success");
                    }

                } else
                    System.out.println("Update Device Token Api Fails");
            }
            break;
        }
        dismissDialog();
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_LOGIN: {
                try {
                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    if (res != null) {
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        if (jsonResponse.responsedata.error_code.equals("101")) {
                            DialogCreator.showMessageDialog(mContext, getString(R.string.login_error_101), getString(R.string.error));
                        } else if (jsonResponse.responsedata.error_code.equals("102")) {
                            DialogCreator.showMessageDialog(mContext, getString(R.string.login_error_102), getString(R.string.error));
                        } else if (jsonResponse.responsedata.error_code.equals("103")) {
                            DialogCreator.showMessageDialog(mContext, getString(R.string.login_error_103), getString(R.string.error));
                        }
                    }
                } catch (Exception e) {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.login_error_null), getString(R.string.error));
                    e.printStackTrace();
                }
                dismissDialog();
                break;
            }
            case AppConstants.DEVICE_FCM_TOKEN: {
                System.out.println("Update Device Token Api Fails Completely");
            }

        }
    }

    private void performLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        user_email = mEmailView.getText().toString().trim();
        String user_password = mPasswordView.getText().toString().trim();

        // Check for a valid password, if the user entered one.
        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            if (!TextUtils.isEmpty(user_email)) {
                if (isEmailValid(user_email)) {
                    if (!TextUtils.isEmpty(user_password)) {
                        if (isPasswordValid(user_password)) {
                            if (pDialog != null && !pDialog.isShowing()) {
                                pDialog.setMessage(getString(R.string.logging_in_please_wait));
                                pDialog.show();
                            }

                            /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                imeiNumber = telephonyManager.getDeviceId();
                                Log.e("TAG","AndroidId" +imeiNumber);


                            } else {
                                imeiNumber = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                                Log.e("TAG","imeiNumber" +imeiNumber);

                            }


                            ///TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }*/
                            //String imeiNumber = telephonyManager.getDeviceId();
                            String imeiNumber = "865294040344858";
                            JsonObjectRequest request = WebRequests.loginRequest(Request.Method.POST, AppConstants.URL_LOGIN, AppConstants.REQUEST_LOGIN, this, user_email, user_password, imeiNumber);
                            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_LOGIN);
                        } else
                            mPasswordView.setError(getString(R.string.error_invalid_password));
                    } else
                        mPasswordView.setError(getString(R.string.error_empty_password));
                } else
                    mEmailView.setError(getString(R.string.error_invalid_email));
            } else
                mEmailView.setError(getString(R.string.error_field_required));
        } else
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));
    }


    private void showMainActivity() {
        Intent i = new Intent(mContext, LandingActivity.class);
        startActivity(i);
        finish();
    }

    private boolean isEmailValid(String email) {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        return email.matches(emailPattern);
        if (email.length() <= 8 && email.length() > 0)
            return true;
        else
            return false;

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AppConstants.ALL_PERMISSIONS_RESULT:
                boolean someAccepted = false;
                boolean someRejected = false;
                ArrayList<String> permissionsRejected = new ArrayList<String>();
                for (String perms : App.getInstance().permissions) {
                    if (CommonUtils.hasPermission(mContext, perms)) {
                        someAccepted = true;
                    } else {
                        someRejected = true;
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    someRejected = true;
                }
                if (someRejected) {
                    CommonUtils.showPostPermissionsShackBar(mContext, rl_login_view, permissionsRejected);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DialogCreator.showExitDialog(this, getString(R.string.exit), getString(R.string.exit_message), getString(R.string.login_screen));
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
                        LocationManagerReceiver locationManagerReceiver = new LocationManagerReceiver(this);
                        break;
                }
                break;
        }
    }

    private void checkCounts() {
        z++;
        if (z == 10) {
            z = 0;
            showDialogForAndroidID(mContext);
        }
    }

    public void showDialogForAndroidID(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dailog_android_id);
        final TextView txtAndroidId;
        txtAndroidId = dialog.findViewById(R.id.android_id);
        Button ok = dialog.findViewById(R.id.btn_ok);
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            dialog.show();
            txtAndroidId.setText(androidId);
        }
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }
}

