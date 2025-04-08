package com.ruyiruyi.rylibrary.model;

import java.util.List;

/**
  * Created by qc
  * on 2022/11/23.
  * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
  */
public class IMGridUsers {
    private String id;
    private String parentId;
    private List<IMGridUsers> children;
    private String name;
    private String no;

    private String lastTime;
    private String lastNews;
    private String unRead;

    private boolean status;
    private boolean expand;
    private List<Users> users;

    public IMGridUsers() {
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastNews() {
        return lastNews;
    }

    public void setLastNews(String lastNews) {
        this.lastNews = lastNews;
    }

    public String getUnRead() {
        return unRead;
    }

    public void setUnRead(String unRead) {
        this.unRead = unRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<IMGridUsers> getChildren() {
        return children;
    }

    public void setChildren(List<IMGridUsers> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "IMGridUsers{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", children=" + children +
                ", name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", lastNews='" + lastNews + '\'' +
                ", unRead='" + unRead + '\'' +
                ", status=" + status +
                ", expand=" + expand +
                ", users=" + users +
                '}';
    }

    public static class Users{
        private String id;
        private String userCode;
        private String fullName;
        private String email;
        private String phone;
        private String sex;
        private String birthday;
        private String groupId;
        private String headUrl;
        private String state;
        private String deptId;
        private String deptName;
        private String gridNo;

        private boolean status;

        public Users() {
        }

        public Users(String id, String userCode, String fullName, String email, String phone, String sex, String birthday, String groupId, String headUrl, String state, String deptId, String deptName, String gridNo) {
            this.id = id;
            this.userCode = userCode;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.sex = sex;
            this.birthday = birthday;
            this.groupId = groupId;
            this.headUrl = headUrl;
            this.state = state;
            this.deptId = deptId;
            this.deptName = deptName;
            this.gridNo = gridNo;
        }

        public Users(String id, String userCode, String fullName, String email, String phone, String sex, String birthday, String groupId, String headUrl, String state, String deptId, String deptName, String gridNo, boolean status) {
            this.id = id;
            this.userCode = userCode;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.sex = sex;
            this.birthday = birthday;
            this.groupId = groupId;
            this.headUrl = headUrl;
            this.state = state;
            this.deptId = deptId;
            this.deptName = deptName;
            this.gridNo = gridNo;
            this.status = status;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getGridNo() {
            return gridNo;
        }

        public void setGridNo(String gridNo) {
            this.gridNo = gridNo;
        }

        @Override
        public String toString() {
            return "Users{" +
                    "id='" + id + '\'' +
                    ", userCode='" + userCode + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", sex='" + sex + '\'' +
                    ", birthday='" + birthday + '\'' +
                    ", groupId='" + groupId + '\'' +
                    ", headUrl='" + headUrl + '\'' +
                    ", state='" + state + '\'' +
                    ", deptId='" + deptId + '\'' +
                    ", deptName='" + deptName + '\'' +
                    ", gridNo='" + gridNo + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}
