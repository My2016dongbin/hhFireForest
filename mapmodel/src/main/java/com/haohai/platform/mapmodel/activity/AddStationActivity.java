package com.haohai.platform.mapmodel.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.bean.AddCheck;
import com.haohai.platform.mapmodel.bean.ResourceTypeList;
import com.haohai.platform.mapmodel.bean.planResourceDTOS;
import com.haohai.platform.mapmodel.bean.stationinfo;
import com.haohai.platform.mapmodel.multitype.CheckFieldViewBinder;
import com.haohai.platform.mapmodel.multitype.ChooseImage;
import com.haohai.platform.mapmodel.multitype.ChooseImageViewBinder;
import com.haohai.platform.mapmodel.multitype.people;
import com.haohai.platform.mapmodel.multitype.peopleViewBinder;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.MessagePicturesLayout;
import com.ruyiruyi.rylibrary.db.CheckField;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.image.ImageUtils;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.GifSizeFilter;
import com.ruyiruyi.rylibrary.utils.LinePathView;
import com.ruyiruyi.rylibrary.utils.image.ImagPagerUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.ielse.view.imagewatcher.ImageWatcher;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class AddStationActivity extends HhBaseActivity implements CheckFieldViewBinder.OnCheckFieldItemClick, ChooseImageViewBinder.OnChooseImageClickListener , MessagePicturesLayout.Callback, peopleViewBinder.OnPeopleLsitItemClick {

    private static final String TAG = AddStationActivity.class.getSimpleName();
    private Intent intent;
    private String id;
    private String name;
    private String gird;
    private String girdno;
    private int resourceType;
    private LinePathView mPathView;
    private TextView clearQianmingView;
    private TextView postDataButton;
    private String access_token;
    public List<String> fullList;
    private ProgressDialog progressDialog;
    private ImageView backButton;
    private Dialog qianmingDialog;
    private View qianmingInflater;
    private TextView qianmingButton;
    private String checkTime;
    private String checkTimeOne;
    private boolean hasOneQianming = false;

    private FrameLayout oneImageLayout;
    private FrameLayout twoImageLayout;
    private ImageView oneImage;
    private ImageView twoImage;
    private ImageView oneImageDelete;
    private ImageView twoImageDelete;
    public List<Uri> uriChooseList;
    public boolean isHasPermission = true;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private EditText beizhuEdit;
    private Bitmap evaluate;
    private Bitmap evaluateOne;
    private Bitmap evaluateTwo;
    private Bitmap evaluateThree;
    private List<CheckField> checkFieldList;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private List<Object> photoItems = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private CheckFieldViewBinder checkFieldProvider;
    private View rootView;
    private int rootViewVisibleHeight;
    private FrameLayout buttonLayout;
    private String endTime;
    private String pic1 = "";
    private String pic2 = "";
    private Paint paint;
    private Dialog qianmingOneDialog;
    private View qianmingOneInflater;
    private LinePathView mPathOneView;
    private TextView clearQianmingOneView;
    private TextView qianmingOneButton;
    private TextView qianmingPassButton;
    private RecyclerView photoListView;
    private MultiTypeAdapter photoAdapter;
    private ChooseImageViewBinder chooseImageViewBinder;
    private ImageWatcher vImageWatcher;
    private List<people> peopleList;
    private boolean hasNet = true;
    private List<ChooseImage> list = new ArrayList<>();
    public int currentPostNum = 0;
    private boolean currentChoose = true;
    private DbManager db;
    private TextView resourceName;
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
    private List<planResourceDTOS.CheckuserVOSFirejd> checkusers =new ArrayList<>();
    private List<Object> imglist=new ArrayList<>();;
    private List<ResourceTypeList> resourceTypeLists = new ArrayList<>();
    private List<String> typeNameList=new ArrayList<>();
    private int checktype;
    private AlertDialog.Builder builder;
    private int choose1 = 0;
    private int choose2 = 0;
    private int choose3 = 0;
    private List<stationinfo> stationinfos = new ArrayList<>();
    private List<String> stationList= new ArrayList<>();
    private List<planResourceDTOS.ImgsFirejd> imgsFirejds = new ArrayList<>();
    private TextView isPassview;
    private planResourceDTOS planResourceDTOS=new planResourceDTOS();
    private String currentResName;
    private double currentResLat;
    private double currentResLng;
    private String currentType;
    private int requst_code=2;
    private int resStatus=3;
    private String planResourceType;

    class ImagePostThread extends Thread {

        @Override
        public void run() {
            super.run();

            postMoreImagesService(0);



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stasion);
        progressDialog = new ProgressDialog(this);


        access_token = new DbConfig(this).getUser().getToken();
        db = new DbConfig(this).getDbManager();
        uriChooseList = new ArrayList<>();
        checkFieldList = new ArrayList<>();
        fullList = new ArrayList<>();
        peopleList=new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);

        //loginGetToken();

        initView();

        //配置点击查看大图
        initImageLoader();
        getTypeformService();
    }

    private void initView() {
        Date date = new Date();
        String time = date.toLocaleString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        checkTime = dateFormat.format(date);
        beizhuEdit = (EditText) findViewById(R.id.beizhu_edit);
        backButton = (ImageView) findViewById(R.id.back_button);
        resourceName=findViewById(R.id.resource_name);
        resourceGird=findViewById(R.id.resource_gird);
        resourceEndTime=findViewById(R.id.end_time);
        zzrText=findViewById(R.id.zzr_text);
        isPassview=findViewById(R.id.is_pass);

        oneImageLayout = (FrameLayout) findViewById(R.id.one_image_layout);
        twoImageLayout = (FrameLayout) findViewById(R.id.two_imag_layout);
        oneImage = (ImageView) findViewById(R.id.one_image);
        twoImage = (ImageView) findViewById(R.id.two_image);
        oneImageDelete = (ImageView) findViewById(R.id.one_image_delete);
        twoImageDelete = (ImageView) findViewById(R.id.two_image_delete);

        // mPathView = (LinePathView) findViewById(R.id.path_view);
        //  clearQianmingView = (TextView) findViewById(R.id.clear_qianming_view);
        postDataButton = (TextView) findViewById(R.id.post_data_view);

        photoListView = (RecyclerView) findViewById(R.id.phote_recycle);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        photoListView.setLayoutManager(gridLayoutManager);
        photoAdapter = new MultiTypeAdapter(photoItems);
        photoListView.setHasFixedSize(true);
        photoListView.setNestedScrollingEnabled(false);
        chooseImageViewBinder = new ChooseImageViewBinder(this);
        chooseImageViewBinder.setListener(this);
        photoAdapter.register(ChooseImage.class, chooseImageViewBinder);
        photoListView.setAdapter(photoAdapter);
        assertHasTheSameAdapter(photoListView, photoAdapter);

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
        updateData();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RxViewAction.clickNoDouble(postDataButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (list.size()>0){
                            postMoreImagesService(0);
                        }else {
                            postDataToServiceFromDb();
                        }

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
                                checkusers.add(new planResourceDTOS.CheckuserVOSFirejd(3,peopleList.get(i).getId(),peopleList.get(i).getFullName()));
                            }
                        }
                        zzrText.setText(zzrName.toString());
                        try {
                            db.saveOrUpdate(checkusers);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        peopleListDialog.dismiss();
                        Log.e(TAG, "call: "+zzrName.toString() );
                    }
                });

        oneImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                judgePower();
                if (uriChooseList.size() == 0){  //添加图片
                    addImage();
                }else {         //查看图片
                    showBigImage(0);
                }
            }
        });
        twoImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriChooseList.size() == 1){
                    addImage();
                }else {
                    showBigImage(1);
                }
            }
        });
        oneImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriChooseList.size() == 1){  //只有一张图
                    uriChooseList.remove(0);
                    Glide.with(getApplicationContext()).load(R.drawable.ic_bigphoto).into(oneImage);
                    oneImageDelete.setVisibility(View.GONE);
                    twoImageLayout.setVisibility(View.GONE);
                }else {     //如果有两张图
                    uriChooseList.remove(0);
                    Glide.with(getApplicationContext()).load(uriChooseList.get(0)).into(oneImage);
                    Glide.with(getApplicationContext()).load(R.drawable.ic_bigphoto).into(twoImage);
                    oneImageDelete.setVisibility(View.VISIBLE);
                    twoImageDelete.setVisibility(View.GONE);
                }
            }
        });
        twoImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uriChooseList.remove(1);
                Glide.with(getApplicationContext()).load(R.drawable.ic_bigphoto).into(twoImage);
                twoImageDelete.setVisibility(View.GONE);
            }
        });
        RxViewAction.clickNoDouble(resourceGird)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showtypeChangeDailog();
                    }
                });
        RxViewAction.clickNoDouble(resourceName)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showStationChangeDailog();
                    }
                });
        RxViewAction.clickNoDouble(isPassview)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showIspassChangeDailog();
                    }
                });
        listView = (RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        listView.setHasFixedSize(true);
        listView.setNestedScrollingEnabled(false);
        adapter = new MultiTypeAdapter(items);
        register();
        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);
    }
    private void register() {
        checkFieldProvider = new CheckFieldViewBinder();
        checkFieldProvider.setListener(this);
        adapter.register(CheckField.class, checkFieldProvider);
    }
    private void getresourceName(int choose1) {
        Log.e(TAG, "getresourceName: "+ resourceTypeLists.get(choose1).getApiUrl());
        JSONObject jsonObject =new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource"+resourceTypeLists.get(choose1).getApiUrl()+"/list");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.setBodyContent(jsonObject.toString());
        Log.e(TAG, "resource: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --2-" + result );
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    if (jsonObject1.getString("code").equals("200")) {
                        JSONArray data = jsonObject1.getJSONArray("data");
                        Gson gson = new Gson();
                        stationinfos.clear();
                        stationList.clear();
                        choose2=0;
                        stationinfos = gson.fromJson(String.valueOf(data), new TypeToken<List<stationinfo>>() {
                        }.getType());
                        for (int i = 0; i < stationinfos.size(); i++) {
                            stationList.add(stationinfos.get(i).getName());
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

    private void getTypeformService() {
        JSONObject jsonObject =new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/resourceList/list");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.setBodyContent(jsonObject.toString());
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
                        resourceTypeLists.clear();
                        typeNameList.clear();
                        choose1=0;
                        resourceTypeLists = gson.fromJson(String.valueOf(data), new TypeToken<List<ResourceTypeList>>() {
                        }.getType());
                        for (int i = 0; i < resourceTypeLists.size(); i++) {
                            typeNameList.add(resourceTypeLists.get(i).getName());
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

    private void postMoreImagesService(final int index){
         Log.e(TAG, "postimage: " + index);
        ChooseImage chooseImage = list.get(index);
        try {

            Uri uri = chooseImage.getUri();
            int degree = ImageUtils.readPictureDegree(uri.toString());
            Bitmap photo = ImageUtils.getBitmapFormUri(getApplicationContext(), uri);
            Bitmap shuiYinPhoto = drawTextToBitmap(this, photo,checkTime.replace("T"," ") + "  " + name, "", "", "", "", "", paint, 10, 40);
            evaluate = rotaingImageView(degree, shuiYinPhoto);

        } catch (IOException e) {

        }
        pic1 = ImageUtils.savePhoto(this.evaluate, this.getObbDir().getAbsolutePath(),checkTime + "pic" +index);

        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_HUAWEI + "oa/api/workReport/fileUploadAnByNotToken");
        params.setAsJsonContent(true);
        params.setMultipart(true);
        // params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("file", new File(pic1),null,pic1);
        params.addHeader("Authorization","bearer " + access_token);
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
                        Log.i(TAG, "imgStrArray: " + imgStrArray.length());
                        for (int i = 0; i < imgStrArray.length(); i++) {
                            imgsFirejds.add(new planResourceDTOS.ImgsFirejd(imgStrArray.get(i).toString(),1));
                        }
                        currentPostNum++;
                       /* if (index < list.size()){
                            postMoreImagesService(index + 1);
                        }else {
                            postQianmingService();//上传签名
                        }*/
                        if (currentPostNum >= list.size()){
                            Log.e(TAG, "postdata--3" );
                            //postQianmingService();//上传签名
                            postDataToServiceFromDb();
                        }else {
                            postMoreImagesService(index +1);
                        }


                    }else {
                        Toast.makeText(AddStationActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: 请求失败" );
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
        Log.e(TAG, "postDataToServiceFromDb: 111" );
        //planResourceDTOS.setCheckuserVOS(checkusers);
        planResourceDTOS.setImgs(imgsFirejds);
        planResourceDTOS.setCheckType(2);
        planResourceDTOS.setLatitude(currentResLat);
        planResourceDTOS.setLongitude(currentResLng);
        planResourceDTOS.setName(currentResName);
        planResourceDTOS.setResourceType(currentType);
        planResourceDTOS.setDescription(beizhuEdit.getText().toString());
        planResourceDTOS.setStatus(resStatus);
        List<planResourceDTOS.ItemsFirejd> itemsFirejds = new ArrayList<>();
        for (int i = 0; i < checkFieldList.size(); i++) {
            itemsFirejds.add(new planResourceDTOS.ItemsFirejd(checkFieldList.get(i).getCode(),id, checkFieldList.get(i).state,1));
        }
        planResourceDTOS.setItems(itemsFirejds);
        Log.e(TAG, "postDataToServiceFromDb: itemsFirejds.size() = " +  checkFieldList.size() + "," + itemsFirejds.size());
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("backinfo",planResourceDTOS);
        //设置返回数据
        AddStationActivity.this.setResult(requst_code, intent);
        //关闭Activity
        AddStationActivity.this.finish();
    }

    /*
  * 根据String Path 删除图片
  * */
    public static void deleteImage(String imgPath, Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = context.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }

        if (result) {
         /*   imageList.remove(imgPath);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();*/
            Log.e("UtilsRY ", "DeleteImage: 图片删除成功");
        } else {
            Log.e("UtilsRY ", "DeleteImage: 图片删除失败");
        }
    }


    public static String savePhoto(Bitmap photoBitmap, String path,
                                   String photoName) {
        String localPath = null;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                            fileOutputStream)) { // 转换完成
                        localPath = photoFile.getPath();
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                localPath = null;
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                localPath = null;
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                        fileOutputStream = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localPath;
    }

    /**
     * 压缩后转base64
     * @param filePath
     * @param type
     * @return
     */
    public String compressImage(String filePath, String type) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.toLowerCase().contains("png")) {
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }

        byte[] datas = baos.toByteArray();
        Log.e("size", (datas.length / 1024) + "");
        return Base64.encodeToString(datas,Base64.DEFAULT);

    }

    public  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
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

    public void showDialogProgress(ProgressDialog dialog, String message) {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);
        dialog.show();
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
                        int size = 2 - uriChooseList.size();
                        Matisse.from(AddStationActivity.this)
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

    private void showBigImage(int phoneNum) {
        ArrayList<String> picList = new ArrayList<>();
        String oneUri = "";
        if (phoneNum == 0){
            oneUri = uriChooseList.get(0).toString();
            picList.add(oneUri);
            if (uriChooseList.size() == 2){
                picList.add(uriChooseList.get(1).toString());
            }
        }else {
            oneUri = uriChooseList.get(1).toString();
            picList.add(oneUri);
            picList.add(uriChooseList.get(0).toString());
        }
        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(AddStationActivity.this, picList);
        imagPagerUtil.setContentText("");
        imagPagerUtil.show();


       /* picList.add(oneUri); //点击哪张 把哪张放第一个
        for (int i = 0; i < list.size(); i++) {     //除去点击那张  其他放进去
            if (!oneUri.equals(list.get(i).getUri().toString())){
                picList.add(list.get(i).getUri().toString());
            }
        };
        String content = evaluateEditText.getText().toString();     //放评论
        ImagPagerUtil imagPagerUtil = new ImagPagerUtil(EvaluateActivity.this, picList);
        imagPagerUtil.setContentText(content);
        imagPagerUtil.show();*/
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

    private void judgePower() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权读写手机存储权限", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权读写手机存储权限", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权相机权限", Toast.LENGTH_SHORT).show();
            finish();
            finish();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权定位权限", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            List<Uri> uriList = Matisse.obtainResult(data);

            //  Log.e(TAG, "onActivityResult: " + uriList.get(0).toString());
            //去掉重复图片
            int uriSize = uriList.size();
            int listSize = list.size();
            int index = 0;
            for (int i = 0; i < uriList.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (uriList.get(i).toString().equals(list.get(j).getUri().toString())) {

                        Toast.makeText(this, "不可添加重复图片！", Toast.LENGTH_SHORT).show();
                        uriList.remove(i);
                        if (uriList.size() == 0) {
                            return;
                        }

                    }
                }
            }


            // 判断只能添加五张图片
            if ( (uriList.size() + list.size()) > 9){
                Toast.makeText(this, "最多只能添加9张", Toast.LENGTH_SHORT).show();
                int size =  9 - list.size();
                for (int i = 0; i < size; i++) {
                    ChooseImage chooseImage = new ChooseImage();
                    chooseImage.setUri(uriList.get(i));
                    chooseImage.setAdd(false);
                    list.add(chooseImage);
                    // items.add(evaluateImage);
                }
            }else {
                //不足5张的添加  添加图片按钮
                int size = uriList.size();
                for (int i = 0; i < size; i++) {
                    ChooseImage chooseImage = new ChooseImage();
                    chooseImage.setUri(uriList.get(i));
                    chooseImage.setAdd(false);
                    list.add(chooseImage);
                    // items.add(evaluateImage);
                }
                ChooseImage chooseImage = new ChooseImage();
                chooseImage.setAdd(true);
                // items.add(evaluateImage);
            }
            // assertAllRegistered(adapter,items);;
            // adapter.notifyDataSetChanged();
            updateData();
        }
      /*  if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> uriList = Matisse.obtainResult(data);
            for (int i = 0; i < uriList.size(); i++) {
                uriChooseList.add(uriList.get(i));
            }

            if (uriChooseList.size()>1){  //有两张图
                twoImageLayout.setVisibility(View.VISIBLE);
                twoImageDelete.setVisibility(View.VISIBLE);
                oneImageDelete.setVisibility(View.VISIBLE   );
                Glide.with(this).load(uriChooseList.get(0)).into(oneImage);
                Glide.with(this).load(uriChooseList.get(1)).into(twoImage);
            }else {                     //有一张图
                twoImageLayout.setVisibility(View.VISIBLE);
                twoImageDelete.setVisibility(View.GONE);
                oneImageDelete.setVisibility(View.VISIBLE);
                Glide.with(this).load(uriChooseList.get(0)).into(oneImage);
                Glide.with(this).load(R.drawable.ic_bigphoto).into(twoImage);
            }

        }*/
    }

    /**
     * 检察按钮点击回调
     * @param state
     */
    @Override
    public void onCheckFieldItemClickLinstener(String code, int state) {
        Log.e(TAG, "onCheckFieldItemClickLinstener: "+state );
        for (int i = 0; i < checkFieldList.size(); i++) {
            if (checkFieldList.get(i).getCode().equals(code)) {
                checkFieldList.get(i).state = state;
            }
        }
    }

    private boolean isKeyboardShown(View rootView) {
        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
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

    @Override
    public void onImageAddClickListener(boolean add, Uri uri,String id) {
        if (add){
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            int size = 9 - list.size();
                            Matisse.from(AddStationActivity.this)
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
                                    .forResult(REQUEST_CODE_CHOOSE);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else {
            //点击查看大图
            ArrayList<String> picList = new ArrayList<>();
            String oneUri = uri.toString();
            picList.add(oneUri); //点击哪张 把哪张放第一个
            for (int i = 0; i < list.size(); i++) {     //除去点击那张  其他放进去
                if (!oneUri.equals(list.get(i).getUri().toString())){
                    picList.add(list.get(i).getUri().toString());
                }
            };
            String content = "";     //放评论
            ImagPagerUtil imagPagerUtil = new ImagPagerUtil(AddStationActivity.this, picList);
            imagPagerUtil.setContentText(content);
            imagPagerUtil.show();
        }
    }
    @Override
    public void onImageDelete(Uri uri,String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUri().equals(uri)) {
                list.remove(i);
            }
        }
        updateData();
    }

    @Override
    public void onThumbPictureClick(ImageView i, List<ImageView> imageGroupList, List<String> urlList) {
        vImageWatcher.show(i, imageGroupList, urlList);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void updateData() {
        photoItems.clear();
        if (list==null){
            ChooseImage chooseImage = new ChooseImage();
            chooseImage.setAdd(true);
            photoItems.add(chooseImage);
        }else {
            if (list.size()<9){
                for (int i = 0; i < list.size(); i++) {
                    photoItems.add(list.get(i));
                }
                ChooseImage chooseImage = new ChooseImage();
                chooseImage.setAdd(true);
                photoItems.add(chooseImage);
            }else {
                for (int i = 0; i < list.size(); i++) {
                    photoItems.add(list.get(i));
                }
            }

            assertAllRegistered(photoAdapter,photoItems);
            photoAdapter.notifyDataSetChanged();

        }
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
    private void showtypeChangeDailog() {

        int size=typeNameList.size();
        String[] items = (String[])typeNameList.toArray(new String[size]);
        builder = new AlertDialog.Builder(this).setTitle("资源类型")
                .setSingleChoiceItems(items, choose1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: 类别choose---" + i);
                        choose1 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: "+i );
                        resourceGird.setText(resourceTypeLists.get(choose1).getName());
                        currentType=resourceTypeLists.get(choose1).getCode();
                        getresourceName(choose1);
                    }
                });
        builder.create().show();
    }
    private void showStationChangeDailog() {
        Log.e(TAG, "showStationChangeDailog: "+stationList.size() );
        int size=stationList.size();
        String[] items = (String[])stationList.toArray(new String[size]);
        builder = new AlertDialog.Builder(this).setTitle("资源名称")
                .setSingleChoiceItems(items, choose2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: 类别choose---" + i);
                        choose2 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: "+i );
                        if (stationinfos.size()>0) {
                            resourceName.setText(stationinfos.get(choose2).getName());
                            currentResName = stationinfos.get(choose2).getName();
                            currentResLat = stationinfos.get(choose2).getPosition().getLat();
                            currentResLng = stationinfos.get(choose2).getPosition().getLng();
                            id = stationinfos.get(choose2).getId();
                            initCheckFieldIntoDb();
                        }
                    }
                });
        builder.create().show();
    }
    private void showIspassChangeDailog() {
        String[] items = {"是","否"};
        builder = new AlertDialog.Builder(this).setTitle("是否通过")
                .setSingleChoiceItems(items, choose3, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: 类别choose---" + i);
                        choose3 = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.e(TAG, "onClick: "+i );
                        if (choose3==0){
                            isPassview.setText("是");
                            resStatus=4;
                        }else if (choose3==1){
                            isPassview.setText("否");
                            resStatus=3;
                        }
                    }
                });
        builder.create().show();
    }
    private void initCheckFieldIntoDb() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResourceItemHistory/selectResourceItem");
        params.setConnectTimeout(20000);
        params.addHeader("Authorization","bearer " + access_token);
        params.addParameter("planResourceId",id);
        params.addParameter("planResourceType",currentType);
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
                                initCheckFieldData();
                            }else {

                            }
                            Log.e(TAG, "onSuccess: checkFieldList.size() " + checkFieldList.size() );
                        }catch (Exception e){
                            Log.e(TAG, "onSuccess: error " + e.toString() );
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
    private void initCheckFieldData() {
        Log.e(TAG, "initCheckFieldData: "+checkFieldList.size() );
        items.clear();
        for (int i = 0; i < checkFieldList.size(); i++) {
            items.add(checkFieldList.get(i));
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }
}