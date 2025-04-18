package com.haohai.platform.mapmodel.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.utils.LinePathView;
import com.haohai.platform.mapmodel.bean.AddCompresive;
import com.haohai.platform.mapmodel.bean.planResourceDTOS;
import com.haohai.platform.mapmodel.multitype.ChooseImage;
import com.haohai.platform.mapmodel.multitype.CompresiveStationViewBinder;
import com.haohai.platform.mapmodel.multitype.Empty;
import com.haohai.platform.mapmodel.multitype.EmptyViewBinder;
import com.haohai.platform.mapmodel.multitype.checkuser;
import com.haohai.platform.mapmodel.multitype.people;
import com.haohai.platform.mapmodel.multitype.peopleViewBinder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.Area;
import com.ruyiruyi.rylibrary.db.CheckField;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.image.ImageUtils;
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class CompresiveCheckActivity extends HhBaseActivity implements CompresiveStationViewBinder.OnOneBodyItemClick , peopleViewBinder.OnPeopleLsitItemClick,DatePicker.OnDateChangedListener {

    private static final String TAG = CompresiveCheckActivity.class.getSimpleName();
    private LinePathView mPathView;
    private TextView clearQianmingView;
    private TextView postDataButton;
    private String access_token;
    private String fullPath = "";
    private String fullOnePath = "";
    private String fullImagePath = "";
    private String fullImagePath2 = "";
    public List<String> fullList;
    private ProgressDialog progressDialog;
    private ImageView backButton;
    private Dialog qianmingDialog;
    private View qianmingInflater;
    private TextView qianmingButton;
    private String checkTime;
    private boolean hasOneQianming = false;
    public List<Uri> uriChooseList;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private EditText beizhuEdit;
    private List<CheckField> checkFieldList;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private List<Object> photoItems = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private CompresiveStationViewBinder compresiveStationViewBinder;
    private Paint paint;
    private Dialog qianmingOneDialog;
    private View qianmingOneInflater;
    private LinePathView mPathOneView;
    private TextView clearQianmingOneView;
    private TextView qianmingOneButton;
    private TextView qianmingPassButton;
    private List<people> peopleList;
    private boolean hasNet = true;
    private List<ChooseImage> list = new ArrayList<>();
    private boolean currentChoose = true;
    private DbManager db;
    private EditText resourceName;
    private TextView resourceGird;
    private TextView resourceEndTime;
    private List<Object> peopleItems = new ArrayList<>();
    private MultiTypeAdapter resourceAdapter;
    private Dialog peopleListDialog;
    private View peopleListInflater;
    private RecyclerView peopleListView;
    private TextView okBtn;
    private TextView zzrText;
    private ArrayList zzrName =new ArrayList();
    private List<AddCompresive.ImgsFirejd> imgsFirejds =new ArrayList<>();
    private List<checkuser> checkusers =new ArrayList<>();
    private List<Object> imglist=new ArrayList<>();;
    private Dialog shaixuanDialog;
    private View shaixuanInflater;
    private TextView shengLayout;
    private TextView shiLayout;
    private TextView quLayout;
    private TextView yesButton;
    public int currentChooseArea = 0;  //当前在选择省还是市   0选择省  1选择市
    private WheelView areaWy;
    public int shengSelectIndex = 0;
    public int shiSelectIndex = 0;
    public int quSelectIndex = 0;
    public boolean isChooseSheng = false;
    public String currentChooseSheng = "";
    public String currentChooseShi = "";
    public String currentChooseQu = "";
    public List<Area> shengList;
    public List<String> shengStrList;
    public List<Area> shiList;
    public List<String> shiStrList;
    public List<Area> quList;
    public List<String> quStrList;
    private List<Area> allAreaList;
    private int choose1 = 0;
    private StringBuffer date;
    private int year;
    private int month;
    private int day;
    private String wanggeid;
    private TextView addResource;
    private List<planResourceDTOS> planResourceDTOSList = new ArrayList<>();
    private ImageView listButton;
    private String id;
    private TextView jcqkEdit;

    class ImagePostThread extends Thread {
        @Override
        public void run() {
            super.run();
            postQianmingService();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprehensive_check);
        progressDialog = new ProgressDialog(this);
        access_token = new DbConfig(this).getUser().getToken();
        db = new DbConfig(this).getDbManager();
        uriChooseList = new ArrayList<>();
        checkFieldList = new ArrayList<>();
        fullList = new ArrayList<>();
        peopleList=new ArrayList<>();
        shengList = new ArrayList<>();
        shiList = new ArrayList<>();
        quList = new ArrayList<>();
        shengStrList = new ArrayList<>();
        shiStrList = new ArrayList<>();
        quStrList = new ArrayList<>();
        allAreaList = new ArrayList<>();
        date=new StringBuffer();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        initView();
        //配置点击查看大图
        initImageLoader();
        getAreaFromService();
        initDateTime();
    }

    private void initView() {
        beizhuEdit = (EditText) findViewById(R.id.beizhu_edit);
        backButton = (ImageView) findViewById(R.id.back_button);
        resourceName=findViewById(R.id.resource_name);
        resourceGird=findViewById(R.id.resource_gird);
        resourceEndTime=findViewById(R.id.end_time);
        zzrText=findViewById(R.id.zzr_text);
        listButton=findViewById(R.id.list_button);
        addResource=findViewById(R.id.add_resource);
        listView = (RecyclerView) findViewById(R.id.list_view);
        jcqkEdit=findViewById(R.id.jcqk_edit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        listView.setNestedScrollingEnabled(false);
        adapter = new MultiTypeAdapter(items);
        register();
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

        postDataButton = (TextView) findViewById(R.id.post_data_view);

        qianmingDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        qianmingInflater = LayoutInflater.from(this).inflate(R.layout.dialog_qianmings,null);
        qianmingInflater.setMinimumWidth(10000);
        mPathView = ((LinePathView) qianmingInflater.findViewById(R.id.path_views));
        clearQianmingView = ((TextView) qianmingInflater.findViewById(R.id.clear_qianming_view));
        qianmingButton = ((TextView) qianmingInflater.findViewById(R.id.qianming_button));
        qianmingDialog.setContentView(qianmingInflater);
        Window qianmingDialogWindow = qianmingDialog.getWindow();
        qianmingDialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lpSearch = qianmingDialogWindow.getAttributes();
        qianmingDialogWindow.setAttributes(lpSearch);
        qianmingDialog.setCanceledOnTouchOutside(true);

        mPathView.clear();
        mPathView.setBackColor(Color.WHITE);
        mPathView.setPaintWidth(20);
        mPathView.setPenColor(Color.BLACK);
        mPathView.setVisibility(View.VISIBLE);

        /**
         * 整治人列表 dialog
         */
        peopleListDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        peopleListInflater = LayoutInflater.from(this).inflate(R.layout.dialog_people_list, null);
        peopleListInflater.setMinimumWidth(10000);
        peopleListView = ((RecyclerView) peopleListInflater.findViewById(R.id.people_listview));
        okBtn=peopleListInflater.findViewById(R.id.ok_btn);
        peopleListDialog.setContentView(peopleListInflater);
        Window resourceListWindow = peopleListDialog.getWindow();
        resourceListWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams resourceListLp = resourceListWindow.getAttributes();
        WindowManager resourcewm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int resourceheight = resourcewm.getDefaultDisplay().getHeight();
        resourceListLp.height = (int) (resourceheight * 0.8);
        resourceListWindow.setAttributes(resourceListLp);
        peopleListDialog.setCanceledOnTouchOutside(true);
        LinearLayoutManager resourcelinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        peopleListView.setLayoutManager(resourcelinearLayoutManager);
        resourceAdapter = new MultiTypeAdapter(peopleItems);
        peopleViewBinder peopleViewBinder = new peopleViewBinder();
        peopleViewBinder.setListener(this);
        resourceAdapter.register(people.class, peopleViewBinder);
        peopleListView.setAdapter(resourceAdapter);
        assertHasTheSameAdapter(peopleListView, resourceAdapter);
        /**
         * 所属网格 dialog
         */
        shaixuanDialog=new Dialog(this, R.style.ActionSheetDialogStyle);
        shaixuanInflater= LayoutInflater.from(this).inflate(R.layout.dialog_wangge,null);
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
        shengLayout=shaixuanInflater.findViewById(R.id.sheng_text);
        shiLayout=shaixuanInflater.findViewById(R.id.shi_text);
        quLayout=shaixuanInflater.findViewById(R.id.qu_text);
        yesButton=shaixuanInflater.findViewById(R.id.yes_button);
        //第一次签名
        RxViewAction.clickNoDouble(qianmingButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mPathView.getTouched()) {
                            try {
                                Date date = new Date();

                                String time = date.toLocaleString();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);

                                checkTime = dateFormat.format(date);

                                //保存
                                //mPathView.save(getObbDir().getAbsolutePath() + checkTime +"qm.png", true, 10);
                                //保存 bingo
                                Bitmap bitMap = mPathView.getBitMap();
                                Log.e(TAG, "initView: MediaStore.Images.Media.insertImage(getContentResolver(), bitMap, null,null) = " + MediaStore.Images.Media.insertImage(getContentResolver(), bitMap, null,null) );
                                signUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitMap, null,null));

                                qianmingDialog.dismiss();
                                //qianmingOneDialog.show();
                                if (list.size() > 0){
                                    showDialogProgress(progressDialog,"正在上传");
                                    new ImagePostThread().start();
                                   /* for (int i = 0; i < list.size(); i++) {
                                        postMoreImagesService(i);
                                    }*/
                                    // postImageService();    //上传图片
                                }else {
                                    Log.e(TAG, "postdata--4" );
                                    showDialogProgress(progressDialog,"正在上传");
                                    postQianmingService();//上传签名
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CompresiveCheckActivity.this, "您没有签名~请签名后提交", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        clearQianmingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPathView.clear();
                mPathView.setBackColor(Color.WHITE);
                mPathView.setPaintWidth(20);
                mPathView.setPenColor(Color.BLACK);
            }
        });
        RxViewAction.clickNoDouble(postDataButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        qianmingDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(zzrText)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getPerpleListFromService1();
                    }
                });
        RxViewAction.clickNoDouble(okBtn)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        zzrName.clear();
                        checkusers.clear();
                        for (int i = 0; i <peopleList.size() ; i++) {
                            if (peopleList.get(i).isCheck()){
                                zzrName.add(peopleList.get(i).getFullName());
                                checkusers.add(new checkuser("",3,2,peopleList.get(i).getId(),peopleList.get(i).getFullName()));
                            }
                        }
                        zzrText.setText(zzrName.toString());
                        peopleListDialog.dismiss();
                        Log.e(TAG, "call: "+zzrName.toString() );
                    }
                });
        RxViewAction.clickNoDouble(resourceGird)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        shaixuanDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(yesButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        resourceGird.setText(currentChooseQu);
                        shaixuanDialog.dismiss();
                        for (int i = 0; i < quList.size(); i++) {
                            if (quList.get(i).getName().equals(currentChooseQu)) {
                                wanggeid=quList.get(i).getId();
                            }
                        }
                        Log.e(TAG, "call: "+wanggeid );
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
                            if (shengLayout.getText().toString().equals("请选择省")) {
                                Toast.makeText(CompresiveCheckActivity.this, "请先选择省", Toast.LENGTH_SHORT).show();
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
                });
        RxViewAction.clickNoDouble(quLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseArea = 2;
                        Log.e(TAG, "call: 12321--" + shiLayout.getText().toString());

                            if (shiLayout.getText().toString().equals("请选择市")) {
                                Toast.makeText(CompresiveCheckActivity.this, "请先选择市", Toast.LENGTH_SHORT).show();
                            } else {
                                String currentshiId = "";
                                for (int i = 0; i < shiList.size(); i++) {
                                    if (shiList.get(i).getName().equals(currentChooseShi)) {
                                        currentshiId = shiList.get(i).getId();
                                    }
                                }
                                initqu(currentshiId);
                        }
                    }
                });
        RxViewAction.clickNoDouble(resourceEndTime)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showDataDialog();
                    }
                });
        RxViewAction.clickNoDouble(addResource)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivityForResult(new Intent(getApplicationContext(), AddStationActivity.class),2);
                    }
                });
        RxViewAction.clickNoDouble(listButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivityForResult(new Intent(getApplicationContext(), ComprehensivecheckListActivity.class),2);
                    }
                });
    }

    private void register() {
        compresiveStationViewBinder = new CompresiveStationViewBinder();
        compresiveStationViewBinder.setListener(this);
        adapter.register(planResourceDTOS.class, compresiveStationViewBinder);
        adapter.register(Empty.class,new EmptyViewBinder());
    }

    //保存 bingo
    private Uri signUri;

    /**
     * 第一个人签名
     */
    private void postQianmingService() {

        //String qianmingUrlStr = getObbDir().getAbsolutePath() + checkTime +"qm.png";
        //Bitmap qmBitmap = BitmapFactory.decodeFile(qianmingUrlStr);
        //保存 bingo
        Bitmap qmBitmap = null;
        try {
            qmBitmap = ImageUtils.getBitmapFormUri(getApplicationContext(), signUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String qmSTR = ImageUtils.savePhoto(qmBitmap,this.getObbDir().getAbsolutePath() + "", checkTime + "qm");

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_HUAWEI + "oa/api/workReport/fileUploadAnByNotToken");
        params.setAsJsonContent(true);
        params.setMultipart(true);    //以表单得形式上传  文件上传必须要
        // params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("file", new File(qmSTR),null,qmSTR);
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "postDataService: ---" +"qmSTR " + qmSTR  );
        Log.e(TAG, "反馈---" + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if (code.equals("200")){
                        JSONObject data = jsonObject1.getJSONObject("data");
                        String imgStr = data.getString("all");

                        //网络获取数据
                        // postDataToService();
                        //本地数据
                        imgsFirejds.add(new AddCompresive.ImgsFirejd(imgStr,2));
                        postDataToServiceFromDb();
                    }else {
                        Toast.makeText(CompresiveCheckActivity.this, "签名上传失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: "+ex );
                hasNet = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });

    }

    private void postDataToServiceFromDb() {
        if (resourceEndTime.getText().equals("选择截止时间")){
            Toast.makeText(this, "请选择截止时间", Toast.LENGTH_SHORT).show();
            return;
        }
        AddCompresive addCompresive =new AddCompresive();
        addCompresive.setCheckStationCount(planResourceDTOSList.size());
        addCompresive.setCheckUserName(new DbConfig(this).getUser().getFullName());
        addCompresive.setCheckuserVOS(checkusers);
        addCompresive.setDescription(beizhuEdit.getText().toString());
        addCompresive.setStartTime(checkTime.replace("T"," "));
        addCompresive.setEndTime(resourceEndTime.getText().toString()+" 00:00:00");
        addCompresive.setGridName(resourceGird.getText().toString());
        addCompresive.setGridNo(wanggeid);
        addCompresive.setImgs(imgsFirejds);
        addCompresive.setName(resourceName.getText().toString());
        addCompresive.setPlanResourceDTOS(planResourceDTOSList);
        addCompresive.setType(3);
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(addCompresive);
        Log.e(TAG, "postDataToServiceFromDb: "+json1 );
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan/installCheckPlan" );
        params.setBodyContent(json1);
        params.addHeader("Authorization","bearer " + access_token);
        params.addHeader("networktype","Internet");
        params.setConnectTimeout(10000);
        Log.e(TAG, "postDataToServiceFromDb---" + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --数据上传成功--" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        Toast.makeText(CompresiveCheckActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

                        if (hasOneQianming){
                            qianmingOneDialog.dismiss();
                        }else {
                            qianmingDialog.dismiss();
                        }
                        //postResourceToServiceFromDb();
                        finish();
                    } else {
                        Toast.makeText(CompresiveCheckActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        imgsFirejds.clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" +ex.toString());
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
    private void postResourceToServiceFromDb() {
        Gson gson1 = new Gson();
        String json1 = gson1.toJson(planResourceDTOSList);
        Log.e(TAG, "postDataToServiceFromDb: "+json1 );
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/plan/saveResourceItem" );
        params.setBodyContent(json1);
        params.addHeader("Authorization","bearer " + access_token);
        params.setConnectTimeout(10000);
        Log.e(TAG, "postDataToServiceFromDb---" + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --数据上传成功--" + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        Toast.makeText(CompresiveCheckActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

                        if (hasOneQianming){
                            qianmingOneDialog.dismiss();
                        }else {
                            qianmingDialog.dismiss();
                        }
                        finish();
                    }else if (code.equals("403")&&message.equals("没有指定资源项的特权")){
                        Toast.makeText(CompresiveCheckActivity.this, "您没有权限", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CompresiveCheckActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        imgsFirejds.clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" +ex.toString());
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
    public void showDialogProgress(ProgressDialog dialog, String message) {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);
        dialog.show();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private void getPerpleListFromService1() {
        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("gridNo", girdno);
//        } catch (JSONException e) {
//        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/user/list");
        params.setAsJsonContent(true);
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
                        Gson gson = new Gson();
                        peopleList.clear();
                        peopleList = gson.fromJson(String.valueOf(data), new TypeToken<List<people>>() {
                        }.getType());

                        for (int i = 0; i < peopleList.size(); i++) {
                            peopleList.get(i).setCheck(false);
                        }
                        initPeopleListData();
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
    private void initPeopleListData() {
        peopleItems.clear();
        for (int i = 0; i < peopleList.size(); i++) {
            peopleItems.add(peopleList.get(i));
        }
        assertAllRegistered(resourceAdapter, peopleItems);
        resourceAdapter.notifyDataSetChanged();
        peopleListDialog.show();
    }
    @Override
    public void onChecklistItemClickListener(planResourceDTOS planResourceDTOS) {
        Iterator<planResourceDTOS> iterator = planResourceDTOSList.iterator();
        while (iterator.hasNext()) {
            planResourceDTOS integer = iterator.next();
            if (integer.getName().equals(planResourceDTOS.getName()))
                iterator.remove();
        }
        initstationData();
    }
    @Override
    public void onPeopleListItemClickListener(people people) {
        for (int i = 0; i <peopleList.size() ; i++) {
            if (peopleList.get(i).getId().equals(people.getId())){
                if (peopleList.get(i).isCheck()){
                    peopleList.get(i).setCheck(false);
                }else {
                    peopleList.get(i).setCheck(true);
                }
            }
        }
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

        showAreaDialog(shiStrList);
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
    private void showDataDialog() {
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

                resourceEndTime.setText(date);


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
    }
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
    private void initstationData() {
        items.clear();
        if (planResourceDTOSList.size()==0){
            items.add(new Empty(""));
        }else {
            for (int i = 0; i < planResourceDTOSList.size(); i++) {
                items.add(planResourceDTOSList.get(i));
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult:"+resultCode);
        if (requestCode==2&& resultCode == 2){
            planResourceDTOSList.add((planResourceDTOS) data.getSerializableExtra("backinfo"));
            Log.e(TAG, "onActivityResult: "+ planResourceDTOSList.get(0).getItems().size());
            jcqkEdit.setText("共"+ planResourceDTOSList.get(0).getItems().size()+"检查点");
            initstationData();
        }
    }
}