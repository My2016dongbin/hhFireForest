package com.river.szdapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LLoginActivity extends Activity {

	private Button btnLogin;
	private EditText txtUserID;
	private EditText txtPass;
	private String URL="http://139.129.19.220:7030/api/SZDLogin/";
	private String UserID="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		LocationManager locationManager
				= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(gps==false)
			ToastUtil3.showToast(LLoginActivity.this, "请先开启GPS");


		//初始化按钮，并绑定监听事件
		btnLogin=(Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
										public void onClick(View v)
										{

											ResponseOnClick();

										}
									}
		);
	}


	private void ResponseOnClick() {

		//for test 测试如何跳转百度地图



//    	  Uri uri = Uri.parse("baidumap://map/direction?destination=latlng:"+"36.08734723760949"+","+ "120.42430614189682"+"|name:"+"目的地名称"+"&mode=driving");
//    	  this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
//    	  return;

		//for test over


		HttpThread thread = new HttpThread(handler);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		txtUserID=(EditText)findViewById(R.id.accountEt);
		//String UserID=txtUserID.getText().toString();
		UserID=txtUserID.getText().toString();
		txtPass=(EditText)findViewById(R.id.pwdEt);
		String Pass=txtPass.getText().toString();
		nameValuePairs.add(new BasicNameValuePair("UserID", UserID));
		nameValuePairs.add(new BasicNameValuePair("UserPass", Pass));

		thread.doStart(URL,  nameValuePairs);


	}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			String st= msg.getData().getString("result");

			try {
				st = StringEscapeUtils.unescapeJava(st);
				st=st.substring(0,st.length()-1);
				st=st.substring(1,st.length());
				JSONObject jsonObject = JSON.parseObject(st);

				int Code= jsonObject.getInteger("code");
				String msg1=jsonObject.getString("msg");



				if(Code==0)
				{
					//保存用户名
					SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
					Editor edit = preference.edit();
					edit.putString("User",UserID);
					UserModel.UserID=UserID;
					edit.commit();
					Log.e("TAG", "handleMessage: save UserID " + UserID );

					Intent intent = new Intent(LLoginActivity.this, MainTabActivity.class);
					startActivity(intent);
				}
				else
				{

					ToastUtil3.showToast(LLoginActivity.this, "用户名或密码错误..!");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};


	private class HttpThread extends Thread{
		Handler handler = null;
		String url = null;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		ProgressDialog progressDialog = null;

		//构造函数
		public HttpThread(Handler handler){
			this.handler = handler;
		}

		/**
		 * 启动线程
		 */
		public void doStart(String url,
							List<NameValuePair> nameValuePairs){
			this.url=url;

			this.nameValuePairs = nameValuePairs;
			progressDialog = ProgressDialog.show(LLoginActivity.this,
					"提示","正在请求请稍等...", true);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			this.start();
		}
		/**
		 * 线程运行
		 */
		@Override
		public void run() {
			System.out.println("jack");
			super.run();
			try{
				//web service 请求

				String st= HttpHelper.invoke1(url,"LoginCheck", nameValuePairs);
				//取消进度框
				progressDialog.dismiss();
				//构造消息
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString("result", st);
				message.setData(bundle);
				handler.sendMessage(message);

			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}


}
