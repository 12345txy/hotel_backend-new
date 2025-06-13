package com.hotel.enums;

/**
 * 队列状态枚举
 */
public enum QueueStatus {
    NONE("NONE", "不在队列"),
    SERVICE("SERVICE", "服务队列"),
    WAITING("WAITING", "等待队列");

    private final String code;
    private final String description;

    QueueStatus(String code, String description) {
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