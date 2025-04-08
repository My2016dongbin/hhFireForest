package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by Administrator on 2021/7/14.
 */

public class JwdModel {
    private Double longitude;
    private Double latitude;

    public JwdModel(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

}
