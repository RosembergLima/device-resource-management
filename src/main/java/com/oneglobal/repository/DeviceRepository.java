package com.oneglobal.repository;

import com.oneglobal.enums.DeviceStateEnum;
import com.oneglobal.model.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

  Page<Device> findByBrandIgnoreCase(String brand, Pageable pageable);
  Page<Device> findByState(DeviceStateEnum state, Pageable pageable);
  Page<Device> findByBrandIgnoreCaseAndState(String brand, DeviceStateEnum state, Pageable pageable);

}
