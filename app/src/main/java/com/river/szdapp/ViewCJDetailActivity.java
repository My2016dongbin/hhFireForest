package com.river.szdapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.river.util.HttpHelper;
import com.river.util.ToastUtil3;

import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.TextView;

public class ViewCJDetailActivity extends Activity {

	private String URL="http://139.129.19.220:7030/api/SZDCJDetail/";
	private TextView tView;

	private String PicPath="";
	private String PicFileNames="";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_cjdetail);
		tView=(TextView)findViewById(R.id.tvContents);



		Intent intent=getIntent();
		String ID=intent.getStringExtra("CJLogID");
		GetData(ID);


		Button btnViewPic=(Button)findViewById(R.id.btnViewDetail);
		btnViewPic.setOnClickListener(new OnClickListener(){
										  public void onClick(View v)
										  {


											  Intent intent = new Intent(ViewCJDetailActivity.this, ViewCJLogPicActivity.class);
											  Bundle bundle=new Bundle();
											  bundle.putString("picPath", PicPath);
											  bundle.putString("PicFileNames", PicFileNames);
											  intent.putExtras(bundle);
											  startActivity(intent);

										  }
									  }
		);


		Button btnBack=(Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener(){
									   public void onClick(View v)
									   {


										   Intent intent1 = new Intent(ViewCJDetailActivity.this, ViewDataTableActivity.class);

										   startActivity(intent1);

									   }
								   }
		);
	}


	public void GetData(String CJLogID)
	{
		HttpThread thread = new HttpThread(handler);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		//String CJLogID="186";
		nameValuePairs.add(new BasicNameValuePair("CJID", CJLogID));

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




				if(Code==0)
				{

					//解析基础数据
					JSONObject basicDataJsonObject=JSON.parseObject(jsonObject.getString("basicdata"));
					String detail="";
					detail+="采集时间:";
					detail+=  basicDataJsonObject.getString("Time");

					detail+="\n采集人:";
					detail+=  basicDataJsonObject.getString("CreatorUserID");

					detail+="\n经度:";
					detail+=  basicDataJsonObject.getString("JD");

					detail+="\n纬度:";
					detail+=  basicDataJsonObject.getString("WD");

					detail+="\n海拔:";
					detail+=  basicDataJsonObject.getString("HB");

					detail+="\n二维码编号:";
					detail+=  basicDataJsonObject.getString("QDID");

					detail+="\n林班信息:";
					detail+=  basicDataJsonObject.getString("lxx");

					detail+="\n小班信息:";
					detail+=  basicDataJsonObject.getString("lxx1");

					//  detail+="\n文件路径:";
					String head="http://139.129.19.220:7030//oimages//";
					String filePathString=basicDataJsonObject.getString("FilePath");

					String basePath = "";
					try{
						basePath=filePathString.split("\\\\")[8]+"//"+filePathString.split("\\\\")[9]+"//"+filePathString.split("\\\\")[10];
					}catch(Exception e){
						Log.e("TAG", "parsefilePathString: " + e.getMessage() );
					}
					PicPath=  head+basePath+"//";
					Log.e("TAG", "handleMessage: PicPath " + PicPath );

					String FileNames=basicDataJsonObject.getString("FileName");
					PicFileNames=FileNames;


					//解析扩展数据
					String extend=jsonObject.getString("extenddata");
					JSONArray jarr=JSONArray.parseArray(extend);

					String extendv=jsonObject.getString("extenddatav");
					JSONArray jarrv=JSONArray.parseArray(extendv);
					for (Iterator iterator = jarr.iterator(), iteratorv = jarrv.iterator(); iterator.hasNext()&&iteratorv.hasNext();) {
						String job=(String)iterator.next();
						detail+="\n"+job;

						String jobv=(String)iteratorv.next();
						detail+=":"+jobv;
					}
					//extenddatav

					tView.setText(detail);

				}
				else
				{

					ToastUtil3.showToast(ViewCJDetailActivity.this, "获取数据异常");
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
			progressDialog = ProgressDialog.show(ViewCJDetailActivity.this,
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

				String st= HttpHelper.invoke1(url,"GetCJDataDetail", nameValuePairs);
				//取消进度框
				progressDialog.dismiss();
				//构造消息
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString("result", st);
				Log.e("ViewCJDetailActivity", "run: result" + st );
				message.setData(bundle);
				handler.sendMessage(message);

			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

}
