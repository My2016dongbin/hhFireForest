package com.haohai.platform.fireforestplatform;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.trace.LBSTraceClient;
import android.os.Trace;

import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.google.gson.Gson;
import com.haohai.platform.fireforestplatform.ui.LocationService;
//import com.haohai.platform.fireforestplatform.ui.utils.AuthTypeUtil;
import com.haohai.platform.fireforestplatform.ui.model.OfflineLocation;
import com.haohai.platform.fireforestplatform.ui.receiver.NetworkChangeReceiver;
import com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity;
import com.haohai.platform.mapmodel.fragment.MapNewFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoIOSFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoNewFragment;
import com.haohai.platform.platformmodel.ui.fragment.VideoNewFragmentIOS;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.SignallingServiceObserver;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.SignallingEventType;
import com.netease.nimlib.sdk.avsignalling.event.CanceledInviteEvent;
import com.netease.nimlib.sdk.avsignalling.event.ChannelCloseEvent;
import com.netease.nimlib.sdk.avsignalling.event.ChannelCommonEvent;
import com.netease.nimlib.sdk.avsignalling.event.ControlEvent;
import com.netease.nimlib.sdk.avsignalling.event.InviteAckEvent;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.event.UserJoinEvent;
import com.netease.nimlib.sdk.avsignalling.event.UserLeaveEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.netease.nimlib.sdk.avsignalling.model.MemberInfo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruyiruyi.rylibrary.base.CallingActivity;
import com.ruyiruyi.rylibrary.bus.DoLogin;
import com.ruyiruyi.rylibrary.bus.DoUpdate;
import com.ruyiruyi.rylibrary.bus.MessageWrap;
import com.ruyiruyi.rylibrary.bus.MkToast;
import com.ruyiruyi.rylibrary.db.BeiDouLocation;
import com.ruyiruyi.rylibrary.db.Calling;
import com.ruyiruyi.rylibrary.db.CloseChannel;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Requestaddress;
import com.ruyiruyi.rylibrary.db.User;
import com.haohai.platform.platformmodel.ui.fragment.AppsFragment;
import com.haohai.platform.platformmodel.ui.fragment.MyFragment;
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
//import com.vsg.trustaccess.sdks.VSGService;
//import com.vsg.trustaccess.sdks.logic.AuthStateManager;
//import com.vsg.trustaccess.sdks.logic.CharonVpnService;
//import com.vsg.trustaccess.sdks.logic.TunnelStateManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*@Route(path = RouteUtils.LoginToMain)*/
public class MainActivity extends BaseFragmentActivity implements NetworkChangeReceiver.NetStateChangeObserver{

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
    @SuppressLint("HandlerLeak")
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
    private ChangeTabReceiver changeTabReceiver;
    private Requestaddress requestaddress;


    /*退出登录回调*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageWrap message) {
        finish();
    }


    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPowerManager;

    @SuppressLint("InvalidWakeLockTag")
    public void turnOnScreen() {
        // turn on screen
        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            mWakeLock.acquire();
            mWakeLock.release();
        } catch (Exception e) {

        }
    }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏显示
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕不息屏
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//点亮屏幕
                super.onCreate(savedInstanceState);

                turnOnScreen();
                //初始化绑定网易云信信令回调时间
                bindYunXinCallback();

                //网络状态
                NetworkChangeReceiver.registerReceiver(this);
                NetworkChangeReceiver.registerObserver(this);

                EventBus.getDefault().register(this);
                //延时获取通知栏权限
                requestNotificationPermission();

                android.os.Debug.startMethodTracing();
                setContentView(R.layout.activity_main);


                requestaddress=new DbConfig(this).getRequestaddress();
                //开启MQTT服务
                //startService(new Intent(getApplicationContext(), MQTTService.class));
                //开启轨迹服务
                startService(new Intent(getApplicationContext(), TrackService.class));
                //获取人员组织数据
                startService(new Intent(getApplicationContext(), DataService.class));
                //开启百度定位服务
                //startService(new Intent(getApplicationContext(), LocationService.class));todo 暂未开启百度定位
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


                //开启鹰眼轨迹
                //initBDTrace();
            }

            private LBSTraceClient mTraceClient;
            private com.baidu.trace.Trace mTrace;
            private boolean serviceStatus = false;
            private void initBDTrace() {
                // 轨迹服务ID
                long serviceId = 241948;
                // 设备标识
                String id = new DbConfig(this).getUser().getId();
                String entityName = id+"";
                // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
                boolean isNeedObjectStorage = false;
                // 初始化轨迹服务
                mTrace = new com.baidu.trace.Trace(serviceId, entityName, isNeedObjectStorage);
                //同意隐私政策
                LBSTraceClient.setAgreePrivacy(this, true);
                // 初始化轨迹服务客户端
                try {
                    mTraceClient = new LBSTraceClient(getApplicationContext());
                    CommonData.mTraceClient = mTraceClient;
                    // 定位周期(单位:秒)
                    int gatherInterval = 5;
                    // 打包回传周期(单位:秒)
                    int packInterval = 10;
                    // 设置定位和打包周期
                    mTraceClient.setInterval(gatherInterval, packInterval);
                    // 开启服务
                    mTraceClient.startTrace(mTrace, mTraceListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "initBDTrace: BDTrace" + e.toString() );
                }
            }
            // 初始化轨迹服务监听器
            private final OnTraceListener mTraceListener = new OnTraceListener() {
                @Override
                public void onBindServiceCallback(int i, String s) {
                    Log.e(TAG, "BDTrace onBindServiceCallback: " + i + s );
                    if(!serviceStatus){
                        // 开启服务
                        mTraceClient.startTrace(mTrace, mTraceListener);
                    }
                }

                // 开启服务回调
                @Override
                public void onStartTraceCallback(int status, String message) {
                    Log.e(TAG, "BDTrace onStartTraceCallback: " + status + message );
                    if(status == 0){
                        serviceStatus = true;
                        // 开启采集
                        mTraceClient.startGather(mTraceListener);
                    }else{
                        if(!serviceStatus){
                            // 开启服务
                            mTraceClient.startTrace(mTrace, mTraceListener);
                        }
                    }
                }
                // 停止服务回调
                @Override
                public void onStopTraceCallback(int status, String message) {
                    Log.e(TAG, "BDTrace onStopTraceCallback: " + message );
                }
                // 开启采集回调
                @Override
                public void onStartGatherCallback(int status, String message) {
                    Log.e(TAG, "BDTrace onStartGatherCallback: " + status +message );
                }
                // 停止采集回调
                @Override
                public void onStopGatherCallback(int status, String message) {
                    Log.e(TAG, "BDTrace onStopGatherCallback: " +message );
                }
                // 推送回调
                @Override
                public void onPushCallback(byte messageNo, PushMessage message) {
                    Log.e(TAG, "BDTrace onPushCallback: " );
                }

                @Override
                public void onInitBOSCallback(int i, String s) {
                    Log.e(TAG, "BDTrace onInitBOSCallback: " );
                }

                @Override
                public void onTraceDataUploadCallBack(int i, String s, int i1, int i2) {
                    Log.e(TAG, "BDTrace onTraceDataUploadCallBack: " );
                }
            };


            private Observer<ChannelCommonEvent> onlineObserver;
            private void bindYunXinCallback() {
                // 在线通知事件观察者
                onlineObserver = new Observer<ChannelCommonEvent>() {
                    @Override
                    public void onEvent(ChannelCommonEvent event) {
                        SignallingEventType eventType = event.getEventType();

                /*if(event!=null){//TODO 屏蔽
                    return;
                }*/
                        switch (eventType) {
                            case CLOSE:
                                //Toast.makeText(MainActivity.this, "频道关闭回调", Toast.LENGTH_SHORT).show();
                                ChannelCloseEvent channelCloseEvent = (ChannelCloseEvent) event;
                                EventBus.getDefault().post(new CloseChannel());
                                break;
                            case JOIN:
                                UserJoinEvent userJoinEvent = (UserJoinEvent) event;
                                //Toast.makeText(MainActivity.this, "有人加入频道回调"+userJoinEvent.getFromAccountId(), Toast.LENGTH_SHORT).show();
                                break;
                            case INVITE:
                                if(CommonData.isCalling){
                                    return;
                                }
                                CommonData.isCalling = true;
                                InvitedEvent invitedEvent = (InvitedEvent) event;
                                CommonData.invitedEvent = invitedEvent;
                                //Toast.makeText(MainActivity.this, "被邀请回调 reqId:"+ reqId + " getRequestId:  " + invitedEvent.getRequestId(), Toast.LENGTH_SHORT).show();
                                if(!Objects.equals(invitedEvent.getRequestId(), reqId)){
                                    reqId = invitedEvent.getRequestId();
                                    //Toast.makeText(MainActivity.this, "被邀请 next requestId= " + invitedEvent.getRequestId(), Toast.LENGTH_SHORT).show();
                                    callInvited(invitedEvent);
                                }
                                break;
                            case CANCEL_INVITE:
                                //Toast.makeText(MainActivity.this, "邀请人取消邀请回调", Toast.LENGTH_SHORT).show();
                                CanceledInviteEvent canceledInviteEvent = (CanceledInviteEvent) event;
                                EventBus.getDefault().post(new CloseChannel());
                                break;
                            case REJECT:
                                if(CommonData.isCalling){
                                    return;
                                }
                                InviteAckEvent eventReject = (InviteAckEvent) event;
                                //Toast.makeText(MainActivity.this, "拒绝邀请回调", Toast.LENGTH_SHORT).show();
                                String rejectId = eventReject.getRequestId().substring(0,eventReject.getRequestId().length()-4);
                                if(CommonData.personList.size()>1){
                                    CommonData.personListSize--;
                                    //Toast.makeText(MainActivity.this,CommonData.personListSize+"", Toast.LENGTH_SHORT).show();
                                    if(CommonData.personListSize == 0){
                                        EventBus.getDefault().post(new CloseChannel());
                                    }
                                }else{
                                    EventBus.getDefault().post(new CloseChannel());
                                }
                                break;
                            case ACCEPT:
                                if(CommonData.isCalling){
                                    return;
                                }
                                InviteAckEvent ackEvent = (InviteAckEvent) event;
                                //Toast.makeText(MainActivity.this, "接受邀请回调  reqId : " + reqId + " ackEvent.getRequestId() " + ackEvent.getRequestId(), Toast.LENGTH_SHORT).show();
                                CommonData.isCalling = true;
                                if(!Objects.equals(ackEvent.getRequestId(), reqId)){
                                    reqId = ackEvent.getRequestId();
                                    //Toast.makeText(MainActivity.this, "对方已接收邀请 next requestId = " + ackEvent.getRequestId(), Toast.LENGTH_SHORT).show();
//                            joinChannel(ackEvent);
                                    //加入音频房间
                                    //joinRoom(CommonData.audioRoomName);//移到CallingActivity
                                    EventBus.getDefault().post(new Calling(CommonData.audioRoomName));
                                }
                                break;
                            case LEAVE:
                                UserLeaveEvent userLeaveEvent = (UserLeaveEvent) event;
                                //Toast.makeText(MainActivity.this, "有人离开频道回调"+userLeaveEvent.getFromAccountId(), Toast.LENGTH_SHORT).show();
                                break;
                            case CONTROL:
                                //Toast.makeText(MainActivity.this, "自定义回调", Toast.LENGTH_SHORT).show();
                                ControlEvent controlEvent = (ControlEvent) event;
                                break;
                        }
                    }
                };

                //注册
                NIMClient.getService(SignallingServiceObserver.class).observeOnlineNotification(onlineObserver, true);

            }

            /**
             * 加入信令频道
             * @param ackEvent
             */
            private void joinChannel(InviteAckEvent ackEvent) {
                NIMClient.getService(SignallingService.class).join(CommonData.xdChannelId, Long.parseLong(user.getId()), "", false).setCallback(
                        new RequestCallbackWrapper<ChannelFullInfo>() {

                            @Override
                            public void onResult(int i, ChannelFullInfo channelFullInfo, Throwable throwable) {
                                if (i == ResponseCode.RES_SUCCESS) {
                                    Toast.makeText(MainActivity.this, "加入频道成功", Toast.LENGTH_SHORT).show();

                                    //加入音频房间
                                    joinRoom(CommonData.audioRoomName);
                                } else if (i == ResponseCode.RES_CHANNEL_MEMBER_HAS_EXISTS) {
                                    Toast.makeText(MainActivity.this, "已经在频道中", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "加入频道失败 code=" + i + "cid" + CommonData.xdChannelId, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            ///登录环信bus
            @Subscribe(threadMode = ThreadMode.MAIN)
            public void onGetMessage(DoLogin doLogin) {
                doLogin();
            }


            ///版本更新bus
            @Subscribe(threadMode = ThreadMode.MAIN)
            public void onGetMessage(DoUpdate doUpdate) {
                if (user.getIsLogin() == 1) {
                    //版本更新
                    getVersion();
                }
            }

            ///Toast bus
            @Subscribe(threadMode = ThreadMode.MAIN)
            public void onGetMessage(MkToast mkToast) {
                Toast.makeText(this, mkToast.getMsg(), Toast.LENGTH_SHORT).show();
            }


            /**
             * 登录环信
             */
            private void doLogin() {
                CommonData.testId = user.getId();
                RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/im/user/getToken");
                params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
                params.addParameter("accid",user.getId());
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            Log.e(TAG, "onSuccess: doLogin token " + result );
                            JSONObject object = new JSONObject(result);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject obj = (JSONObject) data.get(0);
                            JSONObject model = obj.getJSONObject("info");
                            String accid = model.getString("accid");
                            String token = model.getString("token");
                            CommonData.wyyAccId = accid;
                            CommonData.wyyToken = token;

                            LoginInfo info = new LoginInfo(accid,token);
                            RequestCallback<LoginInfo> callback =
                                    new RequestCallback<LoginInfo>() {
                                        @Override
                                        public void onSuccess(LoginInfo param) {
                                            // your code
                                            Log.e(TAG, "onSuccess: 网易云信login" +param);
                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            Log.e(TAG, "网易云信login"+"onFailed code " + code);
                                            if (code == 302) {
                                                // your code
                                            } else {
                                                // your code
                                            }
                                        }

                                        @Override
                                        public void onException(Throwable exception) {
                                            // your code
                                            Log.e(TAG, "网易云信login"+"onException code " + exception);
                                        }
                                    };

                            //执行手动登录
                            NIMClient.getService(AuthService.class).login(info).setCallback(callback);

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

            /**
             * 加入音频房间
             * @param roomName
             */
            private void joinRoom(String roomName) {
                RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/nertc/getToken");
                params.addHeader("Authorization","bearer " + user.getToken());
                params.addParameter("uid",Long.parseLong(user.getId()));
                params.addParameter("channelName",roomName);
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            Log.e(TAG, "onSuccess: doLogin token " + result );
                            JSONObject object = new JSONObject(result);
                            JSONArray data = object.getJSONArray("data");
                            JSONObject model = (JSONObject) data.get(0);
                            String token = model.getString("token");
                            CommonData.wyyToken = token;

                            Toast.makeText(MainActivity.this, "准备加入房间："+roomName, Toast.LENGTH_SHORT).show();
                            NERtcEx.getInstance().joinChannel(token,roomName,Long.parseLong(user.getId()));

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    NIMClient.getService(SignallingService.class).queryChannelMemberList(CommonData.xdChannelName).setCallback(new RequestCallbackWrapper<List<MemberInfo>>() {
                                        @Override
                                        public void onResult(int code, List<MemberInfo> result, Throwable exception) {
                                            Log.e(TAG, "onResult: yunxin" + result );
                                        }
                                    });
                                }
                            },10000);

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

            /**
             * 关闭频道
             */
            private void closeChannel() {
                NIMClient.getService(SignallingService.class).close(CommonData.xdChannelId, true,   "关闭频道的自定义字段").setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        Toast.makeText(MainActivity.this, "关闭频道成功 ， channelId =  " + CommonData.xdChannelId, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(int code) {
                        Toast.makeText(MainActivity.this, "关闭频道失败， code =  " + code,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast.makeText(MainActivity.this, "关闭频道异常， exception =  " + exception, Toast.LENGTH_SHORT).show();;
                    }
                });
            }


            private String reqId;

            /**
             * 信令被邀请回调
             * @param invitedEvent
             */
            private void callInvited(InvitedEvent invitedEvent) {
                //接受
                //acceptInvite(invitedEvent);
                //拒绝
                //rejectInvite("",event);
                reqId = invitedEvent.getRequestId();
                String customInfo = invitedEvent.getCustomInfo();
                String pushPayload = invitedEvent.getPushConfig().getPushPayload();
                String type = "audio";
                try {
                    JSONObject object = new JSONObject(pushPayload);
                    type = object.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean isVideo = false;
                if(type.contains("video")){
                    isVideo = true;
                }
                Intent intent = new Intent(this, CallingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra("channelId",invitedEvent.getChannelBaseInfo().getChannelId());
                intent.putExtra("accountId",invitedEvent.getFromAccountId());
                intent.putExtra("requestId",invitedEvent.getRequestId());
                intent.putExtra("customInfo",customInfo);
                intent.putExtra("isCalling",false);
                intent.putExtra("isVideo",isVideo);
                startActivity(intent);
            }
            /**
             * 接受对方的的邀请并加入频道
             * @param invitedEvent
             */
            private void acceptInvite(InvitedEvent invitedEvent) {
                InviteParamBuilder inviteParam = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                        invitedEvent.getFromAccountId(),
                        invitedEvent.getRequestId());
                NIMClient.getService(SignallingService.class).acceptInviteAndJoin(inviteParam, Long.parseLong(user.getId())).setCallback(
                        new RequestCallbackWrapper<ChannelFullInfo>() {

                            @Override
                            public void onResult(int code, ChannelFullInfo channelFullInfo, Throwable throwable) {
                                //参考官方文档中关于api以及错误码的说明
                                if (code == ResponseCode.RES_SUCCESS) {
                                    Toast.makeText(MainActivity.this, "接收邀请成功", Toast.LENGTH_SHORT).show();

                                    //加入音频房间
                                    joinRoom(invitedEvent.getRequestId());
                                } else {
                                    Toast.makeText(MainActivity.this, "接收邀请返回的结果 ， code = " + code +
                                            (throwable == null ? "" : ", throwable = " +
                                                    throwable.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            /**
             * 拒绝对方的邀请
             */
            private void rejectInvite(String customInfo,InvitedEvent invitedEvent) {
                InviteParamBuilder inviteParam = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                        invitedEvent.getFromAccountId(),
                        invitedEvent.getRequestId());
                if (!TextUtils.isEmpty(customInfo)) {
                    inviteParam.customInfo(customInfo);
                }
                NIMClient.getService(SignallingService.class).rejectInvite(inviteParam);
            }


            @Override
            protected void onDestroy() {
                super.onDestroy();
                unregisterReceiver(changeTabReceiver);
                unregisterReceiver(fireWeixingReceiver);
                EventBus.getDefault().unregister(this);
                /*// 停止服务
                mTraceClient.stopTrace(mTrace, mTraceListener);
                // 停止采集
                mTraceClient.stopGather(mTraceListener);*/
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
                    OfflineLocation offlineLocation = new OfflineLocation(location.date,"","","","",0,"","",0,location.date,"","",new OfflineLocation.LatLng(location.latitude,location.longitude),"","","","","","");
                    offlineLocationList.add(offlineLocation);
                }
                RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/trajectory/trackListUpload");
                params.setAsJsonContent(true);
                params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
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
                //  tabsCell.addView(R.drawable.ic_mine, R.drawable.ic_mine_selected, "我的 ");

            }


            private void requestNotificationPermission() {
                Window win = getWindow();
                win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕长亮
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //打开屏幕
                //通知栏权限
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean enabled = isNotificationEnabled(MainActivity.this);

                        Log.e(TAG, "requestNotificationPermission: " + enabled );
                        if (!enabled) {
                            /**
                             * 跳到通知栏设置界面
                             * @param context
                             */
                            Intent localIntent = new Intent();
                            //直接跳转到应用通知设置的代码：
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                localIntent.putExtra("app_package", MainActivity.this.getPackageName());
                                localIntent.putExtra("app_uid", MainActivity.this.getApplicationInfo().uid);
                            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                localIntent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                            } else {
                                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                if (Build.VERSION.SDK_INT >= 9) {
                                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                                } else if (Build.VERSION.SDK_INT <= 8) {
                                    localIntent.setAction(Intent.ACTION_VIEW);
                                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                                    localIntent.putExtra("com.android.settings.ApplicationPkgName", MainActivity.this.getPackageName());
                               }
                            }
                            MainActivity.this.startActivity(localIntent);
                        }
                    }
                },3000);

                //自启动权限
                if(isIgnoringBatteryOptimizations()){
                    requestIgnoreBatteryOptimizations();
                }
            }

            /**
             * 下面俩是申请后台运行的
             * @return
             */
            @RequiresApi(api = Build.VERSION_CODES.M)
            private boolean isIgnoringBatteryOptimizations() {
                boolean isIgnoring = false;
                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (powerManager != null) {
                    isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
                }
                return isIgnoring;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void requestIgnoreBatteryOptimizations() {
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * 获取通知权限
             * @param context
             */
            @TargetApi(Build.VERSION_CODES.KITKAT)
            private boolean isNotificationEnabled(Context context) {

                String CHECK_OP_NO_THROW = "checkOpNoThrow";
                String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

                AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                ApplicationInfo appInfo = context.getApplicationInfo();
                String pkg = context.getApplicationContext().getPackageName();
                int uid = appInfo.uid;

                Class appOpsClass = null;
                try {
                    appOpsClass = Class.forName(AppOpsManager.class.getName());
                    Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                            String.class);
                    Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

                    int value = (Integer) opPostNotificationValue.get(Integer.class);
                    return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            private List<Fragment> initFragment() {
                List<Fragment> fragments = new ArrayList<>();

                //fragments.add(new MapHomeFragment());
//        fragments.add(new MapFragment());
                fragments.add(new MapNewFragment());
//                fragments.add(new VideoNewFragmentIOS());
                fragments.add(new VideoNewFragment());
//                fragments.add(new VideoIOSFragment());
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

            @Override
            protected void onResume() {
                super.onResume();
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

            /**
             * 版本更新
             */
            private void getVersion() {
                try {
                    versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode + "";
                    Log.e(TAG, "getVersion: versionCode -- " + versionCode);
                } catch (PackageManager.NameNotFoundException e) {

                }
                RequestParams params = new RequestParams(requestaddress.getRequstUrl() +"auth/api/androidUpgrade/getCurrent");
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
