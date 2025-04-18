package com.haohai.platform.mapmodel.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.activity.CheckDetailActivity;
import com.haohai.platform.mapmodel.activity.ResourceCheckActivity;
import com.haohai.platform.mapmodel.activity.ResourceshenheActivity;
import com.haohai.platform.mapmodel.activity.ResourcezhengzhiActivity;
import com.haohai.platform.mapmodel.bean.MessagePlanFilter;
import com.ruyiruyi.rylibrary.bus.RefreshModel;
import com.haohai.platform.mapmodel.fragment.base.HhBaseFragment;
import com.haohai.platform.mapmodel.listener.OnLoadMoreListener;
import com.haohai.platform.mapmodel.multitype.CheckPlanList;
import com.haohai.platform.mapmodel.multitype.CheckPlanListStationViewBinder;
import com.haohai.platform.mapmodel.multitype.CheckPlanListViewBinder;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.haohai.platform.mapmodel.multitype.EmptyViewBinder;
import com.haohai.platform.platformmodel.ui.Multitype.Bottom;
import com.haohai.platform.platformmodel.ui.Multitype.BottomViewBinder;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;

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
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity.ORDER_CHANGE;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class CheckPlanLeftFg extends HhBaseFragment implements CheckPlanListViewBinder.OnPlanItemClick, CheckPlanListStationViewBinder.OnOneBodyItemClick {
    private static final String TAG = CheckPlanLeftFg.class.getSimpleName();
    private ProgressDialog progressDialog;
    private boolean isShowDialog = true;
    private List<CheckPlanList> checkplanList;
    private List<CheckPlanList> checkplanLists;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private RecyclerView listView;
    private int currentPage = 1;
    private int totalSize;
    private int lastPage=1;
    private int allTotal = 0;
    private int pageSize = 20;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkplan, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        progressDialog = new ProgressDialog(getActivity());
        isShowDialog = true;

        initView();
        getDataFromService("");


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void initView() {
        checkplanList=new ArrayList<>();
        checkplanLists=new ArrayList<>();

        adapter = new MultiTypeAdapter(items);
        listView = getView().findViewById(R.id.check_plan_listview);
        listView.setOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (lastPage > currentPage){
                    currentPage += 1;
                    isShowDialog = false;
                    getDataFromService("");
                }
            }
        });
        register();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.check_plan_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkplanLists.clear();
                isShowDialog = false;
                currentPage = 1;
                getDataFromService("");
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessagePlanFilter message) {
        String jsonStr = message.message.replaceAll("nullType","1");
        getDataFromService(jsonStr);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(RefreshModel message) {
        checkplanList.clear();
        isShowDialog = true;
        getDataFromService("");
    }

    private void getDataFromService(String jsonStr) {
        checkplanLists.clear();
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto=new JSONObject();
            dto.put("type","1");
            jsonObject.put("dto",dto);
            jsonObject.put("limit",pageSize);
            jsonObject.put("page",currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan/selectCheckPlan");
        params.setBodyContent(jsonStr.isEmpty()?jsonObject.toString():jsonStr);
        params.addBodyParameter("isAndroid","1");
        Log.e(TAG, "getDataFromService: jsonObject" + jsonObject.toString());
        Log.e(TAG, "getDataFromService: jsonStr" + jsonStr.toString());
        Log.e(TAG, "getDataFromService: " + params);
        params.addHeader("Authorization", "bearer " + new DbConfig(getActivity()).getUser().getToken());

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        allTotal = getJsonObj.getInt("totalSize");
                        lastPage = allTotal / pageSize;
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Gson gson = new Gson();
                        checkplanList.clear();
                        checkplanList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<CheckPlanList>>() {
                        }.getType());
                        checkplanLists.addAll(checkplanList);
                        Log.e(TAG, "onSuccess: "+checkplanList.get(0).planResourceDTOS.size() );
                    }else {
                        Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();
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
                swipeRefreshLayout.setRefreshing(false);
                initData();
            }
        });
    }
    private void initData() {
        items.clear();
        adapter.notifyDataSetChanged();
        if (checkplanLists.size()==0&&currentPage == 1){
            items.add(new Empty("暂无数据"));
        }else {
            for (int i = 0; i < checkplanLists.size(); i++) {
                items.add(checkplanLists.get(i));
                if (checkplanLists.get(i).getPlanResourceDTOS().size()>0) {
                    for (int j = 0; j < checkplanLists.get(i).getPlanResourceDTOS().size(); j++) {
                        Log.e(TAG, ": "+checkplanLists.get(i).getPlanResourceDTOS().get(j).getStatus() );
                        items.add(checkplanLists.get(i).getPlanResourceDTOS().get(j));
                    }
                }
            }
        }
        if (currentPage > 1){
            if (lastPage > currentPage) {    //显示下拉加载
                items.add(new Bottom("加载更多..."));
            } else {
                items.add(new Bottom("全部加载完成"));
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
    private void register() {
        CheckPlanListViewBinder checkPlanListViewBinder = new CheckPlanListViewBinder();
        checkPlanListViewBinder.setListener(this);
        CheckPlanListStationViewBinder checkPlanListStationViewBinder =new CheckPlanListStationViewBinder();
        checkPlanListStationViewBinder.setListener(this);
        adapter.register(CheckPlanList.class, checkPlanListViewBinder);
        adapter.register(CheckPlanList.PlanResourceDTOSFirejd.class,checkPlanListStationViewBinder);
        adapter.register(Empty.class,new EmptyViewBinder());
        adapter.register(Bottom.class,new BottomViewBinder());
    }

    @Override
    public void onChecklistItemClickListener(CheckPlanList.PlanResourceDTOSFirejd planResourceDTOSFirejd,String type) {
        if (type=="jiancha") {
            Intent intent = new Intent(getActivity(), ResourceCheckActivity.class);
            intent.putExtra("planid", planResourceDTOSFirejd.getPlanId());
            intent.putExtra("resourceID", planResourceDTOSFirejd.getId());
            intent.putExtra("resourceName", planResourceDTOSFirejd.getName());
            intent.putExtra("resourcegird", planResourceDTOSFirejd.getGridName());
            //intent.putExtra("endTime", planResourceDTOSFirejd.getEndTime());
            intent.putExtra("girdno", planResourceDTOSFirejd.getgridNo());
            intent.putExtra("resourcetype", planResourceDTOSFirejd.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);
        }else if (type=="shenhe"){
            Intent intent = new Intent(getActivity(), ResourceshenheActivity.class);
            intent.putExtra("planid", planResourceDTOSFirejd.getPlanId());
            intent.putExtra("resourceID", planResourceDTOSFirejd.getId());
            intent.putExtra("resourceName", planResourceDTOSFirejd.getName());
            intent.putExtra("resourcegird", planResourceDTOSFirejd.getGridName());
            intent.putExtra("endTime", planResourceDTOSFirejd.getEndTime());
            intent.putExtra("girdno", planResourceDTOSFirejd.getgridNo());
            intent.putExtra("resourcetype", planResourceDTOSFirejd.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);
        }else if (type=="zhengzhi"){
            Intent intent = new Intent(getActivity(), ResourcezhengzhiActivity.class);
            intent.putExtra("planid", planResourceDTOSFirejd.getPlanId());
            intent.putExtra("resourceID", planResourceDTOSFirejd.getId());
            intent.putExtra("resourceName", planResourceDTOSFirejd.getName());
            intent.putExtra("resourcegird", planResourceDTOSFirejd.getGridName());
            intent.putExtra("girdno", planResourceDTOSFirejd.getgridNo());
            intent.putExtra("resourcetype", planResourceDTOSFirejd.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);
        }
    }
    @Override
    public void onPlanItemClickListener(String id) {
        Intent intent = new Intent(getActivity(), CheckDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
    @Override
    public void onOrderCheckClickListener(CheckPlanList checkPlanList) {

    }

}
