package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by Administrator on 2021/2/5.
 */

public class VideoId {
    private String monitorId;
    private String videoId;
    private String videoName;
    private int videoPlayer;

    private String controlMonitorId;
    private String controlControlId;

    public String getControlMonitorId() {
        return controlMonitorId;
    }

    public void setControlMonitorId(String controlMonitorId) {
        this.controlMonitorId = controlMonitorId;
    }

    public String getControlControlId() {
        return controlControlId;
    }

    public void setControlControlId(String controlControlId) {
        this.controlControlId = controlControlId;
    }

    public VideoId(String monitorId, String videoId, String videoName, int videoPlayer,String controlMonitorId,String controlControlId) {
        this.monitorId = monitorId;
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
        this.controlMonitorId = controlMonitorId;
        this.controlControlId = controlControlId;
    }
    public VideoId(String monitorId, String videoId, String videoName, int videoPlayer) {
        this.monitorId = monitorId;
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
    }

    public VideoId(String videoId, String videoName, int videoPlayer) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
    }
    public VideoId(String videoId, String videoName, int videoPlayer,String chennelId) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoPlayer = videoPlayer;
        this.monitorId = chennelId;
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
}