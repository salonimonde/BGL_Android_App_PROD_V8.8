package com.sgl.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sgl.R;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    private OnPairButtonClickListener mListener;

    public DeviceListAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mListener = (OnPairButtonClickListener) context;
    }

    public void setData(List<BluetoothDevice> data)
    {
        mData = data;
    }

    public int getCount()
    {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView	=  mInflater.inflate(R.layout.cell_bt_item, null);

            holder = new ViewHolder();
            holder.nameTv = convertView.findViewById(R.id.txt_name);
            holder.addressTv = convertView.findViewById(R.id.txt_address);
            holder.pairBtn = convertView.findViewById(R.id.btn_pair);
            holder.linearLayoutClick = convertView.findViewById(R.id.linear_click);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device	= mData.get(position);

        holder.nameTv.setText(device.getName());
        holder.addressTv.setText(device.getAddress());
        holder.linearLayoutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                {
                    mListener.onPairButtonClick(position);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder
    {
        LinearLayout linearLayoutClick;
        TextView nameTv;
        TextView addressTv;
        Button pairBtn;
    }

    public interface OnPairButtonClickListener
    {
        void onPairButtonClick(int position);
    }
}
