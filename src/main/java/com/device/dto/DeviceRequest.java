package com.device.dto;

import com.device.enums.DeviceStateEnum;
import jakarta.validation.constraints.NotBlank;

public record DeviceRequest(@NotBlank String name,
                            @NotBlank String brand,
                            DeviceStateEnum state) {

}
