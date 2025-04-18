package com.haohai.platform.mapmodel.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.multitype.FireMission;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.multitype.CheckhistoryViewBinder;
import com.haohai.platform.mapmodel.multitype.CheckzhengzhiViewBinder;
import com.haohai.platform.mapmodel.multitype.HistoryNo;
import com.haohai.platform.mapmodel.multitype.HistoryNoViewBinder;
import com.haohai.platform.mapmodel.multitype.historylist;
import com.ruyiruyi.rylibrary.db.CheckField;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();
    private List<CheckField> checkFieldList;
    private CheckhistoryViewBinder checkhistoryViewBinder;
    private HistoryNoViewBinder historyNoViewBinder;
    private MultiTypeAdapter adapter;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private Intent intent;
    private String id;
    private List<historylist> historylists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        intent= getIntent();
        id = intent.getStringExtra("resourceId");
        Log.e(TAG, "onCreate: "+id );
        initView();
        initCheckFieldIntoDb();
    }

    private void initView() {
        DbManager db = new DbConfig(getApplicationContext()).getDbManager();
        try {
            checkFieldList= db.selector(CheckField.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        listView = (RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        listView.setNestedScrollingEnabled(false);
        adapter = new MultiTypeAdapter(items);
        register();
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);
        register();
    }
    private void register() {
        checkhistoryViewBinder = new CheckhistoryViewBinder();
        adapter.register(historylist.ChildFirejd.class, checkhistoryViewBinder);
        historyNoViewBinder=new HistoryNoViewBinder();
        adapter.register(HistoryNo.class,historyNoViewBinder);
    }
    private void initCheckFieldData() {
        items.clear();
//        for (int i = 0; i < checkFieldList.size(); i++) {
//            items.add(checkFieldList.get(i));
//        }
        for (int i = 0; i <historylists.size() ; i++) {
            int num =i+1;
            items.add(new HistoryNo("第"+num+"次"));
            Log.e(TAG, "initCheckFieldData: "+historylists.get(i).getChild().size() );
            for (int j = 0; j <historylists.get(i).getChild().size() ; j++) {
                items.add(historylists.get(i).getChild().get(j));
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
    private void initCheckFieldIntoDb() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResourceItemHistory/selectResourceItem");
        params.setConnectTimeout(20000);
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.addParameter("planResourceId",id);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess12------ " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        checkFieldList.clear();
                        Gson gson = new Gson();
                        historylists = gson.fromJson(String.valueOf(data), new TypeToken<List<historylist>>() {
                        }.getType());
                        try {
                            JSONObject object = data.getJSONObject(0);
                            JSONArray datachild = object.getJSONArray("child");
                            for (int j = 0; j <datachild.length() ; j++) {
                                JSONObject datachild1=datachild.getJSONObject(j);
                                String createUser = datachild1.optString("createUser");
                                String updateUser = datachild1.optString("updateUser");
                                String createTime = datachild1.optString("createTime");
                                String updateTime = datachild1.optString("updateTime");
                                String id = datachild1.optString("id");
                                String resourceType = datachild1.optString("resourceType");
                                String code1 = datachild1.optString("code");
                                String name = datachild1.optString("name");
                                String fieldType = datachild1.optString("fieldType");
                                String description = datachild1.optString("description");
                                String groupId = datachild1.optString("groupId");
                                int status = datachild1.optInt("status");
                                CheckField checkField = new CheckField(id, code1, createTime, createUser, description, fieldType, groupId, name, resourceType, updateTime, updateUser,status);
                                checkFieldList.add(checkField);
                            }
                            Log.e(TAG, "onSuccess: "+historylists.size() );
                            if (historylists.size() > 0) {
                                initCheckFieldData();
                            }else {

                            }
                        }catch (Exception e){
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 检查内容请求失败" + ex.toString());
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