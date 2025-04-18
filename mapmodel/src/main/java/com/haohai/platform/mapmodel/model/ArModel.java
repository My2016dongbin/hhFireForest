package com.haohai.platform.mapmodel.model;

/**
 * Created by qc
 * on 2022/6/2.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ArModel {
    private float lng;
    private float lat;
    private String dsName;

    public ArModel(float lng, float lat, String name) {
        this.lng = lng;
        this.lat = lat;
        this.dsName = name;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public String getName() {
        return dsName;
    }

    public void setName(String dsName) {
        this.dsName = dsName;
    }
}
