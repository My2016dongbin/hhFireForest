package com.ruyiruyi.rylibrary.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "beidoulocation")
public class BeiDouLocation {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;

    @Column(name = "date")
    public String date;

    @Column(name = "longdate")
    public long longDate;

    @Column(name = "latitude")
    public double latitude;

    @Column(name = "longitude")
    public double longitude;

    @Column(name = "location")
    public String location;

    public BeiDouLocation() {
    }

    public BeiDouLocation(String id, String date, long longDate, double latitude, double longitude, String location) {
        this.id = id;
        this.date = date;
        this.longDate = longDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getLongDate() {
        return longDate;
    }

    public void setLongDate(long longDate) {
        this.longDate = longDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return
                "id = " + id +
                " date = " + date +
                " longDate = " + longDate +
                " latitude = " + latitude +
                " longitude = " + longitude +
                " location = " + location
                ;
    }
}
