package com.oneglobal.mapper;

import com.oneglobal.dto.DevicePatchRequest;
import com.oneglobal.dto.DeviceRequest;
import com.oneglobal.dto.DeviceResponse;
import com.oneglobal.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationTime", ignore = true)
  Device toEntity(DeviceRequest dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationTime", ignore = true)
  void updateEntityFromDto(DeviceRequest dto, @MappingTarget Device entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationTime", ignore = true)
  void updateEntityFromPatchDto(DevicePatchRequest dto, @MappingTarget Device entity);

  DeviceResponse toResponse(Device entity);
}

