package com.haohai.platform.mapmodel.activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.mapmodel.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.AESUtils3;
import com.ruyiruyi.rylibrary.utils.AesUtil;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import rx.functions.Action1;

public class ChangePassActivity extends HhBaseActivity {
    ImageView iv_back;
    EditText et_old;
    EditText et_new;
    TextView tv_submit;
    DYLoadingView dy3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        RxViewAction.clickNoDouble(iv_back).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                onBackPressed();
            }
        });

        et_old = findViewById(R.id.et_old);
        et_new = findViewById(R.id.et_new);
        tv_submit = findViewById(R.id.tv_submit);
        dy3 = findViewById(R.id.dy3);

        RxViewAction.clickNoDouble(tv_submit).subscribe(unused -> {
            Log.e("TAG", "onCreate: bingo");
            if(et_old.getText().toString().length()<6){
                Toast.makeText(this, "请输入合理的原密码", Toast.LENGTH_SHORT).show();
                return;
            }
            if(et_new.getText().toString().length()<6){
                Toast.makeText(this, "请输入合理的新密码", Toast.LENGTH_SHORT).show();
                return;
            }
            submit();
        });
    }


    void showDY3(){
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }
    void hideDY3(){
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    private String aesPassword = "nPhPGOMzoMTdN9wq";//AES私钥
//    private final String rsaPassword_g = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCFPmKB1bh6oGLagic663u/xWNkrtDbLTMeJuROwe5w9ipWwSDIzqos+2p7IukUPi7yZoYv080m8Wu4RsMOBzCUb0H9TSer68KW1Wqky75DtKY+UKj1Y3wU8H+CxVXoN10q3GCgjaEdtQZabcjjwoUvIHT7xTCzB0P3TxjZ7/fscwIDAQAB";//RSA公钥
//    private final String rsaPassword_s = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIU+YoHVuHqgYtqCJzrre7/FY2Su0NstMx4m5E7B7nD2KlbBIMjOqiz7ansi6RQ+LvJmhi/TzSbxa7hGww4HMJRvQf1NJ6vrwpbVaqTLvkO0pj5QqPVjfBTwf4LFVeg3XSrcYKCNoR21BlptyOPChS8gdPvFMLMHQ/dPGNnv9+xzAgMBAAECgYAtPqfYiqggC8JFjJihq0DUN8SudaY6JrkK7g3sqHG9Lfnmh6IIThT/PUhFE++tjggHC8VZDETHiocXhf/KDary3BegSUIAfsGkr81tlTfhBjMsCOLH22LeZw//XIx7OplStK/CetX2727Ds5fGol9C+e6D1WOSwCJOO+jLlxvAQQJBAOTs0qzjpmHLZPaTgoFq62fDdr6lBHq+ReIBGLyPX9Ezvtp81r6/KVTeaB22LxtTpO2OJXXcatR4dfMH+lqSjXECQQCVAJxulw6eNdOYAAwyMoLRCVBS66j2ylmhEUN9uPAr8o7XC5PB8pF/fsMk5Q6WDe6uHE7v7PSd8ypSQMeoRPYjAkEArznm+Jc4H9sD6Ql393/TuJURK1Q8XYePDjMwsAQ+n28wQyUTauX/yQqEP1nYLN6Ve5A2dETHMOMTxXbx1qoewQJAUgVgF1B374dZztZX4FoFwOQLn1myTQfehtdl+5MOQmLnVmE9GQpaJYC2E10zxk4tERLsMQ6TKU9uAJFAVtR/WQJAY+JHFUxmIZWb7YyKu60bYYI/beje9fNmNppyMtZMrwEYsvtiU5y4GnmWZVyRNNYCgghXT3KdUsD09F4YHRTiwA==";//RSA私钥

    private final String rsaPassword_g = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCq6jRoYYLf3Vm+ownCz10nrxdojIIVuZlaqxrSOAehPHVJZ038P1LjsbimfaklYa2q4jpCFFgQG4ttQ2h/iliqFt5ZTTHYSYPREg6opGs9RlGb2+mVPSHVi8BTFygLFBFJzYiCIQ21Lhus+UBhWjivj/pL0gvCjRddXcX6bpwJSwIDAQAB";//RSA公钥
    private final String rsaPassword_s = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKrqNGhhgt/dWb6jCcLPXSevF2iMghW5mVqrGtI4B6E8dUlnTfw/UuOxuKZ9qSVhrariOkIUWBAbi21DaH+KWKoW3llNMdhJg9ESDqikaz1GUZvb6ZU9IdWLwFMXKAsUEUnNiIIhDbUuG6z5QGFaOK+P+kvSC8KNF11dxfpunAlLAgMBAAECgYBgoYvB3Ce/ZAmCc/Fn2A+mCSNl89L0b3vZvFWstwrxSRpSxvpbfH3jyC5Ky08fmGs06zTe+VuUt84Ll4n0WgaoK+Pj1JwZfIVW/a7qNo1pD3XUK42jERIrMakIf/7/Ii6cX0AjQxBf/0k1UmrTxtmh46LRbwGZA9W7ctt20z+IwQJBAPeKRZOXskDG7RePJyUvcoSQ3ch/P28/2KPeomcTaYYoqne9o3Kvg+iCAIRfHsVNFodXmwce8y/l1MnY/vSRxGECQQCwwYni9cS5Ih/57qNgKM1CQSuBVRQoHrBSCYM/OaYSVCoXCMcuoSlEQ/MAEWvp6YprVv6vqzHj/0Z7Z/xgey0rAkEAvgHb8DOTttc67EeM06U88PbF1n2eMoW+g+KDpD0pVbpnRyxAhuqkhNctEG53Dxlh/pdHP0sJfi2bjShMY2x0YQJAcyfUNppZ7SePX5yasgZDG9wrhNoyBKVhyEDMUj+zs5NDzLf6VKXIpeIDCdNP1BhEBwSpbzeAjIL+n12y7gSx+wJAYxXmpRyGZMEJuad97lee2wtqekqfDtBmg1sFv0XiKQYCRTkaCIkzWsw75AGyBEQ4ZzCXKC7oO3AJfrWs7X1/wg==";//RSA私钥
    //AES加密
    private void submit() {
        String old_ = null;
        String new_ = null;
        String key_ = null;
        try {
            old_ = AesUtil.encrypt(et_old.getText().toString(),aesPassword);
            new_ = AesUtil.encrypt(et_new.getText().toString(),aesPassword);
            key_ = AESUtils3.EncryptRSA(aesPassword, rsaPassword_g);
//            key_ = com.haohaiencode.commons.codec.binary.Base64.encodeBase64String( AESUtils3.encryptByPublicKey(com.haohaiencode.commons.codec.binary.Base64.decodeBase64(aesPassword), rsaPassword_g) );
            Log.e("TAG", "submit: old_ " + old_ );
            Log.e("TAG", "submit: new_ " + new_ );
            Log.e("TAG", "submit: key_ " + key_ );
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("newPasswd",new_);
            jsonObject.put("oldPasswd",old_);
            jsonObject.put("aeskey",key_);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_QUANXIAN + "api/auth/user/modfiy/passwd");
        params.setBodyContent(jsonObject.toString());
        params.addBodyParameter("newPasswd",new_);
        params.addBodyParameter("oldPasswd",old_);
        params.addBodyParameter("aeskey",key_);
        params.addParameter("newPasswd",new_);
        params.addParameter("oldPasswd",old_);
        params.addParameter("aeskey",key_);
        params.addHeader("Authorization", "Bearer " + new DbConfig(ChangePassActivity.this).getUser().getToken());
        showDY3();
        Log.e("change", "submit: " + jsonObject.toString() );
        x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: bingo changepwd" + result );
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code") == 200){
                        Toast.makeText(ChangePassActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(ChangePassActivity.this, jsonObject.getString("message")+"", Toast.LENGTH_SHORT).show();
                    }

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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDY3();
                    }
                },500);
            }
        });
    }
}