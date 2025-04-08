package com.haohai.platform.mapmodel.bean;

import java.util.List;

/**
 * Created by qc
 * on 2022/7/29.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class SignListModel {
    private int page;
    private int limit;
    private Dto dto;

    public SignListModel() {
    }

    public SignListModel(int page, int limit, Dto dto) {
        this.page = page;
        this.limit = limit;
        this.dto = dto;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Dto getDto() {
        return dto;
    }

    public void setDto(Dto dto) {
        this.dto = dto;
    }

    public static class Dto{
        private List<String> ids;
        private String month;

        public Dto(List<String> ids, String month) {
            this.ids = ids;
            this.month = month;
        }

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }
}
