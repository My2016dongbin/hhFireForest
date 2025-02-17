package com.ruyiruyi.rylibrary.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "beidouperson")
public class BeidouPerson {
    @Column(name = "id",isId = true,autoGen = false)
    public String id;

    @Column(name = "name")
    public String name;

    @Column(name = "code")
    public String code;

    @Column(name = "lastnews")
    public String lastNews;

    @Column(name = "lasttime")
    public String lastTime;

    @Column(name = "updatelong")
    public long updateLong;

    @Column(name = "count")
    public int count;

    @Column(name = "unread")
    public int unRead;

    public BeidouPerson() {
    }

    public BeidouPerson(String id, String name, String code, String lastNews, String lastTime, long updateLong, int count, int unRead) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.lastNews = lastNews;
        this.lastTime = lastTime;
        this.updateLong = updateLong;
        this.count = count;
        this.unRead = unRead;
    }

    public long getUpdateLong() {
        return updateLong;
    }

    public void setUpdateLong(long updateLong) {
        this.updateLong = updateLong;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLastNews() {
        return lastNews;
    }

    public void setLastNews(String lastNews) {
        this.lastNews = lastNews;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getUnRead() {
        return unRead;
    }

    public void setUnRead(int unRead) {
        this.unRead = unRead;
    }

    @Override
    public String toString() {
        return
                "id = " + id +
                " name = " + name +
                " code = " + code +
                " lastNews = " + lastNews +
                " lastTime = " + lastTime +
                " count = " + count +
                " unRead = " + unRead
                ;
    }
}
