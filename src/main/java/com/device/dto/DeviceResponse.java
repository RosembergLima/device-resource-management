package com.device.dto;

import com.device.enums.DeviceStateEnum;
import java.time.Instant;

public record DeviceResponse(Long id,
                             String name,
                             String brand,
                             DeviceStateEnum state,
                             Instant creationTime) {

}
