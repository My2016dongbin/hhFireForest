package com.haohai.platform.mapmodel.bean;

public class UploadPostUserPost {
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

    String fireName;
    double latitude;
    double longitude;
    String address;
    String reporter;
    String discoverTime;
    String reportTime;
    String comment;
    String picPath1;
    String picPath2;
    String videoPath1;

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getFireName() {
        return fireName;
    }

    public void setFireName(String fireName) {
        this.fireName = fireName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getDiscoverTime() {
        return discoverTime;
    }

    public void setDiscoverTime(String discoverTime) {
        this.discoverTime = discoverTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPicPath1() {
        return picPath1;
    }

    public void setPicPath1(String picPath1) {
        this.picPath1 = picPath1;
    }

    public String getPicPath2() {
        return picPath2;
    }

    public void setPicPath2(String picPath2) {
        this.picPath2 = picPath2;
    }

    public String getVideoPath1() {
        return videoPath1;
    }

    public void setVideoPath1(String videoPath1) {
        this.videoPath1 = videoPath1;
    }

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
        return "UploadPostUserPost{" +
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
                ", fireName='" + fireName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", reporter='" + reporter + '\'' +
                ", discoverTime='" + discoverTime + '\'' +
                ", reportTime='" + reportTime + '\'' +
                ", comment='" + comment + '\'' +
                ", picPath1='" + picPath1 + '\'' +
                ", picPath2='" + picPath2 + '\'' +
                ", videoPath1='" + videoPath1 + '\'' +
                '}';
    }
}
