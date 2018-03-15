/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.notification.repository;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.maintain.notification.entity.NotificationTemplate;

public interface NotificationTemplateRepository extends BaseRepository<NotificationTemplate, Long> {

    @Query("from NotificationTemplate o where name=?1")
    NotificationTemplate findByName(String name);
}
