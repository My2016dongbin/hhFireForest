package com.river.szdapp;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public abstract class BaseActivity extends Activity {
	protected String UserID="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_base);
		//判断是否有登陆
		//加载之前保存的用户名
		SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
		String userid=preference.getString("User","");
		if(userid!=null&&userid!="")
		{
			this.UserID=userid;
		}
		else  //跳转到登陆页面
		{
			Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
			startActivity(intent);
		}

	}

	@Override
	protected void onResume() {

		//判断是否有登陆
		//加载之前保存的用户名
		SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
		String userid=preference.getString("User","");
		if(userid!=null&&userid!="")
		{
			this.UserID=userid;
		}
		else  //跳转到登陆页面
		{
			Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
			startActivity(intent);
		}

	}


}
