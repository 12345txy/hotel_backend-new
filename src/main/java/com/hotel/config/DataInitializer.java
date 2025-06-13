package com.hotel.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
//    private final RoomMapper roomMapper;
//    private final AirConditionerMapper acMapper;
//
    @Override
    public void run(String... args) throws Exception {
//        initializeRooms();
//        initializeAirConditioners();
    }
//
//    /**
//     * 初始化房间数据
//     */
//    private void initializeRooms() {
//        // 检查是否已有房间数据
//        if (roomMapper.selectCount(null) > 0) {
//            log.info("房间数据已存在，跳过初始化");
//            return;
//        }
//
//        // 创建5个房间（根据测试用例配置初始温度）
//        double[] initialTemps = {32.0, 28.0, 30.0, 29.0, 35.0}; // 房间1-5的初始温度
//
//        for (int i = 1; i <= 5; i++) {
//            Room room = new Room()
//                .setId((long) i)  // 直接设置ID为1-5
//                .setStatus(RoomStatus.AVAILABLE.getCode())
//                .setCurrentTemp(initialTemps[i-1])
//                .setInitialTemp(initialTemps[i-1]) // 设置初始温度
//                .setAcOn(false)
//                .setIsWarmingBack(false)
//                .setServiceDuration(0L)
//                .setWaitingDuration(0L)
//                .setCreateTime(LocalDateTime.now())
//                .setUpdateTime(LocalDateTime.now());
//
//            // 使用insertOrUpdate确保ID正确设置
//            if (roomMapper.selectById(room.getId()) == null) {
//                roomMapper.insert(room);
//            }
//        }
//
//        log.info("成功初始化5个房间 (ID: 1-5)");
//    }
//
//    /**
//     * 初始化空调数据
//     */
//    private void initializeAirConditioners() {
//        // 检查是否已有空调数据
//        if (acMapper.selectCount(null) > 0) {
//            log.info("空调数据已存在，跳过初始化");
//            return;
//        }
//
//        // 创建3台空调
//        for (int i = 1; i <= 3; i++) {
//            AirConditioner ac = new AirConditioner()
//                .setAcNumber(i)
//                .setOn(false)
//                .setCurrentTemp(0.0)
//                .setTargetTemp(0.0)
//                .setServiceDuration(0L)
//                .setCost(0.0)
//                .setPriority(0)
//                .setServiceTime(0L)
//                .setCreateTime(LocalDateTime.now())
//                .setUpdateTime(LocalDateTime.now());
//
//            acMapper.insert(ac);
//        }
//
//        log.info("成功初始化3台空调");
//    }
} 