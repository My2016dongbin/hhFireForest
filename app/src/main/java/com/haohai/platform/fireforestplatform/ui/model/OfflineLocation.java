package com.haohai.platform.fireforestplatform.ui.model;

public class OfflineLocation {
    String actualUploadTime;//实际上传的时间
    String cityName;//当前轨迹所在的城市
    String createTime;//创建时间
    String createUser;//创建人
    String deptId;//部门id
    int distance;//与上一轨迹之间的距离
    String groupId;//组织id
    String id;//主键
    int intervalTime;//间隔时间
    String offlineUploadTime;//离线缓存上传的时间   upload
    String phoneCharge;//手机的电量
    String phoneSignal;//手机的信号强度
    LatLng position;//位置信息   upload
    String positionState;//用户的轨迹状态
    String reserve;//储备字段
    String updateTime;//更新时间
    String updateUser;//更新人
    String userId;//用户id
    String userName;//用户名

    public OfflineLocation() {
    }

    public OfflineLocation(String actualUploadTime, String cityName, String createTime, String createUser, String deptId, int distance, String groupId, String id, int intervalTime, String offlineUploadTime, String phoneCharge, String phoneSignal, LatLng position, String positionState, String reserve, String updateTime, String updateUser, String userId, String userName) {
        this.actualUploadTime = actualUploadTime;
        this.cityName = cityName;
        this.createTime = createTime;
        this.createUser = createUser;
        this.deptId = deptId;
        this.distance = distance;
        this.groupId = groupId;
        this.id = id;
        this.intervalTime = intervalTime;
        this.offlineUploadTime = offlineUploadTime;
        this.phoneCharge = phoneCharge;
        this.phoneSignal = phoneSignal;
        this.position = position;
        this.positionState = positionState;
        this.reserve = reserve;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.userId = userId;
        this.userName = userName;
    }

    public String getActualUploadTime() {
        return actualUploadTime;
    }

    public void setActualUploadTime(String actualUploadTime) {
        this.actualUploadTime = actualUploadTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getOfflineUploadTime() {
        return offlineUploadTime;
    }

    public void setOfflineUploadTime(String offlineUploadTime) {
        this.offlineUploadTime = offlineUploadTime;
    }

    public String getPhoneCharge() {
        return phoneCharge;
    }

    public void setPhoneCharge(String phoneCharge) {
        this.phoneCharge = phoneCharge;
    }

    public String getPhoneSignal() {
        return phoneSignal;
    }

    public void setPhoneSignal(String phoneSignal) {
        this.phoneSignal = phoneSignal;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getPositionState() {
        return positionState;
    }

    public void setPositionState(String positionState) {
        this.positionState = positionState;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static class LatLng{
        double lat;
        double lng;

        public LatLng() {
        }

        public LatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

    }
}
