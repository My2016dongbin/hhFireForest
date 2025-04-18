package com.haohai.platform.firelibrary.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.activity.copy.ResourceCheckActivity;
import com.haohai.platform.firelibrary.ui.activity.copy.ResourceshenheActivity;
import com.haohai.platform.firelibrary.ui.activity.copy.ResourcezhengzhiActivity;
import com.haohai.platform.firelibrary.ui.multitype.ProtectStationList;
import com.haohai.platform.firelibrary.ui.multitype.ProtectStationListBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.Area;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Grid;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity.ORDER_CHANGE;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

@Route(path = RouteUtils.ProtectStation)
public class ProtectStationActivity extends HhBaseActivity implements ProtectStationListBinder.OnProtectListItemClick{

    private static final String TAG = ProtectStationActivity.class.getSimpleName();
    private ActionBar actionBar;
    private TextView tv_area;
    private TextView tv_date;
    private TextView tv_status;
    private FrameLayout fl_area;
    private FrameLayout fl_date;
    private FrameLayout fl_status;
    private RecyclerView listView;
    private List<ProtectStationList> protectStationLists = new ArrayList<>();
    private final List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private final boolean isShowDialog = true;
    private final boolean isShuaxin = false;
    private final boolean isSearch = false;
    private String title = "";

    @Autowired
    String token;
    @Autowired
    int checkType;
    @Autowired
    String resourceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect_station);
        ARouter.getInstance().inject(this);

        progressDialog = new ProgressDialog(this);

        if(Objects.equals(resourceType, "checkStation")){
            title = "护林检查站列表";
            checkType = 3;
        }
        if(Objects.equals(resourceType, "team")){
            title = "防火检查站列表";
            checkType = 4;
        }
        if(Objects.equals(resourceType, "materialRepository")){
            title = "物资检查站列表";
            checkType = 5;
        }
        if(Objects.equals(resourceType, "other")){
            title = "其他资源点列表";
        }

        initView();
        getDataFromService();
    }


    private void getDataFromService() {
        protectStationLists.clear();
        items.clear();
        adapter.notifyDataSetChanged();
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectBody = new JSONObject();
        try {
            /*checkType =类型  3:护林检查站检查 4:物资库检查 5:防火队伍检查 6:隐患排查 当前状态 status = 1 待检测*/
            jsonObject.put("resourceType",resourceType);
            if(status != -1){
                jsonObject.put("status",status);
            }
            jsonObject.put("startTime", date);
            Calendar calendar = Calendar.getInstance();
            String endTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(calendar.getTime());
            jsonObject.put("endTime", date.equals("")?"":endTime );
            jsonObject.put("gridName",currentJiedao.contains("请选择")?"":currentJiedao);
            jsonObject.put("gridNo",currentJiedaoId);
//            jsonObject.put("parentGridName",currentQu.contains("请选择")?"":currentQu);
//            jsonObject.put("parentGridNo",currentQuId);

            jsonObjectBody.put("dto",jsonObject);
            jsonObjectBody.put("limit",500);
            jsonObjectBody.put("page",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResource/queryResource");
        params.setBodyContent(jsonObjectBody.toString());

        Log.e(TAG, "getDataFromService: params = " + params);
        Log.e(TAG, "getDataFromService: jsonObjectBody = " + jsonObjectBody.toString());
        params.addHeader("Authorization", "bearer " + token);

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: ProtectStationList" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        JSONArray array = object.getJSONArray("data");
                        JSONObject out = (JSONObject) array.get(0);
                        JSONArray list = out.getJSONArray("dataList");
                        protectStationLists.clear();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject o = (JSONObject) list.get(i);
                            Log.e(TAG, "onSuccess: o = " + o);
                            ProtectStationList model = new ProtectStationList();
                            model.setTitle(o.getString("name"));
                            model.setResourceType(o.getString("resourceType"));
                            model.setArea(o.getString("gridName"));
                            model.setGridName(o.getString("gridName"));
                            model.setCheckType(checkType+"");
                            model.setCheckTypeNet(o.getString("checkType"));
                            model.setGridNo(o.getString("gridNo"));
                            model.setGroupId(o.getString("groupId"));
                            model.setEndTime(o.getString("endTime"));
                            model.setApiUrl("/api/checkStation");
                            model.setStatus(o.getInt("status"));
                            model.setLongitude(o.getString("longitude"));
                            model.setLatitude(o.getString("latitude"));
                            model.setUserType(o.getInt("userType"));
                            model.setCheckDate(o.getString("startTime"));
                            model.setEndDate(o.getString("endTime"));
                            model.setRemark(o.getString("description"));
                            model.setId(o.getString("id"));
                            model.setResourceId(o.getString("resourceId"));
                            JSONArray checkusers = o.getJSONArray("checkusers");
                            if(checkusers!=null && checkusers.length()!=0){
                                JSONObject users = (JSONObject) checkusers.get(0);
                                model.setWorker(users.getString("userName"));
                            }else{
                                model.setWorker("");
                            }
                            protectStationLists.add(model);
                        }

                        upDataData();
                    }else{
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

    private List<Area> allAreaList = new ArrayList<>();
    private List<Grid> jiedaoList = new ArrayList<>();
    private List<Grid> quList = new ArrayList<>();
    private List<String> quStrList = new ArrayList<>();
    private List<String> jiedaoStrList = new ArrayList<>();
    private String currentQuId = "";
    private String currentQu = "请选择区";
    private String currentJiedaoId = "";
    private String currentJiedao = "请选择街道";
    private void getAreaFromService() {
        JSONObject jsonObject = new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/sysArea/getAllSysArea");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        Log.i(TAG, "getAreaFromService: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:diqu -- " + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    allAreaList.clear();
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

    /**
     * 获取所有街道数据
     */
    private void getAllJieDao() {
        jiedaoList.clear();
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            jiedaoList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .where("level", "=", "1")
                    .where("parentid", "=", currentQuId)
                    .findAll();
            if(jiedaoList == null){
                jiedaoList = new ArrayList<>();
            }

            jiedaoStrList.clear();
            jiedaoStrList.add("请选择街道");
            for (int i = 0; i < jiedaoList.size(); i++) {
                jiedaoStrList.add(jiedaoList.get(i).getName());
            }
            showGridDialog(jiedaoStrList,false);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有区的数据
     */
    private void getAllQu() {
        quList.clear();
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            quList = db.selector(Grid.class)
                    .where("state", "=", "ACTIVE")
                    .and("level", "=", "3")
                    .and("groupid", "like", "001003%")
                    .findAll();
            if(quList == null){
                quList = new ArrayList<>();
            }
            quStrList.clear();
            Log.e(TAG, "getAllQu: "+quList.size() );
            quStrList.add("请选择区");
            for (int i = 0; i < quList.size(); i++) {
                if (!quList.get(i).getName().equals("高新区")){
                    quStrList.add(quList.get(i).getName());
                }
            }
            showGridDialog(quStrList,true);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        actionBar = findViewById(R.id.action_bar);
        tv_area = findViewById(R.id.tv_area);
        tv_date = findViewById(R.id.tv_date);
        tv_status = findViewById(R.id.tv_status);
        fl_area = findViewById(R.id.fl_area);
        fl_date = findViewById(R.id.fl_date);
        fl_status = findViewById(R.id.fl_status);
        actionBar.setTitle(title);
        tv_area.setText(currentQu+"/"+currentJiedao);
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

        RxViewAction.clickNoDouble(fl_area).subscribe(unused -> {
            getAllQu();
        });
        RxViewAction.clickNoDouble(fl_date).subscribe(unused -> {
            showDateDialog();
        });
        RxViewAction.clickNoDouble(fl_status).subscribe(unused -> {
            showStatusDialog();
        });

    }

    private void register() {
        ProtectStationListBinder protectStationListBinder = new ProtectStationListBinder();
        protectStationListBinder.setListener(this);
        adapter.register(ProtectStationList.class, protectStationListBinder);
    }

    private void upDataData() {

        /*if(protectStationLists.size() == 0){//TODO 测试
            ProtectStationList model = new ProtectStationList();
            model.setTitle("西石岭检查站1");
            model.setArea("兰山区/银雀山街道");
            model.setStatus(3);
            model.setCheckDate("2021-07-29T12:12:00");
            model.setEndDate("2021-07-29T12:12:00");
            model.setWorker("文香");
            model.setRemark("此处是备注此处是备注此处是备注此处是备注此处是备注此处是备注");
            model.setId("213131");
            protectStationLists.add(model);
            ProtectStationList model2 = new ProtectStationList();
            model2.setTitle("西石岭检查站2");
            model2.setArea("兰山区/银雀山街道");
            model2.setStatus(4);
            model2.setCheckDate("2021-07-29T12:12:00");
            model2.setEndDate("2021-07-29T12:12:00");
            model2.setWorker("文香");
            model2.setRemark("此处是备注此处是备注此处是备注此处是备注此处是备注此处是备注");
            model2.setId("1231312321");
            protectStationLists.add(model2);
        }*/

        items.clear();
        for (int i = 0; i < protectStationLists.size(); i++) {
            items.add(protectStationLists.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    /**
     * itemBinder 条目点击回调
     */
    @Override
    public void onProtectListItemClickListener(ProtectStationList protectStationList) {

    }

    /**
     * itemBinder 条目检查回调
     */
    @Override
    public void onCheckClickListener(ProtectStationList resource) {

        Intent intent = new Intent(getApplicationContext(), ResourceCheckActivity.class);
        intent.putExtra("resourceID", resource.getResourceId());
        intent.putExtra("id", resource.getId());
        intent.putExtra("resourceName", resource.getTitle());
        intent.putExtra("checkType", Integer.parseInt(resource.getCheckType()));
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
    public void onZhengzhiClickListener(ProtectStationList resource) {
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
        intent.putExtra("checkType", Integer.parseInt(resource.getCheckType()));
        startActivityForResult(intent, ORDER_CHANGE);
    }
    /**
     * itemBinder 条目审核回调
     */
    @Override
    public void onShenheClickListener(ProtectStationList resource) {
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
        intent.putExtra("checkType", Integer.parseInt(resource.getCheckType()));
        startActivityForResult(intent, ORDER_CHANGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ORDER_CHANGE) {
            Log.e(TAG, "onActivityResult: " );
            //刷新数据
            getDataFromService();
        }
    }

    private void showGridDialog(List<String> gridList, boolean isQu) {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.bottom_list, null);
        LinearLayout ll_list = view.findViewById(R.id.ll_list);
        for (int i = 0; i < gridList.size(); i++) {
            String str = gridList.get(i);
            View item = View.inflate(this, R.layout.bottom_list_item, null);
            TextView tv_title = item.findViewById(R.id.tv_title);
            tv_title.setText(str);
            RxViewAction.clickNoDouble(tv_title).subscribe(unused -> {
                if(isQu){
                    currentQu = str;
                    tv_area.setText(str);
                    for (int m = 0; m < quList.size(); m++) {
                        if(quList.get(m).getName().equals(currentQu)){
                            currentQuId = quList.get(m).getId();
                            break;
                        }
                    }
                    getAllJieDao();
                }else{
                    currentJiedao = str;
                    tv_area.setText(currentQu+"/"+currentJiedao);
                    for (int m = 0; m < jiedaoList.size(); m++) {
                        if(quList.get(m).getName().equals(currentQu)){
                            currentJiedaoId = jiedaoList.get(m).getId();
                            break;
                        }
                    }
                }
                dialog.dismiss();
                getDataFromService();
            });
            ll_list.addView(item);
        }

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.AppTheme);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        dialog.show();

    }
    private String date = "";
    private void showDateDialog() {
        List<String> list = new ArrayList<>();
        list.add("近一周");
        list.add("近一个月");
        list.add("近三个月");
        list.add("近半年");
        list.add("近一年");
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.bottom_list, null);
        LinearLayout ll_list = view.findViewById(R.id.ll_list);
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            View item = View.inflate(this, R.layout.bottom_list_item, null);
            TextView tv_title = item.findViewById(R.id.tv_title);
            tv_title.setText(str);
            RxViewAction.clickNoDouble(tv_title).subscribe(unused -> {
                tv_date.setText(str);
                Calendar calendar = Calendar.getInstance();
                if(Objects.equals(str, "近一周")){
                    calendar.add(Calendar.DAY_OF_MONTH,-7);
                }
                if(Objects.equals(str, "近一个月")){
                    calendar.add(Calendar.MONTH,-1);
                }
                if(Objects.equals(str, "近三个月")){
                    calendar.add(Calendar.MONTH,-3);
                }
                if(Objects.equals(str, "近半年")){
                    calendar.add(Calendar.MONTH,-6);
                }
                if(Objects.equals(str, "近一年")){
                    calendar.add(Calendar.MONTH,-12);
                }
                date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(calendar.getTime());
                dialog.dismiss();
                getDataFromService();
            });
            ll_list.addView(item);
        }

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.AppTheme);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }
    private int status = -1;
    private void showStatusDialog() {
        List<String> list = new ArrayList<>();
        list.add("待检查");
        list.add("待审核");
        list.add("待整治");
        list.add("已通过");
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.bottom_list, null);
        LinearLayout ll_list = view.findViewById(R.id.ll_list);
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            View item = View.inflate(this, R.layout.bottom_list_item, null);
            TextView tv_title = item.findViewById(R.id.tv_title);
            tv_title.setText(str);
            RxViewAction.clickNoDouble(tv_title).subscribe(unused -> {
                tv_status.setText(str);
                if(Objects.equals(str, "待检查")){
                    status = 1;
                }
                if(Objects.equals(str, "待审核")){
                    status = 2;
                }
                if(Objects.equals(str, "待整治")){
                    status = 3;
                }
                if(Objects.equals(str, "已通过")){
                    status = 4;
                }
                dialog.dismiss();
                getDataFromService();
            });
            ll_list.addView(item);
        }

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.AppTheme);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }
}