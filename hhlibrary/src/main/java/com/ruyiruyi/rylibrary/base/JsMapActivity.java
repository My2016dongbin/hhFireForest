package com.ruyiruyi.rylibrary.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.utils.JsApi;
import com.ruyiruyi.rylibrary.utils.LatLngChange;
import com.ruyiruyi.rylibrary.utils.LatLngChangeNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class JsMapActivity extends HhBaseActivity implements JsApi.OnJsClickListener {

    private double longitude_double;
    private double latitude_double;
    private ActionBar mActionBar;
    private DWebView dWebView;
    private String latitude = "0";
    private String longitude = "0";
    private int MAP_REUEST_CODE = 2;
    private String cityAddress;
    private String city;
    private String district;
    private LatLng currentPt;
    //地理编码
    private GeoCoder mSearch;
    private TextView mStateBar;
    private TextView mStateBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_map);
        Intent intent_g = getIntent();
        longitude_double = intent_g.getDoubleExtra("longitude_double", 0.00);
        latitude_double = intent_g.getDoubleExtra("latitude_double", 0.00);
        longitude = longitude_double + "";
        latitude = latitude_double + "";
        if (longitude_double == 0.00 && latitude_double == 0.00) {
            Toast.makeText(JsMapActivity.this, "请检查授予定位权限并开启定位!", Toast.LENGTH_SHORT).show();
        }
        //ActionBar
        mActionBar = (ActionBar) findViewById(R.id.map_actionbar);
        mActionBar.setTitle("选择地图定位");
        mActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int var1) {
                switch (var1) {
                    case -1:
                        onBackPressed();
                        break;
                }
            }
        });

        initView();

    }

    @SuppressLint("JavascriptInterface")
    private void initView() {
        mStateBar = (TextView) findViewById(R.id.state);
        mStateBar2 = (TextView) findViewById(R.id.state2);
        dWebView = ((DWebView) findViewById(R.id.dwebview));
        dWebView.getSettings().setJavaScriptEnabled(true);

        String url = "file:///android_asset/jsmap/mars_demo.html";
        //url：网页地址；name/pwd:cookie信息
        // String isUser = SPUtils.getInstance().getString("userContent");
        dWebView.loadUrl(url);
        dWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //加载逃生出口资源点
                // getDataFromService();
                //加载道路
                // getRoadDataFromService();
            }
        });


        dWebView.addJavascriptInterface(this, "jk");
        JsApi jsApi = new JsApi(this);
        jsApi.setListener(this);
        dWebView.addJavascriptObject(jsApi, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dWebView.setWebContentsDebuggingEnabled(true);
        }
        //添加跨域支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            dWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            dWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = dWebView.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(dWebView.getSettings(), true);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // String mapUrl = "http://www.google.cn/maps/vt?lyrs=y@189&gl=cn&x={x}&y={y}&z={z}http://www.google.cn/maps/vt?lyrs=y@189&gl=cn&x={x}&y={y}&z={z}"; // 瓦片路径
        String mapUrl = "assets/qds_2005261619/{z}/{x}/{y}.png"; // 瓦片路径
        dWebView.callHandler("initMap", new Object[]{mapUrl}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });

        //创建新的地理编码检索实例；
        mSearch = GeoCoder.newInstance();
        //创建地理编码检索监听者；
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }
                //获取反向地理编码结果
                List<PoiInfo> poiList = result.getPoiList();
                if (null == poiList || poiList.size() == 0) {
                    Toast.makeText(JsMapActivity.this, "请在地图上标注资源点位置", Toast.LENGTH_SHORT).show();
                    mStateBar.setText("");
                    mStateBar2.setText("");
                } else {
                    PoiInfo poiInfo = poiList.get(0);
                    Log.e("TAG", "onGetReverseGeoCodeResult: ----" + poiInfo.toString() );
                    cityAddress = poiInfo.address + poiInfo.name;
                    Log.e("TAG", "onGetReverseGeoCodeResult: " + result.getAddressDetail().city );
                    Log.e("TAG", "onGetReverseGeoCodeResult: " + result.getAddressDetail().district );
                    Log.e("TAG", "onGetReverseGeoCodeResult: " + result.getAddressDetail().street );
                    Log.e("TAG", "onGetReverseGeoCodeResult: " + result.getAddressDetail().streetNumber );
                    city = poiInfo.city;
                    district = result.getAddressDetail().district;

                    mStateBar.setText(poiInfo.name);
                    mStateBar2.setText(poiInfo.address);
                }

            }
        };
        //设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(listener);

        //初始定位
        final JSONObject jsonObject = new JSONObject();
        final JSONArray array = new JSONArray();
        JSONObject posObj = new JSONObject();
        try {
            jsonObject.put("name","当前位置");
            jsonObject.put("id","user_position_id");
            posObj.put("lat", latitude_double);
            posObj.put("lng", longitude_double);
            jsonObject.put("position", posObj);
            array.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("GPSposition", jsonObject.toString());
        dWebView.callHandler("GPSflyto", new Object[]{new Gson().toJson(jsonObject.toString())}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });
        //当前位置图标
        dWebView.callHandler("removeDataSource", new Object[]{"user"}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,removeDataSource value is " + retValue);
            }
        });
        dWebView.callHandler("showPointforresource", new Object[]{"user", array, ""}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.d("jsbridge", "call succeed,return value is " + retValue);
            }
        });

        //默认选择初始点位
        List<MapModel> mapModelList = new ArrayList<>();
        MapModel mapModel = new MapModel("location", "",new MapPosition(Double.parseDouble(longitude), Double.parseDouble(latitude),0.00), "ic_onebody");
        mapModelList.add(mapModel);

        dWebView.callHandler("removeDataSource", new Object[]{"ic_onebody"}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.e("TAG", "onValue:  qingchu" + retValue);
            }
        });
        dWebView.callHandler("showPoint", new Object[]{"ic_onebody", new Gson().toJson(mapModelList), ""}, new OnReturnValue<String>() {
            @Override
            public void onValue(String retValue) {
                Log.e("TAG", "onValue:  dadian" + retValue);
            }
        });

        double[] doubles_bd09 = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(latitude), Double.parseDouble(longitude));
        currentPt = new LatLng(doubles_bd09[0],doubles_bd09[1]);
        //发起地理编码检索；
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentPt));
    }

    @Override
    public void onJsEscapeDetailsClickListener(String json) {

    }

    @Override
    public void onJsClickListener(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");


            List<MapModel> mapModelList = new ArrayList<>();
            MapModel mapModel = new MapModel("location", "",new MapPosition(Double.parseDouble(longitude), Double.parseDouble(latitude),0.00), "ic_onebody");
            mapModelList.add(mapModel);

            dWebView.callHandler("removeDataSource", new Object[]{"ic_onebody"}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.e("TAG", "onValue:  qingchu" + retValue);
                }
            });
            dWebView.callHandler("showPoint", new Object[]{"ic_onebody", new Gson().toJson(mapModelList), ""}, new OnReturnValue<String>() {
                @Override
                public void onValue(String retValue) {
                    Log.e("TAG", "onValue:  dadian" + retValue);
                }
            });

            double[] doubles_bd09 = LatLngChangeNew.calWGS84toBD09(Double.parseDouble(latitude), Double.parseDouble(longitude));
            currentPt = new LatLng(doubles_bd09[0],doubles_bd09[1]);
            //发起地理编码检索；
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentPt));


        } catch (Exception e) {
        }
    }



    public void mapclick(View view) {
        if (view.getId() == R.id.tv_map) {
            if (null == longitude || null == latitude) {
                Toast.makeText(JsMapActivity.this, "请在地图上选择资源点位置", Toast.LENGTH_SHORT).show();
            } else {
                if (city == "" || city == null) {
                    Toast.makeText(this, "请重新选择", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("cityAddress", cityAddress);
                    intent.putExtra("city", city);
                    intent.putExtra("district", district);
                    JsMapActivity.this.setResult(MAP_REUEST_CODE, intent);
                    finish();
                }

            }
        }
    }
}