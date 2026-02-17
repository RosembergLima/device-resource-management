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

  /**
   * Persists a new device with default state AVAILABLE.
   * Transactional write to ensure atomicity.
   *
   * @param req creation payload
   * @return persisted device representation
   */
  @Transactional
  public DeviceResponse create(DeviceRequest req) {
    Device device = mapper.toEntity(req);
    device.setState(DeviceStateEnum.AVAILABLE);
    return mapper.toResponse(repository.save(device));
  }

  /**
   * Applies a partial update to a device. Non-null fields in the request are copied to the entity.
   *
   * @param id device identifier
   * @param req patch payload (nullable fields)
   * @return updated device representation
   * @throws DeviceInUseException if current device state is IN_USE
   */
  @Transactional
  public DeviceResponse update(Long id, DevicePatchRequest req) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
        throw new DeviceInUseException("Cannot update the device with state IN_USE");
    }
    mapper.updateEntityFromPatchDto(req, device);
    return mapper.toResponse(repository.save(device));
  }

  /**
   * Finds a device by id or throws DeviceNotFoundException.
   *
   * @param id identifier
   * @return device response
   */
  @Transactional(readOnly = true)
  public DeviceResponse findById(Long id) {
    return mapper.toResponse(findOrThrow(id));
  }

  /**
   * Queries devices using the Specification pattern with optional filters, returning a paginated result.
   * If state is not provided, defaults to AVAILABLE devices.
   *
   * @param brand optional brand filter
   * @param state optional state filter
   * @param pageable pagination and sorting config
   * @return page of devices matching filters
   */
  @Transactional(readOnly = true)
  public Page<DeviceResponse> findAll(String brand, DeviceStateEnum state, Pageable pageable) {

    DeviceStateEnum effectiveState = (state != null) ? state : DeviceStateEnum.AVAILABLE;

    Specification<Device> spec = Specification.where(DeviceSpecifications.withBrand(brand))
        .and(DeviceSpecifications.withState(effectiveState));

    return repository.findAll(spec, pageable)
        .map(mapper::toResponse);
  }

  /**
   * Performs a soft-delete by setting the device state to INACTIVE.
   *
   * @param id device identifier
   * @throws DeviceInUseException if current device state is IN_USE
   */
  @Transactional
  public void delete(Long id) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
      throw new DeviceInUseException("In-use devices cannot be deleted");
    }
    device.setState(DeviceStateEnum.INACTIVE);
    repository.save(device);
  }

  /**
   * Helper that retrieves a device or throws a domain-specific exception.
   */
  private Device findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
  }
}
