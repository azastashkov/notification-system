package org.notification.loadclient.repository;

import org.notification.loadclient.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
