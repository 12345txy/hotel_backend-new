package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Customer;
import com.hotel.mapper.CustomerMapper;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private RoomService roomService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer saveCustomer(Customer customer) {
        // 设置创建时间和入住状态
        customer.setCreateTime(LocalDateTime.now());
        customer.setStatus("CHECKED_IN");
        save(customer);
        log.info("顾客 {} 入住成功，房间ID: {}", customer.getName(), customer.getCurrentRoomId());
        return customer;
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<Customer> getCustomerByRoomId(Long roomId) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getCurrentRoomId, roomId)
                .eq(Customer::getStatus, "CHECKED_IN");
        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCustomer(Customer customer) {
        return updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkOut(Long roomId) {
        // 查询当前入住的顾客
        Optional<Customer> customerOpt = getCustomerByRoomId(roomId);
        if (customerOpt.isEmpty()) {
            log.warn("房间 {} 无当前入住顾客", roomId);
            return false;
        }

        Customer customer = customerOpt.get();
        // 设置退房时间和状态
        customer.setCheckOutTime(LocalDateTime.now());
        customer.setStatus("CHECKED_OUT");
        customer.setCurrentRoomId(null);

        // 更新顾客信息
        boolean updateResult = updateById(customer);

        // 更新房间状态为可用
        if (updateResult) {
            roomService.updateRoomStatus(roomId, "AVAILABLE");
            log.info("房间 {} 退房成功，顾客: {}", roomId, customer.getName());
        }

        return updateResult;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return list();
    }
    @Override
    public Customer findCurrentCustomerByRoomId(Long roomId) {
        // 构建查询条件：房间ID匹配且状态为"CHECKED_IN"
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("current_room_id", roomId)
                .eq("status", "CHECKED_IN");

        // 执行查询
        return baseMapper.selectOne(queryWrapper);
    }

}