package com.ruyiruyi.rylibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.model.ChannelBaseInfo;
import com.netease.nimlib.sdk.avsignalling.model.SignallingPushConfig;
import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.model.Emptys;
import com.ruyiruyi.rylibrary.model.EmptysViewBinder;
import com.ruyiruyi.rylibrary.model.IMGridUsers;
import com.ruyiruyi.rylibrary.model.IMGridUsersBinder;
import com.ruyiruyi.rylibrary.model.IMOnlineUsers;
import com.ruyiruyi.rylibrary.model.IMOnlineUsersBinder;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.utils.CircleImageView;
import com.ruyiruyi.rylibrary.utils.CommonData;
import com.ruyiruyi.rylibrary.utils.DYLoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class IMOnlineActivity extends HhBaseActivity implements IMOnlineUsersBinder.OnBeiDouItemClickListener {
    private final String TAG = IMOnlineActivity.class.getSimpleName();
    private ActionBar actionBar;
    DYLoadingView dy3;
    TextView tv_go;
    TextView tv_go_video;
    LinearLayout ll_grid;
    SwipeRefreshLayout swipeRefreshLayout;
    private User user;

    private List<IMOnlineUsers> onlineList = new ArrayList<>();
    private List<IMOnlineUsers.Users> selectList = new ArrayList<>();//选中用户列表

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imactivity);
        actionBar = findViewById(R.id.my_action);
        actionBar.setTitle("语音通话");
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick(){
            @Override
            public void onItemClick(int var1) {
                switch ((var1)){
                    case -1:
                        onBackPressed();
                        break;
                }
            }
        });

        initView();
        postData();
    }

    private void postData() {
        RequestParams paramS = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/grid/gridUser/list");
        paramS.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        JSONObject object = new JSONObject();
        try {
            object.put("onlineState","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        paramS.setBodyContent(object.toString());
        Log.e(TAG, "postData: --`" + paramS);
        showDY3();
        x.http().post(paramS, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "postData: onSuccess --`" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        JSONArray data = object.getJSONArray("data");
                        if(data!=null && data.length()!=0){
                            JSONObject jsonObject = (JSONObject) data.get(0);
                            JSONArray gridUserTrees = jsonObject.getJSONArray("gridUserTrees");
                            if(gridUserTrees!=null && gridUserTrees.length()!=0){
                                Gson gson = new Gson();
                                onlineList = gson.fromJson(String.valueOf(gridUserTrees), new TypeToken<List<IMOnlineUsers>>() {}.getType());

                                Log.e(TAG, "onSuccess: gridUserTrees = " + gridUserTrees );
                                Log.e(TAG, "onSuccess: onlineList = " + onlineList.toString() );

                                ll_grid.removeAllViews();
                                ll_grid.addView(drawGridOnline(onlineList));
                            }
                            /*if(gridUserTrees!=null && gridUserTrees.length()!=0){
                                JSONObject obj = (JSONObject) gridUserTrees.get(0);
                                JSONArray children = obj.getJSONArray("children");
                                if(children!=null && children.length()!=0){
                                    Gson gson = new Gson();
                                    onlineList = gson.fromJson(String.valueOf(children), new TypeToken<List<IMOnlineUsers>>() {}.getType());

                                    Log.e(TAG, "onSuccess: children = " + children );
                                    Log.e(TAG, "onSuccess: onlineList = " + onlineList.toString() );

                                    ll_grid.removeAllViews();
                                    ll_grid.addView(drawGridOnline(onlineList));

                                }
                            }*/
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "postData: onSuccess e --" + e);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "postData: onError -- ex" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDY3();
                    }
                },3000);
            }
        });
    }

    private View drawGridOnline(List<IMOnlineUsers> onlineList) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_beidou_gridlist,null);
        FrameLayout fl_grid_view = view.findViewById(R.id.fl_grid);
        fl_grid_view.setVisibility(View.GONE);
        LinearLayout ll_out = view.findViewById(R.id.ll_users);
        for (int i = 0; i < onlineList.size(); i++) {
            View item = LayoutInflater.from(this).inflate(R.layout.item_beidou_gridlist,null);
            FrameLayout fl_grid = item.findViewById(R.id.fl_grid);
            ImageView iv_status = item.findViewById(R.id.iv_status);
            TextView tv_name = item.findViewById(R.id.tv_name);
            LinearLayout ll_users = item.findViewById(R.id.ll_users);
            ImageView iv_down = item.findViewById(R.id.iv_down);
            IMOnlineUsers imGridUsers = onlineList.get(i);
            tv_name.setText(imGridUsers.getName());
            RxViewAction.clickNoDouble(fl_grid).subscribe(unused -> {
                imGridUsers.setExpand(!imGridUsers.isExpand());
                if(imGridUsers.isExpand()){
                    ll_users.setVisibility(View.VISIBLE);
                    iv_down.setImageDrawable(getResources().getDrawable(R.mipmap.ic_downs));
                    ll_users.removeAllViews();
                    if(imGridUsers.getUserList()!=null && !imGridUsers.getUserList().isEmpty()){
                        ll_users.addView(drawOnlineUsers(imGridUsers.getUserList(),false));
                    }
                    if(imGridUsers.getChildren()!=null && !imGridUsers.getChildren().isEmpty()){
                        ll_users.addView(drawGridOnline(imGridUsers.getChildren()));
                    }
                }else{
                    ll_users.setVisibility(View.GONE);
                    iv_down.setImageDrawable(getResources().getDrawable(R.mipmap.ic_ups));
                }
            });
            if(imGridUsers.isExpand()){
                ll_users.setVisibility(View.VISIBLE);
                iv_down.setImageDrawable(getResources().getDrawable(R.mipmap.ic_downs));
            }else{
                ll_users.setVisibility(View.GONE);
                iv_down.setImageDrawable(getResources().getDrawable(R.mipmap.ic_ups));
            }
            RxViewAction.clickNoDouble(iv_status).subscribe(unused -> {
                imGridUsers.setStatus(!imGridUsers.isStatus());
                if(imGridUsers.isStatus()){
                    iv_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_se));
                }else{
                    iv_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_uns));
                }
                OnBeiDouGridSelect(imGridUsers.getUserList(),imGridUsers.isStatus());
            });
            if(imGridUsers.isStatus()){
                iv_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_se));
            }else{
                iv_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_uns));
            }

            ll_out.addView(item);
        }

        return view;
    }

    private View drawOnlineUsers(List<IMOnlineUsers.Users> userList,boolean auto) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_beidou_gridlist,null);
        FrameLayout fl_grid_view = view.findViewById(R.id.fl_grid);
        fl_grid_view.setVisibility(View.GONE);
        LinearLayout items = view.findViewById(R.id.ll_users);
        for (int i = 0; i < userList.size(); i++) {
            IMOnlineUsers.Users users = userList.get(i);
            View item = LayoutInflater.from(this).inflate(R.layout.item_grid_userslist,null);
            ImageView item_status = item.findViewById(R.id.item_status);
            LinearLayout ll_items = item.findViewById(R.id.ll_items);
            CircleImageView iv_user = item.findViewById(R.id.iv_user);
            TextView tv_name = item.findViewById(R.id.tv_name);
            TextView tv_content = item.findViewById(R.id.tv_content);
            TextView tv_unread = item.findViewById(R.id.tv_unread);
            if(users.isStatus()){
                item_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_se));
            }else{
                item_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_uns));
            }
            //父级网格点击事件
            if(!auto){
                if(users.isStatus()){
                    item_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_se));
                }else{
                    item_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_uns));
                }
            }
            RxViewAction.clickNoDouble(ll_items).subscribe(unused -> {
                users.setStatus(!users.isStatus());
                if(users.isStatus()){
                    item_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_se));
                }else{
                    item_status.setImageDrawable(getResources().getDrawable(R.mipmap.ic_uns));
                }
                OnBeiDouUsersSelect(users,users.isStatus());
            });
            tv_name.setText(users.getFullName());
            tv_content.setText(users.getId());
            if(!Objects.equals(users.getOnlineState(), "online")){
                ll_items.setAlpha(0.3f);
                ll_items.setClickable(false);
            }
            items.addView(item);
        }


        return view;
    }

    private void initView() {
        dy3 = findViewById(R.id.dy3);
        tv_go = findViewById(R.id.tv_go);
        tv_go_video = findViewById(R.id.tv_go_video);
        ll_grid = findViewById(R.id.ll_grid);

        user = new DbConfig(this).getUser();
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RxViewAction.clickNoDouble(tv_go).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(selectList.isEmpty()){
                    Toast.makeText(IMOnlineActivity.this, "请先选择通话人员", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectList.size() > 1){
                    Toast.makeText(IMOnlineActivity.this, "最多选择一名通话人员", Toast.LENGTH_SHORT).show();
                    return;
                }
                inviteType = "audio";
                //创建房间
                createRoom();
            }
        });

        RxViewAction.clickNoDouble(tv_go_video).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(selectList.isEmpty()){
                    Toast.makeText(IMOnlineActivity.this, "请先选择通话人员", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectList.size() > 1){
                    Toast.makeText(IMOnlineActivity.this, "最多选择一名通话人员", Toast.LENGTH_SHORT).show();
                    return;
                }
                inviteType = "video";
                //创建房间
                createRoom();
            }
        });
    }

    /**
     * 信令&&房间融合创建
     */
    private void createRoom() {
        String dateStr = new Date().getTime()+"";
        account = selectList.get(0).getId().trim();
        roomName = account+dateStr.substring(dateStr.length()-4);
        channelName = roomName;
        CommonData.audioRoomName = roomName;
        CommonData.xdChannelName = channelName;

        //1.创建房间
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/nertc/room");
        params.addHeader("Authorization","bearer " + user.getToken());
        params.addParameter("uid",user.getId());
        params.addParameter("channelName",roomName);
        Log.e(TAG, "createRoom: " + user.getToken() );
        Log.e(TAG, "createRoom: " + params.toString() );
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.e(TAG, "onSuccess: createRoom " + result );
                    JSONObject object = new JSONObject(result);
                    JSONArray data = object.getJSONArray("data");
                    JSONObject model = (JSONObject) data.get(0);
                    roomId = model.getString("cid");
                    CommonData.audioRoomId = roomId;

                    //2.创建频道
                    NIMClient.getService(SignallingService.class).create(ChannelType.CUSTOM, channelName, "").setCallback(
                            new RequestCallbackWrapper<ChannelBaseInfo>() {

                                @Override
                                public void onResult(int i, ChannelBaseInfo channelBaseInfo, Throwable throwable) {
                                    if (i == ResponseCode.RES_SUCCESS) {
                                        channelId = channelBaseInfo.getChannelId();
                                        CommonData.xdChannelId = channelId;
                                        //Toast.makeText(IMActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                        //3.发起邀请
                                        inviteOther();
                                    } else {
                                        //Toast.makeText(IMActivity.this, "创建失败， code = " + i + (throwable == null ? "" : ", throwable = " + throwable.getMessage()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


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

    private String channelName;
    private String channelId;
    private String roomName;
    private String roomId;
    private String account;
    private boolean hasOpen = false;
    private String inviteType = "audio";//@audio音频通话 @video视频通话
    /**
     * 邀请别人
     */
    private void inviteOther() {
        Log.e(TAG, "inviteOther: yunxin roomId = " + roomId );
        Log.e(TAG, "inviteOther: yunxin roomName = " + roomName );
        Log.e(TAG, "inviteOther: yunxin channelId = " + channelId );
        Log.e(TAG, "inviteOther: yunxin channelName = " + channelName );
        CommonData.personList = new ArrayList<>(selectList);
        CommonData.personListSize = CommonData.personList.size();
        CommonData.inviteOtherList.clear();
        hasOpen = false;
        for (int i = 0; i < selectList.size(); i++) {
            IMOnlineUsers.Users person = selectList.get(i);

            String invitedRequestId = String.valueOf(System.currentTimeMillis());
            InviteParamBuilder param = new InviteParamBuilder(channelId, person.getId(), roomName);
            Map<String,Object> pushPayload = new HashMap<>();
            pushPayload.put("type",inviteType);
            param.pushConfig(new SignallingPushConfig(true,"发起邀请","发起了邀请",pushPayload));//适配前端 传递额外参数
            param.customInfo(new DbConfig(IMOnlineActivity.this).getUser().getFullName()/*+"@"+inviteType*/);//传递额外参数(发起人名字&&通话类型) 旧版
            CommonData.inviteOtherParam = param;
            CommonData.inviteOtherId = person.getId();
            CommonData.inviteOtherList.add(param);
            //Toast.makeText(this, "发起邀请 ：channelId = " + channelId + ", requestId = " + invitedRequestId, Toast.LENGTH_SHORT).show();
            param.offlineEnabled(true);
            int finalI = i;
            NIMClient.getService(SignallingService.class).invite(param).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    //Toast.makeText(IMActivity.this, "邀请成功 ：channelId = " + channelId + ", requestId = " + invitedRequestId, Toast.LENGTH_SHORT).show();

                    if(!hasOpen){
                        hasOpen = true;

                        boolean isVideo = false;
                        if(inviteType.endsWith("video")){
                            isVideo = true;
                        }
                        //进入语音界面
                        Intent intent = new Intent(IMOnlineActivity.this, CallingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.putExtra("isCalling",true);
                        intent.putExtra("isVideo",isVideo);
                        startActivity(intent);
                    }

                }

                @Override
                public void onFailed(int code) {
                    if(Objects.equals(person.getUserCode(), new DbConfig(IMOnlineActivity.this).getUser().getUserCode())){
                        Toast.makeText(IMOnlineActivity.this, "您不能给自己拨打", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(IMOnlineActivity.this, person.getUserCode() + "不在线", Toast.LENGTH_SHORT).show();
                    /*if(code == 10202){
                        Toast.makeText(IMActivity.this, person.getUserCode()+"不在线" + code, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(IMActivity.this, person.getUserCode()+"邀请失败" + code, Toast.LENGTH_SHORT).show();
                    }*/
                }

                @Override
                public void onException(Throwable exception) {
                    //Toast.makeText(IMActivity.this, "邀请异常 ：exception = " + exception, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    void showDY3(){
        dy3.setVisibility(View.VISIBLE);
        dy3.start();
    }
    void hideDY3(){
        dy3.setVisibility(View.GONE);
        dy3.stop();
    }

    @Override
    public void OnBeiDouItemClick(IMOnlineUsers imGridUsers) {

    }

    public void OnBeiDouGridSelect(List<IMOnlineUsers.Users> list,boolean status) {
        List<Integer> indexList = new ArrayList<>();
        List<IMOnlineUsers.Users> listCopy = new ArrayList<>(selectList);
        if(selectList.isEmpty() && status){
            selectList.addAll(list);
            theEnd();
            return;
        }
        outer:for (int i = 0; i < list.size(); i++) {
            IMOnlineUsers.Users returnUsers = list.get(i);
            for (int m = 0; m < listCopy.size(); m++) {
                IMOnlineUsers.Users users = listCopy.get(m);
                if(users.getId().equals(returnUsers.getId())){
                    if(status){
                        //选中
                        break outer;
                    }else{
                        //移除
                        selectList.remove(users);
                        IMOnlineUsers.Users usersCopy = new IMOnlineUsers.Users();
                        usersCopy = users;
                        usersCopy.setStatus(!users.isStatus());
                        selectList.remove(users);
                        selectList.remove(usersCopy);
                    }
                }

                if(m == listCopy.size()-1&& status){
                    selectList.add(users);
                }
            }
        }
        theEnd();
    }

    void changeUsers(boolean status, List<Integer> indexList, List<IMOnlineUsers.Users> listAdding){
        List<IMOnlineUsers.Users> list = new ArrayList<>();
        List<Integer> indexCopyList = new ArrayList<>();
        for (int i = 0; i < indexList.size(); i++) {
            if(status){
                //添加
                list.add(listAdding.get(indexList.get(i)));
            }else{
                //移除
                for (int m = 0; m < listAdding.size(); m++) {
                    IMOnlineUsers.Users users = listAdding.get(m);
                    if((m != indexList.get(i)) && (!indexCopyList.contains(indexList.get(i)))){
                        list.add(selectList.get(indexList.get(i)));
                        indexCopyList.add(indexList.get(i));
                    }
                }
            }
        }
        if(!status){
            selectList.clear();
        }
        selectList.addAll(list);

        theEnd();;
    }
    void theEnd(){
        /*StringBuilder toastStr = new StringBuilder();
        for (int i = 0; i < selectList.size(); i++) {
            toastStr.append(selectList.get(i).getFullName()).append(",");
        }
        Toast.makeText(this, selectList.size()+""+toastStr, Toast.LENGTH_SHORT).show();*/
    }

    public void OnBeiDouUsersSelect(IMOnlineUsers.Users users,boolean status) {
        if(selectList.isEmpty() && status){
            selectList.add(users);
            theEnd();
            return;
        }
        for (int i = 0; i < selectList.size(); i++) {
            IMOnlineUsers.Users usersModel = selectList.get(i);
            if(usersModel.getId().equals(users.getId())){
                IMOnlineUsers.Users users_ = selectList.get(i);
                IMOnlineUsers.Users users_c = new IMOnlineUsers.Users();
                users_c = users_;
                users_c.setStatus(!users_.isStatus());
                if(!status){
                    selectList.remove(users_);
                    selectList.remove(users_c);
                }
                theEnd();
                return;
            }
            if(i == selectList.size()-1 && status){
                selectList.add(users);
                theEnd();
                return;
            }
        }
    }
}