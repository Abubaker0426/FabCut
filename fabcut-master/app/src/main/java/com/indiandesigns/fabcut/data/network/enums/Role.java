package com.indiandesigns.fabcut.data.network.enums;

public enum Role {

    LEADER("leader"), FOLLOWER("follower");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}