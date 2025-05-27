package com.river.gridview;

import com.river.szdapp.CaptureActivity;
import com.river.szdapp.LLoginActivity;
import com.river.szdapp.MainTabActivity;
import com.river.szdapp.R;
import com.river.util.ToastUtil3;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Description:gridview鐨凙dapter
 * @author http://blog.csdn.net/finddreams
 */
public class MyGridAdapter extends BaseAdapter {
	private Context mContext;
	private Toast toast;

	public String[] img_text = { "数据采集", "枯死木普查", "枯死木除治", "效果追踪",};
	public int[] imgs = { R.drawable.app_aapay, R.drawable.filechooser,
			R.drawable.app_phonecharge, R.drawable.fullscreen };

	public MyGridAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void showMsg(String arg) {
		if (toast == null) {
			toast = Toast.makeText(mContext, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_item, parent, false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		iv.setTag(position);

		iv.setBackgroundResource(imgs[position]);



		iv.setOnClickListener(new OnClickListener(){
								  public void onClick(View v)
								  {

									  showMsg(v.getTag().toString());
									  Intent intent = new Intent(mContext, CaptureActivity.class);
									  Bundle bundle=new Bundle();
									  if(v.getTag().toString().equals("0"))
									  {
										  bundle.putString("cjtype", "数据采集");
									  }
									  else  if(v.getTag().toString().equals("1"))
									  {
//					   ToastUtil3.showToast(mContext, "开发中，敬请期待");
//					   return;
										  bundle.putString("cjtype", "枯死木普查");
									  }
									  else  if(v.getTag().toString().equals("2"))
									  {

//					   ToastUtil3.showToast(mContext, "开发中，敬请期待");
//					   return;


										  bundle.putString("cjtype", "枯死木除治");
									  }
									  else  if(v.getTag().toString().equals("3"))
									  {
										  bundle.putString("cjtype", "效果追踪");
//					   ToastUtil3.showToast(mContext, "开发中，敬请期待");
//					   return;
									  }
//				   ToastUtil3.showToast(mContext, "开发中，敬请期待");
//				   return;		

									  //for test 20200723
									  intent.putExtras(bundle);
									  mContext.startActivity(intent);
									  //20200723

								  }
							  }
		);

		tv.setText(img_text[position]);
		return convertView;
	}

}
