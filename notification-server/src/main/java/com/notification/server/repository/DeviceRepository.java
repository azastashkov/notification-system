package com.notification.server.repository;

import com.notification.server.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByUserIdAndActiveTrue(Long userId);
}
