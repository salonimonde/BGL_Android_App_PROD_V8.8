package com.sgl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.Consumer;
import com.sgl.models.MeterImage;
import com.sgl.models.Sequence;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.utils.LocationManagerReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class UnBillMeterReadingActivity  extends ParentActivity implements View.OnClickListener, LocationListener, RadioGroup.OnCheckedChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private Context mContext;
    private LinearLayout linearSuspicious, linearMobileNumber;
    private EditText edtObservation, edtComments, edtAboutMeter, edtKWHReading, edtMobileNo, edtMeterNo;
    private ImageView cameraSuspicious, imgSuspicious, imgMeter, cameraMeter;
    private TextView mConsumerName, txtSuspiciousActivity, lblSusImage, txtConsumerMeterNumber, txtMobileNumber;
    private Spinner mMeterStatus, mReaderStatus, mMeterType;
    public final String METER_IMAGE_DIRECTORY_NAME = "meter";
    public final String SUSPICIOUS_IMAGE_DIRECTORY_NAME = "suspicious";
    private Button btnSubmit;
    private ImageView img_back, btnViewMore;
    private Consumer consumer;
    private RadioGroup radioGroup;
    private Typeface regular, bold;
    private LocationManagerReceiver mLocationManagerReceiver;
    private Bitmap mBitmapMeterSuspicious, mBitmapMeterReading;
    private String consumerName = "", consumerAddress = "", consumerNumber = "", consumerMeterNo = "", consumerMobileNo = "",
            mMeterReadingImageName = "", mMeterReadingSPImageName = "", meterStatusCode = "", meterType = "", consumerZoneCode = "";
    private RadioButton radioYes, radioNo;
    private int arrayForReasonCode = R.array.reader_status;
    private RelativeLayout updateMobileNo;
    private ImageView imgUpdateMobileNo;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 10 sec
    private static int FASTEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters

    private ArrayList<Sequence> localSequence;
    private int assignedSequence = 0;
    private  File mediaStorageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_bill_meter_reading_activity);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        mContext = this;
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        if (checkPlayServices())
        {
            // Building the GoogleApi client
            buildGoogleApiClient();
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
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
        initUI();

    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), getString(R.string.this_device_is_not_supported), Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void initUI()
    {
        txtSuspiciousActivity = findViewById(R.id.Suspicious_Activity);
        txtSuspiciousActivity.setTypeface(bold);
        lblSusImage = findViewById(R.id.lbl_Sus_img);
        lblSusImage.setTypeface(bold);
        edtObservation = findViewById(R.id.edt_observation);
        edtObservation.setTypeface(bold);
        edtComments = findViewById(R.id.comments);
        edtComments.setTypeface(bold);
        imgMeter = findViewById(R.id.img_meter);
        cameraMeter = findViewById(R.id.camera_meter);
        btnSubmit = findViewById(R.id.submit);
        btnSubmit.setTypeface(bold);
        imgSuspicious = findViewById(R.id.img_suspicious);
        cameraSuspicious = findViewById(R.id.camera_suspicious);
        img_back = findViewById(R.id.img_back);
        TextView lTilObservations = findViewById(R.id.txt_observation);
        lTilObservations.setTypeface(regular);
        updateMobileNo = findViewById(R.id.updatemetermobile);
        updateMobileNo.setOnClickListener(this);
        radioGroup = findViewById(R.id.rg_yesno);
        radioGroup.setOnCheckedChangeListener(this);

        cameraMeter.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        cameraSuspicious.setOnClickListener(this);

        Intent i = getIntent();
        if (i != null) {
            if (consumer == null) {
                consumer = (Consumer) i.getSerializableExtra(AppConstants.CURRENT_CONSUMER_OBJ);
            }
        }

        Sequence getSeq = new Sequence();
        getSeq.meter_reader_id = consumer.meter_reader_id;
        getSeq.zone_code = consumer.zone_code;
        getSeq.route_code = consumer.route_code;
        getSeq.cycle_code = consumer.bill_cycle_code;

        localSequence = DatabaseManager.getSequence(mContext, getSeq);
        if (localSequence != null) {
            assignedSequence = Integer.parseInt(localSequence.get(0).sequence);
        }

        mConsumerName = findViewById(R.id.consumerName);
        mConsumerName.setTypeface(regular);
        txtConsumerMeterNumber = findViewById(R.id.txt_consumer_meter_number);
        txtConsumerMeterNumber.setTypeface(regular);
        radioYes =  findViewById(R.id.rb_yes);
        radioYes.setOnClickListener(this);
        radioNo =  findViewById(R.id.rb_no);
        radioNo.setOnClickListener(this);
        linearSuspicious = findViewById(R.id.linear_suspicious);
        linearSuspicious.setVisibility(View.GONE);
        btnViewMore = findViewById(R.id.btn_view_more);
        btnViewMore.setOnClickListener(this);
        edtAboutMeter = findViewById(R.id.edt_about_meter);
        edtAboutMeter.setTypeface(bold);
        edtKWHReading = findViewById(R.id.edt_kwh_reading);
        edtKWHReading.setTypeface(bold);
        edtMeterNo = findViewById(R.id.edt_panel_no);
        edtMeterNo.setTypeface(bold);
        edtMobileNo = findViewById(R.id.edt_mobile_no);
        edtMobileNo.setTypeface(bold);

        txtMobileNumber = findViewById(R.id.txt_mobile_number);
        txtMobileNumber.setTypeface(bold);
        linearMobileNumber = findViewById(R.id.linear_mobile_number);

        mMeterStatus = findViewById(R.id.meter_status_spinner);
        mReaderStatus = findViewById(R.id.reader_status_spinner);
        setReasonCodeSpinner(arrayForReasonCode);
        imgUpdateMobileNo = findViewById(R.id.updatemobile);

        //New fields for CESC ends, Piyush : 22/03/2017

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.update_meter_status_all))
        {
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mMeterStatus.setAdapter(adapter);
        mMeterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                meterStatusCode = mMeterStatus.getSelectedItem().toString().trim();
                if (meterStatusCode.equals(getString(R.string.normal)))
                {
                    arrayForReasonCode = R.array.reader_status_normal;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                }
                else if (meterStatusCode.equals(getString(R.string.meter_status_faulty)))
                {
                    arrayForReasonCode = R.array.reader_status_faulty;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                }
                else if (meterStatusCode.equals(getString(R.string.rcnt)))
                {
                    arrayForReasonCode = R.array.reader_status_rcnt;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(false);
                }
                else
                {
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
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        mMeterType = findViewById(R.id.meter_type_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.meter_type))
        {
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mMeterType.setAdapter(adapter2);
        mMeterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if (!mMeterType.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.update_meter_type)))
                    meterType = mMeterType.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        edtMobileNo.addTextChangedListener(new TextWatcher()
        {
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

        mMeterReadingImageName = "ME_" + consumer.consumer_no + "_" + consumer.meter_reader_id;
        mMeterReadingSPImageName = "SP_" + consumer.consumer_no + "_" + consumer.meter_reader_id;

        edtKWHReading.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (count == 0)
                {
                    setDynamicValues(R.array.reader_status);
                }
                else
                {
                    setDynamicValues(R.array.update_meter_status_without_rcnt);
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {}
        });

        initAllUIComponents();
    }

    private void setDynamicValues(int s)
    {
        setMeterStatusSpinner(s);
    }

    private void setMeterStatusSpinner(int meterStatus)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(meterStatus))
        {
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mMeterStatus.setAdapter(adapter);
    }

    private void initAllUIComponents()
    {
        consumerName = (consumer.consumer_name == null) ? "" : consumer.consumer_name;
        consumerAddress = (consumer.address == null) ? "" : consumer.address;
        consumerNumber = (consumer.consumer_no == null) ? "" : consumer.consumer_no;
        consumerMeterNo = (consumer.meter_no == null) ? "" : consumer.meter_no;
        consumerMobileNo = (consumer.mobile_no == null) ? "" : consumer.mobile_no;
        consumerZoneCode = (consumer.zone_code == null) ? "" : consumer.zone_code;
        txtConsumerMeterNumber.setText(consumerNumber + " | " + consumerMeterNo);
        txtConsumerMeterNumber.setSelected(true);
        edtMobileNo.setText(consumerMobileNo);
        edtMeterNo.setText(consumerMeterNo);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case AppConstants.CAMERA_RESULT_CODE:
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

                if (mLastLocation != null) {
                    consumer.cur_lat = String.valueOf(mLastLocation.getLatitude());
                    consumer.cur_lng = String.valueOf(mLastLocation.getLongitude());

                } else {
                    consumer.cur_lat = "0";
                    consumer.cur_lng = "0";
                }

                mBitmapMeterReading = getBitmapScaled(METER_IMAGE_DIRECTORY_NAME, "ME_" + consumer.consumer_no + "_" + consumer.meter_reader_id);
                if (mBitmapMeterReading != null) {
                    imgMeter.setImageBitmap(mBitmapMeterReading);
                }
                mediaStorageDir.deleteOnExit();
                break;
            case AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE:

                mBitmapMeterSuspicious = getBitmapScaled(SUSPICIOUS_IMAGE_DIRECTORY_NAME, "SP_" + consumer.consumer_no + "_" + consumer.meter_reader_id);
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
                compressedImage = CommonUtils.addWaterMarkDate(compressedImage, CommonUtils.getCurrentDateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compressedImage;
    }

    public Uri getOutputMediaFileUri(String dirname, String filename) throws IOException {
        File file = getMeterFilePath(dirname, filename);
        return FileProvider.getUriForFile(mContext, this.getPackageName() + ".provider", file);
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
        return new File(mediaStorageDir.getPath() + File.separator + filename + ".jpg");
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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
    public void onClick(View v)
    {

        //To show the details of consumer starts, Piyush : 02-03-17
        if(v == btnViewMore)
        {
            DialogCreator.showConsumerDetailsDialog(mContext, consumerName, consumerAddress, consumerNumber, consumerMeterNo, consumer.bill_cycle_code, consumer.zone_code,consumer.mobile_no,consumer.route_code, null, null, null);
        }
        //To show the details of consumer ends, Piyush : 02-03-17

        //To show/hide suspicious view starts, Piyush : 02-03-17
        if(v == radioYes)
        {
            linearSuspicious.setVisibility(View.VISIBLE);
        }

        if(v == radioNo)
        {
            edtObservation.setText("");
            mBitmapMeterSuspicious = null;
            imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
            linearSuspicious.setVisibility(View.GONE);
        }
        //To show/hide suspicious view ends, Piyush : 02-03-17

        if(v == cameraMeter)
        {
            mLocationManagerReceiver = new LocationManagerReceiver(mContext);
            createLocationRequest();
            startLocationUpdates();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            try {
                fileUri = getOutputMediaFileUri(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
                List<ResolveInfo> resolvedIntentActivities = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_RESULT_CODE);
        }
        if(v == cameraSuspicious && (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes))
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            try
            {
                fileUri = getOutputMediaFileUri(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
                List<ResolveInfo> resolvedIntentActivities = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo resolvedIntentInfo : resolvedIntentActivities)
                {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE);
        }
        if(v == btnSubmit)
        {
            doSubmitOps(0);
        }
        if(v == img_back)
        {
            finish();
        }

        if(v == updateMobileNo)
        {
            if(linearMobileNumber.getVisibility()==View.GONE)
            {
                imgUpdateMobileNo.setRotation(180);
                linearMobileNumber.setVisibility(View.VISIBLE);
            }
            else
            {
                CommonUtils.hideKeyBoard(mContext);
                imgUpdateMobileNo.setRotation(0);
                linearMobileNumber.setVisibility(View.GONE);
                edtMobileNo.setText(consumer.mobile_no == null || consumer.mobile_no.equals("None") ? "" : consumer.mobile_no);
            }
        }
    }

    private void doSubmitOps(int whereToGo)
    {
        String current_kwh = edtKWHReading.getText().toString().trim();
        String meterStatus = mMeterStatus.getSelectedItem().toString().trim();
        String readerStatus = mReaderStatus.getSelectedItem().toString().trim();

        String meter_image = null;
        if(((BitmapDrawable) imgMeter.getDrawable()) != null) {
            meter_image = CommonUtils.getBitmapEncodedString(mBitmapMeterReading);
            MeterImage meterImage = new MeterImage();
            meterImage.image = meter_image;
            consumer.meter_image = meterImage;
            consumer.isUploaded = "False";
            if(current_kwh.isEmpty())
            {
                if(meterStatus.equals(getString(R.string.rcnt)))
                {
                    if (readerStatus.equals(getString(R.string.reader_status_mandatory)))
                    {
                        DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), getString(R.string.error));
                    }
                    else
                    {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                            consumer.suspicious_activity = "True";
                            String encodedImage = "";
                            if (mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                MeterImage suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                consumer.suspicious_activity_image = suspicious_activity_image;
                                if (!edtObservation.getText().toString().isEmpty()) {
                                    String obs = edtObservation.getText().toString().trim();
                                    consumer.suspicious_remark = obs;
                                }
                                if (validateNumber()) {
                                    submitReading(whereToGo);
                                }
                            } else {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), getString(R.string.error));
                            }
                        } else {
                            consumer.suspicious_activity = "False";
                            if (validateNumber()) {
                                submitReading(whereToGo);
                            }
                        }
                    }
                }
                else
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_reading), getString(R.string.error));
            }
            else {
                if (meterStatus.equals(getString(R.string.meter_status_mandatory))) {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), getString(R.string.error));
                } else {
                    if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                        DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), getString(R.string.error));
                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                            consumer.suspicious_activity = "True";
                            String encodedImage = "";
                            if (mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                MeterImage suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                consumer.suspicious_activity_image = suspicious_activity_image;
                                if (!edtObservation.getText().toString().isEmpty()) {
                                    String obs = edtObservation.getText().toString().trim();
                                    consumer.suspicious_remark = obs;
                                }
                                if (validateNumber()) {
                                    submitReading(whereToGo);
                                }
                            } else {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), getString(R.string.error));
                            }
                        } else {
                            consumer.suspicious_activity = "False";
                            if (validateNumber()) {
                                submitReading(whereToGo);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            DialogCreator.showMessageDialog(mContext, getString(R.string.blank_meter_reading_image), getString(R.string.error));
        }
    }

    private void submitReading(int whereToGo)
    {

        // Sequence Logic starts
        consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"
                +consumer.route_code+"|"+String.valueOf(String.format("%04d",assignedSequence));

        consumer.meter_status = meterStatusCode;
        consumer.reader_status = mReaderStatus.getSelectedItem().toString().trim();
        consumer.location_guidance = edtAboutMeter.getText().toString().trim();
        consumer.current_meter_reading = edtKWHReading.getText().toString().trim();
        consumer.zone_code = consumerZoneCode;

        if(validateNumber())
        {
            consumer.mobile_no = edtMobileNo.getText().toString().trim();
        }

        consumer.meter_type = meterType;
        if (meterStatusCode.equalsIgnoreCase(getString(R.string.rcnt))) {
            consumer.current_meter_reading = "0";
        }
        consumer.reader_remark_comment = edtComments.getText().toString();
        consumer.isUploaded = "False";

        Sequence updateSequence = new Sequence();
        updateSequence.meter_reader_id = consumer.meter_reader_id;
        updateSequence.cycle_code = consumer.bill_cycle_code;
        updateSequence.route_code = consumer.route_code;
        updateSequence.zone_code = consumer.zone_code;
        updateSequence.sequence = String.valueOf(++assignedSequence);
        DatabaseManager.UpdateSequence(mContext, updateSequence);


        //delete saved images folder
        File meterFile = new File(Environment.getExternalStorageDirectory(), METER_IMAGE_DIRECTORY_NAME);
        if(meterFile.isDirectory())
        {
            String[] children = meterFile.list();
            for(int i = 0; i < children.length; i++)
            {
                new File(meterFile, children[i]).delete();
            }
            meterFile.delete();
        }
        File spFile = new File(Environment.getExternalStorageDirectory(), SUSPICIOUS_IMAGE_DIRECTORY_NAME);
        if(spFile.isDirectory())
        {
            String[] children = spFile.list();
            for(int i = 0; i < children.length; i++)
            {
                new File(spFile, children[i]).delete();
            }
            spFile.delete();
        }

        if(whereToGo == 0)
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            PackageManager pm = this.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            if(isGPSEnabled && hasGps)
            {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;

                consumer.reading_taken_by = App.ConsumerAddedBy;
                consumer.reading_date = CommonUtils.getCurrentDateTime();
                DatabaseManager.saveUnBillConsumer(mContext, consumer);
                //Changes For Online Reading Ends Avinesh 6-04-17

                Toast.makeText(mContext, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                finish();
                if(AddNewConsumerActivity.addNewConsumerActivity != null)
                {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
            }
            else if(isNetworkEnabled && !hasGps)
            {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                //Save it into the DB

                mLocationManagerReceiver.saveConsumerWithLatLng(mContext, consumer, true);
                Toast.makeText(mContext, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                finish();
                if(AddNewConsumerActivity.addNewConsumerActivity != null)
                {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
            }
            else
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        }
        else if(whereToGo == 1)
        {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            PackageManager pm = this.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            if(isGPSEnabled && hasGps)
            {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                mLocationManagerReceiver.saveConsumerWithLatLng(mContext, consumer, false);
                Toast.makeText(mContext, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                Intent qrCodeIntent = new Intent(mContext, SearchStreetWiseActivity.class);
                qrCodeIntent.putExtra(AppConstants.CURRENT_METER_READER_ID, LandingActivity.meter_reader_id);
                startActivity(qrCodeIntent);
                if(AddNewConsumerActivity.addNewConsumerActivity != null)
                {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
                this.finish();

            }
            else if(isNetworkEnabled && !hasGps)
            {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                mLocationManagerReceiver.saveConsumerWithLatLng(mContext, consumer, false);
                Toast.makeText(mContext, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                if(App.ConsumerAddedBy == getString(R.string.meter_reading_qr_code))
                {
                    Intent qrCodeIntent = new Intent(mContext, SearchStreetWiseActivity.class);
                    qrCodeIntent.putExtra(AppConstants.CURRENT_METER_READER_ID, LandingActivity.meter_reader_id);
                    startActivity(qrCodeIntent);
                }
                if(AddNewConsumerActivity.addNewConsumerActivity != null)
                {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
                this.finish();
            }
            else
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i)
    {
        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)
            edtObservation.setEnabled(true);
        else
            edtObservation.setEnabled(false);
    }

    private boolean validateNumber()
    {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String PHONE_PATTERN1 = "^[7-9][0-9]{9}$";
        final String PHONE_PATTERN2 = "";
        pattern1 = Pattern.compile(PHONE_PATTERN1);
        pattern2 = Pattern.compile(PHONE_PATTERN2);
        String phone = edtMobileNo.getText().toString().trim();
        matcher1 = pattern1.matcher(phone);
        matcher2 = pattern2.matcher(phone);

        if(matcher1.matches() || matcher2.matches())
        {
            edtMobileNo.setError(null);
            return true;
        }
        else
        {
            edtMobileNo.setError(getString(R.string.enter_valid_mobile_no));
            return false;
        }
    }

    private void setReasonCodeSpinner(int reasonCode)
    {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(reasonCode))
        {
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mReaderStatus.setAdapter(adapter1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null)
        {        mGoogleApiClient.connect();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        mLocationManagerReceiver = new LocationManagerReceiver(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}