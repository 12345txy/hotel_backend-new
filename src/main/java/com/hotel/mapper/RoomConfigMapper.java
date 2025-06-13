package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Room;
import com.hotel.entity.RoomConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房间Mapper接口
 */
@Mapper
public interface RoomConfigMapper extends BaseMapper<RoomConfig> {
} 