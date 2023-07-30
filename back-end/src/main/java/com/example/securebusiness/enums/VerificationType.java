package com.example.securebusiness.enums;

public enum VerificationType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");
    private final String type;

    VerificationType(String type) {
        this.type = type;
    }

    private String getType() {
        return this.type.toLowerCase();
    }
}
