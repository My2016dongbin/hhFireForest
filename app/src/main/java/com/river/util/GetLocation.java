package com.river.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;



public class GetLocation {

	private LocationClient mLocationClient;
	//private MyLocationListenner myListener;


	public void GetL(Context context,BDLocationListener myListener)
	{
		mLocationClient = new LocationClient(context);

		mLocationClient.setAK("dMGlsmpHOollXfGZ8jKfMpjQ");
		//myListener = new MyLocationListenner();
		mLocationClient.registerLocationListener(myListener);

		setLocationOption();
		mLocationClient.start();
		mLocationClient.requestLocation();
	}


	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);				//打开gps
		option.setCoorType("GCJ02");		//设置坐标类型
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		option.setScanSpan(20);	//设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
//			option.setPriority(LocationClientOption.NetWorkFirst);      //设置网络优先
		option.setPriority(LocationClientOption.GpsFirst);        //不设置，默认是gps优先

//			option.setPoiNumber(5);	//最多返回POI个数
//			option.setPoiDistance(1000); //poi查询距离
//			option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息

		option.disableCache(true);

		mLocationClient.setLocOption(option);
	}






}
