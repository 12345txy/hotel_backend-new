package com.hotel.service.impl;

import com.hotel.entity.ACConfig;
import com.hotel.entity.AirConditioner;
import com.hotel.entity.RoomRequest;
import com.hotel.mapper.ACConfigMapper;
import com.hotel.service.ACService;
import com.hotel.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ACServiceImpl implements ACService {

    private final ACConfigMapper acConfigMapper;
    private final RoomService roomService;

    @Value("${hotel.ac.total-count}")
    private int acCount;

    private final Map<Long, AirConditioner> acs;
    private ACConfig acConfig;

    @Autowired
    public ACServiceImpl(ACConfigMapper acConfigMapper,  RoomService roomService) {
        this.acConfigMapper = acConfigMapper;
        this.roomService = roomService;
        this.acs = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        acConfig = acConfigMapper.selectById(1);
        for (long id = 1; id <= acCount; id++) {
            acs.put( id, new AirConditioner(id,acConfig));
        }
        log.info("初始化{}个空调", acCount);
    }

    public void printStatus() {
        for (AirConditioner ac : acs.values()) {
            log.info("空调{}状态: {}", ac.getId(), ac.getSummary());
        }
    }

    @Override
    public RoomRequest initRequest(Long roomId) {
        RoomRequest request = new RoomRequest(roomId);
        request.setTargetTemp(acConfig.getDefaultTemp());
        request.setFanSpeed(acConfig.getDefaultSpeed());

        return request;
    }

    @Override
    public RoomRequest startAC(Long roomId) {
        for (AirConditioner ac : acs.values()) {
            if (!ac.getOn()) {
                ac.init(roomId);
                RoomRequest request = initRequest(roomId);
                request.setServingTime(LocalDateTime.now());
                request.setCurrentACId(ac.getId());
                request.setAcOn(true);
                return request;
            }
        }
        return null;
    }

    @Override
    public boolean changeTemp(Long acId, Double targetTemp) {
        AirConditioner ac = acs.get(acId);
        if (targetTemp < ac.getMinTemp() || targetTemp > ac.getMaxTemp()){
            return false;
        }
        ac.setTargetTemp(targetTemp);
        return true;
    }

    @Override
    public boolean changeFanSpeed(Long acId, String fanSpeed) {
        AirConditioner ac = acs.get(acId);
        if (fanSpeed == null || fanSpeed.isEmpty()){
            return false;
        }
        ac.setFanSpeed(fanSpeed);
        return true;
    }

    @Override
    public void tick(){
        // 降温
        for (AirConditioner ac : acs.values()) {
            if (ac.getOn() && ac.getServingRoomId() != null) {
                roomService.coolingRoom(ac.getServingRoomId(), ac.getCoolingRate());
            }
        }
    }

    @Override
    public void update(List<RoomRequest> servingRooms) {
        // 更新空调的服务房间
        // 需要关闭的空调
        log.info("关闭空调");
        for (AirConditioner ac : acs.values()) {
            if (ac.getOn()){
                boolean isServing = false;
                for (RoomRequest roomRequest : servingRooms) {
                    if (roomRequest.getRoomId().equals(ac.getServingRoomId())){
                        isServing = true;
                        break;
                    }
                }
                if (!isServing){
                    ac.setOn(false);
                    ac.setServingRoomId(null);
                    log.info("空调{}已停止服务", ac.getId());
                }
            }
        }
        // 启动未服务的请求
        log.info("启动未服务请求");
        for (RoomRequest roomRequest : servingRooms) {
            if (roomRequest.getCurrentACId() == null){
                for (AirConditioner ac : acs.values()) {
                    if (!ac.getOn() && ac.getServingRoomId() == null){
                        ac.init(roomRequest);
                        roomRequest.setCurrentACId(ac.getId());
                        roomRequest.setAcOn(true);
                        roomRequest.setServingTime(LocalDateTime.now());
                        break;
                    }
                }
            }
        }

    }

    @Override
    public AirConditioner getACByRoomId(Long roomId) {
        for (AirConditioner ac : acs.values()) {
            if (ac.getOn() && roomId.equals(ac.getServingRoomId())) {
                return ac;
            }
        }
        return null;
    }
}
