package com.szip.jswitch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.jswitch.Model.DrawDataBean;
import com.szip.jswitch.MyApplication;
import com.szip.jswitch.R;
import com.szip.jswitch.Util.DateUtil;
import com.szip.jswitch.Util.MathUitl;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Administrator on 2019/12/22.
 */

public class BloodAdapter extends BaseAdapter{

    private ArrayList<DrawDataBean> drawDataBeans;
    private Context mContext;
    private int type;
    public BloodAdapter(ArrayList<DrawDataBean> drawDataBeans,int type, Context mContext) {
        this.drawDataBeans = drawDataBeans;
        this.type = type;
        this.mContext = mContext;
    }

    public void setDrawDataBeans(ArrayList<DrawDataBean> drawDataBeans) {
        this.drawDataBeans = drawDataBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return drawDataBeans.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return drawDataBeans.get(position);
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
                    R.layout.adapter_blood, null, false);
            holder = new ViewHolder();
            holder.timeTv = convertView.findViewById(R.id.timeTv);
            holder.dataTv = convertView.findViewById(R.id.dataTv);
            holder.unitTv = convertView.findViewById(R.id.unitTv);
            holder.itemLl = convertView.findViewById(R.id.itemLl);
            holder.itemLl1 = convertView.findViewById(R.id.itemLl1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == drawDataBeans.size()){
            holder.itemLl.setVisibility(View.VISIBLE);
            holder.itemLl1.setVisibility(View.GONE);
        }else {
            holder.itemLl.setVisibility(View.GONE);
            holder.itemLl1.setVisibility(View.VISIBLE);
            holder.timeTv.setText(DateUtil.getStringDateFromSecond(drawDataBeans.get(position).getTime(),"HH:mm"));
            if (type == 0){//血压
                holder.dataTv.setText(String.format(Locale.ENGLISH,"%d/%d", drawDataBeans.get(position).getValue()+45,
                        drawDataBeans.get(position).getValue1()+45));
                holder.unitTv.setText("mmHg");
            }else if (type == 1){//血氧
                holder.dataTv.setText(drawDataBeans.get(position).getValue()+70+"%");
                holder.unitTv.setText("SaO2");
            }else {
                if (MyApplication.getInstance().getUserInfo().getTempUnit()==0){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",(drawDataBeans.get(position).getValue()+340)/10f));
                    holder.unitTv.setText("℃");
                }else {
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f", MathUitl.c2f((drawDataBeans.get(position).getValue()+340)/10f)));
                    holder.unitTv.setText("℉");
                }

            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView timeTv,dataTv,unitTv;
        LinearLayout itemLl,itemLl1;
    }
}
