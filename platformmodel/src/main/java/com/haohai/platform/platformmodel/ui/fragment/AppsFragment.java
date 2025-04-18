package com.haohai.platform.platformmodel.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.acticity.AttendanceActivity;
import com.haohai.platform.platformmodel.ui.acticity.FalvActivity;
import com.haohai.platform.platformmodel.ui.acticity.LeaveFlowApproveListActivity;
import com.haohai.platform.platformmodel.ui.acticity.LeaveFlowListActivity;
import com.haohai.platform.platformmodel.ui.acticity.NewsActivity;
import com.haohai.platform.platformmodel.ui.acticity.OnlineWeatherActivity;
import com.haohai.platform.platformmodel.ui.acticity.WeatherActivity;
import com.haohai.platform.platformmodel.ui.acticity.WorkReportListActivity;
import com.haohai.platform.platformmodel.ui.fragment.base.HhBaseFragment;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.request.RequestUtils;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import rx.functions.Action1;

/**
 * Created by geyang on 2020/7/2.
 */

public class AppsFragment extends HhBaseFragment {
    private LinearLayout hulinLayout;
    private LinearLayout fanghuoLayout;
    private LinearLayout wuziLayout;
    private LinearLayout kaoqinLayout;
    private LinearLayout huibaoLayout;
    private LinearLayout liuchengLayout;
    private LinearLayout daishenpiLayout;
    private LinearLayout huoqingLayout;
    private LinearLayout renwuLayout;
    private LinearLayout yinhuanpaichaLayout;
    private LinearLayout tianqiLayout;
    private LinearLayout ll_zhjc;
    private LinearLayout flfgLayout;
    private TextView tv_num_hlz;
    private TextView tv_num_fhd;
    private TextView tv_num_wzk;
    private TextView tv_num_qtzyd;
    private LinearLayout otherResourceLayout;
    private FrameLayout fl_hl;
    private FrameLayout fl_fh;
    private FrameLayout fl_wz;
    private FrameLayout fl_qt;
    private FrameLayout fl_rc;
    private LinearLayout ll_one;
    private LinearLayout ll_two;
    private LinearLayout ll_three;
    private LinearLayout ll_1;
    private LinearLayout ll_2;
    private LinearLayout ll_3;
    private LinearLayout ll_4;

    private void initPermission() {
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-forestprotection")){
            fl_hl.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-fireprovention")){
            fl_fh.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-material")){
            fl_wz.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-otherresourcce")){
            fl_qt.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-dailycheck")){
            fl_rc.setVisibility(View.GONE);
            ll_2.setVisibility(View.GONE);
        }
        if((!CommonUtils.hasPermission(getActivity(),"app-application-btn-forestprotection"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-fireprovention"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-material"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-otherresourcce"))){
            ll_1.setVisibility(View.GONE);
        }
        if((!CommonUtils.hasPermission(getActivity(),"app-application-btn-forestprotection"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-fireprovention"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-material"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-otherresourcce"))
        && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-dailycheck"))){
            ll_one.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-weather")){
            tianqiLayout.setVisibility(View.GONE);
            ll_3.setVisibility(View.GONE);
            ll_two.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-news")){
            flfgLayout.setVisibility(View.GONE);
            ll_4.setVisibility(View.GONE);
            ll_three.setVisibility(View.GONE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apps, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        bindView();
        initData();
    }

    private void initData() {
        for (int i = 0; i < 4; i++) {
            final int current = i;
            String resourceType = "";
            if(current == 0){
                resourceType = "checkStation";
            }
            if(current == 1){
                resourceType = "team";
            }
            if(current == 2){
                resourceType = "materialRepository";
            }
            if(current == 3){
                resourceType = "other";
            }
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectBody = new JSONObject();
            try {
                /*checkType =类型  3:护林检查站检查 4:物资库检查 5:防火队伍检查 6:隐患排查 当前状态 status = 1 待检测*/
                jsonObject.put("resourceType",resourceType);

                jsonObjectBody.put("dto",jsonObject);
                jsonObjectBody.put("limit",100);
//                jsonObjectBody.put("status",status);
//                jsonObjectBody.put("endTime", date);
//            jsonObjectBody.put("gridName","兰山街道");
//            jsonObjectBody.put("gridNo","121");
                jsonObjectBody.put("page",1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestParams params = new RequestParams(RequestUtils.REQUEST_URL + "resource/api/planResource/queryResource");
            params.setBodyContent(jsonObjectBody.toString());
            params.addHeader("Authorization", "bearer " + new DbConfig(getContext()).getUser().token);

            params.setConnectTimeout(10000);
            x.http().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    Log.e("bingo", "onSuccess: app " + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        if(object.getInt("code")==200){
                            JSONArray array = object.getJSONArray("data");
                            if(array!=null && array.length()>0){
                                JSONObject obj = (JSONObject) array.get(0);
                                int number = obj.getInt("totalSize");
                                Log.e("bingo", "onSuccess: " + number );
                                if(number != 0){
                                    if(current == 0){
                                        tv_num_hlz.setVisibility(View.VISIBLE);
                                        tv_num_hlz.setText(number>99?"99+":number+"");
                                    }
                                    if(current == 1){
                                        tv_num_fhd.setVisibility(View.VISIBLE);
                                        tv_num_fhd.setText(number>99?"99+":number+"");
                                    }
                                    if(current == 2){
                                        tv_num_wzk.setVisibility(View.VISIBLE);
                                        tv_num_wzk.setText(number>99?"99+":number+"");
                                    }
                                    if(current == 3){
                                        tv_num_qtzyd.setVisibility(View.VISIBLE);
                                        tv_num_qtzyd.setText(number>99?"99+":number+"");
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("bingo", "onError: " + ex.toString());
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
        fl_hl = ((FrameLayout) getView().findViewById(R.id.fl_hl));
        fl_fh = ((FrameLayout) getView().findViewById(R.id.fl_fh));
        fl_wz = ((FrameLayout) getView().findViewById(R.id.fl_wz));
        fl_qt = ((FrameLayout) getView().findViewById(R.id.fl_qt));
        fl_rc = ((FrameLayout) getView().findViewById(R.id.fl_rc));
        ll_one = ((LinearLayout) getView().findViewById(R.id.ll_one));
        ll_two = ((LinearLayout) getView().findViewById(R.id.ll_two));
        ll_three = ((LinearLayout) getView().findViewById(R.id.ll_three));
        ll_1 = ((LinearLayout) getView().findViewById(R.id.ll_1));
        ll_2 = ((LinearLayout) getView().findViewById(R.id.ll_2));
        ll_3 = ((LinearLayout) getView().findViewById(R.id.ll_3));
        ll_4 = ((LinearLayout) getView().findViewById(R.id.ll_4));
        hulinLayout = ((LinearLayout) getView().findViewById(R.id.hulin_layout));
        fanghuoLayout = ((LinearLayout) getView().findViewById(R.id.fanghuo_layout));
        wuziLayout = ((LinearLayout) getView().findViewById(R.id.wuziku_layout));
        kaoqinLayout = ((LinearLayout) getView().findViewById(R.id.kaoqin_layout));
        huibaoLayout = ((LinearLayout) getView().findViewById(R.id.huibao_layout));
        liuchengLayout = ((LinearLayout) getView().findViewById(R.id.liucheng_layout));
        daishenpiLayout = ((LinearLayout) getView().findViewById(R.id.daishenpi_layout));
        yinhuanpaichaLayout=((LinearLayout) getView().findViewById(R.id.yinhuanpaicha_layout));
        otherResourceLayout=((LinearLayout) getView().findViewById(R.id.ll_other));
        tianqiLayout=((LinearLayout) getView().findViewById(R.id.tianqi_layout));
        ll_zhjc=((LinearLayout) getView().findViewById(R.id.ll_zhjc));
        flfgLayout=((LinearLayout) getView().findViewById(R.id.flfg_layout));
        tv_num_hlz=((TextView) getView().findViewById(R.id.tv_num_hlz));
        tv_num_fhd=((TextView) getView().findViewById(R.id.tv_num_fhd));
        tv_num_wzk=((TextView) getView().findViewById(R.id.tv_num_wzk));
        tv_num_qtzyd=((TextView) getView().findViewById(R.id.tv_num_qtzyd));
        initPermission();
    }

    private void bindView() {
        /**
         * 火情上报
         */
/*        RxViewAction.clickNoDouble(huoqingLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.FireAdd)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });*/
//        RxViewAction.clickNoDouble(ll_zhjc).subscribe(unused -> {
//            Intent intent = new Intent(this,Compre);
//            startActivity(intent);
//        });
        /**
         * 护林站
         */
        RxViewAction.clickNoDouble(hulinLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.ProtectStation)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .withInt("checkType",3)
                                .withString("resourceType","checkStation")
                                .navigation();
                    }
                });
        /**
        /**
         * 防火队
         */
        RxViewAction.clickNoDouble(fanghuoLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.ProtectStation)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .withInt("checkType",4)
                                .withString("resourceType","team")
                                .navigation();
                    }
                });
        /**
        /**
         * 物资库
         */
        RxViewAction.clickNoDouble(wuziLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.ProtectStation)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .withInt("checkType",5)
                                .withString("resourceType","materialRepository")
                                .navigation();
                    }
                });
        /**
        /**
         * 隐患排查
         */
        RxViewAction.clickNoDouble(yinhuanpaichaLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.HiddenDangerrList)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
        /**
        /**
         * 其他资源点
         */
        RxViewAction.clickNoDouble(otherResourceLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.ProtectStation)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .withInt("checkType",7)
                                .withString("resourceType","other")
                                .navigation();
                    }
                });
        /**
         * 任务调度
         */
/*        RxViewAction.clickNoDouble(renwuLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ARouter.getInstance().build(RouteUtils.FireMissionList)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });*/

        /**
         * 待审批
         */
        RxViewAction.clickNoDouble(daishenpiLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), LeaveFlowApproveListActivity.class));
                    }
                });
        /**
         * 流程申请
         */
        RxViewAction.clickNoDouble(liuchengLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), LeaveFlowListActivity.class));
                    }
                });

        /**
         * 工作汇报
         */
        RxViewAction.clickNoDouble(huibaoLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), WorkReportListActivity.class));
                    }
                });
        /**
         * 我的考勤
         */
        RxViewAction.clickNoDouble(kaoqinLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), AttendanceActivity.class));
                    }
                });
        /**
         * 天气
         */
        RxViewAction.clickNoDouble(tianqiLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), OnlineWeatherActivity.class));
                    }
                });
        /**
         * 法律
         */
        RxViewAction.clickNoDouble(flfgLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getContext(), NewsActivity.class));
//                        startActivity(new Intent(getContext(), FalvActivity.class));
                    }
                });

    }

}
