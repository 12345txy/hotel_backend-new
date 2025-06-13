package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Bill;
import com.hotel.entity.BillDetail;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.mapper.BillMapper;
import com.hotel.service.BillDetailService;
import com.hotel.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {

    @Autowired
    private BillDetailService billDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Bill createAndSettleBill(List<BillDetail> billDetails, Customer customer, Room room) {
        // 参数校验
        Assert.notEmpty(billDetails, "账单详单不能为空");
        Assert.notNull(customer, "顾客信息不能为空");
        Assert.notNull(room, "房间信息不能为空");
        Assert.notNull(customer.getCheckInTime(), "入住时间不能为空");
        Assert.notNull(customer.getCheckOutTime(), "退房时间不能为空");

        // 创建账单
        Bill bill = new Bill();
        bill.setRoomId(room.getId());
        bill.setCheckInTime(customer.getCheckInTime());
        bill.setCheckOutTime(customer.getCheckOutTime());

        // 计算住宿天数
        long days = customer.getCheckInDays();
        bill.setStayDays((int) days);

        // 计算房费
        double roomFee = room.getDailyRate() * days;
        bill.setRoomFee(roomFee);

        // 计算空调总费用
        double acTotalFee = calculateAcTotalFee(billDetails);
        bill.setAcTotalFee(acTotalFee);

        // 计算总金额
        double totalAmount = roomFee + acTotalFee;
        bill.setTotalAmount(totalAmount);

        // 保存账单
        save(bill);

        // 记录日志
        log.info("创建并结算账单成功，房间ID: {}, 顾客ID: {}, 总金额: {}",
                room.getId(), customer.getId(), totalAmount);

        return bill;
    }

    @Override
    public List<Bill> getBillsByRoomId(Long roomId) {
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bill::getRoomId, roomId)
                .orderByDesc(Bill::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<Bill> getBillsByCustomerId(Long customerId) {
        // 这里需要关联查询顾客和账单，可能需要自定义SQL或使用关联查询
        // 简化实现，假设顾客ID和房间ID有对应关系
        return getBillsByRoomId(customerId);
    }

    @Override
    public Bill getFullBillById(Long billId) {
        Bill bill = getById(billId);
        if (bill != null) {
            // 获取账单对应的详单
            List<BillDetail> billDetails = billDetailService.getBillDetailsByRoomIdAndTimeRange(
                    bill.getRoomId(), bill.getCheckInTime(), bill.getCheckOutTime());
            bill.setBillDetails(billDetails);
        }
        return bill;
    }


    /**
     * 计算空调总费用
     */
    private double calculateAcTotalFee(List<BillDetail> billDetails) {
        return billDetails.stream()
                .mapToDouble(BillDetail::getCost)
                .sum();
    }
}
