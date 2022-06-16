package com.vmo.core.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MyHouse {
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    /**
     * Hành động bấm chuông cửa
     */
    public void rangDoorbellBy(String guestName) {
        // Phát ra một sự kiện DoorBellEvent
        // source (Nguồn phát ra) chính là class này
        applicationEventPublisher.publishEvent(new DoorBellEvent(this, guestName));
    }
}
