package com.haohai.platform.firelibrary.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.multitype.DuDao;
import com.haohai.platform.firelibrary.ui.multitype.DudaoViewBinder;
import com.haohai.platform.firelibrary.ui.multitype.FireMissionViewBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

@Route(path = RouteUtils.DuDaoList)
public class DuDaoListActivity extends HhBaseActivity implements DudaoViewBinder.OnDuDaoItemClick, DatePicker.OnDateChangedListener {
    private static final String TAG = DuDaoListActivity.class.getSimpleName();
    private ActionBar actionBar;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout view_status;
    private LinearLayout view_date;
    private TextView text_date;
    private TextView text_status;
    private ProgressDialog progressDialog;
    private Dialog filterDialog;
    private View filterInflater;
    private TextView all;
    private TextView start;
    private TextView ing;
    private TextView end;
    private TextView delay;
    private boolean isShowDialog = true;
    private boolean isShuaxin = false;
    private boolean isSearch = false;
    private  List<DuDao> fireMissionList;
    public static int ORDER_CHANGE = 113;
    @Autowired
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dudao);
        ARouter.getInstance().inject(this);
        progressDialog = new ProgressDialog(this);
        fireMissionList = new ArrayList<>();
        init_();
        bind_();
        isShowDialog = true;
    }

    private void bind_() {
        RxViewAction.clickNoDouble(view_date).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                showDataDialog(true);
            }
        });
        RxViewAction.clickNoDouble(view_status).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                filterDialog.show();
            }
        });
        RxViewAction.clickNoDouble(all).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                status = -1;
                text_status.setText("全部");
                filterDialog.dismiss();
                getDataFromService();
            }
        });
        RxViewAction.clickNoDouble(start).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                status = 0;
                text_status.setText("未开始");
                filterDialog.dismiss();
                getDataFromService();
            }
        });
        RxViewAction.clickNoDouble(ing).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                status = 1;
                text_status.setText("进行中");
                filterDialog.dismiss();
                getDataFromService();
            }
        });
        RxViewAction.clickNoDouble(end).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                status = 2;
                text_status.setText("已完成");
                filterDialog.dismiss();
                getDataFromService();
            }
        });
        RxViewAction.clickNoDouble(delay).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                status = 3;
                text_status.setText("已延期");
                filterDialog.dismiss();
                getDataFromService();
            }
        });
    }

    private StringBuffer date = new StringBuffer();//"2023-12-01 00:00:00"
    private StringBuffer endDate = new StringBuffer();//"2023-12-31 23:59:59"
    private StringBuffer start_ = new StringBuffer();
    private StringBuffer end_ = new StringBuffer();
    private int year = 2023;
    private int month = 12;
    private int day = 1;
    private int status = -1;//0未开始 1进行中 2已完成 3已延期
    /**
     * 日期选择控件
     */
    private void showDataDialog(boolean start) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(start){
                    if (date.length() > 0) { //清除上次记录的日期
                        date.delete(0, date.length());
                        start_.delete(0, start_.length());
                    }
                    date.append(year);
                    if (month <= 9) {
                        date.append("-0").append((month + 1));
                        start_.append("0").append((month + 1));
                    } else {
                        date.append("-").append((month + 1));
                        start_.append("-").append((month + 1));
                    }
                    if (day < 10) {
                        date.append("-0").append(day);
                        start_.append("-0").append(day);
                    } else {
                        date.append("-").append(day);
                        start_.append("-").append(day);
                    }
                    date.append(" 00:00:00");
                    showDataDialog(false);
                }else{
                    if (endDate.length() > 0) { //清除上次记录的日期
                        endDate.delete(0, endDate.length());
                        end_.delete(0, end_.length());
                    }
                    endDate.append(year);
                    if (month <= 9) {
                        endDate.append("-0").append((month + 1));
                        end_.append("0").append((month + 1));
                    } else {
                        endDate.append("-").append((month + 1));
                        end_.append("-").append((month + 1));
                    }
                    if (day < 10) {
                        endDate.append("-0").append(day);
                        end_.append("-0").append(day);
                    } else {
                        endDate.append("-").append(day);
                        end_.append("-").append(day);
                    }
                    endDate.append(" 23:59:59");
                    text_date.setText(start_ + "~" + end_);

                    getDataFromService();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
        Calendar date = Calendar.getInstance();
        int year1 = date.get(Calendar.YEAR);
        int month1 = date.get(Calendar.MONTH);
        int day1 = date.get(Calendar.DATE);
        String endData = year1 - 10 + "-" + month1 + "-" + day1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(endData);
        } catch (ParseException e) {

        }
        long starTimre = date2.getTime();


        long endTimre = System.currentTimeMillis();

        //datePicker.setMaxDate(endTimre);
        //datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }

    private String parse9(String str) {
        if(str == null){
            return "暂无记录";
        }
        String r = str;
        try{
            r = str.substring(0,10).replace("T"," ");
        }catch (Exception e){
            //HhLog.e(e.getMessage());
        }
        return r;
    }

    private void getDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            if(!text_date.getText().toString().contains("时间")){
                jsonObject.put("supervisoryStartTime",date);
                jsonObject.put("supervisoryEndTime",endDate);
            }
            if(!text_status.getText().toString().contains("任务状态") && status!=-1){
                jsonObject.put("status",status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * oa/api/taskManagement/page  {"page":1,"limit":20,"dto":{}} post
         * 分页功能接口
         */
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "oa/api/SupervisorySystem/page");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());

        Log.e(TAG, "getDataFromService: " + params);
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray dataList = data.getJSONArray("dataList");
                        Gson gson = new Gson();
                        fireMissionList.clear();
                        fireMissionList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<DuDao>>() {
                        }.getType());

                        initData();
                    }else {
                        Toast.makeText(DuDaoListActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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
        items.addAll(fireMissionList);
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    private void init_() {
        actionBar = (ActionBar) findViewById(R.id.action_bar);
        actionBar.setTitle("督导检查");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch ((var1)) {
                    case -1:
                        onBackPressed();
                        break;
                        case -2:
                        break;
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fire_mission_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = (RecyclerView) findViewById(R.id.fire_mission_listview);

        //下拉刷新
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getDataFromService();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        register();

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

        text_date = findViewById(R.id.text_date);
        text_status = findViewById(R.id.text_status);
        view_date = findViewById(R.id.view_date);
        view_status = findViewById(R.id.view_status);



        //筛选dialog
        filterDialog = new Dialog(this, R.style.centerDialog);
        filterInflater = LayoutInflater.from(this).inflate(R.layout.filter_dudao, null);
        all = ( filterInflater.findViewById(R.id.all));
        start = ( filterInflater.findViewById(R.id.start));
        ing = ( filterInflater.findViewById(R.id.ing));
        end = ( filterInflater.findViewById(R.id.end));
        delay = ( filterInflater.findViewById(R.id.delay));
        filterDialog.setContentView(filterInflater);
        Window searchDialogWindow = filterDialog.getWindow();
        searchDialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lpSearch = searchDialogWindow.getAttributes();
        searchDialogWindow.setAttributes(lpSearch);
        filterDialog.setCanceledOnTouchOutside(true);

        Calendar instance = Calendar.getInstance();
        year = instance.get(Calendar.YEAR);
        month = instance.get(Calendar.MONTH);
        day = instance.get(Calendar.DAY_OF_MONTH);
    }

    private void register() {
        DudaoViewBinder dudaoViewBinder = new DudaoViewBinder();
        dudaoViewBinder.setListener(this);
        dudaoViewBinder.setContext(this);
        adapter.register(DuDao.class, dudaoViewBinder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromService();
    }

    /**
     * 任务单条目点击
     * @param duDao
     */
    @Override
    public void onDuDaoItemClickListener(DuDao duDao) {
        Intent intent = new Intent(getApplicationContext(), DuDaoInfoActivity.class);
        intent.putExtra("ID",duDao.getId());
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:requestCode " + requestCode);
        Log.e(TAG, "onActivityResult:resultCode " +  resultCode);
        if (resultCode == ORDER_CHANGE) {
            Log.e(TAG, "onActivityResult: " );
            fireMissionList.clear();
            isShowDialog = true;
            getDataFromService();
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int years, int monthOfYear, int dayOfMonth) {
        year = years;
        month = monthOfYear;
        day = dayOfMonth;
    }
}
