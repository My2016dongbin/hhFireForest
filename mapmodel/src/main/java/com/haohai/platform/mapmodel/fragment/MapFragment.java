package com.haohai.platform.mapmodel.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.alibaba.android.arouter.launcher.ARouter;
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
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.model.LatLng;
import com.haohai.platform.firelibrary.utils.LatLngChange;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.Utils.JsApi;
import com.haohai.platform.mapmodel.Utils.MNCTransparentDialog;
import com.haohai.platform.mapmodel.activity.PlayerActivity;
import com.haohai.platform.mapmodel.activity.PlayerRtspActivity;
import com.haohai.platform.mapmodel.activity.WeixingActivity;
import com.haohai.platform.mapmodel.fragment.base.HhBaseFragment;
import com.haohai.platform.mapmodel.model.MapModel;
import com.haohai.platform.mapmodel.model.WeixingModel;
import com.haohai.platform.mapmodel.model.MapPosition;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.haohai.platform.mapmodel.multitype.EmptyViewBinder;
import com.haohai.platform.mapmodel.multitype.OneBodyFire;
import com.haohai.platform.mapmodel.multitype.OneBodyFireViewBinder;
import com.haohai.platform.mapmodel.multitype.ResourceList;
import com.haohai.platform.mapmodel.multitype.ResourceListViewBinder;
import com.haohai.platform.mapmodel.multitype.WeixingModelViewBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.base.AutoStartActivity;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Setting;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.db.UserMenu;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.service.BackgroundMp3Service;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;
import com.ruyiruyi.rylibrary.utils.NumberUtils;
import com.ruyiruyi.rylibrary.utils.image.ImagPagerUtil;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

import static android.content.Context.MODE_PRIVATE;
import static com.haohai.platform.mapmodel.R.id.weixing_layout;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

/**
 * Created by geyang on 2020/7/21.
 */

public class MapFragment extends HhBaseFragment implements JsApi.OnJsClickListener, ResourceListViewBinder.OnResourceLsitItemClick,WeixingModelViewBinder.OnWeixingInfoItemClick,OneBodyFireViewBinder.OnOneBodyItemClick, DatePicker.OnDateChangedListener,INaviInfoCallback {

    private static final String TAG = "MapFragment";
    private DWebView dWebView;
    private Button jiaButton;
    private Button jianButton;
    private myReceiver Receiver;
    private TextView weixingButton;
    private LinearLayout weixingButtonLayout;

    private boolean isWeixingButtonShow = false;
    private boolean isZiyuanButtonShow = false;
    private boolean isonebodyButtonShow = false;
    private LinearLayout weixinLayout;


    private Dialog mapChooseDialog;
    private View mapChooseInflater;
    private LinearLayout gugeGaochengMap;
    private LinearLayout gugeYingxiangMap;
    private LinearLayout tiandiShiliangMap;
    private LinearLayout tiandiYingxiangMap;
    private TextView mapChooseButton;
    private TextView mapDimensionChoos;
    private Button chaoshiButton;
    private LinearLayout quyuLayout;
    private ImageView gugeYingxiangImage;
    private TextView gugeYingxiangText;
    //    private ImageView gugeGaochengImage;
//    private TextView gugeGaochengText;
    private ImageView tiandiShiliangImage;
    private TextView tiandiShiliangText;
    private ImageView tiandiYingxiangImage;
    private TextView tiandiYingxiangText;
    private ImageView tucengButton;
    private ImageView mapButton;
    private static final int EXIT = 1;
    private static final int GUGE_YINGXIANG = 2;
    private static final int GUGE_GAOCHENG = 3;
    private static final int TIANDI_SHILIANG = 4;
    private static final int TIANDI_YINGXIANG = 5;
    private static final int MAP_GAOFEN = 13;
    private static final int DIALOG_FIRE_SHOW = 6;
    private static final int DIALOG_ONEBODY_FIRE_SHOW = 20;
    private static final int DIALOG_MOINTOR_SHOW = 22;
    private static final int MAP_ERROR_SHOW = 8;
    private static final int FIRE_ERROR_SHOW = 9;
    private static final int TWO_D = 10;
    private static final int THREE_D = 11;
    private static final int SHOW_MAP = 7;
    private static final int TIME_CHANGE = 21;
    private static boolean isExit = false;
    private Number currentLongitude;
    private Number currentLatitude;
    private TextView weixingShezhiView;
    private TextView weixingChaxunView;
    private TextView weixingLiebiaoView;
    private TextView onebody_view;
    private TextView ddrw_view;
    private Dialog searchDialog;
    private View searchInflater;
    private LinearLayout oneHoursLayout;
    private LinearLayout currntTimeLayout;
    private LinearLayout threeHoursLayout;
    private LinearLayout oneDayLayout;
    private LinearLayout threeDayLayout;
    private LinearLayout fiveDayLayout;
    private LinearLayout gaojiSearchLayout;

    public int currentFireFindTime = 3;  //时间
    private ProgressDialog progressDialog;
    private User user;
    private List<WeixingModel> weixingModelList;
    private List<OneBodyFire> oneBodyFireList;
    private List<OneBodyFire> oneBodyFireListIsReal;
    private List<OneBodyFire> oneBodyFireListNotHandle;
    private List<OneBodyFire> oneBodyFireFenleiList;
    private List<ResourceList> resourceListList;

    private Dialog fireInfoListDialog;
    private Dialog onebodyListDialog;
    private View fireInfoListInflater;
    private View onebodyListInflater;
    private TextView fireCountText;
    private RecyclerView weixingListView;
    private SmartRefreshLayout onebodyRefreshLayout;
    private RecyclerView onebodyListView;
    private LinearLayout onebodyLayout;
    private TextView fenleiView;
    private List<Object> weixingItems = new ArrayList<>();
    private MultiTypeAdapter weixingAdapter;
    private List<Object> onebodyItems = new ArrayList<>();
    private MultiTypeAdapter onebodyAdapter;
    private List<Object> resourceItems = new ArrayList<>();
    private MultiTypeAdapter resourceAdapter;
    private Dialog gaojiDialog;
    private View gaojiInflater;

    private int currentFireListType = 1;  //1是时间排序  2是编号分类
    private int choose1 = 1;
    private AlertDialog.Builder builder;
    private WeixingModel currentWeixingModel;
    private OneBodyFire currentOneBodyFire;
    private ResourceList currentResourceList;
    private Dialog weixingFireDialog;
    private View weixingFireInflater;
    private TextView fireAddressText;
    private TextView fireTimeText;
    private TextView jingWeiText;
    private TextView kexinText;
    private TextView mianjiText;
    private TextView cishuText;
    private TextView leixingText;
    private TextView shujuyuanText;
    private TextView huodianCodeText;
    private TextView fankuiText;
    private ImageView huodianOneImage;
    private ImageView huodianTwoImage;
    private TextView xiangyuanmianjiView;
    private TextView xiangyuanshuView;
    private TextView retypeView;
    private boolean isShowSearchDialog = false;
    private LinearLayout ziyuanLayout;
    private TextView ziyuanButton;
    private TextView onebodyButton;
    private LinearLayout ziyuanButtonLayout;
    private LinearLayout onebodyButtonLayout;
    private FireWeixingReceiver fireWeixingReceiver;
    private String weixingFireId = "";
    private boolean isTuisong = false;
    private String obTime;  //观测时间
    private LinearLayout gaojiStarTimeLayout;
    private LinearLayout gaojiEndTimeLayout;
    private TextView gaojiStartimeText;
    private TextView gaojiEndTimeText;
    private TextView chongzhiButton;
    private TextView findButton;
    private StringBuffer date;
    private StringBuffer endDate;
    private int year;
    private int month;
    private int day;
    public int chooseHour;
    public int chooseMinute;
    public boolean isChooseStarTime ;
    private ProgressDialog gaojiFindDialog;
    private Dialog resourceinfoDialog;
    private View resourceInflater;
    String resorcetype = "";
    String kejianguangUrl = "";
    String kejianguangMId = "";
    String rechengxiangUrl = "";
    String rechengxiangMId= "";
    private int currentPage = 1;
    private int totalSize;
    private int lastPage;
    private TextView shaixuanbutton;
    private TextView oneBodyKJGButton;
    private TextView oneBodyRCXButton;
    private String cameraid;
    private long et;
    private long st;
    private String rtspurl;
    private String cameraname;
    ImageView orderWarnImageView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            int what = data.getInt("what");
            switch (what){
                case DIALOG_FIRE_SHOW:
                    String id = data.getString("id");
                    for (int i = 0; i < weixingModelList.size(); i++) {
                        if (weixingModelList.get(i).getId().equals(id)) {
                            currentWeixingModel = weixingModelList.get(i);
                        }
                    }

                    //飞到精确点上
                    initWeixingFlyMap();
                    //加载卫星详细数据
                    initWeixinModelData();
                    break;
                case DIALOG_ONEBODY_FIRE_SHOW:
                    String oneBodyid = data.getString("id");
                    for (int i = 0; i < oneBodyFireList.size(); i++) {
                        if (oneBodyFireList.get(i).getId().equals(oneBodyid)) {
                            currentOneBodyFire = oneBodyFireList.get(i);
                        }
                    }

                    //飞到精确点上
                    initOneBodyFlyMap();
                    //加载卫星详细数据
                    initOneBodyFireModelData() ;
                    break;
                case DIALOG_MOINTOR_SHOW:
                    String resourceid = data.getString("id");
                    resourceinfoDialog.show();
                    getinfofromid(resourceid);
                    break;
                case TIME_CHANGE:
                    Log.e(TAG, "handleMessage: 报警查询");
                    getonebodyDataFromSetviceAuto();
                    break;
            }

        }
    };
    private Dialog oneBodyFireDialog;
    private View oneBodyFireInflater;
    private TextView mingchengView;
    private ImageView share_image;
    private TextView dizhiView;
    private TextView shijianView;
    private TextView jingweiduView;
    private ImageView yitijiOneView;
    private ImageView yitijiTwoView;
    private boolean isGaojiFind = false;
    private TextView resourcenameview;
    private TextView resoucedizhiview;
    private TextView resourcejingweiduview;
    private Button kejianguangbutton;
    private Button rechengxiangbutton;
    private Dialog resourceListDialog;
    private View resourceListInflater;
    private RecyclerView resourceListView;
    private LinearLayout zhenshiLayout;
    private TextView zhenshiButton;
    private TextView wubaoButton;
    private TextView zhenshiTextView;
    public int isReleas = 1;  //0疑似火情  1是真实火情  2是未处理
    public int isReleasList = 2;  //0疑似火情  1是真实火情  2是未处理 3是全部
    private TextView fenleiTextView;
    private LinearLayout fenleiLayout;
    private LinearLayout oneBodyFenleiLayout;
    private TextView weixingShijianView;
    private LinearLayout daohangLayout;
    private UserMenu usermenu;
    private Timer timer;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_new, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
        progressDialog = new ProgressDialog(getContext());

        user = new DbConfig(getContext()).getUser();
        weixingModelList = new ArrayList<>();
        oneBodyFireList = new ArrayList<>();
        oneBodyFireListNotHandle = new ArrayList<>();
        oneBodyFireListIsReal = new ArrayList<>();
        oneBodyFireFenleiList = new ArrayList<>();
        resourceListList = new ArrayList<>();
        currentWeixingModel = new WeixingModel();
        usermenu = new UserMenu();
        Log.e(TAG, "onActivityCreated: "+usermenu.getAppMapBtnSatelliteFirealarm() );
        getLocation();
        initDateTime();
        initView();
        bindView();
        //代码 注册 广播接收器
        Receiver = new myReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("zcd.voicerobot");
        getActivity().registerReceiver(Receiver, filter);

        //实例化IntentFilter对象
        IntentFilter fireFilter = new IntentFilter();
        fireFilter.addAction("fire_weixing_tengxun");
        fireWeixingReceiver = new FireWeixingReceiver();
        //注册广播接收
        getContext().registerReceiver(fireWeixingReceiver, fireFilter);

        autoStart();

        isShowSearchDialog = false;
        isTuisong = false;
        currentFireFindTime = 3;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // (1) 使用handler发送消息
                Log.e(TAG, "mapservice: 过了10秒" );

                Message message = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("what", TIME_CHANGE);
                message.setData(b);
                mHandler.sendMessage(message);
            }
        },0, 10000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        getWeixingDataFromSetvice();

        currentPage = 1;
        if (isReleasList == 1){
            //("真实火点");
            getonebodyDataFromSetviceReal();
        } else if (isReleasList == 2){
            //("未处理");
            getonebodyDataFromSetviceNotHandle();
        } else if(isReleasList == 3){
            //("全部");
            isAllTag = true;
            getonebodyDataFromSetviceReal();
            getonebodyDataFromSetviceNotHandle();
        }

        getResourcesListFromService();
    }


    private void autoStart() {
        SharedPreferences autoStart = getActivity().getSharedPreferences("autoStart",MODE_PRIVATE);
        String str = autoStart.getString("autoStart","");
        if(Objects.equals(str, "autoStart")){
            return;
        }
        showTipsDialog("为保证提供准确的位置服务需要您辅助开启自启动权限，现在前往开启吗?");
        SharedPreferences.Editor autoStartEdit = getActivity().getSharedPreferences("autoStart",MODE_PRIVATE).edit();
        autoStartEdit.putString("autoStart","autoStart");
        autoStartEdit.apply();
    }


    public void showTipsDialog(String msg) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(getActivity());
        mncTransDialog.setCancelable(false);
        View dialogView = LayoutInflater.from(getActivity()).inflate(com.haohai.platform.mapmodel.R.layout.dialog_tips, null, false);
        TextView message_text = (TextView) dialogView.findViewById(com.haohai.platform.mapmodel.R.id.message_text);
        message_text.setText(msg);
        final TextView tv_queren = (TextView) dialogView.findViewById(com.haohai.platform.mapmodel.R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(com.haohai.platform.mapmodel.R.id.tv_left);
        //确定
        RxViewAction.clickNoDouble(tv_queren).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();
                getActivity().startActivity(new Intent(getActivity(), AutoStartActivity.class));
            }
        });
        //取消
        RxViewAction.clickNoDouble(tv_left).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();
            }
        });
        mncTransDialog.show();
        Window window = mncTransDialog.getWindow();//对话框窗口
        window.setGravity(Gravity.CENTER);//设置对话框显示在屏幕中间
        window.setWindowAnimations(com.haohai.platform.mapmodel.R.style.dialog_style);//添加动画
        window.setContentView(dialogView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(Receiver);
        getActivity().unregisterReceiver(fireWeixingReceiver);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }


    /**
     * 卫星推送消息点击广播
     */
    class FireWeixingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");//12 火警    2 任务

            if(Objects.equals(type, "4")){
                weixingFireId = intent.getStringExtra("fire_id");
                obTime = intent.getStringExtra("ob_time");
                Log.e(TAG, "onReceive:weixingFireId " + weixingFireId);
                Log.e(TAG, "onReceive:obTime " + obTime);
                isShowSearchDialog = false;
                isTuisong = true;
                isGaojiFind = false;
                getWeixingDataFromSetvice();
            }else if(Objects.equals(type, "12")){
                isReleasList = 2;//0疑似火情  1是真实火情  2是未处理 3是全部
                fenleiTextView.setText("未处理");
                currentPage = 1;
                getonebodyDataFromSetviceNotHandle();
                onebodyListDialog.show();
            }else if(Objects.equals(type, "2")){
                //MainActivity已处理
            }


        }
    }

    private void bindView() {
        /**
         * 报警点击
         */
        RxViewAction.clickNoDouble(orderWarnImageView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        orderWarnImageView.setVisibility(View.GONE);
                        currentPage = 1;
                        if (isReleasList == 1){
                            //("真实火点");
                            getonebodyDataFromSetviceReal();
                        } else if (isReleasList == 2){
                            //("未处理");
                            getonebodyDataFromSetviceNotHandle();
                        } else if(isReleasList == 3){
                            //("全部");
                            isAllTag = true;
                            getonebodyDataFromSetviceReal();
                            getonebodyDataFromSetviceNotHandle();
                        }
                        onebodyListDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(ddrw_view)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ARouter.getInstance().build(RouteUtils.FireMissionList)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
        /**
         * 导航
         */
        RxViewAction.clickNoDouble(daohangLayout)
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
                        Log.e(TAG, "jingweiStr: "+jingweiStr );
                        if (!jingweiStr.isEmpty()) {
                            List<String> jingweiList = Arrays.asList(jingweiStr.split(","));
                            starweidu = jingweiList.get(1);
                            starjingdu = jingweiList.get(0);
                        }

                        LatLng latLng = new LatLngChange().transformFromWGSToGCJ(new LatLng(currentOneBodyFire.getAlarmLatitude(), currentOneBodyFire.getAlarmLongitude()));

                        Log.e(TAG, "call: starweidu=" + starweidu );
                        Log.e(TAG, "call:starjingdu= " + starjingdu );

                        Poi start = new Poi("", new com.amap.api.maps.model.LatLng(Double.parseDouble(starweidu), Double.parseDouble(starjingdu)), "");
                        Poi end = new Poi(currentOneBodyFire.getName(), new com.amap.api.maps.model.LatLng(latLng.latitude, latLng.longitude), "");
                        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                        params.setUseInnerVoice(true);
                        Log.e(TAG, "call: start" + starweidu );
                        Log.e(TAG, "call: end" + latLng.latitude );
                        AmapNaviPage.getInstance().showRouteActivity(getContext(), params, MapFragment.this);
                    }
                });
        /**
         * 分类点击
         */
        RxViewAction.clickNoDouble(oneBodyFenleiLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showOneBodyFenleiChangeDailog();
                    }
                });
        /**
         * 真实火警点击
         */
        RxViewAction.clickNoDouble(zhenshiButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isReleas = 1;
                        postIsReleaseFireToService();
                    }
                });
        /**
         * 误报火警点击
         */
        RxViewAction.clickNoDouble(wubaoButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isReleas = 0;
                        postIsReleaseFireToService();
                    }
                });
        /**
         * 时间点击
         */
        RxViewAction.clickNoDouble(gaojiStartimeText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isChooseStarTime = true;
                        showDataDialog();
                    }
                });

        RxViewAction.clickNoDouble(gaojiEndTimeText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isChooseStarTime = false;
                        showDataDialog();
                    }
                });

        /**
         * 一体机火点图片1点击
         */
        RxViewAction.clickNoDouble(yitijiOneView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ArrayList<String> picList = new ArrayList<>();
                        picList.add(currentOneBodyFire.getPicPath1().replace("10.10.13.3:80","183.230.142.120:8012"));
                        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(getActivity(), picList);
                        imagPagerUtil.setContentText("");
                        imagPagerUtil.show();
                    }
                });
        /**
         * 一体机火点图片2 点击
         */
        RxViewAction.clickNoDouble(yitijiTwoView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ArrayList<String> picList = new ArrayList<>();
                        picList.add(currentOneBodyFire.getPicPath2().replace("10.10.13.3:80","183.230.142.120:8012"));
                        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(getActivity(), picList);
                        imagPagerUtil.setContentText("");
                        imagPagerUtil.show();
                    }
                });

        /*/
         * 按钮点击
         */
        RxViewAction.clickNoDouble(findButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isGaojiFind = true;
                        //findFirePost();
                        getWeixingDataFromSetvice();
                    }
                });
        RxViewAction.clickNoDouble(chongzhiButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //时间初始化
                        gaojiStartimeText.setText("请输入开始时间");
                        gaojiEndTimeText.setText("请输入结束时间");
                    }
                });
        /**
         *  卫星火点图片1点击
         */
        RxViewAction.clickNoDouble(huodianOneImage)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ArrayList<String> picList = new ArrayList<>();
                        picList.add("http://web.ehaohai.com:2018" + currentWeixingModel.getLightImageAddress());
                        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(getActivity(), picList);
                        imagPagerUtil.setContentText("");
                        imagPagerUtil.show();
                    }
                });
        /**
         *  卫星火点图片2点击
         */
        RxViewAction.clickNoDouble(huodianTwoImage)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ArrayList<String> picList = new ArrayList<>();
                        picList.add("http://web.ehaohai.com:2018" +  currentWeixingModel.getIrImageAddress());
                        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(getActivity(), picList);
                        imagPagerUtil.setContentText("");
                        imagPagerUtil.show();
                    }
                });
        //卫星列表数据  分类点击
        RxViewAction.clickNoDouble(fenleiLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showLeibieChangeDailog();
                    }
                });


        // 顶部listdialog弹出的点击事件
        RxViewAction.clickNoDouble(mapButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mapChooseDialog.show();
                    }
                });
        //切换谷歌高程地图
/*        RxViewAction.clickNoDouble(gugeGaochengMap)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        gugeGaochengImage.setImageResource(R.drawable.ic_google_gc_select);
//                        gugeGaochengText.setTextColor(getResources().getColor(R.color.theme_primary));

                        gugeYingxiangImage.setImageResource(R.drawable.ic_google_sl);
                        gugeYingxiangText.setTextColor(getResources().getColor(R.color.c7));

                        tiandiShiliangImage.setImageResource(R.drawable.ic_tdt_sl);
                        tiandiShiliangText.setTextColor(getResources().getColor(R.color.c7));

                        tiandiYingxiangImage.setImageResource(R.drawable.ic_tdt_yx);
                        tiandiYingxiangText.setTextColor(getResources().getColor(R.color.c7));

                        dWebView.callHandler("google_gaocheng", new OnReturnValue<String>() {
                            @Override
                            public void onValue(String retValue) {
                                mapChooseDialog.hide();
                            }
                        });
                    }
                });*/
        //本地地图
        RxViewAction.clickNoDouble(gugeYingxiangMap)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        gugeGaochengImage.setImageResource(R.drawable.ic_google_gc);
//                        gugeGaochengText.setTextColor(getResources().getColor(R.color.c7));

                        gugeYingxiangImage.setImageResource(R.drawable.ic_google_sl_select);
                        gugeYingxiangText.setTextColor(getResources().getColor(R.color.map));

                        tiandiShiliangImage.setImageResource(R.drawable.ic_tdt_sl);
                        tiandiShiliangText.setTextColor(getResources().getColor(R.color.c12));

                        tiandiYingxiangImage.setImageResource(R.drawable.ic_tdt_yx);
                        tiandiYingxiangText.setTextColor(getResources().getColor(R.color.c12));

                        dWebView.callHandler("google_yingxiang", new OnReturnValue<String>() {
                            @Override
                            public void onValue(String retValue) {
                                Log.e(TAG, "onValue:  fanhiu2" + retValue);
                                mapChooseDialog.hide();
                            }
                        });

                    }
                });
        //天地图矢量地图
        RxViewAction.clickNoDouble(tiandiShiliangMap)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                                gugeGaochengImage.setImageResource(R.drawable.ic_google_gc);
//                                gugeGaochengText.setTextColor(getResources().getColor(R.color.c7));

                        gugeYingxiangImage.setImageResource(R.drawable.ic_google_sl);
                        gugeYingxiangText.setTextColor(getResources().getColor(R.color.c12));

                        tiandiShiliangImage.setImageResource(R.drawable.ic_tdt_sl_select);
                        tiandiShiliangText.setTextColor(getResources().getColor(R.color.map));

                        tiandiYingxiangImage.setImageResource(R.drawable.ic_tdt_yx);
                        tiandiYingxiangText.setTextColor(getResources().getColor(R.color.c12));
                        dWebView.callHandler("tianditu_shiliang", new OnReturnValue<String>() {
                            @Override
                            public void onValue(String retValue) {
                                Log.e(TAG, "onValue:  fanhiu3" + retValue);
                                mapChooseDialog.hide();
                            }
                        });
                    }
                });
        //天地图影像地图  天地图中文标记
        RxViewAction.clickNoDouble(tiandiYingxiangMap)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

//                        gugeGaochengImage.setImageResource(R.drawable.ic_google_gc);
//                        gugeGaochengText.setTextColor(getResources().getColor(R.color.c7));

                        gugeYingxiangImage.setImageResource(R.drawable.ic_google_sl);
                        gugeYingxiangText.setTextColor(getResources().getColor(R.color.c12));

                        tiandiShiliangImage.setImageResource(R.drawable.ic_tdt_sl);
                        tiandiShiliangText.setTextColor(getResources().getColor(R.color.c12));

                        tiandiYingxiangImage.setImageResource(R.drawable.ic_tdt_yx_select);
                        tiandiYingxiangText.setTextColor(getResources().getColor(R.color.map));

                        dWebView.callHandler("tianditu_yingxiang", new OnReturnValue<String>() {
                            @Override
                            public void onValue(String retValue) {
                                Log.e(TAG, "onValue:  fanhiu4" + retValue);
                                mapChooseDialog.hide();
                            }
                        });
                    }
                });
        /*卫星按钮点击*/
        //控制卫星按钮布局显示隐藏
        RxViewAction.clickNoDouble(weixingButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (isWeixingButtonShow) {
                            isWeixingButtonShow = false;
                            weixingButtonLayout.setVisibility(View.GONE);
                        } else {
                            isWeixingButtonShow = true;
                            weixingButtonLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

        //卫星设置点击
        RxViewAction.clickNoDouble(weixingShezhiView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), WeixingActivity.class));
                    }
                });
        //卫星查询点击
        RxViewAction.clickNoDouble(weixingChaxunView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        searchDialog.show();
                    }
                });
        //卫星列表点击
        RxViewAction.clickNoDouble(weixingLiebiaoView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        fireInfoListDialog.show();
                    }
                });
        //当前时间点击  1
        RxViewAction.clickNoDouble(currntTimeLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("当前时间");
                        searchDialog.hide();
                        currentFireFindTime = 1;
                        isShowSearchDialog = true;
                        isTuisong = false;
                        isGaojiFind = false;
                        getWeixingDataFromSetvice();
                    }
                });

        //一小时内点击 1
        RxViewAction.clickNoDouble(oneHoursLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("1小时内");
                        searchDialog.hide();
                        currentFireFindTime = 1;
                        isShowSearchDialog = true;
                        isTuisong = false;
                        isGaojiFind = false;
                        getWeixingDataFromSetvice();

                    }
                });

        //三小时内点击  3
        RxViewAction.clickNoDouble(threeHoursLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("3小时内");
                        searchDialog.hide();
                        currentFireFindTime = 3;
                        isShowSearchDialog = true;
                        isTuisong = false;
                        isGaojiFind = false;
                        getWeixingDataFromSetvice();

                    }
                });

        //一天内点击  24
        RxViewAction.clickNoDouble(oneDayLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("1天内");
                        searchDialog.hide();
                        currentFireFindTime = 24;
                        isShowSearchDialog = true;
                        isTuisong = false;
                        isGaojiFind = false;
                        getWeixingDataFromSetvice();

                    }
                });

        //三天内点击  72
        RxViewAction.clickNoDouble(threeDayLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("3天内");
                        searchDialog.hide();
                        currentFireFindTime = 72;
                        isShowSearchDialog = true;
                        isTuisong = false;
                        isGaojiFind = false;
                        getWeixingDataFromSetvice();

                    }
                });

        //五天内点击 120
        RxViewAction.clickNoDouble(fiveDayLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("5天内");
                        searchDialog.hide();
                        currentFireFindTime = 120;
                        isShowSearchDialog = true;
                        isTuisong = false;
                        isGaojiFind = false;
                        getWeixingDataFromSetvice();

                    }
                });

        //高级点击
        RxViewAction.clickNoDouble(gaojiSearchLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        weixingShijianView.setText("查询时间内");
                        searchDialog.hide();
                        isShowSearchDialog = true;
                        isTuisong = false;
                        gaojiDialog.show();

                    }
                });
        /*资源类型点击*/
        RxViewAction.clickNoDouble(retypeView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // startActivity(new Intent(getContext(), RecyclerViewActivity.class));
                        resourceListDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(ziyuanButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (isZiyuanButtonShow) {
                            isZiyuanButtonShow = false;
                            ziyuanButtonLayout.setVisibility(View.GONE);
                        } else {
                            isZiyuanButtonShow = true;
                            ziyuanButtonLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
        /*一体机点击*/
        RxViewAction.clickNoDouble(onebodyButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (isonebodyButtonShow) {
                            isonebodyButtonShow = false;
                            onebodyButtonLayout.setVisibility(View.GONE);
                        } else {
                            isonebodyButtonShow = true;
                            onebodyButtonLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
        RxViewAction.clickNoDouble(onebody_view)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        currentPage = 1;
//                        getonebodyDataFromSetviceReal();
//                        getonebodyDataFromSetviceNotHandle();
                        onebodyListDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(kejianguangbutton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String urlcode = kejianguangUrl;
                        if (urlcode.equals("")){
                            Toast.makeText(getContext(), "暂无可见光摄像头", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent=new Intent();
                        intent.putExtra("id",urlcode);
                        intent.putExtra("monitorId",kejianguangMId);
                        intent.setAction("video_play");
                        getContext().sendBroadcast(intent);
                        resourceinfoDialog.dismiss();
                    }
                });
        RxViewAction.clickNoDouble(rechengxiangbutton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String urlcode = rechengxiangUrl;
                        if (urlcode.equals("")){
                            Toast.makeText(getContext(), "暂无热成像摄像头", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent=new Intent();
                        intent.putExtra("id",urlcode);
                        intent.putExtra("monitorId",rechengxiangMId);
                        intent.setAction("video_play");
                        getContext().sendBroadcast(intent);
                        resourceinfoDialog.dismiss();
                    }
                });

        RxViewAction.clickNoDouble(share_image)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //分享
                        double[] doubles = new LatLngChangeNew().calWGS84toBD09(currentOneBodyFire.getAlarmLatitude(), currentOneBodyFire.getAlarmLongitude());
                        //初始化一个WXWebpageObject，填写url
                        WXWebpageObject webpage = new WXWebpageObject();
                        webpage.webpageUrl ="http://api.map.baidu.com/marker?location="+doubles[0]+","+doubles[1]+"&title="+currentOneBodyFire.getName()+"&output=html&src=webapp.baidu.openAPIdemo";
                        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
                        WXMediaMessage msg = new WXMediaMessage(webpage);
                        msg.title = "有新的火点提醒";
                        msg.description = currentOneBodyFire.getName();
                        Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_fire);
                        //创建byte的输出流
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(8192);
                        //关联Bitmap和输出流
                        thumbBmp.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                        //转换生成byte数组
                        byte [] bytes =byteArrayOutputStream.toByteArray();
                        msg.thumbData =bytes;

                        //构造一个Req
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage";
                        req.message =msg;

                        //调用 api 接口，发送数据到微信
                        CommonData.wxApi.sendReq(req);
                    }
                });

        RxViewAction.clickNoDouble(oneBodyKJGButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "getMonitorId: "+currentOneBodyFire.getMonitorId() );
                        Log.e(TAG, "getMonitorId: "+currentOneBodyFire.getVideoPath1());
                        rtspurl = currentOneBodyFire.getVideoPath1().replace("10.10.13.3:80","183.230.142.120:8012");
                        if (rtspurl.equals("rtmp")) {
                            Intent intent = new Intent(getContext(), PlayerRtspActivity.class);
                            //     intent.putExtra("PLAYER_URL", "rtmp://10.135.49.202:1935/playBack/af7ae7cb-d637-66cb-c1cc-ab86af9cc9af-main/1616382481/1616381276.flv?streamType=1&manufacturer=1&startTime=1616381246&endTime=1616381276");
                            intent.putExtra("PLAYER_URL", rtspurl);
                            intent.putExtra("PLAYER_NAME", cameraname);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), PlayerActivity.class);
                            //     intent.putExtra("PLAYER_URL", "rtmp://10.135.49.202:1935/playBack/af7ae7cb-d637-66cb-c1cc-ab86af9cc9af-main/1616382481/1616381276.flv?streamType=1&manufacturer=1&startTime=1616381246&endTime=1616381276");
                            intent.putExtra("PLAYER_URL", rtspurl);
                            intent.putExtra("PLAYER_NAME", cameraname);
                            startActivity(intent);
                        }
                    }
                });

        RxViewAction.clickNoDouble(oneBodyRCXButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "getMonitorId: "+currentOneBodyFire.getVideoPath2());
                        rtspurl = currentOneBodyFire.getVideoPath2().replace("10.10.13.3:80","183.230.142.120:8012");
                        if (rtspurl.equals("rtmp")) {
                            Intent intent = new Intent(getContext(), PlayerRtspActivity.class);
                            //     intent.putExtra("PLAYER_URL", "rtmp://10.135.49.202:1935/playBack/af7ae7cb-d637-66cb-c1cc-ab86af9cc9af-main/1616382481/1616381276.flv?streamType=1&manufacturer=1&startTime=1616381246&endTime=1616381276");
                            intent.putExtra("PLAYER_URL", rtspurl);
                            intent.putExtra("PLAYER_NAME", cameraname);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getContext(), PlayerActivity.class);
                            //     intent.putExtra("PLAYER_URL", "rtmp://10.135.49.202:1935/playBack/af7ae7cb-d637-66cb-c1cc-ab86af9cc9af-main/1616382481/1616381276.flv?streamType=1&manufacturer=1&startTime=1616381246&endTime=1616381276");
                            intent.putExtra("PLAYER_URL", rtspurl);
                            intent.putExtra("PLAYER_NAME", cameraname);
                            startActivity(intent);
                        }
                    }
                });
//        RxViewAction.clickNoDouble(ddrw_view)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        ARouter.getInstance().build(RouteUtils.FireMissionList)
//                                .withString("token",new DbConfig(getContext()).getUser().getToken())
//                                .navigation();
//                    }
//                });
        /*RxViewAction.clickNoDouble(shaixuanbutton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                });*/
    }

    private void showOneBodyFenleiChangeDailog() {
        //默认选中第一个  //0疑似火情  1是真实火情  2是未处理 3是全部
        final String[] items = {"全部", "未处理", "真实火点"};
        //isReleasList = 3;
        builder = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.ic_launcher).setTitle("火情分类")
                .setSingleChoiceItems(items,choose1 , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: 类别choose---" + i);
                        choose1 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: choose1=" +choose1);
                        oneBodyFireFenleiList.clear();
                        if (choose1 == 2){
                            isReleasList = 1;
                            fenleiTextView.setText("真实火点");
                            currentPage = 1;
                            getonebodyDataFromSetviceReal();
                        } else if (choose1 == 1){
                            isReleasList = 2;
                            fenleiTextView.setText("未处理");
                            currentPage = 1;
                            getonebodyDataFromSetviceNotHandle();
                        } else {
                            isReleasList = 3;
                            fenleiTextView.setText("全部");
                            currentPage = 1;
                            isAllTag = true;
                            getonebodyDataFromSetviceReal();
                            getonebodyDataFromSetviceNotHandle();
                        }
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //选择状态回正
                        if(isReleasList == 1){
                            choose1 = 2;
                        }else if(isReleasList == 2){
                            choose1 = 1;
                        }else if (isReleasList == 3){
                            choose1 = 0;
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * 是否是真实火情数据提交
     */
    private void postIsReleaseFireToService() {

        showDialogProgress(progressDialog, "提交中...");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fireId", currentOneBodyFire.getId());
            jsonObject.put("type", isReleas);
            jsonObject.put("isAndroid", 2);
        } catch (JSONException e) {
        }
        RequestParams params = null;
        if (currentOneBodyFire.getType() == 2) {
            params = new RequestParams(RequestUtils.REQUEST_URL + "fire/api/monitorFirealarm/realOrError");
        }else if (currentOneBodyFire.getType() == 4) {
            params = new RequestParams(RequestUtils.REQUEST_URL + "fire/api/BuildingFirealarm/realOrError");
        }else if (currentOneBodyFire.getType() == 5) {
            params = new RequestParams(RequestUtils.REQUEST_URL + "fire/api/StealingFirealarm/realOrError");
        }

        // params.setBodyContent(jsonObject.toString());
        params.addParameter("id", currentOneBodyFire.getId());
        params.addParameter("type", isReleas);
        params.addParameter("isAndroid", 2);
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "resource: --"  + params);
        x.http().request(HttpMethod.GET,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: 真实火点:" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        Toast.makeText(getContext(), "上报成功", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < oneBodyFireList.size(); i++) {
                            if (oneBodyFireList.get(i).getId().equals(currentOneBodyFire.getId())) {
                                oneBodyFireList.get(i).setIsReal(isReleas);
                            }
                        }
                        initOneBodyFireModelData();

                        //处理之后从移除该火情
                        for (int i = 0; i < oneBodyFireFenleiList.size(); i++) {
                            if (oneBodyFireFenleiList.get(i).getId().equals(currentOneBodyFire.getId())) {
                                oneBodyFireFenleiList.remove(i);
                                break;
                            }
                        }
                        if (isReleas == 0){//0疑似火情  1是真实火情  2是未处理
                            //处理之后从移除该火情

                            for (int i = 0; i < oneBodyFireList.size(); i++) {
                                if (oneBodyFireList.get(i).getId().equals(currentOneBodyFire.getId())) {
                                    oneBodyFireList.remove(i);
                                    break;
                                }
                            }
                        }

                        Message message = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("id", oneBodyFireFenleiList.get(0).getId());
                        b.putInt("what",DIALOG_ONEBODY_FIRE_SHOW);
                        message.setData(b);
                        mHandler.sendMessage(message);

                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onStart() {
        super.onStart();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        orderWarnImageView = ((ImageView) getView().findViewById(R.id.order_warn_image));
        dWebView = ((DWebView) getView().findViewById(R.id.dwebview));
        dWebView.getSettings().setJavaScriptEnabled(true);

        String url = "file:///android_asset/map/mars_demo.html";
        //url：网页地址；name/pwd:cookie信息
        // String isUser = SPUtils.getInstance().getString("userContent");
        dWebView.loadUrl(url);
        dWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //加载逃生出口资源点
                // getDataFromService();
                //加载道路
                // getRoadDataFromService();
            }
        });


        dWebView.addJavascriptInterface(this, "jk");
        JsApi jsApi = new JsApi(getContext());
        jsApi.setListener(this);
        dWebView.addJavascriptObject(jsApi, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dWebView.setWebContentsDebuggingEnabled(true);
        }
        //添加跨域支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            dWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            dWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = dWebView.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(dWebView.getSettings(), true);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // String mapUrl = "http://www.google.cn/maps/vt?lyrs=y@189&gl=cn&x={x}&y={y}&z={z}http://www.google.cn/maps/vt?lyrs=y@189&gl=cn&x={x}&y={y}&z={z}"; // 瓦片路径
        String mapUrl = "assets/qds_2005261619/{z}/{x}/{y}.png"; // 瓦片路径
        dWebView.callHandler("initMap", new Object[]{mapUrl}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });
        gaojiFindDialog = new ProgressDialog(getContext());
        date = new StringBuffer();
        endDate = new StringBuffer();
         /*资源类型*/
        retypeView = ((TextView) getView().findViewById(R.id.retype_view));
        ziyuanLayout = ((LinearLayout) getView().findViewById(R.id.ziyuan_layout));
        ziyuanButton = ((TextView) getView().findViewById(R.id.ziyuan_button));
        onebodyButtonLayout = ((LinearLayout) getView().findViewById(R.id.onebody_button_layout));
        if (isonebodyButtonShow) {
            onebodyButtonLayout.setVisibility(View.VISIBLE);
        } else {
            onebodyButtonLayout.setVisibility(View.GONE);
        }
        ziyuanButtonLayout = ((LinearLayout) getView().findViewById(R.id.ziyuan_button_layout));
        if (isZiyuanButtonShow) {
            ziyuanButtonLayout.setVisibility(View.VISIBLE);
        } else {
            ziyuanButtonLayout.setVisibility(View.GONE);
        }
        /*一体机*/
        onebody_view=((TextView) getView().findViewById(R.id.onebody_view));
        /*调度任务*/
        ddrw_view=((TextView) getView().findViewById(R.id.ddrw_button));
        /*卫星布局加载*/
        weixingButton = ((TextView) getView().findViewById(R.id.weixing_button));
        weixingButtonLayout = ((LinearLayout) getView().findViewById(R.id.weixing_button_layout));
        weixinLayout = ((LinearLayout) getView().findViewById(weixing_layout));
        weixingShezhiView = ((TextView) getView().findViewById(R.id.weixing_shezhi_view));
        weixingChaxunView = ((TextView) getView().findViewById(R.id.weixing_chaxun_ciew));
        weixingLiebiaoView = ((TextView) getView().findViewById(R.id.weixing_list_view));

        if (isWeixingButtonShow) {
            weixingButtonLayout.setVisibility(View.VISIBLE);
        } else {
            weixingButtonLayout.setVisibility(View.GONE);
        }
        /**
         * 卫星查询信息 dialog
         */
        searchDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        searchInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_weixing, null);
        searchInflater.setMinimumWidth(10000);
        oneHoursLayout = ((LinearLayout) searchInflater.findViewById(R.id.one_hours));
        currntTimeLayout = ((LinearLayout) searchInflater.findViewById(R.id.current_time));
        threeHoursLayout = ((LinearLayout) searchInflater.findViewById(R.id.three_hours));
        oneDayLayout = ((LinearLayout) searchInflater.findViewById(R.id.one_day));
        threeDayLayout = ((LinearLayout) searchInflater.findViewById(R.id.three_day));
        fiveDayLayout = ((LinearLayout) searchInflater.findViewById(R.id.five_day));
        gaojiSearchLayout = ((LinearLayout) searchInflater.findViewById(R.id.gaoji_search));
        searchDialog.setContentView(searchInflater);
        Window searchDialogWindow = searchDialog.getWindow();
        searchDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lpSearch = searchDialogWindow.getAttributes();
        searchDialogWindow.setAttributes(lpSearch);
        searchDialog.setCanceledOnTouchOutside(true);
        /**
         * 火警信息 dialog
         */
        fireInfoListDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        fireInfoListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fireinfo_list, null);
        fireInfoListInflater.setMinimumWidth(10000);
        fireCountText = ((TextView) fireInfoListInflater.findViewById(R.id.fire_count_textview));
        weixingListView = ((RecyclerView) fireInfoListInflater.findViewById(R.id.fire_info_listview));
        fenleiLayout = ((LinearLayout) fireInfoListInflater.findViewById(R.id.fenlei_layout));
        fenleiView = ((TextView) fireInfoListInflater.findViewById(R.id.fenlei_View));
        weixingShijianView = ((TextView) fireInfoListInflater.findViewById(R.id.weixing_shijian_view));
        weixingShijianView.setText("3小时内");
        fireInfoListDialog.setContentView(fireInfoListInflater);
        Window fireListWindow = fireInfoListDialog.getWindow();
        fireListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams fireListLp = fireListWindow.getAttributes();

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        fireListLp.height = (int) (height * 0.8);
        fireListWindow.setAttributes(fireListLp);
        fireInfoListDialog.setCanceledOnTouchOutside(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        weixingListView.setLayoutManager(linearLayoutManager);
        weixingAdapter = new MultiTypeAdapter(weixingItems);
        register();
        weixingListView.setAdapter(weixingAdapter);
        assertHasTheSameAdapter(weixingListView, weixingAdapter);

        /**
         * 火点信息dialog
         */
        weixingFireDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        weixingFireInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fire_weixing, null);
        weixingFireInflater.setMinimumWidth(10000);
        fireAddressText = ((TextView) weixingFireInflater.findViewById(R.id.address_text));
        fireTimeText = ((TextView) weixingFireInflater.findViewById(R.id.time_text));
        jingWeiText = ((TextView) weixingFireInflater.findViewById(R.id.jing_wei_text));
        kexinText = ((TextView) weixingFireInflater.findViewById(R.id.kexin_text));
        mianjiText = ((TextView) weixingFireInflater.findViewById(R.id.mianji_text));
        cishuText = ((TextView) weixingFireInflater.findViewById(R.id.cishu_text));
        leixingText = ((TextView) weixingFireInflater.findViewById(R.id.leixing_text));
        shujuyuanText = ((TextView) weixingFireInflater.findViewById(R.id.shujuyuan_text));
        huodianCodeText = ((TextView) weixingFireInflater.findViewById(R.id.huodian_code_text));
        fankuiText = ((TextView) weixingFireInflater.findViewById(R.id.fankui_text));
        huodianOneImage = ((ImageView) weixingFireInflater.findViewById(R.id.huodian_image_one));
        huodianTwoImage = ((ImageView) weixingFireInflater.findViewById(R.id.huodian_image_two));
        xiangyuanmianjiView = ((TextView) weixingFireInflater.findViewById(R.id.xiangyuanmianji_view));
        xiangyuanshuView = ((TextView) weixingFireInflater.findViewById(R.id.xiangyuanshu_view));
        weixingFireDialog.setContentView(weixingFireInflater);
        Window fireDialogWindow = weixingFireDialog.getWindow();
        fireDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lpFire = fireDialogWindow.getAttributes();
        fireDialogWindow.setAttributes(lpFire);
        weixingFireDialog.setCanceledOnTouchOutside(true);

        /**
         * 一体机 dialog
         */
        onebodyListDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        onebodyListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_onebody_list, null);
        onebodyListInflater.setMinimumWidth(10000);
        onebodyRefreshLayout = ((SmartRefreshLayout) onebodyListInflater.findViewById(R.id.refreshLayout));
        onebodyListView = ((RecyclerView) onebodyListInflater.findViewById(R.id.onebody_info_listview));
        onebodyListDialog.setContentView(onebodyListInflater);
        onebodyLayout =((LinearLayout) getView().findViewById(R.id.onebady_layout));
        onebodyButton = ((TextView) getView().findViewById(R.id.onebody_button));
        fenleiTextView = ((TextView) onebodyListInflater.findViewById(R.id.fenlei_view));
        oneBodyFenleiLayout = ((LinearLayout) onebodyListInflater.findViewById(R.id.fenlei_layout));
        //oneBodyFenleiLayout.setVisibility(View.GONE);
        Window onebodyListWindow = onebodyListDialog.getWindow();
        onebodyListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams onebodyListLp = onebodyListWindow.getAttributes();

        WindowManager obwm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int onebodyheight = obwm.getDefaultDisplay().getHeight();
        onebodyListLp.height = (int) (onebodyheight * 0.8);
        onebodyListWindow.setAttributes(onebodyListLp);
        onebodyListDialog.setCanceledOnTouchOutside(true);

        LinearLayoutManager onebodylinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        onebodyListView.setLayoutManager(onebodylinearLayoutManager);
        onebodyAdapter = new MultiTypeAdapter(onebodyItems);

        OneBodyFireViewBinder oneBodyFireViewBinder = new OneBodyFireViewBinder();
        oneBodyFireViewBinder.setListener(this);
        onebodyAdapter.register(OneBodyFire.class, oneBodyFireViewBinder);
        onebodyAdapter.register(Empty.class,new EmptyViewBinder());
        onebodyListView.setAdapter(onebodyAdapter);
        assertHasTheSameAdapter(onebodyListView, onebodyAdapter);
        /*onebody_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getonebodyDataFromSetvice();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onebody_swipe.setRefreshing(false);
                    }
                },1000);
            }
        });
        //加载更多
        onebodyListView.setOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                currentPage += 1;
                Log.i(TAG, "onLoadMore: "+currentPage);
                progressDialog.dismiss();
                getonebodyDataFromSetvice();

            }
        });*/

        onebodyRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        onebodyRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        onebodyRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000);
                currentPage = 1;

                if (isReleasList == 1){
                    //("真实火点");
                    getonebodyDataFromSetviceReal();
                } else if (isReleasList == 2){
                    //("未处理");
                    getonebodyDataFromSetviceNotHandle();
                } else if(isReleasList == 3){
                    //("全部");
                    isAllTag = true;
                    getonebodyDataFromSetviceReal();
                    getonebodyDataFromSetviceNotHandle();
                }

            }
        });
        onebodyRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000);
                currentPage += 1;
                Log.i(TAG, "onLoadMore: "+currentPage);
                progressDialog.dismiss();

                if (isReleasList == 1){
                    //("真实火点");
                    getonebodyDataFromSetviceReal();
                } else if (isReleasList == 2){
                    //("未处理");
                    getonebodyDataFromSetviceNotHandle();
                } else if(isReleasList == 3){
                    //("全部");
                    isAllTag = true;
                    getonebodyDataFromSetviceReal();
                    getonebodyDataFromSetviceNotHandle();
                }
            }
        });
        /**
         *  一体机火点详细信息
         */
        oneBodyFireDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        oneBodyFireInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fire_onebody, null);
        oneBodyFireInflater.setMinimumWidth(10000);
        mingchengView = ((TextView) oneBodyFireInflater.findViewById(R.id.mingcheng_view));
        share_image = ((ImageView) oneBodyFireInflater.findViewById(R.id.share_image));
        dizhiView = ((TextView) oneBodyFireInflater.findViewById(R.id.dizhi_view));
        shijianView = ((TextView) oneBodyFireInflater.findViewById(R.id.shijian_view));
        jingweiduView = ((TextView) oneBodyFireInflater.findViewById(R.id.jingweidu_view));
        yitijiOneView = ((ImageView) oneBodyFireInflater.findViewById(R.id.yitiji_one_image));
        yitijiTwoView = ((ImageView) oneBodyFireInflater.findViewById(R.id.yitiji_two_image));
        zhenshiLayout = ((LinearLayout) oneBodyFireInflater.findViewById(R.id.zhenshi_layout));
        zhenshiButton = ((TextView) oneBodyFireInflater.findViewById(R.id.zhenshi_button));
        wubaoButton = ((TextView) oneBodyFireInflater.findViewById(R.id.wubao_button));
        zhenshiTextView = ((TextView) oneBodyFireInflater.findViewById(R.id.zhenshi_text_view));
        daohangLayout = ((LinearLayout) oneBodyFireInflater.findViewById(R.id.daohang_layout));
        oneBodyKJGButton = ((TextView) oneBodyFireInflater.findViewById(R.id.kejianguang_button));
        oneBodyRCXButton = ((TextView) oneBodyFireInflater.findViewById(R.id.rechengxiang_button));

        oneBodyFireDialog.setContentView(oneBodyFireInflater);
        Window oneBodyfireDialogWindow = oneBodyFireDialog.getWindow();
        oneBodyfireDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams oneBodyLpFire = oneBodyfireDialogWindow.getAttributes();
        oneBodyfireDialogWindow.setAttributes(oneBodyLpFire);
        oneBodyFireDialog.setCanceledOnTouchOutside(true);


        /**
         * 资源点列表 dialog
         */
        resourceListDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        resourceListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_resource_list, null);
        resourceListInflater.setMinimumWidth(10000);
        resourceListView = ((RecyclerView) resourceListInflater.findViewById(R.id.resource_listview));
        resourceListDialog.setContentView(resourceListInflater);
        Window resourceListWindow = resourceListDialog.getWindow();
        resourceListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceListLp = resourceListWindow.getAttributes();

        WindowManager resourcewm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int resourceheight = resourcewm.getDefaultDisplay().getHeight();
        resourceListLp.height = (int) (resourceheight * 0.8);
        resourceListWindow.setAttributes(fireListLp);
        resourceListDialog.setCanceledOnTouchOutside(true);

        LinearLayoutManager resourcelinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        resourceListView.setLayoutManager(resourcelinearLayoutManager);
        resourceAdapter = new MultiTypeAdapter(resourceItems);


        ResourceListViewBinder resourceListViewBinder = new ResourceListViewBinder();
        resourceListViewBinder.setListener(this);
        resourceAdapter.register(ResourceList.class, resourceListViewBinder);

        resourceListView.setAdapter(resourceAdapter);
        assertHasTheSameAdapter(resourceListView, resourceAdapter);

        /**
         *  资源点详细信息
         */
        resourceinfoDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        resourceInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_resource_info, null);
        resourceInflater.setMinimumWidth(10000);
        resourcenameview = ((TextView) resourceInflater.findViewById(R.id.resourcename_view));
        resoucedizhiview = ((TextView) resourceInflater.findViewById(R.id.resoucedizhi_view));
        resourcejingweiduview = ((TextView) resourceInflater.findViewById(R.id.resourcejingweidu_view));
        kejianguangbutton = ((Button) resourceInflater.findViewById(R.id.kejianguang_button));
        rechengxiangbutton = ((Button) resourceInflater.findViewById(R.id.rechengxiang_button));

        resourceinfoDialog.setContentView(resourceInflater);
        Window resourceDialogWindow = resourceinfoDialog.getWindow();
        resourceDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceLpFire = resourceDialogWindow.getAttributes();
        resourceDialogWindow.setAttributes(resourceLpFire);
        resourceinfoDialog.setCanceledOnTouchOutside(true);
        /** 高级查询
         */
        gaojiDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        gaojiInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gaoji,null);
        gaojiInflater.setMinimumWidth(10000);
        gaojiDialog.setContentView(gaojiInflater);
        Window gaojiDialogWindow = gaojiDialog.getWindow();
        gaojiDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams gaojiLp = gaojiDialogWindow.getAttributes();

        WindowManager wmGaoji = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int heightGaoji = wmGaoji.getDefaultDisplay().getHeight();
        gaojiLp.height = (int) (heightGaoji * 0.8);
        gaojiDialogWindow.setAttributes(gaojiLp);
        gaojiDialog.setCanceledOnTouchOutside(true);
        //时间
        gaojiStarTimeLayout = ((LinearLayout) gaojiInflater.findViewById(R.id.gaoji_startime_layout));
        gaojiEndTimeLayout = ((LinearLayout) gaojiInflater.findViewById(R.id.gaoji_endtime_layout));
        gaojiStartimeText = ((TextView) gaojiInflater.findViewById(R.id.gaoji_startime_text));
        gaojiEndTimeText = ((TextView) gaojiInflater.findViewById(R.id.gaoji_endtime_text));
        //重置  查询
        chongzhiButton = ((TextView) gaojiInflater.findViewById(R.id.chongzhi_button));
        findButton = ((TextView) gaojiInflater.findViewById(R.id.find_button));
        /*地图布局加载*/

        mapButton = ((ImageView) getView().findViewById(R.id.map_list_button));
        /**
         * 地图切换 dialog
         */
        mapChooseDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyleLeft);
        mapChooseInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_map_choose_new, null);
        mapChooseInflater.setMinimumWidth(10000);
        //gugeGaochengMap = ((LinearLayout) mapChooseInflater.findViewById(R.id.guge_gaocheng));
        gugeYingxiangMap = ((LinearLayout) mapChooseInflater.findViewById(R.id.guge_yingxiang));
        tiandiShiliangMap = ((LinearLayout) mapChooseInflater.findViewById(R.id.tiandi_shiliang));
        tiandiYingxiangMap = ((LinearLayout) mapChooseInflater.findViewById(R.id.tiandi_yingxiang));
        gugeYingxiangImage = ((ImageView) mapChooseInflater.findViewById(R.id.guge_yingxiang_image));
        gugeYingxiangText = ((TextView) mapChooseInflater.findViewById(R.id.guge_yingxiang_text));
//        gugeGaochengImage = ((ImageView) mapChooseInflater.findViewById(R.id.guge_gaocheng_image));
//        gugeGaochengText = ((TextView) mapChooseInflater.findViewById(R.id.gugegaocheng_text));
        tiandiShiliangImage = ((ImageView) mapChooseInflater.findViewById(R.id.tiandi_shiliang_image));
        tiandiShiliangText = ((TextView) mapChooseInflater.findViewById(R.id.tiandi_shiliang_text));
        tiandiYingxiangImage = ((ImageView) mapChooseInflater.findViewById(R.id.tiandi_yingxiang_image));
        tiandiYingxiangText = ((TextView) mapChooseInflater.findViewById(R.id.tiandi_yingxiang_text));
        mapChooseDialog.setContentView(mapChooseInflater);
        Window dialogWindow = mapChooseDialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager wmMap = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wmMap.getDefaultDisplay().getWidth();
        int height1 = wmMap.getDefaultDisplay().getHeight();
        lp.width = (int) (width * 0.65);
        lp.height = (int) height1;
        dialogWindow.setAttributes(lp);
        mapChooseDialog.setCanceledOnTouchOutside(true);


        final JSONObject jsonObject = new JSONObject();
        JSONObject posObj = new JSONObject();
        try {
            posObj.put("lat", currentLatitude);
            posObj.put("lng", currentLongitude);
            jsonObject.put("position", posObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("GPSposition", jsonObject.toString());
        dWebView.callHandler("GPSflyto", new Object[]{new Gson().toJson(jsonObject.toString())}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });
        Log.e(TAG, "initView: "+usermenu.getAppMapBtnTask());
        if (usermenu.getAppMapBtnSatelliteFirealarm()){
            weixinLayout.setVisibility(View.VISIBLE);
        }else {
            weixinLayout.setVisibility(View.GONE);
        }
        if (usermenu.getAppSatelliteFirealarmBtnList()){
            weixingLiebiaoView.setVisibility(View.VISIBLE);
        }else {
            weixingLiebiaoView.setVisibility(View.GONE);
        }if (usermenu.getAppSatelliteFirealarmBtnQuery()){
            weixingChaxunView.setVisibility(View.VISIBLE);
        }else {
            weixingChaxunView.setVisibility(View.GONE);
        }if (usermenu.getAppSatelliteFirealarmBtnSetting()){
            weixingShezhiView.setVisibility(View.VISIBLE);
        }else {
            weixingShezhiView.setVisibility(View.GONE);
        }if (usermenu.getAppMapBtnResource()){
            ziyuanLayout.setVisibility(View.VISIBLE);
        }else {
            ziyuanLayout.setVisibility(View.GONE);
        }if (usermenu.getAppMapBtnFirealarm()){
            onebodyLayout.setVisibility(View.VISIBLE);
        }else {
            onebodyLayout.setVisibility(View.GONE);
        }if (usermenu.getAppMapBtnTask()){
            ddrw_view.setVisibility(View.VISIBLE);
        }else {
            ddrw_view.setVisibility(View.GONE);
        }
    }

    private void showLeibieChangeDailog() {
        //默认选中第一个
        final String[] items = {"按时间分类", "按编号分类"};

        if (currentFireListType == 1) {
            choose1 = 0;
        } else {
            choose1 = 1;
        }
        builder = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.ic_launcher).setTitle("单选列表")
                .setSingleChoiceItems(items, choose1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: 类别choose---" + i);
                        choose1 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (choose1 == 0) { //按时间分类
                            currentFireListType = 1;
                            fenleiView.setText("按时间分类");
                        } else {  //按编号分类
                            currentFireListType = 2;
                            fenleiView.setText("按编号分类");
                        }
                        isTuisong = false;
                        initWeixingData();
                    }
                });
        builder.create().show();
    }

    private void register() {
        WeixingModelViewBinder weixingModelViewBinder = new WeixingModelViewBinder();
        weixingModelViewBinder.setListener(this);
        weixingAdapter.register(WeixingModel.class, weixingModelViewBinder);
        weixingAdapter.register(Empty.class, new EmptyViewBinder());
    }

    /**
     * 从服务器获取卫星数据
     */
    private void getWeixingDataFromSetvice() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        if (isShowSearchDialog) {
            showDialogProgress(progressDialog, "查询中...");
        }
        Setting setting = new DbConfig(getContext()).getSetting();
        final JSONObject jsonObject = new JSONObject();
        try {

            if (isTuisong) {     //是推送查询
                jsonObject.put("endTime", format.format(c.getTime()).replace(" ", "T"));
                jsonObject.put("startTime", obTime);
            } else {     //不是推送查询
                if (isGaojiFind){       //gaoji
                    String startTimeStr = "";
                    if (!gaojiStartimeText.getText().toString().equals("请输入开始时间")) {
                        startTimeStr = gaojiStartimeText.getText().toString();
                    }
                    String endTimeStr = "";
                    if (!gaojiEndTimeText.getText().toString().equals("请输入结束时间")){
                        endTimeStr = gaojiEndTimeText.getText().toString();
                    }
                    jsonObject.put("endTime", endTimeStr);
                    jsonObject.put("startTime",startTimeStr);
                }else {
                    jsonObject.put("endTime", format.format(c.getTime()).replace(" ", "T"));
                    c.add(Calendar.HOUR, -currentFireFindTime);    //获取currentFireFindTime小时之前的时间
                    //jsonObject.put("startTime","2020-11-08T10:00:00");   //测试用 上线要改过来
                    jsonObject.put("startTime", format.format(c.getTime()).replace(" ", "T"));
                }

            }


            if (!user.getGridNo().isEmpty()) {
                if (user.getGridNo().contains("0000")) {
                    jsonObject.put("provinceCode", user.getGridNo());
                } else if (user.getGridNo().contains("00")) {
                    jsonObject.put("cityCode", user.getGridNo());
                } else {
                    jsonObject.put("countyCode", user.getGridNo());
                }
            }
            if (setting != null) {
                jsonObject.put("satellite", "");
                jsonObject.put("landType", "");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "fire/api/satelliteFirealarm/list");

        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + user.getToken());
        Log.e(TAG, "getWeixingDataFromSetvice: " + params);
        Log.e(TAG, "getWeixingDataFromSetvice: " + jsonObject.toString());
        Log.e(TAG, "getWeixingDataFromSetvice: " + user.getToken());
        params.setConnectTimeout(100000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:weixing " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        Gson gson = new Gson();
                        weixingModelList.clear();
                        weixingModelList = gson.fromJson(String.valueOf(data), new TypeToken<List<WeixingModel>>() {
                        }.getType());

                        initWeixingData();
                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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
                if (isShowSearchDialog) {
                    progressDialog.dismiss();
                }

            }
        });

    }

    /**
     * 从服务器获取资源列表
     */
    private void getResourcesListFromService() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isDisplay", "1");
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/resourceList/list");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        Gson gson = new Gson();
                        resourceListList.clear();
                        resourceListList = gson.fromJson(String.valueOf(data), new TypeToken<List<ResourceList>>() {
                        }.getType());

                        for (int i = 0; i < resourceListList.size(); i++) {
                            resourceListList.get(i).setCheck(false);
                        }
                        initResourceListData();
                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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

            }
        });
    }


    private int alarmTotalSize = 0;

    /**
     * 从服务器获取一体机数据(自动)
     */
    private void getonebodyDataFromSetviceAuto() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();

        if (isShowSearchDialog) {
            showDialogProgress(progressDialog, "查询中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            jsonObject.put("dto", dto);
            jsonObject.put("limit", 50);
            jsonObject.put("page", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams( RequestUtils.REQUEST_URL +"/fire/api/monitorFirealarm/page");


        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + user.getToken());
        Log.e(TAG, "getonebodyDataFromSetvice: " + params);
        Log.e(TAG, "getonebodyDataFromSetvice: " + jsonObject.toString());
        Log.e(TAG, "getonebodyDataFromSetvice: " + user.getToken());
        params.setConnectTimeout(100000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getonebodyDataonSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        int totalSize = getJsonObj.getInt("totalSize");
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        List<OneBodyFire> list = new Gson().fromJson(String.valueOf(dataList), new TypeToken<List<OneBodyFire>>() {
                        }.getType());
                        Log.i(TAG, "dataList: "+dataList);
                        if ( currentFirstFire!=null && (!currentFirstFire.getId().equals(list.get(0).getId())) ) {   //有新的报警数据
//                            if(list.get(0).getIsReal()==1){
                                orderWarnImageView.setVisibility(View.VISIBLE);
                                Intent intenta = new Intent(getActivity(), com.ruyiruyi.rylibrary.service.BackgroundMp3Service.class);
                                intenta.putExtra("type", "14");
                                getActivity().startService(intenta);

                                currentFirstFire = list.get(0);
//                            }

                            initOneBodyFireData();
                            currentPage = 1;

                            if (isReleasList == 1){
                                //("真实火点");
                                getonebodyDataFromSetviceReal();
                            } else if (isReleasList == 2){
                                //("未处理");
                                getonebodyDataFromSetviceNotHandle();
                            } else if(isReleasList == 3){
                                //("全部");
                                isAllTag = true;
                                getonebodyDataFromSetviceNotHandle();
                                getonebodyDataFromSetviceReal();
                            }

                        }else{
                            currentFirstFire = list.get(0);
                        }

                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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
                if (isShowSearchDialog) {
                    progressDialog.dismiss();
                }

            }
        });

    }

    private OneBodyFire currentFirstFire;
    private boolean isAllTag = false;
    private int isAllNum = 0;
    /**
     * 从服务器获取一体机数据-真实
     */
    private void getonebodyDataFromSetviceReal() {
        try{
            showDialogProgress(progressDialog, "查询中...");
        }catch(Exception e){
            Log.e(TAG, "getonebodyDataFromSetviceReal: " + e );
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            //已处理 真实火警
            dto.put("isHandle","1");
            dto.put("isReal","1");
            jsonObject.put("dto", dto);
            jsonObject.put("limit", 20);
            jsonObject.put("page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams( RequestUtils.REQUEST_URL +"/fire/api/monitorFirealarm/page");


        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + user.getToken());
        Log.e(TAG, "getonebodyDataFromSetvice: " + params);
        Log.e(TAG, "getonebodyDataFromSetvice: " + jsonObject.toString());
        Log.e(TAG, "getonebodyDataFromSetvice: " + user.getToken());
        params.setConnectTimeout(100000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getonebodyDataonSuccess: qc 真实 params " + params);
                Log.e(TAG, "getonebodyDataonSuccess: qc 真实" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        int totalSize = getJsonObj.getInt("totalSize");
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Log.e(TAG, "dataList: "+dataList);
                        Gson gson = new Gson();

                        if(currentPage == 1){
                            oneBodyFireList.clear();
                        }
                        List<OneBodyFire> oneBodyFireAllList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<OneBodyFire>>() {
                        }.getType());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //已处理 真实火警
                                oneBodyFireListIsReal.clear();
                                oneBodyFireListIsReal.addAll(oneBodyFireAllList);
                                oneBodyFireList.addAll(oneBodyFireListIsReal);

                                oneBodyFireFenleiList.clear();
                                if(isAllTag){
                                    if(isAllNum == 0){
                                        isAllNum++;

                                        if(isReleasList == 1){
                                            //真实火点
                                            fenleiTextView.setText("真实火点");
                                            oneBodyFireFenleiList.addAll(oneBodyFireList);
                                        }else if(isReleasList == 2){
                                            //未处理 不可能
                                            fenleiTextView.setText("未处理");
                                        }else{
                                            //全部
                                            fenleiTextView.setText("全部");
                                            oneBodyFireFenleiList.addAll(oneBodyFireList);
                                        }
                                        return;
                                    }else{
                                        isAllTag = false;
                                        isAllNum = 0;
                                    }
                                }

                                if(isReleasList == 1){
                                    //真实火点
                                    fenleiTextView.setText("真实火点");
                                    oneBodyFireFenleiList.addAll(oneBodyFireList);
                                }else if(isReleasList == 2){
                                    //未处理 不可能
                                    fenleiTextView.setText("未处理");
                                }else{
                                    //全部
                                    fenleiTextView.setText("全部");
                                    oneBodyFireFenleiList.addAll(oneBodyFireList);
                                }
                                if(oneBodyFireFenleiList.size()!=0){
                                    OneBodyFire bodyFire = oneBodyFireFenleiList.get(0);
                                    final JSONObject jsonObject = new JSONObject();
                                    JSONObject posObj = new JSONObject();
                                    try {
                                        posObj.put("lat", bodyFire.getAlarmLatitude());
                                        posObj.put("lng", bodyFire.getAlarmLongitude());
                                        jsonObject.put("position", posObj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    dWebView.callHandler("GPSflyto", new Object[]{new Gson().toJson(jsonObject.toString())}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                }
                                initOneBodyFireData();
                            }
                        },1000);

                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
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
                try{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    },1000);
                }catch(Exception e){
                    Log.e(TAG, "onFinished: " + e );
                }

            }
        });

    }
    /**
     * 从服务器获取一体机数据-未处理
     */
    private void getonebodyDataFromSetviceNotHandle() {
        if (isShowSearchDialog) {
            showDialogProgress(progressDialog, "查询中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            //未处理
            dto.put("isHandle","0");
            jsonObject.put("dto", dto);
            jsonObject.put("limit", 20);
            jsonObject.put("page", currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams( RequestUtils.REQUEST_URL +"/fire/api/monitorFirealarm/page");


        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + user.getToken());
        Log.e(TAG, "getonebodyDataFromSetvice: " + params);
        Log.e(TAG, "getonebodyDataFromSetvice: " + jsonObject.toString());
        Log.e(TAG, "getonebodyDataFromSetvice: " + user.getToken());
        params.setConnectTimeout(100000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getonebodyDataonSuccess: qc 未处理 params " + params);
                Log.e(TAG, "getonebodyDataonSuccess: qc 未处理 " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        int totalSize = getJsonObj.getInt("totalSize");
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Log.e(TAG, "dataList: "+dataList);
                        Gson gson = new Gson();

                        if(currentPage == 1){
                            oneBodyFireList.clear();
                        }
                        List<OneBodyFire> oneBodyFireAllList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<OneBodyFire>>() {
                        }.getType());

                        //未处理
                        oneBodyFireListNotHandle.clear();
                        oneBodyFireListNotHandle.addAll(oneBodyFireAllList);
                        oneBodyFireList.addAll(oneBodyFireListNotHandle);

                        oneBodyFireFenleiList.clear();
                        if(isAllTag){
                            if(isAllNum == 0){
                                isAllNum++;

                                if(isReleasList == 1){
                                    //真实火点 不可能
                                    fenleiTextView.setText("真实火点");
                                }else if(isReleasList == 2){
                                    //未处理
                                    fenleiTextView.setText("未处理");
                                    oneBodyFireFenleiList.addAll(oneBodyFireList);
                                }else{
                                    //全部
                                    fenleiTextView.setText("全部");
                                    oneBodyFireFenleiList.addAll(oneBodyFireList);
                                }
                                return;
                            }else{
                                isAllTag = false;
                                isAllNum = 0;
                            }
                        }

                        if(isReleasList == 1){
                            //真实火点 不可能
                            fenleiTextView.setText("真实火点");
                        }else if(isReleasList == 2){
                            //未处理
                            fenleiTextView.setText("未处理");
                            oneBodyFireFenleiList.addAll(oneBodyFireList);
                        }else{
                            //全部
                            fenleiTextView.setText("全部");
                            oneBodyFireFenleiList.addAll(oneBodyFireList);
                        }

                        if(oneBodyFireFenleiList.size()!=0){
                            OneBodyFire bodyFire = oneBodyFireFenleiList.get(0);
                            final JSONObject jsonObject = new JSONObject();
                            JSONObject posObj = new JSONObject();
                            try {
                                posObj.put("lat", bodyFire.getAlarmLatitude());
                                posObj.put("lng", bodyFire.getAlarmLongitude());
                                jsonObject.put("position", posObj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dWebView.callHandler("GPSflyto", new Object[]{new Gson().toJson(jsonObject.toString())}, new OnReturnValue<String>() {
                                @Override
                                public void onValue(String retValue) {
                                    Log.d("jsbridge", "call succeed,return value is " + retValue);
                                }
                            });
                        }
                        initOneBodyFireData();
                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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
                if (isShowSearchDialog) {
                    progressDialog.dismiss();
                }

            }
        });

    }

    /**
     * 初始化一体机火点数据
     */
    private void initOneBodyFireData() {
        //往地图上打一体机火点
        initYitijiFireMap();
        //往列表上展示数据

        Log.e(TAG, "initOneBodyFireData:size== " + oneBodyFireFenleiList.size() );
        onebodyItems.clear();
        if (oneBodyFireFenleiList.size() == 0){
            onebodyItems.add(new Empty("暂无数据"));
        }else {
            for (int i = 0; i < oneBodyFireFenleiList.size(); i++) {
                onebodyItems.add(oneBodyFireFenleiList.get(i));
            }
        }


        assertAllRegistered(onebodyAdapter, onebodyItems);
        onebodyAdapter.notifyDataSetChanged();
    }


    private void initResourceListData() {
        resourceItems.clear();
        for (int i = 0; i < resourceListList.size(); i++) {
            resourceItems.add(resourceListList.get(i));
        }

        assertAllRegistered(resourceAdapter, resourceItems);
        resourceAdapter.notifyDataSetChanged();
    }
    /**
     * 地图上加载一体机火点
     */
    private void initYitijiFireMap() {
        //清除旧的卫星定位
        dWebView.callHandler("removeDataSource", new Object[]{"ic_onebody"}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.e(TAG, "onValue:  qingchu" + retValue);
            }
        });
        //打入新的卫星点位
        if (oneBodyFireFenleiList.size() > 0) {
            List<MapModel> mapModelList = new ArrayList<>();
            for (int i = 0; i < oneBodyFireFenleiList.size(); i++) {
                OneBodyFire oneBodyFire = oneBodyFireFenleiList.get(i);
                MapModel mapModel = new MapModel(oneBodyFire.getId(), oneBodyFire.getName(),new MapPosition(oneBodyFire.getAlarmLongitude(), oneBodyFire.getAlarmLatitude(),0.00), "ic_onebody");
                mapModelList.add(mapModel);
            }

            Log.e(TAG, "initWeixingMap: " + new Gson().toJson(mapModelList));
            dWebView.callHandler("showPoint", new Object[]{"ic_onebody", new Gson().toJson(mapModelList), ""}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.e(TAG, "onValue:  dadian" + retValue);
                }
            });
        }

    }

    /**
     * 初始化卫星数据
     */
    private void initWeixingData() {
        //往地图上打点
        initWeixingMap();

        //往列表上展示
        Log.e(TAG, "initWeixingData: " + weixingModelList.size());
        fireCountText.setText(weixingModelList.size() + "");
        weixingItems.clear();
        if (weixingModelList.size() > 0) {
            if (currentFireListType == 1) {      //按时间分类
                for (int i = 0; i < weixingModelList.size(); i++) {
                    if (i > 0) {
                        if (weixingModelList.get(i).getObservationDatetime().equals(weixingModelList.get(i - 1).getObservationDatetime())) {    //如果这一条如上一条时间相同
                            weixingModelList.get(i).setShowTime(false);
                            weixingModelList.get(i).setShowLine(false);
                        } else {
                            weixingModelList.get(i).setShowTime(true);
                            weixingModelList.get(i).setShowLine(true);
                        }
                    } else {
                        weixingModelList.get(i).setShowTime(true);
                        weixingModelList.get(i).setShowLine(false);
                    }
                    weixingModelList.get(i).setFireListType(1);
                    weixingItems.add(weixingModelList.get(i));
                }
            } else {         //按编号分类
                for (int i = 0; i < weixingModelList.size(); i++) {
                    if (i > 0) {
                        if (weixingModelList.get(i).getFireNo().equals(weixingModelList.get(i - 1).getFireNo())) {    //如果这一条如上一条时间相同
                            weixingModelList.get(i).setShowTime(false);
                            weixingModelList.get(i).setShowLine(false);
                        } else {
                            weixingModelList.get(i).setShowTime(true);
                            weixingModelList.get(i).setShowLine(true);
                        }
                    } else {
                        weixingModelList.get(i).setShowTime(true);
                        weixingModelList.get(i).setShowLine(false);
                    }
                    weixingModelList.get(i).setFireListType(2);
                    weixingItems.add(weixingModelList.get(i));
                }
            }

        } else {
            weixingItems.add(new Empty("暂无卫星火情"));
        }

        assertAllRegistered(weixingAdapter, weixingItems);
        weixingAdapter.notifyDataSetChanged();


        //显示消息列表
        if (isShowSearchDialog) {
            fireInfoListDialog.show();
        }

        //弹出推送详情
        if (isTuisong) {

            for (int i = 0; i < weixingModelList.size(); i++) {
                if (weixingModelList.get(i).getId().equals(weixingFireId)) {
                    currentWeixingModel = weixingModelList.get(i);
                }
            }

            //飞到精确点上
            initWeixingFlyMap();
            //加载卫星详细数据
            initWeixinModelData();
        }

    }

    private void initWeixingListData() {

    }

    /**
     * 卫星数据地图打点
     */
    private void initWeixingMap() {
        //清除旧的卫星定位
        dWebView.callHandler("removeDataSource", new Object[]{"fire_weixing"}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.e(TAG, "onValue:  qingchu" + retValue);
            }
        });
        //打入新的卫星点位
        if (weixingModelList.size() > 0) {
            List<MapModel> mapModelList = new ArrayList<>();
            for (int i = 0; i < weixingModelList.size(); i++) {
                WeixingModel weixingModel = weixingModelList.get(i);
                MapModel mapModel = new MapModel(weixingModel.getId(), weixingModel.getFormattedAddress(), new MapPosition(Double.parseDouble(weixingModel.getLongitude()), Double.parseDouble(weixingModel.getLatitude()), 0.00), "fire_weixng");
                mapModelList.add(mapModel);
            }

            Log.e(TAG, "initWeixingMap: " + new Gson().toJson(mapModelList));
            dWebView.callHandler("showPoint", new Object[]{"fire_weixing", new Gson().toJson(mapModelList), ""}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.e(TAG, "onValue:  dadian" + retValue);
                }
            });
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
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(getContext(), "请打开GPS和使用网络定位以提高精度", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getContext(), "Please Open Your GPS or Location Service", Toast.LENGTH_SHORT).show();

        }
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
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
     * 地图资源点点击回调
     */
    @Override
    public void onJsEscapeDetailsClickListener(String json) {
        Log.e(TAG, "onJsEscapeDetailsClickListener: " + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String resourcetype = jsonObject.getString("resourcetype");
            if (resourcetype.equals("fire_weixing")) {
                String id = jsonObject.getString("id");
                Message message = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("id", id);
                b.putInt("what",DIALOG_FIRE_SHOW);
                message.setData(b);
                mHandler.sendMessage(message);
            }else if (resourcetype.equals("ic_onebody")){
                String id = jsonObject.getString("id");
                Message message = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("id", id);
                b.putInt("what",DIALOG_ONEBODY_FIRE_SHOW);
                message.setData(b);
                mHandler.sendMessage(message);
            }else if (resourcetype.equals("monitor")){
//                String id = jsonObject.getString("id");
//                Intent intent=new Intent();
//                intent.putExtra("id",id);
//                intent.setAction("video_play");
//                getContext().sendBroadcast(intent);

                String id = jsonObject.getString("id");
                Message message = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("id", id);
                b.putInt("what",DIALOG_MOINTOR_SHOW);
                message.setData(b);
                mHandler.sendMessage(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
      /*  if (type.equals("fire_weixing")){
            for (int i = 0; i < weixingModelList.size(); i++) {
                if (weixingModelList.get(i).getId().equals(id)) {
                    currentWeixingModel = weixingModelList.get(i);
                }
            }
        }

        //飞到精确点上
        initWeixingFlyMap();
        //加载卫星详细数据
        initWeixinModelData();*/
    }




    /**
     * 地图  飞到卫星火点精确点上
     */
    private void initWeixingFlyMap() {
        final JSONObject jsonObject = new JSONObject();
        JSONObject posObj = new JSONObject();
        try {
            posObj.put("lat", Double.parseDouble(currentWeixingModel.getLatitude()));
            posObj.put("lng", Double.parseDouble(currentWeixingModel.getLongitude()));
            jsonObject.put("position", posObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("GPSposition", jsonObject.toString());
        dWebView.callHandler("GPSflyto", new Object[]{new Gson().toJson(jsonObject.toString())}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });
    }

    /**
     * 加载卫星详细数据
     */
    private void initWeixinModelData() {

        Log.e(TAG, "initWeixinModelData: ceshi1");
        if (currentWeixingModel.getFormattedAddress().isEmpty()) {

            fireAddressText.setText("边境热源");
        } else {
            fireAddressText.setText(currentWeixingModel.getFormattedAddress());
        }
        fireTimeText.setText(currentWeixingModel.getObservationDatetime().replace("T", "  ").substring(0,currentWeixingModel.getObservationDatetime().indexOf(".")));
        jingWeiText.setText(NumberUtils.saveOneBitTwo(Double.parseDouble(currentWeixingModel.getLongitude())) + "  " + NumberUtils.saveOneBitTwo(Double.parseDouble(currentWeixingModel.getLatitude())));
        kexinText.setText(currentWeixingModel.getCredibility() + "");
        mianjiText.setText(currentWeixingModel.getArea() + "");
        cishuText.setText(currentWeixingModel.getObservationFrequency() + "");
        leixingText.setText("林地(" + getTwoDouble(currentWeixingModel.getWoodland() * 100) + "%)草地(" + getTwoDouble(currentWeixingModel.getGrassland() * 100) + "%)农田(" + getTwoDouble(currentWeixingModel.getFarmland() * 100) + "%)其他(" + getTwoDouble(currentWeixingModel.getOtherland() * 100) + "%)");
        Log.e(TAG, "initWeixinModelData: ceshi2");
        shujuyuanText.setText(currentWeixingModel.getSatellite());
        huodianCodeText.setText(currentWeixingModel.getFireNo());
        Log.e(TAG, "initWeixinModelData: ceshi3");
        xiangyuanmianjiView.setText(currentWeixingModel.getPixelArea() + "");
        xiangyuanshuView.setText(currentWeixingModel.getPixelNumber() + "");
        Log.e(TAG, "initWeixinModelData: ceshi4");
        Log.e(TAG, "initWeixinModelData: ceshi4" +currentWeixingModel.getLightImageAddress());
        if (currentWeixingModel.getLightImageAddress() != null){
            Glide.with(getContext()).load("http://web.ehaohai.com:2018" + currentWeixingModel.getLightImageAddress())
                    .error(R.drawable.ic_no_pic)
                    .placeholder(R.drawable.ic_jaizai).into(huodianOneImage);
        }else {
            Glide.with(getContext()).load(R.drawable.ic_no_pic).into(huodianOneImage);
        }

        Log.e(TAG, "initWeixinModelData: ceshi5");
        if (currentWeixingModel.getIrImageAddress() != null){
            Glide.with(getContext()).load("http://web.ehaohai.com:2018" + currentWeixingModel.getIrImageAddress())
                    .error(R.drawable.ic_no_pic)
                    .placeholder(R.drawable.ic_jaizai).into(huodianTwoImage);
        }else {
            Glide.with(getContext()).load(R.drawable.ic_no_pic).into(huodianTwoImage);
        }



        Log.e(TAG, "initWeixinModelData: ceshi6");
        weixingFireDialog.show();
    }

    public Double getTwoDouble(double f) {
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }
    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                if (endDate.length() > 0) { //清除上次记录的日期
                    endDate.delete(0, endDate.length());
                }
                if (isChooseStarTime){
                    date.append(String.valueOf(year));
                    if (month < 9){
                        date.append("-0").append(String.valueOf(month + 1));
                    }else {
                        date.append("-").append(String.valueOf(month + 1));
                    }
                    if (day <10){
                        date.append("-0").append(String.valueOf(day));
                    } else {
                        date.append("-").append(String.valueOf(day));
                    }
                    gaojiStartimeText.setText(date);
                    //gaojiStartimeText.setText(date.append(String.valueOf(year)).append("/").append(String.valueOf(month + 1)).append("/").append(day));
                }else {
                    date.append(String.valueOf(year));
                    if (month < 9){
                        date.append("-0").append(String.valueOf(month + 1));
                    }else {
                        date.append("-").append(String.valueOf(month + 1));
                    }
                    if (day <10){
                        date.append("-0").append(String.valueOf(day));
                    } else {
                        date.append("-").append(String.valueOf(day));
                    }
                    gaojiEndTimeText.setText(date);
                    // gaojiEndTimeText.setText(date.append(String.valueOf(year)).append("/").append(String.valueOf(month + 1)).append("/").append(day));
                }
                dialog.dismiss();
                showTimeDialog();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final android.support.v7.app.AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getContext(), R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        Log.i(TAG, "showDataDialog: " + endData);
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
        datePicker.init(year, month, day,this);
    }
    /**
     * 日期选择控件
     */
    private void showTimeDialog() {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder1.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isChooseStarTime){
                    if (chooseHour < 10 && chooseMinute <10){
                        gaojiStartimeText.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    }else if (chooseHour < 10 && chooseMinute >10){
                        gaojiStartimeText.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    }else if (chooseHour > 10 && chooseMinute < 10){
                        gaojiStartimeText.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    }else {
                        gaojiStartimeText.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                    //gaojiStartimeText.append("  " + chooseHour + ":" + chooseMinute);
                }else {
                    if (chooseHour < 10 && chooseMinute <10){
                        gaojiEndTimeText.append("  0" + chooseHour + ":0" + chooseMinute + ":00");
                    }else if (chooseHour < 10 && chooseMinute >10){
                        gaojiEndTimeText.append("  0" + chooseHour + ":" + chooseMinute + ":00");
                    }else if (chooseHour > 10 && chooseMinute < 10){
                        gaojiEndTimeText.append("  " + chooseHour + ":0" + chooseMinute + ":00");
                    }else {
                        gaojiEndTimeText.append("  " + chooseHour + ":" + chooseMinute + ":00");
                    }
                    //  gaojiEndTimeText.append("  " + chooseHour + ":" + chooseMinute);
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
        View dialogView = View.inflate(getContext(), R.layout.dialog_time, null);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR);
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
        //初始化日期监听事件
        //   timePicker.init(year, month, day, this);
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        chooseHour = calendar.get(Calendar.HOUR);
        chooseMinute = calendar.get(Calendar.MINUTE);

    }
    class myReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            String msg = intent.getStringExtra("message");
            //Toast.makeText(context, "广播已经接收", Toast.LENGTH_SHORT).show();
            Log.i("onReceive: ", msg);
            dWebView.callHandler("GPSpoint", new Object[]{new Gson().toJson(msg)}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.d("jsbridge", "call succeed,return value is " + retValue);
                }
            });
        }
    }

    /**
     * 卫星列表条目点击
     *
     * @param weixingModel
     */
    @Override
    public void onWeixingInfoClick(WeixingModel weixingModel) {
        currentWeixingModel = weixingModel;

        fireInfoListDialog.hide();
        //飞到精确点上
        initWeixingFlyMap();
        //加载卫星详细数据
        initWeixinModelData();

    }

    /**
     * 一体机火点条目点击
     * @param oneBodyFire
     */
    @Override
    public void onOneBodyItemClickListener(OneBodyFire oneBodyFire) {
        currentOneBodyFire = oneBodyFire;
        onebodyListDialog.dismiss();
        //飞到一体机精确点上
        initOneBodyFlyMap();
        //加载一体机详细数据
        initOneBodyFireModelData();

    }

    void hideShow(){
        try{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initOneBodyFireData();
                    onebodyListDialog.hide();
                    onebodyListDialog.show();
                }
            },1000);
        }catch(Exception e){

        }
    }

    /**
     *  一体机火点详情
     */
    private void initOneBodyFireModelData() {
        mingchengView.setText(currentOneBodyFire.getName());
        dizhiView.setText(currentOneBodyFire.getAddress());
        shijianView.setText(currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
        jingweiduView.setText(currentOneBodyFire.getAlarmLongitude() +"、" + currentOneBodyFire.getAlarmLatitude());
        Log.e(TAG, "getPicPath1: "+currentOneBodyFire.getPicPath1().replace("10.10.13.3:80","183.230.142.120:8012"));
        Log.e(TAG, "getPicPath2: "+currentOneBodyFire.getPicPath2().replace("10.10.13.3:80","183.230.142.120:8012"));
        Glide.with(getContext()).load(currentOneBodyFire.getPicPath1().replace("10.10.13.3:80","183.230.142.120:8012"))
                .error(R.drawable.ic_jaizai)
                .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                Log.e(TAG, "onException: pic1 - 1" );
                String str = currentOneBodyFire.getPicPath1().replace("10.10.13.3:80","183.230.142.120:8012");
                if(!str.contains("tmp")){
                    str += ".tmp";
                }

                Log.e(TAG, "onException: pic1 - 1 str = " + str);
                final DownloadTask downloadTask = new DownloadTask(getActivity());
                downloadTask.setLocation(true);
                downloadTask.execute(str);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(yitijiOneView);
        Glide.with(getContext()).load(currentOneBodyFire.getPicPath2().replace("10.10.13.3:80","183.230.142.120:8012"))
                .error(R.drawable.ic_jaizai)
                .placeholder(R.drawable.ic_jaizai).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                String str = currentOneBodyFire.getPicPath2().replace("10.10.13.3:80","183.230.142.120:8012");
                if(!str.contains("tmp")){
                    str += ".tmp";
                }

                final DownloadTask downloadTask = new DownloadTask(getActivity());
                downloadTask.setLocation(false);
                downloadTask.execute(str);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(yitijiTwoView);

        if(currentOneBodyFire.getIsReal() == null){
            zhenshiLayout.setVisibility(View.VISIBLE);
            zhenshiTextView.setVisibility(View.GONE);
        }else {
            zhenshiLayout.setVisibility(View.GONE);
            zhenshiTextView.setVisibility(View.VISIBLE);
            if (currentOneBodyFire.getIsReal() == 0){
                zhenshiTextView.setText("疑似火情");
            }else {
                zhenshiTextView.setText("真实火情");
            }
        }
        oneBodyFireDialog.show();
    }

    private File file1 = null;
    private File file2 = null;
    /**
     * 下载文件
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private boolean isLeft = true;

        public DownloadTask(Context context) {
            this.context = context;
        }

        void setLocation(boolean location){
            isLeft = location;
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
                    if(isLeft){
                        file1 = new File(getActivity().getObbDir().getAbsolutePath(),
                                "pic1" + new Date().getTime() + ".jpeg");

                        if (!file1.exists()) {
                            // 判断父文件夹是否存在
                            if (!file1.getParentFile().exists()) {
                                file1.getParentFile().mkdirs();
                            }
                        }
                    }else{
                        file2 = new File(getActivity().getObbDir().getAbsolutePath(),
                                "pic1" + new Date().getTime() + ".jpeg");

                        if (!file2.exists()) {
                            // 判断父文件夹是否存在
                            if (!file2.getParentFile().exists()) {
                                file2.getParentFile().mkdirs();
                            }
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                if(isLeft){
                    output = new FileOutputStream(file1);
                }else{
                    output = new FileOutputStream(file2);
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
                if(isLeft){
                    Log.e(TAG, "onPostExecute: isLeft " + file1.getPath() );
                    Glide.with(getContext()).load(file1)
                            .error(R.drawable.ic_no_pic)
                            .placeholder(R.drawable.ic_jaizai).into(yitijiOneView);
                }else{
                    Log.e(TAG, "onPostExecute: isRight " + file2.getPath() );
                    Glide.with(getContext()).load(file2)
                            .error(R.drawable.ic_no_pic)
                            .placeholder(R.drawable.ic_jaizai).into(yitijiTwoView);
                }
            }

        }
    }

    /**
     *  资源点详情
     */
    private void initresourceFireModelData() {
        mingchengView.setText(currentOneBodyFire.getName());
        dizhiView.setText(currentOneBodyFire.getAddress());
        shijianView.setText(currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
        jingweiduView.setText(currentOneBodyFire.getAlarmLongitude() +"、" + currentOneBodyFire.getAlarmLatitude());
        oneBodyFireDialog.show();
    }
    /**
     * 一体机飞到地图上
     */
    private void initOneBodyFlyMap() {
        final JSONObject jsonObject = new JSONObject();
        JSONObject posObj = new JSONObject();
        try {
            posObj.put("lat", currentOneBodyFire.getAlarmLatitude());
            posObj.put("lng", currentOneBodyFire.getAlarmLongitude());
            jsonObject.put("position", posObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("GPSposition", jsonObject.toString());
        dWebView.callHandler("GPSflyto", new Object[]{new Gson().toJson(jsonObject.toString())}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });
    }

    /**
     * 资源列表的电机回调
     * @param resourceList
     */
    @Override
    public void onResourceListItemClickListener(ResourceList resourceList) {
        for (int i = 0; i < resourceListList.size(); i++) {
            if (resourceListList.get(i).getId().equals(resourceList.getId())) {
                Log.e(TAG, "onResourceListItemClickListener: "+resourceList.isCheck());
                if (resourceList.isCheck()){    //隐藏资源点
                    resourceListList.get(i).setCheck(false);
                    Log.e(TAG, "onResourceListItemClickListener: "+resourceListList.get(i).getName());
                    if (resourceListList.get(i).getName().equals("视频监控点")) {
                        resorcetype = "monitor";
                    }else if (resourceListList.get(i).getName().equals("消防专业队")){
                        resorcetype = "team";
                    }else if (resourceListList.get(i).getName().equals("危险源")){
                        resorcetype = "dangerSource";
                    }else if (resourceListList.get(i).getName().equals("物资储备库")){
                        resorcetype = "foreastRoom";
                    }else if (resourceListList.get(i).getName().equals("水源地")){
                        resorcetype = "waterSource";
                    }else if (resourceListList.get(i).getName().equals("墓地")){
                        resorcetype = "cemetery";
                    }else if (resourceListList.get(i).getName().equals("瞭望塔")){
                        resorcetype = "watchTower";
                    }else if (resourceListList.get(i).getName().equals("护林检查站")){
                        resorcetype = "checkStation";
                    }else if (resourceListList.get(i).getName().equals("森林防火监测中心")){
                        resorcetype = "foreastCenter";
                    }
                    //清除旧的卫星定位
                    dWebView.callHandler("removeDataSource", new Object[]{resorcetype}, new OnReturnValue<String>() {
                        @Override
                        public void onValue(String retValue) {
                            Log.e(TAG, "onValue:  qingchu" + retValue);
                        }
                    });
                }else {         //显示资源点逻辑
                    resourceListList.get(i).setCheck(true);
                    getResourceDataFromService(resourceList.getApiUrl(),resourceList.getName());
                }
            }
        }

        initResourceListData();
        resourceListDialog.dismiss();

    }

    /**
     * 获取资源数据
     * @param ApiUrl
     * @param nameresult
     */
    private void getResourceDataFromService(String ApiUrl,String nameresult) {
        Log.e(TAG, "getResourceDataFromService: "+nameresult );
        showDialogProgress(progressDialog,"加载中...");
        if (nameresult.equals("视频监控点")) {
            resorcetype = "monitor";
        }else if (nameresult.equals("消防专业队")){
            resorcetype = "team";
        }else if (nameresult.equals("危险源")){
            resorcetype = "dangerSource";
        }else if (nameresult.equals("物资储备库")){
            resorcetype = "foreastRoom";
        }else if (nameresult.equals("水源地")){
            resorcetype = "waterSource";
        }else if (nameresult.equals("墓地")){
            resorcetype = "cemetery";
        }else if (nameresult.equals("瞭望塔")){
            resorcetype = "watchTower";
        }else if (nameresult.equals("护林检查站")){
            resorcetype = "checkStation";
        }else if (nameresult.equals("森林防火监测中心")){
            resorcetype = "foreastCenter";
        }
        JSONObject resorcelistobj =new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource"+ApiUrl+"/list");
        params.setAsJsonContent(true);
        params.setBodyContent(resorcelistobj.toString());
        Log.e(TAG, "getResourceList: " + resorcelistobj.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getResourceonSuccess: --1-" + result );
                try {
                    JSONObject json0bject = new JSONObject(result);
                    Log.e("result", json0bject.toString());
                    JSONArray data = json0bject.getJSONArray("data");
                    Log.e("data1", data.toString());
                    dWebView.callHandler("showPointforresource", new Object[]{resorcetype, data, ""}, new OnReturnValue<String>() {
                        @Override
                        public void onValue(String retValue) {
                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                        }
                    });
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
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 根据监控点id获取信息
     */
    private void getinfofromid(final String resourceid){
        showDialogProgress(progressDialog,"加载中...");
        //获取摄像头URL
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("monitorId", resourceid);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/camera/list");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        for(int i=0; i<data.length(); i++){
                            JSONObject dataListsObj = data.getJSONObject(i);
                            if (dataListsObj.getInt("cameraType")==1){
                                kejianguangUrl= dataListsObj.getString("id");
                                kejianguangMId= dataListsObj.getString("monitorId");
                                Log.i(TAG, "kejianguangUrl: "+kejianguangUrl);
                            }else {
                                rechengxiangUrl= dataListsObj.getString("id");
                                rechengxiangMId= dataListsObj.getString("monitorId");
                                Log.i(TAG, "rechengxiangUrl: "+rechengxiangUrl);
                            }
                        }

                        //获取详情
                        getShipinReourceXiangqing(resourceid);
                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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

            }
        });


    }

    private void getShipinReourceXiangqing(String resourceid) {
        RequestParams infoparams = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/monitor");
        infoparams.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        infoparams.addParameter("id",resourceid);
        infoparams.addHeader("infoAuthorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "inforesource: --"  + infoparams);
        x.http().get(infoparams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "infoonSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        for(int i=0; i<data.length(); i++){
                            JSONObject dataListsObj = data.getJSONObject(i);
                            resourcenameview.setText(dataListsObj.getString("name"));
                            if (dataListsObj.getString("districtName").equals("null")||dataListsObj.getString("districtName").isEmpty()) {
                                resoucedizhiview.setText(dataListsObj.getString("streetName"));
                            }else {
                                resoucedizhiview.setText(dataListsObj.getString("districtName")+dataListsObj.getString("streetName"));
                            }

                            String positionStr = dataListsObj.getString("position");
                            Log.e("position1", positionStr);
                            JSONObject positionObj = new JSONObject(positionStr);
                            resourcejingweiduview.setText(positionObj.getString("lng")+"，"+positionObj.getString("lat"));
                        }
                    } else {
                        Toast.makeText(getContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "未开启本应用地理位置信息，请先开启！", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

}