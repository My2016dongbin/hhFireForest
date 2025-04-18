package com.haohai.platform.mapmodel.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.multitype.Historyzg;
import com.haohai.platform.mapmodel.multitype.HistoryzgViewBinder;
import com.haohai.platform.mapmodel.multitype.Historyzgpic;
import com.haohai.platform.mapmodel.multitype.HistoryzgpicViewBinder;
import com.haohai.platform.mapmodel.multitype.LookImage;
import com.haohai.platform.mapmodel.multitype.LookImageViewBinder;
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

public class HistoryZZActivity extends AppCompatActivity implements LookImageViewBinder.OnChooseImageClickListener,HistoryzgpicViewBinder.OnChooseImageClickListener{
    private RecyclerView historyZGList;
    private MultiTypeAdapter historyAdapter;
    private List<Object> historyItems = new ArrayList<>();
    private HistoryzgViewBinder historyzgViewBinder;
    private HistoryzgpicViewBinder historyzgpicViewBinder;
    private String access_token;
    private String TAG = HistoryZZActivity.class.getSimpleName();
    private int maxcount=0;
    private String historyid;
    private String historydes;
    private String count;
    private TextView beizhuEdit;
    private int datanum=0;
    private String id;
    private List<Historyzg> historyzgList =new ArrayList<>();
    private Intent intent;
    private String name;
    private TextView resourceName;
    private RecyclerView lookPhotoListView;
    private MultiTypeAdapter lookPhotoAdapter;
    private List<Object> lookPhotoItems = new ArrayList<>();
    private LookImageViewBinder lookImageViewBinder;
    private List<LookImage> looklist = new ArrayList<>();
    private ImageView backButton;
    private List<Historyzgpic> historyzgpicList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_z_z);
        access_token = new DbConfig(this).getUser().getToken();
        intent = getIntent();
        id = intent.getStringExtra("resourceID");
        name = intent.getStringExtra("resourcename");
        initView();
        initCheckFieldIntoDb();
    }

    private void initView() {
        refreshLayout=findViewById(R.id.refresh_layout);
        refreshLayout.setEnabled(false);
        backButton = (ImageView) findViewById(R.id.back_button);
        resourceName=findViewById(R.id.resource_name);
        resourceName.setText(name);
        beizhuEdit=findViewById(R.id.beizhu_edit);
        historyZGList = (RecyclerView) findViewById(R.id.history_zg);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyZGList.setLayoutManager(linearLayoutManager);
        historyAdapter = new MultiTypeAdapter(historyItems);
        historyZGList.setHasFixedSize(true);
        historyZGList.setNestedScrollingEnabled(false);
        historyzgViewBinder = new HistoryzgViewBinder(this);
        historyzgpicViewBinder=new HistoryzgpicViewBinder(this);
        historyzgpicViewBinder.setListener(this);
        historyAdapter.register(Historyzg.class, historyzgViewBinder);
        historyAdapter.register(Historyzgpic.class, historyzgpicViewBinder);
        historyZGList.setAdapter(historyAdapter);
        assertHasTheSameAdapter(historyZGList, historyAdapter);

        lookPhotoListView = (RecyclerView) findViewById(R.id.phote_recycle_look);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(this, 3);
        lookPhotoListView.setLayoutManager(gridLayoutManager1);
        lookPhotoAdapter = new MultiTypeAdapter(lookPhotoItems);
        lookPhotoListView.setHasFixedSize(true);
        lookPhotoListView.setNestedScrollingEnabled(false);
        lookImageViewBinder = new LookImageViewBinder(this);
        lookImageViewBinder.setListener(this);
        lookPhotoAdapter.register(LookImage.class, lookImageViewBinder);
        lookPhotoListView.setAdapter(lookPhotoAdapter);
        assertHasTheSameAdapter(lookPhotoListView, lookPhotoAdapter);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void initCheckFieldIntoDb() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResourceItemHistory/selectResourceItem");
        params.setConnectTimeout(20000);
        params.addHeader("Authorization","bearer " + access_token);
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

                        try {
                            if (data.length()>0){
                                JSONObject object = data.getJSONObject(0);
                                maxcount=object.getInt("count");
                            }
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject dataObject = data.getJSONObject(i);
                                //maxcount=dataObject.getInt("count");
                                Log.e(TAG, "onSuccess12: "+dataObject.getInt("count"));
                                if (dataObject.getInt("count")<maxcount){
                                    JSONObject historyobject = data.getJSONObject(i);
                                    historyid = historyobject.getString("id");
                                    historydes = historyobject.getString("description");
                                    Historyzg historyzg = new Historyzg();
                                    //historyzg.setImgurl(data.getJSONObject(i).getString("img"));
                                    historyzg.setId(historyid);
                                    historyzg.setDescription(historydes);
                                    historyzg.setCishu(dataObject.getInt("count"));
                                    historyzg.setUserName(historyobject.getString("userName"));
                                    historyzg.setUserType(historyobject.getString("userType"));
                                    historyzg.setRegulation(historyobject.getString("regulation"));
                                    Log.e(TAG, "onSuccess: "+historyobject.getString("createUser"));
                                    historyzg.setCreateUser(historyobject.getString("createUser"));
                                    historyzgList.add(historyzg);
                                    getHistoryPictureListFromService(historyid,historydes,dataObject.getInt("count"));
                                }
                                datanum=i;
                            }
                            JSONObject object = data.getJSONObject(datanum);
                            beizhuEdit.setText(object.getString("description"));
                            count=object.getString("id");
                            getPictureListFromService();
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
    private void getPictureListFromService() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("historyId", count);
            jsonObject.put("type", 1);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/imgSignature/list");
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + access_token);
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            LookImage lookImage = new LookImage();
                            lookImage.setUri(data.getJSONObject(i).getString("img"));
                            lookImage.setuCheckId(i+"");
                            looklist.add(lookImage);
                            // items.add(evaluateImage);
                            updatepic();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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

            }
        });
    }
    private void getHistoryPictureListFromService(final String id, final String historydes, final int hisnum) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("historyId", id);
            jsonObject.put("type", 3);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/imgSignature/list");
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + access_token);
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            // items.add(evaluateImage);
                            historyzgpicList.add(new Historyzgpic(data.getJSONObject(i).getString("img"),id));
                        }
                        updatehistorypic();
                    } else {
                        Toast.makeText(getApplicationContext(), "数据获取失败", Toast.LENGTH_SHORT).show();
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

            }
        });
    }
    private void updatehistorypic() {
        historyItems.clear();
        if (historyzgList==null){
        }else {
            for (int i = 0; i < historyzgList.size(); i++) {
                historyItems.add(historyzgList.get(i));
                if (historyzgpicList.size()>0) {
                    for (int j = 0; j < historyzgpicList.size(); j++) {
                        if (historyzgList.get(i).getId().equals(historyzgpicList.get(j).getId())) {
                            historyItems.add(historyzgpicList.get(j));
                        }
                    }
                }
            }
            assertAllRegistered(historyAdapter,historyItems);
            historyAdapter.notifyDataSetChanged();
        }
    }
    private void updatepic() {
        lookPhotoItems.clear();
        if (looklist==null){

        }else {
            for (int i = 0; i < looklist.size(); i++) {
                lookPhotoItems.add(looklist.get(i));
            }
            assertAllRegistered(lookPhotoAdapter,lookPhotoItems);
            lookPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onImageClick(String imgurl) {
        Intent intent = new Intent(getApplicationContext(), PicActivity.class);
        intent.putExtra("pic",imgurl);
        startActivity(intent);
    }

    @Override
    public void onZZImageClick(String imgurl) {
        Intent intent = new Intent(getApplicationContext(), PicActivity.class);
        intent.putExtra("pic",imgurl);
        startActivity(intent);
    }
}