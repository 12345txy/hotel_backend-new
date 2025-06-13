package com.hotel.enums;

/**
 * 房间状态枚举
 */
public enum RoomStatus {
    AVAILABLE("AVAILABLE", "可用"),
    OCCUPIED("OCCUPIED", "已入住"),
    MAINTENANCE("MAINTENANCE", "维护中");
    
    private final String code;
    private final String description;
    
    RoomStatus(String code, String description) {
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