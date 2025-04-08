package com.haohai.platform.platformmodel.ui.model;

/**
 * Created by qc
 * on 2022/5/7.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class CommandModel {
    private String priority;
    private String taskContent;
    private String taskStartTime;
    private String taskEndTime;
    private String taskImg;
    private Position position;
    private String taskType;

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getTaskImg() {
        return taskImg;
    }

    public void setTaskImg(String taskImg) {
        this.taskImg = taskImg;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public static class Position{
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
