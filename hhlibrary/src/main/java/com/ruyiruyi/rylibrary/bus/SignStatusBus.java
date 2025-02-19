package com.ruyiruyi.rylibrary.bus;

public class SignStatusBus {

    public final String message;

    public static SignStatusBus getInstance(String message) {
        return new SignStatusBus(message);
    }

    private SignStatusBus(String message) {
        this.message = message;
    }
}