package com.river.szdapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;





import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.river.util.HttpHelper;
import com.river.util.ToastUtil3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Button btnLogin;
	private EditText txtUserID;
	private EditText txtPass;
	private Toast toast;
	private String url="http://139.129.19.220:7030/api/SZDLogin/";
	public  ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		btnLogin=(Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
										public void onClick(View v)
										{

											// showMsg("ls");



											progressDialog = ProgressDialog.show(LoginActivity.this,
													"提示","正在请求请稍等...", true);
											progressDialog.setCancelable(true);
											progressDialog.setCanceledOnTouchOutside(false);

											// Android 4.0 之后不能在主线程中请求HTTP请求
											new Thread(new Runnable(){
												@Override
												public void run() {

													DoLogin();
												}
											}).start();

										}
									}
		);
	}

	public void showMsg(String arg) {
		if (toast == null) {
			toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}

	private void DoLogin()
	{



		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		txtUserID=(EditText)findViewById(R.id.accountEt);
		String UserID=txtUserID.getText().toString();

		txtPass=(EditText)findViewById(R.id.pwdEt);
		String Pass=txtPass.getText().toString();
		nameValuePairs.add(new BasicNameValuePair("UserID", UserID));
		nameValuePairs.add(new BasicNameValuePair("UserPass", Pass));



		String st= HttpHelper.invoke1(url,"LoginCheck", nameValuePairs);




		if(st!="") //如果登陆成功
		{

			try {

				st = StringEscapeUtils.unescapeJava(st);
				st=st.substring(0,st.length()-1);
				st=st.substring(1,st.length());

				JSONObject jsonObject = JSON.parseObject(st);

				int Code= jsonObject.getInteger("code");
				String msg=jsonObject.getString("msg");

				progressDialog.dismiss();

				if(Code==0)
				{
					//保存用户名
					SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
					Editor edit = preference.edit();
					edit.putString("User1",UserID);
					edit.commit();

					Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);

					startActivity(intent);
				}
				else
				{

					ToastUtil3.showToast(LoginActivity.this, "ls1");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
	}
}
