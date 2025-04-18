package com.haohai.platform.platformmodel.ui.model;

import java.util.List;

/**
 * Created by qc
 * on 2022/9/1.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class GridModel {
    GridMonitor monitor;
    List<GridCamera> cameraList;

    boolean status = false;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public GridModel(GridMonitor monitor, List<GridCamera> cameraList) {
        this.monitor = monitor;
        this.cameraList = cameraList;
    }

    public GridModel() {
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
}
