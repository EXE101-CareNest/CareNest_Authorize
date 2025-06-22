package com.exe.carenest.authorizeservice.infrastructure.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class RedisEvent implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;


    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /*
       Trigger reload data to redis
  */
    public void triggerReload() {
        publisher.publishEvent(new PermissionReloadEvent(this));
    }
}
