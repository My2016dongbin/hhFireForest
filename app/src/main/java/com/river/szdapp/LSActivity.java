package com.river.szdapp;

import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LSActivity extends Activity {

	private com.tencent.smtt.sdk.WebView mWebView;

	private WebViewClient client = new WebViewClient() {
		/**
		 * 防止加载网页时调起系统浏览器
		 */
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
		String userid=preference.getString("User","");
		if(userid!=null&&userid!="")
		{
			//this.UserID=userid;
			UserModel.UserID=userid;
		}
		else  //跳转到登陆页面
		{
			Intent intent = new Intent(LSActivity.this, LLoginActivity.class);
			startActivity(intent);
		}

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.activity_ls);
		mWebView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.web_filechooser);
		//一些初始化配置
		initWebViewSettings();
		//mWebView.loadUrl("file:////android_asset/TDTMap.html?");


//	        if(UserModel.UserID=="")
//	        {
//	        	Intent intent = new Intent(LSActivity.this, LLoginActivity.class);
//      			startActivity(intent);
//	        }












		int a=(int)(Math.random()*1000);
		String rr=String.valueOf(a);
//		mWebView.loadUrl("http://47.104.249.216:7031/TDTMap.html?UserID="+UserModel.UserID+"&rmd="+rr);
		mWebView.loadUrl("http://139.129.19.220:7031/TDTMap.html?UserID="+UserModel.UserID+"&rmd="+rr);
		mWebView.getView().setClickable(true);






		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
									   JsResult arg3) {
				return super.onJsConfirm(arg0, arg1, arg2, arg3);
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



		//   mWebView.setWebViewClient(client);

//	        mWebView.setWebViewClient(new WebViewClient() {
//	            @Override
//	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//	                return false;
//	            }
//
//	            @Override
//	            public void onPageFinished(WebView view, String url) {
//	                super.onPageFinished(view, url);
//	            }
//	        });



		//初始化按钮，并绑定监听事件
		Button btnLogin=(Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
										public void onClick(View v)
										{

											test();

										}
									}
		);







	}





	public void  test() {

		initWebViewSettings();
		int a=(int)(Math.random()*1000);
		String rr=String.valueOf(a);
//		mWebView.loadUrl("http://47.104.249.216:7031/TDTMap.html?UserID=escape("+UserModel.UserID+")&rmd="+rr);
//		mWebView.loadUrl("http://139.129.19.220:7031/TDTMap.html?UserID=escape("+UserModel.UserID+")&rmd="+rr);//bingo 报错替换
		mWebView.loadUrl("http://139.129.19.220:7031/TDTMap.html?UserID="+UserModel.UserID+"&rmd="+rr);
		mWebView.getView().setClickable(true);




		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
									   JsResult arg3) {
				return super.onJsConfirm(arg0, arg1, arg2, arg3);
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

	}




	private void initWebViewSettings() {
		WebSettings webSetting = mWebView.getSettings();
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setLoadWithOverviewMode(true);
		webSetting.setSupportMultipleWindows(false);
		webSetting.setAppCacheEnabled(true);
		// webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setJavaScriptEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		//  webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

	}
}

