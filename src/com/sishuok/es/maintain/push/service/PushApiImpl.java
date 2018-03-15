/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.sishuok.es.maintain.push.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class PushApiImpl implements PushApi {

    @Autowired
    private PushService pushService;

    @Override
    public void pushUnreadMessage(final Long userId, Long unreadMessageCount) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("unreadMessageCount", unreadMessageCount);
        pushService.push(userId, data);
    }

    @Override
    public void pushNewNotification(final Long userId, List<Map<String, Object>> notifiations) {
        Map<String, Object> data = Maps.newHashMap();
        data.put("notifications", notifiations);
        pushService.push(userId, data);
    }

    @Override
    public void offline(final Long userId) {
        pushService.offline(userId);
    }
}
