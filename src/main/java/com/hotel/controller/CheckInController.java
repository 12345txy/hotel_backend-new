package com.hotel.controller;

import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotel")
public class CheckInController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private CustomerService customerService;

    /**
     * 获取当前空闲状态的房间ID列表
     * @return 空闲房间ID列表
     */
    @GetMapping("/available")
    public ResponseEntity<List<Long>> getAvailableRooms() {
        // 获取所有状态为"AVAILABLE"的房间ID
        List<Long> availableRoomIds = roomService.getAllRooms().stream()
                .filter(room -> "AVAILABLE".equals(room.getStatus()))
                .map(Room::getId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableRoomIds);
    }

    /**
     * 办理入住
     * @param checkInRequest 入住请求参数
     * @return 入住信息
     */
    @PostMapping("/checkin")
    public ResponseEntity<Map<String, Object>> checkIn(@RequestBody CheckInRequest checkInRequest) {
        // 参数校验
        if (checkInRequest == null || checkInRequest.getRoomId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "参数不能为空"));
        }

        // 获取指定房间
        Room room = roomService.getRoomById(checkInRequest.getRoomId());
        if (room == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "房间不存在"));
        }

        // 检查房间状态
        if (!"AVAILABLE".equals(room.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message", "房间当前不可用"));
        }

        // 创建顾客对象
        Customer customer = new Customer();
        customer.setName(checkInRequest.getName());
        customer.setIdCard(checkInRequest.getIdCard());
        customer.setPhoneNumber(checkInRequest.getPhoneNumber());
        customer.setCurrentRoomId(checkInRequest.getRoomId());
        customer.setCheckInTime(LocalDateTime.now());
        customer.setStatus("CHECKED_IN");
        customer.setCreateTime(LocalDateTime.now());

        // 保存顾客信息
        Customer savedCustomer = customerService.saveCustomer(customer);

        // 更新房间状态为已入住
        room.setStatus("OCCUPIED");
        roomService.updateRoom(room);

        // 构建返回结果
        Map<String, Object> result = Map.of(
                "customer", savedCustomer,
                "roomId", checkInRequest.getRoomId(),
                "checkInTime", savedCustomer.getCheckInTime()
        );

        return ResponseEntity.ok(result);
    }

    /**
     * 入住请求DTO
     */
    @Data
    public static class CheckInRequest {
        private String name;
        private String idCard;
        private String gender;
        private String phoneNumber;
        private Long roomId;
    }
}