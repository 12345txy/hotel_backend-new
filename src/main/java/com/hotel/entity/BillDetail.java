package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 账单详单实体类
 */
@Data
@Accessors(chain = true)
@TableName("bill_details")
public class BillDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 空调模式：COOLING(制冷), HEATING(制热)
     */
    private String acMode;

    /**
     * 风速：LOW(低), MEDIUM(中), HIGH(高)
     */
    private String fanSpeed;
    
    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 持续时间(分钟)
     */
    private Long duration;

    /**
     * 费用
     */
    private Double cost;

    /**
     * 费率(元/分钟)
     */
    private Double rate;

    /**
     * 详单类型：ADJUST_FAN(调风), SCHEDULE(调度), TARGET_REACHED(达到目标温度), AC_OFF(关机)
     */
    private String detailType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 