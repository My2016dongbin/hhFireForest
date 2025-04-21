package com.ruyiruyi.rylibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.utils.CommonUtil;

import java.util.List;

import rx.functions.Action1;

public class AutoStartActivity extends BaseActivity {
    ImageView iv_back;
    TextView tv_go;
    TextView tv_goVivo;
    TextView tv_go3;
    TextView tv_go_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_start);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_go = (TextView) findViewById(R.id.tv_go);
        tv_goVivo = (TextView) findViewById(R.id.tv_go2);
        tv_go3 = (TextView) findViewById(R.id.tv_go3);
        tv_go_float = (TextView) findViewById(R.id.tv_go_float);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });
        RxViewAction.clickNoDouble(tv_go_float).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                checkOverlayPermission();
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
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }



    protected boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                List<ResolveInfo> infos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (infos == null || infos.isEmpty()) {
                    return true;
                }
                startActivityForResult(intent,1718);
                return false;
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                List<ResolveInfo> infos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (infos == null || infos.isEmpty()) {
                    return true;
                }
                startActivityForResult(intent,1718);
            }
        }
        return true;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1718) {
            if (Settings.canDrawOverlays(this)) {
                Log.e("TAG", "onActivityResult granted");
            }
        }
    }
}