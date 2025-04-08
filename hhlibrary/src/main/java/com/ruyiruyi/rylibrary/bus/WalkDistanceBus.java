package com.ruyiruyi.rylibrary.bus;

public class WalkDistanceBus {

    public final String message;

    public static WalkDistanceBus getInstance(String message) {
        return new WalkDistanceBus(message);
    }

    private WalkDistanceBus(String message) {
        this.message = message;
    }
}