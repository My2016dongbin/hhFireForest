package com.haohai.platform.fireforestplatform.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.ruyiruyi.rylibrary.utils.Config;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Config.APPID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(getIntent(), this);
        WXEntryActivity.this.finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if(resp.getType() == ConstantsAPI.COMMAND_SENDAUTH){
           //微信登录
        }else if(resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){
           //微信分享
            String result = "";
            switch (resp.errCode){
                case BaseResp.ErrCode.ERR_OK:
                    result = "分享成功";
                    break;
                default:
                    result = "分享失败";
                    break;

            }
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
        WXEntryActivity.this.finish();
    }
}
