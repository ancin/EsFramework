package com.sishuok.es.maintain.notification.repository;

import org.es.framework.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sishuok.es.maintain.notification.entity.NotificationData;

public interface NotificationDataRepository extends BaseRepository<NotificationData, Long> {

    @Modifying
    @Query("update NotificationData o set o.read=true where userId=?1")
    void markReadAll(Long userId);
}
