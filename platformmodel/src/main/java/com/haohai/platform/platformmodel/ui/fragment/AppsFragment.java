package com.haohai.platform.platformmodel.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.haohai.platform.platformmodel.R;
import com.haohai.platform.platformmodel.ui.acticity.AttendanceActivity;
import com.haohai.platform.platformmodel.ui.acticity.LeaveFlowApproveListActivity;
import com.haohai.platform.platformmodel.ui.acticity.LeaveFlowListActivity;
import com.haohai.platform.platformmodel.ui.acticity.WorkReportListActivity;
import com.haohai.platform.platformmodel.ui.fragment.base.HhBaseFragment;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.db.DbConfig;
import com.ruyiruyi.rylibrary.route.RouteUtils;
import com.ruyiruyi.rylibrary.utils.CommonUtils;

import rx.functions.Action1;

/**
 * Created by geyang on 2020/7/2.
 */

public class AppsFragment extends HhBaseFragment {
    private LinearLayout kaoqinLayout;
    private LinearLayout huibaoLayout;
    private LinearLayout liuchengLayout;
    private LinearLayout daishenpiLayout;
    private LinearLayout ll_1;
    private LinearLayout huoqingLayout;
    private LinearLayout renwuLayout;
    private LinearLayout yinhuanpaichaLayout;

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
    }


    private void initView() {
        kaoqinLayout = ((LinearLayout) getView().findViewById(R.id.kaoqin_layout));
        huibaoLayout = ((LinearLayout) getView().findViewById(R.id.huibao_layout));
        liuchengLayout = ((LinearLayout) getView().findViewById(R.id.liucheng_layout));
        daishenpiLayout = ((LinearLayout) getView().findViewById(R.id.daishenpi_layout));

        ll_1 = ((LinearLayout) getView().findViewById(R.id.ll_1));
        huoqingLayout = ((LinearLayout) getView().findViewById(R.id.huoqing_layout));
        renwuLayout = ((LinearLayout) getView().findViewById(R.id.renwu_layout));
        yinhuanpaichaLayout=((LinearLayout) getView().findViewById(R.id.yinhuanpaicha_layout));
        initPermission();
    }

    private void initPermission() {
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-fire")){//TODO 应用权限
            huoqingLayout.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-danger")){
            yinhuanpaichaLayout.setVisibility(View.GONE);
        }
        if(!CommonUtils.hasPermission(getActivity(),"app-application-btn-task")){
            renwuLayout.setVisibility(View.GONE);
        }
        if((!CommonUtils.hasPermission(getActivity(),"app-application-btn-fire"))
                && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-danger"))
                && (!CommonUtils.hasPermission(getActivity(),"app-application-btn-task"))){
            ll_1.setVisibility(View.GONE);
        }
    }

    private void bindView() {
        /**
         * 火情上报
         */
        RxViewAction.clickNoDouble(huoqingLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.FireAdd)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
        /**
         * 火情上报
         */
        RxViewAction.clickNoDouble(yinhuanpaichaLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.HiddenDangerr)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
        /**
         * 任务调度
         */
        RxViewAction.clickNoDouble(renwuLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ARouter.getInstance().build(RouteUtils.FireMissionList)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });

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
    }

}
