package com.haohai.platform.mapmodel.model;

import java.util.Arrays;

/**
 * Created by qc
 * on 2022/9/16.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ResModel {
    private String method;
    private Object[] args;
    private boolean adding;

    public ResModel() {
    }

    public ResModel(String method, Object[] args, boolean adding) {
        this.method = method;
        this.args = args;
        this.adding = adding;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public boolean isAdding() {
        return adding;
    }

    public void setAdding(boolean adding) {
        this.adding = adding;
    }

    @Override
    public String toString() {
        return "ResModel{" +
                "method='" + method + '\'' +
                ", args=" + Arrays.toString(args) +
                ", adding=" + adding +
                '}';
    }
}
