package com.haohai.platform.fireforestplatform.ui.acticity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.utils.TraceServiceImpl;
import com.haohai.platform.platformmodel.ui.acticity.base.HhBaseActivity;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.ui.AESUtils3;
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

import rx.functions.Action1;

@Route(path = RouteUtils.OutLogin)
public class LoginActivity extends HhBaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText userNameEdit;
    private EditText passwordEdit;
    private TextView loginButton;
    private ProgressDialog loginDialog;
    private static boolean isExit = false;
    private long time = 0;
    private static final int EXIT = 1;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case EXIT:
                    isExit = false;
                    break;
            }
        }
    };
    private TextView registerButton;
    private String access_token;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginDialog = new ProgressDialog(this);

        initView();
    }

    private void initView() {

        userNameEdit = (EditText) findViewById(R.id.username_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        loginButton = (TextView) findViewById(R.id.login_button);



        RxViewAction.clickNoDouble(loginButton)
                .subscribe(new Action1<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void call(Void aVoid) {

                        loginToService();
                    }
                });

        user = new DbConfig(this).getUserOut();
        Log.i(TAG, "initView: "+user);
        if (user!=null){
            userNameEdit.setText(user.getUserName());
            passwordEdit.setText(user.getUserPasswd());
            userNameEdit.setSelection(userNameEdit.getText().length());
        }
    }

    private void loginToServiceOld() {

        if (userNameEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialogProgress(loginDialog,"登陆中...              ");
        final RequestParams params = new RequestParams(RequestUtils.LOGIN_URL + "auth/oauth/token");
        // params.addBodyParameter("reqJson", jsonObject.toString());
        params.addParameter("username",userNameEdit.getText().toString());
        params.addParameter("password",passwordEdit.getText().toString());
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
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }else  if (ex.toString().contains("401")) {
                    Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
        if (userNameEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialogProgress(loginDialog,"登录中...              ");
        String user_ = null;
        String pass_ = null;
        try {
            user_ = AesUtil.encrypt(userNameEdit.getText().toString(),aesPassword);
            pass_ = AesUtil.encrypt(passwordEdit.getText().toString(),aesPassword);
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
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONObject obj = (JSONObject) data.get(0);
                    access_token = obj.getString("access_token");

                    getUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: " + ex.toString());
                if (ex.toString().contains("400")) {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }else  if (ex.toString().contains("401")) {
                    Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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

    private void getUserInfo() {
        final RequestParams params = new RequestParams(RequestUtils.LOGIN_URL + "auth/api/auth/user/get/userinfo");
        // params.addBodyParameter("reqJson", jsonObject.toString());
        params.setConnectTimeout(10000);
        params.addHeader("Authorization","bearer " + access_token);
        Log.e(TAG, "getUserInfo:-- " + params );
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:--2- " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("200")){
                        Log.e(TAG, "onSuccess: --3-" );
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
                        String imToken = userJsonObj.getString("imToken");

                        Set<String> tagSet = new LinkedHashSet<String>();
                        tagSet.add(gridNo);
                        tagSet.add(id);
                        tagSet.add(groupId+"-pd");
                        XGPushManager.setTags(getApplicationContext(),"setTag",tagSet);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(15000);//休眠3秒
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
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                /**
                                 * 要执行的操作
                                 */
                            }
                        }.start();
                        User user = new User(id, userCode,userNameEdit.getText().toString(), passwordEdit.getText().toString(), fullName, email, phone, sex, entryTime, birthday, type, isSuperAdmin, comment, groupId,
                                gridNo, bkchar2, money, lockMoney, groupName, state, 1, access_token,headUrl,imToken);
                        user.isShangchuan = false;
                        user.setIsyunyin(1);
                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();
                        try {
                            db.delete(User.class);
                            db.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        //  doLogin();
                        loginDialog.dismiss();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       /* ARouter.getInstance().build(RouteUtils.LoginToMain)
                                .withInt("state",1)
                                .navigation();*/


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
     * 登录环信
     */
   /* private void doLogin() {
        LoginInfo info = new LoginInfo(userNameEdit.getText().toString(),access_token);
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        // your code
                        Log.e(TAG, "onSuccess: " +param);
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 302) {
                            Log.e(TAG, "账号密码错误 ");
                            // your code
                        } else {
                            // your code
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        // your code
                    }
                };

        //执行手动登录
        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
    }*/

    @Override
    public void onBackPressed() {

        exit();

    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次回到主页",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(EXIT, 2000);
        } else {

            Intent intent = new Intent("haohai.haohai.baseActivity");       //关闭程序
            intent.putExtra("closeAll", 1);
            sendBroadcast(intent);//发送广播

            Intent intent1 = new Intent("out_login_main");       //关闭首页
            sendBroadcast(intent1);//发送广播

            this.finish();
            //  System.exit(0);
            TraceServiceImpl.stopService();
        }
    }
}
