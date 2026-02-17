package com.device.specification;

import com.device.enums.DeviceStateEnum;
import com.device.model.Device;
import org.springframework.data.jpa.domain.Specification;

public final class DeviceSpecifications {

  private DeviceSpecifications() {} // util class

  public static Specification<Device> withBrand(String brand) {
    return (root, query, cb) -> brand == null
        ? cb.conjunction()
        : cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
  }

  public static Specification<Device> withState(DeviceStateEnum state) {
    return (root, query, cb) -> state == null
        ? cb.conjunction()
        : cb.equal(root.get("state"), state);
  }

}
