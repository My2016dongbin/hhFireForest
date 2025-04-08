package com.haohai.platform.fireforestplatform.ui.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.listener.MyLocationListener;
import com.ruyiruyi.rylibrary.bus.MessageWrap;
import com.ruyiruyi.rylibrary.bus.SignStatusBus;
import com.ruyiruyi.rylibrary.bus.WalkDistanceBus;
import com.ruyiruyi.rylibrary.db.BeiDouLocation;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.haohai.platform.platformmodel.ui.model.PositionModel;
import com.haohai.platform.platformmodel.ui.utils.TrackReceiver;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.time.FastDateFormat;
import com.ruyiruyi.rylibrary.ui.dialog.Common;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackService extends Service implements SensorEventListener {

    private static final String TAG = TrackService.class.getSimpleName();
    private Timer timer;
    public static int state = 0;  //0更改用户位置  1上传用户轨迹


    private double currentLongitude;
    private double currentLatitude;
    private String oldLongitude = "";
    private String oldLatitude = "";
    private String currentTime;
    private long currentLongTime;
    private static final int TIME_CHANGE = 10;
    private static final int AN_CHANGE = 1001;
    private int playTimer = 0;
    private List<PositionModel> positionModelList;
    private TrackReceiver trackReceiver;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIME_CHANGE) {
                playTimer += 10000;
                if (playTimer % 60000 == 0) {
                    //开启守护服务
                    startService(new Intent(getApplicationContext(), TrackService.class));
                }
                Date date = new Date();
                String time = date.toLocaleString();
                Log.e(TAG, "时间time为： " + time);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                currentTime = dateFormat.format(date);
                currentLongTime = date.getTime();
                //获取经纬度（百度地图）
                //getBaiduLocation();todo 暂未开启百度定位
                User user = new DbConfig(getApplicationContext()).getUser();
                if (user != null) {      //用户不存在
                    if (user.getIsLogin() == 1 /*&& CommonData.hasSign*/) {//签到后才上传位置信息已修改
                        initLocation(user);
                    }
                }
            } else if (msg.what == AN_CHANGE) {
                getLocation();
                mHandler.sendEmptyMessageDelayed(AN_CHANGE, 2000);
            }
        }
    };

    private void initLocation(User user) {
        changeUserPosition(user);
    }

    private int dis_int = 0;

    private void changeUserPosition(User user) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm");
        String format = simpleDateFormat.format(new Date());
        Log.e(TAG, "changeUserPosition: date = " + format);
        if (format.contains("00:00")) {//跨天清零
            CommonData.walkDistance = 0;
            //通知UI刷新巡护距离
            EventBus.getDefault().post(WalkDistanceBus.getInstance(""));
        }
        if (!CommonData.hasGet) {
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", user.getId());

            JSONObject posObj = new JSONObject();
            EventBus.getDefault().post(SignStatusBus.getInstance(""));
            if (CommonData.lat != 0 && CommonData.lng != 0) {
                if (CommonData.lastLat != 0 && CommonData.lastLng != 0) {
                    double distance = CommonUtil.distance(CommonData.lastLng, CommonData.lastLat, CommonData.lng, CommonData.lat);
                    if (distance <= 0.2 && distance > 0) {
                        if (!CommonData.hasSensor || CommonData.hasMove) {//没有传感器或者传感器检测到了移动
                            double dis_double = distance * 1000;
                            String dis_str = dis_double + "";
                            dis_int = Integer.parseInt(dis_str.substring(0, dis_str.indexOf("."))) + 1;
                            CommonData.walkDistance += dis_int;
                            //通知UI刷新巡护距离
                            EventBus.getDefault().post(WalkDistanceBus.getInstance(""));
                            //Toast.makeText(this, distance+" in testInfo", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "changeUserPosition: " + distance + " in testInfo");
                        } else {
                            dis_int = 0;
                        }
                    } else {
                        dis_int = 0;
                        //Toast.makeText(this, distance+" out testInfo", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "changeUserPosition: " + distance + " out testInfo");
                    }
                    Log.e(TAG, "changeUserPosition: distance = " + distance);
                    Log.e(TAG, "changeUserPosition: LatLng = " + CommonData.lastLng + "," + CommonData.lastLat + " | " + CommonData.lng + "," + CommonData.lat);

                }
                CommonData.lastLat = CommonData.lat;
                CommonData.lastLng = CommonData.lng;
                double[] doubles = {CommonData.lat, CommonData.lng};
                try {
                    doubles = new LatLngChangeNew().calBD09toWGS84(CommonData.lat, CommonData.lng);
                } catch (Exception e) {
                    Log.e(TAG, "calBD09toWGS84");
                }
                posObj.put("lat", CommonUtil.parsePointCount(CommonData.lat_84 + "", 6));
                posObj.put("lng", CommonUtil.parsePointCount(CommonData.lng_84 + "", 6));
                //存储位置信息
                DbConfig dbConfig = new DbConfig(getApplicationContext());
                DbManager db = dbConfig.getDbManager();
                User userM = dbConfig.getUser();
                userM.setLatitude(CommonData.lat);
                userM.setLongitude(CommonData.lng);
                try {
                    db.delete(User.class);
                    db.saveOrUpdate(userM);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }

            jsonObject.put("position", posObj);
            jsonObject.put("dayTotalDistance", CommonData.walkDistance);//总距离
            jsonObject.put("distance", dis_int);//距离上一次距离
            Log.e(TAG, "changeUserPosition: " + CommonData.walkDistance);
            Log.e(TAG, "changeUserPosition: " + dis_int);
        } catch (JSONException e) {
        }

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/trajectory/trackUpload");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.setConnectTimeout(10000);
        Log.e(TAG, "service---" + params);
        Log.e(TAG, "service---" + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:更新用户位置成功 " + result);
                if (hasNotice) {
                    CommonData.hasMove = false;
                }
                //通知UI刷新巡护距离
                EventBus.getDefault().post(WalkDistanceBus.getInstance("1"));

                //  Toast.makeText(TrackService.this, "", Toast.LENGTH_SHORT).show();
                Log.i("UpdateGUI: ", "11111");
                //broadcast
                // service 通过广播来更新GUI
                Intent intent = new Intent();
                intent.putExtra("message", jsonObject.toString());
                intent.setAction("zcd.voicerobot");
                sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" + ex.toString());
                ///存储当前时间位置
                double[] doubles = new LatLngChangeNew().calBD09toWGS84(Double.parseDouble(CommonData.lat + ""), Double.parseDouble(CommonData.lng + ""));
                String lat_ = CommonUtil.parsePointCount(doubles[0] + "", 6);
                String lng_ = CommonUtil.parsePointCount(doubles[1] + "", 6);
                BeiDouLocation beiDouLocation = new BeiDouLocation("location" + currentTime, currentTime, currentLongTime, Double.parseDouble(lat_), Double.parseDouble(lng_), Double.parseDouble(lng_) + "," + Double.parseDouble(lat_));
                try {
                    new DbConfig(TrackService.this).getDbManager().saveOrUpdate(beiDouLocation);
                    Log.e(TAG, "onError: 离线位置存储" + beiDouLocation.toString());
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    public TrackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        initBaiduLoc();

        //检测传感器
        boolean a = getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
        if (a) {
            CommonData.hasSensor = true;
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        positionModelList = new ArrayList<>();
        //getBaiduLocation();todo 暂未开启百度定位
        Log.e(TAG, "onCreate: 1");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "onCreate: 2");
                Message message = new Message();
                message.what = TIME_CHANGE;
                mHandler.sendMessage(message);   //需要开启轨迹上传 打开
            }
        }, 0, 5000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行

        mHandler.sendEmptyMessageDelayed(AN_CHANGE, 2000);


    }

    void initBaiduLoc() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；
        //LocationMode.Fuzzy_Locating, 模糊定位模式；v9.2.8版本开始支持，可以降低API的调用频率，但同时也会降低定位精度；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setNeedNewVersionRgc(true);
        //可选，设置是否需要最新版本的地址信息。默认需要，即参数为true

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), com.ruyiruyi.rylibrary.R.raw.alive);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Notification notification;
        Intent intent1 = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("location", "location", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this, "location");
            builder.setContentIntent(PendingIntent.getActivity(this, 0, intent1, 0))
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("为您持续巡护中...")
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("为您持续巡护中...")
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent1, 0))
                    .build();
        }
        startForeground(110, notification);

        return START_STICKY;
    }

    /*退出登录回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageWrap messageWrap) {
        timer.cancel();
        mLocationClient.stop();
        myListener = null;
        stopSelf();
    }

    private void getBaiduLocation() {
        Log.e(TAG, "getBaiduLocation: bingo");
        mLocationClient.start();
    }


    /**
     * 获取当前位置经纬度
     *
     * @return
     */
    @JavascriptInterface
    public String getLocationOld() {
        //获得位置服务
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(getContext(), "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }*/
        String provider = judgeProvider(locationManager);
        //有位置提供器的情况
        List<String> providerList = locationManager.getProviders(true);
        // 测试一般都在室内，这里颠倒了书上的判断顺序
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            //Toast.makeText(getContext(), "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();

        }
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            try {
                Log.e(TAG, "getLocation: 84 " + location.getLongitude() + "," + location.getLatitude());
                double[] doubles = LatLngChangeNew.calWGS84toBD09(location.getLatitude(), location.getLongitude());
                CommonData.lng_84 = location.getLongitude();
                CommonData.lat_84 = location.getLatitude();
                CommonData.lng_an = doubles[1];
                CommonData.lat_an = doubles[0];
                CommonData.lng = doubles[1];
                CommonData.lat = doubles[0];
                Log.e(TAG, "getLocation: bd09 " + doubles[1] + "," + doubles[0]);
                return doubles[1] + "," + doubles[0];
            } catch (Exception e) {
                return "0.00,0.00";
            }

        } else {
            CommonData.lng_84 = 0;
            CommonData.lat_84 = 0;
            CommonData.lng_an = 0;
            CommonData.lat_an = 0;
            CommonData.lng = 0;
            CommonData.lat = 0;
        }
        return null;
    }


    /**
     * 获取当前位置经纬度
     * @return
     */
    // @JavascriptInterface
    public void getLocation() {
        //获得位置服务
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_HIGH);//低功耗

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "您未开启定位权限", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0001f, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e(TAG, "onLocationChanged: " + location.getLongitude() + "," + location.getLatitude());
                double longitude = 0.00;
                double latitude = 0.00;
                try {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                } catch (Exception e) {

                }

                currentLongitude = longitude;
                currentLatitude = latitude;
                double[] doubles = LatLngChangeNew.calWGS84toBD09(location.getLatitude(), location.getLongitude());
                CommonData.lng_84 = location.getLongitude();
                CommonData.lat_84 = location.getLatitude();
                CommonData.lng_an = doubles[1];
                CommonData.lat_an = doubles[0];
                CommonData.lng = doubles[1];
                CommonData.lat = doubles[0];

                //  Toast.makeText(TrackService.this, "经纬度发生改变了,经度" +longitude + "纬度" +latitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS已开启", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "请打开GPS", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Toast.makeText(this, "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        // 获取最好的定位方式
        String provider = locationManager.getBestProvider(criteria, true); // true 代表从打开的设备中查找

        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        // 测试一般都在室内，这里颠倒了书上的判断顺序
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(this, "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();
            return;
        }


        //有位置提供器的情况
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // return null;
            }
            Location location= locationManager.getLastKnownLocation(provider);
            double longitude = 0.00;
            double latitude = 0.00;
            try {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                oldLongitude = longitude +"";
                oldLatitude = latitude + "";
                currentLongitude = longitude ;
                currentLatitude = latitude ;
                double[] doubles = LatLngChangeNew.calWGS84toBD09(location.getLatitude(), location.getLongitude());
                CommonData.lng_84 = location.getLongitude();
                CommonData.lat_84 = location.getLatitude();
                CommonData.lng_an = doubles[1];
                CommonData.lat_an = doubles[0];
                CommonData.lng = doubles[1];
                CommonData.lat = doubles[0];
                Log.e(TAG, "getLocation: --" + longitude);
                Log.e(TAG, "getLocation: *--" + latitude);
            }catch (Exception e){

            }
         /*   BigDecimal   la   =   new BigDecimal(latitude);
            double   lat = la.setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue();*/
            //    return longitude + "," + latitude;
            //   return "0.00,0.00";
        }else {
            //  return "0.00,0.00";
        }
    }

    /**
     * 定位器provider
     *
     * @param locationManager
     * @return
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;//网络定位
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        } else {
            //Toast.makeText(getContext(), "未开启本应用地理位置信息，请先开启！", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSensorManager.unregisterListener(this);
    }

    private boolean hasNotice = false;
    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.e("Counter-SensorChanged",event.values[0]+"---"+event.accuracy+"---"+event.timestamp);
        double value = event.values[0];
        if(value != 0){
            hasNotice = true;
        }
        if(value > CommonData.sensorValue){
            if(value - CommonData.sensorValue > 1){
                CommonData.hasMove = true;
            }
        }else{
            if(CommonData.sensorValue - value > 1){
                CommonData.hasMove = true;
            }
        }

        CommonData.sensorValue = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
