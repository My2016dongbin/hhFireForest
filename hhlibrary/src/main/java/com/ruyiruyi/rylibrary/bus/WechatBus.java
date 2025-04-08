package com.ruyiruyi.rylibrary.bus;

public class WechatBus {

    public final Boolean message;//true 终止

    public static WechatBus getInstance(Boolean message) {
        return new WechatBus(message);
    }

    private WechatBus(Boolean message) {
        this.message = message;
    }
}