package com.sgl.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.db.DatabaseManager;
import com.sgl.models.Disconnection;
import com.sgl.models.UploadDisconnectionNotices;
import com.sgl.utils.App;
import com.sgl.utils.CommonUtils;
import com.sgl.utils.DialogCreator;
import com.sgl.utils.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisconnectionNoticeDetailActivity extends ParentActivity implements LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private Typeface regular, bold;
    private GoogleMap map;
    private ImageView imageBack;
    private double currentLat = 0, currentLong = 0, destinationLat = 0, destinationLong = 0;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private Handler handler;
    private Runnable runnable;

    private Disconnection disconnection;
    private TextView txtToolbarTitle, txtNoticeNumber;
    private ImageView imgViewMore, imgCall;
    private Spinner mDeliveryStatus, mDeliveryRemark;
    private String deliveryStatus = "", deliveryRemark = "";
    private Button btnSubmit;
    private UploadDisconnectionNotices uploadDisconnectionNotices;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 5000; // 1 sec
    private static int FASTEST_INTERVAL = 5000; // 1 sec
    private static int DISPLACEMENT = 0; // 0 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnection_notice_detail);
        mContext = this;

        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();

        Intent intent = getIntent();
        if (intent != null) {
            disconnection = (Disconnection) intent.getSerializableExtra(AppConstants.DISCONNECTION_ADAPTER_VALUE);
        }

        imageBack = findViewById(R.id.img_back);
        imageBack.setOnClickListener(this);
        imgViewMore = findViewById(R.id.img_view_more);
        imgViewMore.setOnClickListener(this);
        imgCall = findViewById(R.id.img_call);
        imgCall.setOnClickListener(this);
        btnSubmit = findViewById(R.id.submit);
        btnSubmit.setOnClickListener(this);

        txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        txtToolbarTitle.setTypeface(regular);
        txtNoticeNumber = findViewById(R.id.txt_notice_number);
        txtNoticeNumber.setTypeface(bold);
        txtNoticeNumber.setText(disconnection.disconnection_notice_no);

        mDeliveryStatus = findViewById(R.id.spinner_delivery_status);
        setDeliveryStatus(R.array.delivery_status);
        mDeliveryRemark = findViewById(R.id.spinner_delivery_remark);
        setDeliveryRemark(R.array.delivery_remark_default);

        mDeliveryStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                deliveryStatus = mDeliveryStatus.getSelectedItem().toString().trim();
                if (deliveryStatus.equals(getString(R.string.delivered)))
                    setDeliveryRemark(R.array.delivery_remark_delivered);
                else if (deliveryStatus.equals(getString(R.string.not_delivered)))
                    setDeliveryRemark(R.array.delivery_remark_not_delivered);
                else
                    setDeliveryRemark(R.array.delivery_remark_default);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        mDeliveryRemark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                deliveryRemark = mDeliveryRemark.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        String mobileNo = disconnection.contact_no;

        if(mobileNo == null || mobileNo.equals("0") || mobileNo.isEmpty())
            imgCall.setVisibility(View.GONE);
        else
            imgCall.setVisibility(View.VISIBLE);

        String prvLat = disconnection.latitude;
        String prvLong = disconnection.longitude;

        if (prvLat.isEmpty() && prvLong.isEmpty()) {
            destinationLat = 0;
            destinationLong = 0;
        } else {
            destinationLat = Double.parseDouble(prvLat);
            destinationLong = Double.parseDouble(prvLong);
        }

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        getLocation();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Getting reference to SupportMapFragment of the activity_main
                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                // Getting Map for the SupportMapFragment
                fm.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;

                        LatLng current = new LatLng(currentLat, currentLong);
                        LatLng destination = new LatLng(destinationLat, destinationLong);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        map.setMyLocationEnabled(true);

                        MarkerOptions options = new MarkerOptions();

                        if (destinationLat == 0 && destinationLong == 0) {
                            options.position(current);
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            Toast.makeText(mContext, getString(R.string.meter_location_not_available), Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < 2; i++) {
                                // Setting the position of the marker
                                if (i == 0) {
                                    options.position(current);
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                } else {
                                    options.position(destination);
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                                }
                                map.addMarker(options);
                            }

                            // Getting URL to the Google Directions API
                            String url = getDirectionsUrl(current, destination);

                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);
                        }
                    }
                });
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private void getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                currentLat = mLastLocation.getLatitude();
                currentLong = mLastLocation.getLongitude();
            } else {
                currentLat = 0;
                currentLong = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of txtBinderCode
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of txtBinderCode
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            checkPlayServices();
            // Resuming the periodic location updates
            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void finish() {
        super.finish();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View v) {
        if (v == imageBack) {
            finish();
        }

        if (v == imgViewMore) {
            DialogCreator.showConsumerDetailsDialog(mContext, disconnection.consumer_name, disconnection.address, disconnection.consumer_no,
                    disconnection.zone_code, disconnection.contact_no, disconnection.binder_code, "",
                    disconnection.disconnection_notice_no, "", "", "");
        }

        if (v == btnSubmit) {
            checkValidation();
        }

        if(v == imgCall)
        {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", disconnection.contact_no, null));
            startActivity(intent);
        }
    }

    private void checkValidation() {
        if (deliveryStatus != null && deliveryRemark != null) {
            if (deliveryStatus.equals(getString(R.string.delivery_status_mandatory)))
                Toast.makeText(mContext, getString(R.string.please_provide_delivery_status), Toast.LENGTH_SHORT).show();
            else {
                if (deliveryRemark.equals(getString(R.string.delivery_remark_mandatory)))
                    Toast.makeText(mContext, getString(R.string.please_provide_delivery_remark), Toast.LENGTH_SHORT).show();
                else {
                   submitData();
                }
            }
        }
    }
    private void submitData()
    {
        uploadDisconnectionNotices = new UploadDisconnectionNotices();

        uploadDisconnectionNotices.meter_reader_id = disconnection.meter_reader_id;
        uploadDisconnectionNotices.binder_code = disconnection.binder_code;
        uploadDisconnectionNotices.consumer_no = disconnection.consumer_no;
        uploadDisconnectionNotices.consumer_name = disconnection.consumer_name;
        uploadDisconnectionNotices.job_card_id = disconnection.job_card_id;
        uploadDisconnectionNotices.zone_code = disconnection.zone_code;
        uploadDisconnectionNotices.bill_month = disconnection.bill_month;
        uploadDisconnectionNotices.current_date = CommonUtils.getCurrentDateTime();
        uploadDisconnectionNotices.delivery_status = deliveryStatus;
        uploadDisconnectionNotices.delivery_remark = deliveryRemark;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            uploadDisconnectionNotices.current_latitude = String.valueOf(mLastLocation.getLatitude());
            uploadDisconnectionNotices.current_longitude = String.valueOf(mLastLocation.getLongitude());
        } else {
            uploadDisconnectionNotices.current_latitude = "0";
            uploadDisconnectionNotices.current_longitude = "0";
        }

        DatabaseManager.saveUploadDisconnectionNotices(mContext, uploadDisconnectionNotices);
        DatabaseManager.updateDCJobCardStatus(mContext, disconnection, AppConstants.JOB_CARD_STATUS_COMPLETED);

        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
//        Log.i("Connection", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>
    {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url)
        {
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {e.printStackTrace();}
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try
            {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {e.printStackTrace();}
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);
            lineOptions.color(Color.GRAY);

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++)
            {
                // Fetching i-th txtBinderCode
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th txtBinderCode
                for (int j = 0; j < path.size(); j++)
                {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the txtBinderCode to LineOptions
                lineOptions.addAll(points);
            }

            // Drawing polyline in the Google Map for the i-th txtBinderCode
            if(points.size()!=0)map.addPolyline(lineOptions);//to avoid crash
        }
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(mContext, getString(R.string.this_device_is_not_supported), Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void setDeliveryStatus(int deliveryArray)
    {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(deliveryArray)) {
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
        mDeliveryStatus.setAdapter(adapter2);
    }

    private void setDeliveryRemark(int deliveryRemark)
    {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(deliveryRemark)) {
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
        mDeliveryRemark.setAdapter(adapter2);
    }
}