package com.river.szdapp;




import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.river.util.GetLocation;


public class BaiduTestActivity extends Activity implements OnClickListener {
	//public class BaiduTestActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	private TextView tv_address;
	private LocationClient mLocationClient;
	private MyLocationListenner myListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baidu_test);

		mLocationClient = new LocationClient(getApplicationContext());
		/**——————————————————————————————————————————————————————————————————
		 * 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
		 * ——————————————————————————————————————————————————————————————————
		 */
		mLocationClient.setAK("v1RA3449EtrBbbcy8iynQaLcBY21kxkp");
		myListener = new MyLocationListenner();
		mLocationClient.registerLocationListener(myListener);

		findView();
	}

	private void findView() {
		Button bt_start = (Button) findViewById(R.id.bt_start);
		tv_address = (TextView) findViewById(R.id.tv_address);

		bt_start.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_start:





				if (mLocationClient != null){
//				if (mLocationClient != null && mLocationClient.isStarted()){
					Log.i(TAG, "start position");
					setLocationOption();
					mLocationClient.start();
					mLocationClient.requestLocation();
				}else{
					Log.d(TAG, "locClient is null or not started");
				}

				break;

			default:
				break;
		}
	}

	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//打开gps
		option.setCoorType("bd09ll");		//设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		option.setScanSpan(2000);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
//		option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
		option.setPriority(LocationClientOption.GpsFirst);        //不设置，默认是gps优先

//		option.setPoiNumber(5);	//最多返回POI个数
//		option.setPoiDistance(1000); //poi查询距离
//		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息

		option.disableCache(true);

		mLocationClient.setLocOption(option);
	}

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				/**
				 * 格式化显示地址信息
				 */
				sb.append("\n省：");
				sb.append(location.getProvince());
				sb.append("\n市：");
				sb.append(location.getCity());
				sb.append("\n区/县：");
				sb.append(location.getDistrict());
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}
			sb.append("\nsdk version : ");
			sb.append(mLocationClient.getVersion());
			sb.append("\nisCellChangeFlag : ");
			sb.append(location.isCellChangeFlag());

			Log.i(TAG, sb.toString());

			tv_address.setText(sb.toString());
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if(poiLocation.hasPoi()){
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			}else{
				sb.append("noPoi information");
			}

			Log.i(TAG, sb.toString());
		}
	}

	@Override
	public void onStop() {

		if(mLocationClient!=null){
			mLocationClient.stop();
			mLocationClient = null;
		}
		super.onStop();
	}

}