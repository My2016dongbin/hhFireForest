package com.haohai.platform.mapmodel.multitype;

public class OrderPlanPosition {
    double lng;
    double lat;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "OrderPlanPosition{" +
                "lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
