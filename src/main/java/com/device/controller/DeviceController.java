package com.device.controller;

import com.device.dto.DevicePatchRequest;
import com.device.dto.DeviceRequest;
import com.device.dto.DeviceResponse;
import com.device.enums.DeviceStateEnum;
import com.device.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final DeviceService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DeviceResponse create(@Valid @RequestBody DeviceRequest req) {
    return service.create(req);
  }

  @GetMapping
  public Page<DeviceResponse> findAll(
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) DeviceStateEnum state,
      Pageable pageable) {
    return service.findAll(brand, state, pageable);
  }

  @GetMapping("/{id}")
  public DeviceResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PatchMapping("/{id}")
  public DeviceResponse partialUpdate(@PathVariable Long id, @RequestBody DevicePatchRequest req) {
    return service.partialUpdate(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
