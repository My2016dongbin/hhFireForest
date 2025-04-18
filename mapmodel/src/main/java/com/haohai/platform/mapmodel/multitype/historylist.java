package com.haohai.platform.mapmodel.multitype;

import java.util.List;

public class historylist {

        public String groupId;
        public String id;
        public String planResourceId;
        public Object parentId;
        public Object code;
        public int level;
        public int status;
        public String description;
        public String regulation;
        public int count;
        public int type;
        public List<ChildFirejd> child;

    public historylist(String groupId, String id, String planResourceId, Object parentId, Object code, int level, int status, String description, String regulation, int count, int type, List<ChildFirejd> child) {
        this.groupId = groupId;
        this.id = id;
        this.planResourceId = planResourceId;
        this.parentId = parentId;
        this.code = code;
        this.level = level;
        this.status = status;
        this.description = description;
        this.regulation = regulation;
        this.count = count;
        this.type = type;
        this.child = child;
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

    public String getPlanResourceId() {
        return planResourceId;
    }

    public void setPlanResourceId(String planResourceId) {
        this.planResourceId = planResourceId;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ChildFirejd> getChild() {
        return child;
    }

    public void setChild(List<ChildFirejd> child) {
        this.child = child;
    }

    public static class ChildFirejd {
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

        public String getPlanResourceId() {
            return planResourceId;
        }

        public void setPlanResourceId(String planResourceId) {
            this.planResourceId = planResourceId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public ChildFirejd(String groupId, String id, String planResourceId, String parentId, String code, int level, int status, int count, int type) {
            this.groupId = groupId;
            this.id = id;
            this.planResourceId = planResourceId;
            this.parentId = parentId;
            this.code = code;
            this.level = level;
            this.status = status;
            this.count = count;
            this.type = type;
        }

        public String groupId;
            public String id;
            public String planResourceId;
            public String parentId;
            public String code;
            public int level;
            public int status;
            public int count;
            public int type;
        }

}
