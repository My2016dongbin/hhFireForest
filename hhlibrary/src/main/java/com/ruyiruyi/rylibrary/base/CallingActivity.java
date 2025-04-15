package com.ruyiruyi.rylibrary.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.bus.WechatBus;
import com.ruyiruyi.rylibrary.db.Calling;
import com.ruyiruyi.rylibrary.db.CloseChannel;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.model.IMGridUsers;
import com.ruyiruyi.rylibrary.multitype.IMGridUser_UsersViewBinder;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.service.WechatService;
import com.ruyiruyi.rylibrary.utils.CircleImageView;
import com.ruyiruyi.rylibrary.utils.CommonData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.NERtcParameters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;


public class CallingActivity extends HhBaseActivity implements NERtcCallback {
    private LinearLayout ll_one;
    private RecyclerView rlv_grid;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private LinearLayout ll_status_un;
    private LinearLayout ll_status_ing;
    private ImageView iv_yes;
    private ImageView iv_no;
    private ImageView iv_shutup;
    private ImageView iv_shutup_back;
    private ImageView iv_close;
    private ImageView iv_loudly;
    private ImageView iv_loudly_back;
    private CircleImageView iv_header;
    private TextView tv_time;
    private TextView tv_time2;
    private TextView tv_id;
    private TextView tv_name;

    private String channelId;
    private String accountId;
    private String requestId;
    private String customInfo;
    private boolean isCalling;
    private User user;
    private final String TAG = CallingActivity.class.getSimpleName();
    private boolean isShutUp = false;
    private boolean isLoudly = true;
    @SuppressLint("HandlerLeak")
    private final Handler mHandlerCall = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10) {
                if(!tv_time.getText().toString().contains(":")){
                    iv_close.setImageDrawable(getResources().getDrawable(R.mipmap.call_gray));
                    if(isCalling){
                        Toast.makeText(CallingActivity.this, "对方未接听", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CallingActivity.this, "未接听", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        EventBus.getDefault().register(this);
        startRing();
        init_();
        stupNERtc();
        bind_();
    }


    /**
     * 初始化SDK
     */
    private void stupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        NERtcEx.getInstance().setParameters(parameters); //先设置参数，后初始化

        NERtcOption options = new NERtcOption();

        try {
            NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.yun_xin_key), this, options);
        } catch (Exception e) {
            // 可能由于没有release导致初始化失败，release后再试一次
            NERtcEx.getInstance().release();
            try {
                NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.yun_xin_key), this, options);
            } catch (Exception ex) {
                Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        setLocalAudioEnable(true);
    }


    /**
     * 设置本地音频的可用性
     */
    private void setLocalAudioEnable(boolean enable) {
        NERtcEx.getInstance().enableLocalAudio(enable);
    }

    private void bind_() {
        RxViewAction.clickNoDouble(iv_yes).subscribe(unused -> {
            EventBus.getDefault().post(WechatBus.getInstance(true));//关闭铃声
            acceptInvite();
            ll_status_un.setVisibility(View.GONE);
            ll_status_ing.setVisibility(View.VISIBLE);
            tv_time.setText("正在连接..");
            tv_time2.setText("正在连接..");
        });
        RxViewAction.clickNoDouble(iv_close).subscribe(unused -> {
            iv_close.setImageDrawable(getResources().getDrawable(R.mipmap.call_gray));
            if(tv_time.getText().toString().equals("等待接听")){
                //取消邀请
                cancelInviteOther();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },1000);
        });
        RxViewAction.clickNoDouble(iv_no).subscribe(unused -> {
            iv_no.setImageDrawable(getResources().getDrawable(R.mipmap.call_gray));
            rejectInvite("",CommonData.invitedEvent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },1000);
        });
        RxViewAction.clickNoDouble(iv_shutup).subscribe(unused -> {
            isShutUp = !isShutUp;
            if(isShutUp){
                iv_shutup_back.setVisibility(View.VISIBLE);
                closeMicrophone();
            }else{
                iv_shutup_back.setVisibility(View.GONE);
                openMicrophone();
            }
        });
        RxViewAction.clickNoDouble(iv_loudly).subscribe(unused -> {
            isLoudly = !isLoudly;
            if(isLoudly){
                iv_loudly_back.setVisibility(View.VISIBLE);
                openSpeaker();
            }else{
                iv_loudly_back.setVisibility(View.GONE);
                closeSpeaker();
            }
        });
    }

    private void init_() {
        size_ = CommonData.personList.size();
        rlv_grid = findViewById(R.id.rlv_grid);
        ll_one = findViewById(R.id.ll_one);
        ll_status_un = findViewById(R.id.ll_status_un);
        ll_status_ing = findViewById(R.id.ll_status_ing);
        iv_yes = findViewById(R.id.iv_yes);
        iv_no = findViewById(R.id.iv_no);
        iv_shutup = findViewById(R.id.iv_shutup);
        iv_shutup_back = findViewById(R.id.iv_shutup_back);
        iv_close = findViewById(R.id.iv_close);
        iv_loudly = findViewById(R.id.iv_loudly);
        iv_loudly_back = findViewById(R.id.iv_loudly_back);
        iv_header = findViewById(R.id.iv_header);
        tv_time = findViewById(R.id.tv_time);
        tv_time2 = findViewById(R.id.tv_time2);
        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(CallingActivity.this, 5,GridLayoutManager.VERTICAL, false);
        rlv_grid.setLayoutManager(gridLayoutManager);
        adapter = new MultiTypeAdapter(items);
        adapter.register(IMGridUsers.Users.class, new IMGridUser_UsersViewBinder());
        rlv_grid.setAdapter(adapter);
        assertHasTheSameAdapter(rlv_grid, adapter);

        user = new DbConfig(this).getUser();
        Intent intent = getIntent();
        isCalling = intent.getBooleanExtra("isCalling",false);
        if(!isCalling){
            //被邀请
            channelId = intent.getStringExtra("channelId");
            accountId = intent.getStringExtra("accountId");
            requestId = intent.getStringExtra("requestId");
            customInfo = intent.getStringExtra("customInfo");
            CommonData.xdChannelId = channelId;
            tv_time2.setVisibility(View.GONE);
            tv_id.setText(accountId);
            tv_name.setText(customInfo);
        }else{
            //发起邀请
            ll_status_un.setVisibility(View.GONE);
            ll_status_ing.setVisibility(View.VISIBLE);
            tv_time.setText("等待接听");
            tv_time2.setText("等待接听");
            if(CommonData.personList.size()>1){
                rlv_grid.setVisibility(View.VISIBLE);
                ll_one.setVisibility(View.GONE);
                tv_time2.setVisibility(View.VISIBLE);
                tv_time.setVisibility(View.GONE);
                parseItems();
            }else{
                if(!CommonData.personList.isEmpty()){
                    IMGridUsers.Users user_ = CommonData.personList.get(0);
                    tv_name.setText(user_.getFullName());
                    tv_id.setText(user_.getId());
                }
                tv_time2.setVisibility(View.GONE);
                tv_time.setVisibility(View.VISIBLE);
                rlv_grid.setVisibility(View.GONE);
                ll_one.setVisibility(View.VISIBLE);
            }
        }

        mHandlerCall.sendEmptyMessageDelayed(10,10000);
    }

    void parseItems(){
        items.clear();
        items.addAll(CommonData.personList);

        assertAllRegistered(adapter, items);
        adapter.notifyDataSetChanged();
    }

    private AudioManager audioManager;
    private void openSpeaker(){
        audioManager.setSpeakerphoneOn(true);
        audioManager.setSpeakerphoneOn(true);
    }
    private void closeSpeaker(){
        audioManager.setSpeakerphoneOn(false);
        audioManager.setSpeakerphoneOn(false);
    }
    private void openMicrophone(){
        setLocalAudioEnable(true);
    }
    private void closeMicrophone(){
        setLocalAudioEnable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(WechatBus.getInstance(true));//关闭铃声
        EventBus.getDefault().unregister(this);
        CommonData.isCalling = false;
        NERtcEx.getInstance().leaveChannel();
        if(isCalling){
            closeChannel();
        }else{
            leave();
        }
        // 销毁实例
        NERtcEx.getInstance().release();
        mHandler.removeMessages(TIMER);
        mHandlerCall.removeMessages(10);
    }
    private void leave() {
        NIMClient.getService(SignallingService.class).leave(channelId, false, null).setCallback(
                new RequestCallbackWrapper<Void>() {

                    @Override
                    public void onResult(int i, Void aVoid, Throwable throwable) {
                        if (i == ResponseCode.RES_SUCCESS) {
                            //Toast.makeText(CallingActivity.this, "离开频道成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(CallingActivity.this, "离开频道失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void startRing() {
        CommonData.ringing = true;
        Intent intent_ = new Intent(this, WechatService.class);
        startService(intent_);
    }

    /**
     * 接受对方的的邀请并加入频道
     */
    private void acceptInvite() {
        CommonData.isCalling = true;
        InviteParamBuilder inviteParam = new InviteParamBuilder(channelId,accountId,requestId);
        //Toast.makeText(this, "channelId:"+channelId+",uid:"+CommonData.testId, Toast.LENGTH_LONG).show();
        NIMClient.getService(SignallingService.class).acceptInviteAndJoin(inviteParam, Long.parseLong(CommonData.testId)).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {

                    @Override
                    public void onResult(int code, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        //参考官方文档中关于api以及错误码的说明
                        if (code == ResponseCode.RES_SUCCESS) {
                            //Toast.makeText(CallingActivity.this, "接收邀请成功"+CommonData.testId, Toast.LENGTH_SHORT).show();

                            //加入音频房间
                            joinRoom(requestId);
                        } else {
                            //Toast.makeText(CallingActivity.this, "接收邀请返回的结果 ， code = " + code +(throwable == null ? "" : ", throwable = " +throwable.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 拒绝对方的邀请
     */
    private void rejectInvite(String customInfo,InvitedEvent invitedEvent) {
        InviteParamBuilder inviteParam = new InviteParamBuilder(invitedEvent.getChannelBaseInfo().getChannelId(),
                invitedEvent.getFromAccountId(),
                invitedEvent.getRequestId());
        if (!TextUtils.isEmpty(customInfo)) {
            inviteParam.customInfo(customInfo);
        }
        NIMClient.getService(SignallingService.class).rejectInvite(inviteParam);
    }

    /**
     * 取消邀请别人
     */
    private void cancelInviteOther() {
        if(CommonData.inviteOtherList == null){
            return;
        }
        for (int i = 0; i < CommonData.inviteOtherList.size(); i++) {
            InviteParamBuilder param = CommonData.inviteOtherList.get(i);
            param.offlineEnabled(true);
            NIMClient.getService(SignallingService.class).cancelInvite(param).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    Toast.makeText(CallingActivity.this, "取消邀请成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(int code) {
                    //Toast.makeText(CallingActivity.this, "取消邀请失败 ：code = " + code, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable exception) {
                    //Toast.makeText(CallingActivity.this, "取消邀请异常 ：exception = " + exception, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }



    private boolean hasJoined = false;//是否已经加入频道、房间
    ///加入房间(对方加入频道)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(Calling calling) {
        if(isCalling){
            EventBus.getDefault().post(WechatBus.getInstance(true));//关闭铃声
            joinChannelXD();//加入信令频道
            joinRoom(calling.getRoom());//加入语音房间
        }
    }

    private boolean channelCancel = false;
    ///频道关闭
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(CloseChannel closeChannel) {
        iv_close.setImageDrawable(getResources().getDrawable(R.mipmap.call_gray));
        if(!channelCancel){
            Toast.makeText(CallingActivity.this, "通话已结束", Toast.LENGTH_SHORT).show();
            channelCancel = true;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }


    /**
     * 加入音频房间
     * @param roomName
     */
    private void joinRoom(String roomName) {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/nertc/getToken");
        params.addHeader("Authorization","bearer " + user.getToken());
        params.addParameter("uid",Long.parseLong(CommonData.testId));
        params.addParameter("channelName",roomName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(TAG, "onSuccess: doLogin token " + result );
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    JSONObject model = (JSONObject) data.get(0);
                    Log.e(TAG, "onSuccess: model = " + model );
                    String token = model.getString("token");
                    CommonData.wyyToken = token;

                    //Toast.makeText(CallingActivity.this, "准备加入房间："+roomName, Toast.LENGTH_SHORT).show();
                    NERtcEx.getInstance().joinChannel(token,roomName,Long.parseLong(CommonData.testId));
                    Log.e(TAG, "onSuccess: yunxin " + token );
                    Log.e(TAG, "onSuccess: yunxin " + roomName );
                    Log.e(TAG, "onSuccess: yunxin " + Long.parseLong(user.getId()) );
                    //开始通话计时
                    startTime();

                    if(isShutUp){
                        closeMicrophone();
                    }else{
                        openMicrophone();
                    }
                    if(isLoudly){
                        openSpeaker();
                    }else{
                        closeSpeaker();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private final int TIMER = 1;
    private Calendar calendar = Calendar.getInstance();
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    break;
                case TIMER:
                    calendar.add(Calendar.SECOND,1);
                    String hour = parseZero(calendar.get(Calendar.HOUR_OF_DAY));
                    String minute = parseZero(calendar.get(Calendar.MINUTE));
                    String second = parseZero(calendar.get(Calendar.SECOND));
                    tv_time.setText(hour+":"+minute+":"+second);
                    tv_time2.setText(hour+":"+minute+":"+second);
                    mHandler.sendEmptyMessageDelayed(TIMER,1000);
                    break;
            }
        }
    };

    private String parseZero(int number) {
        String str = "";
        if(number > 9){
            str = number + "";
        }else{
            str = "0"+number;
        }
        return str;
    }

    private void startTime() {
        calendar.set(2022,12,1,0,0,0);
        hasJoined = true;
        tv_time.setText("00:00:00");
        tv_time2.setText("00:00:00");
        mHandler.sendEmptyMessageDelayed(TIMER,1000);
    }

    private void joinChannelXD() {
        //Toast.makeText(this, "准备加入频道id " + CommonData.xdChannelId + "," + CommonData.testId, Toast.LENGTH_LONG).show();
        NIMClient.getService(SignallingService.class).join(channelId, Long.parseLong(CommonData.testId), "", false).setCallback(
                new RequestCallbackWrapper<ChannelFullInfo>() {

                    @Override
                    public void onResult(int i, ChannelFullInfo channelFullInfo, Throwable throwable) {
                        if (i == ResponseCode.RES_SUCCESS) {
                            //Toast.makeText(CallingActivity.this, "加入频道成功", Toast.LENGTH_SHORT).show();
                        } else if (i == ResponseCode.RES_CHANNEL_MEMBER_HAS_EXISTS) {
                            //Toast.makeText(CallingActivity.this, "已经在频道中", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(CallingActivity.this, "加入频道失败 code=" + i, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 关闭频道
     */
    private void closeChannel() {
        NIMClient.getService(SignallingService.class).close(CommonData.xdChannelId, true,   "通话已结束").setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                //Toast.makeText(CallingActivity.this, "关闭频道,通话已结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                //Toast.makeText(CallingActivity.this, "关闭频道失败， code =  " + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                //Toast.makeText(CallingActivity.this, "关闭频道异常， exception =  " + exception, Toast.LENGTH_SHORT).show();
            }
        });
    }



    /*@Override
    public void onJoinChannel(int i, long l, long l1) {
        Log.e(TAG, "onJoinChannel: " + i +","+ l + "," + l1 );
        //Toast.makeText(this, i+"", Toast.LENGTH_SHORT).show();
        setLocalAudioEnable(true);
    }*/

    @Override
    public void onJoinChannel(int i, long l, long l1, long l2) {
        Log.e(TAG, "onJoinChannel: " + i +","+ l + "," + l1 );
        //Toast.makeText(this, i+"", Toast.LENGTH_SHORT).show();
        setLocalAudioEnable(true);
    }

    @Override
    public void onLeaveChannel(int i) {

        Log.e(TAG, "onLeaveChannel: " + i );
    }

    @Override
    public void onUserJoined(long l) {

        Log.e(TAG, "onUserJoined: " + l );
    }

    private int size_ = 0;
    @Override
    public void onUserLeave(long l, int i) {

        Log.e(TAG, "onUserLeave: " + l +","+ i );
        if(!isCalling){
            /*if(Long.parseLong(accountId) == l){ //系统自带此功能 暂已屏蔽
                iv_close.setImageDrawable(getResources().getDrawable(R.mipmap.call_gray));
                Toast.makeText(CallingActivity.this, "通话已结束", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
            }*/
        }else{
            for (int j = 0; j < CommonData.personList.size(); j++) {
                IMGridUsers.Users users_ = CommonData.personList.get(j);
                if(Long.parseLong(users_.getId()) == l){
                    size_--;
                }
            }
            if(size_ == 0){
                iv_close.setImageDrawable(getResources().getDrawable(R.mipmap.call_gray));
                //Toast.makeText(CallingActivity.this, "通话已结束", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
            }
        }
    }

    @Override
    public void onUserAudioStart(long l) {

        Log.e(TAG, "onUserAudioStart: " + l );
    }

    @Override
    public void onUserAudioStop(long l) {

        Log.e(TAG, "onUserAudioStop: " + l );
    }

    @Override
    public void onUserVideoStart(long l, int i) {

        Log.e(TAG, "onUserVideoStart: " + l +","+ i );
    }

    @Override
    public void onUserVideoStop(long l) {

        Log.e(TAG, "onUserVideoStop: " + l );
    }

    @Override
    public void onDisconnect(int i) {

        Log.e(TAG, "onDisconnect: " + i  );
    }

    @Override
    public void onClientRoleChange(int i, int i1) {

        Log.e(TAG, "onClientRoleChange: " + i +","+ i1  );
    }
}