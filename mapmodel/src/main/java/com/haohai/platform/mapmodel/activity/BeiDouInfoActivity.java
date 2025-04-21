package com.haohai.platform.mapmodel.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.haohai.ledge.videolibrary.utils.CommonUtil;
import com.haohai.platform.firelibrary.ui.activity.base.HhBaseActivity;
import com.haohai.platform.firelibrary.ui.multitype.Emptys;
import com.haohai.platform.firelibrary.ui.multitype.EmptysViewBinder;
import com.haohai.platform.mapmodel.R;
import com.haohai.platform.mapmodel.multitype.BeidouNewsBinder;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.bus.BeidouInfoRefresh;
import com.ruyiruyi.rylibrary.bus.BeidouRefresh;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.cell.DYLoadingView;
import com.ruyiruyi.rylibrary.db.BeidouNews;
import com.ruyiruyi.rylibrary.db.BeidouPerson;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.db.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.ex.DbException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.drakeet.multitype.MultiTypeAdapter;
import rx.functions.Action1;

import static me.drakeet.multitype.MultiTypeAsserts.assertAllRegistered;
import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

public class BeiDouInfoActivity extends HhBaseActivity implements BeidouNewsBinder.OnBeiDouNewsClickListener {

    ///北斗通讯
    private static boolean enable = false;
    private static final String ACTION_MSG_BD_MSG_RECEIVED = "android.intent.action.beidou.msg.received";
    private static final String ACTION_MSG_BD_IC_INFO_RECEIVED = "android.intent.action.beidou.msg.bd.info.received";
    private static final String ACTION_MSG_BD_MSG_RESULT = "android.intent.action.beidou.msg.result";
    private static final String ACTION_MSG_BD_FKXX_RECEIVED = "android.intent.action.beidou.feedbackinfo.received";
    private static final String ACTION_MSG_BD_DWXX_RECEIVED = "android.intent.action.beidou.msg.dwxx.received";
    private static final String ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED = "android.intent.action.beidou.bdmsg.enable_state.received";
    private static final String ACTION_MSG_BD_BDPROTOCOL_VERSION_SET = "android.intent.action.beidou.msg.bdprotocol.version.set";

    private static final String ACTION_MSG_BD_GLXX_INFO_RECEIVED = "android.intent.action.beidou.msg.glxxInfo.received";
    private static final String ACTION_MSG_BD_ZBSC_INFO_RECEIVED = "android.intent.action.beidou.msg.zbscInfo.received";
    private static final String ACTION_MSG_BD_SJXX_INFO_RECEIVED = "android.intent.action.beidou.msg.sjxxInfo.received";
    private static final String ACTION_MSG_BD_XHXX_INFO_RECEIVED = "android.intent.action.beidou.msg.xhxxInfo.received";
    private static final String ACTION_MSG_BD_ZJXX_INFO_RECEIVED = "android.intent.action.beidou.msg.zjxxInfo.received";

    /*system receive part */
    private static final String ACTION_MSG_BD_MSG_SEND = "android.intent.action.beidou.msg.send";
    private static final String ACTION_MSG_BD_NUMBER_REQUEST = "android.intent.action.beidou.msg.number.request";
    private static final String ACTION_MSG_BD_POWER_INFO_REQUEST = "android.intent.action.beidou.msg.bd.info.request_bd_power";
    private static final String ACTION_MSG_BD_DWSQ_REQUEST = "android.intent.action.beidou.msg.dwsq.request";
    private static final String ACTION_MSG_BD_BDMSG_ENABLE_SATE_REQUEST = "android.intent.action.beidou.msg.bdmsg.enable_state.request";

    private static final String ACTION_MSG_BD_GXZR_REQUEST = "android.intent.action.beidou.msg.gxzr.request";
    private static final String ACTION_MSG_BD_GXDQ_REQUEST = "android.intent.action.beidou.msg.gxdq.request";
    private static final String ACTION_MSG_BD_ZBZH_REQUEST = "android.intent.action.beidou.msg.zbzh.request";
    private static final String ACTION_MSG_BD_XGXL_REQUEST = "android.intent.action.beidou.msg.xgxl.request";
    private static final String ACTION_MSG_BD_SJSC_REQUEST = "android.intent.action.beidou.msg.sjsc.request";
    private static final String ACTION_MSG_BD_XTZJ_REQUEST = "android.intent.action.beidou.msg.xtzj.request";
    private static final String ACTION_MSG_BD_MSG_ENABLE_REQUEST = "android.intent.action.beidou.msg.enable.request";
    private static final String ACTION_MSG_BD_PASS_THROUGH_MSG_REQUEST = "android.intent.action.beidou.msg.passthroughmsg.request";

    private static final String BD_MSG_ENABLE = "bd_msg_enable";
    private ActionBar actionBar;
    private DYLoadingView dy3;
    private EditText et_send;
    private ImageView iv_send;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listView;
    private List<Object> items = new ArrayList<>();
    private MultiTypeAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<BeidouNews> beidouNewsList;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bei_dou_info);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        actionBar = findViewById(R.id.my_action);
        actionBar.setTitle(name+"");
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

        //注册北斗消息接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MSG_BD_MSG_RECEIVED);
        filter.addAction(ACTION_MSG_BD_IC_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_MSG_RESULT);
        filter.addAction(ACTION_MSG_BD_FKXX_RECEIVED);
        filter.addAction(ACTION_MSG_BD_DWXX_RECEIVED);
        filter.addAction(ACTION_MSG_BD_GLXX_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_ZBSC_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED);
        filter.addAction(ACTION_MSG_BD_SJXX_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_XHXX_INFO_RECEIVED);
        filter.addAction(ACTION_MSG_BD_ZJXX_INFO_RECEIVED);
        registerReceiver(mBeidouModuleInfoReceiver, filter);

        initView();
        initData(true);
        ///将此联系人消息标记已读
        new DbConfig(this).setReadStateByPersonId(id);
    }



    ///刷新消息对话
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(BeidouInfoRefresh refresh) {
        initData(false);
    }

    private void initView() {
        dy3 = findViewById(R.id.dy3);
        et_send = findViewById(R.id.et_send);
        iv_send = findViewById(R.id.iv_send);
        et_send.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ///滑动到底部最新消息
                            linearLayoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
                        }
                    },120);
                }
            }
        });


        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        listView = findViewById(R.id.rlv);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        listView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter(items);

        BeidouNewsBinder beidouNewsBinder = new BeidouNewsBinder();
        beidouNewsBinder.setListener(this);
        beidouNewsBinder.setContext(this);
        adapter.register(BeidouNews.class, beidouNewsBinder);
        adapter.register(Emptys.class, new EmptysViewBinder());

        listView.setAdapter(adapter);
        assertHasTheSameAdapter(listView, adapter);


        //发送
        RxViewAction.clickNoDouble(iv_send).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        DbConfig dbConfig = new DbConfig(BeiDouInfoActivity.this);
        User user = dbConfig.getUser();
        Date date = new Date();
        //消息内容解析(浩海规定格式： 136.123123,36.123521|后面是消息内容 )
        String sendMessage = user.getLongitude()+","+user.getLatitude() + "|" + et_send.getText().toString();
        byte[] bytes = new byte[0];
        try {
            bytes = sendMessage.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(et_send.getText().toString().length()==0){
            Toast.makeText(this, "不能发送空消息", Toast.LENGTH_SHORT).show();
            return;
        }
        if(bytes.length>84){
            Toast.makeText(this, "发送内容不能超过84字节", Toast.LENGTH_SHORT).show();
            return;
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sendTime = format.format(date);
        BeidouNews messageNews = new BeidouNews(/*生成随机数id*/id+sendTime,name,id,et_send.getText().toString(), user.getLongitude()+","+user.getLatitude(),sendTime,date.getTime(),"",1,-1);
        dbConfig.sendBeiDouMessage(messageNews);

        ///更新列表数据
        BeidouPerson personById = dbConfig.getBeiDouPersonById(id);
        personById.setCount(personById.getCount()+1);
        personById.setUpdateLong(date.getTime());
        personById.setLastNews(et_send.getText().toString());
        personById.setLastTime(sendTime);
        try {
            dbConfig.getDbManager().saveOrUpdate(personById);
        } catch (DbException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new BeidouRefresh());
        ///北斗通讯
        //number: 北斗号码
        //type: // 0:混发, 1:汉字, 2:代码
        //mode(TXSQType): 0x46---->代码/混发;    0x44----->汉字
        Intent sendIntent = new Intent(ACTION_MSG_BD_PASS_THROUGH_MSG_REQUEST);
        Bundle sendBundle = new Bundle();
        sendBundle.putString("number", id);
        sendBundle.putInt("TXSQType", 0x44);
        sendBundle.putInt("type", 1);
        sendBundle.putInt("len", bytes.length);
        sendBundle.putInt("bitLen", bytes.length * 8);
        sendBundle.putByteArray("content", bytes);
        sendIntent.putExtras(sendBundle);
        sendBroadcast(sendIntent);
        ///重置&&刷新数据
        et_send.setText("");
        initData(false);
    }


    private void initData(boolean loading) {
        beidouNewsList = new ArrayList<>();

        DbConfig dbConfig = new DbConfig(BeiDouInfoActivity.this);
        //读取
        if(loading){
            showDY3();
        }
        beidouNewsList = dbConfig.getBeiDouNewsListByPersonId(id);
        update();
        if(loading){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideDY3();
                }
            },1000);
        }
    }

    private void update() {
        items.clear();

        if (beidouNewsList == null || beidouNewsList.size() == 0){
            items.add(new Emptys("你们还没有对话"));
        }else{
            for (int i = 0; i < beidouNewsList.size(); i++) {
                items.add(beidouNewsList.get(i));
            }
        }
        assertAllRegistered(adapter,items);
        adapter.notifyDataSetChanged();
        ///滑动到底部最新消息
        linearLayoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, Integer.MIN_VALUE);
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
    public void OnBeiDouNewsClick(BeidouNews beidouNews) {
        CommonUtil.hideSoftKeyboard(BeiDouInfoActivity.this);
        et_send.clearFocus();
    }
    @Override
    public void OnBeiDouNewsLongClick(BeidouNews beidouNews) {
        String copyMessage = beidouNews.getContent()+"【位置：" + beidouNews.getLocation() + "】";
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", copyMessage);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "消息已复制", Toast.LENGTH_SHORT).show();
    }




    private BroadcastReceiver mBeidouModuleInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            String tempStr = "";
            Bundle bundle = null;
            if (ACTION_MSG_BD_IC_INFO_RECEIVED.equals(action)) {
                bundle = intent.getExtras();
                String ic_number = bundle.getString("number");

                tempStr = "ServiceFre is:" + String.valueOf(bundle.getInt("service_frequency"))
                        + "communiationLevel is:" + String.valueOf(bundle.getInt("communication_level"))
                        + "Ic num is:" + bundle.getString("number")
                        + "BID is:" + String.valueOf(bundle.getInt("BID"))
                        + "Frame is:" + String.valueOf(bundle.getInt("Frame"))
                        + "Feature is:" + String.valueOf(bundle.getInt("Feature"))
                        + "Flag is:" + String.valueOf(bundle.getInt("Flag"))
                        + "UserNum is:" + String.valueOf(bundle.getInt("UserNum"));
                /*txt_sim.setText(tempStr);*/
            }
            ///北斗消息接收 bingo
            if (ACTION_MSG_BD_MSG_RECEIVED.equals(action)) {
                ///若正在与此联系人聊天
                bundle = intent.getExtras();
                String number = bundle.getString("number");
                if(Objects.equals(id, number)){
                    ///刷新此聊天数据
                    initData(false);
                    ///通讯录标记已读
                    new DbConfig(BeiDouInfoActivity.this).setReadStateByPersonId(id);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new BeidouRefresh());
                        }
                    },1000);
                }

            }
            //短报文发送状态
            if (ACTION_MSG_BD_FKXX_RECEIVED.equals(action)) {
                /*bundle = intent.getExtras();
                String fkContent = bundle.getString("FeedBackInfo");//zhong wen zi fu
                int fkTag = bundle.getInt("FeedBackTag"); //fkbacktag info
                String fkExtraInfo = bundle.getString("FeedBackExtraInfo");
                tempStr = "State is:" + fkContent + " fkTag is:0" + fkTag + " ExtraInfo is:" + fkExtraInfo;
                Log.e("TAG", "onReceive: bingo 短报文发送状态 " + tempStr  );*/
            }
            if (ACTION_MSG_BD_DWXX_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_DWXX_RECEIVED2"  );
                bundle = intent.getExtras();
                byte m_Type = bundle.getByte("m_Type");
                short m_byHeightData = bundle.getShort("m_byHeightData");
                byte m_byHeightSymbol = bundle.getByte("m_byHeightSymbol");
                int time_hour = (int) bundle.getByte("m_byHour");
                int time_minute = bundle.getByte("m_byMinute");
                int time_second = bundle.getByte("m_bySecond1");
                int time_minsecond = bundle.getByte("m_bySecond2");
                int lonDegree = bundle.getByte("m_byLonDegree");
                int lonMinute = bundle.getByte("m_byLonMinute");
                int lonSecond = bundle.getByte("m_byLonSecond1");
                int lonminSecond = bundle.getByte("m_byLonSecond2");
                int latDegree = bundle.getByte("m_byLatDegree");
                int latMinute = bundle.getByte("m_byLatMinute");
                int latSecond = bundle.getByte("m_byLatSecond1");
                int latminSecond = bundle.getByte("m_byLatSecond2");

                int heightAbnormalData = bundle.getByte("m_byHeightAbnormalData");
                int heightAbnormalSymbol = bundle.getByte("m_byHeightAbnormalSymbol");
                int address = bundle.getInt("m_address");
                byte[] byteAddress = bundle.getByteArray("m_ByteAddress");
                String timpStr = "time is:" + time_hour + ":" + time_minute + ":"
                        + time_second + "." + time_minsecond + "; lon is: "
                        + lonDegree + "度" + lonMinute + "分" + lonSecond + "."
                        + lonminSecond + "秒; lat is: " + latDegree + "度" + latMinute + "分"
                        + latSecond + "." + latminSecond + "秒;" + " 高度:" + m_byHeightData + "m"
                        + "  " + heightAbnormalSymbol + "  " + heightAbnormalData
                        + "Type is:" + m_Type + " address is:" + address;
                /*txt_bd_pos.setText(timpStr);*/

            }
            if (ACTION_MSG_BD_GLXX_INFO_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_GLXX_INFO_RECEIVED3"  );
                Bundle bundleglxx = intent.getExtras();
                byte[] glxxInfo = bundleglxx.getByteArray("GLXXInfo");
                String glxxStr = new String(glxxInfo);
                /*txt_send_state.setText(glxxStr);*/
            }
            if (ACTION_MSG_BD_ZBSC_INFO_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_ZBSC_INFO_RECEIVED4"  );
                Bundle bundlezbsc = intent.getExtras();
                int changeMode = bundlezbsc.getInt("ZBSC_ChangeMode");
                int x = bundlezbsc.getInt("ZBSC_X");
                int y = bundlezbsc.getInt("ZBSC_Y");
                int z = bundlezbsc.getInt("ZBSC_Z");
                /*txt_send_state.setText("changeMode=" + changeMode + ";x=" + x + ";y=" + y + ";z=" + z);*/
            }
            if (ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_BDMSG_ENABLE_STATE_RECEIVED5"  );
                /*btn_enable_bd.setEnabled(true);
                enable = intent.getExtras().getBoolean("BDMsg_enable_state");
                if (enable) {
                    btn_enable_bd.setText(MainActivity.this.getResources().getString(R.string.str_disable_bd));
                } else {
                    btn_enable_bd.setText(MainActivity.this.getResources().getString(R.string.str_enable_bd));
                }
                */
            }
            if (ACTION_MSG_BD_SJXX_INFO_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_SJXX_INFO_RECEIVED6"  );
                Bundle bundlesjxx = intent.getExtras();
                if (bundlesjxx != null) {
                    int year = bundlesjxx.getInt("m_year");
                    int month = bundlesjxx.getInt("m_month");
                    int day = bundlesjxx.getInt("m_day");
                    int hour = bundlesjxx.getInt("m_hour");
                    int minute = bundlesjxx.getInt("m_minute");
                    int second = bundlesjxx.getInt("m_second");
                    /*txt_bd_time.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);*/
                }
            }
            if (ACTION_MSG_BD_XHXX_INFO_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_XHXX_INFO_RECEIVED7"  );
                Bundle bundlesxhxx = intent.getExtras();
                if (bundlesxhxx != null) {
                    int seriaNo = bundlesxhxx.getInt("XHXX_Number");
                    /*txt_bd_time.setText(String.valueOf(seriaNo));*/
                }
            }
            if (ACTION_MSG_BD_ZJXX_INFO_RECEIVED.equals(action)) {
                Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_ZJXX_INFO_RECEIVED8"  );
                Bundle bundlezjxx = intent.getExtras();
                if (bundlezjxx != null) {
                    int icStatus = bundlezjxx.getByte("ICStatus");
                    int hardwareStatus = bundlezjxx.getByte("HardwareStatus");
                    int batteryStatus = bundlezjxx.getByte("BatteryStatus");
                    int inboundStatus = bundlezjxx.getByte("InboundStatus");
                    int power1 = bundlezjxx.getByte("Power1");
                    int power2 = bundlezjxx.getByte("Power2");
                    int power3 = bundlezjxx.getByte("Power3");
                    int power4 = bundlezjxx.getByte("Power4");
                    int power5 = bundlezjxx.getByte("Power5");
                    int power6 = bundlezjxx.getByte("Power6");

                    String str = String.valueOf(icStatus) + ";" + String.valueOf(hardwareStatus)
                            + ";" + String.valueOf(batteryStatus) + ";" + String.valueOf(inboundStatus)
                            + ";" + String.valueOf(inboundStatus) + ";" + String.valueOf(power1)
                            + ";" + String.valueOf(power2) + ";" + String.valueOf(power3)
                            + ";" + String.valueOf(power4) + ";" + String.valueOf(power5)
                            + ";" + String.valueOf(power6);
                    Log.e("TAG", "onReceive: bingo ACTION_MSG_BD_ZJXX_INFO_RECEIVED8 = " + str);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBeidouModuleInfoReceiver);
        EventBus.getDefault().unregister(this);
    }
}
