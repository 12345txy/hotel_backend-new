package com.hotel.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 测试控制器
 * 提供系统测试和调试接口
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    
//    private final ACService scheduleTaskService;
//    private final TemperatureService temperatureService;
//    private final RoomMapper roomMapper;
//
//    /**
//     * 手动触发时间片检查
//     */
//    @PostMapping("/time-slice-check")
//    public String triggerTimeSliceCheck() {
//        scheduleTaskService.triggerTimeSliceCheck();
//        return "时间片检查已触发";
//    }
//
//    /**
//     * 手动触发温度更新
//     */
//    @PostMapping("/temperature-update")
//    public String triggerTemperatureUpdate() {
//        scheduleTaskService.triggerTemperatureUpdate();
//        return "温度更新已触发";
//    }
//
//    /**
//     * 获取所有房间状态
//     */
//    @GetMapping("/rooms/status")
//    public List<Map<String, Object>> getRoomsStatus() {
//        List<Room> rooms = roomMapper.selectList(null);
//        return rooms.stream().map(this::buildRoomStatus).toList();
//    }
//
//    /**
//     * 获取指定房间详细状态
//     */
//    @GetMapping("/rooms/{roomId}/status")
//    public Map<String, Object> getRoomStatus(@PathVariable Long roomId) {
//        Room room = roomMapper.findByRoomId(roomId);
//        if (room == null) {
//            throw new RuntimeException("房间不存在");
//        }
//        return buildRoomStatus(room);
//    }
//
//    /**
//     * 设置房间温度（测试用）
//     */
//    @PostMapping("/rooms/{roomId}/temperature")
//    public String setRoomTemperature(@PathVariable Long roomId,
//                                   @RequestParam Double temperature) {
//        Room room = roomMapper.findByRoomId(roomId);
//        if (room == null) {
//            throw new RuntimeException("房间不存在");
//        }
//
//        room.setCurrentTemp(temperature);
//        roomMapper.updateById(room);
//
//        return String.format("房间%d温度已设置为%.1f度", room.getId(), temperature);
//    }
//
//    /**
//     * 模拟时间流逝（加速测试）
//     */
//    @PostMapping("/time/forward")
//    public String forwardTime(@RequestParam Integer minutes) {
//        log.info("模拟时间前进{}分钟", minutes);
//
//        // 这里可以添加时间加速逻辑
//        // 实际实现中可能需要修改所有时间相关的计算
//
//        return String.format("时间已前进%d分钟", minutes);
//    }
//
//    /**
//     * 重置系统状态（测试用）
//     */
//    @PostMapping("/reset")
//    public String resetSystem() {
//        List<Room> rooms = roomMapper.selectList(null);
//        for (Room room : rooms) {
//            room.setAcOn(false)
//                .setCurrentAcId(null)
//                .setCustomerId(null)
//                .setStatus("AVAILABLE")
//                .setServiceStartTime(null)
//                .setWaitingStartTime(null)
//                .setServiceDuration(0L)
//                .setWaitingDuration(0L)
//                .setIsWarmingBack(false)
//                .setCurrentTemp(room.getInitialTemp()); // 重置到初始温度
//            roomMapper.updateById(room);
//        }
//
//        return "系统状态已重置";
//    }
//
//    /**
//     * 构建房间状态信息
//     */
//    private Map<String, Object> buildRoomStatus(Room room) {
//        Map<String, Object> status = new HashMap<>();
//                    status.put("roomNumber", room.getId());
//        status.put("status", room.getStatus());
//        status.put("currentTemp", room.getCurrentTemp());
//        status.put("targetTemp", room.getTargetTemp());
//        status.put("initialTemp", room.getInitialTemp());
//        status.put("acOn", room.getAcOn());
//        status.put("acMode", room.getAcMode());
//        status.put("fanSpeed", room.getFanSpeed());
//        status.put("currentAcId", room.getCurrentAcId());
//        status.put("customerName", room.getCustomerId() != null ? "已入住" : "空闲");
//        status.put("serviceDuration", room.getServiceDuration());
//        status.put("waitingDuration", room.getWaitingDuration());
//        status.put("isWarmingBack", room.getIsWarmingBack());
//        status.put("serviceStartTime", room.getServiceStartTime());
//        status.put("waitingStartTime", room.getWaitingStartTime());
//        status.put("warmingStartTime", room.getWarmingStartTime());
//
//        // 计算服务状态
//        if (room.getCurrentAcId() != null) {
//            status.put("serviceStatus", "服务中");
//        } else if (room.getWaitingStartTime() != null && room.getAcOn()) {
//            status.put("serviceStatus", "等待中");
//        } else if (room.getIsWarmingBack()) {
//            status.put("serviceStatus", "回温中");
//        } else {
//            status.put("serviceStatus", "空闲");
//        }
//
//        return status;
//    }
} 