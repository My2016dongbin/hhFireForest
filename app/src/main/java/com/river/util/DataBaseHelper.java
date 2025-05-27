package com.river.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public DataBaseHelper(Context context, String name, int version) {
		super(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
	//String sql = "create table szdlog(_id integer primary key autoincrement,time,region,product,operator)";
	String sql = "create table szdlog(_id integer primary key autoincrement,time,region,product,operator,rivername,gps,extend,projectid,productid,code,state)";
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		arg0.execSQL(sql);
		

	}

}
