package com.hotel.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Data
@Slf4j
public class BaseQueue {
    protected PriorityQueue<RoomRequest> queue;

    public BaseQueue() {
        this.queue = new PriorityQueue<>();
    }

    public void simpleEnqueue(RoomRequest roomRequest){
        queue.add(roomRequest);
    }

    public int size(){
        return queue.size();
    }

    public List<Long> getAllRoomIds(){
        List<Long> roomIds = new ArrayList<>();
        for (RoomRequest roomRequest : queue){
            roomIds.add(roomRequest.getRoomId());
        }
        return roomIds;
    }

    public List<RoomRequest> getAllRequests(){
        return List.copyOf(queue);
    }

    public void printStatus(){
        for (RoomRequest roomRequest : queue) {
            log.info(roomRequest.getAllInfo());
        }
    }

    public RoomRequest dequeue(){
        return queue.poll();
    }

    public RoomRequest peek(){
        return queue.peek();
    }

    /**
     * 删除指定房间的请求
     * 此方法不修改请求中的时间信息，需要手动处理
     * @param roomId 房间id
     * @return 删除的请求
     */
    public RoomRequest dequeue(Long roomId){
        RoomRequest result = null;
        List<RoomRequest> temp = new ArrayList<>();
        while (!queue.isEmpty()){
            RoomRequest roomRequest = queue.poll();
            if (!roomRequest.getRoomId().equals(roomId)){
                temp.add(roomRequest);
            }else{
                result = roomRequest;
            }
        }
        for (RoomRequest roomRequest : temp) {
            simpleEnqueue(roomRequest);
        }
        return result;
    }

    public String changeTemp(Long roomId, Double targetTemp) {
        RoomRequest roomRequest = dequeue(roomId);
        if (roomRequest != null) {
            roomRequest.setTargetTemp(targetTemp);
            simpleEnqueue(roomRequest);
            return "温度已调整";
        }else{
            return "不存在房间" + roomId + "的请求";
        }
    }

    public String changeFanSpeed(Long roomId, String fanSpeed) {
        RoomRequest roomRequest = dequeue(roomId);
        if (roomRequest != null) {
            roomRequest.setFanSpeed(fanSpeed);
            simpleEnqueue(roomRequest);
            return "风速已调整";
        }else{
            return "不存在房间" + roomId + "的请求";
        }
    }


    public RoomRequest getRoomRequest(Long roomId){
        for (RoomRequest roomRequest : queue) {
            if (roomRequest.getRoomId().equals(roomId)) {
                return roomRequest;
            }
        }
        return null;
    }

    public boolean checkRoomId(Long roomId){
        for (RoomRequest roomRequest : queue) {
            if (roomRequest.getRoomId().equals(roomId)) {
                return true;
            }
        }
        return false;
    }
}
