package com.ruyiruyi.rylibrary.bus;

/**
 * Created by qc
 * on 2024/5/28.
 * Copyright © 2018 青岛浩海网络科技股份有限公司 版权所有
 */
public class StopAudio {
    private String tag;

    public StopAudio() {
    }

    public StopAudio(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
