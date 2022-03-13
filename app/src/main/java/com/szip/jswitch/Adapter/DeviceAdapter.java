package com.szip.jswitch.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.jswitch.Model.DrawDataBean;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/12/27.
 */

public class DeviceAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<BluetoothDevice> list = new ArrayList<>();

    public DeviceAdapter( Context mContext) {
        this.mContext = mContext;
    }

    public BluetoothDevice getDevice(int pos){
        return list.get(pos);
    }

    public void addDevice(BluetoothDevice device){
        if (!list.contains(device)) {
            list.add(device);
        }
        notifyDataSetChanged();
    }

    public void clearList(){
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_device, null, false);
            holder = new ViewHolder();
            holder.nameTv = convertView.findViewById(R.id.deviceNameTv);
            holder.addressTv = convertView.findViewById(R.id.deviceAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (list.get(position).getName()!=null)
            holder.nameTv.setText(list.get(position).getName());
        if (list.get(position).getAddress()!=null)
            holder.addressTv.setText(list.get(position).getAddress());

        return convertView;
    }

    private static class ViewHolder {
        TextView nameTv,addressTv;
    }

}
