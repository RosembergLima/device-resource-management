package com.oneglobal.controller;

import com.oneglobal.dto.DeviceRequest;
import com.oneglobal.dto.DeviceResponse;
import com.oneglobal.enums.DeviceStateEnum;
import com.oneglobal.service.DeviceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public List<DeviceResponse> findAll() { return service.findAll(); }

  @GetMapping("/brand/{brand}")
  public List<DeviceResponse> findByBrand(@PathVariable String brand) {
    return service.findByBrand(brand);
  }

  @GetMapping("/state/{state}")
  public List<DeviceResponse> findByState(@PathVariable DeviceStateEnum state) {
    return service.findByState(state);
  }

  @GetMapping("/{id}")
  public DeviceResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PutMapping("/{id}")           // full update
  public DeviceResponse update(@PathVariable Long id, @Valid @RequestBody DeviceRequest req) {
    return service.update(id, req);
  }

  @PatchMapping("/{id}")         // partial update
  public DeviceResponse partialUpdate(@PathVariable Long id, @RequestBody DeviceRequest req) {
    return service.partialUpdate(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
