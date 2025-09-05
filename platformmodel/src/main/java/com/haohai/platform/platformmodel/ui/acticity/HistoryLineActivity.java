package com.haohai.platform.platformmodel.ui.acticity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.platformmodel.ui.Multitype.Empty;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.Multitype.EmptyViewBinder;
import com.haohai.platform.platformmodel.ui.Multitype.PersonViewBinder;
import com.haohai.platform.platformmodel.ui.Multitype.TreeViewBinder;
import com.haohai.platform.platformmodel.ui.acticity.base.HhBaseActivity;
import com.haohai.platform.platformmodel.ui.model.HistoryLine;
import com.haohai.platform.platformmodel.ui.model.Person;
import com.haohai.platform.platformmodel.ui.model.Tree;
import com.haohai.platform.platformmodel.ui.utils.MapHelper;
import com.haohai.platform.platformmodel.ui.utils.TreeUtils;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class HistoryLineActivity extends HhBaseActivity implements SensorEventListener, DatePicker.OnDateChangedListener, TreeViewBinder.OnTreeClick, PersonViewBinder.OnPersonClick {
    FrameLayout fl_bar;
    IndicatorSeekBar bar;
    LinearLayout ll_bitmaps;
    ImageView info_show;
    TextView info_index;
    TextView info_text;
    ImageView iv_back;
    TextView tv_find;
    private LocationClient mLocationClient;
    private DYLoadingView dy3;
    private Dialog searchDialog;
    private View searchInflater;
    private Dialog resultDialog;
    private View resultInflater;
    private LinearLayout ll_page1;
    private RecyclerView ll_page2;
    private TextView tv_title;
    private FrameLayout fl_date;
    private FrameLayout fl_date_end;
    private FrameLayout fl_start;
    private FrameLayout fl_end;
    private FrameLayout fl_user;
    private FrameLayout fl_back;
    private TextView tv_search;
    private TextView tv_date;
    private TextView tv_date_end;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_user;
    private TextView tv_result_title;
    private ImageView cha;
    private ImageView cha_result;
    private TextView result_km;
    private TextView result_time;
    private TextView result_start;
    private TextView result_end;

    private com.amap.api.maps.MapView aMapView;
    private com.amap.api.maps.AMap aMap;
    private int status = 0;//0未查询 1日期 2查询结束
    private String TAG = HistoryLineActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_line);
        aMapView = ((com.amap.api.maps.MapView) findViewById(com.haohai.platform.mapmodel.R.id.aMapView));
        aMapView.onCreate(savedInstanceState);
        aMap = aMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        flyBaiduMap(36.195139,117.098229);//泰安市
        mHandler = new Handler(getMainLooper());
        init();
        initTree();
        startLocation();
    }


    private MultiTypeAdapter adapter;
    private List<Object> items = new ArrayList<>();
    private List<Tree> treeList = new ArrayList<>();
    private void initTree() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/grid/listGridNewTrees");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: initTree" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    treeList = new Gson().fromJson(String.valueOf(object.getJSONArray("data")),new TypeToken<List<Tree>>(){}.getType());

                    treeList = TreeUtils.parseLevel(treeList);
                    initTreeData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //在这处理数据逻辑  获取到人员的条目 然后吧人员条目根据id 放到treeList 里 后调佣initTreeData ()进行更新

    private void initTreeData () {
        items.clear();
        if (treeList.size() == 0) {
            items.add(new Empty("暂无数据"));
        } else {
            items.addAll(treeList);
        }
        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }


    /**
     * 启动定位
     */
    private void startLocation() {
        // 定位初始化
        mLocationClient = new LocationClient(this);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(1000);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationClientOption.setIsNeedAddress(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation
        locationClientOption.setIsNeedLocationPoiList(true);
        // 设置定位参数
        mLocationClient.setLocOption(locationClientOption);
        // 开启定位
        mLocationClient.start();
    }

    void showDY3() {
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }

    void hideDY3() {
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    private List<LatLng> lineList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<com.amap.api.maps.model.LatLng> allList = new ArrayList<>();
    private double distance = 0;

    private String searchId;

    private void postData(){
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/trajectory/userTrajectoryByTime/"+new DbConfig(this).getUser().getId());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addParameter("id",new DbConfig(this).getUser().getId());
        params.addParameter("time",tv_date.getText().toString());
        Log.e(TAG,"postData " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG,"postData " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");

                    List<com.amap.api.maps.model.LatLng> list = new ArrayList<>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = (JSONObject) data.get(i);
                        JSONObject position = obj.getJSONObject("position");
                        com.amap.api.maps.model.LatLng latLng = new com.amap.api.maps.model.LatLng(position.getDouble("lat"),position.getDouble("lng"));
                        list.add(latLng);
                        allList.add(latLng);
                        timeList.add(obj.getString("offlineUploadTime"));
                    }
                    //result_km.setText("1024" + "m");

                    drawPolyLine(list);


                    searchDialog.dismiss();
                    /*tv_result_title.setText(tv_date.getText().toString() + "巡护轨迹");
                    //result_time.setText((longs[0] * 24 + longs[1]) + ":" + longs[2] + ":" + longs[3]);
                    result_start.setText(tv_start.getText().toString());
                    result_end.setText(tv_end.getText().toString());
                    resultDialog.show();

                    //轨迹Bar数据初始化
                    bar.setMin(0f);
                    bar.setMax(timeList.size()*1.0f - 1);
                    Log.e(TAG, "onSuccess: timeList " + timeList );
                    if(timeList.isEmpty()){
                        fl_bar.setVisibility(View.GONE);
                    }else{
                        fl_bar.setVisibility(View.VISIBLE);
                        bar.setProgress(timeList.size()-1);
                        flyBaiduMap(allList.get(allList.size()-1).latitude,allList.get(allList.size()-1).longitude);
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private String parseSix(String str) {
        if (str.length() > 6) {
            return str.substring(0, 6);
        }
        return str;
    }
    private long parseDate(long date) {
        String str = date + "";
        if (str.length() > 10) {
            return Long.parseLong(str.substring(0, 10));
        }
        return date;
    }


    private BitmapDescriptor mGreenTexture =
            BitmapDescriptorFactory.fromAsset("Icon_road_blue__.png");
    private BitmapDescriptor mBitmapCar = BitmapDescriptorFactory.fromResource(R.drawable.icon_car);
    private BitmapDescriptor mBitmapStart = BitmapDescriptorFactory.fromResource(R.drawable.ic_qi);
    private BitmapDescriptor mBitmapEnd = BitmapDescriptorFactory.fromResource(R.drawable.ic_zhong);
    private Handler mHandler;
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private static final int TIME_INTERVAL = 30;
    private static final double DISTANCE = 0.000005;


    private Polyline polyline; // 保存上一次的轨迹线

    /**
     * 绘制轨迹
     */
    @SuppressLint("SetTextI18n")
    private void drawPolyLine(List<com.amap.api.maps.model.LatLng> list) {
        if (list == null || list.size() < 2) {
            Toast.makeText(this, "轨迹点位数据太少，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        aMap.clear();
        // 清除旧的轨迹线（避免把其他 Marker 也清了）
        if (polyline != null) {
            polyline.remove();
        }

        // 设置折线样式
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(list)
                .color(0xFF1E90FF) // 轨迹线颜色 (深蓝)
                .width(12f)        // 轨迹线宽度
                .setUseTexture(true) // 支持纹理（如果要画虚线/箭头）
                .geodesic(true);     // 大地曲线，更符合地理实际

        // 添加轨迹线
        polyline = aMap.addPolyline(polylineOptions);

        // 起点 Marker
        aMap.addMarker(new MarkerOptions()
                .position(list.get(0))
                .snippet("")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // 终点 Marker
        aMap.addMarker(new MarkerOptions()
                .position(list.get(list.size() - 1))
                .snippet("")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // 自动缩放到轨迹范围
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (com.amap.api.maps.model.LatLng latLng : list) {
            boundsBuilder.include(latLng);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
    }


    BitmapDescriptor mIconMarker;
    Bitmap bitmap;

    private Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    private void flyBaiduMap(double lat, double lng) {
        //飞到精确点上
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.amap.api.maps.model.LatLng(lat,lng),16));

    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        } else if (slope == 0.0) {
            if (toPoint.longitude > fromPoint.longitude) {
                return -90;
            } else {
                return 90;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude
                - fromPoint.longitude));
        return slope;
    }

    private int loopTag = 0;//防止跳点前一次轨迹



    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * 1 / slope) / Math.sqrt(1 + 1 / (slope * slope)));
    }

    /**
     * 计算y方向每次移动的距离
     */
    private double getYMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {
        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    @Override
    protected void onPause() {
        super.onPause();
        aMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        aMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (null != mBitmapCar) {
            mBitmapCar.recycle();
        }

        if (null != mGreenTexture) {
            mGreenTexture.recycle();
        }

        aMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        aMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onTreeClickListener() {

    }

    @Override
    public void onTreeItemClickListener(Tree tree) {

    }

    private String chooseUserId;
    @Override
    public void onPersonClickListener(Person person) {
        searchPage = 0;
        tv_title.setText("查询巡护轨迹");
        ll_page2.setVisibility(View.GONE);
        ll_page1.setVisibility(View.VISIBLE);
        fl_back.setVisibility(View.GONE);
        tv_user.setText(person.getFullName());
        chooseUserId = person.getId();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            //mBaiduMap.setMyLocationData(locData);
        }
    }

    private int searchPage = 0;//0 时间选择  1 用户选择
    private void init() {
        chooseUserId = new DbConfig(this).getUser().getId();//默认本账号UserId
        ll_bitmaps = findViewById(R.id.ll_bitmaps);
        info_show = findViewById(R.id.info_show);
        info_index = findViewById(R.id.info_index);
        info_text = findViewById(R.id.info_text);
        iv_back = findViewById(R.id.iv_back);
        tv_find = findViewById(R.id.tv_find);
        dy3 = findViewById(R.id.dy3);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                finish();
            }
        });
        RxViewAction.clickNoDouble(tv_find).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                searchDialog.hide();
                resultDialog.hide();
                searchDialog.show();
            }
        });
        date = new StringBuffer();
        endDate = new StringBuffer();
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);


        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09"); // 设置坐标类型
        option.setScanSpan(1000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();

        //查询巡护轨迹
        searchDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        searchInflater = LayoutInflater.from(this).inflate(R.layout.dialog_line_search, null);
        searchInflater.setMinimumWidth(10000);
        tv_title = searchInflater.findViewById(R.id.tv_title);
        ll_page1 = searchInflater.findViewById(R.id.ll_page1);
        ll_page2 = searchInflater.findViewById(R.id.ll_page2);
        fl_date = searchInflater.findViewById(R.id.fl_date);
        fl_date_end = searchInflater.findViewById(R.id.fl_date_end);
        fl_start = searchInflater.findViewById(R.id.fl_start);
        fl_end = searchInflater.findViewById(R.id.fl_end);
        fl_user = searchInflater.findViewById(R.id.fl_user);
        fl_back = searchInflater.findViewById(R.id.fl_back);
        tv_date = searchInflater.findViewById(R.id.tv_date);
        tv_date_end = searchInflater.findViewById(R.id.tv_date_end);
        tv_start = searchInflater.findViewById(R.id.tv_start);
        cha = searchInflater.findViewById(R.id.cha);
        tv_end = searchInflater.findViewById(R.id.tv_end);
        tv_user = searchInflater.findViewById(R.id.tv_user);
        tv_search = searchInflater.findViewById(R.id.tv_search);
        RxViewAction.clickNoDouble(fl_date).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = true;
                showDataDialog();
            }
        });
        RxViewAction.clickNoDouble(fl_start).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = true;
                showTimeDialog();
            }
        });
        RxViewAction.clickNoDouble(fl_date_end).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = false;
                showDataDialog();
            }
        });
        RxViewAction.clickNoDouble(fl_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                searchPage = 0;
                tv_title.setText("查询巡护轨迹");
                ll_page2.setVisibility(View.GONE);
                ll_page1.setVisibility(View.VISIBLE);
                fl_back.setVisibility(View.GONE);
            }
        });
        RxViewAction.clickNoDouble(fl_user).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                searchPage = 1;
                tv_title.setText("选择查询用户");
                ll_page1.setVisibility(View.GONE);
                ll_page2.setVisibility(View.VISIBLE);
                fl_back.setVisibility(View.VISIBLE);
            }
        });
        RxViewAction.clickNoDouble(fl_end).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isStart = false;
                showTimeDialog();
            }
        });
        RxViewAction.clickNoDouble(cha).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                searchDialog.dismiss();
            }
        });
        RxViewAction.clickNoDouble(tv_search).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if (tv_date.getText().toString().contains("请选择")) {
                    Toast.makeText(HistoryLineActivity.this, "请选择开始日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_start.getText().toString().contains("请选择")) {
                    Toast.makeText(HistoryLineActivity.this, "请选择开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_date_end.getText().toString().contains("请选择")) {
                    Toast.makeText(HistoryLineActivity.this, "请选择结束日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tv_end.getText().toString().contains("请选择")) {
                    Toast.makeText(HistoryLineActivity.this, "请选择结束时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                postData();
            }
        });
        searchDialog.setContentView(searchInflater);
        Window fireListWindow = searchDialog.getWindow();
        fireListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp = fireListWindow.getAttributes();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        fireListLp.height = (int) (height * 0.3);
        fireListWindow.setAttributes(fireListLp);
        searchDialog.setCanceledOnTouchOutside(true);
        searchDialog.show();

        //查询结束
        resultDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        resultInflater = LayoutInflater.from(this).inflate(R.layout.dialog_line_result, null);
        resultInflater.setMinimumWidth(10000);
        tv_result_title = resultInflater.findViewById(R.id.tv_result_title);
        cha_result = resultInflater.findViewById(R.id.cha_result);
        result_km = resultInflater.findViewById(R.id.tv_km);
        result_time = resultInflater.findViewById(R.id.tv_time);
        result_start = resultInflater.findViewById(R.id.tv_start);
        result_end = resultInflater.findViewById(R.id.tv_end);
        RxViewAction.clickNoDouble(cha_result).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                resultDialog.dismiss();
                searchDialog.show();
            }
        });
        resultDialog.setContentView(resultInflater);
        Window fireListWindow2 = resultDialog.getWindow();
        fireListWindow2.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp2 = fireListWindow2.getAttributes();
        WindowManager wm2 = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height2 = wm2.getDefaultDisplay().getHeight();
        fireListLp2.height = (int) (height2 * 0.2);
        fireListWindow2.setAttributes(fireListLp2);
        resultDialog.setCanceledOnTouchOutside(true);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ll_page2.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);
        TreeViewBinder binder = new TreeViewBinder();
        binder.setListener(this,this);
        binder.setPersonListener(this);//待优化
        adapter.register(Tree.class, binder);
        PersonViewBinder binder_person = new PersonViewBinder();
        binder_person.setListener(this);
        adapter.register(Person.class, binder_person);
        adapter.register(Empty.class, new EmptyViewBinder());
        ll_page2.setAdapter(adapter);
        assertHasTheSameAdapter(ll_page2, adapter);


        initDateTime();

    }

    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private float mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private MyLocationData myLocationData;
    private float mCurrentAccracy;
    private boolean isFirstLoc = true;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (float) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection)
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            //mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private StringBuffer date;
    private StringBuffer endDate;
    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (endDate.length() > 0) { //清除上次记录的日期
                    endDate.delete(0, endDate.length());
                }
                date.append(String.valueOf(year));
                if (month < 9) {
                    date.append("-0").append(String.valueOf(month + 1));
                } else {
                    date.append("-").append(String.valueOf(month + 1));
                }
                if (day < 10) {
                    date.append("-0").append(String.valueOf(day));
                } else {
                    date.append("-").append(String.valueOf(day));
                }
                if (isStart) {
                    tv_date.setText(date);
                } else {
                    tv_date_end.setText(date);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final android.support.v7.app.AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, com.haohai.platform.firelibrary.R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(com.haohai.platform.firelibrary.R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long starTimre = date2.getTime();


        long endTimre = System.currentTimeMillis();

        datePicker.setMaxDate(endTimre);
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);
        tv_date.setText(year + "-" + CommonUtil.parseZero(month+1) + "-" + CommonUtil.parseZero(day));
        tv_start.setText(CommonUtil.parseZero(chooseHour) + ":" + CommonUtil.parseZero(chooseMinute) + ":00");

        calendar.add(Calendar.HOUR_OF_DAY, 2);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseMinute = calendar.get(Calendar.MINUTE);

        tv_date_end.setText(year + "-" + CommonUtil.parseZero(month+1) + "-" + CommonUtil.parseZero(day));
        tv_end.setText(CommonUtil.parseZero(chooseHour) + ":" + CommonUtil.parseZero(chooseMinute) + ":00");
    }

    private boolean isStart = true;

    /**
     * 日期选择控件
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showTimeDialog() {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(this);
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (isStart) {
                    tv_start.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_start.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_start.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_start.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_start.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                } else {
                    tv_end.setText("");
                    if (chooseHour < 10 && chooseMinute < 10) {
                        tv_end.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    } else if (chooseHour < 10 && chooseMinute > 10) {
                        tv_end.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    } else if (chooseHour > 10 && chooseMinute < 10) {
                        tv_end.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    } else {
                        tv_end.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                }

                dialog.dismiss();
            }
        });
        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final android.support.v7.app.AlertDialog timeDialog = builder1.create();
        View dialogView = View.inflate(this, R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        timePicker.setIs24HourView(true);   //设置时间显示为24小时

        timePicker.setHour(hour);  //设置当前小时
        timePicker.setMinute(minute); //设置当前分（0-59）

        timeDialog.setTitle("设置时间");
        timeDialog.setView(dialogView);
        timeDialog.show();


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //获取当前选择的时间
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                chooseHour = hourOfDay;
                chooseMinute = minute;
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
}