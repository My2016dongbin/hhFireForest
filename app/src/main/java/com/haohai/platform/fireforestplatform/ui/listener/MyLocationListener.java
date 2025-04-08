package com.haohai.platform.fireforestplatform.ui.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.fireforestplatform.MyApplication;
import com.ruyiruyi.rylibrary.bus.MkToast;
import com.ruyiruyi.rylibrary.ui.dialog.Common;
import com.ruyiruyi.rylibrary.utils.CommonData;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class MyLocationListener extends BDAbstractLocationListener {
    private static final String TAG = MyLocationListener.class.getSimpleName();
    private Date date;

    @Override
    public void onReceiveLocation(BDLocation location){
        date = new Date();
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        double latitude = location.getLatitude();    //获取纬度信息
        double longitude = location.getLongitude();    //获取经度信息
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        if(latitude != 0 && longitude != 0 /*&& (!String.valueOf(latitude).contains("E"))*/){
            Log.e(TAG, "onReceiveLocation: time " + CommonData.time );
            Log.e(TAG, "onReceiveLocation: getTime() " + date.getTime() );
            if( String.valueOf(latitude).contains("E") || CommonData.lng_an==0 || (CommonUtil.distance(CommonData.lng_an,CommonData.lat_an,longitude,latitude) <= 0.12)){
                Log.e(TAG, "onReceiveLocation: distance points = " + CommonData.lng_an + "," + CommonData.lat_an + "|" + longitude + "," +latitude );
                Log.e(TAG, "onReceiveLocation: distance = " + CommonUtil.distance(CommonData.lng_an,CommonData.lat_an,longitude,latitude) );
                if(CommonData.lat != latitude && CommonData.lng != longitude){
                    /*double distance = CommonUtil.distance(CommonData.lng, CommonData.lat, longitude, latitude);
                    if((distance <= 1 && distance > 0) || (CommonData.lat==0&&CommonData.lng==0)){
                        CommonData.time = date.getTime();
                        CommonData.lat = latitude;
                        CommonData.lng = longitude;
                        Log.e(TAG, "onReceiveLocation:经纬度是 in" +  latitude +"," +longitude);
                        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences("haohai_file", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putFloat("latitude", (float) latitude);
                        editor.putFloat("longitude", (float) longitude);
                        editor.apply();
                    }else{
                        long longTime = date.getTime();
                        if(longTime - CommonData.time >= 300000){
                            CommonData.lat = latitude;
                            CommonData.lng = longitude;
                            CommonData.time = longTime;
                            Log.e(TAG, "onReceiveLocation:经纬度是 in else" +  latitude +"," +longitude);
                        }
                    }*/
                    if(String.valueOf(latitude).contains("E")){
                        if(CommonData.lng_an==0){
                            return;
                        }
                        CommonData.lat = CommonData.lat_an;
                        CommonData.lng = CommonData.lng_an;
                        Log.e(TAG, "onReceiveLocation:经纬度是 in E " +  CommonData.lat +"," +CommonData.lng);
                        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences("haohai_file", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putFloat("latitude", (float) CommonData.lat);
                        editor.putFloat("longitude", (float) CommonData.lng);
                        editor.apply();
                        EventBus.getDefault().post(new MkToast( "lat:" + CommonData.lat + ",lng:" + CommonData.lng + "| E "));
                    }else{
                        CommonData.lat = latitude;
                        CommonData.lng = longitude;
                        Log.e(TAG, "onReceiveLocation:经纬度是 in" +  latitude +"," +longitude);
                        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences("haohai_file", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putFloat("latitude", (float) latitude);
                        editor.putFloat("longitude", (float) longitude);
                        editor.apply();
                        EventBus.getDefault().post(new MkToast( "lat:" + CommonData.lat + ",lng:" + CommonData.lng + "| lat_an:" + CommonData.lat_an + ",CommonData.lng_an:" + CommonData.lng_an + "| distance：" + (CommonUtil.distance(CommonData.lng_an,CommonData.lat_an,longitude,latitude))));
                    }
                }
            }else{
                Log.e(TAG, "onReceiveLocation: distance else = " + CommonUtil.distance(CommonData.lng_an,CommonData.lat_an,longitude,latitude) );
            }
        }

        Log.e(TAG, "onReceiveLocation:经纬度是 " +  latitude +"," +longitude);
        Log.e(TAG, "onReceiveLocation:经纬度是 An" +  CommonData.lat_an +"," + CommonData.lng_an);
    }
}
