package com.river.szdapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.river.szdapp.BaiduTestActivity.MyLocationListenner;
import com.river.util.DataBaseHelper;
import com.river.util.HttpHelper;
import com.river.util.ToastUtil3;

import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LUploadActivity extends Activity {
	  private String URL="http://139.129.19.220:7030/api/SZDInfoItem/";
	
	 
	  private List<String>  elementL=new ArrayList<String>();
	  private List<String> names=new ArrayList<String>();
	  private static final int PHOTO_GRAPH = 1;// 拍照
	  private String rivername="";
	  private String ProjectID="";
	  private String ProductID="";
	  private String Code="";
	  public String QDCode="";
	  private LinearLayout.LayoutParams lp;
	  
	  private String region="";
	  private String productname="";
	  private String jd="";
	  private String wd="";
	  private String hb="";
	  
	  private String wc="";//误差
	  
	  private String cjType="";
	  
	  private boolean isGPSOpen=false;
	  
	  Location location;   
	 
	  
	  
		private LocationClient mLocationClient;
		private MyLocationListenner myListener;
		 DataBaseHelper helper;
		
		/**
		 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
		 */
		public class MyLocationListenner implements BDLocationListener {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return ;
				
				jd=String.valueOf(location.getLongitude());
				wd=String.valueOf(location.getLatitude());

			}
			
			public void onReceivePoi(BDLocation poiLocation) {
				if (poiLocation == null){
					return ; 
				}
				StringBuffer sb = new StringBuffer(256);
				sb.append("Poi time : ");
				sb.append(poiLocation.getTime());
				sb.append("\nerror code : "); 
				sb.append(poiLocation.getLocType());
				sb.append("\nlatitude : ");
				sb.append(poiLocation.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(poiLocation.getLongitude());
				sb.append("\nradius : ");
				sb.append(poiLocation.getRadius());
				if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
					sb.append("\naddr : ");
					sb.append(poiLocation.getAddrStr());
				} 
				if(poiLocation.hasPoi()){
					sb.append("\nPoi:");
					sb.append(poiLocation.getPoi());
				}else{				
					sb.append("noPoi information");
				}
				
				//Log.i(TAG, sb.toString());
			}
		}
		
		public void GetGPSReady() {
			
			
			LocationManager locationManager
	        = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
			boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if(gps==false)
			{
				ToastUtil3.showToast(LUploadActivity.this, "请先打开gps");
				return;
			}				
			
			else
				this.isGPSOpen=true;
			
			
			
			
			 // 閼惧嘲褰嘗ocationManager鐎电钖?   
	        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);   
	  
	        // 鐎规矮绠烠riteria鐎电钖?   
	        Criteria criteria = new Criteria();   
	        // 鐠佸墽鐤嗙?规矮缍呯划鍓р?樻惔锟? Criteria.ACCURACY_COARSE 濮ｆ棁绶濈划妤冩殣閿涳拷 Criteria.ACCURACY_FINE閸掓瑦鐦潏鍐翱缂侊拷   
	        criteria.setAccuracy(Criteria.ACCURACY_FINE);
	        // 鐠佸墽鐤嗛弰顖氭儊闂囷拷鐟曚焦鎹ｉ幏鏂句繆閹拷 Altitude   
	        criteria.setAltitudeRequired(true);
	        // 鐠佸墽鐤嗛弰顖氭儊闂囷拷鐟曚焦鏌熸担宥勪繆閹拷 Bearing   
	        criteria.setBearingRequired(true);   
	        // 鐠佸墽鐤嗛弰顖氭儊閸忎浇顔忔潻鎰儉閸熷棙鏁圭拹锟?   
	        criteria.setCostAllowed(true);   
	        // 鐠佸墽鐤嗙?靛湱鏁稿┃鎰畱闂囷拷濮癸拷   
	       // criteria.setPowerRequirement(Criteria.POWER_LOW);   
	        criteria.setPowerRequirement(Criteria.POWER_HIGH);   
	        // 閼惧嘲褰嘒PS娣団剝浼呴幓鎰返閼帮拷   
	        String bestProvider = lm.getBestProvider(criteria, true);   
	        Log.e("yao", "bestProvider = " + bestProvider);
	  
	        // 閼惧嘲褰囩?规矮缍呮穱鈩冧紖   
	        location = lm.getLastKnownLocation(bestProvider);
			Log.e("yao", "bestProvider = location " + location);
	        
	        
	        
	     // 娴ｅ秶鐤嗛惄鎴濇儔閸ｏ拷   
	        LocationListener locationListener = new LocationListener() {   
	  
	            // 瑜版挷缍呯純顔芥暭閸欐ɑ妞傜憴锕?褰?   
	            @Override  
	            public void onLocationChanged(Location location) {   
	                Log.e("yao", "bestProvider " + location.toString());
	                Log.e("yao", "bestProvider " + location.hasAltitude());
	                Log.e("yao", "bestProvider " + location.getAltitude());
	                updateLocation(location);
	            }   
	  
	            // Provider婢惰鲸鏅ラ弮鎯靶曢崣锟?   
	            @Override  
	            public void onProviderDisabled(String arg0) {   
	                Log.i("yao", arg0);   
	  
	            }   
	  
	            // Provider閸欘垳鏁ら弮鎯靶曢崣锟?   
	            @Override  
	            public void onProviderEnabled(String arg0) {   
	                Log.i("yao", arg0);   
	            }   
	  
	            // Provider閻樿埖锟戒焦鏁奸崣妯绘鐟欙箑褰?   
	            @Override  
	            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {   
	                Log.i("yao", "onStatusChanged");   
	            }   
	        };   
	  
	        // 500濮ｎ偆顫楅弴瀛樻煀娑擄拷濞嗏槄绱濊箛鐣屾殣娴ｅ秶鐤嗛崣妯哄   
	        lm.requestLocationUpdates(bestProvider, 500, 0, locationListener);   
	  
	    }   
	  
	    // 閺囧瓨鏌婃担宥囩枂娣団剝浼?   
	    // 鏇存柊浣嶇疆淇℃伅   
	    private void updateLocation(Location location) {   
	        if (location != null) {   
	        	
//	            tv1.setText("瀹氫綅瀵硅薄淇℃伅濡備笅锛?" + location.toString()+"\n\t鍏朵腑娴锋嫈:"+location.getAltitude() +"\n\t鏂瑰悜:"+location.getBearing()+ "\n\t鍏朵腑缁忓害锛?" + location.getLongitude() + "\n\t鍏朵腑绾害锛?"  
//	                    + location.getLatitude()+"\n\t鎻愪緵鍟嗭細"+location.getProvider()+"\n\t閫熷害锛?"+location.getSpeed()+"\n\t鏃堕棿锛?"+location.getTime());   
	        	jd=String.valueOf(location.getLongitude());
	        	wd=String.valueOf(location.getLatitude());
	        	hb=String.valueOf(location.getAltitude());	        	
	        	wc=String.valueOf(location.getAccuracy()/2);
				Log.e("yao bestProvider ", " " + jd + "," + wd + "," + hb + "," + wc);

			} else {
	            Log.e("yao", "娌℃湁鑾峰彇鍒板畾浣嶅璞ocation");
	        }   
	    }     
	        
	        
	        
	        
	 
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		   
		setContentView(R.layout.activity_lupload);
		
		
		  
        SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
  		String userid=preference.getString("User","");
  		if(userid!=null&&userid!="")
  		{
  			//this.UserID=userid;
  		  UserModel.UserID=userid;
  		}
  		else  //跳转到登陆页面
  		{
  			Intent intent = new Intent(LUploadActivity.this, LLoginActivity.class);			
  			startActivity(intent);	
  		}
		
		lp= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
		lp.setMargins(10, 20, 30, 40);
		names.clear();
		
		ResponseOnClick();
//		 this.rivername=Environment.getExternalStorageDirectory()  
//                 + "/szd/"+ProjectID+"_"+ProductID+"_"+Code+"/";
		
		Date date = new Date();
		SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMddhhmmss");
		String timeString=dateFormat.format(date);
		
		 this.rivername=Environment.getExternalStorageDirectory()  
                 + "/szd/"+timeString+"/"+ProjectID+"_"+ProductID+"_"+Code+"/";
		 
		// InitBaidu();
		// StartDW();
		 
		 GetGPSReady();
		 updateLocation(location);
		 setTitle(cjType);
		 ShowJWHMsg();
		
		
		
	}
	
	
	public void ShowJWHMsg() {
		
		 TextView tvGPS=(TextView)findViewById(R.id.tvGPS);
		 
		 tvGPS.setText("经度:"+jd+"\n纬度:"+wd+"\n海拔:"+hb+"米 \n误差:"+wc+"米");
		
	}
	
	
	public void StartDW()
	{
		if (mLocationClient != null){
			
			LocationManager locationManager
	        = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
			boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if(gps==false)
			{
				ToastUtil3.showToast(LUploadActivity.this, "请先打开gps");
				return;
			}				
			
			else
				this.isGPSOpen=true;
			
//			if (mLocationClient != null && mLocationClient.isStarted()){
			//Log.i(TAG, "start position");
			setLocationOption();
			mLocationClient.start();
			mLocationClient.requestLocation();	
		}else{
			//Log.d(TAG, "locClient is null or not started");
		}
	}
	
	
	public void InitBaidu()
	{
		mLocationClient = new LocationClient(getApplicationContext());
		/**——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK("v1RA3449EtrBbbcy8iynQaLcBY21kxkp");
		myListener = new MyLocationListenner();
		mLocationClient.registerLocationListener(myListener);
	}
	
	//设置相关参数
		private void setLocationOption(){
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);				//打开gps
			option.setCoorType("bd09ll");		//设置坐标类型
			option.setServiceName("com.baidu.location.service_v2.9");
			option.setAddrType("all");
			option.setScanSpan(2000);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
//			option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
			option.setPriority(LocationClientOption.GpsFirst);        //不设置，默认是gps优先
			
//			option.setPoiNumber(5);	//最多返回POI个数	
//			option.setPoiDistance(1000); //poi查询距离		
//			option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
			
			option.disableCache(true);	
			
			mLocationClient.setLocOption(option);
		}
	
	
	
	public void TakePhoto()
	{
		if(this.isGPSOpen==false)
		{
			ToastUtil3.showToast(LUploadActivity.this, "请先打开gps");
			return;
		}
		if(this.jd.equals("")||this.wd.equals("")){
			ToastUtil3.showToast(LUploadActivity.this, "定位失败");
			return;
			
		}
		
		
		File myphotos = new File(this.rivername);
	      File[] files = myphotos.listFiles();
	      if(files==null)
	      {
	    	  
	      }
	      else if(files.length>=5)
	      {
	    	  ToastUtil3.showToast(LUploadActivity.this, "不能超过5张照片");
	    	  return;
	      }   
		
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
           Date date = new Date();  
           SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间  
           String filename = format.format(date) + ".jpg";  
//           File fileFolder = new File(Environment.getExternalStorageDirectory()  
//                   + "/szd/"+ProjectID+"_"+ProductID+"_"+Code+"/");  
           File fileFolder = new File(rivername);  
           if (!fileFolder.exists()) { // 如果目录不存在，则创建 
               //fileFolder.mkdir();  
           	fileFolder.mkdirs();
           }  
//           intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory()  
//                   + "/szd/"+ProjectID+"_"+ProductID+"_"+Code+"/",filename)));    
           intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(this.rivername,filename)));  
//           this.rivername=Environment.getExternalStorageDirectory()  
//                   + "/szd/"+ProjectID+"_"+ProductID+"_"+Code+"/"+filename;
           
        
         
           startActivityForResult(intent, PHOTO_GRAPH);
	}
	
	private void gotoView()
	{
		//看看有没有拍照
		 File myphotos = new File(this.rivername);
	      File[] files = myphotos.listFiles();
	      if(files==null|| files.length==0)
	      {
	    	  ToastUtil3.showToast(LUploadActivity.this, "请先拍照");
	    	  return;
	      }
		
		Intent intent = new Intent(LUploadActivity.this, ViewPicsActivity.class);	
		  Bundle bundle=new Bundle();
		  bundle.putString("path",this.rivername );
		  intent.putExtras(bundle);
		  startActivity(intent);	
	}
	
	
	private void Init(int type)
	{
		
		LinearLayout temp=(LinearLayout)findViewById(R.id.riverPanel); 
		 //取得资源的引用。
		   Resources resource = getBaseContext().getResources() ;		   
		   Drawable myDrawable1 = resource.getDrawable(R.drawable.sharp) ;	
		if(type==1)
		{
			
			
			
			
			Button	btnTakePhoto=new Button(LUploadActivity.this);
			//btnTakePhoto.setBackgroundColor(Color.parseColor("#39B8B6"));
			btnTakePhoto.setBackground(myDrawable1);
			btnTakePhoto.setTextColor(Color.WHITE);
	        btnTakePhoto.setText("拍照");
//	        btnTakePhoto.setBackgroundColor(Color.parseColor("#39B8B6"));
//	        btnTakePhoto.setTextColor(getResources().getColor(Color.WHITE));
	        btnTakePhoto.setLayoutParams(lp);
	        
	        temp.addView(btnTakePhoto);
	        
	        btnTakePhoto.setOnClickListener(new OnClickListener(){
				public void onClick(View v)
				{
					
					TakePhoto();
							
				}
			}
			);
	        
	        
	        Button	btnUpload=new Button(LUploadActivity.this);
			
	        btnUpload.setText("上传");
	       // btnUpload.setTextColor(getResources().getColor(Color.WHITE));
	       // btnUpload.setBackground(myDrawable);
	        btnUpload.setBackground(myDrawable1);
	        btnUpload.setTextColor(Color.WHITE);
	        
	        
	      //  btnUpload.setBackgroundColor(Color.parseColor("#39B8B6"));
	        btnUpload.setLayoutParams(lp);
	       temp.addView(btnUpload);
	       btnUpload.setOnClickListener(new OnClickListener(){
				public void onClick(View v)
				{
					
					
					Upload();
							
				}
			}
			);
			
	       
	       
	       
	       Button	btnViewPics=new Button(LUploadActivity.this);
			 //取得资源的引用。
	    
	      btnViewPics.setText("查看图片");
	      btnViewPics.setBackground(myDrawable1);
	      btnViewPics.setTextColor(Color.WHITE);
	      //btnViewPics.setBackgroundColor(Color.parseColor("#39B8B6"));
	     // btnViewPics.setBackground(myDrawable);
	     // btnViewPics.setBackgroundColor(Color.parseColor("#39B8B6"));
	      btnViewPics.setLayoutParams(lp);
	      temp.addView(btnViewPics);
	      btnViewPics.setOnClickListener(new OnClickListener(){
				public void onClick(View v)
				{
					
					 gotoView();
					    
							
				}
			}
			);
	      
			   Button	btnDW=new Button(LUploadActivity.this);
					 //取得资源的引用。
			 
			   btnDW.setText("重新定位");
			   btnDW.setBackground(myDrawable1);
			   btnDW.setTextColor(Color.WHITE);
			  // btnDW.setBackgroundColor(Color.parseColor("#39B8B6"));
			 //  btnDW.setBackgroundColor(Color.parseColor("#39B8B6"));
			   btnDW.setLayoutParams(lp);
			   temp.addView(btnDW);
			   btnDW.setOnClickListener(new OnClickListener(){
						public void onClick(View v)
						{
							
							// StartDW();
							
							 GetGPSReady();
							 updateLocation(location);
							 
							 ShowJWHMsg();
							    
									
						}
					}
					);
			   
			   

			   Button	btnSave=new Button(LUploadActivity.this);
					 //取得资源的引用。
			  	
			   btnSave.setText("暂存");
			   btnSave.setBackground(myDrawable1);
			   btnSave.setTextColor(Color.WHITE);
			  // btnSave.setBackgroundColor(Color.parseColor("#39B8B6"));
		//	   btnSave.setBackgroundColor(Color.parseColor("#39B8B6"));
			   btnSave.setLayoutParams(lp);
			   temp.addView(btnSave);
			   btnSave.setOnClickListener(new OnClickListener(){
						public void onClick(View v)
						{						
							 Save("缓存本地成功");					    
									
						}
					}
					);
		}
		
		
		
		   
		   
		   Button	btnBack=new Button(LUploadActivity.this);
		
		   
		   
	   btnBack.setText("返回");
	   btnBack.setBackground(myDrawable1);
	   btnBack.setTextColor(Color.WHITE);
	 //  btnBack.setBackgroundColor(Color.parseColor("#39B8B6"));
	  // btnBack.setBackgroundColor(Color.parseColor("#39B8B6"));
	   btnBack.setLayoutParams(lp);
	   temp.addView(btnBack);
	   btnBack.setOnClickListener(new OnClickListener(){
				public void onClick(View v)
				{						
					// Save("缓存本地成功");		
					//ToastUtil3.showToast(LUploadActivity.this, "i am back");
					GoBack();
							
				}
			}
			);
		       
       
       
	}
	
	
	private void GoBack()
	{
		this.finish();
	}
	
	private void Save(String msg)
	{
		  
		//看看有没有拍照
		 File myphotos = new File(this.rivername);
	      File[] files = myphotos.listFiles();
	      if(files==null|| files.length==0)
	      {
	    	  ToastUtil3.showToast(LUploadActivity.this, "请先拍照");
	    	  return;
	      }
	      
	      if(this.jd.equals("")||this.wd.equals("")){
				ToastUtil3.showToast(LUploadActivity.this, "定位失败");
				return;
				
			}
	     
		//String jd=" 113.098306";
		//String wd="22.965039";
		//String hb="10";
		//String jsonString = "{\"code\":\""+Code+"\",\"ProjectID\":\""+ProjectID+"\",\"ProductID\":\""+ProductID+"\"}";
		//String jsonString = "{\"cjtype\":\""+cjType+"\",\"code\":\""+Code+"\",\"ProjectID\":\""+ProjectID+"\",\"ProductID\":\""+ProductID+"\",\"JD\":\""+jd+"\",\"WD\":\""+wd+"\",\"HB\":\""+hb+"\"}";
		String jsonString = "{\"cjtype\":\""+cjType+"\",\"code\":\""+Code+"\",\"ProjectID\":\""
				+ProjectID+"\",\"ProductID\":\""+ProductID+"\",\"JD\":\""+jd+"\",\"WD\":\""
				+wd+"\",\"HB\":\""+hb+"\",\"UserID\":\""+UserModel.UserID+"\"}";
		//先拼接内容
		LinearLayout temp=(LinearLayout)findViewById(R.id.riverPanel); 
		String stempString="{";
		int hitTime=0;
		// for (int i = 0; i < temp.getChildCount()-5; i++) 
		for (int i = 0; i < temp.getChildCount()-7; i++) 
		 {
			
			 if(elementL.get(i)=="1")
			 {
				 EditText et = (EditText) temp.getChildAt(i+1);
				 stempString+="\""+names.get(hitTime)+"\":\""+et.getText()+"\",";
				 hitTime++;
				 
				 
			 }
			 else if(elementL.get(i)=="2")
			 {
				
				 Spinner sp=(Spinner)temp.getChildAt(i+1);
				 stempString+="\""+names.get(hitTime)+"\":\""+(String) sp.getSelectedItem()+"\",";
				 hitTime++;
				 //stempString+= (String) sp.getSelectedItem();
			 }		
               
        }
		 
		  ToastUtil3.showToast(LUploadActivity.this, stempString);  	
		  stempString=stempString.substring(0,stempString.length()-1);
		  stempString+="}";
		
		
		
		
		
		
		
		
		
		  Date date = new Date();
			SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
			String timeString=dateFormat.format(date);
		
		
		
		File file = getFilesDir();
		   String path = file.getAbsolutePath()+"/river3.db";
		   helper = new DataBaseHelper(this, path, 1);
	        final SQLiteDatabase db = helper.getReadableDatabase();
	        db.execSQL("insert into szdlog values(null,?,?,?,?,?,?,?,?,?,?,?)",
	        		new String[]{timeString,region,productname,UserModel.UserID,rivername,jsonString,stempString,ProjectID,ProductID,Code,"未上报"} );
	        		//new String[]{timeString,region,productname,"4",rivername,jsonString,stempString,ProjectID,ProductID,Code,"未上报"} );
	        //ToastUtil3.showToast(LUploadActivity.this, "暂存成功");
	        
	       
	        
	        Intent intent = new Intent(LUploadActivity.this, ViewDataTableActivity.class);
	        Bundle bundle=new Bundle();
			bundle.putString("msg", msg);
			bundle.putString("view", "local");
			intent.putExtras(bundle);
		    startActivity(intent);	
	   
	}
	
	
	
	private void Upload() 
	{
		
		if(this.isGPSOpen==false)
		{
			ToastUtil3.showToast(LUploadActivity.this, "请先打开gps");
			return;
		}
		if(this.jd.equals("")||this.wd.equals("")){
			ToastUtil3.showToast(LUploadActivity.this, "定位失败");
			return;
			
		}
		//看看有没有拍照
		 File myphotos = new File(this.rivername);
	      File[] files = myphotos.listFiles();
	      if(files==null|| files.length==0)
	      {
	    	  ToastUtil3.showToast(LUploadActivity.this, "请先拍照");
	    	  return;
	      }
	     
	      
	    //看看网络是否正常
	      if(Check(LUploadActivity.this)==false)
		  {
			 // ToastUtil3.showToast(LUploadActivity.this, "网络不行，先暂存");  	
			  Save("网络不行，先暂存");
			  return;
		  }
	      
	      
	      
	      
	      
	      
	      
		//String jd=" 113.098306";
		//String wd="22.965039";
		//String hb="10";
		//String jsonString = "{\"code\":\""+Code+"\",\"ProjectID\":\""+ProjectID+"\",\"ProductID\":\""+ProductID+"\"}";
		
		//String jsonString = "{\"cjtype\":\""+cjType+"\",\"code\":\""+Code+"\",\"ProjectID\":\""+ProjectID+"\",\"ProductID\":\""+ProductID+"\",\"JD\":\""+jd+"\",\"WD\":\""+wd+"\",\"HB\":\""+hb+"\"}";
		String jsonString = "{\"cjtype\":\""+cjType+"\",\"code\":\""+Code+"\",\"ProjectID\":\""
		+ProjectID+"\",\"ProductID\":\""+ProductID+"\",\"JD\":\""+jd+"\",\"WD\":\""
		+wd+"\",\"HB\":\""+hb+"\",\"UserID\":\""+UserModel.UserID+"\"}";
		//先拼接内容
		LinearLayout temp=(LinearLayout)findViewById(R.id.riverPanel); 
		String stempString="{";
		int hitTime=0;
		 for (int i = 0; i < temp.getChildCount()-7; i++) 
		//for (int i = 1; i < temp.getChildCount()-4; i++) 
		 {
			
			 if(elementL.get(i)=="1")
			 {
				 EditText et = (EditText) temp.getChildAt(i+1);
				 stempString+="\""+names.get(hitTime)+"\":\""+et.getText()+"\",";
				 hitTime++;
				 
			 }
			 else if(elementL.get(i)=="2")
			 {
				
				 Spinner sp=(Spinner)temp.getChildAt(i+1);
				 stempString+="\""+names.get(hitTime)+"\":\""+(String) sp.getSelectedItem()+"\",";
				 hitTime++;
				 //stempString+= (String) sp.getSelectedItem();
			 }		
                
         }
		 
		  ToastUtil3.showToast(LUploadActivity.this, stempString);  	
		  stempString=stempString.substring(0,stempString.length()-1);
		  stempString+="}";
		  //jsonString+=stempString;			
		  Map<String, String> params0 = new HashMap<String, String>();
	       params0.put("GpsImgJson", jsonString);
	       params0.put("ExtendJson", stempString);

	      
	     //   String st= HttpHelper.invokePost(url,"AddImg", rivername, params0);
	       HttpThread thread = new HttpThread(handler);	
	       thread.doStart("http://139.129.19.220:7030/api/SZDLog/", null,"2",rivername,params0,ProjectID+"_"+ProductID+"_"+Code);
	  
	}
	
	
	  private void ResponseOnClick() {	
		  
		 
		    Intent intent=getIntent();
		    String type=intent.getStringExtra("type");
		    ProjectID=intent.getStringExtra("projectid");
		    ProductID=intent.getStringExtra("productid");
		    Code=intent.getStringExtra("code");
		    QDCode=intent.getStringExtra("code");
		    cjType=intent.getStringExtra("cjtype");
		    region=intent.getStringExtra("region");
		    productname=intent.getStringExtra("productname");
		    
		    if(Check(LUploadActivity.this)==false)
			  {
				  ToastUtil3.showToast(LUploadActivity.this, "网络不行");  	
				  ReadTxt();
				  return;
			  }
		    
		    
			HttpThread thread = new HttpThread(handler);	
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();   
			
			 nameValuePairs.add(new BasicNameValuePair("ProjectID", ProjectID));
			 nameValuePairs.add(new BasicNameValuePair("ProductID", ProductID));
			 nameValuePairs.add(new BasicNameValuePair("CJType", cjType));
			 // 加上采集人账号，用于判断是否是属于这个项目的
			 nameValuePairs.add(new BasicNameValuePair("UserID", UserModel.UserID));
			type="1";
			thread.doStart(URL, nameValuePairs,"1","",null,"");		
				
			
		}
	
	  private void WriteTXT(String st,String FilePath)
	  {
		  File filename=new File(FilePath);
		  try {
	            if(!filename.exists()) {
	                filename.createNewFile();              
	            }
	        }catch (Exception e) {
	            // TODO: handle exception
	            e.printStackTrace();
	        }
		  
		  try {
	            //写入的txt文档的路径
	            PrintWriter pw=new PrintWriter(FilePath);
	            //写入的内容
	            pw.write(st);
	            pw.flush();
	            pw.close();
	            //flag=true;
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
	  }
	  
	  
	  
	  
	  
	  public boolean Check(Context context)
		{
			   Activity act = (Activity) context;
		      
		        
		        NetworkInfo netIntfo = null;
		        try {
		        	ConnectivityManager  cm = (ConnectivityManager) act.getSystemService(act.CONNECTIVITY_SERVICE);
		            netIntfo =  cm.getActiveNetworkInfo();
		        } catch (Exception e) {
		            //异常处理 
		            Toast.makeText(act, "没有网络权限，请给予相关权限", Toast.LENGTH_LONG).show();
		            return false;
		        }
		        
		        if(netIntfo==null){
		           return false;
		            
		        }else{
		            //如果有网络 显示正常
		           return true;
		            
		        }
		       
		}
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  private void ReadTxt()
	  {
		  
		  String st="";
		  
		  
		  String filePath=Environment.getExternalStorageDirectory()  
	                 + "/szd/info/";
			
	        //  filePath+=QDCode+"_"+cjType+".txt";  //20201117屏蔽 改成用项目和产品来表示
		  filePath+=ProjectID+"_"+ProductID+"_"+cjType+".txt"; 
		  
		  
		  
		  
		  
		  
		  
		  
		  
		  File file = new File(filePath);
	        if(file.isFile() && file.exists()){
	            try {
	                FileInputStream fileInputStream = new FileInputStream(file);
	                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
	                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	                 
	                StringBuffer sb = new StringBuffer();
	                String text = null;
	                while((text = bufferedReader.readLine()) != null){
	                    sb.append(text);
	                }
	                st=sb.toString();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	        else{
	        	 ToastUtil3.showToast(LUploadActivity.this, "本地获取采集项失败");
				   Init(0);	
				   return;
	        }
	        
	        
	       
		  
		  
		  
		  
		  
		
		  try {				
						
				JSONObject jsonObject = JSON.parseObject(st);			
				
				int Code= jsonObject.getInteger("code");
				//String msg1=jsonObject.getString("msg");		
				if(Code==0) //如果返回结果正确
			    {
					
					
					String itemsString=jsonObject.getString("data");	
					LinearLayout temp=(LinearLayout)findViewById(R.id.riverPanel);
				  
				   //转换成数组数据并遍历
					JSONArray jarr=JSONArray.parseArray(itemsString);//JSON.parseArray(jsonStr);
					for (Iterator iterator = jarr.iterator(); iterator.hasNext();) {
						JSONObject job=(JSONObject)iterator.next();
						String textName=job.get("Name").toString();
						names.add(textName);
					
						TextView textView = new TextView(LUploadActivity.this);
		                textView.setText(textName);
		                //textView.setTextColor(Color.RED);
		                textView.setTextSize(20);		                
		                temp.addView(textView);
		                elementL.add("0");
		                
		                if(job.get("SItem1").equals("")==false) //说明是下拉框
		                {
			                
			                Spinner spinner=new Spinner(LUploadActivity.this);
			                List<String>  list =  new ArrayList<String>();
			                if(job.get("SItem1").equals("")==false)
			                	list.add(job.get("SItem1").toString());
			                if(job.get("SItem2").equals("")==false)
			                	list.add(job.get("SItem2").toString());
			                if(job.get("SItem3").equals("")==false)
			                	list.add(job.get("SItem3").toString());
			                if(job.get("SItem4").equals("")==false)
			                	list.add(job.get("SItem4").toString());
			                if(job.get("SItem5").equals("")==false)
			                	list.add(job.get("SItem5").toString());
			                if(job.get("SItem6").equals("")==false)
			                	list.add(job.get("SItem6").toString());
			               
			                ArrayAdapter<String> adapter=new ArrayAdapter<String>(LUploadActivity.this,R.layout.personal_spinner,list);
			                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			                spinner.setAdapter(adapter);
			                spinner.setPrompt(textName);	
			                
			                spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			        			@Override
			        			public void onItemSelected(AdapterView<?> parent, View view,
			        					int position, long id) {
			        				position++;
			        				//鐐瑰嚮澶勭悊浜嬩欢
			        			}
			        			@Override
			        			public void onNothingSelected(AdapterView<?> parent) {
			        			}		
			        		});
			                
//			                ArrayAdapter<String> adapter=new ArrayAdapter<String>(LUploadActivity.this,android.R.layout.simple_spinner_item,list);
//			                spinner.setAdapter(adapter);
//			                spinner.setPrompt(textName);			                
			                temp.addView(spinner); 
			                elementL.add("2");
		                }
		                else  //说明是输入框
		                {
		                	EditText editText=new EditText(LUploadActivity.this);
			                editText.setHeight(70);	
			               // editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
			                editText.setMinLines(5);
			                editText.setGravity(Gravity.TOP);
			                //取得资源的引用。
			                Resources resource = getBaseContext().getResources() ;
			                //得到图片的引用
			                Drawable myDrawable = resource.getDrawable(R.drawable.qq_edit_login) ;			                
			                editText.setBackground(myDrawable);
			                temp.addView(editText);
			                elementL.add("1");
						}
						
					}
				  
					 temp.invalidate();
				  ToastUtil3.showToast(LUploadActivity.this, "本地获取采集项成功");
				  Init(1);			
			   }
			   else 
			   {
				   
				   ToastUtil3.showToast(LUploadActivity.this, "本地获取采集项失败");
				   Init(0);	
			   }
			  
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			String st= msg.getData().getString("result");
			String htype=msg.getData().getString("htype");
			Log.e("result", "handleMessage: result" + st );
			Log.e("result", "handleMessage: htype" + htype );
			if(htype.equals("1"))  //获取信息项的处理
			{
				if(st.equals("请求失败"))
				{
					ReadTxt();
					return;
				}
				
	         try {				
					st = StringEscapeUtils.unescapeJava(st);
					st=st.substring(0,st.length()-1);
					st=st.substring(1,st.length());				
					JSONObject jsonObject = JSON.parseObject(st);			
					
					int Code= jsonObject.getInteger("code");
					//String msg1=jsonObject.getString("msg");		
					if(Code==0) //如果返回结果正确
				    {
						//20200718写到TXT中
						 String filePath=Environment.getExternalStorageDirectory()  
				                 + "/szd/info/";
						 File fileFolder = new File(filePath);  
				           if (!fileFolder.exists()) { // 如果目录不存在，则创建 
				               //fileFolder.mkdir();  
				           	fileFolder.mkdirs();
				           }  
				         // filePath+=QDCode+"_"+cjType+".txt";
				      
				           //  filePath+=QDCode+"_"+cjType+".txt";  //20201117屏蔽 改成用项目和产品来表示
				 		  filePath+=ProjectID+"_"+ProductID+"_"+cjType+".txt"; 
						
						
						
						
						WriteTXT(st,filePath);
					    
						
						String itemsString=jsonObject.getString("data");	
						LinearLayout temp=(LinearLayout)findViewById(R.id.riverPanel);
					  
					   //转换成数组数据并遍历
						JSONArray jarr=JSONArray.parseArray(itemsString);//JSON.parseArray(jsonStr);
						for (Iterator iterator = jarr.iterator(); iterator.hasNext();) {
							JSONObject job=(JSONObject)iterator.next();
							String textName=job.get("Name").toString();
							names.add(textName);
						
							TextView textView = new TextView(LUploadActivity.this);
			                textView.setText(textName);
			                //textView.setTextColor(Color.RED);
			                textView.setTextSize(20);		                
			                temp.addView(textView);
			                elementL.add("0");
			                
			                if(job.get("SItem1").equals("")==false) //说明是下拉框
			                {
				                
				                Spinner spinner=new Spinner(LUploadActivity.this);
				                List<String>  list =  new ArrayList<String>();
				                if(job.get("SItem1").equals("")==false)
				                	list.add(job.get("SItem1").toString());
				                if(job.get("SItem2").equals("")==false)
				                	list.add(job.get("SItem2").toString());
				                if(job.get("SItem3").equals("")==false)
				                	list.add(job.get("SItem3").toString());
				                if(job.get("SItem4").equals("")==false)
				                	list.add(job.get("SItem4").toString());
				                if(job.get("SItem5").equals("")==false)
				                	list.add(job.get("SItem5").toString());
				                if(job.get("SItem6").equals("")==false)
				                	list.add(job.get("SItem6").toString());
				               
				                ArrayAdapter<String> adapter=new ArrayAdapter<String>(LUploadActivity.this,R.layout.personal_spinner,list);
				                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				                spinner.setAdapter(adapter);
				                spinner.setPrompt(textName);	
				                
				                spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

				        			@Override
				        			public void onItemSelected(AdapterView<?> parent, View view,
				        					int position, long id) {
				        				position++;
				        				
				        			}
				        			@Override
				        			public void onNothingSelected(AdapterView<?> parent) {
				        			}		
				        		});
				                
//				                ArrayAdapter<String> adapter=new ArrayAdapter<String>(LUploadActivity.this,android.R.layout.simple_spinner_item,list);
//				                spinner.setAdapter(adapter);
//				                spinner.setPrompt(textName);			                
				                temp.addView(spinner); 
				                elementL.add("2");
			                }
			                else  //说明是输入框
			                {
			                	EditText editText=new EditText(LUploadActivity.this);
				                editText.setHeight(70);	
				               // editText.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
				                editText.setMinLines(5);
				                editText.setGravity(Gravity.TOP);
				                //取得资源的引用。
				                Resources resource = getBaseContext().getResources() ;
				                //得到图片的引用
				                Drawable myDrawable = resource.getDrawable(R.drawable.qq_edit_login) ;			                
				                editText.setBackground(myDrawable);
				                temp.addView(editText);
				                elementL.add("1");
							}
							
						}
					  
						 temp.invalidate();
					  ToastUtil3.showToast(LUploadActivity.this, "获取采集项成功");
					  Init(1);			
				   }
				   else 
				   {
					   String wrongmsg= jsonObject.getString("msg");
					   ToastUtil3.showToast(LUploadActivity.this, "获取采集项失败,原因是"+wrongmsg);
					   Init(0);	
				   }
				  
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else  //提交图片后的处理
			{
				if(st.equals("请求失败"))
				{
					Save("提交数据失败，先缓存1");
					return;
				}
				
	         try {				
					st = StringEscapeUtils.unescapeJava(st);
					st=st.substring(0,st.length()-1);
					st=st.substring(1,st.length());				
					JSONObject jsonObject = JSON.parseObject(st);			
					
					int Code= jsonObject.getInteger("code");
					//String msg1=jsonObject.getString("msg");		
					if(Code==0) //如果返回结果正确
				    {
						   
				        Intent intent = new Intent(LUploadActivity.this, ViewDataTableActivity.class);
				        Bundle bundle=new Bundle();
						bundle.putString("msg", "提交数据成功");
						intent.putExtras(bundle);
					    startActivity(intent);	
				    }
					else {
						Save("提交数据失败，先缓存2");
						
					}
					
					
				    }
	               catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	         
	         
	         
			}
		}
	};
      
      
      private class HttpThread extends Thread{
  		Handler handler = null;
  		String url = null;
  	    String type="";//1是获取信息项，2是上传信息项
  		String fileName="";//文件名称
  		String lsm="";
  		//String cjType="";  //0,1,2,3用来获取采集类型
  		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();   
  		ProgressDialog progressDialog = null;
  		Map<String, String> params0= new HashMap<String, String>();;
  		
  		//构造函数
  		public HttpThread(Handler handler){
  			this.handler = handler;
  		}
  		
  		/**
  		 * 启动线程
  		 */
  		public void doStart(String url,
  				List<NameValuePair> nameValuePairs,String type,String fileName,Map<String, String> params0,String lsm){
  			this.url=url;
  			this.type=type;
  			this.nameValuePairs = nameValuePairs;
  			this.fileName=fileName;
  			this.params0=params0;
  			this.lsm=lsm;
  			//this.cjType=cjType;
  			progressDialog = ProgressDialog.show(LUploadActivity.this,
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
  			super.run();
  			try{  
  				String st;
  				if(type=="1")   
  				{
  					st= HttpHelper.invoke1(url,"GetInfoItems", nameValuePairs);  
  				   //取消进度框
  					progressDialog.dismiss();
  					//构造消息  					
  					Message message = handler.obtainMessage();
  					Bundle bundle = new Bundle();
  					bundle.putString("result", st);
  					bundle.putString("htype", "1");
  					message.setData(bundle);
  					handler.sendMessage(message);
  				}
  				
  				else if(type=="2")   
  				{
  					st= HttpHelper.invokePost1(url,"Add_Img", fileName, params0,lsm);
  					//取消进度框
  					progressDialog.dismiss();
  					//构造消息  					
  					Message message = handler.obtainMessage();
  					Bundle bundle = new Bundle();
  					bundle.putString("result", st);
  					bundle.putString("htype", "2");
  					message.setData(bundle);
  					handler.sendMessage(message);
				}
  					
  				
  			}catch(Exception e){
  				e.printStackTrace();
  			}
  		
  		}
      }

}
