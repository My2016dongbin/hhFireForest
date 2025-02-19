package com.haohai.platform.fireforestplatform.ui.acticity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.haohai.platform.fireforestplatform.MainActivity;
import com.haohai.platform.fireforestplatform.R;
import com.haohai.platform.fireforestplatform.ui.utils.TraceServiceImpl;
import com.haohai.platform.platformmodel.ui.acticity.base.HhBaseActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.Requestaddress;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
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

import rx.functions.Action1;

@Route(path = RouteUtils.OutLogin)
public class LoginActivity extends HhBaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText userNameEdit;
    private LinearLayout ll_ip;
    private LinearLayout ll_port;
    private TextView tv_ip;
    private EditText et_ip;
    private EditText et_port;
    private EditText passwordEdit;
    private TextView loginButton;
    private TextView loginButtonnw;
    private ProgressDialog loginDialog;
    private static boolean isExit = false;
    private long time = 0;
    private static final int EXIT = 1;
    @SuppressLint("HandlerLeak")
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
    private boolean isIp = false;
    private Requestaddress requestaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginDialog = new ProgressDialog(this);
        requestaddress=new Requestaddress();
        initView();
    }

    private void initView() {

        userNameEdit = (EditText) findViewById(R.id.username_edit);
        ll_ip = (LinearLayout) findViewById(R.id.ll_ip);
        ll_port = (LinearLayout) findViewById(R.id.ll_port);
        tv_ip = (TextView) findViewById(R.id.tv_ip);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_port = (EditText) findViewById(R.id.et_port);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        loginButton = (TextView) findViewById(R.id.login_button);
        loginButtonnw = (TextView) findViewById(R.id.login_nw_button);

        et_ip.setText(RequestUtils.IP);
        et_port.setText(RequestUtils.PORT + "");

        RxViewAction.clickNoDouble(tv_ip).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(!isIp){
                    //设置ip
                    ll_ip.setVisibility(View.VISIBLE);
                    ll_port.setVisibility(View.VISIBLE);
                    tv_ip.setText("保存");

                }else{
                    //保存ip
                    if(et_ip.getText().toString().isEmpty() || et_port.getText().toString().isEmpty()){
                        Toast.makeText(LoginActivity.this, "请输入合理的ip和端口号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ll_ip.setVisibility(View.GONE);
                    ll_port.setVisibility(View.GONE);
                    tv_ip.setText("设置ip端口");
                    SharedPreferences sp = getSharedPreferences("haohai_file", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("ip", et_ip.getText().toString());
                    editor.putInt("port", Integer.parseInt(et_port.getText().toString()));
                    editor.apply();
                    RequestUtils.IP = et_ip.getText().toString();
                    RequestUtils.PORT = Integer.parseInt(et_port.getText().toString());
                    Toast.makeText(LoginActivity.this, "已保存", Toast.LENGTH_SHORT).show();
                }
                isIp = !isIp;
            }
        });

        RxViewAction.clickNoDouble(loginButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        requestaddress.setIfinternet(true);
                        requestaddress.setRequstUrl(RequestUtils.REQUEST_URL_BASE());
                        Log.e(TAG, "call: "+ requestaddress.getRequstUrl());
                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();

                        try {
                            db.delete(Requestaddress.class);
                            db.saveOrUpdate(requestaddress);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        loginToService();
                    }
                });
        RxViewAction.clickNoDouble(loginButtonnw)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        requestaddress.setIfinternet(false);
                        requestaddress.setRequstUrl("http://10.10.2.27:8000/");
                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();

                        try {
                            db.delete(Requestaddress.class);
                            db.saveOrUpdate(requestaddress);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
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

    private void loginToService() {

        if (userNameEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialogProgress(loginDialog,"登陆中...              ");
        final RequestParams params = new RequestParams(requestaddress.getRequstUrl() + "auth/oauth/token");
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


    private User user_;
    private void getUserInfo() {
        final RequestParams params = new RequestParams(requestaddress.getRequstUrl() + "auth/api/auth/user/get/userinfo");
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

                        User user = new User(id, userCode,userNameEdit.getText().toString(), passwordEdit.getText().toString(), fullName, email, phone, sex, entryTime, birthday, type, isSuperAdmin, comment, groupId,
                                gridNo, bkchar2, money, lockMoney, groupName, state, 1, access_token,headUrl);
                        user.isShangchuan = false;
                        user.setIsyunyin(1);
                        user_ = user;
                        DbConfig dbConfig = new DbConfig(getApplicationContext());
                        DbManager db = dbConfig.getDbManager();

                        try {
                            db.delete(User.class);
                            db.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        doLogin();
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

                        postPer("app-video");


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


    private DbManager dbManager;
    private User userPermission;
    private void postPer(String id) {
        DbConfig dbConfig = new DbConfig(LoginActivity.this);
        dbManager = dbConfig.getDbManager();
        userPermission = dbConfig.getUser();
        userPermission.setPermission("");
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/auth/list/element/from/menu");
        params.addParameter("menuCode",id);
        params.addHeader("Authorization", "bearer " + new DbConfig(LoginActivity.this).getUser().getToken());
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

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       /* ARouter.getInstance().build(RouteUtils.LoginToMain)
                                .withInt("state",1)
                                .navigation();*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LoginActivity.this, "权限获取异常", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                loginDialog.dismiss();
            }
        });
    }

    /**
     * 登录环信
     */
    private void doLogin() {
        CommonData.testId = user_.getId();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/im/user/getToken");
        params.addHeader("Authorization","bearer " + access_token);
        params.addParameter("accid",user_.getId());
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
