package com.ruyiruyi.rylibrary.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "beidounews")
public class BeidouNews {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;

    @Column(name = "person")
    public String person;

    @Column(name = "personid")
    public String personId;

    @Column(name = "content")
    public String content;

    @Column(name = "location")
    public String location;

    @Column(name = "sendtime")
    public String sendTime;

    @Column(name = "updatelong")
    public long updateLong;

    @Column(name = "readtime")
    public String readTime;

    @Column(name = "state")
    public int state; //0 未读 1 已读

    @Column(name = "send")
    public int send; //0 接收 1 发送

    public BeidouNews() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BeidouNews(String id, String person, String personId, String content, String location, String sendTime, long updateLong, String readTime, int send, int state) {
        this.id = id;
        this.person = person;
        this.personId = personId;
        this.content = content;
        this.location = location;
        this.sendTime = sendTime;
        this.updateLong = updateLong;
        this.readTime = readTime;
        this.send = send;
        this.state = state;
    }

    public long getUpdateLong() {
        return updateLong;
    }

    public void setUpdateLong(long updateLong) {
        this.updateLong = updateLong;
    }

    public int getSend() {
        return send;
    }

    public void setSend(int send) {
        this.send = send;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
