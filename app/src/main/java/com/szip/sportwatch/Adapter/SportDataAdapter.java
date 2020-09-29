package com.szip.sportwatch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;

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
            holder.dataTv = convertView.findViewById(R.id.dataTv);
            holder.buttonFirstTv = convertView.findViewById(R.id.buttonFirstTv);
            holder.buttonSecondTv = convertView.findViewById(R.id.buttonSecondTv);
            holder.typeIv = convertView.findViewById(R.id.typeIv);
            holder.unitTv = convertView.findViewById(R.id.unitTv);
            holder.buttonFirstUnitTv = convertView.findViewById(R.id.buttonFirstUnitTv);
            holder.buttonSecondUnitTv = convertView.findViewById(R.id.buttonSecondUnitTv);
            holder.buttonFirstIv = convertView.findViewById(R.id.buttonFirstIv);
            holder.buttonSecondIv = convertView.findViewById(R.id.buttonSecondIv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SportData sportData = list.get(position);

        holder.timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));

        holder.unitTv.setVisibility(View.VISIBLE);
        holder.buttonFirstUnitTv.setVisibility(View.VISIBLE);
        holder.buttonFirstIv.setVisibility(View.VISIBLE);
        holder.buttonFirstTv.setVisibility(View.VISIBLE);
        holder.buttonSecondTv.setVisibility(View.VISIBLE);
        holder.buttonSecondUnitTv.setVisibility(View.VISIBLE);
        holder.buttonSecondIv.setVisibility(View.VISIBLE);
        switch (sportData.type){
            case 1:{//徒步
                holder.typeIv.setImageResource(R.mipmap.sport_icon_run);
                holder.dataTv.setText(sportData.step+"");
                holder.unitTv.setText("steps");

                holder.buttonFirstIv.setImageResource(R.mipmap.steps_icon_distance);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.buttonFirstUnitTv.setText("m");
                } else{
                    holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.buttonFirstUnitTv.setText("Mi");
                }
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_kcal);
                holder.buttonSecondTv.setText(sportData.calorie+"");
                holder.buttonSecondUnitTv.setText("kcal");
            }
            break;
            case 2:{//跑步
                holder.typeIv.setImageResource(R.mipmap.sport_icon_outrun);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_speed);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_kcal);
                holder.buttonSecondTv.setText(sportData.calorie+"");
                holder.buttonSecondUnitTv.setText("kcal");
            }
            break;
            case 5:{//马拉松
                holder.typeIv.setImageResource(R.mipmap.sport_icon_marathon);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_speed);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_kcal);
                holder.buttonSecondTv.setText(sportData.calorie+"");
                holder.buttonSecondUnitTv.setText("kcal");
            }
            break;
            case 6:{
                holder.typeIv.setImageResource(R.mipmap.sport_icon_trainingrun);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_speed);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_kcal);
                holder.buttonSecondTv.setText(sportData.calorie+"");
                holder.buttonSecondUnitTv.setText("kcal");
            }
            break;
            case 7:
            case 3:{//室内跑步
                holder.typeIv.setImageResource(R.mipmap.sport_icon_treadmill);
                holder.dataTv.setText(sportData.step+"");
                holder.unitTv.setText("steps");
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_list_icon_kcal);
                holder.buttonFirstTv.setText(sportData.calorie+"");
                holder.buttonFirstUnitTv.setText("kcal");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 4:{//登山
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_mountain);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_steps);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d",sportData.step));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_icon_high);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.height/1f));
                    holder.buttonSecondUnitTv.setText("m");
                } else{
                    holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.height)));
                    holder.buttonSecondUnitTv.setText("Mi");
                }
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
                holder.dataTv.setText(sportData.calorie+"");
                holder.unitTv.setText("kcal");
                holder.buttonFirstUnitTv.setVisibility(View.GONE);
                holder.buttonFirstIv.setVisibility(View.GONE);
                holder.buttonFirstTv.setVisibility(View.GONE);
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 11:{//骑行
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_bike);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_speed);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 12:{//滑冰
                holder.typeIv.setImageResource(R.mipmap.sport_icon_skii);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_speed);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
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
                holder.dataTv.setText(sportData.calorie+"");
                holder.unitTv.setText("kcal");
                holder.buttonFirstUnitTv.setVisibility(View.GONE);
                holder.buttonFirstIv.setVisibility(View.GONE);
                holder.buttonFirstTv.setVisibility(View.GONE);
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 17:{//足球
                holder.typeIv.setImageResource(R.mipmap.sport_list_icon_type_football);
                holder.dataTv.setText(sportData.calorie+"");
                holder.unitTv.setText("kcal");
                holder.buttonFirstUnitTv.setVisibility(View.GONE);
                holder.buttonFirstIv.setVisibility(View.GONE);
                holder.buttonFirstTv.setVisibility(View.GONE);
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 18:{//游泳

            }
            break;
            case 19:{//攀岩
                holder.typeIv.setImageResource(R.mipmap.sport_icon_climb);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.height/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.height)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstUnitTv.setVisibility(View.GONE);
                holder.buttonFirstIv.setVisibility(View.GONE);
                holder.buttonFirstTv.setVisibility(View.GONE);
                holder.buttonSecondTv.setVisibility(View.GONE);
                holder.buttonSecondUnitTv.setVisibility(View.GONE);
                holder.buttonSecondIv.setVisibility(View.GONE);
            }
            break;
            case 20:{//划船
                holder.typeIv.setImageResource(R.mipmap.sport_icon_boat);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_speed);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d'",sportData.speed));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 21:{//高尔夫
                holder.typeIv.setImageResource(R.mipmap.sport_icon_golf);
                holder.dataTv.setText(String.format(Locale.ENGLISH,"%d",sportData.pole));
                holder.unitTv.setText("pole");
                holder.buttonFirstIv.setImageResource(R.mipmap.sport_icon_steps);
                holder.buttonFirstTv.setText(String.format(Locale.ENGLISH,"%02d",sportData.step));
                holder.buttonFirstUnitTv.setText("");
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
            case 22:{//冲浪
                holder.typeIv.setImageResource(R.mipmap.sport_icon_surfing);
                if (((MyApplication)mContext.getApplicationContext()).getUserInfo().getUnit().equals("metric")){
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.1f",sportData.distance/1f));
                    holder.unitTv.setText("m");
                } else{
                    holder.dataTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance)));
                    holder.unitTv.setText("Mi");
                }
                holder.buttonFirstUnitTv.setVisibility(View.GONE);
                holder.buttonFirstIv.setVisibility(View.GONE);
                holder.buttonFirstTv.setVisibility(View.GONE);
                holder.buttonSecondIv.setImageResource(R.mipmap.sport_list_icon_time);
                holder.buttonSecondTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                        sportData.sportTime%3600/60,sportData.sportTime%3600%60));
                holder.buttonSecondUnitTv.setText("");
            }
            break;
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView timeTv,dataTv, buttonSecondTv, buttonFirstTv,unitTv,buttonFirstUnitTv,buttonSecondUnitTv;
        ImageView typeIv, buttonFirstIv,buttonSecondIv;
    }
}
