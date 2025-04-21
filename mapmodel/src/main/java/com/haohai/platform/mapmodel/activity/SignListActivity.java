package com.haohai.platform.mapmodel.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.bean.SignInfo;
import com.haohai.platform.mapmodel.model.SignMonth;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignListActivity extends HhBaseActivity {
    private ActionBar actionBar;
    private String TAG = SignListActivity.class.getSimpleName();
    private DYLoadingView dy3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_list);
        actionBar = (ActionBar) findViewById(R.id.my_action);
        actionBar.setTitle("考勤记录");
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

        initView();
        bindView();
        postData();
    }

    void showDY3(){
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }
    void hideDY3(){
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    private List<SignMonth> signMonthList = new ArrayList<>();
    private void postData() {
        SignInfo signInfo = new SignInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        signInfo.setTime(formatter.format(curDate));
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "api/attendance/myAttDataByTime");//-2休息  -3旷工
        params.setBodyContent(new Gson().toJson(signInfo));
        params.addHeader("Authorization", "bearer " + new DbConfig(SignListActivity.this).getUser().getToken());
        showDY3();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo info = " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray list = data.getJSONArray("list");
                    signMonthList.clear();
                    for (int i = 0; i < list.length(); i++) {
                        SignMonth signMonth = new Gson().fromJson(list.get(i).toString(), SignMonth.class);
                        signMonthList.add(signMonth);
                    }
                    Log.e(TAG, "onSuccess: bingo signMonthList = " + signMonthList.toString() );

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
                    }
                },1000);
            }
        });
    }

    private void bindView() {

    }

    private void initView() {
        dy3 = findViewById(R.id.dy3);
    }
}
