package com.edupedu.app.model.enums;

public enum ClassStatus {
    PASSED("PASSED"),
    FAILED("FAILED"),
    IN_PROGRESS("IN_PROGRESS"),
    RETAKING("RETAKING");
    // NOT_TAKEN
    private final String name;

    ClassStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
