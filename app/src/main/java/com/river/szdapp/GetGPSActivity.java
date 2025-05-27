package com.river.szdapp;



import android.app.Activity;   
import android.content.Context;   
import android.location.Criteria;   
import android.location.Location;   
import android.location.LocationListener;   
import android.location.LocationManager;   
import android.os.Bundle;   
import android.util.Log;   
import android.view.View;   
import android.widget.Button;   
import android.widget.TextView;   
  
public class GetGPSActivity extends Activity {   
  
    TextView tv1;   
    Location location;   
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.activity_get_gps);  
  
        // 鐎规矮绠烾I缂佸嫪娆�   
        Button b1 = (Button) findViewById(R.id.bt_button1);   
        tv1 = (TextView) findViewById(R.id.tv_textview1);   
  
        // 閼惧嘲褰嘗ocationManager鐎电钖�   
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);   
  
        // 鐎规矮绠烠riteria鐎电钖�   
        Criteria criteria = new Criteria();   
        // 鐠佸墽鐤嗙�规矮缍呯划鍓р�樻惔锟� Criteria.ACCURACY_COARSE 濮ｆ棁绶濈划妤冩殣閿涳拷 Criteria.ACCURACY_FINE閸掓瑦鐦潏鍐翱缂侊拷   
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   
        // 鐠佸墽鐤嗛弰顖氭儊闂囷拷鐟曚焦鎹ｉ幏鏂句繆閹拷 Altitude   
        criteria.setAltitudeRequired(true);   
        // 鐠佸墽鐤嗛弰顖氭儊闂囷拷鐟曚焦鏌熸担宥勪繆閹拷 Bearing   
        criteria.setBearingRequired(true);   
        // 鐠佸墽鐤嗛弰顖氭儊閸忎浇顔忔潻鎰儉閸熷棙鏁圭拹锟�   
        criteria.setCostAllowed(true);   
        // 鐠佸墽鐤嗙�靛湱鏁稿┃鎰畱闂囷拷濮癸拷   
        criteria.setPowerRequirement(Criteria.POWER_LOW);   
  
        // 閼惧嘲褰嘒PS娣団剝浼呴幓鎰返閼帮拷   
        String bestProvider = lm.getBestProvider(criteria, true);   
        Log.i("yao", "bestProvider = " + bestProvider);   
  
        // 閼惧嘲褰囩�规矮缍呮穱鈩冧紖   
        location = lm.getLastKnownLocation(bestProvider);   
  
        // 缂佹瑦瀵滈柦顔剧拨鐎规氨鍋ｉ崙鑽ゆ磧閸氼剙娅�   
        b1.setOnClickListener(new View.OnClickListener() {   
            @Override  
            public void onClick(View v) {   
                updateLocation(location);   
            }   
        });   
  
        // 娴ｅ秶鐤嗛惄鎴濇儔閸ｏ拷   
        LocationListener locationListener = new LocationListener() {   
  
            // 瑜版挷缍呯純顔芥暭閸欐ɑ妞傜憴锕�褰�   
            @Override  
            public void onLocationChanged(Location location) {   
                Log.i("yao", location.toString());   
                updateLocation(location);   
            }   
  
            // Provider婢惰鲸鏅ラ弮鎯靶曢崣锟�   
            @Override  
            public void onProviderDisabled(String arg0) {   
                Log.i("yao", arg0);   
  
            }   
  
            // Provider閸欘垳鏁ら弮鎯靶曢崣锟�   
            @Override  
            public void onProviderEnabled(String arg0) {   
                Log.i("yao", arg0);   
            }   
  
            // Provider閻樿埖锟戒焦鏁奸崣妯绘鐟欙箑褰�   
            @Override  
            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {   
                Log.i("yao", "onStatusChanged");   
            }   
        };   
  
        // 500濮ｎ偆顫楅弴瀛樻煀娑擄拷濞嗏槄绱濊箛鐣屾殣娴ｅ秶鐤嗛崣妯哄   
        lm.requestLocationUpdates(bestProvider, 500, 0, locationListener);   
  
    }   
  
    // 閺囧瓨鏌婃担宥囩枂娣団剝浼�   
    // 鏇存柊浣嶇疆淇℃伅   
    private void updateLocation(Location location) {
        if (location != null) {
            tv1.setText("位置:" + location.toString()+"\n\t"+location.getAltitude() +"\n\t"+location.getBearing()+ "\n\t" + location.getLongitude() + "\n\t"
                    + location.getLatitude()+"\n\t"+location.getProvider()+"\n\t"+location.getSpeed()+"\n\t"+location.getTime());
        } else {
            Log.i("yao", "娌℃湁鑾峰彇鍒板畾浣嶅璞ocation");
        }
        /*if (location != null) {
            tv1.setText("瀹氫綅瀵硅薄淇℃伅濡備笅锛�" + location.toString()+"\n\t鍏朵腑娴锋嫈:"+location.getAltitude() +"\n\t鏂瑰悜:"+location.getBearing()+ "\n\t鍏朵腑缁忓害锛�" + location.getLongitude() + "\n\t鍏朵腑绾害锛�"  
                    + location.getLatitude()+"\n\t鎻愪緵鍟嗭細"+location.getProvider()+"\n\t閫熷害锛�"+location.getSpeed()+"\n\t鏃堕棿锛�"+location.getTime());   
        } else {   
            Log.i("yao", "娌℃湁鑾峰彇鍒板畾浣嶅璞ocation");   
        }  */
    }     
}  
