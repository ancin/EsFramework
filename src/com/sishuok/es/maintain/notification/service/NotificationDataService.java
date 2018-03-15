/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.notification.service;

import org.es.framework.common.service.BaseService;
import org.springframework.stereotype.Service;

import com.sishuok.es.maintain.notification.entity.NotificationData;
import com.sishuok.es.maintain.notification.repository.NotificationDataRepository;

@Service
public class NotificationDataService extends BaseService<NotificationData, Long> {

    private NotificationDataRepository getNotificationDataRepository() {
        return (NotificationDataRepository) baseRepository;
    }

    public void markReadAll(final Long userId) {
        getNotificationDataRepository().markReadAll(userId);
    }

    public void markRead(final Long notificationId) {
        NotificationData data = findOne(notificationId);
        if (data == null || data.getRead().equals(Boolean.TRUE)) {
            return;
        }
        data.setRead(Boolean.TRUE);
        update(data);
    }
}
