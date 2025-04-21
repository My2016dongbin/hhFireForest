package com.haohai.platform.mapmodel.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;

import rx.functions.Action1;

public class UserInfoActivity extends HhBaseActivity {
    ImageView iv_back;
    TextView tv_user;
    TextView tv_sex;
    TextView tv_phone;
    TextView tv_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });

        tv_user = findViewById(R.id.tv_user);
        tv_sex = findViewById(R.id.tv_sex);
        tv_phone = findViewById(R.id.tv_phone);
        tv_account = findViewById(R.id.tv_account);
        tv_user.setText(new CommonUtil().parseNull(new DbConfig(this).getUser().getFullName(),""));
        tv_sex.setText(new CommonUtil().parseNull(new DbConfig(this).getUser().getSex(),""));
        tv_phone.setText(new CommonUtil().parseNull(new DbConfig(this).getUser().getPhone(),""));
        tv_account.setText(new CommonUtil().parseNull(new DbConfig(this).getUser().getUserCode(),""));
    }
}