package com.oneglobal.service;

import com.oneglobal.dto.DeviceRequest;
import com.oneglobal.dto.DeviceResponse;
import com.oneglobal.enums.DeviceStateEnum;
import com.oneglobal.model.Device;
import com.oneglobal.repository.DeviceRepository;
import com.oneglobal.service.exception.DeviceInUseException;
import com.oneglobal.service.exception.DeviceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

  private final DeviceRepository repository;
  private final ModelMapper mapper;

  public DeviceResponse create(DeviceRequest req) {
    Device device = mapper.map(req, Device.class);
    return mapper.map(repository.save(device), DeviceResponse.class);
  }

  public DeviceResponse update(Long id, DeviceRequest req) {   // full update (PUT)
    Device device = findOrThrow(id);

    if (device.getState() == DeviceStateEnum.IN_USE) {
      if (!device.getName().equals(req.name()) || !device.getBrand().equals(req.brand())) {
        throw new DeviceInUseException("Name and brand cannot be updated when device is IN_USE");
      }
    }

    device.setName(req.name());
    device.setBrand(req.brand());
    device.setState(req.state());

    return mapper.map(repository.save(device), DeviceResponse.class);
  }

  public DeviceResponse partialUpdate(Long id, DeviceRequest req) {  // partial (PATCH)
    Device device = findOrThrow(id);

    if (device.getState() == DeviceStateEnum.IN_USE) {
      if (req.name() != null && !device.getName().equals(req.name())) {
        throw new DeviceInUseException("Cannot update name when device is IN_USE");
      }
      if (req.brand() != null && !device.getBrand().equals(req.brand())) {
        throw new DeviceInUseException("Cannot update brand when device is IN_USE");
      }
    }

    if (req.name() != null) device.setName(req.name());
    if (req.brand() != null) device.setBrand(req.brand());
    if (req.state() != null) device.setState(req.state());

    return mapper.map(repository.save(device), DeviceResponse.class);
  }

  public DeviceResponse findById(Long id) {
    return mapper.map(findOrThrow(id), DeviceResponse.class);
  }

  public List<DeviceResponse> findAll() {
    return repository.findAll().stream()
        .map(d -> mapper.map(d, DeviceResponse.class))
        .toList();
  }

  public List<DeviceResponse> findByBrand(String brand) {
    return repository.findByBrandIgnoreCase(brand).stream()
        .map(d -> mapper.map(d, DeviceResponse.class))
        .toList();
  }

  public List<DeviceResponse> findByState(DeviceStateEnum state) {
    return repository.findByState(state).stream()
        .map(d -> mapper.map(d, DeviceResponse.class))
        .toList();
  }

  public void delete(Long id) {
    Device device = findOrThrow(id);
    if (device.getState() == DeviceStateEnum.IN_USE) {
      throw new DeviceInUseException("In-use devices cannot be deleted");
    }
    repository.deleteById(id);
  }

  private Device findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
  }
}
