package com.oneglobal.model;

import com.oneglobal.enums.DeviceStateEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "devices")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Device {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 50)
  private String brand;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeviceStateEnum state = DeviceStateEnum.AVAILABLE;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private Instant creationTime;
}