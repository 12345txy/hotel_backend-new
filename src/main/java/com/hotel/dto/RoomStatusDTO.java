package com.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomStatusDTO {
    private Long roomId;
    private Double currentTemp;
    private Double targetTemp;
    private String fanSpeed;
    private String mode;
}
