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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    log.info("Creating a new device with name: {} and brand: {} and state: {}", req.name(), req.brand(), req.state());
    Device device = mapper.toEntity(req);
    Device savedDevice = repository.save(device);
    log.debug("Successfully created device with ID: {}", savedDevice.getId());
    return mapper.toResponse(savedDevice);
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
    log.info("Updating device with ID: {}", id);
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
        log.warn("Attempted to update device with ID: {} but it is currently IN_USE", id);
        throw new DeviceInUseException("Cannot update the device with state IN_USE");
    }
    mapper.updateEntityFromPatchDto(req, device);
    Device updatedDevice = repository.save(device);
    log.debug("Successfully updated device with ID: {}", id);
    return mapper.toResponse(updatedDevice);
  }

  /**
   * Finds a device by id or throws DeviceNotFoundException.
   *
   * @param id identifier
   * @return device response
   */
  @Transactional(readOnly = true)
  public DeviceResponse findById(Long id) {
    log.info("Searching for device with ID: {}", id);
    Device device = findOrThrow(id);
    return mapper.toResponse(device);
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
    log.info("Listing all devices with brand: {} and state: {}", brand, state);

    Specification<Device> spec = Specification.where(DeviceSpecifications.withBrand(brand));
    if (state != null) {
      spec = spec.and(DeviceSpecifications.withState(state));
    } else {
      spec = spec.and((root, query, cb) ->
          cb.notEqual(root.get("state"), DeviceStateEnum.INACTIVE));
    }

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
    log.info("Soft-deleting device with ID: {}", id);
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
      log.warn("Attempted to delete device with ID: {} but it is currently IN_USE", id);
      throw new DeviceInUseException("In-use devices cannot be deleted");
    }
    device.setState(DeviceStateEnum.INACTIVE);
    repository.save(device);
    log.debug("Device with ID: {} successfully set to INACTIVE", id);
  }

  /**
   * Helper that retrieves a device or throws a domain-specific exception.
   */
  private Device findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> {
          log.warn("Device not found with ID: {}", id);
          return new DeviceNotFoundException("Device not found: " + id);
        });
  }
}
