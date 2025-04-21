package com.haohai.platform.firelibrary.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.multitype.Ducha;
import com.haohai.platform.firelibrary.ui.multitype.DuchaViewBinder;
import com.haohai.platform.firelibrary.ui.multitype.Empty;
import com.haohai.platform.firelibrary.ui.multitype.EmptyViewBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

@Route(path = RouteUtils.DuchaList)
public class DuchaActivity extends HhBaseActivity implements DuchaViewBinder.OnDuchaItemClick {
    private static final String TAG = DuchaActivity.class.getSimpleName();
    private ActionBar actionBar;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private boolean isShowDialog = true;
    private boolean isShuaxin = false;
    private boolean isSearch = false;
    private  List<Ducha> fireMissionList;
    private  List<Ducha> fireMissionTypeList;
    public static int ORDER_CHANGE = 113;
    @Autowired
    String token;

    private String operatorName;
    private int currentFireListType = 3;  //3"全部，0未开始，1执行中，2已结束")
    private int choose1 = 0;
    private AlertDialog.Builder builder;
    private int currentPage = 1;
    private int totalSize;
    private int lastPage;
    private LinearLayout fireTypeLayout;
    private LinearLayout orderStateLayout;
    private TextView fireTypeView;
    private TextView orderStateView;
    private int shipinChoose1 = 1;
    public int shipinState = 2;  //2：森林防火4：海域监控5：砂石盗采 0全部
    private User user;
    private String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ducha_list);
        ARouter.getInstance().inject(this);
        orderStateView = (TextView) findViewById(R.id.order_state_view);

        operatorName = getIntent().getStringExtra("operatorName");
        filter = getIntent().getStringExtra("filter");
        Log.e(TAG, "onCreate: filter = " + filter );
        if(Objects.equals(filter, "all")){
            choose1 = 0;
            currentFireListType = 3;
            orderStateView.setText("全部");
        }
        if(Objects.equals(filter, "ing")){
            choose1 = 2;
            currentFireListType = 1;
            orderStateView.setText("进行中");
        }
        if(Objects.equals(filter, "end")){
            choose1 = 3;
            currentFireListType = 2;
            orderStateView.setText("已结束");
        }
        if(Objects.equals(filter, "wait")){
            choose1 = 1;
            currentFireListType = 0;
            orderStateView.setText("未开始");
        }

        progressDialog = new ProgressDialog(this);
        fireMissionList = new ArrayList<>();
        fireMissionTypeList = new ArrayList<>();
        user = new DbConfig(this).getUser();
        initView();
        isShowDialog = true;
        getDataFromService();
    }

    private void getDataFromService() {
        /*if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            dto.put("groupId",new DbConfig(this).getUser().getGroupId());
            if (shipinState == 0){
                dto.put("taskType", null);
            }else {
                dto.put("taskType", shipinState);
            }
            if(currentFireListType == 3){
            }else {
                dto.put("status", currentFireListType);
            }

            if(operatorName!=null && !operatorName.isEmpty()){
                dto.put("operatorName", operatorName);
            }

            jsonObject.put("limit", 100);
            jsonObject.put("dto", dto);
            jsonObject.put("page", currentPage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        *//**
         * oa/api/taskManagement/page  {"page":1,"limit":20,"dto":{}} post
         * 分页功能接口
         *//*
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/taskManagement/page");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());

        Log.e(TAG, "getDataFromService: " + params);
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONObject data = jsonObject1.getJSONObject("data");
                    //  JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        JSONArray dataList = data.getJSONArray("dataList");
                        Log.i(TAG, "dataList: "+dataList);
                        totalSize=data.getInt("totalSize");
                        Log.i(TAG, "getonebodyDataonSuccess: "+totalSize);
                        lastPage= (totalSize + 50 -1) / 50;     //计算最大分页数

                        Gson gson = new Gson();
                        *//*choose1 = 0;
                        orderStateView.setText("全部");*//*//TODO
                        fireMissionList.clear();
                        fireMissionTypeList.clear();

                        fireMissionList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<Ducha>>() {
                        }.getType());
                        fireMissionTypeList.addAll(fireMissionList);
                        if (isShuaxin){
                            swipeRefreshLayout.setRefreshing(false);
                        }

//                        parseIntent();
                        fireMissionTypeList.clear();
                        fireMissionTypeList.addAll(fireMissionList);
                        initData();
                    }else {
                        Toast.makeText(DuchaActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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
        });*/

        fireMissionList.clear();
        fireMissionList.add(new Ducha("薛家岛东环岛路","2023-02-16 12:33:55","36.028464","120.298418","山东省青岛市黄岛区薛家岛街道"));
        fireMissionList.add(new Ducha("小珠山台子沟","2023-03-06 16:32:15","35.927858","120.133866","山东省青岛市黄岛区灵山卫街道"));
        fireMissionList.add(new Ducha("辛安上庄检查站(下庄小学)","2023-03-25 09:16:36","35.9818","120.10235","山东省青岛市黄岛区辛安街道"));
        fireMissionList.add(new Ducha("毛家山南","2023-04-08 10:18:22","35.931992","120.121718","山东省青岛市黄岛区灵山卫街道"));
        fireMissionList.add(new Ducha("大溪谷东山","2023-05-16 18:22:36","35.951347","120.052644","山东省青岛市黄岛区灵山卫街道"));
        fireMissionTypeList.clear();
        fireMissionTypeList.addAll(fireMissionList);
        initData();
    }

    private void initData() {

        if(currentPage == 1){
            items.clear();
        }

        if (fireMissionTypeList.size() == 0){
            items.add(new Empty());
        }
        for (int i = 0; i < fireMissionTypeList.size(); i++) {
            items.add(fireMissionTypeList.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    private void parseIntent(){
        //3"全部，0未开始，1执行中，2已结束")
        if (Objects.equals(filter, "all")) {
            currentFireListType = 3;
            orderStateView.setText("全部");
            fireMissionTypeList.clear();
            fireMissionTypeList.addAll(fireMissionList);
            initData();
        } else if (Objects.equals(filter, "wait")){
            currentFireListType = 0;
            orderStateView.setText("未开始");
            fireMissionTypeList.clear();
            for (int j = 0; j < fireMissionList.size(); j++) {
                if (fireMissionList.get(j).getStatus() == 0) {
                    fireMissionTypeList.add(fireMissionList.get(j));
                }
            }
            initData();
        }else if (Objects.equals(filter, "ing")){
            currentFireListType = 1;
            orderStateView.setText("进行中");
            fireMissionTypeList.clear();
            for (int j = 0; j < fireMissionList.size(); j++) {
                if (fireMissionList.get(j).getStatus() == 1) {
                    fireMissionTypeList.add(fireMissionList.get(j));
                }
            }
            initData();
        }else if (Objects.equals(filter, "end")){
            currentFireListType = 2;
            orderStateView.setText("已结束");
            fireMissionTypeList.clear();
            for (int j = 0; j < fireMissionList.size(); j++) {
                if (fireMissionList.get(j).getStatus() == 2) {
                    fireMissionTypeList.add(fireMissionList.get(j));
                }
            }
            initData();
        }else{
            initData();
        }
    }

    private void initView() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("督查督办");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch ((var1)) {
                    case -1:
                        onBackPressed();
                        break;
                    case -3:
                      //  showLeibieChangeDailog();
                        break;
                }
            }
        });

        fireTypeLayout = (LinearLayout) findViewById(R.id.fire_type_layout);
        orderStateLayout = (LinearLayout) findViewById(R.id.order_state_layout);
        fireTypeView = (TextView) findViewById(R.id.fire_type_view);


        fireTypeLayout.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fire_mission_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = (RecyclerView) findViewById(R.id.fire_mission_listview);

        //下拉刷新
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               isShowDialog = false;
                isShuaxin = true;
                swipeRefreshLayout.setRefreshing(false);
                getDataFromService();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        register();

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

    }

    private void register() {
        DuchaViewBinder fireMissionViewBinder = new DuchaViewBinder();
        fireMissionViewBinder.setListener(this);
        adapter.register(Ducha.class, fireMissionViewBinder);
        adapter.register(Empty.class, new EmptyViewBinder());
    }

    /**
     * 单条目点击
     * @param fireMission
     */
    @Override
    public void onDuchaItemClickListener(Ducha fireMission) {
        /*Intent intent = new Intent(getApplicationContext(), FireMissionInfoActivity.class);
        intent.putExtra("ID",fireMission.getId());
        startActivityForResult(intent,ORDER_CHANGE);*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:requestCode " + requestCode);
        Log.e(TAG, "onActivityResult:resultCode " +  resultCode);
        if (resultCode == ORDER_CHANGE) {
            Log.e(TAG, "onActivityResult: 111" );
            fireMissionList.clear();
            isShowDialog = true;
            getDataFromService();
        }
    }
}
