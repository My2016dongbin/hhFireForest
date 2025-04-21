package com.haohai.platform.mapmodel.bean;

public class UploadPost {
    String taskType;
    String createUser;
    String groupId;
    String description;
    String memberName;
    String userId;
    String taskContent;
    String taskRegion;
    String taskImg;
    String reserve;
    String createTime;
    LatLngModel position;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getTaskRegion() {
        return taskRegion;
    }

    public void setTaskRegion(String taskRegion) {
        this.taskRegion = taskRegion;
    }

    public String getTaskImg() {
        return taskImg;
    }

    public void setTaskImg(String taskImg) {
        this.taskImg = taskImg;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public LatLngModel getPosition() {
        return position;
    }

    public void setPosition(LatLngModel position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "UploadPost{" +
                "taskType='" + taskType + '\'' +
                ", createUser='" + createUser + '\'' +
                ", groupId='" + groupId + '\'' +
                ", description='" + description + '\'' +
                ", memberName='" + memberName + '\'' +
                ", userId='" + userId + '\'' +
                ", taskContent='" + taskContent + '\'' +
                ", taskRegion='" + taskRegion + '\'' +
                ", taskImg='" + taskImg + '\'' +
                ", reserve='" + reserve + '\'' +
                ", createTime='" + createTime + '\'' +
                ", position=" + position +
                '}';
    }
}
