package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ac_config")
public class ACConfig {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    private String mode;
    private Double minTemp;
    private Double maxTemp;
    private Double defaultTemp;
    private Double rate;
    private Double lowSpeedRate;
    private Double midSpeedRate;
    private Double highSpeedRate;
    private String defaultSpeed;
}
