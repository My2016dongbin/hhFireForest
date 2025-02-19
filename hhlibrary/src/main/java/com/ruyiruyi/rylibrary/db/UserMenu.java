package com.ruyiruyi.rylibrary.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.converter.BooleanColumnConverter;

/**
 * Created by Administrator on 2021/5/22.
 */
@Table(name = "usermenu")
public class UserMenu {
    @Column(name = "id",isId = true,autoGen = true)
    private String id;
    //    卫星
    @Column(name = "appmapbtnsatellitefirealarm",autoGen = true)
    private boolean appMapBtnSatelliteFirealarm =true;
    //    火情列表
    @Column(name = "appsatellitefirealarmbtnlist")
    private boolean appSatelliteFirealarmBtnList= true;
    //    火情查询
    @Column(name = "appsatellitefirealarmbtnquery")
    private boolean appSatelliteFirealarmBtnQuery= true;
    //    卫星设置
    @Column(name = "appsatellitefirealarmbtnsetting")
    private boolean appSatelliteFirealarmBtnSetting= true;
//    资源
    @Column(name = "appmapbtnresource")
    private boolean appMapBtnResource= true;
//    报警
    @Column(name = "appmapbtnfirealarm")
    private boolean appMapBtnFirealarm= true;
//    任务
    @Column(name = "appmapbtntask")
    private boolean appMapBtnTask= true;
//    方向控制
    @Column(name = "appvideobtndirectioncontrol")
    private boolean appVideoBtnDirectionControl= true;
//    拉进拉远聚焦
    @Column(name = "appvideobtnzoomcontrol")
    private boolean appVideoBtnZoomControl= true;
//    火情上报
    @Column(name = "appapplicationbtnreport")
    private boolean appApplicationBtnReport= true;
//    保存
    @Column(name = "appreportbtnadd")
    private boolean appReportBtnAdd= true;
//    隐患排查
    @Column(name = "appapplicationbtndangercheck")
    private boolean appApplicationBtnDangerCheck= true;
    //    保存
    @Column(name = "appdangercheckbtnadd")
    private boolean appDangerCheckBtnAdd= true;
//    调度任务
    @Column(name = "appapplicationbtntask")
    private boolean appApplicationBtnTask= true;
//    实时位置上传
    @Column(name = "appsettingbtnposition")
    private boolean appSettingBtnPosition= true;
    //    首页地图
    @Column(name = "appmap")
    private boolean appmap= true;
    //    首页视频
    @Column(name = "appvideo")
    private boolean appvideo= true;
    //    首页应用
    @Column(name = "appapplication")
    private boolean appapplication= true;

    //    首页我的
    @Column(name = "appsetting")
    private boolean appsetting= true;

    public UserMenu() {

    }

    public UserMenu(boolean appMapBtnSatelliteFirealarm, boolean appSatelliteFirealarmBtnList, boolean appSatelliteFirealarmBtnQuery, boolean appSatelliteFirealarmBtnSetting, boolean appMapBtnResource, boolean appMapBtnFirealarm, boolean appMapBtnTask, boolean appVideoBtnDirectionControl, boolean appVideoBtnZoomControl, boolean appApplicationBtnReport, boolean appReportBtnAdd, boolean appApplicationBtnDangerCheck, boolean appDangerCheckBtnAdd, boolean appApplicationBtnTask, boolean appSettingBtnPosition,boolean appmap,boolean appvideo,boolean appapplication,boolean appsetting) {
        this.appMapBtnSatelliteFirealarm = appMapBtnSatelliteFirealarm;
        this.appSatelliteFirealarmBtnList = appSatelliteFirealarmBtnList;
        this.appSatelliteFirealarmBtnQuery = appSatelliteFirealarmBtnQuery;
        this.appSatelliteFirealarmBtnSetting = appSatelliteFirealarmBtnSetting;
        this.appMapBtnResource = appMapBtnResource;
        this.appMapBtnFirealarm = appMapBtnFirealarm;
        this.appMapBtnTask = appMapBtnTask;
        this.appVideoBtnDirectionControl = appVideoBtnDirectionControl;
        this.appVideoBtnZoomControl = appVideoBtnZoomControl;
        this.appApplicationBtnReport = appApplicationBtnReport;
        this.appReportBtnAdd = appReportBtnAdd;
        this.appApplicationBtnDangerCheck = appApplicationBtnDangerCheck;
        this.appDangerCheckBtnAdd = appDangerCheckBtnAdd;
        this.appApplicationBtnTask = appApplicationBtnTask;
        this.appSettingBtnPosition = appSettingBtnPosition;
        this.appmap = appmap;
        this.appvideo=appvideo;
        this.appapplication=appapplication;
        this.appsetting=appsetting;
    }


    public boolean getAppMapBtnSatelliteFirealarm() {
        return appMapBtnSatelliteFirealarm;
    }

    public void setAppMapBtnSatelliteFirealarm(boolean appMapBtnSatelliteFirealarm) {
        this.appMapBtnSatelliteFirealarm = appMapBtnSatelliteFirealarm;
    }

    public boolean getAppSatelliteFirealarmBtnList() {
        return appSatelliteFirealarmBtnList;
    }

    public void setAppSatelliteFirealarmBtnList(boolean appSatelliteFirealarmBtnList) {
        this.appSatelliteFirealarmBtnList = appSatelliteFirealarmBtnList;
    }

    public boolean getAppSatelliteFirealarmBtnQuery() {
        return appSatelliteFirealarmBtnQuery;
    }

    public void setAppSatelliteFirealarmBtnQuery(boolean appSatelliteFirealarmBtnQuery) {
        this.appSatelliteFirealarmBtnQuery = appSatelliteFirealarmBtnQuery;
    }

    public boolean getAppSatelliteFirealarmBtnSetting() {
        return appSatelliteFirealarmBtnSetting;
    }

    public void setAppSatelliteFirealarmBtnSetting(boolean appSatelliteFirealarmBtnSetting) {
        this.appSatelliteFirealarmBtnSetting = appSatelliteFirealarmBtnSetting;
    }

    public boolean getAppMapBtnResource() {
        return appMapBtnResource;
    }

    public void setAppMapBtnResource(boolean appMapBtnResource) {
        this.appMapBtnResource = appMapBtnResource;
    }

    public boolean getAppMapBtnFirealarm() {
        return appMapBtnFirealarm;
    }

    public void setAppMapBtnFirealarm(boolean appMapBtnFirealarm) {
        this.appMapBtnFirealarm = appMapBtnFirealarm;
    }

    public boolean getAppMapBtnTask() {
        return appMapBtnTask;
    }

    public void setAppMapBtnTask(boolean appMapBtnTask) {
        this.appMapBtnTask = appMapBtnTask;
    }

    public boolean getAppVideoBtnDirectionControl() {
        return appVideoBtnDirectionControl;
    }

    public void setAppVideoBtnDirectionControl(boolean appVideoBtnDirectionControl) {
        this.appVideoBtnDirectionControl = appVideoBtnDirectionControl;
    }

    public boolean getAppVideoBtnZoomControl() {
        return appVideoBtnZoomControl;
    }

    public void setAppVideoBtnZoomControl(boolean appVideoBtnZoomControl) {
        this.appVideoBtnZoomControl = appVideoBtnZoomControl;
    }

    public boolean getAppApplicationBtnReport() {
        return appApplicationBtnReport;
    }

    public void setAppApplicationBtnReport(boolean appApplicationBtnReport) {
        this.appApplicationBtnReport = appApplicationBtnReport;
    }

    public boolean getAppReportBtnAdd() {
        return appReportBtnAdd;
    }

    public void setAppReportBtnAdd(boolean appReportBtnAdd) {
        this.appReportBtnAdd = appReportBtnAdd;
    }

    public boolean getAppApplicationBtnDangerCheck() {
        return appApplicationBtnDangerCheck;
    }

    public void setAppApplicationBtnDangerCheck(boolean appApplicationBtnDangerCheck) {
        this.appApplicationBtnDangerCheck = appApplicationBtnDangerCheck;
    }

    public boolean getAppDangerCheckBtnAdd() {
        return appDangerCheckBtnAdd;
    }

    public void setAppDangerCheckBtnAdd(boolean appDangerCheckBtnAdd) {
        this.appDangerCheckBtnAdd = appDangerCheckBtnAdd;
    }

    public boolean getAppApplicationBtnTask() {
        return appApplicationBtnTask;
    }

    public void setAppApplicationBtnTask(boolean appApplicationBtnTask) {
        this.appApplicationBtnTask = appApplicationBtnTask;
    }

    public boolean getAppSettingBtnPosition() {
        return appSettingBtnPosition;
    }

    public void setAppSettingBtnPosition(boolean appSettingBtnPosition) {
        this.appSettingBtnPosition = appSettingBtnPosition;
    }

    public boolean isAppmap() {
        return appmap;
    }

    public void setAppmap(boolean appmap) {
        this.appmap = appmap;
    }

    public boolean isAppvideo() {
        return appvideo;
    }

    public void setAppvideo(boolean appvideo) {
        this.appvideo = appvideo;
    }

    public boolean isAppapplication() {
        return appapplication;
    }

    public void setAppapplication(boolean appapplication) {
        this.appapplication = appapplication;
    }

    public boolean isAppsetting() {
        return appsetting;
    }

    public void setAppsetting(boolean appsetting) {
        this.appsetting = appsetting;
    }
}