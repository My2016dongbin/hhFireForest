package com.haohai.platform.fireforestplatform;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Trace;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.ui.LocationService;
//import com.haohai.platform.fireforestplatform.ui.utils.AuthTypeUtil;
import com.haohai.platform.fireforestplatform.ui.model.PostModel;
import com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity;
import com.haohai.platform.firelibrary.ui.service.MQTTService;
import com.haohai.platform.mapmodel.fragment.MapFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoIOSFragment;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruyiruyi.rylibrary.bus.DoUpdate;
import com.ruyiruyi.rylibrary.bus.MainRefreshModel;
import com.ruyiruyi.rylibrary.bus.MapDialogDismiss;
import com.ruyiruyi.rylibrary.bus.RefreshModel;
import com.ruyiruyi.rylibrary.bus.VideoPause;
import com.ruyiruyi.rylibrary.bus.VideoResume;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.haohai.platform.platformmodel.ui.fragment.AppsFragment;
import com.haohai.platform.platformmodel.ui.fragment.MyFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoNewFragment;
import com.haohai.platform.platformmodel.ui.service.DataService;
import com.haohai.platform.platformmodel.ui.service.TrackService;
import com.ruyiruyi.rylibrary.base.BaseFragmentActivity;
import com.ruyiruyi.rylibrary.cell.HomeTabsCell;
import com.ruyiruyi.rylibrary.cell.NoCanSlideViewPager;
import com.ruyiruyi.rylibrary.cell.downcell.CommonProgressDialog;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.ui.adapter.FragmentViewPagerAdapter;
import com.ruyiruyi.rylibrary.utils.AndroidUtilities;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.LayoutHelper;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import rxhttp.wrapper.entity.Progress;
import rxhttp.wrapper.param.RxHttp;

/*@Route(path = RouteUtils.LoginToMain)*/
public class MainActivity extends BaseFragmentActivity {

/*    @Autowired
    int state;*/
    private static final String TAG = MainActivity.class.getSimpleName();
    private FrameLayout content;
    private NoCanSlideViewPager viewPager;
    private HomeTabsCell tabsCell;
    private List<String> titles;
    private HomePagerAdapeter pagerAdapter;
    private static boolean isExit = false;
    private long time = 0;
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EXIT:
                    isExit = false;
                    break;
            }

        }
    };
    private static final int EXIT = 1;
    private OutLoginMainBroad outLoginMainBroad;
    private String versionCode;
    private String versionService;
    private CommonProgressDialog mBar;
    private static final String DOWNLOAD_NAME = "dapingtai_";
    public boolean isGengxin = false;
    private Uri tempUri;
    private User user;
    private FireWeixingReceiver fireWeixingReceiver;
    private Intent mForegroundService;
    private ChangeTabReceiver changeTabReceiver;

    private SharedPreferences sp;
    private boolean isFirst = false;
    private ProgressDialog progressDialog;


    ///版本更新bus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(DoUpdate doUpdate) {
        if (user.getIsLogin() == 1) {
            //版本更新
            getVersion();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        android.os.Debug.startMethodTracing();
        setContentView(R.layout.activity_main);
       // permissionRequest(mExternalStoragePermissions);
        progressDialog = new ProgressDialog(this);
        sp = this.getSharedPreferences("data", 0);
        String spString = sp.getString("grid", "");
        Log.e(TAG, "onCreate: qcDancer" + spString );
        if(spString == null || Objects.equals(spString, "")){
            isFirst = true;
            showDialogProgress(progressDialog,"首次进入初始化中请稍候...");
            postPermissions();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("grid", "complete");
                    editor.apply();
                    progressDialog.dismiss();

                    initPager();
                }
            },14000);
        }else{
            isFirst = false;
        }
        Trace.beginSection("zhazha");


/*
        if (!whitelistUtil.isIgnoringBatteryOptimizations(this)){
            whitelistUtil.requestIgnoreBatteryOptimizations(this);
        }
*/


        //后台保活
    //    TraceServiceImpl.sShouldStopService=false;
   //     DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
      //  ARouter.getInstance().inject(this);
        //开启MQTT服务
//        startService(new Intent(getApplicationContext(), MQTTService.class));
        //开启轨迹服务
        startService(new Intent(getApplicationContext(), TrackService.class));
        //获取人员组织数据
        startService(new Intent(getApplicationContext(), DataService.class));
        //开启百度定位服务
        startService(new Intent(getApplicationContext(), LocationService.class));
        //启动前台服务
       /* if (!ForegroundService.serviceIsLive) {
            // Android 8.0使用startForegroundService在前台启动新服务
            mForegroundService = new Intent(this, ForegroundService.class);
            mForegroundService.putExtra("Foreground", "This is a foreground service.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(mForegroundService);
            } else {
                startService(mForegroundService);
            }
        } else {
            Toast.makeText(this, "前台服务正在运行中...", Toast.LENGTH_SHORT).show();
        }*/
        //保持屏幕常亮
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  清除屏幕常亮

        //  Log.e(TAG, "onCreate: " +  state);
        Log.e(TAG, "onCreate: " +  new DbConfig(this).getUser().getUserName());
        Log.e(TAG, "onCreate: " +  new DbConfig(this).getUser().getToken());

        initPager();


        user = new DbConfig(this).getUser();
        if (user.getIsLogin() == 1) {
            //版本更新
            getVersion();
        }

        /**
         * 广播注册
         */
        //实例化IntentFilter对象(腾讯推送火警点击)
        IntentFilter fireFilter = new IntentFilter();
        fireFilter.addAction("fire_weixing_tengxun");
        fireWeixingReceiver = new FireWeixingReceiver();
        registerReceiver(fireWeixingReceiver,fireFilter);
        //视频监控点广播
        changeTabReceiver=new ChangeTabReceiver();
        IntentFilter resourcefilter = new IntentFilter();
        resourcefilter.addAction("video_play");
        registerReceiver(changeTabReceiver, resourcefilter);

        //配置点击查看大图
        initImageLoader();
        Trace.endSection();
        android.os.Debug.stopMethodTracing();
    }

    /**
     * 获取按钮权限
     */
    private void postPermissions() {
        DbConfig dbConfig = new DbConfig(this);
        dbManager = dbConfig.getDbManager();
        userPermission = dbConfig.getUser();
        userPermission.setPermission("");
        permissionCount = 0;
        String[] menuIdList = {"app-map","app-video","app-application","app-setting"};//获取当前用户菜单：/auth/api/auth/auth/list/menu/by/user
        for (int i = 0; i < menuIdList.length; i++) {
            postPer(menuIdList[i]);
        }
        postMainPer();
    }


    private DbManager dbManager;
    private User userPermission;
    private int permissionCount;
    private void postPer(String id) {
        RequestParams params = new RequestParams("http://218.201.136.183:18001/" + "auth/api/auth/auth/list/element/from/menu");
        params.addParameter("menuCode",id);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        Log.e(TAG, "postPermissions: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: permissions" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray dataList = object.getJSONArray("data");
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject o = (JSONObject) dataList.get(i);
                        String code = o.getString("elementCode");
                        userPermission.addPermission(code+"_");
                    }
                    permissionCount++;
                    if(permissionCount == 4){
                        try {
                            Log.e(TAG, "onSuccess: permissions ==>" + userPermission.getPermission() );
                            dbManager.delete(User.class);
                            dbManager.saveOrUpdate(userPermission);

                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private void postMainPer() {
        RequestParams params = new RequestParams("http://218.201.136.183:18001/" + "auth/api/auth/auth/user/auth");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        Log.e(TAG, "postPermissions: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: permissions postMainPer" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    JSONObject obj = (JSONObject) data.get(0);
                    JSONArray dataList = obj.getJSONArray("menuDTOS");
                    CommonData.hasMainMap = false;
                    CommonData.hasMainVideo = false;
                    CommonData.hasMainApp = false;
                    CommonData.hasMainMy = false;
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject o = (JSONObject) dataList.get(i);
                        String menuCode = o.getString("menuCode");
                        if(Objects.equals(menuCode, "app-map")){
                            CommonData.hasMainMap = true;
                        }
                        if(Objects.equals(menuCode, "app-video")){
                            CommonData.hasMainVideo = true;
                        }
                        if(Objects.equals(menuCode, "app-application")){
                            CommonData.hasMainApp = true;
                        }
                        if(Objects.equals(menuCode, "app-setting")){
                            CommonData.hasMainMy = true;
                        }
                    }
                    if(!CommonData.hasMainMap && !CommonData.hasMainVideo && !CommonData.hasMainApp && !CommonData.hasMainMy){
                        CommonData.hasMainMy = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonData.hasMainMap = true;
                    CommonData.hasMainVideo = true;
                    CommonData.hasMainApp = true;
                    CommonData.hasMainMy = true;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MainRefreshModel message) {
        Log.e(TAG, "onGetMessage: onGetMessage main" );
        //EventBus.getDefault().post(MapDialogDismiss.getInstance());
        initPager();
    }

    private void initPager() {
        content = new FrameLayout(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));


        viewPager = new NoCanSlideViewPager(this);

        content.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT)));

        tabsCell = new HomeTabsCell(this);
        content.addView(tabsCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT), Gravity.BOTTOM));
        tabsCell.setViewPager(viewPager);

        initTitle();
        pagerAdapter = new HomePagerAdapeter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);


        //设置不能左右滑动
        viewPager.setNoScroll(true);
        //设置不显示滑动动画
        viewPager.setScrollAnim(false);
        viewPager.setCurrentItem(0);
        tabsCell.setSelected(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position!=1){
                    EventBus.getDefault().post(new VideoPause());
                }else{
                    EventBus.getDefault().post(new VideoResume());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(changeTabReceiver);
        unregisterReceiver(fireWeixingReceiver);
    }
    class HomePagerAdapeter extends FragmentViewPagerAdapter {

        private final List<String> mPageTitle = new ArrayList<>();

        public HomePagerAdapeter(FragmentManager fragmentManager, List<String> pageTitles, List<Fragment> fragments) {
            super(fragmentManager, fragments);
            mPageTitle.clear();
            mPageTitle.addAll(pageTitles);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitle.get(position);
        }
    }

    private void initTitle() {




      //  tabsCell.addView(R.drawable.ic_mine, R.drawable.ic_mine_selected, "我的 ");

    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();

        //fragments.add(new MapHomeFragment());
        if(CommonData.hasMainMap){
            tabsCell.addView(R.drawable.ic_mode_map, R.drawable.ic_mode_map_selected, "地图");
            fragments.add(new MapFragment());
        }
        if(CommonData.hasMainVideo){
            tabsCell.addView(R.drawable.ic_jiankong, R.drawable.ic_jiankong_selected, "视频监控");
            fragments.add(new VideoIOSFragment());
        }
        if(CommonData.hasMainApp){
            tabsCell.addView(R.drawable.ic_yingyong, R.drawable.ic_yingyong_selected, "应用");
            fragments.add(new AppsFragment());
        }
        if(CommonData.hasMainMy){
            tabsCell.addView(R.drawable.ic_xiaoxi, R.drawable.ic_xiaoxi_selected, "我的");
            fragments.add(new MyFragment() );
        }
        //fragments.add(new VideoFragment());
        return fragments;
    }

    protected List<String> initPagerTitle() {
        titles = new ArrayList<>();
        if(CommonData.hasMainMap){
            titles.add("地图");
        }
        if(CommonData.hasMainVideo){
            titles.add("视频监控");
        }
        if(CommonData.hasMainApp){
            titles.add("应用");
        }
        if(CommonData.hasMainMy){
            titles.add("我的");
        }
        return titles;
    }
    @Override
    public void onBackPressed() {

        exit();

    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次回到主页",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(EXIT, 2000);
        } else {
            //VSGService. getInstance().logout (MainActivity.this, null);

            Intent intent = new Intent("haohai.haohai.baseActivity");       //关闭程序
            intent.putExtra("closeAll", 1);
            sendBroadcast(intent);//发送广播

            this.finish();
            //  System.exit(0);
        }
    }


    //定义一个广播
    public class OutLoginMainBroad extends BroadcastReceiver {

        public void onReceive(Context arg0, Intent intent) {

            finish();

        }
    }


    /**
     * 版本更新
     */
    private void getVersion() {
        try {
            versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode + "";
            Log.e(TAG, "getVersion: versionCode -- " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {

        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_QUANXIAN +"api/androidUpgrade/getCurrent");
        Log.e(TAG, "version: " + params);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        //   params.addBodyParameter("reqJson", jsonObject.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:version-- " + result);
                JSONObject jsonObject1 = null;
                try {
                    jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")) {
                        JSONObject object = jsonObject1.getJSONArray("data").getJSONObject(0);
                        versionService = object.getString("version");
                        String versionDescription = object.getString("versionDescription");
                        String apkUrl = object.getString("apkUrl");
                        String isForce = object.getString("isForce");
                        Log.e(TAG, "onSuccess:version--1 ");
                        Log.e(TAG, "onSuccess:version--versionCode " + versionCode);
                        Log.e(TAG, "onSuccess:version--versionService " + versionService);
                        // ShowDialog(versionService, apkUrl, versionDescription, isForce);
                        if (Integer.parseInt(versionCode) < Integer.parseInt(versionService)) {
                            Log.e(TAG, "onSuccess:version--2 ");
                            ShowDialog(versionService, apkUrl, versionDescription, isForce);
                        }

                    }

                } catch (JSONException e) {

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

            }
        });
    }

    /**
     * 用户更新dialog
     *
     * @param version
     * @param downloadUrl
     * @param versionDesc
     * @param isMustUpgrade
     */
    private void ShowDialog(String version, final String downloadUrl, String versionDesc, String isMustUpgrade) {
        if (isMustUpgrade.equals("0")) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("版本更新")
                    .setMessage(versionDesc)
                    .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            mBar = new CommonProgressDialog(MainActivity.this);
                            mBar.setCanceledOnTouchOutside(false);
                            mBar.setTitle("正在下载");
                            mBar.setCustomTitle(LayoutInflater.from(
                                    MainActivity.this).inflate(
                                    R.layout.title_dialog, null));
                            mBar.setMessage("正在下载");
                            mBar.setIndeterminate(true);
                            mBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mBar.setCancelable(false);
                            /*final DownloadTask downloadTask = new DownloadTask(
                                    MainActivity.this);
                            downloadTask.execute(downloadUrl);*/
                            mBar.show();
                            downApk(downloadUrl);
                            mBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    /*downloadTask.cancel(true);*/
                                }
                            });
                        }
                    }).show();
        } else {
            Log.e(TAG, "onSuccess:version--4 ");
            new android.app.AlertDialog.Builder(this)
                    .setTitle("版本更新")
                    .setMessage(versionDesc)
                    .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            mBar = new CommonProgressDialog(MainActivity.this);
                            mBar.setCanceledOnTouchOutside(false);
                            mBar.setTitle("正在下载");
                            mBar.setCustomTitle(LayoutInflater.from(
                                    MainActivity.this).inflate(
                                    R.layout.title_dialog, null));
                            mBar.setMessage("正在下载");
                            mBar.setIndeterminate(true);
                            mBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mBar.setCancelable(false);
                            /*final DownloadTask downloadTask = new DownloadTask(
                                    MainActivity.this);
                            downloadTask.execute(downloadUrl);*/
                            mBar.show();
                            downApk(downloadUrl);
                            mBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    /*downloadTask.cancel(true);*/
                                }
                            });
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * 下载应用
     *
     * @author Administrator
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(MainActivity.this.getObbDir().getAbsolutePath(),
                            DOWNLOAD_NAME + versionService + ".apk");

                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
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
            mBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mBar.setIndeterminate(false);
            mBar.setMax(100);
            mBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mBar.dismiss();
            if (result != null) {
                Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
                Log.e(TAG, "onPostExecute: result = " + result);
            } else {
                isGengxin = true;
                update();
            }

        }
    }
    String filePath;
    int currentProgress = 0;
    int lastProgress = 0;
    long currentSize = 0;
    long totalSize = 0;
    void downApk(String url){
        //文件存储路径
        String destPath = getExternalCacheDir() + "/" + System.currentTimeMillis() + ".apk";
        RxHttp.get(url)
                .downloadProgress(destPath,currentSize)
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(60000, TimeUnit.MILLISECONDS)
                .doOnNext(new Consumer<Progress<String>>() {
                    @Override
                    public void accept(Progress<String> progress) throws Exception {
                        if(lastProgress==0){
                            //下载进度回调,0-100，仅在进度有更新时才会回调，最多回调101次，最后一次回调文件存储路径
                            currentProgress = progress.getProgress(); //当前进度 0-100
                        }else{
                            //下载进度回调,0-100，仅在进度有更新时才会回调，最多回调101次，最后一次回调文件存储路径
                            currentProgress = (int) (lastProgress + (progress.getProgress()*0.01)*(100-lastProgress)/1); //当前进度 0-100
                        }
                        currentSize = progress.getCurrentSize(); //当前已下载的字节大小
                        totalSize = progress.getTotalSize();     //要下载的总字节大小
                        filePath = progress.getResult(); //文件存储路径，最后一次回调才有内容
                        mBar.setIndeterminate(false);
                        mBar.setMax(100);
                        mBar.setProgress(currentProgress);
                    }
                })
                .filter(Progress::isCompleted)//下载完成，才继续往下走
                .map(Progress::getResult) //到这，说明下载完成，返回下载目标路径
                .as(RxLife.as(this)) //感知生命周期
                .subscribe(s -> {//s为String类型，这里为文件存储路径
                    mBar.dismiss();
                    //下载完成，处理相关逻辑
                    isGengxin = true;
                    update();
                }, throwable -> {
                    //mBar.dismiss();
                    lastProgress = currentProgress;
                    downApk(url);
                    //下载失败，处理相关逻辑
                    //Toast.makeText(MainActivity.this, "网络异常请稍后重试", Toast.LENGTH_SHORT).show();
                });

        /*long length = new File(destPath).length(); //已下载的文件长度
        RxHttp.get(url)
                .setRangeHeader(length)  //设置开始下载位置，结束位置默认为文件末尾
                .asDownload(destPath, length, progress -> {
                    //下载进度回调,0-100，仅在进度有更新时才会回调
                    int currentProgress = progress.getProgress(); //当前进度 0-100
                    long currentSize = progress.getCurrentSize(); //当前已下载的字节大小
                    long totalSize = progress.getTotalSize();     //要下载的总字节大小
                }, AndroidSchedulers.mainThread()) //指定主线程回调
                .as(RxLife.as(this)) //加入感知生命周期的观察者
                .subscribe(s -> { //s为String类型
                    //下载成功，处理相关逻辑
                }, throwable -> {
                    //下载失败，处理相关逻辑
                });*/

    }

    private void update() {
        isGengxin = false;
        //安装应用

        String fileName = MainActivity.this.getObbDir().getAbsolutePath() + "/" + DOWNLOAD_NAME + versionService + ".apk";

        if (Build.VERSION.SDK_INT >= 24) {
            File file = new File(filePath);
            tempUri = FileProvider.getUriForFile(MainActivity.this, "com.haohai.platform.fireforestplatform", file);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(tempUri, "application/vnd.android.package-archive");
            startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);
        }


    }

    /**
     * 卫星推送消息点击广播
     */
    class FireWeixingReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if (type.equals("4")){
                viewPager.setCurrentItem(0);
                tabsCell.setSelected(0);

            }else {
                startActivity(new Intent(getApplicationContext(), FireMissionListActivity.class));
            }
        }
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }
    class ChangeTabReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            String msg = intent.getStringExtra("id");
            //Toast.makeText(context, "广播已经接收", Toast.LENGTH_SHORT).show();
            Log.i("onReceive: ", msg);
            viewPager.setCurrentItem(1);
            tabsCell.setSelected(1);
        }
    }
}
