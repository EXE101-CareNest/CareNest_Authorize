package com.exe.carenest.authorizeservice.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.time.Clock;


public class PermissionReloadEvent extends ApplicationEvent {

    public PermissionReloadEvent(Object source) {
        super(source);
    }

    public PermissionReloadEvent(Object source, Clock clock) {
        super(source, clock);
    }


}