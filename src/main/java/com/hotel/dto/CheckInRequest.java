package com.hotel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 入住请求DTO
 */
@Data
public class CheckInRequest {
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    private String idCard;
    
    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    private String phoneNumber;
    
    /**
     * 选择的房间号
     */
    @NotNull(message = "房间号不能为空")
    private Integer roomNumber;
} 