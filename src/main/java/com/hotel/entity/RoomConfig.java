package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("room_config")
public class RoomConfig {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private Double defaultTemp;
    private Double dailyRate;
}
