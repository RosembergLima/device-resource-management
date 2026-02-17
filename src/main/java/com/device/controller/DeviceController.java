package com.device.controller;

import com.device.dto.DevicePatchRequest;
import com.device.dto.DeviceRequest;
import com.device.dto.DeviceResponse;
import com.device.enums.DeviceStateEnum;
import com.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

/**
 * REST controller exposing CRUD and query operations for Device resources.
 */
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final DeviceService service;

  /**
   * Creates a new device resource.
   *
   * @param req validated payload containing device attributes (name, brand)
   * @return created device representation
   */
  @Operation(summary = "Create a new device")
  @ApiResponse(responseCode = "201", description = "Device created",
      content = @Content(schema = @Schema(implementation = DeviceResponse.class)))
  @ApiResponse(responseCode = "400", description = "Validation error",
      content = @Content)
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DeviceResponse create(@Valid @RequestBody DeviceRequest req) {
    return service.create(req);
  }

  /**
   * Retrieves a paginated list of devices, optionally filtered by brand and state.
   *
   * @param brand optional brand filter (case-insensitive)
   * @param state optional device state filter
   * @param pageable pagination and sorting information
   * @return page of matching devices
   */
  @Operation(summary = "List devices with optional filtering and pagination")
  @ApiResponse(responseCode = "200", description = "Devices retrieved")
  @GetMapping
  public Page<DeviceResponse> findAll(
      @Parameter(description = "Brand filter (case-insensitive)")
      @RequestParam(required = false) String brand,
      @Parameter(description = "Device state filter")
      @RequestParam(required = false) DeviceStateEnum state,
      @ParameterObject Pageable pageable) {
    return service.findAll(brand, state, pageable);
  }

  /**
   * Retrieves a device by its identifier.
   *
   * @param id device identifier
   * @return device representation
   */
  @Operation(summary = "Get device by id")
  @ApiResponse(responseCode = "200", description = "Device found",
      content = @Content(schema = @Schema(implementation = DeviceResponse.class)))
  @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DeviceResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  /**
   * Partially updates a device. Only non-null fields will be applied.
   *
   * @param id device identifier
   * @param req payload with fields to update
   * @return updated device representation
   */
  @Operation(summary = "Partially update a device")
  @ApiResponse(responseCode = "200", description = "Device updated",
      content = @Content(schema = @Schema(implementation = DeviceResponse.class)))
  @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
  @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "Device in use, cannot update", content = @Content)
  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public DeviceResponse partialUpdate(@PathVariable Long id, @RequestBody DevicePatchRequest req) {
    return service.update(id, req);
  }

  /**
   * Soft-deletes a device by marking it as INACTIVE.
   *
   * @param id device identifier
   */
  @Operation(summary = "Delete a device (soft delete sets state to INACTIVE)")
  @ApiResponse(responseCode = "204", description = "Device deleted")
  @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
  @ApiResponse(responseCode = "409", description = "Device in use, cannot delete", content = @Content)
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}