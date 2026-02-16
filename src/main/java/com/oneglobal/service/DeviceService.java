package com.oneglobal.service;

import com.oneglobal.dto.DevicePatchRequest;
import com.oneglobal.dto.DeviceRequest;
import com.oneglobal.dto.DeviceResponse;
import com.oneglobal.enums.DeviceStateEnum;
import com.oneglobal.mapper.DeviceMapper;
import com.oneglobal.model.Device;
import com.oneglobal.repository.DeviceRepository;
import com.oneglobal.service.exception.DeviceInUseException;
import com.oneglobal.service.exception.DeviceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService {

  private final DeviceRepository repository;
  private final DeviceMapper mapper;

  @Transactional
  public DeviceResponse create(DeviceRequest req) {
    Device device = mapper.toEntity(req);
    return mapper.toResponse(repository.save(device));
  }

  @Transactional
  public DeviceResponse partialUpdate(Long id, DevicePatchRequest req) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
        throw new DeviceInUseException("Cannot update the device with state IN_USE");
    }
    mapper.updateEntityFromPatchDto(req, device);
    return mapper.toResponse(repository.save(device));
  }

  public DeviceResponse findById(Long id) {
    return mapper.toResponse(findOrThrow(id));
  }

  public Page<DeviceResponse> findAll(String brand, DeviceStateEnum state, Pageable pageable) {
    if (brand != null && state != null) {
      return repository.findByBrandIgnoreCaseAndState(brand, state, pageable).map(mapper::toResponse);
    } else if (brand != null) {
      return repository.findByBrandIgnoreCase(brand, pageable).map(mapper::toResponse);
    } else if (state != null) {
      return repository.findByState(state, pageable).map(mapper::toResponse);
    }
    return repository.findAll(pageable).map(mapper::toResponse);
  }

  @Transactional
  public void delete(Long id) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
      throw new DeviceInUseException("In-use devices cannot be deleted");
    }
    repository.delete(device);
  }

  private Device findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
  }
}
