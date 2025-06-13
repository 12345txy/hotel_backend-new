package com.hotel.enums;

/**
 * 空调模式枚举
 */
public enum AcMode {
    COOLING("COOLING", "制冷"),
    HEATING("HEATING", "制热");
    
    private final String code;
    private final String description;
    
    AcMode(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
} 