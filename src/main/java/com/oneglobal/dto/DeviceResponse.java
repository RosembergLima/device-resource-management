package com.oneglobal.dto;

import com.oneglobal.enums.DeviceStateEnum;
import java.time.Instant;

public record DeviceResponse(Long id,
                             String name,
                             String brand,
                             DeviceStateEnum state,
                             Instant creationTime) {

}
