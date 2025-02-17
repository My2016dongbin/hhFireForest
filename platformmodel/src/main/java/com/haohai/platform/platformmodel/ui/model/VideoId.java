package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by Administrator on 2021/2/5.
 */

public class VideoId {
    private String monitorId;
    private String serial;
    private String videoId;
    private String videoName;
    private String sId;
    private int videoPlayer;

    public VideoId(String monitorId,String serial,String videoId, String videoName, int videoPlayer,String sId) {
        this.monitorId = monitorId;
        this.serial = serial;
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
        this.sId = sId;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public VideoId(String monitorId, String serial, String videoId, String videoName, int videoPlayer) {
        this.monitorId = monitorId;
        this.serial = serial;
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
    }

    public VideoId(String videoId, String videoName, int videoPlayer) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getVideoPlayer() {
        return videoPlayer;
    }

    public void setVideoPlayer(int videoPlayer) {
        this.videoPlayer = videoPlayer;
    }

    @Override
    public String toString() {
        return "VideoId{" +
                "monitorId='" + monitorId + '\'' +
                ", serial='" + serial + '\'' +
                ", videoId='" + videoId + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoPlayer=" + videoPlayer +
                '}';
    }
}
