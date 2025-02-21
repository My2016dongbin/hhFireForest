package com.ruyiruyi.rylibrary.db;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.ruyiruyi.rylibrary.bus.BeidouRefresh;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

public class DbConfig {
    public Context context;

    public DbConfig(Context context) {
        this.context = context;
    }

    public DbManager.DaoConfig getDaoConfig() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("platform.db")
                .setAllowTransaction(true)
                .setDbDir(context.getFilesDir())
                //.setDbDir(Environment.getExternalStorageDirectory())
                .setDbVersion(12);

        return daoConfig;
    }
    public DbManager getDbManager(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        return db;
    }

    public void sendBeiDouMessage(BeidouNews beidouNews){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            db.saveOrUpdate(beidouNews);
        } catch (DbException e) {
        }
    }
    public String getBeiDouPersonNameById(String id){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeidouPerson> beidouPersonList = db.selector(BeidouPerson.class)
                    .where("id","=",id)
                    .findAll();
            if(beidouPersonList!=null && beidouPersonList.size()!=0){
                return beidouPersonList.get(0).getName();
            }

        } catch (DbException e) {
        }
        return "";
    }
    public List<BeidouPerson> getBeiDouPersonList(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeidouPerson> beidouPersonList = db.selector(BeidouPerson.class)
                    .where("unread",">",0)
                    .orderBy("updatelong",true)
                    .findAll();
            List<BeidouPerson> beidouPersonList2 = db.selector(BeidouPerson.class)
                    .where("unread","=",0)
                    .orderBy("updatelong",true)
                    .findAll();
            if(beidouPersonList!=null){
                if(beidouPersonList2!=null){
                    beidouPersonList.addAll(beidouPersonList2);
                }
                return beidouPersonList;
            }else{
                return beidouPersonList2;
            }

        } catch (DbException e) {
        }
        return null;
    }

    public void setReadStateByPersonId(String id){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        boolean changed = false;
        try {
            ///消息
            List<BeidouNews> beidouNewsList = db.selector(BeidouNews.class)
                    .where("personid","=",id).findAll();
            if(beidouNewsList==null || beidouNewsList.size()==0){
                return;
            }
            for (int i = 0; i < beidouNewsList.size(); i++) {
                BeidouNews beidouNews = beidouNewsList.get(i);
                if(beidouNews.getState() == 0){
                    changed = true;
                    beidouNews.setState(1);
                    db.saveOrUpdate(beidouNews);
                }
            }

            //(有未读消息状态改变时)
            if(changed){
                ///列表
                List<BeidouPerson> beidouPersonList = db.selector(BeidouPerson.class)
                        .where("id","=",id).findAll();
                if(beidouPersonList.size()>0){
                    BeidouPerson beidouPerson = beidouPersonList.get(0);
                    beidouPerson.setCount(beidouNewsList.size());
                    beidouPerson.setUnRead(0);
                    db.saveOrUpdate(beidouPerson);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //刷新通讯录列表
                        EventBus.getDefault().post(new BeidouRefresh());
                    }
                },1000);
            }

        } catch (DbException e) {
        }
    }

    public BeidouPerson getBeiDouPersonById(String id){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeidouPerson> list = db.selector(BeidouPerson.class)
                    .where("id","=",id).findAll();
            if(list!=null && list.size()!=0){
                return list.get(0);
            }

        } catch (DbException e) {
        }
        return null;
    }

    public List<BeidouNews> getBeiDouNewsListByPersonId(String id){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeidouNews> beidouNewsList = db.selector(BeidouNews.class)
                    .where("personid","=",id)
                    .orderBy("updatelong")
                    .findAll();
            return beidouNewsList;

        } catch (DbException e) {
        }
        return null;
    }

    public List<BeidouNews> getBeiDouNewsLoading(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeidouNews> beidouNewsList = db.selector(BeidouNews.class)
                    .where("state","=",-1)
                    .findAll();
            return beidouNewsList;

        } catch (DbException e) {
        }
        return null;
    }
    public void clearLocations(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            db.delete(BeiDouLocation.class);
        } catch (DbException e) {
        }
    }
    public List<BeiDouLocation> getBeiDouLocationList(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeiDouLocation> locations = db.selector(BeiDouLocation.class)
                    .findAll();
            return locations;

        } catch (DbException e) {
        }
        return null;
    }
    public List<BeidouNews> getBeiDouNewsList(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<BeidouNews> beidouNewsList = db.selector(BeidouNews.class)
                    .findAll();
            return beidouNewsList;

        } catch (DbException e) {
        }
        return null;
    }
    public List<Area> getAreaList(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<Area> areaList = db.selector(Area.class)
                    .findAll();
            return areaList;

        } catch (DbException e) {
        }
        return null;
    }
    public List<Department> getDepartment(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<Department> departmentList = db.selector(Department.class)
                    .findAll();
            return departmentList;

        } catch (DbException e) {
        }
        return null;
    }
    public List<UserModel> getUserModel(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<UserModel> userModelList = db.selector(UserModel.class)
                    .findAll();
            return userModelList;

        } catch (DbException e) {
        }
        return null;
    }

    public Attendance getAttendance(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<Attendance> attendances = db.selector(Attendance.class)
                    .findAll();
            if (attendances != null){
                return attendances.get(0);
            }

        } catch (DbException e) {
        }
        return null;
    }

    public User getUser(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<User> users = db.selector(User.class)
                    .findAll();
            if (users != null){
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    if (user.getIsLogin() == 1){
                        return user;
                    }
                }
            }

        } catch (DbException e) {
        }
        return null;
    }

    public User getUserOut(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<User> users = db.selector(User.class)
                    .findAll();
            if (users != null){
                User user = users.get(0);
                return user;

            }

        } catch (DbException e) {
        }
        return null;
    }

    public Setting getSetting(){
        DbManager.DaoConfig daoConfig = getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        try {
            List<Setting> settings = db.selector(Setting.class)
                    .findAll();
            if (settings != null){
                Setting setting = settings.get(0);
                return setting;
            }

        } catch (DbException e) {
        }
        return null;
    }
}
