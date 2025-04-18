package com.haohai.platform.firelibrary.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.google.gson.JsonObject;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.activity.copy.ResourceCheckActivity;
import com.haohai.platform.firelibrary.ui.activity.copy.ResourceshenheActivity;
import com.haohai.platform.firelibrary.ui.activity.copy.ResourcezhengzhiActivity;
import com.haohai.platform.firelibrary.ui.model.LatLng;
import com.haohai.platform.firelibrary.ui.multitype.FireMission;
import com.haohai.platform.firelibrary.ui.multitype.FireMissionViewBinder;
import com.haohai.platform.firelibrary.ui.multitype.HiddenDangerList;
import com.haohai.platform.firelibrary.ui.multitype.HiddenDangerListBinder;
import com.haohai.platform.firelibrary.ui.multitype.ProtectStationList;
import com.ruyiruyi.rylibrary.base.AppDelegate;
import com.ruyiruyi.rylibrary.bus.RefreshModel;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.utils.LatLngChange;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity.ORDER_CHANGE;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

@Route(path = RouteUtils.HiddenDangerrList)
public class HiddenDangerListActivity extends HhBaseActivity implements HiddenDangerListBinder.OnHiddenListItemClick, INaviInfoCallback {
    private static final String TAG = HiddenDangerListActivity.class.getSimpleName();
    private ActionBar actionBar;
    private RecyclerView listView;
    private List<HiddenDangerList> hiddenDangerList = new ArrayList<>();
    private final List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private final boolean isShowDialog = true;
    private final boolean isShuaxin = false;
    private final boolean isSearch = false;

    @Autowired
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_danger_list);
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);

        progressDialog = new ProgressDialog(this);

        initView();
        getDataFromService();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(RefreshModel message) {
        getDataFromService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void getDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObject.put("checkType",6);

            jsonObjectBody.put("dto",jsonObject);
            jsonObjectBody.put("limit",200);
            jsonObjectBody.put("page",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResource/queryResource");
        params.setBodyContent(jsonObjectBody.toString());

        Log.e(TAG, "getDataFromService: params = " + params);
        Log.e(TAG, "getDataFromService: jsonObjectBody = " + jsonObjectBody.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: hiddenDangerList" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        JSONArray array = object.getJSONArray("data");
                        JSONObject out = (JSONObject) array.get(0);
                        JSONArray list = out.getJSONArray("dataList");
                        hiddenDangerList.clear();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject o = (JSONObject) list.get(i);
                            Log.e(TAG, "onSuccess: o = " + o);
                            HiddenDangerList model = new HiddenDangerList();
                            model.setTitle(o.getString("name"));
//                            model.setResourceType(o.getString("resourceType"));
                            model.setArea(o.getString("gridName"));
                            model.setCheckType("6");
                            model.setGridName(o.getString("gridName"));
                            model.setGridNo(o.getString("gridNo"));
                            model.setEndTime(o.getString("endTime"));
                            model.setGroupId(o.getString("groupId"));
                            model.setApiUrl("/api/checkStation");
                            model.setStatus(o.getInt("status"));
                            model.setLongitude(o.getString("longitude"));
                            model.setLatitude(o.getString("latitude"));
                            model.setUserType(o.getInt("userType"));
                            model.setCheckDate(o.getString("createTime"));
                            model.setPlayDate(o.getString("regulationTime"));//bingo
                            model.setWorker(o.getString("createUser")==null?"":o.getString("createUser"));
                            model.setResult(o.getString("description"));
                            model.setId(o.getString("id"));
                            model.setResourceId(o.getString("resourceId"));
                            JSONArray checkusers = o.getJSONArray("checkusers");
                            /*if(checkusers!=null && checkusers.length()!=0){
                                JSONObject users = (JSONObject) checkusers.get(0);
                                model.setWorker(users.getString("userName"));
                            }else{
                                model.setWorker("");
                            }*/
                            hiddenDangerList.add(model);
                        }

                        upDataData();
                    }else{//TODO 测试
                        upDataData();
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

    private void initView() {
        actionBar = findViewById(R.id.action_bar);
        actionBar.setTitle("日常检查列表");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch ((var1)) {
                    case -1:
                        onBackPressed();
                        break;
                    case -2:
                        break;
                }
            }
        });

        swipeRefreshLayout = findViewById(R.id.hidden_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = findViewById(R.id.hidden_listview);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getDataFromService();
            swipeRefreshLayout.setRefreshing(false);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        register();

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

    }

    private void register() {
        HiddenDangerListBinder hiddenDangerListBinder = new HiddenDangerListBinder();
        hiddenDangerListBinder.setListener(this);
        adapter.register(HiddenDangerList.class, hiddenDangerListBinder);
    }

    private void upDataData() {

        /*if(hiddenDangerList.size() == 0){//TODO 测试
            HiddenDangerList model = new HiddenDangerList();
            model.setTitle("西石岭检查站1");
            model.setArea("兰山区/银雀山街道");
            model.setStatus(3);
            model.setId("21321312321");
            model.setLongitude("120.999");
            model.setLatitude("120.666");
            model.setCheckDate("2021-07-29T12:12:00");
            model.setPlayDate("2021-07-29T12:12:00");
            model.setWorker("文香");
            model.setResult("不可抗拒因素");
            hiddenDangerList.add(model);
            HiddenDangerList model2 = new HiddenDangerList();
            model2.setTitle("西石岭检查站2");
            model2.setArea("兰山区/银雀山街道");
            model2.setStatus(4);
            model2.setId("2313213123");
            model2.setLongitude("120.999");
            model2.setLatitude("120.666");
            model2.setCheckDate("2021-07-29T12:12:00");
            model2.setPlayDate("2021-07-29T12:12:00");
            model2.setWorker("文香");
            model2.setResult("不可抗拒因素");
            hiddenDangerList.add(model2);
        }*/

        items.clear();
        for (int i = 0; i < hiddenDangerList.size(); i++) {
            items.add(hiddenDangerList.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    /**
     * itemBinder 条目点击回调
     */
    @Override
    public void onHiddenListItemClickListener(HiddenDangerList hiddenDangerList) {
        Intent intent = new Intent(this,ResourceInfoActivity.class);
        intent.putExtra("id",hiddenDangerList.getId());
        startActivity(intent);
    }

    /**
     * itemBinder 条目检查回调
     */
    @Override
    public void onCheckClickListener(HiddenDangerList resource) {
//        Intent intent = new Intent(this,CheckingActivity.class);
//        intent.putExtra("id",hiddenDangerList.getId());
//        startActivity(intent);

        Intent intent = new Intent(getApplicationContext(), ResourceCheckActivity.class);
        intent.putExtra("resourceID", resource.getResourceId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("checkType", 6);
        intent.putExtra("from", "resourceserch");
        intent.putExtra("APIURL", resource.getApiUrl());
        intent.putExtra("GRID_NO", resource.getGridNo());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("girdno", resource.getGridNo());
        intent.putExtra("longitude", resource.getLongitude());
        intent.putExtra("latitude", resource.getLatitude());
        intent.putExtra("lat", resource.getLatitude());
        intent.putExtra("lng", resource.getLongitude());
        intent.putExtra("groupId",resource.getGroupId());
        startActivity(intent);
    }
    /**
     * itemBinder 条目整治回调
     */
    @Override
    public void onZhengzhiClickListener(HiddenDangerList resource) {
        Intent intent = new Intent(getApplicationContext(), ResourcezhengzhiActivity.class);
        intent.putExtra("resourceID", resource.getId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("endTime", resource.getEndTime());
        intent.putExtra("girdno", resource.getGridNo());
        intent.putExtra("longitude", resource.getLongitude());
        intent.putExtra("latitude", resource.getLatitude());
        intent.putExtra("resourcetype", resource.getCheckType());
        intent.putExtra("checkType", 6);
        startActivityForResult(intent, ORDER_CHANGE);
    }
    /**
     * itemBinder 条目审核回调
     */
    @Override
    public void onShenheClickListener(HiddenDangerList resource) {
        Intent intent = new Intent(getApplicationContext(), ResourceshenheActivity.class);
        intent.putExtra("resourceID", resource.getId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("resourcegird", resource.getGridName());
        intent.putExtra("endTime", resource.getEndTime());
        intent.putExtra("girdno", resource.getGridNo());
        intent.putExtra("longitude", resource.getLongitude());
        intent.putExtra("latitude", resource.getLatitude());
        intent.putExtra("resourcetype", resource.getCheckType());
        intent.putExtra("checkType", 6);
        startActivityForResult(intent, ORDER_CHANGE);
    }
    /**
     * itemBinder 导航按钮点击
     */
    @Override
    public void onGuideClickListener(HiddenDangerList resource) {

       /*  //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
             AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
             //启动导航组件
             AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);*/
        double[] startGroup = bdToGaoDe(Double.parseDouble(AppDelegate.latitude),Double.parseDouble(AppDelegate.longitude));
        double[] doubles = LatLngChangeNew.calWGS84toGCJ02(Double.parseDouble(resource.getLatitude()), Double.parseDouble(resource.getLongitude()));

        Poi start = new Poi("我的位置", new com.amap.api.maps.model.LatLng(startGroup[0], startGroup[1]), "");
        Poi end = new Poi(resource.getTitle(), new com.amap.api.maps.model.LatLng(doubles[0], doubles[1]), "");
        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
        params.setUseInnerVoice(true);
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, HiddenDangerListActivity.this);
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