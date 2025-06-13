package com.hotel.enums;

/**
 * 账单状态枚举
 */
public enum BillStatus {
    
    /**
     * 未支付
     */
    UNPAID("UNPAID", "未支付"),
    
    /**
     * 已支付
     */
    PAID("PAID", "已支付"),
    
    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");
    
    private final String code;
    private final String description;
    
    BillStatus(String code, String description) {
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