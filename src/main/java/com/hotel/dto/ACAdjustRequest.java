package com.hotel.dto;

import lombok.Data;

/**
 * 空调调节请求DTO
 */
@Data
public class ACAdjustRequest {
    
    /**
     * 空调模式：COOLING(制冷), HEATING(制热)
     */
    private String mode;
    
    /**
     * 风速：LOW(低), MEDIUM(中), HIGH(高)
     */
    private String fanSpeed;
    
    /**
     * 目标温度
     */
    private Double targetTemp;
} 