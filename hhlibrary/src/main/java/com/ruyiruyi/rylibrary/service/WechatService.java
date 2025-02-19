package com.ruyiruyi.rylibrary.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.bus.SignStatusBus;
import com.ruyiruyi.rylibrary.bus.WechatBus;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.utils.CommonData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class WechatService extends Service {
    private static final String TAG = WechatService.class.getSimpleName();
    private static final int WECHAT = 1;
    private MediaPlayer mediaPlayer;
    private String messageWeb;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "handleMessage: 播放完毕");
                    WechatService.this.onDestroy();
                    break;
            }
        }
    };
    private String type;
    private int soundCount=0;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "onCreate: 已创建啊");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        type = intent.getStringExtra("type");
    }

    private void startVoice() {
     //   Log.e(TAG, "onCreate:  messageWeb = " + messageWeb);
        Log.e(TAG, "startVoice: ");
        //接下来根据messageWeb分类通知语音播报

        try {

            //方案一：本地音频
            Log.e(TAG, "startVoice: 开始播放" );
            User user = new DbConfig(this).getUser();
            int isyunyin = user.getIsyunyin();
            Log.e(TAG, "startVoice: " + isyunyin );
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int current1 = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
            Log.e("service", "系统音量值：-" + current1);

            int current2 = am.getStreamVolume(AudioManager.STREAM_RING);
            Log.e("service", "系统铃声值：-" + current2);

            if (isyunyin == 1 && current1!=0  && current2!=0){

                Log.e(TAG, "service   "  );

                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.wechat);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.e(TAG, "onCompletion: " );
                        if(0==soundCount||1==soundCount) {
                            mediaPlayer.start();
                            soundCount++;
                        }else {
                            mHandler.sendEmptyMessageDelayed(WECHAT, 100);
                        }
                    }
                });


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand:" );
        EventBus.getDefault().register(this);
        if(CommonData.ringing){
            startVoice();
        }else{
            WechatService.this.onDestroy();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(WechatBus message) {
        if(message.message){
            WechatService.this.onDestroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(WECHAT);
        mediaPlayer.stop();
        EventBus.getDefault().unregister(this);
    }
}