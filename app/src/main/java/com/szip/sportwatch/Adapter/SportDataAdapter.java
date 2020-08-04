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
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            holder.unitTv = convertView.findViewById(R.id.unitTv);
            holder.calorieUnitTv = convertView.findViewById(R.id.calorieUnitTv);
            holder.calorieIv = convertView.findViewById(R.id.calorieIv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.timeTv.setText(DateUtil.getStringDateFromSecond(list.get(position).time,"MM/dd HH:mm:ss"));

        holder.sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",list.get(position).sportTime/3600,
                list.get(position).sportTime%3600/60,list.get(position).sportTime%3600%60));

        switch (list.get(position).type){
            case 1:{//走路
            }
                break;
            case 2://跑步
            case 5:
            case 6:
            case 7:
            case 3:{//室内跑步
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.distanceTv.setText(String.format(Locale.ENGLISH,"%.1f",list.get(position).distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(list.get(position).distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.calorieUnitTv.setVisibility(View.VISIBLE);
                holder.calorieIv.setVisibility(View.VISIBLE);
                holder.calorieTv.setVisibility(View.VISIBLE);
                holder.calorieTv.setText(list.get(position).calorie+"");
            }
                break;
            case 4:{//登山
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_mountain);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.distanceTv.setText(String.format(Locale.ENGLISH,"%.1f",list.get(position).distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(list.get(position).distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.calorieUnitTv.setVisibility(View.VISIBLE);
                holder.calorieIv.setVisibility(View.VISIBLE);
                holder.calorieTv.setVisibility(View.VISIBLE);
                holder.calorieTv.setText(list.get(position).calorie+"");
            }
                break;
            case 8:{//跳绳

            }
                break;
            case 9:{//羽毛球

            }
                break;
            case 10:{//篮球
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_basketball);
                holder.distanceTv.setText(list.get(position).calorie+"");
                holder.unitTv.setText("kcal");
                holder.calorieUnitTv.setVisibility(View.GONE);
                holder.calorieIv.setVisibility(View.GONE);
                holder.calorieTv.setVisibility(View.GONE);
            }
                break;
            case 11:{//骑行
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_bike);
                holder.distanceTv.setText(list.get(position).calorie+"");
                holder.unitTv.setText("kcal");
                holder.calorieUnitTv.setVisibility(View.GONE);
                holder.calorieIv.setVisibility(View.GONE);
                holder.calorieTv.setVisibility(View.GONE);
            }
                break;
            case 12:{//滑冰

            }
                break;
            case 13:{//健身房

            }
                break;
            case 14:{//瑜伽

            }
                break;
            case 15:{//网球

            }
                break;
            case 16:{//乒乓球
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_pingpang);
                holder.distanceTv.setText(list.get(position).calorie+"");
                holder.unitTv.setText("kcal");
                holder.calorieUnitTv.setVisibility(View.GONE);
                holder.calorieIv.setVisibility(View.GONE);
                holder.calorieTv.setVisibility(View.GONE);
            }
                break;
            case 17:{//足球
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_football);
                holder.distanceTv.setText(list.get(position).calorie+"");
                holder.unitTv.setText("kcal");
                holder.calorieUnitTv.setVisibility(View.GONE);
                holder.calorieIv.setVisibility(View.GONE);
                holder.calorieTv.setVisibility(View.GONE);
            }
                break;
            case 18:{//游泳

            }
                break;
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView timeTv,distanceTv,sportTimeTv,calorieTv,unitTv,calorieUnitTv;
        ImageView typeIv,calorieIv;
    }
}
