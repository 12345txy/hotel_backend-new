package com.hotel.enums;

/**
 * 风速枚举
 */
public enum FanSpeed {
    LOW("LOW", "低风", 1, 0.5),
    MEDIUM("MEDIUM", "中风", 2, 1.0),
    HIGH("HIGH", "高风", 3, 1.5);
    
    private final String code;
    private final String description;
    private final int priority;
    private final double rate;
    
    FanSpeed(String code, String description, int priority, double rate) {
        this.code = code;
        this.description = description;
        this.priority = priority;
        this.rate = rate;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public double getRate() {
        return rate;
    }
    
    public static FanSpeed fromCode(String code) {
        for (FanSpeed fanSpeed : values()) {
            if (fanSpeed.code.equals(code)) {
                return fanSpeed;
            }
        }
        return MEDIUM; // 默认中风
    }
} 