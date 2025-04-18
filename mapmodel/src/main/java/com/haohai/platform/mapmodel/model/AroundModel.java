package com.haohai.platform.mapmodel.model;

/**
 * Created by qc
 * on 2022/5/31.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class AroundModel {
    private Position position;
    private String id;
    private String name;

    public AroundModel(Position position, String id, String name) {
        this.position = position;
        this.id = id;
        this.name = name;
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
        private double lat;
        private double lng;

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
