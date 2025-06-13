package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 客户实体类
 */
@Data
@Accessors(chain = true)
@TableName("customers")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 当前入住房间ID
     */
    private Long currentRoomId;

    /**
     * 入住时间
     */
    private LocalDateTime checkInTime;

    /**
     * 退房时间
     */
    private LocalDateTime checkOutTime;

    /**
     * 客户状态：CHECKED_IN(已入住), CHECKED_OUT(已退房)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 住宿天数
     */
    private Long checkInDays;

} 