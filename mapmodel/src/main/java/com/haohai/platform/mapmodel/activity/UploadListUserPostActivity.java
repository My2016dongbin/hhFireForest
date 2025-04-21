package com.haohai.platform.mapmodel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.haohai.platform.firelibrary.ui.activity.Pic1Activity;
import com.haohai.platform.firelibrary.ui.activity.PlayerActivity;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.bean.UploadPostUserPost;
import com.haohai.platform.mapmodel.multitype.UploadPostUserPostViewBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class UploadListUserPostActivity extends HhBaseActivity implements UploadPostUserPostViewBinder.OnPicClick {
    ImageView iv_back;
    ImageView iv_right;
    private DYLoadingView dy3;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list_change);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });
        RxViewAction.clickNoDouble(iv_right).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                startActivity(new Intent(UploadListUserPostActivity.this,UploadUserPostActivity.class));
            }
        });

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        dy3 = findViewById(R.id.dy3);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = (RecyclerView) findViewById(R.id.listview);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        UploadPostUserPostViewBinder binder = new UploadPostUserPostViewBinder();
        binder.setListener(this,this);
        adapter.register(UploadPostUserPost.class,binder);

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

    }

    List<UploadPostUserPost> modelList = new ArrayList<>();
    private void initData() {
        JSONObject object = new JSONObject();
        JSONObject dto = new JSONObject();
        try {
            dto.put("fireName","");
            dto.put("endTime","");
            dto.put("startTime","");
            object.put("dto",dto);
            object.put("limit",200);
            object.put("page",0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_LOGIN + "fire/api/reportFirealarm/page");
        params.setBodyContent(object.toString());
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        Log.e("TAG", "onSuccess: bingo uploadList" + params.toString() );
        Log.e("TAG", "onSuccess: bingo uploadList" + object.toString() );
        showDY3();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("TAG", "onSuccess: bingo uploadList" + result );
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        JSONObject rModel = (JSONObject) data.get(0);
                        JSONArray dataList = rModel.getJSONArray("dataList");
                        Gson gson = new Gson();
                        modelList.clear();
                        for (int i = 0; i < dataList.length(); i++) {
                            modelList.add(gson.fromJson(dataList.get(i).toString(),UploadPostUserPost.class));
                        }
                        updateListView();
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDY3();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },500);
            }
        });
    }

    private void updateListView() {
        items.clear();
        for (int i = 0; i < modelList.size(); i++) {
            items.add(modelList.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }


    void showDY3(){
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }
    void hideDY3(){
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    @Override
    public void OnPicClickListener(String picUrl) {
        Intent intent = new Intent(UploadListUserPostActivity.this, Pic1Activity.class);
        intent.putExtra("pic",picUrl);
        intent.putExtra("from", UploadListUserPostActivity.class);
        startActivity(intent);
    }
    @Override
    public void OnVideoClickListener(String videoUrl) {
        Intent intent = new Intent(UploadListUserPostActivity.this, PlayerActivity.class);
        intent.putExtra("PLAYER_URL",videoUrl);
        intent.putExtra("PLAYER_NAME", "视频详情");
        intent.putExtra("from", UploadListUserPostActivity.class);
        startActivity(intent);
    }
}