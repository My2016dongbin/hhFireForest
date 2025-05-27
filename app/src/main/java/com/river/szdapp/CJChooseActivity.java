package com.river.szdapp;



import com.river.gridview.MyGridAdapter;
import com.river.gridview.MyGridView;
import com.river.util.ToastUtil3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class CJChooseActivity extends Activity {
	private MyGridView gridview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cjchoose);
		initView();
		Intent intent=getIntent();
		String msg=intent.getStringExtra("msg");
		if(msg!=null)
		{
			ToastUtil3.showToast(CJChooseActivity.this, msg);
		}

		setTitle("采集类型选择");
	}

	private void initView() {
		gridview=(MyGridView) findViewById(R.id.gridview);
		gridview.setAdapter(new MyGridAdapter(this));



	}
}
