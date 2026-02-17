package com.device.specification;

import com.device.enums.DeviceStateEnum;
import com.device.model.Device;
import org.springframework.data.jpa.domain.Specification;

public final class DeviceSpecifications {

  private DeviceSpecifications() {} // util class

  /**
   * Specification that filters by brand (case-insensitive). Returns a no-op conjunction when brand is null.
   * @param brand brand filter value (nullable)
   * @return specification predicate for brand
   */
  public static Specification<Device> withBrand(String brand) {
    return (root, query, cb) -> brand == null
        ? cb.conjunction()
        : cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
  }

  /**
   * Specification that filters by device state. Returns a no-op conjunction when state is null.
   * @param state desired device state (nullable)
   * @return specification predicate for state
   */
  public static Specification<Device> withState(DeviceStateEnum state) {
    return (root, query, cb) -> state == null
        ? cb.conjunction()
        : cb.equal(root.get("state"), state);
  }

}
