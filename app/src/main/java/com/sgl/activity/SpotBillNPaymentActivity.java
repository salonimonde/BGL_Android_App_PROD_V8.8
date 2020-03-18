package com.sgl.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sgl.R;
import com.sgl.configuration.AppConstants;
import com.sgl.utils.AppPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SpotBillNPaymentActivity extends ParentActivity implements View.OnClickListener {

    private Context mContext;
    private Button btnPrint;
    private ImageView imgBack;

    // android built in classes for bluetooth operations
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;

    // needed for communication to bluetooth device / network
    private InputStream mmInputStream;
    private Thread workerThread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private volatile boolean stopWorker;

    private byte[] format = {0, 32, 0};

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>();

    private boolean fromAdapter = false, isException = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_bill_n_payment);

        mContext = this;

        btnPrint = findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(this);

        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            finish();
        } else if (v == btnPrint) {
            printFunction();
        }
    }

    private void printFunction() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, getString(R.string.bluetooth_feature_not_supported), Toast.LENGTH_SHORT).show();
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                if (mmSocket != null) {
                    if (mmSocket.isConnected())
                        printingReceipt();
                    else
                        mBluetoothAdapter.startDiscovery();
                } else
                    mBluetoothAdapter.startDiscovery();
            } else {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1000);
            }
        }
    }

    private void printingReceipt() {
        try {
            boolean isBTConnected = connectToBT();
            if (isBTConnected) {
                dismissLoadingDialog();

                showLoadingDialog(getString(R.string.printing));

                final OutputStream mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();

                try {
                    String print = "";
                    print = getString(R.string.printing_matter);

                    mmOutputStream.write(format);
                    mmOutputStream.write(print.getBytes(), 0, print.getBytes().length);
                    AppPreferences.getInstance(mContext).putString(AppConstants.PAIRED_BLUETOOTH_DEVICE, "" + mmDevice);
                    dismissDialog();
                    mmSocket.close();
                } catch (Exception e) {
                    dismissDialog();
                    e.printStackTrace();
                }
            } else {
                if (isException) {
                    Toast.makeText(mContext, getString(R.string.error_while_connecting_device), Toast.LENGTH_SHORT).show();
                    unPairDevice(mmDevice);
                    AppPreferences.getInstance(mContext).putString(AppConstants.PAIRED_BLUETOOTH_DEVICE, "");
                } else
                    printFunction();
            }
        } catch (Exception e) {
            dismissLoadingDialog();
            Toast.makeText(mContext, getString(R.string.please_select_bluetooth_printer_to_print), Toast.LENGTH_LONG).show();
            dismissDialog();
        }
    }

    private void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    try {
                        showToast("Bluetooth Enabled");
                        mBluetoothAdapter.startDiscovery();
                    } catch (Exception e) {
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<>();
                showLoadingDialog(getString(R.string.scanning));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                dismissLoadingDialog();
                boolean isPairedFound = false;
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    String pairedDevice = AppPreferences.getInstance(mContext).getString(AppConstants.PAIRED_BLUETOOTH_DEVICE, "");
                    if (pairedDevice != null && pairedDevice.length() > 0) {
                        for (BluetoothDevice bt : pairedDevices) {
                            if (bt.getAddress().equals(pairedDevice)) {
                                for (BluetoothDevice bluetoothDevice : mDeviceList) {
                                    if (bluetoothDevice.getAddress().equals(pairedDevice)) {
                                        mmDevice = bt;
                                        isPairedFound = true;
                                        printingReceipt();
                                        break;
                                    }
                                }
                            }
                        }
                        if (!isPairedFound) {
                            goToDeviceListActivity();
                        }
                    } else {
                        goToDeviceListActivity();
                    }
                } else {
                    goToDeviceListActivity();
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDeviceList.contains(device)) {
                    mDeviceList.add(device);
                    showToast("Found device " + device.getName());
                }
            }
        }
    };

    private void goToDeviceListActivity() {
        Intent newIntent = new Intent(mContext, DeviceListActivity.class);
        newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
        startActivityForResult(newIntent, 100);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String connectionStatus = AppPreferences.getInstance(this).getString(AppConstants.CHECK_BT_CONNECTION, "");
        if (connectionStatus.equalsIgnoreCase("true")) {
            mmDevice = data.getParcelableExtra(AppConstants.BT_DEVICE);
            printingReceipt();
        } else if (connectionStatus.equalsIgnoreCase("false")) {
            Toast.makeText(mContext, getString(R.string.please_select_bluetooth_printer_to_print), Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissDialog() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                try {
                    dismissLoadingDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    private boolean connectToBT() {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

            if (mmSocket.isConnected())
                mmSocket.close();
            try {
                mmSocket.connect();
                return true;
            } catch (IOException e) {
                try {
                    mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                    ;
                    mmSocket.connect();
                    return true;
                } catch (Exception e1) {
                    e1.printStackTrace();
                    isException = true;
                    mmSocket.close();
                    AppPreferences.getInstance(mContext).putString(AppConstants.PAIRED_BLUETOOTH_DEVICE, "");
                    unPairDevice(mmDevice);
                    return false;
                }
            }
        } catch (Exception E) {
            return false;
        }
    }

    private void unPairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
