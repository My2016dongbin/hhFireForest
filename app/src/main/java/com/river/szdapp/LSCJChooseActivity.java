package com.river.szdapp;

import java.util.ArrayList;

import com.river.util.ToastUtil3;



import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LSCJChooseActivity extends Activity {

	// 声明ListView控件
	private ListView mListView;

	// 声明数组链表，其装载的类型是ListItem(封装了一个Drawable和一个String的类)
	private ArrayList<ListItem> mList;

	/**
	 * Acitivity的入口方法
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 指定Activity的布局使用activity_main.xml
		setContentView(R.layout.activity_lscjchoose);


		setTitle("数据采集");



		// 通过findviewByID获取到ListView对象
		mListView = (ListView) findViewById(R.id.listView1);

		// 获取Resources对象
		Resources res = this.getResources();

		mList = new ArrayList<LSCJChooseActivity.ListItem>();

		// 初始化data，装载八组数据到数组链表mList中
		ListItem item = new ListItem();
		item.setImage(res.getDrawable(R.drawable.c1));
		item.setTitle("数据采集 >");
		mList.add(item);

		item = new ListItem();
		item.setImage(res.getDrawable(R.drawable.c2));
		item.setTitle("枯死木普查 >");
		mList.add(item);


		item = new ListItem();
		item.setImage(res.getDrawable(R.drawable.c3));
		item.setTitle("枯死木除治 >");
		mList.add(item);

		item = new ListItem();
		item.setImage(res.getDrawable(R.drawable.c4));
		item.setTitle("效果追踪 >");
		mList.add(item);

		item = new ListItem();
		item.setImage(res.getDrawable(R.drawable.c4));
		item.setTitle("返回主界面");
		mList.add(item);


		// 获取MainListAdapter对象
		MainListViewAdapter adapter = new MainListViewAdapter();

		// 将MainListAdapter对象传递给ListView视图
		mListView.setAdapter(adapter);



		Intent intent=getIntent();
		String msg=intent.getStringExtra("msg");
		if(msg!=null)
		{
			ToastUtil3.showToast(LSCJChooseActivity.this, msg);
		}



//
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
									long id) {



				if(pos==0)
				{
					Intent intent = new Intent(LSCJChooseActivity.this, CaptureActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("cjtype", "数据采集");
					intent.putExtras(bundle);
					startActivity(intent);
				}
				else if(pos==1)
				{
					Intent intent = new Intent(LSCJChooseActivity.this, CaptureActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("cjtype", "枯死木普查");
					intent.putExtras(bundle);
					startActivity(intent);

				}
				else if(pos==2)
				{
					Intent intent = new Intent(LSCJChooseActivity.this, CaptureActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("cjtype", "枯死木除治");
					intent.putExtras(bundle);
					startActivity(intent);

				}
				else if(pos==3)
				{
					Intent intent = new Intent(LSCJChooseActivity.this, CaptureActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("cjtype", "效果追踪");
					intent.putExtras(bundle);
					startActivity(intent);

				}
				else if(pos==4)
				{
					Intent intent1 = new Intent(LSCJChooseActivity.this, MainTabActivity.class);
					startActivity(intent1);
				}






			}
		});



	}



	/**
	 * 定义ListView适配器MainListViewAdapter
	 */
	class MainListViewAdapter extends BaseAdapter {

		/**
		 * 返回item的个数
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		/**
		 * 返回item的内容
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		/**
		 * 返回item的id
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/**
		 * 返回item的视图
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ListItemView listItemView;

			// 初始化item view
			if (convertView == null) {
				// 通过LayoutInflater将xml中定义的视图实例化到一个View中
				convertView = LayoutInflater.from(LSCJChooseActivity.this).inflate(
						R.layout.items, null);

				// 实例化一个封装类ListItemView，并实例化它的两个域
				listItemView = new ListItemView();
				listItemView.imageView = (ImageView) convertView
						.findViewById(R.id.image);
				listItemView.textView = (TextView) convertView
						.findViewById(R.id.title);

				// 将ListItemView对象传递给convertView
				convertView.setTag(listItemView);
			} else {
				// 从converView中获取ListItemView对象
				listItemView = (ListItemView) convertView.getTag();
			}

			// 获取到mList中指定索引位置的资源
			Drawable img = mList.get(position).getImage();
			String title = mList.get(position).getTitle();

			// 将资源传递给ListItemView的两个域对象
			listItemView.imageView.setImageDrawable(img);
			listItemView.textView.setText(title);

			// 返回convertView对象
			return convertView;
		}

	}

	/**
	 * 封装两个视图组件的类
	 */
	class ListItemView {
		ImageView imageView;
		TextView textView;
	}

	/**
	 * 封装了两个资源的类
	 */
	class ListItem {
		private Drawable image;
		private String title;

		public Drawable getImage() {
			return image;
		}

		public void setImage(Drawable image) {
			this.image = image;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}
}
