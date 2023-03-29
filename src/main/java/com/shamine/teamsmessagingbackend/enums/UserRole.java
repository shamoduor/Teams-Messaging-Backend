package com.shamine.teamsmessagingbackend.enums;

public enum UserRole {
    USER(511);

    private final int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}