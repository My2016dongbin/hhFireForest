package com.haohai.platform.mapmodel.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.activity.FireAddActivity;
import com.haohai.platform.firelibrary.ui.activity.FireMissionInfoActivity;
import com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.multitype.FireMission;
import com.haohai.platform.firelibrary.ui.multitype.FireMissionViewBinder;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.listener.OnLoadMoreListener;
import com.haohai.platform.mapmodel.multitype.CheckPlanList;
import com.haohai.platform.mapmodel.multitype.CheckPlanListStationViewBinder;
import com.haohai.platform.mapmodel.multitype.CheckPlanListViewBinder;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.haohai.platform.mapmodel.multitype.EmptyViewBinder;
import com.haohai.platform.platformmodel.ui.Multitype.Bottom;
import com.haohai.platform.platformmodel.ui.Multitype.BottomViewBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.Area;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.ui.cell.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
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

import static com.haohai.platform.firelibrary.ui.activity.FireMissionListActivity.ORDER_CHANGE;
import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class CheckplanActivity extends HhBaseActivity implements DatePicker.OnDateChangedListener,CheckPlanListStationViewBinder.OnOneBodyItemClick,CheckPlanListViewBinder.OnPlanItemClick {
    private static final String TAG = CheckplanActivity.class.getSimpleName();
    private LinearLayout diquLayout;
    private Dialog shaixuanDialog;
    private View shaixuanInflater;
    private TextView allText;
    private TextView yizhouText;
    private TextView yiyueText;
    private TextView sanyueText;
    private TextView bannianText;
    private TextView yinianText;
    private TextView gaojiStartimeText;
    private TextView gaojiEndtimeText;
    private TextView shengLayout;
    private TextView shiLayout;
    private TextView quLayout;
    private TextView allztText;
    private TextView daijianchaText;
    private TextView jinxinghzongText;
    private TextView yiwanchengText;
    private TextView findButton;
    private StringBuffer date;
    private int year;
    private int month;
    private int day;
    private TextView chongzhiButton;
    private ProgressDialog progressDialog;
    private boolean isShowDialog = true;
    private List<CheckPlanList> checkplanList;
    private List<CheckPlanList> checkplanLists;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private RecyclerView listView;
    private int currentPage = 1;
    private int totalSize;
    private int lastPage=1;
    private int allTotal = 0;
    private int pageSize = 20;
    private boolean isShuaxin=false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String CurrentTime;
    private String chooetime="";
    private String endtime;
    public int currentChooseArea = 0;  //当前在选择省还是市   0选择省  1选择市
    private WheelView areaWy;
    public int shengSelectIndex = 0;
    public int shiSelectIndex = 0;
    public int quSelectIndex = 0;
    public boolean isChooseSheng = false;
    public String currentChooseSheng = "山东省";
    public String currentChooseShi = "临沂市";
    public String currentChooseQu = "";
    private boolean fromMap = false;
    public List<Area> shengList;
    public List<String> shengStrList;
    public List<Area> shiList;
    public List<String> shiStrList;
    public List<Area> quList;
    public List<String> quStrList;
    private List<Area> allAreaList;
    private int chooseStatus;
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkplan);
        progressDialog = new ProgressDialog(this);
        isShowDialog = true;
        shengList = new ArrayList<>();
        shiList = new ArrayList<>();
        quList = new ArrayList<>();
        shengStrList = new ArrayList<>();
        shiStrList = new ArrayList<>();
        quStrList = new ArrayList<>();
        allAreaList = new ArrayList<>();
        initView();
        initDateTime();
        getDataFromService();
        if (new DbConfig(getApplicationContext()).getAreaList() == null) {
            getAreaFromService();
        } else {
            allAreaList = new DbConfig(getApplicationContext()).getAreaList();
            initArea();
        }
    }

    private void initView() {
        checkplanList=new ArrayList<>();
        checkplanLists=new ArrayList<>();
        date = new StringBuffer();
        diquLayout =findViewById(R.id.diqu_layout);
        backButton = (ImageView) findViewById(R.id.back_button);
        shaixuanDialog=new Dialog(this, R.style.ActionSheetDialogStyle);
        shaixuanInflater= LayoutInflater.from(this).inflate(R.layout.dialog_timecheck,null);
        shaixuanInflater.setMinimumWidth(10000);
        shaixuanDialog.setContentView(shaixuanInflater);
        Window gaojiDialogWindow = shaixuanDialog.getWindow();
        gaojiDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams gaojiLp = gaojiDialogWindow.getAttributes();

        WindowManager wmGaoji = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int heightGaoji = wmGaoji.getDefaultDisplay().getHeight();
        gaojiLp.height = (int) (heightGaoji * 0.8);
        gaojiDialogWindow.setAttributes(gaojiLp);
        shaixuanDialog.setCanceledOnTouchOutside(true);
        initshaixuanView();
        adapter = new MultiTypeAdapter(items);
        listView = findViewById(R.id.check_plan_listview);
        listView.setOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                Log.e(TAG, "onLoadMore: lastPage-" + lastPage);
                Log.e(TAG, "onLoadMore: currentPage- " + currentPage);
                if (lastPage > currentPage){
                    currentPage += 1;
                    isShowDialog = false;
                    getDataFromService();
                }
            }
        });
        register();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.check_plan_refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);
        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkplanLists.clear();
                isShowDialog = false;
                currentPage = 1;
                getDataFromService();
            }
        });
        /**
         * 省市的点击
         */
        RxViewAction.clickNoDouble(shengLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 0;
                        //     getAllAre();
                        showAreaDialog(shengStrList);
                    }
                });
        RxViewAction.clickNoDouble(shiLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 1;
                        Log.e(TAG, "call: 12321--" + shengLayout.getText().toString());
                        if (fromMap) {
                            showAreaDialog(shiStrList);
                        } else {
                            if (shengLayout.getText().toString().equals("请选择省")) {
                                Toast.makeText(CheckplanActivity.this, "请先选择省", Toast.LENGTH_SHORT).show();
                            } else {
                                String currentShengId = "";
                                for (int i = 0; i < shengList.size(); i++) {
                                    if (shengList.get(i).getName().equals(currentChooseSheng)) {
                                        currentShengId = shengList.get(i).getId();
                                    }
                                }

                                initShi(currentShengId);
                            }
                        }
                    }
                });
        RxViewAction.clickNoDouble(quLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 2;
                        Log.e(TAG, "call: 12321--" + shiLayout.getText().toString());
                        if (fromMap) {
                            showAreaDialog(quStrList);
                        } else {
                            if (shiLayout.getText().toString().equals("请选择市")) {
                                Toast.makeText(CheckplanActivity.this, "请先选择市", Toast.LENGTH_SHORT).show();
                            } else {
                                String currentShengId = "";
                                for (int i = 0; i < shengList.size(); i++) {
                                    if (shengList.get(i).getName().equals(currentChooseSheng)) {
                                        currentShengId = shengList.get(i).getId();
                                    }
                                }
                                initShi(currentShengId);
                                String currentshiId = "";
                                Log.e(TAG, "call: "+shiList.size() );
                                for (int i = 0; i < shiList.size(); i++) {
                                    if (shiList.get(i).getName().equals(currentChooseShi)) {
                                        currentshiId = shiList.get(i).getId();
                                    }
                                }
                                initqu(currentshiId);
                                ;
                            }
                        }
                    }
                });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initshaixuanView() {
        allText=shaixuanInflater.findViewById(R.id.all_text);
        yizhouText=shaixuanInflater.findViewById(R.id.yizhou_text);
        yiyueText=shaixuanInflater.findViewById(R.id.yiyue_text);
        sanyueText=shaixuanInflater.findViewById(R.id.sanyue_text);
        bannianText=shaixuanInflater.findViewById(R.id.bannian_text);
        yinianText=shaixuanInflater.findViewById(R.id.yinian_text);
        gaojiStartimeText=shaixuanInflater.findViewById(R.id.gaoji_startime_text);
        gaojiEndtimeText=shaixuanInflater.findViewById(R.id.gaoji_endtime_text);
        shengLayout=shaixuanInflater.findViewById(R.id.sheng_text);
        shiLayout=shaixuanInflater.findViewById(R.id.shi_text);
        quLayout=shaixuanInflater.findViewById(R.id.qu_text);
        allztText=shaixuanInflater.findViewById(R.id.allzt_text);
        daijianchaText=shaixuanInflater.findViewById(R.id.daijiancha_text);
        jinxinghzongText=shaixuanInflater.findViewById(R.id.jinxinghzong_text);
        yiwanchengText=shaixuanInflater.findViewById(R.id.yiwancheng_text);
        chongzhiButton = shaixuanInflater.findViewById(R.id.chongzhi_button);
        findButton=shaixuanInflater.findViewById(R.id.find_button);
        RxViewAction.clickNoDouble(allText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooetime="";
                        allText.setBackgroundResource(R.drawable.bg_text_lan);
                        allText.setTextColor(getResources().getColor(R.color.c12));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_hui);
                        yizhouText.setTextColor(getResources().getColor(R.color.c6));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiyueText.setTextColor(getResources().getColor(R.color.c6));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        sanyueText.setTextColor(getResources().getColor(R.color.c6));
                        yinianText.setBackgroundResource(R.drawable.bg_text_hui);
                        yinianText.setTextColor(getResources().getColor(R.color.c6));
                        bannianText.setBackgroundResource(R.drawable.bg_text_hui);
                        bannianText.setTextColor(getResources().getColor(R.color.c6));
                        gaojiStartimeText.setText("请输入开始时间");
                    }
                });
        RxViewAction.clickNoDouble(yizhouText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        allText.setBackgroundResource(R.drawable.bg_text_hui);
                        allText.setTextColor(getResources().getColor(R.color.c6));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_lan);
                        yizhouText.setTextColor(getResources().getColor(R.color.c12));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiyueText.setTextColor(getResources().getColor(R.color.c6));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        sanyueText.setTextColor(getResources().getColor(R.color.c6));
                        yinianText.setBackgroundResource(R.drawable.bg_text_hui);
                        yinianText.setTextColor(getResources().getColor(R.color.c6));
                        bannianText.setBackgroundResource(R.drawable.bg_text_hui);
                        bannianText.setTextColor(getResources().getColor(R.color.c6));
                        chooetime = getDateStr(CurrentTime,7);
                        gaojiStartimeText.setText("请输入开始时间");
                    }
                });
        RxViewAction.clickNoDouble(yiyueText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        allText.setBackgroundResource(R.drawable.bg_text_hui);
                        allText.setTextColor(getResources().getColor(R.color.c6));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_hui);
                        yizhouText.setTextColor(getResources().getColor(R.color.c6));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_lan);
                        yiyueText.setTextColor(getResources().getColor(R.color.c12));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        sanyueText.setTextColor(getResources().getColor(R.color.c6));
                        yinianText.setBackgroundResource(R.drawable.bg_text_hui);
                        yinianText.setTextColor(getResources().getColor(R.color.c6));
                        bannianText.setBackgroundResource(R.drawable.bg_text_hui);
                        bannianText.setTextColor(getResources().getColor(R.color.c6));
                        chooetime = getDateStr(CurrentTime,30);
                        gaojiStartimeText.setText("请输入开始时间");
                    }
                });
        RxViewAction.clickNoDouble(sanyueText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        allText.setBackgroundResource(R.drawable.bg_text_hui);
                        allText.setTextColor(getResources().getColor(R.color.c6));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_hui);
                        yizhouText.setTextColor(getResources().getColor(R.color.c6));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiyueText.setTextColor(getResources().getColor(R.color.c6));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_lan);
                        sanyueText.setTextColor(getResources().getColor(R.color.c12));
                        yinianText.setBackgroundResource(R.drawable.bg_text_hui);
                        yinianText.setTextColor(getResources().getColor(R.color.c6));
                        bannianText.setBackgroundResource(R.drawable.bg_text_hui);
                        bannianText.setTextColor(getResources().getColor(R.color.c6));
                        chooetime = getDateStr(CurrentTime,90);
                        gaojiStartimeText.setText("请输入开始时间");
                    }
                });
        RxViewAction.clickNoDouble(yinianText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        allText.setBackgroundResource(R.drawable.bg_text_hui);
                        allText.setTextColor(getResources().getColor(R.color.c6));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_hui);
                        yizhouText.setTextColor(getResources().getColor(R.color.c6));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiyueText.setTextColor(getResources().getColor(R.color.c6));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        sanyueText.setTextColor(getResources().getColor(R.color.c6));
                        yinianText.setBackgroundResource(R.drawable.bg_text_lan);
                        yinianText.setTextColor(getResources().getColor(R.color.c12));
                        bannianText.setBackgroundResource(R.drawable.bg_text_hui);
                        bannianText.setTextColor(getResources().getColor(R.color.c6));
                        chooetime = getDateStr(CurrentTime,365);
                        gaojiStartimeText.setText("请输入开始时间");
                    }
                });
        RxViewAction.clickNoDouble(bannianText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        allText.setBackgroundResource(R.drawable.bg_text_hui);
                        allText.setTextColor(getResources().getColor(R.color.c6));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_hui);
                        yizhouText.setTextColor(getResources().getColor(R.color.c6));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiyueText.setTextColor(getResources().getColor(R.color.c6));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        sanyueText.setTextColor(getResources().getColor(R.color.c6));
                        yinianText.setBackgroundResource(R.drawable.bg_text_hui);
                        yinianText.setTextColor(getResources().getColor(R.color.c6));
                        bannianText.setBackgroundResource(R.drawable.bg_text_lan);
                        bannianText.setTextColor(getResources().getColor(R.color.c12));
                        chooetime = getDateStr(CurrentTime,180);
                        gaojiStartimeText.setText("请输入开始时间");
                    }
                });
        RxViewAction.clickNoDouble(allztText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooseStatus=0;
                        allztText.setBackgroundResource(R.drawable.bg_text_lan);
                        allztText.setTextColor(getResources().getColor(R.color.c12));
                        daijianchaText.setBackgroundResource(R.drawable.bg_text_hui);
                        daijianchaText.setTextColor(getResources().getColor(R.color.c6));
                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_hui);
                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c6));
                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiwanchengText.setTextColor(getResources().getColor(R.color.c6));
                    }
                });
        RxViewAction.clickNoDouble(daijianchaText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooseStatus=1;
                        allztText.setBackgroundResource(R.drawable.bg_text_hui);
                        allztText.setTextColor(getResources().getColor(R.color.c6));
                        daijianchaText.setBackgroundResource(R.drawable.bg_text_lan);
                        daijianchaText.setTextColor(getResources().getColor(R.color.c12));
                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_hui);
                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c6));
                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiwanchengText.setTextColor(getResources().getColor(R.color.c6));
                    }
                });
        RxViewAction.clickNoDouble(jinxinghzongText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooseStatus=2;
                        allztText.setBackgroundResource(R.drawable.bg_text_hui);
                        allztText.setTextColor(getResources().getColor(R.color.c6));
                        daijianchaText.setBackgroundResource(R.drawable.bg_text_hui);
                        daijianchaText.setTextColor(getResources().getColor(R.color.c6));
                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_lan);
                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c12));
                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiwanchengText.setTextColor(getResources().getColor(R.color.c6));
                    }
                });
        RxViewAction.clickNoDouble(yiwanchengText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        chooseStatus=3;
                        allztText.setBackgroundResource(R.drawable.bg_text_hui);
                        allztText.setTextColor(getResources().getColor(R.color.c6));
                        daijianchaText.setBackgroundResource(R.drawable.bg_text_hui);
                        daijianchaText.setTextColor(getResources().getColor(R.color.c6));
                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_hui);
                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c6));
                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_lan);
                        yiwanchengText.setTextColor(getResources().getColor(R.color.c12));
                    }
                });
        RxViewAction.clickNoDouble(diquLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        shaixuanDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(gaojiStartimeText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showDataDialog(gaojiStartimeText);
                    }
                });

        RxViewAction.clickNoDouble(gaojiEndtimeText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showDataDialog(gaojiEndtimeText);
                    }
                });
        RxViewAction.clickNoDouble(chongzhiButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        allText.setBackgroundResource(com.haohai.platform.firelibrary.R.drawable.bg_text_lan);
//                        allztText.setTextColor(getResources().getColor(com.haohai.platform.firelibrary.R.color.c12));
//                        daijianchaText.setBackgroundResource(R.drawable.bg_text_lan);
//                        daijianchaText.setTextColor(getResources().getColor(R.color.c12));
//                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_hui);
//                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c6));
//                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_hui);
//                        yiwanchengText.setTextColor(getResources().getColor(R.color.c6));
//                        daijianchaText.setBackgroundResource(R.drawable.bg_text_hui);
//                        daijianchaText.setTextColor(getResources().getColor(R.color.c6));
//                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_hui);
//                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c6));
//                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_hui);
//                        yiwanchengText.setTextColor(getResources().getColor(R.color.c6));
//                        gaojiStartimeText.setText("请输入开始时间");
//                        gaojiEndtimeText.setText("请输入结束时间");



                        chooetime="";
                        allText.setBackgroundResource(R.drawable.bg_text_lan);
                        allText.setTextColor(getResources().getColor(R.color.c12));
                        yizhouText.setBackgroundResource(R.drawable.bg_text_hui);
                        yizhouText.setTextColor(getResources().getColor(R.color.c6));
                        yiyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiyueText.setTextColor(getResources().getColor(R.color.c6));
                        sanyueText.setBackgroundResource(R.drawable.bg_text_hui);
                        sanyueText.setTextColor(getResources().getColor(R.color.c6));
                        yinianText.setBackgroundResource(R.drawable.bg_text_hui);
                        yinianText.setTextColor(getResources().getColor(R.color.c6));
                        bannianText.setBackgroundResource(R.drawable.bg_text_hui);
                        bannianText.setTextColor(getResources().getColor(R.color.c6));
                        gaojiStartimeText.setText("请输入开始时间");
                        gaojiEndtimeText.setText("请输入结束时间");

                        chooseStatus=0;
                        allztText.setBackgroundResource(R.drawable.bg_text_lan);
                        allztText.setTextColor(getResources().getColor(R.color.c12));
                        daijianchaText.setBackgroundResource(R.drawable.bg_text_hui);
                        daijianchaText.setTextColor(getResources().getColor(R.color.c6));
                        jinxinghzongText.setBackgroundResource(R.drawable.bg_text_hui);
                        jinxinghzongText.setTextColor(getResources().getColor(R.color.c6));
                        yiwanchengText.setBackgroundResource(R.drawable.bg_text_hui);
                        yiwanchengText.setTextColor(getResources().getColor(R.color.c6));



                        //shengLayout.setText("请选择省");
                        //shiLayout.setText("请选择市");
                        quLayout.setText("请选择区");
//                        chooseStatus=0;
//                        chooetime="";
                    }
                });
        RxViewAction.clickNoDouble(findButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        selectDataFromService();
                    }
                });
    }
    private void showDataDialog(final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }

                date.append(String.valueOf(year));
                if (month < 9){
                    date.append("-0").append(String.valueOf(month + 1));
                }else {
                    date.append("-").append(String.valueOf(month + 1));
                }
                if (day <10){
                    date.append("-0").append(String.valueOf(day));
                } else {
                    date.append("-").append(String.valueOf(day));
                }
                textView.setText(date);
                chooetime="";

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog  dialog = builder.create();
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
        datePicker.setMinDate(starTimre);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int monthnow=month+1;
        CurrentTime = year + "-" + monthnow + "-" + day;
        Log.e(TAG, "initDateTime: "+CurrentTime );
    }
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
    private void getDataFromService() {
        checkplanLists.clear();
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto=new JSONObject();
            jsonObject.put("dto",dto);
            jsonObject.put("limit",pageSize);
            jsonObject.put("page",currentPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan/selectCheckPlan");
        params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("isAndroid","1");
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        Log.e(TAG, "getDataFromService: " + params);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        allTotal = getJsonObj.getInt("totalSize");
                        lastPage = allTotal / pageSize;
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                        Gson gson = new Gson();
                        checkplanList.clear();
                        checkplanList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<CheckPlanList>>() {
                        }.getType());
                        checkplanLists.addAll(checkplanList);
                        Log.e(TAG, "onSuccess: "+checkplanList.get(0).planResourceDTOS.size() );
                        initData();
                        swipeRefreshLayout.setRefreshing(false);
                    }else {
                        Toast.makeText(CheckplanActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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
        adapter.notifyDataSetChanged();
        if (checkplanLists.size()==0&&currentPage == 1){
            items.add(new Empty("暂无数据"));
        }else {
            for (int i = 0; i < checkplanLists.size(); i++) {
                items.add(checkplanLists.get(i));
                if (checkplanLists.get(i).getPlanResourceDTOS().size()>0) {
                    for (int j = 0; j < checkplanLists.get(i).getPlanResourceDTOS().size(); j++) {
                        Log.e(TAG, ": "+checkplanLists.get(i).getPlanResourceDTOS().get(j).getStatus() );
                        items.add(checkplanLists.get(i).getPlanResourceDTOS().get(j));
                    }
                }
            }
        }
        if (currentPage > 0){
            if (lastPage > currentPage) {    //显示下拉加载
                items.add(new Bottom("加载更多..."));
            } else {
                items.add(new Bottom("全部加载完成"));
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
    private void register() {
        CheckPlanListViewBinder checkPlanListViewBinder = new CheckPlanListViewBinder();
        checkPlanListViewBinder.setListener(this);
        CheckPlanListStationViewBinder checkPlanListStationViewBinder =new CheckPlanListStationViewBinder();
        checkPlanListStationViewBinder.setListener(this);
        adapter.register(CheckPlanList.class, checkPlanListViewBinder);
        adapter.register(CheckPlanList.PlanResourceDTOSFirejd.class,checkPlanListStationViewBinder);
        adapter.register(Empty.class,new EmptyViewBinder());
        adapter.register(Bottom.class,new BottomViewBinder());
    }

    @Override
    public void onChecklistItemClickListener(CheckPlanList.PlanResourceDTOSFirejd planResourceDTOSFirejd,String type) {
        if (type=="jiancha") {
            Intent intent = new Intent(getApplicationContext(), ResourceCheckActivity.class);
            intent.putExtra("planid", planResourceDTOSFirejd.getPlanId());
            intent.putExtra("resourceID", planResourceDTOSFirejd.getId());
            intent.putExtra("resourceName", planResourceDTOSFirejd.getName());
            intent.putExtra("resourcegird", planResourceDTOSFirejd.getGridName());
            intent.putExtra("endTime", planResourceDTOSFirejd.getEndTime());
            intent.putExtra("girdno", planResourceDTOSFirejd.getgridNo());
            intent.putExtra("resourcetype", planResourceDTOSFirejd.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);
        }else if (type=="shenhe"){
            Intent intent = new Intent(getApplicationContext(), ResourceshenheActivity.class);
            intent.putExtra("planid", planResourceDTOSFirejd.getPlanId());
            intent.putExtra("resourceID", planResourceDTOSFirejd.getId());
            intent.putExtra("resourceName", planResourceDTOSFirejd.getName());
            intent.putExtra("resourcegird", planResourceDTOSFirejd.getGridName());
            intent.putExtra("endTime", planResourceDTOSFirejd.getEndTime());
            intent.putExtra("girdno", planResourceDTOSFirejd.getgridNo());
            intent.putExtra("resourcetype", planResourceDTOSFirejd.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);
        }else if (type=="zhengzhi"){
            Intent intent = new Intent(getApplicationContext(), ResourcezhengzhiActivity.class);
            intent.putExtra("planid", planResourceDTOSFirejd.getPlanId());
            intent.putExtra("resourceID", planResourceDTOSFirejd.getId());
            intent.putExtra("resourceName", planResourceDTOSFirejd.getName());
            intent.putExtra("resourcegird", planResourceDTOSFirejd.getGridName());
            intent.putExtra("girdno", planResourceDTOSFirejd.getgridNo());
            intent.putExtra("resourcetype", planResourceDTOSFirejd.getCheckType());
            startActivityForResult(intent, ORDER_CHANGE);
        }
    }
    @Override
    public void onPlanItemClickListener(String id) {
        Intent intent = new Intent(getApplicationContext(), CheckDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
    @Override
    public void onOrderCheckClickListener(CheckPlanList checkPlanList) {

    }
     /**
     * 显示地区选择的dialog
     */
    private void showAreaDialog(List<String> strList) {
        View areaView = LayoutInflater.from(this).inflate(com.haohai.platform.firelibrary.R.layout.dialog_area, null);
        areaWy = ((WheelView) areaView.findViewById(com.haohai.platform.firelibrary.R.id.wheel_view_area));
        areaWy.setIsLoop(false);
        if (currentChooseArea == 0) {
            areaWy.setItems(strList, shengSelectIndex);//init selected position is 0 初始选中位置为0
        } else if (currentChooseArea == 1) {
            areaWy.setItems(strList, shiSelectIndex);//init selected position is 0 初始选中位置为0
        } else {
            areaWy.setItems(strList, quSelectIndex);
        }

        areaWy.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                fromMap = false;
                if (currentChooseArea == 0) {   //选择省
                    isChooseSheng = true;
                    currentChooseSheng = areaWy.getSelectedItem();
                    shengSelectIndex = areaWy.getSelectedPosition();
                    shengLayout.setText(currentChooseSheng);
                    //选择剩要初始化市
                    shiLayout.setText("请选择市");
                    currentChooseShi = "请选择市";
                    shiSelectIndex = 0;
                    quLayout.setText("请选择市");
                    currentChooseQu = "请选择市";
                    quSelectIndex = 0;
                } else if (currentChooseArea == 1) {                          //选择市
                    currentChooseShi = areaWy.getSelectedItem();
                    shiSelectIndex = areaWy.getSelectedPosition();
                    shiLayout.setText(currentChooseShi);
                    quLayout.setText("请选择市");
                    currentChooseQu = "请选择市";
                    quSelectIndex = 0;
                } else {
                    currentChooseQu = areaWy.getSelectedItem();
                    quSelectIndex = areaWy.getSelectedPosition();
                    quLayout.setText(currentChooseQu);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择区域")
                .setView(areaView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String areStr = "";
                        String area = areaWy.getSelectedItem();


                    }
                })
                .show();
    }
    private void initqu(String currentshiId) {
        quList.clear();
        quStrList.clear();
        quStrList.add("请选择区");
        for (int i = 0; i < allAreaList.size(); i++) {
            if (allAreaList.get(i).getParentId().equals(currentshiId)) {
                quList.add(allAreaList.get(i));
                quStrList.add(allAreaList.get(i).getName());
            }
        }

        showAreaDialog(quStrList);
    }

    private void initShi(String currentShengId) {
        shiList.clear();
        shiStrList.clear();
        shiStrList.add("请选择市");
        for (int i = 0; i < allAreaList.size(); i++) {
            if (allAreaList.get(i).getParentId().equals(currentShengId)) {
                shiList.add(allAreaList.get(i));
                shiStrList.add(allAreaList.get(i).getName());
            }
        }

        //showAreaDialog(shiStrList);
    }

    private void initArea() {
        shengStrList.clear();
        shengList.clear();
        shengStrList.add("请选择省");
        for (int i = 0; i < allAreaList.size(); i++) {
            if (allAreaList.get(i).getLevel().equals("1")) {
                shengList.add(allAreaList.get(i));
                shengStrList.add(allAreaList.get(i).getName());
            }
        }
    }
    /**
     * 获取区域数据
     */
    private void getAreaFromService() {
        showDialogProgress(progressDialog, "数据获取中...");
        JSONObject jsonObject = new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/sysArea/getAllSysArea");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        Log.i(TAG, "getAreaFromService: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:diqu -- " + result);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    allAreaList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        String id = object.getString("id");
                        String name = object.getString("name");
                        String parentId = object.getString("parentId");
                        String level = object.getString("level");
                        String createTime = object.getString("createTime");
                        Area area = new Area(id, name, parentId, createTime, level);
                        allAreaList.add(area);
                    }
                    DbConfig dbConfig = new DbConfig(getApplicationContext());
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.saveOrUpdate(allAreaList);
                    } catch (DbException e) {

                    }

                    initArea();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" + ex.toString());
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
    private void selectDataFromService() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"加载中...");
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            JSONObject dto = new JSONObject();
            jsonObject.put("dto", dto);
            jsonObject.put("limit",200);
            jsonObject.put("page",1);
            if (chooetime!=""){
                if (!gaojiStartimeText.getText().toString().equals("请输入开始时间")){
                    dto.put("startTime", gaojiStartimeText.getText().toString()+" 00:00:00");
                }else {
                    dto.put("startTime", chooetime + " 00:00:00");
                }
            }else {
                if (!gaojiStartimeText.getText().toString().equals("请输入开始时间")){
                    dto.put("startTime", gaojiStartimeText.getText().toString()+" 00:00:00");
                }
            }
            if (chooseStatus!=0){
                dto.put("status", chooseStatus);
            }
            if (!gaojiEndtimeText.getText().toString().equals("请输入结束时间")){
                dto.put("endTime", gaojiEndtimeText.getText().toString()+" 00:00:00");
            }
            if (!quLayout.getText().toString().equals("")){
                dto.put("gridName", quLayout.getText().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan/selectCheckPlan");
        params.setAsJsonContent(true);
        params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("isAndroid","1");
        Log.e(TAG, "getDataFromService: " + params);
        Log.e(TAG, "getDataFromService: " + jsonObject.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        JSONObject getJsonObj = data.getJSONObject(0);//获取json数组中的第一项
                        JSONArray dataList = getJsonObj.getJSONArray("dataList");
                            Gson gson = new Gson();
                            checkplanList.clear();
                            checkplanList = gson.fromJson(String.valueOf(dataList), new TypeToken<List<CheckPlanList>>() {
                            }.getType());
                            checkplanLists.clear();
                            checkplanLists.addAll(checkplanList);
                            initData();
                            swipeRefreshLayout.setRefreshing(false);
                            shaixuanDialog.dismiss();
                    }else {
                        Toast.makeText(CheckplanActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
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
    //Day:日期字符串例如 2015-3-10  Num:需要减少的天数例如 7
    public static String getDateStr(String day,int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() - (long)Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:requestCode " + requestCode);
        Log.e(TAG, "onActivityResult:resultCode " +  resultCode);
        if (resultCode == ORDER_CHANGE) {
            Log.e(TAG, "onActivityResult: 111" );
            checkplanList.clear();
            isShowDialog = true;
            getDataFromService();
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getDataFromService();
    }
}