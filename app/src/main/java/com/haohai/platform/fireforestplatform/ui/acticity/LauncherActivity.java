package com.haohai.platform.fireforestplatform.ui.acticity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.utils.AuthTypeUtil;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.ui.AESUtils3;
import com.ruyiruyi.rylibrary.utils.AesUtil;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.vsg.trustaccess.sdks.VSGService;
import com.vsg.trustaccess.sdks.logic.AuthStateManager;
import com.vsg.trustaccess.sdks.logic.TunnelStateManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LauncherActivity extends AppCompatActivity implements AuthStateManager.AuthStateListener,TunnelStateManager.TunnelTotalStateListener,VSGService.KeyCertStateListener {

    private static final String ACTION_MSG_BD_NUMBER_REQUEST = "android.intent.action.beidou.msg.number.request";
    private static final int GO_GUIDE = 101;
    private final int REQUEST_CODE = 688;
    private static final String TAG = LauncherActivity.class.getSimpleName();
    public boolean isHasPermission = true;
    private User user;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_GUIDE:
                    Log.e(TAG, "handleMessage:11 " );
                    //        startService(new Intent(getApplicationContext(), TrackService.class));
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    user = new DbConfig(getApplicationContext()).getUser();
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

            }

        }

    };


    private void getTokenFromService() {
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
                    String access_token = jsonObject.getString("access_token");
                    user.setToken(access_token);
                    DbConfig dbConfig = new DbConfig(getApplicationContext());
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.saveOrUpdate(user);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    Log.e(TAG, "onSuccess:更新token= " + access_token);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String access_token;
    private String aesPassword;//AES私钥
//    private final String rsaPassword_g = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFPmKB1bh6oGLagic663u/xWNkrtDbLTMeJuROwe5w9ipWwSDIzqos+2p7IukUPi7yZoYv080m8Wu4RsMOBzCUb0H9TSer68KW1Wqky75DtKY+UKj1Y3wU8H+CxVXoN10q3GCgjaEdtQZabcjjwoUvIHT7xTCzB0P3TxjZ7/fscwIDAQAB";//RSA公钥
//    private final String rsaPassword_s = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIU+YoHVuHqgYtqCJzrre7/FY2Su0NstMx4m5E7B7nD2KlbBIMjOqiz7ansi6RQ+LvJmhi/TzSbxa7hGww4HMJRvQf1NJ6vrwpbVaqTLvkO0pj5QqPVjfBTwf4LFVeg3XSrcYKCNoR21BlptyOPChS8gdPvFMLMHQ/dPGNnv9+xzAgMBAAECgYAtPqfYiqggC8JFjJihq0DUN8SudaY6JrkK7g3sqHG9Lfnmh6IIThT/PUhFE++tjggHC8VZDETHiocXhf/KDary3BegSUIAfsGkr81tlTfhBjMsCOLH22LeZw//XIx7OplStK/CetX2727Ds5fGol9C+e6D1WOSwCJOO+jLlxvAQQJBAOTs0qzjpmHLZPaTgoFq62fDdr6lBHq+ReIBGLyPX9Ezvtp81r6/KVTeaB22LxtTpO2OJXXcatR4dfMH+lqSjXECQQCVAJxulw6eNdOYAAwyMoLRCVBS66j2ylmhEUN9uPAr8o7XC5PB8pF/fsMk5Q6WDe6uHE7v7PSd8ypSQMeoRPYjAkEArznm+Jc4H9sD6Ql393/TuJURK1Q8XYePDjMwsAQ+n28wQyUTauX/yQqEP1nYLN6Ve5A2dETHMOMTxXbx1qoewQJAUgVgF1B374dZztZX4FoFwOQLn1myTQfehtdl+5MOQmLnVmE9GQpaJYC2E10zxk4tERLsMQ6TKU9uAJFAVtR/WQJAY+JHFUxmIZWb7YyKu60bYYI/beje9fNmNppyMtZMrwEYsvtiU5y4GnmWZVyRNNYCgghXT3KdUsD09F4YHRTiwA==";//RSA私钥

    private final String rsaPassword_g = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCq6jRoYYLf3Vm+ownCz10nrxdojIIVuZlaqxrSOAehPHVJZ038P1LjsbimfaklYa2q4jpCFFgQG4ttQ2h/iliqFt5ZTTHYSYPREg6opGs9RlGb2+mVPSHVi8BTFygLFBFJzYiCIQ21Lhus+UBhWjivj/pL0gvCjRddXcX6bpwJSwIDAQAB";//RSA公钥
    private final String rsaPassword_s = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKrqNGhhgt/dWb6jCcLPXSevF2iMghW5mVqrGtI4B6E8dUlnTfw/UuOxuKZ9qSVhrariOkIUWBAbi21DaH+KWKoW3llNMdhJg9ESDqikaz1GUZvb6ZU9IdWLwFMXKAsUEUnNiIIhDbUuG6z5QGFaOK+P+kvSC8KNF11dxfpunAlLAgMBAAECgYBgoYvB3Ce/ZAmCc/Fn2A+mCSNl89L0b3vZvFWstwrxSRpSxvpbfH3jyC5Ky08fmGs06zTe+VuUt84Ll4n0WgaoK+Pj1JwZfIVW/a7qNo1pD3XUK42jERIrMakIf/7/Ii6cX0AjQxBf/0k1UmrTxtmh46LRbwGZA9W7ctt20z+IwQJBAPeKRZOXskDG7RePJyUvcoSQ3ch/P28/2KPeomcTaYYoqne9o3Kvg+iCAIRfHsVNFodXmwce8y/l1MnY/vSRxGECQQCwwYni9cS5Ih/57qNgKM1CQSuBVRQoHrBSCYM/OaYSVCoXCMcuoSlEQ/MAEWvp6YprVv6vqzHj/0Z7Z/xgey0rAkEAvgHb8DOTttc67EeM06U88PbF1n2eMoW+g+KDpD0pVbpnRyxAhuqkhNctEG53Dxlh/pdHP0sJfi2bjShMY2x0YQJAcyfUNppZ7SePX5yasgZDG9wrhNoyBKVhyEDMUj+zs5NDzLf6VKXIpeIDCdNP1BhEBwSpbzeAjIL+n12y7gSx+wJAYxXmpRyGZMEJuad97lee2wtqekqfDtBmg1sFv0XiKQYCRTkaCIkzWsw75AGyBEQ4ZzCXKC7oO3AJfrWs7X1/wg==";//RSA私钥
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

    protected AuthStateManager mStateManager;
    private  final int PREPARE_VPN_SERVICE = 0;
    private TunnelStateManager mTunnelStateManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        setContentView(R.layout.activity_launcher);
        //权限获取
        requestPower();
        //通知权限
        requestNotification();

        ///查询本机北斗卡号
        Bundle simBundle = new Bundle();
        simBundle.putInt("FrameNum", 0);
        Intent simIntent = new Intent(ACTION_MSG_BD_NUMBER_REQUEST);
        simIntent.putExtras(simBundle);
        sendBroadcast(simIntent);

        try{
            CommonData.icNumber = new DbConfig(LauncherActivity.this).getUser().getIcNumber();
        }catch (Exception e){
        }

        mStateManager = AuthStateManager.getStateManager();
        mTunnelStateManager = TunnelStateManager.getStateManager();
        mTunnelStateManager.registerTotalStateListener(this);

    }

    private void requestNotification() {

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
                Toast.makeText(LauncherActivity.this, "1111", Toast.LENGTH_SHORT).show();
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
            //TODO AuthTypeUtil.checkNetworkConnectivity(LauncherActivity.this);
            ///handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
            clockPermission();
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
              // handler.sendEmptyMessageDelayed(GO_GUIDE,3000);
                //TODO AuthTypeUtil.checkNetworkConnectivity(LauncherActivity.this);
                ///handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
                clockPermission();
            } else {
                judgePower();
            }
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            isHasPermission = false;
            Toast.makeText(this, "请授权录音权限", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
        if(mTunnelStateManager != null){
            mTunnelStateManager.unregisterTotalStateListener(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " );
        if(mStateManager != null){ mStateManager.unregisterListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " );
        mStateManager.registerListener(LauncherActivity.this);

    }


    @Override
    public void keyCertState(VSGService.KeyCertType keyCertType, int container) {
        VSGService.getInstance().unregisterKeyCertStateListener();
        /*
         ****************可以使用Key中证书*****************************
         * KEYCERTTYPE_RSA_ENC：国际标准，使用的加密证书
         * KEYCERTTYPE_RSA_SIGN：国际标准，使用的签名证书
         * KEYCERTTYPE_SM2_SIGN_ENC：国密标准，需要双证书(加密和签名同时存在)
         * ****************不可以使用卡中证书*****************************
         * KEYCERTTYPE_KEYNOTEXIST:Key不存在
         * KEYCERTTYPE_CERTNOTEXIST:证书不存在
         * KEYCERTTYPE_SM2_SIGN:国密签名证书存在
         * KEYCERTTYPE_SM2_ENC:国密加密证书存在
         * */
        Log.e(TAG, "keyCertState: 证书");
        switch (keyCertType) {
            case KEYCERTTYPE_RSA_ENC:
                Toast.makeText(getApplicationContext(),"国际加密证书存在，容器号："+container, Toast.LENGTH_SHORT).show();
                break;
            case KEYCERTTYPE_RSA_SIGN:
                Toast.makeText(getApplicationContext(),"国际签名证书存在，容器号："+container, Toast.LENGTH_SHORT).show();
                break;
            case KEYCERTTYPE_SM2_SIGN_ENC:
                Toast.makeText(getApplicationContext(),"国密双证书存在，容器号："+container, Toast.LENGTH_SHORT).show();
                break;
            case KEYCERTTYPE_SM2_SIGN:
                Toast.makeText(getApplicationContext(),"国密签名证书存在", Toast.LENGTH_SHORT).show();
                return;
            case KEYCERTTYPE_SM2_ENC:
                Toast.makeText(getApplicationContext(),"国密加密证书存在", Toast.LENGTH_SHORT).show();
                break;
            case KEYCERTTYPE_KEYNOTEXIST:
                Toast.makeText(getApplicationContext(),"KEY不存在！", Toast.LENGTH_SHORT).show();
                return;
            case KEYCERTTYPE_CERTNOTEXIST:
                Toast.makeText(getApplicationContext(),"证书不存在！", Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
    }

    @Override
    public void tunnelTotalStateChanged() {
        TunnelStateManager.TunnelState state = mTunnelStateManager.getTunnelState();
        TunnelStateManager.TunnelErrorState errorState = mTunnelStateManager.getTunnelErrorState();
        Log.e(TAG, "tunnelstate:"+state+",error:"+errorState);
        if(reportErrorTunnelState(errorState)){
            return;
        }
        switch (state){
            case DISABLED:
                Toast.makeText(getApplicationContext(), "未连接", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTING:
                Toast.makeText(getApplicationContext(), "连接中", Toast.LENGTH_SHORT).show();
                break;
            case CONNECTED:
             //   Toast.makeText(getApplicationContext(), "已连接", Toast.LENGTH_SHORT).show();
                break;
            case DISCONNECTING:
                Toast.makeText(getApplicationContext(), "断开连接中", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Vpn 回调接口
     */
    @Override
    public void authStateChanged() {
        AuthStateManager.AuthState state = mStateManager.getAuthState();
        Log.e(TAG, "authStateChanged: " +state);

        int errorCode = mStateManager.getAuthErrorCode();
        if(reportErrorState(errorCode)){ /*具体实现可以参考demo*/

            return;
        }
        switch (state){
            case CONNECTING_SEVER:
                Log.e(TAG, "authStateChanged: 0" );
                break;
            case CONNECTING_SERVER_SUCCESS:
                Log.e(TAG, "authStateChanged: 1" );
                AuthTypeUtil.userPasswordAuth(LauncherActivity.this, true);
                break;
            case MODIFY_PASSWD_SUCCESS:
                Log.e(TAG, "authStateChanged: 2" );
                Toast.makeText(getApplicationContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
                break;
            case NEED_PASSWORD_AUTH:
                Log.e(TAG, "authStateChanged: 3" );
                AuthTypeUtil.userPasswordAuth(LauncherActivity.this,false);
                break;
            case NEED_CERT_AUTH:
                Log.e(TAG, "authStateChanged: 4" );
                AuthTypeUtil.certificateAuth(LauncherActivity.this,false);
                break;
            case NEED_DYNAMIC_TOKEN:
                Log.e(TAG, "authStateChanged: 5" );
                //  startActivity(new Intent(MainActivity.this,DynamicTokenActivity.class));
                break;
            case NEED_SMS_AUTH:
                Log.e(TAG, "authStateChanged: 6" );
                //  startActivity(new Intent(MainActivity.this,SmsActivity.class));
                break;
            case NEED_TERMINAL_AUTH:
                Log.e(TAG, "authStateChanged: 7" );
                //     permissionRequest(mReadPhoneStatePermissions);
                AuthTypeUtil.terminalAuth(LauncherActivity.this,false);
                break;
            case NEED_COMMIT_TERMINAL_INFO:
                Log.e(TAG, "authStateChanged: 8" );
                /*直接提交终端信息*/
                AuthTypeUtil.commitTerminalInfoAuth(LauncherActivity.this,false);
                break;
            case NEED_MODIFY_PASSWORD:
                Log.e(TAG, "authStateChanged: 9" );
                /*Intent intent = new Intent(MainActivity.this,PwdChangeActivity.class);
                intent.putExtra(FIRSTPWDCHANGEACTION,true);
                startActivity(intent);*/
                break;
            case AUTH_SUCCESS:
                Log.e(TAG, "authStateChanged: 10" );
                //认证成功后 跳转进首页
              //  handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
                Toast.makeText(getApplicationContext(), "认证成功", Toast.LENGTH_SHORT).show();
                break;
            case GET_INTERGRATION_XML:
                Log.e(TAG, "authStateChanged: 11" );
                break;
            case GET_INTERGRATION_XML_SUCCESS:
                Log.e(TAG, "authStateChanged: 12" );
                Toast.makeText(getApplicationContext(), "获取资源成功", Toast.LENGTH_SHORT).show();
                if(!VSGService.getInstance().isHaveAccessResource()){
                    Toast.makeText(getApplicationContext(), "没有可访问的资源", Toast.LENGTH_SHORT).show();
                    return;
                }
                prepareVPNService();
                break;
            case SHARED_LOGIN_FAILED:
                Log.e(TAG, "authStateChanged: 13" );
                //TODO AuthTypeUtil.checkNetworkConnectivity(LauncherActivity.this);
                handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
                break;
            case SHARED_LOGIN_SUCCESS:
                Log.e(TAG, "authStateChanged: 14" );
                break;
            default:
                break;
        }
    }
    private boolean reportErrorTunnelState(TunnelStateManager.TunnelErrorState error){
        if (error == TunnelStateManager.TunnelErrorState.NO_ERROR)
        {
            return false;
        }

        switch (error)
        {
            case PEER_AUTH_FAILED:
                Toast.makeText(this, "用户认证失败！", Toast.LENGTH_SHORT).show();
                break;
            case LOOKUP_FAILED:
                Toast.makeText(this,"lookup_failed", Toast.LENGTH_SHORT).show();
                break;
            case UNREACHABLE:
                Toast.makeText(this, "网关不可达", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "其他错误："+error, Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    private void prepareVPNService(){
        Intent intent;
        try {
            intent = VpnService.prepare(this);
        } catch (IllegalStateException ex) {
            /*
             * this happens if the always-on VPN feature (Android 4.2+) is
             * activated
             */
            Toast.makeText(getApplicationContext(), "不支持VpnService", Toast.LENGTH_SHORT).show();
            return;
        }
        /* store profile info until the user grants us permission */
        if (intent != null) {
            try {
                startActivityForResult(intent, PREPARE_VPN_SERVICE);
            } catch (ActivityNotFoundException ex) {
                /*
                 * it seems some devices, even though they come with Android 4,
                 * don't have the VPN components built into the system image.
                 * com.android.vpndialogs/com.android.vpndialogs.ConfirmDialog
                 * will not be found then
                 */
                Toast.makeText(getApplicationContext(), "不支持vpn", Toast.LENGTH_SHORT).show();
            }
        } else { /* user already granted permission to use VpnService */

            onActivityResult(PREPARE_VPN_SERVICE, RESULT_OK, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: 毁掉");
        switch (requestCode) {
            case PREPARE_VPN_SERVICE:
                Log.e(TAG, "onActivityResult: 1" );
                /*if (resultCode == RESULT_OK) {
                    VSGService.getInstance().startNCTunnel(LauncherActivity.this,null);
                    handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
                }*/
                break;
            default:
                Log.e(TAG, "onActivityResult:2 ");
                super.onActivityResult(requestCode, resultCode, data);
                handler.sendEmptyMessageDelayed(GO_GUIDE, 2000);
        }
    }

    private boolean reportErrorState(int error){
        if(error == 0){
            return false;
        }
        String errormsg = mStateManager.getAuthErrorMsg();
        switch (error){
            case AuthStateManager.LocalStateCode.GATEWAY_INACCESSIBLE:
                Toast.makeText(getApplicationContext(), "网关不可达,请稍后再试", Toast.LENGTH_SHORT).show();

                break;
            case AuthStateManager.AuthStateCode.USER_SESSION_NOT_FOUND:
                Toast.makeText(getApplicationContext(), "用户会话超时，请重新登录!", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), "errorstate:"+ Integer.toHexString(error)+",errormsg:"+errormsg, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
