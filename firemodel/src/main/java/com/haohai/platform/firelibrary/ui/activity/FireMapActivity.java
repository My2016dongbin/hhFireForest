package com.haohai.platform.firelibrary.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.utils.CommonUtil;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;

import java.util.List;

public class FireMapActivity extends HhBaseActivity {
    private static final String TAG = FireMapActivity.class.getSimpleName();

    /**
     * 当前地点击点
     */
    private LatLng currentPt;

    private String touchType;

    /**
     * 用于显示地图状态的面板
     */
    private TextView mStateBar;
    private TextView mStateBar2;
    private Button animateStatus;

    private LatLng center;
    //地理编码
    private GeocodeSearch geocoderSearch;
    private String cityAddress;
    private String state;
    private String latitude;
    private String longitude;
    private double longitude_double;
    private double latitude_double;
    private ActionBar mActionBar;
    private String city;

    public static final int MAP_REUEST_CODE = 2;
    public static final double LATITUDE_DEF = 0.00;//默认天安数码城: latitude: 36.32087806111286, longitude: 120.44349123197962
    public static final double LONGTITUDE_DEF = 0.00;//默认天安数码城: latitude: 36.32087806111286, longitude: 120.44349123197962

    private com.amap.api.maps.MapView aMapView;
    private com.amap.api.maps.AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        setContentView(R.layout.activity_fire_map);
        //ActionBar
        mActionBar = (ActionBar) findViewById(R.id.map_actionbar);
        mActionBar.setTitle("选择地图定位");
        mActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch (var1) {
                    case -1:
                        onBackPressed();
                        break;
                }
            }
        });
        //获取Intent传递的值
        Intent intent_g = getIntent();
        //默认天安数码城: latitude: 36.32087806111286, longitude: 120.44349123197962
        longitude_double = intent_g.getDoubleExtra("longitude_double", LONGTITUDE_DEF);
        latitude_double = intent_g.getDoubleExtra("latitude_double", LATITUDE_DEF);
        double[] doubles = LatLngChangeNew.calBD09toGCJ02(latitude_double, longitude_double);
        latitude_double = doubles[0];
        longitude_double = doubles[1];

        if (longitude_double == LONGTITUDE_DEF && latitude_double == LATITUDE_DEF) {
            Toast.makeText(FireMapActivity.this, "请检查授予定位权限并开启定位!", Toast.LENGTH_SHORT).show();
        }
        //       Log.e(TAG, "registerclick333: " + "longitude_double" + longitude_double + "latitude_double" + latitude_double);
        //39.86017837104533   116.45288578361887
        Log.e(TAG, "call: longitude_double" +longitude_double );
        Log.e(TAG, "call: latitude_double" + latitude_double);

        ///TODO 高德地图
        try {
            com.amap.api.services.core.ServiceSettings.updatePrivacyShow(this, true, true);
            com.amap.api.services.core.ServiceSettings.updatePrivacyAgree(this, true);
        } catch (Throwable ignore) {}
        aMapView = (com.amap.api.maps.MapView) findViewById(R.id.aMapView);
        aMapView.onCreate(savedInstanceState);
        aMap = aMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.amap.api.maps.model.LatLng(latitude_double,longitude_double),16));
        mStateBar = (TextView) findViewById(R.id.state);
        mStateBar2 = (TextView) findViewById(R.id.state2);


        try {
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
                    if (rCode == 1000) {
                        if (regeocodeResult != null
                                && regeocodeResult.getRegeocodeAddress() != null) {
                            cityAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                            city = regeocodeResult.getRegeocodeAddress().getCity();
                            latitude = CommonUtil.parsePointSplit(String.valueOf(currentPt.latitude),6);
                            longitude = CommonUtil.parsePointSplit(String.valueOf(currentPt.longitude),6);

                            Log.e(TAG, "逆地理编码成功: " + cityAddress);
                            mStateBar.setText(cityAddress);
                            mStateBar2.setText("(" + longitude + " , " + latitude + ")");
                        } else {
                            Log.e(TAG, "逆地理编码结果为空");
                        }
                    } else {
                        Log.e(TAG, "逆地理编码失败，错误码: " + rCode);
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    Log.e(TAG, "onGeocodeSearched" + geocodeResult);
                }
            });
        } catch (AMapException e) {
            e.printStackTrace();
        }


        initListener();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        aMapView.onSaveInstanceState(outState);
    }

    /**
     * 对地图事件的消息响应
     */
    private void initListener() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                currentPt = latLng;
                aMap.clear();
                com.amap.api.maps.model.BitmapDescriptor btm = com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(R.drawable.ic_tomap);
                aMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(btm)
                );

                //发起地理编码检索；
                try {
                    Log.e("发起地理编码检索","发起地理编码检索"+currentPt.latitude + " , " + currentPt.longitude);
                    RegeocodeQuery regeocodeQuery = new RegeocodeQuery(new LatLonPoint(currentPt.latitude, currentPt.longitude), 200, GeocodeSearch.AMAP);
                    geocoderSearch.getFromLocationAsyn(regeocodeQuery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void mapclick(View view) {
        int i = view.getId();
        if (i == R.id.tv_map) {
            if (null == longitude || null == latitude) {
                Toast.makeText(FireMapActivity.this, "请在地图上选择火点位置", Toast.LENGTH_SHORT).show();
            } else {
                if (city == "" || city == null) {
                    Toast.makeText(this, "请重新选择", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("cityAddress", cityAddress);
                    intent.putExtra("city", city);
                    FireMapActivity.this.setResult(MAP_REUEST_CODE, intent);
                    finish();
                }

            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        aMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        aMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        aMapView.onPause();
    }
}
