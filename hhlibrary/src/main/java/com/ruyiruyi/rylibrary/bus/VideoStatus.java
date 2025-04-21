package com.ruyiruyi.rylibrary.bus;

/**
 * Created by qc
 * on 2024/1/18.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class VideoStatus {
    private boolean status;

    public VideoStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
