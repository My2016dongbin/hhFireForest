package com.river.szdapp;




import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

public class MainTabActivity extends TabActivity implements OnCheckedChangeListener{

	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	//private Intent mEIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);














		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintabs);

		SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
		String userid=preference.getString("User","");
		if(userid!=null&&userid!="")
		{
			//this.UserID=userid;
			UserModel.UserID=userid;
		}
		else  //跳转到登陆页面
		{
			Intent intent = new Intent(MainTabActivity.this, LLoginActivity.class);
			startActivity(intent);
		}

		this.mAIntent = new Intent(this,LSActivity.class);
		// this.mAIntent = new Intent(this,RiverActivity.class);
		// this.mBIntent = new Intent(this,CJChooseActivity.class);
		this.mBIntent = new Intent(this,LSCJChooseActivity.class);
		this.mCIntent = new Intent(this,ViewDataTableActivity.class);
		this.mDIntent = new Intent(this,SettingsActivity.class);
		//  this.mEIntent = new Intent(this,SettingsActivity.class);

		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button0)).setTextColor(getResources().getColorStateList(R.color.darkcyan));

		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button2))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button3))
				.setOnCheckedChangeListener(this);
//        ((RadioButton) findViewById(R.id.radio_button4))
//		.setOnCheckedChangeListener(this);
//
		setupIntent();




	}



	public void SwitchButton(int type)
	{
		if(type==1)
		{
			RadioButton b=	 ((RadioButton) findViewById(R.id.radio_button0));
			Drawable drawable = getResources().getDrawable(R.drawable.icon_1_y);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
			b.setCompoundDrawables(null, drawable, null, null);

			b.setTextColor(getResources().getColorStateList(R.color.darkcyan));



			RadioButton b1=	 ((RadioButton) findViewById(R.id.radio_button1));
			Drawable drawable1 = getResources().getDrawable(R.drawable.icon_2_n);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),drawable1.getMinimumHeight());
			b1.setCompoundDrawables(null, drawable1, null, null);
			b1.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b2=	 ((RadioButton) findViewById(R.id.radio_button2));
			Drawable drawable2 = getResources().getDrawable(R.drawable.icon_3_n);
			drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),drawable2.getMinimumHeight());
			b2.setCompoundDrawables(null, drawable2, null, null);
			b2.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b3=	 ((RadioButton) findViewById(R.id.radio_button3));
			Drawable drawable3 = getResources().getDrawable(R.drawable.icon_4_n);
			drawable3.setBounds(0, 0, drawable3.getMinimumWidth(),drawable3.getMinimumHeight());
			b3.setCompoundDrawables(null, drawable3, null, null);
			b3.setTextColor(getResources().getColorStateList(R.color.black));
		}

		if(type==2)
		{
			RadioButton b=	 ((RadioButton) findViewById(R.id.radio_button1));
			Drawable drawable = getResources().getDrawable(R.drawable.icon_2_y);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
			b.setCompoundDrawables(null, drawable, null, null);
			b.setTextColor(getResources().getColorStateList(R.color.darkcyan));

			RadioButton b1=	 ((RadioButton) findViewById(R.id.radio_button0));
			Drawable drawable1 = getResources().getDrawable(R.drawable.icon_1_n);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),drawable1.getMinimumHeight());
			b1.setCompoundDrawables(null, drawable1, null, null);
			b1.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b2=	 ((RadioButton) findViewById(R.id.radio_button2));
			Drawable drawable2 = getResources().getDrawable(R.drawable.icon_3_n);
			drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),drawable2.getMinimumHeight());
			b2.setCompoundDrawables(null, drawable2, null, null);
			b2.setTextColor(getResources().getColorStateList(R.color.black));


			RadioButton b3=	 ((RadioButton) findViewById(R.id.radio_button3));
			Drawable drawable3 = getResources().getDrawable(R.drawable.icon_4_n);
			drawable3.setBounds(0, 0, drawable3.getMinimumWidth(),drawable3.getMinimumHeight());
			b3.setCompoundDrawables(null, drawable3, null, null);
			b3.setTextColor(getResources().getColorStateList(R.color.black));
		}
		else if(type==3)
		{
			RadioButton b=	 ((RadioButton) findViewById(R.id.radio_button2));
			Drawable drawable = getResources().getDrawable(R.drawable.icon_3_y);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
			b.setCompoundDrawables(null, drawable, null, null);
			b.setTextColor(getResources().getColorStateList(R.color.darkcyan));

			RadioButton b1=	 ((RadioButton) findViewById(R.id.radio_button0));
			Drawable drawable1 = getResources().getDrawable(R.drawable.icon_1_n);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),drawable1.getMinimumHeight());
			b1.setCompoundDrawables(null, drawable1, null, null);
			b1.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b2=	 ((RadioButton) findViewById(R.id.radio_button1));
			Drawable drawable2 = getResources().getDrawable(R.drawable.icon_2_n);
			drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),drawable2.getMinimumHeight());
			b2.setCompoundDrawables(null, drawable2, null, null);
			b2.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b3=	 ((RadioButton) findViewById(R.id.radio_button3));
			Drawable drawable3 = getResources().getDrawable(R.drawable.icon_4_n);
			drawable3.setBounds(0, 0, drawable3.getMinimumWidth(),drawable3.getMinimumHeight());
			b3.setCompoundDrawables(null, drawable3, null, null);
			b3.setTextColor(getResources().getColorStateList(R.color.black));
		}
		else if(type==4)
		{
			RadioButton b=	 ((RadioButton) findViewById(R.id.radio_button3));
			Drawable drawable = getResources().getDrawable(R.drawable.icon_4_y);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
			b.setCompoundDrawables(null, drawable, null, null);
			b.setTextColor(getResources().getColorStateList(R.color.darkcyan));

			RadioButton b1=	 ((RadioButton) findViewById(R.id.radio_button0));
			Drawable drawable1 = getResources().getDrawable(R.drawable.icon_1_n);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),drawable1.getMinimumHeight());
			b1.setCompoundDrawables(null, drawable1, null, null);
			b1.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b2=	 ((RadioButton) findViewById(R.id.radio_button1));
			Drawable drawable2 = getResources().getDrawable(R.drawable.icon_2_n);
			drawable2.setBounds(0, 0, drawable2.getMinimumWidth(),drawable2.getMinimumHeight());
			b2.setCompoundDrawables(null, drawable2, null, null);
			b2.setTextColor(getResources().getColorStateList(R.color.black));

			RadioButton b3=	 ((RadioButton) findViewById(R.id.radio_button2));
			Drawable drawable3 = getResources().getDrawable(R.drawable.icon_3_n);
			drawable3.setBounds(0, 0, drawable3.getMinimumWidth(),drawable3.getMinimumHeight());
			b3.setCompoundDrawables(null, drawable3, null, null);
			b3.setTextColor(getResources().getColorStateList(R.color.black));
		}

	}










	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			switch (buttonView.getId()) {
				case R.id.radio_button0:
					SwitchButton(1);
					this.mTabHost.setCurrentTabByTag("A_TAB");
					setTitle("地图展示");
					break;
				case R.id.radio_button1:

					SwitchButton(2);

					this.mTabHost.setCurrentTabByTag("B_TAB");
					//getActionBar()
					setTitle("数据采集");
					break;
				case R.id.radio_button2:

//				RadioButton b=	 ((RadioButton) findViewById(R.id.radio_button1));
//				// 使用代码设置drawableTop
//				Drawable drawable = getResources().getDrawable(R.drawable.icon_3_n);
//				// 这一步必须要做,否则不会显示.
//				drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
//				b.setCompoundDrawables(null, drawable, null, null);
					SwitchButton(3);


					this.mTabHost.setCurrentTabByTag("C_TAB");
					setTitle("数据上报");
					break;
				case R.id.radio_button3:
					SwitchButton(4);
					this.mTabHost.setCurrentTabByTag("D_TAB");
					setTitle("我的设置");
					break;
//			case R.id.radio_button4:
//				this.mTabHost.setCurrentTabByTag("MORE_TAB");
//				break;
			}
		}

	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_home,
				R.drawable.icon_1_n, this.mAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_news,
				R.drawable.icon_2_n, this.mBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB",
				R.string.main_manage_date, R.drawable.icon_3_n,
				this.mCIntent));

		localTabHost.addTab(buildTabSpec("D_TAB", R.string.main_friends,
				R.drawable.icon_4_n, this.mDIntent));

//		localTabHost.addTab(buildTabSpec("MORE_TAB", R.string.more,
//				R.drawable.icon_5_n, this.mEIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
										 final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
}