package com.haohai.platform.mapmodel.bean;

import com.haohai.platform.mapmodel.multitype.checkuser;

import java.util.List;

public class AddCompresive {

    public int checkStationCount;
    public String checkUserName;
    public List<checkuser> checkuserVOS;
    public String description;
    public String startTime;
    public String endTime;
    public String gridName;
    public String gridNo;
    public List<ImgsFirejd> imgs;
    public String name;
    public List<planResourceDTOS> planResourceDTOS;
    public int status;
    public int type;

    public static class ImgsFirejd {
        public String img;
        public int type;

        public ImgsFirejd(String img, int type) {
            this.img = img;
            this.type = type;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public AddCompresive() {
    }

    public AddCompresive(int checkStationCount, String checkUserName, List<checkuser> checkuserVOS, String description, String startTime, String endTime, String gridName, String gridNo, List<ImgsFirejd> imgs, String name, List<com.haohai.platform.mapmodel.bean.planResourceDTOS> planResourceDTOS, int status, int type) {
        this.checkStationCount = checkStationCount;
        this.checkUserName = checkUserName;
        this.checkuserVOS = checkuserVOS;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gridName = gridName;
        this.gridNo = gridNo;
        this.imgs = imgs;
        this.name = name;
        this.planResourceDTOS = planResourceDTOS;
        this.status = status;
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public int getCheckStationCount() {
        return checkStationCount;
    }

    public void setCheckStationCount(int checkStationCount) {
        this.checkStationCount = checkStationCount;
    }

    public String getCheckUserName() {
        return checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public List<checkuser> getCheckuserVOS() {
        return checkuserVOS;
    }

    public void setCheckuserVOS(List<checkuser> checkuserVOS) {
        this.checkuserVOS = checkuserVOS;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public List<ImgsFirejd> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImgsFirejd> imgs) {
        this.imgs = imgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<com.haohai.platform.mapmodel.bean.planResourceDTOS> getPlanResourceDTOS() {
        return planResourceDTOS;
    }

    public void setPlanResourceDTOS(List<com.haohai.platform.mapmodel.bean.planResourceDTOS> planResourceDTOS) {
        this.planResourceDTOS = planResourceDTOS;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
