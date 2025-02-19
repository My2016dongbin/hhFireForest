package com.ruyiruyi.rylibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CommonUtil;

import rx.functions.Action1;

public class AutoStartActivity extends BaseActivity {
    ImageView iv_back;
    TextView tv_go;
    TextView tv_goVivo;
    TextView tv_go3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_start);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_go = (TextView) findViewById(R.id.tv_go);
        tv_goVivo = (TextView) findViewById(R.id.tv_go2);
        tv_go3 = (TextView) findViewById(R.id.tv_go3);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });
        RxViewAction.clickNoDouble(tv_go3).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                gotoAppDetailIntent(AutoStartActivity.this);
            }
        });
        RxViewAction.clickNoDouble(tv_go).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                start();
            }
        });
        RxViewAction.clickNoDouble(tv_goVivo).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                AutoStartActivity.this.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

    }

    private void start() {
        CommonUtil.enterWhiteListSetting(this);
    }


    /**
     * 跳转到应用详情界面
     */
    public static void gotoAppDetailIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }
}