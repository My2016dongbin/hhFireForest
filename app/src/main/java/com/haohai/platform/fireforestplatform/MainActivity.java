package com.haohai.platform.fireforestplatform;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Trace;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.ui.LocationService;
import com.haohai.platform.fireforestplatform.ui.acticity.LauncherActivity;
import com.haohai.platform.fireforestplatform.ui.service.AlarmPointService;
//import com.haohai.platform.fireforestplatform.ui.utils.AuthTypeUtil;
import com.haohai.platform.fireforestplatform.ui.utils.TraceServiceImpl;
import com.haohai.platform.fireforestplatform.ui.utils.whitelistUtil;
import com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity;
import com.haohai.platform.firelibrary.ui.service.MQTTService;
import com.haohai.platform.mapmodel.fragment.MapNewFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoNewFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoTSFragment;
import com.haohai.platform.platformmodel.ui.service.ForegroundService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruyiruyi.rylibrary.bus.MapLine;
import com.ruyiruyi.rylibrary.bus.OutLogin;
import com.ruyiruyi.rylibrary.db.Area;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.haohai.platform.platformmodel.ui.fragment.AppsFragment;
import com.haohai.platform.platformmodel.ui.fragment.MyFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoIOSFragment;
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
import com.tencent.android.tpns.mqtt.util.Debug;
import com.tencent.android.tpush.XGPushManager;
//import com.vsg.trustaccess.sdks.VSGService;
//import com.vsg.trustaccess.sdks.logic.AuthStateManager;
//import com.vsg.trustaccess.sdks.logic.CharonVpnService;
//import com.vsg.trustaccess.sdks.logic.TunnelStateManager;
import com.xdandroid.hellodaemon.DaemonEnv;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
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

import static android.app.Notification.FLAG_NO_CLEAR;
import static android.support.v4.app.NotificationCompat.FLAG_ONGOING_EVENT;

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


    /**
     * 获取区域数据
     */
    private void getAreaFromService() {
        JSONObject jsonObject = new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL +  "auth/api/sysArea/getAllSysArea");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.i(TAG, "getAreaFromService: "+params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:diqu -- " + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    List<Area> allAreaList = new ArrayList();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        String id = object.getString("id");
                        String name = object.getString("name");
                        String parentId = object.getString("parentId");
                        String level = object.getString("level");
                        String createTime = object.getString("createTime");
                        Area area = new Area(id, name, parentId, createTime, level);
                        allAreaList.add(area);
                    }
                    DbConfig dbConfig = new DbConfig(getApplicationContext());
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.saveOrUpdate(allAreaList);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new MapLine());
                            }
                        },2000);
                    } catch (DbException e) {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        getAreaFromService();
        android.os.Debug.startMethodTracing();
        setContentView(R.layout.activity_main);
        User user = new DbConfig(this).getUser();
        String permission = user.getPermission();
        String[] strings = permission.split(",");
        for (int i = 0; i < strings.length; i++) {
            LogUtil.e("permission = " + strings[i] );
        }

        //开启轨迹服务
        startService(new Intent(getApplicationContext(), TrackService.class));
        //获取人员组织数据
        startService(new Intent(getApplicationContext(), DataService.class));
        //开启百度定位服务
        startService(new Intent(getApplicationContext(), LocationService.class));

        //保持屏幕常亮
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  清除屏幕常亮

        //  Log.e(TAG, "onCreate: " +  state);
        Log.e(TAG, "onCreate: " +  new DbConfig(this).getUser().getUserName());
        Log.e(TAG, "onCreate: " +  new DbConfig(this).getUser().getToken());

        content = new FrameLayout(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(content, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));


        viewPager = new NoCanSlideViewPager(this);

        content.addView(viewPager, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP, 0, 0, 0, 0));

        tabsCell = new HomeTabsCell(this);
        //content.addView(tabsCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(HomeTabsCell.CELL_HEIGHT), Gravity.BOTTOM));
        tabsCell.setViewPager(viewPager);

        initTitle();
        pagerAdapter = new HomePagerAdapeter(getSupportFragmentManager(), initPagerTitle(), initFragment());
        viewPager.setAdapter(pagerAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

               /* if (position == 0){
                    pagerAdapter.notifyDataSetChanged();
                }*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置不能左右滑动
        viewPager.setNoScroll(true);
        //设置不显示滑动动画
        viewPager.setScrollAnim(false);
        viewPager.setCurrentItem(0);
        tabsCell.setSelected(0);


        this.user = new DbConfig(this).getUser();
        if (this.user.getIsLogin() == 1) {
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
        NotificationManagerCompat notification = NotificationManagerCompat.from(this);
        boolean isEnabled = notification.areNotificationsEnabled();
        if (!isEnabled) {
            //未打开通知
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("请在“通知”中打开通知权限")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("android.provider.extra.APP_PACKAGE", MainActivity.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", MainActivity.this.getPackageName());
                                intent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                                startActivity(intent);
                            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {  //4.4
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                            } else if (Build.VERSION.SDK_INT >= 15) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                            }
                            startActivity(intent);

                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
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
//        if(CommonData.hasMainMap) {
            tabsCell.addView(R.drawable.ic_mode_map, R.drawable.ic_mode_map_selected, "地图");
//        }
        if(CommonData.hasMainVideo) {
            tabsCell.addView(R.drawable.ic_jiankong, R.drawable.ic_jiankong_selected, "视频监控");
        }
        if(CommonData.hasMainApp) {
            tabsCell.addView(R.drawable.ic_yingyong, R.drawable.ic_yingyong_selected, "应用");
        }
        if(CommonData.hasMainMy) {
            tabsCell.addView(R.drawable.ic_xiaoxi, R.drawable.ic_xiaoxi_selected, "我的");
        }
      //  tabsCell.addView(R.drawable.ic_mine, R.drawable.ic_mine_selected, "我的 ");

    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();

//        if(CommonData.hasMainMap){
            fragments.add(new MapNewFragment());
//        }
        if(CommonData.hasMainVideo) {
            fragments.add(new VideoTSFragment());
        }
        if(CommonData.hasMainApp) {
            fragments.add(new AppsFragment());
        }
        if(CommonData.hasMainMy) {
            fragments.add(new MyFragment());
        }

        return fragments;
    }

    protected List<String> initPagerTitle() {
        titles = new ArrayList<>();
//        if(CommonData.hasMainMap){
            titles.add("地图");
//        }
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

    //退出登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(OutLogin event) {
        finish();
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
        RequestParams params = new RequestParams(RequestUtils.REQUEST_QUANXIAN +"api/fushanAndroidUpgrade/getCurrent");
        Log.e(TAG, "version: " + params);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
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
                            // downFile(URLData.DOWNLOAD_URL);
                            final DownloadTask downloadTask = new DownloadTask(
                                    MainActivity.this);
                            downloadTask.execute(downloadUrl);
                            mBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    downloadTask.cancel(true);
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
                            // downFile(URLData.DOWNLOAD_URL);
                            final DownloadTask downloadTask = new DownloadTask(
                                    MainActivity.this);
                            downloadTask.execute(downloadUrl);
                            mBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    downloadTask.cancel(true);
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

//                // 申请多个权限。大神的界面
//                AndPermission.with(MainActivity.this)
//                        .requestCode(REQUEST_CODE_PERMISSION_OTHER)
//                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
//                        .rationale(new RationaleListener() {
//                                       @Override
//                                       public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
//                                           // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
//                                           AndPermission.rationaleDialog(MainActivity.this, rationale).show();
//                                       }
//                                   }
//                        )
//                        .send();
                // 申请多个权限。
               /* AndPermission.with(MainActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_SD)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(rationaleListener
                        )
                        .send();*/


                Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(context, "File downloaded",
                // Toast.LENGTH_SHORT)
                // .show();
                isGengxin = true;
                if (Build.VERSION.SDK_INT >= 26) {
                    update();
                   /* boolean b = getPackageManager().canRequestPackageInstalls();
                    if (b) {
                        update();
                    } else {
                        //请求安装未知应用来源的权限
                        //  ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10086);
                        //   Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,Uri.fromParts("package:"+ getPackageName()));
                        //  startActivityForResult(intent, 10086);

                        ActivityCompat.requestPermissions(TEMainActivity.this, new String[]{android.Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10086);
                    }*/
                } else {
                    update();
                }

            }

        }
    }

    private void update() {
        isGengxin = false;
        //安装应用
        //  Intent intent = new Intent(Intent.ACTION_VIEW);


        String fileName = MainActivity.this.getObbDir().getAbsolutePath() + "/" + DOWNLOAD_NAME + versionService + ".apk";
      /*  File file = null;
        file = new File(fileName);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= 24) {
            tempUri = FileProvider.getUriForFile(MainActivity.this, "com.hht.hsatellitemobile.fileProvider", file);
        } else {
            tempUri = Uri.fromFile(new File(this.getObbDir().getAbsolutePath(), DOWNLOAD_NAME + version + ".apk"));
        }

        Log.e(TAG, "update: ----" + tempUri);
        intent.setDataAndType(tempUri,
                "application/vnd.android.package-archive");
        startActivity(intent);*/
        if (Build.VERSION.SDK_INT >= 24) {
            File file = new File(fileName);
            tempUri = FileProvider.getUriForFile(MainActivity.this, "com.haohai.platform.fireforestplatform.fileProvider", file);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(tempUri, "application/vnd.android.package-archive");
            startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
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
