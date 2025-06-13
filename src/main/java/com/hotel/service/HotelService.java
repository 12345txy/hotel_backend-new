package com.hotel.service;

/**
 * 酒店服务类
 */
public interface HotelService {

//    private final RoomMapper roomMapper;
//    private final CustomerMapper customerMapper;
//    private final BillDetailMapper billDetailMapper;
//    private final BillMapper billMapper;
//    private final ACScheduleService scheduleService;
//    private final TemperatureService temperatureService;
//
//    @Value("${hotel.billing.room-rate:100.0}")
//    private double roomRate;
//
//    /**
//     * 获取可用房间列表
//     */
//    public List<Room> getAvailableRooms() {
//        return roomMapper.findAvailableRooms();
//    }
//
//    /**
//     * 办理入住
//     */
//    @Transactional
//    public String checkIn(CheckInRequest request) {
//        // 检查房间是否可用（roomId直接就是1-5）
//        Room room = roomMapper.selectById(request.getRoomNumber().longValue());
//        if (room == null) {
//            throw new RuntimeException("房间不存在");
//        }
//
//        if (!RoomStatus.AVAILABLE.getCode().equals(room.getStatus())) {
//            throw new RuntimeException("房间不可用");
//        }
//
//        // 创建客户记录
//        Customer customer = new Customer()
//            .setName(request.getName())
//            .setIdCard(request.getIdCard())
//            .setPhoneNumber(request.getPhoneNumber())
//            .setCurrentRoomId(room.getId())
//            .setCheckInTime(LocalDateTime.now())
//            .setStatus("CHECKED_IN")
//            .setCreateTime(LocalDateTime.now())
//            .setUpdateTime(LocalDateTime.now());
//
//        customerMapper.insert(customer);
//
//        // 更新房间状态
//        room.setStatus(RoomStatus.OCCUPIED.getCode())
//            .setCustomerId(customer.getId())
//            .setCheckInTime(LocalDateTime.now())
//            .setCurrentTemp(32.0) // 初始温度
//            .setAcOn(false)
//            .setUpdateTime(LocalDateTime.now());
//
//        roomMapper.updateById(room);
//
//        log.info("客户{}成功入住房间{}", customer.getName(), room.getId());
//        return "入住成功";
//    }
//
//    /**
//     * 办理退房
//     */
//    @Transactional
//    public BillResponse checkOut(Long roomId) {
//        Room room = roomMapper.findByRoomId(roomId);
//        if (room == null || !RoomStatus.OCCUPIED.getCode().equals(room.getStatus())) {
//            throw new RuntimeException("房间未入住");
//        }
//
//        Customer customer = customerMapper.selectById(room.getCustomerId());
//        if (customer == null) {
//            throw new RuntimeException("客户信息不存在");
//        }
//
//        // 关闭空调（如果开启的话）
//        if (Boolean.TRUE.equals(room.getAcOn())) {
//            scheduleService.removeAcRequest(room.getId().intValue(), "退房关机");
//        }
//
//        // 计算账单
//        LocalDateTime checkOutTime = LocalDateTime.now();
//        long stayDaysLong = Duration.between(customer.getCheckInTime(), checkOutTime).toDays();
//        if (stayDaysLong == 0) stayDaysLong = 1; // 至少一天
//        int stayDays = (int) stayDaysLong;
//
//        double roomFee = stayDays * roomRate;
//
//        // 计算空调费用
//        List<BillDetail> acDetails = billDetailMapper.findByCustomerId(customer.getId());
//        double acTotalFee = acDetails.stream().mapToDouble(BillDetail::getCost).sum();
//
//        double totalAmount = roomFee + acTotalFee;
//
//        // 生成账单号
//        String billNumber = "BILL" + System.currentTimeMillis();
//
//        // 创建账单主表
//        Bill bill = new Bill();
//        bill.setBillNumber(billNumber);
//        bill.setRoomId(room.getId());
//        bill.setCustomerId(customer.getId());
//        bill.setCustomerName(customer.getName());
//        bill.setCheckInTime(customer.getCheckInTime());
//        bill.setCheckOutTime(checkOutTime);
//        bill.setStayDays(stayDays);
//        bill.setRoomFee(roomFee);
//        bill.setAcTotalFee(acTotalFee);
//        bill.setTotalAmount(totalAmount);
//        bill.setStatus(BillStatus.UNPAID.getCode());
//        bill.setCreateTime(LocalDateTime.now());
//        bill.setUpdateTime(LocalDateTime.now());
//
//        billMapper.insert(bill);
//
//        // 更新详单关联账单ID
//        for (BillDetail detail : acDetails) {
//            detail.setBillId(bill.getId());
//            billDetailMapper.updateById(detail);
//        }
//
//        // 更新客户和房间状态
//        customer.setCheckOutTime(checkOutTime)
//            .setCurrentRoomId(null)
//            .setStatus("CHECKED_OUT")
//            .setUpdateTime(LocalDateTime.now());
//        customerMapper.updateById(customer);
//
//        room.setStatus(RoomStatus.AVAILABLE.getCode())
//            .setCustomerId(null)
//            .setCheckInTime(null)
//            .setAcOn(false)
//            .setAcMode(null)
//            .setFanSpeed(null)
//            .setTargetTemp(null)
//            .setCurrentAcId(null)
//            .setAcRequestTime(null)
//            .setUpdateTime(LocalDateTime.now());
//        roomMapper.updateById(room);
//
//        // 设置账单的详单列表
//        bill.setBillDetails(acDetails);
//
//        // 构建账单响应
//        BillResponse billResponse = BillResponse.fromBill(bill);
//
//        log.info("客户{}成功退房，总费用{}", customer.getName(), totalAmount);
//        return billResponse;
//    }
//
//    /**
//     * 开启空调
//     * @param roomId 房间ID
//     * @param currentTemp 当前温度（可选参数）
//     */
//    @Transactional
//    public String startAc(Long roomId, Double currentTemp) {
//        Room room = roomMapper.findByRoomId(roomId);
//        if (room == null || !RoomStatus.OCCUPIED.getCode().equals(room.getStatus())) {
//            throw new RuntimeException("房间未入住");
//        }
//
//        if (Boolean.TRUE.equals(room.getAcOn())) {
//            return "空调已开启";
//        }
//
//        // 如果提供了当前温度参数，则设置它
//        if (currentTemp != null) {
//            room.setCurrentTemp(currentTemp);
//            room.setInitialTemp(currentTemp); // 同时更新初始温度
//            log.info("房间{}设置当前温度为{}°C", room.getId(), currentTemp);
//        }
//
//        // 设置默认参数
//        room.setAcOn(true)
//            .setAcMode("COOLING")
//            .setFanSpeed("MEDIUM")
//            .setTargetTemp(25.0)
//            .setAcRequestTime(LocalDateTime.now())
//            .setWaitingStartTime(LocalDateTime.now())
//            .setWaitingDuration(0L)
//            .setServiceDuration(0L)
//            .setIsWarmingBack(false)
//            .setUpdateTime(LocalDateTime.now());
//
//        roomMapper.updateById(room);
//
//        // 添加到调度队列
//        String scheduleResult = scheduleService.addAcRequest(room);
//
//        String tempInfo = currentTemp != null ?
//            String.format("，当前温度已设置为%.1f°C", currentTemp) : "";
//
//        log.info("房间{}开启空调{}", room.getId(), tempInfo);
//        return "空调请求已提交" + tempInfo + "\n" + scheduleResult;
//    }
//
//    /**
//     * 开启空调（重载方法，保持向后兼容）
//     */
//    @Transactional
//    public String startAc(Long roomId) {
//        return startAc(roomId, null);
//    }
//
//    /**
//     * 关闭空调
//     */
//    @Transactional
//    public String stopAc(Long roomId) {
//        Room room = roomMapper.findByRoomId(roomId);
//        if (room == null || !Boolean.TRUE.equals(room.getAcOn())) {
//            return "空调未开启";
//        }
//
//        // 生成详单
//        createBillDetail(room, "AC_OFF");
//
//        // 更新房间状态
//        room.setAcOn(false)
//            .setAcMode(null)
//            .setFanSpeed(null)
//            .setTargetTemp(null)
//            .setCurrentAcId(null)
//            .setAcRequestTime(null)
//            .setUpdateTime(LocalDateTime.now());
//
//        roomMapper.updateById(room);
//
//        // 从调度队列移除
//        String scheduleResult = scheduleService.removeAcRequest(room.getId().intValue(), "关机");
//
//        log.info("房间{}关闭空调", room.getId());
//        return "空调已关闭\n" + scheduleResult;
//    }
//
//    /**
//     * 调节空调参数
//     */
//    @Transactional
//    public String adjustAc(Long roomId, String mode, String fanSpeed, Double targetTemp) {
//        Room room = roomMapper.findByRoomId(roomId);
//        if (room == null || !Boolean.TRUE.equals(room.getAcOn())) {
//            throw new RuntimeException("空调未开启");
//        }
//
//        boolean fanSpeedChanged = false;
//
//        // 更新房间参数
//        if (mode != null) {
//            room.setAcMode(mode);
//        }
//        if (fanSpeed != null && !fanSpeed.equals(room.getFanSpeed())) {
//            room.setFanSpeed(fanSpeed);
//            fanSpeedChanged = true;
//        }
//        if (targetTemp != null) {
//            room.setTargetTemp(targetTemp);
//        }
//        room.setUpdateTime(LocalDateTime.now());
//
//        roomMapper.updateById(room);
//
//        String scheduleResult = "";
//        if (fanSpeedChanged) {
//            // 风速变化会影响调度优先级
//            scheduleResult = scheduleService.adjustAcRequest(room.getId().intValue(), fanSpeed, mode, targetTemp);
//            log.info("房间{}调整空调参数", room.getId());
//        } else {
//            log.info("房间{}调整空调参数", room.getId());
//        }
//
//        return "空调参数已调整\n" + scheduleResult;
//    }
//
//    /**
//     * 创建账单详单
//     */
//    private void createBillDetail(Room room, String detailType) {
//        if (room.getServiceStartTime() == null) {
//            return; // 没有服务开始时间，不生成详单
//        }
//
//        LocalDateTime endTime = LocalDateTime.now();
//        Duration duration = Duration.between(room.getServiceStartTime(), endTime);
//        long minutes = duration.toMinutes();
//
//        if (minutes <= 0) {
//            return; // 服务时间不足1分钟，不生成详单
//        }
//
//        double rate = getRateByFanSpeed(room.getFanSpeed());
//        double cost = minutes * rate;
//
//        BillDetail detail = new BillDetail()
//            .setBillId(null) // 退房时会设置
//            .setRoomId(room.getId())
//            .setCustomerId(room.getCustomerId())
//            .setAcId(room.getCurrentAcId())
//            .setAcMode(room.getAcMode())
//            .setFanSpeed(room.getFanSpeed())
//            .setStartTime(room.getServiceStartTime())
//            .setEndTime(endTime)
//            .setDuration(minutes)
//            .setCost(cost)
//            .setRate(rate)
//            .setDetailType(detailType)
//            .setCreateTime(LocalDateTime.now());
//
//        billDetailMapper.insert(detail);
//
//        log.info("房间{}生成账单详单，费用{}", room.getId(), cost);
//    }
//
//    /**
//     * 根据风速获取费率
//     */
//    private double getRateByFanSpeed(String fanSpeed) {
//        if (fanSpeed == null) return 0.33;
//
//        switch (fanSpeed) {
//            case "LOW": return 0.33;
//            case "MEDIUM": return 0.5;
//            case "HIGH": return 1.0;
//            default: return 0.33;
//        }
//    }
} 