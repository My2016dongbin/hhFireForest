package com.haohai.platform.firelibrary.ui.activity.copy.other;

import java.util.List;

public class AddZhengzhi {

    public String description;
    public List<ImgsFirejd> imgs;
    public String planResourceId;
    public AddZhengzhi() {
    }

    public AddZhengzhi(String description, List<ImgsFirejd> imgs, String planResourceId) {
        this.description = description;
        this.imgs = imgs;
        this.planResourceId = planResourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImgsFirejd> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImgsFirejd> imgs) {
        this.imgs = imgs;
    }
    public String getPlanResourceId() {
        return planResourceId;
    }

    public void setPlanResourceId(String planResourceId) {
        this.planResourceId = planResourceId;
    }
    public static class ImgsFirejd {
        public ImgsFirejd(String img, int type) {
            this.img = img;
            this.type = type;
        }

        public String img;
        public int type;
    }
}
