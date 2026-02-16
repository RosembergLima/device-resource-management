package com.oneglobal.dto;

import com.oneglobal.enums.DeviceStateEnum;

public record DevicePatchRequest(String name,
                                 String brand,
                                 DeviceStateEnum state) {
}
