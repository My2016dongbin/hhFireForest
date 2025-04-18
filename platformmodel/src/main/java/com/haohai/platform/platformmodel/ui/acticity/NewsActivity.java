package com.haohai.platform.platformmodel.ui.acticity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.Multitype.Empty;
import com.haohai.platform.platformmodel.ui.Multitype.EmptyViewBinder;
import com.haohai.platform.platformmodel.ui.Multitype.NewsViewBinder;
import com.haohai.platform.platformmodel.ui.acticity.base.HhBaseActivity;
import com.haohai.platform.platformmodel.ui.model.News;
import com.haohai.platform.platformmodel.ui.utils.OnLoadMoreListener;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

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
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class NewsActivity extends HhBaseActivity implements NewsViewBinder.OnNewsItemClick {
    TextView tv_title;
    ImageView backButton;
    ImageView add_news;
    SwipeRefreshLayout sw_news;
    RecyclerView rlv_news;
    EditText et_search;
    private ProgressDialog progressDialog;
    private int currentPage = 1;
    private List<News> newsList = new ArrayList<>();
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initView();
        getDataFromService();
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        tv_title = findViewById(R.id.tv_title);
        et_search = findViewById(R.id.et_search);
        backButton = findViewById(R.id.back_button);
        add_news = findViewById(R.id.add_news);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if(!CommonUtils.hasPermission(this,"app-application-btn-news-add")){
            add_news.setVisibility(View.GONE);
        }
        add_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(NewsActivity.this,AddNewsActivity.class));
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return true;
            }
        });
        sw_news = findViewById(R.id.sw_news);
        rlv_news = findViewById(R.id.rlv_news);
        rlv_news.setOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

            }
        });
        adapter = new MultiTypeAdapter(items);
        NewsViewBinder binder = new NewsViewBinder();
        binder.setListener(this,this);
        adapter.register(News.class,binder);
        adapter.register(Empty.class,new EmptyViewBinder());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rlv_news.setLayoutManager(linearLayoutManager);
        rlv_news.setAdapter(adapter);
        assertHasTheSameAdapter(rlv_news, adapter);
        //下拉刷新
        sw_news.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsList.clear();
                currentPage = 1;
                getDataFromService();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sw_news.setRefreshing(false);
                    }
                },1000);
            }
        });
    }

    private void search() {
        closeKeybord(NewsActivity.this);
        getDataFromService();
    }

    /**
     * 关闭软键盘
     * @param activity
     */
    public static void closeKeybord(Activity activity) {
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void getDataFromService() {
        showDialogProgress(progressDialog,"数据加载中...");

        JSONObject object = new JSONObject();
        try {
            object.put("name",et_search.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/journalism/list");
        params.addHeader("Authorization","bearer " + new DbConfig(NewsActivity.this).getUser().getToken());
        params.setBodyContent(object.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: getNews" + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    newsList.clear();
                    newsList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<News>>() {
                    }.getType());

                    initData();
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


    private void initData() {
        items.clear();

        if (newsList.size()==0) {
            items.add(new Empty());
        }else {
            items.addAll(newsList);
        }

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromService();
    }

    @Override
    public void OnNewsItemClickListener(News news) {
        Intent intent = new Intent(this,NewsDetailActivity.class);
        intent.putExtra("id",news.getId());
        intent.putExtra("name",news.getName());
        intent.putExtra("describes",news.getDescribes());
        intent.putExtra("url",news.getUrl());
        intent.putExtra("createTime",news.getCreateTime());
        startActivity(intent);
    }
}