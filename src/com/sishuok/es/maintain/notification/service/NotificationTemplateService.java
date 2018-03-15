/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.notification.service;

import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.maintain.notification.entity.NotificationTemplate;
import com.sishuok.es.maintain.notification.repository.NotificationTemplateRepository;

@Service
public class NotificationTemplateService extends BaseService<NotificationTemplate, Long> {

    private NotificationTemplateRepository getNotificationTemplateRepository() {
        return (NotificationTemplateRepository) baseRepository;
    }

    public NotificationTemplate findByName(final String name) {
        return getNotificationTemplateRepository().findByName(name);
    }
}
