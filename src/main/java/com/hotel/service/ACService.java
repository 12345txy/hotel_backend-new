package com.hotel.service;

import com.hotel.entity.AirConditioner;
import com.hotel.entity.Room;
import com.hotel.entity.RoomRequest;

import java.util.List;

/**
 * 空调服务对象
 * 管理所有空调实例
 */
public interface ACService {
    RoomRequest initRequest(Long roomId);
    RoomRequest startAC(Long roomId);
    void printStatus();
    boolean changeTemp(Long acId, Double targetTemp);
    boolean changeFanSpeed(Long acId, String fanSpeed);
    void tick();
    void update(List<RoomRequest> servingRooms);
    AirConditioner getACByRoomId(Long roomId);
}
