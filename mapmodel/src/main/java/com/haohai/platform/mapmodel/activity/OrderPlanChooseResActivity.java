package com.haohai.platform.mapmodel.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haohai.platform.firelibrary.ui.view.MNCTransparentDialog;
import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.bus.RefreshModel;
import com.haohai.platform.mapmodel.fragment.CheckPlanRightFg;
import com.haohai.platform.mapmodel.multitype.Resource;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Grid;
import com.ruyiruyi.rylibrary.request.RequestUtils;

import org.greenrobot.eventbus.EventBus;
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
import java.util.Objects;

import rx.functions.Action1;

public class OrderPlanChooseResActivity extends AppCompatActivity {
    private static final String TAG = OrderPlanChooseResActivity.class.getSimpleName();
    private ImageView backButton;
    private TextView tv_complete;
    private LinearLayout ll_area;
    private LinearLayout ll_resource;
    private ScrollView sv_resource;
    private LinearLayout ll_qu;
    private LinearLayout ll_jiedao;
    public List<Grid> quList;
    public List<Grid> jiedaoList;
    private String gridNo;
    private String parentGridNo;
    private String id;
    private int quIndex = 0;
    private int jiedaoIndex = 0;
    private String access_token;
    private LayoutInflater inflater;
    private int state = 0;//0 网格选择 1 资源点选择
    private List<Resource> checkedResource;
    private boolean cityRoot = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gridNo = getIntent().getStringExtra("gridNo");
        parentGridNo = getIntent().getStringExtra("parentGridNo");
        id = getIntent().getStringExtra("id");
        setContentView(R.layout.activity_order_plan_choose_res);
        initView();
        initData();
    }

    private void initData() {
        getAllQu();
    }

    private void initView() {
        cityRoot = new DbConfig(this).getUser().getJobTitle().contains("市");
        inflater = LayoutInflater.from(OrderPlanChooseResActivity.this);
        access_token = new DbConfig(this).getUser().getToken();
        quList = new ArrayList<>();
        checkedResource = new ArrayList<>();
        jiedaoList = new ArrayList<>();
        backButton = findViewById(R.id.back_button);
        ll_qu = findViewById(R.id.ll_qu);
        tv_complete = findViewById(R.id.tv_complete);
        ll_area = findViewById(R.id.ll_area);
        sv_resource = findViewById(R.id.sv_resource);
        ll_resource = findViewById(R.id.ll_resource);
        ll_jiedao = findViewById(R.id.ll_jiedao);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RxViewAction.clickNoDouble(tv_complete).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(checkedResource.size() == 0){
                    Toast.makeText(OrderPlanChooseResActivity.this, "您还没有选择资源点", Toast.LENGTH_SHORT).show();
                    return;
                }
                showCompleteDialog("确定绑定资源点吗？");
            }
        });

    }

    public void showCompleteDialog(String msg) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tips, null, false);
        TextView message_text = (TextView) dialogView.findViewById(R.id.message_text);
        message_text.setText(msg);
        final TextView tv_queren = (TextView) dialogView.findViewById(R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(R.id.tv_left);
        tv_queren.setText("确定");
        //确定
        RxViewAction.clickNoDouble(tv_queren).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();

                postComplete();
            }
        });
        //取消
        RxViewAction.clickNoDouble(tv_left).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();

            }
        });
        mncTransDialog.show();
        Window window = mncTransDialog.getWindow();//对话框窗口
        window.setGravity(Gravity.CENTER);//设置对话框显示在屏幕中间
        window.setWindowAnimations(R.style.dialog_style);//添加动画
        window.setContentView(dialogView);
    }

    void postComplete(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("id",id);
            obj.put("gridName",jiedaoList.get(jiedaoIndex).getName());
            obj.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
            obj.put("parentGridName",quList.get(quIndex).getName());
            obj.put("parentGridNo",quList.get(quIndex).getGridNo());
            obj.put("planResourceDTOS",checkedResource);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String mdo = new Gson().toJson(obj);
        JSONObject nameValuePairs = new JSONObject();
        try {
            JSONObject xbj = new JSONObject(mdo);
            nameValuePairs = xbj.getJSONObject("nameValuePairs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "postComplete: bingo" + nameValuePairs.toString());
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan/randomSaveResource");
        params.setBodyContent(nameValuePairs.toString());
        params.addHeader("Authorization","bearer " + access_token);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200){
                        Toast.makeText(OrderPlanChooseResActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        setResult(CheckPlanRightFg.CHOOSE_RESOURCE);
                        EventBus.getDefault().post(RefreshModel.getInstance());
                        finish();
                    }else{
                        Toast.makeText(OrderPlanChooseResActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(state == 1){
            state = 0;
            ll_area.setVisibility(View.VISIBLE);
            sv_resource.setVisibility(View.GONE);
            tv_complete.setVisibility(View.GONE);
        }else{
            finish();
        }

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
                    .where("parentid", "=", quList.get(quIndex).getId())
                    .findAll();
            jiedaoIndex = 0;

            //当前任务网格下选择(市级、区级账号区分)街道
            if(!cityRoot){
                Grid grid = new Grid();
                for (int i = 0; i < jiedaoList.size(); i++) {
                    if(jiedaoList.get(i).getGridNo().contains(gridNo)){
                        grid = jiedaoList.get(i);
                    }
                }
                jiedaoList.clear();
                jiedaoList.add(grid);
            }

            inflateJiedao();
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

            int removeIndex = -1;
            for (int i = 0; i < quList.size(); i++) {
                if (quList.get(i).getName().contains("高新")) {
                    removeIndex = i;
                }
            }
            if(removeIndex!=-1){
                quList.remove(removeIndex);
            }

            //当前任务网格下选择(市级、区级账号区分)区
            if(!cityRoot){
                Grid grid = new Grid();
                for (int i = 0; i < quList.size(); i++) {
                    if(quList.get(i).getGridNo().contains(parentGridNo)){
                        grid = quList.get(i);
                    }
                }
                quList.clear();
                quList.add(grid);
            }else{
                Grid grid = new Grid();
                for (int i = 0; i < quList.size(); i++) {
                    if(quList.get(i).getGridNo().contains(gridNo)){
                        grid = quList.get(i);
                    }
                }
                quList.clear();
                quList.add(grid);
            }

            inflateQu();

            //获取默认街道
            if(quList.size()!=0){
                quIndex = 0;
                getAllJieDao();
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void inflateQu() {
        ll_qu.removeAllViews();
        for (int i = 0; i < quList.size(); i++) {
            View view = inflater.inflate(R.layout.item_order_plan_qu,null);
            View v_indicator = view.findViewById(R.id.v_indicator);
            TextView tv_qu = view.findViewById(R.id.tv_qu);
            LinearLayout ll_item = view.findViewById(R.id.ll_item);
            tv_qu.setText(quList.get(i).getName());
            if(i == quIndex){
                v_indicator.setVisibility(View.VISIBLE);
                tv_qu.setTextColor(getResources().getColor(R.color.theme_primary));
            }else{
                v_indicator.setVisibility(View.INVISIBLE);
                tv_qu.setTextColor(getResources().getColor(R.color.c5mid));
            }
            int finalI = i;
            RxViewAction.clickNoDouble(ll_item).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    quIndex = finalI;
                    inflateQu();
                    getAllJieDao();
                }
            });
            ll_qu.addView(view);
        }
    }

    private void inflateJiedao() {
        ll_jiedao.removeAllViews();
        for (int i = 0; i < jiedaoList.size(); i++) {
            View view = inflater.inflate(R.layout.item_order_plan_qu,null);
            TextView tv_jiedao = view.findViewById(R.id.tv_qu);
            LinearLayout ll_item = view.findViewById(R.id.ll_item);
            tv_jiedao.setText(jiedaoList.get(i).getName());
            if(i == jiedaoIndex){
                tv_jiedao.setTextColor(getResources().getColor(R.color.theme_primary));
            }else{
                tv_jiedao.setTextColor(getResources().getColor(R.color.c5mid));
            }
            int finalI = i;
            RxViewAction.clickNoDouble(ll_item).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    jiedaoIndex = finalI;
                    inflateJiedao();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            state = 1;
                            ll_area.setVisibility(View.GONE);
                            sv_resource.setVisibility(View.VISIBLE);
                            tv_complete.setVisibility(View.VISIBLE);
                            //获取当前所选网格资源点
                            ll_resource.removeAllViews();
                            getGridResource();
                        }
                    },500);
                }
            });
            ll_jiedao.addView(view);
        }
    }


    private void inflateResource(List<Resource> resourceList, String title) {
        final boolean[] show = {true};
        View outView = inflater.inflate(R.layout.item_order_plan_res,null);
        TextView tv_type = outView.findViewById(R.id.tv_type);
        ImageView iv_type = outView.findViewById(R.id.iv_type);
        FrameLayout fl_type = outView.findViewById(R.id.fl_type);
        LinearLayout ll_list = outView.findViewById(R.id.ll_list);
        tv_type.setText(title);
        if(resourceList.size()!=0){
            iv_type.setImageDrawable(getResources().getDrawable(R.mipmap.bt_arrow_down_gray));
            ll_list.setVisibility(View.VISIBLE);
        }else{
            ll_list.setVisibility(View.GONE);
        }
        for (int i = 0; i < resourceList.size(); i++) {
            final boolean[] check = {false};
            View view = inflater.inflate(R.layout.item_ll,null);
            LinearLayout ll_item = view.findViewById(R.id.ll_out);
            ImageView iv_check = view.findViewById(R.id.iv_check);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(resourceList.get(i).name);
            iv_check.setImageDrawable(getResources().getDrawable(R.mipmap.check_n));
            //资源点选择
            int finalI = i;
            RxViewAction.clickNoDouble(ll_item).subscribe(new Action1<Void>() {
                @Override
                public void call(Void unused) {
                    if(!check[0]){
                        iv_check.setImageDrawable(getResources().getDrawable(R.mipmap.check_y));
                        check[0] = true;
                        clickResourceItem(resourceList.get(finalI),true);
                    }else{
                        iv_check.setImageDrawable(getResources().getDrawable(R.mipmap.check_n));
                        check[0] = false;
                        clickResourceItem(resourceList.get(finalI),false);
                    }
                }
            });
            ll_list.addView(view);
        }
        //类型标题伸缩
        RxViewAction.clickNoDouble(fl_type).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(!show[0] && resourceList.size()!=0){
                    ll_list.setVisibility(View.VISIBLE);
                    show[0] = true;
                    iv_type.setImageDrawable(getResources().getDrawable(R.mipmap.bt_arrow_down_gray));
                } else if(show[0] && resourceList.size()!=0){
                    ll_list.setVisibility(View.GONE);
                    show[0] = false;
                    iv_type.setImageDrawable(getResources().getDrawable(R.mipmap.bt_arrow_up_gray));
                }else{
                    ll_list.setVisibility(View.GONE);
                    show[0] = false;
                }
            }
        });
        ll_resource.addView(outView);
    }

    private void clickResourceItem(Resource resource, boolean check) {
        if(check){//添加
            for (int i = 0; i < checkedResource.size(); i++) {
                Resource item = checkedResource.get(i);
                if(Objects.equals(item.getId(), resource.getId())){
                    return;
                }
                if(i == checkedResource.size()-1){
                    checkedResource.add(resource);
                    return;
                }
            }
            if(checkedResource.size() == 0){
                checkedResource.add(resource);
            }
        }else{//删除
            for (int i = 0; i < checkedResource.size(); i++) {
                Resource item = checkedResource.get(i);
                if(Objects.equals(item.getId(), resource.getId())){
                    checkedResource.remove(i);
                    return;
                }
            }
        }
    }


    private void getGridResource() {
        initCheckStationIntoDb();
        initHelicopterPointIntoDb();
        initMaterialRepositoryIntoDb();
        initCemeteryIntoDb();
        initDangerSourceIntoDb();
        initFireCommandIntoDb();
        initWatchTowerIntoDb();
        initWaterSourceIntoDb();
        initMonitorIntoDb();
        initTeamDTOIntoDb();
    }


    /**
     * 获取护林员检查站
     */
    private void initCheckStationIntoDb() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/checkStation/list");
        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: --" +  jsonObject.toString());
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess3------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        /*checkedResource = new Gson().fromJson(String.valueOf(data), new TypeToken<List<OrderPlanResource>>() {
                        }.getType());*/
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String description = object.optString("description");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String picture = object.optString("picture");
                                    String state = object.optString("state");

                                    String peopleCount = object.optString("peopleCount");
                                    String extinguisherCount = object.optString("extinguisherCount");
                                    String sawCount = object.optString("sawCount");
                                    String truckCount = object.optString("truckCount");
                                    String dataSnapshot = object.optString("dataSnapshot");
                                    String isAllday = object.optString("isAllday");
                                    String mountain = object.optString("mountain");
                                    String peopleName = object.optString("peopleName");
                                    String waterPistolCount = object.optString("waterPistolCount");
                                    String twoToolCount = object.optString("twoToolCount");
                                    String otherToolCount = object.optString("otherToolCount");
                                    String hasMonitor = object.optString("hasMonitor");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "checkStation", "/api/checkStation", gridId,
                                            gridName, gridNo, "护林检查站", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("checkStation");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }

                        inflateResource(resourceList,"护林检查站");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:checkStation 请求失败" + ex.toString());
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
     * 直升机机降点
     */
    private void initHelicopterPointIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/helicopterPoint/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess11----- " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String address = object.optString("address");
                                    String createTime = object.optString("createTime");
                                    String createUser = object.optString("createUser");
                                    String description = object.optString("description");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String gridId = object.optString("gridId");
                                    String gridName = object.optString("gridName");
                                    String gridNo = object.optString("gridNo");
                                    String type = object.optString("type");
                                    String groupId = object.optString("groupId");
                                    String iconFile = object.optString("iconFile");
                                    String id = object.optString("id");
                                    String leaderId = object.optString("leaderId");
                                    String leaderName = object.optString("leaderName");

                                    String leaderPhone = object.optString("leaderPhone");
                                    String name = object.optString("name");
                                    String picture = object.optString("picture");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");
                                    String textColor = object.optString("textColor");
                                    String updateTime = object.optString("updateTime");
                                    String updateUser = object.optString("updateUser");
                                    String waterSource = object.optString("waterSource");
                                    String resourceType = object.optString("resourceType");
                                    String state = object.optString("state");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "helicopterPoint", "/api/helicopterPoint", gridId,
                                            gridName, gridNo, "直升机机降点", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("helicopterPoint");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }
                            }
                        }

                        inflateResource(resourceList,"直升机机降点");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: materialRepository请求失败" + ex.toString());
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
     * 物资库
     */
    private void initMaterialRepositoryIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/materialRepository/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess10------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String type = object.optString("type");
                                    String name = object.optString("name");
                                    Log.e(TAG, "onSuccess: wuzi" + name);
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    int fireEquipmentCount = object.optInt("fireEquipmentCount");
                                    int safeEquipmentCount = object.optInt("safeEquipmentCount");
                                    int outdoorEquipmentCount = object.optInt("outdoorEquipmentCount");
                                    int communicateEquipmentCount = object.optInt("communicateEquipmentCount");
                                    int carCount = object.optInt("carCount");
                                    int machineCount = object.optInt("machineCount");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");
                                    String state = object.optString("state");

                                    String windFireCount = object.optString("windFireCount");
                                    String sprayFireCount = object.optString("sprayFireCount");
                                    String waterPumpCount = object.optString("waterPumpCount");
                                    String twoToolCount = object.optString("twoToolCount");
                                    String waterPistolCount = object.optString("waterPistolCount");
                                    String chainSawCount = object.optString("chainSawCount");
                                    String bushCutterCount = object.optString("bushCutterCount");
                                    String fireCutterCount = object.optString("fireCutterCount");
                                    String fireproofClothesCount = object.optString("fireproofClothesCount");
                                    String glovesCount = object.optString("glovesCount");
                                    String helmetCount = object.optString("helmetCount");
                                    String shoesCount = object.optString("shoesCount");
                                    String waterBagCount = object.optString("waterBagCount");
                                    String waterSacCount = object.optString("waterSacCount");
                                    String oilDrumCount = object.optString("oilDrumCount");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "materialRepository", "/api/materialRepository", gridId,
                                            gridName, gridNo, "物资储备库", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("materialRepository");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }


                        inflateResource(resourceList,"物资储备库");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: materialRepository请求失败" + ex.toString());
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
     * 墓地数据
     */
    private void initCemeteryIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/cemetery/list");

        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess9--- " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    Log.e(TAG, "onSuccess: 1111");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");

                                    String graveCount = object.optString("graveCount");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), "", "cemetery", "/api/cemetery", gridId,
                                            gridName, gridNo, "墓地", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("cemetery");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }


                        inflateResource(resourceList,"墓地");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: cemetery请求失败" + ex.toString());
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
     * 重大危险源数据
     */
    private void initDangerSourceIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/dangerSource/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess8------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");

                                    String isMajorHazard = object.optString("isMajorHazard");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "dangerSource", "/api/dangerSource", gridId,
                                            gridName, gridNo, "危险源", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("dangerSource");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }


                        inflateResource(resourceList,"危险源");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:dangerSource 请求失败" + ex.toString());

                if (ex.toString().contains("timeout")){
                    initDangerSourceIntoDb();
                }
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
     * 指挥部数据
     */
    private void initFireCommandIntoDb() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/fireCommand/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess7------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "fireCommand", "/api/fireCommand", gridId,
                                            gridName, gridNo, "森林防火监测中心", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("fireCommand");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }


                        inflateResource(resourceList,"森林防火监测中心");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError:fireCommand 请求失败" + ex.toString());
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
     * 瞭望塔数据
     */
    private void initWatchTowerIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/watchTower/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess6------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");

                                    String watchRange = object.optString("watchRange");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), "", "watchTower", "/api/watchTower", gridId,
                                            gridName, gridNo, "瞭望塔", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("watchTower");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }

                        inflateResource(resourceList,"瞭望塔");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: watchTower请求失败" + ex.toString());
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
     * 水源地数据
     */
    private void initWaterSourceIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/waterSource/list");
        params.setConnectTimeout(200000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess5------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String type = object.optString("type");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String waterCapacity = object.optString("waterCapacity");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    String isHelicopterWater = object.optString("isHelicopterWater");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "waterSource", "/api/waterSource", gridId,
                                            gridName, gridNo, "水源地", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("waterSource");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }

                        inflateResource(resourceList,"水源地");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: waterSource请求失败" + ex.toString());

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
     * 视频监控点数据
     */
    private void initMonitorIntoDb() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/monitor/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "initResourceIntoDb: " + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess4------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String monitorNo = object.optString("monitorNo");
                                    String isOnline = object.optString("isOnline");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String districtNo = object.optString("districtNo");
                                    String districtName = object.optString("districtName");
                                    String streetNo = object.optString("streetNo");
                                    String streetName = object.optString("streetName");
                                    String monitorArea = object.optString("monitorArea");
                                    String placeName = object.optString("placeName");
                                    String sweepArea = object.optString("sweepArea");
                                    String altitude = object.optString("altitude");
                                    String towerHeight = object.optString("towerHeight");
                                    String northCorrection = object.optString("northCorrection");
                                    String horizontalCorrection = object.optString("horizontalCorrection");
                                    String visualRange = object.optString("visualRange");
                                    String visualTime = object.optString("visualTime");
                                    String horizontalAngle = object.optString("horizontalAngle");
                                    String verticalAngle = object.optString("verticalAngle");
                                    String remark = object.optString("remark");
                                    String state = object.optString("state");

                                    String monitorRange = object.optString("monitorRange");
                                    String isNetworking = object.optString("isNetworking");
                                    String isIntelligentEntry = object.optString("isIntelligentEntry");
                                    String description = object.optString("description");
                                    String picture = object.optString("description");


                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");
                                    // String otherPic = " ";

                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), "", "camera", "/api/camera", gridId,
                                            gridName, gridNo, "视频监控点", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("monitor");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }

                        inflateResource(resourceList,"视频监控点");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: monitor请求失败" + ex.toString());
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
     * 下载专业队伍资源点
     */
    private void initTeamDTOIntoDb() {
        Log.e(TAG, "bingo: " );
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("gridNo",jiedaoList.get(jiedaoIndex).getGridNo());
        } catch (JSONException e) {
            Log.e(TAG, "bingo e = : " + e.toString());
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/team/list");
        params.setConnectTimeout(20000);
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "bingo initTeamDTOIntoDb: jsonObject.toString() = " + jsonObject.toString() );
        params.addHeader("Authorization","bearer " + access_token);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess2------ " + result);
                try {
                    List<Resource> resourceList = new ArrayList<>();
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject1.getJSONArray("data");
                        if (data.length()>0) {
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    JSONObject object = data.getJSONObject(i);
                                    String createUser = object.optString("createUser");
                                    String updateUser = object.optString("updateUser");
                                    String createTime = object.optString("createTime");
                                    String updateTime = object.optString("updateTime");
                                    String resourceType = object.optString("resourceType");
                                    String groupId = object.optString("groupId");
                                    String id = object.optString("id");
                                    String name = object.optString("name");
                                    String no = object.optString("no");
                                    String address = object.optString("address");
                                    String gridId = object.optString("gridId");
                                    String gridNo = object.optString("gridNo");
                                    String gridName = object.optString("gridName");
                                    String type = object.optString("type");
                                    String peopleNumber = object.optString("peopleNumber");
                                    String leaderName = object.optString("leaderName");
                                    String leaderPhone = object.optString("leaderPhone");
                                    String picture = object.optString("picture");
                                    String textColor = object.optString("textColor");
                                    String iconFile = object.optString("iconFile");
                                    String description = object.optString("description");
                                    String state = object.optString("state");
                                    JSONObject position = object.getJSONObject("position");
                                    String lng = position.optString("lng");
                                    String lat = position.optString("lat");
                                    String districtName = object.optString("districtName");
                                    String districtNo = object.optString("districtNo");
                                    String streetName = object.optString("streetName");
                                    String streetNo = object.optString("streetNo");

                                    String teamCount = object.optString("teamCount");
                                    String phone = object.optString("phone");
                                    String truckCount = object.optString("truckCount");
                                    String troopCarrierCount = object.optString("troopCarrierCount");
                                    String commandCarCount = object.optString("commandCarCount");
                                    String waterPumpCount = object.optString("waterPumpCount");
                                    String windFireCount = object.optString("windFireCount");
                                    String twoToolCount = object.optString("twoToolCount");
                                    String waterPistolCount = object.optString("waterPistolCount");
                                    String intercomCount = object.optString("intercomCount");
                                    String equipmentTruckCount = object.optString("equipmentTruckCount");
                                    String barracksMeasure = object.optString("barracksMeasure");
                                    String waterCarCount = object.optString("waterCarCount");
                                    String otherPic = object.optString("otherPic");
                                    String checkState = object.optString("checkState");

                                    Resource resource = new Resource(id, name, Double.parseDouble(lng), Double.parseDouble(lat), type, "team", "/api/team", gridId,
                                            gridName, gridNo, "队伍驻防点", groupId);
                                    resource.setDistrictNo(districtNo);

                                    resource.setResourceId(id);
                                    resource.setResourceType("team");
                                    resource.setLatitude(Double.parseDouble(lat));
                                    resource.setLongitude(Double.parseDouble(lng));
                                    resource.setParentGridName(quList.get(quIndex).getName());
                                    resource.setParentGridNo(quList.get(quIndex).getGridNo());

                                    resourceList.add(resource);
                                } catch (Exception e) {
                                    continue;
                                }

                            }
                        }

                        inflateResource(resourceList,"队伍驻防点");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: team请求失败" + ex.toString());
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