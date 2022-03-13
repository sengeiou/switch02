package com.szip.jswitch.Activity.gpsSport;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.szip.jswitch.R;

public class GoogleMapFragment extends DialogFragment implements OnMapReadyCallback, LocationSource {
    private OnLocationChangedListener mListener;
    private TextView distanceTv,speedTv,calorieTv;
    private View mRootView;
    private MapView mapView;
    private Location location;
    private GoogleMap googleMap;
    private int speed;
    private float distance,calorie;

    private ImageView gpsIv;


    public GoogleMapFragment(int speed, float distance, float calorie, Location location) {
        this.speed = speed;
        this.distance = distance;
        this.calorie = calorie;
        this.location = location;
    }

    public void setData(int speed, float distance, float calorie,float acc){
        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
        if (acc>=29){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
        }else if (acc>=15){
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
        }else {
            gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
        }
    }

    public void setLocation(Location location){
        if (mListener!=null){
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng appointLoc = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(appointLoc));
            mListener.onLocationChanged(location);
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        }
        initView();
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

    private void initView() {
        speedTv = mRootView.findViewById(R.id.speedTv);
        distanceTv = mRootView.findViewById(R.id.distanceTv);
        calorieTv = mRootView.findViewById(R.id.calorieTv);

        gpsIv = mRootView.findViewById(R.id.gpsIv);
        speedTv.setText(String.format("%d'%d''",speed/60,speed%60));
        distanceTv.setText(String.format("%.2f",distance/1000));
        calorieTv.setText(String.format("%.1f",calorie));
        mapView = mRootView.findViewById(R.id.googleMap);
        mRootView.findViewById(R.id.gaodeMap).setVisibility(View.GONE);
        mRootView.findViewById(R.id.backIv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (location!=null){
            float acc = location.getAccuracy();
            if (acc>=29){
                gpsIv.setImageResource(R.mipmap.sport_icon_gps_1);
            }else if (acc>=15){
                gpsIv.setImageResource(R.mipmap.sport_icon_gps_2);
            }else {
                gpsIv.setImageResource(R.mipmap.sport_icon_gps_3);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onCreate(getArguments());
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS != errorCode) {
            GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), 0).show();
        } else {
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView!=null){
            Log.d("LOCATION******","onPause");
            mapView.onDestroy();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setUpMap();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

    /**
     * 设置一些amap的属性
     */
    @SuppressLint("MissingPermission")
    private void setUpMap() {
        // 自定义系统定位小蓝点
        googleMap.setLocationSource(this);// 设置定位监听
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(19f));
        if (mListener!=null&&location!=null){
            // 移动地图到指定经度的位置
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng appointLoc = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(appointLoc));
            mListener.onLocationChanged(location);
        }

    }
}
