package com.szip.sportwatch.Activity.GpsSport;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.szip.sportwatch.R;

public class MapFragment extends DialogFragment implements LocationSource,AMapLocationListener {

    private TextView distanceTv,speedTv,calorieTv;
    private View mRootView;
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private int speed;
    private float distance,calorie;

    public MapFragment(int speed, float distance, float calorie) {
        this.speed = speed;
        this.distance = distance;
        this.calorie = calorie;
    }

    public void setData(int speed, float distance, float calorie){
        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        }
        initView(savedInstanceState);

        return mRootView;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();

        if(window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setWindowAnimations(R.style.MapAnim);
        }
        return dialog;
    }

    private void initView(Bundle savedInstanceState) {
        speedTv = mRootView.findViewById(R.id.speedTv);
        distanceTv = mRootView.findViewById(R.id.distanceTv);
        calorieTv = mRootView.findViewById(R.id.calorieTv);


        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
        mapView = mRootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mRootView.findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView!=null)
            mapView.onDestroy();
        deactivate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView!=null)
            mapView.onResume();
    }

    private boolean firstLoad = true;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("LOCATION******",aMapLocation.toStr());
        if (aMapLocation != null) {
            if(firstLoad){
				/*aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
						new LatLng(aLocation.getLatitude(),aLocation.getLongitude()), 18, 0, 30)), 1000, null);*/
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()), 18, 0, 0)), 1000, new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        firstLoad = false;
                    }
                    @Override
                    public void onCancel() {

                    }
                });
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.mipmap.mylocation_icon));// 设置小蓝点的图标
//        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.showMyLocation(true);
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }
}
