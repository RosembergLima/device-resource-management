package com.device.mapper;

import com.device.dto.DevicePatchRequest;
import com.device.dto.DeviceRequest;
import com.device.dto.DeviceResponse;
import com.device.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceMapper {

  /**
   * Maps a creation DTO to a new Device entity. The id and creationTime are ignored.
   * @param dto request payload
   * @return new Device entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationTime", ignore = true)
  @Mapping(target = "state", defaultValue = "AVAILABLE")
  Device toEntity(DeviceRequest dto);

  /**
   * Applies a patch request to an existing entity, ignoring null values.
   * @param dto patch payload with nullable fields
   * @param entity target entity to update
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creationTime", ignore = true)
  void updateEntityFromPatchDto(DevicePatchRequest dto, @MappingTarget Device entity);

  /**
   * Maps a Device entity to its response DTO.
   * @param entity device entity
   * @return response DTO
   */
  DeviceResponse toResponse(Device entity);
}

