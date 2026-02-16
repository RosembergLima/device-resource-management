package com.oneglobal.repository;

import com.oneglobal.enums.DeviceStateEnum;
import com.oneglobal.model.Device;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

  List<Device> findByBrandIgnoreCase(String brand);
  List<Device> findByState(DeviceStateEnum state);

}
