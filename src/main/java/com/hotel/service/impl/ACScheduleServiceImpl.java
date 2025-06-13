package com.hotel.service.impl;

import com.hotel.entity.*;
import com.hotel.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class ACScheduleServiceImpl implements ACScheduleService {
    private final ACService acService;
    private final RoomService roomService;
    private final BillDetailService billDetailService;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final CustomerService customerService;

    @Value("${hotel.ac.total-count}")
    private int acCount;

    @Value("${hotel.ac.time-slice}")
    private int timeSlice;

    @Value("${hotel.ac.heating-rate}")
    private double heatingRate;

    @Value("${hotel.ac.mode}")
    private int mode;

    @Value("${hotel.ac.wake-up-temp}")
    private double wakeUpTemp;

    @Value("${hotel.time-multiplier}")
    private int timeMultiplier;

    @Value("${hotel.tick-time}")
    private int tickTime;

    private int globalTick;

    private final ReentrantLock scheduleLock = new ReentrantLock();
    private ServingQueue servingQueue;
    private final WaitingQueue waitingQueue = new WaitingQueue();
    private final Map<Long, RoomRequest> sleepingRequests = new HashMap<>();
    private boolean isRunning = false;


    @Autowired
    public ACScheduleServiceImpl(ACService acService, RoomService roomService, BillDetailService billDetailService, CustomerService customerService) {
        this.acService = acService;
        this.roomService = roomService;
        this.billDetailService = billDetailService;
        this.customerService = customerService;
        this.globalTick = 0;
    }

    @PostConstruct
    public void init() {
        servingQueue = new ServingQueue(timeMultiplier);
    }

    private void printStatus() {
        log.info("开始打印状态");
        acService.printStatus();
        roomService.printStatus();
        servingQueue.printStatus();
        waitingQueue.printStatus();
        // 打印休眠请求
        log.info("休眠请求：");
        for (Map.Entry<Long, RoomRequest> entry : sleepingRequests.entrySet()) {
            log.info(entry.getValue().getAllInfo());
        }
    }

    /**
     * 实现时间片轮转和调度逻辑
     */
    private void schedule() {
        log.info("开始调度");
        // 移除服务队列中达到目标温度的房间
        log.info("移除服务队列中达到目标温度的房间");
        List<RoomRequest> servingRoomIds = servingQueue.getAllRequests();
        for (RoomRequest request : servingRoomIds) {
            Long roomId = request.getRoomId();
            Room room = roomService.getRoomById(roomId);
            // 如果当前温度小于等于目标温度，则移除出服务队列
            if ((room.getCurrentTemp() - request.getTargetTemp()) * mode <= 0) {
                RoomRequest sleepingRequest = servingQueue.dequeue(roomId);
                // todo: 记录详单
                BillDetail detail = billDetailService.createBillDetailByRequest(request, "TARGET_REACHED", mode, globalTick);
//                billDetailService.saveBillDetail(detail);

                request.sleep();
                sleepingRequests.put(roomId, sleepingRequest);
                log.info("房间{}已满足要求，进入休眠队列", roomId);
            }
        }
        // 查看是否有恢复请求的房间
        log.info("查看是否有恢复请求的房间");
        List<Long> wakeUpRoomIds = new ArrayList<>();
        for (RoomRequest request : sleepingRequests.values()) {
            Long roomId = request.getRoomId();
            Room room = roomService.getRoomById(roomId);
            // 如果当前温度大于等于目标温度+唤醒温度，则加入等待队列
            if ((room.getCurrentTemp() - request.getTargetTemp()) * mode >= wakeUpTemp) {
                wakeUpRoomIds.add(roomId);
                waitingQueue.enqueue(request);
                log.info("房间{}已加入等待队列", roomId);
            }
        }
        wakeUpRoomIds.forEach(sleepingRequests::remove);
        // 判断服务队列是否有空余
        log.info("服务队列剩余{}个位置", acCount - servingQueue.size());
        while (servingQueue.size() < acCount) {
            if (waitingQueue.size() > 0) {
                RoomRequest request = waitingQueue.dequeue();
                servingQueue.enqueue(request, globalTick);
                log.info("房间{}已加入服务队列", request.getRoomId());
            } else {
                break;
            }
        }
        // 判断是否发生置换
        log.info("开始检查置换");
        while (waitingQueue.size() > 0) {
            RoomRequest request = waitingQueue.peek();
            if (servingQueue.checkReplace(request, timeSlice)) {
                waitingQueue.dequeue();
                RoomRequest waitingRequest = servingQueue.dequeue();
                // todo: 记录详单
                BillDetail  detail = billDetailService.createBillDetailByRequest(waitingRequest, "SCHEDULE", mode, globalTick);
//                billDetailService.saveBillDetail(detail);

                waitingQueue.enqueue(waitingRequest);
                servingQueue.enqueue(request, globalTick);
                log.info("发生置换, 房间{}换入, 房间{}换出",
                        request.getRoomId(), waitingRequest.getRoomId());
            } else {
                break;
            }
        }
        // 更新空调状态
        log.info("更新空调状态");
        acService.update(servingQueue.getAllRequests());
        // 更新请求状态
        for (RoomRequest request : servingQueue.getAllRequests()){
            request.setJustRecord(false);
        }
        for (RoomRequest request : sleepingRequests.values()){
            request.setJustRecord(false);
        }
        for (RoomRequest request : waitingQueue.getAllRequests()){
            request.setJustRecord(false);
        }
        log.info("调度完成");
    }

    private void startTick() {
        Runnable task = this::tick;
        executor.scheduleAtFixedRate(task, 4500, tickTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 实现系统时钟
     * 每5秒执行一次
     */
    private void tick() {
        log.info("tick");
        scheduleLock.lock();

        // 进行调度
        schedule();
        // 打印状态
        printStatus();
        // 空调工作
        acService.tick();
        // 房间升温
        List<Long> servingRoomIds = servingQueue.getAllRoomIds();
        roomService.heatingRooms(servingRoomIds, heatingRate);
        // 全局时间增加
        globalTick++;

        scheduleLock.unlock();
    }


    public String startAC(Long roomId, Double currentTemp) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return "房间不存在";
        }
        String result;

        if (!isRunning){
            isRunning = true;
            startTick();
        }

        scheduleLock.lock();
        try {
            log.info("房间{}请求空调服务", roomId);
            // 检查是否已有请求
            RoomRequest oldRequest = servingQueue.getRoomRequest(roomId);
            if (oldRequest == null) {
                oldRequest = waitingQueue.getRoomRequest(roomId);
            }
            if (oldRequest != null) {
                result = "房间已请求空调服务";
            } else {
                // 判断是否到达温度
                RoomRequest defaultRequest = acService.initRequest(roomId);
                if ((room.getCurrentTemp() - defaultRequest.getTargetTemp()) * mode <= 0) {
                    sleepingRequests.put(roomId, defaultRequest);
                    result = "已达到目标温度, 进入休眠状态";
                } else  // 根据队列创建请求
                if (servingQueue.size() < acCount) {
                    // 添加到服务队列
                    RoomRequest request = acService.startAC(roomId);
                    if (request == null) {
                        return "无法启动空调";
                    }
                    servingQueue.enqueue(request, globalTick);
                    result = "空调已启动";
                } else {
                    // 添加到等待队列
                    RoomRequest request = acService.initRequest(roomId);
                    waitingQueue.enqueue(request);
                    result = "排队中";
                }
            }

        } finally {
            scheduleLock.unlock();
        }

        return result;
    }

    @Override
    public String changeTemp(Long roomId, Double targetTemp) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return "房间不存在";
        }
        String result;

        scheduleLock.lock();
        try {
            log.info("房间{}请求调整温度", roomId);
            if (servingQueue.checkRoomId(roomId)) {
                // 请求在服务队列
                RoomRequest request = servingQueue.getRoomRequest(roomId);
                if (!acService.changeTemp(request.getCurrentACId(), targetTemp)) {
                    result = "空调无法调整至目标温度";
                    log.info(result);
                } else {
                    result = servingQueue.changeTemp(roomId, targetTemp);
                    log.info("{} in serve -temp", result);
                }
            } else if (waitingQueue.checkRoomId(roomId)) {
                // 请求在等待队列
                result = waitingQueue.changeTemp(roomId, targetTemp);
                log.info("{} in wait -temp", result);
            } else if (sleepingRequests.get(roomId) != null) {
                RoomRequest request = sleepingRequests.get(roomId);
                request.setTargetTemp(targetTemp);
                result = "温度已调整";
                log.info("{} in sleep -temp", result);
            } else {
                result = "房间未开启空调";
                log.info(result);
            }

        } finally {
            scheduleLock.unlock();
        }
        return result;
    }

    public String changeFanSpeed(Long roomId, String fanSpeed) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return "房间不存在";
        }
        String result;

        scheduleLock.lock();
        try {
            log.info("房间{}请求调整风速", roomId);
            if (servingQueue.checkRoomId(roomId)) {
                // 请求在服务队列
                RoomRequest request = servingQueue.getRoomRequest(roomId);
                if (!acService.changeFanSpeed(request.getCurrentACId(), fanSpeed)) {
                    result = "空调无法调整至目标风速";
                    log.info(result);
                } else {
                    // todo: 记录详单
                    BillDetail detail = billDetailService.createBillDetailByRequest(request, "ADJUST_FAN", mode, globalTick);
//                    billDetailService.saveBillDetail(detail);

                    result = servingQueue.changeFanSpeed(roomId, fanSpeed, globalTick);
                    log.info("{} in serve -fan", result);
                }
            } else if (waitingQueue.checkRoomId(roomId)) {
                // 请求在等待队列
                result = waitingQueue.changeFanSpeed(roomId, fanSpeed);
                log.info("{} in wait -fan", result);
            } else if (sleepingRequests.get(roomId) != null) {
                RoomRequest request = sleepingRequests.get(roomId);
                request.setFanSpeed(fanSpeed);
                result = "风速已调整";
                log.info("{} in sleep -fan", result);
            } else {
                result = "房间未开启空调";
                log.info(result);
            }

        } finally {
            scheduleLock.unlock();
        }
        return result;
    }

    public String stopAC(Long roomId) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return "房间不存在";
        }
        String result;

        scheduleLock.lock();
        try {
            log.info("房间{}关闭空调服务", roomId);
            Customer customer = customerService.findCurrentCustomerByRoomId(roomId);
            customer.setCheckInDays(customer.getCheckInDays()+1);
            customerService.updateCustomer(customer); // 假设存在此方法
            System.out.println("房间:"+roomId+"入住天数"+customer.getCheckInDays());

            // 检查是否已有请求
            RoomRequest request = servingQueue.getRoomRequest(roomId);
            if (request != null) {
                // todo: 记录详单
                BillDetail detail = billDetailService.createBillDetailByRequest(request, "AC_OFF", mode, globalTick);
//                billDetailService.saveBillDetail(detail);

                // 从服务队列中移除并记录详单
                servingQueue.dequeue(roomId);
            } else {
                // 从等待队列和休眠队列移除
                waitingQueue.dequeue(roomId);
                sleepingRequests.remove(roomId);
            }
            result = "房间空调已关闭";
        } finally {
            scheduleLock.unlock();
        }

        return result;
    }

    @Override
    public List<RoomRequest> getServingQueue() {
        return new ArrayList<>(servingQueue.getQueue());
    }

    @Override
    public List<RoomRequest> getWaitingQueue() {
        return new ArrayList<>(waitingQueue.getQueue());
    }

    @Override
    public Map<String, Object> getRoomACAccumulatedData(Long roomId) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        
        // 获取当前房间的住户
        Customer currentCustomer = customerService.findCurrentCustomerByRoomId(roomId);
        if (currentCustomer == null) {
            throw new RuntimeException("房间当前无住户");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        scheduleLock.lock();
        try {
            // 实时计算：基于内存中的RoomRequest数据
            int totalTime = 0;
            double totalFee = 0.0;
            
            // 1. 检查服务队列中的请求
            RoomRequest servingRequest = servingQueue.getRoomRequest(roomId);
            if (servingRequest != null && servingRequest.getServingTime() != null) {
                // 计算当前正在服务的时间和费用
                int currentDuration = globalTick - servingRequest.getTick();
                double currentFee = calculateFee(servingRequest.getFanSpeed(), currentDuration);
                
                totalTime += currentDuration;
                totalFee += currentFee;
                
                log.info("房间{}当前正在服务: {}分钟, {}元", roomId, currentDuration, currentFee);
            }
            
            // 2. 检查等待队列中的请求
            RoomRequest waitingRequest = waitingQueue.getRoomRequest(roomId);
            if (waitingRequest != null) {
                // 等待中不产生费用，但记录等待时间
                log.info("房间{}当前在等待队列", roomId);
            }
            
            // 3. 检查休眠队列中的请求
            RoomRequest sleepingRequest = sleepingRequests.get(roomId);
            if (sleepingRequest != null) {
                log.info("房间{}当前在休眠状态", roomId);
            }
            
            // 4. 添加数据库中已有的详单（如果需要历史累计）
            List<BillDetail> billDetails = billDetailService.getBillDetailsByRoomIdAndCustomerId(roomId, currentCustomer.getId());
            long dbTotalTime = billDetails.stream().mapToLong(BillDetail::getDuration).sum();
            double dbTotalFee = billDetails.stream().mapToDouble(BillDetail::getCost).sum();
            
            totalTime += (int) dbTotalTime;
            totalFee += dbTotalFee;
            
            result.put("Time", totalTime);
            result.put("Fee", Math.round(totalFee * 100.0) / 100.0);
            result.put("customerId", currentCustomer.getId());
            result.put("customerName", currentCustomer.getName());
            result.put("status", getACStatus(roomId)); // 添加当前状态
            
            log.info("房间{}实时统计: 总时间{}分钟, 总费用{}元", roomId, totalTime, totalFee);
            
        } finally {
            scheduleLock.unlock();
        }
        
        return result;
    }

    // 辅助方法：计算费用
    private double calculateFee(String fanSpeed, int duration) {
        double rate;
        switch (fanSpeed) {
            case "L":
                rate = 1.0 / 3;
                break;
            case "M":
                rate = 0.5;
                break;
            case "H":
                rate = 1.0;
                break;
            default:
                rate = 0.5;
        }
        return rate * duration;
    }

    // 辅助方法：获取空调状态
    private String getACStatus(Long roomId) {
        if (servingQueue.checkRoomId(roomId)) {
            return "SERVING";
        } else if (waitingQueue.checkRoomId(roomId)) {
            return "WAITING";
        } else if (sleepingRequests.containsKey(roomId)) {
            return "SLEEPING";
        } else {
            return "OFF";
        }
    }
}