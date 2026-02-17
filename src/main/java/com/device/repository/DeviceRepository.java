package com.device.repository;

import com.device.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for Device entities.
 * Extends JpaSpecificationExecutor to support dynamic filtering via Specifications.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>,
    JpaSpecificationExecutor<Device> {

}
