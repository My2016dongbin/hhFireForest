package com.haohai.platform.platformmodel.ui.acticity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.utils.LatLngChange;
//import com.haohai.platform.mapmodel.fragment.MapFragment;
import com.haohai.platform.mapmodel.multitype.OneBodyFire;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.acticity.base.HhBaseActivity;
import com.haohai.platform.platformmodel.ui.model.CommandModel;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import rx.functions.Action1;

import static com.haohai.platform.mapmodel.fragment.MapNewFragment.ONE_BODY;

public class CommandActivity extends HhBaseActivity implements INaviInfoCallback {
    ImageView iv_back;
    TextView tv_find;
    private MapView baiduMapView;
    private BaiduMap mBaiduMap;
    private DYLoadingView dy3;
    private List<OneBodyFire> oneBodyFireAllList;
    private List<OneBodyFire> oneBodyFireList;
    private List<OverlayOptions> optionsAllList;
    private Dialog openDialog;
    private View openInflater;
    private Dialog guideDialog;
    private View guideInflater;
    private TextView tv_title_open;
    private ImageView cha_open;
    private LinearLayout ll_1;
    private LinearLayout ll_2;
    private LinearLayout ll_3;
    private LinearLayout ll_4;
    private LinearLayout ll_5;
    private LinearLayout ll_6;
    private LinearLayout ll_clean;
    private TextView tv_qidian;
    private TextView tv_zhongdian;
    private TextView tv_car;
    private TextView tv_walk;
    private ImageView cha_guide;
    private TextView tv_start;
    private TextView tv_end;
    private FrameLayout fl_start;
    private FrameLayout fl_end;
    private TextView tv_guide;
    private int guideType = 0;
    private LatLng startPoint;
    private LatLng endPoint;
    private LatLng currentPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        init();
        getOnebodyDataFromService();
    }

    private void commandTask() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/taskManagement/saveTask");
        CommandModel commandModel = new CommandModel();
        commandModel.setPriority("1");
        commandModel.setTaskContent("内容");
        commandModel.setTaskStartTime("2022-05-07 08:30:00");
        commandModel.setTaskEndTime("2022-05-07 17:30:00");
        commandModel.setTaskImg("");
        commandModel.setTaskType("");
        commandModel.setPosition(new CommandModel.Position(36.123456,120.123456));
        params.setBodyContent(new Gson().toJson(commandModel));
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        Log.e("TAG", "commandTask: token = " + new DbConfig(this).getUser().getToken());
        Log.e("TAG", "commandTask: commandModel = " + new Gson().toJson(commandModel) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: " + result );
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: " + ex.getMessage() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private Marker endMarker;
    private Marker startMarker;
    private void init() {
        iv_back = findViewById(R.id.iv_back);
        tv_find = findViewById(R.id.tv_find);
        baiduMapView = findViewById(R.id.baidu_mapview);
        dy3 = findViewById(R.id.dy3);
        initDateTime();
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                finish();
            }
        });
        RxViewAction.clickNoDouble(tv_find).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                openDialog.show();
            }
        });
        baiduMapView.showZoomControls(false);
        mBaiduMap = baiduMapView.getMap();
        //显示卫星图层
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration myLocationConfiguration =
                new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        mBaiduMap.getUiSettings().setCompassEnabled(false);
        //只显示道路 不显示其他标注
        mBaiduMap.showMapPoi(true);
        //设置最大最小缩放等级
        mBaiduMap.setMaxAndMinZoomLevel(17, 5);
        flyBaiduMap(CommonData.lat,CommonData.lng);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(beforeAdding){
                    if(currentAddIndex==7){
                        if(endMarker==null){
                            MarkerOptions marker = new MarkerOptions()
                                    .icon(currentAddingBitmap).
                                            position(latLng);
                            endMarker = (Marker) mBaiduMap.addOverlay(marker);
                        }
                        endMarker.setPosition(latLng);
                        beforeAdding = false;
                        Toast.makeText(CommandActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        endPoint = latLng;
                        String lat = endPoint.latitude+"";
                        int index = lat.indexOf(".");
                        String lng = endPoint.longitude+"";
                        int index2 = lng.indexOf(".");
                        tv_end.setText("N"+lat.substring(0,index+3)+"°,E"+lng.substring(0,index2+3)+"°");
                        if(leftAdding){
                            openDialog.show();
                        }else{
                            guideDialog.show();
                        }
                    }else if(currentAddIndex==8){
                        if(startMarker==null){
                            MarkerOptions marker = new MarkerOptions()
                                    .icon(currentAddingBitmap).
                                            position(latLng);
                            startMarker = (Marker) mBaiduMap.addOverlay(marker);
                        }
                        startMarker.setPosition(latLng);
                        beforeAdding = false;
                        Toast.makeText(CommandActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        startPoint = latLng;
                        String lat = startPoint.latitude+"";
                        int index = lat.indexOf(".");
                        String lng = startPoint.longitude+"";
                        int index2 = lng.indexOf(".");
                        tv_start.setText("N"+lat.substring(0,index+3)+"°,E"+lng.substring(0,index2+3)+"°");
                        if(leftAdding){
                            openDialog.show();
                        }else{
                            guideDialog.show();
                        }
                    }else{
                        MarkerOptions marker = new MarkerOptions()
                                .icon(currentAddingBitmap).
                                        position(latLng);
                        mBaiduMap.addOverlay(marker);
                        beforeAdding = false;
                        Toast.makeText(CommandActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        if(leftAdding){
                            openDialog.show();
                        }else{
                            guideDialog.show();
                        }
                    }

                    currentPoint = latLng;
                    String lat = currentPoint.latitude+"";
                    int index = lat.indexOf(".");
                    String lng = currentPoint.longitude+"";
                    int index2 = lng.indexOf(".");
                    tv_title_open.setText("N"+lat.substring(0,index+3)+"°,E"+lng.substring(0,index2+3)+"°");
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        oneBodyFireAllList = new ArrayList<>();
        oneBodyFireList = new ArrayList<>();
        optionsAllList = new ArrayList<>();


        //设置起点终点
        openDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        openInflater = LayoutInflater.from(this).inflate(R.layout.dialog_fight_open, null);
        openInflater.setMinimumWidth(10000);
        tv_title_open = openInflater.findViewById(R.id.tv_title_open);
        cha_open = openInflater.findViewById(R.id.cha_open);
        ll_1 = openInflater.findViewById(R.id.ll_1);
        ll_2 = openInflater.findViewById(R.id.ll_2);
        ll_3 = openInflater.findViewById(R.id.ll_3);
        ll_4 = openInflater.findViewById(R.id.ll_4);
        ll_5 = openInflater.findViewById(R.id.ll_5);
        ll_6 = openInflater.findViewById(R.id.ll_6);
        ll_clean = openInflater.findViewById(R.id.ll_clean);
        tv_zhongdian = openInflater.findViewById(R.id.tv_zhongdian);
        tv_qidian = openInflater.findViewById(R.id.tv_qidian);
        RxViewAction.clickNoDouble(cha_open).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                openDialog.dismiss();
            }
        });
        RxViewAction.clickNoDouble(ll_1).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(1);
            }
        });
        RxViewAction.clickNoDouble(ll_2).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(2);
            }
        });
        RxViewAction.clickNoDouble(ll_3).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(3);
            }
        });
        RxViewAction.clickNoDouble(ll_4).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(4);
            }
        });
        RxViewAction.clickNoDouble(ll_5).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(5);
            }
        });
        RxViewAction.clickNoDouble(ll_6).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(6);
            }
        });
        RxViewAction.clickNoDouble(ll_clean).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                mBaiduMap.clear();
                initOneBodyFireMarker();
                tv_start.setText("请选择");
                tv_end.setText("请选择");
                Toast.makeText(CommandActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
            }
        });
        RxViewAction.clickNoDouble(tv_zhongdian).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(7);
            }
        });
        RxViewAction.clickNoDouble(tv_qidian).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(8);
            }
        });
        openDialog.setContentView(openInflater);
        Window fireListWindow = openDialog.getWindow();
        fireListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp = fireListWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        fireListLp.height = (int) (height * 0.5);
        fireListWindow.setAttributes(fireListLp);
        openDialog.setCanceledOnTouchOutside(true);
        openDialog.show();

        //导航规划
        guideDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        guideInflater = LayoutInflater.from(this).inflate(R.layout.dialog_fight_guide, null);
        guideInflater.setMinimumWidth(10000);
        tv_car = guideInflater.findViewById(R.id.tv_car);
        tv_walk = guideInflater.findViewById(R.id.tv_walk);
        cha_guide = guideInflater.findViewById(R.id.cha_guide);
        fl_start = guideInflater.findViewById(R.id.fl_start);
        fl_end = guideInflater.findViewById(R.id.fl_end);
        tv_start = guideInflater.findViewById(R.id.tv_start);
        tv_end = guideInflater.findViewById(R.id.tv_end);
        tv_guide = guideInflater.findViewById(R.id.tv_guide);

        RxViewAction.clickNoDouble(cha_guide).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                guideDialog.dismiss();
                openDialog.show();
            }
        });
        RxViewAction.clickNoDouble(tv_car).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                guideType = 0;
                tabChange();
            }
        });
        RxViewAction.clickNoDouble(tv_walk).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                guideType = 1;
                tabChange();
            }
        });
        RxViewAction.clickNoDouble(fl_end).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(7);
            }
        });
        RxViewAction.clickNoDouble(fl_start).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                beforeAdding(8);
            }
        });
        RxViewAction.clickNoDouble(tv_guide).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(tv_start.getText().toString().contains("请选择") || tv_end.getText().toString().contains("请选择") ){
                    Toast.makeText(CommandActivity.this, "请先选择起点和终点", Toast.LENGTH_SHORT).show();
                    return;
                }
                //开始导航
                guide();
            }
        });

        guideDialog.setContentView(guideInflater);
        Window fireListWindow2 = guideDialog.getWindow();
        fireListWindow2.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp2 = fireListWindow2.getAttributes();
        WindowManager wm2 = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height2 = wm2.getDefaultDisplay().getHeight();
        fireListLp2.height = (int) (height2 * 0.32);
        fireListWindow2.setAttributes(fireListLp2);
        guideDialog.setCanceledOnTouchOutside(true);

    }

    private void guide() {
        double[] start_ = LatLngChangeNew.calBD09toGCJ02(startPoint.latitude, startPoint.longitude);
        double[] end_ = LatLngChangeNew.calBD09toGCJ02(endPoint.latitude, endPoint.longitude);

        Poi start = new Poi("", new com.amap.api.maps.model.LatLng(start_[0], start_[1]), "");
        Poi end = new Poi("指挥作战终点", new com.amap.api.maps.model.LatLng(end_[0], end_[1]), "");
        AmapNaviType type = AmapNaviType.DRIVER;
        if(guideType == 1){
            type = AmapNaviType.WALK;
        }
        AmapNaviParams params = new AmapNaviParams(start, null, end, type, AmapPageType.ROUTE);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(CommandActivity.this, params, CommandActivity.this);
    }

    private void tabChange() {
        if(guideType == 0){
            tv_car.setTextColor(getResources().getColor(R.color.bluedrak));
            tv_walk.setTextColor(getResources().getColor(R.color.c7));
            tv_car.setBackgroundResource(R.drawable.cikck_car);
            tv_walk.setBackgroundResource(R.drawable.cikck_walk);
        }else{
            tv_car.setTextColor(getResources().getColor(R.color.c7));
            tv_walk.setTextColor(getResources().getColor(R.color.bluedrak));
            tv_car.setBackgroundResource(R.drawable.cikck_walk);
            tv_walk.setBackgroundResource(R.drawable.cikck_car);
        }
    }

    private String currentAddStr = "";
    private int currentAddIndex = -1;
    private boolean beforeAdding = false;
    private boolean leftAdding = true;

    private void beforeAdding(int i) {
        currentAddIndex = i;
        switch (i){
            case 1:
                currentAddStr = "防火队伍";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.duiwu);
                break;
            case 2:
                currentAddStr = "防火队员";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.duiyuan);
                break;
            case 3:
                currentAddStr = "飞机";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.feiji);
                break;
            case 4:
                currentAddStr = "护林员";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.hulin);
                break;
            case 5:
                currentAddStr = "消防车";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.xiaofang);
                break;
            case 6:
                currentAddStr = "救护车";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.jiuhu);
                break;
            case 7:
                currentAddStr = "终点";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_zhong);
                break;
            case 8:
                currentAddStr = "起点";
                currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_qi);
                break;
        }
        Toast.makeText(this, "请在地图上选择要添加的'"+ currentAddStr + "'的位置", Toast.LENGTH_SHORT).show();
        beforeAdding = true;
        if(currentAddIndex == 7 || currentAddIndex == 8 ){
            leftAdding = false;
        }else{
            leftAdding = true;
        }
        openDialog.dismiss();
        guideDialog.dismiss();
    }

    private void addCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.fillColor(0x22FF6A6A); // 填充颜色
        circleOptions.stroke(new Stroke(1, 0xE6FF6A6A));
        circleOptions.center(latLng);
        circleOptions.zIndex(1);
        circleOptions.radius(400);
        mBaiduMap.addOverlay(circleOptions);
    }

    private void flyBaiduMap(double lat, double lng) {
        //飞到精确点上
        com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(
                lat, lng);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(17);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    private void flyBaiduMap84(double lat, double lnt) {
        //飞到精确点上
        double[] position = LatLngChangeNew.calWGS84toBD09(lat, lnt);
        com.baidu.mapapi.model.LatLng ll = new com.baidu.mapapi.model.LatLng(
                position[0], position[1]);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(17);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != baiduMapView) {
            baiduMapView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        baiduMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBaiduMap) {
            mBaiduMap.clear();
        }

        if (null != baiduMapView) {
            baiduMapView.onDestroy();
        }
    }

    private StringBuffer date = new StringBuffer();
    private StringBuffer endDate = new StringBuffer();
    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);
    }


    /**
     * 从服务器获取一体机数据
     */
    private void getOnebodyDataFromService() {
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            jsonObject.put("dto", dto);
            jsonObject.put("limit", 200);
            jsonObject.put("page", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams( RequestUtils.REQUEST_URL +"/fire/api/monitorFirealarm/page");
        params.setBodyContent(jsonObject.toString());
        Log.e("TAG", "getonebodyDataFromSetvice: NetworkType");
        params.addHeader("NetworkType", "Internet");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.setConnectTimeout(100000);
        showDY3();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "oneBodySuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Gson gson = new Gson();
                        oneBodyFireAllList.clear();
                        List<OneBodyFire> oneBodyJsonList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<OneBodyFire>>() {
                        }.getType());
                        //将所有疑似火情剔除
                        for (int i = 0; i < oneBodyJsonList.size(); i++) {
                            if (oneBodyJsonList.get(i).getIsReal()!=null) {
                                if (oneBodyJsonList.get(i).getIsReal() == 1){
                                    oneBodyFireAllList.add(oneBodyJsonList.get(i));
                                }
                            }else {
                                oneBodyFireAllList.add(oneBodyJsonList.get(i));
                            }
                        }
                        oneBodyFireList.clear();
                        for (int j = 0; j < oneBodyFireAllList.size(); j++) {
                            if (oneBodyFireAllList.get(j).getIsReal()==null) {
                                oneBodyFireList.add(oneBodyFireAllList.get(j));
                            }
                        }
                        if(oneBodyFireList.size()!=0){
                            flyBaiduMap84(oneBodyFireList.get(0).getAlarmLatitude(),oneBodyFireList.get(0).getAlarmLongitude());
                            initOneBodyFireMarker();
                        }else{
                            Toast.makeText(CommandActivity.this, "暂无火情", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CommandActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hideDY3();
            }
        });

    }


    void showDY3(){
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }
    void hideDY3(){
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    private BitmapDescriptor mBitmapEnd = BitmapDescriptorFactory.fromResource(R.drawable.huodian);
    private BitmapDescriptor currentAddingBitmap = BitmapDescriptorFactory.fromResource(R.drawable.duiwu);
    /**
     * 往百度地图上打点
     */
    private void initOneBodyFireMarker() {
        for (int i = 0; i < oneBodyFireList.size(); i++) {
            double[] doubles = LatLngChangeNew.calWGS84toBD09(oneBodyFireList.get(i).getAlarmLatitude(), oneBodyFireList.get(i).getAlarmLongitude());
            LatLng point = new LatLng(doubles[0], doubles[1]);
            Bundle bundle = new Bundle();
            bundle.putString("id", oneBodyFireList.get(i).getId());
            bundle.putInt("type", ONE_BODY);
            MarkerOptions marker = new MarkerOptions()
                    .icon(mBitmapEnd).
                            position(point);
            mBaiduMap.addOverlay(marker);
            addCircle(point);
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }
}