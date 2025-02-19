package com.haohai.platform.firelibrary.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.model.LatLng;
import com.haohai.platform.firelibrary.ui.multitype.FireMission;
import com.haohai.platform.firelibrary.utils.LatLngChange;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

public class FireMissionInfoActivity extends HhBaseActivity implements INaviInfoCallback {
    private static final String TAG = FireMissionInfoActivity.class.getSimpleName();
    private ActionBar actionBar;
    private String id;

    private ProgressDialog progressDialog;
    private FireMission fireMission;
    private TextView kaishishijianView;
    private TextView jieshushijianView;
    private TextView zhixingrenView;
    private TextView faburenView;
    private TextView renwuneirongView;
    private TextView jingduView;
    private TextView weiduView;
    private ImageView imageOneView;
    private ImageView imageTwoView;
    private ImageView imageThreeView;
    private TextView dizhiView;
    private TextView orderStateView;
    private TextView kaishirenwuView;
    private TextView daozheliView;
    private boolean isChange = false;
    private TextView shangbaoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_mission_info);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        isChange = false;
        initView();
        bindView();

        getDataFromService();
    }

    private void bindView() {
        /**
         * 到这里
         */
        RxViewAction.clickNoDouble(daozheliView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                      /*  //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
                        AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
//启动导航组件
                        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);*/
                        String jingweiStr = getLocation();
                        String starweidu = "";
                        String starjingdu = "";
                        if (!jingweiStr.isEmpty()) {
                            List<String> jingweiList = Arrays.asList(jingweiStr.split(","));
                            starweidu = jingweiList.get(1);
                            starjingdu = jingweiList.get(0);
                        }

                        LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(fireMission.getPosition().getLat(), fireMission.getPosition().getLng()));

                        Poi start = new Poi("", new com.amap.api.maps.model.LatLng(Double.parseDouble(starweidu), Double.parseDouble(starjingdu)), "");
                        Poi end = new Poi(fireMission.getReserve(), new com.amap.api.maps.model.LatLng(latLng.latitude, latLng.longitude), "");
                        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                        params.setUseInnerVoice(true);
                        Log.e(TAG, "call: start" + starweidu );
                        Log.e(TAG, "call: end" + latLng.latitude );
                        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, FireMissionInfoActivity.this);
                    }
                });
        /**
         * 开始任务
         */
        RxViewAction.clickNoDouble(kaishirenwuView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (fireMission.getStatus() != 2){
                            changeStateToService();
                        }else {
                            Toast.makeText(FireMissionInfoActivity.this, "任务已完成", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        /**
         * 现场上报
         */
        RxViewAction.clickNoDouble(shangbaoButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(getApplicationContext(), FireSceneActivity.class);
                        intent.putExtra("ID",id);
                        startActivity(intent);
                    }
                });
    }


    private void initView() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("任务详情");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch ((var1)) {
                    case -1:
                        onBackPressed();
                        break;
                }
            }
        });

        shangbaoButton = (TextView) findViewById(R.id.shangbao_button);
        kaishirenwuView = (TextView) findViewById(R.id.kaishirenwu_view);
        daozheliView = (TextView) findViewById(R.id.daozheli_view);

        kaishishijianView = (TextView) findViewById(R.id.kaishishijian_view);
        jieshushijianView = (TextView) findViewById(R.id.jieshushijian_view);
        zhixingrenView = (TextView) findViewById(R.id.zhixingren_view);
        faburenView = (TextView) findViewById(R.id.faburen_view);
        renwuneirongView = (TextView) findViewById(R.id.renwuneirong_view);
        jingduView = (TextView) findViewById(R.id.jingdu_view);
        weiduView = (TextView) findViewById(R.id.weidu_view);
        imageOneView = (ImageView) findViewById(R.id.image_one_view);
        imageTwoView = (ImageView) findViewById(R.id.image_two_view);
        imageThreeView = (ImageView) findViewById(R.id.image_three_view);
        dizhiView = (TextView) findViewById(R.id.dizhi_view);
    }

    /**
     * 更改任务单状态
     */
    private void changeStateToService() {
        showDialogProgress(progressDialog,"提交中...");
        JSONObject jsonObject = new JSONObject();

        try {
            //0未开始，1执行中，2已结束
            if (fireMission.getStatus() == 0){
                jsonObject.put("status", 1);
            }else if(fireMission.getStatus() == 1){
                jsonObject.put("status", 2);
            }
            jsonObject.put("id",fireMission.getId());

        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/taskManagement");
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        Log.e(TAG, "changeStateToService: " + params);
        Log.e(TAG, "changeStateToService: " + jsonObject.toString());
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        isChange = true;
                      getDataFromService();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }


    private void getDataFromService() {
        showDialogProgress(progressDialog,"加载中...");
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/taskManagement");
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.addParameter("id",id);
        Log.e(TAG, "postData:-- params--" + params);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        Gson gson = new Gson();
                        fireMission = gson.fromJson(data.toString(), FireMission.class);

                        initData();
                    }else {
                        Toast.makeText(FireMissionInfoActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
                    }



                  /*  for (int i = 0; i < data.length(); i++) {
                        JSONObject dto = data.getJSONObject(i).getJSONObject("dto");
                        FlowApprove flowApprove = gson.fromJson(dto.toString(), FlowApprove.class);
                        flowApproveList.add(flowApprove);
                    }*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    private void initData() {
        kaishishijianView.setText(fireMission.getTaskStartTime());
        jieshushijianView.setText(fireMission.getTaskEndTime());
        zhixingrenView.setText(fireMission.getOperatorName());
        renwuneirongView.setText(fireMission.getTaskContent());
        jingduView.setText(fireMission.getPosition().getLng() +" ");
        weiduView.setText(fireMission.getPosition().getLat() + " ");
        dizhiView.setText(fireMission.getReserve());

        //0未开始，1执行中，2已结束
        if (fireMission.getStatus() == 0) {
            kaishirenwuView.setText("未开始");
        }else if (fireMission.getStatus() == 1){
            kaishirenwuView.setText("执行中");
        }else if (fireMission.getStatus() == 2){
            kaishirenwuView.setText("已结束");
        }

        String[] imgArray = fireMission.getTaskImg().split(",");
        if (imgArray.length==1){
            imageOneView.setVisibility(View.VISIBLE);
            imageTwoView.setVisibility(View.GONE);
            imageThreeView.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(imgArray[0].replace("10.10.13.3:80","183.230.142.120:8081"))
                    .error(R.drawable.ic_jaizai)
                    .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException: pic1 - 1" );
                    String str = imgArray[0].replace("10.10.13.3:80","183.230.142.120:8081");
                    if(!str.contains("tmp")){
                        str += ".tmp";
                    }

                    Log.e(TAG, "onException: pic1 - 1 str = " + str);
                    final DownloadTask downloadTask = new DownloadTask(FireMissionInfoActivity.this);
                    downloadTask.setLocation(1);
                    downloadTask.execute(str);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageOneView);
        }else if (imgArray.length==2){
            imageOneView.setVisibility(View.VISIBLE);
            imageTwoView.setVisibility(View.VISIBLE);
            imageThreeView.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(imgArray[0].replace("10.10.13.3:80","183.230.142.120:8081"))
                    .error(R.drawable.ic_jaizai)
                    .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException: pic1 - 1" );
                    String str = imgArray[0].replace("10.10.13.3:80","183.230.142.120:8081");
                    if(!str.contains("tmp")){
                        str += ".tmp";
                    }

                    Log.e(TAG, "onException: pic1 - 1 str = " + str);
                    final DownloadTask downloadTask = new DownloadTask(FireMissionInfoActivity.this);
                    downloadTask.setLocation(1);
                    downloadTask.execute(str);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageOneView);
            Glide.with(getApplicationContext()).load(imgArray[1].replace("10.10.13.3:80","183.230.142.120:8081"))
                    .error(R.drawable.ic_jaizai)
                    .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException: pic1 - 1" );
                    String str = imgArray[1].replace("10.10.13.3:80","183.230.142.120:8081");
                    if(!str.contains("tmp")){
                        str += ".tmp";
                    }

                    Log.e(TAG, "onException: pic1 - 1 str = " + str);
                    final DownloadTask downloadTask = new DownloadTask(FireMissionInfoActivity.this);
                    downloadTask.setLocation(2);
                    downloadTask.execute(str);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageTwoView);
        }else {
            imageOneView.setVisibility(View.VISIBLE);
            imageTwoView.setVisibility(View.VISIBLE);
            imageThreeView.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext()).load(imgArray[0].replace("10.10.13.3:80","183.230.142.120:8081"))
                    .error(R.drawable.ic_jaizai)
                    .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException: pic1 - 1" );
                    String str = imgArray[0].replace("10.10.13.3:80","183.230.142.120:8081");
                    if(!str.contains("tmp")){
                        str += ".tmp";
                    }

                    Log.e(TAG, "onException: pic1 - 1 str = " + str);
                    final DownloadTask downloadTask = new DownloadTask(FireMissionInfoActivity.this);
                    downloadTask.setLocation(1);
                    downloadTask.execute(str);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageOneView);
            Glide.with(getApplicationContext()).load(imgArray[1].replace("10.10.13.3:80","183.230.142.120:8081"))
                    .error(R.drawable.ic_jaizai)
                    .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException: pic1 - 1" );
                    String str = imgArray[1].replace("10.10.13.3:80","183.230.142.120:8081");
                    if(!str.contains("tmp")){
                        str += ".tmp";
                    }

                    Log.e(TAG, "onException: pic1 - 1 str = " + str);
                    final DownloadTask downloadTask = new DownloadTask(FireMissionInfoActivity.this);
                    downloadTask.setLocation(2);
                    downloadTask.execute(str);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageTwoView);
            Glide.with(getApplicationContext()).load(imgArray[2].replace("10.10.13.3:80","183.230.142.120:8081"))
                    .error(R.drawable.ic_jaizai)
                    .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "onException: pic1 - 1" );
                    String str = imgArray[2].replace("10.10.13.3:80","183.230.142.120:8081");
                    if(!str.contains("tmp")){
                        str += ".tmp";
                    }

                    Log.e(TAG, "onException: pic1 - 1 str = " + str);
                    final DownloadTask downloadTask = new DownloadTask(FireMissionInfoActivity.this);
                    downloadTask.setLocation(3);
                    downloadTask.execute(str);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            }).into(imageThreeView);
         }
    }

    private File file1 = null;
    private File file2 = null;
    private File file3 = null;
    /**
     * 下载文件
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private int number = 1;

        public DownloadTask(Context context) {
            this.context = context;
        }

        void setLocation(int num){
            number = num;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    if(number == 1){
                        file1 = new File(FireMissionInfoActivity.this.getObbDir().getAbsolutePath(),
                                "pic1" + new Date().getTime() + ".jpeg");

                        if (!file1.exists()) {
                            // 判断父文件夹是否存在
                            if (!file1.getParentFile().exists()) {
                                file1.getParentFile().mkdirs();
                            }
                        }
                    }else if (number == 2){
                        file2 = new File(FireMissionInfoActivity.this.getObbDir().getAbsolutePath(),
                                "pic1" + new Date().getTime() + ".jpeg");

                        if (!file2.exists()) {
                            // 判断父文件夹是否存在
                            if (!file2.getParentFile().exists()) {
                                file2.getParentFile().mkdirs();
                            }
                        }
                    }else if (number == 3){
                        file3 = new File(FireMissionInfoActivity.this.getObbDir().getAbsolutePath(),
                                "pic1" + new Date().getTime() + ".jpeg");

                        if (!file3.exists()) {
                            // 判断父文件夹是否存在
                            if (!file3.getParentFile().exists()) {
                                file3.getParentFile().mkdirs();
                            }
                        }
                    }

                } else {
                    Toast.makeText(FireMissionInfoActivity.this, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                if(number == 1){
                    output = new FileOutputStream(file1);
                }else if(number == 2){
                    output = new FileOutputStream(file2);
                }else if(number == 3){
                    output = new FileOutputStream(file3);
                }
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                Log.e(TAG, "doInBackground1: e.toString() " + e.toString() );
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                //Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
            } else {
                //加载图片
                if(number == 1){
                    Log.e(TAG, "onPostExecute: isLeft " + file1.getPath() );
                    Glide.with(FireMissionInfoActivity.this).load(file1)
                            .error(R.drawable.ic_no_pic)
                            .placeholder(R.drawable.ic_jaizai).into(imageOneView);
                }else if(number == 2){
                    Log.e(TAG, "onPostExecute: isRight " + file2.getPath() );
                    Glide.with(FireMissionInfoActivity.this).load(file2)
                            .error(R.drawable.ic_no_pic)
                            .placeholder(R.drawable.ic_jaizai).into(imageTwoView);
                }else if(number == 3){
                    Log.e(TAG, "onPostExecute: isRight " + file3.getPath() );
                    Glide.with(FireMissionInfoActivity.this).load(file3)
                            .error(R.drawable.ic_no_pic)
                            .placeholder(R.drawable.ic_jaizai).into(imageThreeView);
                }
            }

        }
    }
    @Override
    public void onBackPressed() {
        if (isChange){
            setResult(FireMissionListActivity.ORDER_CHANGE);
            finish();
        }else {

            finish();
        }

    }
    
    /**
     * 获取当前位置经纬度
     *
     * @return
     */
    @JavascriptInterface
    public String getLocation() {
        //获得位置服务
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(this, "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
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
            Toast.makeText(this, "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();

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
                return location.getLongitude() + "," + location.getLatitude();
            } catch (Exception e) {
                return "0.00,0.00";
            }

        }
        return null;
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
            Toast.makeText(this, "未开启本应用地理位置信息，请先开启！", Toast.LENGTH_SHORT).show();
        }
        return null;
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
