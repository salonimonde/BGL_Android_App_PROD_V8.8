package com.sgl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.sgl.R;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JsonResponse;
import com.sgl.models.Response;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.sgl.webservice.WebRequests;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends ParentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ServiceCaller
{
    private static int RESULT_LOAD_IMG = 1, z = 0;
    private ImageView imgBack, cam, imgLogOut, imgMyScore, imgCash;
    private CircleImageView imgView;
    private Toolbar toolbar;
    private TextView txtName, txtCity, txtEmail, txtPhone, txtTitle, txtVersion;
    private String imgDecodeableString;
    private UserProfile userProfile;
    private RelativeLayout relativeLayoutImage;
    private Context mContext;
    private Locale myLocale;
    private RadioButton btnEnglish, btnHindi;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private LinearLayout linearGetDatabases;
    private ProgressDialog pDialog;
    private Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        setupUI();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.this_device_is_not_supported), Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void setupUI() {
        
        Typeface regular = App.getSansationRegularFont();
        imgCash = findViewById(R.id.img_cash);
        imgCash.setOnClickListener(this);
        imgLogOut = findViewById(R.id.img_logout);
        imgLogOut.setOnClickListener(this);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        imgMyScore = findViewById(R.id.img_my_score);
        imgMyScore.setOnClickListener(this);
        txtCity = findViewById(R.id.lbl_city);
        txtCity.setTypeface(regular);
        txtName = findViewById(R.id.lbl_namedata);
        txtName.setTypeface(regular);
        txtEmail = findViewById(R.id.lbl_mr_email_id);
        txtEmail.setTypeface(regular);
        txtTitle = findViewById(R.id.title_bar);
        txtTitle.setTypeface(regular);
        txtPhone = findViewById(R.id.lbl_phone_no);
        txtPhone.setTypeface(regular);
        imgView = findViewById(R.id.iv_profile);
        cam = findViewById(R.id.ic_camera);
        cam.setOnClickListener(this);
        relativeLayoutImage = findViewById(R.id.relative_image);

        linearGetDatabases = findViewById(R.id.linear_get_database);
        linearGetDatabases.setOnClickListener(this);
        
        if (userProfile != null) {
            txtName.setText(userProfile.meter_reader_name);
            txtPhone.setText(userProfile.contact_no);
            txtEmail.setText(userProfile.email_id);
            txtCity.setText(userProfile.city);
            String reader_name = userProfile.meter_reader_name;
            String reader_id = userProfile.meter_reader_id;
            txtName.setText(reader_name);
            toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                setTitle(getString(R.string.my_profile));
                toolbar.setSubtitle(reader_name + "(" + reader_id + ")");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
            if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
                Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgView);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap myImage = getBitmapFromURL(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image);
                           // relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), myImage));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        btnEnglish = findViewById(R.id.rb_english);
        btnEnglish.setOnClickListener(this);
        btnHindi = findViewById(R.id.rb_hindi);
        btnHindi.setOnClickListener(this);

        String languageSelected = AppPreferences.getInstance(this).getString(AppConstants.LANGUAGE_SELECTED, "");
        if (languageSelected.equals("hindi")) {
            btnHindi.setChecked(true);
        } else {
            btnEnglish.setChecked(true);
        }



        txtVersion = findViewById(R.id.lbl_version_no);
        try{

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVersion.setText(pInfo.versionName);

        }catch (Exception e){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnEnglish) {
            AppPreferences.getInstance(this).putString(AppConstants.LANGUAGE_SELECTED, "english");
            setLocale("en");
        }
        if (v == btnHindi) {
            AppPreferences.getInstance(this).putString(AppConstants.LANGUAGE_SELECTED, "hindi");
            setLocale("hi");
        }

        if (v == linearGetDatabases) {
            checkCounts();
        }
        switch (v.getId()) {
            case R.id.img_cash:
                Intent in = new Intent(mContext, MyPaymentActivity.class);
                startActivity(in);
                break;
                
            case R.id.ic_camera:
                changeImage();
                break;
                
            case R.id.img_logout:
//                performLogout();
                break;
        }
    }

    /*   Method for click on cancel button on dilog */
    public void changeImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_profile_photo);
        builder.setItems(new CharSequence[]
                        {getString(R.string.gallery), getString(R.string.Remove)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                loadImageFromGallery();
                                break;
                            case 1:
                                setDefault();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void loadImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void setDefault() {
        imgView.setImageResource(R.drawable.defaultprofile);
        relativeLayoutImage.setBackgroundColor(CommonUtils.getColor(mContext, R.color.colorPrimary));
        userProfile.profile_image = "";
        DatabaseManager.saveImage(mContext, userProfile);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodeableString = cursor.getString(columnIndex);
                    cursor.close();
                }

                Bitmap bm = BitmapFactory.decodeFile(imgDecodeableString);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] byteArrayImage = baos.toByteArray();
                String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                if (CommonUtils.isNetworkAvailable(mContext)) {
                    initProgressDialog();
                    if (pDialog != null && !pDialog.isShowing()) {
                        pDialog.setMessage("Please wait...");
                        pDialog.show();
                    }
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("profile_image", encodedImage.toString() == null ? "" : encodedImage.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest request = WebRequests.profileImageChange(Request.Method.POST, AppConstants.URL_USER_PROFILE_IMAGE, AppConstants.REQUEST_USER_PROFILE_IMAGE, this, obj, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
                    App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_USER_PROFILE_IMAGE);

                } else {
                    Toast.makeText(mContext, R.string.error_internet_not_connected, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, R.string.er_you_havent_picked_image, Toast.LENGTH_LONG).show();
            }
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onConnected( Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mContext, LandingActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {

    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {

            case AppConstants.REQUEST_USER_PROFILE_IMAGE: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        response = jsonResponse.responsedata;
                        Toast.makeText(mContext, jsonResponse.message.toString(), Toast.LENGTH_SHORT).show();
                        CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + jsonResponse.responsedata.user_info.get(0).
                                profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgView);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap myImage = getBitmapFromURL(AppConstants.PROFILE_IMAGE_URL + response.user_info.get(0).profile_image);
                                   // relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), myImage));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
                        DatabaseManager.saveImage(mContext, jsonResponse.responsedata.user_info.get(0));

                        dismissDialog();
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                } else
                    Toast.makeText(mContext, R.string.er_data_not_avaliable, Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_USER_PROFILE_IMAGE: {
                dismissDialog();
                break;
            }
        }
    }
    
    private void checkCounts() {
        z++;
        if (z == 10) {
            z = 0;
            showDialogDatabase(mContext);
        }
    }

    public void showDialogDatabase(final Context context) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = promptView.findViewById(R.id.txt_title);
        txtTitle.setTypeface(regular);
        txtError = promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);

        edtUserName = promptView.findViewById(R.id.edt_user_name);
        edtUserName.setText("BGL Database");
        edtPassword = promptView.findViewById(R.id.edt_password);

        Button ok = promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (edtUserName.getText().toString().trim().equals("BGL Database") && edtPassword.getText().toString().trim().equals("GetBGLDB")) {
                    txtError.setVisibility(View.GONE);
                    getDatabase();
                    alert.dismiss();
                    done();
                } else {
                    txtError.setVisibility(View.VISIBLE);
                }
            }
        });

        Button cancel = promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtError.setVisibility(View.GONE);
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }

    private void getDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/SGL.db";
                String backupDBPath = "BackUpSGL.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, AppConstants.ZERO, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void done() {
        Toast.makeText(mContext, "Database Retrieved Successfully", Toast.LENGTH_SHORT).show();
    }
    
    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}