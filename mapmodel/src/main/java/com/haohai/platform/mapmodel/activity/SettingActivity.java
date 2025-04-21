package com.haohai.platform.mapmodel.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.Utils.MNCTransparentDialog;
import com.haohai.platform.mapmodel.bean.MessageWrap;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.ex.DbException;

import rx.functions.Action1;

public class SettingActivity extends HhBaseActivity {
    private ImageView iv_back;
    TextView tv_version;
    LinearLayout ll_exit;
    LinearLayout ll_password;
    LinearLayout ll_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });

        initView();
        bindView();
    }

    private void bindView() {
        try {
            String versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            tv_version.setText(getString(R.string.app_name)+"V"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        RxViewAction.clickNoDouble(ll_info).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(SettingActivity.this,UserInfoActivity.class));
            }
        });
        RxViewAction.clickNoDouble(ll_password).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(new Intent(SettingActivity.this,ChangePassActivity.class));
            }
        });
        RxViewAction.clickNoDouble(ll_exit).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showExitDialog("确定退出登录吗？");
            }
        });
    }

    public void showExitDialog(String msg) {
        final MNCTransparentDialog mncTransDialog = new MNCTransparentDialog(SettingActivity.this);
        View dialogView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_tokendown, null, false);
        TextView message_text = (TextView) dialogView.findViewById(R.id.message_text);
        message_text.setText(msg);
        final TextView tv_queren = (TextView) dialogView.findViewById(R.id.tv_right);
        final TextView tv_left = (TextView) dialogView.findViewById(R.id.tv_left);
        //确认
        RxViewAction.clickNoDouble(tv_queren).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mncTransDialog.dismiss();


                EventBus.getDefault().post(MessageWrap.getInstance(msg));
                XGPushManager.unregisterPush(SettingActivity.this);

                DbConfig dbConfig = new DbConfig(SettingActivity.this);
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
                SettingActivity.this.finish();
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

    private void initView() {
        ll_exit = findViewById(R.id.ll_exit);
        ll_info = findViewById(R.id.ll_info);
        ll_password = findViewById(R.id.ll_password);
        tv_version = findViewById(R.id.tv_version);
    }
}
