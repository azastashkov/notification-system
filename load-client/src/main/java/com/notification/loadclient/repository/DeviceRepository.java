package com.notification.loadclient.repository;

import com.notification.loadclient.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
