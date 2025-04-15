package com.ruyiruyi.rylibrary.base;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avsignalling.SignallingService;
import com.netease.nimlib.sdk.avsignalling.builder.CallParamBuilder;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.constant.ChannelType;
import com.netease.nimlib.sdk.avsignalling.model.ChannelBaseInfo;
import com.netease.nimlib.sdk.avsignalling.model.ChannelFullInfo;
import com.ruyiruyi.rylibrary.R;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.cell.DYLoadingView;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;
import com.ruyiruyi.rylibrary.model.IMGridUsersBinder;
import com.ruyiruyi.rylibrary.model.Emptys;
import com.ruyiruyi.rylibrary.model.EmptysViewBinder;
import com.ruyiruyi.rylibrary.model.IMGridUsers;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.ui.dialog.Common;
import com.ruyiruyi.rylibrary.utils.CommonData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class IMActivity extends HhBaseActivity implements IMGridUsersBinder.OnBeiDouItemClickListener {
    private final String TAG = IMActivity.class.getSimpleName();
    private ActionBar actionBar;
    DYLoadingView dy3;
    TextView tv_go;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private List<IMGridUsers> dataList;//网格含用户列表
    private List<IMGridUsers.Users> selectList = new ArrayList<>();//选中用户列表
    private User user;

    @Override
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
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/grid/listGridNewTrees");
        params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
        params.addParameter("gridNo","371681");
        Log.e(TAG, "postData: --" + params);
        showDY3();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "postData: onSuccess --" + result);
                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt("code")==200){
                        JSONArray data = object.getJSONArray("data");
                        //街道权限
                        if(data!=null && data.length()!=0){
                            JSONObject c = (JSONObject) data.get(0);
                            if(c.getString("name").contains("街道")){
                                Gson gson = new Gson();
                                dataList = gson.fromJson(String.valueOf(data), new TypeToken<List<IMGridUsers>>() {}.getType());

                                IMGridUsers gridUsers = new IMGridUsers();
                                gridUsers.setName("邹平市");
                                gridUsers.setNo("371681");
                                dataList.add(gridUsers);

                                update();
                                postUsers();
                                return;
                            }
                            //邹平市权限
                            if(Objects.equals(c.getString("name"), "邹平市")){
                                Gson gson = new Gson();
                                dataList = gson.fromJson(String.valueOf(c.getJSONArray("children")), new TypeToken<List<IMGridUsers>>() {}.getType());

                                IMGridUsers gridUsers = new IMGridUsers();
                                gridUsers.setName("邹平市");
                                gridUsers.setNo("371681");
                                dataList.add(gridUsers);

                                update();
                                postUsers();
                                return;
                            }
                        }
                        //其它权限
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject model = (JSONObject) data.get(i);
                            if(Objects.equals(model.getString("name"), "滨州市")){
                                JSONArray children = model.getJSONArray("children");

                                for (int m = 0; m < children.length(); m++) {
                                    JSONObject cell = (JSONObject) children.get(m);
                                    if(Objects.equals(cell.getString("name"), "邹平市")){
                                        Gson gson = new Gson();
                                        dataList = gson.fromJson(String.valueOf(cell.getJSONArray("children")), new TypeToken<List<IMGridUsers>>() {}.getType());

                                        IMGridUsers gridUsers = new IMGridUsers();
                                        gridUsers.setName("邹平市");
                                        gridUsers.setNo("371681");
                                        dataList.add(gridUsers);

                                        update();
                                        postUsers();
                                        return;
                                    }
                                }
                            }
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

    private void postUsers() {
        for (int i = 0; i < dataList.size(); i++) {
            IMGridUsers imGridUsers = dataList.get(i);
            JSONObject object = new JSONObject();
            try {
                object.put("gridNo",imGridUsers.getNo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "auth/api/auth/user/list");
            params.addHeader("Authorization", "bearer " + new DbConfig(this).getUser().getToken());
            params.setBodyContent(object.toString());
            Log.e(TAG, "postUsers: --" + params);
            int finalI = i;
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if(object.getInt("code")==200){
                            JSONArray data = object.getJSONArray("data");
                            dataList.get(finalI).setUsers(new Gson().fromJson(String.valueOf(data), new TypeToken<List<IMGridUsers.Users>>() {}.getType()));
                            update();
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
    }

    private void initView() {
        dataList = new ArrayList<>();
        dy3 = findViewById(R.id.dy3);
        tv_go = findViewById(R.id.tv_go);

        user = new DbConfig(this).getUser();
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = findViewById(R.id.rlv);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                postData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        IMGridUsersBinder imGridUsersBinder = new IMGridUsersBinder();
        imGridUsersBinder.setListener(this);
        imGridUsersBinder.setContext(this);
        adapter.register(IMGridUsers.class, imGridUsersBinder);
        adapter.register(Emptys.class, new EmptysViewBinder());

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);

        RxViewAction.clickNoDouble(tv_go).subscribe(new Action1<Void>() {
            @Override
            public void call(Void unused) {
                if(selectList.isEmpty()){
                    Toast.makeText(IMActivity.this, "请先选择通话人员", Toast.LENGTH_SHORT).show();
                    return;
                }
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
            IMGridUsers.Users person = selectList.get(i);

            String invitedRequestId = String.valueOf(System.currentTimeMillis());
            InviteParamBuilder param = new InviteParamBuilder(channelId, person.getId(), roomName);
            param.customInfo(new DbConfig(IMActivity.this).getUser().getFullName());
            CommonData.inviteOtherParam = param;
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
                        //进入语音界面
                        Intent intent = new Intent(IMActivity.this, CallingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.putExtra("isCalling",true);
                        startActivity(intent);
                    }

                }

                @Override
                public void onFailed(int code) {
                    Toast.makeText(IMActivity.this, person.getUserCode() + "不在线", Toast.LENGTH_SHORT).show();
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

    private void update() {
        items.clear();

        if (dataList == null || dataList.size() == 0){
            items.add(new Emptys("您还没有对话"));
        }else{
            for (int i = 0; i < dataList.size(); i++) {
                items.add(dataList.get(i));
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnBeiDouItemClick(IMGridUsers imGridUsers) {

    }
    @Override
    public void OnBeiDouGridSelect(List<IMGridUsers.Users> list,boolean status) {
        List<Integer> indexList = new ArrayList<>();
        List<IMGridUsers.Users> listCopy = new ArrayList<>(selectList);
        if(selectList.isEmpty() && status){
            selectList.addAll(list);
            theEnd();
            return;
        }
        outer:for (int i = 0; i < list.size(); i++) {
            IMGridUsers.Users returnUsers = list.get(i);
            for (int m = 0; m < listCopy.size(); m++) {
                IMGridUsers.Users users = listCopy.get(m);
                if(users.getId().equals(returnUsers.getId())){
                    if(status){
                        //选中
                        break outer;
                    }else{
                        //移除
                        selectList.remove(users);
                        IMGridUsers.Users usersCopy = new IMGridUsers.Users();
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

    void changeUsers(boolean status, List<Integer> indexList, List<IMGridUsers.Users> listAdding){
        List<IMGridUsers.Users> list = new ArrayList<>();
        List<Integer> indexCopyList = new ArrayList<>();
        for (int i = 0; i < indexList.size(); i++) {
            if(status){
                //添加
                list.add(listAdding.get(indexList.get(i)));
            }else{
                //移除
                for (int m = 0; m < listAdding.size(); m++) {
                    IMGridUsers.Users users = listAdding.get(m);
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

    @Override
    public void OnBeiDouUsersSelect(IMGridUsers.Users users,boolean status) {
        if(selectList.isEmpty() && status){
            selectList.add(users);
            theEnd();
            return;
        }
        for (int i = 0; i < selectList.size(); i++) {
            IMGridUsers.Users usersModel = selectList.get(i);
            if(usersModel.getId().equals(users.getId())){
                IMGridUsers.Users users_ = selectList.get(i);
                IMGridUsers.Users users_c = new IMGridUsers.Users();
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