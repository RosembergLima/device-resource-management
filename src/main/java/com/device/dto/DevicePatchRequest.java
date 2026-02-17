package com.device.dto;

import com.device.enums.DeviceStateEnum;

public record DevicePatchRequest(String name,
                                 String brand,
                                 DeviceStateEnum state) {
}
