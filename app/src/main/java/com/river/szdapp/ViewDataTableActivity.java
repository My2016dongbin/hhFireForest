package com.river.szdapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.river.util.DataBaseHelper;
import com.river.util.HttpHelper;
import com.river.util.HttpThread;
//import com.river.util.MyAdapter;
import com.river.util.ToastUtil3;


import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.renderscript.Type;
import android.view.Menu;
import android.view.MenuItem;



public class ViewDataTableActivity extends Activity {

	private static final String NAMESPACE ="http://river.org/";
	private static String URL ="http://139.129.19.220:7030/api/SZDGetLogs/";

	DataBaseHelper helper;
	private TextView textView;
	private ListView listView;
	private GridView gridView;
	private Button button;
	private Button button1;
	private Button Next;
	private Button Pre;
	private Button btnSubmit;
	private Button btnBack;
	private Button btnScan;
	private Button btnViewDetail;
	private Button btnDH;
	private EditText ett;
	private EditText ett1;
	private HorizontalScrollView layout;
	private ArrayList<String> list1;
	private ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
	private int PageIndex=1;
	private String mode="local";
	private int WebCount=0;

	MyAdapter adapter;


	private Calendar calendar;// 用来装日期的
	private DatePickerDialog dialog;

	private HashMap<String, String> htJd=new HashMap<String, String>();
	private HashMap<String, String> htWd=new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_view_data_table);
		listView = (ListView) findViewById(R.id.listview);
		button = (Button) findViewById(R.id.button);
		button1 = (Button) findViewById(R.id.button1);
		layout = (HorizontalScrollView) findViewById(R.id.layout);
		Next=(Button) findViewById(R.id.next);
		Pre=(Button) findViewById(R.id.pre);
		btnSubmit=(Button) findViewById(R.id.Submit);
		btnBack=(Button) findViewById(R.id.btnBack);
		btnScan=(Button) findViewById(R.id.btnScan);

		btnViewDetail=(Button) findViewById(R.id.btnViewDetail);
		btnDH=(Button)findViewById(R.id.btnDH);







//	        ArrayList<String> list = new ArrayList<String>();
//
//	        list.add("日期");
//	        list.add("地区");
//	        list.add("产品");
//	        list.add("操作人");
//
//	        lists.add(list);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GetDataFromLocal();


			}
		});


		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GetDataFromWeb();


			}
		});

		Next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//	LocalNext();
				Next();


			}
		});

		Pre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//LocalPre();
				Pre();


			}
		});

		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//LocalPre();
				Submit();


			}
		});

		btnViewDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//LocalPre();
				GotoDetail();


			}
		});

		btnDH.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//LocalPre();
				DH();


			}
		});








		ett=(EditText)findViewById(R.id.qdcodeet1);
		ett.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//	LocalNext();
				calendar = Calendar.getInstance();
				dialog = new DatePickerDialog(ViewDataTableActivity.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
												  int monthOfYear, int dayOfMonth) {
								String month="";
								if(monthOfYear+1<10)
									month="0"+String.valueOf(monthOfYear+1);
								else
									month=String.valueOf(monthOfYear+1);
								String day="";
								if(dayOfMonth<10)
									day="0"+String.valueOf(dayOfMonth);
								else
									day=String.valueOf(dayOfMonth);



								ett.setText(year + "-" + month + "-"
										+ day);
							}
						}, calendar.get(Calendar.YEAR), calendar
						.get(Calendar.MONTH), calendar
						.get(Calendar.DAY_OF_MONTH));
				dialog.show();


			}
		});



		ett1=(EditText)findViewById(R.id.qdcodeet2);
		ett1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//	LocalNext();
				calendar = Calendar.getInstance();
				dialog = new DatePickerDialog(ViewDataTableActivity.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
												  int monthOfYear, int dayOfMonth) {

								String month="";
								if(monthOfYear+1<10)
									month="0"+String.valueOf(monthOfYear+1);
								else
									month=String.valueOf(monthOfYear+1);
								String day="";
								if(dayOfMonth<10)
									day="0"+String.valueOf(day);
								else
									day=String.valueOf(dayOfMonth);



								ett1.setText(year + "-" + month + "-"
										+ day);
							}
						}, calendar.get(Calendar.YEAR), calendar
						.get(Calendar.MONTH), calendar
						.get(Calendar.DAY_OF_MONTH));
				dialog.show();


			}
		});



		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//LocalPre();
				Intent intent = new Intent(ViewDataTableActivity.this, MainTabActivity.class);
				startActivity(intent);





			}
		});


		btnScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub


				Intent intent = new Intent(ViewDataTableActivity.this, LSCaptureActivity.class);
				startActivity(intent);

			}
		});






		Intent intent=getIntent();
		String msg=intent.getStringExtra("msg");



		//20200920

		String qdcode=intent.getStringExtra("qdcode");
		if(qdcode!=null&& qdcode.equals("")==false)
		{
			((EditText)findViewById(R.id.qdcodeet)).setText(qdcode);
		}

		//over


		String view=intent.getStringExtra("view");
		if(view!=null&&view.equals("local"))
		{
			GetDataFromLocal();
		}
		else
		{
			GetDataFromWeb();
		}
		TextView tv=(TextView)findViewById(R.id.msg);
		tv.setText(msg);
	}


	public void Pre()
	{
		if(this.mode.equals("local"))
		{
			LocalPre();
		}
		else
		{
			WebPre();
		}
	}

	public void Next()
	{
		if(this.mode.equals("local"))
		{
			LocalNext();
		}
		else
		{
			WebNext();
		}
	}


	//从本机数据库读取本机数据
	public void GetDataFromLocal()
	{
		this.mode="local";
		this.btnSubmit.setVisibility(View.VISIBLE);
		this.btnViewDetail.setVisibility(View.INVISIBLE);
		this.btnDH.setVisibility(View.INVISIBLE);
		File file = getFilesDir();
		String path = file.getAbsolutePath()+"/river3.db";
		helper = new DataBaseHelper(this, path, 1);
		final SQLiteDatabase db = helper.getReadableDatabase();
//	        db.execSQL("insert into szdlog values(null,?,?,?,?)",
//	        		new String[]{"1","2","3","4"} );
//	        db.execSQL("insert into trade values(null,?,?,?,?,?)",
//	        		new String[]{"123456","100","622712546985642","2009-12-21 20:21:12","成功"} );
//	        db.execSQL("insert into trade values(null,?,?,?,?,?)",
//	        		new String[]{"123456","100","622712546985642","2009-12-21 20:21:12","成功"} );
		int i = 0;




		String QDCode =((EditText)findViewById(R.id.qdcodeet)).getText().toString();
		//	   String where="where operator='"+UserModel.UserID+"' ";
		String where="where operator='"+UserModel.UserID+"' and state='未上报'"; //只查看未提交的 20201224
//		   if(QDCode.equals("")==false)
//			   where+=" and code like'%"+QDCode+"%'"; //20201130精确查询
		if(QDCode.equals("")==false)
			where+=" and code ='"+QDCode+"'";

		String StartTime=ett.getText().toString();
		if(StartTime.equals("")==false)
			where+=" and time >='"+StartTime+"'";

		String EndTime=ett1.getText().toString();
		if(EndTime.equals("")==false)
			where+=" and time <='"+EndTime+"'";




		// Cursor cursor = db.rawQuery("select _id,time,region,product,operator,state from szdlog where operator='"+UserModel.UserID+"' order by _id desc limit 0,5 ", null);
		Cursor cursor = db.rawQuery("select _id,time,region,product,operator,state,code from szdlog "+where+"  order by _id desc limit 0,5 ", null);
		lists.clear();
		ArrayList<String> list = new ArrayList<String>();

		list.add("日期");
		list.add("地区");
		list.add("产品");
		list.add("操作人");
		list.add("状态");
		list.add("编号");

		lists.add(list);
		while (cursor.moveToNext()) {
			//"create table trade(_id integer primary key autoincrement,serialNo,money,cardNum,date,status)";
			list1 = new ArrayList<String>();
			list1.add(cursor.getString(cursor.getColumnIndex("_id")));
			list1.add(cursor.getString(cursor.getColumnIndex("time")));
			list1.add(cursor.getString(cursor.getColumnIndex("region")));
			list1.add(cursor.getString(cursor.getColumnIndex("product")));
			list1.add(cursor.getString(cursor.getColumnIndex("operator")));
			list1.add(cursor.getString(cursor.getColumnIndex("state")));
			list1.add(cursor.getString(cursor.getColumnIndex("code")));

			lists.add(list1);
		}
		cursor.close();
		adapter = new MyAdapter(ViewDataTableActivity.this, lists);

		listView.setAdapter(adapter);
		layout.setVisibility(View.VISIBLE);

		TextView tv=(TextView)findViewById(R.id.msg);
		tv.setText("当前显示本机数据");

		TextView tvPages=(TextView)findViewById(R.id.pages);
		//tvPages.setText("当前第1页");

		//   int Count=allCaseNum(db,"szdlog","");
		int Count=allCaseNum(db,"szdlog",where); //只查看未提交的 20201224
		int page=0;
		if(Count%5==0)
		{
			page=Count/5;
		}
		else
		{
			page=Count/5+1;
		}

		tvPages.setText("共"+String.valueOf(page)+"页,当前第1页");
	}


	//下一页
	public void LocalNext()
	{


		File file = getFilesDir();
		String path = file.getAbsolutePath()+"/river3.db";
		helper = new DataBaseHelper(this, path, 1);
		final SQLiteDatabase db = helper.getReadableDatabase();
		//  int Count=allCaseNum(db,"szdlog","where state='未上报'");



		String QDCode =((EditText)findViewById(R.id.qdcodeet)).getText().toString();
		//   String where="where operator='"+UserModel.UserID+"' ";
		String where="where operator='"+UserModel.UserID+"' and state='未上报'"; //只查看未提交的 20201224
//			   if(QDCode.equals("")==false)
//				   where+=" and code like'%"+QDCode+"%'";//20201130精确查询
		if(QDCode.equals("")==false)
			where+=" and code ='"+QDCode+"'";

		String StartTime=ett.getText().toString();
		if(StartTime.equals("")==false)
			where+=" and time >='"+StartTime+"'";

		String EndTime=ett1.getText().toString();
		if(EndTime.equals("")==false)
			where+=" and time <='"+EndTime+"'";


		int Count=allCaseNum(db,"szdlog",where);
		// int Count=allCaseNum(db,"szdlog",where); //只查看未提交的 20201224


		int page=0;
		if(Count%5==0)
		{
			page=Count/5;
		}
		else
		{
			page=Count/5+1;
		}
		if(PageIndex+1>page)
		{
			ToastUtil3.showToast(ViewDataTableActivity.this, "已结是最后一页");
		}
		else
		{
			Integer A=new Integer(PageIndex*5);


//	    	   String QDCode =((EditText)findViewById(R.id.qdcodeet)).getText().toString();
//			//   String where="where operator='"+UserModel.UserID+"' ";
//	    	   String where="where operator='"+UserModel.UserID+"' and state='未上报'"; //只查看未提交的 20201224
////			   if(QDCode.equals("")==false)
////				   where+=" and code like'%"+QDCode+"%'";//20201130精确查询
//			   if(QDCode.equals("")==false)
//				   where+=" and code ='"+QDCode+"'";
//
//			   String StartTime=ett.getText().toString();
//			   if(StartTime.equals("")==false)
//				   where+=" and time >='"+StartTime+"'";
//
//			   String EndTime=ett1.getText().toString();
//			   if(EndTime.equals("")==false)
//				   where+=" and time <='"+EndTime+"'";


			Cursor cursor = db.rawQuery("select  _id,time,region,product,operator,state,code from szdlog "+where+" order by _id desc limit "+A.toString()+",5 ", null);
			lists.clear();

			ArrayList<String> list = new ArrayList<String>();

			list.add("日期");
			list.add("地区");
			list.add("产品");
			list.add("操作人");
			list.add("状态");
			list.add("编号");
			lists.add(list);

			while (cursor.moveToNext()) {
				//"create table trade(_id integer primary key autoincrement,serialNo,money,cardNum,date,status)";
				list1 = new ArrayList<String>();
				list1.add(cursor.getString(cursor.getColumnIndex("_id")));
				list1.add(cursor.getString(cursor.getColumnIndex("time")));
				list1.add(cursor.getString(cursor.getColumnIndex("region")));
				list1.add(cursor.getString(cursor.getColumnIndex("product")));
				list1.add(cursor.getString(cursor.getColumnIndex("operator")));
				list1.add(cursor.getString(cursor.getColumnIndex("state")));
				list1.add(cursor.getString(cursor.getColumnIndex("code")));
				lists.add(list1);
			}
			PageIndex++;
			cursor.close();
			MyAdapter adapter = new MyAdapter(ViewDataTableActivity.this, lists);
			listView.setAdapter(adapter);
			layout.setVisibility(View.VISIBLE);
		}
		TextView tvPages=(TextView)findViewById(R.id.pages);
		tvPages.setText("共"+String.valueOf(page)+"页,当前第"+String.valueOf(PageIndex)+"页");




//
//		   Map<String, String> params0 = new HashMap<String, String>();
//	       params0.put("GpsImgJson", jsonString);
//	       params0.put("ExtendJson", stempString);
	}


	public void GotoDetail()
	{
		ArrayList<Object> aaArrayList= adapter.ids;
//			  String temp="";
//
//
		if(aaArrayList==null||aaArrayList.size()==0)
		{
			ToastUtil3.showToast(ViewDataTableActivity.this, "请选择要查看详情的记录");
		}
		else {
			String id= aaArrayList.get(0).toString();

			Intent intent1 = new Intent(ViewDataTableActivity.this, ViewCJDetailActivity.class);
			Bundle bundle1=new Bundle();
			bundle1.putString("CJLogID", id);
			intent1.putExtras(bundle1);
			startActivity(intent1);



		}





	}



	public void DH()
	{
		ArrayList<Object> aaArrayList= adapter.ids;
//			  String temp="";
//
//
		if(aaArrayList==null||aaArrayList.size()==0)
		{
			ToastUtil3.showToast(ViewDataTableActivity.this, "请选择要导航的记录");
		}
		else {
			String id= aaArrayList.get(0).toString();

//			 Intent intent1 = new Intent(ViewDataTableActivity.this, ViewCJDetailActivity.class);
//				Bundle bundle1=new Bundle();
//				bundle1.putString("CJLogID", id);
//				intent1.putExtras(bundle1);
//				startActivity(intent1);


			//add by river 20210130 如果要恢复，就把上面的注释去掉
			String jd=this.htJd.get(id);
			String wd=this.htWd.get(id);
			//  Uri uri = Uri.parse("baidumap://map/direction?destination=latlng:"+"36.08734723760949"+","+ "120.42430614189682"+"|name:"+"目的地名称"+"&mode=driving");
			Uri uri = Uri.parse("baidumap://map/direction?destination=latlng:"+wd+","+ jd+"|name:"+"采集点"+"&mode=driving");
			this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
		}





	}















	public void Submit()
	{

		btnSubmit.setText("批量提交中，请耐心等待");


//		   File file = getFilesDir();
//		   String path = file.getAbsolutePath()+"/river3.db";
//		   helper = new DataBaseHelper(this, path, 1);
//	       final SQLiteDatabase db = helper.getReadableDatabase();
//		   ArrayList<Object> aaArrayList= adapter.ids;
//			  String temp="";
//
//
//
//
//			   for (int i = 0; i < aaArrayList.size(); i++) {
//		            //System.out.println(adapter.ids.get(i));
//		            //ToastUtil3.showToast(ViewDataTableActivity.this, aaArrayList.get(i).toString());
//				   Cursor cursor = db.rawQuery("select  * from szdlog where _id ="+aaArrayList.get(i).toString(), null);
//				   cursor.moveToNext();
//
//				   String State=cursor.getString(cursor.getColumnIndex("state"));
//				   if(State.equals("已上报"))
//					   continue;
//
//				   Map<String, String> params0 = new HashMap<String, String>();
//			       params0.put("GpsImgJson", cursor.getString(cursor.getColumnIndex("gps")));
//			       params0.put("ExtendJson", cursor.getString(cursor.getColumnIndex("extend")));
//
//
//
//
//
//				   Upload(cursor.getString(cursor.getColumnIndex("rivername")), params0, cursor.getString(cursor.getColumnIndex("projectid")),
//						   cursor.getString(cursor.getColumnIndex("productid")), cursor.getString(cursor.getColumnIndex("code")),aaArrayList.get(i).toString());
//				   temp+=aaArrayList.get(i).toString();
//				   cursor.close();
//				   try {
//					Thread.sleep(1000*5);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//		        }
		// ToastUtil3.showToast(ViewDataTableActivity.this, "提交完成，请留意最新状态");
//			   try {
//				Thread.sleep(1000*5);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		//  GetDataFromLocal();
		//  btnSubmit.setText("本机数据提交");








		HttpThread thread = new HttpThread(handler);
		thread.doStart("http://139.129.19.220:7030/api/SZDLog/","2",this);

	}


	public void Upload(String rivername,Map<String, String> params0,String ProjectID,String ProductID,String Code,String ID)
	{
		HttpThread thread = new HttpThread(handler);
		thread.doStart("http://139.129.19.220:7030/api/SZDLog/", null,"2",rivername,params0,ProjectID+"_"+ProductID+"_"+Code,ID);
	}


	//下一页
	public void LocalPre()
	{
		File file = getFilesDir();
		String path = file.getAbsolutePath()+"/river3.db";
		helper = new DataBaseHelper(this, path, 1);
		final SQLiteDatabase db = helper.getReadableDatabase();






		String QDCode =((EditText)findViewById(R.id.qdcodeet)).getText().toString();
		//   String where="where operator='"+UserModel.UserID+"' ";
		String where="where operator='"+UserModel.UserID+"' and state='未上报'"; //只查看未提交的 20201224
//			   if(QDCode.equals("")==false)
//			   		where+=" and code like'%"+QDCode+"%'";//20201130精确查询
		if(QDCode.equals("")==false)
			where+=" and code ='"+QDCode+"'";

		String StartTime=ett.getText().toString();
		if(StartTime.equals("")==false)
			where+=" and time >='"+StartTime+"'";

		String EndTime=ett1.getText().toString();
		if(EndTime.equals("")==false)
			where+=" and time <='"+EndTime+"'";
		int Count=allCaseNum(db,"szdlog",where);
		int page=0;
		if(Count%5==0)
		{
			page=Count/5;
		}
		else
		{
			page=Count/5+1;
		}
		if(PageIndex==1)
		{
			ToastUtil3.showToast(ViewDataTableActivity.this, "已经是第一页");
		}
		else
		{
			PageIndex--;
			Integer A=new Integer((PageIndex-1)*5);

//	    	   String QDCode =((EditText)findViewById(R.id.qdcodeet)).getText().toString();
//			//   String where="where operator='"+UserModel.UserID+"' ";
//	    	   String where="where operator='"+UserModel.UserID+"' and state='未上报'"; //只查看未提交的 20201224
////			   if(QDCode.equals("")==false)
////			   		where+=" and code like'%"+QDCode+"%'";//20201130精确查询
//			   if(QDCode.equals("")==false)
//				   where+=" and code ='"+QDCode+"'";
//
//			   String StartTime=ett.getText().toString();
//			   if(StartTime.equals("")==false)
//				   where+=" and time >='"+StartTime+"'";
//
//			   String EndTime=ett1.getText().toString();
//			   if(EndTime.equals("")==false)
//				   where+=" and time <='"+EndTime+"'";


			Cursor cursor = db.rawQuery("select  _id,time,region,product,operator,state,code from szdlog "+where+" order by _id desc limit "+A.toString()+",5 ", null);
			lists.clear();

			ArrayList<String> list = new ArrayList<String>();



			list.add("日期");
			list.add("地区");
			list.add("产品");
			list.add("操作人");
			list.add("状态");
			list.add("编号");
			lists.add(list);

			while (cursor.moveToNext()) {
				//"create table trade(_id integer primary key autoincrement,serialNo,money,cardNum,date,status)";
				list1 = new ArrayList<String>();
				list1.add(cursor.getString(cursor.getColumnIndex("_id")));
				list1.add(cursor.getString(cursor.getColumnIndex("time")));
				list1.add(cursor.getString(cursor.getColumnIndex("region")));
				list1.add(cursor.getString(cursor.getColumnIndex("product")));
				list1.add(cursor.getString(cursor.getColumnIndex("operator")));
				list1.add(cursor.getString(cursor.getColumnIndex("state")));
				list1.add(cursor.getString(cursor.getColumnIndex("code")));
				lists.add(list1);
			}

			cursor.close();
			MyAdapter adapter = new MyAdapter(ViewDataTableActivity.this, lists);
			listView.setAdapter(adapter);
			layout.setVisibility(View.VISIBLE);
		}
		TextView tvPages=(TextView)findViewById(R.id.pages);
		tvPages.setText("共"+String.valueOf(page)+"页,当前第"+String.valueOf(PageIndex)+"页");


	}


	public int allCaseNum(SQLiteDatabase db,String TableName,String Where ){
		String sql = "select count(*) from "+TableName+" "+Where;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}




	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {

			String st= msg.getData().getString("result");
			String type=msg.getData().getString("type");
			try
			{
				if(type.equals("1")==true)
				{
					st = StringEscapeUtils.unescapeJava(st);
					st=st.substring(0,st.length()-1);
					st=st.substring(1,st.length());
					JSONObject jsonObject = JSON.parseObject(st);

					int Code= jsonObject.getInteger("code");
					String datas=jsonObject.getString("data");

					WebCount=jsonObject.getInteger("total");



					if(Code==0)
					{
						lists.clear();
						htJd.clear();
						htWd.clear();

						ArrayList<String> list = new ArrayList<String>();

						list.add("日期");
						list.add("地区");
						list.add("产品");
						list.add("操作人");
						list.add("状态");
						list.add("编号");
						lists.add(list);

						JSONArray jarr=JSONArray.parseArray(datas);//JSON.parseArray(jsonStr);
						for (Iterator iterator = jarr.iterator(); iterator.hasNext();) {
							JSONObject job=(JSONObject)iterator.next();
							//String textName=job.get("Time").toString();
							list1 = new ArrayList<String>();
							list1.add(job.get("ID").toString());
							list1.add(job.get("Time").toString());
							list1.add(job.get("Region").toString());
							list1.add(job.get("ProductName").toString());
							list1.add(job.get("UserID").toString());

							list1.add("已上报");
							list1.add(job.get("QDCode").toString());



							//add by river20210130
							if(job.containsKey("Jd")==true)
							{
								htJd.put(job.get("ID").toString(), job.get("Jd").toString());
								htWd.put(job.get("ID").toString(), job.get("Wd").toString());
							}
							// over


							lists.add(list1);
						}
					}
					TextView tv=(TextView)findViewById(R.id.msg);
					tv.setText("当前显示历史数据");




					int page=0;
					if(WebCount%5==0)
					{
						page=WebCount/5;
					}
					else
					{
						page=WebCount/5+1;
					}

					TextView tvPages=(TextView)findViewById(R.id.pages);
					tvPages.setText("共"+String.valueOf(page)+"页,当前第"+String.valueOf(PageIndex)+"页");
				}

				else   //更新状态
				{
					GetDataFromLocal();
				}



			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//   MyAdapter adapter = new MyAdapter(ViewDataTableActivity.this, lists);
			adapter = new MyAdapter(ViewDataTableActivity.this, lists);
			listView.setAdapter(adapter);
			layout.setVisibility(View.VISIBLE);


		}
	};




	public  void WebPre()
	{
		if(PageIndex==1)
		{
			ToastUtil3.showToast(ViewDataTableActivity.this, "已结是第一页");
		}
		else
		{
			PageIndex--;
			GetDataFromWeb();

		}
	}

	public  void WebNext()
	{

		int page=0;
		if(WebCount%5==0)
		{
			page=WebCount/5;
		}
		else
		{
			page=WebCount/5+1;
		}
		if(PageIndex>=page)
		{
			ToastUtil3.showToast(ViewDataTableActivity.this, "已结是最后一页");
		}
		else
		{
			PageIndex++;
			GetDataFromWeb();

		}
	}


	//从服务器端获取数据
	public void GetDataFromWeb()
	{

		this.mode="web";
		this.btnSubmit.setVisibility(View.INVISIBLE);
		this.btnViewDetail.setVisibility(View.VISIBLE);
		this.btnDH.setVisibility(View.VISIBLE);
		HttpThread thread = new HttpThread(handler);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("UserID", "张三"));
		nameValuePairs.add(new BasicNameValuePair("UserID", UserModel.UserID));

		Integer A=new Integer(this.PageIndex);
		nameValuePairs.add(new BasicNameValuePair("PageIndex", A.toString()));

		String QDCode =((EditText)findViewById(R.id.qdcodeet)).getText().toString();
		nameValuePairs.add(new BasicNameValuePair("QDCode", QDCode));



		String StartTime=ett.getText().toString();
		nameValuePairs.add(new BasicNameValuePair("StartTime", StartTime));
		String EndTime=ett1.getText().toString();
		nameValuePairs.add(new BasicNameValuePair("EndTime", EndTime));


		thread.doStart(URL,  nameValuePairs,"1");
	}

	private class HttpThread extends Thread{
		Handler handler = null;
		String url = null;

		private String Type;  //1是获取数据，2是补上传
		private String ID;


		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		ProgressDialog progressDialog = null;





		String fileName="";//文件名称
		String lsm="";

		Map<String, String> params0= new HashMap<String, String>();;

		Context context;










		//构造函数
		public HttpThread(Handler handler){
			this.handler = handler;
		}

		/**
		 * 启动线程
		 */
		public void doStart(String url,
							List<NameValuePair> nameValuePairs,String type){
			this.url=url;

			this.nameValuePairs = nameValuePairs;

			this.Type=type;

			progressDialog = ProgressDialog.show(ViewDataTableActivity.this,
					"提示","正在请求请稍等...", true);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(true);
			this.start();
		}

		/**
		 * 启动线程
		 */
		public void doStart(String url,
							List<NameValuePair> nameValuePairs,String type,String fileName,Map<String, String> params0,String lsm,String ID){
			this.url=url;
			//this.type=type;
			this.nameValuePairs = nameValuePairs;
			this.fileName=fileName;
			this.params0=params0;
			this.lsm=lsm;
			this.Type=type;
			this.ID=ID;
			//this.cjType=cjType;
			progressDialog = ProgressDialog.show(ViewDataTableActivity.this,
					"提示","正在请求请稍等...", true);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			this.start();
		}

		/**
		 * 启动线程
		 */
		public void doStart(String url,String type,
							Context context){
			this.url=url;

			this.context=context;

			this.Type=type;

			progressDialog = ProgressDialog.show(ViewDataTableActivity.this,
					"提示","正在请求请稍等...", true);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(true);
			this.start();
		}


		/**
		 * 线程运行
		 */
		@Override
		public void run() {
			System.out.println("jack");
			super.run();
			try{
				//web service 请求
				String st;
				if(Type.equals("1"))
				{
					st= HttpHelper.invoke1(url,"GetCJLogs", nameValuePairs);
					//取消进度框
					progressDialog.dismiss();
					//构造消息
					Message message = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("result", st);
					bundle.putString("type", "1");
					message.setData(bundle);
					handler.sendMessage(message);
				}

				else
				{




					File file = getFilesDir();
					String path = file.getAbsolutePath()+"/river3.db";
					DataBaseHelper helper = new DataBaseHelper(context, path, 1);
					final SQLiteDatabase db = helper.getReadableDatabase();
					ArrayList<Object> aaArrayList= adapter.ids;
					String temp="";




					for (int i = 0; i < aaArrayList.size(); i++) {
						//System.out.println(adapter.ids.get(i));
						//ToastUtil3.showToast(ViewDataTableActivity.this, aaArrayList.get(i).toString());
						Cursor cursor = db.rawQuery("select  * from szdlog where _id ="+aaArrayList.get(i).toString(), null);
						cursor.moveToNext();

						String State=cursor.getString(cursor.getColumnIndex("state"));
						if(State.equals("已上报"))
							continue;

						Map<String, String> params0 = new HashMap<String, String>();
						params0.put("GpsImgJson", cursor.getString(cursor.getColumnIndex("gps")));
						params0.put("ExtendJson", cursor.getString(cursor.getColumnIndex("extend")));





//	  						   Upload(cursor.getString(cursor.getColumnIndex("rivername")), params0, cursor.getString(cursor.getColumnIndex("projectid")),
//	  								   cursor.getString(cursor.getColumnIndex("productid")), cursor.getString(cursor.getColumnIndex("code")),aaArrayList.get(i).toString());


						st= HttpHelper.invokePost1(url,"Add_Img",cursor.getString(cursor.getColumnIndex("rivername")), params0,
								cursor.getString(cursor.getColumnIndex("projectid"))+
										cursor.getString(cursor.getColumnIndex("productid"))+cursor.getString(cursor.getColumnIndex("code"))


						);


						st = StringEscapeUtils.unescapeJava(st);
						st=st.substring(0,st.length()-1);
						st=st.substring(1,st.length());
						JSONObject jsonObject = JSON.parseObject(st);












						// temp+=aaArrayList.get(i).toString();
						cursor.close();


						int Code= jsonObject.getInteger("code");
						if(Code==0)
						{

							String sql="update szdlog set state='已上报' where _id="+aaArrayList.get(i).toString();
							db.execSQL(sql);
						}
						else
						{
							String sql="update szdlog set state='上报失败' where _id="+aaArrayList.get(i).toString();
							db.execSQL(sql);
						}


					}





					//st= HttpHelper.invokePost1(url,"Add_Img", fileName, params0,lsm);
					//取消进度框
					progressDialog.dismiss();




					//构造消息
					Message message = handler.obtainMessage();
					Bundle bundle = new Bundle();

					bundle.putString("type", "2");

					message.setData(bundle);
					handler.sendMessage(message);
//	  					







				}

			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}


}
