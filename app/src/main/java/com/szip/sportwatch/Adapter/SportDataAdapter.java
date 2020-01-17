package com.szip.sportwatch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.Model.DrawDataBean;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class SportDataAdapter extends BaseAdapter {

    private Context mContext;
    private List<SportData> list;


    public SportDataAdapter(List<SportData> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    public void setList(List<SportData> list) {
        this.list = list;
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
                    R.layout.adapter_sport_list, null, false);
            holder = new ViewHolder();
            holder.timeTv = convertView.findViewById(R.id.timeTv);
            holder.distanceTv = convertView.findViewById(R.id.distanceTv);
            holder.sportTimeTv = convertView.findViewById(R.id.sportTimeTv);
            holder.calorieTv = convertView.findViewById(R.id.calorieTv);
            holder.typeIv = convertView.findViewById(R.id.typeIv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.timeTv.setText(DateUtil.getStringDateFromSecond(list.get(position).time,"MM/dd HH:mm:ss"));
        holder.distanceTv.setText(String.format("%.2f",list.get(position).distance/1000f));
        holder.sportTimeTv.setText(String.format("%02d:%02d:%02d",list.get(position).sportTime/3600,
                list.get(position).sportTime%3600/60,list.get(position).sportTime%3600%60));
        holder.calorieTv.setText(list.get(position).calorie+"");
        if (list.get(position).type==2)
        holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type);


        return convertView;
    }

    private static class ViewHolder {
        TextView timeTv,distanceTv,sportTimeTv,calorieTv;
        ImageView typeIv;
    }
}
