package com.haohai.platform.firelibrary.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.haohai.platform.firelibrary.R;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.model.Pictures;
import com.haohai.platform.firelibrary.ui.multitype.ChooseImage;
import com.haohai.platform.firelibrary.ui.view.MNCTransparentDialog;
import com.ruyiruyi.rylibrary.utils.LinePathView;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
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
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import rx.functions.Action1;

@Route(path = RouteUtils.Checking)
public class CheckingActivity extends HhBaseActivity {
    private static final String TAG = CheckingActivity.class.getSimpleName();
    private ActionBar actionBar;
    private FrameLayout fl_pass;
    private EditText et_detail;
    private EditText et_remark;
    private TextView tv_rowscreen;
    private LinearLayout ll_pictures;
    private TextView tv_pass;
    private Button btn_submit;
    private Button btn_left;
    private Button btn_right;
    private LinePathView path_view;
    private ProgressDialog progressDialog;
    private final boolean isShowDialog = true;
    private boolean pass = true;


    @Autowired
    String token;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking2);
        ARouter.getInstance().inject(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        progressDialog = new ProgressDialog(this);

        initView();
        initPictures();
    }

    private List<Pictures> picturesList = new ArrayList<>();
    private List<Pictures> postPicturesList = new ArrayList<>();
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
                    Matisse.from(CheckingActivity.this)
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
                        Toast.makeText(CheckingActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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

    private String signStr = "";
    private Pictures signPictures = new Pictures();
    private List<String> imgStrList = new ArrayList<>();
    private void submit() {
        if (isShowDialog){
            showDialogProgress(progressDialog,"提交中...");
        }
        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("checkType",6);
            int status;
            if(pass){
                status = 4;
            }else{
                status = 3;
            }
            jsonObjectBody.put("id",id);
            jsonObjectBody.put("userId",new DbConfig(this).getUser().getId());
            jsonObjectBody.put("status",status);
            jsonObjectBody.put("regulation",et_detail.getText().toString());
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
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().token);

        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: hiddenDangerList" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        Toast.makeText(CheckingActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(CheckingActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(CheckingActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
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

    private void initView() {
        actionBar = findViewById(R.id.action_bar);
        fl_pass = findViewById(R.id.fl_pass);
        et_detail = findViewById(R.id.et_detail);
        et_remark = findViewById(R.id.et_remark);
        tv_pass = findViewById(R.id.tv_pass);
        tv_rowscreen = findViewById(R.id.tv_rowscreen);
        btn_submit = findViewById(R.id.btn_submit);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        path_view = findViewById(R.id.path_view);
        ll_pictures = findViewById(R.id.ll_pictures);
        actionBar.setTitle("隐患排查");
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
        RxViewAction.clickNoDouble(fl_pass).subscribe(unused -> {
            showBottomDialog();
        });
        RxViewAction.clickNoDouble(tv_rowscreen).subscribe(unused -> {

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
                    Toast.makeText(CheckingActivity.this, "请至少上传一张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(signPictures.getUri()==null){
                    Toast.makeText(CheckingActivity.this, "您还没有签名", Toast.LENGTH_SHORT).show();
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

    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.AppTheme);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                tv_pass.setText("是");
                pass = true;
            }
        });

        dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                tv_pass.setText("否");
                pass = false;
            }
        });

    }

}