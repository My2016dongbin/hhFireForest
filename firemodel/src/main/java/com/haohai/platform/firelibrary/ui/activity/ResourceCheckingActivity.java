package com.haohai.platform.firelibrary.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.model.Pictures;
import com.haohai.platform.firelibrary.ui.model.ResourceChecking;
import com.haohai.platform.firelibrary.ui.view.MNCTransparentDialog;
import com.ruyiruyi.rylibrary.utils.LinePathView;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.CheckField;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.image.ImageUtils;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.utils.GifSizeFilter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import rx.functions.Action1;

@Route(path = RouteUtils.ResourceChecking)
public class ResourceCheckingActivity extends HhBaseActivity {
    private static final String TAG = ResourceCheckingActivity.class.getSimpleName();
    private ActionBar actionBar;
    private TextView tv_name;
    private TextView tv_grid;
    private TextView tv_rowscreen;
    private LinearLayout ll_items;
    private LinearLayout ll_pictures;
    private FrameLayout fl_worker;
    private FrameLayout fl_enddate;
    private ScrollView sv_out;
    private TextView tv_enddate;
    private EditText et_remark;
    private TextView tv_worker;
    private Button btn_left;
    private Button btn_right;
    private Button btn_submit;
    private LinePathView path_view;
    private ProgressDialog progressDialog;
    private final boolean isShowDialog = true;
    private String endDateStr = "";

    @Autowired
    String token;

    private String id;
    private String endDate;
    private int checkType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_checking);
        ARouter.getInstance().inject(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        endDate = intent.getStringExtra("endDate");
        checkType = intent.getIntExtra("checkType",5);

        progressDialog = new ProgressDialog(this);


        initView();
        initPictures();
        getPerpleListFromService1();
        initCheckFieldIntoDb();
    }



    private List<CheckField> checkFieldList = new ArrayList<>();
    private void initCheckFieldIntoDb() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResourceItemHistory/selectResourceItem");
        params.setConnectTimeout(20000);
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().token);
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
                        checkFieldList.clear();
                        try {
                            JSONObject object = data.getJSONObject(0);
                            JSONArray datachild = object.getJSONArray("child");
                            for (int j = 0; j <datachild.length() ; j++) {
                                JSONObject datachild1=datachild.getJSONObject(j);
                                String createUser = datachild1.optString("createUser");
                                String updateUser = datachild1.optString("updateUser");
                                String createTime = datachild1.optString("createTime");
                                String updateTime = datachild1.optString("updateTime");
                                String id = datachild1.optString("id");
                                String resourceType = datachild1.optString("resourceType");
                                String code1 = datachild1.optString("code");
                                String name = datachild1.optString("name");
                                String fieldType = datachild1.optString("fieldType");
                                String description = datachild1.optString("description");
                                String groupId = datachild1.optString("groupId");
                                int status = datachild1.optInt("status");
                                CheckField checkField = new CheckField(id, code1, createTime, createUser, description, fieldType, groupId, name, resourceType, updateTime, updateUser,status);
                                checkFieldList.add(checkField);
                            }
                            if (checkFieldList.size() > 0) {
                                initTest();
                            }else {

                            }
                        }catch (Exception e){
                        }

                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();
                        try {
                            Log.e(TAG, "onSuccess: team11" );
                            db.saveOrUpdate(checkFieldList);
                        } catch (DbException e) {
                            e.printStackTrace();
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


    private List<Pictures> picturesList = new ArrayList<>();
    private int maxPicture = 5;
    private int picNumber = 0;//当前有几张
    private void initPictures() {
        ll_pictures.removeAllViews();
        //去空
        for (int i = 0; i < picturesList.size(); i++) {
            if(picturesList.get(i).getUri() == null){
                picturesList.remove(i);
            }
        }
        //补空
        if(picturesList.size() < maxPicture){
            picturesList.add(new Pictures());
        }
        //判断有几张图片
        if(picturesList.size() < maxPicture){
            picNumber = picturesList.size()-1;
        }else{
            if(picturesList.get(maxPicture-1).getUri() == null){
                picNumber = maxPicture-1;
            }else{
                picNumber = maxPicture;
            }
        }

        for (int i = 0; i < picturesList.size(); i++) {
            int current = i;
            if(picturesList.get(i).getUri() == null){
                //空添加
                View view = View.inflate(this, R.layout.pictures_item_empty, null);
                ImageView iv_empty = view.findViewById(R.id.iv_empty);
                RxViewAction.clickNoDouble(iv_empty).subscribe(unused -> {
                    int size = maxPicture - picNumber;
                    Matisse.from(ResourceCheckingActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .capture(true)
                            .captureStrategy(
                                    new CaptureStrategy(true,"com.haohai.platform.fireforestplatform")
                            )
                            .maxSelectable(size)
                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                            .gridExpectedSize(
                                    getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(123);
                });
                ll_pictures.addView(view);
                return;
            }else{
                //满展示
                View view = View.inflate(this, R.layout.pictures_item, null);
                ImageView iv_show = view.findViewById(R.id.iv_show);
                ImageView iv_delete = view.findViewById(R.id.iv_delete);
                iv_show.setImageURI(picturesList.get(current).getUri());
                RxViewAction.clickNoDouble(iv_show).subscribe(unused -> {
                    //show
                });
                RxViewAction.clickNoDouble(iv_delete).subscribe(unused -> {
                    picturesList.remove(current);
                    initPictures();
                });
                ll_pictures.addView(view);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            List<Uri> uriList = Matisse.obtainResult(data);
            Log.e(TAG, "onActivityResult: = uriList.size()"   +uriList.size());
            Log.e(TAG, "onActivityResult: uriList.toString() = "   +uriList.toString());
            for (int i = 0; i < uriList.size(); i++) {
                Pictures picture = new Pictures();
                picture.setUri(uriList.get(i));
                picturesList.add(picture);
            }
            initPictures();
        }
    }

    private void initTest() {
        if(checkFieldList.size() > 5){
            checkFieldList = checkFieldList.subList(0,4);
        }

        for (int i = 0; i < checkFieldList.size(); i++) {
            int it = i;
            View item = LayoutInflater.from(this).inflate(R.layout.resource_checking_item, null, false);
            TextView tv_itemtitle = item.findViewById(R.id.tv_itemtitle);
            Switch switchr = item.findViewById(R.id.switchr);
            tv_itemtitle.setText(parseCode(checkFieldList.get(i).getCode()));
            switchr.setChecked(checkFieldList.get(i).state==1);
            switchr.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkFieldList.get(it).state = isChecked?1:0;
            });
            ll_items.addView(item);
        }
    }

    private String parseCode(String code) {
        String codename = "";
        switch (code){
            case "isMaterialAdequate":
                codename="是否按标准储备足够数量的物资";
                break;
            case "isCollectFire":
                codename="各卡口是否配备火种收集箱，严格火种收缴";
                break;
            case "isCombustiblesClear":
                codename="道路两侧、林缘地带和墓地周边可燃物是否按规定清理";
                break;
            case "isPropagandaSlogan":
                codename="是否在重要区域张贴、悬挂宣传标语";
                break;
            case "isWarmSafety":
                codename="护林检查站内是否配备取暖设备并保障取暖安全";
                break;
            case "isEquipmentAdequate":
                codename="护林员防护装备是否配备齐全充足";
                break;
            case "isFireEquipment":
                codename="护林检查站是否配备基本的灭火装备";
                break;
            case "isMaterialTidy":
                codename="仓库内物资和装备是否摆放整齐有序";
                break;
            case "isEquipmentUsable":
                codename="防灭火装备是否能够正常使用";
                break;
            case "isPeopleUseEquipment":
                codename="护林员是否会正确使用防灭火装备";
                break;
            case "isEquipmentTidy":
                codename="护林检查站内防火装备是否摆放整齐有序" ;
                break;
            case "isCivilizedSacrifice":
                codename="是否广泛开展用鲜花换烧纸等文明祭祀、无火祭祀 ";
                break;
            case "isDeploy":
                codename="是否对森林防灭火工作进行了部署 ";
                break;
            case "isOilSafety":
                codename="各类油料是否单独存放，并确保安全 ";
                break;
            case "isEquipmentOverhaul":
                codename="是否全面检修和维护保养扑火装备，确保完好率100% ";
                break;
            case "isLedgerComplete":
                codename="采购、入库、调用、销毁等台账是否齐全 ";
                break;
            case "isWaterBag":
                codename="是否在道路两侧、墓地等重点部位布设水囊（水罐）等水灭火设施 ";
                break;
            case "isWaterBagFilled":
                codename="水囊(水罐)是否已经注满水 ";
                break;
            case "isWaterBagPump":
                codename="水囊(水罐)是否配备高压水泵 ";
                break;
            case "isAllDuty":
                codename="是否全员值班备勤，集中食宿 ";
                break;
            case "isDrill":
                codename="是否组织了培训演练 ";
                break;
            case "isPatrol":
                codename="是否开展动态巡逻 ";
                break;
            case "isEquipmentComplete":
                codename="个人防护装备是否齐全充足 ";
                break;
            case "isCarComplete":
                codename="森林防火车辆是否配齐 ";
                break;
            case "isCarOverhaul":
                codename = "森林防火车辆是否开展全面检修，确保安全运行 ";
                break;
            case "isCarSpecialDrive":
                codename = "森林防火车辆是否安排专人驾驶，驾驶员证件与所驾车型是否相符 ";
                break;
            case "isPropagandaSign":
                codename = "是否在进山主要路口检查站位置设立了宣传标牌 ";
                break;
            case "isOrganizeCheck":
                codename="是否作出安排并组织开展了检查 ";
                break;
            case "isNofireNotice":
                codename="是否在进山主要路口张贴禁火通告";
                break;
            case "isFireDangerFlag":
                codename="在林内景点、主要进山路口等是否悬挂相应森林火险预警旗 ";
                break;
            case "isStaffOnduty":
                codename="护林检查站等卡口执勤人员是否在岗 ";
                break;
            case "isWearArmband":
                codename="护林员是否佩带防火检查袖标 ";
                break;
            case "isPeopleRegister":
                codename="各卡口是否做好进山人员、车辆登记 ";
                break;
            case "isInstitutionWall":
                codename="护林检查站内工作制度是否上墙";
                break;
            case "isGridWall":
                codename="护林检查站责任网格名单是否上墙 ";
                break;
            case "isPropaganda":
                codename="是否协调电视、广播、报刊、网络等新闻媒体和电信部门广泛开展全民森林防火宣传教育 ";
                break;
            case "isSmoke":
                codename="护林员没有在岗吸烟现象 ";
                break;
            case "isButt":
                codename="护林检查站附近没有烟头 ";
                break;
        }
        return codename;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        actionBar = findViewById(R.id.action_bar);
        tv_name = findViewById(R.id.tv_name);
        tv_grid = findViewById(R.id.tv_grid);
        tv_rowscreen = findViewById(R.id.tv_rowscreen);
        ll_items = findViewById(R.id.ll_items);
        ll_pictures = findViewById(R.id.ll_pictures);
        tv_enddate = findViewById(R.id.tv_enddate);
        try{
            tv_enddate.setText(endDate.replace("T"," ").substring(0,19));
        }catch(Exception e){

        }
        et_remark = findViewById(R.id.et_remark);
        tv_worker = findViewById(R.id.tv_worker);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        btn_submit = findViewById(R.id.btn_submit);
        path_view = findViewById(R.id.path_view);
        fl_worker = findViewById(R.id.fl_worker);
        fl_enddate = findViewById(R.id.fl_enddate);
        sv_out = findViewById(R.id.sv_out);
        actionBar.setTitle("资源检查");
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
        RxViewAction.clickNoDouble(btn_left).subscribe(unused -> {
            path_view.clear();
            path_view.setOnlyWatch(false);

            btn_right.setClickable(true);
            btn_right.setBackground(getDrawable(R.drawable.btn_theme));
        });
        RxViewAction.clickNoDouble(btn_right).subscribe(unused -> {
            if(path_view.getTouched()){
                path_view.setOnlyWatch(true);
                Toast.makeText(this, "签名已确认", Toast.LENGTH_SHORT).show();

                btn_right.setClickable(false);
                btn_right.setBackground(getDrawable(R.drawable.btn_theme_gray));

                try {
                    path_view.save(getObbDir().getAbsolutePath() + "sign.png");
                    Uri parse = Uri.parse(getObbDir().getAbsolutePath() + "sign.png");
                    signPictures.setType(1);
                    signPictures.setUri(parse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "您还没有签名", Toast.LENGTH_SHORT).show();
            }
        });
        RxViewAction.clickNoDouble(tv_rowscreen).subscribe(unused -> {

        });
        /*RxViewAction.clickNoDouble(fl_enddate).subscribe(unused -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(ResourceCheckingActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    endDateStr = "" + year + "-" + month + "-" + dayOfMonth + " ";
                    new TimePickerDialog(ResourceCheckingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            endDateStr += hourOfDay + ":" + minute;
                            tv_enddate.setText(endDateStr);
                        }
                    },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
                }
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        });*/
        RxViewAction.clickNoDouble(fl_worker).subscribe(unused -> {
            showBottomDialog();
        });
        RxViewAction.clickNoDouble(btn_submit).subscribe(unused -> {
            showMessageDialog("确认提交审核吗？");
        });

    }



    private int postIndex = 0;
    public void showMessageDialog(String msg) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_tokendown, null, false);
        TextView message_text = (TextView) dialogView.findViewById(R.id.message_text);
        message_text.setText(msg);
        final TextView tv_right = (TextView) dialogView.findViewById(R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(R.id.tv_left);
        //确认
        RxViewAction.clickNoDouble(tv_right).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();
                if(picNumber == 0){
                    Toast.makeText(ResourceCheckingActivity.this, "请至少上传一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(signPictures.getUri()==null){
                    Toast.makeText(ResourceCheckingActivity.this, "您还没有签名", Toast.LENGTH_SHORT).show();
                    return;
                }

                postPicturesList.clear();
                postIndex = 0;
                for (int i = 0; i < picturesList.size(); i++) {
                    if(picturesList.get(i).getUri() == null ){
                        picturesList.remove(i);
                        break;
                    }
                }
                for (int i = 0; i < picturesList.size(); i++) {
                    postMoreImagesService(picturesList.get(i).getUri(),false);
                }
                if(signPictures.getUri()!=null){
                    postMoreImagesService(signPictures.getUri(),true);
                }
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




    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }


    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text, String name, String ctx,
                                           String text1, String name1, String ctx1,
                                           Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, paddingLeft, paddingTop, paint);
        canvas.drawText(name, paddingLeft, paddingTop+100, paint);
        canvas.drawText(ctx, paddingLeft, paddingTop+200, paint);

        canvas.drawText(text1, paddingLeft, paddingTop+300, paint);
        canvas.drawText(name1, paddingLeft, paddingTop+400, paint);
        canvas.drawText(ctx1, paddingLeft, paddingTop+500, paint);

        return bitmap;
    }

    private Bitmap evaluate;
    private String checkTime;
    private String name;
    private Paint paint;

    private void postMoreImagesService(Uri uri,boolean isSign){
        Log.e(TAG, "postMoreImagesService: bingo uri = " + uri);
        showDialogProgress(progressDialog,"请稍候...");

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);

        name = "post";
        checkTime = dateFormat.format(date);
        try {
            int degree = ImageUtils.readPictureDegree(uri.toString());
            Bitmap photo = ImageUtils.getBitmapFormUri(getApplicationContext(), uri);
            Bitmap shuiYinPhoto = drawTextToBitmap(this, photo,checkTime.replace("T"," ") + "  " + name, "", "", "", "", "", paint, 10, 40);
            evaluate = rotaingImageView(degree, shuiYinPhoto);

        } catch (IOException e) {

        }
        String savePhoto = ImageUtils.savePhoto(this.evaluate, this.getObbDir().getAbsolutePath(), checkTime + "pic" + new Random().nextInt(1000));

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_HUAWEI + "oa/api/workReport/fileUploadAnByNotToken");
        params.setAsJsonContent(true);
        params.setMultipart(true);
        // params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("file", new File(savePhoto),null,savePhoto);
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().token);
        Log.e(TAG, "postimage: " + params );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "postimage: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        JSONObject data = jsonObject1.getJSONObject("data");
                        JSONArray imgStrArray = data.getJSONArray("img");
                        String imageUrl = imgStrArray.get(0).toString();
                        if(!isSign){
                            Pictures pictures = new Pictures();
                            pictures.setUrl(imageUrl);
                            pictures.setType(1);
                            postPicturesList.add(pictures);
                        }
                        postIndex++;
                        if(postIndex >= picNumber+1){
                            submit();
                        }
                    }else {
                        Toast.makeText(ResourceCheckingActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
            }
        });
    }
    private List<Pictures> postPicturesList = new ArrayList<>();

    private String signStr = "";
    private Pictures signPictures = new Pictures();
    private List<String> imgStrList = new ArrayList<>();
    private void submit() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"提交中...");
        }
        JSONObject jsonObjectImages = new JSONObject();
        JSONObject jsonObjectItems = new JSONObject();
        JSONObject jsonObjectCheckUser = new JSONObject();
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("checkType",checkType);
            jsonObjectBody.put("id",id);
            jsonObjectBody.put("userId",new DbConfig(this).getUser().getId());
            jsonObjectBody.put("checkuserVOS",checkUserList.get(checkUserIndex));
            jsonObjectBody.put("description",et_remark.getText().toString());

            JSONArray imagesArray = new JSONArray();
            for (int i = 0; i < postPicturesList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                Pictures pic = postPicturesList.get(i);
                jsonObject.put("img",pic.getUrl());
                jsonObject.put("type",1);
                imagesArray.put(jsonObject);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("img",signPictures.getUrl());
            jsonObject.put("type",2);
            imagesArray.put(jsonObject);

//            jsonObjectBody.put("imgs", imagesArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResource/changeHiddenPerils");
        params.setBodyContent(jsonObjectBody.toString());

        Log.e(TAG, "getDataFromService: params = " + params);
        Log.e(TAG, "getDataFromService: jsonObjectBody = " + jsonObjectBody.toString());
        params.addHeader("Authorization", "bearer " + new DbConfig(ResourceCheckingActivity.this).getUser().token);

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: hiddenDangerList" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        Toast.makeText(ResourceCheckingActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(ResourceCheckingActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ResourceCheckingActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
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

    JSONArray checkUserList = new JSONArray();
    List<String> checkUserStrList = new ArrayList<>();
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
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().token);
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        checkUserList = jsonObject1.getJSONArray("data");
                        for (int i = 0; i < checkUserList.length(); i++) {
                            JSONObject model = (JSONObject) checkUserList.get(i);
                            checkUserStrList.add(model.getString("fullName"));
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

    private int checkUserIndex = 0;
    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.bottom_list, null);
        LinearLayout ll_list = view.findViewById(R.id.ll_list);
        for (int i = 0; i < checkUserStrList.size(); i++) {
            String str = checkUserStrList.get(i);
            View item = View.inflate(this, R.layout.bottom_list_item, null);
            TextView tv_title = item.findViewById(R.id.tv_title);
            tv_title.setText(str);
            RxViewAction.clickNoDouble(tv_title).subscribe(unused -> {
                tv_worker.setText(str);
                for (int j = 0; j < checkUserList.length(); j++) {
                    try {
                        JSONObject obj = (JSONObject) checkUserList.get(j);
                        if(Objects.equals(obj.getString("userName"), "str")){
                            checkUserIndex = j;
                            break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            });
            ll_list.addView(item);
        }

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.AppTheme);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
        dialog.show();

    }

}