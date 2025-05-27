package com.river.szdapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.river.util.HttpHelper;
import com.river.util.ToastUtil3;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewCJLogPicActivity extends Activity implements View.OnClickListener{

	private int i=0;
	private Button pre,next,back;
	private String[] fileNames;
	private String picPath="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_cjlog_pic);


		init();
		Intent intent=getIntent();
		picPath=intent.getStringExtra("picPath");

		String PicFileNames=intent.getStringExtra("PicFileNames");
		fileNames=PicFileNames.split("\\|");
		String URL=picPath+PicFileNames.split("\\|")[0];
		//ToastUtil3.showToast(ViewCJLogPicActivity.this, String.valueOf(PicFileNames.split("\\|").length));
		GetPic(URL);



	}

	/**
	 * 对布局文件进行初始化
	 * */
	private void init(){
		pre = (Button) findViewById(R.id.pre);
		pre.setOnClickListener(this);
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(this);


		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);

	}


	public void onClick(View v) {
		switch(v.getId()){
			case R.id.pre:
				i --;
				if(i < 0) {
					i = 0;
					Toast.makeText(this, "已经是第一张了", Toast.LENGTH_SHORT).show();
					break;
				}
				String URL=picPath+fileNames[i];
				GetPic(URL);
				break;
			case R.id.next:
				i ++;
				if(i >= fileNames.length) {
					i = fileNames.length - 1;
					Toast.makeText(this, "已经是最后一张了", Toast.LENGTH_SHORT).show();
					break;
				}
				String URL1=picPath+fileNames[i];
				GetPic(URL1);
				break;



			case R.id.back:
				//Toast.makeText(this, "i am back", Toast.LENGTH_SHORT).show();
				this.finish();
				break;

		}
	}



	public void GetPic(String URL)
	{
		HttpThread thread = new HttpThread(handler);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


		thread.doStart(URL,  nameValuePairs);
	}



	public  Bitmap getHttpBitmap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setConnectTimeout(0);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}


	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {


			try {
				//progressDialog.dismiss();

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
			progressDialog = ProgressDialog.show(ViewCJLogPicActivity.this,
					"提示","正在请求请稍等...", true);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(true);
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





				Bitmap bitmap=getHttpBitmap(this.url);

				//取消进度框
				if ((progressDialog != null) && progressDialog.isShowing())
					progressDialog.dismiss();
				progressDialog.dismiss();
				progressDialog.cancel();
				ImageView image1 = (ImageView) findViewById(R.id.imageview);
				//
//  					                        //从网上取图片
				image1 .setImageBitmap(bitmap); //设置Bitmap






			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}























}
