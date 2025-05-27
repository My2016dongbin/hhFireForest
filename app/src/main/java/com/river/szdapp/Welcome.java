package com.river.szdapp;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class Welcome extends Activity implements Runnable{
	
	private ImageView imageView;
	private AnimationDrawable animDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.welcome);
		
		imageView = (ImageView) findViewById(R.id.welcome);

		animDrawable=(AnimationDrawable) imageView.getBackground();
        

	    new Thread(Welcome.this).start();
	}



	public void run() {
		try{
		Thread.sleep(2000L);
		Intent intent=new Intent();
		intent.setClass(Welcome.this, MainTabActivity.class);
		startActivity(intent);
		this.finish();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
