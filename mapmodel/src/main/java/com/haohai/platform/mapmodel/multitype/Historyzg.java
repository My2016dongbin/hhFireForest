package com.haohai.platform.mapmodel.multitype;

public class Historyzg {
    public String description;
    public String imgurl;
    public String id;
    public String userType;
    public String userName;
    public String regulation;
    public String createUser;

    public int cishu;
    public Historyzg() {
    }

    public Historyzg(String description, String imgurl, String id, int cishu) {
        this.description = description;
        this.imgurl = imgurl;
        this.id = id;
        this.cishu = cishu;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getRegulation() {
        return regulation;
    }

    public void setRegulation(String regulation) {
        this.regulation = regulation;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    public int getCishu() {
        return cishu;
    }

    public void setCishu(int cishu) {
        this.cishu = cishu;
    }
}
