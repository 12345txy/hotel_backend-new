package com.hotel.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.BillDetail;
import com.hotel.entity.Customer;
import com.hotel.entity.RoomRequest;
import com.hotel.mapper.BillDetailMapper;
import com.hotel.service.BillDetailService;
import com.hotel.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账单详单服务实现类
 */
@Service
@Slf4j
public class BillDetailServiceImpl extends ServiceImpl<BillDetailMapper, BillDetail> implements BillDetailService {
    @Value("${hotel.time-multiplier}")
    private int timeMultiplier;

    @Autowired
    private CustomerService customerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBillDetail(BillDetail billDetail) {
        // 参数校验
        Assert.notNull(billDetail, "账单详单不能为空");
        Assert.notNull(billDetail.getRoomId(), "房间ID不能为空");
        Assert.notNull(billDetail.getAcMode(), "空调模式不能为空");
        Assert.notNull(billDetail.getFanSpeed(), "风速不能为空");
        Assert.notNull(billDetail.getStartTime(), "开始时间不能为空");
        Assert.notNull(billDetail.getEndTime(), "结束时间不能为空");
        Assert.notNull(billDetail.getRate(), "费率不能为空");

        // 设置创建时间
        billDetail.setCreateTime(LocalDateTime.now());

        // 计算持续时间（分钟）
        Duration duration = Duration.between(billDetail.getStartTime(), billDetail.getEndTime());
        long minutes = duration.toMinutes();
        billDetail.setDuration(minutes);

        // 计算费用
        billDetail.setCost(billDetail.getRate() * minutes);

        // 保存到数据库
        return save(billDetail);
    }

    @Override
    public List<BillDetail> getBillDetailsByRoomIdAndTimeRange(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        // 参数校验
        Assert.notNull(roomId, "房间ID不能为空");
        Assert.notNull(startTime, "开始时间不能为空");
        Assert.notNull(endTime, "结束时间不能为空");

        // 构建查询条件
        QueryWrapper<BillDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId)
                .ge("start_time", startTime)
                .le("end_time", endTime)
                .orderByAsc("start_time");

        // 执行查询
        return list(queryWrapper);
    }

    @Override
    public List<BillDetail> getBillDetailsByRoomId(Long roomId) {
        Assert.notNull(roomId, "房间ID不能为空");
        
        QueryWrapper<BillDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId)
                .orderByAsc("start_time");
        
        return list(queryWrapper);
    }

    @Override
    public List<BillDetail> getBillDetailsByRoomIdAndCustomerId(Long roomId, Long customerId) {
        Assert.notNull(roomId, "房间ID不能为空");
        Assert.notNull(customerId, "客户ID不能为空");
        
        QueryWrapper<BillDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId)
                .eq("customer_id", customerId)
                .orderByAsc("start_time");
        
        return list(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillDetail createBillDetailByRequest(RoomRequest request, String detailType, int mode, int tick) {
        // 参数校验
        Assert.notNull(request, "房间请求不能为空");
        Assert.notNull(request.getRoomId(), "房间ID不能为空");
        Assert.notNull(request.getServingTime(), "服务开始时间不能为空");
        Assert.hasText(detailType, "详单类型不能为空");
        log.info("请求已校验");
        
        if (request.getJustRecord()){
            log.info("重复记录");
            return null;
        }
        request.setJustRecord(true);
        
        // 创建账单详单
        BillDetail billDetail = new BillDetail();
        billDetail.setRoomId(request.getRoomId());
        
        // 新增：获取当前房间的住户ID
        Customer currentCustomer = customerService.findCurrentCustomerByRoomId(request.getRoomId());
        if (currentCustomer != null) {
            billDetail.setCustomerId(currentCustomer.getId());
            log.info("关联客户ID: {} 到详单", currentCustomer.getId());
        } else {
            log.warn("房间 {} 当前没有住户，无法关联客户ID", request.getRoomId());
        }
        
        billDetail.setAcMode(mode == 1 ? "COOLING" : "HEATING");
        billDetail.setFanSpeed(request.getFanSpeed());
        billDetail.setStartTime(request.getServingTime());
        billDetail.setEndTime(LocalDateTime.now());
        billDetail.setDetailType(detailType);
        billDetail.setRequestTime(request.getRequestTime());
        
        // 计算持续时间（分钟）
        int duration = tick - request.getTick();
        billDetail.setDuration((long)duration);

        // 设置费率
        double rate;
        switch (request.getFanSpeed()) {
            case "L":
                rate = 1.0 / 3; // 低风 1/3 元每分钟
                break;
            case "M":
                rate = 0.5;     // 中风 0.5 元每分钟
                break;
            case "H":
                rate = 1.0;     // 高风 1 元每分钟
                break;
            default:
                rate = 0.5;     // 默认费率
        }
        billDetail.setRate(rate);
        billDetail.setCreateTime(LocalDateTime.now());
        // 计算费用
        billDetail.setCost(rate * duration);

        // 保存到数据库
        boolean success = save(billDetail);

        return success ? billDetail : null;
    }
}
