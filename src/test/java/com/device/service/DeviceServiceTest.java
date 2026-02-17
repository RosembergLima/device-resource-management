package com.device.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.device.dto.DevicePatchRequest;
import com.device.dto.DeviceRequest;
import com.device.dto.DeviceResponse;
import com.device.enums.DeviceStateEnum;
import com.device.mapper.DeviceMapper;
import com.device.model.Device;
import com.device.repository.DeviceRepository;
import com.device.service.exception.DeviceInUseException;
import com.device.service.exception.DeviceNotFoundException;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

  @Mock
  private DeviceRepository repository;

  @Mock
  private DeviceMapper mapper;

  @InjectMocks
  private DeviceService service;

  private Device device;
  private DeviceResponse deviceResponse;
  private DeviceRequest deviceRequest;

  @BeforeEach
  void setUp() {
    device = Device.builder()
        .id(1L)
        .name("iPhone 15")
        .brand("Apple")
        .state(DeviceStateEnum.AVAILABLE)
        .creationTime(Instant.now())
        .build();

    deviceResponse = new DeviceResponse(1L, "iPhone 15", "Apple", DeviceStateEnum.AVAILABLE, Instant.now());
    deviceRequest = new DeviceRequest("iPhone 15", "Apple", null);
  }

  @Nested
  @DisplayName("Create operations")
  class CreateTests {
    @Test
    void create_shouldReturnDeviceResponse() {
      when(mapper.toEntity(deviceRequest)).thenReturn(device);
      when(repository.save(any(Device.class))).thenReturn(device);
      when(mapper.toResponse(device)).thenReturn(deviceResponse);

      DeviceResponse result = service.create(deviceRequest);

      assertNotNull(result);
      assertEquals(deviceResponse.id(), result.id());
      verify(repository).save(device);
    }
  }

  @Nested
  @DisplayName("Read operations")
  class ReadTests {
    @Test
    void findById_shouldReturnDeviceResponse_whenIdExists() {
      when(repository.findById(1L)).thenReturn(Optional.of(device));
      when(mapper.toResponse(device)).thenReturn(deviceResponse);

      DeviceResponse result = service.findById(1L);

      assertNotNull(result);
      assertEquals(1L, result.id());
    }

    @Test
    void findById_shouldThrowException_whenIdDoesNotExist() {
      when(repository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(DeviceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnPageOfDeviceResponses() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Device> devicePage = new PageImpl<>(List.of(device));
      when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(devicePage);
      when(mapper.toResponse(device)).thenReturn(deviceResponse);

      Page<DeviceResponse> result = service.findAll("Apple", DeviceStateEnum.AVAILABLE, pageable);

      assertNotNull(result);
      assertEquals(1, result.getTotalElements());
    }
  }

  @Nested
  @DisplayName("Update operations")
  class UpdateTests {
    @Test
    void update_shouldReturnUpdatedDeviceResponse() {
      DevicePatchRequest patchRequest = new DevicePatchRequest("iPhone 15 Pro", null, null);
      when(repository.findById(1L)).thenReturn(Optional.of(device));
      when(repository.save(device)).thenReturn(device);
      when(mapper.toResponse(device)).thenReturn(deviceResponse);

      DeviceResponse result = service.update(1L, patchRequest);

      assertNotNull(result);
      verify(mapper).updateEntityFromPatchDto(patchRequest, device);
      verify(repository).save(device);
    }

    @Test
    void update_shouldThrowException_whenDeviceInUse() {
      device.setState(DeviceStateEnum.IN_USE);
      DevicePatchRequest patchRequest = new DevicePatchRequest("iPhone 15 Pro", null, null);
      when(repository.findById(1L)).thenReturn(Optional.of(device));

      assertThrows(DeviceInUseException.class, () -> service.update(1L, patchRequest));
    }
  }

  @Nested
  @DisplayName("Delete operations")
  class DeleteTests {
    @Test
    void delete_shouldChangeStateToInactive() {
      when(repository.findById(1L)).thenReturn(Optional.of(device));

      service.delete(1L);

      assertEquals(DeviceStateEnum.INACTIVE, device.getState());
      verify(repository).save(device);
    }

    @Test
    void delete_shouldThrowException_whenDeviceInUse() {
      device.setState(DeviceStateEnum.IN_USE);
      when(repository.findById(1L)).thenReturn(Optional.of(device));

      assertThrows(DeviceInUseException.class, () -> service.delete(1L));
    }
  }
}
