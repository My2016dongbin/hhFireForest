package com.river.szdapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CheckGPRSActivity extends Activity {

	private ConnectivityManager cm ;
	private TextView textView ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_gprs);
		Check(this);
	}




	public void Check(Context context)
	{
		Activity act = (Activity) context;
		textView = (TextView) act.findViewById(R.id.textView1);

		int color ; //存放颜色
		String text ; //存放显示的内容

		NetworkInfo netIntfo = null;
		try {
			cm = (ConnectivityManager) act.getSystemService(act.CONNECTIVITY_SERVICE);
			netIntfo =  cm.getActiveNetworkInfo();
		} catch (Exception e) {
			//异常处理
			Toast.makeText(act, "没有网络权限，请给予相关权限", Toast.LENGTH_LONG).show();
		}

		if(netIntfo==null){
			//如果没有网络 显示不正常
			text = act.getResources().getString(R.string.netWerk_1);
			color = act.getResources().getColor(R.color.palegreen);

		}else{
			//如果有网络 显示正常
			text = act.getResources().getString(R.string.netWerk_0);
			color = act.getResources().getColor(R.color.beige);

		}
		//设置文本
		textView.setText(text);
		//设置背景颜色
		textView.setBackgroundColor(color);
	}
}
