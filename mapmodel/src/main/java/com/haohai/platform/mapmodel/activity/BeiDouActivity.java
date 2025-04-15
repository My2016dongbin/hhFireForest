package com.haohai.platform.mapmodel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.multitype.Emptys;
import com.haohai.platform.firelibrary.ui.multitype.EmptysViewBinder;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.multitype.BeidouPersonBinder;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.haohai.platform.mapmodel.multitype.EmptyViewBinder;
import com.ruyiruyi.rylibrary.bus.BeidouRefresh;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.cell.DYLoadingView;
import com.ruyiruyi.rylibrary.db.BeidouNews;
import com.ruyiruyi.rylibrary.db.BeidouPerson;
import com.ruyiruyi.rylibrary.db.DbConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class BeiDouActivity extends HhBaseActivity implements BeidouPersonBinder.OnBeiDouItemClickListener {
    private ActionBar actionBar;
    DYLoadingView dy3;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;

    List<BeidouPerson> beiDouPersonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bei_dou);
        actionBar = findViewById(R.id.my_action);
        actionBar.setTitle("北斗通讯");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){
            @Override
            public void onItemClick(int var1) {
                switch ((var1)){
                    case -1:
                        onBackPressed();
                        break;
                }
            }
        });
        EventBus.getDefault().register(this);
        initView();
        initData();
    }
    private void initData() {
        beiDouPersonList = new ArrayList<>();

        DbConfig dbConfig = new DbConfig(BeiDouActivity.this);
        //模拟读取
        showDY3();
        beiDouPersonList = dbConfig.getBeiDouPersonList();
        update();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideDY3();
            }
        },1000);

    }

    private void initView() {
        dy3 = findViewById(R.id.dy3);


        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = findViewById(R.id.rlv);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //fuckData();
                initData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        BeidouPersonBinder beidouPersonBinder = new BeidouPersonBinder();
        beidouPersonBinder.setListener(this);
        adapter.register(BeidouPerson.class, beidouPersonBinder);
        adapter.register(Emptys.class, new EmptysViewBinder());

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);
    }

    private void fuckData() {
        //模拟存储（模拟数据）
        DbConfig dbConfig = new DbConfig(BeiDouActivity.this);
        DbManager db = dbConfig.getDbManager();
        ///清除所有数据
        try {
            db.delete(BeidouPerson.class);
            db.delete(BeidouNews.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        ///孙建军 - 相互对话、接收未读
        BeidouPerson beidouPerson = new BeidouPerson("001","孙建军","001","吃了吗","2022-03-01 15:36:06",CommonUtil.parseToLong("2022-03-01 15:36:06"),25,23);
        try {
            db.saveOrUpdate(beidouPerson);
        } catch (DbException e) {
            e.printStackTrace();
        }
        BeidouNews news1 = new BeidouNews("-2","孙建军","001","您好，我是卖直播课的，偶尔也卖卖编程书籍，《Java是怎么炼成的》、《Android从入门到放弃》都是我的作品，有兴趣您可以联系我","120°18'8'',36°18'10''","2022-03-01 15:26:06",CommonUtil.parseToLong("2022-03-01 15:26:06"),"",0,1);
        BeidouNews news2 = new BeidouNews("-1","孙建军","001","别说话了，我睡了","120°18'8'',36°18'10''","2022-03-01 15:30:06",CommonUtil.parseToLong("2022-03-01 15:30:06"),"",1,1);
        try {
            db.saveOrUpdate(news1);
            db.saveOrUpdate(news2);
        } catch (DbException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 23; i++) {
            BeidouNews beidouNews = new BeidouNews(i+"","孙建军","001","吃了吗","120°18'8'',36°18'10''","2022-03-01 15:36:"+ new CommonUtil().parseZero(i),CommonUtil.parseToLong("2022-03-01 15:36:"+ new CommonUtil().parseZero(i)),"",0,0);
            try {
                db.saveOrUpdate(beidouNews);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        ///鄢生厚 - 接收未读
        BeidouPerson beidouPerson1 = new BeidouPerson("002","鄢生厚","002","你还好吗","2022-03-01 15:35:06",CommonUtil.parseToLong("2022-03-01 15:35:06"),1,1);
        try {
            db.saveOrUpdate(beidouPerson1);
        } catch (DbException e) {
            e.printStackTrace();
        }
        BeidouNews news3 = new BeidouNews("-3","鄢生厚","002","你还好吗","120°18'8'',36°18'10''","2022-03-01 15:30:06",CommonUtil.parseToLong("2022-03-01 15:30:06"),"",0,0);
        try {
            db.saveOrUpdate(news3);
        } catch (DbException e) {
            e.printStackTrace();
        }

        ///宣彩 - 发送已读
        BeidouPerson beidouPerson2 = new BeidouPerson("003","宣彩","003","别再和我说话了","2022-03-01 15:30:06",CommonUtil.parseToLong("2022-03-01 15:30:06"),1,0);
        try {
            db.saveOrUpdate(beidouPerson2);
        } catch (DbException e) {
            e.printStackTrace();
        }
        BeidouNews news4 = new BeidouNews("-4","宣彩","003","别再和我说话了","120°18'8'',36°18'10''","2022-03-01 15:30:06",CommonUtil.parseToLong("2022-03-01 15:30:06"),"",1,1);
        try {
            db.saveOrUpdate(news4);
        } catch (DbException e) {

        }

        ///邰岚 - 无消息
        BeidouPerson beidouPerson3 = new BeidouPerson("004","邰岚","004","","",0,0,0);
        try {
            db.saveOrUpdate(beidouPerson3);
        } catch (DbException e) {
            e.printStackTrace();
        }

        initData();

    }



    ///刷新通讯录列表
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(BeidouRefresh refresh) {
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void update() {
        items.clear();

        if (beiDouPersonList == null || beiDouPersonList.size() == 0){
            items.add(new Emptys("您还没有对话"));
        }else{
            for (int i = 0; i < beiDouPersonList.size(); i++) {
                items.add(beiDouPersonList.get(i));
            }
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
    public void OnBeiDouItemClick(BeidouPerson beidouPerson) {
        Intent intent = new Intent(BeiDouActivity.this,BeiDouInfoActivity.class);
        intent.putExtra("id",beidouPerson.getId());
        intent.putExtra("name",beidouPerson.getName());
        startActivity(intent);
    }
}
