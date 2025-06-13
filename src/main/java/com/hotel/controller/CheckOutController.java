package com.hotel.controller;

import com.hotel.entity.Bill;
import com.hotel.entity.BillDetail;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.service.BillDetailService;
import com.hotel.service.BillService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.vo.CheckoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotel")
public class CheckOutController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BillDetailService billDetailService;

    @Autowired
    private BillService billService;

    // 日期时间格式化
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 办理退房并生成指定格式的账单
     * @param roomId 房间ID
     * @return 指定格式的退房响应
     */
    @PostMapping("/checkout/{roomId}")
    public ResponseEntity<CheckoutResponse> checkout(@PathVariable Long roomId) {
        // 获取当前时间作为退房时间
        LocalDateTime checkOutTime = LocalDateTime.now();

        // 查找当前房间的顾客
        Optional<Customer> customerOpt = customerService.getCustomerByRoomId(roomId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Customer customer = customerOpt.get();

        // 更新顾客信息（设置退房时间和状态）
        customer.setCheckOutTime(checkOutTime);
        customer.setStatus("CHECKED_OUT");
        customer.setCurrentRoomId(null);
        customerService.updateCustomer(customer);

        // 更新房间状态为空闲
        Room room = roomService.getRoomById(roomId);
        room.setStatus("AVAILABLE");
        roomService.updateRoom(room);

        // 获取入住时间到退房时间的所有详单
        List<BillDetail> billDetails = billDetailService.getBillDetailsByRoomIdAndTimeRange(
                roomId, customer.getCheckInTime(), checkOutTime);

        // 创建并结算账单
        Bill bill = billService.createAndSettleBill(billDetails, customer, room);

        // 转换为响应格式
        CheckoutResponse response = convertToResponseFormat(bill, billDetails);

        return ResponseEntity.ok(response);
    }

    /**
     * 转换为指定的响应格式
     */
    private CheckoutResponse convertToResponseFormat(Bill bill, List<BillDetail> details) {
        CheckoutResponse response = new CheckoutResponse();

        // 转换详单列表
        List<CheckoutResponse.DetailBill> detailBillList = new ArrayList<>();
        for (BillDetail detail : details) {
            CheckoutResponse.DetailBill detailBill = new CheckoutResponse.DetailBill();
            detailBill.setRoomId(detail.getRoomId());
            detailBill.setStartTime(detail.getStartTime().toString());
            detailBill.setEndTime(detail.getEndTime().toString());
            detailBill.setDuration(detail.getDuration());
            detailBill.setFanSpeed(detail.getFanSpeed());
            detailBill.setCurrentFee(detail.getCost());
            detailBill.setFee(detail.getCost()); // 假设Fee和CurrentFee相同
            detailBillList.add(detailBill);
        }
        response.setDetailBill(detailBillList);

        // 转换账单信息
        CheckoutResponse.BillResponse billResponse = new CheckoutResponse.BillResponse();
        billResponse.setRoomId(bill.getRoomId());
        billResponse.setCheckinTime(bill.getCheckInTime().format(DATE_FORMATTER));
        billResponse.setCheckoutTime(bill.getCheckOutTime().format(DATE_FORMATTER));
        billResponse.setDuration(bill.getStayDays().toString());
        billResponse.setRoomFee(bill.getRoomFee());
        billResponse.setAcFee(bill.getAcTotalFee());
        response.setBill(billResponse);

        return response;
    }
}
