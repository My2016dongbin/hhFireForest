package com.haohai.platform.platformmodel.ui.acticity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haohai.platform.firelibrary.ui.activity.AddResourceCheckingActivity;
import com.haohai.platform.firelibrary.ui.model.Pictures;
import com.haohai.platform.firelibrary.ui.multitype.AddResourceCheck;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.acticity.base.HhBaseActivity;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.image.ImageUtils;
import com.ruyiruyi.rylibrary.request.RequestUtils;
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
import java.util.Objects;
import java.util.Random;

//import me.rosuh.filepicker.config.FilePickerManager;
//import me.rosuh.filepicker.filetype.FileType;
//import me.rosuh.filepicker.filetype.PageLayoutFileType;
//import me.rosuh.filepicker.filetype.TextFileType;
import rx.functions.Action1;

import static com.haohai.platform.firelibrary.ui.activity.AddResourceCheckingActivity.drawTextToBitmap;
import static com.haohai.platform.firelibrary.ui.activity.AddResourceCheckingActivity.rotaingImageView;

public class AddNewsActivity extends HhBaseActivity {
    TextView tv_title;
    ImageView backButton;
    private LinearLayout ll_pictures;
    private EditText et_title;
    private EditText et_content;
    private ImageView iv_file;
    private ImageView iv_file_delete;
    private ImageView iv_file_upload;
    private LinearLayout ll_file_upload;
    private TextView tv_file_upload;
    private TextView tv_submit;
    private List<Pictures> picturesList = new ArrayList<>();
    private int maxPicture = 5;
    private int picNumber = 0;//当前有几张
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        initView();
        initPictures();
    }

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
                    Matisse.from(AddNewsActivity.this)
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

    private String filePath = "";
    private String fileName = "";
    private String fileType = "";
    private String fileUrl = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            List<Uri> uriList = Matisse.obtainResult(data);
            for (int i = 0; i < uriList.size(); i++) {
                Pictures picture = new Pictures();
                picture.setUri(uriList.get(i));
                picturesList.add(picture);
            }
            initPictures();
        }
       /* if(requestCode == FilePickerManager.REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                List<String> obtainData = FilePickerManager.obtainData();
                Log.e("TAG", "onActivityResult: obtainData = " + obtainData );
                String file = obtainData.get(0);
                String fileT = obtainData.get(0);
                filePath = file;
                while(file.contains("/")){
                    int indexOf = file.indexOf("/");
                    int length = file.length();
                    file = file.substring(indexOf+1,length);
                }
                fileName = file;
                fileType = fileT;
                iv_file.setVisibility(View.GONE);
                ll_file_upload.setVisibility(View.VISIBLE);
                tv_file_upload.setText(file);
                while(fileT.contains(".")){
                    int indexOf = fileT.indexOf(".");
                    int length = fileT.length();
                    fileT = fileT.substring(indexOf+1,length);
                }
            }else{
                Toast.makeText(this, "您没有选择任何文件", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        ll_pictures = findViewById(com.haohai.platform.firelibrary.R.id.ll_pictures);
        tv_title = findViewById(R.id.tv_title);
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        iv_file = findViewById(R.id.iv_file);
        iv_file_delete = findViewById(R.id.iv_file_delete);
        iv_file_upload = findViewById(R.id.iv_file_upload);
        ll_file_upload = findViewById(R.id.ll_file_upload);
        tv_file_upload = findViewById(R.id.tv_file_upload);
        tv_submit = findViewById(R.id.tv_submit);
        backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RxViewAction.clickNoDouble(iv_file_delete).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                ll_file_upload.setVisibility(View.GONE);
                iv_file.setVisibility(View.VISIBLE);
                filePath = "";
            }
        });
        RxViewAction.clickNoDouble(tv_submit).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                /*if(picNumber == 0){
                    Toast.makeText(AddNewsActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if(et_title.getText().toString().isEmpty()){
                    Toast.makeText(AddNewsActivity.this, "请输入新闻标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_content.getText().toString().isEmpty()){
                    Toast.makeText(AddNewsActivity.this, "请输入新闻内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Objects.equals(filePath, "")){
                    Toast.makeText(AddNewsActivity.this, "请选择文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                postPicturesList.clear();
                for (int i = 0; i < picturesList.size(); i++) {
                    Pictures pictures = picturesList.get(i);
                    if(pictures.getUri() != null ){
                        postPictures(pictures.getUri());
                    }
                }
                postFile(filePath);
            }
        });
        RxViewAction.clickNoDouble(iv_file).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                /*List<FileType> types = new ArrayList<>();
                types.add(new PageLayoutFileType());
                types.add(new TextFileType());
                FilePickerManager
                        .from(AddNewsActivity.this)
                        .maxSelectable(1)
                        .enableSingleChoice()
                        .registerFileType(types,true)
                        .forResult(FilePickerManager.REQUEST_CODE);*/
            }
        });
    }

    private Bitmap evaluate;
    private String checkTime;
    private String name;
    private Paint paint;
    private List<Pictures> postPicturesList = new ArrayList<>();
    private int postIndex = 0;

    private void postPictures(Uri uri) {
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
        Log.e("TAG", "postimage: " + params );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "postimage: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        JSONObject data = jsonObject1.getJSONObject("data");
                        JSONArray imgStrArray = data.getJSONArray("img");
                        String imageUrl = imgStrArray.get(0).toString();
                        Pictures pictures = new Pictures();
                        pictures.setUrl(imageUrl);
                        pictures.setType(1);
                        postPicturesList.add(pictures);
                    }else {
                        Toast.makeText(AddNewsActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
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

    private void postFile(String path) {

        showDialogProgress(progressDialog,"请稍候...");
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_HUAWEI + "oa/api/workReport/fileUploadAnByNotToken");
        params.setAsJsonContent(true);
        params.setMultipart(true);
        params.addBodyParameter("file", new File(path),null,fileName);
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().token);
        Log.e("TAG", "postimage: " + params );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "postimage: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    String message = jsonObject1.getString("message");
                    if (code.equals("200")){
                        JSONObject data = jsonObject1.getJSONObject("data");
                        JSONArray imgStrArray = data.getJSONArray("img");
                        String imageUrl = imgStrArray.get(0).toString();
                        fileUrl = imageUrl;

                        postNews();
                    }else {
                        Toast.makeText(AddNewsActivity.this, "文件上传失败", Toast.LENGTH_SHORT).show();
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

    private void postNews() {

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        String dateStr = simpleDateFormat.format(date);
        JSONObject object = new JSONObject();
        try {
            object.put("name",et_title.getText().toString());
            object.put("describes",et_content.getText().toString());
            object.put("remark",fileName);//文件名存储
            object.put("url",fileUrl);
            object.put("groupId",new DbConfig(this).getUser().getGroupId());
            object.put("createTime",dateStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/journalism");
        params.addHeader("Authorization","bearer " + new DbConfig(this).getUser().getToken());
        params.setBodyContent(object.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: addNews" + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200){
                        Toast.makeText(AddNewsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(AddNewsActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(AddNewsActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
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