package com.river.szdapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.river.util.HttpHelper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UploadActivity extends Activity {
	private TextView tvCode;
	private TextView tvProduct;
	private Button btnTakePhoto;
	private Button btnUpload;
	private static final int PHOTO_GRAPH = 1;// 拍照
	private String rivername="";
	private String url="http://139.129.19.220:7030/api/SZDLog/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		tvCode=(TextView)findViewById(R.id.edcode);
		tvProduct=(TextView)findViewById(R.id.edproduct);
		btnTakePhoto=(Button)findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(new OnClickListener(){
											public void onClick(View v)
											{
												TakePhoto();

											}
										}
		);

		btnUpload=(Button)findViewById(R.id.btnUplaod);
		btnUpload.setOnClickListener(new OnClickListener(){
										 public void onClick(View v)
										 {
											 // Android 4.0 之后不能在主线程中请求HTTP请求
											 new Thread(new Runnable(){
												 @Override
												 public void run() {
													 Upload();
												 }
											 }).start();

										 }
									 }
		);



		Init();

	}


	public void Init()
	{
		Intent intent=getIntent();
		String type=intent.getStringExtra("type");
		String projectid=intent.getStringExtra("projectid");
		String productid=intent.getStringExtra("productid");
		String code=intent.getStringExtra("code");
//	    tvCode.setText(code);
//	    tvProduct.setText(product);
	}

	public void TakePhoto()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
		String filename = format.format(date) + ".jpg";
		File fileFolder = new File(Environment.getExternalStorageDirectory()
				+ "/szd/");
		if (!fileFolder.exists()) { // 如果目录不存在，则创建
			//fileFolder.mkdir();
			fileFolder.mkdirs();
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory()
				+ "/szd/",filename)));
		this.rivername=Environment.getExternalStorageDirectory()
				+ "/szd/"+filename;

		startActivityForResult(intent, PHOTO_GRAPH);
	}


	public void Upload1()
	{
		String code="1";
		String product="23";
		String FileName="12";
		String jsonString = "{\"Code\":\""+code+"\",\"Product\":\""+product+"\",\"FileName\":\""+product+"\"}";


		Map<String, String> params0 = new HashMap<String, String>();
		params0.put("Code", code);
		params0.put("Product", product);
		params0.put("FileName", FileName);


		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("FileName", code));
		nameValuePairs.add(new BasicNameValuePair("Product", code));
		nameValuePairs.add(new BasicNameValuePair("Code", code));
//        // 遍历map，设置参数到list中
//        for (Map.Entry<String, String> entry : params0.entrySet()) {
//        	nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
//        }





		//nameValuePairs.add(new  BasicNameValuePair("", jsonString));

		String st= HttpHelper.invoke1(url,"UploadSZDLog", nameValuePairs);
	}



	public void Upload()
	{

//		String code="";
//		String product="";
//		String jsonString = "{\"code\":\""+code+"\",\"product\":\""+product+"\"}";
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();        
//		nameValuePairs.add(new  BasicNameValuePair("", jsonString));
		//String resultString= HttpHelper.invokePost("GetStockList", nameValuePairs);

		String code="1";
		String product="23";
		//	String filename="";
//		String jsonString = "{\"code\":\""+code+"\",\"FileName\":\""+filename+
//                "\",\"product\":\""+product+"\"}";
		String jsonString = "{\"code\":\""+code+"\",\"product\":\""+product+"\"}";

		Map<String, String> params0 = new HashMap<String, String>();
		params0.put("GpsImgJson", jsonString);
		String st= HttpHelper.invokePost(url,"AddImg", rivername, params0);
	}
}
