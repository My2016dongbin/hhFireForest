package com.haohai.platform.mapmodel.model;

import java.util.List;

/**
 * Created by qc
 * on 2022/9/13.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class Grid {
    private String id;
    private String parentId;
    private List<Grid> children;
    private String name;
    private String no;
    private String data;
    private boolean selected = false;

    public Grid() {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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

    public List<Grid> getChildren() {
        return children;
    }

    public void setChildren(List<Grid> children) {
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
