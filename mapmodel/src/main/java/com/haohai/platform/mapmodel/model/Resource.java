package com.haohai.platform.mapmodel.model;

/**
 * Created by qc
 * on 2022/9/27.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Resource {
    private String id;
    private String name;
    private String resourceName;
    private String address;
    private Position position;

    public Resource() {
    }

    public Resource(String id, String name, String address, Position position) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.position = position;
    }

    public Resource(String id, String name, String resourceName, String address, Position position) {
        this.id = id;
        this.name = name;
        this.resourceName = resourceName;
        this.address = address;
        this.position = position;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name==null?resourceName:name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static class Position{
        private double lng;
        private double lat;

        public Position(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

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
    }
}
