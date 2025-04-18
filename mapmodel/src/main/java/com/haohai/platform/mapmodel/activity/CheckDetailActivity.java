package com.haohai.platform.mapmodel.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.multitype.CheckDetail;
import com.haohai.platform.mapmodel.multitype.CheckDetailBinder;
import com.haohai.platform.mapmodel.multitype.CheckPlanList;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;

public class CheckDetailActivity extends HhBaseActivity {
    private static final String TAG = CheckDetailActivity.class.getSimpleName();
    private TextView resourceName;
    private TextView resourceGird;
    private TextView startTime;
    private TextView endTime;
    private TextView checkLevel;
    private TextView checkPeople;
    private TextView beizhuEdit;
    private ProgressDialog progressDialog;
    private boolean isShowDialog = true;
    private List<CheckPlanList> checkplanList;
    private Intent intent;
    private String resourceId;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter = new MultiTypeAdapter(items);;
    private RecyclerView listView;
    private List<CheckDetail> checkDetails = new ArrayList<>();
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);
        progressDialog=new ProgressDialog(this);
        checkplanList=new ArrayList<>();
        intent=getIntent();
        resourceId=intent.getStringExtra("id");
        initView();
        getDataFromService();
        getHistoryFromService();
    }

    private void initView() {
        backButton = (ImageView) findViewById(R.id.back_button);
        resourceName=findViewById(R.id.resource_name);
        resourceGird=findViewById(R.id.resource_gird);
        startTime=findViewById(R.id.start_time);
        endTime=findViewById(R.id.end_time);
        checkLevel=findViewById(R.id.check_level);
        checkPeople=findViewById(R.id.check_people);
        beizhuEdit=findViewById(R.id.beizhu_edit);
        CheckDetailBinder checkDetailBinder=new CheckDetailBinder();
        adapter.register(CheckDetail.class,checkDetailBinder);
        listView=findViewById(R.id.check_plan_detail_listview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void getDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan");
        params.addBodyParameter("id",resourceId);
        Log.e(TAG, "getDataFromService: " + params);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject dataobj=data.getJSONObject(0);
                        resourceName.setText(dataobj.getString("name"));
                        resourceGird.setText(dataobj.getString("gridName"));
                        startTime.setText(dataobj.getString("startTime"));
                        endTime.setText(dataobj.getString("endTime"));
                        checkLevel.setText(dataobj.getInt("taskLevel")==1?"市-区":"区-街道");
                        checkPeople.setText(dataobj.getString("checkUserName"));
                        beizhuEdit.setText(dataobj.getString("description"));
                    }else {
                        Toast.makeText(CheckDetailActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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
    private void getHistoryFromService() {
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto=new JSONObject();
            jsonObject.put("dto",dto);
            jsonObject.put("limit",200);
            jsonObject.put("page",1);
            dto.put("planId",resourceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planSchedule/page");
        Log.e(TAG, "getDataFromService: " + params);
        Log.e(TAG, "getDataFromService: " + params);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.setBodyContent(jsonObject.toString());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess2: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Gson gson =new Gson();
                        checkDetails.clear();
                        checkDetails = gson.fromJson(String.valueOf(dataList), new TypeToken<List<CheckDetail>>() {
                        }.getType());
                        initData();
                    }else {
                        Toast.makeText(CheckDetailActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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

    private void initData() {
        items.clear();
        //倒序查询data
        for (int i = checkDetails.size(); i > 0; i--) {
            items.add(checkDetails.get(i-1));
            if (i-1==0){
                checkDetails.get(i-1).isNow=true;
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
}