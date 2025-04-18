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
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ScrollView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.model.LatLng;
import com.haohai.platform.firelibrary.utils.LatLngChange;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.Utils.JsApi;
import com.haohai.platform.mapmodel.activity.CheckPlanAndFilterActivity;
import com.haohai.platform.mapmodel.activity.CompresiveCheckActivity;
import com.haohai.platform.mapmodel.activity.PlayerActivity;
import com.haohai.platform.mapmodel.activity.PlayerRtspActivity;
import com.haohai.platform.mapmodel.activity.ResourceAddActivity;
import com.haohai.platform.mapmodel.activity.ResourceSearchActivity;
import com.haohai.platform.mapmodel.activity.WeixingActivity;
import com.haohai.platform.mapmodel.bean.ResourceType;
import com.haohai.platform.mapmodel.fragment.base.HhBaseFragment;
import com.haohai.platform.mapmodel.listener.OnLoadMoreListener;
import com.haohai.platform.mapmodel.model.ArModel;
import com.haohai.platform.mapmodel.model.Around;
import com.haohai.platform.mapmodel.model.AroundModel;
import com.haohai.platform.mapmodel.model.MapModel;
import com.haohai.platform.mapmodel.model.WeixingModel;
import com.haohai.platform.mapmodel.model.MapPosition;
import com.haohai.platform.mapmodel.multitype.AroundViewBinder;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.haohai.platform.mapmodel.multitype.EmptyViewBinder;
import com.haohai.platform.mapmodel.multitype.OneBodyFire;
import com.haohai.platform.mapmodel.multitype.OneBodyFireViewBinder;
import com.haohai.platform.platformmodel.ui.Multitype.LeaveFlow;
import com.ruyiruyi.rylibrary.bus.MainRefreshModel;
import com.ruyiruyi.rylibrary.bus.MapDialogDismiss;
import com.ruyiruyi.rylibrary.db.ResourceList;
import com.haohai.platform.mapmodel.multitype.ResourceListViewBinder;
import com.haohai.platform.mapmodel.multitype.WeixingModelViewBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.base.AppDelegate;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Grid;
import com.ruyiruyi.rylibrary.db.Resource;
import com.ruyiruyi.rylibrary.db.Setting;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.service.BackgroundMp3Service;
import com.ruyiruyi.rylibrary.utils.CommonUtils;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;
import com.ruyiruyi.rylibrary.utils.NumberUtils;
import com.ruyiruyi.rylibrary.utils.image.ImagPagerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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

import static com.haohai.platform.mapmodel.R.id.add;
import static com.haohai.platform.mapmodel.R.id.weixing_layout;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

/**
 * Created by geyang on 2020/7/21.
 */

public class MapFragment extends HhBaseFragment implements JsApi.OnJsClickListener, ResourceListViewBinder.OnResourceLsitItemClick, OneBodyFireViewBinder.OnOneBodyItemClick, DatePicker.OnDateChangedListener, INaviInfoCallback, WeixingModelViewBinder.OnWeixingInfoItemClick, AroundViewBinder.AroundItemClick {

    private static final String TAG = "MapFragment";
    private DWebView dWebView;
    private myReceiver Receiver;
    private boolean isZiyuanButtonShow = false;
    private boolean isonebodyButtonShow = false;
    private static final int DIALOG_FIRE_SHOW = 6;
    private static final int DIALOG_ONEBODY_FIRE_SHOW = 20;
    private static final int DIALOG_MOINTOR_SHOW = 22;
    private static final int DIALOG_RESOURCE = 23;
    private static final int TIME_CHANGE = 21;
    private static boolean isExit = false;
    private Number currentLongitude;
    private Number currentLatitude;
    private TextView onebody_view;
    private TextView ddrw_view;
    private ImageView orderWarnImageView;
    public int currentFireFindTime = 3;  //时间
    private ProgressDialog progressDialog;
    private User user;
    private List<OneBodyFire> oneBodyFireList;
    private List<OneBodyFire> oneBodyFireFenleiList;
    private List<ResourceList> resourceListList;
    private Dialog searchDialog;
    private View searchInflater;
    private LinearLayout oneHoursLayout;
    private LinearLayout currntTimeLayout;
    private LinearLayout threeHoursLayout;
    private LinearLayout oneDayLayout;
    private LinearLayout threeDayLayout;
    private LinearLayout fiveDayLayout;
    private LinearLayout gaojiSearchLayout;
    private Dialog fireInfoListDialog;
    private Dialog onebodyListDialog;
    private View fireInfoListInflater;
    private View onebodyListInflater;
    private TextView fireCountText;
    private RecyclerView weixingListView;
    private RecyclerView onebodyListView;
    private LinearLayout onebodyLayout;
    private TextView fenleiView;
    private List<Object> weixingItems = new ArrayList<>();
    private MultiTypeAdapter weixingAdapter;
    private List<Object> onebodyItems = new ArrayList<>();
    private MultiTypeAdapter onebodyAdapter;
    private List<Object> resourceItems = new ArrayList<>();
    private MultiTypeAdapter aroundAdapter;
    private List<Object> aroundItems = new ArrayList<>();
    private MultiTypeAdapter resourceAdapter;
    private Dialog gaojiDialog;
    private View gaojiInflater;

    private int currentFireListType = 1;  //1是时间排序  2是编号分类
    private int choose1 = 2;
    private int aroundKm = 5;
    private AlertDialog.Builder builder;
    private WeixingModel currentWeixingModel;
    private OneBodyFire currentOneBodyFire;
    private com.haohai.platform.mapmodel.multitype.Resource currentAround;
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
    private TextView around_view;
    private TextView addres_view;
    private boolean isShowSearchDialog = false;
    private LinearLayout ziyuanLayout;
    private TextView ziyuanButton;
    private TextView onebodyButton;
    private LinearLayout ziyuanButtonLayout;
    private LinearLayout check_layout;
    private TextView ziyuanPlanButton;
    private TextView ziyuanCheckButton;
    private TextView ziyuanPaichaButton;
    private TextView ziyuanComprehensivecheckButton;
    private LinearLayout alarmlistLayout;
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
    private Dialog otherresourceinfoDialog;
    private View resourceInflater;
    private View otherresourceInflater;
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
    private List<WeixingModel> weixingModelList;
    private TextView weixingButton;
    private LinearLayout weixingButtonLayout;
    private boolean isWeixingButtonShow = false;
    private boolean clickOtherRes = false;
    private LinearLayout weixinLayout;
    private TextView weixingShezhiView;
    private TextView weixingChaxunView;
    private TextView weixingLiebiaoView;
    private Timer timer;
    private List<Grid> quList = new ArrayList<>();
    private boolean isResourceAll = false;
    private List<BroadcastReceiver> broadcastList = new ArrayList<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle data = msg.getData();
            int what = data.getInt("what");
            switch (what){
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
                case DIALOG_RESOURCE:
                    String resId = data.getString("id");
                    String type = data.getString("type");
                    if(Objects.equals(type, "around")){
                        break;
                    }
                    for (int i = 0; i < allResourcePoints.size(); i++) {
                        int indexs = i;
                        try {
                            if(resId.equals(allResourcePoints.get(i).getString("id"))){
                                resourceObj = allResourcePoints.get(i);
                                Log.e(TAG, "handleMessage: resourceObj == " + resourceObj );
                                otherresourcenameview.setText(allResourcePoints.get(i).getString("name"));
                                otherresoucedizhiview.setText(parseNull(allResourcePoints.get(i).getString("address")));
                                if(allResourcePoints.get(i).getString("address")==null){
                                    otherresoucedizhiview.setVisibility(View.GONE);
                                }
                                JSONObject position = allResourcePoints.get(i).getJSONObject("position");
                                otherresourcejingweiduview.setText(position.getString("lng")+","+position.getString("lat"));
                                RxViewAction.clickNoDouble(iv_guide).subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void unused) {
                                        //导航
                                        try {
                                            double[] startGroup = bdToGaoDe(Double.parseDouble(AppDelegate.latitude),Double.parseDouble(AppDelegate.longitude));
                                            double[] endGroup = new double[0];
                                            endGroup = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(position.getString("lat")),Double.parseDouble(position.getString("lng")));
                                            Poi start = new Poi("我的位置", new com.amap.api.maps.model.LatLng(startGroup[0], startGroup[1]), "");
                                            Poi end = new Poi(allResourcePoints.get(indexs).getString("name"), new com.amap.api.maps.model.LatLng(endGroup[0], endGroup[1]), "");
                                            AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                                            params.setUseInnerVoice(true);
                                            AmapNaviPage.getInstance().showRouteActivity(getActivity(), params, MapFragment.this);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                RxViewAction.clickNoDouble(res_edit).subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void unused) {
                                        otherresourceinfoDialog.dismiss();
                                        editRes(type,resId,resourceObj);
                                    }
                                });
                                RxViewAction.clickNoDouble(res_delete).subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void unused) {
                                        otherresourceinfoDialog.dismiss();
                                        deleteRes(type,resId);
                                    }
                                });
                                otherresourceinfoDialog.show();
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TIME_CHANGE:
                    Log.e(TAG, "handleMessage: 报警查询");
                    //getonebodyDataFromSetvice();
                    break;
            }            return false;
        }
    });

    private void editRes(String type,String id,JSONObject resourceObj) {
        Intent intent = new Intent(getActivity(), ResourceAddActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("type",type);
        intent.putExtra("resourceObj",resourceObj.toString());
        startActivity(intent);
    }
    private void deleteRes(String type,String id) {
        if(Objects.equals(type, "foreastRoom")){
            type = "materialRepository";
        }
        JSONObject resObj =new JSONObject();
        try {
            resObj.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/"+ type);
        params.setAsJsonContent(true);
        params.setBodyContent(resObj.toString());
        Log.e(TAG, "deleteRes: " + resObj.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "deleteRes: --"  + params);
        String finalType = type;
        x.http().request(HttpMethod.DELETE,params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "deleteRes Success: " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if(Objects.equals(code, "200")){
                        //Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();

                        otherresourceinfoDialog.dismiss();
                       /* //清除旧的卫星定位
                        dWebView.callHandler("removeDataSource", new Object[]{finalType}, new OnReturnValue<String>() {
                            @Override
                            public void onValue(String retValue) {
                                Log.e(TAG, "onValue:  qingchu" + retValue);
                            }
                        });
                        if(Objects.equals(finalType, "materialRepository")){//物资储备库修改过字段。。
                            dWebView.callHandler("removeDataSource", new Object[]{"foreastRoom"}, new OnReturnValue<String>() {
                                @Override
                                public void onValue(String retValue) {
                                    Log.d("jsbridge", "call succeed,removeDataSource value is " + retValue);
                                }
                            });
                        }
                        if(Objects.equals(finalType, "watchTower")||Objects.equals(finalType, "fireCommand")){//水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                            dWebView.callHandler("removeDataSource", new Object[]{"waterSource"}, new OnReturnValue<String>() {
                                @Override
                                public void onValue(String retValue) {
                                    Log.d("jsbridge", "call succeed,return value is " + retValue);
                                }
                            });
                        }
                        postAround5Km(Double.parseDouble(latitude),Double.parseDouble(longitude));*/
                        Toast.makeText(getActivity(), "资源点删除成功,地图即将重置", Toast.LENGTH_SHORT).show();
                        User user = new DbConfig(getActivity()).getUser();
                        //user.setDataIsChange(true);
                        DbConfig dbConfig = new DbConfig(getActivity());
                        DbManager db = dbConfig.getDbManager();
                        try {
                            db.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(MapDialogDismiss.getInstance());
                            }
                        },1000);
                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(MainRefreshModel.getInstance());
                            }
                        },4000);*/
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
                progressDialog.dismiss();
            }
        });
    }

    private Dialog oneBodyFireDialog;
    private View oneBodyFireInflater;
    private TextView mingchengView;
    private TextView dizhiView;
    private TextView shijianView;
    private TextView jingweiduView;
    private ImageView yitijiOneView;
    private ImageView yitijiTwoView;
    private boolean isGaojiFind = false;
    private TextView resourcenameview;
    private TextView otherresourcenameview;
    private TextView resoucedizhiview;
    private TextView otherresoucedizhiview;
    private TextView res_edit;
    private TextView res_delete;
    private TextView resourcejingweiduview;
    private TextView otherresourcejingweiduview;
    private ImageView iv_guide;
    private Button kejianguangbutton;
    private Button rechengxiangbutton;
    private Dialog resourceListDialog;
    private Dialog aroundDialog;
    private View resourceListInflater;
    private View aroundListInflater;
    private RecyclerView aroundListView;
    private LinearLayout ll_around_loc;
    private TextView tv_around_loc;
    private TextView tv_around_clear;
    private RecyclerView resourceListView;
    private TextView aroundFenleiTextView;
    private TextView tv_grid;
    private TextView tv_type;
    private ScrollView sv_grid;
    private LinearLayout ll_grid;
    private LinearLayout aroundFenleiLayout;
    private boolean resourceType = true;
    private LinearLayout zhenshiLayout;
    private TextView zhenshiButton;
    private TextView wubaoButton;
    private TextView zhenshiTextView;
    public int isReleas = 2;  //0疑似火情  1是真实火情  2是未处理
    public int isReleasList = 3;  //0疑似火情  1是真实火情  2是未处理 3是全部
    private TextView fenleiTextView;
    private LinearLayout fenleiLayout;
    private LinearLayout oneBodyFenleiLayout;
    private TextView weixingShijianView;
    private LinearLayout daohangLayout;
    private List<Grid> gridList = new ArrayList<>();
    private DbManager db;
    private boolean cityRoot;
    private List<Resource> resourceList = new ArrayList<>();

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
        EventBus.getDefault().register(this);

        cityRoot = new DbConfig(getActivity()).getUser().getJobTitle().contains("市");
        user = new DbConfig(getContext()).getUser();
        oneBodyFireList = new ArrayList<>();
        oneBodyFireFenleiList = new ArrayList<>();
        resourceListList = new ArrayList<>();
        weixingModelList = new ArrayList<>();
        currentWeixingModel = new WeixingModel();
        DbConfig dbConfig = new DbConfig(getContext());
        db = dbConfig.getDbManager();
        getLocation();
        initDateTime();
        initView();
        bindView();
        //代码 注册 广播接收器
        Receiver = new myReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("zcd.voicerobot");
        getActivity().registerReceiver(Receiver, filter);
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
        getonebodyDataFromSetvice();
        getResourcesListFromService();
        initGridIntoDb();
        getWeixingDataFromSetvice();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MapDialogDismiss message) {
        //关闭所有dialog
        try{
            aroundDialog.dismiss();
            fireInfoListDialog.dismiss();
            gaojiDialog.dismiss();
            gaojiFindDialog.dismiss();
            oneBodyFireDialog.dismiss();
            onebodyListDialog.dismiss();
            otherresourceinfoDialog.dismiss();
            resourceinfoDialog.dismiss();
            weixingFireDialog.dismiss();
            resourceListDialog.dismiss();
            searchDialog.dismiss();
        }catch(Exception e){

        }

        try{
            Log.e(TAG, "onGetMessage: size = " + broadcastList.size() );
            for (int i = 0; i < broadcastList.size(); i++) {
                try{
                    getActivity().unregisterReceiver(broadcastList.get(i));
                }catch(Exception e){
                    Log.e(TAG, "onGetMessage:" + e );
                }
                if(i == broadcastList.size()-1 || broadcastList.size()==0){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            broadcastList.clear();
                            EventBus.getDefault().post(MainRefreshModel.getInstance());
                            getActivity().unregisterReceiver(Receiver);
                            onDestroy();
                        }
                    },500);
                }
            }
        }catch(Exception e){
            Log.e(TAG, "onGetMessage: e = " + e );
        }
    }

    private double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.sin(theta);
        gd_lat_lon[1] = z * Math.cos(theta);
        return gd_lat_lon;
    }
    private double[] gaoDeToBaidu(double gd_lat, double gd_lon) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.sin(theta) + 0.006;
        bd_lat_lon[1] = z * Math.cos(theta) + 0.0065;
        return bd_lat_lon;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try{
            getActivity().unregisterReceiver(Receiver);
            for (int i = 0; i < broadcastList.size(); i++) {
                getActivity().unregisterReceiver(broadcastList.get(i));
            }
        }catch(Exception e){

        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    private void bindView() {


        /*资源类型点击*/
        RxViewAction.clickNoDouble(retypeView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        resourceListDialog.show();
                    }
                });
        /*资源周边点击*/
        RxViewAction.clickNoDouble(around_view)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        aroundDialog.show();
                        initAroundData();
                    }
                });

        /*资源添加*/
        RxViewAction.clickNoDouble(addres_view)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(getActivity(), ResourceAddActivity.class);
                        startActivity(intent);
                    }
                });

        /*资源*/
        RxViewAction.clickNoDouble(ziyuanButtonLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isResLayout = !isResLayout;
                        if(isResLayout){
                            if(CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-add")){//地图-添加资源
                                addres_view.setVisibility(View.VISIBLE);
                            }
                            if(CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-type")){//地图-资源类型
                                retypeView.setVisibility(View.VISIBLE);
                            }
                            if(CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-other")){//地图-周边
                                around_view.setVisibility(View.VISIBLE);
                            }
                        }else{
                            addres_view.setVisibility(View.GONE);
                            retypeView.setVisibility(View.GONE);
                            around_view.setVisibility(View.GONE);
                        }
                    }
                });
        /**
         * 报警点击
         */
        RxViewAction.clickNoDouble(orderWarnImageView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        orderWarnImageView.setVisibility(View.GONE);
                        initOneBodyFireData();
                        onebodyListDialog.show();
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
        /**
         * 一体机火点图片1点击
         */
        RxViewAction.clickNoDouble(yitijiOneView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ArrayList<String> picList = new ArrayList<>();
                        picList.add(currentOneBodyFire.getPicPath1());
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
                        picList.add(currentOneBodyFire.getPicPath2());
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
        RxViewAction.clickNoDouble(ziyuanButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (isZiyuanButtonShow) {
                            isZiyuanButtonShow = false;
                            ziyuanPlanButton.setVisibility(View.GONE);
                            ziyuanCheckButton.setVisibility(View.GONE);
                            ziyuanPaichaButton.setVisibility(View.GONE);
                            //ziyuanComprehensivecheckButton.setVisibility(View.GONE);
                        } else {
                            isZiyuanButtonShow = true;
                            if(CommonUtils.hasPermission(getActivity(),"app-map-btn-check-task")){//地图-检查任务
                                ziyuanPlanButton.setVisibility(View.VISIBLE);
                            }
                            if(!new DbConfig(getActivity()).getUser().getJobTitle().contains("街道")){
                                if(CommonUtils.hasPermission(getActivity(),"app-map-btn-check-resource")){//地图-检查-资源检查
                                    ziyuanCheckButton.setVisibility(View.VISIBLE);
                                }
                                if(CommonUtils.hasPermission(getActivity(),"app-map-btn-check-daily")){//地图-检查-日常检查
                                    ziyuanPaichaButton.setVisibility(View.VISIBLE);
                                }
                            }
                            //ziyuanComprehensivecheckButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
        RxViewAction.clickNoDouble(ziyuanComprehensivecheckButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), CompresiveCheckActivity.class));
                    }
                });
        RxViewAction.clickNoDouble(ziyuanPlanButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //startActivity(new Intent(getContext(),CheckplanActivity.class));
                        startActivity(new Intent(getContext(), CheckPlanAndFilterActivity.class));
                    }
                });
        RxViewAction.clickNoDouble(ziyuanCheckButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), ResourceSearchActivity.class));
                    }
                });
        RxViewAction.clickNoDouble(alarmlistLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        initOneBodyFireData();
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
        RxViewAction.clickNoDouble(oneBodyKJGButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "getMonitorId: "+currentOneBodyFire.getMonitorId() );
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        st = date.getTime();
                        et = date.getTime()+30000;
                        Log.e(TAG, "getTime: "+currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
                        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/camera/list");
                        params.addHeader("Authorization", "bearer " + user.getToken());
                        JSONObject camerajson = new JSONObject();
                        try {
                            camerajson.put("monitorId", currentOneBodyFire.getMonitorId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.setBodyContent(camerajson.toString());
                        Log.e(TAG, "params: "+params);
                        x.http().post(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Log.e(TAG, "camera " + result);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(result);
                                    if (jsonObject1.getString("code").equals("200")) {
                                        JSONArray data = jsonObject1.getJSONArray("data");
                                        for (int i = 0; i < data.length(); i++) {
                                            Log.e(TAG, "cameraonSuccess: "+data.getJSONObject(i).getString("cameraType"));
                                            if (data.getJSONObject(i).getString("cameraType").equals("1")){
                                                cameraid=data.getJSONObject(i).getString("id");
                                                cameraname = data.getJSONObject(i).getString("name");
                                                RequestParams params1 = new RequestParams(RequestUtils.REQUEST_URL + "/resource/api/guide/getVideo");
                                                params1.addHeader("Authorization", "bearer " + user.getToken());
                                                JSONObject camerajson1 = new JSONObject();
                                                try {
                                                    camerajson1.put("id", cameraid);
                                                    camerajson1.put("enumCode", "10003");
                                                    camerajson1.put("startTime", st);
                                                    camerajson1.put("endTime", et);
                                                    camerajson1.put("protocol", "rtmp");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                params1.setBodyContent(camerajson1.toString());
                                                Log.e(TAG, "params1 " + params1);
                                                Log.e(TAG, "params1 " + camerajson1);
                                                x.http().post(params1, new Callback.CommonCallback<String>() {

                                                    @Override
                                                    public void onSuccess(String result) {
                                                        Log.e(TAG, "camera1 " + result);
                                                        JSONObject jsonObject2 = null;
                                                        try {
                                                        jsonObject2 = new JSONObject(result);
                                                        if (jsonObject2.getString("code").equals("200")) {
                                                            JSONArray data = jsonObject2.getJSONArray("data");
                                                            rtspurl = data.getJSONObject(0).getJSONObject("data").getString("url");
                                                            Log.e(TAG, "rtspurl: "+rtspurl );
                                                            if (rtspurl == null) {
                                                                Toast.makeText(getContext(), "暂无可见光视频", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
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
                });

        RxViewAction.clickNoDouble(oneBodyRCXButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.e(TAG, "getMonitorId: "+currentOneBodyFire.getMonitorId() );
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = simpleDateFormat.parse(currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        st = date.getTime();
                        et = date.getTime()+30000;
                        Log.e(TAG, "getTime: "+currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
                        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/camera/list");
                        params.addHeader("Authorization", "bearer " + user.getToken());
                        JSONObject camerajson = new JSONObject();
                        try {
                            camerajson.put("monitorId", currentOneBodyFire.getMonitorId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.setBodyContent(camerajson.toString());
                        Log.e(TAG, "params: "+params);
                        x.http().post(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                Log.e(TAG, "camera " + result);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(result);
                                    if (jsonObject1.getString("code").equals("200")) {
                                        JSONArray data = jsonObject1.getJSONArray("data");
                                        for (int i = 0; i < data.length(); i++) {
                                            Log.e(TAG, "cameraonSuccess: "+data.getJSONObject(i).getString("cameraType"));
                                            if (data.getJSONObject(i).getString("cameraType").equals("2")){
                                                cameraid=data.getJSONObject(i).getString("id");
                                                cameraname = data.getJSONObject(i).getString("name");
                                                RequestParams params1 = new RequestParams(RequestUtils.REQUEST_URL + "/resource/api/guide/getVideo");
                                                params1.addHeader("Authorization", "bearer " + user.getToken());
                                                JSONObject camerajson1 = new JSONObject();
                                                try {
                                                    camerajson1.put("id", cameraid);
                                                    camerajson1.put("enumCode", "10003");
                                                    camerajson1.put("startTime", st);
                                                    camerajson1.put("endTime", et);
                                                    camerajson1.put("protocol", "rtmp");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                params1.setBodyContent(camerajson1.toString());
                                                Log.e(TAG, "params1 " + params1);
                                                Log.e(TAG, "params1 " + camerajson1);
                                                x.http().post(params1, new Callback.CommonCallback<String>() {

                                                    @Override
                                                    public void onSuccess(String result) {
                                                        Log.e(TAG, "camera1 " + result);
                                                        JSONObject jsonObject2 = null;
                                                        try {
                                                            jsonObject2 = new JSONObject(result);
                                                            if (jsonObject2.getString("code").equals("200")) {
                                                                JSONArray data = jsonObject2.getJSONArray("data");
                                                                rtspurl = data.getJSONObject(0).getJSONObject("data").getString("url");
                                                                Log.e(TAG, "rtspurl: "+rtspurl );
                                                                if (rtspurl == null) {
                                                                    Toast.makeText(getContext(), "暂无可见光视频", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
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
                });
        RxViewAction.clickNoDouble(ziyuanPaichaButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ARouter.getInstance().build(RouteUtils.AddResourceChecking)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
    }

    private void showAroundKmChangeDialog() {
        //默认选中第三个  //1Km   3Km   5Km   10Km   15Km
        final String[] items = {"1Km", "3Km", "5Km", "10Km", "15Km"};
        builder = new AlertDialog.Builder(getContext()).setIcon(R.mipmap.ic_launcher).setTitle("选择周边距离")
                .setSingleChoiceItems(items,choose1 , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choose1 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aroundList.clear();
                        JSONArray list = new JSONArray();
                        if(aroundObj == null){
                            Toast.makeText(getActivity(), "请先在地图上选择周边点位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (choose1 == 0){
                            aroundKm = 1;
                            aroundFenleiTextView.setText("1Km");
                            try {
                                JSONArray list1 = aroundObj.getJSONArray("list1");
                                list = list1;
                                aroundList = new Gson().fromJson(String.valueOf(list1), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 1){
                            aroundKm = 3;
                            aroundFenleiTextView.setText("3Km");
                            try {
                                JSONArray list3 = aroundObj.getJSONArray("list3");
                                list = list3;
                                aroundList = new Gson().fromJson(String.valueOf(list3), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 2){
                            aroundKm = 5;
                            aroundFenleiTextView.setText("5Km");
                            try {
                                JSONArray list5 = aroundObj.getJSONArray("list5");
                                list = list5;
                                aroundList = new Gson().fromJson(String.valueOf(list5), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 3){
                            aroundKm = 10;
                            aroundFenleiTextView.setText("10Km");
                            try {
                                JSONArray list10 = aroundObj.getJSONArray("list10");
                                list = list10;
                                aroundList = new Gson().fromJson(String.valueOf(list10), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (choose1 == 4){
                            aroundKm = 15;
                            aroundFenleiTextView.setText("15Km");
                            try {
                                JSONArray list15 = aroundObj.getJSONArray("list15");
                                list = list15;
                                aroundList = new Gson().fromJson(String.valueOf(list15), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                                }.getType());
                                for (int x = 0; x < aroundList.size(); x++) {
                                    JSONObject obj = (JSONObject) list.get(x);
                                    aroundList.get(x).setObj(obj);
                                }
                                initAroundData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //清除所有资源点
                        clearAllResourceMarker();
                        //添加资源点
                        JSONArray finalList = list;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    //分组添加
                                    JSONArray array_sp = new JSONArray();
                                    JSONArray array_xf = new JSONArray();
                                    JSONArray array_wx = new JSONArray();
                                    JSONArray array_wz = new JSONArray();
                                    JSONArray array_md = new JSONArray();
                                    JSONArray array_sy1 = new JSONArray();
                                    JSONArray array_sy2 = new JSONArray();
                                    JSONArray array_sy3 = new JSONArray();
                                    JSONArray array_hl = new JSONArray();
                                    JSONArray array_sl = new JSONArray();
                                    JSONArray array_zsj = new JSONArray();
                                    for (int n = 0; n < finalList.length(); n++) {
                                        JSONObject o = (JSONObject) finalList.get(n);
                                        allResourcePoints.add(o);
                                        o.put("state","ACTIVE");
                                        String resourceTypes = o.getString("resourceType");
                                        if (resourceTypes.equals("monitor")) {//视频监控点
                                            array_sp.put(o);
                                        }else if (resourceTypes.equals("team")){//消防专业队
                                            array_xf.put(o);
                                        }else if (resourceTypes.equals("dangerSource")){//危险源
                                            array_wx.put(o);
                                        }else if (resourceTypes.equals("foreastRoom")||resourceTypes.equals("materialRepository")){//物资储备库
                                            array_wz.put(o);
                                        }else if (resourceTypes.equals("cemetery")){//墓地
                                            array_md.put(o);
                                        }else if (resourceTypes.contains("waterSource")){//水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                            array_sy1.put(o);
                                        }else if (resourceTypes.contains("watchTower")){//水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                            array_sy2.put(o);
                                        }else if (resourceTypes.contains("fireCommand")){//水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                            array_sy3.put(o);
                                        }else if (resourceTypes.equals("checkStation")){//护林检查站
                                            array_hl.put(o);
                                        }else if (resourceTypes.equals("foreastCenter")){//森林防火监测中心
                                            array_sl.put(o);
                                        }else if (resourceTypes.equals("helicopterPoint")){//直升机升降点
                                            array_zsj.put(o);
                                        }
                                    }
                                    dWebView.callHandler("showPointforresource", new Object[]{"monitor", array_sp, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"team", array_xf, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"dangerSource", array_wx, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"foreastRoom", array_wz, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"cemetery", array_md, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    //水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                    dWebView.callHandler("showPointforresource", new Object[]{"waterSource", array_sy1, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"watchTower", array_sy2, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"fireCommand", array_sy3, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"checkStation", array_hl, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"foreastCenter", array_sl, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"helicopterPoint", array_zsj, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                }catch(Exception e){

                                }
                            }
                        },2000);
                    }
                });
        builder.create().show();
    }

    private void showOneBodyFenleiChangeDailog() {
        //默认选中第一个  //0疑似火情  1是真实火情  2是未处理 3是全部
        final String[] items = {"全部", "未处理", "真实火点"};
        isReleasList = 3;
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
                        /*if (choose1 == 3) {
                            isReleasList = 0;
                            fenleiTextView.setText("疑似火点");
                            for (int j = 0; j < oneBodyFireList.size(); j++) {
                                if (oneBodyFireList.get(j).getIsReal()!=null) {
                                    if (oneBodyFireList.get(j).getIsReal()== 0) {
                                        oneBodyFireFenleiList.add(oneBodyFireList.get(j));
                                    }

                                }
                            }
                          //  currentPage = 1;
                            initOneBodyFireData();
                        } else */if (choose1 == 2){
                            isReleasList = 1;
                            fenleiTextView.setText("真实火点");
                            for (int j = 0; j < oneBodyFireList.size(); j++) {
                                if (oneBodyFireList.get(j).getIsReal()!=null) {
                                    if (oneBodyFireList.get(j).getIsReal()== 1) {
                                        oneBodyFireFenleiList.add(oneBodyFireList.get(j));
                                    }

                                }
                            }
                            //   currentPage = 1;
                            initOneBodyFireData();
                        } else if (choose1 == 1){
                            isReleasList = 2;
                            fenleiTextView.setText("未处理");
                            for (int j = 0; j < oneBodyFireList.size(); j++) {
                                if (oneBodyFireList.get(j).getIsReal()==null) {
                                    oneBodyFireFenleiList.add(oneBodyFireList.get(j));
                                }
                            }
                            //   currentPage = 1;
                            initOneBodyFireData();
                        } else {
                            isReleasList = 3;
                            fenleiTextView.setText("全部");
                            oneBodyFireFenleiList.addAll(oneBodyFireList);
                            //    currentPage = 1;
                            initOneBodyFireData();
                        }
                        //initWeixingData();
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
        around_view = ((TextView) getView().findViewById(R.id.around_view));
        addres_view = ((TextView) getView().findViewById(R.id.addres_view));
        ziyuanButton = ((TextView) getView().findViewById(R.id.ziyuan_button));
        alarmlistLayout =  getView().findViewById(R.id.alarmlist_layout);
        ziyuanButtonLayout = ((LinearLayout) getView().findViewById(R.id.ziyuan_button_layout));
        ziyuanPlanButton= getView().findViewById(R.id.ziyuan_plan_button);
        check_layout= getView().findViewById(R.id.ziyuan_layout);
        ziyuanCheckButton=getView().findViewById(R.id.ziyuan_check_button);
        ziyuanPaichaButton=getView().findViewById(R.id.ziyuan_paicha_button);
        if((!CommonUtils.hasPermission(getActivity(),"app-map-btn-check"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-map-btn-check-task"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-map-btn-check-resource"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-map-btn-check-daily"))){//地图-检查
            check_layout.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-check-task")){//地图-检查任务
            ziyuanPlanButton.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-check-resource")){//地图-资源检查
            ziyuanCheckButton.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-check-daily")){//地图-日常检查
            ziyuanPaichaButton.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-firealarm")){//地图-报警列表
            alarmlistLayout.setVisibility(View.GONE);
        }
        if((!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-type"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-other"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-add"))){//地图-资源权限
            ziyuanButtonLayout.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-type")){//地图-资源类型
            retypeView.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-other")){//地图-资源周边
            around_view.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-map-btn-resource-add")){//地图-资源添加
            addres_view.setVisibility(View.GONE);
        }
        if(new DbConfig(getActivity()).getUser().getJobTitle().contains("街道")){
            ziyuanCheckButton.setVisibility(View.GONE);
            ziyuanPaichaButton.setVisibility(View.GONE);
        }
        ziyuanComprehensivecheckButton=getView().findViewById(R.id.ziyuan_comprehensivecheck_button);
        /*卫星布局加载*/
        weixingButton = ((TextView) getView().findViewById(R.id.weixing_button));
        weixingButtonLayout = ((LinearLayout) getView().findViewById(R.id.weixing_button_layout));
        weixinLayout = ((LinearLayout) getView().findViewById(weixing_layout));
        weixingShezhiView = ((TextView) getView().findViewById(R.id.weixing_shezhi_view));
        weixingChaxunView = ((TextView) getView().findViewById(R.id.weixing_chaxun_ciew));
        weixingLiebiaoView = ((TextView) getView().findViewById(R.id.weixing_list_view));
        if((!CommonUtils.hasPermission(getActivity(),"app-map-btn-satelliteFirealarm"))//地图-卫星权限
                &&(!CommonUtils.hasPermission(getActivity(),"app-satelliteFirealarm-btn-list"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-satelliteFirealarm-btn-query"))
                &&(!CommonUtils.hasPermission(getActivity(),"app-satelliteFirealarm-btn-setting"))){
            weixinLayout.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-satelliteFirealarm-btn-list")){//地图-卫星-火情列表
            weixingLiebiaoView.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-satelliteFirealarm-btn-query")){//地图-卫星-火情查询
            weixingChaxunView.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-satelliteFirealarm-btn-setting")){//地图-卫星-卫星设置
            weixingShezhiView.setVisibility(View.GONE);
        }

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
        weixingFireDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clickOtherRes = false;
            }
        });
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
        onebodyListView = ((RecyclerView) onebodyListInflater.findViewById(R.id.onebody_info_listview));
        //shaixuanbutton=((TextView) onebodyListInflater.findViewById(R.id.shaixuan));
        onebodyListDialog.setContentView(onebodyListInflater);
//        onebodyLayout =((LinearLayout) getView().findViewById(R.id.onebady_layout));
//        onebodyButton = ((TextView) getView().findViewById(R.id.onebody_button));
        fenleiTextView = ((TextView) onebodyListInflater.findViewById(R.id.fenlei_view));
        oneBodyFenleiLayout = ((LinearLayout) onebodyListInflater.findViewById(R.id.fenlei_layout));
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
        //加载更多
        onebodyListView.setOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "onLoadMore: lastPage-" + lastPage);
                Log.e(TAG, "onLoadMore: currentPage- " + currentPage);
           /*     if (lastPage > currentPage){
                    currentPage += 1;
                    Log.i(TAG, "onLoadMore: "+currentPage);
                    progressDialog.dismiss();
                     isShowSearchDialog = true;
                getonebodyDataFromSetvice();
                }*/

            }
        });
        /**
         *  一体机火点详细信息
         */
        oneBodyFireDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        oneBodyFireInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fire_onebody, null);
        oneBodyFireInflater.setMinimumWidth(10000);
        mingchengView = ((TextView) oneBodyFireInflater.findViewById(R.id.mingcheng_view));
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
        oneBodyFireDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clickOtherRes = false;
            }
        });
        Window oneBodyfireDialogWindow = oneBodyFireDialog.getWindow();
        oneBodyfireDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams oneBodyLpFire = oneBodyfireDialogWindow.getAttributes();
        oneBodyfireDialogWindow.setAttributes(oneBodyLpFire);
        oneBodyFireDialog.setCanceledOnTouchOutside(true);


        //原资源点Dialog
        resourceListDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        resourceListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_resource_list, null);
        resourceListInflater.setMinimumWidth(10000);
        resourceListView = resourceListInflater.findViewById(R.id.resource_listview);
        tv_type = resourceListInflater.findViewById(R.id.tv_type);
        tv_grid = resourceListInflater.findViewById(R.id.tv_grid);
        sv_grid = resourceListInflater.findViewById(R.id.sv_grid);
        ll_grid = resourceListInflater.findViewById(R.id.ll_grid);

        //资源点周边Dialog
        aroundDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        aroundListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_around_list, null);
        aroundListInflater.setMinimumWidth(10000);
        aroundListView = ((RecyclerView) aroundListInflater.findViewById(R.id.onebody_info_listview));
        ll_around_loc = ((LinearLayout) aroundListInflater.findViewById(R.id.ll_around_loc));
        tv_around_loc = ((TextView) aroundListInflater.findViewById(R.id.tv_around_loc));
        tv_around_clear = ((TextView) aroundListInflater.findViewById(R.id.tv_around_clear));
        aroundDialog.setContentView(aroundListInflater);
        aroundFenleiTextView = ((TextView) aroundListInflater.findViewById(R.id.fenlei_view));
        aroundFenleiLayout = ((LinearLayout) aroundListInflater.findViewById(R.id.fenlei_layout));
        aroundFenleiTextView.setText(aroundKm + "Km");
        Window aroundListWindow = aroundDialog.getWindow();
        aroundListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams aroundListLp = aroundListWindow.getAttributes();

        WindowManager aroundwm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int aroundheight = aroundwm.getDefaultDisplay().getHeight();
        aroundListLp.height = (int) (aroundheight * 0.8);
        aroundListWindow.setAttributes(aroundListLp);
        aroundDialog.setCanceledOnTouchOutside(true);
        RxViewAction.clickNoDouble(aroundFenleiLayout).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                showAroundKmChangeDialog();
            }
        });
        RxViewAction.clickNoDouble(ll_around_loc).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                aroundDialog.dismiss();
                Toast.makeText(getActivity(), "请在地图上点击选择周边位置", Toast.LENGTH_SHORT).show();
                canAroundClick = true;
            }
        });
        RxViewAction.clickNoDouble(tv_around_clear).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                aroundDialog.dismiss();
                Toast.makeText(getActivity(), "已重置", Toast.LENGTH_SHORT).show();
                clearAllResourceMarker();
                //清除旧的卫星定位
                dWebView.callHandler("clearFireCircleDs", new Object[]{""}, new OnReturnValue<String>() {
                    @Override
                    public void onValue(String retValue) {
                        Log.e(TAG, "onValue:  clearFireCircleDs " + retValue);
                    }
                });
                tv_around_loc.setText("请选择周边位置");
                aroundObj = null;
                aroundList.clear();
                initAroundData();
            }
        });

        LinearLayoutManager aroundlinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        aroundListView.setLayoutManager(aroundlinearLayoutManager);
        aroundAdapter = new MultiTypeAdapter(aroundItems);

        AroundViewBinder aroundViewBinder = new AroundViewBinder();
        aroundViewBinder.setListener(this);
        aroundAdapter.register(com.haohai.platform.mapmodel.multitype.Resource.class, aroundViewBinder);
        aroundAdapter.register(Empty.class,new EmptyViewBinder());
        aroundListView.setAdapter(aroundAdapter);
        assertHasTheSameAdapter(aroundListView, aroundAdapter);

        /**
         *  视频资源点详细信息
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
        resourceinfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clickOtherRes = false;
            }
        });
        Window resourceDialogWindow = resourceinfoDialog.getWindow();
        resourceDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceLpFire = resourceDialogWindow.getAttributes();
        resourceDialogWindow.setAttributes(resourceLpFire);
        resourceinfoDialog.setCanceledOnTouchOutside(true);

        /**
         *  其它资源点详细信息
         */
        otherresourceinfoDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        otherresourceInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_resource_info_other, null);
        otherresourceInflater.setMinimumWidth(10000);
        otherresourcenameview = ((TextView) otherresourceInflater.findViewById(R.id.resourcename_view));
        otherresoucedizhiview = ((TextView) otherresourceInflater.findViewById(R.id.resoucedizhi_view));
        res_edit = ((TextView) otherresourceInflater.findViewById(R.id.res_edit));
        res_delete = ((TextView) otherresourceInflater.findViewById(R.id.res_delete));
        otherresourcejingweiduview = ((TextView) otherresourceInflater.findViewById(R.id.resourcejingweidu_view));
        iv_guide = ((ImageView) otherresourceInflater.findViewById(R.id.iv_guide));

        otherresourceinfoDialog.setContentView(otherresourceInflater);
        otherresourceinfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clickOtherRes = false;
            }
        });
        Window resourceDialogWindowOther = otherresourceinfoDialog.getWindow();
        resourceDialogWindowOther.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceLpFireOther = resourceDialogWindow.getAttributes();
        resourceDialogWindowOther.setAttributes(resourceLpFireOther);
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
    }

    ///资源选中未选中-区&&街道  r有值为资源类型单点 否则为区、街道选择
    private void parseGridPost(boolean selected, String gridId, String gridNo,ResourceList r) {
        new Handler().postDelayed(() -> {
            for (int i = 0; i < resourceListList.size(); i++) {
                resourceListList.get(i).setCheck(false);
            }
            initResourceListData();

            if(!selected){
                //清除所有资源点
                clearAllResourceMarker();
                if(isResourceAll){//当前全市已选 临沂市gridId cd36e64f-2a68-11ec-894b-00ffe368a917 gridNo 3713
                    for (int i = 0; i < resourceListList.size(); i++) {
                        ResourceList resource = resourceListList.get(i);
                        getResourceDataFromGrid(resource.getApiUrl(),"cd36e64f-2a68-11ec-894b-00ffe368a917","3713",resource.getName());
                    }
                    return;
                }
                //区
                for (int i = 0; i < quList.size(); i++) {
                    Grid qu = quList.get(i);
                    if(qu.isStatus()){
                        for (int m = 0; m < resourceListList.size(); m++) {
                            ResourceList resource = resourceListList.get(m);
                            getResourceDataFromGrid(resource.getApiUrl(),qu.getId(),qu.getGridNo(),resource.getName());
                        }
                        continue;
                    }
                    List<Grid> listJieDao = qu.getChildren();
                    //街道
                    for (int j = 0; j < listJieDao.size(); j++) {
                        Grid jiedao = listJieDao.get(j);
                        if(jiedao.isStatus()){
                            for (int m = 0; m < resourceListList.size(); m++) {
                                ResourceList resource = resourceListList.get(m);
                                getResourceDataFromGrid(resource.getApiUrl(),jiedao.getId(),jiedao.getGridNo(),resource.getName());
                            }
                            continue;
                        }
                        List<ResourceList> listRes = jiedao.getResourceList();
                        //资源类型
                        for (int m = 0; m < listRes.size(); m++) {
                            ResourceList res = listRes.get(m);
                            if(res.isStatus()){
                                ResourceList resource = listRes.get(m);
                                getResourceDataFromGrid(resource.getApiUrl(),jiedao.getId(),jiedao.getGridNo(),resource.getName());
                            }
                        }
                    }
                }
            }else{
                //获取当前选中网格资源点数据
                if(r!=null){
                    getResourceDataFromGrid(r.getApiUrl(),gridId,gridNo,r.getName());
                }else{
                    for (int i = 0; i < resourceListList.size(); i++) {
                        ResourceList resource = resourceListList.get(i);
                        getResourceDataFromGrid(resource.getApiUrl(),gridId,gridNo,resource.getName());
                    }
                }
            }
        },1000);
    }

    //清除资源类型
    private void clearTypeResource(){
        for (int i = 0; i < resourceListList.size(); i++) {
            resourceListList.get(i).setCheck(false);
        }
        //清除旧的卫星定位
        dWebView.callHandler("removeDataSource", new Object[]{resorcetype}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.e(TAG, "onValue:  qingchu" + retValue);
            }
        });
        if(Objects.equals(resorcetype, "materialRepository")){//物资储备库修改过字段。。
            dWebView.callHandler("removeDataSource", new Object[]{"foreastRoom"}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.d("jsbridge", "call succeed,removeDataSource value is " + retValue);
                }
            });
        }
        if(Objects.equals(resorcetype, "watchTower")||Objects.equals(resorcetype, "fireCommand")){//水源地 现有 waterSource 新建 watchTower 改造 fireCommand
            dWebView.callHandler("removeDataSource", new Object[]{"waterSource"}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.d("jsbridge", "call succeed,return value is " + retValue);
                }
            });
        }
    }
    private void initGridIntoDb() {
        DbManager db = new DbConfig(getActivity()).getDbManager();
        List<Grid> list = new ArrayList<>();
        try {
            list = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .findAll();
        }catch (Exception e){
        }
        if(list == null){
            list = new ArrayList<>();
        }
        User root = new DbConfig(getContext()).getUser();
        String gridRoot = root.getGridRoot();

        if(list.size()>0 && Objects.equals(gridRoot, "yes")){
            initResGridView();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("updateTime", "2010-11-21T08:36:31.420Z");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/grid/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + user.getToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess13------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        gridList.clear();
                        Gson gson = new Gson();
                        gridList = gson.fromJson(String.valueOf(data), new TypeToken<List<Grid>>(){}.getType());
                        Log.e(TAG, "onSuccess: " +gridList.size() );
                        DbConfig dbConfig = new DbConfig(getContext());
                        DbManager db = dbConfig.getDbManager();
                        try {
                            db.saveOrUpdate(gridList);
                            User users = dbConfig.getUser();
                            users.setGridRoot("yes");
                            db.saveOrUpdate(users);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initResGridView();
                                }
                            },60000);
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
                Log.e(TAG, "onError: materialRepository请求失败" + ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }
    private void initResGridView() {
        /**
         * 资源点列表 dialog
         */
        ll_grid.removeAllViews();
        RxViewAction.clickNoDouble(tv_type).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                tv_type.setTextColor(getResources().getColor(R.color.white_tr));
                tv_type.setBackgroundResource(R.drawable.resource_choosed);
                tv_grid.setTextColor(getResources().getColor(R.color.c5));
                tv_grid.setBackgroundResource(R.drawable.resource);
                resourceListView.setVisibility(View.VISIBLE);
                sv_grid.setVisibility(View.GONE);
                resourceType = true;
            }
        });
        RxViewAction.clickNoDouble(tv_grid).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                tv_grid.setTextColor(getResources().getColor(R.color.white_tr));
                tv_grid.setBackgroundResource(R.drawable.resource_choosed);
                tv_type.setTextColor(getResources().getColor(R.color.c5));
                tv_type.setBackgroundResource(R.drawable.resource);
                sv_grid.setVisibility(View.VISIBLE);
                resourceListView.setVisibility(View.GONE);
                resourceType = false;
            }
        });
        try {
            DbManager db = new DbConfig(getActivity()).getDbManager();
            quList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .and("level", "=", "3")
                    .and("groupid", "like", "001003%")
                    .findAll();
            if(quList == null){
                quList = new ArrayList<>();
            }
        }catch (Exception e){
        }
        //构建市
        View item = null;
        try{
            item = LayoutInflater.from(getActivity()).inflate(com.haohai.platform.platformmodel.R.layout.item_gridtrees,null);
        }catch (Exception e){
            Log.e(TAG, "initResGridView: error" + e.toString());
        }
        if(item == null){
            return;
        }
        LinearLayout ll_outOut = item.findViewById(com.haohai.platform.platformmodel.R.id.ll_out);
        LinearLayout ll_inOut = item.findViewById(com.haohai.platform.platformmodel.R.id.ll_in);//用于监控点-摄像头便于动态加载
        ImageView iv_statusOut = item.findViewById(com.haohai.platform.platformmodel.R.id.iv_status);
        ImageView iv_statusOut2 = item.findViewById(com.haohai.platform.platformmodel.R.id.iv_status2);
        iv_statusOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
        iv_statusOut2.setImageDrawable(getResources().getDrawable(R.drawable.square));
        iv_statusOut2.setVisibility(View.VISIBLE);
        TextView tv_gridtreesOut = item.findViewById(com.haohai.platform.platformmodel.R.id.tv_gridtrees);
        tv_gridtreesOut.setText("临沂市");
        final boolean[] cityTag = {false};
        ll_inOut.setVisibility(View.GONE);
        iv_statusOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        RxViewAction.clickNoDouble(iv_statusOut2).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                isResourceAll = !isResourceAll;
                if(isResourceAll){
                    iv_statusOut2.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                    /*cityTag[0] = false;
                    iv_statusOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    ll_inOut.setVisibility(View.GONE);*/
                    //市 选中 --》所有选中 （子节点gridNo.contains祖节点gridNo）
                    sendBroadcastResourceSelect("3713",true,0,"cd36e64f-2a68-11ec-894b-00ffe368a917");
                }else{
                    iv_statusOut2.setImageDrawable(getResources().getDrawable(R.drawable.square));
                    /*cityTag[0] = true;
                    iv_statusOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                    ll_inOut.setVisibility(View.VISIBLE);*/
                    //市 未选 --》所有未选 （子节点gridNo.contains祖节点gridNo）
                    sendBroadcastResourceSelect("3713",false,0,"cd36e64f-2a68-11ec-894b-00ffe368a917");
                }
            }
        });
        RxViewAction.clickNoDouble(ll_outOut).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                cityTag[0] = !cityTag[0];
                if(cityTag[0]){
                    iv_statusOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                    ll_inOut.setVisibility(View.VISIBLE);
                }else{
                    iv_statusOut.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    ll_inOut.setVisibility(View.GONE);
                }
            }
        });
        for (int i = 0; i < quList.size(); i++) {
            //构建区
            final boolean[] quItemStatus = {false};
            View itemIn = LayoutInflater.from(getActivity()).inflate(com.haohai.platform.platformmodel.R.layout.item_gridtrees,null);
            LinearLayout ll_outIn = itemIn.findViewById(com.haohai.platform.platformmodel.R.id.ll_out);
            LinearLayout ll_inIn = itemIn.findViewById(com.haohai.platform.platformmodel.R.id.ll_in);//用于监控点-摄像头便于动态加载
            ImageView iv_statusIn = itemIn.findViewById(com.haohai.platform.platformmodel.R.id.iv_status);
            ImageView iv_statusIn2 = itemIn.findViewById(com.haohai.platform.platformmodel.R.id.iv_status2);
            iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
            iv_statusIn2.setImageDrawable(getResources().getDrawable(R.drawable.square));
            iv_statusIn2.setVisibility(View.VISIBLE);
            TextView tv_gridtreesIn = itemIn.findViewById(com.haohai.platform.platformmodel.R.id.tv_gridtrees);

            tv_gridtreesIn.setText(quList.get(i).getName());
            List<Grid> jiedaoList = new ArrayList<>();
            try {
                jiedaoList = db.selector(Grid.class)
                        .where("state", "=", "ACTIVE")
                        .where("level", "=", "1")
                        .where("parentid", "=", quList.get(i).getId())
                        .findAll();
                if(jiedaoList == null){
                    jiedaoList = new ArrayList<>();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            quList.get(i).setChildren(jiedaoList);
            iv_statusIn2.setTag(i);
            ll_inIn.setVisibility(View.GONE);//
            iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));//
            RxViewAction.clickNoDouble(iv_statusIn2).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    int index = (int) iv_statusIn2.getTag();
                    quList.get(index).setStatus(!(quList.get(index).isStatus()));
                    if (quList.get(index).isStatus()) {
                        iv_statusIn2.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                        /*quItemStatus[0] = false;//
                        ll_inIn.setVisibility(View.GONE);//
                        iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));//*/
                        //区 选中 --》所有选中 （子节点gridNo.contains祖节点gridNo）
                        sendBroadcastResourceSelect(quList.get(index).getGridNo(),true,1,quList.get(index).getId());
                    } else {
                        iv_statusIn2.setImageDrawable(getResources().getDrawable(R.drawable.square));
                        /*quItemStatus[0] = true;//
                        ll_inIn.setVisibility(View.VISIBLE);//
                        iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));//*/
                        //区 未选 --》所有选中 （子节点gridNo.contains祖节点gridNo）
                        sendBroadcastResourceSelect(quList.get(index).getGridNo(),false,1,quList.get(index).getId());
                    }
                    //TODO 去除所选
                    //TODO post所选数据
                    Log.e(TAG, "call: bingo " + quList.toString() );
                }
            });
            RxViewAction.clickNoDouble(ll_outIn).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    quItemStatus[0] = !quItemStatus[0];
                    if(quItemStatus[0]){
                        ll_inIn.setVisibility(View.VISIBLE);
                        iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                    }else{
                        ll_inIn.setVisibility(View.GONE);
                        iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    }
                }
            });
            //资源网格点击回调广播-区
            BroadcastReceiver broadcastReceiverQu = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        int index = (int) iv_statusIn2.getTag();
                        String gridNo = intent.getStringExtra("gridNo");
                        boolean status = intent.getBooleanExtra("status",false);
                        if(quList.get(index).getGridNo().contains(gridNo)){
                            if(status){
                                iv_statusIn2.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                                quList.get(index).setStatus(true);
                                quItemStatus[0] = false;//
                                ll_inIn.setVisibility(View.GONE);//
                                iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));//
                            } else {
                                iv_statusIn2.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                quList.get(index).setStatus(false);
                                quItemStatus[0] = true;//
                                ll_inIn.setVisibility(View.VISIBLE);//
                                iv_statusIn.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));//
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            IntentFilter intentFilterQu = new IntentFilter();
            intentFilterQu.addAction("SELECT");
            getActivity().registerReceiver(broadcastReceiverQu, intentFilterQu);
            broadcastList.add(broadcastReceiverQu);
            for (int j = 0; j < quList.get(i).getChildren().size(); j++) {
                //构建街道
                List<IGridRes> iGridResList = new ArrayList<>();
                final boolean[] jdItemStatus = {false};
                View itemJd = LayoutInflater.from(getActivity()).inflate(com.haohai.platform.platformmodel.R.layout.item_gridtrees,null);
                LinearLayout ll_outJd = itemJd.findViewById(com.haohai.platform.platformmodel.R.id.ll_out);
                LinearLayout ll_inJd = itemJd.findViewById(com.haohai.platform.platformmodel.R.id.ll_in);//用于监控点-摄像头便于动态加载
                ImageView iv_statusJd = itemJd.findViewById(com.haohai.platform.platformmodel.R.id.iv_status);
                ImageView iv_statusJd2 = itemJd.findViewById(com.haohai.platform.platformmodel.R.id.iv_status2);
                iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                iv_statusJd2.setImageDrawable(getResources().getDrawable(R.drawable.square));
                iv_statusJd2.setVisibility(View.VISIBLE);
                TextView tv_gridtreesJd = itemJd.findViewById(com.haohai.platform.platformmodel.R.id.tv_gridtrees);
                tv_gridtreesJd.setText(quList.get(i).getChildren().get(j).getName());

                iv_statusJd2.setTag(j);
                int finalI = i;
                ll_inJd.setVisibility(View.GONE);//
                iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));//
                RxViewAction.clickNoDouble(iv_statusJd2).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void unused) {
                        int index = (int) iv_statusJd2.getTag();
                        quList.get(finalI).getChildren().get(index).setStatus(!(quList.get(finalI).getChildren().get(index).isStatus()));
                        if (quList.get(finalI).getChildren().get(index).isStatus()) {
                            iv_statusJd2.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                            /*jdItemStatus[0] = false;//
                            ll_inJd.setVisibility(View.GONE);//
                            iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));//*/
                            //街道 选中 --》所有选中 （子节点gridNo.contains祖节点gridNo）
                            sendBroadcastResourceSelect(quList.get(finalI).getChildren().get(index).getGridNo(),true,2,quList.get(finalI).getChildren().get(index).getId());
                        } else {
                            iv_statusJd2.setImageDrawable(getResources().getDrawable(R.drawable.square));
                            /*jdItemStatus[0] = true;//
                            ll_inJd.setVisibility(View.VISIBLE);//
                            iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));//*/
                            //街道 未选 --》所有选中 （子节点gridNo.contains祖节点gridNo）
                            sendBroadcastResourceSelect(quList.get(finalI).getChildren().get(index).getGridNo(),false,2,quList.get(finalI).getChildren().get(index).getId());
                        }
                        //接口通信(街道 控制下面资源点状态)
                        for (int m = 0; m < iGridResList.size(); m++) {
                            IGridRes iGridRes = iGridResList.get(m);
                            iGridRes.selectChanged(quList.get(finalI).getChildren().get(index).getGridNo(),quList.get(finalI).getChildren().get(index).isStatus());
                        }
                    }
                });
                RxViewAction.clickNoDouble(ll_outJd).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void unused) {
                        jdItemStatus[0] = !jdItemStatus[0];
                        if(jdItemStatus[0]){
                            ll_inJd.setVisibility(View.VISIBLE);
                            iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                        }else{
                            ll_inJd.setVisibility(View.GONE);
                            iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                        }
                    }
                });

                //资源网格点击回调广播-街道
                BroadcastReceiver broadcastReceiverJd = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            int index = (int) iv_statusJd2.getTag();
                            String gridNo = intent.getStringExtra("gridNo");
                            boolean status = intent.getBooleanExtra("status",false);
                            if(quList.get(finalI).getChildren().get(index).getGridNo().contains(gridNo)){
                                if(status){
                                    iv_statusJd2.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                                    quList.get(finalI).getChildren().get(index).setStatus(true);
                                    jdItemStatus[0] = false;//
                                    ll_inJd.setVisibility(View.GONE);//
                                    iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));//
                                } else {
                                    iv_statusJd2.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                    quList.get(finalI).getChildren().get(index).setStatus(false);
                                    jdItemStatus[0] = true;//
                                    ll_inJd.setVisibility(View.VISIBLE);//
                                    iv_statusJd.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));//
                                }
                                //接口通信(街道 控制下面资源点状态)
                                for (int m = 0; m < iGridResList.size(); m++) {
                                    IGridRes iGridRes = iGridResList.get(m);
                                    iGridRes.selectChanged(quList.get(finalI).getChildren().get(index).getGridNo(),status);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                IntentFilter intentFilterJd = new IntentFilter();
                intentFilterJd.addAction("SELECT");
                getActivity().registerReceiver(broadcastReceiverJd, intentFilterJd);
                broadcastList.add(broadcastReceiverJd);
                //资源列表回调广播
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            int index = (int) iv_statusJd2.getTag();
                            final String gridNoRes = quList.get(finalI).getChildren().get(index).getGridNo();
                            quList.get(finalI).getChildren().get(index).setResourceList(new ArrayList<>(resourceListList));
                            for (int k = 0; k < quList.get(finalI).getChildren().get(index).getResourceList().size(); k++) {
                                View cameraView = LayoutInflater.from(getActivity()).inflate(com.haohai.platform.platformmodel.R.layout.item_gridcamera,null);
                                LinearLayout ll_camera = cameraView.findViewById(com.haohai.platform.platformmodel.R.id.ll_camera);
                                View view = cameraView.findViewById(com.haohai.platform.platformmodel.R.id.view);
                                view.setVisibility(View.VISIBLE);
                                ImageView iv_camera = cameraView.findViewById(com.haohai.platform.platformmodel.R.id.iv_camera);//isOnLine 0 离线 1 在线
                                iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                TextView tv_camera = cameraView.findViewById(com.haohai.platform.platformmodel.R.id.tv_camera);
                                tv_camera.setText(quList.get(finalI).getChildren().get(index).getResourceList().get(k).getName());

                                iv_camera.setTag(k);
                                RxViewAction.clickNoDouble(ll_camera).subscribe(new Action1<Void>() {
                                    @Override
                                    public void call(Void unused) {
                                        int indexK = (int) iv_camera.getTag();
                                        quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).setStatus(!(quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).isStatus()));
                                        if (quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).isStatus()) {
                                            iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                                            parseGridPost(true,quList.get(finalI).getChildren().get(index).getId(),quList.get(finalI).getChildren().get(index).getGridNo(),quList.get(finalI).getChildren().get(index).getResourceList().get(indexK));
                                        } else {
                                            iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                            parseGridPost(false,quList.get(finalI).getChildren().get(index).getId(),quList.get(finalI).getChildren().get(index).getGridNo(),quList.get(finalI).getChildren().get(index).getResourceList().get(indexK));
                                        }
                                    }
                                });
                                IGridRes iGridRes = new IGridRes() {
                                    @Override
                                    public void selectChanged(String gridNo, boolean status) {
                                        int indexK = (int) iv_camera.getTag();
                                        if(gridNoRes.contains(gridNo)){
                                            if(status){
                                                iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                                                quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).setStatus(true);
                                            } else {
                                                iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                                quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).setStatus(false);
                                            }
                                        }
                                    }
                                };
                                iGridResList.add(iGridRes);
                                /*//资源网格点击回调广播-资源 （性能问题已弃用 改用接口通信）
                                BroadcastReceiver broadcastReceiverRes = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        try {
                                            int indexK = (int) iv_camera.getTag();
                                            String gridNo = intent.getStringExtra("gridNo");
                                            boolean status = intent.getBooleanExtra("status",false);
                                            if(gridNoRes.contains(gridNo)){
                                                if(status){
                                                    iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square_check));
                                                    quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).setStatus(true);
                                                } else {
                                                    iv_camera.setImageDrawable(getResources().getDrawable(R.drawable.square));
                                                    quList.get(finalI).getChildren().get(index).getResourceList().get(indexK).setStatus(false);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                IntentFilter intentFilterRes = new IntentFilter();
                                intentFilterRes.addAction("SELECT");
                                try{
                                    getActivity().registerReceiver(broadcastReceiverRes, intentFilterRes);
                                }catch (Exception e){
                                    Log.e(TAG, "onReceive: bingo broadcast " + e.getMessage() );
                                }
                                broadcastList.add(broadcastReceiverRes);*/
                                ll_inJd.addView(cameraView);
                            }
                            ll_inIn.addView(itemJd);

                            getActivity().unregisterReceiver(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("RES");
                getActivity().registerReceiver(broadcastReceiver, intentFilter);
                broadcastList.add(broadcastReceiver);
            }
            ll_inOut.addView(itemIn);
        }
        ll_grid.addView(item);
        resourceListDialog.setContentView(resourceListInflater);
        Window resourceListWindow = resourceListDialog.getWindow();
        resourceListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceListLp = resourceListWindow.getAttributes();

        WindowManager resourcewm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int resourceheight = resourcewm.getDefaultDisplay().getHeight();
        resourceListLp.height = (int) (resourceheight * 0.8);
        resourceListWindow.setAttributes(resourceListLp);
        resourceListDialog.setCanceledOnTouchOutside(true);

        LinearLayoutManager resourcelinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        resourceListView.setLayoutManager(resourcelinearLayoutManager);
        resourceAdapter = new MultiTypeAdapter(resourceItems);


        ResourceListViewBinder resourceListViewBinder = new ResourceListViewBinder();
        resourceListViewBinder.setListener(this);
        resourceAdapter.register(ResourceList.class, resourceListViewBinder);

        resourceListView.setAdapter(resourceAdapter);
        assertHasTheSameAdapter(resourceListView, resourceAdapter);
    }
    private boolean isResLayout = false;
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
                        sendBroadcastResource();
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

    private void sendBroadcastResource() {
        Intent it = new Intent();
        it.setAction("RES");
        getActivity().sendBroadcast(it);
    }

    // （子节点gridNo.contains祖节点gridNo）由此通知子节点选中状态
    private void sendBroadcastResourceSelect(String gridNo,boolean selected,int areaTag,String gridId) {//areaTag 0市 1区 2街道
        Intent it = new Intent();
        it.setAction("SELECT");
        it.putExtra("gridNo",gridNo);
        it.putExtra("status",selected);
        getActivity().sendBroadcast(it);

        parseGridPost(selected,gridId,gridNo,null);
    }


    /**
     * 从服务器获取一体机数据
     */
    private void getonebodyDataFromSetvice() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            jsonObject.put("dto", dto);
            jsonObject.put("limit", 200);
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
                Log.e(TAG, "getonebodyDataonSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        //totalSize=data.getInt("totalSize");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Log.i(TAG, "dataList: "+dataList);
                        totalSize=getJsonObj.getInt("totalSize");
                        Log.i(TAG, "getonebodyDataonSuccess: "+totalSize);
                        lastPage= (totalSize + 50 -1) / 50;     //计算最大分页数
                        Log.i(TAG, "getonebodyDataonSuccess: "+lastPage);
                        Gson gson = new Gson();
                        if (oneBodyFireList.size() > 0) {   //已经有数据
                            JSONObject object = dataList.getJSONObject(0);
                            String id = object.getString("id");
                            if (!id.equals(oneBodyFireList.get(0).getId())) {   //有新的报警数据
                                orderWarnImageView.setVisibility(View.VISIBLE);
                                Log.e(TAG, "onSuccess: 任务单" + oneBodyFireList.size());
                                Intent intenta = new Intent(getContext(), BackgroundMp3Service.class);
                                intenta.putExtra("type", "14");
                                getContext().startService(intenta);
                                oneBodyFireList.clear();
                                List<OneBodyFire> oneBodyFireAllList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<OneBodyFire>>() {
                                }.getType());
                                //将所有疑似火情剔除
                                for (int i = 0; i < oneBodyFireAllList.size(); i++) {
                                    if (oneBodyFireAllList.get(i).getIsReal() != null) {
                                        if (oneBodyFireAllList.get(i).getIsReal() == 1) {
                                            oneBodyFireList.add(oneBodyFireAllList.get(i));
                                        }
                                    } else {
                                        oneBodyFireList.add(oneBodyFireAllList.get(i));
                                    }
                                }
                                isReleasList = 3;
                                oneBodyFireFenleiList.clear();

                                fenleiTextView.setText("未处理");
                                for (int j = 0; j < oneBodyFireList.size(); j++) {
                                    if (oneBodyFireList.get(j).getIsReal() == null) {
                                        oneBodyFireFenleiList.add(oneBodyFireList.get(j));
                                    }
                                }
                                initOneBodyFireData();
                            }
                        } else {
                            oneBodyFireList.clear();
                            List<OneBodyFire> oneBodyFireAllList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<OneBodyFire>>() {
                            }.getType());
                            //将所有疑似火情剔除
                            for (int i = 0; i < oneBodyFireAllList.size(); i++) {
                                if (oneBodyFireAllList.get(i).getIsReal() != null) {
                                    if (oneBodyFireAllList.get(i).getIsReal() == 1) {
                                        oneBodyFireList.add(oneBodyFireAllList.get(i));
                                    }
                                } else {
                                    oneBodyFireList.add(oneBodyFireAllList.get(i));
                                }
                            }
                            isReleasList = 3;
                            oneBodyFireFenleiList.clear();

                            fenleiTextView.setText("未处理");
                            for (int j = 0; j < oneBodyFireList.size(); j++) {
                                if (oneBodyFireList.get(j).getIsReal() == null) {
                                    oneBodyFireFenleiList.add(oneBodyFireList.get(j));
                                }
                            }
                            initOneBodyFireData();
                        }
                        //initOneBodyFireData();
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
     * 初始化周边资源数据
     */
    private void initAroundData() {

        aroundItems.clear();
        if (aroundList.size() == 0){
            aroundItems.add(new Empty("暂无数据"));
        }else {
            aroundItems.addAll(aroundList);
        }


        assertAllRegistered(aroundAdapter, aroundItems);
        aroundAdapter.notifyDataSetChanged();
    }

    private void clearAllResourceMarker() {
        for (int i = 0; i < resourceListList.size(); i++) {
            ResourceList resource = resourceListList.get(i);
            String resourceName = resource.getName();
            String type = "";
            if (resourceName.equals("视频监控点")) {
                type = "monitor";
            }else if (resourceName.equals("直升机机降点")){
                type = "helicopterPoint";
            }else if (resourceName.equals("消防专业队")){
                type = "team";
            }else if (resourceName.equals("队伍驻防点")){
                type = "team";
            }else if (resourceName.equals("危险源")){
                type = "dangerSource";
            }else if (resourceName.equals("物资储备库")){
                type = "foreastRoom";
            }else if (resourceName.equals("墓地")){
                type = "cemetery";
            }else if (resourceName.equals("新建水源地")){
                type = "watchTower";
            }else if (resourceName.equals("改造水源地")){
                type = "fireCommand";
            }else if (resourceName.equals("现有水源地")){
                type = "waterSource";
            }else if (resourceName.equals("规划建设水源地")){
                type = "watchTower";
            }else if (resourceName.equals("护林检查站")){
                type = "checkStation";
            }else if (resourceName.equals("森林防火监测中心")){
                type = "foreastCenter";
            }
            //清除旧的卫星定位
            dWebView.callHandler("removeDataSource", new Object[]{resource.getCode()}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.e(TAG, "onValue:  qingchu getCode " + retValue);
                }
            });
            //清除旧的卫星定位
            dWebView.callHandler("removeDataSource", new Object[]{type}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.e(TAG, "onValue:  qingchu type " + retValue);
                }
            });
        }
    }


    /**
     * 初始化一体机火点数据
     */
    private void initOneBodyFireData() {
        //往地图上打一体机火点
        initYitijiFireMap();
        //往列表上展示数据

        Log.e(TAG, "initOneBodyFireData:size== " + oneBodyFireFenleiList.size() );
       /* if (oneBodyFireFenleiList.size()==0&&currentPage==1){
            onebodyItems.clear();
        }else {
            for (int i = 0; i < oneBodyFireFenleiList.size(); i++) {
                onebodyItems.add(oneBodyFireFenleiList.get(i));
            }
        }*/
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



    Gson gson = new Gson();
    @JavascriptInterface
    public void pointclick(Object msg){
        ResourceType resourceType = gson.fromJson(String.valueOf(msg), ResourceType.class);
        Toast.makeText(getActivity(), resourceType.resourcetype + resourceType.id + "bingo", Toast.LENGTH_SHORT).show();
    }



    /**
     * 地图资源点点击回调
     */
    @Override
    public void onJsEscapeDetailsClickListener(String json) {
        clickOtherRes = true;
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
                b.putString("type", resourcetype);
                b.putInt("what",DIALOG_MOINTOR_SHOW);
                message.setData(b);
                mHandler.sendMessage(message);
            }else{
                String id = jsonObject.getString("id");
                Message message = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("id", id);
                b.putString("type", resourcetype);
                b.putInt("what",DIALOG_RESOURCE);
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

    private boolean canAroundClick = false;
    private String latitude = "0";
    private String longitude = "0";
    @Override
    public void onJsClickListener(String json) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if(clickOtherRes){
                        return;
                    }
                    if(!canAroundClick){
                        return;
                    }
                    canAroundClick = false;
                    JSONObject jsonObject = new JSONObject(json);
                    latitude = jsonObject.getString("latitude");
                    longitude = jsonObject.getString("longitude");
                    String lngStr = longitude+"";
                    String latStr = latitude+"";
                    try{
                        lngStr = lngStr.substring(0,12);
                        latStr = latStr.substring(0,12);
                    }catch (Exception e){
                    }
                    tv_around_loc.setText("当前选择:"+lngStr+","+latStr);
                    postAround5Km(Double.parseDouble(latitude),Double.parseDouble(longitude));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            aroundDialog.show();
                        }
                    });
                    //资源类型列表状态恢复
                    reStartResType();
                    //资源网格状态恢复
                    sendBroadcastResourceSelect("3713",false,0,"cd36e64f-2a68-11ec-894b-00ffe368a917");
                    JSONObject object = new JSONObject();
                    object.put("lng",Double.parseDouble(longitude));
                    object.put("lat",Double.parseDouble(latitude));
                    object.put("dsName","周边");
                    dWebView.callHandler("addFireCircleDs", new Object[]{new Gson().toJson(object.toString())}, new OnReturnValue<String>() {
                        @Override
                        public void onValue(String retValue) {
                            Log.e(TAG, "onValue:  yuan" + retValue);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },500);
    }

    private void reStartResType() {
        for (int i = 0; i < resourceListList.size(); i++) {
            resourceListList.get(i).setCheck(false);
        }
        initResourceListData();
    }

    private List<com.haohai.platform.mapmodel.multitype.Resource> aroundList = new ArrayList<>();
    private JSONObject aroundObj;
    /**
     * 查询周边5Km资源
     * @param latitude
     * @param longitude
     */
    private void postAround5Km(double latitude, double longitude) {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/resourceList/getAroundResourceListNew");
        Around around = new Around(new Around.Position(latitude,longitude),5);
        params.setBodyContent(new Gson().toJson(around));
        params.addHeader("Authorization","bearer " + new DbConfig(getActivity()).getUser().getToken());
        Log.e(TAG, "postAround5Km: params" +  params);
        Log.e(TAG, "postAround5Km: toJson" +  new Gson().toJson(around));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "postAround5Km: " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if(code != 200){
                        Toast.makeText(getActivity(), "获取周边数据失败,请稍候重试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONArray data = jsonObject.getJSONArray("data");
                    aroundObj = (JSONObject) data.get(0);
                    JSONArray list1 = aroundObj.getJSONArray("list1");
                    JSONArray list3 = aroundObj.getJSONArray("list3");
                    JSONArray list5 = aroundObj.getJSONArray("list5");
                    JSONArray list10 = aroundObj.getJSONArray("list10");
                    JSONArray list15 = aroundObj.getJSONArray("list15");
                    JSONArray list = new JSONArray();
                    aroundList.clear();
                    if(aroundKm == 1){
                        list = list1;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                        }.getType());
                    }else if(aroundKm == 3){
                        list = list3;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                        }.getType());
                    }else if(aroundKm == 5){
                        list = list5;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                        }.getType());
                    }else if(aroundKm == 10){
                        list = list10;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                        }.getType());
                    }else if(aroundKm == 15){
                        list = list15;
                        aroundList = new Gson().fromJson(String.valueOf(list), new TypeToken<List<com.haohai.platform.mapmodel.multitype.Resource>>() {
                        }.getType());
                    }
                    ();
                    //清除所有资源点
                    clearAllResourceMarker();
                    //添加资源点
                    JSONArray finalList = list;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                try{
                                    //分组添加
                                    JSONArray array_sp = new JSONArray();
                                    JSONArray array_xf = new JSONArray();
                                    JSONArray array_wx = new JSONArray();
                                    JSONArray array_wz = new JSONArray();
                                    JSONArray array_md = new JSONArray();
                                    JSONArray array_sy1 = new JSONArray();
                                    JSONArray array_sy2 = new JSONArray();
                                    JSONArray array_sy3 = new JSONArray();
                                    JSONArray array_hl = new JSONArray();
                                    JSONArray array_sl = new JSONArray();
                                    JSONArray array_zsj = new JSONArray();
                                    for (int n = 0; n < finalList.length(); n++) {
                                        JSONObject o = (JSONObject) finalList.get(n);
                                        allResourcePoints.add(o);
                                        o.put("state","ACTIVE");
                                        String resourceTypes = o.getString("resourceType");
                                        if (resourceTypes.equals("monitor")) {//视频监控点
                                            array_sp.put(o);
                                        }else if (resourceTypes.equals("team")){//消防专业队
                                            array_xf.put(o);
                                        }else if (resourceTypes.equals("dangerSource")){//危险源
                                            array_wx.put(o);
                                        }else if (resourceTypes.equals("foreastRoom")||resourceTypes.equals("materialRepository")){//物资储备库
                                            array_wz.put(o);
                                        }else if (resourceTypes.equals("cemetery")){//墓地
                                            array_md.put(o);
                                        }else if (resourceTypes.contains("waterSource")){//水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                            array_sy1.put(o);
                                        }else if (resourceTypes.contains("watchTower")){//水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                            array_sy2.put(o);
                                        }else if (resourceTypes.contains("fireCommand")){//水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                            array_sy3.put(o);
                                        }else if (resourceTypes.equals("checkStation")){//护林检查站
                                            array_hl.put(o);
                                        }else if (resourceTypes.equals("foreastCenter")){//森林防火监测中心
                                            array_sl.put(o);
                                        }else if (resourceTypes.equals("helicopterPoint")){//直升机升降点
                                            array_zsj.put(o);
                                        }
                                    }
                                    dWebView.callHandler("showPointforresource", new Object[]{"monitor", array_sp, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"team", array_xf, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"dangerSource", array_wx, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"foreastRoom", array_wz, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"cemetery", array_md, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    //水源地 水源地 现有 waterSource 新建 watchTower 改造 fireCommand
                                    dWebView.callHandler("showPointforresource", new Object[]{"waterSource", array_sy1, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"watchTower", array_sy2, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"fireCommand", array_sy3, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"checkStation", array_hl, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"foreastCenter", array_sl, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                    dWebView.callHandler("showPointforresource", new Object[]{"helicopterPoint", array_zsj, ""}, new OnReturnValue<String>() {
                                        @Override
                                        public void onValue(String retValue) {
                                            Log.d("jsbridge", "call succeed,return value is " + retValue);
                                        }
                                    });
                                }catch (Exception e){
                                    Log.e(TAG, "run: e = " + e.getMessage() );
                                }
                        }
                    },2000);
                    for (int i = 0; i < aroundList.size(); i++) {
                        JSONObject obj = (JSONObject) list.get(i);
                        aroundList.get(i).setObj(obj);
                    }
                    //initAroundData();
                    Log.e("list", list.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "获取周边数据失败,请稍候重试", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAroundItemClickListener(com.haohai.platform.mapmodel.multitype.Resource resource) {
        currentAround = resource;
        aroundDialog.dismiss();
        //飞到精确点上
        initAroundFlyMap();
        //加载周边资源详细数据

        otherresourcenameview.setText(resource.getName());
        otherresoucedizhiview.setText(parseNull(resource.getAddress()));
        if(resource.getAddress()==null){
            otherresoucedizhiview.setVisibility(View.GONE);
        }
        otherresourcejingweiduview.setText(resource.getPosition().getLng()+","+resource.getPosition().getLat());
        RxViewAction.clickNoDouble(iv_guide).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                //导航
                try {
                    double[] startGroup = bdToGaoDe(Double.parseDouble(AppDelegate.latitude),Double.parseDouble(AppDelegate.longitude));
                    double[] endGroup = new double[0];
                    endGroup = LatLngChangeNew.calWGS84toGCJ02(resource.getPosition().getLat(),resource.getPosition().getLng());
                    Poi start = new Poi("我的位置", new com.amap.api.maps.model.LatLng(startGroup[0], startGroup[1]), "");
                    Poi end = new Poi(resource.getName(), new com.amap.api.maps.model.LatLng(endGroup[0], endGroup[1]), "");
                    AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
                    params.setUseInnerVoice(true);
                    AmapNaviPage.getInstance().showRouteActivity(getActivity(), params, MapFragment.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        RxViewAction.clickNoDouble(res_edit).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                editRes(resource.getResourceType(),resource.getId(),resource.obj);
            }
        });
        RxViewAction.clickNoDouble(res_delete).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                deleteRes(resource.getResourceType(),resource.getId());
            }
        });
        otherresourceinfoDialog.show();
    }

    public String parseNull(String address) {
        String str = "";
        if(address==null || address.equals("null") || address.isEmpty()){
            str = "暂无详细地址";
        }else{
            str = address;
        }
        return str;
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

    /**
     *  一体机火点详情
     */
    private void initOneBodyFireModelData() {
        mingchengView.setText(currentOneBodyFire.getName());
        dizhiView.setText(parseNull(currentOneBodyFire.getAddress()));
        shijianView.setText(currentOneBodyFire.getAlarmDatetime().replace("T"," ").substring(0,currentOneBodyFire.getAlarmDatetime().indexOf(".")));
        jingweiduView.setText(currentOneBodyFire.getAlarmLongitude() +"、" + currentOneBodyFire.getAlarmLatitude());
        Log.e(TAG, "getPicPath1: "+currentOneBodyFire.getPicPath1() );
        Glide.with(getContext()).load(currentOneBodyFire.getPicPath1())
                .error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_jaizai).into(yitijiOneView);
        Glide.with(getContext()).load(currentOneBodyFire.getPicPath2())
                .error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_jaizai).into(yitijiTwoView);
    /*    Glide.with(getContext()).load("http://10.135.49.201:81/snap/a54dbc0a-eb21-0ab6-8de5-55acda985571/938a02f2-b7fa-472e-8ddb-eeeaeb9604ae_2.jpg")
                .error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_jaizai).into(yitijiOneView);
        Glide.with(getContext()).load("http://10.135.49.201:81/snap/a54dbc0a-eb21-0ab6-8de5-55acda985571/938a02f2-b7fa-472e-8ddb-eeeaeb9604ae_2.jpg")
                .error(R.drawable.ic_no_pic)
                .placeholder(R.drawable.ic_jaizai).into(yitijiTwoView);*/

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
    /**
     *  资源点详情
     */
    private void initresourceFireModelData() {
        mingchengView.setText(currentOneBodyFire.getName());
        dizhiView.setText(parseNull(currentOneBodyFire.getAddress()));
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
     * 周边飞到地图上
     */
    private void initAroundFlyMap() {
        Log.e(TAG, "initAroundFlyMap: currentAround.toString() = " + currentAround.toString() );
        final JSONObject jsonObject = new JSONObject();
        JSONObject posObj = new JSONObject();
        try {
            posObj.put("lat", currentAround.getPosition().getLat());
            posObj.put("lng", currentAround.getPosition().getLng());
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

    private List<JSONObject> allResourcePoints = new ArrayList<>();
    private JSONObject resourceObj = new JSONObject();
    /**
     * 资源列表的电机回调
     * @param resourceList
     */
    @Override
    public void onResourceListItemClickListener(ResourceList resourceList) {

        int checkNumber = 0;
        for (int i = 0; i < resourceListList.size(); i++) {
          if(resourceListList.get(i).isCheck()){
              checkNumber++;
          }
        }
        if(checkNumber == 0){
            //清除所有资源点
            clearAllResourceMarker();
        }
        for (int i = 0; i < resourceListList.size(); i++) {
            if (resourceListList.get(i).getId().equals(resourceList.getId())) {
                Log.e(TAG, "onResourceListItemClickListener: "+resourceList.isCheck());
                if (resourceList.isCheck()){    //隐藏资源点
                    resourceListList.get(i).setCheck(false);
                    Log.e(TAG, "onResourceListItemClickListener: "+resourceListList.get(i).getName());
                    if (resourceListList.get(i).getName().equals("视频监控点")) {
                        resorcetype = "monitor";
                    }else if (resourceListList.get(i).getName().equals("直升机机降点")) {
                        resorcetype = "helicopterPoint";
                    }else if (resourceListList.get(i).getName().equals("消防专业队")){
                        resorcetype = "team";
                    }else if (resourceListList.get(i).getName().equals("队伍驻防点")){
                        resorcetype = "team";
                    }else if (resourceListList.get(i).getName().equals("危险源")){
                        resorcetype = "dangerSource";
                    }else if (resourceListList.get(i).getName().equals("物资储备库")){
                        resorcetype = "foreastRoom";
                    }else if (resourceListList.get(i).getName().equals("新建水源地")){
                        resorcetype = "watchTower";
                    }else if (resourceListList.get(i).getName().equals("改造水源地")){
                        resorcetype = "fireCommand";
                    }else if (resourceListList.get(i).getName().equals("规划建设水源地")){
                        resorcetype = "watchTower";
                    }else if (resourceListList.get(i).getName().equals("现有水源地")){
                        resorcetype = "waterSource";
                    }else if (resourceListList.get(i).getName().equals("墓地")){
                        resorcetype = "cemetery";
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
        //resourceListDialog.dismiss();

    }

    /**
     * 获取资源数据
     * @param ApiUrl
     * @param nameresult
     */
    private void getResourceDataFromService(String ApiUrl,String nameresult) {
        Log.e(TAG, "getResourceDataFromService: "+nameresult );
        String resorcetypeing = "";
        showDialogProgress(progressDialog,"加载中...");
        if (nameresult.equals("视频监控点")) {
            resorcetypeing = "monitor";
        }else if (nameresult.equals("直升机机降点")){
            resorcetypeing = "helicopterPoint";
        }else if (nameresult.equals("消防专业队")){
            resorcetypeing = "team";
        }else if (nameresult.equals("队伍驻防点")){
            resorcetypeing = "team";
        }else if (nameresult.equals("危险源")){
            resorcetypeing = "dangerSource";
        }else if (nameresult.equals("物资储备库")){
            resorcetypeing = "foreastRoom";
        }else if (nameresult.equals("墓地")){
            resorcetypeing = "cemetery";
        }else if (nameresult.equals("新建水源地")){
            resorcetypeing = "watchTower";
        }else if (nameresult.equals("改造水源地")){
            resorcetypeing = "fireCommand";
        }else if (nameresult.equals("现有水源地")){
            resorcetypeing = "waterSource";
        }else if (nameresult.equals("规划建设水源地")){
            resorcetypeing = "watchTower";
        }else if (nameresult.equals("护林检查站")){
            resorcetypeing = "checkStation";
        }else if (nameresult.equals("森林防火监测中心")){
            resorcetypeing = "foreastCenter";
        }
        JSONObject resorcelistobj =new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource"+ApiUrl+"/list");
        params.setAsJsonContent(true);
        params.setBodyContent(resorcelistobj.toString());
        Log.e(TAG, "getResourceList: " + resorcelistobj.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "resource: --"  + params);
        String finalResorcetypeing = resorcetypeing;
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getResourceonSuccess: --1-" + result );
                try {
                    JSONObject json0bject = new JSONObject(result);
                    Log.e("result", json0bject.toString());
                    JSONArray data = json0bject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject o = (JSONObject) data.get(i);
                        allResourcePoints.add(o);
                    }
                    Log.e("data1", data.toString());
                    dWebView.callHandler("showPointforresource", new Object[]{finalResorcetypeing, data, ""}, new OnReturnValue<String>() {
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            progressDialog.dismiss();
                        }catch(Exception e){
                        }
                    }
                },1000);
            }
        });
    }

    /**
     * 获取资源数据 - 网格
     */
    private void getResourceDataFromGrid(String apiUrl,String gridId,String gridNo,String resourceName) {
        showDialogProgress(progressDialog,"加载中...");
        String type = "";
        if (resourceName.equals("视频监控点")) {
            type = "monitor";
        }else if (resourceName.equals("直升机机降点")){
            type = "helicopterPoint";
        }else if (resourceName.equals("消防专业队")){
            type = "team";
        }else if (resourceName.equals("队伍驻防点")){
            type = "team";
        }else if (resourceName.equals("危险源")){
            type = "dangerSource";
        }else if (resourceName.equals("物资储备库")){
            type = "foreastRoom";
        }else if (resourceName.equals("墓地")){
            type = "cemetery";
        }else if (resourceName.equals("新建水源地")){
            type = "watchTower";
        }else if (resourceName.equals("改造水源地")){
            type = "fireCommand";
        }else if (resourceName.equals("现有水源地")){
            type = "waterSource";
        }else if (resourceName.equals("规划建设水源地")){
            type = "watchTower";
        }else if (resourceName.equals("护林检查站")){
            type = "checkStation";
        }else if (resourceName.equals("森林防火监测中心")){
            type = "foreastCenter";
        }
        JSONObject resorcelistobj =new JSONObject();
        try {
            resorcelistobj.put("gridNo",gridNo);
            //resorcelistobj.put("gridId",gridId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource"+apiUrl+"/list");
        params.setAsJsonContent(true);
        params.setBodyContent(resorcelistobj.toString());
        Log.e(TAG, "getResourceList: " + resorcelistobj.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
        Log.e(TAG, "resource: --"  + params);
        String finalType = type;
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getResource Grid Success: " + resourceName + finalType + result );
                try {
                    JSONObject json0bject = new JSONObject(result);
                    Log.e("result", json0bject.toString());
                    JSONArray data = json0bject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject o = (JSONObject) data.get(i);
                        allResourcePoints.add(o);
                    }
                    Log.e("data1", data.toString());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dWebView.callHandler("showPointforresource", new Object[]{finalType, data, ""}, new OnReturnValue<String>() {
                                @Override
                                public void onValue(String retValue) {
                                    Log.d("jsbridge", "call succeed,return value is " + retValue);
                                }
                            });
                        }
                    },500);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            progressDialog.dismiss();
                        }catch (Exception e){

                        }
                    }
                },2000);
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
                                resoucedizhiview.setText(parseNull(dataListsObj.getString("streetName")));
                            }else {
                                resoucedizhiview.setText(parseNull(dataListsObj.getString("districtName")+dataListsObj.getString("streetName")));
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