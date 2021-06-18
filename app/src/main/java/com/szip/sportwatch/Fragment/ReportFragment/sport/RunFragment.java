package com.szip.sportwatch.Fragment.ReportFragment.sport;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.szip.sportwatch.Fragment.BaseFragment;
import com.szip.sportwatch.DB.dbModel.SportData;
import com.szip.sportwatch.MyApplication;
import com.szip.sportwatch.R;
import com.szip.sportwatch.Util.DateUtil;
import com.szip.sportwatch.Util.MathUitl;
import com.szip.sportwatch.View.MyScrollView;
import com.szip.sportwatch.View.SportReportView;
import com.szip.sportwatch.View.SportSpeedView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RunFragment extends BaseFragment implements OnMapReadyCallback {

    private MyScrollView myScrollView;
    private RelativeLayout bgRl;
    private MapView mapView;
    private com.google.android.gms.maps.MapView googleMapView;
    private GoogleMap googleMap;
    private View mapBackView;

    private TextView dataTv,timeTv,distanceTv,unitTv,kcalTv,sportTimeTv,averageTv1, averageTv2,averageTv3,averageTv4,averageTv5;
    private SportReportView tableView1, tableView2,tableView3, tableView4;
    private SportData sportData;
    private SportSpeedView sportSpeed;

    private String[] heartArray = new String[0];
    private String[] strideArray = new String[0];
    private String[] speedArray = new String[0];
    private String[] speedPerHourArray = new String[0];
    private String[] altitudeArray = new String[0];

    public RunFragment(SportData sportData) {
        this.sportData = sportData;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_run;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        initView(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView!=null)
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView!=null)
        mapView.onDestroy();
    }

    private void initView(Bundle savedInstanceState) {
        myScrollView = getView().findViewById(R.id.scrollId);
        bgRl = getView().findViewById(R.id.bgRl);
        googleMapView = getView().findViewById(R.id.googleMap);
        mapView = getView().findViewById(R.id.gaodeMap);
        mapBackView = getView().findViewById(R.id.mapBackView);
        mapBackView.getBackground().setAlpha(0);

        timeTv = getView().findViewById(R.id.timeTv);
        sportTimeTv = getView().findViewById(R.id.sportTimeTv);
        distanceTv = getView().findViewById(R.id.distanceTv);
        unitTv = getView().findViewById(R.id.unitTv);
        kcalTv = getView().findViewById(R.id.kcalTv);
        averageTv1 = getView().findViewById(R.id.averageTv1);
        averageTv2 = getView().findViewById(R.id.averageTv2);
        averageTv3 = getView().findViewById(R.id.averageTv3);
        averageTv4 = getView().findViewById(R.id.averageTv4);
        averageTv5 = getView().findViewById(R.id.averageTv5);
        tableView1 = getView().findViewById(R.id.tableView1);
        tableView2 = getView().findViewById(R.id.tableView2);
        tableView3 = getView().findViewById(R.id.tableView3);
        tableView4 = getView().findViewById(R.id.tableView4);
        sportSpeed = getView().findViewById(R.id.sportSpeed);
        dataTv = getView().findViewById(R.id.dataTv);


        if(sportData.type==6){
            ((TextView)getView().findViewById(R.id.sportIdTv)).setText(R.string.training);
            bgRl.setBackground(getView().getResources().getDrawable(R.drawable.sport_bg_purple));
            ((ImageView)getView().findViewById(R.id.bgIv)).setImageResource(R.mipmap.sport_bg_trainrun);
        }
        if (sportData.latArray!=null&&!sportData.latArray.equals("")){
            if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
                mapView.onCreate(savedInstanceState);
                googleMapView.setVisibility(View.GONE);
                makeLine();
            } else {
                googleMapView.onCreate(getArguments());
                googleMapView.onResume();
                try {
                    MapsInitializer.initialize(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
                if (ConnectionResult.SUCCESS != errorCode) {
                    GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), 0).show();
                } else {
                    googleMapView.getMapAsync(this);
                }
                mapView.setVisibility(View.GONE);
            }
            myScrollView.setOnScrollListener(listener);
        }else {
            getView().findViewById(R.id.mapViewtop).setVisibility(View.GONE);
        }
    }

    private void initData() {
        heartArray = sportData.getHeartArray().split(",");
        strideArray = sportData.getStrideArray().split(",");
        speedArray = sportData.getSpeedArray().split(",");
        speedPerHourArray = sportData.getSpeedPerHourArray().split(",");
        altitudeArray = sportData.getAltitudeArray().split(",");
        dataTv.setText(sportData.step+"");
        timeTv.setText(DateUtil.getStringDateFromSecond(sportData.time,"MM/dd HH:mm:ss"));
        sportTimeTv.setText(String.format(Locale.ENGLISH,"%02d:%02d:%02d",sportData.sportTime/3600,
                sportData.sportTime%3600/60,sportData.sportTime%3600%60));
        if (MyApplication.getInstance().getUserInfo().getUnit()==0){
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f",sportData.distance/1000f));
            unitTv.setText("km");
        } else{
            distanceTv.setText(String.format(Locale.ENGLISH,"%.2f", MathUitl.metric2Miles(sportData.distance*10)));
            unitTv.setText("mile");
        }
        kcalTv.setText(String.format(Locale.ENGLISH,"%.1f", sportData.calorie/1000f));
        averageTv1.setText(sportData.heart+"");
        averageTv2.setText(sportData.stride+"");
        averageTv3.setText(String.format(Locale.ENGLISH,"%.1f",sportData.speedPerHour/10f));
        averageTv4.setText(sportData.height+"");
        averageTv5.setText(String.format(Locale.ENGLISH,"%02d'%02d''",sportData.speed/60,sportData.speed%60));
        tableView1.addData(heartArray);
        tableView2.addData(strideArray);
        tableView3.addData(speedPerHourArray);
        tableView4.addData(altitudeArray);
        sportSpeed.addData(speedArray);

        if (sportData.getHeartArray().equals("")){
            getView().findViewById(R.id.heartLl).setVisibility(View.GONE);
        }
        if (sportData.getAltitudeArray().equals("")){
            getView().findViewById(R.id.altitudeLl).setVisibility(View.GONE);
        }
        if (sportData.getSpeedPerHourArray().equals("")){
            getView().findViewById(R.id.speedPerHourLl).setVisibility(View.GONE);
        }
    }

    private void makeLine(){
        if (getResources().getConfiguration().locale.getCountry().equals("CN")){
            AMap aMap = mapView.getMap();
            List<LatLng> latLngs = new ArrayList<LatLng>();
            String[] lats = sportData.latArray.split(",");
            String[] lngs = sportData.lngArray.split(",");
            double [] option = MathUitl.getMapOption(lats,lngs);

            LatLng centerBJPoint= new LatLng(option[0],
                    option[1]);
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(centerBJPoint,
                    (float) option[2], 0, 0)));
            for (int a = 1;a<lats.length;a++){
                latLngs.add(new LatLng((Integer.valueOf(lats[0])+Integer.valueOf(lats[a]))/1000000.0,
                        (Integer.valueOf(lngs[0])+Integer.valueOf(lngs[a]))/1000000.0));
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLngs.get(0));
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.sport_icon_gps_start));
            aMap.addMarker(markerOptions);
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(latLngs.get(latLngs.size()-1));
            markerOptions1.icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.sport_icon_gps_end));
            aMap.addMarker(markerOptions1);
            aMap.addPolyline(new PolylineOptions().
                    addAll(latLngs).
                    width(14).
                    useGradient(true).
                    color(Color.parseColor("#1BC416")));
        }else {
            List<com.google.android.gms.maps.model.LatLng> latLngs = new ArrayList<com.google.android.gms.maps.model.LatLng>();
            String[] lats = sportData.latArray.split(",");
            String[] lngs = sportData.lngArray.split(",");
            double [] option = MathUitl.getMapOption(lats,lngs);

            googleMap.getUiSettings().setZoomControlsEnabled(false);// 隐藏缩放按钮
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.zoomTo((float) option[2]));
            com.google.android.gms.maps.model.LatLng centerBJPoint= new com.google.android.gms.maps.model.LatLng(option[0],
                    option[1]);
            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLng(centerBJPoint));
            for (int a = 1;a<lats.length;a++){
                latLngs.add(new com.google.android.gms.maps.model.LatLng((Integer.valueOf(lats[0])+Integer.valueOf(lats[a]))/1000000.0,
                        (Integer.valueOf(lngs[0])+Integer.valueOf(lngs[a]))/1000000.0));
            }
            com.google.android.gms.maps.model.MarkerOptions markerOptions = new com.google.android.gms.maps.model.MarkerOptions();
            markerOptions.position(latLngs.get(0));
            markerOptions.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                    .fromResource(R.mipmap.sport_icon_gps_start));
            googleMap.addMarker(markerOptions);
            com.google.android.gms.maps.model.MarkerOptions markerOptions1 = new com.google.android.gms.maps.model.MarkerOptions();
            markerOptions1.position(latLngs.get(latLngs.size()-1));
            markerOptions1.icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                    .fromResource(R.mipmap.sport_icon_gps_end));
            googleMap.addMarker(markerOptions1);
            googleMap.addPolyline(new com.google.android.gms.maps.model.PolylineOptions().
                    addAll(latLngs).
                    width(14).
                    color(Color.parseColor("#1BC416")));
        }
    }

    private MyScrollView.OnScrollListener listener = new MyScrollView.OnScrollListener() {
        @Override
        public void onScroll(int scrollY) {
            int alpha = 0;
            alpha = scrollY/(bgRl.getTop()/255);
            if (alpha>255)
                alpha = 255;
            mapBackView.getBackground().setAlpha(alpha);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        makeLine();
    }
}
