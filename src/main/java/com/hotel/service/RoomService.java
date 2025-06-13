package com.hotel.service;

import com.hotel.entity.Room;

import java.util.List;

/**
 * 房间服务对象
 * 管理所有房间对象
 */
public interface RoomService {

    void printStatus();

    /**
     * 根据房间ID获取房间对象
     * @param roomId 房间ID
     * @return 房间对象
     */
    Room getRoomById(Long roomId);

    /**
     *  对目标房间进行降温
     * @param roomId 房间ID
     * @param decreaseTemp 降温温度
     */
    void coolingRoom(Long roomId, Double decreaseTemp);

    /**
     * 对所有房间进行升温
     * @param excludeRoomIds 排除房间ID列表
     * @param increaseTemp 升温温度
     */
    void heatingRooms(List<Long> excludeRoomIds, Double increaseTemp);
    List<Room> getAllRooms();
    List<Long> getAvailableRoomIds();
    boolean updateRoomStatus(Long roomId, String status);
    boolean updateRoom(Room room);
}
