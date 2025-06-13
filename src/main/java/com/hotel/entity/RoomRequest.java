package com.hotel.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoomRequest {
    /**
     * 房间ID
     */
    private final Long roomId;

    /**
     * 目标温度
     */
    private Double targetTemp;

    /**
     * 风速：L(低), M(中), H(高)
     */
    private String fanSpeed;

//    /**
//     * 空调模式：COOLING(制冷), HEATING(制热)
//     */
//    private String acMode;

    /**
     * 请求时间
     */
    private final LocalDateTime requestTime;

    /**
     * 进入等待队列的时间
     */
    private LocalDateTime waitingTime;

    /**
     * 进入服务队列的时间
     */
    private LocalDateTime servingTime;

    /**
     * 当前服务的空调ID
     */
    private Long currentACId;

    /**
     * 空调是否开启
     */
    private Boolean acOn;

    /**
     * 是否刚完成详单记录
     */
    private Boolean justRecord;

    /**
     * 系统时间刻
     */
    private int tick;

    public RoomRequest(Long roomId) {
        this.roomId = roomId;
        this.targetTemp = null;
        this.fanSpeed = null;
        this.requestTime = LocalDateTime.now();
        this.waitingTime = null;
        this.servingTime = null;
        this.currentACId = null;
        this.acOn = false;
        this.justRecord = false;
        this.tick = 0;
    }

    public int getFanSpeedPriority(){
        int priority = 0;
        if (this.fanSpeed != null) {
            switch (this.fanSpeed) {
                case "L":
                    priority = 1;
                    break;
                case "M":
                    priority = 2;
                    break;
                case "H":
                    priority = 3;
                    break;
                default:
                    break;
            }
        }

        return priority;
    }

    public String getAllInfo(){
        return "房间ID：" + this.roomId +
                " 目标温度：" + this.targetTemp +
                " 风速：" + this.fanSpeed +
                " 请求时间：" + this.requestTime +
                " 进入等待队列时间：" + this.waitingTime +
                " 进入服务队列时间：" + this.servingTime +
                " 当前服务的空调ID：" + this.currentACId +
                " 空调是否开启：" + this.acOn +
                " 是否刚完成详单记录：" + this.justRecord;
    }

    public void sleep(){
        waitingTime = null;
        servingTime = null;
        currentACId = null;
        acOn = false;
        justRecord = false;
    }
}
