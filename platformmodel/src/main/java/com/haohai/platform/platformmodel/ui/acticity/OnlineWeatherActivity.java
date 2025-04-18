package com.haohai.platform.platformmodel.ui.acticity;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haohai.platform.platformmodel.R;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OnlineWeatherActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView wind_text;

    private TextView speed_text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;
    String weatherResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_weather);

        initView();
        postData();
    }

    private void postData() {
        //天气数据
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "fire/api/hefengWeather/getWeatherList");
        User user = new DbConfig(this).getUser();
        params.addParameter("gridNo",user.getGridNo());
        params.addHeader("Authorization","bearer " + user.token);
        x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "onSuccess: bingo weather" + result );
                weatherResult = result;
                bindData();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(OnlineWeatherActivity.this, "天气信息加载失败请稍后重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

        //每日一图
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Glide.with(OnlineWeatherActivity.this).load(bingPic).into(bingPicImg);
                        }catch (Exception e){

                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void bindData() {
        try {
            JSONObject jsonObject = new JSONObject(weatherResult);
            JSONArray data = jsonObject.getJSONArray("data");
            forecastLayout.removeAllViews();
            for (int i = 0; i < data.length(); i++) {
                JSONObject model = (JSONObject) data.get(i);
                if(i == 0){
                    titleCity.setText(model.getString("name"));
                    String updateTime = Calendar.getInstance().getTime().getHours() + ":" + Calendar.getInstance().getTime().getMinutes();
                    titleUpdateTime.setText(updateTime);
                    degreeText.setText(model.getString("temp")+"℃");
                    weatherInfoText.setText(model.getString("text"));
                    aqiText.setText(model.getString("humidity"));
                    pm25Text.setText(model.getString("pressure"));
                    wind_text.setText(model.getString("windDir"));
                    speed_text.setText( (Objects.equals(model.getString("windSpeed"), "null") ?"1":model.getString("windSpeed"))  + "级");
                }else{
                    View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                    TextView dateText = (TextView) view.findViewById(R.id.date_text);
                    TextView infoText = (TextView) view.findViewById(R.id.info_text);
                    TextView maxText = (TextView) view.findViewById(R.id.max_text);
                    TextView minText = (TextView) view.findViewById(R.id.min_text);
                    dateText.setText(model.getString("fxDate").substring(0,10));
                    infoText.setText(model.getString("textDay"));
                    maxText.setText(model.getString("tempMax"));
                    minText.setText(model.getString("tempMin"));
                    forecastLayout.addView(view);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        wind_text = (TextView) findViewById(R.id.wind_text);
        speed_text = (TextView) findViewById(R.id.speed_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String updateTime = Calendar.getInstance().getTime().getHours() + ":" + Calendar.getInstance().getTime().getMinutes();
                titleUpdateTime.setText(updateTime);
                postData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                    }
                },1000);
            }
        });
    }
}