package com.hotel.entity;

import com.hotel.enums.FanSpeed;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * 房间实体类
 */
@Data
public class Room {

    private final Long id;

    /**
     * 房间状态：AVAILABLE(可用), OCCUPIED(已入住), MAINTENANCE(维护中)
     */
    private String status;

    /**
     * 默认温度
     */
    private final Double defaultTemp;

    /**
     * 当前温度
     */
    private Double currentTemp;

    /**
     * 日租
     */
    private final Double dailyRate;


    /**
     * 构造函数
     * @param roomConfig 房间配置对象
     */
    public Room(RoomConfig roomConfig){
        this.id = roomConfig.getId();
        this.defaultTemp = roomConfig.getDefaultTemp();
        this.dailyRate = roomConfig.getDailyRate();
        this.currentTemp = this.defaultTemp;
    }

    public String getAllInfo() {
        return "房间号：" + this.id
                + " 房间温度：" + this.currentTemp
                + " 房间默认温度：" + this.defaultTemp
                + " 房间日费：" + this.dailyRate;
    }

} 