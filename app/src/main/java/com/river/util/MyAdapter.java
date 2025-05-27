package com.river.util;



import java.util.ArrayList;
import java.util.List;

import com.river.szdapp.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<ArrayList<String>> lists;

	public ArrayList<Object> ids=new ArrayList<Object>();


	public MyAdapter(Context context, ArrayList<ArrayList<String>> lists) {
		super();
		this.context = context;
		this.lists = lists;
		inflater = LayoutInflater.from(context);


	}

	public ArrayList<Object> getids()
	{
		return ids;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}



	@Override
	public View getView(int index, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ArrayList<String> list = lists.get(index);



		if(view == null){
			view = inflater.inflate(R.layout.list_item, null);

		}
		view.setBackgroundColor(Color.WHITE);
		TextView textView1 = (TextView) view.findViewById(R.id.text1);
		TextView textView2 = (TextView) view.findViewById(R.id.text2);
		TextView textView3 = (TextView) view.findViewById(R.id.text3);
		TextView textView4 = (TextView) view.findViewById(R.id.text4);
//		TextView textView5 = (TextView) view.findViewById(R.id.text5);
//		TextView textView6 = (TextView) view.findViewById(R.id.text6);
		textView1.setTextColor(Color.BLACK);
		textView2.setTextColor(Color.BLACK);
		textView3.setTextColor(Color.BLACK);
		textView4.setTextColor(Color.BLACK);
//		textView5.setTextColor(Color.BLACK);
//		textView6.setTextColor(Color.BLACK);
		if(index==0)
		{
			textView1.setText(list.get(0));
			textView2.setText(list.get(1));
			textView3.setText(list.get(2));
			textView4.setText(list.get(3));
		}
		else
		{
			textView1.setText(list.get(1));
			textView2.setText(list.get(2));
			textView3.setText(list.get(3));
			textView4.setText(list.get(4));
		}




		//单选按钮
		final CheckBox checkBox=(CheckBox)view.findViewById(R.id.rb_check_button);

		if(index!=0)
		{
			checkBox.setTag(list.get(0));
		}
		checkBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkBox.isChecked()){


					ids.add(checkBox.getTag());

				}else {
					ids.remove(checkBox.getTag());

				}
			}
		});

























//		textView5.setText(list.get(4));
//		textView6.setText(list.get(5));
		if(index == 0){
			view.setBackgroundResource(R.color.head_bg);
			textView1.setTextColor(Color.WHITE);
			textView2.setTextColor(Color.WHITE);
			textView3.setTextColor(Color.WHITE);
			textView4.setTextColor(Color.WHITE);
//			textView5.setTextColor(Color.WHITE);
//			textView6.setTextColor(Color.WHITE);
		}else{
			if(index%2 != 0){
				view.setBackgroundColor(Color.argb(250 ,  255 ,  255 ,  255 ));
			}else{
				view.setBackgroundColor(Color.argb(250 ,  224 ,  243 ,  250 ));
			}
		}

		return view;
	}

}
