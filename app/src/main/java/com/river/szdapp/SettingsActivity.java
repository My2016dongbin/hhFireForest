package com.river.szdapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends Activity {
	
	private Button btnLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		
		btnLogout=(Button) findViewById(R.id.button);

	        
		btnLogout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
				logOut();
				
			        
				}
			});
	}
	
	
	public void logOut()
	{
		//SharedPreferences preferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);
		SharedPreferences preference = getSharedPreferences("szd",Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = preference.edit();
	    editor.clear();
	    editor.commit();
	    
	    Intent intent = new Intent(SettingsActivity.this, LLoginActivity.class);			
		startActivity(intent);	
	
	}
}
