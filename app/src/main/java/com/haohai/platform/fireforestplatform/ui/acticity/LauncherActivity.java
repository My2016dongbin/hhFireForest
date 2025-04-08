package com.haohai.platform.fireforestplatform.ui.acticity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Requestaddress;
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
import java.util.Set;

public class LauncherActivity extends AppCompatActivity {

    private static final int GO_GUIDE = 101;
    private static final int GO_MAIN = 102;
    private final int REQUEST_CODE = 688;
    private static final String TAG = LauncherActivity.class.getSimpleName();
    public boolean isHasPermission = true;
    private String access_token;
    private User user;
    private ProgressDialog loginDialog;
    private Requestaddress requestaddress;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_GUIDE:
                    Log.e(TAG, "handleMessage:11 " );
                    //        startService(new Intent(getApplicationContext(), TrackService.class));
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    User user = new DbConfig(getApplicationContext()).getUser();
                 //     Log.e(TAG, "handleMessage: " + user.getIsLogin());
                    if (user == null){      //用户不存在
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                         finish();
                    }else {
                        if (user.getIsLogin() == 0) {       //用户未登录
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            //每次打开都重新更新token后进行登录
                            loginToService();
                        }
                    }
                    break;
                case GO_MAIN:
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;

            }

        }
    };

    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPowerManager;

    @SuppressLint("InvalidWakeLockTag")
    public void turnOnScreen() {
        // turn on screen
        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
            mWakeLock.acquire();
            mWakeLock.release();
        } catch (Exception e) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        turnOnScreen();
        setContentView(R.layout.activity_launcher);
        ipSetting();
        user = new DbConfig(this).getUserOut();
        loginDialog = new ProgressDialog(this);
        requestaddress = new DbConfig(this).getRequestaddress();
        //权限获取
        requestPower();
    }

    private void ipSetting() {
        SharedPreferences sp = getSharedPreferences("haohai_file", Context.MODE_PRIVATE);
        String ip = sp.getString("ip", RequestUtils.IP);
        int port = sp.getInt("port", RequestUtils.PORT);
        CommonData.lat = sp.getFloat("latitude", 0);
        CommonData.lng = sp.getFloat("longitude", 0);
        Log.e(TAG, "ipSetting: latLng " + CommonData.lat + "，" + CommonData.lng );
        RequestUtils.IP = ip;
        RequestUtils.PORT = port;
        Log.e(TAG, "ipSetting: ip = " + ip + ",port = " + port );
        Log.e(TAG, "ipSetting: REQUEST_URL_BASE = " + RequestUtils.REQUEST_URL_BASE() );
    }

    private void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.RECORD_AUDIO
                        }, 1);
                //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.RECORD_AUDIO
                        }, 1);
            }
        } else {
            //   handler.sendEmptyMessage(GO_GUIDE);
           // handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);

            clockPermission();
            /*if (user !=null){
                loginToService();
            }else{
                handler.sendEmptyMessageDelayed(GO_GUIDE,3000);
            }*/
        }
    }

    private void clockPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String pn= getPackageName();
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(pn)) {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + pn));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }else{
                    handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       /* Log.e(TAG, "onRequestPermissionsResult:requestCode --" + requestCode);

        Log.e(TAG, "onRequestPermissionsResult: permissions--" + permissions.toString());
        Log.e(TAG, "onRequestPermissionsResult:  permissions.length--" +  permissions.length);
        Log.e(TAG, "onRequestPermissionsResult: grantResults--" + grantResults.toString());
        Log.e(TAG, "onRequestPermissionsResult: grantResults.length--" + grantResults.length);
*/
        for (int i = 0; i < permissions.length; i++) {

            Log.e(TAG, "onRequestPermissionsResult: permissions------" + permissions[i]);
        }


        if (requestCode == 1) {

            boolean isPremission = true;
            for (int i = 0; i < grantResults.length; i++) {
                Log.e(TAG, "onRequestPermissionsResult: permissions++++++" + grantResults[i]);
                if (grantResults[i] == -1) {
                    isPremission = false;
                }
                //  Log.e(TAG, "onRequestPermissionsResult: permissions++++++" +  grantResults[i]);
            }

            if (isPremission) {          //有权限
                //handler.sendEmptyMessageDelayed(GO_GUIDE,3000);
                clockPermission();
            } else {
                //judgePower();//TODO 去除权限限制
                handler.sendEmptyMessageDelayed(GO_GUIDE,3000);
            }
        }
    }
    private void judgePower() {

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
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权定位权限", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void loginToService() {

        final RequestParams params = new RequestParams(requestaddress.getRequstUrl() + "auth/oauth/token");
        // params.addBodyParameter("reqJson", jsonObject.toString());
        params.addParameter("username",user.getUserName());
        params.addParameter("password",user.getUserPasswd());
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
                    user.setToken(access_token);
                    DbConfig dbConfig = new DbConfig(getApplicationContext());
                    DbManager db = dbConfig.getDbManager();

                    try {
                        db.delete(User.class);
                        db.saveOrUpdate(user);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    doLogin();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
                if (ex.toString().contains("400")) {
                    Toast.makeText(LauncherActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }else  if (ex.toString().contains("401")) {
                    Toast.makeText(LauncherActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LauncherActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                }

                loginDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if(access_token!=null && !access_token.isEmpty()){
                    postPer("app-video");
                }

            }
        });
    }

    private DbManager dbManager;
    private User userPermission;
    private void postPer(String id) {
        DbConfig dbConfig = new DbConfig(LauncherActivity.this);
        dbManager = dbConfig.getDbManager();
        userPermission = dbConfig.getUser();
        userPermission.setPermission("");
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/auth/list/element/from/menu");
        params.addParameter("menuCode",id);
        params.addHeader("Authorization", "bearer " + new DbConfig(LauncherActivity.this).getUser().getToken());
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
                    try {
                        Log.e(TAG, "onSuccess: permissions ==>" + userPermission.getPermission() );
                        dbManager.delete(User.class);
                        dbManager.saveOrUpdate(userPermission);

                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                       /* ARouter.getInstance().build(RouteUtils.LoginToMain)
                                .withInt("state",1)
                                .navigation();*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LauncherActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                handler.sendEmptyMessageDelayed(GO_MAIN, 1000  );
            }
        });
    }

    /**
     * 登录环信
     */
    private void doLogin() {
        CommonData.testId = user.getId();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/im/user/getToken");
        params.addHeader("Authorization","bearer " + access_token);
        params.addParameter("accid",user.getId());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(TAG, "onSuccess: doLogin token " + result );
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    JSONObject obj = (JSONObject) data.get(0);
                    JSONObject model = obj.getJSONObject("info");
                    String accid = model.getString("accid");
                    String token = model.getString("token");
                    CommonData.wyyAccId = accid;
                    CommonData.wyyToken = token;

                    LoginInfo info = new LoginInfo(accid,token);
                    RequestCallback<LoginInfo> callback =
                            new RequestCallback<LoginInfo>() {
                                @Override
                                public void onSuccess(LoginInfo param) {
                                    // your code
                                    Log.e(TAG, "onSuccess: 网易云信login" +param);
                                }

                                @Override
                                public void onFailed(int code) {
                                    Log.e(TAG, "网易云信login"+"onFailed code " + code);
                                    if (code == 302) {
                                        // your code
                                    } else {
                                        // your code
                                    }
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    // your code
                                    Log.e(TAG, "网易云信login"+"onException code " + exception);
                                }
                            };

                    //执行手动登录
                    NIMClient.getService(AuthService.class).login(info).setCallback(callback);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: 回调");
        switch (requestCode) {
            default:
                super.onActivityResult(requestCode, resultCode, data);
                handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
        }
    }
}
