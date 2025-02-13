package com.haohai.platform.fireforestplatform.ui.acticity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Route(path = "/forest/launch")
public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = AuthenticationActivity.class.getSimpleName();
    private ProgressDialog loginDialog;
    private String access_token;
//    private String userName = "lixin";//lixin   Xayj1357!
//    private String passWord = "Xayj1357!";
//    private String userName = "admin";
//    private String passWord = "a123456";
    private String userName = "xian";
    private String passWord = "Xian@2023!";
    private String authToken = "";
    @Autowired
    String token;
    @SuppressLint("HandlerLeak")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ARouter.getInstance().inject(this);
        loginDialog = new ProgressDialog(this);
        authToken = getIntent().getStringExtra("authToken");
//        authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjczMzlmMmVlLTdhNjgtNDllNC05Mjc1LTI1NzFkMTU1MjlkMyIsImp0aSI6IjczMzlmMmVlLTdhNjgtNDllNC05Mjc1LTI1NzFkMTU1MjlkMyIsInVzZXJuYW1lIjoibGl4aW4ifQ.Fff4UAnPPijDSI60-B7KUUQaDZGwxlkEHqBSizmIh1I";

        authentication();

    }

    private void authentication() {
        showDialogProgress(loginDialog,"同步中...              ");
//        RequestParams params = new RequestParams("http://10.22.245.145:8090/v4/oauth/check-token");//内网
//        RequestParams params = new RequestParams("http://123.138.59.82:8090/v4/oauth/check-token");//外网
        RequestParams params = new RequestParams("https://yjglj.xa.gov.cn/safety/euip-oauth/v4/oauth/check-token");//外网
        params.setConnectTimeout(6000);
        params.addParameter("access_token",authToken);//token
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String user_name = jsonObject.getString("user_name");
                    if(user_name!=null && !user_name.isEmpty()){
                        pass_();
                    }else{
                        refuse_();
                    }
                }catch (Exception e){
                    refuse_();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                refuse_();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void refuse_() {
        loginDialog.dismiss();
        Toast.makeText(this, "鉴权不通过", Toast.LENGTH_SHORT).show();
        delayFinish(2000);
        Log.e(TAG, "refuse_: " );
    }

    private void pass_() {
        Toast.makeText(this, "同步成功", Toast.LENGTH_SHORT).show();
        loginDialog.dismiss();
        loginToService();
        Log.e(TAG, "pass_: " );
    }


    public void showDialogProgress(ProgressDialog dialog, String message) {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(message);
        dialog.show();
    }

    private void loginToService() {
        final RequestParams params = new RequestParams(RequestUtils.LOGIN_URL + "auth/oauth/token");
        // params.addBodyParameter("reqJson", jsonObject.toString());
        params.addParameter("username",userName);
        params.addParameter("password",passWord);
        params.addParameter("grant_type","password");
        params.addParameter("client_id","client_password");
        params.addParameter("client_secret","123456");
        params.setConnectTimeout(10000);
        Log.e(TAG, "loginGetToken: --"  + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    access_token = jsonObject.getString("access_token");

                    getUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
                if (ex.toString().contains("400")) {
                    Toast.makeText(AuthenticationActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }else  if (ex.toString().contains("401")) {
                    Toast.makeText(AuthenticationActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AuthenticationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                }

                loginDialog.dismiss();
                delayFinish(3000);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void delayFinish(int delayMillis){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },delayMillis);
    }

    private void goMain() {
        if(permissionCount == 4 && permissionMain){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loginDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Log.e(TAG, "goMain: userPermission = " + userPermission.getPermission() + CommonData.hasMainMap + CommonData.hasMainVideo + CommonData.hasMainApp + CommonData.hasMainMy );
                    finish();
                }
            },3000);

        }
    }

    private void getUserInfo() {
        final RequestParams params = new RequestParams(RequestUtils.LOGIN_URL + "auth/api/auth/user/get/userinfo");
        params.setConnectTimeout(10000);
        params.addHeader("Authorization","bearer " + access_token);
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e(TAG, "getUserInfo:-- " + params );
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("200")){
                        JSONArray data = jsonObject.getJSONArray("data");
                        JSONObject userJsonObj = data.getJSONObject(0);
                        String id = userJsonObj.getString("id");
                        String userCode = userJsonObj.getString("userCode");
                        String fullName = userJsonObj.getString("fullName");
                        String email = userJsonObj.getString("email");
                        String phone = userJsonObj.getString("phone");
                        String sex = userJsonObj.getString("sex");
                        String entryTime = userJsonObj.getString("entryTime");
                        String birthday = userJsonObj.getString("birthday");
                        String type = userJsonObj.getString("type");
                        String isSuperAdmin = userJsonObj.getString("isSuperAdmin");
                        String comment = userJsonObj.getString("comment");
                        String groupId = userJsonObj.getString("groupId");

                        String gridNo = userJsonObj.getString("gridNo");
                        String bkchar2 = userJsonObj.getString("bkchar2");
                        String money = userJsonObj.getString("money");
                        String lockMoney = userJsonObj.getString("lockMoney");
                        String groupName = userJsonObj.getString("groupName");
                        String headUrl = userJsonObj.getString("headUrl");
                        String state = userJsonObj.getString("state");

                        User user = new User(id, userCode,userName, passWord, fullName, email, phone, sex, entryTime, birthday, type, isSuperAdmin, comment, groupId,
                                gridNo, bkchar2, money, lockMoney, groupName, state, 1, access_token,headUrl);
                        user.isShangchuan = false;
                        user.setIsyunyin(0);
                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();

                        try {
                            db.delete(User.class);
                            db.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        loginDialog.dismiss();
                        Set<String> tagSet = new LinkedHashSet<String>();
                        tagSet.add(gridNo);
                        Log.e(TAG, "gridNo: "+gridNo);
                        tagSet.add(id);
                        XGPushManager.setTags(getApplicationContext(),"setTag",tagSet);
                        //开启华为推送
                        XGPushConfig.enableOtherPush(getApplicationContext(), true);
                        XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
                            @Override
                            public void onSuccess(Object data, int flag) {
                                //token在设备卸载重装的时候有可能会变
                                Log.d("TPush", "注册成功，设备token为：" + data);
                            }

                            @Override
                            public void onFail(Object data, int errCode, String msg) {
                                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                            }
                        });
                        postPermissions();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                loginDialog.dismiss();
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
     * 获取按钮权限
     */
    private void postPermissions() {
        showDialogProgress(loginDialog,"正在获取权限...");
        DbConfig dbConfig = new DbConfig(this);
        dbManager = dbConfig.getDbManager();
        userPermission = dbConfig.getUser();
        userPermission.setPermission("");
        permissionCount = 0;
        String[] menuIdList = {"app-map","app-video","app-application","app-setting"};//获取当前用户菜单：/auth/api/auth/auth/list/menu/by/user
        for (int i = 0; i < menuIdList.length; i++) {
            postPer(menuIdList[i]);
        }
        postMainPer();
    }


    private void postMainPer() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST__URL_HLJ + "auth/api/auth/auth/user/auth");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e(TAG, "postPermissions: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: permissions postMainPer" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    JSONObject obj = (JSONObject) data.get(0);
                    JSONArray dataList = obj.getJSONArray("menuDTOS");
                    /*CommonData.hasMainMap = false;
                    CommonData.hasMainVideo = false;
                    CommonData.hasMainApp = false;
                    CommonData.hasMainMy = false;*/
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject o = (JSONObject) dataList.get(i);
                        String menuCode = o.getString("menuCode");
                        if(Objects.equals(menuCode, "app-map")){
                            CommonData.hasMainMap = true;
                        }
                        if(Objects.equals(menuCode, "app-video")){
                            CommonData.hasMainVideo = true;
                        }
                        if(Objects.equals(menuCode, "app-application")){
                            CommonData.hasMainApp = true;
                        }
                        if(Objects.equals(menuCode, "app-setting")){
                            CommonData.hasMainMy = true;
                        }
                    }
                    Log.e(TAG, "handleMessage: getQcl" + CommonData.hasMainMap + CommonData.hasMainVideo + CommonData.hasMainApp + CommonData.hasMainMy );
                    if(!CommonData.hasMainMap && !CommonData.hasMainVideo && !CommonData.hasMainApp && !CommonData.hasMainMy){
                        CommonData.hasMainMy = true;
                    }
                    permissionMain = true;
                    DbConfig dbConfig = new DbConfig(AuthenticationActivity.this);
                    User user = dbConfig.getUser();
                    user.setHasMainMap(CommonData.hasMainMap);
                    user.setHasMainVideo(CommonData.hasMainVideo);
                    user.setHasMainApp(CommonData.hasMainApp);
                    user.setHasMainMy(CommonData.hasMainMy);
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.delete(User.class);
                        db.saveOrUpdate(user);

                        goMain();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonData.hasMainMap = true;
                    CommonData.hasMainVideo = true;
                    CommonData.hasMainApp = true;
                    CommonData.hasMainMy = true;
                    Toast.makeText(AuthenticationActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
                    loginDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(AuthenticationActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
                loginDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private DbManager dbManager;
    private User userPermission;
    private int permissionCount;
    private boolean permissionMain = false;
    private void postPer(String id) {
        RequestParams params = new RequestParams(RequestUtils.REQUEST__URL_HLJ + "auth/api/auth/auth/list/element/from/menu");
        params.addParameter("menuCode",id);
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        Log.e(TAG, "postPermissions: " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: permissions" + result );
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray dataList = object.getJSONArray("data");
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject o = (JSONObject) dataList.get(i);
                        String code = o.getString("elementCode");
                        userPermission.addPermission(code+"_");
                    }
                    permissionCount++;
                    if(permissionCount == 4){
                        try {
                            Log.e(TAG, "onSuccess: permissions ==>" + userPermission.getPermission() );
                            dbManager.delete(User.class);
                            dbManager.saveOrUpdate(userPermission);
                            goMain();

                        } catch (DbException e) {
                            e.printStackTrace();
                            Toast.makeText(AuthenticationActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
                            loginDialog.dismiss();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AuthenticationActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
                    loginDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(AuthenticationActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
                loginDialog.dismiss();
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
