package org.notification.server.service;

import org.notification.server.model.Device;
import org.notification.server.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Cacheable("devices")
    public List<Device> getActiveDevices(Long userId) {
        return deviceRepository.findByUserIdAndActiveTrue(userId);
    }
}
