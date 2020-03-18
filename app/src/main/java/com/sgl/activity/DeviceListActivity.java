package com.sgl.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.sgl.R;
import com.sgl.adapters.DeviceListAdapter;
import com.sgl.configuration.AppConstants;
import com.sgl.utils.AppPreferences;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

public class DeviceListActivity extends Activity implements DeviceListAdapter.OnPairButtonClickListener
{

    private Context mContext;
    private ListView mListView;
    private DeviceListAdapter mAdapter;
    private BluetoothDevice device;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        mContext = this;

        mListView = findViewById(R.id.lv_paired);

        ArrayList<BluetoothDevice> mDeviceList	= getIntent().getExtras().getParcelableArrayList("device.list");
        HashSet<BluetoothDevice> uniqueDevices = new HashSet<>(mDeviceList);
        bluetoothDevices = new ArrayList<>();
        for (BluetoothDevice value : uniqueDevices) {
            bluetoothDevices.add(value);
        }

        mAdapter = new DeviceListAdapter(mContext);
        mAdapter.setData(bluetoothDevices);
        mListView.setAdapter(mAdapter);

        registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(mPairReceiver);
        super.onDestroy();
    }

    private void showToast(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void unPairDevice(BluetoothDevice device)
    {
        try
        {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {e.printStackTrace();}
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING)
                {
//                    dismissLoadingDialog();
                    sendResultBack();
                }
                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED)
                {
                    showToast(getString(R.string.unpaired));
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void sendResultBack()
    {
        AppPreferences.getInstance(this).putString(AppConstants.CHECK_BT_CONNECTION, "true");
        Intent intent = new Intent();
        intent.putExtra(AppConstants.BT_DEVICE, device);
        setResult(AppConstants.BT_DEVICE_DATA, intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        AppPreferences.getInstance(this).putString(AppConstants.CHECK_BT_CONNECTION, "false");
        finish();
    }

    @Override
    public void onPairButtonClick(int position)
    {
        device = bluetoothDevices.get(position);
        sendResultBack();
    }
}