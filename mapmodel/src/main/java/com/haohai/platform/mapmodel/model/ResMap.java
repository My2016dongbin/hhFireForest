package com.haohai.platform.mapmodel.model;

/**
 * Created by qc
 * on 2022/9/20.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class ResMap {
    private String id;
    private String name;
    private Position position;

    public ResMap() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    public static class Position {
        private double lat = 0;
        private double lng = 0;

        public Position() {
        }

        public Position(double lat, double lng) {
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
