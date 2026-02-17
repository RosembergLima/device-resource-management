package com.device.service;

import com.device.dto.DevicePatchRequest;
import com.device.dto.DeviceRequest;
import com.device.dto.DeviceResponse;
import com.device.enums.DeviceStateEnum;
import com.device.mapper.DeviceMapper;
import com.device.model.Device;
import com.device.repository.DeviceRepository;
import com.device.service.exception.DeviceInUseException;
import com.device.service.exception.DeviceNotFoundException;
import com.device.specification.DeviceSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    device.setState(DeviceStateEnum.AVAILABLE);
    return mapper.toResponse(repository.save(device));
  }

  @Transactional
  public DeviceResponse update(Long id, DevicePatchRequest req) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
        throw new DeviceInUseException("Cannot update the device with state IN_USE");
    }
    mapper.updateEntityFromPatchDto(req, device);
    return mapper.toResponse(repository.save(device));
  }

  @Transactional(readOnly = true)
  public DeviceResponse findById(Long id) {
    return mapper.toResponse(findOrThrow(id));
  }

  @Transactional(readOnly = true)
  public Page<DeviceResponse> findAll(String brand, DeviceStateEnum state, Pageable pageable) {

    DeviceStateEnum effectiveState = (state != null) ? state : DeviceStateEnum.AVAILABLE;

    Specification<Device> spec = Specification.where(DeviceSpecifications.withBrand(brand))
        .and(DeviceSpecifications.withState(effectiveState));

    return repository.findAll(spec, pageable)
        .map(mapper::toResponse);
  }

  @Transactional
  public void delete(Long id) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
      throw new DeviceInUseException("In-use devices cannot be deleted");
    }
    device.setState(DeviceStateEnum.INACTIVE);
    repository.save(device);
  }

  private Device findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
  }
}
