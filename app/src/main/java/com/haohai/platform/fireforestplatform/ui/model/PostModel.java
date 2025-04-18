package com.haohai.platform.fireforestplatform.ui.model;

/**
 * Created by qc
 * on 2022/6/30.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class PostModel {
   private int page;
   private int limit;
   private Dto dto;

    public PostModel(int page, int limit, Dto dto) {
        this.page = page;
        this.limit = limit;
        this.dto = dto;
    }

    public PostModel(Dto dto) {
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
       private String elementName;
       private String menuId;

       public Dto(String elementName, String menuId) {
           this.elementName = elementName;
           this.menuId = menuId;
       }

       public String getElementName() {
           return elementName;
       }

       public void setElementName(String elementName) {
           this.elementName = elementName;
       }

       public String getMenuId() {
           return menuId;
       }

       public void setMenuId(String menuId) {
           this.menuId = menuId;
       }

       @Override
       public String toString() {
           return "Dto{" +
                   "elementName='" + elementName + '\'' +
                   ", menuId='" + menuId + '\'' +
                   '}';
       }
   }

    @Override
    public String toString() {
        return "PostModel{" +
                "page=" + page +
                ", limit=" + limit +
                ", dto=" + dto +
                '}';
    }
}
