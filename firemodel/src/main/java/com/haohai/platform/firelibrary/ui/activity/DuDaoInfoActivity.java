package com.haohai.platform.firelibrary.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.multitype.DuDao;
import com.haohai.platform.firelibrary.ui.multitype.FireMission;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class DuDaoInfoActivity extends HhBaseActivity {
    private static final String TAG = DuDaoInfoActivity.class.getSimpleName();
    private ActionBar actionBar;
    private SwipeRefreshLayout refresh;
    private TextView text_status;
    private TextView text_upload;
    private TextView title;
    private TextView state;
    private TextView start;
    private TextView end;
    private TextView person;
    private LinearLayout ll_pictures;
    private LinearLayout ll_upload;
    private TextView tv_upload;
    private String id;
    private int status = 0;
    private DuDao duDao;
    private List<DuDao> sceneList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dudao_list_info);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        initView();
        bindView();
    }

    private void bindView() {
        RxViewAction.clickNoDouble(text_status).subscribe(unused -> {
            if(status == 2){
                Toast.makeText(this, "该任务已结束", Toast.LENGTH_SHORT).show();
                return;
            }
            putDdStatus();
        });
        RxViewAction.clickNoDouble(text_upload).subscribe(unused -> {
            Intent intent = new Intent(getApplicationContext(), DuDaoSceneActivity.class);
            intent.putExtra("ID",id);
            startActivity(intent);
        });
    }

    private void putDdStatus() {
        //任务状态，0未开始，1进行中，2已结束(任务最终状态)，3已延期
        int s = 2;
        if(status==0){
            s = 1;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("status",s);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/SupervisorySystem");
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        int finalS = s;
        showDialogProgress(progressDialog,"修改中..");
        x.http().request(HttpMethod.PUT,params,new Callback.CommonCallback<String>(){
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: put " + result );
                if(result!=null && result.contains("200")){
                    status = finalS;
                    refreshData();
                    Toast.makeText(DuDaoInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
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
                hideDialogProgress(progressDialog);
            }
        });
    }

    private void initView() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("督导检查详情");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch ((var1)) {
                    case -1:
                        onBackPressed();
                        break;
                }
            }
        });
        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(false);
            initData();
        });
        text_status = findViewById(R.id.text_status);
        text_upload = findViewById(R.id.text_upload);

        title = findViewById(R.id.title);
        state = findViewById(R.id.status);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        person = findViewById(R.id.person);
        ll_pictures = findViewById(R.id.ll_pictures);
        ll_upload = findViewById(R.id.ll_upload);
        tv_upload = findViewById(R.id.tv_upload);

    }

    private void initData() {
        sceneList = new ArrayList<>();
        showDialogProgress(progressDialog,"加载中..");
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/SupervisorySystem");
        params.addParameter("id",id);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    duDao = new Gson().fromJson(data.toString(), new TypeToken<DuDao>(){}.getType());
                    String s = duDao.getStatus();
                    if(s!=null&&!s.isEmpty())status=Integer.parseInt(s);
                    refreshData();

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
                hideDialogProgress(progressDialog);
            }
        });

        //获取反馈信息
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("supervisoryId",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams entry = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/SupervisorySystemDetail/page");
        entry.setAsJsonContent(true);
        entry.setBodyContent(jsonObject.toString());

        Log.e(TAG, "getDataFromService: " + entry);
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        entry.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        entry.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet

        entry.setConnectTimeout(10000);
        x.http().post(entry, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: 反馈 " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray dataList = data.getJSONArray("dataList");
                        Gson gson = new Gson();
                        sceneList.clear();
                        sceneList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<DuDao>>() {
                        }.getType());
                        refreshData();

                    }else {
                        Toast.makeText(DuDaoInfoActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void refreshData() {
        if(status==0){
            text_status.setText("未开始");
            state.setText("未开始");
        }else if(status==1){
            text_status.setText("进行中");
            state.setText("进行中");
        } else if(status==2){
            text_status.setText("已结束");
            state.setText("已结束");
        } else if(status==3){
            text_status.setText("已延期");
            state.setText("已延期");
        }else{
            text_status.setText("即将延期");
            state.setText("即将延期");
        }

        title.setText(duDao.getSupervisoryContent()+"");
        start.setText(new CommonUtils().parseDate(duDao.getSupervisoryStartTime()));
        end.setText(new CommonUtils().parseDate(duDao.getSupervisoryEndTime()));
        person.setText(duDao.getSupervisoryMember()+"");

        String imgStr = duDao.getSupervisoryImg();
        String[] strings;
        if(imgStr!=null && !imgStr.isEmpty()){
            strings = imgStr.split(",");
            ll_pictures.removeAllViews();
            for (int i = 0; i < strings.length; i++) {
                String urls = strings[0];
                View p_ = LayoutInflater.from(this).inflate(R.layout.image, null);
                ImageView image = p_.findViewById(R.id.image);
                Glide.with(this).load(urls).error(R.drawable.ic_no_pic).into(image);
                image.setOnClickListener(v -> {
                    Intent intent;
                    if(urls!=null && urls.contains("mp4")){
                        //视频
                        intent = new Intent(DuDaoInfoActivity.this, PicVideoActivity.class);
                    }else{
                        //图片
                        intent = new Intent(this, PicVideoActivity.class);
                    }
                    intent.putExtra("url",urls);
                    startActivity(intent);
                });
                ll_pictures.addView(p_);
            }
        }

        if(sceneList!=null && !sceneList.isEmpty()){
            ll_upload.setVisibility(View.VISIBLE);
            tv_upload.setVisibility(View.VISIBLE);
            ll_upload.removeAllViews();
            for (int i = 0; i < sceneList.size(); i++) {
                DuDao model = sceneList.get(i);
                View view = LayoutInflater.from(this).inflate(R.layout.upload_info, null);
                TextView user = view.findViewById(R.id.user);
                TextView time = view.findViewById(R.id.time);
                TextView live_detail = view.findViewById(R.id.live_detail);
                TextView live_other = view.findViewById(R.id.live_other);
                LinearLayout ll_pictures = view.findViewById(R.id.ll_pictures);
                LinearLayout ll_videos = view.findViewById(R.id.ll_videos);
                user.setText(model.getCreateUser());
                time.setText(new CommonUtils().parseDate(model.getCreateTime()));
                live_detail.setText(model.getSiteConditions());
                live_other.setText(model.getOtherConditions());
                List<String> pictures = CommonUtils.parseListString(model.getImgUrl());
                List<String> videos = CommonUtils.parseListString(model.getVideoUrl());
                ll_pictures.removeAllViews();
                for (int m = 0; m < pictures.size(); m++) {
                    String s = pictures.get(m);
                    View p_ = LayoutInflater.from(this).inflate(R.layout.image, null);
                    ImageView image = p_.findViewById(R.id.image);
                    Glide.with(this).load(s).error(R.drawable.ic_no_pic).into(image);
                    image.setOnClickListener(v -> {
                        Intent intent;
                        if(s!=null && s.contains("mp4")){
                            //视频
                            intent = new Intent(DuDaoInfoActivity.this, PicVideoActivity.class);
                        }else{
                            //图片
                            intent = new Intent(this, PicVideoActivity.class);
                        }
                        intent.putExtra("url",s);
                        startActivity(intent);
                    });
                    ll_pictures.addView(p_);
                }
                ll_videos.removeAllViews();
                for (int m = 0; m < videos.size(); m++) {
                    String s = videos.get(m);
                    View v_ = LayoutInflater.from(this).inflate(R.layout.image, null);
                    ImageView image = v_.findViewById(R.id.image);
                    Glide.with(this).load(s).error(R.drawable.ic_no_pic).into(image);
                    image.setOnClickListener(v -> {
                        Intent intent;
                        if(s!=null && s.contains("mp4")){
                            //视频
                            intent = new Intent(DuDaoInfoActivity.this, PicVideoActivity.class);
                        }else{
                            //图片
                            intent = new Intent(this, PicVideoActivity.class);
                        }
                        intent.putExtra("url",s);
                        startActivity(intent);
                    });
                    ll_videos.addView(v_);
                }

                ll_upload.addView(view);
            }
        }else{
            ll_upload.setVisibility(View.GONE);
            tv_upload.setVisibility(View.GONE);
        }

    }


}
