package com.haohai.platform.fireforestplatform.ui.acticity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.db.UserMenu;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.AESUtils3;
import com.ruyiruyi.rylibrary.utils.AesUtil;
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
    private final int REQUEST_CODE = 688;
    private static final String TAG = LauncherActivity.class.getSimpleName();
    public boolean isHasPermission = true;
    private String access_token;
    private User user;
    private ProgressDialog loginDialog;
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
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
                            /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();*/
                            //每次打开都重新更新token后进行登录
                            loginToService();
                        }
                    }
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        user = new DbConfig(this).getUserOut();
        loginDialog = new ProgressDialog(this);
        //权限获取
        requestPower();
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
                        != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 1);
                Toast.makeText(LauncherActivity.this, "1111", Toast.LENGTH_SHORT).show();
                //这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 1);
            }
        } else {
            //   handler.sendEmptyMessage(GO_GUIDE);
           // handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
//
//            if (user !=null){
//                loginToService();
//            }

            clockPermission();
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
            Intent batterySaver = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);//TODO
            startActivity(batterySaver);
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
                judgePower();
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
            finish();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权定位权限", Toast.LENGTH_SHORT).show();

        }
    }
    private void loginToServiceOld() {

        final RequestParams params = new RequestParams(RequestUtils.LOGIN_URL + "auth/oauth/token");
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
                    handler.sendEmptyMessageDelayed(GO_GUIDE, 1000  );
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

            }
        });
    }


    private String aesPassword;//AES私钥
    private final String rsaPassword_g = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCq6jRoYYLf3Vm+ownCz10nrxdojIIVuZlaqxrSOAehPHVJZ038P1LjsbimfaklYa2q4jpCFFgQG4ttQ2h/iliqFt5ZTTHYSYPREg6opGs9RlGb2+mVPSHVi8BTFygLFBFJzYiCIQ21Lhus+UBhWjivj/pL0gvCjRddXcX6bpwJSwIDAQAB";//RSA公钥
    //private final String rsaPassword_g = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFPmKB1bh6oGLagic663u/xWNkrtDbLTMeJuROwe5w9ipWwSDIzqos+2p7IukUPi7yZoYv080m8Wu4RsMOBzCUb0H9TSer68KW1Wqky75DtKY+UKj1Y3wU8H+CxVXoN10q3GCgjaEdtQZabcjjwoUvIHT7xTCzB0P3TxjZ7/fscwIDAQAB";//RSA公钥
    private final String rsaPassword_s = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIU+YoHVuHqgYtqCJzrre7/FY2Su0NstMx4m5E7B7nD2KlbBIMjOqiz7ansi6RQ+LvJmhi/TzSbxa7hGww4HMJRvQf1NJ6vrwpbVaqTLvkO0pj5QqPVjfBTwf4LFVeg3XSrcYKCNoR21BlptyOPChS8gdPvFMLMHQ/dPGNnv9+xzAgMBAAECgYAtPqfYiqggC8JFjJihq0DUN8SudaY6JrkK7g3sqHG9Lfnmh6IIThT/PUhFE++tjggHC8VZDETHiocXhf/KDary3BegSUIAfsGkr81tlTfhBjMsCOLH22LeZw//XIx7OplStK/CetX2727Ds5fGol9C+e6D1WOSwCJOO+jLlxvAQQJBAOTs0qzjpmHLZPaTgoFq62fDdr6lBHq+ReIBGLyPX9Ezvtp81r6/KVTeaB22LxtTpO2OJXXcatR4dfMH+lqSjXECQQCVAJxulw6eNdOYAAwyMoLRCVBS66j2ylmhEUN9uPAr8o7XC5PB8pF/fsMk5Q6WDe6uHE7v7PSd8ypSQMeoRPYjAkEArznm+Jc4H9sD6Ql393/TuJURK1Q8XYePDjMwsAQ+n28wQyUTauX/yQqEP1nYLN6Ve5A2dETHMOMTxXbx1qoewQJAUgVgF1B374dZztZX4FoFwOQLn1myTQfehtdl+5MOQmLnVmE9GQpaJYC2E10zxk4tERLsMQ6TKU9uAJFAVtR/WQJAY+JHFUxmIZWb7YyKu60bYYI/beje9fNmNppyMtZMrwEYsvtiU5y4GnmWZVyRNNYCgghXT3KdUsD09F4YHRTiwA==";//RSA私钥
    //AES加密
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loginToService() {
//        aesPassword = AESUtils3.getKey();
//        aesPassword = "dec3c3dc6928c04a0469ee92a7bb590132";
        aesPassword = "nPhPGOMzoMTdN9wq";
        if (user.getUserName().isEmpty()){
            return;
        }
        if (user.getUserPasswd().isEmpty()){
            return;
        }
        String user_ = null;
        String pass_ = null;
        try {
            user_ = AesUtil.encrypt(user.getUserName(),aesPassword);
            pass_ = AesUtil.encrypt(user.getUserPasswd(),aesPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final RequestParams params = new RequestParams(RequestUtils.LOGIN_URL + "auth/api/auth/user/encryptedLogin");
        String str = null;
        String str_de = null;
        try {
            str = AESUtils3.EncryptRSA(aesPassword, rsaPassword_g);
            str_de = AESUtils3.DecryptRSA(str, rsaPassword_s);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "loginToService: e = " + e );
        }
        Log.e(TAG, "loginToService: username_ " + user_);
        Log.e(TAG, "loginToService: password_ " + pass_);
        JSONObject object = new JSONObject();
        try {
            object.put("aeskey",str);
            object.put("userName",user_);
            object.put("userPasswd",pass_);
            object.put("grant_type","password");
            object.put("client_id","client_password");
            object.put("client_secret","123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.setBodyContent(object.toString());
        Log.e(TAG, "loginToService: object = " + object.toString() );
        params.setConnectTimeout(10000);
        Log.e(TAG, "loginGetToken: --"  + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: --1-" + result );
                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if(code == 200){
                        JSONArray data = jsonObject.getJSONArray("data");
                        JSONObject obj = (JSONObject) data.get(0);
                        access_token = obj.getString("access_token");
                        user.setToken(access_token);
                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();
                        try {
                            db.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, "onSuccess:更新token= " + access_token);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if(access_token!=null && !access_token.isEmpty()){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_CANCELED) {
                    //Toast.makeText(this, "您拒绝了后台锁定，位置上传服务可能出现异常", Toast.LENGTH_LONG).show();
                }
                handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
                break;
            default:
                Log.e(TAG, "onActivityResult:2 ");
                super.onActivityResult(requestCode, resultCode, data);
                handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
        }
    }
}
