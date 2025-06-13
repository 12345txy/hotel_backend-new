package com.hotel.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

@Slf4j
public class ServingQueue extends BaseQueue{
    private final int timeMultiplier;

    public ServingQueue(int timeMultiplier) {
        super();
        queue = new PriorityQueue<>(
                Comparator.comparing(RoomRequest::getFanSpeedPriority)
                        .thenComparing(RoomRequest::getRequestTime)
                        .thenComparing(RoomRequest::getRoomId, Comparator.reverseOrder())
        );
        this.timeMultiplier = timeMultiplier;
    }

    public void printStatus(){
        log.info("服务队列: ");
        super.printStatus();
    }

    public String changeFanSpeed(Long roomId, String fanSpeed, int timeTick) {
        RoomRequest roomRequest = dequeue(roomId);
        if (roomRequest != null) {
            roomRequest.setFanSpeed(fanSpeed);
            roomRequest.setServingTime(LocalDateTime.now());
            roomRequest.setWaitingTime(null);
            roomRequest.setTick(timeTick);
            simpleEnqueue(roomRequest);
            return "风速已调整";
        }else{
            return "不存在房间" + roomId + "的请求";
        }
    }

    public void enqueue(RoomRequest roomRequest, int timeTick) {
        roomRequest.setServingTime(LocalDateTime.now());
        roomRequest.setWaitingTime(null);
        roomRequest.setTick(timeTick);
        simpleEnqueue(roomRequest);
    }

    public boolean checkReplace(RoomRequest roomRequest, int timeSlice) {
        // 判断是否可以替换
        RoomRequest candidate = peek();
        // 如果是刚换入的请求，则不能替换
        Duration servingDuration = Duration.between(candidate.getServingTime(), LocalDateTime.now());
        long servingSeconds = servingDuration.getSeconds() * timeMultiplier;
        if (servingSeconds < 1){
            return false;
        }
        if (candidate.getFanSpeedPriority() < roomRequest.getFanSpeedPriority()){
            // 优先级调度
            return true;
        }
        if (candidate.getFanSpeedPriority() == roomRequest.getFanSpeedPriority()){
            // 时间片调度
            Duration duration = Duration.between(roomRequest.getWaitingTime(), LocalDateTime.now());
//            long minutes = duration.toMinutes();
            long seconds = duration.getSeconds() * timeMultiplier;
//            seconds += minutes * 60;
            seconds += (long) duration.getNano() * timeMultiplier / 1000000000L;
            log.info("seconds: {}, timeMulti: {}", duration.getSeconds(), timeMultiplier);
            // 允许5秒误差
            log.info("房间{}等待时间: {}s",  roomRequest.getRoomId(), seconds);
            return seconds - timeSlice >= -5;
        }
        return false;
    }
}
