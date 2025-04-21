package com.haohai.platform.platformmodel.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.ruyiruyi.rylibrary.ui.HiddenListActivity;
import com.ruyiruyi.rylibrary.ui.WeatherActivity;
import com.ruyiruyi.rylibrary.ui.web.WebsActivity;

import rx.functions.Action1;

/**
 * Created by geyang on 2020/7/2.
 */

public class AppsFragment extends HhBaseFragment {
    private LinearLayout kaoqinLayout;
    private LinearLayout huibaoLayout;
    private LinearLayout liuchengLayout;
    private LinearLayout daishenpiLayout;
    private LinearLayout huoqingLayout;
    private LinearLayout renwuLayout;
    private LinearLayout ducha_layout;
    private LinearLayout level_layout;
    private LinearLayout weather_layout;
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

        huoqingLayout = ((LinearLayout) getView().findViewById(R.id.huoqing_layout));
        renwuLayout = ((LinearLayout) getView().findViewById(R.id.renwu_layout));
        ducha_layout = ((LinearLayout) getView().findViewById(R.id.ducha_layout));
        level_layout = ((LinearLayout) getView().findViewById(R.id.level_layout));
        weather_layout = ((LinearLayout) getView().findViewById(R.id.weather_layout));
        yinhuanpaichaLayout=((LinearLayout) getView().findViewById(R.id.yinhuanpaicha_layout));
    }

    private void bindView() {
        /**
         * 火情上报
         */
        RxViewAction.clickNoDouble(huoqingLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        ARouter.getInstance().build(RouteUtils.FireUpload)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
        /**
         * 隐患排查
         */
        RxViewAction.clickNoDouble(yinhuanpaichaLayout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        /*ARouter.getInstance().build(RouteUtils.HiddenDangerr)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();*/

                        Intent intent = new Intent(getContext(), HiddenListActivity.class);
                        startActivity(intent);
                    }
                });
        /**
         * 督查督办
         */
        RxViewAction.clickNoDouble(ducha_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        ARouter.getInstance().build(RouteUtils.DuchaList)
                                .withString("token",new DbConfig(getContext()).getUser().getToken())
                                .navigation();
                    }
                });
        /**
         * 火险等级
         */
        RxViewAction.clickNoDouble(level_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(getContext(), WebsActivity.class);
                        //intent.putExtra("webUrl", "https://www.baidu.com");
                        //intent.putExtra("webUrl", "https://www.qdsylj.com/garden_h5/?userName="+"用户"+"&userId="+"136"+"&userPost="+"管理员");
                        intent.putExtra("webUrl", "http://117.132.5.139:6801/#/fireLevel");
                        intent.putExtra("title", "火险等级");
                        startActivity(intent);
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
         * 天气
         */
        RxViewAction.clickNoDouble(weather_layout)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(getActivity(), WeatherActivity.class));
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
