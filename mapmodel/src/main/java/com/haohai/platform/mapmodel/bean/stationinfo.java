package com.haohai.platform.mapmodel.bean;

public class stationinfo {
    public String name;
    public String picture;
    public String id;
    public PositionFirejd position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public stationinfo(String name, String picture, String id, PositionFirejd position) {
        this.name = name;
        this.picture = picture;
        this.id = id;
        this.position = position;
    }

    public static class PositionFirejd {
        public double lng;
        public double lat;

        public PositionFirejd(double lng, double lat) {
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

    public PositionFirejd getPosition() {
        return position;
    }

    public void setPosition(PositionFirejd position) {
        this.position = position;
    }
}
