package com.haohai.platform.platformmodel.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.google.gson.reflect.TypeToken;
import com.haohai.ledge.videolibrary.utils.OrientationUtils;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.fragment.base.HhBaseFragment;
import com.haohai.platform.platformmodel.ui.model.GridCamera;
import com.haohai.platform.platformmodel.ui.model.GridModel;
import com.haohai.platform.platformmodel.ui.model.GridPointModel;
import com.haohai.platform.platformmodel.ui.model.GridTrees;
import com.haohai.platform.platformmodel.ui.model.Organization;
import com.haohai.platform.platformmodel.ui.model.PostStar;
import com.haohai.platform.platformmodel.ui.model.VideoId;
import com.haohai.platform.platformmodel.ui.model.VideoModel;
import com.haohai.platform.platformmodel.ui.utils.tree.TreeAdapter;
import com.haohai.platform.platformmodel.ui.utils.tree.TreePoint;
import com.haohai.platform.platformmodel.ui.utils.tree.TreeUtils;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.utils.CommonUtils;
import com.ywl5320.wlmedia.WlMedia;
import com.ywl5320.wlmedia.enums.WlComplete;
import com.ywl5320.wlmedia.listener.WlOnMediaInfoListener;
import com.ywl5320.wlmedia.listener.WlOnVideoViewListener;
import com.ywl5320.wlmedia.surface.WlSurfaceView;
import com.ywl5320.wlmedia.widget.WlCircleLoadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import rx.functions.Action1;

public class VideoIOSFragment extends HhBaseFragment implements TreeAdapter.OnPlayerItemClick {
    private static final String TAG = VideoIOSFragment.class.getSimpleName();
    public int currentVideo = 1;
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
    private WlSurfaceView video1Player;
    private WlSurfaceView video2Player;
    private WlSurfaceView video3Player;
    private WlSurfaceView video4Player;
    private WlSurfaceView video5Player;
    private WlSurfaceView video6Player;
    private WlSurfaceView video7Player;
    private WlSurfaceView video8Player;
    private WlSurfaceView video9Player;
    private Dialog videoListDialog;
    private View videoListInflater;
    private List<TreePoint> pointList = new ArrayList<>();
    private HashMap<String, TreePoint> pointMap = new HashMap<>();
    private TreeAdapter adapter;
    private String token;
    private String aqishiToken;
    private ProgressDialog progressDialog;

    private List<Organization> organizationList;
    private List<VideoModel> videoModelList;
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
    private int currentChooseVideo;
    private boolean currentVideo1PlayerState = false;
    private boolean currentVideo2PlayerState = false;
    private boolean currentVideo3PlayerState = false;
    private boolean currentVideo4PlayerState = false;
    private boolean currentVideo5PlayerState = false;
    private boolean currentVideo6PlayerState = false;
    private boolean currentVideo7PlayerState = false;
    private boolean currentVideo8PlayerState = false;
    private boolean currentVideo9PlayerState = false;
    private LinearLayout yichuLayout;
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
    private ScrollView sv_gridtrees;
    private LinearLayout ll_left;
    private LinearLayout ll_right;
    private View v_left;
    private View v_right;
    private TextView tv_left;
    private TextView tv_right;
    private OrientationUtils orientationUtils;
    private String currentClickItemName;
    private String currentClickItemId;
    private String currentClickMonitorId;
    private String currentChannelId;
    private ImageView addImageView;

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
    private ChangeTabReceiver changeTabReceiver;
    private User user;
    private List<VideoId> videoIdList;
    private WlMedia wlMedia1;
    private WlMedia wlMedia2;
    private WlMedia wlMedia3;
    private WlMedia wlMedia4;
    private WlMedia wlMedia5;
    private WlMedia wlMedia6;
    private WlMedia wlMedia7;
    private WlMedia wlMedia8;
    private WlMedia wlMedia9;
    private int moveType = 0;//1：上，2：下，3：左，4：右，5：左上，6：左下，7：右上，8：右下
    private int steptype = 0;
    private boolean isStop = false; //true false
    private WlCircleLoadView wlCircleLoadView;
    private WlCircleLoadView wlCircleLoadView2;
    private WlCircleLoadView wlCircleLoadView3;
    private WlCircleLoadView wlCircleLoadView4;
    private WlCircleLoadView wlCircleLoadView5;
    private WlCircleLoadView wlCircleLoadView6;
    private WlCircleLoadView wlCircleLoadView7;
    private WlCircleLoadView wlCircleLoadView8;
    private WlCircleLoadView wlCircleLoadView9;

    private boolean rootCtrlDirection = false;
    private boolean rootCtrlFocus= false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_ios, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        organizationList = new ArrayList<>();
        videoModelList = new ArrayList<>();
        videoIdList = new ArrayList<>();
        if(CommonUtils.hasPermission(getActivity(),"app-video-btn-directionControl")){
            rootCtrlDirection = true;
        }
        if(CommonUtils.hasPermission(getActivity(),"app-video-btn-zoomControl")){
            rootCtrlFocus = true;
        }

        intiView();
        user = new DbConfig(getContext()).getUser();
        token =  user.getToken();
        getTokenFromService();
        getDataFromService();
        changeTabReceiver=new ChangeTabReceiver();
        IntentFilter resourcefilter = new IntentFilter();
        resourcefilter.addAction("video_play");
        getActivity().registerReceiver(changeTabReceiver, resourcefilter);
        bingView();

    //    video1Player.setUpLazy("http://121.36.6.140:80/group1/M00/00/02/wKgAzF-ZLKKEL2wIAAAAADPd4yE796.mp4", false, null, null, "11");
    }

    /**
     * 获取视频树
     */
    List<GridTrees> gridTreesList = new ArrayList<>();
    private void getDataFromService() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL +"resource/api/grid/listGridTreesNew");
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet

        params.setConnectTimeout(10000);
        Log.e(TAG, "listGridNewTrees: ---" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo gridNew" + result );
                sv_gridtrees.removeAllViews();
                gridTreesList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            gridTreesList.add(new Gson().fromJson(data.get(i).toString(),GridTrees.class));
                        }

                        sv_gridtrees.addView(buildGridTrees(gridTreesList));

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

    private void getDataFromServiceStar() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL +"resource/api/grid/listGridTreesNew");
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        params.addParameter("sheetType","collection");
        params.setConnectTimeout(10000);
        Log.e(TAG, "listGridNewTrees: ---" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo gridNew" + result );
                sv_gridtrees.removeAllViews();
                gridTreesList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            gridTreesList.add(new Gson().fromJson(data.get(i).toString(),GridTrees.class));
                        }

                        sv_gridtrees.addView(buildGridTrees(gridTreesList));

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
    View buildGridTrees(List<GridTrees> treesList){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees_out,null);
        LinearLayout ll_big = view.findViewById(R.id.ll_big);
        for (int i = 0; i < treesList.size(); i++) {
            GridTrees gridTrees = treesList.get(i);
            View item = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees,null);
            LinearLayout ll_out = item.findViewById(R.id.ll_out);
            LinearLayout ll_in = item.findViewById(R.id.ll_in);//用于监控点-摄像头便于动态加载
            ImageView iv_status = item.findViewById(R.id.iv_status);
            TextView tv_gridtrees = item.findViewById(R.id.tv_gridtrees);
            tv_gridtrees.setText(gridTrees.getName());
            //有Children子项(递归展示)
            if(gridTrees.getChildren()!=null && gridTrees.getChildren().size()>0){
                List<GridTrees> itemList = new ArrayList<>();
                for (int m = 0; m < gridTrees.getChildren().size(); m++) {
                        itemList.add(gridTrees.getChildren().get(m));
                }
                //递归
                View childTrees = buildGridTrees(itemList);
                //初始化绑定
                if(gridTrees.isStatus()){
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                    childTrees.setVisibility(View.VISIBLE);
                }else{
                    iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                    childTrees.setVisibility(View.GONE);
                }
                RxViewAction.clickNoDouble(ll_out).subscribe(unused -> {
                    if(gridTrees.isStatus()){
                        gridTrees.setStatus(false);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                        childTrees.setVisibility(View.GONE);

                        //隐藏/移除新View 91
                        ll_in.setVisibility(View.GONE);
                        ll_in.removeAllViews();
                    }else{
                        gridTrees.setStatus(true);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                        childTrees.setVisibility(View.VISIBLE);


                        //监控点-摄像头数据 91
                        if(gridTrees.getMonitorDetailVOs()!=null && gridTrees.getMonitorDetailVOs().size()>0){
                            View monitorView = buildMonitor(gridTrees.getMonitorDetailVOs(),gridTrees.getGroupId());
                            ll_in.addView(monitorView);
                            ll_in.setVisibility(View.VISIBLE);
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
                RxViewAction.clickNoDouble(ll_out).subscribe(unused -> {

                    if(gridTrees.isStatus()){
                        gridTrees.setStatus(false);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
                        //隐藏/移除新View 91
                        ll_in.setVisibility(View.GONE);
                        ll_in.removeAllViews();
                    }else{
                        gridTrees.setStatus(true);
                        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
                        //展示/添加新View

                        //监控点-摄像头数据
                        if(gridTrees.getMonitorDetailVOs()!=null && gridTrees.getMonitorDetailVOs().size()>0){
                            View monitorView = buildMonitor(gridTrees.getMonitorDetailVOs(),gridTrees.getGroupId());
                            ll_in.addView(monitorView);
                            ll_in.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }


            ll_big.addView(item);
        }
        return ll_big;
    }

    private String currentGroupId = "";
    /**
     * 构建监控点子View
     */
    View buildMonitor(List<GridModel> gridPointModelList,String groupId){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees_out,null);
        LinearLayout ll_big = view.findViewById(R.id.ll_big);//取一个LinearLayout
        for (int i = 0; i < gridPointModelList.size(); i++) {
            GridModel model = gridPointModelList.get(i);
            View item = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridtrees,null);
            LinearLayout ll_out = item.findViewById(R.id.ll_out);
            LinearLayout ll_in = item.findViewById(R.id.ll_in);
            ImageView iv_status = item.findViewById(R.id.iv_status);
            TextView tv_gridtrees = item.findViewById(R.id.tv_gridtrees);
            tv_gridtrees.setText(model.getMonitor().getName());
            if(model.isStatus()){
                iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_open));
            }else{
                iv_status.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
            }
            RxViewAction.clickNoDouble(ll_out).subscribe(unused -> {
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
                        GridCamera gridCamera = model.getCameraList().get(m);
                        //构建摄像头
                        View cameraView = LayoutInflater.from(getActivity()).inflate(R.layout.item_gridcamera,null);
                        LinearLayout ll_camera = cameraView.findViewById(R.id.ll_camera);
                        ImageView iv_star = cameraView.findViewById(R.id.iv_star);
                        ImageView iv_camera = cameraView.findViewById(R.id.iv_camera);//isOnLine 0 离线 1 在线
                        TextView tv_camera = cameraView.findViewById(R.id.tv_camera);
                        tv_camera.setText(gridCamera.getName());
                        gridCamera.setStatus(Objects.equals(gridCamera.getCollectionState(), "1"));
                        if(gridCamera.isStatus()){
                            iv_star.setImageDrawable(getResources().getDrawable(R.drawable.star_sl));
                        }else{
                            iv_star.setImageDrawable(getResources().getDrawable(R.drawable.star_un));
                        }
                        RxViewAction.clickNoDouble(ll_camera).subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void unused) {
                                currentGroupId = parseGroupId(groupId);

                                RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/mediaKit/getLiveUrl");
                                params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
                                params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
                                params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
                                Log.e(TAG, "call: gridCamera = " + gridCamera );
                                params.addBodyParameter("cameraId",gridCamera.getId());
                                params.addBodyParameter("manufacturer","2");
                                params.addBodyParameter("streamType","2");
                                params.addBodyParameter("protocolType","rtsp");
                                Log.e(TAG, "onSuccess: params = " + params );
                                x.http().get(params, new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            Log.e(TAG, "onSuccess: result = " + result );
                                            JSONObject object = new JSONObject(result);
                                            JSONArray data = object.getJSONArray("data");
                                            JSONObject obj = (JSONObject) data.get(0);
                                            String url = obj.getString("url");


                                            currentClickItemName = gridCamera.getName();
                                            currentClickItemId = gridCamera.getMonitorId();
                                            currentChannelId = gridCamera.getId();
                                            //跳转播放视频流
                                            videoListDialog.dismiss();
                                            Log.e(TAG, "call: gridCamera.getRtspUrl() = " + gridCamera.getRtspUrl() );
                                            if (isAddVideoViewClick) {//直接往点击的视频播放机中添加
                                                addVideoPlayer(url);
                                            } else {//往列表中排序添加
                                                getVideoPlayerState();
                                                setVideoPlayer(url);
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
                        });
                        RxViewAction.clickNoDouble(iv_star).subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void unused) {
                                if(gridCamera.isStatus()){
                                    gridCamera.setStatus(false);
                                    if(gridCamera.isStatus()){
                                        iv_star.setImageDrawable(getResources().getDrawable(R.drawable.star_sl));
                                    }else{
                                        iv_star.setImageDrawable(getResources().getDrawable(R.drawable.star_un));
                                    }
                                    postStar(false,new PostStar(gridCamera.getId(),gridCamera.getName(),null,gridCamera.getMonitorId()));
                                }else{
                                    gridCamera.setStatus(true);
                                    if(gridCamera.isStatus()){
                                        iv_star.setImageDrawable(getResources().getDrawable(R.drawable.star_sl));
                                    }else{
                                        iv_star.setImageDrawable(getResources().getDrawable(R.drawable.star_un));
                                    }
                                    postStar(true,new PostStar(gridCamera.getId(),gridCamera.getName(),null,gridCamera.getMonitorId()));
                                }
                            }
                        });

                        ll_in.addView(cameraView);
                    }
                    
                    ll_in.setVisibility(View.VISIBLE);
                }
            });
            ll_big.addView(item);
        }

        return ll_big;
    }

    private void postStar(boolean selected, PostStar postStar) {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL +"resource/api/cameraCollection");
        params.addHeader("Authorization","bearer " + new DbConfig(getActivity()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        params.setBodyContent(new Gson().toJson(postStar));
        Log.e(TAG, "onSuccess: star" + params );
        Log.e(TAG, "onSuccess: star" + new Gson().toJson(postStar) );
        x.http().request(selected ? HttpMethod.POST : HttpMethod.DELETE, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: star" + result );
                Toast.makeText(getActivity(), selected?"收藏成功":"取消成功", Toast.LENGTH_SHORT).show();
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

    private void postPlayerUrl(String ids) {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/mediaKit/getLiveUrl");
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        params.addBodyParameter("cameraId",ids);
        params.addBodyParameter("manufacturer","2");
        params.addBodyParameter("streamType","2");
        params.addBodyParameter("protocolType","rtsp");
        Log.e(TAG, "postPlayerUrl: params = " + params.toString() );
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

    void getGridTreesChildren(String gridId){
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL +"resource/api/monitor/getMonitorDetaisByGridAndType");
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        params.addBodyParameter("gridId",gridId);
        params.addBodyParameter("monitorType","1");

        params.setConnectTimeout(10000);
        Log.e(TAG, "listGridNewTrees: ---" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: bingo gridNew" + result );
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")) {

                        Intent it = new Intent();
                        it.setAction(gridId);
                        it.putExtra("result",result);
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
        video1Player = (  getView().findViewById(R.id.video1_player));
        video2Player = (  getView().findViewById(R.id.video2_player));
        video3Player = (  getView().findViewById(R.id.video3_player));
        video4Player = (  getView().findViewById(R.id.video4_player));
        video5Player = (  getView().findViewById(R.id.video5_player));
        video6Player = (  getView().findViewById(R.id.video6_player));
        video7Player = (  getView().findViewById(R.id.video7_player));
        video8Player = (  getView().findViewById(R.id.video8_player));
        video9Player = (  getView().findViewById(R.id.video9_player));
        video1AddView = (ImageView) getView().findViewById(R.id.video1_add_view);
        video2AddView = (ImageView) getView().findViewById(R.id.video2_add_view);
        video3AddView = (ImageView) getView().findViewById(R.id.video3_add_view);
        video4AddView = (ImageView) getView().findViewById(R.id.video4_add_view);
        video5AddView = (ImageView) getView().findViewById(R.id.video5_add_view);
        video6AddView = (ImageView) getView().findViewById(R.id.video6_add_view);
        video7AddView = (ImageView) getView().findViewById(R.id.video7_add_view);
        video8AddView = (ImageView) getView().findViewById(R.id.video8_add_view);
        video9AddView = (ImageView) getView().findViewById(R.id.video9_add_view);
        wlCircleLoadView = getView().findViewById(R.id.circleview);
        wlCircleLoadView2 = getView().findViewById(R.id.circleview2);
        wlCircleLoadView3 = getView().findViewById(R.id.circleview3);
        wlCircleLoadView4 = getView().findViewById(R.id.circleview4);
        wlCircleLoadView5 = getView().findViewById(R.id.circleview5);
        wlCircleLoadView6 = getView().findViewById(R.id.circleview6);
        wlCircleLoadView7 = getView().findViewById(R.id.circleview7);
        wlCircleLoadView8 = getView().findViewById(R.id.circleview8);
        wlCircleLoadView9 = getView().findViewById(R.id.circleview9);
        wlCircleLoadView.setVisibility(View.GONE);
        wlCircleLoadView2.setVisibility(View.GONE);
        wlCircleLoadView3.setVisibility(View.GONE);
        wlCircleLoadView4.setVisibility(View.GONE);
        wlCircleLoadView5.setVisibility(View.GONE);
        wlCircleLoadView6.setVisibility(View.GONE);
        wlCircleLoadView7.setVisibility(View.GONE);
        wlCircleLoadView8.setVisibility(View.GONE);
        wlCircleLoadView9.setVisibility(View.GONE);
        initVideoPlayer();

        yiView = ((ImageView) getView().findViewById(R.id.yi_view));
        siView = ((ImageView) getView().findViewById(R.id.si_view));
        jiuView = ((ImageView) getView().findViewById(R.id.jiu_view));

        /**
         * 视频列表dialog
         */
        videoListDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyleLeft);
        videoListInflater = LayoutInflater.from(getContext()).inflate(R.layout.dialog_video_list_ios, null);
        videoListInflater.setMinimumWidth(100000);
        sv_gridtrees = ((ScrollView) videoListInflater.findViewById(R.id.sv_gridtrees));
        ll_left = ((LinearLayout) videoListInflater.findViewById(R.id.ll_left));
        ll_right = ((LinearLayout) videoListInflater.findViewById(R.id.ll_right));
        tv_left = ((TextView) videoListInflater.findViewById(R.id.tv_left));
        tv_right = ((TextView) videoListInflater.findViewById(R.id.tv_right));
        v_left = ((View) videoListInflater.findViewById(R.id.v_left));
        v_right = ((View) videoListInflater.findViewById(R.id.v_right));
        RxViewAction.clickNoDouble(ll_left).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                tabLeft();
            }
        });
        RxViewAction.clickNoDouble(ll_right).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                tabRight();
            }
        });

        videoListLayout = ((LinearLayout) videoListInflater.findViewById(R.id.video_list_layout));
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
    }

    private void initVideoPlayer() {

        wlMedia1 = new WlMedia();
        wlMedia1.setFFOptions("rtsp_transport", "tcp");
        wlMedia1.setFFOptions("fflags", "nobuffer");
        video1Player.setWlMedia(wlMedia1);
        wlMedia2 = new WlMedia();
        wlMedia2.setFFOptions("rtsp_transport", "tcp");
        wlMedia2.setFFOptions("fflags", "nobuffer");
        video2Player.setWlMedia(wlMedia2);
        wlMedia3 = new WlMedia();
        wlMedia3.setFFOptions("rtsp_transport", "tcp");
        wlMedia3.setFFOptions("fflags", "nobuffer");
        video3Player.setWlMedia(wlMedia3);
        wlMedia4 = new WlMedia();
        wlMedia4.setFFOptions("rtsp_transport", "tcp");
        wlMedia4.setFFOptions("fflags", "nobuffer");
        video4Player.setWlMedia(wlMedia4);
        wlMedia5 = new WlMedia();
        wlMedia5.setFFOptions("rtsp_transport", "tcp");
        wlMedia5.setFFOptions("fflags", "nobuffer");
        video5Player.setWlMedia(wlMedia5);
        wlMedia6 = new WlMedia();
        wlMedia6.setFFOptions("rtsp_transport", "tcp");
        wlMedia6.setFFOptions("fflags", "nobuffer");
        video6Player.setWlMedia(wlMedia6);
        wlMedia7 = new WlMedia();
        wlMedia7.setFFOptions("rtsp_transport", "tcp");
        wlMedia7.setFFOptions("fflags", "nobuffer");
        video7Player.setWlMedia(wlMedia7);
        wlMedia8 = new WlMedia();
        wlMedia8.setFFOptions("rtsp_transport", "tcp");
        wlMedia8.setFFOptions("fflags", "nobuffer");
        video8Player.setWlMedia(wlMedia8);
        wlMedia9 = new WlMedia();
        wlMedia9.setFFOptions("rtsp_transport", "tcp");
        wlMedia9.setFFOptions("fflags", "nobuffer");
        video9Player.setWlMedia(wlMedia9);
        video1Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia1.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video2Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia2.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video3Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia3.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video4Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia4.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video5Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia5.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video6Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia6.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video7Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia7.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video8Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia8.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });
        video9Player.setOnVideoViewListener(new WlOnVideoViewListener() {
            @Override
            public void initSuccess() {
                wlMedia9.prepared();
            }

            @Override
            public void onSurfaceChange(int width, int height)
            {
            }

            @Override
            public void moveX(double value, int move_type) {

            }

            @Override
            public void onSingleClick() {

            }

            @Override
            public void onDoubleClick() {

            }

            @Override
            public void moveLeft(double value, int move_type) {

            }

            @Override
            public void moveRight(double value, int move_type) {

            }
        });

    }


    void tabLeft(){
        v_left.setVisibility(View.VISIBLE);
        v_right.setVisibility(View.INVISIBLE);
        tv_left.setTextColor(getResources().getColor(R.color.white));
        tv_right.setTextColor(getResources().getColor(R.color.c3));
        getDataFromService();
    }

    void tabRight(){
        v_right.setVisibility(View.VISIBLE);
        v_left.setVisibility(View.INVISIBLE);
        tv_right.setTextColor(getResources().getColor(R.color.white));
        tv_left.setTextColor(getResources().getColor(R.color.c3));
        getDataFromServiceStar();
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
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.onItemClick(position);
            }
        });*/

        RxViewAction.clickNoDouble(yiView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentVideo = 1;
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
                        currentVideo = 4;
                        yiView.setImageResource(R.drawable.ic_one);
                        siView.setImageResource(R.drawable.ic_four_selected);
                        jiuView.setImageResource(R.drawable.ic_nine);
                        initVideoView();
                    }
                });
        RxViewAction.clickNoDouble(jiuView)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        currentVideo = 9;
                        yiView.setImageResource(R.drawable.ic_one);
                        siView.setImageResource(R.drawable.ic_four);
                        jiuView.setImageResource(R.drawable.ic_nine_selected);
                        initVideoView();
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
                        moveType = 25;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 25;
                        isStop = true;
                        moveShexiangtou();
                    }
                } else if (v.getId() == R.id.shang_button) {      //上 1
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        moveType = 21;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 21;
                        isStop = true;
                        moveShexiangtou();
                    }
                } else if (v.getId() == R.id.youshang_button) {       //右上  7
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        moveType = 26;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 26;
                        isStop = true;
                        moveShexiangtou();
                    }
                } else if (v.getId() == R.id.zuo_button) {        //左  3
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        moveType = 23;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 23;
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
                        moveType = 24;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 24;
                        isStop = true;
                        moveShexiangtou();
                    }
                } else if (v.getId() == R.id.zuoxia_button) {    //左下  6
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        moveType = 27;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 27;
                        isStop = true;
                        moveShexiangtou();
                    }
                } else if (v.getId() == R.id.xia_button) {        //下 2
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        moveType = 22;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 22;
                        isStop = true;
                        moveShexiangtou();
                    }
                } else if (v.getId() == R.id.youxia_button) {        //右下  8
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        moveType = 28;
                        isStop = false;
                        moveShexiangtou();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开
                        moveType = 28;
                        isStop = true;
                        moveShexiangtou();
                    }
                }  else if (v.getId() == R.id.jieping_button) {
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        Toast.makeText(getContext(), "暂无控制权限", Toast.LENGTH_SHORT).show();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开

                    }
                } else if (v.getId() == R.id.jujiao_button) {
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        Toast.makeText(getContext(), "暂无控制权限", Toast.LENGTH_SHORT).show();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开

                    }
                } else if (v.getId() == R.id.luxiang_button) {
                    if (action == MotionEvent.ACTION_DOWN) { // 按下
                        Toast.makeText(getContext(), "暂无控制权限", Toast.LENGTH_SHORT).show();
                    } else if (action == MotionEvent.ACTION_UP) { // 松开

                    }
                }
                    if (v.getId() == R.id.lajin_button) {
                        if (action == MotionEvent.ACTION_DOWN) { // 按下
                            moveType = 11;
                            isStop = false;
                            moveShexiangtou();
                        } else if (action == MotionEvent.ACTION_UP) { // 松开
                            moveType = 11;
                            isStop = true;
                            moveShexiangtou();
                        }
                } else if (v.getId() == R.id.layuan_button) {
                        if (action == MotionEvent.ACTION_DOWN) { // 按下
                            moveType = 12;
                            isStop = false;
                            moveShexiangtou();
                        } else if (action == MotionEvent.ACTION_UP) { // 松开
                            moveType = 12;
                            isStop = true;
                            moveShexiangtou();
                        }
                    }
            return false;
        }
    };

    private void moveShexiangtou() {
        if(moveType == 11 || moveType == 12){
            if(!rootCtrlFocus){
                Toast.makeText(getActivity(), "您的账号暂无缩放权限", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            if(!rootCtrlDirection){
                Toast.makeText(getActivity(), "您的账号暂无控制权限", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String id = "";
        String channelId = "";
        boolean isHasVideo = false;
        for (int i = 0; i < videoIdList.size(); i++) {
            if (videoIdList.get(i).getVideoPlayer() == currentChooseVideo) {
                Log.e(TAG, "moveShexiangtou: for" + videoIdList.get(i).toString() );
                isHasVideo = true;
                id = videoIdList.get(i).getVideoId();
                channelId = videoIdList.get(i).getMonitorId();
            }
        }
        if (!isHasVideo){
            Toast.makeText(getContext(), "当前控制器暂无摄像头", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "/resource/api/liveVideo/control");
        params.addBodyParameter("monitorId",id);
        params.addBodyParameter("channelId",channelId);
        params.addBodyParameter("speed","5");
        params.addBodyParameter("stop",isStop?"1":"0");
        params.addBodyParameter("controlType",moveType+"");
        params.addBodyParameter("groupId",currentGroupId);
        params.setConnectTimeout(10000);
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet

        Log.e(TAG, "moveShexiangtou: " + params);
        Log.e(TAG, "moveShexiangtou: " + jsonObject.toString());
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "moveShexiangtou: " + result);

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
        steptype=0;
    }

    /**
     * 移除当前选中视频
     */
    private void yichuVideo() {
        if (currentChooseVideo == 1) {
         //   video1Player.startPrepare();
            wlMedia1.stop();
            video1Player.setVisibility(View.GONE);
            video1AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView.getVisibility()==View.VISIBLE){
                wlCircleLoadView.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 2) {
            wlMedia2.stop();
            video2Player.setVisibility(View.GONE);
            video2AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView2.getVisibility()==View.VISIBLE){
                wlCircleLoadView2.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 3) {
            wlMedia3.stop();
            video3Player.setVisibility(View.GONE);
            video3AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView3.getVisibility()==View.VISIBLE){
                wlCircleLoadView3.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 4) {
            wlMedia4.stop();
            video4Player.setVisibility(View.GONE);
            video4AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView4.getVisibility()==View.VISIBLE){
                wlCircleLoadView4.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 5) {
            wlMedia5.stop();
            video5Player.setVisibility(View.GONE);
            video5AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView5.getVisibility()==View.VISIBLE){
                wlCircleLoadView5.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 6) {
            wlMedia6.stop();
            video6Player.setVisibility(View.GONE);
            video6AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView6.getVisibility()==View.VISIBLE){
                wlCircleLoadView6.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 7) {
            wlMedia7.stop();
            video7Player.setVisibility(View.GONE);
            video7AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView7.getVisibility()==View.VISIBLE){
                wlCircleLoadView7.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 8) {
            wlMedia8.stop();
            video8Player.setVisibility(View.GONE);
            video8AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView8.getVisibility()==View.VISIBLE){
                wlCircleLoadView8.setVisibility(View.GONE);
            }
        } else if (currentChooseVideo == 9) {
            wlMedia9.stop();
            video9Player.setVisibility(View.GONE);
            video9AddView.setVisibility(View.VISIBLE);
            if (wlCircleLoadView9.getVisibility()==View.VISIBLE){
                wlCircleLoadView9.setVisibility(View.GONE);
            }
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
        Log.e(TAG, "initVideoChooseView: "+ currentChooseVideo);
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
            closeVideoPlayer(video2Player,wlMedia2);
            closeVideoPlayer(video3Player,wlMedia3);
            closeVideoPlayer(video4Player,wlMedia4);
            closeVideoPlayer(video5Player,wlMedia5);
            closeVideoPlayer(video6Player,wlMedia6);
            closeVideoPlayer(video7Player,wlMedia7);
            closeVideoPlayer(video8Player,wlMedia8);
            closeVideoPlayer(video9Player,wlMedia9);
            video2AddView.setVisibility(View.VISIBLE);
            video3AddView.setVisibility(View.VISIBLE);
            video4AddView.setVisibility(View.VISIBLE);
            video5AddView.setVisibility(View.VISIBLE);
            video6AddView.setVisibility(View.VISIBLE);
            video7AddView.setVisibility(View.VISIBLE);
            video8AddView.setVisibility(View.VISIBLE);
            video9AddView.setVisibility(View.VISIBLE);
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
            closeVideoPlayer(video3Player,wlMedia3);
            closeVideoPlayer(video6Player,wlMedia6);
            closeVideoPlayer(video7Player,wlMedia7);
            closeVideoPlayer(video8Player,wlMedia8);
            closeVideoPlayer(video9Player,wlMedia9);
            video3AddView.setVisibility(View.VISIBLE);
            video6AddView.setVisibility(View.VISIBLE);
            video7AddView.setVisibility(View.VISIBLE);
            video8AddView.setVisibility(View.VISIBLE);
            video9AddView.setVisibility(View.VISIBLE);
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
        }

    }

    private void closeVideoPlayer(WlSurfaceView videoPlayer,WlMedia wlMedia) {
        videoPlayer.setVisibility(View.GONE);
        wlMedia.stop();
    }

    /**
     * 从服务器获取阿启视的token
     */
    private void getTokenFromService() {
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/aqishi/authentication");
        showDialogProgress(progressDialog, "数据加载中...");
        params.setConnectTimeout(10000);
        Log.e(TAG, "getTokenFromService: " + params);
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "getTokenFromService: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    aqishiToken = jsonObject.getJSONArray("data").getJSONObject(0).getString("data");

                    //getTreeFromAQiShi();
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
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 从阿启视接口获取树
     */
    private void getTreeFromAQiShi() {

        RequestParams params = new RequestParams("http://111.41.48.160:9800/bserver/api/v1/organization/all");
        params.addParameter("token", token);
        params.addParameter("org_code", "");
        params.addParameter("search_type", 0);
        params.addParameter("unit_type", "0,1");
        params.addParameter("category", 0);

        params.setConnectTimeout(10000);
        Log.e(TAG, "getTreeFromAQiShi: ---" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.getJSONArray("data");
                    organizationList.clear();
                    Gson gson = new Gson();
                    organizationList = gson.fromJson(String.valueOf(data), new TypeToken<List<Organization>>() {
                    }.getType());
                    getData();
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
                progressDialog.dismiss();
            }
        });
    }

    private void getData() {

        Log.e(TAG, "getData: size==" + organizationList.size());


        for (int i = 0; i < organizationList.size(); i++) {
            if (organizationList.get(i).getDevice_type().equals("0")) {  //文件夹
                if (organizationList.get(i).getOrg_code().isEmpty()) {   //最顶层
                    Log.e(TAG, "getData: 0");
                    num = num + 1;
                    pointList.add(new TreePoint(organizationList.get(i).getId(), organizationList.get(i).getName(), "0", "0", num, true));

                } else {
                    Log.e(TAG, "getData: 1");
                    num = num + 1;

                    pointList.add(new TreePoint(organizationList.get(i).getId(), organizationList.get(i).getName(), organizationList.get(i).getOrg_code(), "0", num));

                }

            }
        }

        for (int i = 0; i < organizationList.size(); i++) {
            if (!organizationList.get(i).getDevice_type().equals("0")) {  //不是文件夹
                Log.e(TAG, "getData: aa");
                num = num + 1;
                pointList.add(new TreePoint(organizationList.get(i).getId(), organizationList.get(i).getName(), organizationList.get(i).getOrg_code(), "1", num));
            }
        }


        //打乱集合中的数据
        Collections.shuffle(pointList);
        //对集合中的数据重新排序
        updateData();

    }

    private void updateData() {
        Log.e(TAG, "updateData: bbb");
        for (TreePoint treePoint : pointList) {
            pointMap.put(treePoint.getID(), treePoint);
        }
        Collections.sort(pointList, new Comparator<TreePoint>() {
            @Override
            public int compare(TreePoint lhs, TreePoint rhs) {
                int llevel = TreeUtils.getLevel(lhs, pointMap);
                int rlevel = TreeUtils.getLevel(rhs, pointMap);
                if (llevel == rlevel) {
                    if (lhs.getPARENTID().equals(rhs.getPARENTID())) {  //左边小
                        return lhs.getDISPLAY_ORDER() > rhs.getDISPLAY_ORDER() ? 1 : -1;
                    } else {  //如果父辈id不相等
                        //同一级别，不同父辈
                        TreePoint ltreePoint = TreeUtils.getTreePoint(lhs.getPARENTID(), pointMap);
                        TreePoint rtreePoint = TreeUtils.getTreePoint(rhs.getPARENTID(), pointMap);
                        return compare(ltreePoint, rtreePoint);  //父辈
                    }
                } else {  //不同级别
                    if (llevel > rlevel) {   //左边级别大       左边小
                        if (lhs.getPARENTID().equals(rhs.getID())) {
                            return 1;
                        } else {
                            TreePoint lreasonTreePoint = TreeUtils.getTreePoint(lhs.getPARENTID(), pointMap);
                            return compare(lreasonTreePoint, rhs);
                        }
                    } else {   //右边级别大   右边小
                        if (rhs.getPARENTID().equals(lhs.getID())) {
                            return -1;
                        }
                        TreePoint rreasonTreePoint = TreeUtils.getTreePoint(rhs.getPARENTID(), pointMap);
                        return compare(lhs, rreasonTreePoint);
                    }
                }
            }
        });
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onPlayerItemClickListener(String id, String parentid, String name, String rtspUrl) {

    }

    /**
     * 视频列表的条目点击 加载视频
     *
     * @param id
     */
    @Override
    public void onPlayerItemClickListener(String id,String name) {
       /* currentClickItemName = name;
        currentClickItemId = id;
  //      getPlayUrlFromAQISHI(id);
        getPlayUrlFromHaohai(id);*/


    }

    /**
     * 从浩海获取视频流
     * @param id
     */
    private void getPlayUrlFromHaohai(String id) {
        showDialogProgress(progressDialog, "视频加载中...");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("enumCode",10002);
            jsonObject.put("isAndroid","1");
            jsonObject.put("monitoringStreamType","101");     //获取rtsp流
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/guide/getVideo");
        params.setBodyContent(jsonObject.toString());
        params.addHeader("Authorization","bearer " + new DbConfig(getContext()).getUser().getToken());
        params.addHeader("NetworkType","Internet");//内网  Intranet互联网  Internet

        Log.e(TAG, "getTreeFromAQiShiUrl: ---" + params);
        Log.e(TAG, "getTreeFromAQiShiUrl: ---" + jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("code").equals("200")){
                        String playerUrl = "";
                        try {
                            playerUrl = jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("LiveQing").getJSONObject("Body").getString("URL");
                        }catch (Exception e){
                            playerUrl = jsonObject.getJSONArray("data").getJSONObject(0).getJSONObject("data").getString("url");
                        }




                        Log.e(TAG, "onSuccess: " +playerUrl);
                        //  String playerUrl = jsonObject.getString("data");
                        videoListDialog.dismiss();
                        if (isAddVideoViewClick) {       //直接往点击的视频播放机中添加
                            addVideoPlayer(playerUrl);
                        } else {             //往列表中排序添加
                            getVideoPlayerState();
                            setVideoPlayer(playerUrl);
                        }
                    }else {
                        Toast.makeText(getContext(), "当前设备不在线", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getContext(), "暂无播放源", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 阿奇视获取视频流
     * @param id
     */
    private void getPlayUrlFromAQISHI(String id) {
        showDialogProgress(progressDialog, "视频加载中...");
        RequestParams params = new RequestParams("http://111.41.48.160:9800/bserver/api/v1/device/video/preview");
        params.addParameter("token", token);
        params.addParameter("camera_id", id);
        params.addParameter("stream_type", 0);
        params.addParameter("stream_mode", 2);
        params.addParameter("is_local", 1);

        params.setConnectTimeout(10000);
        Log.e(TAG, "getTreeFromAQiShiUrl: ---" + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String playerUrl = jsonObject.getString("data");
                    videoListDialog.dismiss();
                    if (isAddVideoViewClick) {       //直接往点击的视频播放机中添加
                        addVideoPlayer(playerUrl);
                    } else {             //往列表中排序添加
                        getVideoPlayerState();
                        setVideoPlayer(playerUrl);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getContext(), "暂无播放源", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
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
            wlMedia1.setSource(playerUrl);
            wlMedia1.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,1,currentChannelId));
        } else if (currentChooseVideo == 2) {
            video2AddView.setVisibility(View.GONE);
            video2Player.setVisibility(View.VISIBLE);
            wlMedia2.setSource(playerUrl);
            wlMedia2.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,2,currentChannelId));
        } else if (currentChooseVideo == 3) {
            video3AddView.setVisibility(View.GONE);
            video3Player.setVisibility(View.VISIBLE);
            wlMedia3.setSource(playerUrl);
            wlMedia3.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,3,currentChannelId));
        } else if (currentChooseVideo == 4) {
            video4AddView.setVisibility(View.GONE);
            video4Player.setVisibility(View.VISIBLE);
            wlMedia4.setSource(playerUrl);
            wlMedia4.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,4,currentChannelId));
        } else if (currentChooseVideo == 5) {
            video5AddView.setVisibility(View.GONE);
            video5Player.setVisibility(View.VISIBLE);
            wlMedia5.setSource(playerUrl);
            wlMedia5.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,5,currentChannelId));
        } else if (currentChooseVideo == 6) {
            video6AddView.setVisibility(View.GONE);
            video6Player.setVisibility(View.VISIBLE);
            wlMedia6.setSource(playerUrl);
            wlMedia6.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,6,currentChannelId));
        } else if (currentChooseVideo == 7) {
            video7AddView.setVisibility(View.GONE);
            video7Player.setVisibility(View.VISIBLE);
            wlMedia7.setSource(playerUrl);
            wlMedia7.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,7,currentChannelId));
        } else if (currentChooseVideo == 8) {
            video8AddView.setVisibility(View.GONE);
            video8Player.setVisibility(View.VISIBLE);
            wlMedia8.setSource(playerUrl);
            wlMedia8.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,8,currentChannelId));
        } else if (currentChooseVideo == 9) {
            video9AddView.setVisibility(View.GONE);
            video9Player.setVisibility(View.VISIBLE);
            wlMedia9.setSource(playerUrl);
            wlMedia9.prepared();
            videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,9,currentChannelId));
        }
        wlMedia1.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia1.start();
                Log.e(TAG, "onPrepared: 11111");
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息
                Log.e(TAG, "onError: "+msg+"+"+code);
            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）
                Log.e(TAG, "onComplete: "+type+"+"+msg );
                if (type.toString().equals("WL_COMPLETE_TIMEOUT")||type.toString().equals("WL_COMPLETE_EOF")){
                    Toast.makeText(getContext(), "当前网络不佳，尝试重新播放中", Toast.LENGTH_SHORT).show();
                    wlMedia1.prepared();
                }
            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间
            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia2.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia2.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView2.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView2.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia3.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia3.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息
                Log.e(TAG, "onError: "+msg+"+"+code);
            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）
                Log.e(TAG, "onComplete: "+type+"+"+msg );
            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间
                Log.e(TAG, "onTimeInfo1: "+currentTime );
                Log.e(TAG, "onTimeInfo2: "+bufferTime );
            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView3.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView3.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia4.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia4.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView4.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView4.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia5.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia5.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView5.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView5.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia6.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia6.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView6.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView6.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia7.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia7.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView7.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView7.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia8.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia8.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView8.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView8.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia9.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia9.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
                if(load)
                {
                    wlCircleLoadView9.setVisibility(View.VISIBLE);
                }
                else{
                    wlCircleLoadView9.setVisibility(View.GONE);
                }
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
    }

    /**
     * 获取视频状态
     */
    private void getVideoPlayerState() {
        currentVideo1PlayerState = wlMedia1.isPlaying();
        currentVideo2PlayerState = wlMedia2.isPlaying();
        currentVideo3PlayerState = wlMedia3.isPlaying();
        currentVideo4PlayerState = wlMedia4.isPlaying();
        currentVideo5PlayerState = wlMedia5.isPlaying();
        currentVideo6PlayerState = wlMedia6.isPlaying();
        currentVideo7PlayerState = wlMedia7.isPlaying();
        currentVideo8PlayerState = wlMedia8.isPlaying();
        currentVideo9PlayerState = wlMedia9.isPlaying();
    }

    /**
     * 设置视频播放
     *
     * @param playerUrl
     */
    private void setVideoPlayer(String playerUrl) {
        Log.e(TAG, "setVideoPlayer: " + currentClickItemName);
        if (currentVideo == 1) {
            if (!currentVideo1PlayerState) {
                wlMedia1.setSource(playerUrl);
                wlMedia1.prepared();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,1,currentChannelId));
            } else {
                Toast.makeText(getContext(), "目前没有闲置播放器", Toast.LENGTH_SHORT).show();
            }
        } else if (currentVideo == 4) {
            if (!currentVideo1PlayerState) {
                wlMedia1.setSource(playerUrl);
                wlMedia1.prepared();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,1,currentChannelId));
            } else if (!currentVideo2PlayerState) {
                wlMedia2.setSource(playerUrl);
                wlMedia2.prepared();
                video2Player.setVisibility(View.VISIBLE);
                video2AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,2,currentChannelId));
            } else if (!currentVideo4PlayerState) {
                wlMedia4.setSource(playerUrl);
                wlMedia4.prepared();
                video4Player.setVisibility(View.VISIBLE);
                video4AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,4,currentChannelId));
            } else if (!currentVideo5PlayerState) {
                wlMedia5.setSource(playerUrl);
                wlMedia5.prepared();
                video5Player.setVisibility(View.VISIBLE);
                video5AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,5,currentChannelId));
            } else {
                Toast.makeText(getContext(), "目前没有闲置播放器", Toast.LENGTH_SHORT).show();
            }
        } else if (currentVideo == 9) {
            if (!currentVideo1PlayerState) {
                wlMedia1.setSource(playerUrl);
                wlMedia1.prepared();
                video1Player.setVisibility(View.VISIBLE);
                video1AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,1,currentChannelId));
            } else if (!currentVideo2PlayerState) {
                wlMedia2.setSource(playerUrl);
                wlMedia2.prepared();
                video2Player.setVisibility(View.VISIBLE);
                video2AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,2,currentChannelId));
            } else if (!currentVideo3PlayerState) {
                wlMedia3.setSource(playerUrl);
                wlMedia3.prepared();
                video3Player.setVisibility(View.VISIBLE);
                video3AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,3,currentChannelId));
            } else if (!currentVideo4PlayerState) {
                wlMedia4.setSource(playerUrl);
                wlMedia4.prepared();
                video4Player.setVisibility(View.VISIBLE);
                video4AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,4,currentChannelId));
            } else if (!currentVideo5PlayerState) {
                wlMedia5.setSource(playerUrl);
                wlMedia5.prepared();
                video5Player.setVisibility(View.VISIBLE);
                video5AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,5,currentChannelId));
            } else if (!currentVideo6PlayerState) {
                wlMedia6.setSource(playerUrl);
                wlMedia6.prepared();
                video6Player.setVisibility(View.VISIBLE);
                video6AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,6,currentChannelId));
            } else if (!currentVideo7PlayerState) {
                wlMedia7.setSource(playerUrl);
                wlMedia7.prepared();
                video7Player.setVisibility(View.VISIBLE);
                video7AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,7,currentChannelId));
            } else if (!currentVideo8PlayerState) {
                wlMedia8.setSource(playerUrl);
                wlMedia8.prepared();
                video8Player.setVisibility(View.VISIBLE);
                video8AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,8,currentChannelId));
            } else if (!currentVideo9PlayerState) {
                wlMedia9.setSource(playerUrl);
                wlMedia9.prepared();
                video9Player.setVisibility(View.VISIBLE);
                video9AddView.setVisibility(View.GONE);
                videoIdList.add(new VideoId(currentClickItemId,currentClickItemName,9,currentChannelId));
            } else {
                Toast.makeText(getContext(), "目前没有闲置播放器", Toast.LENGTH_SHORT).show();
            }
        }
        wlMedia1.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia1.start();
                Log.e(TAG, "onPrepared: 11111");
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息
                Log.e(TAG, "onError: "+msg );
            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia2.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia2.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia3.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia3.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia4.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia4.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia5.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia5.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia6.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia6.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia7.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia7.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia8.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia8.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
        wlMedia9.setOnMediaInfoListener(new WlOnMediaInfoListener() {
            @Override
            public void onPrepared() {
                //异步准备好后开始播放
                wlMedia9.start();
            }

            @Override
            public void onError(int code, String msg) {
                //错误回调，主要用于查看错误信息

            }

            @Override
            public void onComplete(WlComplete type, String msg) {
                //播放完成（包含：正常播放完成、超时播放完成、手动触发播放完成等）

            }

            @Override
            public void onTimeInfo(double currentTime, double bufferTime) {
                //时间回调，当前时间和缓冲时间

            }

            @Override
            public void onSeekFinish() {
                //seek完成后回调，可用于类似iptv这种快进快退
            }

            @Override
            public void onLoopPlay(int loopCount) {
                //循环播放此时回调
            }

            @Override
            public void onLoad(boolean load) {
                //加载状态回调
            }

            @Override
            public byte[] decryptBuffer(byte[] encryptBuffer) {
                return new byte[0];
            }

            @Override
            public byte[] readBuffer(int read_size) {
                return new byte[0];
            }

            @Override
            public void onPause(boolean pause) {
                //暂停回调
            }
        });
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
        if (currentVideo1PlayerState) {
            wlMedia1.stop();
        }
        if (currentVideo2PlayerState) {
            wlMedia2.stop();
        }
        if (currentVideo3PlayerState) {
            wlMedia3.stop();
        }
        if (currentVideo4PlayerState) {
            wlMedia4.stop();
        }
        if (currentVideo5PlayerState) {
            wlMedia5.stop();
        }
        if (currentVideo6PlayerState) {
            wlMedia6.stop();
        }
        if (currentVideo7PlayerState) {
            wlMedia7.stop();
        }
        if (currentVideo8PlayerState) {
            wlMedia8.stop();
        }
        if (currentVideo9PlayerState) {
            wlMedia9.stop();
        }
    }

    class ChangeTabReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            String videoId = intent.getStringExtra("id");
            String channelId = intent.getStringExtra("channelId");
            String monitorId = intent.getStringExtra("monitorId");
            String groupId = intent.getStringExtra("groupId");
            Log.i("videoOnReceive: ", videoId);
            currentClickItemId = monitorId;
            currentClickMonitorId = monitorId;
            currentChannelId = channelId;
            currentGroupId = parseGroupId(groupId);
            postPlayerUrl(videoId);
            currentClickItemName="监控点视频";
        }
    }

    private String parseGroupId(String groupId) {
        String id = groupId;
        if(groupId!=null && groupId.length()>9){
            id = groupId.substring(0,9);
        }
        return id;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try{
            getActivity().unregisterReceiver(changeTabReceiver);
        }catch(Exception e){

        }
    }
}
