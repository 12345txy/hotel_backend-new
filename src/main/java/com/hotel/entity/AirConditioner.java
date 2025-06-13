package com.hotel.entity;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 空调实体类
 */
@Data
public class AirConditioner {

    private final Long id;
    
//    /**
//     * 空调编号
//     */
//    private Integer acNumber;

    /**
     * 当前服务的房间ID
     */
    private Long servingRoomId;

    /**
     * 是否开启
     */
    private Boolean on;

    /**
     * 工作模式：COOLING(制冷), HEATING(制热)
     */
    private final String mode;

//    /**
//     * 风速：LOW(低), MEDIUM(中), HIGH(高)
//     */
//    private String fanSpeed;

    /**
     * 最高温度
     */
    private final Double maxTemp;

    /**
     * 最低温度
     */
    private final Double minTemp;

    /**
     * 默认温度
     */
    private final Double defaultTemp;

    /**
     * 目标温度
     */
    private Double targetTemp;

    /**
     * 费率
     */
    private final Double rate;

    /**
     * 低风降温速度
     */
    private final Double lowSpeedRate;

    /**
     * 中风降温速度
     */
    private final Double midSpeedRate;

    /**
     * 高风降温速度
     */
    private final Double highSpeedRate;

    /**
     * 默认风速
     */
    private final String defaultSpeed;

    /**
     * 当前风速
     */
    private String fanSpeed;

//    /**
//     * 当前温度
//     */
//    private Double currentTemp;
//
//    /**
//     * 请求时间
//     */
//    private LocalDateTime requestTime;
//
//    /**
//     * 服务开始时间
//     */
//    private LocalDateTime serviceStartTime;
//
//    /**
//     * 服务结束时间
//     */
//    private LocalDateTime serviceEndTime;
//
//    /**
//     * 服务时长(分钟)
//     */
//    private Long serviceDuration;
//
//    /**
//     * 当前费用
//     */
//    private Double cost;
//
//    /**
//     * 优先级(基于风速)
//     */
//    private Integer priority;
//
//    /**
//     * 服务时间(分钟)
//     */
//    private Long serviceTime;
//
//    /**
//     * 创建时间
//     */
//    private LocalDateTime createTime;
//
//    /**
//     * 更新时间
//     */
//    private LocalDateTime updateTime;

    /**
     * 构造函数
     * @param acConfig 空调配置对象
     */
    public AirConditioner(Long id, ACConfig acConfig) {
        this.id = id;
        this.mode = acConfig.getMode();
        this.maxTemp = acConfig.getMaxTemp();
        this.minTemp = acConfig.getMinTemp();
        this.defaultTemp = acConfig.getDefaultTemp();
        this.rate = acConfig.getRate();
        this.lowSpeedRate = acConfig.getLowSpeedRate();
        this.midSpeedRate = acConfig.getMidSpeedRate();
        this.highSpeedRate = acConfig.getHighSpeedRate();
        this.defaultSpeed = acConfig.getDefaultSpeed();
        this.servingRoomId = null;
        this.on = false;
        this.targetTemp = null;
        this.fanSpeed = null;
    }

    /**
     * 获取所有属性信息
     * @return 所有属性信息
     */
    public String getACAllInfo() {
        return "id: " + id +
                ", mode: " + mode +
                ", maxTemp: " + maxTemp +
                ", minTemp: " + minTemp +
                ", defaultTemp: " + defaultTemp +
                ", rate: " + rate +
                ", lowSpeedRate: " + lowSpeedRate +
                ", midSpeedRate: " + midSpeedRate +
                ", highSpeedRate: " + highSpeedRate +
                ", defaultSpeed: " + defaultSpeed +
                ", servingRoomId: " + servingRoomId +
                ", on: " + on +
                ", targetTemp: " + targetTemp +
                ", fanSpeed: " + fanSpeed;
    }
    /**
     * 获取摘要信息
     * @return 摘要信息
     */
    public String getSummary() {
        return "id: " + id +
                ", servingRoomId: " + servingRoomId +
                ", on: " + on +
                ", targetTemp: " + targetTemp +
                ", fanSpeed: " + fanSpeed;
    }

    /**
     * 初始化空调
     * @param roomId 房间ID
     */
    public void init(Long roomId) {
        this.servingRoomId = roomId;
        this.on = true;
        this.targetTemp = this.defaultTemp;
        this.fanSpeed = this.defaultSpeed;
    }

    public void init(RoomRequest request) {
        this.servingRoomId = request.getRoomId();
        this.on = true;
        if (request.getTargetTemp() ==  null){
            request.setTargetTemp(this.defaultTemp);
        }
        double temp = request.getTargetTemp();
        if (temp > this.maxTemp) {
            temp = this.maxTemp;
        }
        if (temp < this.minTemp) {
            temp = this.minTemp;
        }
        this.targetTemp = temp;
        if (request.getFanSpeed() == null){
            request.setFanSpeed(this.defaultSpeed);
        }
        this.fanSpeed = request.getFanSpeed();
    }

    public Double getCoolingRate() {
        switch (this.fanSpeed) {
            case "L":
                return this.lowSpeedRate;
            case "M":
                return this.midSpeedRate;
            case "H":
                return this.highSpeedRate;
            default:
                return 0.0;
        }
    }
} 