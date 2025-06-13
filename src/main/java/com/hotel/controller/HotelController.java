//package com.hotel.controller;
//
//import com.hotel.dto.BillResponse;
//import com.hotel.dto.CheckInRequest;
//import com.hotel.entity.Room;
//import com.hotel.service.HotelService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.List;
//import java.util.Map;
//
///**
// * 酒店业务控制器
// */
//@RestController
//@RequestMapping("/api/hotel")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
//public class HotelController {
//
//    private final HotelService hotelService;
//
//    /**
//     * 获取可用房间列表
//     */
//    @GetMapping("/rooms/available")
//    public ResponseEntity<List<Room>> getAvailableRooms() {
//        List<Room> rooms = hotelService.getAvailableRooms();
//        return ResponseEntity.ok(rooms);
//    }
//
//    /**
//     * 办理入住
//     */
//    // 处理/checkin请求，接收CheckInRequest对象，返回Map<String, String>类型的数据
//    @PostMapping("/checkin")
//    public ResponseEntity<Map<String, String>> checkIn(@Valid @RequestBody CheckInRequest request) {
//        try {
//            // 调用hotelService的checkIn方法，传入CheckInRequest对象，返回结果
//            String result = hotelService.checkIn(request);
//            // 返回结果，状态码为200
//            return ResponseEntity.ok(Map.of("message", result));
//        } catch (Exception e) {
//            // 捕获异常，返回错误信息，状态码为400
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    /**
//     * 办理退房
//     */
//    @PostMapping("/checkout/{roomId}")
//    public ResponseEntity<?> checkOut(@PathVariable Long roomId) {
//        try {
//            BillResponse bill = hotelService.checkOut(roomId);
//            return ResponseEntity.ok(bill);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
//}