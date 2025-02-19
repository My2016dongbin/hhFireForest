package com.haohai.platform.platformmodel.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.haohai.platform.platformmodel.R;
import com.ruyiruyi.rylibrary.base.AutoStartActivity;
import com.ruyiruyi.rylibrary.bus.MessageWrap;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.haohai.platform.platformmodel.ui.fragment.base.HhBaseFragment;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ImageTextView;
import com.ruyiruyi.rylibrary.db.UserMenu;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.utils.glide.GlideCircleTransform;
//import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import rx.functions.Action1;

/**
 * Created by geyang on 2020/7/2.
 */

public class MyFragment extends HhBaseFragment {
    private static final String TAG = MyFragment.class.getSimpleName();
    private TextView outButtonView;
    private ImageView touxiangImage;
    private LinearLayout ll_autostart;
    private ImageTextView touxiangView;
    private User user;
    private TextView nameView;
    private Switch weizhiSwitch;
    private Boolean isShangchuan = true;;
    private LinearLayout gengxinLayout;
    private LinearLayout guanyuLayout;
    private LinearLayout xgmm_layout;
    private Dialog xgmmDialog;
    private View xgmmInflate;
    private UserMenu usermenu;
    private FrameLayout sswzLayout;
    private Switch yuyinSwitch;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);

        user = new DbConfig(getContext()).getUser();
        usermenu = new UserMenu();
        initView();

        bindView();
    }



    private void initView() {
        yuyinSwitch = (Switch) getView().findViewById(R.id.yuyin_switch);
        gengxinLayout = ((LinearLayout) getView().findViewById(R.id.gengxin_layout));
        guanyuLayout = ((LinearLayout) getView().findViewById(R.id.guanyu_layout));
        xgmm_layout = ((LinearLayout) getView().findViewById(R.id.xgmm_layout));
        ll_autostart = getView().findViewById(R.id.ll_autostart);

        weizhiSwitch = ((Switch) getView().findViewById(R.id.weizhi_switch));
        outButtonView = ((TextView) getView().findViewById(R.id.out_login_button));

        touxiangImage = ((ImageView) getView().findViewById(R.id.touxiang_image));
        touxiangView = ((ImageTextView) getView().findViewById(R.id.touxiang_view));
        nameView = ((TextView) getView().findViewById(R.id.name_view));
        sswzLayout= ((FrameLayout) getView().findViewById(R.id.sswz_layout));
        int isyunyin = user.getIsyunyin();
        if (isyunyin == 1){
            yuyinSwitch.setChecked(true);
        }else {
            yuyinSwitch.setChecked(false);
        }
        if (user.getHeadUrl().equals("null")) {
            touxiangImage.setVisibility(View.GONE);
            touxiangView.setVisibility(View.VISIBLE);

            touxiangView.setName(user.getFullName());
        } else {
            touxiangImage.setVisibility(View.VISIBLE);
            touxiangView.setVisibility(View.GONE);
            Glide.with(getContext()).load(user.getHeadUrl()).transform(new GlideCircleTransform(getContext())).into(touxiangImage);
        }
        nameView.setText(user.getFullName());


        RxViewAction.clickNoDouble(ll_autostart).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(getContext(), AutoStartActivity.class));
            }
        });
        RxViewAction.clickNoDouble(gengxinLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(getContext(), "当前是最新版本", Toast.LENGTH_SHORT).show();

                    }
                });
        RxViewAction.clickNoDouble(guanyuLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(getContext(), "青岛浩海网络科技股份有限公司技术支持", Toast.LENGTH_SHORT).show();
                    }
                });
        RxViewAction.clickNoDouble(xgmm_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        xgmmDialog.show();
                    }
                });

        // 修改密码弹框
        xgmmDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        xgmmInflate = LayoutInflater.from(getContext()).inflate(R.layout.dialog_xgmm, null);
        xgmmInflate.setMinimumWidth(10000);
        final EditText newpwdEdit = (EditText) xgmmInflate.findViewById(R.id.newpwdEdit);
        Button btnxgmm = (Button) xgmmInflate.findViewById(R.id.btn_xgmm);
        xgmmDialog.setContentView(xgmmInflate);

        Window addWindow = xgmmDialog.getWindow();
        addWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams addListLp = addWindow.getAttributes();

        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();

        addListLp.width = (int) (width * 0.8);
        addListLp.height = 600;
        addListLp.y = 90;
        addWindow.setAttributes(addListLp);
        xgmmDialog.setCanceledOnTouchOutside(true);
        RxViewAction.clickNoDouble(btnxgmm)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (newpwdEdit.getText().length()>=6){
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("oldPasswd", user.getUserPasswd());
                                jsonObject.put("newPasswd", newpwdEdit.getText().toString());
                            } catch (JSONException e) {
                            }
                            RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/user/modfiy/passwd");
                            params.setAsJsonContent(true);
                            params.setBodyContent(jsonObject.toString());
                            Log.e(TAG, "getDataFromService: " + jsonObject.toString());
                            params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().getToken());
                            Log.e(TAG, "resource: --"  + params);
                            x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    Log.e(TAG, "onSuccess: --1-" + result );
                                    Log.e(TAG, "onSuccess: --1-" + result );
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(result);
                                        if (jsonObject1.getString("code").equals("200")) {
                                            xgmmDialog.dismiss();
                                            user.setUserPasswd(newpwdEdit.getText().toString());
                                        }else {
                                            Toast.makeText(getContext(), "修改密码失败", Toast.LENGTH_SHORT).show();
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
                        }else {
                            Toast.makeText(getContext(), "密码不得低于6位", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        if (usermenu.getAppSettingBtnPosition()){
            sswzLayout.setVisibility(View.VISIBLE);
        }else {
            sswzLayout.setVisibility(View.GONE);
        }
    }
    private void bindView() {
        yuyinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                User user = new DbConfig(getContext()).getUser();
                Log.e(TAG, "onCheckedChanged: "+user.getIsyunyin());
                if (isChecked){
                    Log.e(TAG, "onCheckedChanged: setting1");
                    user.setIsyunyin(1);
                    Log.e(TAG, "getIsyunyin: "+user.getIsyunyin() );
                }else {
                    Log.e(TAG, "onCheckedChanged: setting0");
                    user.setIsyunyin(0);
                    Log.e(TAG, "getIsyunyin: "+user.getIsyunyin() );
                }
                DbConfig dbConfig = new DbConfig(getContext());
                DbManager db = dbConfig.getDbManager();
                try {
                    db.saveOrUpdate(user);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        weizhiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isShangchuan = true;

                    user.isShangchuan = true;
                    DbConfig dbConfig = new DbConfig(getContext());
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.saveOrUpdate(user);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }else {
                    isShangchuan = false;
                    user.isShangchuan = false;
                    DbConfig dbConfig = new DbConfig(getContext());
                    DbManager db = dbConfig.getDbManager();
                    try {
                        db.saveOrUpdate(user);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        RxViewAction.clickNoDouble(outButtonView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
//                        XGPushManager.unregisterPush(getContext());
                        EventBus.getDefault().post(MessageWrap.getInstance(""));

                        DbConfig dbConfig = new DbConfig(getContext());
                        User user = dbConfig.getUser();
                        user.setIsLogin(0);
                        DbManager db = dbConfig.getDbManager();
                        try {
                            db.saveOrUpdate(user);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        ARouter.getInstance().build(RouteUtils.OutLogin)
                                .navigation();
                        getActivity().finish();
                       // startActivity(new Intent(getContext(), LoginActivity.class));
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
