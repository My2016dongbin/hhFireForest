package com.river.szdapp;



import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.river.szdapp.BaiduTestActivity.MyLocationListenner;
import com.river.util.GetLocation;
import com.river.util.ToastUtil3;
import com.river.util.X5WebView;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;

public class MainActivity extends Activity {
//	public class MainActivity extends BaseActivity {
	/**
	 * 作为一个浏览器的示例展示出来，采用android+web的模式
	 */
	private X5WebView mWebView;
	private ViewGroup mViewParent;
	private ImageButton mBack;
	private ImageButton mForward;
	private ImageButton mExit;
	private ImageButton mHome;
	private ImageButton mMore;
	private Button mGo;
	private EditText mUrl;

	//private static final String mHomeUrl = "http://app.html5.qq.com/navi/index";
	private static  String mHomeUrl = "file:////android_asset/TDTMap1.html?";
	private static final String TAG = "SdkDemo";
	private static final int MAX_LENGTH = 14;
	private boolean mNeedTestPage = false;

	private final int disable = 120;
	private final int enable = 255;

	private ProgressBar mPageLoadingProgressBar = null;

	private ValueCallback<Uri> uploadFile;

	private URL mIntentUrl;
	
	private MyLocationListenner myListener;
	
	
  private boolean isGPSOpen=false;
	  
	  Location location;   
	  
	  private String jd="";
	  private String wd="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
//		
//		  SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
//			String userid=preference.getString("User","");
//			if(userid!=null&&userid!="")
//			{
//				//this.UserID=userid;
//			}
//			else  //跳转到登陆页面
//			{
//				Intent intent = new Intent(MainActivity.this, LLoginActivity.class);			
//				startActivity(intent);	
//			}
//		
		
		
		
		
		
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

//		GetLocation g=new GetLocation();
//		myListener=new 	MyLocationListenner();
//		g.GetL(getApplicationContext(), myListener);
		
		// GetGPSReady();
		// updateLocation(location);
		gostrate();

	}
	
	
	
	
	public void GetGPSReady() {
		
		
		LocationManager locationManager
        = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if(gps==false)
		{
			ToastUtil3.showToast(MainActivity.this, "请先打开gps");
			return;
		}				
		
		else
			this.isGPSOpen=true;
		
		
		
		
		 // 閼惧嘲褰嘗ocationManager鐎电钖�   
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);   
  
        // 鐎规矮绠烠riteria鐎电钖�   
        Criteria criteria = new Criteria();   
        // 鐠佸墽鐤嗙�规矮缍呯划鍓р�樻惔锟� Criteria.ACCURACY_COARSE 濮ｆ棁绶濈划妤冩殣閿涳拷 Criteria.ACCURACY_FINE閸掓瑦鐦潏鍐翱缂侊拷   
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   
        // 鐠佸墽鐤嗛弰顖氭儊闂囷拷鐟曚焦鎹ｉ幏鏂句繆閹拷 Altitude   
        criteria.setAltitudeRequired(true);   
        // 鐠佸墽鐤嗛弰顖氭儊闂囷拷鐟曚焦鏌熸担宥勪繆閹拷 Bearing   
        criteria.setBearingRequired(true);   
        // 鐠佸墽鐤嗛弰顖氭儊閸忎浇顔忔潻鎰儉閸熷棙鏁圭拹锟�   
        criteria.setCostAllowed(true);   
        // 鐠佸墽鐤嗙�靛湱鏁稿┃鎰畱闂囷拷濮癸拷   
        criteria.setPowerRequirement(Criteria.POWER_LOW);   
  
        // 閼惧嘲褰嘒PS娣団剝浼呴幓鎰返閼帮拷   
        String bestProvider = lm.getBestProvider(criteria, true);   
        Log.i("yao", "bestProvider = " + bestProvider);   
  
        // 閼惧嘲褰囩�规矮缍呮穱鈩冧紖   
        location = lm.getLastKnownLocation(bestProvider);   
        
        
        
     // 娴ｅ秶鐤嗛惄鎴濇儔閸ｏ拷   
        LocationListener locationListener = new LocationListener() {   
  
            // 瑜版挷缍呯純顔芥暭閸欐ɑ妞傜憴锕�褰�   
            @Override  
            public void onLocationChanged(Location location) {   
                Log.i("yao", location.toString());   
                updateLocation(location);   
            }   
  
            // Provider婢惰鲸鏅ラ弮鎯靶曢崣锟�   
            @Override  
            public void onProviderDisabled(String arg0) {   
                Log.i("yao", arg0);   
  
            }   
  
            // Provider閸欘垳鏁ら弮鎯靶曢崣锟�   
            @Override  
            public void onProviderEnabled(String arg0) {   
                Log.i("yao", arg0);   
            }   
  
            // Provider閻樿埖锟戒焦鏁奸崣妯绘鐟欙箑褰�   
            @Override  
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {   
                Log.i("yao", "onStatusChanged");   
            }   
        };   
  
        // 500濮ｎ偆顫楅弴瀛樻煀娑擄拷濞嗏槄绱濊箛鐣屾殣娴ｅ秶鐤嗛崣妯哄   
      //  lm.requestLocationUpdates(bestProvider, 50000, 0, locationListener);   
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500000, 0, locationListener);
    }   
  
    // 閺囧瓨鏌婃担宥囩枂娣団剝浼�   
    // 鏇存柊浣嶇疆淇℃伅   
    private void updateLocation(Location location) {   
        if (location != null) {   
        	
//            tv1.setText("瀹氫綅瀵硅薄淇℃伅濡備笅锛�" + location.toString()+"\n\t鍏朵腑娴锋嫈:"+location.getAltitude() +"\n\t鏂瑰悜:"+location.getBearing()+ "\n\t鍏朵腑缁忓害锛�" + location.getLongitude() + "\n\t鍏朵腑绾害锛�"  
//                    + location.getLatitude()+"\n\t鎻愪緵鍟嗭細"+location.getProvider()+"\n\t閫熷害锛�"+location.getSpeed()+"\n\t鏃堕棿锛�"+location.getTime());   
        	jd=String.valueOf(location.getLongitude());
        	wd=String.valueOf(location.getLatitude());
        	String ac=String.valueOf(location.getAccuracy());
        	
        	
        	
        	
        	
        	StringBuffer sb = new StringBuffer(256);
			sb.append("la=");
			sb.append(wd);
			sb.append("&lo=");
			sb.append(jd);
			sb.append("&ac=");
			sb.append(ac);
			sb.append("&UserID=");
			sb.append("3");
			
			mHomeUrl+=sb.toString();
			
			
			
			
			
			
			
			
			
			
			
			Intent intent = getIntent();
			if (intent != null) {
				try {
					mIntentUrl = new URL(intent.getData().toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {

				} catch (Exception e) {
				}
			}
			//
			try {
				if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
					getWindow()
							.setFlags(
									android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
									android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
				}
			} catch (Exception e) {
			}

			/*
			 * getWindow().addFlags(
			 * android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
			 */
			setContentView(R.layout.activity_main);
			mViewParent = (ViewGroup) findViewById(R.id.webView1);

			initBtnListenser();

			mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        } else {   
            Log.i("yao", "娌℃湁鑾峰彇鍒板畾浣嶅璞ocation");   
        }   
    }     
        
	
	
	
	
	public void gostrate()
	{
		Intent intent = getIntent();
		if (intent != null) {
			try {
				mIntentUrl = new URL(intent.getData().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {

			} catch (Exception e) {
			}
		}
		//
		try {
			if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
				getWindow()
						.setFlags(
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
			}
		} catch (Exception e) {
		}

		/*
		 * getWindow().addFlags(
		 * android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		setContentView(R.layout.activity_main);
		mViewParent = (ViewGroup) findViewById(R.id.webView1);

		initBtnListenser();

		mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
	}
	
	
	
	
	
	
	public class MyLocationListenner implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("la=");
			sb.append(location.getLatitude());
			sb.append("&lo=");
			sb.append(location.getLongitude());
			
			mHomeUrl+=sb.toString();
			
			
			
			
			
			
			
			
			
			Intent intent = getIntent();
			if (intent != null) {
				try {
					mIntentUrl = new URL(intent.getData().toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {

				} catch (Exception e) {
				}
			}
			//
			try {
				if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
					getWindow()
							.setFlags(
									android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
									android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
				}
			} catch (Exception e) {
			}

			/*
			 * getWindow().addFlags(
			 * android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
			 */
			setContentView(R.layout.activity_main);
			mViewParent = (ViewGroup) findViewById(R.id.webView1);

			initBtnListenser();

			mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	
	
	
	
	
	
	
	

	private void changGoForwardButton(WebView view) {
		if (view.canGoBack())
			mBack.setAlpha(enable);
		else
			mBack.setAlpha(disable);
		if (view.canGoForward())
			mForward.setAlpha(enable);
		else
			mForward.setAlpha(disable);
		if (view.getUrl() != null && view.getUrl().equalsIgnoreCase(mHomeUrl)) {
			mHome.setAlpha(disable);
			mHome.setEnabled(false);
		} else {
			mHome.setAlpha(enable);
			mHome.setEnabled(true);
		}
	}

	private void initProgressBar() {
		mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
																				// ProgressBar(getApplicationContext(),
																				// null,
																				// android.R.attr.progressBarStyleHorizontal);
		mPageLoadingProgressBar.setMax(100);
		mPageLoadingProgressBar.setProgressDrawable(this.getResources()
				.getDrawable(R.drawable.color_progressbar));
	}

	private void init() {

		mWebView = new X5WebView(this, null);

		mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));

		initProgressBar();

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
				mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
				if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
					changGoForwardButton(view);
				/* mWebView.showLog("test Log"); */
			}
		});

		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
					JsResult arg3) {
				return super.onJsConfirm(arg0, arg1, arg2, arg3);
			}

			View myVideoView;
			View myNormalView;
			CustomViewCallback callback;

			// /////////////////////////////////////////////////////////
			//
			/**
			 * 全屏播放配置
			 */
			@Override
			public void onShowCustomView(View view,
					CustomViewCallback customViewCallback) {
				FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
				ViewGroup viewGroup = (ViewGroup) normalView.getParent();
				viewGroup.removeView(normalView);
				viewGroup.addView(view);
				myVideoView = view;
				myNormalView = normalView;
				callback = customViewCallback;
			}

			@Override
			public void onHideCustomView() {
				if (callback != null) {
					callback.onCustomViewHidden();
					callback = null;
				}
				if (myVideoView != null) {
					ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
					viewGroup.removeView(myVideoView);
					viewGroup.addView(myNormalView);
				}
			}

			@Override
			public boolean onJsAlert(WebView arg0, String arg1, String arg2,
					JsResult arg3) {
				/**
				 * 这里写入你自定义的window alert
				 */
				return super.onJsAlert(null, arg1, arg2, arg3);
			}
		});

		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String arg0, String arg1, String arg2,
					String arg3, long arg4) {
				TbsLog.d(TAG, "url: " + arg0);
				new AlertDialog.Builder(MainActivity.this)
						.setTitle("allow to download？")
						.setPositiveButton("yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Toast.makeText(
												MainActivity.this,
												"fake message: i'll download...",
												1000).show();
									}
								})
						.setNegativeButton("no",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										Toast.makeText(
												MainActivity.this,
												"fake message: refuse download...",
												Toast.LENGTH_SHORT).show();
									}
								})
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										// TODO Auto-generated method stub
										Toast.makeText(
												MainActivity.this,
												"fake message: refuse download...",
												Toast.LENGTH_SHORT).show();
									}
								}).show();
			}
		});

		WebSettings webSetting = mWebView.getSettings();
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(false);
		// webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		// webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
		webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
		webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
				.getPath());
		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		// webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		// webSetting.setPreFectch(true);
		long time = System.currentTimeMillis();
		if (mIntentUrl == null) {
			mWebView.loadUrl(mHomeUrl);
		} else {
			mWebView.loadUrl(mIntentUrl.toString());
		}
		TbsLog.d("time-cost", "cost time: "
				+ (System.currentTimeMillis() - time));
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().sync();
	}

	private void initBtnListenser() {
		mBack = (ImageButton) findViewById(R.id.btnBack1);
		mForward = (ImageButton) findViewById(R.id.btnForward1);
		mExit = (ImageButton) findViewById(R.id.btnExit1);
		mHome = (ImageButton) findViewById(R.id.btnHome1);
		mGo = (Button) findViewById(R.id.btnGo1);
		mUrl = (EditText) findViewById(R.id.editUrl1);
		mMore = (ImageButton) findViewById(R.id.btnMore);
		if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16) {
			mBack.setAlpha(disable);
			mForward.setAlpha(disable);
			mHome.setAlpha(disable);
		}
		mHome.setEnabled(false);

		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView != null && mWebView.canGoBack())
					mWebView.goBack();
			}
		});

		mForward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView != null && mWebView.canGoForward())
					mWebView.goForward();
			}
		});

		mGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = mUrl.getText().toString();
				mWebView.loadUrl(url);
				mWebView.requestFocus();
			}
		});

		mMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "not completed",
						Toast.LENGTH_LONG).show();
			}
		});

		mUrl.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mGo.setVisibility(View.VISIBLE);
					if (null == mWebView.getUrl())
						return;
					if (mWebView.getUrl().equalsIgnoreCase(mHomeUrl)) {
						mUrl.setText("");
						mGo.setText("首页");
						mGo.setTextColor(0X6F0F0F0F);
					} else {
						mUrl.setText(mWebView.getUrl());
						mGo.setText("进入");
						mGo.setTextColor(0X6F0000CD);
					}
				} else {
					mGo.setVisibility(View.GONE);
					String title = mWebView.getTitle();
					if (title != null && title.length() > MAX_LENGTH)
						mUrl.setText(title.subSequence(0, MAX_LENGTH) + "...");
					else
						mUrl.setText(title);
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}

		});

		mUrl.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				String url = null;
				if (mUrl.getText() != null) {
					url = mUrl.getText().toString();
				}

				if (url == null
						|| mUrl.getText().toString().equalsIgnoreCase("")) {
					mGo.setText("请输入网址");
					mGo.setTextColor(0X6F0F0F0F);
				} else {
					mGo.setText("进入");
					mGo.setTextColor(0X6F0000CD);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}
		});

		mHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mWebView != null)
					mWebView.loadUrl(mHomeUrl);
			}
		});

		mExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				android.os.Process.killProcess(Process.myPid());
			}
		});
	}

	boolean[] m_selected = new boolean[] { true, true, true, true, false,
			false, true };

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack()) {
				mWebView.goBack();
				if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 16)
					changGoForwardButton(mWebView);
				return true;
			} else
				return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		TbsLog.d(TAG, "onActivityResult, requestCode:" + requestCode
				+ ",resultCode:" + resultCode);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				if (null != uploadFile) {
					Uri result = data == null || resultCode != RESULT_OK ? null
							: data.getData();
					uploadFile.onReceiveValue(result);
					uploadFile = null;
				}
				break;
			default:
				break;
			}
		} else if (resultCode == RESULT_CANCELED) {
			if (null != uploadFile) {
				uploadFile.onReceiveValue(null);
				uploadFile = null;
			}

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent == null || mWebView == null || intent.getData() == null)
			return;
		mWebView.loadUrl(intent.getData().toString());
	}

	@Override
	protected void onDestroy() {
		if (mTestHandler != null)
			mTestHandler.removeCallbacksAndMessages(null);
		if (mWebView != null)
			mWebView.destroy();
		super.onDestroy();
	}

	public static final int MSG_OPEN_TEST_URL = 0;
	public static final int MSG_INIT_UI = 1;
	private final int mUrlStartNum = 0;
	private int mCurrentUrl = mUrlStartNum;
	private Handler mTestHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_OPEN_TEST_URL:
				if (!mNeedTestPage) {
					return;
				}

				String testUrl = "file:///sdcard/outputHtml/html/"
						+ Integer.toString(mCurrentUrl) + ".html";
				if (mWebView != null) {
					mWebView.loadUrl(testUrl);
				}

				mCurrentUrl++;
				break;
			case MSG_INIT_UI:
				init();
				break;
			}
			super.handleMessage(msg);
		}
	};

}
