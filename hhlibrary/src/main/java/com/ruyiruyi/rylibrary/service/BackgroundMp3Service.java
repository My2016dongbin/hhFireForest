package com.ruyiruyi.rylibrary.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.base.AutoStartActivity;
import com.ruyiruyi.rylibrary.base.NullActivity;
import com.ruyiruyi.rylibrary.bus.StopAudio;
import com.ruyiruyi.rylibrary.bus.VideoStatus;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;


public class BackgroundMp3Service extends Service {
    private static final String TAG = BackgroundMp3Service.class.getSimpleName();
    private String messageWeb;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "handleMessage: 播放完毕");
                    BackgroundMp3Service.this.onDestroy();
                    break;
            }
        }
    };
    private String warnType;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: 已创建啊");
        EventBus.getDefault().register(this);
    }
    ///停止播放
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(StopAudio stopAudio) {
        Log.e("bac","StopAudio");
        BackgroundMp3Service.this.onDestroy();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try{
            //音频停止
            mediaPlayer.stop();
            //震动停止
            vibrator.cancel();
        }catch (Exception e){
            Log.e("error","mediaPlayer.stop()");
        }
    }

    private void startVoice() {
     //   Log.e(TAG, "onCreate:  messageWeb = " + messageWeb);
        Log.e(TAG, "startVoice: ");
        //接下来根据messageWeb分类通知语音播报

      /*  //方案二：网络音频
        String stringExtra = "http://zjlt.sc.chinaz.com/Files/DownLoad/sound1/201511/6553.mp3";
        Uri parse = Uri.parse(stringExtra);*/

        try {
          /*  //方案二：网络音频
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(BackgroundMp3Service.this, parse);
            mediaPlayer.prepare();// 进行缓冲
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });*/

            //方案一：本地音频
            Log.e(TAG, "startVoice: 开始播放" );
            User user = new DbConfig(this).getUser();
            int isyunyin = user.getIsyunyin();
            Log.e(TAG, "startVoice: " + isyunyin );
          //  RingtoneManager.getActualDefaultRingtoneUri(this , RingtoneManager.TYPE_NOTIFICATION) == null
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
          //  int max1 = am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);// 1
            int current1 = am.getStreamVolume(AudioManager.STREAM_SYSTEM);
            Log.e(TAG, "系统音量值：-" + current1);

          //  int max2 = am.getStreamMaxVolume(AudioManager.STREAM_RING);// 2
            int current2 = am.getStreamVolume(AudioManager.STREAM_RING);
            Log.e(TAG, "系统铃声值：-" + current2);

            if (isyunyin == 1 && current1!=0  && current2!=0){

                Log.e(TAG, "service  play "  );

                // 获取震动器服务
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // 确定震动模式（强烈或轻微）
                long[] pattern = {0, 300, 600, 300}; // 强烈震动的模式：开启0毫秒，震动200毫秒，休息200毫秒，然后重复
                // 对震动器进行初始化
                vibrator.vibrate(pattern, 0); // 0表示无限循环

                mediaPlayer = MediaPlayer.create(getApplicationContext(), Objects.equals(warnType, "2") ? R.raw.find_order:R.raw.find_fire);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
                final int[] tag = {1};
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(tag[0] < 3){
                            mediaPlayer.start();
                        }else{
                            BackgroundMp3Service.this.onDestroy();
                        }
                        tag[0]++;
                    }
                });
                //mHandler.sendEmptyMessageDelayed(1, 10000);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand:" );

        Notification notification;
        Intent intent_ = new Intent(this, NullActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("location", "location", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification.Builder builder = new Notification.Builder(this, "location");
            builder.setContentIntent(PendingIntent.getActivity(this, 0, intent_, 0))
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentText("为您持续巡护中..")
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("为您持续巡护中..")
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent_, 0))
                    .build();
        }
        startForeground(110, notification);

        warnType = intent.getStringExtra("type");
        startVoice();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}