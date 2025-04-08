package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by qc
 * on 2022/5/7.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class HistoryLine {
    private String startTime;
    private String endTime;
    private String userName;
    private String id;
    private int page;
    private int limit;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
