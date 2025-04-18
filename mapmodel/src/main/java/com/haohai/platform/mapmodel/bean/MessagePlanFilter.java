package com.haohai.platform.mapmodel.bean;

public class MessagePlanFilter {

    public final String message;

    public static MessagePlanFilter getInstance(String message) {
        return new MessagePlanFilter(message);
    }

    private MessagePlanFilter(String message) {
        this.message = message;
    }
}