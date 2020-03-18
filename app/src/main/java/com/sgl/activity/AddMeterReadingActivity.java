package com.sgl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.sgl.R;
import com.sgl.callers.ServiceCaller;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JobCard;
import com.sgl.models.JsonResponse;
import com.sgl.models.MeterImage;
import com.sgl.models.MeterReading;
import com.sgl.models.Sequence;
import com.sgl.models.UploadsHistory;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.AppPreferences;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.utils.LocationManagerReceiver;
import com.sgl.webservice.WebRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

import static android.R.id.message;

public class AddMeterReadingActivity extends ParentActivity implements View.OnClickListener, ServiceCaller, RadioGroup.OnCheckedChangeListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500, FASTEST_INTERVAL = 500, DISPLACEMENT = 0;
    private final String METER_IMAGE_DIRECTORY_NAME = "meter";
    private final String SUSPICIOUS_IMAGE_DIRECTORY_NAME = "suspicious";
    private Context mContext;
    private LinearLayout linearSuspicious;
    private EditText edtObservation, edtComments, edtAboutMeter, edtKWHReading, edtMeterNo, edtMobileNo, edtMeterNoDialog;
    private ImageView cameraSuspicious, imgSuspicious, imgMeter, cameraMeter, btnViewMore, imgBack, imgMap, imgCall;
    private Toast toast;
    private int previousMeterReading = 0, avgConsumption = 0, meterDigits = 0, assignedSequence = 0, doorLockAttempts, permLockAttempts;
    private JobCard userJobCard;
    private JsonResponse spotBillResponse;
    private TextView txtSuspiciousActivity, lblSusiImg, lblMeterReading, txtConsumerMeterNumber, txtMobileNumber;
    private Spinner mMeterStatus, mReaderStatus, mMeterType;
    private Button mSubmitAndNextBtn;
    private RadioGroup radioGroup;
    private Typeface regular, bold;
    private String mMeterReadingImageName, meterStatusCode, readerStatusCode, mMeterReadingSPImageName,
            prv_sequence, mMeterReaderId = "", meterType = "", mobileNo = "", meterStatusOnDialog = "";
    private Bitmap mBitmapMeterSuspicious = null, mBitmapMeterReading = null;
    private LocationManagerReceiver mLocationManagerReceiver;
    private RadioButton btnRadioSuspiciousYes, btnRadioSuspiciousNo, btnMobileNumberNo, btnMobileNumberYes;
    private String consumerName = "", consumerAddress = "", consumerNumber = "", consumerMeterNo = "", phoneNo = "",
            chequeImage = "", chequeNumber = "", chequeAmount = "";
    private UserProfile userProfile;
    private int arrayForReasonCode = R.array.reader_status, arrayForMeterStatus = R.array.update_meter_status_all;
    private AlertDialog alert1, alert2, alert3;
    private MeterReading meterReading;
    private MeterImage suspicious_activity_image;
    private RelativeLayout relativeUpdateMobileNo, relImage;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double currentReading = 0, meterDigit5 = 95000, meterDigit6 = 995000, meterDigit7 = 9995000, meterDigit8 = 99995000;
    private int maxLimit = 5000;
    private boolean valid = false, isForSportBill = false, isUploaded = false;
    private ArrayList<Sequence> localSequence;
    private String latitude = "", longitude = "", isSpotbill = "False";
    private File mediaStorageDir;
    Timer timer;
    long timerInterval = 1000; //1 second
    long timerDelay = 1000; //1 second
    int Count = 0;
    private String revisitFlag = "";
    private ArrayList<MeterReading> readingToUpload, deleteJobs, spotBillReadings;

    public static String meter_reader_id;
    private ProgressDialog pDialog;


    private HashMap<String, String> paymentData;
    private HashMap<String, String> paymentDataCalculated;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meter_reading);
        mContext = this;

        getUserProfileDetails();
        startTimer();
        meterReading = new MeterReading();
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        alert1 = new AlertDialog.Builder(mContext).create();
        alert2 = new AlertDialog.Builder(mContext).create();
        alert3 = new AlertDialog.Builder(mContext).create();
        lblSusiImg = findViewById(R.id.lbl_Sus_img);
        lblSusiImg.setTypeface(bold);
        lblMeterReading = findViewById(R.id.lbl_meterreading);
        lblMeterReading.setTypeface(bold);
        txtSuspiciousActivity = findViewById(R.id.Suspicious_Activity);
        txtSuspiciousActivity.setTypeface(bold);
        txtConsumerMeterNumber = findViewById(R.id.txt_consumer_meter_number);
        txtConsumerMeterNumber.setTypeface(bold);
        imgSuspicious = findViewById(R.id.img_suspicious);
        imgSuspicious.setOnClickListener(this);
        cameraSuspicious = findViewById(R.id.camera_suspicious);
        cameraSuspicious.setOnClickListener(this);
        radioGroup = findViewById(R.id.rg_yesno);
        radioGroup.setOnCheckedChangeListener(this);
        imgMeter = findViewById(R.id.img_meter);
        imgMeter.setOnClickListener(this);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        imgMap = findViewById(R.id.img_map);
        imgMap.setOnClickListener(this);
        imgCall = findViewById(R.id.img_call);
        imgCall.setOnClickListener(this);

        if (checkPlayServices()) {
            buildGoogleApiClient();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        cameraMeter = findViewById(R.id.camera_meter);
        cameraMeter.setOnClickListener(this);
        edtObservation = findViewById(R.id.edt_observation);
        edtObservation.setTypeface(bold);

        mSubmitAndNextBtn = findViewById(R.id.submit_and_next1);
        mSubmitAndNextBtn.setOnClickListener(this);
        edtComments = findViewById(R.id.comments);
        edtComments.setTypeface(bold);

        mMeterStatus = findViewById(R.id.meter_status_spinner);
        setMeterStatusSpinner(R.array.update_meter_status_all);
        mReaderStatus = findViewById(R.id.reader_status_spinner);
        setReasonCodeSpinner(arrayForReasonCode);

        btnRadioSuspiciousYes = findViewById(R.id.rb_yes);
        btnRadioSuspiciousYes.setTypeface(bold);
        btnRadioSuspiciousYes.setOnClickListener(this);
        btnRadioSuspiciousNo = findViewById(R.id.rb_no);
        btnRadioSuspiciousNo.setTypeface(bold);
        btnRadioSuspiciousNo.setOnClickListener(this);
        linearSuspicious = findViewById(R.id.linear_suspicious);
        linearSuspicious.setVisibility(View.GONE);
        btnViewMore = findViewById(R.id.btn_view_more);
        btnViewMore.setOnClickListener(this);
        relativeUpdateMobileNo = findViewById(R.id.rlupdatemobile);
        relativeUpdateMobileNo.setOnClickListener(this);
        edtAboutMeter = findViewById(R.id.edt_about_meter);
        edtAboutMeter.setTypeface(bold);

        relImage = findViewById(R.id.rel_image);
        relImage.setOnClickListener(this);
        //New fields for CESC starts, Piyush : 22/03/2017

        edtKWHReading = findViewById(R.id.edt_kwh_reading);
        edtKWHReading.setTypeface(bold);
        edtKWHReading.setEnabled(true);
        edtMeterNo = findViewById(R.id.edt_panel_no);
        edtMeterNo.setTypeface(bold);
        edtMobileNo = findViewById(R.id.edt_mobile_no);
        edtMobileNo.setTypeface(bold);

        txtMobileNumber = findViewById(R.id.txt_mobile_number);
        txtMobileNumber.setTypeface(bold);
        btnMobileNumberNo = findViewById(R.id.btn_radio_mobile_no);
        btnMobileNumberNo.setTypeface(bold);
        btnMobileNumberNo.setOnClickListener(this);
        btnMobileNumberYes = findViewById(R.id.btn_radio_mobile_yes);
        btnMobileNumberYes.setTypeface(bold);
        btnMobileNumberYes.setOnClickListener(this);
        //New fields for CESC ends, Piyush : 22/03/2017

        TextView txtObservations = findViewById(R.id.txt_observation);
        txtObservations.setTypeface(regular);

        Intent i = getIntent();
        if (i != null) {
            if (userJobCard == null) {
                userJobCard = (JobCard) i.getSerializableExtra(AppConstants.CURRENT_JOB_CARD);
                if (App.ReadingTakenBy == getString(R.string.meter_reading_qr_code)) {
                    toast = Toast.makeText(mContext, getString(R.string.one_consumer_matched), Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    TextView toastMessage = toastView.findViewById(message);
                    toastMessage.setTextSize(10);
                    toastMessage.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        toastView.setBackgroundColor(CommonUtils.getColor(mContext, R.color.black));
                    }
                    toast.show();
                }
            }

        }

        revisitFlag = userJobCard.is_revisit;

        Sequence getSeq = new Sequence();
        getSeq.meter_reader_id = userJobCard.meter_reader_id;
        getSeq.zone_code = userJobCard.zone_code;
        getSeq.route_code = userJobCard.route_code;
        getSeq.cycle_code = userJobCard.bill_cycle_code;

        localSequence = DatabaseManager.getSequence(mContext, getSeq);
        if (localSequence != null) {
            assignedSequence = Integer.parseInt(localSequence.get(0).sequence);
        }
        initAllUIComponents();

        mMeterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                meterStatusCode = mMeterStatus.getSelectedItem().toString().trim();
                if (meterStatusCode.equals(getString(R.string.normal))) {
                    arrayForReasonCode = R.array.reader_status_normal;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                } else if (meterStatusCode.equals(getString(R.string.meter_status_faulty))) {
                    arrayForReasonCode = R.array.reader_status_faulty;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                } else if (meterStatusCode.equals(getString(R.string.rcnt))) {
                    arrayForReasonCode = R.array.reader_status_rcnt;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(false);
                } else {
                    arrayForReasonCode = R.array.reader_status;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(mContext, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

        mReaderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                readerStatusCode = mReaderStatus.getSelectedItem().toString().trim();
                if (readerStatusCode.equals(getString(R.string.meter_number_mismatch)) || readerStatusCode.equals(getString(R.string.meter_change)))
                    showMeterNumberDialog(mContext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        mMeterType = findViewById(R.id.meter_type_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.meter_type)) {
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
        mMeterType.setAdapter(adapter2);
        mMeterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                meterType = mMeterType.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        mMeterReadingImageName = "ME_" + userJobCard.job_card_id + "_" + userJobCard.meter_reader_id;
        mMeterReadingSPImageName = "SP_" + userJobCard.job_card_id + "_" + userJobCard.meter_reader_id;

        edtMobileNo.setVisibility(View.GONE);

        edtMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateNumber();
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateNumber();
            }
        });

        edtKWHReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int start, int before, int count) {
                if (count == 0) {
                    arrayForMeterStatus = R.array.update_meter_status_all;
                    setMeterStatusSpinner(arrayForMeterStatus);
                } else {
                    arrayForMeterStatus = R.array.update_meter_status_without_rcnt;
                    setMeterStatusSpinner(arrayForMeterStatus);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                if (editable.length() > 0 && editable.length() < 10) {
                    String valuee = editable.toString();

                    double value = Double.parseDouble(valuee.substring(0, 1));

                    if (value == 0) {

                        InputFilter filter = new InputFilter() {
                            final int maxDigitsBeforeDecimalPoint = 1;
                            final int maxDigitsAfterDecimalPoint = 3;

                            @Override
                            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                                StringBuilder builder = new StringBuilder(dest);
                                builder.replace(dstart, dend, source.subSequence(start, end).toString());
                                if (!builder.toString().matches("(([0-9]{1})([0-9]{0," + (maxDigitsBeforeDecimalPoint - 1) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimalPoint + "})?"

                                )) {
                                    if (source.length() == 0)
                                        return dest.subSequence(dstart, dend);
                                    return "";
                                }

                                return null;
                            }
                        };

                        edtKWHReading.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//                        edtKWHReading.setFilters(new InputFilter[]{filter});
                    } else {
                        edtKWHReading.setInputType(InputType.TYPE_CLASS_NUMBER);
//                        edtKWHReading.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    }
                }
            }
        });

        //Delete reference of previous photo taken starts, Piyush : 07-03-17
        File fileDeleteMeter = getMeterFilePath(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
        File fileDeleteSuspicious = getMeterFilePath(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
        try {
            if (fileDeleteMeter.exists() || fileDeleteSuspicious.exists()) {
                fileDeleteMeter.delete();
                fileDeleteSuspicious.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Delete reference of previous photo taken ends, Piyush : 07-03-17


        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            mMeterReaderId = userProfile.meter_reader_id;
        }

        try {
            phoneNo = userJobCard.phone_no;
//            phoneNo = "9874521635";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(mContext, getString(R.string.this_device_is_not_supported), Toast.LENGTH_LONG).show();
            }
            return false;
        }

        return true;
    }

    private void initAllUIComponents() {
        consumerName = userJobCard.consumer_name == null || userJobCard.consumer_name.equals("None") || userJobCard.consumer_name.equals("0") || userJobCard.consumer_name.equalsIgnoreCase("NULL") ? "" : userJobCard.consumer_name;
        consumerAddress = userJobCard.address == null || userJobCard.address.equals("None") || userJobCard.address.equals("0") || userJobCard.address.equalsIgnoreCase("NULL") ? "" : userJobCard.address;
        consumerNumber = userJobCard.consumer_no == null || userJobCard.consumer_no.equals("None") || userJobCard.consumer_no.equals("0") || userJobCard.consumer_no.equalsIgnoreCase("NULL") ? "" : userJobCard.consumer_no;
        consumerMeterNo = userJobCard.meter_no == null || userJobCard.meter_no.equals("None") || userJobCard.meter_no.equals("0") || userJobCard.meter_no.equalsIgnoreCase("NULL") ? "" : userJobCard.meter_no;
        mobileNo = userJobCard.phone_no == null || userJobCard.phone_no.equals("None") || userJobCard.phone_no.equals("0") || userJobCard.phone_no.equalsIgnoreCase("NULL") ? "" : userJobCard.phone_no;

        previousMeterReading = (userJobCard.prv_meter_reading == null || userJobCard.prv_meter_reading.equalsIgnoreCase("None")) ? 0 : (int) Math.round(Math.floor(Double.valueOf(userJobCard.prv_meter_reading)));
        avgConsumption = (userJobCard.avg_consumption == null || userJobCard.avg_consumption.equals("") || userJobCard.avg_consumption.equals("None")) ? 0 : Integer.parseInt(userJobCard.avg_consumption);
        meterDigits = (userJobCard.meter_digit == null || userJobCard.meter_digit.equals("") || userJobCard.meter_digit.equals("None")) ? 0 : Integer.parseInt(userJobCard.meter_digit);
        txtConsumerMeterNumber.setText(userJobCard.account_no + " | " + consumerMeterNo);
        txtConsumerMeterNumber.setSelected(true);

        if (!mobileNo.equals("") || mobileNo.length() == 10) {
            imgCall.setVisibility(View.VISIBLE);
            edtMobileNo.setText(mobileNo);
        }

        edtMeterNo.setHint(getString(R.string.meter_no) + ": " + consumerMeterNo);
        edtMeterNo.setVisibility(View.GONE);
        edtAboutMeter.setText(userJobCard.location_guidance);
//        edtKWHReading.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});

        latitude = userJobCard.lattitude;
        longitude = userJobCard.longitude;
        AppPreferences.getInstance(mContext).putString(AppConstants.DESTINATION_LAT, latitude);
        AppPreferences.getInstance(mContext).putString(AppConstants.DESTINATION_LONG, longitude);
        if (latitude.length() > 3 && longitude.length() > 3)
            imgMap.setVisibility(View.VISIBLE);
        else
            imgMap.setVisibility(View.INVISIBLE);
    }

    private void checkValidation() {
        currentReading = Double.parseDouble(edtKWHReading.getText().toString().trim());
        if (currentReading > previousMeterReading) {
            double advance = currentReading - previousMeterReading;
            if (avgConsumption > 0) {
//                if (advance > (2 * avgConsumption)) {
                if (advance > (3 * avgConsumption)) {    //for SGL
                    showOverConsumptionDialog(mContext);
                } else {
//                    showMessageDialogForMeterType(mContext);
                    showMessageDialogForNameChange(mContext);
                }
            } else {
//                showMessageDialogForMeterType(mContext);
                showMessageDialogForNameChange(mContext);

            }
        } else {
            if (currentReading < previousMeterReading) {
                if (meterDigits == 5) {
                    if (previousMeterReading > meterDigit5) {
//                        checkForRoundComplete();
//                        submitReading();
                        abnormalCondition();


                    } else {
                        abnormalCondition();
                    }
                } else {
                    if (meterDigits == 6) {
                        if (previousMeterReading > meterDigit6) {
//                            checkForRoundComplete();
//                            submitReading();
                            abnormalCondition();

                        } else {
                            abnormalCondition();
                        }
                    } else {
                        if (meterDigits == 7) {
                            if (previousMeterReading > meterDigit7) {
//                                checkForRoundComplete();
//                                submitReading();
                                abnormalCondition();

                            } else {
                                abnormalCondition();
                            }
                        } else {
                            if (meterDigits == 8) {
                                if (previousMeterReading > meterDigit8) {
//                                    checkForRoundComplete();
//                                    submitReading();
                                    abnormalCondition();

                                } else {
                                    abnormalCondition();
                                }
                            } else {
                                abnormalCondition();
                            }
                        }
                    }
                }
            } else {
                if (currentReading == previousMeterReading) {
//                    showMessageDialogForMeterType(mContext);
                    showMessageDialogForNameChange(mContext);


                }
            }
        }
    }

    private void checkForRoundComplete() {
        currentReading = Integer.parseInt(edtKWHReading.getText().toString().trim());
        if (currentReading < maxLimit) {
            showRoundCompleteDialog(mContext);
        } else {
            abnormalCondition();
        }
    }

    private void abnormalCondition() {
        showAbnormalReadingDialog(mContext);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstants.CAMERA_RESULT_CODE:
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    meterReading.cur_lat = String.valueOf(mLastLocation.getLatitude());
                    meterReading.cur_lng = String.valueOf(mLastLocation.getLongitude());
                } else {
                    meterReading.cur_lat = userJobCard.lattitude;
                    meterReading.cur_lng = userJobCard.longitude;
                }
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                mBitmapMeterReading = getBitmapScaled(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
                if (mBitmapMeterReading != null) {
                    imgMeter.setImageBitmap(mBitmapMeterReading);
                }
                mediaStorageDir.deleteOnExit();
                break;
            case AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE:
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                mBitmapMeterSuspicious = getBitmapScaled(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
                if (mBitmapMeterSuspicious != null) {
                    imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
                }
                mediaStorageDir.deleteOnExit();
                break;

            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationManagerReceiver.getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                        break;
                }
                break;

            case AppConstants.CHEQUE_COLLECTION_SCREEN:
                if (resultCode == Activity.RESULT_OK) {
                    chequeImage = data.getStringExtra(AppConstants.CHEQUE_COLLECTION_IMAGE);
                    chequeAmount = data.getStringExtra(AppConstants.CHEQUE_COLLECTION_AMOUNT);
                    chequeNumber = data.getStringExtra(AppConstants.CHEQUE_COLLECTION_NUMBER);
                    saveToDatabase();
                }
                break;
        }
    }

    private Bitmap getBitmapScaled(String dirname, String filename) {
        Bitmap compressedImage = null;
        try {
            File file = getMeterFilePath(dirname, filename);
            compressedImage = new Compressor.Builder(mContext)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(1)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .build()
                    .compressToBitmap(file);
            compressedImage = Bitmap.createScaledBitmap(compressedImage, 640, 480, false);
            if (compressedImage != null)
                compressedImage = CommonUtils.addWaterMarkDate(compressedImage, CommonUtils.getCurrentDateTime2());
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return compressedImage;
    }

    public Uri getOutputMediaFileUri(String dirname, String filename) {
        File file = getMeterFilePath(dirname, filename);
        return FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
    }

    public File getMeterFilePath(String dirname, String filename) {
        // External sdcard location
        mediaStorageDir = new File(Environment.getExternalStorageDirectory(), dirname);

        // Create imageDir
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File createdFile = new File(mediaStorageDir.getPath() + File.separator + filename + ".jpg");

        return createdFile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

        Log.d("Click","Clickkkkkk"+v.getId());
        if (v == imgBack) {
            stopTimer();
            finish();
        } else if (v == cameraMeter || v == imgMeter) {
            mLocationManagerReceiver = new LocationManagerReceiver(mContext);

            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            PackageManager pm = mContext.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

            if (isGPSEnabled && hasGps) {
                createLocationRequest();
                startLocationUpdates();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri fileUri = null;
                fileUri = getOutputMediaFileUri(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
                List<ResolveInfo> resolvedIntentActivities = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;

                    mContext.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, AppConstants.CAMERA_RESULT_CODE);
            } else {
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
            }
        } else if (((v == cameraSuspicious || v == imgSuspicious) && (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes))) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            fileUri = getOutputMediaFileUri(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
            List<ResolveInfo> resolvedIntentActivities = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;

                mContext.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE);
        }
        //To show/hide suspicious view starts, Piyush : 02-03-17
        else if (v == btnRadioSuspiciousYes) {
            linearSuspicious.setVisibility(View.VISIBLE);
        } else if (v == btnRadioSuspiciousNo) {
            edtObservation.setText("");
            mBitmapMeterSuspicious = null;
            imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
            linearSuspicious.setVisibility(View.GONE);
        }
        //To show/hide suspicious view ends, Piyush : 02-03-17

        //To show the details of consumer starts, Piyush : 02-03-17
        else if (v == btnViewMore) {
            DialogCreator.showConsumerDetailsDialog(mContext, consumerName, consumerAddress, consumerNumber, consumerMeterNo,
                    userJobCard.bill_cycle_code, userJobCard.zone_code, userJobCard.phone_no, userJobCard.route_code,
                    userJobCard.sd_amount, userJobCard.excluding_sd_amount, userJobCard.total_amount);
        }
        //To show the details of consumer ends, Piyush : 02-03-17
        /*else if (v == mSubmitAndNextBtn) {

        }*/ else if (v == imgMap) {
            Intent intent = new Intent(mContext, GoogleMapActivity.class);
            startActivity(intent);
        } else if (v == imgCall) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + mobileNo));
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(callIntent);
        } else if (v == mSubmitAndNextBtn){
            Log.d("Button Click","Button Clickkkkkk"+mSubmitAndNextBtn.getId());
            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            PackageManager pm = mContext.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            if (isGPSEnabled && hasGps) {
                doSubmitOps();
            } else {
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
            }
        }
    }

    private void doSubmitOps() {
        Log.d("gggggggggggggg","");

        String current_kwh = edtKWHReading.getText().toString().trim();

        meterReading.job_card_id = userJobCard.job_card_id;
        meterReading.meter_reader_id = userJobCard.meter_reader_id;
        meterReading.isRevisit = userJobCard.is_revisit;

        String meter_image = null;
        if ((imgMeter.getDrawable()) != null) {
            meter_image = CommonUtils.getBitmapEncodedString(mBitmapMeterReading);
            MeterImage meterImage = new MeterImage();
            meterImage.image = meter_image;
            meterReading.meter_image = meterImage;
            meterReading.isUploaded = "False";

            String meterStatus = mMeterStatus.getSelectedItem().toString().trim();
            String readerStatus = mReaderStatus.getSelectedItem().toString().trim();

            if (meterStatus.equals(getString(R.string.meter_status_mandatory))) {
                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), getString(R.string.error));
            } else {
                if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), getString(R.string.error));
                } else {
                    if (meterStatus.equals(getString(R.string.rcnt)) || readerStatus.equalsIgnoreCase(getString(R.string.meter_change))) {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                            meterReading.suspicious_activity = "True";
                            String encodedImage = "";
                            if (mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                meterReading.suspicious_activity_image = suspicious_activity_image;

                                if (edtObservation.getText().toString() != null) {
                                    String obs = edtObservation.getText().toString().trim();
                                    meterReading.suspicious_remark = obs;
                                }
                                if (validateNumber()) {
//                                    showMessageDialogForMeterType(mContext);
                                    showMessageDialogForNameChange(mContext);

                                }
                            } else {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), getString(R.string.error));
                            }
                        } else {
                            meterReading.suspicious_activity = "False";
                            if (validateNumber()) {
//                                showMessageDialogForMeterType(mContext);
                                showMessageDialogForNameChange(mContext);
                            }
                        }
                    } else {
                        if (current_kwh.isEmpty()) {
                            DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_reading), getString(R.string.error));
                        } else {
                            if (meterStatus.equals(getString(R.string.meter_status_mandatory))) {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), getString(R.string.error));
                            } else {
                                if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), getString(R.string.error));
                                } else {
                                    if (readerStatus.equalsIgnoreCase(getString(R.string.meter_change))) {
                                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                                            meterReading.suspicious_activity = "True";
                                            String encodedImage = "";
                                            if (mBitmapMeterSuspicious != null) {
                                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                                suspicious_activity_image = new MeterImage();
                                                suspicious_activity_image.image = encodedImage;
                                                meterReading.suspicious_activity_image = suspicious_activity_image;

                                                if (edtObservation.getText().toString() != null) {
                                                    String obs = edtObservation.getText().toString().trim();
                                                    meterReading.suspicious_remark = obs;
                                                }
                                                if (validateNumber()) {
//                                                    showMessageDialogForMeterType(mContext);
                                                    showMessageDialogForNameChange(mContext);

                                                }
                                            } else {
                                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), getString(R.string.error));
                                            }
                                        } else {
                                            meterReading.suspicious_activity = "False";
                                            if (validateNumber()) {
                                                checkValidation();
                                            }
                                        }
                                    } else {
                                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                                            meterReading.suspicious_activity = "True";
                                            String encodedImage = "";
                                            if (mBitmapMeterSuspicious != null) {
                                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                                suspicious_activity_image = new MeterImage();
                                                suspicious_activity_image.image = encodedImage;
                                                meterReading.suspicious_activity_image = suspicious_activity_image;

                                                if (edtObservation.getText().toString() != null) {
                                                    String obs = edtObservation.getText().toString().trim();
                                                    meterReading.suspicious_remark = obs;
                                                }
                                                if (validateNumber()) {
                                                    checkValidation();
                                                }
                                            } else {
                                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), getString(R.string.error));
                                            }
                                        } else {
                                            meterReading.suspicious_activity = "False";
                                            if (validateNumber()) {
                                                checkValidation();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            DialogCreator.showMessageDialog(mContext, getString(R.string.blank_meter_reading_image), getString(R.string.error));
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private void saveToDatabase() {


        String addComments = edtComments.getText().toString().trim();
        String mobileNo = edtMobileNo.getText().toString().trim();
        String locationGuidance = edtAboutMeter.getText().toString().trim();
        String current_kwh = edtKWHReading.getText().toString().trim();

        if (current_kwh.equals("")) {
            meterReading.current_meter_reading = "0";
        } else {
            meterReading.current_meter_reading = current_kwh;
        }

        meterReading.time_taken = stopTimer();

        meterReading.job_card_id = userJobCard.job_card_id;
        meterReading.meter_reader_id = userJobCard.meter_reader_id;
        meterReading.isRevisit = userJobCard.is_revisit;

        meterReading.reader_remark_comment = addComments;
        meterReading.mobile_no = mobileNo == null || mobileNo.equalsIgnoreCase("") ? "0" : mobileNo;
        meterReading.reader_status = readerStatusCode;
        meterReading.meter_status = meterStatusCode;
        meterReading.reading_month = userJobCard.schedule_month;

        meterReading.meter_no = consumerMeterNo;

        meterReading.location_guidance = locationGuidance == "" ? userJobCard.location_guidance : locationGuidance;
        meterReading.zone_code = userJobCard.zone_code;

        meterReading.door_lock_reading_attempt = "" + doorLockAttempts;
        meterReading.permanently_locked_reading_attempt = "" + permLockAttempts;

        if (chequeImage.length() > 10) {
            MeterImage meterImage = new MeterImage();
            meterImage.image = chequeImage;
            meterReading.cheque_image = meterImage;

            meterReading.cheque_number = chequeNumber;
            meterReading.cheque_amount = chequeAmount;
        }

        if (meterReading.meter_type == null) {
            meterReading.meter_type = "";
        }

        // Sequence Logic starts
        meterReading.prv_sequence = userJobCard.prv_sequence;
        meterReading.new_sequence = userJobCard.meter_reader_id + "|" + userJobCard.zone_code + "|" + userJobCard.bill_cycle_code + "|" + userJobCard.route_code + "|" + String.valueOf(String.format("%04d", assignedSequence));


        meterReading.reading_date = CommonUtils.getCurrentDateTime();
        meterReading.reading_taken_by = App.ReadingTakenBy;

        // Start New Field for Spot Billing Added by Saloni /**10/07/2019**/
        meterReading.spot_billing = "True";

        meterReading.due_date = "";
        meterReading.payment = userJobCard.payment;
        meterReading.emi_security_dep = userJobCard.emi_security_dep;
        meterReading.pre_due = userJobCard.pre_due;
        meterReading.total_price = userJobCard.total_price;
        meterReading.consump_days = calculateConsumptionDays(CommonUtils.getDate(), userJobCard.prev_reading_date);

//        meterReading.due_date = "";
        // End New Field for Spot Billing Added by Saloni /**10/07/2019**/

        //TODO :

        /*
        Sequence updateSequence = new Sequence();
        updateSequence.meter_reader_id = userJobCard.meter_reader_id;
        updateSequence.cycle_code = userJobCard.bill_cycle_code;
        updateSequence.route_code = userJobCard.route_code;
        updateSequence.zone_code = userJobCard.zone_code;
        updateSequence.sequence = String.valueOf(++assignedSequence);
        DatabaseManager.UpdateSequence(mContext, updateSequence);



        if (userJobCard.job_card_status.equalsIgnoreCase(AppConstants.JOB_CARD_STATUS_COMPLETED))
            DatabaseManager.saveMeterReading(mContext, meterReading);
        else {
            DatabaseManager.saveMeterReading(mContext, meterReading);
            DatabaseManager.saveJobCardStatus(mContext, userJobCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        */

        // TODO ends


        /*mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            meterReading.cur_lat = String.valueOf(mLastLocation.getLatitude());
            meterReading.cur_lng = String.valueOf(mLastLocation.getLongitude());
        } else {
            meterReading.cur_lat = "0";
            meterReading.cur_lng = "0";
        }*/

        //Changes for Online Reading Ends Avinesh 6-04-17

        mBitmapMeterReading = null;
        mBitmapMeterSuspicious = null;

        //delete saved images folder
        File meterFile = new File(Environment.getExternalStorageDirectory(), METER_IMAGE_DIRECTORY_NAME);
        if (meterFile.isDirectory()) {
            String[] children = meterFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(meterFile, children[i]).delete();
            }
            meterFile.delete();
        }
        File spFile = new File(Environment.getExternalStorageDirectory(), SUSPICIOUS_IMAGE_DIRECTORY_NAME);
        if (spFile.isDirectory()) {
            String[] children = spFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(spFile, children[i]).delete();
            }
            spFile.delete();
        }

        if (mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.meter_status_lock_premise))
                || mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.meter_status_meter_missing))
                || mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.meter_status_inaccessible))) {
            if (CommonUtils.checkAndRequestPermissions(mContext, this)) {
                if (phoneNo.length() != 0) {
                    if (CommonUtils.isNetworkAvailable(mContext)) {
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    } else {
                        Toast.makeText(mContext, R.string.error_internet_not_connected, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.message_can_not_send_to_this_consumer), Toast.LENGTH_LONG).show();
                }
            }
        }

/*
        try {
            sendSMSForDoorLock();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        calculatePaymentData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submitReading() {
        String sequence = userJobCard.prv_sequence;
        if (sequence.length() > 0) {
            int seq = Integer.parseInt(sequence) + 1;
            prv_sequence = "" + seq;
        }

        /*doorLockAttempts = Integer.parseInt(userJobCard.door_lock_reading_attempt);
        permLockAttempts = Integer.parseInt(userJobCard.permanently_locked_reading_attempt);

        if(mReaderStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.door_lock)) && doorLockAttempts < 2)
        {
            doorLockAttempts++;
            String doorLockReadingAttempts = ""+doorLockAttempts;
            DatabaseManager.saveReadingAttempts(mContext, userJobCard, doorLockReadingAttempts, "1");
            launchNext();
        }
        else if(mReaderStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.permanently_locked)) && permLockAttempts < 2)
        {
            permLockAttempts++;
            String permLockedReadingAttempts = ""+permLockAttempts;
            DatabaseManager.saveReadingAttempts(mContext, userJobCard, "1", permLockedReadingAttempts);
            launchNext();
        }
        else
        {*/
//        if(mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.rcnt)))
        saveToDatabase();
//        else
//            chequeCollection();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void chequeCollection() {
        int totalOutstandingAmount = Integer.parseInt(userJobCard.total_amount);
        if (totalOutstandingAmount > 2000) {
            checkCollectionDialog(mContext, userJobCard.sd_amount, userJobCard.excluding_sd_amount, userJobCard.total_amount);
        } else {
            saveToDatabase();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)
            edtObservation.setEnabled(true);
        else
            edtObservation.setEnabled(false);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.d("Latitude: "+mLastLocation.getLatitude(), "Longitude: "+mLastLocation.getLongitude());
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    public void launchNext() {
        ArrayList<JobCard> job = DatabaseManager.getJobCardBySequence(mMeterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED, prv_sequence, userJobCard.route_code);
        if (job != null) {
            Intent i = new Intent(mContext, AddMeterReadingActivity.class);
            i.putExtra(AppConstants.CURRENT_JOB_CARD, job.get(0));
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            //finish();
            Intent i = new Intent(mContext, LandingActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("IS_REVISIT", revisitFlag);
            i.putExtra("Activity", "AddMeterReadingActivity");
            startActivity(i);
        }
    }

    private boolean validateNumber() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String PHONE_PATTERN1 = "^[7-9][0-9]{9}$";
        final String PHONE_PATTERN2 = "";
        pattern1 = Pattern.compile(PHONE_PATTERN1);
        pattern2 = Pattern.compile(PHONE_PATTERN2);
        String phone = edtMobileNo.getText().toString().trim();
        matcher1 = pattern1.matcher(phone);
        matcher2 = pattern2.matcher(phone);

        if (matcher1.matches() || matcher2.matches()) {
            edtMobileNo.setError(null);
            return true;
        } else {
            edtMobileNo.setError(getString(R.string.enter_valid_mobile_no));
            return false;
        }
    }



    //Dialog for validation Dialog start Piyush : 01-04-17
    public void showAbnormalReadingDialog(final Context context) {
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_abnormal_reading, null);
        meterStatusOnDialog = "";

        final TextView txtTitle, txtErrorMessage, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = promptView.findViewById(R.id.txt_title);
        txtErrorMessage = promptView.findViewById(R.id.txt_error_message);
        positive = promptView.findViewById(R.id.btn_yes);
        negative = promptView.findViewById(R.id.btn_no);
        txtError = promptView.findViewById(R.id.txt_error);
        editText = promptView.findViewById(R.id.edt_kwh_reading);
//        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});
        final Spinner mMeterStatusDialog = promptView.findViewById(R.id.meter_status);
        final Spinner mReaderStatusDialog = promptView.findViewById(R.id.reason_code);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.update_meter_status_without_rcnt)) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mMeterStatusDialog.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.reader_status)) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mReaderStatusDialog.setAdapter(adapter1);

        mMeterStatusDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meterStatusOnDialog = mMeterStatusDialog.getSelectedItem().toString().trim();
                if (meterStatusOnDialog.equals(getString(R.string.normal))) {
                    if (readerStatusCode.equals(getString(R.string.meter_number_mismatch))) {
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.array_reader_status_meter_mismatch)) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                                ((TextView) v).setTextSize(14f);
                                return v;
                            }

                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View v = super.getDropDownView(position, convertView, parent);
                                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                                ((TextView) v).setTextSize(14f);
                                return v;
                            }
                        };
                        mReaderStatusDialog.setAdapter(adapter1);
                    } else {

                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.array_reader_actual_reading)) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                                ((TextView) v).setTextSize(14f);
                                return v;
                            }

                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View v = super.getDropDownView(position, convertView, parent);
                                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                                ((TextView) v).setTextSize(14f);
                                return v;
                            }
                        };
                        mReaderStatusDialog.setAdapter(adapter2);
                    }
                } else if (meterStatusOnDialog.equals(getString(R.string.meter_status_faulty))) {
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.reader_status_faulty)) {
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                            ((TextView) v).setTextSize(14f);
                            return v;
                        }

                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);
                            ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                            ((TextView) v).setTextSize(14f);
                            return v;
                        }
                    };
                    mReaderStatusDialog.setAdapter(adapter1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        txtErrorMessage.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        txtError.setTypeface(regular);
        editText.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!valid) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.please_enter_reading));
                    } else {
                        txtError.setVisibility(View.GONE);
                        int val = Integer.parseInt(editText.getText().toString().trim());
                        if (currentReading == val) {
                            mMeterStatusDialog.setVisibility(View.VISIBLE);
                            mReaderStatusDialog.setVisibility(View.VISIBLE);
                            valid = true;
                        } else {
                            txtError.setVisibility(View.VISIBLE);
                            txtError.setText(getString(R.string.reading_does_not_match));
                        }
                    }
                } else {

                    if (mMeterStatusDialog.getSelectedItem().equals(getString(R.string.meter_status_mandatory)) ||
                            mReaderStatusDialog.getSelectedItem().equals(getString(R.string.reader_status_mandatory))) {
//                        Toast.makeText(mContext, "Please Add meter Status", Toast.LENGTH_LONG);
                    } else {
                        valid = false;
                        txtError.setVisibility(View.GONE);
                        meterStatusCode = mMeterStatusDialog.getSelectedItem().toString();
                        readerStatusCode = mReaderStatusDialog.getSelectedItem().toString();
//                        showMessageDialogForMeterType(mContext);
                        showMessageDialogForNameChange(mContext);

                        alert1.dismiss();
                    }
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                txtError.setVisibility(View.GONE);
                mMeterStatusDialog.setVisibility(View.GONE);
                mReaderStatusDialog.setVisibility(View.GONE);
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                valid = false;
                alert1.dismiss();
            }
        });

        alert1.setView(promptView);
        alert1.setCancelable(false);
        if (alert1.isShowing()) {
            alert2.dismiss();
            alert3.dismiss();
        } else {
            alert1.show();
        }
        //OK button code ends
    }


    //Dialog for validation Dialog start Piyush : 01-04-17
    public void showOverConsumptionDialog(final Context context) {
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_over_consumption, null);

        final TextView txtTitle, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = promptView.findViewById(R.id.txt_title);
        positive = promptView.findViewById(R.id.btn_yes);
        negative = promptView.findViewById(R.id.btn_no);
        editText = promptView.findViewById(R.id.edt_kwh_reading);
//        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});
        txtError = promptView.findViewById(R.id.txt_error);
        //Initialising all fields ends

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        editText.setTypeface(regular);
        txtError.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editText.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                    int val = Integer.parseInt(editText.getText().toString().trim());
                    if (currentReading == val) {
//                        showMessageDialogForMeterType(mContext);
                        showMessageDialogForNameChange(mContext);

                        alert2.dismiss();
                    } else {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.reading_does_not_match));
                    }
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                txtError.setVisibility(View.GONE);
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alert2.dismiss();
            }
        });

        alert2.setView(promptView);
        alert2.setCancelable(false);
        if (alert2.isShowing()) {
            alert1.dismiss();
            alert3.dismiss();
        } else {
            alert2.show();
        }
        //OK button code ends
    }

    //Dialog for validation Dialog start Piyush : 01-04-17
    public void showRoundCompleteDialog(final Context context) {
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_round_complete, null);

        final TextView txtTitle, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = promptView.findViewById(R.id.txt_title);
        positive = promptView.findViewById(R.id.btn_yes);
        negative = promptView.findViewById(R.id.btn_no);
        editText = promptView.findViewById(R.id.edt_kwh_reading);
//        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});
        txtError = promptView.findViewById(R.id.txt_error);
        txtError.setVisibility(View.GONE);
        //Initialising all fields ends

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        editText.setTypeface(regular);
        txtError.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editText.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                    int value = Integer.parseInt(editText.getText().toString().trim());
                    if (currentReading == value) {
                        meterStatusCode = "Normal";
                        readerStatusCode = "Round Complete";
                        double advance = (Math.pow(10, meterDigits) + currentReading) - previousMeterReading;
                        int valueAdvance = (int) advance;
//                        showMessageDialogForMeterType(mContext);
                        showMessageDialogForNameChange(mContext);

                        alert3.dismiss();
                    } else {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.reading_does_not_match));
                    }
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                txtError.setVisibility(View.GONE);
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alert3.dismiss();
            }
        });

        alert3.setView(promptView);
        alert3.setCancelable(false);
        if (alert3.isShowing()) {
            alert1.dismiss();
            alert2.dismiss();
        } else {
            alert3.show();
        }
        //OK button code ends
    }
    //Dialog for validation Dialog ends Piyush : 01-04-17

    private void showMeterNumberDialog(Context mContext) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_meter_number, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

        edtMeterNoDialog = promptView.findViewById(R.id.edt_meter_no);
        edtMeterNoDialog.setTypeface(regular);

        final TextView txtError = promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);

        Button ok = promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (edtMeterNoDialog.getText().toString().trim().isEmpty()) {
                    edtMeterNo.setText("");
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    edtMeterNo.setText(edtMeterNoDialog.getText().toString().trim());
                    consumerMeterNo = edtMeterNoDialog.getText().toString().trim();
                    alert.dismiss();
                }
            }
        });

        Button cancel = promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }
    //Dialog for validation Dialog ends Piyush : 01-04-17

    private void checkCollectionDialog(final Context mContext, String sdAmount, String exSdAmount, String totalAmount) {
        Typeface regular = App.getSansationRegularFont(), bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_check_collection, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

        final TextView lblSdAmount, lblExSdAmount, lblTotalAmount, txtSdAmount, txtExSdAmount, txtTotalAmount;

        lblSdAmount = promptView.findViewById(R.id.lbl_sd_amount);
        lblSdAmount.setTypeface(bold);
        lblExSdAmount = promptView.findViewById(R.id.lbl_ex_sd_amount);
        lblExSdAmount.setTypeface(bold);
        lblTotalAmount = promptView.findViewById(R.id.lbl_total_amount);
        lblTotalAmount.setTypeface(bold);

        txtSdAmount = promptView.findViewById(R.id.txt_sd_amount);
        txtSdAmount.setTypeface(regular);
        txtSdAmount.setText(sdAmount);
        txtExSdAmount = promptView.findViewById(R.id.txt_ex_sd_amount);
        txtExSdAmount.setTypeface(regular);
        txtExSdAmount.setText(exSdAmount);
        txtTotalAmount = promptView.findViewById(R.id.txt_total_amount);
        txtTotalAmount.setTypeface(regular);
        txtTotalAmount.setText(totalAmount);

        Button ok = promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChequeCollectionActivity.class);
                intent.putExtra(AppConstants.CURRENT_JOB_CARD, userJobCard);
                startActivityForResult(intent, AppConstants.CHEQUE_COLLECTION_SCREEN);
                alert.dismiss();
            }
        });

        Button cancel = promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveToDatabase();
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }
    //Dialog for validation Dialog ends Piyush : 01-04-17

    private void setMeterStatusSpinner(int meterStatus) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(meterStatus)) {
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
        mMeterStatus.setAdapter(adapter);
    }

    private void setReasonCodeSpinner(int reasonCode) {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(reasonCode)) {
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
        mReaderStatus.setAdapter(adapter1);
        mReaderStatus.setSelection(0);
    }

    // Dialog For Meter Type Added 23/01/2019


    private void showMessageDialogForMeterType(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText("Select Meter Type");
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setVisibility(View.GONE);
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);
        edtPassword.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        ok.setTypeface(regular);
        ok.setText("Smart Meter ");
        ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                meterReading.meter_type = "Smart Meter";
                alert.dismiss();
//                submitReading();

                /*if (!meterStatusCode.equals(getString(R.string.rcnt))) {
                    showMessageDialogPrintBill(context);
                } else if (!meterStatusCode.equals(getString(R.string.meter_faulty))) {
                    showMessageDialogPrintBill(context);
                } else {
                    submitReading();
                }*/
                submitReading();

            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setBackground(getResources().getDrawable(R.drawable.positive_button));
        cancel.setText("Normal Meter");
//        cancel.setLayoutParams(params);
        cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                meterReading.meter_type = "Normal Meter";
                alert.dismiss();
//                submitReading();
                /*if (meterStatusCode.equals(getString(R.string.normal))) {
                    showMessageDialogPrintBill(context);
                } else {
                    submitReading();
                }*/
                submitReading();

            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

    // Dialog for Change Name

    private void showMessageDialogForNameChange(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_change, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final EditText edtValue;
        final TextView txtTitle, txtError, txtSubTitle, txtValue;
        final LinearLayout linearButtonsYesNo, linearButtonsSubmit;

        linearButtonsYesNo = (LinearLayout) promptView.findViewById(R.id.linear_buttons);
        linearButtonsSubmit = (LinearLayout) promptView.findViewById(R.id.linear_buttons_submit);

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(getString(R.string.is_consumer_name_correct));
        txtTitle.setTypeface(regular);

        txtSubTitle = (TextView) promptView.findViewById(R.id.txt_sub_title);
        txtSubTitle.setText(getString(R.string.consumer_name_value));
        txtSubTitle.setTypeface(regular);

        txtValue = (TextView) promptView.findViewById(R.id.txt_value);
        txtValue.setText(userJobCard.consumer_name);
        txtValue.setTypeface(regular);


        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtValue = (EditText) promptView.findViewById(R.id.edt_value);
        edtValue.setHint(getString(R.string.consumer_name)+ "*");
        edtValue.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                meterReading.name_change = "False";
                showMessageDialogForAddressChange(mContext);
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setBackground(getResources().getDrawable(R.drawable.positive_button));
//        cancel.setLayoutParams(params);
        no.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                edtValue.setVisibility(View.VISIBLE);
                linearButtonsYesNo.setVisibility(View.GONE);
                linearButtonsSubmit.setVisibility(View.VISIBLE);
            }
        });

        Button update = (Button) promptView.findViewById(R.id.btn_update);
        update.setTypeface(regular);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtValue.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(getString(R.string.please_enter_name));
                } else {
                    meterReading.name_change = "True";
                    meterReading.changed_name = edtValue.getText().toString();
                    showMessageDialogForAddressChange(mContext);
                    alert.dismiss();
                }



            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterReading.name_change = "False";
                showMessageDialogForAddressChange(mContext);
                alert.dismiss();
            }
        });


        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

    // Dialog for Change Address

    private void showMessageDialogForAddressChange(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_change, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final EditText edtValue;
        final TextView txtTitle, txtError, txtSubTitle, txtValue;
        final LinearLayout linearButtonsYesNo, linearButtonsSubmit;

        linearButtonsYesNo = (LinearLayout) promptView.findViewById(R.id.linear_buttons);
        linearButtonsSubmit = (LinearLayout) promptView.findViewById(R.id.linear_buttons_submit);

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(getString(R.string.is_consumer_address_correct));
        txtTitle.setTypeface(regular);

        txtSubTitle = (TextView) promptView.findViewById(R.id.txt_sub_title);
        txtSubTitle.setText(getString(R.string.consumer_address_value));
        txtSubTitle.setTypeface(regular);

        txtValue = (TextView) promptView.findViewById(R.id.txt_value);
        txtValue.setText(userJobCard.address);
        txtValue.setTypeface(regular);


        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtValue = (EditText) promptView.findViewById(R.id.edt_value);
        edtValue.setHint(getString(R.string.consumer_address)+ "*");
        edtValue.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                meterReading.address_change = "False";
                showMessageDialogForMeterNoChange(mContext);
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setBackground(getResources().getDrawable(R.drawable.positive_button));
//        cancel.setLayoutParams(params);
        no.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                edtValue.setVisibility(View.VISIBLE);
                linearButtonsYesNo.setVisibility(View.GONE);
                linearButtonsSubmit.setVisibility(View.VISIBLE);
            }
        });

        Button update = (Button) promptView.findViewById(R.id.btn_update);
        update.setTypeface(regular);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtValue.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(getString(R.string.please_enter_address));
                } else {
                    meterReading.address_change = "True";
                    meterReading.changed_address = edtValue.getText().toString();
                    showMessageDialogForMeterNoChange(mContext);
                    alert.dismiss();
                }



            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterReading.address_change = "False";
                showMessageDialogForMeterNoChange(mContext);
                alert.dismiss();
            }
        });


        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }


    // Dialog for Change Meter No

    private void showMessageDialogForMeterNoChange(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_change, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final EditText edtValue;
        final TextView txtTitle, txtError, txtSubTitle, txtValue;
        final LinearLayout linearButtonsYesNo, linearButtonsSubmit;

        linearButtonsYesNo = (LinearLayout) promptView.findViewById(R.id.linear_buttons);
        linearButtonsSubmit = (LinearLayout) promptView.findViewById(R.id.linear_buttons_submit);

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(getString(R.string.is_consumer_meter_no_correct));
        txtTitle.setTypeface(regular);

        txtSubTitle = (TextView) promptView.findViewById(R.id.txt_sub_title);
        txtSubTitle.setText(getString(R.string.meter_no));
        txtSubTitle.setTypeface(regular);

        txtValue = (TextView) promptView.findViewById(R.id.txt_value);
        txtValue.setText(userJobCard.meter_no);
        txtValue.setTypeface(regular);


        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtValue = (EditText) promptView.findViewById(R.id.edt_value);
        edtValue.setHint(getString(R.string.meter_no) + "*");
        edtValue.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                meterReading.meter_change = "False";
                showMessageDialogForMobileNoChange(mContext);
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setBackground(getResources().getDrawable(R.drawable.positive_button));
//        cancel.setLayoutParams(params);
        no.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                edtValue.setVisibility(View.VISIBLE);
                linearButtonsYesNo.setVisibility(View.GONE);
                linearButtonsSubmit.setVisibility(View.VISIBLE);
            }
        });

        Button update = (Button) promptView.findViewById(R.id.btn_update);
        update.setTypeface(regular);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtValue.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(getString(R.string.please_enter_meter_no));
                } else {
                    meterReading.meter_change = "True";
                    meterReading.changed_meter = edtValue.getText().toString();
                    showMessageDialogForMobileNoChange(mContext);
                    alert.dismiss();
                }



            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterReading.meter_change = "False";
                showMessageDialogForMobileNoChange(mContext);
                alert.dismiss();
            }
        });


        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

    // Dialog for Change Mobile No

    private void showMessageDialogForMobileNoChange(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_change, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final EditText edtValue;
        final TextView txtTitle, txtError, txtSubTitle, txtValue;
        final LinearLayout linearButtonsYesNo, linearButtonsSubmit;

        linearButtonsYesNo = (LinearLayout) promptView.findViewById(R.id.linear_buttons);
        linearButtonsSubmit = (LinearLayout) promptView.findViewById(R.id.linear_buttons_submit);

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(getString(R.string.is_consumer_mobile_no_correct));
        txtTitle.setTypeface(regular);

        txtSubTitle = (TextView) promptView.findViewById(R.id.txt_sub_title);
        txtSubTitle.setText(getString(R.string.mobile_number));
        txtSubTitle.setTypeface(regular);

        txtValue = (TextView) promptView.findViewById(R.id.txt_value);
        txtValue.setText(userJobCard.phone_no);
        txtValue.setTypeface(regular);


        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtValue = (EditText) promptView.findViewById(R.id.edt_value);
        edtValue.setHint(getString(R.string.mobile_number) + "*");
        edtValue.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                meterReading.mobile_change = "False";
                showMessageDialogForMeterType(mContext);
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setBackground(getResources().getDrawable(R.drawable.positive_button));
//        cancel.setLayoutParams(params);
        no.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                edtValue.setVisibility(View.VISIBLE);
                linearButtonsYesNo.setVisibility(View.GONE);
                linearButtonsSubmit.setVisibility(View.VISIBLE);
            }
        });

        Button update = (Button) promptView.findViewById(R.id.btn_update);
        update.setTypeface(regular);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtValue.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                    txtError.setText(getString(R.string.please_enter_mobile_no));
                } else {
                    if (edtValue.getText().toString().trim().matches("^[0-9][0-9]{9}$")){
                        meterReading.mobile_change = "True";
                        meterReading.changed_mobile = edtValue.getText().toString();
                        showMessageDialogForMeterType(mContext);
                        alert.dismiss();
                    } else {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.enter_valid_mobile_no));
                    }
                }

            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meterReading.mobile_change = "False";
                showMessageDialogForMeterType(mContext);
                alert.dismiss();
            }
        });


        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

    //Dialog for Print Bill Added by Saloni 03/07/2019

    private void showMessageDialogPrintBill(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(R.string.do_you_want_to_print_bill);
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setVisibility(View.GONE);
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);
        edtPassword.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        ok.setTypeface(regular);
        ok.setText("YES");
        ok.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                isForSportBill = true;
                isSpotbill = "True";
                alert.dismiss();
                paymentDataCalculated = calculatePaymentDetails(100, 150, 27, 0, 0, 500, 59);
                submitReading();

            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setBackground(getResources().getDrawable(R.drawable.positive_button));
        cancel.setText("NO");
//        cancel.setLayoutParams(params);
        cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                isSpotbill = "False";
                alert.dismiss();
                submitReading();

            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }


    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
//            CommonUtils.sendSMS(phoneNo, sendMessage);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                Log.i("startTimer", Count + " s");
                Count++;
            }
        }, timerDelay, timerInterval);
    }

    private String stopTimer() {
//        Log.i("stopTimer", "Stopped after " + Count + " s");
        int hours = (int) Count / 3600;
        int remainder = (int) Count - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
//        Log.i("stopTimer", "Stopped time  " + hours + "  hr " + mins + " min " + secs + " sec ");

        Count = 0;
        if (timer != null) {
            timer.cancel();
        }
        if (hours != 0)
            return hours + " hr " + mins + " min " + secs + " sec";
        else
            return mins + " min " + secs + " sec";
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));

        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
            AppPreferences.getInstance(mContext).putString(AppConstants.METER_READER_ID, userProfile.meter_reader_id);
        }
    }

    private void doMeterUpload() {
        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            readingToUpload = DatabaseManager.getMeterReadingsSpotBill(mContext, meter_reader_id, AppConstants.ONE, meterReading.meter_no);
            if (readingToUpload != null && readingToUpload.size() > 0) {
                JSONObject object = getMeterReadingJson(readingToUpload);
                initProgressDialog();
                uploadMeterReading(object);
            } else {
                Toast.makeText(mContext, getString(R.string.no_readings_available_to_be_uploaded), Toast.LENGTH_LONG).show();
            }
        } else {
            DialogCreator.showMessageDialog(mContext, getString(R.string.unable_to_complete_spot_bill_process_please_upload_readings_manually_from_readings_tab), getString(R.string.spot_bill_dialog));
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

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {
            case AppConstants.REQUEST_UPLOAD_METER_READING: {

                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        isUploaded = true;
                        if (jsonResponse.authorization != null) {
//                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        if (jsonResponse.responsedata.new_meter_readings != null && jsonResponse.responsedata.new_meter_readings.size() > 0) {
                            for (int i = 0; i < jsonResponse.responsedata.new_meter_readings.size(); i++) {
                                deleteJobs = DatabaseManager.getMeterReading(mContext, readingToUpload.get(i).meter_reader_id, jsonResponse.responsedata.new_meter_readings.get(i));

                                ArrayList<JobCard> lJobCardArray = DatabaseManager.getJobCard(readingToUpload.get(i).meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, jsonResponse.responsedata.new_meter_readings.get(i));
                                if (lJobCardArray != null) {
                                    if (!lJobCardArray.get(0).phone_no.equalsIgnoreCase("0") && lJobCardArray.get(0).phone_no.length() == 10)
                                        if ((readingToUpload.get(i).reader_status.equalsIgnoreCase(getString(R.string.door_lock)))) {
//                                            sentSMS(lJobCardArray.get(0), readingToUpload.get(i).reading_date);
                                            /*try {
                                                sendSMSForDoorLock();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/

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


                                if (isSpotbill.equals("True")) {
                                    try {
                                        initProgressDialog();
                                        getSpotBilliDetails();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                        //changes for Delete Job cards Ends Avinesh
                        else {
                            DialogCreator.showMessageDialog(mContext, getString(R.string.unable_to_complete_spot_bill_process_please_upload_readings_manually_from_readings_tab), getString(R.string.spot_bill_dialog));
                        }
                    } else {
                        DialogCreator.showMessageDialog(mContext, getString(R.string.unable_to_complete_spot_bill_process_please_upload_readings_manually_from_readings_tab), getString(R.string.spot_bill_dialog));
                    }
                } else {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.unable_to_complete_spot_bill_process_please_upload_readings_manually_from_readings_tab), getString(R.string.spot_bill_dialog));

                }
            }
            break;
            case AppConstants.REQUEST_SPOT_BILL_DETAILS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.spotbilldata != null) {
                            dismissDialog();
                            Intent intent = new Intent(mContext, SpotBillActivity.class);
                            intent.putExtra("previous_sequence", prv_sequence);
                            intent.putExtra("revisit_flag", revisitFlag);
                            intent.putExtra(AppConstants.CURRENT_JOB_CARD, userJobCard);
                            intent.putExtra(AppConstants.SPOT_BILL_CONSUMER_DATA, (Serializable) jsonResponse.spotbilldata);
                            startActivity(intent);
                        }
                    } else {
                        dismissDialog();
                        DialogCreator.showMessageDialog(mContext, getString(R.string.your_spot_bill_data_is_currently_unavailable_please_contact_server_admin), getString(R.string.spot_bill_dialog));

                    }

                }
            }
            break;
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_SPOT_BILL_DETAILS: {
                dismissDialog();
                DialogCreator.showMessageDialog(mContext, getString(R.string.your_spot_bill_data_is_currently_unavailable_please_contact_server_admin), getString(R.string.spot_bill_dialog));
            }
            break;
            case AppConstants.REQUEST_UPLOAD_METER_READING: {
                dismissDialog();
                DialogCreator.showMessageDialog(mContext, getString(R.string.unable_to_complete_spot_bill_process_please_upload_readings_manually_from_readings_tab), getString(R.string.spot_bill_dialog));
            }
        }
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


    private void getSpotBilliDetails() throws JSONException {

        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            JSONObject object = getMeterReadingJsonSpotBill();
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

            JsonObjectRequest request = WebRequests.callPostMethod(object, Request.Method.POST, AppConstants.URL_SPOT_BILL_DETAILS, AppConstants.REQUEST_SPOT_BILL_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_SPOT_BILL_DETAILS);

        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.spot_bill_dialog));
        }
    }

    public JSONObject getMeterReadingJsonSpotBill() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(getString(R.string.consumer_no_api), consumerNumber);
            jsonObject.put(getString(R.string.reading_month), userJobCard.schedule_month);
            jsonObject.put(getString(R.string.is_spot_billing), meterReading.spot_billing);
            jsonObject.put(getString(R.string.job_card_id), userJobCard.job_card_id);
            jsonObject.put(getString(R.string.city_api), userProfile.city);
//            jsonObject.put(getString(R.string.mobile_no), userJobCard.phone_no);
            jsonObject.put(getString(R.string.mobile_no), "8766491254");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
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

    private void sendSMSForDoorLock() throws JSONException {

        if (CommonUtils.isNetworkAvailable(mContext) == true) {
            JSONObject object = getMeterReadingJsonSendSMS();
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

    public JSONObject getMeterReadingJsonSendSMS() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put(getString(R.string.meter_reader_name), userProfile.meter_reader_name);
            jsonObject.put(getString(R.string.reading_date_api), meterReading.reading_date);
            jsonObject.put(getString(R.string.reader_status_api), readerStatusCode);
            jsonObject.put(getString(R.string.meter_status_api), meterStatusCode);
            jsonObject.put(getString(R.string.meter_no_api), meterReading.meter_no);
            jsonObject.put(getString(R.string.consumer_no_api), userJobCard.consumer_no);
            jsonObject.put(getString(R.string.mobile_no), userJobCard.phone_no);
            jsonObject.put(getString(R.string.meter_reading_api), meterReading.current_meter_reading);
//            jsonObject.put(getString(R.string.mobile_no), "8766491254");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    // Function to calculate payment details locally by Saloni starts here
    private HashMap<String, String> calculatePaymentDetails(int prv, int cur, double price, double prvDue,
                                                            double pay, double deposit, double penalty) {
        int prvReading, curReading, consumption;
        double totalPrice, consumptionCharges, current_charges, previousDue, payments, emiDeposit,
                amountBefore, amountAfter, penaltyCharges;

        //(Current Reading) - (Previous Reading) = (Consumption)   /*To Calculate Consumption*/
        //(Consumption) * (Price) = (Consumption Charges / Current Charges)   /*To Calculate Charges*/
        //(Previous Due) - (Payments) + (Current Charges) + (EMI Security Deposit) = (Amount Before Due) /*To calculate amount*/

        prvReading = prv;
        curReading = cur;
        consumption = curReading - prvReading;
        totalPrice = price;
        consumptionCharges = consumption * totalPrice;
        current_charges = Integer.parseInt(String.valueOf(Math.round(consumptionCharges)));
        previousDue = prvDue;
        payments = pay;
        emiDeposit = deposit;
        amountBefore = Integer.parseInt(String.valueOf(Math.round(previousDue - payments + current_charges + emiDeposit)));
        penaltyCharges = penalty;
        amountAfter = Integer.parseInt(String.valueOf(Math.round(amountBefore + penaltyCharges)));


        Log.d("11111111111",""+consumptionCharges);
        Log.d("2222222222",""+current_charges);
        Log.d("33333333333",""+amountBefore);
        Log.d("44444444444",""+amountAfter);

        paymentData = new HashMap<>();
        paymentData.put(getString(R.string.consumption_charges), String.valueOf(consumptionCharges));
        paymentData.put(getString(R.string.current_charges), String.valueOf(current_charges));
        paymentData.put(getString(R.string.amount_before_due), String.valueOf(amountBefore));
        paymentData.put(getString(R.string.amount_after_due), String.valueOf(amountAfter));
        paymentData.put(getString(R.string.bill_type), "Normal");

        return paymentData;
    }

    // Function to calculate average bill locally by Saloni starts here
    private HashMap<String, String> calculateAverage(int prv, int cur, int avgAmount,
                                                     double price, double prvDue, double pay, double deposit,
                                                     double penalty) {
        Integer prvReading, curReading, consumption;
        double totalPrice, consumptionCharges, current_charges, previousDue, payments, emiDeposit,
                amountBefore, penaltyCharges, amountAfter;

        //(Current Reading) - (Previous Reading) = (Consumption)   /*To Calculate Consumption*/
        //(Consumption) * (Price) = (Consumption Charges / Current Charges)   /*To Calculate Charges*/
        //(Previous Due) - (Payments) + (Current Charges) + (EMI Security Deposit) = (Amount Before Due) /*To calculate amount*/



        prvReading = prv;
        curReading = prv + avgAmount;
        consumption = curReading - prvReading;
        totalPrice = price;
        consumptionCharges = consumption * totalPrice;
        current_charges = Integer.parseInt(String.valueOf(Math.round(consumptionCharges)));
        previousDue = prvDue;
        payments = pay;
        emiDeposit = deposit;
        amountBefore = Integer.parseInt(String.valueOf(Math.round(previousDue - payments + current_charges + emiDeposit)));

        penaltyCharges = penalty;
        amountAfter = Integer.parseInt(String.valueOf(Math.round(amountBefore + penaltyCharges)));


        Log.d("11111111111",""+consumptionCharges);
        Log.d("2222222222",""+current_charges);
        Log.d("33333333333",""+amountBefore);
        Log.d("44444444444",""+amountAfter);

/*
        int conCharge, curCharge, amtBef, amtAft;

        conCharge = (int) consumptionCharges;
        curCharge = (int) current_charges;
        amtBef = (int) amountBefore;
        amtAft = (int) amountAfter;


        Log.d("1111111111111",""+conCharge);
        Log.d("222222222",""+curCharge);
        Log.d("333333333",""+amtBef);
        Log.d("33344444444444",""+amtAft);*/
        paymentData = new HashMap<>();
        paymentData.put(getString(R.string.consumption_charges), String.valueOf(consumptionCharges));
        paymentData.put(getString(R.string.current_charges), String.valueOf(current_charges));
        paymentData.put(getString(R.string.amount_before_due), String.valueOf(amountBefore));
        paymentData.put(getString(R.string.amount_after_due), String.valueOf(amountAfter));
        paymentData.put(getString(R.string.bill_type), "Average");

        return paymentData;
    }

    private void launchSpotBillScreen() {
        Intent intent = new Intent(mContext, SpotBillActivity.class);
        intent.putExtra("previous_sequence", prv_sequence);
        intent.putExtra("revisit_flag", revisitFlag);
        intent.putExtra(AppConstants.CURRENT_JOB_CARD, userJobCard);
        intent.putExtra(AppConstants.CURRENT_METER_READING, meterReading);
//        intent.putExtra(AppConstants.SPOT_BILL_CONSUMER_DATA, (Serializable) jsonResponse.spotbilldata);
        intent.putExtra(AppConstants.SPOT_BILL_CONSUMER_DATA, "");
        startActivity(intent);
    }

    private void calculatePaymentData() {

        Integer previous, currentRead, avgAmount;;

        Double  price, prvDue, payments, deposit, penalty;

        previous = Integer.parseInt(userJobCard.prv_meter_reading);
        currentRead = Integer.parseInt(meterReading.current_meter_reading);
        price = Double.parseDouble(userJobCard.total_price);
        prvDue = Double.parseDouble(userJobCard.pre_due);
        payments = Double.parseDouble(userJobCard.payment);
        deposit = Double.parseDouble(userJobCard.emi_security_dep);
        penalty = Double.parseDouble(userJobCard.due_amount);
        avgAmount = Integer.parseInt(userJobCard.avg_unit);
        if (meterStatusCode.equals(getString(R.string.normal)) &&
                readerStatusCode.equals(getString(R.string.actual_reading))) {
            if (currentRead > previous) {
                paymentDataCalculated = calculatePaymentDetails(previous, currentRead, price, prvDue, payments,
                        deposit, penalty);
            } else {
                paymentDataCalculated = calculateAverage(previous, currentRead, avgAmount, price, prvDue, payments,
                        deposit, penalty);
            }
        } else {
            paymentDataCalculated = calculateAverage(previous, currentRead, avgAmount, price, prvDue, payments,
                    deposit, penalty);
        }

        meterReading.consumptionCharges = paymentDataCalculated.get(getString(R.string.consumption_charges));
        meterReading.current_charges = paymentDataCalculated.get(getString(R.string.current_charges));
        meterReading.amt_before_due = paymentDataCalculated.get(getString(R.string.amount_before_due));
        meterReading.amt_after_due = paymentDataCalculated.get(getString(R.string.amount_after_due));
        meterReading.bill_type = paymentDataCalculated.get(getString(R.string.bill_type));

        launchSpotBillScreen();

    }


    private String calculateDueDate(String date){
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");


        Calendar c = Calendar.getInstance();
        Date startDate = null;
        Date resultdate = null;
        try {
            startDate = originalFormat.parse(date);
            c.setTime(originalFormat.parse(date));
            c.add(Calendar.DATE, 21);
            resultdate = new Date(c.getTimeInMillis());

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        String beginDate = targetFormat.format(startDate);
        String dueDate = originalFormat.format(resultdate);
        return dueDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String calculateConsumptionDays(String curDate, String prvDate){

        SimpleDateFormat baseFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat baseFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");

        Date date = null;
        try {
            date = baseFormat1.parse(curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = "";
        if (date != null) {
            formattedDate = targetFormat.format(date);
        }


        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString1 = prvDate;
        String inputString2 = formattedDate;
        String consumptionDays = "";
        try {
            Date date1 = formatter.parse(inputString1);
            Date date2 = formatter.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
//            System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            consumptionDays = String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)); // 28
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return consumptionDays;
    }

    public void submit(View view){

    }

}