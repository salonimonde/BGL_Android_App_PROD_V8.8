package com.sgl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.models.JobCard;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import java.io.File;
import java.util.List;

import id.zelory.compressor.Compressor;

public class ChequeCollectionActivity extends ParentActivity implements View.OnClickListener
{

    private Context mContext;
    private final String CHEQUE_IMAGE_DIRECTORY_NAME = "cheque";
    private String mChequeImageName = "", chequeImage = "";
    private JobCard userJobCard;
    private Bitmap mBitmapChequeImage = null;

    private TextView lblSdAmount, lblExSdAmount, lblTotalAmount;
    private TextView txtConsumerMeterNumber, txtSdAmount, txtExSdAmount, txtTotalAmount;
    private ImageView imgBack, imgCheque, captureImage;
    private EditText edtChequeNo, edtChequeAmount;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheque_collection);
        mContext = this;

        Typeface regular = App.getSansationRegularFont(), bold = App.getSansationBoldFont();

        lblSdAmount = findViewById(R.id.lbl_sd_amount);
        lblSdAmount.setTypeface(bold);
        lblExSdAmount = findViewById(R.id.lbl_ex_sd_amount);
        lblExSdAmount.setTypeface(bold);
        lblTotalAmount = findViewById(R.id.lbl_total_amount);
        lblTotalAmount.setTypeface(bold);

        txtSdAmount = findViewById(R.id.txt_sd_amount);
        txtSdAmount.setTypeface(regular);
        txtExSdAmount = findViewById(R.id.txt_ex_sd_amount);
        txtExSdAmount.setTypeface(regular);
        txtTotalAmount = findViewById(R.id.txt_total_amount);
        txtTotalAmount.setTypeface(regular);
        txtConsumerMeterNumber = findViewById(R.id.txt_consumer_meter_number);
        txtConsumerMeterNumber.setTypeface(bold);

        imgCheque = findViewById(R.id.img_cheque);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        captureImage = findViewById(R.id.capture_image);
        captureImage.setOnClickListener(this);

        edtChequeNo = findViewById(R.id.edt_cheque_no);
        edtChequeAmount = findViewById(R.id.edt_cheque_amount);

        btnSubmit = findViewById(R.id.submit_and_next);
        btnSubmit.setOnClickListener(this);

        edtChequeNo.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                validateNumber();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                validateNumber();
            }
        });

        getUserData();
    }

    private void getUserData()
    {
        Intent i = getIntent();
        if (i != null) {
            if (userJobCard == null) {
                userJobCard = (JobCard) i.getSerializableExtra(AppConstants.CURRENT_JOB_CARD);
            }
        }

        txtConsumerMeterNumber.setText(userJobCard.account_no + " | " + userJobCard.meter_no);
        txtSdAmount.setText(userJobCard.sd_amount);
        txtExSdAmount.setText(userJobCard.excluding_sd_amount);
        txtTotalAmount.setText(userJobCard.total_amount);

        mChequeImageName = "ME_" + userJobCard.job_card_id + "_" + userJobCard.meter_reader_id;
    }

    @Override
    public void onClick(View v)
    {
        if(v == imgBack)
        {
            finish();
        }

        if(v == captureImage)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            fileUri = getOutputMediaFileUri(CHEQUE_IMAGE_DIRECTORY_NAME, mChequeImageName);
            List<ResolveInfo> resolvedIntentActivities = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities)
            {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                mContext.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_RESULT_CODE);
        }

        if(v == btnSubmit)
        {
            String chequeAmount = edtChequeAmount.getText().toString().trim();

            if(mBitmapChequeImage != null)
            {
                if(validateNumber())
                {
                    if(chequeAmount.length() > 0)
                    {
                        sendDataToBackScreen();
                    }
                    else
                    {
                        Toast.makeText(mContext, getString(R.string.enter_cheque_amount), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(mContext, getString(R.string.enter_cheque_number), Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(mContext, getString(R.string.take_cheque_image), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendDataToBackScreen()
    {
        String chequeNo = edtChequeNo.getText().toString().trim();
        String chequeAmount = edtChequeAmount.getText().toString().trim();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(AppConstants.CHEQUE_COLLECTION_IMAGE, chequeImage);
        resultIntent.putExtra(AppConstants.CHEQUE_COLLECTION_NUMBER, chequeNo);
        resultIntent.putExtra(AppConstants.CHEQUE_COLLECTION_AMOUNT, chequeAmount);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private boolean validateNumber()
    {
        String chequeNo = edtChequeNo.getText().toString().trim();

        if (chequeNo.length() > 0 && chequeNo.length() == 6)
        {
            edtChequeNo.setError(null);
            return true;
        }
        else
        {
            edtChequeNo.setError("Enter valid cheque no");
            edtChequeNo.requestFocus();
            return false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case AppConstants.CAMERA_RESULT_CODE:
                mBitmapChequeImage = getBitmapScaled(CHEQUE_IMAGE_DIRECTORY_NAME, mChequeImageName);
                if (mBitmapChequeImage != null) {
                    imgCheque.setImageBitmap(mBitmapChequeImage);
                    chequeImage = CommonUtils.getBitmapEncodedString(mBitmapChequeImage);
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
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), dirname);

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
}