package com.haohai.platform.platformmodel.ui.model;

import java.util.List;

public class GridPointModel {
    GridMonitor monitor;
    List<GridCamera> cameraList;
    boolean status = false;
    String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public GridMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(GridMonitor monitor) {
        this.monitor = monitor;
    }

    public List<GridCamera> getCameraList() {
        return cameraList;
    }

    public void setCameraList(List<GridCamera> cameraList) {
        this.cameraList = cameraList;
    }

    @Override
    public String toString() {
        return "GridPointModel{" +
                "monitor=" + monitor +
                ", cameraList=" + cameraList +
                ", status=" + status +
                ", parentName='" + parentName + '\'' +
                '}';
    }
}
