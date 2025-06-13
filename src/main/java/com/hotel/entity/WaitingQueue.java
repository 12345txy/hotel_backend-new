package com.hotel.entity;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;


@Slf4j
public class WaitingQueue extends BaseQueue{
    public WaitingQueue() {
        super();
        queue = new PriorityQueue<>(
                Comparator.comparing(RoomRequest::getFanSpeedPriority).reversed() // 风速高的优先（优先换入）
                        .thenComparing(RoomRequest::getWaitingTime) // 等待时间长的优先（优先换入）
                        .thenComparing(RoomRequest::getRoomId)                     // 房间号小的优先（优先换入）
        );
    }
    public void printStatus(){
        log.info("等待队列: ");
        super.printStatus();
    }
    public void enqueue (RoomRequest roomRequest) {
        roomRequest.setWaitingTime(LocalDateTime.now());
        roomRequest.setServingTime(null);
        roomRequest.setCurrentACId(null);
        roomRequest.setAcOn(false);
        simpleEnqueue(roomRequest);
    }

}
