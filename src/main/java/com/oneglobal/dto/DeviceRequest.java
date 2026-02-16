package com.oneglobal.dto;

import com.oneglobal.enums.DeviceStateEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeviceRequest(@NotBlank String name,
                            @NotBlank String brand,
                            @NotNull DeviceStateEnum state) {

}
