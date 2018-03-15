/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.notification.service;

import java.util.List;
import java.util.Map;

import com.sishuok.es.maintain.notification.exception.TemplateNotFoundException;

public interface NotificationApi {

    public void notify(Long userId, String templateName, Map<String, Object> context)
                                                                                     throws TemplateNotFoundException;

    /**
     *
     * id :
     * title
     * content
     * date
     *
     * @param userId
     * @return
     */
    public List<Map<String, Object>> topFiveNotification(Long userId);
}
