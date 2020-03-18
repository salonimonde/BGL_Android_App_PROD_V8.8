package com.sgl.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.prowesspride.api.Setup;
import com.sgl.R;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Pattern;

public abstract class EvoluteBTDiscovery extends Activity implements View.OnClickListener {

   /* Setup setup = null;
    *//**CONST: device type bluetooth 2.1*//*
    public static final int DEVICE_TYPE_BREDR = 0x01;
    *//**CONST: device type bluetooth 4.0 ble*//*
    public static final int DEVICE_TYPE_BLE = 0x02;
    *//**CONST: device type bluetooth double mode*//*
    public static final int DEVICE_TYPE_DUMO = 0x03;
    public final static String EXTRA_DEVICE_TYPE = "android.bluetooth.device.extra.DEVICE_TYPE";
    *//** Discovery is Finished *//*
    private boolean _discoveryFinished;
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    *//**bluetooth List View*//*
    private ListView mlvList = null;
    *//**
     * Storage the found bluetooth devices
     * format:<MAC, <Key,Val>>;Key=[RSSI/NAME/COD(class od device)/BOND/UUID]
     * *//*
    private Hashtable<String, Hashtable<String, String>> mhtFDS = null;

    *//**(List of storage arrays for display) dynamic array of objects / ** ListView's *//*
    private ArrayList<HashMap<String, Object>> malListItem = null;
    *//**SimpleAdapter object (list display container objects)*//*
    Context mContext = this;
    private SimpleAdapter msaListItemAdapter = null;
    *//**
     * Scan for Bluetooth devices. (broadcast listener)
     *//*
    private static int iCurrDev = 1;
    private BroadcastReceiver _foundReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, final Intent intent){
            new Thread(){
                public void run(){
                    iCurrDev++;
			*//* bluetooth device profiles*//*
                    Hashtable<String, String> htDeviceInfo = new Hashtable<String, String>();
			
			*//* get the search results *//*
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			*//* create found device profiles to htDeviceInfo*//*
                    Bundle b = intent.getExtras();
                    htDeviceInfo.put("RSSI", String.valueOf(b.get(BluetoothDevice.EXTRA_RSSI)));
                    if (null == device.getName())
                        htDeviceInfo.put("NAME", "Null");
                    else
                        htDeviceInfo.put("NAME", device.getName());

                    htDeviceInfo.put("COD",  String.valueOf(b.get(BluetoothDevice.EXTRA_CLASS)));
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                        htDeviceInfo.put("BOND", "actDiscovery_bond_bonded");
                    else
                        htDeviceInfo.put("BOND", "actDiscovery_bond_nothing");

                    String sDeviceType = String.valueOf(b.get(EXTRA_DEVICE_TYPE));
                    if (!sDeviceType.equals("null"))
                        htDeviceInfo.put("DEVICE_TYPE", sDeviceType);
                    else
                        htDeviceInfo.put("DEVICE_TYPE", "-1"); 

			*//*adding scan to the device profiles*//*
                    mhtFDS.put(device.getAddress(), htDeviceInfo);
			
			*//*Refresh show list*//*
                    //showDevices();
                    myHandler.obtainMessage(987).sendToTarget();
                }
            }.start();
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 987:
                    showDevices();
                    break;
                default:
                    break;
            }
        };
    };

    *//**
     * Bluetooth scanning is finished processing.(broadcast listener)
     *//*
    private BroadcastReceiver _finishedReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent){
            _discoveryFinished = true; //set scan is finished
            unregisterReceiver(_foundReceiver);
            unregisterReceiver(_finishedReceiver);


            if (null != mhtFDS && mhtFDS.size()>0){
                Toast.makeText(mContext, getString(R.string.select_device) , Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, getString(R.string.device_not_found), Toast.LENGTH_LONG).show();
            }
        }
    };


    *//**
     * start run
     * *//*
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act__btdiscovery);

        //DISCLAIMER: INORDER TO SEARCH BlUETOOTH FASTER, AND IF WIFI WAS ALREADY ENABLED BEFORE STARTING OF THE APPLICATION, WE ARE MAKING WIFI DISABLE ONLY DURING SEARCHING PROCESS AND MAKES IT ENABLE IN ONDESTROY IF IT WAS PREVIOUSLY ENABLE...

        try {
            setup = new Setup();
            boolean activate = setup.blActivateLibrary(mContext, R.raw.licence);
            if (activate == true) {

            } else if (activate == false) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.mlvList = findViewById(R.id.actDiscovery_lv);
        this.mlvList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){

                String sMAC = ((TextView)arg1.findViewById(R.id.device_item_ble_mac)).getText().toString();
                Intent result = new Intent();
                result.putExtra("MAC", sMAC);
                result.putExtra("RSSI", mhtFDS.get(sMAC).get("RSSI"));
                result.putExtra("NAME", mhtFDS.get(sMAC).get("NAME"));
                result.putExtra("COD", mhtFDS.get(sMAC).get("COD"));
                result.putExtra("BOND", mhtFDS.get(sMAC).get("BOND"));
                result.putExtra("DEVICE_TYPE", toDeviceTypeString(mhtFDS.get(sMAC).get("DEVICE_TYPE")));
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

        new scanDeviceTask().execute("");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (mBT.isDiscovering())
            mBT.cancelDiscovery();

    }

    private void startSearch(){
        _discoveryFinished = false;

        if (null == mhtFDS)
            this.mhtFDS = new Hashtable<String, Hashtable<String, String>>();
        else
            this.mhtFDS.clear();

		*//* Register Receiver*//*
        IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(_finishedReceiver, discoveryFilter);
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(_foundReceiver, foundFilter);
        mBT.startDiscovery();//start scan

        //the first scan clear show list
    }

    private String toDeviceTypeString(String sDeviceTypeId){
        Pattern pt = Pattern.compile("^[-\\+]?[\\d]+$");
        if (pt.matcher(sDeviceTypeId).matches()){
            switch(Integer.valueOf(sDeviceTypeId)){
                case DEVICE_TYPE_BREDR:
                    return "device_type_bredr";
                case DEVICE_TYPE_BLE:
                    return "device_type_ble";
                case DEVICE_TYPE_DUMO:
                    return "device_type_dumo";
                default:
                    return "device_type_bredr";
            }
        }
        else
            return sDeviceTypeId;
    }

    *//* Show devices list *//*
    protected void showDevices(){
        if (null == this.malListItem)
            this.malListItem = new ArrayList<HashMap<String, Object>>();

        if (null == this.msaListItemAdapter){
            //Generate adapter Item and dynamic arrays corresponding element
            this.msaListItemAdapter = new SimpleAdapter(this,malListItem,//Data Sources
                    R.layout.list_view_item_devices,//ListItem's XML implementation
                    //Child corresponding dynamic arrays and ImageItem
                    new String[] {"NAME","MAC", "COD", "RSSI", "DEVICE_TYPE", "BOND"},
                    //A ImageView ImageItem XML file inside, two TextView ID
                    new int[] {R.id.device_item_ble_name,
                            R.id.device_item_ble_mac,
                            R.id.device_item_ble_cod,
                            R.id.device_item_ble_rssi,
                            R.id.device_item_ble_device_type,
                            R.id.device_item_ble_bond
                    }
            );
            this.mlvList.setAdapter(this.msaListItemAdapter);
        }


        this.malListItem.clear();//Clear history entries
        Enumeration<String> e = this.mhtFDS.keys();

        while (e.hasMoreElements()){
            HashMap<String, Object> map = new HashMap<String, Object>();
            String sKey = e.nextElement();
            map.put("MAC", sKey);
            map.put("NAME", this.mhtFDS.get(sKey).get("NAME"));
            map.put("RSSI", this.mhtFDS.get(sKey).get("RSSI"));
            map.put("COD", this.mhtFDS.get(sKey).get("COD"));
            map.put("BOND", this.mhtFDS.get(sKey).get("BOND"));
            map.put("DEVICE_TYPE", toDeviceTypeString(this.mhtFDS.get(sKey).get("DEVICE_TYPE")));
            this.malListItem.add(map);
        }
        this.msaListItemAdapter.notifyDataSetChanged();
    }

    private class scanDeviceTask extends AsyncTask<String, String, Integer> {
        *//**Constants: Bluetooth is not turned on*//*
        private static final int RET_BLUETOOTH_NOT_START = 0x0001;
        *//**Constant: the device search complete*//*
        private static final int RET_SCAN_DEVICE_FINISHED = 0x0002;
        *//**Wait a Bluetooth device starts the maximum time (in S)*//*
        private static final int miWATI_TIME = 10;
        *//**Every thread sleep time (in ms)*//*
        private static final int miSLEEP_TIME = 150;
        *//**Process waits prompt box*//*
        private ProgressDialog mpd = null;


        @Override
        public void onPreExecute(){
            this.mpd = new ProgressDialog(mContext);
            this.mpd.setMessage(getString(R.string.scanning));
            this.mpd.setCancelable(true);
            this.mpd.setCanceledOnTouchOutside(true);
            this.mpd.setOnCancelListener(new DialogInterface.OnCancelListener(){
                @Override
                public void onCancel(DialogInterface dialog){
                    _discoveryFinished = true;
                }
            });
            this.mpd.show();

            startSearch();
        }

        @Override
        protected Integer doInBackground(String... params){
            if (!mBT.isEnabled())
                return RET_BLUETOOTH_NOT_START;

            int iWait = miWATI_TIME * 3000;
            //Wait miSLEEP_TIME seconds, start the Bluetooth device before you start scanning
            while(iWait > 0){
                if (_discoveryFinished)
                    return RET_SCAN_DEVICE_FINISHED;
                else
                    iWait -= miSLEEP_TIME;
                SystemClock.sleep(miSLEEP_TIME);;
            }
            return RET_SCAN_DEVICE_FINISHED;
        }

        @Override
        public void onProgressUpdate(String... progress){
        }

        @Override
        public void onPostExecute(Integer result){

            if (this.mpd.isShowing())
                this.mpd.dismiss();

            if (mBT.isDiscovering())
                mBT.cancelDiscovery();

            if (RET_SCAN_DEVICE_FINISHED == result){

            }else if (RET_BLUETOOTH_NOT_START == result){
                Toast.makeText(mContext, "actDiscovery_msg_bluetooth_not_start", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan_but:
                new scanDeviceTask().execute("");
                //return true;
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }*/
}