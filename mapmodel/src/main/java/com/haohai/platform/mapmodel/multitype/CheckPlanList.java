package com.haohai.platform.mapmodel.multitype;

import java.util.List;

public class CheckPlanList {

        public int checkStationCount;
        public String checkUserName;
        public String createTime;
        public String createUser;
        public String description;
        public String endTime;
        public String gridNo;
        public String parentGridNo;
        public String gridName;
        public String groupId;
        public String id;
        public String name;
        public int otherCount;
        public String parentGridId;
        public String parentGridName;
        public List<PlanResourceDTOSFirejd> planResourceDTOS;
        public int repositoryCount;
        public String startTime;
        public int status;
        public int taskLevel;
        public int teamCount;
        public int type;

    public CheckPlanList(int checkStationCount, String checkUserName, String createTime, String createUser, String description, String endTime, String gridNo, String gridName, String groupId, String id, String name, int otherCount, String parentGridId, String parentGridName, List<PlanResourceDTOSFirejd> planResourceDTOS, int repositoryCount, String startTime, int status, int taskLevel, int teamCount, int type) {
        this.checkStationCount = checkStationCount;
        this.checkUserName = checkUserName;
        this.createTime = createTime;
        this.createUser = createUser;
        this.description = description;
        this.endTime = endTime;
        this.gridNo = gridNo;
        this.gridName = gridName;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.otherCount = otherCount;
        this.parentGridId = parentGridId;
        this.parentGridName = parentGridName;
        this.planResourceDTOS = planResourceDTOS;
        this.repositoryCount = repositoryCount;
        this.startTime = startTime;
        this.status = status;
        this.taskLevel = taskLevel;
        this.teamCount = teamCount;
        this.type = type;
    }

    public int getCheckStationCount() {
        return checkStationCount;
    }

    public void setCheckStationCount(int checkStationCount) {
        this.checkStationCount = checkStationCount;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getParentGridNo() {
        return parentGridNo;
    }

    public void setParentGridNo(String parentGridNo) {
        this.parentGridNo = parentGridNo;
    }

    public String getCheckUserName() {
        return checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public String getGridId() {
        return gridNo;
    }

    public void setGridId(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public int getOtherCount() {
        return otherCount;
    }

    public void setOtherCount(int otherCount) {
        this.otherCount = otherCount;
    }

    public String getParentGridId() {
        return parentGridId;
    }

    public void setParentGridId(String parentGridId) {
        this.parentGridId = parentGridId;
    }

    public String getParentGridName() {
        return parentGridName;
    }

    public void setParentGridName(String parentGridName) {
        this.parentGridName = parentGridName;
    }

    public List<PlanResourceDTOSFirejd> getPlanResourceDTOS() {
        return planResourceDTOS;
    }

    public void setPlanResourceDTOS(List<PlanResourceDTOSFirejd> planResourceDTOS) {
        this.planResourceDTOS = planResourceDTOS;
    }

    public int getRepositoryCount() {
        return repositoryCount;
    }

    public void setRepositoryCount(int repositoryCount) {
        this.repositoryCount = repositoryCount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(int taskLevel) {
        this.taskLevel = taskLevel;
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class PlanResourceDTOSFirejd {
        public int checkType;
        public String createTime;
        public String createUser;
        public String endTime;
        public String gridNo;
        public String gridName;
        public String groupId;
        public String id;
        public String name;
        public String parentGridId;
        public String parentGridName;
        public String parentGridNo;
        public String planId;
        public String resourceId;
        public String resourceType;
        public int sequence;
        public String startTime;
        public int status;
        public String updateTime;
        public String updateUser;
        public PlanResourceDTOSFirejd(int checkType, String createTime, String createUser, String endTime, String gridNo, String gridName, String groupId, String id, String name, String parentGridId, String parentGridName, String planId, String resourceId, String resourceType, int sequence, String startTime, int status, String updateTime, String updateUser) {
            this.checkType = checkType;
            this.createTime = createTime;
            this.createUser = createUser;
            this.endTime = endTime;
            this.gridNo = gridNo;
            this.gridName = gridName;
            this.groupId = groupId;
            this.id = id;
            this.name = name;
            this.parentGridId = parentGridId;
            this.parentGridName = parentGridName;
            this.planId = planId;
            this.resourceId = resourceId;
            this.resourceType = resourceType;
            this.sequence = sequence;
            this.startTime = startTime;
            this.status = status;
            this.updateTime = updateTime;
            this.updateUser = updateUser;
        }

        public String getGridNo() {
            return gridNo;
        }

        public void setGridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        public String getParentGridNo() {
            return parentGridNo;
        }

        public void setParentGridNo(String parentGridNo) {
            this.parentGridNo = parentGridNo;
        }

        public int getCheckType() {
            return checkType;
        }

        public void setCheckType(int checkType) {
            this.checkType = checkType;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getgridNo() {
            return gridNo;
        }

        public void setgridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        public String getGridName() {
            return gridName;
        }

        public void setGridName(String gridName) {
            this.gridName = gridName;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
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

        public String getParentGridId() {
            return parentGridId;
        }

        public void setParentGridId(String parentGridId) {
            this.parentGridId = parentGridId;
        }

        public String getParentGridName() {
            return parentGridName;
        }

        public void setParentGridName(String parentGridName) {
            this.parentGridName = parentGridName;
        }

        public String getPlanId() {
            return planId;
        }

        public void setPlanId(String planId) {
            this.planId = planId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }

        @Override
        public String toString() {
            return "PlanResourceDTOSFirejd{" +
                    "checkType=" + checkType +
                    ", createTime='" + createTime + '\'' +
                    ", createUser='" + createUser + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", gridNo='" + gridNo + '\'' +
                    ", gridName='" + gridName + '\'' +
                    ", groupId='" + groupId + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", parentGridId='" + parentGridId + '\'' +
                    ", parentGridName='" + parentGridName + '\'' +
                    ", parentGridNo='" + parentGridNo + '\'' +
                    ", planId='" + planId + '\'' +
                    ", resourceId='" + resourceId + '\'' +
                    ", resourceType='" + resourceType + '\'' +
                    ", sequence=" + sequence +
                    ", startTime='" + startTime + '\'' +
                    ", status=" + status +
                    ", updateTime='" + updateTime + '\'' +
                    ", updateUser='" + updateUser + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CheckPlanList{" +
                "checkStationCount=" + checkStationCount +
                ", checkUserName='" + checkUserName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", createUser='" + createUser + '\'' +
                ", description='" + description + '\'' +
                ", endTime='" + endTime + '\'' +
                ", gridNo='" + gridNo + '\'' +
                ", parentGridNo='" + parentGridNo + '\'' +
                ", gridName='" + gridName + '\'' +
                ", groupId='" + groupId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", otherCount=" + otherCount +
                ", parentGridId='" + parentGridId + '\'' +
                ", parentGridName='" + parentGridName + '\'' +
                ", planResourceDTOS=" + planResourceDTOS +
                ", repositoryCount=" + repositoryCount +
                ", startTime='" + startTime + '\'' +
                ", status=" + status +
                ", taskLevel=" + taskLevel +
                ", teamCount=" + teamCount +
                ", type=" + type +
                '}';
    }
}
