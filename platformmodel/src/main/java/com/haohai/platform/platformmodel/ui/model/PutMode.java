package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by qc
 * on 2024/5/27.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PutMode {
    ///auth/api/auth/user put {"id":"","mode":}  mode 0：护林员模式 1：指挥车模式
    private String id;
    private int mode;

    public PutMode() {
    }

    public PutMode(String id, int mode) {
        this.id = id;
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
