package com.sgl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.Consumer;
import com.sgl.models.UserProfile;
import com.sgl.preferences.SharedPrefManager;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.LocationManagerReceiver;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNewConsumerActivity extends ParentActivity implements View.OnClickListener {

    private Context mContext;
    private EditText edtConsumerNo, edtMeterNo, edtPoleNo, edtConsumerName, edtConsumerAddress,
            edtPhoneNo, edtEmail;
    private String meter_reader_id;
    private Button btnAddTaskReading;
    private ImageView imgBack;
    private TextView consumerDetails, feederDetails, title, txtName, txtConsumerNo, txtMeterNo, txtEmailId,
            txtAddress, txtPoleNo, txtPhoneNo, feederDetailSubTitle, consumerDetailsSubTitle;
    private UserProfile userProfile;
    public static AddNewConsumerActivity addNewConsumerActivity;
    private Consumer consumer;
    private Typeface regular, bold;
    private Spinner edtBillMonth, edtRouteId, edtBillCycleCode, edtZoneCode;
    private LocationManagerReceiver mLocationManagerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_consumer);
        mContext = this;

        mLocationManagerReceiver = new LocationManagerReceiver(this);

        regular = App.getSansationRegularFont();


        bold = App.getSansationBoldFont();
        addNewConsumerActivity = this;
        consumerDetails = findViewById(R.id.consumer_details);
        consumerDetails.setTypeface(bold);
        title = findViewById(R.id.title_bar);
        title.setTypeface(regular);
        consumerDetailsSubTitle = findViewById(R.id.consumer_details_sub_title);
        consumerDetailsSubTitle.setTypeface(regular);
        feederDetailSubTitle = findViewById(R.id.feeder_delaits_sub_title);
        feederDetailSubTitle.setTypeface(regular);
        feederDetails = findViewById(R.id.feeder_delaits);
        feederDetails.setTypeface(bold);
        txtPhoneNo = findViewById(R.id.til_phone_no);
        txtPhoneNo.setTypeface(regular);
        txtAddress = findViewById(R.id.til_consumer_address);
        txtAddress.setTypeface(regular);
        txtEmailId = findViewById(R.id.til_consumer_email);
        txtEmailId.setTypeface(regular);
        txtConsumerNo = findViewById(R.id.til_consumer_no);
        txtConsumerNo.setTypeface(regular);
        txtMeterNo = findViewById(R.id.til_meter_no);
        txtMeterNo.setTypeface(regular);
        txtName = findViewById(R.id.til_consumer_name);
        txtName.setTypeface(regular);
        txtPoleNo = findViewById(R.id.til_pole_no);
        txtPoleNo.setTypeface(regular);
        consumer = (Consumer) getIntent().getSerializableExtra(AppConstants.CONSUMER_OBJ);
        edtConsumerNo = findViewById(R.id.edt_consumer_no);
        edtConsumerNo.setTypeface(bold);
        edtConsumerNo.setText(consumer != null ? consumer.consumer_no != null ? consumer.consumer_no : "" : "");
        edtMeterNo = findViewById(R.id.edt_meter_no);
        edtMeterNo.setTypeface(bold);
        edtMeterNo.setText(consumer != null ? consumer.meter_no != null ? consumer.meter_no : "" : "");
        edtBillCycleCode = findViewById(R.id.edt_bill_cycle_code);
        edtRouteId = findViewById(R.id.edt_route_id);
        edtPoleNo = findViewById(R.id.edt_pole_no);
        edtPoleNo.setTypeface(bold);
        edtConsumerName = findViewById(R.id.edt_consumer_name);
        edtConsumerName.setTypeface(bold);
        edtEmail = findViewById(R.id.edt_consumer_email);
        edtEmail.setTypeface(bold);
        edtMeterNo.setText(consumer != null ? consumer.meter_no != null ? consumer.meter_no : "" : "");
        edtMeterNo.setTypeface(bold);
        edtEmail = findViewById(R.id.edt_consumer_email);
        edtEmail.setTypeface(bold);
        edtConsumerName.setText(consumer != null ? consumer.consumer_name != null ? consumer.consumer_name : "" : "");
        edtConsumerAddress = findViewById(R.id.edt_consumer_address);
        edtConsumerAddress.setTypeface(bold);
        edtPhoneNo = findViewById(R.id.edt_phone_no);
        edtPhoneNo.setTypeface(bold);
        edtBillMonth = findViewById(R.id.edt_bill_month);
        imgBack = findViewById(R.id.img_back);
        btnAddTaskReading = findViewById(R.id.btn_add_task_reading);
        imgBack.setOnClickListener(this);
        btnAddTaskReading.setTypeface(regular);
        btnAddTaskReading.setOnClickListener(this);
        edtZoneCode = findViewById(R.id.edt_zone_code);
        getUserProfileDetails();
        setRouteData(meter_reader_id);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        edtPhoneNo.addTextChangedListener(new TextWatcher() {
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

    }

    private boolean validateNumber() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String PHONE_PATTERN1 = "^[7-9][0-9]{9}$";
        final String PHONE_PATTERN2 = "";
        pattern1 = Pattern.compile(PHONE_PATTERN1);
        pattern2 = Pattern.compile(PHONE_PATTERN2);
        String phone = edtPhoneNo.getText().toString().trim();
        matcher1 = pattern1.matcher(phone);
        matcher2 = pattern2.matcher(phone);

        if (matcher1.matches() || matcher2.matches()) {
            edtPhoneNo.setError(null);
            return true;
        } else {
            edtPhoneNo.setError(getString(R.string.enter_valid_mobile_no));
            edtPhoneNo.requestFocus();
            return false;
        }
    }

    private boolean validateEmail() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String EMAIL_PATTERN1 =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$";
        final String EMAIL_PATTERN2 = "";
        pattern1 = Pattern.compile(EMAIL_PATTERN1);
        pattern2 = Pattern.compile(EMAIL_PATTERN2);
        String email = edtEmail.getText().toString().trim();
        matcher1 = pattern1.matcher(email);
        matcher2 = pattern2.matcher(email);
        if (matcher1.matches() || matcher2.matches()) {
            edtEmail.setError(null);
            return true;
        } else {
            edtEmail.setError(getString(R.string.enter_valid_email_id));
            edtEmail.requestFocus();
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_add_task_reading:
                //Check consumer no. is empty or not
                if (edtConsumerNo.getText().toString().trim().length() > 0) {
                    // Check consumer no. is exactly equals 12 if Yes
                    if (edtConsumerNo.getText().toString().trim().length() > 5 && edtConsumerNo.getText().toString().trim().length() <= 8) {
                        //check meter no is entered or not
                        if (edtMeterNo.getText().toString().trim().length() > 0) {
                            //then check meter no is in range 1-20 if Yes
                            if (edtMeterNo.getText().toString().trim().length() > 0 && edtMeterNo.getText().toString().trim().length() <= 20) {
                                //check consumer name is entered or not
                                if (edtConsumerName.getText().toString().trim().length() >= 0) {
                                    //check is phone no. is entered
                                    if (edtPhoneNo.getText().toString().trim().length() > 0) {
                                        // if phone no. is entered then check it is valid or not
                                        if (validateNumber()) {
                                            //check email id is entered
                                            if (edtEmail.getText().toString().trim().length() > 0) {
                                                //if email id is entered then check it is valid or not
                                                if (validateEmail()) {
                                                    checkValidation();
                                                } else {
                                                    Toast.makeText(mContext, getString(R.string.please_enter_valid_email_id), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            //if email Id is not entered
                                            else {
                                                checkValidation();
                                            }
                                        } else {
                                            Toast.makeText(mContext, getString(R.string.please_enter_valid_mobile_number), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //if mobile number is not entered
                                    else {
                                        //check email id is entered
                                        if (edtEmail.getText().toString().trim().length() > 0) {
                                            //if email id is entered then check it is valid or not
                                            if (validateEmail()) {
                                                checkValidation();
                                            } else {
                                                Toast.makeText(mContext, getString(R.string.please_enter_valid_email_id), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        //if email Id is not entered
                                        else {
                                            checkValidation();
                                        }
                                    }
                                } else {
                                    Toast.makeText(mContext, getString(R.string.please_enter_consumer_name), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.please_enter_valid_meter_number), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, getString(R.string.please_enter_meter_number), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.please_enter_valid_consumer_number), Toast.LENGTH_SHORT).show();
                    }
                } else {//check meter no is entered or not
                    if (edtMeterNo.getText().toString().trim().length() > 0) {
                        //then check meter no is in range 1-20 if Yes
                        if (edtMeterNo.getText().toString().trim().length() > 0 && edtMeterNo.getText().toString().trim().length() <= 20) {
                            //check consumer name is entered or not
                            if (edtConsumerName.getText().toString().trim().length() >= 0) {
                                //check is phone no. is entered
                                if (edtPhoneNo.getText().toString().trim().length() > 0) {
                                    // if phone no. is entered then check it is valid or not
                                    if (validateNumber()) {
                                        //check email id is entered
                                        if (edtEmail.getText().toString().trim().length() > 0) {
                                            //if email id is entered then check it is valid or not
                                            if (validateEmail()) {
                                                checkValidation();
                                            } else {
                                                Toast.makeText(mContext, getString(R.string.please_enter_valid_email_id), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        //if email Id is not entered
                                        else {
                                            checkValidation();
                                        }
                                    } else {
                                        Toast.makeText(mContext, getString(R.string.please_enter_valid_mobile_number), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //if mobile number is not entered
                                else {
                                    //check email id is entered
                                    if (edtEmail.getText().toString().trim().length() > 0) {
                                        //if email id is entered then check it is valid or not
                                        if (validateEmail()) {
                                            checkValidation();
                                        } else {
                                            Toast.makeText(mContext, getString(R.string.please_enter_valid_email_id), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //if email Id is not entered
                                    else {
                                        checkValidation();
                                    }
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.please_enter_consumer_name), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, getString(R.string.please_enter_valid_meter_number), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.please_enter_meter_number), Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    private void checkValidation() {
        //check bill month is added or not
        if (edtBillMonth.getSelectedItemPosition() != 0) {
            //check bill cycle is added or not
            if (edtZoneCode.getSelectedItemPosition() != 0) {
                //check txtBinderCode id is added or not
                if (edtBillCycleCode.getSelectedItemPosition() != 0) {
                    //check zone_code code is added or not
                    if (edtRouteId.getSelectedItemPosition() != 0) {
                        // if all conditions are satisfied then call below method
                        validationComplete();
                    } else {
                        Toast.makeText(mContext, getString(R.string.please_add_valid_binder), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.please_add_valid_cycle), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, getString(R.string.please_add_valid_sub_division), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, getString(R.string.please_add_valid_bill_month), Toast.LENGTH_SHORT).show();
        }
    }

    private void validationComplete() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        PackageManager pm = this.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if (isGPSEnabled && hasGps) {
            takeMeterReading();
        } else if (isNetworkEnabled && !hasGps) {
            takeMeterReading();
        } else {
            mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        }
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

    private void setRouteData(String meter_reader_id) {
        ArrayList<String> routes = new ArrayList<>();
        routes.add(getString(R.string.mru_mandatory));
        routes.addAll(DatabaseManager.getRoutes(mContext, meter_reader_id));
        if (routes != null && routes.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, routes) {
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
            edtRouteId.setAdapter(dataAdapter);
        }

        ArrayList<String> billCycleCode = new ArrayList<>();
        billCycleCode.add(getString(R.string.cycle_mandatory));
        billCycleCode.addAll(DatabaseManager.getBillCycleCode(mContext, meter_reader_id));
        if (billCycleCode != null && billCycleCode.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, billCycleCode) {
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
            edtBillCycleCode.setAdapter(dataAdapter);
        }

        ArrayList<String> billMonth = new ArrayList<>();
        billMonth.add(getString(R.string.bill_month_mandatory));
        billMonth.addAll(DatabaseManager.getBillMonth(mContext, meter_reader_id));
        if (billMonth != null && billMonth.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, billMonth) {
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
            edtBillMonth.setAdapter(dataAdapter);
        }

        final ArrayList<String> zoneCode = new ArrayList<>();
        zoneCode.add(getString(R.string.portion_mandatory));
        zoneCode.addAll(DatabaseManager.getZone(mContext, meter_reader_id));
        if (zoneCode != null && zoneCode.size() > 0) {
            ArrayAdapter<String> dataAdapterForZone = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, zoneCode) {
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
            dataAdapterForZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edtZoneCode.setAdapter(dataAdapterForZone);
        }


    }

    private void takeMeterReading() {

        Consumer consumer = new Consumer();
        consumer.meter_reader_id = meter_reader_id;
        consumer.consumer_name = edtConsumerName.getText().toString().trim();
        consumer.consumer_no = (edtConsumerNo.getText().toString().length() > 0 &&
                (edtConsumerNo.getText().toString().length() == 6) || edtConsumerNo.getText().toString().length() == 7
                    || edtConsumerNo.getText().toString().length() == 8) ? edtConsumerNo.getText().toString().trim() : "1234567";
        consumer.meter_no = edtMeterNo.getText().toString().trim();
        consumer.bill_cycle_code = edtBillCycleCode.getSelectedItem().toString().trim();
        consumer.pole_no = edtPoleNo.getText().toString().trim();
        consumer.contact_no = edtPhoneNo.getText().toString().trim();
        consumer.address = edtConsumerAddress.getText().toString().trim();
        consumer.route_code = edtRouteId.getSelectedItem().toString().trim();
        consumer.email_id = edtEmail.getText().toString().trim();
        consumer.reading_month = edtBillMonth.getSelectedItem().toString().trim();
        consumer.mobile_no = edtPhoneNo.getText().toString().trim();
        consumer.zone_code = edtZoneCode.getSelectedItem().toString().trim();

        Intent intent = new Intent(mContext, UnBillMeterReadingActivity.class);
        intent.putExtra(AppConstants.CURRENT_CONSUMER_OBJ, consumer);
        startActivity(intent);
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

}
