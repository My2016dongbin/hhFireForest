package com.haohai.platform.fireforestplatform;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.lechange.opensdk.api.InitParams;
import com.lechange.opensdk.api.LCOpenSDK_Api;
import com.ruyiruyi.rylibrary.bus.DoUpdate;
import com.ruyiruyi.rylibrary.bus.VideoPause;
import com.ruyiruyi.rylibrary.bus.VideoStart;
import com.ruyiruyi.rylibrary.utils.CommonUtils;
import com.ruyiruyi.rylibrary.utils.DahuaTokenInfoDemo;
import com.ruyiruyi.rylibrary.utils.model.DahuaSubDeviceList;
import com.ruyiruyi.rylibrary.utils.model.DahuaSubToken;
import com.ruyiruyi.rylibrary.utils.model.DahuaToken;
import com.haohai.platform.fireforestplatform.ui.model.OfflineLocation;
import com.haohai.platform.fireforestplatform.ui.receiver.NetworkChangeReceiver;
import com.haohai.platform.fireforestplatform.ui.service.VpnService;
import com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity;
import com.haohai.platform.mapmodel.fragment.MapNewFragment;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruyiruyi.rylibrary.bus.BeidouInfoRefresh;
import com.ruyiruyi.rylibrary.bus.BeidouRefresh;
import com.ruyiruyi.rylibrary.db.BeiDouLocation;
import com.ruyiruyi.rylibrary.db.BeidouNews;
import com.ruyiruyi.rylibrary.db.BeidouPerson;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.haohai.platform.platformmodel.ui.fragment.AppsFragment;
import com.haohai.platform.platformmodel.ui.fragment.MyFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoNewFragment;
import com.haohai.platform.platformmodel.ui.service.DataService;
import com.haohai.platform.fireforestplatform.ui.service.TrackService;
import com.ruyiruyi.rylibrary.base.BaseFragmentActivity;
import com.ruyiruyi.rylibrary.cell.HomeTabsCell;
import com.ruyiruyi.rylibrary.cell.NoCanSlideViewPager;
import com.ruyiruyi.rylibrary.cell.downcell.CommonProgressDialog;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.ui.adapter.FragmentViewPagerAdapter;
import com.ruyiruyi.rylibrary.utils.AndroidUtilities;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.LayoutHelper;
import com.ruyiruyi.rylibrary.utils.model.DahuaTokenInfo;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.vsg.trustaccess.sdks.logic.AuthStateManager;
import com.vsg.trustaccess.sdks.logic.TunnelStateManager;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends BaseFragmentActivity implements TunnelStateManager.TunnelTotalStateListener, NetworkChangeReceiver.NetStateChangeObserver {

    ///北斗通讯
    private static boolean enable = false;
    private static final String ACTION_MSG_BD_MSG_RECEIVED = "android.intent.action.beidou.msg.received";
    private static final String ACTION_MSG_BD_IC_INFO_RECEIVED = "android.intent.action.beidou.msg.bd.info.received";
    private static final String ACTION_MSG_BD_MSG_RESULT = "android.intent.action.beidou.msg.result";
    private static final String ACTION_MSG_BD_FKXX_RECEIVED = "android.intent.action.beidou.feedbackinfo.received";
    private static final String ACTION_MSG_BD_DWXX_RECEIVED = "android.intent.action.beidou.msg.dwxx.received";
    private static final String ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED = "android.intent.action.beidou.bdmsg.enable_state.received";
    private static final String ACTION_MSG_BD_BDPROTOCOL_VERSION_SET = "android.intent.action.beidou.msg.bdprotocol.version.set";

    private static final String ACTION_MSG_BD_GLXX_INFO_RECEIVED = "android.intent.action.beidou.msg.glxxInfo.received";
    private static final String ACTION_MSG_BD_ZBSC_INFO_RECEIVED = "android.intent.action.beidou.msg.zbscInfo.received";
    private static final String ACTION_MSG_BD_SJXX_INFO_RECEIVED = "android.intent.action.beidou.msg.sjxxInfo.received";
    private static final String ACTION_MSG_BD_XHXX_INFO_RECEIVED = "android.intent.action.beidou.msg.xhxxInfo.received";
    private static final String ACTION_MSG_BD_ZJXX_INFO_RECEIVED = "android.intent.action.beidou.msg.zjxxInfo.received";

    /*system receive part */
    private static final String ACTION_MSG_BD_MSG_SEND = "android.intent.action.beidou.msg.send";
    private static final String ACTION_MSG_BD_NUMBER_REQUEST = "android.intent.action.beidou.msg.number.request";
    private static final String ACTION_MSG_BD_POWER_INFO_REQUEST = "android.intent.action.beidou.msg.bd.info.request_bd_power";
    private static final String ACTION_MSG_BD_DWSQ_REQUEST = "android.intent.action.beidou.msg.dwsq.request";
    private static final String ACTION_MSG_BD_BDMSG_ENABLE_SATE_REQUEST = "android.intent.action.beidou.msg.bdmsg.enable_state.request";

    private static final String ACTION_MSG_BD_GXZR_REQUEST = "android.intent.action.beidou.msg.gxzr.request";
    private static final String ACTION_MSG_BD_GXDQ_REQUEST = "android.intent.action.beidou.msg.gxdq.request";
    private static final String ACTION_MSG_BD_ZBZH_REQUEST = "android.intent.action.beidou.msg.zbzh.request";
    private static final String ACTION_MSG_BD_XGXL_REQUEST = "android.intent.action.beidou.msg.xgxl.request";
    private static final String ACTION_MSG_BD_SJSC_REQUEST = "android.intent.action.beidou.msg.sjsc.request";
    private static final String ACTION_MSG_BD_XTZJ_REQUEST = "android.intent.action.beidou.msg.xtzj.request";
    private static final String ACTION_MSG_BD_MSG_ENABLE_REQUEST = "android.intent.action.beidou.msg.enable.request";
    private static final String ACTION_MSG_BD_PASS_THROUGH_MSG_REQUEST = "android.intent.action.beidou.msg.passthroughmsg.request";

    private static final String BD_MSG_ENABLE = "bd_msg_enable";


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
    private TunnelStateManager mTunnelStateManager = null;
    private Intent serviceIntent;

    private boolean hasTuisong = false;
    private AuthStateManager mStateManager;

    private  final int PREPARE_VPN_SERVICE = 0;
    private String tuisongId;
    private String tuisongTime;
    private String tuisongType;
    private boolean fromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent();
        serviceIntent.setClass(this,VpnService.class);
        //开启VPN服务
        startService(serviceIntent);

        //网络状态
        NetworkChangeReceiver.registerReceiver(this);
        NetworkChangeReceiver.registerObserver(this);
        EventBus.getDefault().register(this);

        ///初始化北斗通信
        //initBeidouSDK();

        //打开北斗
        setBDMessageEnable(true);

        //注册北斗消息接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MSG_BD_MSG_RECEIVED);
        filter.addAction(ACTION_MSG_BD_IC_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_MSG_RESULT);
        filter.addAction(ACTION_MSG_BD_FKXX_RECEIVED);
        filter.addAction(ACTION_MSG_BD_DWXX_RECEIVED);
        filter.addAction(ACTION_MSG_BD_GLXX_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_ZBSC_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED);
        filter.addAction(ACTION_MSG_BD_SJXX_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_XHXX_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_ZJXX_INFO_RECEIVED);
        registerReceiver(mBeidouModuleInfoReceiver, filter);

        initBeidouUserList();


        Intent intent = getIntent();
        fromLogin = intent.getBooleanExtra("fromLogin",false);
        hasTuisong = intent.getBooleanExtra("isTuisong",false);
        if (hasTuisong){
            tuisongId = intent.getStringExtra("tuisongId");
            tuisongTime = intent.getStringExtra("tuisongTime");
            tuisongType = intent.getStringExtra("tuisongType");
            if (tuisongType.equals("1")||tuisongType.equals("2")||tuisongType.equals("3")||tuisongType.equals("4")||tuisongType.equals("5")){
                startActivity(new Intent(getApplicationContext(), FireMissionListActivity.class));
            }

        }
        if (fromLogin){
            User user = new DbConfig(this).getUser();
            Set<String> tagSet = new LinkedHashSet<String>();
            tagSet.add(user.getGridNo());
            tagSet.add(user.getId());
            tagSet.add(user.getGroupId());
            XGPushManager.setTags(getApplicationContext(),"setTag",tagSet);
            //开启华为推送
            XGPushConfig.enableOtherPush(getApplicationContext(), true);
            XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
                @Override
                public void onSuccess(Object data, int flag) {
                    //token在设备卸载重装的时候有可能会变
                    Log.d("TPush", "注册成功，设备token为：" + data);
                }

                @Override
                public void onFail(Object data, int errCode, String msg) {
                    Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                }
            });
        }





        android.os.Debug.startMethodTracing();
        setContentView(R.layout.activity_main);
       // permissionRequest(mExternalStoragePermissions);

        Trace.beginSection("zhazha");

        //开启MQTT服务
        //startService(new Intent(getApplicationContext(), MQTTService.class));
        //开启轨迹服务
        startService(new Intent(getApplicationContext(), TrackService.class));
        //获取大华乐橙token（大华卡口对讲功能）
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        getDaHuaToken(paramsMap);
        //获取人员组织数据
        startService(new Intent(getApplicationContext(), DataService.class));
        Log.e(TAG, "onCreate: " +  new DbConfig(this).getUser().getUserName());
        Log.e(TAG, "onCreate: " +  new DbConfig(this).getUser().getToken());

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

        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position!=1){
                    EventBus.getDefault().post(new VideoPause());
                }else{
                    EventBus.getDefault().post(new VideoStart());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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


    ///版本更新bus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(DoUpdate doUpdate) {
        if (user.getIsLogin() == 1) {
            //版本更新
            getVersion();
        }
    }

    /**
     * 获取大华乐橙token(大华卡口对讲功能)
     */
    private void getDaHuaToken(Map<String, Object> paramsMap) {
        DahuaToken dahuaToken = CommonUtils.paramsInit(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn:443/openapi/accessToken");
        params.setBodyContent(gson.toJson(dahuaToken));
        Log.e(TAG, "getDaHuaToken: " + gson.toJson(dahuaToken) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: result " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject jsonObject = object.getJSONObject("result");
                    if(jsonObject.getString("msg")!=null&&jsonObject.getString("msg").contains("操作成功")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        CommonData.daHuaTokenStr = data.getString("accessToken");
                        CommonData.daHuaId = object.getString("id");
                        Log.e(TAG, "onSuccess: CommonData " + CommonData.daHuaTokenStr + "," + CommonData.daHuaId );

                        //initDaHuaApi();

//                        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
//                        paramsMap.put("deviceId","8H03AA1PAGC6665");
//                        paramsMap.put("code","hh123456");
//                        paramsMap.put("token",CommonData.daHuaTokenStr);
//                        testBind(paramsMap);

//                        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
//                        paramsMap.put("deviceId","8H03AA1PAGC6665");
//                        paramsMap.put("token",CommonData.daHuaTokenStr);
//                        testInfoList(paramsMap);

//                        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
//                        paramsMap.put("page",1);
//                        paramsMap.put("pageSize",10);
//                        paramsMap.put("source","bind");
//                        paramsMap.put("token",CommonData.daHuaTokenStr);
//                        testInfo(paramsMap);


                        HashMap<String, Object> paramsMap2 = new HashMap<String, Object>();
                        paramsMap2.put("page",1);
                        paramsMap2.put("pageSize",10);
                        paramsMap2.put("source","bind");
                        paramsMap2.put("token",CommonData.daHuaTokenStr);
                        testSubList(paramsMap2);
                    }

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

    private void initDaHuaApi() {
        //初始化LCOpenSDK_Api
        String token = CommonData.daHuaTokenStr; //开发者自己去平台请求授权token
        String host = "openapi.lechange.cn:443";// 国内平台地址：openapi.lechange.cn:443 海外平台地址：openapi.easy4ip.com:443
        InitParams initParams = new InitParams(this, host, token);
        try {
            int iRet = LCOpenSDK_Api.initOpenApi(initParams);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void testBind(Map<String, Object> paramsMap) {
        DahuaToken dahuaToken = CommonUtils.paramsInit(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn/openapi/bindDevice");
        params.setBodyContent(gson.toJson(dahuaToken));
        Log.e(TAG, "testBind: " + gson.toJson(dahuaToken) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:testBind result " + result );
                try {
                    JSONObject object = new JSONObject(result);

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
    private void testInfoList(Map<String, Object> paramsMap) {
        DahuaTokenInfo dahuaTokenInfo = CommonUtils.paramsInitList(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn/openapi/listDeviceDetailsByIds");
        params.setBodyContent(gson.toJson(dahuaTokenInfo));
        Log.e(TAG, "testInfo: " + gson.toJson(dahuaTokenInfo) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:testInfo result " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject results = object.getJSONObject("result");
                    JSONObject data = results.getJSONObject("data");
                    JSONArray deviceList = data.getJSONArray("deviceList");
                    JSONObject device = (JSONObject) deviceList.get(0);
                    CommonData.device = device;

                } catch (Exception e) {
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
    private void testInfo(Map<String, Object> paramsMap) {
        DahuaTokenInfoDemo dahuaTokenInfo = CommonUtils.paramsInitInfo(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn/openapi/listDeviceDetailsByPage");
        params.setBodyContent(gson.toJson(dahuaTokenInfo));
        Log.e(TAG, "testInfo: " + gson.toJson(dahuaTokenInfo) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:testInfo result " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject results = object.getJSONObject("result");
                    JSONObject data = results.getJSONObject("data");
                    JSONArray deviceList = data.getJSONArray("deviceList");
                    JSONObject device = (JSONObject) deviceList.get(0);
                    CommonData.device = device;

                } catch (Exception e) {
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
    private void testSubList(Map<String, Object> paramsMap) {
        DahuaTokenInfoDemo dahuaTokenInfo = CommonUtils.paramsInitInfo(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn/openapi/listSubAccount");
        params.setBodyContent(gson.toJson(dahuaTokenInfo));
        Log.e(TAG, "testSubList: " + gson.toJson(dahuaTokenInfo) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:testSubList result " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject results = object.getJSONObject("result");
                    JSONObject data = results.getJSONObject("data");
                    JSONArray accounts = data.getJSONArray("accounts");
                    JSONObject account = (JSONObject) accounts.get(3);
                    CommonData.subAccount = account.getString("account");
                    CommonData.subOpenid = account.getString("openid");


                    HashMap<String, Object> paramsMap = new HashMap<String, Object>();
                    paramsMap.put("openid",CommonData.subOpenid);
                    paramsMap.put("token",CommonData.daHuaTokenStr);
                    testSubToken(paramsMap);


                } catch (Exception e) {
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

    private void testSubToken(Map<String, Object> paramsMap) {
        DahuaSubToken dahuaTokenInfo = CommonUtils.paramsSubToken(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn/openapi/subAccountToken");
        params.setBodyContent(gson.toJson(dahuaTokenInfo));
        Log.e(TAG, "testSubToken: " + gson.toJson(dahuaTokenInfo) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:testSubToken result " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject results = object.getJSONObject("result");
                    JSONObject data = results.getJSONObject("data");
                    String accessToken = data.getString("accessToken");
                    CommonData.subToken = accessToken;


                    HashMap<String, Object> paramsMap = new HashMap<String, Object>();
                    paramsMap.put("token",CommonData.subToken);
                    paramsMap.put("pageNo",1);
                    paramsMap.put("pageSize",10);
                    testSubDeviceList(paramsMap);

                } catch (Exception e) {
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
    private void testSubDeviceList(Map<String, Object> paramsMap) {
        DahuaSubDeviceList dahuaTokenInfo = CommonUtils.paramsSubDeviceList(paramsMap);
        Gson gson = new Gson();
        RequestParams params = new RequestParams("https://openapi.lechange.cn/openapi/subAccountDeviceList");
        params.setBodyContent(gson.toJson(dahuaTokenInfo));
        Log.e(TAG, "testSubDeviceList: " + gson.toJson(dahuaTokenInfo) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:testSubDeviceList result " + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject results = object.getJSONObject("result");
                    JSONObject data = results.getJSONObject("data");
                    JSONArray deviceList = data.getJSONArray("deviceList");
                    JSONObject device = (JSONObject) deviceList.get(0);
                    CommonData.deviceSub = device;
                    CommonData.subId = device.getString("deviceId");

                } catch (Exception e) {
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

    private void initBeidouUserList() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/user/queryBeidouUsers");
        params.addHeader("Authorization","bearer " + new DbConfig(MainActivity.this).getUser().getToken());
        params.addHeader("NetworkType", "Internet");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo result = " + result );
                DbConfig dbConfig = new DbConfig(MainActivity.this);
                DbManager dbManager = dbConfig.getDbManager();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = (JSONObject) data.get(i);
                        if(dbConfig.getBeiDouPersonById(obj.getString("beidouCardNum"))==null){
                            ///没有该联系人则存储
                            BeidouPerson beidouPerson = new BeidouPerson(obj.getString("beidouCardNum"),obj.getString("fullName"),obj.getString("beidouCardNum"),"","",0,0,0);
                            try {
                                dbManager.saveOrUpdate(beidouPerson);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    }

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


    @Override
    protected void onResume() {
        super.onResume();

        if(viewPager.getCurrentItem() == 1){
            EventBus.getDefault().post(new VideoStart());
        }
        //保证service运行
        try{
            ActivityManager myManager=(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
            boolean active = false;
            for(int i = 0 ; i<runningService.size();i++) {
                Log.e(TAG, "onResume: serviceName = " + runningService.get(i).service.getClassName().toString() );
                if(runningService.get(i).service.getClassName().toString().contains("TrackService")) {
                    active =  true;
                }
            }
            if(!active){
                //开启轨迹服务
                startService(new Intent(getApplicationContext(), TrackService.class));
            }
        }catch (Exception e){

        }
    }

    private void initBeidouSDK() {
        //使用下面方法初始化[特别是第一次安装SDK后]
        Intent init = new Intent();
        init.setPackage("com.kuyou.bd1.sdk");
        init.setAction("kuyou.intent.action.SDK.init");
        init.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        String protocolVersion = "4.0";
        bundle.putString("Protocol", protocolVersion);
        init.putExtras(bundle);
        try{
            startActivity(init);
        }catch (Exception e){
        }
    }

    @Override
    public void tunnelTotalStateChanged() {
        TunnelStateManager.TunnelState state = mTunnelStateManager.getTunnelState();
        TunnelStateManager.TunnelErrorState errorState = mTunnelStateManager.getTunnelErrorState();
        Log.i(TAG, "tunnelstate:"+state+",error:"+errorState);
        if(reportErrorTunnelState(errorState)){
            return;
        }

        switch (state){
            case DISABLED:
                Toast.makeText(getApplicationContext(), "未连接", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING:
                Toast.makeText(getApplicationContext(), "连接中", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTED:
               /* if (hasTuisong){
                    hasTuisong = false;
                    if (tuisongType.equals("4")){
                        viewPager.setCurrentItem(0);
                        tabsCell.setSelected(0);
                    }else {
                        startActivity(new Intent(getApplicationContext(), FireMissionListActivity.class));
                    }
                }*/
                Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECTING:
                Toast.makeText(getApplicationContext(), "断开连接中", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean reportErrorTunnelState(TunnelStateManager.TunnelErrorState error){
        if (error == TunnelStateManager.TunnelErrorState.NO_ERROR)
        {
            return false;
        }

        switch (error)
        {
            case PEER_AUTH_FAILED:
                Toast.makeText(this, "用户认证失败！", Toast.LENGTH_SHORT).show();
                break;
            case LOOKUP_FAILED:
                Toast.makeText(this,"lookup_failed", Toast.LENGTH_SHORT).show();
                break;
            case UNREACHABLE:
                Toast.makeText(this, "网关不可达", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "其他错误："+error, Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    //网络状态变化，回调方法
    @Override
    public void onDisconnect() {
        Log.e(TAG, "onDisconnect: bingo" );
        //网络中断
    }

    @Override
    public void onMobileConnect() {
        Log.e(TAG, "onMobileConnect: bingo" );
        //移动网络连接
        DbConfig dbConfig = new DbConfig(MainActivity.this);
        List<BeiDouLocation> beiDouLocationList = dbConfig.getBeiDouLocationList();
        if(beiDouLocationList!=null){
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   postLocations(beiDouLocationList);
               }
           },1000);
        }
    }

    private void postLocations(List<BeiDouLocation> list) {
        List<OfflineLocation> offlineLocationList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            BeiDouLocation location = list.get(i);
            OfflineLocation offlineLocation = new OfflineLocation("","","","","",0,"","",0,location.date,"","",new OfflineLocation.LatLng(location.latitude,location.longitude),"","","","","","");
            offlineLocationList.add(offlineLocation);
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/trajectory/trackListUpload");
        params.setAsJsonContent(true);
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType", "Internet");
        params.setBodyContent(new Gson().toJson(offlineLocationList));
        Log.e(TAG, "postLocations: bingo new Gson().toJson(offlineLocationList) = " + new Gson().toJson(offlineLocationList) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo" + result );
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if(Objects.equals(code, "200")){
                        Log.e(TAG, "onSuccess: bingo 离线轨迹上传成功"  );
                        //删除已上传轨迹
                        new DbConfig(MainActivity.this).clearLocations();
                    }
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

    @Override
    public void onWifiConnect() {
        Log.e(TAG, "onWifiConnect: bingo" );
        //wifi 连接
        DbConfig dbConfig = new DbConfig(MainActivity.this);
        List<BeiDouLocation> beiDouLocationList = dbConfig.getBeiDouLocationList();
        if(beiDouLocationList!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    postLocations(beiDouLocationList);
                }
            },1000);
        }
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
        tabsCell.addView(R.drawable.ic_mode_map, R.drawable.ic_mode_map_selected, "地图");
        tabsCell.addView(R.drawable.ic_jiankong, R.drawable.ic_jiankong_selected, "视频监控");
        tabsCell.addView(R.drawable.ic_yingyong, R.drawable.ic_yingyong_selected, "应用");
        tabsCell.addView(R.drawable.ic_xiaoxi, R.drawable.ic_xiaoxi_selected, "我的");

    }

    private List<Fragment> initFragment() {
        List<Fragment> fragments = new ArrayList<>();

        MapNewFragment mapNewFragment = new MapNewFragment();
        Bundle bundle = new Bundle();
        if (hasTuisong){
            bundle.putBoolean("hasTuisong",true);
            bundle.putString("tuisongId",tuisongId);
            bundle.putString("tuisongTime",tuisongTime);
            bundle.putString("tuisongType",tuisongType);
            mapNewFragment.setArguments(bundle);
        }else {
            bundle.putBoolean("hasTuisong",false);
            mapNewFragment.setArguments(bundle);
        }
        fragments.add(mapNewFragment);
        fragments.add(new VideoNewFragment());
        fragments.add(new AppsFragment());
        fragments.add(new MyFragment() );

        return fragments;
    }

    protected List<String> initPagerTitle() {
        titles = new ArrayList<>();
        titles.add("首页");
        titles.add("地图");
        titles.add("数据统计");
        titles.add("我的");
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

            Intent intent = new Intent("haohai.haohai.baseActivity");       //关闭程序
            intent.putExtra("closeAll", 1);
            sendBroadcast(intent);//发送广播

            stopService(serviceIntent);

            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: mainActivity");

        unregisterReceiver(fireWeixingReceiver);
        unregisterReceiver(changeTabReceiver);
        unregisterReceiver(mBeidouModuleInfoReceiver);
        setBDMessageEnable(false); //关闭北斗
        EventBus.getDefault().unregister(this);

        //网络状态
        NetworkChangeReceiver.unRegisterReceiver(this);
        NetworkChangeReceiver.unRegisterObserver(this);

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
        params.addHeader("NetworkType", "Internet");
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
        String fileName = MainActivity.this.getObbDir().getAbsolutePath() + "/" + DOWNLOAD_NAME + versionService + ".apk";
        if (Build.VERSION.SDK_INT >= 24) {
            File file = new File(fileName);
            tempUri = FileProvider.getUriForFile(MainActivity.this, "com.haohai.platform.fireforestplatform", file);
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

            //type 2一体机林业 3一体机国土    4是卫星
            tuisongType = intent.getStringExtra("type");
            if (tuisongType.equals("11")||tuisongType.equals("12")||tuisongType.equals("13")||tuisongType.equals("14")||tuisongType.equals("15")){
                viewPager.setCurrentItem(0);
                tabsCell.setSelected(0);
            } else {
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



    private BroadcastReceiver mBeidouModuleInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            Log.e(TAG, "onReceive: bingo 接收到了广播" );
            String action = intent.getAction();
            String tempStr = "";
            Bundle bundle = null;
            ///接收本机北斗卡号 bingo
            if (ACTION_MSG_BD_IC_INFO_RECEIVED.equals(action)) {
                bundle = intent.getExtras();
                String ic_number = bundle.getString("number");
                CommonData.icNumber = ic_number;
                DbConfig dbConfig = new DbConfig(MainActivity.this);
                User user = dbConfig.getUser();
                user.setIcNumber(ic_number);
                try {
                    dbConfig.getDbManager().saveOrUpdate(user);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onReceive: bingo ic_number = " + ic_number );
//                initBeidouUserList();

                /*tempStr = "ServiceFre is:" + String.valueOf(bundle.getInt("service_frequency"))
                        + "communiationLevel is:" + String.valueOf(bundle.getInt("communication_level"))
                        + "Ic num is:" + bundle.getString("number")
                        + "BID is:" + String.valueOf(bundle.getInt("BID"))
                        + "Frame is:" + String.valueOf(bundle.getInt("Frame"))
                        + "Feature is:" + String.valueOf(bundle.getInt("Feature"))
                        + "Flag is:" + String.valueOf(bundle.getInt("Flag"))
                        + "UserNum is:" + String.valueOf(bundle.getInt("UserNum"));*/
                /*txt_sim.setText(tempStr);*/
            }
            ///北斗消息接收 bingo
            if (ACTION_MSG_BD_MSG_RECEIVED.equals(action)) {
                bundle = intent.getExtras();
                String number = bundle.getString("number");
                int msgType = bundle.getInt("msgtype");  // 0:hunfa, 1:hanzi, 2:daima
                int msgLen = bundle.getInt("msglenth");
                int bitLen = bundle.getInt("BitLen");
                boolean is_need_rm_end_char = false;
                if (bitLen % 8 != 0) {
                    is_need_rm_end_char = true;
                }
                byte[] packetBuf = bundle.getByteArray("msgcontent");
                //去除前两位（A4）
                byte[] bytes = Arrays.copyOfRange(packetBuf, 1, packetBuf.length);
                String content = "";
                int crcFlag = bundle.getInt("crcFlag");
                try {
                    if (msgType == 0) {// 0:混发, 1:汉字, 2:代码
                        content = new String(bytes, "GB2312");
                    } else if (msgType == 1) {
                        content = new String(bytes, "GB2312");
                    } else {
                        for (int k = 0; k < msgLen; k++) {
                            content += String.format("%02x ", bytes[k]);
                        }
                        if (is_need_rm_end_char) {
                            content = content.substring(0, content.length() - 2);
                        }
                    }
                } catch (Exception e) {
                    Log.d("BDProtocol", "BDProtocol_onParseTXXXMsg change byte error!!! ");
                }
                //消息内容解析(浩海规定格式： 136.123123,36.123521|后面是消息内容 )
                String text = content;
                String location = "";
                try{
                    int index = content.indexOf("|");
                    location = content.substring(0,index);
                    text = content.substring(index+1,content.length());
                }catch (Exception e){
                    Log.e(TAG, "onReceive: Exception location | e = " + e.toString() );
                }

                ///消息持久化
                DbConfig dbConfig = new DbConfig(MainActivity.this);
                DbManager db = dbConfig.getDbManager();
                Date date = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sendTime = format.format(date);
                BeidouNews news = new BeidouNews(number+sendTime,dbConfig.getBeiDouPersonNameById(number),number,text,location,sendTime,date.getTime(),"",0,0);
                BeidouPerson beiDouPersonId = dbConfig.getBeiDouPersonById(number);
                beiDouPersonId.setLastNews(text);
                beiDouPersonId.setLastTime(sendTime);
                beiDouPersonId.setUpdateLong(date.getTime());
                beiDouPersonId.setCount(beiDouPersonId.getCount()+1);
                beiDouPersonId.setUnRead(beiDouPersonId.getUnRead()+1);
                try {
                    db.saveOrUpdate(news);
                    db.saveOrUpdate(beiDouPersonId);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                //刷新通讯录列表
                EventBus.getDefault().post(new BeidouRefresh());

            }
            //短报文发送状态 bingo
            if (ACTION_MSG_BD_FKXX_RECEIVED.equals(action)) {
                DbConfig dbConfig = new DbConfig(MainActivity.this);
                DbManager dbManager = dbConfig.getDbManager();
                bundle = intent.getExtras();
                String fkContent = bundle.getString("FeedBackInfo");//zhong wen zi fu
                int fkTag = bundle.getInt("FeedBackTag"); //fkbacktag info
                String fkExtraInfo = bundle.getString("FeedBackExtraInfo");
                tempStr = "State is:" + fkContent + " fkTag is:0" + fkTag + " ExtraInfo is:" + fkExtraInfo;
                Log.e("TAG", "onReceive: bingo 短报文发送状态 " + tempStr  );
                List<BeidouNews> beiDouNewsLoading = dbConfig.getBeiDouNewsLoading();
                if(beiDouNewsLoading==null){
                    return;
                }
                for (int i = 0; i < beiDouNewsLoading.size(); i++) {
                    BeidouNews beidouNews = beiDouNewsLoading.get(i);
                    if(fkContent.contains("成功")){
                        //成功
                        beidouNews.setState(0);
                    }else{
                        //失败
                        beidouNews.setState(-2);
                    }
                    try {
                        dbManager.saveOrUpdate(beidouNews);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新对话
                        EventBus.getDefault().post(new BeidouInfoRefresh());
                    }
                },500);
            }
            if (ACTION_MSG_BD_DWXX_RECEIVED.equals(action)) {
                bundle = intent.getExtras();
                byte m_Type = bundle.getByte("m_Type");
                short m_byHeightData = bundle.getShort("m_byHeightData");
                byte m_byHeightSymbol = bundle.getByte("m_byHeightSymbol");
                int time_hour = (int) bundle.getByte("m_byHour");
                int time_minute = bundle.getByte("m_byMinute");
                int time_second = bundle.getByte("m_bySecond1");
                int time_minsecond = bundle.getByte("m_bySecond2");
                int lonDegree = bundle.getByte("m_byLonDegree");
                int lonMinute = bundle.getByte("m_byLonMinute");
                int lonSecond = bundle.getByte("m_byLonSecond1");
                int lonminSecond = bundle.getByte("m_byLonSecond2");
                int latDegree = bundle.getByte("m_byLatDegree");
                int latMinute = bundle.getByte("m_byLatMinute");
                int latSecond = bundle.getByte("m_byLatSecond1");
                int latminSecond = bundle.getByte("m_byLatSecond2");

                int heightAbnormalData = bundle.getByte("m_byHeightAbnormalData");
                int heightAbnormalSymbol = bundle.getByte("m_byHeightAbnormalSymbol");
                int address = bundle.getInt("m_address");
                byte[] byteAddress = bundle.getByteArray("m_ByteAddress");
                String timpStr = "time is:" + time_hour + ":" + time_minute + ":"
                        + time_second + "." + time_minsecond + "; lon is: "
                        + lonDegree + "度" + lonMinute + "分" + lonSecond + "."
                        + lonminSecond + "秒; lat is: " + latDegree + "度" + latMinute + "分"
                        + latSecond + "." + latminSecond + "秒;" + " 高度:" + m_byHeightData + "m"
                        + "  " + heightAbnormalSymbol + "  " + heightAbnormalData
                        + "Type is:" + m_Type + " address is:" + address;
                /*txt_bd_pos.setText(timpStr);*/

            }
            if (ACTION_MSG_BD_GLXX_INFO_RECEIVED.equals(action)) {
                Bundle bundleglxx = intent.getExtras();
                byte[] glxxInfo = bundleglxx.getByteArray("GLXXInfo");
                String glxxStr = new String(glxxInfo);
                /*txt_send_state.setText(glxxStr);*/
            }
            if (ACTION_MSG_BD_ZBSC_INFO_RECEIVED.equals(action)) {
                Bundle bundlezbsc = intent.getExtras();
                int changeMode = bundlezbsc.getInt("ZBSC_ChangeMode");
                int x = bundlezbsc.getInt("ZBSC_X");
                int y = bundlezbsc.getInt("ZBSC_Y");
                int z = bundlezbsc.getInt("ZBSC_Z");
                /*txt_send_state.setText("changeMode=" + changeMode + ";x=" + x + ";y=" + y + ";z=" + z);*/
            }
            if (ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED.equals(action)) {
                /*btn_enable_bd.setEnabled(true);
                enable = intent.getExtras().getBoolean("BDMsg_enable_state");
                if (enable) {
                    btn_enable_bd.setText(MainActivity.this.getResources().getString(R.string.str_disable_bd));
                } else {
                    btn_enable_bd.setText(MainActivity.this.getResources().getString(R.string.str_enable_bd));
                }
                */
            }
            if (ACTION_MSG_BD_SJXX_INFO_RECEIVED.equals(action)) {
                Bundle bundlesjxx = intent.getExtras();
                if (bundlesjxx != null) {
                    int year = bundlesjxx.getInt("m_year");
                    int month = bundlesjxx.getInt("m_month");
                    int day = bundlesjxx.getInt("m_day");
                    int hour = bundlesjxx.getInt("m_hour");
                    int minute = bundlesjxx.getInt("m_minute");
                    int second = bundlesjxx.getInt("m_second");
                    /*txt_bd_time.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);*/
                }
            }
            if (ACTION_MSG_BD_XHXX_INFO_RECEIVED.equals(action)) {
                Bundle bundlesxhxx = intent.getExtras();
                if (bundlesxhxx != null) {
                    int seriaNo = bundlesxhxx.getInt("XHXX_Number");
                    /*txt_bd_time.setText(String.valueOf(seriaNo));*/
                }
            }
            if (ACTION_MSG_BD_ZJXX_INFO_RECEIVED.equals(action)) {
                Bundle bundlezjxx = intent.getExtras();
                if (bundlezjxx != null) {
                    int icStatus = bundlezjxx.getByte("ICStatus");
                    int hardwareStatus = bundlezjxx.getByte("HardwareStatus");
                    int batteryStatus = bundlezjxx.getByte("BatteryStatus");
                    int inboundStatus = bundlezjxx.getByte("InboundStatus");
                    int power1 = bundlezjxx.getByte("Power1");
                    int power2 = bundlezjxx.getByte("Power2");
                    int power3 = bundlezjxx.getByte("Power3");
                    int power4 = bundlezjxx.getByte("Power4");
                    int power5 = bundlezjxx.getByte("Power5");
                    int power6 = bundlezjxx.getByte("Power6");
                    /*txt_power.setText(String.valueOf(icStatus) + ";" + String.valueOf(hardwareStatus)
                            + ";" + String.valueOf(batteryStatus) + ";" + String.valueOf(inboundStatus)
                            + ";" + String.valueOf(inboundStatus) + ";" + String.valueOf(power1)
                            + ";" + String.valueOf(power2) + ";" + String.valueOf(power3)
                            + ";" + String.valueOf(power4) + ";" + String.valueOf(power5)
                            + ";" + String.valueOf(power6));*/
                }
            }
        }
    };


    private void setBDMessageEnable(boolean enable) {
        Intent intent = new Intent(ACTION_MSG_BD_MSG_ENABLE_REQUEST);
        Bundle bundle1 = new Bundle();
        bundle1.putBoolean("BDMSG_Enable", enable);
        intent.putExtras(bundle1);
        sendBroadcast(intent);
    }


}
