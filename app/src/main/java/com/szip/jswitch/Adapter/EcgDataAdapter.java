package com.szip.jswitch.Adapter;

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
 * Created by Administrator on 2019/12/24.
 */

public class EcgDataAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<DrawDataBean> list;

    public EcgDataAdapter(ArrayList<DrawDataBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setList(ArrayList<DrawDataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size()+1;
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
                    R.layout.adapter_ecg_list, null, false);
            holder = new ViewHolder();
            holder.timeTv = convertView.findViewById(R.id.timeTv);
            holder.dataTv = convertView.findViewById(R.id.dataTv);
            holder.itemLl = convertView.findViewById(R.id.itemLl);
            holder.itemLl1 = convertView.findViewById(R.id.itemLl1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == list.size()){
            holder.itemLl.setVisibility(View.VISIBLE);
            holder.itemLl1.setVisibility(View.GONE);
        }else {
            holder.itemLl.setVisibility(View.GONE);
            holder.itemLl1.setVisibility(View.VISIBLE);
            holder.dataTv.setText(list.get(position).getValue()+"");
            holder.timeTv.setText(DateUtil.getStringDateFromSecond(list.get(position).getTime(),"MM/dd HH:mm:ss"));
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView timeTv,dataTv;
        LinearLayout itemLl,itemLl1;
    }
}
