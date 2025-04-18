package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by qc
 * on 2022/6/13.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class News {
    private String createUser;
    private String updateUser;
    private String createTime;
    private String updateTime;
    private String groupId;
    private String id;
    private String name;
    private String url;
    private String remark;
    private String describes;
    private String state;

    public News(String createUser, String updateUser, String createTime, String updateTime, String groupId, String id, String name, String url, String remark, String describes, String state) {
        this.createUser = createUser;
        this.updateUser = updateUser;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.url = url;
        this.remark = remark;
        this.describes = describes;
        this.state = state;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
