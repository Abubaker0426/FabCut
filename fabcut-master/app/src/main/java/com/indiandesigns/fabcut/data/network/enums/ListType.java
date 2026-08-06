package com.indiandesigns.fabcut.data.network.enums;

public enum ListType {

    SIZE(0), QUANTITY(1), RATIO(2), SIZE_QUANTITY(3), SIZE_COMPLETED_QUANTITY(4), SIZE_AVAILABLE_QUANTITY(5);

    private final int value;

    ListType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
