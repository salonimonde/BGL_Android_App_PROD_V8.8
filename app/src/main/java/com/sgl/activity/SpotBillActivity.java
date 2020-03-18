package com.sgl.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.utils.Utils;
import com.prowesspride.api.Printer_GEN;
import com.prowesspride.api.Setup;
import com.sgl.R;
import com.sgl.bluetooth.EvoluteBTConnection;
import com.sgl.bluetooth.EvoluteBTPair;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.JobCard;
import com.sgl.models.MeterImage;
import com.sgl.models.MeterReading;
import com.sgl.models.Response;
import com.sgl.models.Sequence;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;

import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class SpotBillActivity extends ParentActivity implements View.OnClickListener {

    private Context mContext;
    private Button btnPrint;
    private ImageView imgBack;

    InputStream input;// = EvoluteBTConnection.misIn;
    OutputStream outStream;
    public static Printer_GEN prnGen;
    public Setup setup = null;
    int iRetVal;
//    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
//    public static BluetoothDevice mBDevice = null;
    private boolean mblBonded = false;
    private String sDeviceType;

    public static final int DEVICE_NOT_CONNECTED = -100;
    public static final byte REQUEST_DISCOVERY = 0x01;
    public static final int EXIT_ON_RETURN = 21;
    private App mGP = null;

    private UserProfile userProfile;
    private JobCard userJobCard;
    private MeterReading meterReading;
    private String mMeterReaderId = "", prv_sequence = "", revisitFlag = "";

    private Response spotBillResponse;

    private TextView txtConsumerNo, txtInvoiceNo, txtMeterNo, txtConsumerName, txtAddress, txtPrice,
            txtCurrentReadingDate, txtPreviousReadingDate, txtCurrentReading, txtPreviousReading,
            txtConsumption, txtConsumptionCharges, txtEmi, txtArrears, txtcurrent_charges, txtDueDate,
            txtPenalty, txtTotalChargesAfterDue, txtMobileNo, txtMeterStatus, txtAmountBefore, txtAmountAfter,
            txtBillType, txtConsumpDays, lblBeforeDue, lblAfterDue;

    private int assignedSequence = 0;

    private ArrayList<Sequence> localSequence;

/*
    private BroadcastReceiver _mPairingRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                    mblBonded = true;
                else
                    mblBonded = false;
            }
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_bill);
        mContext = this;

        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);

        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        imgBack.setVisibility(View.GONE);

        txtConsumerNo = findViewById(R.id.consumer_no);
        txtInvoiceNo = findViewById(R.id.invoice_no);
        txtMeterNo = findViewById(R.id.meter_no);
        txtConsumerName = findViewById(R.id.consumer_name);
        txtAddress = findViewById(R.id.address);
        txtPrice = findViewById(R.id.price);
        txtCurrentReadingDate = findViewById(R.id.current_reading_date);
        txtPreviousReadingDate = findViewById(R.id.previous_reading_date);
        txtCurrentReading = findViewById(R.id.current_reading);
        txtPreviousReading = findViewById(R.id.previous_reading);
        txtConsumption = findViewById(R.id.consumption);
        txtConsumptionCharges = findViewById(R.id.txt_consumption_charges);
        txtcurrent_charges = findViewById(R.id.txt_current_charges);
        txtDueDate = findViewById(R.id.due_date);
//        txtPenalty = findViewById(R.id.penalty);
//        txtTotalChargesAfterDue = findViewById(R.id.totat_charges_after);
        txtMobileNo = findViewById(R.id.txt_mobile_no);
        txtMeterStatus = findViewById(R.id.txt_meter_status);
        txtAmountBefore = findViewById(R.id.txt_amount_before);
        txtAmountAfter = findViewById(R.id.txt_amount_after_due);
        txtConsumpDays = findViewById(R.id.consumption_days);
        txtBillType = findViewById(R.id.txt_bill_type);
        lblAfterDue = findViewById(R.id.txt_after_due_label);
        lblBeforeDue = findViewById(R.id.txt_before_due_label);

/*
        try {
            setup = new Setup(); // Instantiation
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean result = setup.blActivateLibrary(mContext, R.raw.licence);

        this.mGP = ((App) this.getApplicationContext());*/

        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            mMeterReaderId = userProfile.meter_reader_id;
        }

        Intent intent = getIntent();
        if (intent != null) {
            prv_sequence = intent.getStringExtra("previous_sequence");
            revisitFlag = intent.getStringExtra("revisit_flag");
            if (userJobCard == null) {
                userJobCard = (JobCard) intent.getSerializableExtra(AppConstants.CURRENT_JOB_CARD);
//                spotBillResponse = (Response) intent.getSerializableExtra(AppConstants.SPOT_BILL_CONSUMER_DATA);
            }
            if (meterReading == null){
                meterReading = (MeterReading) intent.getSerializableExtra(AppConstants.CURRENT_METER_READING);
            }
        }

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
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            finish();
        }
        if (v == btnPrint) {
            /*if (EvoluteBTConnection.mbsSocket != null) {
                if (EvoluteBTConnection.mbsSocket.isConnected()) {
                    EnterTextAsync enterTextAsync = new EnterTextAsync();
                    enterTextAsync.execute(0);
                } else
                    new StartBluetoothDeviceTask().execute("");
            } else {
                new StartBluetoothDeviceTask().execute("");
            }*/

            submitReading();
        }
    }

    /* This method shows the EnterTextAsync AsyncTask operation */
   /* public class EnterTextAsync extends AsyncTask<Integer, Integer, Integer> {
        *//* displays the progress dialog un till background task is completed *//*
        @Override
        protected void onPreExecute() {
            try {
            } catch (Exception e) {
            }
            showLoadingDialog();
            super.onPreExecute();
        }

        *//* Task of EnterTextAsync performing in the background *//*
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                String arrears = "";
                if (spotBillResponse.arrears.isEmpty()){
                    arrears = "0";
                } else {
                    arrears = spotBillResponse.arrears;
                }


                SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy");
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null, date1 = null, date2 = null;

                try {
                    date = originalFormat.parse(spotBillResponse.due_date);
                    date1 = originalFormat.parse(spotBillResponse.previous_reading_date);
                    date2 = oldFormat.parse(spotBillResponse.current_reading_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String formattedDate = targetFormat.format(date);
                String formattedDate1 = targetFormat.format(date1);
                String formattedDate2 = targetFormat.format(date2);
                prnGen.iFlushBuf();
                String print = "", print1 = "", print2 = "", print3 = "", print4 = "",
                        print5 = "", print6 = "", print7 = "", print8 = "", print9 = "",
                        print10 = "", print11 = "", print12 = "", print13 = "";

//                print = getString(R.string.printing_matter);
                print = " BHAGYANAGAR GAS LIMITED\n    NATURAL GAS BILL\n" +
                        "DOMESTIC BILL CUM NOTICE";
                print1 = "------------------------------------------";
                print13 = "------------------------";

                print2 = "Date:" + CommonUtils.getDate() +
                        "\nTime:" + CommonUtils.getTime();
                print3 = "Invoice No:" + spotBillResponse.invoice_no +
                        "\nMeter No:" + userJobCard.meter_no +
                        "\nCRN No:" + userJobCard.consumer_no;
                print4 = "Name:" + userJobCard.consumer_name;
                print5 = "Address:" + userJobCard.address
                            + "\nMobile No:"+userJobCard.phone_no;
                print6 = "Previous Reading Date:\n" + formattedDate1
                        + "\nCurrent Reading Date:\n" + formattedDate2;
                print7 = "Previous Reading:" + spotBillResponse.previous_reading
                        + "\nCurrent Reading :"+spotBillResponse.current_reading;
                print8 = "Price(RS/SCM):" + spotBillResponse.total_price
                        + "\nConsumption  :" + spotBillResponse.consumption;
                print9 = "A.Previous Due:" + arrears
                        + "\nB.Payments:" + spotBillResponse.payment
                        + "\nC.Current Charges:"+ spotBillResponse.consumption_charges
                        + "\nD.EMI Security Deposit: " + spotBillResponse.emi_security_deposit;
                print11 = "E.Amount Before Due(A-B+C+D):"+ spotBillResponse.total_charges
                        + "\nF.Due Amount:" + spotBillResponse.amt_after_due_date
                        + "\nG.Amount After Due(E+F):" + spotBillResponse.amt_payable_after_due_date;
                print10 = "TIN No:" + spotBillResponse.tin_no
                        + "\nContact:" + spotBillResponse.contact_no
                        + "\nPay Your Bills From:";
                print12 = "https://play.google.com/store/apps/details?id=com.bynry.cisconsumerapp";

                prnGen.iAddData(Printer_GEN.FONT_LARGE_BOLD, print);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_BOLD, print13);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print2);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print3);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print4);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print5);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print6);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print7);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print8);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print9);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_BOLD, print11);
                prnGen.iAddData(Printer_GEN.FONT_SMALL_BOLD, print1);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, print10);
                prnGen.iAddData(Printer_GEN.FONT_UL_LARGE_NORMAL, print12);
                prnGen.iAddData(Printer_GEN.FONT_LARGE_BOLD, print13 + "\n \n ");
//                prnGen.iBmpPrint(mContext, R.drawable.newimage);



                iRetVal = prnGen.iStartPrinting(1);

            } catch (NullPointerException e) {
                e.printStackTrace();
                iRetVal = DEVICE_NOT_CONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        *//* This displays the status messages of EnterTextAsyc in the dialog box *//*
        @Override
        protected void onPostExecute(Integer result) {
            if (iRetVal == DEVICE_NOT_CONNECTED) {
                Toast.makeText(mContext, getString(R.string.device_not_connected), Toast.LENGTH_SHORT).show();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
//                ptrHandler.obtainMessage(1, "Printing Successfull").sendToTarget();

                Toast.makeText(mContext, getString(R.string.spot_bill_generated_successfully), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, LandingActivity.class);
                startActivity(i);
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                Toast.makeText(mContext, getString(R.string.platen_open), Toast.LENGTH_SHORT).show();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                Toast.makeText(mContext, getString(R.string.paper_out), Toast.LENGTH_SHORT).show();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
//                ptrHandler.obtainMessage(1, "Printer at improper voltage").sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                try {
                    EvoluteBTConnection.closeConn();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(mContext, getString(R.string.print_failed), Toast.LENGTH_SHORT).show();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR) {
//                ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
            } else if (iRetVal == Printer_GEN.NO_RESPONSE) {
                Toast.makeText(mContext, getString(R.string.no_response_from_pride_device), Toast.LENGTH_SHORT).show();
            } else if (iRetVal == Printer_GEN.DEMO_VERSION) {
//                ptrHandler.obtainMessage(1, "Library in demo version").sendToTarget();
            } else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID) {
//                ptrHandler.obtainMessage(1, "Connected  device is not authenticated.").sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_ACTIVATED) {
//                ptrHandler.obtainMessage(1, "Library not activated").sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_SUPPORTED) {
                Toast.makeText(mContext, getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    EvoluteBTConnection.closeConn();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(mContext, getString(R.string.unknown_response_from_device), Toast.LENGTH_SHORT).show();
            }
            dismissLoadingDialog();
            super.onPostExecute(result);
        }
    }*/

    // Turn on Bluetooth of the device
    /*private class StartBluetoothDeviceTask extends AsyncTask<String, String, Integer> {
        private static final int RET_BLUETOOTH_IS_START = 0x0001;
        private static final int RET_BLUETOOTH_START_FAIL = 0x04;
        private static final int miWAIT_TIME = 15;
        private static final int miSLEEP_TIME = 150;
        private ProgressDialog mpd;

        @Override
        public void onPreExecute() {
            mpd = new ProgressDialog(mContext);
            mpd.setMessage(getString(R.string.scanning));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            mpd.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int iWait = miWAIT_TIME * 1000;
            *//* BT isEnable *//*
            if (!mBT.isEnabled()) {
                mBT.enable();
                //Wait miSLEEP_TIME seconds, start the Bluetooth device before you start scanning
                while (iWait > 0) {
                    if (!mBT.isEnabled())
                        iWait -= miSLEEP_TIME;
                    else
                        break;
                    SystemClock.sleep(miSLEEP_TIME);
                }
                if (iWait < 0)
                    return RET_BLUETOOTH_START_FAIL;
            }
            return RET_BLUETOOTH_IS_START;
        }

        *//**
         * After blocking cleanup task execution
         *//*
        @Override
        public void onPostExecute(Integer result) {
            if (mpd.isShowing())
                mpd.dismiss();
            if (RET_BLUETOOTH_START_FAIL == result) {
                // Turning ON Bluetooth failed
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(getString(R.string.error_empty_password));
                builder.setMessage(getString(R.string.error_empty_password));
                builder.setPositiveButton(R.string.error_empty_password, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBT.disable();
                        finish();
                    }
                });
                builder.create().show();
            } else if (RET_BLUETOOTH_IS_START == result) {
                Intent intent = new Intent(mContext, EvoluteBTDiscovery.class);
                startActivityForResult(intent, REQUEST_DISCOVERY);
            }
        }
    }*/

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // TODO : uncomment these lines for bluetooth printer
        *//*if (requestCode == REQUEST_DISCOVERY) {
            if (Activity.RESULT_OK == resultCode) {
                // Device is selected
                this.mhtDeviceInfo.put("NAME", data.getStringExtra("NAME"));
                this.mhtDeviceInfo.put("MAC", data.getStringExtra("MAC"));
                this.mhtDeviceInfo.put("COD", data.getStringExtra("COD"));
                this.mhtDeviceInfo.put("RSSI", data.getStringExtra("RSSI"));
                this.mhtDeviceInfo.put("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
                this.mhtDeviceInfo.put("BOND", data.getStringExtra("BOND"));
                if (this.mhtDeviceInfo.get("BOND").equals("Nothing")) {
                    new PairTask().execute();
                } else {
                    mBDevice = mBT.getRemoteDevice(this.mhtDeviceInfo.get("MAC"));
                    new PairTask().execute();
                }
            } else if (Activity.RESULT_CANCELED == resultCode) {
                // None of device is selected
                Toast.makeText(mContext, getString(R.string.please_select_bluetooth_printer_to_print), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == EXIT_ON_RETURN) {
            finish();
        }*//*
    }*/



    // TODO : uncomment these lines for bluetooth printer
/*
    *//*   This method shows the PairTask  PairTask operation *//*
    private class PairTask extends AsyncTask<String, String, Integer> {
        *//**
         * Constants: the pairing is successful
         *//*
        static private final int RET_BOND_OK = 0x00;
        *//**
         * Constants: Pairing failed
         *//*
        static private final int RET_BOND_FAIL = 0x01;
        *//**
         * Constants: Pairing waiting time (15 seconds)
         *//*
        static private final int iTIMEOUT = 1000 * 15;

        *//**
         * Thread start initialization
         *//*


        @Override
        public void onPreExecute() {
            registerReceiver(_mPairingRequest, new IntentFilter(EvoluteBTPair.PAIRING_REQUEST));
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        }

        *//* Task of PairTask performing in the background*//*
        @Override
        protected Integer doInBackground(String... arg0) {
            final int iStepTime = 150;
            int iWait = iTIMEOUT;
            try {
                mBDevice = mBT.getRemoteDevice(arg0[0]);    //arg0[0] is MAC address
                EvoluteBTPair.createBond(mBDevice);
                mblBonded = false;
            } catch (Exception e1) {
                e1.printStackTrace();
                return RET_BOND_FAIL;
            }
            while (!mblBonded && iWait > 0) {
                SystemClock.sleep(iStepTime);
                iWait -= iStepTime;
            }

            return ((iWait > 0) ? RET_BOND_OK : RET_BOND_FAIL);
        }

        *//* This displays the status messages of PairTask in the dialog box *//*
        @Override
        public void onPostExecute(Integer result) {
            unregisterReceiver(_mPairingRequest);
            if (RET_BOND_OK == result) {
                mhtDeviceInfo.put("BOND", "Bonded");
            } else {
                try {
                    EvoluteBTPair.removeBond(mBDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new ConnSocketTask().execute(mBDevice.getAddress());
            }
        }
    }

    *//*   This method shows the   PairTask operation *//*
    private class ConnSocketTask extends AsyncTask<String, String, Integer> {
        *//**
         * Process waits prompt box
         *//*
        private ProgressDialog mpd = null;
        *//**
         * Constants: connection fails
         *//*
        private static final int CONN_FAIL = 0x01;
        *//**
         * Constant: the connection is established
         *//*
        private static final int CONN_SUCCESS = 0x02;

        *//**
         * Thread start initialization
         *//*
        @Override
        public void onPreExecute() {
            this.mpd = new ProgressDialog(mContext);
            this.mpd.setMessage(getString(R.string.connecting));
            this.mpd.setCancelable(false);
            this.mpd.setCanceledOnTouchOutside(false);
            this.mpd.show();
        }

        *//* Task of  performing in the background*//*
        @Override
        protected Integer doInBackground(String... arg0) {
            if (mGP.createConn(arg0[0])) {
                SystemClock.sleep(2000);
                return CONN_SUCCESS;
            } else {
                return CONN_FAIL;
            }
        }

        *//* This displays the status messages of in the dialog box *//*
        @Override
        public void onPostExecute(Integer result) {
            this.mpd.dismiss();

            if (CONN_SUCCESS == result) {
                Toast.makeText(mContext, "BT connection Established successfully", Toast.LENGTH_SHORT).show();
                genGetSerialNo genSerial = new genGetSerialNo();
                genSerial.execute(0);
            } else {
                Toast.makeText(mContext, getString(R.string.scanning_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class genGetSerialNo extends AsyncTask<Integer, Integer, String> {

        *//**
         * Process waits prompt box
         *//*
        private ProgressDialog mpd = null;

        @Override
        protected void onPreExecute() {
            this.mpd = new ProgressDialog(mContext);
            this.mpd.setMessage("Please wait...");
            this.mpd.setCancelable(false);
            this.mpd.setCanceledOnTouchOutside(false);
            this.mpd.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                try {
                    input = EvoluteBTConnection.misIn;
                    outStream = EvoluteBTConnection.mosOut;
                    prnGen = new Printer_GEN(setup, outStream, input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sDeviceType = prnGen.sGetSerialNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sDeviceType;
        }

        @Override
        protected void onPostExecute(String result) {
            {
                EnterTextAsync enterTextAsync = new EnterTextAsync();
                enterTextAsync.execute(0);
            }
            this.mpd.dismiss();
        }
    }*/

    @Override
    public void onBackPressed() {
        showMessageDialog(mContext);
    }


    private void showMessageDialog(final Context context) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(R.string.going_back_will_cancel_your_spot_billing_process_do_you_still_want_to_go_back);
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
            public void onClick(View v) {
                alert.dismiss();
                launchNext();
            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setBackground(getResources().getDrawable(R.drawable.positive_button));
        cancel.setText("NO");
//        cancel.setLayoutParams(params);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

    public void launchNext() {
        //finish();
        Intent i = new Intent(mContext, LandingActivity.class);
        startActivity(i);

    }

    private void initAllUIComponents() {



        String consumtionCharges, currentCharges, amountBefore, amountAfter;
        /*SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");

        Date date = null, date1 = null, date2 = null;
        try {
            date = originalFormat.parse(spotBillResponse.due_date);
            Log.d("aaaaaaaaaaa",""+spotBillResponse.previous_reading_date);
            date1 = originalFormat.parse(spotBillResponse.previous_reading_date);
            date2 = oldFormat.parse(spotBillResponse.current_reading_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);
        String formattedDate1 = targetFormat.format(date1);
        String formattedDate2 = targetFormat.format(date2);*/

        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = oldFormat.parse(meterReading.reading_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);

        String conNo = "", meterNo = "", name = "";
        conNo = userJobCard.consumer_no == null || userJobCard.consumer_no.equals("None") || userJobCard.consumer_no.equals("0") || userJobCard.consumer_no.equalsIgnoreCase("NULL") ? "" : userJobCard.consumer_no;
        meterNo = userJobCard.meter_no == null || userJobCard.meter_no.equals("None") || userJobCard.meter_no.equals("0") || userJobCard.meter_no.equalsIgnoreCase("NULL") ? "" : userJobCard.meter_no;
        name = userJobCard.consumer_name == null || userJobCard.consumer_name.equals("None") || userJobCard.consumer_name.equals("0") || userJobCard.consumer_name.equalsIgnoreCase("NULL") ? "" : userJobCard.consumer_name;


        txtConsumerNo.setText(conNo + " | " + meterNo + " | " + name);
//        txtInvoiceNo.setText(spotBillResponse.invoice_no == null || spotBillResponse.invoice_no.equals("None") || spotBillResponse.invoice_no.equals("0") || spotBillResponse.invoice_no.equalsIgnoreCase("NULL") ? "" : spotBillResponse.invoice_no);
        txtMeterNo.setText(userJobCard.meter_no == null || userJobCard.meter_no.equals("None") || userJobCard.meter_no.equals("0") || userJobCard.meter_no.equalsIgnoreCase("NULL") ? "" : userJobCard.meter_no);
        txtConsumerName.setText(userJobCard.consumer_name == null || userJobCard.consumer_name.equals("None") || userJobCard.consumer_name.equals("0") || userJobCard.consumer_name.equalsIgnoreCase("NULL") ? "" : userJobCard.consumer_name);
        txtMeterStatus.setText(meterReading.meter_status == null || meterReading.meter_status.equals("None") || meterReading.meter_status.equals("0") || meterReading.meter_status.equalsIgnoreCase("NULL") ? "" : meterReading.meter_status);
//        txtAddress.setText(userJobCard.address == null || userJobCard.address.equals("None") || userJobCard.address.equals("0") || userJobCard.address.equalsIgnoreCase("NULL") ? "" : userJobCard.address);
        txtPrice.setText(userJobCard.total_price == null || userJobCard.total_price.equals("None") || userJobCard.total_price.equals("0") || userJobCard.total_price.equalsIgnoreCase("NULL") ? "" : userJobCard.total_price);

        txtPreviousReadingDate.setText((userJobCard.prv_meter_reading == null || userJobCard.prv_meter_reading.equals("None")
                || userJobCard.prv_meter_reading.equals("0") || userJobCard.prv_meter_reading.equalsIgnoreCase("NULL")
                ? "" : userJobCard.prv_meter_reading) + " | " +(userJobCard.prev_reading_date == null
                || userJobCard.prev_reading_date.equals("None") || userJobCard.prev_reading_date.equals("0") ||
                userJobCard.prev_reading_date.equalsIgnoreCase("NULL") ? "" : userJobCard.prev_reading_date));
        if (meterReading.bill_type.equals("Average")){
            Integer consumptionAverageBill = Integer.parseInt(userJobCard.prv_meter_reading) + Integer.parseInt(userJobCard.avg_unit);

            txtCurrentReading.setText(String.valueOf(consumptionAverageBill));
            txtConsumption.setText(userJobCard.avg_unit + " | " +
                    (meterReading.consump_days == null || meterReading.consump_days.equals("None") ||
                    meterReading.consump_days.equals("0") || meterReading.consump_days.equalsIgnoreCase("NULL") ? "" :
                    meterReading.consump_days) + " Days");
            txtCurrentReadingDate.setText(consumptionAverageBill + " | " + formattedDate);
        } else {
            Integer consumption = Integer.parseInt(meterReading.current_meter_reading) - Integer.parseInt(userJobCard.prv_meter_reading);

            txtCurrentReading.setText(meterReading.current_meter_reading == null || meterReading.current_meter_reading.equals("None") || meterReading.current_meter_reading.equals("0") || meterReading.current_meter_reading.equalsIgnoreCase("NULL") ? "" : meterReading.current_meter_reading);
            txtCurrentReadingDate.setText((meterReading.current_meter_reading == null ||
                    meterReading.current_meter_reading.equals("None")
                    || meterReading.current_meter_reading.equals("0") ||
                    meterReading.current_meter_reading.equalsIgnoreCase("NULL") ? "" :
                    meterReading.current_meter_reading)
                    + " | " +formattedDate);

            txtConsumption.setText(consumption + " | " +
                    (meterReading.consump_days == null || meterReading.consump_days.equals("None") ||
                    meterReading.consump_days.equals("0") || meterReading.consump_days.equalsIgnoreCase("NULL") ? "" :
                    meterReading.consump_days) + " Days");

        }

        String[] consumtionArray, currentChargesArray, amountBeforeArray, amountAfterArray;

        consumtionArray = (meterReading.consumptionCharges == null || meterReading.consumptionCharges.equals("None") || meterReading.consumptionCharges.equals("0") || meterReading.consumptionCharges.equalsIgnoreCase("NULL") ? "" : meterReading.consumptionCharges).split("\\.");
        currentChargesArray = (meterReading.current_charges == null || meterReading.current_charges.equals("None") || meterReading.current_charges.equals("0") || meterReading.current_charges.equalsIgnoreCase("NULL") ? "" : meterReading.current_charges).split("\\.");
        amountBeforeArray = (meterReading.amt_before_due == null || meterReading.amt_before_due.equals("None") || meterReading.amt_before_due.equals("0") || meterReading.amt_before_due.equalsIgnoreCase("NULL") ? "" : meterReading.amt_before_due).split("\\.");
        amountAfterArray = (meterReading.amt_after_due == null || meterReading.amt_after_due.equals("None") || meterReading.amt_after_due.equals("0") || meterReading.amt_after_due.equalsIgnoreCase("NULL") ? "" : meterReading.amt_after_due).split("\\.");

        Log.d("3333333333333333",""+consumtionArray);
        Log.d("3333333333333333",""+consumtionArray[0]);
        Log.d("3333333333333333","");
        Double consumtionCharges1 = Double.parseDouble(meterReading.consumptionCharges == null || meterReading.consumptionCharges.equals("None") || meterReading.consumptionCharges.equals("0") || meterReading.consumptionCharges.equalsIgnoreCase("NULL") ? "" : meterReading.consumptionCharges);
        currentCharges = currentChargesArray[0];
        amountBefore = amountBeforeArray[0];
        amountAfter = amountAfterArray[0];
        txtPreviousReading.setText(userJobCard.prv_meter_reading == null || userJobCard.prv_meter_reading.equals("None") || userJobCard.prv_meter_reading.equals("0") || userJobCard.prv_meter_reading.equalsIgnoreCase("NULL") ? "" : userJobCard.prv_meter_reading);
//        txtConsumptionCharges.setText(consumtionCharges);
        txtConsumptionCharges.setText(String.format("%.2f",consumtionCharges1));
//        txtEmi.setText(String.valueOf(spotBillResponse.emi_security_deposit));
//        txtArrears.setText(spotBillResponse.arrears == null || spotBillResponse.arrears.equals("None") || spotBillResponse.arrears.equals("0") || spotBillResponse.arrears.equalsIgnoreCase("NULL") ? "" : spotBillResponse.arrears);
//        txtcurrent_charges.setText(meterReading.current_charges == null || meterReading.current_charges.equals("None") || meterReading.current_charges.equals("0") || meterReading.current_charges.equalsIgnoreCase("NULL") ? "" : meterReading.current_charges);
        txtcurrent_charges.setText(currentCharges);
//        txtDueDate.setText(meterReading.due_date == null || meterReading.due_date.equals("None") || meterReading.due_date.equals("0") || meterReading.due_date.equalsIgnoreCase("NULL") ? "" : meterReading.due_date);
//        txtPenalty.setText(spotBillResponse.amt_after_due_date == null || spotBillResponse.amt_after_due_date.equals("None") || spotBillResponse.amt_after_due_date.equals("0") || spotBillResponse.amt_after_due_date.equalsIgnoreCase("NULL") ? "" : spotBillResponse.amt_after_due_date);
//        txtTotalChargesAfterDue.setText(spotBillResponse.amt_payable_after_due_date == null || spotBillResponse.amt_payable_after_due_date.equals("None") || spotBillResponse.amt_payable_after_due_date.equals("0") || spotBillResponse.amt_payable_after_due_date.equalsIgnoreCase("NULL") ? "" : spotBillResponse.amt_payable_after_due_date);
        txtMobileNo.setText(userJobCard.phone_no == null || userJobCard.phone_no.equals("None") || userJobCard.phone_no.equals("0") || userJobCard.phone_no.equalsIgnoreCase("NULL") ? "" : userJobCard.phone_no);


//        txtAmountBefore.setText(meterReading.amt_before_due == null || meterReading.amt_before_due.equals("None") || meterReading.amt_before_due.equals("0") || meterReading.amt_before_due.equalsIgnoreCase("NULL") ? "" : meterReading.amt_before_due);
        txtAmountBefore.setText(amountBefore);
//        txtAmountAfter.setText(meterReading.amt_after_due == null || meterReading.amt_after_due.equals("None") || meterReading.amt_after_due.equals("0") || meterReading.amt_after_due.equalsIgnoreCase("NULL") ? "" : meterReading.amt_after_due);
        txtAmountAfter.setText(amountAfter);
        txtConsumpDays.setText(meterReading.consump_days == null || meterReading.consump_days.equals("None") || meterReading.consump_days.equals("0") || meterReading.consump_days.equalsIgnoreCase("NULL") ? "" : meterReading.consump_days);
        txtBillType.setText(meterReading.bill_type == null || meterReading.bill_type.equals("None") || meterReading.bill_type.equals("0") || meterReading.bill_type.equalsIgnoreCase("NULL") ? "" : meterReading.bill_type);


        lblBeforeDue.setText(getString(R.string.amount_due_before));
        lblAfterDue.setText(getString(R.string.amount_due_after));

    }



    private void submitReading() {
        String sequence = userJobCard.prv_sequence;
        if (sequence.length() > 0) {
            int seq = Integer.parseInt(sequence) + 1;
            prv_sequence = "" + seq;
        }
        saveToDatabase();

    }

    private void saveToDatabase() {

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

        launchNext();

    }

}