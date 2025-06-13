package com.hotel.controller;

import com.hotel.service.ACScheduleService;
import com.hotel.service.ACService;
import com.hotel.service.HotelService;
import com.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 空调控制器
 */
@RestController
@RequestMapping("/api/ac")
@CrossOrigin(origins = "*")
public class ACController {
    private final ACService acService;
    private final RoomService roomService;
    private final ACScheduleService acScheduleService;

    public ACController(ACService acService, RoomService roomService, ACScheduleService acScheduleService) {
        this.acService = acService;
        this.roomService = roomService;
        this.acScheduleService = acScheduleService;
    }

    /**
     * 房间开启空调
     * @param roomId 房间ID
     * @param currentTemp 当前温度（可选，不提供则使用默认值）
     */
    @PostMapping("/room/{roomId}/start")
    public ResponseEntity<Map<String, String>> PowerOn(
            @PathVariable Long roomId,
            @RequestParam(required = false) Double currentTemp) {
        try {
            String result = acScheduleService.startAC(roomId, currentTemp);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     *
     * @param roomId 房间ID
     *
     */
    @PostMapping("/room/{roomId}/stop")
    public ResponseEntity<Map<String, String>> PowerOff(@PathVariable Long roomId) {
        try {
            String result = acScheduleService.stopAC(roomId);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 调整空调温度
     */
    @PutMapping("/room/{roomId}/temp")
    public ResponseEntity<Map<String, String>> ChangeTemp(
            @PathVariable Long roomId,
            @RequestParam Double targetTemp) {
        try {
            String result = acScheduleService.changeTemp(roomId, targetTemp);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 调整空调风速
     */
    @PutMapping("/room/{roomId}/speed")
    public ResponseEntity<Map<String, String>> ChangeSpeed(
            @PathVariable Long roomId,
            @RequestParam String fanSpeed) {
        try {
            String result = acScheduleService.changeFanSpeed(roomId, fanSpeed);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 获取房间空调累计运行时间和费用
     */
    @GetMapping("/room/{roomId}/detail")
    public ResponseEntity<Map<String, Object>> getRoomACDetail(@PathVariable Long roomId) {
        try {
            // 调用服务层方法获取累计数据
            Map<String, Object> result = acScheduleService.getRoomACAccumulatedData(roomId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

//    /**
//     * 获取指定房间的空调状态
//     */
//    @GetMapping("/room/{roomId}/status")
//    public ResponseEntity<?> RequestState(@PathVariable Long roomId) {
//        // 暂时保留roomNumber的逻辑，后续可以进一步优化
//        ACScheduleService.AcRequest request = scheduleService.getRoomRequest(roomId.intValue());
//        if (request == null) {
//            return ResponseEntity.ok(Map.of("message", "房间未开启空调"));
//        }
//        return ResponseEntity.ok(request);
//    }
//
//    /**
//     * 获取调度队列状态
//     */
//    @GetMapping("/schedule/status")
//    public ResponseEntity<?> getScheduleStatus() {
//        return ResponseEntity.ok(scheduleService.getDetailedScheduleStatus());
//    }
}