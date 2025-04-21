package com.haohai.platform.mapmodel.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.firelibrary.ui.activity.FireAddActivity;
import com.haohai.platform.firelibrary.ui.activity.FireMapActivity;
import com.haohai.platform.firelibrary.ui.activity.Pic1Activity;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.Utils.MNCTransparentDialog;
import com.haohai.platform.mapmodel.bean.AreaModel;
import com.haohai.platform.mapmodel.bean.LatLngModel;
import com.haohai.platform.mapmodel.bean.UploadPost;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.image.ImageUtils;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.ui.cell.WheelView;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;
import com.ruyiruyi.rylibrary.utils.GifSizeFilter;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.android.tpush.XGPushManager;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import rx.functions.Action1;

public class UploadActivity extends HhBaseActivity {
    ImageView iv_back;
    ImageView iv_right;
    private DYLoadingView dy3;

    EditText et_name;
    LinearLayout ll_area;
    TextView tv_area;
    LinearLayout ll_latlng;
    TextView tv_latlng;
    EditText et_address;
    EditText et_user;
    EditText et_info;
    LinearLayout ll_pic;
    LinearLayout ll_video;
    TextView tv_video;
    TextView tv_submit;
    ImageView iv_area;
    public static final int MAP_REUEST_CODE = 678;
    public static final int REQUEST_CODE_CHOOSE = 679;
    public static final int REQUEST_CODE_VIDEO = 680;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload2);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });
        RxViewAction.clickNoDouble(iv_right).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                startActivity(new Intent(UploadActivity.this,UploadListActivity.class));
            }
        });
        initView();
        initData();
    }


    private void initData() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_LOGIN + "resource/api/grid/gridTree");
        params.addHeader("Authorization", "bearer " + new DbConfig(UploadActivity.this).getUser().getToken());
        params.setBodyContent(new JSONObject().toString());
        Log.e("TAG", "onSuccess: bingo grid" + new DbConfig(UploadActivity.this).getUser().getToken() );
        Log.e("TAG", "onSuccess: bingo grid" + params.toString() );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("TAG", "onSuccess: bingo grid" + result );
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    areaAllList = new Gson().fromJson(String.valueOf(data), new TypeToken<List<AreaModel>>(){}.getType());

                    if(areaAllList.size()!=0){
                        cityList.clear();
                        areaStrList.clear();
                        for (int i = 0; i < areaAllList.size(); i++) {
                            cityList.add(areaAllList.get(i));
                            areaStrList.add(areaAllList.get(i).getName());
                        }

                        initArea();
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

    private String postGroupId = "";
    private void initArea() {
        Log.e("TAG", "initArea: "  );
        tv_area.setText("请选择区域");
        iv_area.setVisibility(View.VISIBLE);
        RxViewAction.clickNoDouble(ll_area).subscribe(unused -> {
            if(areaStrList.size() == 0){
                Toast.makeText(this, "区域信息加载异常，请重新打开此页面", Toast.LENGTH_SHORT).show();
                return;
            }
            showAreaDialog();
        });
    }

    List<Uri> picturesList = new ArrayList<>();
    int maxPicNumber = 2;
    private void initView() {
        dy3 = findViewById(R.id.dy3);
        et_name = findViewById(R.id.et_name);
        ll_area = findViewById(R.id.ll_area);
        tv_area = findViewById(R.id.tv_area);
        ll_latlng = findViewById(R.id.ll_latlng);
        tv_latlng = findViewById(R.id.tv_latlng);
        et_address = findViewById(R.id.et_address);
        et_user = findViewById(R.id.et_user);
        et_info = findViewById(R.id.et_info);
        ll_pic = findViewById(R.id.ll_pic);
        ll_video = findViewById(R.id.ll_video);
        tv_video = findViewById(R.id.tv_video);
        tv_submit = findViewById(R.id.tv_submit);
        iv_area = findViewById(R.id.iv_area);

        LinearLayout ll_time;
        TextView tv_time;
        ImageView iv_time;
        et_user.setText(new DbConfig(UploadActivity.this).getUser().getFullName());

        //图片选择初始化
        initPictures();

        User user = new DbConfig(UploadActivity.this).getUser();

        RxViewAction.clickNoDouble(ll_latlng).subscribe(unused -> {
            Intent intent = new Intent(getApplicationContext(), FireMapActivity.class);
            intent.putExtra("longitude_double", CommonData.lng);
            intent.putExtra("latitude_double", CommonData.lat);
            startActivityForResult(intent, MAP_REUEST_CODE);
        });
        RxViewAction.clickNoDouble(ll_video).subscribe(unused -> {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_CODE_VIDEO);
        });
        RxViewAction.clickNoDouble(tv_submit).subscribe(unused -> {
            if(et_name.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入事件名称", Toast.LENGTH_SHORT).show();
                return;
            }
            if(tv_area.getText().toString().contains("请选择")){
                Toast.makeText(this, "请选择区域", Toast.LENGTH_SHORT).show();
                return;
            }
            if(tv_latlng.getText().toString().contains("请选择")){
                Toast.makeText(this, "请选择位置", Toast.LENGTH_SHORT).show();
                return;
            }
            if(et_address.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入上报地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if(et_user.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入上报人", Toast.LENGTH_SHORT).show();
                return;
            }
            if(et_info.getText().toString().isEmpty()){
                Toast.makeText(this, "请输入描述", Toast.LENGTH_SHORT).show();
                return;
            }
            if(picturesList.isEmpty()){
                Toast.makeText(this, "请至少选择一张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            showSubmitDialog("确定提交吗?");
        });
    }

    void showDY3(){
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }
    void hideDY3(){
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    public void showSubmitDialog(String msg) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(UploadActivity.this);
        View dialogView = LayoutInflater.from(UploadActivity.this).inflate(R.layout.dialog_tokendown, null, false);
        TextView message_text = (TextView) dialogView.findViewById(R.id.message_text);
        message_text.setText(msg);
        final TextView tv_queren = (TextView) dialogView.findViewById(R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(R.id.tv_left);
        //确认
        RxViewAction.clickNoDouble(tv_queren).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();
                postFiles();
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

    int uploadTag = 0;
    String allPicStr = "";
    String videoStr = "";
    private void postFiles() {
        uploadTag = 0;
        showDY3();
        uploadFile(true);
    }

    private void uploadFile(boolean isPicture) {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "api/workReport/fileUploadAn");
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.setAsJsonContent(true);
        params.setMultipart(true);
        try {
            if(isPicture){
                //上传图片
                Uri uri = picturesList.get(uploadTag);
                String pathStr = /*ImageUtils.getRealPathFromURI(UploadActivity.this,uri)*/uri.getPath();
                int degree = ImageUtils.readPictureDegree(pathStr);
                Bitmap photo = ImageUtils.getBitmapFormUri(getApplicationContext(), uri);
                Bitmap evaluate = rotaingImageView(degree, photo);
                String picStr = ImageUtils.savePhoto(evaluate, this.getObbDir().getAbsolutePath(), "upload" + uploadTag);
                Log.e("TAG", "uploadFile: picStr = " + picStr );
                params.addBodyParameter("file", new File(picStr),null,picStr);
            }else{
                //上传视频
                Log.e("TAG", "uploadFile: videoPath = " + videoPath );
                params.addBodyParameter("file", new File(videoPath),null,videoPath);
            }

        } catch (IOException e) {
            Log.e("TAG", "uploadFile: e = " + e.toString() );
        }
        Log.e("TAG", "onSuccess: bingo uploadFile" + params );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e("TAG", "onSuccess: bingo uploadFile" + result );
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200){
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray img = data.getJSONArray("img");
                        String dataStr = "";
                        if(img.length()!=0){
                            dataStr = (String) img.get(0);
                        }

                        if(isPicture){
                            if(allPicStr.isEmpty()){
                                allPicStr = dataStr;
                            }else{
                                allPicStr = allPicStr + "," + dataStr;
                            }
                        }else{
                            videoStr = dataStr;
                        }
                        //公交车循环
                        if(uploadTag < picturesList.size()-1){//上传图片true
                            uploadTag++;
                            uploadFile(true);
                        }else if(uploadTag == picturesList.size()-1 && (!videoPath.isEmpty())){//上传视频(已选视频)false
                            uploadTag++;
                            uploadFile(false);
                        }else{//上传完毕,退出
                            postSubmit();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "onError: ex = " + ex.toString() );
                postSubmit();
                hideDY3();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void postSubmit() {
        double[] doubles = {CommonData.lat,CommonData.lng};
        try{
            doubles = new LatLngChangeNew().calBD09toWGS84(Double.parseDouble(latitude), Double.parseDouble(longitude));
        }catch (Exception e){
        }
        UploadPost uploadPost = new UploadPost();
        LatLngModel latLngModel = new LatLngModel();
        latLngModel.setLat(doubles[0]);
        latLngModel.setLng(doubles[1]);
        uploadPost.setPosition(latLngModel);
        uploadPost.setTaskRegion(tv_area.getText().toString());
        uploadPost.setMemberName(et_user.getText().toString());
        uploadPost.setDescription(et_info.getText().toString());
        uploadPost.setTaskContent(et_address.getText().toString());
        uploadPost.setTaskImg(allPicStr);
        uploadPost.setReserve(videoStr);
        uploadPost.setTaskType(et_name.getText().toString());
        uploadPost.setGroupId(postGroupId);
        uploadPost.setUserId(new DbConfig(UploadActivity.this).getUser().getId());
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "api/taskManagement");
        params.setBodyContent(new Gson().toJson(uploadPost));
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        Log.e("TAG", "postSubmit: bingo upload" + params.toString() );
        Log.e("TAG", "postSubmit: bingo upload" + new Gson().toJson(uploadPost) );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: bingo upload" + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200){
                        if(Objects.equals(jsonObject.getString("message"), "OK")){
                            Toast.makeText(UploadActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(UploadActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(UploadActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(UploadActivity.this, "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                hideDY3();
            }
        });
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

    private void initPictures() {
        ll_pic.removeAllViews();
        for (int i = 0; i < picturesList.size(); i++) {
            final int index = i;
            View showPicView = LayoutInflater.from(UploadActivity.this).inflate(R.layout.pic_show_vide, null, false);
            ImageView iv_show = showPicView.findViewById(R.id.iv_show);
            ImageView iv_delete = showPicView.findViewById(R.id.iv_delete);
            Glide.with(this).load(picturesList.get(index)).into(iv_show);
            RxViewAction.clickNoDouble(iv_show).subscribe(unused -> {
                //展示图片
                Intent intent = new Intent(UploadActivity.this, Pic1Activity.class);
                intent.putExtra("pic",picturesList.get(index).toString());
                intent.putExtra("from",UploadActivity.class);
                startActivity(intent);
            });
            RxViewAction.clickNoDouble(iv_delete).subscribe(unused -> {
                //删除图片
                picturesList.remove(index);
                initPictures();
            });
            ll_pic.addView(showPicView);
        }
        if(picturesList.size() < maxPicNumber){
            View addPicView = LayoutInflater.from(UploadActivity.this).inflate(R.layout.pic_add_vide, null, false);
            FrameLayout fl_add = addPicView.findViewById(R.id.fl_add);
            RxViewAction.clickNoDouble(fl_add).subscribe(unused -> {
                //添加图片
                addImage();
            });
            ll_pic.addView(addPicView);
        }
    }

    private void addImage() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        int size = maxPicNumber - picturesList.size();
                        Matisse.from(UploadActivity.this)
                                .choose(MimeType.allOf())
                                .countable(true)
                                .capture(true)
                                .captureStrategy(
                                        new CaptureStrategy(true,"com.haohai.platform.fireforestplatform")
                                )
                                .maxSelectable(size)
                                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                .gridExpectedSize(
                                        getResources().getDimensionPixelSize(com.haohai.platform.firelibrary.R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                .thumbnailScale(0.85f)
                                .imageEngine(new GlideEngine())
                                .forResult(REQUEST_CODE_CHOOSE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private WheelView wv_area;
    private WheelView wv_qu;
    int areaIndex = 0;
    int quIndex = 0;
    private String areaStr = "请选择区域";
    private String quStr = "请选择区域";
    private String areaId = "";
    private String quId = "";
    private String proStr = "";
    private String proId = "";
    private List<String> areaStrList = new ArrayList<>();
    private List<String> quStrList = new ArrayList<>();
    private AreaModel currentArea = new AreaModel();
    private List<AreaModel> areaAllList = new ArrayList<>();
    private List<AreaModel> cityList = new ArrayList<>();
    private List<AreaModel> quList = new ArrayList<>();
    private void showAreaDialog() {
        View areaView = LayoutInflater.from(this).inflate(R.layout.dialog_grid_p_c, null);
        wv_area = ((WheelView) areaView.findViewById(R.id.wv_area));
        wv_area.setIsLoop(false);
        wv_area.setItems(areaStrList, areaIndex);

        wv_qu = ((WheelView) areaView.findViewById(R.id.wv_qu));
        wv_qu.setIsLoop(false);
        if(quStrList.size()==0){
            quStrList.add("请选择区域");
        }
        wv_qu.setItems(quStrList, quIndex);
        wv_qu.setVisibility(View.VISIBLE);

        wv_area.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                areaStr = wv_area.getSelectedItem();
                areaIndex = wv_area.getSelectedPosition();
                postGroupId = cityList.get(areaIndex).getNo();
                currentArea = cityList.get(areaIndex);
                quList = cityList.get(areaIndex).getChildren();
                quIndex = 0;
                quStrList.clear();
                quStr = "请选择区域";
                if(quList.size()==0){
                    quList.add(new AreaModel(quStr,""));
                }
                for (int i = 0; i < quList.size(); i++) {
                    quStrList.add(quList.get(i).getName());
                }
                wv_qu.setItems(quStrList, quIndex);
                //修改显示信息
                tv_area.setText(areaStr);
            }
        });
        wv_qu.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int selectedIndex, String item) {
                quStr = wv_qu.getSelectedItem();
                quIndex = wv_qu.getSelectedPosition();
                if(quList.size()==0){
                    return;
                }
                postGroupId = quList.get(quIndex).getNo();
                //修改显示信息
                if(quStr.contains("请选择")){
                    tv_area.setText(areaStr);
                }else{
                    tv_area.setText(areaStr + quStr);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("请选择区域")
                .setView(areaView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        postGroupId = cityList.get(areaIndex).getNo();
                        currentArea = cityList.get(areaIndex);
                        areaStr = wv_area.getSelectedItem();
                        areaIndex = wv_area.getSelectedPosition();
                        areaId = cityList.get(areaIndex).getNo();
                        quStr = wv_qu.getSelectedItem();
                        quIndex = wv_qu.getSelectedPosition();
                        try{
                            quId = quList.get(quIndex).getNo();
                        }catch (Exception e){
                            Log.e("TAG", "onClick: " + e.getMessage() );
                        }
                        //修改显示信息
                        if(quStr.contains("请选择")){
                            tv_area.setText(areaStr);
                        }else{
                            tv_area.setText(areaStr + quStr);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    String longitude = "";
    String latitude = "";
    String videoPath = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "onActivityResult: bingo result" + requestCode + "," + resultCode );
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_REUEST_CODE && resultCode == RESULT_OK) {
            longitude = data.getStringExtra("longitude");
            latitude = data.getStringExtra("latitude");
            String cityAddress = data.getStringExtra("cityAddress");
            String currentCity = data.getStringExtra("city");
            tv_latlng.setText(new CommonUtil().parseStrLength(latitude,12) + "," + new CommonUtil().parseStrLength(longitude,12));
            et_address.setText(cityAddress);
        }else  if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> uriList = Matisse.obtainResult(data);
            for (int i = 0; i < uriList.size(); i++) {
                picturesList.add(uriList.get(i));
            }
            initPictures();
        }else if (requestCode == REQUEST_CODE_VIDEO && resultCode == RESULT_OK && null != data) {
            Log.e("TAG", "onActivityResult: bingo video" );
            Uri selectedVideo = data.getData();
            //videoPath = selectedVideo.getPath();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            videoPath = cursor.getString(columnIndex);
            cursor.close();
            Log.e("TAG", "onActivityResult: " + videoPath);
            tv_video.setText("重新选择视频");
        }
    }
}