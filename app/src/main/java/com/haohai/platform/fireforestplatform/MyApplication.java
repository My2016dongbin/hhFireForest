package com.haohai.platform.fireforestplatform;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.model.UpdateConfig;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.cretin.www.cretinautoupdatelibrary.utils.SSLUtils;
import com.haohai.platform.fireforestplatform.ui.rxhttp.RequestUtils;
import com.haohai.platform.fireforestplatform.ui.utils.TraceServiceImpl;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.ruyiruyi.rylibrary.route.Action;
import com.ruyiruyi.rylibrary.utils.OkHttp3Connection;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.xdandroid.hellodaemon.DaemonEnv;

import org.xutils.x;

import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

import dalvik.system.DexFile;
//import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient;
import rxhttp.RxHttpPlugins;

/**
 * Created by geyang on 2020/6/2.
 */

public class MyApplication extends Application {

    private ConcurrentSkipListMap<String, Class> map;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils
        x.Ext.init(this);
        x.Ext.setDebug(true);
        /*OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();*/

        initTengxunTuisong();


        //如果你想使用okhttp作为下载的载体，那么你需要自己依赖okhttp，更新库不强制依赖okhttp！可以使用如下代码创建一个OkHttpClient 并在UpdateConfig中配置setCustomDownloadConnectionCreator start
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30_000, TimeUnit.SECONDS)
                .readTimeout(30_000, TimeUnit.SECONDS)
                .writeTimeout(30_000, TimeUnit.SECONDS)
                //如果你需要信任所有的证书，可解决根证书不被信任导致无法下载的问题 start
                .sslSocketFactory(SSLUtils.createSSLSocketFactory())
                .hostnameVerifier(new SSLUtils.TrustAllHostnameVerifier())
                //如果你需要信任所有的证书，可解决根证书不被信任导致无法下载的问题 end
                .retryOnConnectionFailure(true);
        //如果你想使用okhttp作为下载的载体，那么你需要自己依赖okhttp，更新库不强制依赖okhttp！可以使用如下代码创建一个OkHttpClient 并在UpdateConfig中配置setCustomDownloadConnectionCreator end
        //APK升级-当你希望使用传入model的方式，让插件自己解析并实现更新
        UpdateConfig updateConfig = new UpdateConfig()
                .setDebug(true)//是否是Debug模式
                .setCustomDownloadConnectionCreator(new OkHttp3Connection.Creator(builder))
                .setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_MODEL)//设置获取更新信息的方式
                .setShowNotification(true)//配置更新的过程中是否在通知栏显示进度
                .setNotificationIconRes(R.mipmap.ic_launcher)//配置通知栏显示的图标
                .setUiThemeType(TypeConfig.UI_THEME_G)//配置UI的样式，一种有12种样式可供选择
                .setAutoDownloadBackground(false)//是否需要后台静默下载，如果设置为true，则调用checkUpdate方法之后会直接下载安装，不会弹出更新页面。当你选择UI样式为TypeConfig.UI_THEME_CUSTOM，静默安装失效，您需要在自定义的Activity中自主实现静默下载，使用这种方式的时候建议setShowNotification(false)，这样基本上用户就会对下载无感知了
                //.setCustomActivityClass(CustomActivity.class)//如果你选择的UI样式为TypeConfig.UI_THEME_CUSTOM，那么你需要自定义一个Activity继承自RootActivity，并参照demo实现功能，在此处填写自定义Activity的class
                .setNeedFileMD5Check(false)//是否需要进行文件的MD5检验，如果开启需要提供文件本身正确的MD5校验码，DEMO中提供了获取文件MD5检验码的工具页面，也提供了加密工具类Md5Utils
                //.setCustomDownloadConnectionCreator(new OkHttp3Connection.Creator(builder));//如果你想使用okhttp作为下载的载体，可以使用如下代码创建一个OkHttpClient，并使用demo中提供的OkHttp3Connection构建一个ConnectionCreator传入，在这里可以配置信任所有的证书，可解决根证书不被信任导致无法下载apk的问题
                ;
        AppUpdateUtils.init(this, updateConfig);


        // 网易云信 SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将进行自动登录）。不能对初始化语句添加进程判断逻辑。
        NIMClient.init(this, loginInfo(), options());


        //路由配置
        ARouter.openLog();     // Print log
        ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        ARouter.init(this); // As early as possible, it is recommended to initialize in the Application

        getAllActivities(this);


        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //网易云信配置
       // NIMClient.init(this, loginInfo(), options());
        // 使用 `NIMUtil` 类可以进行主进程判断。
        // boolean mainProcess = NIMUtil.isMainProcess(context)
      //  if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
      //  }
        DaemonEnv.initialize(this, TraceServiceImpl.class,DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        TraceServiceImpl.sShouldStopService=false;
        DaemonEnv.startServiceMayBind(TraceServiceImpl.class);
    }


    // 网易云信 如果提供，将同时进行自动登录。如果当前还没有登录用户，请传入null。详见自动登录章节。
    private LoginInfo loginInfo() {
        return null;
    }

    // 网易云信 设置初始化配置参数，如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        return options;
    }

    private void initTengxunTuisong() {
        //腾讯推送
        XGPushConfig.enableDebug(this,true);
        //开启小米推送
        XGPushConfig.setMiPushAppId(getApplicationContext(), "2882303761518845436");
        XGPushConfig.setMiPushAppKey(getApplicationContext(), "5981884523436");

        //oppo推送
        XGPushConfig.setOppoPushAppId(getApplicationContext(), "93a4e3e3bcb3448387c2af2eadc1f4ee");
        XGPushConfig.setOppoPushAppKey(getApplicationContext(), "e317f095327f4ca0bf3f2837f9fece03");

        //打开第三方推送  华为推送
        XGPushConfig.enableOtherPush(getApplicationContext(), true);


        //设置魅族APPID和APPKEY
        XGPushConfig.setMzPushAppId(this, "136981");
        XGPushConfig.setMzPushAppKey(this, "ae5633cd5a4146a1832b7cc3caa4d205");


    }

    // 如果提供，将同时进行自动登录。如果当前还没有登录用户，请传入null。详见自动登录章节。
   /* private LoginInfo loginInfo() {

        return null;
    }*/

    // 设置初始化配置参数，如果返回值为 null，则全部使用默认参数。
    /*private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;


        return options;
    }*/

    private void getAllActivities(Context ctx){
        try {
            //通过资源路径获得DexFile
            DexFile e = new DexFile(ctx.getPackageResourcePath());
            Enumeration entries = e.entries();
            //遍历所有元素
            while(entries.hasMoreElements()) {
                String entryName = (String)entries.nextElement();
                //匹配Activity包名与类名
                if(entryName.contains("activity") && entryName.contains("Activity")) {
                    //通过反射获得Activity类
                    Class entryClass = Class.forName(entryName);
                    if(entryClass.isAnnotationPresent(Action.class)) {
                        Action action = (Action)entryClass.getAnnotation(Action.class);
                        this.map.put(action.value(), entryClass);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
