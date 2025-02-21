package com.haohai.platform.platformmodel.ui.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.haohai.ledge.videolibrary.listener.GSYSampleCallBack;
import com.haohai.ledge.videolibrary.listener.GSYVideoShotSaveListener;
import com.haohai.ledge.videolibrary.utils.OrientationUtils;
import com.haohai.ledge.videolibrary.video.MultiSampleVideo;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.fragment.base.HhBaseFragment;
import com.haohai.platform.platformmodel.ui.model.GridCamera;
import com.haohai.platform.platformmodel.ui.model.GridPointModel;
import com.haohai.platform.platformmodel.ui.model.GridTrees;
import com.haohai.platform.platformmodel.ui.model.VideoId;
import com.lechange.common.log.Logger;
import com.lechange.opensdk.api.InitParams;
import com.lechange.opensdk.api.LCOpenSDK_Api;
import com.lechange.opensdk.device.LCOpenSDK_DeviceInit;
import com.lechange.opensdk.listener.LCOpenSDK_EventListener;
import com.lechange.opensdk.listener.LCOpenSDK_TalkerListener;
import com.lechange.opensdk.media.LCOpenSDK_ParamReal;
import com.lechange.opensdk.media.LCOpenSDK_ParamTalk;
import com.lechange.opensdk.media.LCOpenSDK_PlayWindow;
import com.lechange.opensdk.media.LCOpenSDK_Talk;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.bus.VideoPause;
import com.ruyiruyi.rylibrary.bus.VideoStart;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import rx.functions.Action1;

import static com.haohai.ledge.videolibrary.video.base.GSYVideoView.CURRENT_STATE_PLAYING;

public class VideoNewFragment extends HhBaseFragment {
    private static final String TAG = VideoNewFragment.class.getSimpleName();
    public int currentVideo = 1;
    public int lastVideo = 1;
    public int tagVideo = 1;
    private ImageView yiView;
    private ImageView siView;
    private ImageView jiuView;
    private LinearLayout yiLayout;
    private LinearLayout siLayout;
    private LinearLayout jiuLayout;
    private FrameLayout video1_layout;
    private FrameLayout video2_layout;
    private FrameLayout video3_layout;
    private FrameLayout video4_layout;
    private FrameLayout video5_layout;
    private FrameLayout video6_layout;
    private FrameLayout video7_layout;
    private FrameLayout video8_layout;
    private FrameLayout video9_layout;
    private MultiSampleVideo video1Player;
    private MultiSampleVideo video2Player;
    private MultiSampleVideo video3Player;
    private MultiSampleVideo video4Player;
    private MultiSampleVideo video5Player;
    private MultiSampleVideo video6Player;
    private MultiSampleVideo video7Player;
    private MultiSampleVideo video8Player;
    private MultiSampleVideo video9Player;
    private Dialog videoListDialog;
    private View videoListInflater;

    public int num = 0;
    private ImageView listButton;
    private FrameLayout video_click1_layout;
    private FrameLayout video_click2_layout;
    private FrameLayout video_click3_layout;
    private FrameLayout video_click4_layout;
    private FrameLayout video_click5_layout;
    private FrameLayout video_click6_layout;
    private FrameLayout video_click7_layout;
    private FrameLayout video_click8_layout;
    private FrameLayout video_click9_layout;
    private int currentChooseVideo = 0;
    private int currentVideo1PlayerState = 0;
    private int currentVideo2PlayerState = 0;
    private int currentVideo3PlayerState = 0;
    private int currentVideo4PlayerState = 0;
    private int currentVideo5PlayerState = 0;
    private int currentVideo6PlayerState = 0;
    private int currentVideo7PlayerState = 0;
    private int currentVideo8PlayerState = 0;
    private int currentVideo9PlayerState = 0;
    private Button yichuButton;
    private ImageView video1AddView;
    private ImageView video2AddView;
    private ImageView video3AddView;
    private ImageView video4AddView;
    private ImageView video5AddView;
    private ImageView video6AddView;
    private ImageView video7AddView;
    private ImageView video8AddView;
    private ImageView video9AddView;
    private boolean isAddVideoViewClick = false;
    private LinearLayout videoListLayout;
    private ImageView listDialogImage;
    private OrientationUtils orientationUtils;
    private ImageView addImageView;



    private ScrollView sv_gridtrees;
    private LinearLayout ll_sv;
    private String currentClickItemName;
    private String currentClickItemId;
    private String currentClickItemMonitorId;
    private String currentClickItemSerial;


    private Dialog addDialog;
    private View addInflate;
    private TextView huoqingView;
    private TextView yinhuanView;
    private Button zuoshangButton;
    private Button shangButton;
    private Button youshangButton;
    private Button zuoButton;
    private Button xunhangButton;
    private Button youButton;
    private Button zuoxiaButton;
    private Button xiaButton;
    private Button youxiaButton;
    private Button lajinButton;
    private Button jiepingButton;
    private Button jujiaoButton;
    private Button layuanButton;
    private Button luxiangButton;
    private User user;
    private List<VideoId> videoIdList;

    private int moveType = 0;//1：上，2：下，3：左，4：右，5：左上，6：左下，7：右上，8：右下
    private int steptype = 0;
    private String kktype = "";
    private boolean isStop = false; //true false
    private boolean isRecording = false; //录像
    private boolean isTalking = false; //对讲
    private File file;//录像图片文件夹

    private ChangeTabReceiver changeTabReceiver;


    private FrameLayout frLiveWindowContent;
    public LCOpenSDK_PlayWindow mPlayWin;
    private PlayStatus playStatus = PlayStatus.ERROR;
    private int bateMode;
    private SoundStatus soundStatus = SoundStatus.PLAY;
    private SpeakStatus speakStatus = SpeakStatus.STOP;
    private AudioTalkerListener audioTalkerListener = new AudioTalkerListener();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_new, container, false);
    }

    class ChangeTabReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            String videoId = intent.getStringExtra("id");
            String deviceId = intent.getStringExtra("deviceId");
            String serial = intent.getStringExtra("serial");
            //       Toast.makeText(context, "广播已经接收", Toast.LENGTH_SHORT).show();
            Log.i("videoonReceive: ", videoId);
            postPlayerUrl(videoId);
            currentClickItemName="监控点视频";
            currentClickItemId = videoId;
            currentClickItemMonitorId = deviceId==null?"":deviceId;
            currentClickItemSerial = serial==null?"":serial;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        changeTabReceiver=new ChangeTabReceiver();
        IntentFilter resourcefilter = new IntentFilter();
        resourcefilter.addAction("video_play");
        getActivity().registerReceiver(changeTabReceiver, resourcefilter);
        EventBus.getDefault().register(this);

        videoIdList = new ArrayList<>();
        user = new DbConfig(getContext()).getUser();

        intiView();

        getDataFromService();
        bingView();
    }

    /**
     * 获取视频树
     */
    List<GridTrees> gridTreesList = new ArrayList<>();
    List<GridTrees> gridTreesList2 = new ArrayList<>();
    private void getDataFromService() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL +"resource/api/grid/listGridTreesNew");
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType", "Internet");

        params.setConnectTimeout(10000);
        Log.e(TAG, "listGridNewTrees: ---" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo gridNew" + result );
                ll_sv.removeAllViews();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            gridTreesList.add(new Gson().fromJson(data.get(i).toString(),GridTrees.class));
                            gridTreesList2.add(new Gson().fromJson(data.get(i).toString(),GridTrees.class));
                        }

                        TextView title1 = new TextView(getActivity());
                        title1.setText("视频监控点");
                        title1.setTextSize(14);
                        title1.setPadding(26,10,26,10);
                        title1.setTextColor(getResources().getColor(R.color.c2));
                        ll_sv.addView(title1);
                        ll_sv.addView(buildGridTrees(gridTreesList,false));
                        TextView title2 = new TextView(getActivity());
                        title2.setText("卡口");
                        title2.setTextSize(14);
                        title2.setPadding(26,10,26,10);
                        title2.setTextColor(getResources().getColor(R.color.c2));
                        ll_sv.addView(title2);
                        ll_sv.addView(buildGridTrees(gridTreesList2,true));


                        TextView view = new TextView(getActivity());
                        view.setHeight(180);
                        //view.setBackgroundColor(getResources().getColor(R.color.theme_primary));
                        ll_sv.addView(view);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: bingo gridNew error" + ex.toString() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }


    /**
     * 构建树框架
     */
    View buildGridTrees(List<GridTrees> treesList,boolean isKaKou){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees_out,null);
        LinearLayout ll_big = view.findViewById(R.id.ll_big);
        for (int i = 0; i < treesList.size(); i++) {
            final GridTrees gridTrees = treesList.get(i);
            View item = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees,null);
            LinearLayout ll_out = item.findViewById(R.id.ll_out);
            final LinearLayout ll_in = item.findViewById(R.id.ll_in);//用于监控点-摄像头便于动态加载
            final ImageView iv_status = item.findViewById(R.id.iv_status);
            TextView tv_gridtrees = item.findViewById(R.id.tv_gridtrees);
            tv_gridtrees.setText(gridTrees.getName());
            //有Children子项(递归展示)
            if(gridTrees.getChildren()!=null && gridTrees.getChildren().size()>0){
                List<GridTrees> itemList = new ArrayList<>();
                for (int m = 0; m < gridTrees.getChildren().size(); m++) {
                    itemList.add(gridTrees.getChildren().get(m));
                }
                //递归
                final View childTrees = buildGridTrees(itemList,isKaKou);
                //初始化绑定
                if(gridTrees.isStatus()){
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                    childTrees.setVisibility(View.VISIBLE);
                }else{
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    childTrees.setVisibility(View.GONE);
                }
                RxViewAction.clickNoDouble(ll_out).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(gridTrees.isStatus()){
                            gridTrees.setStatus(false);
                            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                            childTrees.setVisibility(View.GONE);
                        }else{
                            gridTrees.setStatus(true);
                            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                            childTrees.setVisibility(View.VISIBLE);
                        }
                    }
                });

                ll_out.addView(childTrees);
            }else{
                //无Children子项(点击加载监控点-摄像头)

                if(gridTrees.isStatus()){
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                    //展示新View(暂无意义)
                    ll_in.setVisibility(View.VISIBLE);
                }else{
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    //隐藏新View(暂无意义)
                    ll_in.setVisibility(View.GONE);
                }
                RxViewAction.clickNoDouble(ll_out).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(gridTrees.isStatus()){
                            gridTrees.setStatus(false);
                            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                            //隐藏/移除新View
                            ll_in.setVisibility(View.GONE);
                            ll_in.removeAllViews();
                        }else{
                            gridTrees.setStatus(true);
                            iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                            //展示/添加新View

                            //加载监控点-摄像头数据
                            getGridTreesChildren(gridTrees.getId(),isKaKou);

                            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    try {
                                        List<GridPointModel> pointModelList = new ArrayList<>();
                                        String result = intent.getStringExtra("result");
                                        boolean isKaKou_Bro = intent.getBooleanExtra("isKaKou",false);
                                        if(isKaKou != isKaKou_Bro){
                                            return;
                                        }
                                        JSONObject jsonObject = new JSONObject(result);
                                        JSONArray data = jsonObject.getJSONArray("data");
                                        Gson gson = new Gson();
                                        for (int x = 0; x < data.length(); x++) {
                                            pointModelList.add(gson.fromJson(data.get(x).toString(),GridPointModel.class));
                                        }

                                        //monitor子View
                                        View monitorView = buildMonitor(pointModelList);
                                        ll_in.addView(monitorView);
                                        ll_in.setVisibility(View.VISIBLE);
                                        getActivity().unregisterReceiver(this);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction(gridTrees.getId());
                            getActivity().registerReceiver(broadcastReceiver, intentFilter);
                        }
                    }
                });
            }


            ll_big.addView(item);
        }
        return ll_big;
    }

    /**
     * 构建监控点子View
     */
    View buildMonitor(List<GridPointModel> gridPointModelList){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees_out,null);
        LinearLayout ll_big = view.findViewById(R.id.ll_big);//取一个LinearLayout
        for (int i = 0; i < gridPointModelList.size(); i++) {
            final GridPointModel model = gridPointModelList.get(i);
            View item = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees,null);
            LinearLayout ll_out = item.findViewById(R.id.ll_out);
            final LinearLayout ll_in = item.findViewById(R.id.ll_in);
            final ImageView iv_status = item.findViewById(R.id.iv_status);
            TextView tv_gridtrees = item.findViewById(R.id.tv_gridtrees);
            tv_gridtrees.setText(model.getMonitor().getName());
            if(model.isStatus()){
                iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
            }else{
                iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
            }
            RxViewAction.clickNoDouble(ll_out).subscribe(new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    if(model.isStatus()){
                        model.setStatus(false);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                        //隐藏/移除新View
                        ll_in.setVisibility(View.GONE);
                        ll_in.removeAllViews();
                    }else{
                        model.setStatus(true);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                        //显示/添加新View
                        for (int m = 0; m < model.getCameraList().size(); m++) {
                            final GridCamera gridCamera = model.getCameraList().get(m);
                            //构建摄像头
                            View cameraView = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridcamera,null);
                            LinearLayout ll_camera = cameraView.findViewById(R.id.ll_camera);
                            ImageView iv_camera = cameraView.findViewById(R.id.iv_camera);//isOnLine 0 离线 1 在线
                            TextView tv_camera = cameraView.findViewById(R.id.tv_camera);
                            tv_camera.setText(gridCamera.getName());
                            RxViewAction.clickNoDouble(ll_camera).subscribe(new Action1<Void>() {
                                @Override
                                public void call(Void unused) {
                                    for (int j = 0; j < videoIdList.size(); j++) {
                                        VideoId video = videoIdList.get(j);
                                        if(Objects.equals(video.getMonitorId(), gridCamera.getDeviceId()) && Objects.equals(video.getVideoName(), gridCamera.getName())){
                                            Toast.makeText(getActivity(), "该视频已在播放列表中", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    postPlayerUrl(gridCamera.getId());

                                    currentClickItemName = gridCamera.getName();
                                    currentClickItemId = gridCamera.getId();
                                    currentClickItemMonitorId = gridCamera.getDeviceId();
                                    currentClickItemSerial = gridCamera.getSerial();
                                }
                            });

                            ll_in.addView(cameraView);
                        }

                        ll_in.setVisibility(View.VISIBLE);
                    }
                }
            });
            ll_big.addView(item);
        }

        return ll_big;
    }

    private void postPlayerUrl(String ids) {
//        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/mediaKit/getStreamAndroidByOut");
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/mediaKit/getLiveUrl");//
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType", "Internet");
        params.addBodyParameter("cameraId",ids);
        params.addBodyParameter("manufacturer","2");//
        params.addBodyParameter("streamType","2");//
        params.addBodyParameter("protocolType","rtmp");//
        Log.e(TAG, "onSuccess: bingo postPlayerUrl params" + params );
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(TAG, "onSuccess: bingo postPlayerUrl" + result );
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    if(data.length()!=0){
                        JSONObject obj = (JSONObject) data.get(0);
                        String url = obj.getString("url");

                        //跳转播放视频流
                        videoListDialog.dismiss();
                        if (isAddVideoViewClick) {//直接往点击的视频播放机中添加
                            addVideoPlayer(url);
                        } else {//往列表中排序添加
                            getVideoPlayerState();
                            setVideoPlayer(url);
                        }
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

    /**
     * 往点击的视频播放器中添加视频
     *
     * @param playerUrl
     */
    private void addVideoPlayer(String playerUrl) {
        if (currentChooseVideo == 1) {
            video1AddView.setVisibility(View.GONE);
            video1Player.setVisibility(View.VISIBLE);
            video1Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video1Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,1,playerUrl));
        } else if (currentChooseVideo == 2) {
            video2AddView.setVisibility(View.GONE);
            video2Player.setVisibility(View.VISIBLE);
            video2Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video2Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,2,playerUrl));
        } else if (currentChooseVideo == 3) {
            video3AddView.setVisibility(View.GONE);
            video3Player.setVisibility(View.VISIBLE);
            video3Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video3Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,3,playerUrl));
        } else if (currentChooseVideo == 4) {
            video4AddView.setVisibility(View.GONE);
            video4Player.setVisibility(View.VISIBLE);
            video4Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video4Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,4,playerUrl));
        } else if (currentChooseVideo == 5) {
            video5AddView.setVisibility(View.GONE);
            video5Player.setVisibility(View.VISIBLE);
            video5Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video5Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,5,playerUrl));
        } else if (currentChooseVideo == 6) {
            video6AddView.setVisibility(View.GONE);
            video6Player.setVisibility(View.VISIBLE);
            video6Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video6Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,6,playerUrl));
        } else if (currentChooseVideo == 7) {
            video7AddView.setVisibility(View.GONE);
            video7Player.setVisibility(View.VISIBLE);
            video7Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video7Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,7,playerUrl));
        } else if (currentChooseVideo == 8) {
            video8AddView.setVisibility(View.GONE);
            video8Player.setVisibility(View.VISIBLE);
            video8Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video8Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,8,playerUrl));
        } else if (currentChooseVideo == 9) {
            video9AddView.setVisibility(View.GONE);
            video9Player.setVisibility(View.VISIBLE);
            video9Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
            video9Player.startButtonLogic();
            videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,9,playerUrl));
        }
    }


    /**
     * 设置视频播放
     *
     * @param playerUrl
     */
    private void setVideoPlayer(String playerUrl) {
        Log.e(TAG, "setVideoPlayer: " + currentClickItemName);
        if (currentVideo == 1) {
            if (currentVideo1PlayerState == 0) {
                video1Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video1Player.startButtonLogic();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,1,playerUrl));
            } else {
                Toast.makeText(getContext(), "目前没有闲置播放器", Toast.LENGTH_SHORT).show();
            }
        } else if (currentVideo == 4) {
            if (currentVideo1PlayerState == 0) {
                video1Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video1Player.startButtonLogic();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,1,playerUrl));
            } else if (currentVideo2PlayerState == 0) {
                video2Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video2Player.startButtonLogic();
                video2Player.setVisibility(View.VISIBLE);
                video2AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,2,playerUrl));
            } else if (currentVideo4PlayerState == 0) {
                video4Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video4Player.startButtonLogic();
                video4Player.setVisibility(View.VISIBLE);
                video4AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,4,playerUrl));
            } else if (currentVideo5PlayerState == 0) {
                video5Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video5Player.startButtonLogic();
                video5Player.setVisibility(View.VISIBLE);
                video5AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,5,playerUrl));
            } else {
                Toast.makeText(getContext(), "目前没有闲置播放器", Toast.LENGTH_SHORT).show();
            }
        } else if (currentVideo == 9) {
            if (currentVideo1PlayerState == 0) {
                video1Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video1Player.startButtonLogic();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,1,playerUrl));
            } else if (currentVideo2PlayerState == 0) {
                video2Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video2Player.startButtonLogic();
                video2Player.setVisibility(View.VISIBLE);
                video2AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,2,playerUrl));
            } else if (currentVideo3PlayerState == 0) {
                video3Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video3Player.startButtonLogic();
                video3Player.setVisibility(View.VISIBLE);
                video3AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,3,playerUrl));
            } else if (currentVideo4PlayerState == 0) {
                video4Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video4Player.startButtonLogic();
                video4Player.setVisibility(View.VISIBLE);
                video4AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,4,playerUrl));
            } else if (currentVideo5PlayerState == 0) {
                video5Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video5Player.startButtonLogic();
                video5Player.setVisibility(View.VISIBLE);
                video5AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,5,playerUrl));
            } else if (currentVideo6PlayerState == 0) {
                video6Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video6Player.startButtonLogic();
                video6Player.setVisibility(View.VISIBLE);
                video6AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,6,playerUrl));
            } else if (currentVideo7PlayerState == 0) {
                video7Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video7Player.startButtonLogic();
                video7Player.setVisibility(View.VISIBLE);
                video7AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,7,playerUrl));
            } else if (currentVideo8PlayerState == 0) {
                video8Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video8Player.startButtonLogic();
                video8Player.setVisibility(View.VISIBLE);
                video8AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,8,playerUrl));
            } else if (currentVideo9PlayerState == 0) {
                video9Player.setUpLazy(playerUrl, false, null, null, currentClickItemName);
                video9Player.startButtonLogic();
                video9Player.setVisibility(View.VISIBLE);
                video9AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemMonitorId,currentClickItemSerial,currentClickItemId,currentClickItemName,9,playerUrl));
            } else {
                Toast.makeText(getContext(), "目前没有闲置播放器", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //获取监控点-摄像头数据
    void getGridTreesChildren(final String gridId,boolean isKaKou){
        String url = "";
        if(isKaKou){
            url = "resource/api/monitor/getKakouMonitorDetaisByGrid";
        }else{
//            url = "resource/api/monitor/getMonitorDetaisByGridAndType";
            url = "resource/api/monitor/getMonitorDetaisByGrid";
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + url);
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType", "Internet");
        params.addBodyParameter("gridId",gridId);
        //params.addBodyParameter("monitorType","1");

        params.setConnectTimeout(10000);
        Log.e(TAG, "listGridNewTrees: --kk-" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo gridNew --kk-" + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {

                        Intent it = new Intent();
                        it.setAction(gridId);
                        it.putExtra("result",result);
                        it.putExtra("isKaKou",isKaKou);
                        getActivity().sendBroadcast(it);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onError: bingo gridNew error" + ex.toString() );
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
    }


    private void intiView() {
        addImageView = ((ImageView) getView().findViewById(R.id.add_view));
        //按钮布局
        yichuButton = ((Button) getView().findViewById(R.id.yichu_button));
        zuoshangButton = ((Button) getView().findViewById(R.id.zuoshang_button));
        shangButton = ((Button) getView().findViewById(R.id.shang_button));
        youshangButton = ((Button) getView().findViewById(R.id.youshang_button));
        zuoButton = ((Button) getView().findViewById(R.id.zuo_button));
        xunhangButton = ((Button) getView().findViewById(R.id.xunhang_button));
        youButton = ((Button) getView().findViewById(R.id.you_button));
        zuoxiaButton = ((Button) getView().findViewById(R.id.zuoxia_button));
        xiaButton = ((Button) getView().findViewById(R.id.xia_button));
        youxiaButton = ((Button) getView().findViewById(R.id.youxia_button));
        lajinButton = ((Button) getView().findViewById(R.id.lajin_button));
        jiepingButton = ((Button) getView().findViewById(R.id.jieping_button));
        jujiaoButton = ((Button) getView().findViewById(R.id.jujiao_button));
        layuanButton = ((Button) getView().findViewById(R.id.layuan_button));
        luxiangButton = ((Button) getView().findViewById(R.id.luxiang_button));

        listButton = ((ImageView) getView().findViewById(R.id.list_button));
        //视频布局
        yiLayout = ((LinearLayout) getView().findViewById(R.id.yi_layout));
        siLayout = ((LinearLayout) getView().findViewById(R.id.si_layout));
        jiuLayout = ((LinearLayout) getView().findViewById(R.id.jiu_layout));
        video1_layout = ((FrameLayout) getView().findViewById(R.id.video1_layout));
        video2_layout = ((FrameLayout) getView().findViewById(R.id.video2_layout));
        video3_layout = ((FrameLayout) getView().findViewById(R.id.video3_layout));
        video4_layout = ((FrameLayout) getView().findViewById(R.id.video4_layout));
        video5_layout = ((FrameLayout) getView().findViewById(R.id.video5_layout));
        video6_layout = ((FrameLayout) getView().findViewById(R.id.video6_layout));
        video7_layout = ((FrameLayout) getView().findViewById(R.id.video7_layout));
        video8_layout = ((FrameLayout) getView().findViewById(R.id.video8_layout));
        video9_layout = ((FrameLayout) getView().findViewById(R.id.video9_layout));
        video_click1_layout = (FrameLayout) getView().findViewById(R.id.video_click1_layout);
        video_click2_layout = (FrameLayout) getView().findViewById(R.id.video_click2_layout);
        video_click3_layout = (FrameLayout) getView().findViewById(R.id.video_click3_layout);
        video_click4_layout = (FrameLayout) getView().findViewById(R.id.video_click4_layout);
        video_click5_layout = (FrameLayout) getView().findViewById(R.id.video_click5_layout);
        video_click6_layout = (FrameLayout) getView().findViewById(R.id.video_click6_layout);
        video_click7_layout = (FrameLayout) getView().findViewById(R.id.video_click7_layout);
        video_click8_layout = (FrameLayout) getView().findViewById(R.id.video_click8_layout);
        video_click9_layout = (FrameLayout) getView().findViewById(R.id.video_click9_layout);
        video1Player = ((MultiSampleVideo) getView().findViewById(R.id.video1_player));
        video2Player = ((MultiSampleVideo) getView().findViewById(R.id.video2_player));
        video3Player = ((MultiSampleVideo) getView().findViewById(R.id.video3_player));
        video4Player = ((MultiSampleVideo) getView().findViewById(R.id.video4_player));
        video5Player = ((MultiSampleVideo) getView().findViewById(R.id.video5_player));
        video6Player = ((MultiSampleVideo) getView().findViewById(R.id.video6_player));
        video7Player = ((MultiSampleVideo) getView().findViewById(R.id.video7_player));
        video8Player = ((MultiSampleVideo) getView().findViewById(R.id.video8_player));
        video9Player = ((MultiSampleVideo) getView().findViewById(R.id.video9_player));
        video1AddView = (ImageView) getView().findViewById(R.id.video1_add_view);
        video2AddView = (ImageView) getView().findViewById(R.id.video2_add_view);
        video3AddView = (ImageView) getView().findViewById(R.id.video3_add_view);
        video4AddView = (ImageView) getView().findViewById(R.id.video4_add_view);
        video5AddView = (ImageView) getView().findViewById(R.id.video5_add_view);
        video6AddView = (ImageView) getView().findViewById(R.id.video6_add_view);
        video7AddView = (ImageView) getView().findViewById(R.id.video7_add_view);
        video8AddView = (ImageView) getView().findViewById(R.id.video8_add_view);
        video9AddView = (ImageView) getView().findViewById(R.id.video9_add_view);
        initVideoPlayer(video1Player, 1);
        initVideoPlayer(video2Player, 2);
        initVideoPlayer(video3Player, 3);
        initVideoPlayer(video4Player, 4);
        initVideoPlayer(video5Player, 5);
        initVideoPlayer(video6Player, 6);
        initVideoPlayer(video7Player, 7);
        initVideoPlayer(video8Player, 8);
        initVideoPlayer(video9Player, 9);

        yiView = ((ImageView) getView().findViewById(R.id.yi_view));
        siView = ((ImageView) getView().findViewById(R.id.si_view));
        jiuView = ((ImageView) getView().findViewById(R.id.jiu_view));

        /**
         * 视频列表dialog
         */
        videoListDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyleLeft);
        videoListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_video_list_new, null);
        videoListInflater.setMinimumWidth(100000);
        sv_gridtrees = ((ScrollView) videoListInflater.findViewById(R.id.sv_gridtrees));
        ll_sv = ((LinearLayout) videoListInflater.findViewById(R.id.ll_sv));
        listDialogImage = ((ImageView) videoListInflater.findViewById(R.id.list_dialog_button));
        videoListDialog.setContentView(videoListInflater);
        Window videoListDialogWindow = videoListDialog.getWindow();
        videoListDialogWindow.setGravity(Gravity.LEFT);

        WindowManager.LayoutParams lpvideoList = videoListDialogWindow.getAttributes();
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();
        lpvideoList.width = (int) (width * 0.7);
        lpvideoList.height = height * 1;
        videoListDialogWindow.setAttributes(lpvideoList);
        videoListDialog.setCanceledOnTouchOutside(true);

        /**
         * 火情dialog
         */
        addDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        addInflate = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_fire, null);
        addInflate.setMinimumWidth(10000);

        huoqingView = ((TextView) addInflate.findViewById(R.id.huoqing_view));
        yinhuanView = ((TextView) addInflate.findViewById(R.id.yinhuan_view));
        addDialog.setContentView(addInflate);

        Window addWindow = addDialog.getWindow();
        addWindow.setWindowAnimations(R.style.ActionSheetDialogTopScaleAnimation);
        addWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addWindow.setGravity(Gravity.TOP | Gravity.RIGHT);
        WindowManager.LayoutParams addListLp = addWindow.getAttributes();

        WindowManager addwm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int addheight = addwm.getDefaultDisplay().getHeight();
        int addwidth = addwm.getDefaultDisplay().getWidth();

        addListLp.width = (int) (addwidth * 0.4);
        addListLp.height = 330;
        addListLp.y = 90;
        addWindow.setAttributes(addListLp);
        addDialog.setCanceledOnTouchOutside(true);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化LCOpenSDK_Api
                String token = CommonData.daHuaTokenStr; //开发者自己去平台请求授权token
                String host = "openapi.lechange.cn:443";// 国内平台地址：openapi.lechange.cn:443 海外平台地址：openapi.easy4ip.com:443
                InitParams initParams = new InitParams(getActivity(), host, token);
                try {
                    int iRet = LCOpenSDK_Api.initOpenApi(initParams);
                    LCOpenSDK_DeviceInit.getInstance();
                } catch (Throwable throwable) {
                    Log.e(TAG, "intiView: initOpenApi e" + throwable.getMessage() );
                    throwable.printStackTrace();
                }
            }
        },6000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getView()==null){
                    return;
                }
                mPlayWin = new LCOpenSDK_PlayWindow();
                frLiveWindowContent = getView().findViewById(R.id.fr_live_window_content);
                // 初始化播放窗口
                mPlayWin.initPlayWindow(getActivity(), frLiveWindowContent, 0, false);
                setWindowListener(mPlayWin);
                mPlayWin.openTouchListener();//开启收拾监听
            }
        },8000);
    }


    /**
     * 开始播放
     */
    public void play() {
        stop();
        bateMode = 0;
        String playToken = null;
        int channelId = -1;
        try {
            JSONArray channelList = CommonData.deviceSub.getJSONArray("channels");
            Log.e(TAG, "play: channelList = " + channelList );
            JSONObject channel = (JSONObject) channelList.get(0);
            Log.e(TAG, "play: channel = " + channel );
            channelId = Integer.parseInt(channel.getString("channelId"));
        } catch (Exception e) {
            Log.e(TAG, "play: e" + e.toString() );
            e.printStackTrace();
        }
        Log.e(TAG, "playTalk: plays" +
                        CommonData.subToken +","+
                CommonData.subId +","+
                channelId +","+//channelId
                CommonData.subId +","+//psk
                playToken +","+//playToken
                bateMode +","+
                true +","+true +","+(-1)//i
                );
        LCOpenSDK_ParamReal paramReal = new LCOpenSDK_ParamReal(
                CommonData.subToken,
                CommonData.subId,
                channelId,//channelId
                CommonData.subId,//psk
                playToken,//playToken
                bateMode,
                true,true,-1//imageSize
        );
        mPlayWin.playRtspReal(paramReal);

    }

    /**
     * 停止播放
     */
    public void stop() {
        closeAudio();// 关闭音频
        stopTalking();//关闭对讲
        mPlayWin.stopRtspReal(true);// 关闭视频
    }


    /**
     * 打开声音
     */
    public boolean openAudio() {
        return mPlayWin.playAudio() == 0;
    }
    /**
     * 关闭声音
     */
    public boolean closeAudio() {
        return mPlayWin.stopAudio() == 0;
    }

    private void setWindowListener(LCOpenSDK_PlayWindow playWin) {
        playWin.setWindowListener(new LCOpenSDK_EventListener() {
            //手势缩放开始事件
            @Override
            public void onZoomBegin(int index) {
                super.onZoomBegin(index);
            }

            //手势缩放中事件
            @Override
            public void onZooming(int index, float dScale) {
                super.onZooming(index, dScale);
                mPlayWin.doScale(dScale);
            }

            //缩放结束事件
            @Override
            public void onZoomEnd(int index, ZoomType zoomType) {
                super.onZoomEnd(index, zoomType);
            }

            //窗口单击事件
            @Override
            public void onControlClick(int index, float dx, float dy) {
                super.onControlClick(index, dx, dy);
            }

            //窗口双击事件
            @Override
            public void onWindowDBClick(int index, float dx, float dy) {
                super.onWindowDBClick(index, dx, dy);
            }

            //滑动开始事件
            @Override
            public boolean onSlipBegin(int index, Direction direction, float dx, float dy) {
                return super.onSlipBegin(index, direction, dx, dy);
            }

            //滑动中事件
            @Override
            public void onSlipping(int index, Direction direction, float prex, float prey, float dx, float dy) {
                super.onSlipping(index, direction, prex, prey, dx, dy);
                mPlayWin.doTranslate(dx,dy);
            }

            //滑动结束事件
            @Override
            public void onSlipEnd(int index, Direction direction, float dx, float dy) {
                super.onSlipEnd(index, direction, dx, dy);
                mPlayWin.doTranslateEnd();
            }

            //长按开始回调
            @Override
            public void onWindowLongPressBegin(int index, Direction direction, float dx, float dy) {
                super.onWindowLongPressBegin(index, direction, dx, dy);
            }

            //长按事件结束
            @Override
            public void onWindowLongPressEnd(int index) {
                super.onWindowLongPressEnd(index);
            }

            /**
             * 播放事件回调
             * resultSource:  0--RTSP  1--HLS  5--DHHTTP  99--OPENAPI
             */
            @Override
            public void onPlayerResult(int index, String code, int resultSource) {
                //mPlayWin.setSEnhanceMode(4);//设置降噪等级最大
                super.onPlayerResult(index, code, resultSource);
                Log.e(TAG, "onPlayerResult: index= " + index + " , code= " + code + " , resultSource= " + resultSource);
                boolean failed = false;
                if (resultSource == 99) {
                    //code  -1000 HTTP交互出错或超时
                    failed = true;
                } else {
                    if (resultSource == 5 && (!(code.equals("1000") || code.equals("0") || code.equals("4000")))) {
                        // code 1000-开启播放成功  0-开始拉流
                        failed = true;
                        if (code.equals("1000005")) {
                            //inputEncryptKey();
                        }
                    }

                    else if (resultSource == 0 && (code.equals("0") || code.equals("1") || code.equals("3") || code.equals("7"))) {
                        // code
                        // 0-组帧失败，错误状态
                        // 1-内部要求关闭,如连接断开等，错误状态
                        // 3-RTSP鉴权失败，错误状态
                        // 7-秘钥错误
                        failed = true;
                        if (code.equals("7")) {
                            //inputEncryptKey();
                        }
                    }
                }
                if (failed) {
                    //loadingStatus(LoadStatus.LOAD_ERROR, getResources().getString(R.string.lc_demo_device_video_play_error) + ":" + code + "." + resultSource, "");
                    playStatus = PlayStatus.ERROR;
                }
            }

            //分辨率改变事件
            @Override
            public void onResolutionChanged(int index, int width, int height) {
                super.onResolutionChanged(index, width, height);
            }

            //播放开始回调
            @Override
            public void onPlayBegan(int index) {
                super.onPlayBegan(index);
                Log.e(TAG, "onPlayBegan: index= " + index);
                //loadingStatus(LoadStatus.LOAD_SUCCESS, "", "");
                playStatus = PlayStatus.PLAY;
            }

            //接收数据回调
            @Override
            public void onReceiveData(int index, int len) {
                super.onReceiveData(index, len);
                Log.e(TAG, "onReceiveData: index= " + index + " , len= " + len);
            }

            //接收帧流回调
            @Override
            public void onStreamCallback(int index, byte[] bytes, int len) {
                super.onStreamCallback(index, bytes, len);
                Log.e(TAG, "onStreamCallback: index= " + index + " , len= " + len);
            }

            //播放结束事件
            @Override
            public void onPlayFinished(int index) {
                super.onPlayFinished(index);
                Log.e(TAG, "onPlayFinished: index= " + index);
            }

            //播放时间信息回调
            @Override
            public void onPlayerTime(int index, long time) {
                super.onPlayerTime(index, time);
                Log.e(TAG, "onPlayerTime: index= " + index + " , time= " + time);
            }


            @Override
            public void onIVSInfo(int index, final String ivsInfo, long type, long len, long realLen) {
                super.onIVSInfo(index, ivsInfo, type, len, realLen);
                Log.e(TAG, "onIVSInfo: index= " + index + " , ivsInfo= " + ivsInfo);

                if (playStatus !=PlayStatus.PLAY) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (ivsInfo != null && ivsInfo.contains("PtzLimitStatus")) {
                                final String source = ivsInfo.substring(ivsInfo.lastIndexOf("[") + 1, ivsInfo.lastIndexOf("]")).replace(" ", "");
                                String[] target = source.split(",");
                                if (target != null && target.length == 2) {
                                    final String hor = target[0];
                                    final String ver = target[1];
                                    if (hor.equals("1") || hor.equals("-1") || ver.equals("1") || ver.equals("-1")) {
                                        /*if(hor.equals("1")){
                                            ivLimitLeft.setVisibility(View.VISIBLE);
                                            ivLimitRight.setVisibility(View.GONE);
                                            ivLimitDown.setVisibility(View.GONE);
                                            ivLimitUp.setVisibility(View.GONE);
                                        }else if(hor.equals("-1")){
                                            ivLimitLeft.setVisibility(View.GONE);
                                            ivLimitRight.setVisibility(View.VISIBLE);
                                            ivLimitDown.setVisibility(View.GONE);
                                            ivLimitUp.setVisibility(View.GONE);
                                        }else if(ver.equals("1")){
                                            ivLimitLeft.setVisibility(View.GONE);
                                            ivLimitRight.setVisibility(View.GONE);
                                            ivLimitDown.setVisibility(View.VISIBLE);
                                            ivLimitUp.setVisibility(View.GONE);
                                        }else if(ver.equals("-1")){
                                            ivLimitLeft.setVisibility(View.GONE);
                                            ivLimitRight.setVisibility(View.GONE);
                                            ivLimitDown.setVisibility(View.GONE);
                                            ivLimitUp.setVisibility(View.VISIBLE);
                                        }*/
                                    }
                                }
                            }else{
                                /*ivLimitLeft.setVisibility(View.GONE);
                                ivLimitRight.setVisibility(View.GONE);
                                ivLimitDown.setVisibility(View.GONE);
                                ivLimitUp.setVisibility(View.GONE);*/
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void initVideoPlayer(final MultiSampleVideo videoPlayer, int position) {
        String url = "rtsp://admin:hh123456@192.168.1.12:554/Streaming/Channels/101";
        videoPlayer.setPlayTag(TAG);
        videoPlayer.setPlayPosition(position);

        boolean isPlaying = videoPlayer.getCurrentPlayer().isInPlayingState();
        Log.e(TAG, "onBindViewHolder:isPlaying " + isPlaying);
        if (!isPlaying) {
            videoPlayer.setUpLazy(url, false, null, null, "这是title");
        }

        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        //设置旋转
        orientationUtils = new OrientationUtils(getActivity(), videoPlayer);

        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // resolveFullBtn(videoPlayer);
                videoPlayer.startWindowFullscreen(getContext(), false, true);
            }
        });
        videoPlayer.setRotateViewAuto(true);
        videoPlayer.setLockLand(true);
        videoPlayer.setReleaseWhenLossAudio(false);  //长时间失去音频焦点，暂停播放器
        videoPlayer.setVideoTypeErrorIsPlayer(true);  //视频播放格式出现错误 是否重新进行播放
        videoPlayer.setShowFullAnimation(true);
        videoPlayer.setIsTouchWiget(false);
        videoPlayer.setNeedLockFull(true);

        videoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            private String fullKey;

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                fullKey = "null";
                Log.e(TAG, "onQuitFullscreen: ");
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
                videoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                fullKey = videoPlayer.getKey();
                Log.e(TAG, "onEnterFullscreen: ");
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                Log.e(TAG, "onAutoComplete: ");
            }
        });

        videoPlayer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e(TAG, "onFocusChange: " + hasFocus );
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void bingView() {
        /**
         * 添加火情上报 跟隐患排查
         */
        RxViewAction.clickNoDouble(addImageView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        addDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(huoqingView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        addDialog.dismiss();
                        ARouter.getInstance().build(RouteUtils.FireAdd)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();

                    }
                });

        RxViewAction.clickNoDouble(yinhuanView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        addDialog.dismiss();
                        ARouter.getInstance().build(RouteUtils.HiddenDangerr)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });


        RxViewAction.clickNoDouble(listDialogImage)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        videoListDialog.dismiss();
                    }
                });
        yichuButton.setOnTouchListener(buttonListener);
        zuoshangButton.setOnTouchListener(buttonListener);
        shangButton.setOnTouchListener(buttonListener);
        youshangButton.setOnTouchListener(buttonListener);
        zuoButton.setOnTouchListener(buttonListener);
        xunhangButton.setOnTouchListener(buttonListener);
        youButton.setOnTouchListener(buttonListener);
        zuoxiaButton.setOnTouchListener(buttonListener);
        xiaButton.setOnTouchListener(buttonListener);
        youxiaButton.setOnTouchListener(buttonListener);
        lajinButton.setOnTouchListener(buttonListener);
        jiepingButton.setOnTouchListener(buttonListener);
        jujiaoButton.setOnTouchListener(buttonListener);
        layuanButton.setOnTouchListener(buttonListener);
        luxiangButton.setOnTouchListener(buttonListener);

        /**
         * 顶部listdialog弹出的点击事件
         */
        RxViewAction.clickNoDouble(listButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = false;
                        videoListDialog.show();
                    }
                });

        RxViewAction.clickNoDouble(yiView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        lastVideo = currentVideo;
                        if(lastVideo == 1){
                            return;
                        }
                        currentVideo = 1;
                        currentChooseVideo = 0;
                        initVideoChooseView();
                        yiView.setImageResource(R.drawable.ic_one_selected);
                        siView.setImageResource(R.drawable.ic_four);
                        jiuView.setImageResource(R.drawable.ic_nine);
                        initVideoView();
                    }
                });

        RxViewAction.clickNoDouble(siView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        lastVideo = currentVideo;
                        if(lastVideo == 4){
                            return;
                        }
                        currentVideo = 4;
                        currentChooseVideo = 0;
                        initVideoChooseView();
                        yiView.setImageResource(R.drawable.ic_one);
                        siView.setImageResource(R.drawable.ic_four_selected);
                        jiuView.setImageResource(R.drawable.ic_nine);
                        initVideoView();

                        tagVideo = currentVideo;
                    }
                });
        RxViewAction.clickNoDouble(jiuView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        lastVideo = currentVideo;
                        if(lastVideo == 9){
                            return;
                        }
                        currentVideo = 9;
                        currentChooseVideo = 0;
                        initVideoChooseView();
                        yiView.setImageResource(R.drawable.ic_one);
                        siView.setImageResource(R.drawable.ic_four);
                        jiuView.setImageResource(R.drawable.ic_nine_selected);
                        initVideoView();

                        tagVideo = currentVideo;
                    }
                });


        /**
         * 视频选中的9个点击
         */

        RxViewAction.clickNoDouble(video_click1_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 1;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click2_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 2;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click3_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 3;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click4_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 4;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click5_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 5;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click6_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 6;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click7_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 7;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click8_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 8;
                        initVideoChooseView();
                    }
                });
        RxViewAction.clickNoDouble(video_click9_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentChooseVideo = 9;
                        initVideoChooseView();
                    }
                });

        /**
         * 点击添加按钮添加视频
         */

        RxViewAction.clickNoDouble(video1AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });

        RxViewAction.clickNoDouble(video2AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });

        RxViewAction.clickNoDouble(video3AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });

        RxViewAction.clickNoDouble(video4AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });

        RxViewAction.clickNoDouble(video5AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(video6AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(video7AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });
        RxViewAction.clickNoDouble(video8AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });

        RxViewAction.clickNoDouble(video9AddView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAddVideoViewClick = true;
                        videoListDialog.show();
                    }
                });

    }


    private View.OnTouchListener buttonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            //1：上，2：下，3：左，4：右，5：左上，6：左下，7：右上，8：右下
            if (v.getId() == R.id.yichu_button) {
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    yichuButton.setBackgroundResource(R.drawable.ic_button_hover);
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    yichuButton.setBackgroundResource(R.drawable.ic_button);
                    yichuVideo();
                }
            } else if (v.getId() == R.id.zuoshang_button) {       //左上  5
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 7;
                    kktype = "upleft";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 7;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.shang_button) {      //上 1
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 3;
                    kktype = "up";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 3;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.youshang_button) {       //右上  7
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 9;
                    kktype = "upright";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 9;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.zuo_button) {        //左  3
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 1;
                    kktype = "left";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 1;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.xunhang_button) {
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    Toast.makeText(getContext(), "暂无控制权限", Toast.LENGTH_SHORT).show();
                } else if (action == MotionEvent.ACTION_UP) { // 松开

                }
            } else if (v.getId() == R.id.you_button) {        //右  4
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 2;
                    kktype = "right";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 2;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.zuoxia_button) {    //左下  6
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 8;
                    kktype = "downleft";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 8;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.xia_button) {        //下 2
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 4;
                    kktype = "down";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 4;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.youxia_button) {        //右下  8
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    moveType = 10;
                    kktype = "downright";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    moveType = 10;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            }  else if (v.getId() == R.id.jieping_button) {
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    File appDir = new File(Environment.getExternalStorageDirectory(), "firePrevention");
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    //图片文件名称
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String timeS = sdf.format(date);
                    String fileName = "监控截图_"+timeS + ".jpg";
                    File file = new File(appDir, fileName);
                    video1Player.saveFrame(file, true, new GSYVideoShotSaveListener() {
                        @Override
                        public void result(boolean success, File file) {
                            Toast.makeText(getActivity(), "截图已保存至"+ file.getPath(), Toast.LENGTH_LONG).show();
                        }
                    });

                    /*new Thread(){
                        @Override
                        public void run() {
                            Bitmap srcBitmap = Bitmap.createBitmap(1920,1080, Bitmap.Config.ARGB_8888);
                            //video1Player.getCustomManager().pause();
                            boolean currentFrame = video1Player.getCustomManager().getIJKMediaPlayer().getCurrentFrame(srcBitmap);
                            Log.e("TAG", "=======0===currentFrame====" + currentFrame);
                            //插入相册 解决了华为截图显示问题
                            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), srcBitmap, "", "");
                            //video1Player.getCustomManager().start();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "截图已保存", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }.start();*/
                } else if (action == MotionEvent.ACTION_UP) { // 松开

                }
            } else if (v.getId() == R.id.jujiao_button) {
                //TODO 对讲屏蔽
                /*if (action == MotionEvent.ACTION_DOWN) { // 按下
                    isTalking = !isTalking;
                    if(isTalking){
                        Toast.makeText(getContext(), "开始对讲", Toast.LENGTH_SHORT).show();
                        jujiaoButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_luxiang2),null,null);
                        jujiaoButton.setText("停止");
                        play();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openTalking();
                            }
                        },3000);
                    }else{
                        Toast.makeText(getContext(), "对讲已关闭", Toast.LENGTH_SHORT).show();
                        jujiaoButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_luxiang1),null,null);
                        jujiaoButton.setText("对讲");
                        stopTalking();
                        stop();
                    }
                } else if (action == MotionEvent.ACTION_UP) { // 松开

                }*/
            } else if (v.getId() == R.id.luxiang_button) {
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    isRecording = !isRecording;
                    if(isRecording){
                        luxiangButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_luxiang2),null,null);
                        luxiangButton.setText("停止");
                        Toast.makeText(getActivity(), "开始录像", Toast.LENGTH_SHORT).show();
                        file = new File(Environment.getExternalStorageDirectory(), "firePrevention");
                        Log.e(TAG, "===file.exists()===" + file.exists());
                        Log.e(TAG, "===file.mkdirs()===" + file.mkdirs());
                        Log.e(TAG, "===file.mkdirs()=file==" + file.getAbsolutePath());
                        if (!file.exists()) {
                            file.mkdirs();
                            Log.e(TAG, "===000===" + file.exists());
                        }
                        Log.e(TAG, "===111===" + file.exists());
                        String date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
                        String s = file.getAbsolutePath() + "/" + date + ".mp4";
                        File fileS = new File(s);
                        try {
                            boolean newFile = fileS.createNewFile();
                            Log.e(TAG, "===newFile===" + newFile);

                        } catch (IOException e) {
                            Log.e(TAG, "===newFile===" + e);
                            e.printStackTrace();
                        }
                        video1Player.getCustomManager().getIJKMediaPlayer().startRecord(fileS.getAbsolutePath());
                    }else{
                        luxiangButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_luxiang1),null,null);
                        luxiangButton.setText("录像");
                        video1Player.getCustomManager().getIJKMediaPlayer().stopRecord();
                        String path = "";
                        try{
                            path = file.getPath();
                        }catch (Exception e){
                            Log.e(TAG, "onTouch: " + e.getMessage() );
                        }
                        Toast.makeText(getActivity(), "录像已保存至"+ path +"文件夹", Toast.LENGTH_SHORT).show();
                    }


                } else if (action == MotionEvent.ACTION_UP) { // 松开

                }
            }
            if (v.getId() == R.id.lajin_button) {
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    steptype = 5;
                    kktype = "zoomin";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    steptype = 5;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            } else if (v.getId() == R.id.layuan_button) {
                if (action == MotionEvent.ACTION_DOWN) { // 按下
                    steptype = 6;
                    kktype = "zoomout";
                    isStop = false;
                    moveShexiangtou();
                } else if (action == MotionEvent.ACTION_UP) { // 松开
                    steptype = 6;
                    kktype = "stop";
                    isStop = true;
                    moveShexiangtou();
                }
            }
            return false;
        }
    };

    //开始对讲
    private void openTalking() {
        String playToken = null;
        int channelId = -1;//多通道通道号参数传入对应的通道号，单通道传-1
        try {
            JSONArray channelList = CommonData.deviceSub.getJSONArray("channels");
            Log.e(TAG, "play: channelList = " + channelList );
            JSONObject channel = (JSONObject) channelList.get(0);
            Log.e(TAG, "play: channel = " + channel );
            channelId = Integer.parseInt(channel.getString("channelId"));
        } catch (Exception e) {
            Log.e(TAG, "play: e" + e.toString() );
            e.printStackTrace();
        }
        /*try {
            playToken = CommonData.device.getString("playToken");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        openAudio();
        soundStatus = SoundStatus.STOP;
        speakStatus = SpeakStatus.OPENING;
        LCOpenSDK_Talk.setListener(audioTalkerListener);//对讲前先设备监听

        Logger.d(TAG, "playTalk, channelId = " + channelId);

        Log.e(TAG, "playTalk: talks" +
                CommonData.subToken +","+
                CommonData.subId +","+
                (-1) +","+//channelId
                CommonData.subId +","+//psk
                playToken +","+//playToken
                true +","+"talk"//i
        );
        LCOpenSDK_ParamTalk paramTalk = new LCOpenSDK_ParamTalk(
                CommonData.subToken,
                CommonData.subId,
                -1,
                CommonData.subId,//TextUtils.isEmpty(encryptKey) ? deviceListBean.deviceId : encryptKey,
                playToken,//playToken
                true,"talk"
        );
        LCOpenSDK_Talk.playTalk(paramTalk);
    }

    //释放对讲
    private void stopTalking() {
        LCOpenSDK_Talk.stopTalk();
        LCOpenSDK_Talk.setListener(null);//停止对讲后对讲监听置为空
    }

    // 截屏
    private void screenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        view.setBackgroundColor(getResources().getColor(R.color.white));
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setBackgroundColor(getResources().getColor(R.color.transparent));
        saveImageToGallery(getActivity(),bitmap);
    }
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片 创建文件夹
        File appDir = new File(Environment.getExternalStorageDirectory(), "hh_firePrevention");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //图片文件名称
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeS = sdf.format(date);
        String fileName = "监控截图_"+timeS + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context,"保存成功！",Toast.LENGTH_SHORT).show();
    }

    private void moveShexiangtou() {
        String id = "";
        String monitorId = "";
        String serial = "";
        boolean isHasVideo = false;
        for (int i = 0; i < videoIdList.size(); i++) {
            if (videoIdList.get(i).getVideoPlayer() == currentChooseVideo) {
                isHasVideo = true;
                id = videoIdList.get(i).getVideoId();
                monitorId = videoIdList.get(i).getMonitorId();
                serial = videoIdList.get(i).getSerial();
            }
        }
        if (!isHasVideo){
            Toast.makeText(getContext(), "当前控制器暂无摄像头", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("enumCode",11001);
            jsonObject.put("stop",isStop);
            if (steptype == 0){
                jsonObject.put("direction",moveType);   //转动
            }else {
                jsonObject.put("step",steptype);        //拉进拉远
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/guide/yunTaiReverse");
        params.setBodyContent(jsonObject.toString());
        params.setConnectTimeout(10000);
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType", "Internet");

        Log.e(TAG, "moveShexiangtou: " + params);
        Log.e(TAG, "moveShexiangtou: " + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "moveShexiangtou: onSuccess" + result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "moveShexiangtou: onError" + ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "moveShexiangtou: onCancelled" + cex.toString());

            }

            @Override
            public void onFinished() {
            }
        });
        steptype=0;

        //卡口控制
        RequestParams paramskk = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/liveVideo/controlGB");
        paramskk.addBodyParameter("deviceId",monitorId);
        paramskk.addBodyParameter("serial",serial);
        paramskk.addBodyParameter("speed", Objects.equals(kktype, "stop") ?"1":"5");
        paramskk.addBodyParameter("controlType",kktype);
        paramskk.setConnectTimeout(10000);
        paramskk.addHeader("Authorization","Bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType", "Internet");

        Log.e(TAG, "moveKk: " + paramskk);
        Log.e(TAG, "moveKk: " + new DbConfig(getContext()).getUser().getToken());
        x.http().get(paramskk, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "moveKk: onSuccess" + result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "moveKk: onError" + ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "moveKk: onCancelled" + cex.toString());

            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 移除当前选中视频
     */
    private void yichuVideo() {
        for (int i = 0; i < videoIdList.size(); i++) {
            if (videoIdList.get(i).getVideoPlayer()==currentChooseVideo) {
                videoIdList.remove(i);
                break;
            }
        }
        if (currentChooseVideo == 1) {
            //   video1Player.startPrepare();
            video1Player.onVideoReset();
            video1Player.setVisibility(View.GONE);
            video1AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 2) {
            video2Player.onVideoReset();
            video2Player.setVisibility(View.GONE);
            video2AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 3) {
            video3Player.onVideoReset();
            video3Player.setVisibility(View.GONE);
            video3AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 4) {
            video4Player.onVideoReset();
            video4Player.setVisibility(View.GONE);
            video4AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 5) {
            video5Player.onVideoReset();
            video5Player.setVisibility(View.GONE);
            video5AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 6) {
            video6Player.onVideoReset();
            video6Player.setVisibility(View.GONE);
            video6AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 7) {
            video7Player.onVideoReset();
            video7Player.setVisibility(View.GONE);
            video7AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 8) {
            video8Player.onVideoReset();
            video8Player.setVisibility(View.GONE);
            video8AddView.setVisibility(View.VISIBLE);
        } else if (currentChooseVideo == 9) {
            video9Player.onVideoReset();
            video9Player.setVisibility(View.GONE);
            video9AddView.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(getActivity(), "请选择要移除的视频", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击视频 选中视频带篮筐
     */
    private void initVideoChooseView() {
        video1_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video2_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video3_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video4_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video5_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video6_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video7_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video8_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video9_layout.setBackgroundResource(R.drawable.bg_video_choose_no);
        video_click1_layout.setVisibility(View.VISIBLE);
        video_click2_layout.setVisibility(View.VISIBLE);
        video_click3_layout.setVisibility(View.VISIBLE);
        video_click4_layout.setVisibility(View.VISIBLE);
        video_click5_layout.setVisibility(View.VISIBLE);
        video_click6_layout.setVisibility(View.VISIBLE);
        video_click7_layout.setVisibility(View.VISIBLE);
        video_click8_layout.setVisibility(View.VISIBLE);
        video_click9_layout.setVisibility(View.VISIBLE);
        if (currentChooseVideo == 1) {
            video1_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click1_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 2) {
            video2_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click2_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 3) {
            video3_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click3_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 4) {
            video4_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click4_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 5) {
            video5_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click5_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 6) {
            video6_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click6_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 7) {
            video7_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click7_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 8) {
            video8_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click8_layout.setVisibility(View.GONE);
        } else if (currentChooseVideo == 9) {
            video9_layout.setBackgroundResource(R.drawable.bg_video_choose);
            video_click9_layout.setVisibility(View.GONE);
        }
    }

    private void initVideoView() {
        if (currentVideo == 1) {
            Log.e(TAG, "initVideoView: 1");
            yiLayout.setVisibility(View.VISIBLE);
            siLayout.setVisibility(View.GONE);
            jiuLayout.setVisibility(View.GONE);
            video1_layout.setVisibility(View.VISIBLE);
            video2_layout.setVisibility(View.GONE);
            video3_layout.setVisibility(View.GONE);
            video4_layout.setVisibility(View.GONE);
            video5_layout.setVisibility(View.GONE);
            video6_layout.setVisibility(View.GONE);
            video7_layout.setVisibility(View.GONE);
            video8_layout.setVisibility(View.GONE);
            video9_layout.setVisibility(View.GONE);
            /*closeVideoPlayer(video2Player);//TODO 9切4切1&4切1遮盖视频不移除
            closeVideoPlayer(video3Player);
            closeVideoPlayer(video4Player);
            closeVideoPlayer(video5Player);
            closeVideoPlayer(video6Player);
            closeVideoPlayer(video7Player);
            closeVideoPlayer(video8Player);
            closeVideoPlayer(video9Player);
            video2AddView.setVisibility(View.VISIBLE);
            video3AddView.setVisibility(View.VISIBLE);
            video4AddView.setVisibility(View.VISIBLE);
            video5AddView.setVisibility(View.VISIBLE);
            video6AddView.setVisibility(View.VISIBLE);
            video7AddView.setVisibility(View.VISIBLE);
            video8AddView.setVisibility(View.VISIBLE);
            video9AddView.setVisibility(View.VISIBLE);*/
            Log.e(TAG, "initVideoView: 1" + videoIdList );
            Log.e(TAG, "initVideoView: debug 1" + tagVideo + "," + currentVideo );
        } else if (currentVideo == 4) {
            Log.e(TAG, "initVideoView: 4");
            yiLayout.setVisibility(View.VISIBLE);
            siLayout.setVisibility(View.VISIBLE);
            jiuLayout.setVisibility(View.GONE);
            video1_layout.setVisibility(View.VISIBLE);
            video2_layout.setVisibility(View.VISIBLE);
            video3_layout.setVisibility(View.GONE);
            video4_layout.setVisibility(View.VISIBLE);
            video5_layout.setVisibility(View.VISIBLE);
            video6_layout.setVisibility(View.GONE);
            video7_layout.setVisibility(View.GONE);
            video8_layout.setVisibility(View.GONE);
            video9_layout.setVisibility(View.GONE);
            /*closeVideoPlayer(video3Player);//TODO 9切4切1&4切1遮盖视频不移除
            closeVideoPlayer(video6Player);
            closeVideoPlayer(video7Player);
            closeVideoPlayer(video8Player);
            closeVideoPlayer(video9Player);
            video3AddView.setVisibility(View.VISIBLE);
            video6AddView.setVisibility(View.VISIBLE);
            video7AddView.setVisibility(View.VISIBLE);
            video8AddView.setVisibility(View.VISIBLE);
            video9AddView.setVisibility(View.VISIBLE);*/
            Log.e(TAG, "initVideoView: debug 4" + tagVideo + "," + currentVideo );
            if(tagVideo == 4){
                return;
            }
            //TODO 9->4  3-4  4-5
            List<VideoId> vList = new ArrayList<>();
            int num3 = 0;
            int num4 = 0;
            for (int i = 0; i < videoIdList.size(); i++) {
                VideoId videoId = videoIdList.get(i);
                if(videoId.getVideoPlayer() == 3 && num3==0){
                    num3++;
                    videoId.setVideoPlayer(4);
                }else if(videoId.getVideoPlayer() == 4 && num4==0){
                    num4++;
                    videoId.setVideoPlayer(5);
                }
                vList.add(videoId);
            }
            videoIdList = new ArrayList<>(vList);
            Log.e(TAG, "initVideoView: 4" + videoIdList );
            initReViewChanged();
        } else if (currentVideo == 9) {
            Log.e(TAG, "initVideoView: 9");
            yiLayout.setVisibility(View.VISIBLE);
            siLayout.setVisibility(View.VISIBLE);
            jiuLayout.setVisibility(View.VISIBLE);
            video1_layout.setVisibility(View.VISIBLE);
            video2_layout.setVisibility(View.VISIBLE);
            video3_layout.setVisibility(View.VISIBLE);
            video4_layout.setVisibility(View.VISIBLE);
            video5_layout.setVisibility(View.VISIBLE);
            video6_layout.setVisibility(View.VISIBLE);
            video7_layout.setVisibility(View.VISIBLE);
            video8_layout.setVisibility(View.VISIBLE);
            video9_layout.setVisibility(View.VISIBLE);
            Log.e(TAG, "initVideoView: debug 9s" + tagVideo + "," + currentVideo );
            if(tagVideo == 9){
                return;
            }
            //TODO 4->9  4-3  5-4
            List<VideoId> vList = new ArrayList<>();
            int num4 = 0;
            int num5 = 0;
            for (int i = 0; i < videoIdList.size(); i++) {
                VideoId videoId = videoIdList.get(i);
                if(videoId.getVideoPlayer() == 4 && num4==0){
                    num4++;
                    videoId.setVideoPlayer(3);
                } else if(videoId.getVideoPlayer() == 5 && num5==0){
                    num5++;
                    videoId.setVideoPlayer(4);
                }
                vList.add(videoId);
            }
            videoIdList = new ArrayList<>(vList);
            Log.e(TAG, "initVideoView: 9" + videoIdList );
            initReViewChanged();
        }

    }

    private void initReViewChanged() {
        video1Player.setVisibility(View.GONE);
        video1AddView.setVisibility(View.VISIBLE);
        video2Player.setVisibility(View.GONE);
        video2AddView.setVisibility(View.VISIBLE);
        video3Player.setVisibility(View.GONE);
        video3AddView.setVisibility(View.VISIBLE);
        video4Player.setVisibility(View.GONE);
        video4AddView.setVisibility(View.VISIBLE);
        video5Player.setVisibility(View.GONE);
        video5AddView.setVisibility(View.VISIBLE);
        video6Player.setVisibility(View.GONE);
        video6AddView.setVisibility(View.VISIBLE);
        video7Player.setVisibility(View.GONE);
        video7AddView.setVisibility(View.VISIBLE);
        video8Player.setVisibility(View.GONE);
        video8AddView.setVisibility(View.VISIBLE);
        int num1 = 0;
        int num2 = 0;
        int num3 = 0;
        int num4 = 0;
        int num5 = 0;
        int num6 = 0;
        int num7 = 0;
        int num8 = 0;
        for (int i = 0; i < videoIdList.size(); i++) {
            VideoId videoId = videoIdList.get(i);
            if(videoId.getVideoPlayer() == 1 && num1==0){
                num1++;
                video1Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video1Player.startButtonLogic();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 2 && num2==0){
                num2++;
                video2Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video2Player.startButtonLogic();
                video2Player.setVisibility(View.VISIBLE);
                video2AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 3 && num3==0){
                num3++;
                video3Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video3Player.startButtonLogic();
                video3Player.setVisibility(View.VISIBLE);
                video3AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 4 && num4==0){
                num4++;
                video4Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video4Player.startButtonLogic();
                video4Player.setVisibility(View.VISIBLE);
                video4AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 5 && num5==0){
                num5++;
                video5Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video5Player.startButtonLogic();
                video5Player.setVisibility(View.VISIBLE);
                video5AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 6 && num6==0){
                num6++;
                video6Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video6Player.startButtonLogic();
                video6Player.setVisibility(View.VISIBLE);
                video6AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 7 && num7==0){
                num7++;
                video7Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video7Player.startButtonLogic();
                video7Player.setVisibility(View.VISIBLE);
                video7AddView.setVisibility(View.GONE);
            }
            if(videoId.getVideoPlayer() == 8 && num8==0){
                num8++;
                video8Player.setUpLazy(videoId.getUrl(), false, null, null, videoId.getVideoName());
                video8Player.startButtonLogic();
                video8Player.setVisibility(View.VISIBLE);
                video8AddView.setVisibility(View.GONE);
            }
        }
    }

    private void closeVideoPlayer(MultiSampleVideo videoPlayer) {
        videoPlayer.setVisibility(View.GONE);
        videoPlayer.onVideoReset();


    }

    /**
     * 获取视频状态
     */
    private void getVideoPlayerState() {
        currentVideo1PlayerState = video1Player.getCurrentState();
        currentVideo2PlayerState = video2Player.getCurrentState();
        currentVideo3PlayerState = video3Player.getCurrentState();
        currentVideo4PlayerState = video4Player.getCurrentState();
        currentVideo5PlayerState = video5Player.getCurrentState();
        currentVideo6PlayerState = video6Player.getCurrentState();
        currentVideo7PlayerState = video7Player.getCurrentState();
        currentVideo8PlayerState = video8Player.getCurrentState();
        currentVideo9PlayerState = video9Player.getCurrentState();
    }

    @Override
    public void onResume() {
        super.onResume();
        getVideoPlayerState();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK){
                    if (video1Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示

                        video1Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video1Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video1Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video2Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video2Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video3Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video3Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video4Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video4Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video5Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video5Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video6Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video6Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video7Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video7Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video8Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video8Player.clearFullscreenLayout();
                        return true;
                    }
                    if (video9Player.isIfCurrentIsFullscreen()){        //判断是否是全屏显示
                        video9Player.clearFullscreenLayout();
                        return true;
                    }

                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        getVideoPlayerState();
        videoPause();
    }

    /**
     * 视频不在前台的时候播放中的视频暂停
     */
    private void videoPause() {
        if (currentVideo1PlayerState == CURRENT_STATE_PLAYING) {
            video1Player.onVideoPause();
        }
        if (currentVideo2PlayerState == CURRENT_STATE_PLAYING) {
            video2Player.onVideoPause();
        }
        if (currentVideo3PlayerState == CURRENT_STATE_PLAYING) {
            video3Player.onVideoPause();
        }
        if (currentVideo4PlayerState == CURRENT_STATE_PLAYING) {
            video4Player.onVideoPause();
        }
        if (currentVideo5PlayerState == CURRENT_STATE_PLAYING) {
            video5Player.onVideoPause();
        }
        if (currentVideo6PlayerState == CURRENT_STATE_PLAYING) {
            video6Player.onVideoPause();
        }
        if (currentVideo7PlayerState == CURRENT_STATE_PLAYING) {
            video7Player.onVideoPause();
        }
        if (currentVideo8PlayerState == CURRENT_STATE_PLAYING) {
            video8Player.onVideoPause();
        }
        if (currentVideo9PlayerState == CURRENT_STATE_PLAYING) {
            video9Player.onVideoPause();
        }
    }


    ///视频流暂停bus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(VideoPause videoPause) {
        if (user.getIsLogin() == 1) {
            Log.e(TAG, "onPause: bus " );
            getVideoPlayerState();
            videoPause();
        }
    }

    ///视频流恢复bus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(VideoStart videoSTart) {
        if (user.getIsLogin() == 1) {
            Log.e(TAG, "onStart: bus " );
            getVideoPlayerState();
            videoStart();
        }
    }

    private void videoStart() {
        video1Player.startButtonLogic();
        video2Player.startButtonLogic();
        video3Player.startButtonLogic();
        video4Player.startButtonLogic();
        video5Player.startButtonLogic();
        video6Player.startButtonLogic();
        video7Player.startButtonLogic();
        video8Player.startButtonLogic();
        video9Player.startButtonLogic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(changeTabReceiver);
        EventBus.getDefault().unregister(this);
    }


    public enum PlayStatus {
        PLAY, PAUSE, ERROR
    }
    public enum SoundStatus {
        PLAY, STOP, NO_SUPPORT
    }

    public enum SpeakStatus {
        PLAY, STOP, NO_SUPPORT,OPENING
    }

    class AudioTalkerListener extends LCOpenSDK_TalkerListener {
        public AudioTalkerListener() {
            super();
        }

        @Override
        public void onTalkResult(String error, int type) {
            super.onTalkResult(error, type);
            Log.e(TAG, "onTalkResult: " + error + "," + type );
            Log.e(TAG, "onTalkResult: params" + CommonData.subAccount + "," + CommonData.subOpenid + "," + CommonData.subToken );
            boolean talkResult = false;
            if (type == 99 || error.equals("-1000") || error.equals("0") || error.equals("1") || error.equals("3")) {
                talkResult = false;
            } else if (error.equals("4")) {
                talkResult = true;
            }
            final boolean finalTalkResult = talkResult;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!finalTalkResult) {
                        stopTalking();
                        // 提示对讲打开失败
                        Toast.makeText(getActivity(), "开启对讲失败", Toast.LENGTH_SHORT).show();
                        speakStatus = SpeakStatus.STOP;
                    } else {
                        // 提示对讲打开成功
                        Toast.makeText(getActivity(), "开启对讲成功", Toast.LENGTH_SHORT).show();
                        speakStatus = SpeakStatus.PLAY;

                    }
                }
            });
        }

        @Override
        public void onTalkPlayReady() {
            super.onTalkPlayReady();
        }

        @Override
        public void onAudioRecord(byte[] bytes, int i, int i1, int i2, int i3) {
            super.onAudioRecord(bytes, i, i1, i2, i3);
        }

        @Override
        public void onAudioReceive(byte[] bytes, int i, int i1, int i2, int i3) {
            super.onAudioReceive(bytes, i, i1, i2, i3);
        }

        @Override
        public void onDataLength(int i) {
            super.onDataLength(i);
        }
    }
}