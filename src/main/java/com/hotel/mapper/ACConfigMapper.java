package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.ACConfig;
import com.hotel.entity.AirConditioner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 空调Mapper接口
 */
@Mapper
public interface ACConfigMapper extends BaseMapper<ACConfig> {
} 