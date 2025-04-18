package com.haohai.platform.mapmodel.multitype;

public class CheckDetail {
    public String id;
    public String planId;
    public String status;
    public String statusName;
    public boolean isNow;

    public CheckDetail(String id, String planId, String status, String statusName, boolean isNow) {
        this.id = id;
        this.planId = planId;
        this.status = status;
        this.statusName = statusName;
        this.isNow = isNow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    public boolean isNow() {
        return isNow;
    }

    public void setNow(boolean now) {
        isNow = now;
    }
}
